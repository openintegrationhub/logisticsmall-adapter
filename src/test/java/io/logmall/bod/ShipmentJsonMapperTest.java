package io.logmall.bod;


import static org.junit.Assert.assertNotNull;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import io.logmall.bod.ShipmentJsonMapper;


public class ShipmentJsonMapperTest {
	Unmarshaller unmarshaller;
	Marshaller marshaller;
	
	
	@Test
	public void shipmentJsonMapperTest() throws JAXBException {
		ShipmentJsonMapper shipmentJsonMapper = new ShipmentJsonMapper();
		assertNotNull(shipmentJsonMapper);		
	}
}
