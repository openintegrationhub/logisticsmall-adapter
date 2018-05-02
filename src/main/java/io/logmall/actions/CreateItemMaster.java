package io.logmall.actions;

import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ItemMasterService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.ChangeItemMaster;
import de.fraunhofer.ccl.bo.model.bod.RespondItemMaster;
import de.fraunhofer.ccl.bo.model.bod.builder.change.CreateOrReplaceBODBuilder;
import de.fraunhofer.ccl.bo.model.bod.verb.Change;
import de.fraunhofer.ccl.bo.model.entity.common.PredefinedMeasureUnitType;
import de.fraunhofer.ccl.bo.model.entity.common.Quantity;
import de.fraunhofer.ccl.bo.model.entity.common.QuantityClassification;
import de.fraunhofer.ccl.bo.model.entity.common.Status;
import de.fraunhofer.ccl.bo.model.entity.itemmaster.ItemMaster;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.bod.ConfigurationParameters;
import io.logmall.bod.ItemMasterMinimal;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.mapper.StandaloneBusinessObjectDocumentJsonMapper;
import io.logmall.util.MeasureUtil;

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
			ParametersJsonMapper<ItemMasterMinimal> itemMasterMinimalJsonMapper = new ParametersJsonMapper<>(
					ItemMasterMinimal.class);
			ItemMasterMinimal itemMasterMinimal = itemMasterMinimalJsonMapper.fromJson(body);

			ChangeItemMaster requestBod = createItemMaster(itemMasterMinimal);
			@SuppressWarnings({ "rawtypes", "unchecked" })
			StandaloneBusinessObjectDocumentJsonMapper<BusinessObjectDocument<Change, ItemMaster>> standaloneBusinessObjectDocumentJsonMapper = new StandaloneBusinessObjectDocumentJsonMapper(
					requestBod.getClass());
			standaloneBusinessObjectDocumentJsonMapper.logAsJson(requestBod);
			// ----- setup communication -----
			// contains action's configuration
			ConfigurationParameters configuration = new ParametersJsonMapper<>(ConfigurationParameters.class).fromJson(parameters.getConfiguration());
			LOGGER.info("App Server URL: " + configuration.getServerURLd());
			ItemMasterService itemMasterService = ResteasyIntegration.newInstance()
					.createClientProxy(ItemMasterService.class, configuration.getServerURLd());
			RespondItemMaster response = (RespondItemMaster) itemMasterService.put(requestBod);
			
			
			LOGGER.info("MinimalItemMaster successfully created:\t" + response.toString());
			JsonObject responseBody = getEventBody(response);
			Message data = new Message.Builder().body(responseBody).build();
			// emitting the message to the platform
			parameters.getEventEmitter().emitData(data);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		}
	}

	private JsonObject getEventBody(RespondItemMaster response) throws JAXBException, NotFoundException {
		List<ItemMaster> nouns = response.getNounsForIteration();
		if (!nouns.isEmpty()) {
			ItemMaster itemMasterNoun = nouns.get(0);
			ItemMasterMinimal itemMasterMinimalNoun = new ItemMasterMinimal();
			itemMasterMinimalNoun
					.setBaseQuantityClassificationUnit(itemMasterNoun.getBaseQuantityClassification().getUnit());
			itemMasterMinimalNoun.setDescription(itemMasterNoun.getDescription());
			itemMasterMinimalNoun.setIdentifier(itemMasterNoun.getDisplayIdentifierId());
			itemMasterMinimalNoun.setStatusCode(itemMasterNoun.getStatus().getCode());
			ParametersJsonMapper<ItemMasterMinimal> mapper = new ParametersJsonMapper<>(ItemMasterMinimal.class);
			JsonObject jsonObject = mapper.toJson(itemMasterMinimalNoun);
			return jsonObject;
		} else
			throw new NotFoundException("No Item Master found");
	}

	private ChangeItemMaster createItemMaster(ItemMasterMinimal itemMasterMinimal) {

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
		ChangeItemMaster requestBod = (ChangeItemMaster) createBODBuilderItemMaster.build();
		return requestBod;
	}

	private Quantity generateNewQuantity() {
		Quantity quantity = new Quantity();
		return quantity;
	}
}
