package io.logmall.util;

import io.elastic.api.EventEmitter;
import io.elastic.api.EventEmitter.Callback;

public final class EventEmitterUtil {

	public static EventEmitter buildWithCallbackToLog() {
		Callback callback = CallbackUtil.buildCallbackToLog();
		EventEmitter.Builder eventEmitterBuilder = new EventEmitter.Builder();
		eventEmitterBuilder.onData(callback);
		eventEmitterBuilder.onError(callback);
		eventEmitterBuilder.onHttpReplyCallback(callback);
		eventEmitterBuilder.onRebound(callback);
		eventEmitterBuilder.onSnapshot(callback);
		eventEmitterBuilder.onUpdateKeys(callback);
		return eventEmitterBuilder.build();
		
	}
		
}
