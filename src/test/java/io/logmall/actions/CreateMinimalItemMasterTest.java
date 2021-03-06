package io.logmall.actions;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.model.bod.RespondItemMaster;
import io.elastic.api.ExecutionParameters;
import io.logmall.bod.ItemMasterMinimal;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.res.ResourceResolver;
import io.logmall.util.ExecutionParametersUtil;
@Ignore
public class CreateMinimalItemMasterTest {
	private static final String RESOURCE = "MinimalItemMaster.json";
	Scanner scanner = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateMinimalItemMasterTest.class);


	@Test
	public void testExecute() {
		File file = new File(ResourceResolver.class.getClassLoader().getResource(RESOURCE).getFile());
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer fileContents = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				fileContents.append(line);
				line = br.readLine();
			}
			br.close();
			String changeMinimalItemMasterJSON = fileContents.toString();

			JsonObject jsonObject;
			jsonObject = (JsonObject) Json.createReader(new StringReader(changeMinimalItemMasterJSON)).read();

			ParametersJsonMapper<ItemMasterMinimal> itemMasterMinimalJsonMapper = new ParametersJsonMapper<>(ItemMasterMinimal.class);
			ItemMasterMinimal itemMasterMinimal = itemMasterMinimalJsonMapper.fromJson(jsonObject);
			assertEquals("5", itemMasterMinimal.getBaseQuantityClassificationUnit());
			
			CallbackListener<RespondItemMaster> callbackListener = new CallbackListener<>();
			ExecutionParameters parameters = ExecutionParametersUtil.getExecutionParameters(jsonObject,callbackListener.getCallBack());
			
			itemMasterMinimal = itemMasterMinimalJsonMapper.fromJson(parameters.getMessage().getBody());
			assertEquals("","5", itemMasterMinimal.getBaseQuantityClassificationUnit());
			
			new CreateItemMaster().execute(parameters);
			callbackListener.wait(RespondItemMaster.class);

		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	@Test
	public void testMarshal() throws JAXBException {
		ItemMasterMinimal itemMasterMinimal = new ItemMasterMinimal();
		itemMasterMinimal.setBaseQuantityClassificationUnit("4");
		itemMasterMinimal.setDescription("bla");
		itemMasterMinimal.setIdentifier("name");
		itemMasterMinimal.setStatusCode("1");

		Marshaller marshaller = JAXBContext.newInstance(ItemMasterMinimal.class).createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(itemMasterMinimal, stringWriter);
		LOGGER.info(stringWriter.toString());
	}
	
}
