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
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.bod.ItemMasterJsonMapper;


public class CreateItemMaster implements Module{
	private static final Logger logger = LoggerFactory.getLogger(CreateItemMaster.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 * execution parameters
	 */

	@Override
	public void execute(final ExecutionParameters parameters) {

		logger.info("Going to create new ItemMaster");

		try {

			// incoming message
			final Message message = parameters.getMessage();

			// body contains the mapped data
			final JsonObject body = message.getBody();

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
			JsonString serverURL = configuration.getJsonString("serverURLd");
			logger.info("App Server URL: " + serverURL.getString());
			ItemMasterService itemMasterService = ResteasyIntegration.newInstance().createClientProxy(ItemMasterService.class,
					serverURL.getString());
			logger.info("Got ServerURL " + serverURL.getString());

			ItemMasterJsonMapper itemMasterJsonMapper = new ItemMasterJsonMapper();
			ChangeItemMaster changeItemMaster = itemMasterJsonMapper.fromJson(body);

			BusinessObjectDocument<Respond, ItemMaster> response = itemMasterService.put(changeItemMaster);
			logger.info("ItemMaster successfully created");
			logger.info("Emitting data: " + response);

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		}
	}
	
}
