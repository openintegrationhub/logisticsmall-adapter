package io.logmall.mapper;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParametersJsonMapper<T extends Serializable> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersJsonMapper.class);

	private Class<T> parametersClass;

	Unmarshaller unmarshaller;
	
	Marshaller marshaller;

	public ParametersJsonMapper(Class<T> parametersClass) throws JAXBException {
		this.parametersClass = parametersClass;
		
		JAXBContext jaxbContext = getJAXBContext(parametersClass);
		
		unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
	}

	protected JAXBContext getJAXBContext(Class<T> forClass) throws JAXBException {
		return JAXBContext.newInstance(parametersClass);
	}

	public T fromJson(JsonObject json) throws JAXBException {
		LOGGER.info("fromJson: " + json);

		StreamSource jsonStream = new StreamSource(new StringReader(json.toString()));
		try {
			return getUnmarshaller().unmarshal(jsonStream, parametersClass).getValue();
		} finally {

		}
	}
	
	public JsonObject toJson(T parameter) throws JAXBException {
		LOGGER.info("toJson: " + parameter);
		StringWriter stringWriter = new StringWriter();
		getMarshaller().marshal(parameter, stringWriter);
		JsonReader jsonReader = Json.createReader(new StringReader(stringWriter.toString()));
		try {
			return jsonReader.readObject();
		} finally {
			jsonReader.close();
		}
	}

	protected Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	protected Class<T> getParametersClass() {
		return parametersClass;
	}

	protected Marshaller getMarshaller() {
		return marshaller;
	}
	
	
	
	public void logAsJson(T object) throws JAXBException {
		StringWriter stringWriter = new StringWriter();
		getMarshaller().marshal(object, stringWriter);
		LOGGER.info(stringWriter.toString());
		
	}
}
