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
import de.fraunhofer.ccl.bo.model.bod.ChangeShipment;
import de.fraunhofer.ccl.bo.model.bod.verb.Respond;
import de.fraunhofer.ccl.bo.model.entity.shipment.Shipment;

public final class ShipmentJsonMapper {

	private static final Logger logger = LoggerFactory.getLogger(ShipmentJsonMapper.class);
	
	private JsonFactory jsonFactory;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;
	
	public ShipmentJsonMapper() throws JAXBException {
		this.jsonFactory = new JsonFactory(new BusinessObjectContextResolver());
		this.unmarshaller = jsonFactory.createUnmarshaller();
		this.marshaller = jsonFactory.createMarshaller(true);
	}
	
	public ChangeShipment fromJson(JsonObject json) throws JAXBException {
		logger.info("fromJson: " + json);
		StringReader stringReader = new StringReader(json.toString());
		try {
			return (ChangeShipment) unmarshaller.unmarshal(stringReader);
		} finally {
			stringReader.close();
		}		
	}
	
	public JsonObject fromBOD(BusinessObjectDocument<Respond, Shipment> bod) throws JAXBException {
		logger.info("fromBOD: " + bod);
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
