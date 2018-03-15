package io.logmall.actions;


import javax.json.JsonObject;
import javax.json.JsonString;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ItemMasterService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.ChangeItemMaster;
import de.fraunhofer.ccl.bo.model.bod.verb.Respond;
import de.fraunhofer.ccl.bo.model.entity.itemmaster.ItemMaster;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Module;
import io.logmall.Constants;
import io.logmall.bod.ItemMasterJsonMapper;



public class CreateItemMaster implements Module{
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateItemMaster.class);

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

			// access the value of the status field defined in trigger's fields section of
			// component.json
			JsonString serverURL = configuration.getJsonString(Constants.URL_CONFIGURATION_KEY);
			LOGGER.info("App Server URL: " + serverURL.getString());
			ItemMasterService itemMasterService = ResteasyIntegration.newInstance().createClientProxy(ItemMasterService.class,
					serverURL.getString());
			ItemMasterJsonMapper itemMasterJsonMapper = new ItemMasterJsonMapper();
			ChangeItemMaster changeItemMaster = itemMasterJsonMapper.fromJson(body);
			LOGGER.info("Change action code: " + changeItemMaster.getVerb().getActionCode());
			BusinessObjectDocument<Respond, ItemMaster> response = itemMasterService.put(changeItemMaster);
			LOGGER.info("ItemMaster successfully created");
			LOGGER.info("Emitting data: " + response);

		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
			
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		}
	}
	
}
