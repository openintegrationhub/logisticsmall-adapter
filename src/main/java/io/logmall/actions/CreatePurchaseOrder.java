package io.logmall.actions;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.ChangeShipment;
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

import de.fraunhofer.ccl.bo.converter.xml.oagis.JsonFactory;
import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.PartyMasterService;
import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ShipmentService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.ChangePartyMaster;
import de.fraunhofer.ccl.bo.model.bod.GetItemMaster;
import de.fraunhofer.ccl.bo.model.bod.RespondPartyMaster;
import de.fraunhofer.ccl.bo.model.bod.RespondShipment;
import de.fraunhofer.ccl.bo.model.bod.builder.change.CreateOrReplaceBODBuilder;
import de.fraunhofer.ccl.bo.model.bod.builder.get.GetByExampleBODBuilder;
import de.fraunhofer.ccl.bo.model.bod.verb.Change;
import de.fraunhofer.ccl.bo.model.entity.common.PredefinedMeasureUnitType;
import de.fraunhofer.ccl.bo.model.entity.common.Quantity;
import de.fraunhofer.ccl.bo.model.entity.common.Status;
import de.fraunhofer.ccl.bo.model.entity.item.Item;
import de.fraunhofer.ccl.bo.model.entity.itemmaster.ItemMaster;
import de.fraunhofer.ccl.bo.model.entity.party.Address;
import de.fraunhofer.ccl.bo.model.entity.party.Contact;
import de.fraunhofer.ccl.bo.model.entity.party.Location;
import de.fraunhofer.ccl.bo.model.entity.party.Party;
import de.fraunhofer.ccl.bo.model.entity.partymaster.PartyMaster;
import de.fraunhofer.ccl.bo.model.entity.partymaster.TermsOfDelivery;
import de.fraunhofer.ccl.bo.model.entity.shipment.Shipment;
import de.fraunhofer.ccl.bo.model.entity.shipment.ShipmentItemLine;


