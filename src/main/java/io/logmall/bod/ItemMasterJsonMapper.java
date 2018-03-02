package io.logmall.bod;

import java.io.StringReader;

import javax.json.JsonObject;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.model.bod.ChangeItemMaster;

public class ItemMasterJsonMapper extends StandaloneBusinessObjectJsonMapper{
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemMasterJsonMapper.class);

	public ItemMasterJsonMapper() throws JAXBException {
		super();
	}

	public ChangeItemMaster fromJson(JsonObject json) throws JAXBException {
		LOGGER.info("fromJson: " + json);
		StringReader stringReader = new StringReader(json.toString());
		try {
			return (ChangeItemMaster) unmarshaller.unmarshal(stringReader);
		} finally {
			stringReader.close();
		}
	}
}
