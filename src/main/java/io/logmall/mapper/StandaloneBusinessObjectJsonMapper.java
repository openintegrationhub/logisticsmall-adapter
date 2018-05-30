package io.logmall.mapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import de.fraunhofer.ccl.bo.converter.xml.oagis.BusinessObjectContextResolver;
import de.fraunhofer.ccl.bo.model.entity.businessobject.StandaloneBusinessObject;

public class StandaloneBusinessObjectJsonMapper<T extends StandaloneBusinessObject> extends ParametersJsonMapper<T> {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(StandaloneBusinessObjectJsonMapper.class);

	BusinessObjectContextResolver businessObjectContextResolver = new BusinessObjectContextResolver();
	JAXBContext staticBOJaxbContext = businessObjectContextResolver.getContext();

	public StandaloneBusinessObjectJsonMapper(Class<T> standaloneBusinessObjectClass) throws JAXBException {
		super(standaloneBusinessObjectClass);
	}

	@Override
	protected JAXBContext getJAXBContext(Class<T> forClass) throws JAXBException {
		return staticBOJaxbContext;
	}
}
