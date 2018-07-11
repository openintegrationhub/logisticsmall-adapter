package io.logmall.mapper;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.converter.xml.oagis.BusinessObjectContextResolver;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.verb.Verb;
import de.fraunhofer.ccl.bo.model.entity.common.BusinessObjectReferencable;



public class StandaloneBusinessObjectDocumentJsonMapper<T extends BusinessObjectDocument<? extends Verb, ? extends BusinessObjectReferencable>>
		extends ParametersJsonMapper<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneBusinessObjectDocumentJsonMapper.class);
	
	
	static JAXBContext staticBOJaxbContext;
	
	static {
		try {
			staticBOJaxbContext = new BusinessObjectContextResolver().getContext();
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public StandaloneBusinessObjectDocumentJsonMapper(Class<T> standaloneBusinessObjectDocumentClass)
			throws JAXBException {
		super(standaloneBusinessObjectDocumentClass);
		if (staticBOJaxbContext == null)
			throw new IllegalStateException("jaxbcontext must not be null");
		
	}

	@Override
	protected JAXBContext getJAXBContext(Class<T> forClass) throws JAXBException {
		return StandaloneBusinessObjectDocumentJsonMapper.staticBOJaxbContext;
	}
}