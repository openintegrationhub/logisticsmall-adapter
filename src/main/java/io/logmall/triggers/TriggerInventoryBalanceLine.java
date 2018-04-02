package io.logmall.triggers;

import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.bod.ConfigurationParameters;
import io.logmall.bod.InventoryBalanceParameters;
import io.logmall.mapper.ParametersJsonMapper;

public class TriggerInventoryBalanceLine implements Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(TriggerInventoryBalanceLine.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */
	@Override
	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Read InventoryBalance data");
		try {
			// contains trigger's configuration
			ConfigurationParameters configuration = new ParametersJsonMapper<>(ConfigurationParameters.class).fromJson(parameters.getConfiguration());
			LOGGER.info("App Server URL: " + configuration.getServerUrl());
			
			String itemMaster = configuration.getItemMaster();
			JsonObject responseBody = getEventBody(itemMaster);
			Message data;
			data = new Message.Builder().body(responseBody).build();
			parameters.getEventEmitter().emitData(data);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		} 
	}
	
	private JsonObject getEventBody(String itemMasterStr) throws JAXBException, NotFoundException {

		if (itemMasterStr == null)
			throw new NotFoundException("No item master");
		
		InventoryBalanceParameters parameters = new InventoryBalanceParameters();
		parameters.setItemMaster(itemMasterStr);
		
		ParametersJsonMapper<InventoryBalanceParameters> mapper = new ParametersJsonMapper<>(InventoryBalanceParameters.class);
		return mapper.toJson(parameters);
	}



}
