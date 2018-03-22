package io.logmall.actions;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ItemMasterService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.builder.change.CreateOrReplaceBODBuilder;
import de.fraunhofer.ccl.bo.model.bod.verb.Change;
import de.fraunhofer.ccl.bo.model.bod.verb.Respond;
import de.fraunhofer.ccl.bo.model.entity.common.PredefinedMeasureUnitType;
import de.fraunhofer.ccl.bo.model.entity.common.Quantity;
import de.fraunhofer.ccl.bo.model.entity.common.QuantityClassification;
import de.fraunhofer.ccl.bo.model.entity.common.Status;
import de.fraunhofer.ccl.bo.model.entity.itemmaster.ItemMaster;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.Constants;
import io.logmall.bod.ItemMasterMinimal;
import io.logmall.mapper.ItemMasterMinimalJsonMapper;
import io.logmall.mapper.StandaloneBusinessObjectJsonMapper;

public class CreateItemMaster implements Module {
	public MeasureUtil measureUtil = new MeasureUtil();
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateItemMaster.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */

	@Override
	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Going to create new Minimal ItemMaster");
		try {
			// ------ setup data -----
			// body contains the mapped data
			final JsonObject body = parameters.getMessage().getBody();
			ItemMasterMinimalJsonMapper itemMasterMinimalJsonMapper = new ItemMasterMinimalJsonMapper();
			ItemMasterMinimal itemMasterMinimal = itemMasterMinimalJsonMapper.fromJson(body);

			// Only Stk available, therefore, no retrieval is done out of the configuration
			QuantityClassification quantityClassification = new QuantityClassification();
			quantityClassification.setUnit("Stk");
			String status = "inactive";
			if ("1".equals(itemMasterMinimal.getStatusCode())) {
				status = "active";
			}

			ItemMaster itemMaster = new ItemMaster();
			itemMaster.setDisplayIdentifier(itemMasterMinimal.getIdentifier());
			itemMaster.setBaseQuantityClassification(quantityClassification);
			itemMaster.setDescription(itemMasterMinimal.getDescription());
			itemMaster.setStatus(new Status(status));
			itemMaster.setAverageRunSizeQuantity(generateNewQuantity());
			itemMaster.setOrderQuantity(generateNewQuantity());

			itemMaster.setTargetStock(MeasureUtil.getMeasure(PredefinedMeasureUnitType.COUNTABLE));
			itemMaster.setReorderPoint(MeasureUtil.getMeasure(PredefinedMeasureUnitType.COUNTABLE));
			itemMaster.setWeight(MeasureUtil.getMeasure(PredefinedMeasureUnitType.WEIGHT));
			itemMaster.setLength(MeasureUtil.getMeasure(PredefinedMeasureUnitType.LENGTH));
			itemMaster.setWidth(MeasureUtil.getMeasure(PredefinedMeasureUnitType.LENGTH));
			itemMaster.setHeight(MeasureUtil.getMeasure(PredefinedMeasureUnitType.LENGTH));
			itemMaster.setServiceLevel(MeasureUtil.getMeasure(PredefinedMeasureUnitType.COUNTABLE));

			CreateOrReplaceBODBuilder.Builder<ItemMaster> createBODBuilderItemMaster = CreateOrReplaceBODBuilder
					.newInstance(ItemMaster.class);
			createBODBuilderItemMaster.forCreationOrReplacement();
			createBODBuilderItemMaster.withNoun(itemMaster);
			BusinessObjectDocument<Change, ItemMaster> requestBod = createBODBuilderItemMaster.build();

			StandaloneBusinessObjectJsonMapper standaloneBusinessObjectJsonMapper = new StandaloneBusinessObjectJsonMapper();
			standaloneBusinessObjectJsonMapper.logBOD(requestBod);
			// ----- setup communication -----
			// contains action's configuration
			final JsonObject configuration = parameters.getConfiguration();
			JsonString serverURL = configuration.getJsonString(Constants.URL_CONFIGURATION_KEY);
			LOGGER.info("App Server URL: " + serverURL.getString());
			ItemMasterService itemMasterService = ResteasyIntegration.newInstance()
					.createClientProxy(ItemMasterService.class, serverURL.getString());
			BusinessObjectDocument<Respond, ItemMaster> response = itemMasterService.put(requestBod);
			LOGGER.info("MinimalItemMaster successfully created:\t" + response.toString());

			List<ItemMaster> nouns = response.getNounsForIteration();
			if (!nouns.isEmpty()) {
				ItemMaster itemMasterNoun = nouns.get(0);
				ItemMasterMinimal itemMasterMinimalNoun = new ItemMasterMinimal();
				itemMasterMinimalNoun
						.setBaseQuantityClassificationUnit(itemMasterNoun.getBaseQuantityClassification().getUnit());
				itemMasterMinimalNoun.setDescription(itemMasterNoun.getDescription());
				itemMasterMinimalNoun.setIdentifier(itemMasterNoun.getDisplayIdentifierId());
				itemMasterMinimalNoun.setStatusCode(itemMasterNoun.getStatus().getCode());

				Marshaller marshaller = JAXBContext.newInstance(ItemMasterMinimal.class).createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
				marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

				StringWriter stringWriter = new StringWriter();
				marshaller.marshal(itemMasterMinimalNoun, stringWriter);

				LOGGER.info(stringWriter.toString());

				JsonReader jsonParser = Json.createReader(new StringReader(stringWriter.toString()));
				JsonObject jsonObject = jsonParser.readObject();

				final Message data = new Message.Builder().body(jsonObject).build();
				// emitting the message to the platform
				parameters.getEventEmitter().emitData(data);
			}

		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());

		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		}
	}

	private Quantity generateNewQuantity() {
		Quantity quantity = new Quantity();
		return quantity;
	}
}
