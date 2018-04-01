package io.logmall.util;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.EventEmitter;
import io.elastic.api.EventEmitter.Callback;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.logmall.Constants;

public final class ExecutionParametersUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionParametersUtil.class);

	public static ExecutionParameters getExecutionParameters(JsonObject body) {
		return getExecutionParameters(body, null);
	}
	
	public static ExecutionParameters getExecutionParameters(JsonObject body, Callback callback) {
		if (callback == null) {
			callback = new Callback() {
				@Override
				public void receive(Object data) {
					if (data instanceof Message) {
						Message message = (Message) data;
						JsonObject jsonObject = message.getBody();
						LOGGER.info("received: " + jsonObject);
					}
				}
			};
		}
		EventEmitter.Builder eventEmitterBuilder = new EventEmitter.Builder();
		eventEmitterBuilder.onData(callback);
		eventEmitterBuilder.onError(callback);
		eventEmitterBuilder.onHttpReplyCallback(callback);
		eventEmitterBuilder.onRebound(callback);
		eventEmitterBuilder.onSnapshot(callback);
		eventEmitterBuilder.onUpdateKeys(callback);
		final EventEmitter eventEmitter = eventEmitterBuilder.build();

		Message inputMessage;
		if (body != null)
			inputMessage = new Message.Builder().body(body).build();
		else
			inputMessage = new Message.Builder().build();
		
		ExecutionParameters.Builder executionParametersBuilder = new ExecutionParameters.Builder(inputMessage,
				eventEmitter);

		JsonReader jsonParser = Json.createReader(new StringReader(Constants.OTC_URL_CONFIGURATION));
		JsonObject jsonUrl = jsonParser.readObject();

		executionParametersBuilder.configuration(jsonUrl);

		return executionParametersBuilder.build();
	}

	public static ExecutionParameters getExecutionParameters() {
		return getExecutionParameters(null);
	}
}
