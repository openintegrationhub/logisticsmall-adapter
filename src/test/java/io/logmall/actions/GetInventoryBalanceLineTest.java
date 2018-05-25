package io.logmall.actions;

import javax.json.JsonObject;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.logmall.bod.InventoryBalanceLineMinimal;
import io.logmall.bod.InventoryBalanceParameters;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.util.ExecutionParametersUtil;

@Ignore
public class GetInventoryBalanceLineTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GetInventoryBalanceLineTest.class);
	
	/**
	 * Test reading inventory balance from mall application
	 */
	@Test
	public void testExecuteWithBody() throws JAXBException {
		InventoryBalanceParameters parameters = new InventoryBalanceParameters();
		parameters.setItemMaster("1");
		JsonObject body = new ParametersJsonMapper<InventoryBalanceParameters>(InventoryBalanceParameters.class).toJson(parameters);
		CallbackListener<InventoryBalanceLineMinimal> callbackListener = new CallbackListener<>();
		new GetInventoryBalanceLine().execute(ExecutionParametersUtil.getExecutionParameters(body,callbackListener.getCallBack()));
		InventoryBalanceLineMinimal balance = callbackListener.wait(InventoryBalanceLineMinimal.class);
		Assert.assertNotNull(balance);
	}
	@Test
	public void testMarshal() throws JAXBException {
		InventoryBalanceParameters parameters = new InventoryBalanceParameters();
		parameters.setItemMaster("1");
		ParametersJsonMapper<InventoryBalanceParameters> mapper = new ParametersJsonMapper<>(InventoryBalanceParameters.class);
		JsonObject body = mapper.toJson(parameters);
		InventoryBalanceParameters parametersFromJson = mapper.fromJson(body);
		Assert.assertNotNull(parametersFromJson.getItemMaster());
	}
	
}
