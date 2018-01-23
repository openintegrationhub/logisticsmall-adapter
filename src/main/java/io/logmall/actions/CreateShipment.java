package io.logmall.actions;

import javax.json.JsonObject;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ShipmentService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.ChangeShipment;
import de.fraunhofer.ccl.bo.model.bod.verb.Respond;
import de.fraunhofer.ccl.bo.model.entity.shipment.Shipment;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.bod.ShipmentJsonMapper;

/**
 * Action to create a Shipment.
 */
public class CreateShipment implements Module {
    
	private static final Logger logger = LoggerFactory.getLogger(CreateShipment.class);

    /**
     * Executes the actions's logic by sending a request to the logmall API and emitting response to the platform.
     *
     * @param parameters execution parameters
     */
    @Override
    public void execute(final ExecutionParameters parameters) {
        logger.info("Going to create new shipment");
        // incoming message
        final Message message = parameters.getMessage();

        // body contains the mapped data
        final JsonObject body = message.getBody();

        // contains action's configuration
        final JsonObject configuration = parameters.getConfiguration();

        try {
        	ShipmentJsonMapper shipmentJsonMapper = new ShipmentJsonMapper();
        	ChangeShipment changeShipment = shipmentJsonMapper.fromJson(body);
			ShipmentService shipmentService = ResteasyIntegration.newInstance().createClientProxy(ShipmentService.class, "");
			BusinessObjectDocument<Respond, Shipment> response = shipmentService.put(changeShipment);
			logger.info("Shipment successfully created");
	        final Message data = new Message.Builder().body(shipmentJsonMapper.fromBOD(response)).build();
	        logger.info("Emitting data: " + response);
	        // emitting the message to the platform
	        parameters.getEventEmitter().emitData(data);

        } catch (JAXBException e) {
			logger.error(e.getMessage(),e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		}
    }
    
 
}
