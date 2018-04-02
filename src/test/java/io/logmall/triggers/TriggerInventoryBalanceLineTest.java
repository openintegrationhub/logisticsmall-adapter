package io.logmall.triggers;

import javax.json.JsonObject;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.ExecutionParameters;
import io.logmall.actions.CallbackListener;
import io.logmall.bod.ConfigurationParameters;
import io.logmall.bod.InventoryBalanceParameters;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.util.ExecutionParametersUtil;

public class TriggerInventoryBalanceLineTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(TriggerInventoryBalanceLineTest.class);

	/**
	 * Test reading inventory balance from mall application
	 */
	@Test
	public void testExecuteWithBody() throws JAXBException {
		CallbackListener<InventoryBalanceParameters> callbackListener = new CallbackListener<>();
		ConfigurationParameters configurationParameters = new ConfigurationParameters();
		configurationParameters.setItemMaster("1");
		ExecutionParameters executionParameters = ExecutionParametersUtil.getExecutionParameters(null,
				callbackListener.getCallBack(), configurationParameters);
		new TriggerInventoryBalanceLine().execute(executionParameters);
		InventoryBalanceParameters balanceParameters = callbackListener.wait(InventoryBalanceParameters.class);
		Assert.assertNotNull(balanceParameters);
	}

	@Test
	public void testMarshaller() throws JAXBException {
		ConfigurationParameters configurationParameters = new ConfigurationParameters();
		configurationParameters.setItemMaster("1");
		configurationParameters.setServerUrl(ConfigurationParameters.OTC_URL_CONFIGURATION_VALUE);
		JsonObject jsonObject = new ParametersJsonMapper<ConfigurationParameters>(ConfigurationParameters.class)
				.toJson(configurationParameters);
		LOGGER.info(jsonObject.toString());
	}

}
