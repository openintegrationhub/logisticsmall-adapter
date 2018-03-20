package io.logmall.mapper;

import java.io.StringReader;

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.logmall.bod.ItemMasterMinimal;


public class ItemMasterMinimalJsonMapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemMasterMinimalJsonMapper.class);

	public ItemMasterMinimalJsonMapper() throws JAXBException {
		super();
	}

	public ItemMasterMinimal fromJson(JsonObject json) throws JAXBException {
		LOGGER.info("fromJson: " + json);
		
		StreamSource jsonStream = new StreamSource(new StringReader(json.toString()));
		try {
			Unmarshaller unmarshaller = JAXBContext.newInstance(ItemMasterMinimal.class).createUnmarshaller();
			unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
			return unmarshaller.unmarshal (jsonStream, ItemMasterMinimal.class).getValue();
			//return JAXB.unmarshal(stringReader, ItemMasterMinimal.class);
		} finally {
			// jsonStream.close();
		}
	}
}
