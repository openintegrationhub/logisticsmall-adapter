package io.logmall.triggers;

import io.elastic.api.Module;
import io.elastic.api.EventEmitter.Callback;
import io.logmall.Constants;
import io.logmall.actions.CreateItemMaster;
import io.logmall.res.ResourceResolver;
import io.elastic.api.EventEmitter;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ItemMasterService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

public class GetItemMasterJaxRs implements Module {

	private static final Logger logger = LoggerFactory.getLogger(GetItemMasterJaxRs.class);

	private static final String RESOURCE = "ChangeItemMaster.json";
	Scanner scanner = null;


	/**
	 * Executes the trigger's logic by sending a request to the Logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */
	@Override
	 public void execute(final ExecutionParameters parameters) {
		File file = new File(ResourceResolver.class.getClassLoader().getResource(RESOURCE).getFile());
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer fileContents = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				fileContents.append(line);
				line = br.readLine();
			}
			br.close();
			String changeItemMasterJSON = fileContents.toString();

			final Message message = new Message.Builder()
					.body((JsonObject) Json.createReader(new StringReader(changeItemMasterJSON)).read()).build();
			Callback callback = new Callback() {
				@Override
				public void receive(Object data) {

				}
			};
			EventEmitter.Builder eventEmitterBuilder = new EventEmitter.Builder();
			eventEmitterBuilder.onData(callback);
			eventEmitterBuilder.onError(callback);
			eventEmitterBuilder.onHttpReplyCallback(callback);
			eventEmitterBuilder.onRebound(callback);
			eventEmitterBuilder.onSnapshot(callback);
			eventEmitterBuilder.onUpdateKeys(callback);
			final EventEmitter eventEmitter = eventEmitterBuilder.build();

			JsonReader jsonParser = Json.createReader(new StringReader(Constants.LOGATA_DEV_CONFIGURATION));

			ExecutionParameters.Builder executionParametersBuilder = new ExecutionParameters.Builder(message,
					eventEmitter);
			executionParametersBuilder.configuration(jsonParser.readObject());
			new CreateItemMaster().execute(executionParametersBuilder.build());

		} catch (FileNotFoundException e) {
			Assert.fail("FileNotFoundException: " + e.getMessage() + " \n Cause: \n");
			e.printStackTrace();
		} catch (IOException e) {
			Assert.fail("IOException: " + e.getMessage() + " \n Cause: \n" + e.getCause());
			e.printStackTrace();
		} catch (Throwable e) {
			Assert.fail("Throwable: " + e.getMessage() + " \n Cause: \n" + e.getCause());
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		

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
			//parameters.getEventEmitter().emitData(message);
	}}
}
