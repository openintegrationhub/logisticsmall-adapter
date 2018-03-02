package io.logmall.bod;

import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.converter.xml.oagis.BusinessObjectContextResolver;
import de.fraunhofer.ccl.bo.converter.xml.oagis.JsonFactory;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.verb.Respond;
import de.fraunhofer.ccl.bo.model.entity.businessobject.StandaloneBusinessObject;

public class StandaloneBusinessObjectJsonMapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneBusinessObjectJsonMapper.class);
	private Marshaller marshaller;
	private JsonFactory jsonFactory;
	protected static Unmarshaller unmarshaller;
	
	public StandaloneBusinessObjectJsonMapper() throws JAXBException {
		this.jsonFactory = new JsonFactory(new BusinessObjectContextResolver());
		unmarshaller = jsonFactory.createUnmarshaller();
		this.marshaller = jsonFactory.createMarshaller(true);
	}
	
	public JsonObject fromBOD(BusinessObjectDocument<Respond, StandaloneBusinessObject> bod) throws JAXBException {
		LOGGER.info("fromBOD: " + bod);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(bod, stringWriter);
		JsonReader jsonReader = Json.createReader(new StringReader(stringWriter.toString()));
		try {
			return jsonReader.readObject();
		} finally {
			jsonReader.close();
		}
	}
	
	
}
