package io.logmall.actions;

import java.io.Serializable;

import javax.json.JsonObject;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.entity.businessobject.StandaloneBusinessObject;
import io.elastic.api.EventEmitter.Callback;
import io.elastic.api.Message;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.mapper.StandaloneBusinessObjectDocumentJsonMapper;
import io.logmall.mapper.StandaloneBusinessObjectJsonMapper;

public class CallbackListener<T extends Serializable> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallbackListener.class);

	private final Object semaphore;

	private RuntimeException receivedException;
	private JsonObject received;

	private boolean interrupt = false;

	public CallbackListener() {
		semaphore = new Object();
	}

	public Callback getCallBack() {
		return new Callback() {
			@Override
			public void receive(Object data) {
				if (data instanceof Message) {
					Message message = (Message) data;
					synchronized (semaphore) {
						received = message.getBody();
						LOGGER.info("received " + received);
					}
				} else if (data instanceof RuntimeException) {
					receivedException = (RuntimeException) data;
					LOGGER.error("received client response failure: " + data);
				} else {
					synchronized (semaphore) {
						interrupt = true;
						LOGGER.info("interrupted");
					}
				}
			}
		};

	}

	/**
	 * 
	 * @param receiveType
	 * @return
	 * @throws RuntimeException if the callback receives an RuntimeException
	 */
	public T wait(Class<T> receiveType) throws RuntimeException {

		do {
			synchronized (semaphore) {
				if (received != null) {
					try {
						return marshallReceived(received, receiveType);
					} catch (JAXBException e) {
						LOGGER.error(e.getMessage(), e);
						return null;
					}
				} else if (receivedException != null) {
					throw receivedException;
				}

			}

			try {
				LOGGER.debug("Going to sleep...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.warn(e.getMessage());
				return null;
			}
		} while (!interrupt);

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private T marshallReceived(JsonObject received, Class<T> receivedType) throws JAXBException {
		ParametersJsonMapper<T> mapper;
		if (received == null)
			throw new NullPointerException();
		if (StandaloneBusinessObject.class.isAssignableFrom(receivedType))
			mapper = new StandaloneBusinessObjectJsonMapper(receivedType);
		else if (BusinessObjectDocument.class.isAssignableFrom(receivedType))
			mapper = new StandaloneBusinessObjectDocumentJsonMapper(receivedType);
		else
			mapper = new ParametersJsonMapper<>(receivedType);
		return mapper.fromJson(received);
	}

	public RuntimeException getReceivedException() {
		return receivedException;
	}

}
