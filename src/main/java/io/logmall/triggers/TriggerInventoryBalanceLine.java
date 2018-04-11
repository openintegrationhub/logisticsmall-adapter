package io.logmall.triggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Module;
import io.logmall.actions.GetInventoryBalanceLine;

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
			new GetInventoryBalanceLine().execute(parameters);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		} 
	}


}
