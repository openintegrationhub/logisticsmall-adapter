package io.logmall.actions;


import javax.json.JsonObject;
import javax.json.JsonString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ItemMasterService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.ChangeItemMaster;
import de.fraunhofer.ccl.bo.model.bod.RespondItemMaster;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.Constants;
import io.logmall.mapper.StandaloneBusinessObjectDocumentJsonMapper;



public class SendItemMasterBOD implements Module{
	private static final Logger LOGGER = LoggerFactory.getLogger(SendItemMasterBOD.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 * execution parameters
	 */

	@Override
	public void execute(final ExecutionParameters parameters) {

		LOGGER.info("Going to create new ItemMaster");

		try {

			// body contains the mapped data
			final JsonObject body = parameters.getMessage().getBody();

			// contains action's configuration
			final JsonObject configuration = parameters.getConfiguration();

			// access the value of the apiKey field defined in credentials section of
			// component.json
			// final JsonString apiKey = configuration.getJsonString("apiKey");
			// if (apiKey == null) {
			// throw new IllegalStateException("apiKey is required");
			// }

			JsonString serverURL = configuration.getJsonString(Constants.URL_CONFIGURATION_KEY);
			LOGGER.info("App Server URL: " + serverURL.getString());
			StandaloneBusinessObjectDocumentJsonMapper<ChangeItemMaster> changeItemMasterJsonMapper = new StandaloneBusinessObjectDocumentJsonMapper<>(ChangeItemMaster.class);
			ChangeItemMaster changeItemMaster = changeItemMasterJsonMapper.fromJson(body);
			LOGGER.info("Change action code: " + changeItemMaster.getVerb().getActionCode());
			ItemMasterService itemMasterService = ResteasyIntegration.newInstance().createClientProxy(ItemMasterService.class,
					serverURL.getString());
			RespondItemMaster response = (RespondItemMaster) itemMasterService.put(changeItemMaster);
			LOGGER.info("ItemMaster successfully created");
			LOGGER.info("Emitting data: " + response);
			StandaloneBusinessObjectDocumentJsonMapper<RespondItemMaster> respondItemMasterJsonMapper = new StandaloneBusinessObjectDocumentJsonMapper<>(RespondItemMaster.class);
			Message responseMessage = new Message.Builder().body(respondItemMasterJsonMapper.toJson(response)).build();
			parameters.getEventEmitter().emitData(responseMessage);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		} 
	}
	
}
