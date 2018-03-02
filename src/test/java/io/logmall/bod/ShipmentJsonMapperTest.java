package io.logmall.bod;


import static org.junit.Assert.assertNotNull;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;


public class ShipmentJsonMapperTest {
	Unmarshaller unmarshaller;
	Marshaller marshaller;
	
	
	@Test
	public void shipmentJsonMapperTest() throws JAXBException {
		StandaloneBusinessObjectJsonMapper shipmentJsonMapper = new StandaloneBusinessObjectJsonMapper();
		assertNotNull(shipmentJsonMapper);		
	}
}
