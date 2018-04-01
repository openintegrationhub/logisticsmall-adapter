package io.logmall.triggers;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.EventEmitter;
import io.elastic.api.EventEmitter.Callback;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.Constants;
import io.logmall.actions.CreateItemMaster;
import io.logmall.res.ResourceResolver;

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
			//Assert.fail("FileNotFoundException: " + e.getMessage() + " \n Cause: \n");
			e.printStackTrace();
		} catch (IOException e) {
			//Assert.fail("IOException: " + e.getMessage() + " \n Cause: \n" + e.getCause());
			e.printStackTrace();
		} catch (Throwable e) {
			//Assert.fail("Throwable: " + e.getMessage() + " \n Cause: \n" + e.getCause());
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}
