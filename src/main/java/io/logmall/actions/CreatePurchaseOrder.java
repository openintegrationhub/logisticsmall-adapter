package io.logmall.actions;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.PartyMasterService;
import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.PurchaseOrderService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.ChangePartyMaster;
import de.fraunhofer.ccl.bo.model.bod.ChangePurchaseOrder;
import de.fraunhofer.ccl.bo.model.bod.RespondPartyMaster;
import de.fraunhofer.ccl.bo.model.bod.RespondPurchaseOrder;
import de.fraunhofer.ccl.bo.model.bod.builder.change.CreateOrReplaceBODBuilder;
import de.fraunhofer.ccl.bo.model.bod.verb.Change;
import de.fraunhofer.ccl.bo.model.entity.common.PredefinedMeasureUnitType;
import de.fraunhofer.ccl.bo.model.entity.common.Quantity;
import de.fraunhofer.ccl.bo.model.entity.item.Item;
import de.fraunhofer.ccl.bo.model.entity.party.Address;
import de.fraunhofer.ccl.bo.model.entity.party.Contact;
import de.fraunhofer.ccl.bo.model.entity.party.Location;
import de.fraunhofer.ccl.bo.model.entity.party.Party;
import de.fraunhofer.ccl.bo.model.entity.partymaster.PartyMaster;
import de.fraunhofer.ccl.bo.model.entity.partymaster.TermsOfDelivery;
import de.fraunhofer.ccl.bo.model.entity.purchaseorder.PurchaseOrder;
import de.fraunhofer.ccl.bo.model.entity.purchaseorder.PurchaseOrderLine;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.bod.ConfigurationParameters;
import io.logmall.bod.CustomerAddress;
import io.logmall.bod.PurchaseOrderLineMinimal;
import io.logmall.bod.PurchaseOrderMinimal;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.mapper.StandaloneBusinessObjectDocumentJsonMapper;
import io.logmall.util.MeasureUtil;

