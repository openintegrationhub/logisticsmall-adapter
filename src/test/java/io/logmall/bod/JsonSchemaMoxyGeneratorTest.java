package io.logmall.bod;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.model.entity.shipment.Shipment;

public class JsonSchemaMoxyGeneratorTest {

	private static final Logger logger = LoggerFactory.getLogger(JsonSchemaMoxyGeneratorTest.class);

	JsonSchemaMoxyGenerator underTest = new JsonSchemaMoxyGenerator();
	
	@Test
	public void testGenerate() {
		try {
			String schema = underTest.generate(Shipment.class);
			logger.info(schema);
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
