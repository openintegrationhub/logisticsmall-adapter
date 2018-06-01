package io.logmall.mapper;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.converter.xml.oagis.BusinessObjectContextResolver;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.verb.Verb;

import de.fraunhofer.ccl.bo.model.entity.common.aspect.BusinessObjectReferencable;

public class StandaloneBusinessObjectDocumentJsonMapper<T extends BusinessObjectDocument<? extends Verb, ? extends BusinessObjectReferencable>>
		extends ParametersJsonMapper<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneBusinessObjectDocumentJsonMapper.class);
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(StandaloneBusinessObjectDocumentJsonMapper.class);
	
	BusinessObjectContextResolver businessObjectContextResolver = new BusinessObjectContextResolver();
	JAXBContext staticBOJaxbContext = businessObjectContextResolver.getContext();

	public StandaloneBusinessObjectDocumentJsonMapper(Class<T> standaloneBusinessObjectDocumentClass)
			throws JAXBException {
		super(standaloneBusinessObjectDocumentClass);
		StringWriter stringWriter = new StringWriter();
		getMarshaller().marshal(standaloneBusinessObjectDocumentClass, stringWriter);
		String jsonPayload = stringWriter.toString();
		LOGGER.info("--------------------------- JSON Payload standaloneBusinessObjectDocumentClass-------------------- \n" + jsonPayload + "\n------------------------------------------------------------");
	
	}

	@Override
	protected JAXBContext getJAXBContext(Class<T> forClass) throws JAXBException {
		return staticBOJaxbContext;
	}
}