public class CreatePurchaseOrder implements Module {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreatePurchaseOrder.class);

	@Override
	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Going to create new Minimal Purchase Order");
		try {
			// ------ setup data -----
			// body contains the mapped data
			final JsonObject body = parameters.getMessage().getBody();
			ParametersJsonMapper<PurchaseOrderMinimal> purchaseOrderMinimalJsonMapper = new ParametersJsonMapper<>(
					PurchaseOrderMinimal.class);
			PurchaseOrderMinimal purchaseOrderMinimal = purchaseOrderMinimalJsonMapper.fromJson(body);

			ConfigurationParameters configuration = new ParametersJsonMapper<>(ConfigurationParameters.class)
					.fromJson(parameters.getConfiguration());
			LOGGER.info("App Server URL: " + configuration.getServerURLd());
			ChangePurchaseOrder requestBodPurchaseOrder = createPurchaseOrder(purchaseOrderMinimal, configuration);

			@SuppressWarnings({ "rawtypes", "unchecked" })
			StandaloneBusinessObjectDocumentJsonMapper<BusinessObjectDocument<Change, PurchaseOrder>> standaloneBusinessObjectDocumentJsonMapper = new StandaloneBusinessObjectDocumentJsonMapper(
					requestBodPurchaseOrder.getClass());
			standaloneBusinessObjectDocumentJsonMapper.logAsJson(requestBodPurchaseOrder);

			PurchaseOrderService purchaseOrderService = ResteasyIntegration.newInstance()
					.createClientProxy(PurchaseOrderService.class, configuration.getServerURLd());
			RespondPurchaseOrder response = (RespondPurchaseOrder) purchaseOrderService.put(requestBodPurchaseOrder);

			LOGGER.info("MinimalPurchaseOrder successfully created:\t" + response.toString());
			JsonObject responseBody = getEventBody(response);
			Message data = new Message.Builder().body(responseBody).build();
			// emitting the message to the platform
			parameters.getEventEmitter().emitData(data);
		} catch (Exception e) {
			LOGGER.error("going to emit exception: " + e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		}
	}

	private JsonObject getEventBody(RespondPurchaseOrder response) throws JAXBException, NotFoundException {
		List<PurchaseOrder> nouns = response.getNounsForIteration();
		if (!nouns.isEmpty()) {
			PurchaseOrder purchaseOrderNoun = nouns.get(0);
			PurchaseOrderMinimal purchaseOrderMinimalNoun = new PurchaseOrderMinimal();
			purchaseOrderMinimalNoun.setPurchaseOrderIdentifier(purchaseOrderNoun.getDisplayIdentifierId());
			purchaseOrderMinimalNoun.setOrderDateTime(purchaseOrderNoun.getOrderDate());

			TermsOfDelivery termsOfDelivery = purchaseOrderNoun.getTermsOfDelivery();
			if (termsOfDelivery != null) {
				purchaseOrderMinimalNoun.setDeliveryTypeCode(termsOfDelivery.getDeliveryTypeCode());
			}

			Party customer = purchaseOrderNoun.getCustomer();
			if (customer != null) {
				purchaseOrderMinimalNoun.setName(customer.getName());

				CustomerAddress address = new CustomerAddress();
				Address postalAdress = purchaseOrderNoun.getCustomer().getMasterData().getPostalAddress();
				address.setStreet(postalAdress.getStreet());
				address.setNumber(postalAdress.getNumber());
				address.setPostalCode(postalAdress.getPostalCode());
				address.setCity(postalAdress.getCity());
				address.setCountryCode(postalAdress.getCountryCode());
				purchaseOrderMinimalNoun.setAddress(address);
			}

			ParametersJsonMapper<PurchaseOrderMinimal> mapper = new ParametersJsonMapper<>(PurchaseOrderMinimal.class);
			JsonObject jsonObject = mapper.toJson(purchaseOrderMinimalNoun);
			return jsonObject;
		} else
			throw new NotFoundException("No Item Master found");
	}

	private ChangePurchaseOrder createPurchaseOrder(PurchaseOrderMinimal purchaseOrderMinimal,
			ConfigurationParameters configuration) throws JAXBException {

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setDisplayIdentifier(purchaseOrderMinimal.getPurchaseOrderIdentifier());
		purchaseOrder.setOrderDate(purchaseOrderMinimal.getOrderDateTime());
		purchaseOrder.setFreightCost(MeasureUtil.getMeasure(PredefinedMeasureUnitType.CURRENCY));

		TermsOfDelivery termsOfDelivery = TermsOfDelivery.newEmptyInstance();
		termsOfDelivery.setDeliveryTypeCode(purchaseOrderMinimal.getDeliveryTypeCode());
		purchaseOrder.setTermsOfDelivery(termsOfDelivery);

		Address postalAddress = new Address();
		postalAddress.setStreet(purchaseOrderMinimal.getAddress().getStreet());
		postalAddress.setNumber(purchaseOrderMinimal.getAddress().getNumber());
		postalAddress.setPostalCode(purchaseOrderMinimal.getAddress().getPostalCode());
		postalAddress.setCity(purchaseOrderMinimal.getAddress().getCity());
		postalAddress.setCountryCode(purchaseOrderMinimal.getAddress().getCountryCode());
		postalAddress.setCareOfName(purchaseOrderMinimal.getName());

		PartyMaster partyMaster = new PartyMaster();
		partyMaster.setName("Customer");
		partyMaster.setDisplayIdentifier(partyMaster.getName());

		CreateOrReplaceBODBuilder.Builder<PartyMaster> createBODBuilderPartyMaster = CreateOrReplaceBODBuilder
				.newInstance(PartyMaster.class);
		createBODBuilderPartyMaster.forCreationOrReplacement();
		createBODBuilderPartyMaster.withNoun(partyMaster);
		ChangePartyMaster requestBodPartyMaster = (ChangePartyMaster) createBODBuilderPartyMaster.build();

		PartyMasterService partyMasterService = ResteasyIntegration.newInstance()
				.createClientProxy(PartyMasterService.class, configuration.getServerURLd());
		RespondPartyMaster responsePartyMaster = (RespondPartyMaster) partyMasterService.put(requestBodPartyMaster);
		partyMaster = responsePartyMaster.getNouns().get(0);

		Contact contact = new Contact();
		contact.setFamilyName(purchaseOrderMinimal.getName());
		contact.setGivenName(purchaseOrderMinimal.getFirstName());

		Party customerParty = new Party();
		customerParty.setDisplayIdentifier(purchaseOrderMinimal.getName());
		customerParty.setMasterData(partyMaster);

		Set<Contact> contacts = new HashSet<>();
		contacts.add(contact);
		customerParty.setContacts(contacts);

		Location location = new Location();
		location.setPostalAddress(postalAddress);
		Set<Location> locations = new HashSet<>();
		locations.add(location);
		customerParty.setLocations(locations);

		purchaseOrder.setCustomer(customerParty);

		for (PurchaseOrderLineMinimal purchaseOrderLineMinimal : purchaseOrderMinimal.getLines()) {
			PurchaseOrderLine purchaseOrderLine = new PurchaseOrderLine();
			Item item = new Item();
			item.setDisplayIdentifier(purchaseOrderLineMinimal.getItemMasterIdentifier());
			purchaseOrderLine.setItem(item);

			purchaseOrderLine.setNumber(purchaseOrderLineMinimal.getLineNumber());
			Quantity orderedQuantity = new Quantity();
			orderedQuantity.setUnitName(purchaseOrderLineMinimal.getQuantityUnit());
			orderedQuantity.setValue(purchaseOrderLineMinimal.getOrderedQuantity());
			purchaseOrderLine.setOrderedQuantity(orderedQuantity);
			purchaseOrder.addItemLine(purchaseOrderLine);

		}

		CreateOrReplaceBODBuilder.Builder<PurchaseOrder> createBODBuilderPurchaseOrder = CreateOrReplaceBODBuilder
				.newInstance(PurchaseOrder.class);
		createBODBuilderPurchaseOrder.forCreationOrReplacement();
		createBODBuilderPurchaseOrder.withNoun(purchaseOrder);
		ChangePurchaseOrder requestBod = (ChangePurchaseOrder) createBODBuilderPurchaseOrder.build();
		
		
//		StringWriter stringWriter = new StringWriter();
//        JsonFactory jsonFactory = new JsonFactory();
//        jsonFactory.createMarshaller(true).marshal(requestBod, stringWriter);
//        String jsonPayload = stringWriter.toString();
//        LOGGER.info("--------------------------- XML Payload -------------------- \n" + jsonPayload + "\n------------------------------------------------------------");

		return requestBod;
	}
}
