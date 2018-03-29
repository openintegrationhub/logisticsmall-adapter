package io.logmall.actions;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.EventEmitter;
import io.elastic.api.EventEmitter.Callback;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.logmall.Constants;
import io.logmall.bod.BalanceMinimal;

public class ReadBalanceTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadBalanceTest.class);
	
	/**
	 * Test reading inventory balance from mall application
	 */
	@Ignore
	@Test
	public void testExecute() {

		Callback callback = new Callback() {
			@Override
			public void receive(Object data) {
				if (data instanceof Message) {
					Message message = (Message) data;
					JsonObject jsonObject = message.getBody();
					LOGGER.info("received: " + jsonObject);
				}
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

		Message inputMessage = new Message.Builder().build();
		ExecutionParameters.Builder executionParametersBuilder = new ExecutionParameters.Builder(inputMessage,
				eventEmitter);

		JsonReader jsonParser = Json.createReader(new StringReader(Constants.OTC_URL_CONFIGURATION));
		JsonObject jsonUrl = jsonParser.readObject();

		executionParametersBuilder.configuration(jsonUrl);

		ExecutionParameters parameters = executionParametersBuilder.build();
		new ReadBalance().execute(parameters);
	}
	
	@Ignore
	@Test
	public void testUnmarshal() throws JAXBException {
		Marshaller marshaller = JAXBContext.newInstance(BalanceMinimal.class).createMarshaller();
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
	}
}
