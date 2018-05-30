package io.logmall.mapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import de.fraunhofer.ccl.bo.converter.xml.oagis.BusinessObjectContextResolver;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.verb.Verb;

import de.fraunhofer.ccl.bo.model.entity.common.aspect.BusinessObjectReferencable;

public class StandaloneBusinessObjectDocumentJsonMapper<T extends BusinessObjectDocument<? extends Verb, ? extends BusinessObjectReferencable>>
		extends ParametersJsonMapper<T> {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(StandaloneBusinessObjectDocumentJsonMapper.class);
	BusinessObjectContextResolver businessObjectContextResolver = new BusinessObjectContextResolver();
	JAXBContext staticBOJaxbContext = businessObjectContextResolver.getContext();

	public StandaloneBusinessObjectDocumentJsonMapper(Class<T> standaloneBusinessObjectDocumentClass)
			throws JAXBException {
		super(standaloneBusinessObjectDocumentClass);
	}

	@Override
	protected JAXBContext getJAXBContext(Class<T> forClass) throws JAXBException {
		return staticBOJaxbContext;
	}
}