package io.logmall.triggers;

import io.elastic.api.Module;
import io.logmall.Constants;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ItemMasterService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

public class GetItemMasterJaxRs implements Module {

	private static final Logger logger = LoggerFactory.getLogger(GetItemMasterJaxRs.class);

	/**
	 * Executes the trigger's logic by sending a request to the Logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */
	@Override
	 public void execute(final ExecutionParameters parameters) {
		

//		// contains action's configuration
//		final JsonObject configuration = parameters.getConfiguration();
//
////		 access the value of the apiKey field defined in credentials section of
////		 component.json
//		// final JsonString apiKey = configuration.getJsonString("apiKey");
//		// if (apiKey == null) {
//		// throw new IllegalStateException("apiKey is required");
//		// }
////
////		// access the value of the status field defined in trigger's fields section of
////		// component.json
//		JsonString serverURL = configuration.getJsonString(Constants.URL_CONFIGURATION_KEY);
//		logger.info("App Server URL: " + serverURL.getString());
//		try {
//			ItemMasterService itemMasterService = ResteasyIntegration.newInstance().createClientProxy(ItemMasterService.class,
//					serverURL.getString());
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		
////
////        // access the value of the status field defined in trigger's fields section of component.json
////        final JsonString status = configuration.getJsonString("status");
////        if (status == null) {
////            throw new IllegalStateException("status field is required");
////        }
////        // access the value of the apiKey field defined in credentials section of component.json
////        final JsonString apiKey = configuration.getJsonString("apiKey");
////        if (apiKey == null) {
////            throw new IllegalStateException("apiKey is required");
////        }
////
////        logger.info("About to find pets by status {}", status.getString());
//
//		//
//		 final JsonArray items = ;
//		//
//		// logger.info("Got {} pets", pets.size());
//		//
//		// // emitting naked arrays is forbidden by the platform
//		 final JsonObject body = Json.createObjectBuilder()
//		 .add("pets", items)
//		 .build();
//		 final Message data
//		 = new Message.Builder().body(body).build();
//		//
//		// logger.info("Emitting data");
//		//
//		// // emitting the message to the platform
//		parameters.getEventEmitter().emitData(data);
	}
}
