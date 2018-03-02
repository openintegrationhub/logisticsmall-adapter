package io.logmall.bod;

import java.io.StringReader;
import javax.json.JsonObject;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.model.bod.ChangeShipment;

public class ShipmentJsonMapper extends StandaloneBusinessObjectJsonMapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemMasterJsonMapper.class);
	public ShipmentJsonMapper() throws JAXBException {
		super();
	}

	public ChangeShipment fromJson(JsonObject json) throws JAXBException {
		LOGGER.info("fromJson: " + json);
		StringReader stringReader = new StringReader(json.toString());
		try {
			return (ChangeShipment) unmarshaller.unmarshal(stringReader);
		} finally {
			stringReader.close();
		}
	}
}
