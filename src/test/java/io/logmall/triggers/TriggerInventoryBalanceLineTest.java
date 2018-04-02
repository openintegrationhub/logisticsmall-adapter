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
		Assert.assertEquals("1", balanceParameters.getItemMaster());
	}

	@Test
	public void testMarshaller() throws JAXBException {
		ConfigurationParameters configurationParameters = new ConfigurationParameters();
		configurationParameters.setItemMaster("1");
		configurationParameters.setServerURLd(ConfigurationParameters.OTC_URL_CONFIGURATION_VALUE);
		ParametersJsonMapper<ConfigurationParameters> mapper = new ParametersJsonMapper<>(ConfigurationParameters.class);
		JsonObject jsonObject = mapper.toJson(configurationParameters);
		ConfigurationParameters marshalledConfigurationParamters = mapper.fromJson(jsonObject);
		Assert.assertEquals(configurationParameters.getItemMaster(), marshalledConfigurationParamters.getItemMaster());
		
		
	}
	
	@Test
	public void testMarshallerInventoryBalanceParamters() throws JAXBException {
		InventoryBalanceParameters inventoryBalanceParameters = new InventoryBalanceParameters();
		inventoryBalanceParameters.setItemMaster("1");
		ParametersJsonMapper<InventoryBalanceParameters> mapper2 = new ParametersJsonMapper<>(InventoryBalanceParameters.class);
		JsonObject jsonObject2 = mapper2.toJson(inventoryBalanceParameters);
		LOGGER.info(jsonObject2.toString());
		InventoryBalanceParameters balanceParameters = mapper2.fromJson(jsonObject2);
		Assert.assertNotNull(balanceParameters);
		Assert.assertEquals("1", balanceParameters.getItemMaster());
	}

}
