package io.logmall.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Scanner;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.converter.xml.oagis.BusinessObjectContextResolver;
import de.fraunhofer.ccl.bo.converter.xml.oagis.XmlFactory;
import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ShipmentService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.ChangeShipment;
import de.fraunhofer.ccl.bo.model.bod.verb.Respond;
import de.fraunhofer.ccl.bo.model.entity.shipment.Shipment;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.res.ResourceResolver;

/**
 * Action to create a Shipment.
 */
public class CreateShipment implements Module {

	private static final Logger logger = LoggerFactory.getLogger(CreateShipment.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */
	@Override
	public void execute(final ExecutionParameters parameters) {

		logger.info("Going to create new shipment");
		Scanner scanner = null;
		try {

			// incoming message
			final Message message = parameters.getMessage();

			// body contains the mapped data
			final JsonObject body = message.getBody();

			// contains action's configuration
			final JsonObject configuration = parameters.getConfiguration();

			JsonString serverURL = configuration.getJsonString("serverURLd");
			logger.info("App Server URL: " + serverURL.getString());

			File file = new File(ResourceResolver.class.getClassLoader()
					.getResource("ChangeShipment.xml").getFile());
//			if(inputStream == null) {
//				inputStream = ResourceResolver.class.getClassLoader()
//						.getResourceAsStream("/io/logmall/res/ChangeShipment.xml");
//			} else {
//				throw new NullPointerException("InputStream Problem");
//			}
			

		    BufferedReader br = new BufferedReader(new FileReader(file));
		    StringBuffer fileContents = new StringBuffer();
		    String line = br.readLine();
		    while (line != null) {
		        fileContents.append(line);
		        line = br.readLine();
		    }

		    br.close();

			String changeShipmentXML = fileContents.toString();
			XmlFactory xmlFactory = new XmlFactory(new BusinessObjectContextResolver());
			Unmarshaller unmarshaller = xmlFactory.createUnmarshaller();
			ChangeShipment changeShipment = (ChangeShipment) unmarshaller
					.unmarshal(new StringReader(changeShipmentXML));

			// ShipmentJsonMapper shipmentJsonMapper = new ShipmentJsonMapper();
			// ChangeShipment changeShipment = shipmentJsonMapper.fromJson(body);
			ShipmentService shipmentService = ResteasyIntegration.newInstance().createClientProxy(ShipmentService.class,
					serverURL.getString());
			logger.info("Got ServerURL " + serverURL.getString());
			BusinessObjectDocument<Respond, Shipment> response = shipmentService.put(changeShipment);
			logger.info("Shipment successfully created");
			// final Message data = new
			// Message.Builder().body(shipmentJsonMapper.fromBOD(response)).build();
			logger.info("Emitting data: " + response);
			// emitting the message to the platform
			// parameters.getEventEmitter().emitData(data);

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

}
