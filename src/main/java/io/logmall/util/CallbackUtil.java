package io.logmall.util;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.EventEmitter.Callback;
import io.elastic.api.Message;

public final class CallbackUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallbackUtil.class);

	private CallbackUtil() {};
	
	public static Callback buildCallbackToLog() {
		return new Callback() {
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
}