public class CreatePurchaseOrder implements Module {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreatePurchaseOrder.class);
	
	@Override
	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Going to create new Minimal Purchase Order");
		try {

			// body contains the mapped data
			ParametersJsonMapper<PurchaseOrderMinimal> purchaseOrderMinimalJsonMapper = new ParametersJsonMapper<>(
					PurchaseOrderMinimal.class);
			PurchaseOrderMinimal purchaseOrderMinimal = purchaseOrderMinimalJsonMapper.fromJson(parameters.getMessage().getBody());
			ConfigurationParameters configuration = new ParametersJsonMapper<>(ConfigurationParameters.class)
					.fromJson(parameters.getConfiguration());
			LOGGER.info("App Server URL to which the data should be sent: " + configuration.getServerURLd());
			
			ChangeShipment requestBodShipment = createShipment(purchaseOrderMinimal, configuration);
			LOGGER.info("request Bod Shipment LanguageCode= : " + requestBodShipment.getLanguageCode() + "\n" + 
			"request Bod Shipment" + requestBodShipment.getBodId()+ "\n"  );
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			StandaloneBusinessObjectDocumentJsonMapper<BusinessObjectDocument<Change, Shipment>> standaloneBusinessObjectDocumentJsonMapper = new StandaloneBusinessObjectDocumentJsonMapper(
					requestBodShipment.getClass());
			standaloneBusinessObjectDocumentJsonMapper.logAsJson(requestBodShipment);			
			
			ShipmentService shipmentService = ResteasyIntegration.newInstance()
					.createClientProxy(ShipmentService.class, configuration.getServerURLd());
			RespondShipment response = (RespondShipment) shipmentService.put(requestBodShipment);
			LOGGER.info("MinimalPurchaseOrder successfully created:\t" + purchaseOrderMinimal.toString());	
			// emitting the message to the platform
			parameters.getEventEmitter().emitData(new Message.Builder().body(getEventBody(response)).build());
		} catch (Exception e) {
			LOGGER.error("going to emit exception: " + e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		}
	}
	
	private ChangeShipment createShipment(PurchaseOrderMinimal purchaseOrderMinimal,
			ConfigurationParameters configuration) throws JAXBException {
		
		Shipment shipment = new Shipment();
		shipment.setDisplayIdentifier(purchaseOrderMinimal.getPurchaseOrderIdentifier());

		TermsOfDelivery termsOfDelivery = TermsOfDelivery.newEmptyInstance();
		termsOfDelivery.setDeliveryTypeCode("Door");
		shipment.setTermsOfDelivery(termsOfDelivery);
		shipment.setDateOfShipment(purchaseOrderMinimal.getOrderDateTime());
		shipment.setType("SHP");
		shipment.setStatus(new Status("Pending"));
		shipment.setVolume(MeasureUtil.getMeasure(PredefinedMeasureUnitType.VOLUME));
		shipment.setLoadingMeter(MeasureUtil.getMeasure(PredefinedMeasureUnitType.LENGTH));
		shipment.setNetWeight(MeasureUtil.getMeasure(PredefinedMeasureUnitType.WEIGHT));
		shipment.setGrossWeight(MeasureUtil.getMeasure(PredefinedMeasureUnitType.WEIGHT));
		
		
		Address postalAddress = completePostalAddress(purchaseOrderMinimal);
		PartyMaster partyMaster = giveBOIDFromPartyMaster(configuration);		
		Party customerParty = createCustomerParty(purchaseOrderMinimal, partyMaster, postalAddress);
		shipment.setConsignor(customerParty);

		for (PurchaseOrderLineMinimal purchaseOrderLineMinimal : purchaseOrderMinimal.getLines()) {
			ShipmentItemLine shipmentItemLine = new ShipmentItemLine();
			Item item = new Item();
			item.setDisplayIdentifier(purchaseOrderLineMinimal.getItemMasterIdentifier());
			item.setMasterData(findItemMaster(purchaseOrderLineMinimal.getItemMasterIdentifier()));
			
			shipmentItemLine.setItem(item);
			shipmentItemLine.setNumber(purchaseOrderLineMinimal.getLineNumber());
		
			Quantity orderedQuantity = new Quantity();			
			orderedQuantity.setUnitName("Stk");
			orderedQuantity.setValue(purchaseOrderLineMinimal.getOrderedQuantity());
			shipmentItemLine.setOrderedQuantity(orderedQuantity);
			
			Quantity shippedQuantity = new Quantity();			
			shippedQuantity.setUnitName("Stk");
			shippedQuantity.setValue(BigDecimal.ZERO);
			shipmentItemLine.setShippedQuantity(shippedQuantity);
			
			shipment.addItemLine(shipmentItemLine);
		}

		CreateOrReplaceBODBuilder.Builder<Shipment> createBODBuilderShipment = CreateOrReplaceBODBuilder
				.newInstance(Shipment.class);
		createBODBuilderShipment.forCreationOrReplacement();
		createBODBuilderShipment.withNoun(shipment);
		ChangeShipment requestBod = (ChangeShipment) createBODBuilderShipment.build();

		StringWriter stringWriter = new StringWriter();
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.createMarshaller(true).marshal(requestBod, stringWriter);
		String jsonPayload = stringWriter.toString();
		LOGGER.info("--------------------------- RequestBod: \"createPurchaseOrder\" before StandaloneBusinessObjectDocumentJsonMapper is called ------------------- \n" + jsonPayload
				+ "\n------------------------------------------------------------");
		return requestBod;
	}

	private Party createCustomerParty(PurchaseOrderMinimal purchaseOrderMinimal, PartyMaster partyMaster,
			Address postalAddress) {
		Party customerParty = new Party();
		customerParty.setDisplayIdentifier(purchaseOrderMinimal.getName());
		customerParty.setName(purchaseOrderMinimal.getName());
		customerParty.setMasterData(partyMaster);

		Location location = new Location();
		location.setPostalAddress(postalAddress);
		Set<Location> locations = new HashSet<>();
		locations.add(location);
		
		Contact contact = new Contact();
		contact.setFamilyName(purchaseOrderMinimal.getName());
		contact.setGivenName(purchaseOrderMinimal.getFirstName());
		Set<Contact> contacts = new HashSet<>();
		contacts.add(contact);
		customerParty.setContacts(contacts);
		customerParty.setLocations(locations);
		return customerParty;
	}

	private PartyMaster giveBOIDFromPartyMaster(ConfigurationParameters configuration) throws JAXBException {
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
		return partyMaster;
	}

	private Address completePostalAddress(PurchaseOrderMinimal purchaseOrderMinimal) {
		Address postalAddress = new Address();
		postalAddress.setStreet(purchaseOrderMinimal.getAddress().getStreet());
		postalAddress.setNumber(purchaseOrderMinimal.getAddress().getNumber());
		postalAddress.setPostalCode(purchaseOrderMinimal.getAddress().getPostalCode());
		postalAddress.setCity(purchaseOrderMinimal.getAddress().getCity());
		postalAddress.setCountryCode(purchaseOrderMinimal.getAddress().getCountryCode());
		postalAddress.setCareOfName(purchaseOrderMinimal.getName());
		return postalAddress;
	}
	
	private JsonObject getEventBody(RespondShipment response) throws JAXBException, NotFoundException {
		List<Shipment> nouns = response.getNounsForIteration();
		if (!nouns.isEmpty()) {
			Shipment shipmentNoun = nouns.get(0);
			PurchaseOrderMinimal purchaseOrderMinimalNoun = new PurchaseOrderMinimal();
			purchaseOrderMinimalNoun.setPurchaseOrderIdentifier(shipmentNoun.getDisplayIdentifierId());
			purchaseOrderMinimalNoun.setOrderDateTime(shipmentNoun.getDateOfShipment());
			TermsOfDelivery termsOfDelivery = shipmentNoun.getTermsOfDelivery();
			if (termsOfDelivery != null) {
				purchaseOrderMinimalNoun.setDeliveryTypeCode(termsOfDelivery.getDeliveryTypeCode());
			}
			Party customer = shipmentNoun.getConsignee();
			if (customer != null) {
				purchaseOrderMinimalNoun.setName(customer.getName());
				CustomerAddress address = giveCustomerAddressInformation(shipmentNoun);
				purchaseOrderMinimalNoun.setAddress(address);
			}
			ParametersJsonMapper<PurchaseOrderMinimal> mapper = new ParametersJsonMapper<>(PurchaseOrderMinimal.class);
			JsonObject jsonObject = mapper.toJson(purchaseOrderMinimalNoun);
			return jsonObject;
		} else
			throw new NotFoundException("No Item Master found");
	}

	private CustomerAddress giveCustomerAddressInformation(Shipment shipmentNoun) {
		CustomerAddress address = new CustomerAddress();
		Address postalAdress = shipmentNoun.getConsignor().getMasterData().getPostalAddress();
		address.setStreet(postalAdress.getStreet());
		address.setNumber(postalAdress.getNumber());
		address.setPostalCode(postalAdress.getPostalCode());
		address.setCity(postalAdress.getCity());
		address.setCountryCode(postalAdress.getCountryCode());
		return address;
	}
	
	private ItemMaster findItemMaster(String itemMasterDisplayID) {
		ItemMaster itemMasterResult = null;
		ItemMaster itemMasterExample = new ItemMaster();
		itemMasterExample.setDisplayIdentifier(itemMasterDisplayID);

		GetByExampleBODBuilder.Builder<ItemMaster> getBODBuilderItemMaster = GetByExampleBODBuilder
				.newInstance(ItemMaster.class);
		getBODBuilderItemMaster.withExample(itemMasterExample);
		GetItemMaster bodBuilderItemMaster = (GetItemMaster) getBODBuilderItemMaster.build();

		List<ItemMaster> itemMasters = bodBuilderItemMaster.getNouns();
		if (itemMasters != null && !itemMasters.isEmpty()) {
			itemMasterResult = ItemMaster.newEmptyInstance();
			itemMasterResult.setDisplayIdentifier(itemMasters.get(0).getDisplayIdentifierId());
		}
		LOGGER.info("------------ found ItemMaster: ----- " + itemMasterResult.getDisplayIdentifierId());
		return itemMasterResult;
	}
}
