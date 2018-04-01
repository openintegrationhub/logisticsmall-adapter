package io.logmall.actions;

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.logmall.bod.InventoryBalanceMinimal;
import io.logmall.bod.InventoryBalanceParameters;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.util.ExecutionParametersUtil;

public class ReadBalanceTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadBalanceTest.class);
	
	/**
	 * Test reading inventory balance from mall application
	 */
//	@Ignore
	@Test
	public void testExecute() {
		CallbackListener<InventoryBalanceMinimal> callbackListener = new CallbackListener<>();
		new GetInventoryBalance().execute(ExecutionParametersUtil.getExecutionParameters(null, callbackListener.getCallBack()));
		InventoryBalanceMinimal balance = callbackListener.wait(InventoryBalanceMinimal.class);
		Assert.assertNotNull(balance);
	}
	
	@Test
	public void testExecuteWithBody() throws JAXBException {
		InventoryBalanceParameters parameters = new InventoryBalanceParameters();
		parameters.setItemMaster("1");
		JsonObject body = new ParametersJsonMapper<InventoryBalanceParameters>(InventoryBalanceParameters.class).toJson(parameters);
		CallbackListener<InventoryBalanceMinimal> callbackListener = new CallbackListener<>();
		new GetInventoryBalance().execute(ExecutionParametersUtil.getExecutionParameters(body,callbackListener.getCallBack()));
		InventoryBalanceMinimal balance = callbackListener.wait(InventoryBalanceMinimal.class);
		Assert.assertNotNull(balance);
	}
	
//	@Ignore
	@Test
	public void testUnmarshal() throws JAXBException {
		Marshaller marshaller = JAXBContext.newInstance(InventoryBalanceMinimal.class).createMarshaller();
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
	}
	
//	@Ignore
	@Test
	public void validateSchema() throws JAXBException {
		org.eclipse.persistence.jaxb.JAXBContext jaxbContext = (org.eclipse.persistence.jaxb.JAXBContext) JAXBContext.newInstance(InventoryBalanceMinimal.class);
	}
	

}
