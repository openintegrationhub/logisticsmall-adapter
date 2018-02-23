package io.logmall.actions;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Assert;
import org.junit.Test;

import io.elastic.api.EventEmitter;
import io.elastic.api.EventEmitter.Callback;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;

public class CreateShipmentTest {

	//@Test
	public void testExecute() {
		Message.Builder messageBuilder = new Message.Builder();
		final Message message = messageBuilder.build();
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

		String s = "{\"serverURLd\": \"https://otc.logistics-mall.com/instance-repository-resteasy/rest\"}";
		JsonReader jsonParser = Json.createReader(new StringReader(s));

		final JsonObject configuration = jsonParser.readObject();

		ExecutionParameters.Builder eBuilder = new ExecutionParameters.Builder(message, eventEmitter);
		eBuilder.configuration(configuration);
		ExecutionParameters parameters = eBuilder.build();
		try {
			new CreateShipment().execute(parameters);

		} catch (Throwable e) {
			Assert.fail(e.getMessage() + " \n Cause: \n" + e.getCause());
		}
	}

}
