package io.logmall.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import io.elastic.api.EventEmitter;
import io.elastic.api.EventEmitter.Callback;
import io.logmall.res.ResourceResolver;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;

public class CreateOrReplaceShipmentTest {
	Scanner scanner = null;

	@Test
	public void testExecute() {
		File file = new File(ResourceResolver.class.getClassLoader().getResource("ChangeShipment.json").getFile());
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer fileContents = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				fileContents.append(line);
				line = br.readLine();				
			}
			br.close();
			String changeShipmentJSON = fileContents.toString();

			final Message message = new Message.Builder()
					.body((JsonObject) Json.createReader(new StringReader(changeShipmentJSON)).read()).build();
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

			String s = "{\"serverURLd\": \"https://otc.logistics-mall.com/instance-repository-resteasy/rest\"}";
			JsonReader jsonParser = Json.createReader(new StringReader(s));

			ExecutionParameters.Builder executionParametersBuilder = new ExecutionParameters.Builder(message,
					eventEmitter);
			executionParametersBuilder.configuration(jsonParser.readObject());
			new CreateOrReplaceShipment().execute(executionParametersBuilder.build());

		}  
		catch (FileNotFoundException e) {
			Assert.fail("FileNotFoundException: "+e.getMessage() + " \n Cause: \n");
			 e.printStackTrace();
		} 
		catch (IOException e) {
			Assert.fail("IOException: "+e.getMessage() + " \n Cause: \n" + e.getCause());
			e.printStackTrace();
		}
		catch (Throwable e) {
			Assert.fail("Throwable: "+e.getMessage() + " \n Cause: \n" + e.getCause());
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

	}

}