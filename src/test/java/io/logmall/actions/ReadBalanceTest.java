package io.logmall.actions;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Ignore;
import org.junit.Test;

import io.elastic.api.EventEmitter;
import io.elastic.api.EventEmitter.Callback;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.logmall.Constants;

public class ReadBalanceTest {
	
	/**
	 * Test reading inventory balance from mall application
	 */
	@Ignore
	@Test
	public void testExecute() {

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

		Message inputMessage = new Message.Builder().build();
		ExecutionParameters.Builder executionParametersBuilder = new ExecutionParameters.Builder(inputMessage,
				eventEmitter);

		JsonReader jsonParser = Json.createReader(new StringReader(Constants.OTC_URL_CONFIGURATION));
		JsonObject jsonUrl = jsonParser.readObject();

		executionParametersBuilder.configuration(jsonUrl);

		ExecutionParameters parameters = executionParametersBuilder.build();
		new ReadBalance().execute(parameters);
	}
}
