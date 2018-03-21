package io.logmall.actions;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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

import io.elastic.api.EventEmitter;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.EventEmitter.Callback;
import io.logmall.Constants;
import io.logmall.bod.ItemMasterMinimal;
import io.logmall.mapper.ItemMasterMinimalJsonMapper;
import io.logmall.res.ResourceResolver;

public class CreateMinimalItemMasterTest {
	private static final String RESOURCE = "ChangeMinimalItemMaster.json";
	Scanner scanner = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateMinimalItemMasterTest.class);

	@Ignore
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

			ItemMasterMinimalJsonMapper itemMasterMinimalJsonMapper = new ItemMasterMinimalJsonMapper();
			ItemMasterMinimal itemMasterMinimal = itemMasterMinimalJsonMapper.fromJson(jsonObject);
			assertEquals("5", itemMasterMinimal.getBaseQuantityClassificationUnit());

			final Message message = new Message.Builder().body(jsonObject).build();
			Callback callback = new Callback() {
				@Override
				public void receive(Object data) {

				}
			};
			EventEmitter.Builder eventEmitterBuilder = new EventEmitter.Builder();
			eventEmitterBuilder.onData(callback);
			eventEmitterBuilder.onError(callback);
			eventEmitterBuilder.onHttpReplyCallback(callback);
			eventEmitterBuilder.onRebound(callback);
			eventEmitterBuilder.onSnapshot(callback);
			eventEmitterBuilder.onUpdateKeys(callback);
			final EventEmitter eventEmitter = eventEmitterBuilder.build();

			JsonReader jsonParser = Json.createReader(new StringReader(Constants.OTC_URL_CONFIGURATION));
			jsonObject = jsonParser.readObject();
			
			ExecutionParameters.Builder executionParametersBuilder = new ExecutionParameters.Builder(message,
					eventEmitter);
			
			executionParametersBuilder.configuration(jsonObject);

			itemMasterMinimal = itemMasterMinimalJsonMapper.fromJson(message.getBody());
			assertEquals("","5", itemMasterMinimal.getBaseQuantityClassificationUnit());

			new CreateItemMaster().execute(executionParametersBuilder.build());

		} catch (FileNotFoundException e) {
			Assert.fail("FileNotFoundException: " + e.getMessage() + " \n Cause: \n");
			e.printStackTrace();
		} catch (IOException e) {
			Assert.fail("IOException: " + e.getMessage() + " \n Cause: \n" + e.getCause());
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

	}
	@Ignore
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
