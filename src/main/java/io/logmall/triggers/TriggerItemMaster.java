package io.logmall.triggers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Module;
import io.logmall.actions.CreateItemMaster;
import io.logmall.res.ResourceResolver;
import io.logmall.util.ExecutionParametersUtil;

public class TriggerItemMaster implements Module {

	private static final Logger logger = LoggerFactory.getLogger(TriggerItemMaster.class);

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

			ExecutionParameters executionParameters = ExecutionParametersUtil.getExecutionParameters(
					(JsonObject) Json.createReader(new StringReader(changeItemMasterJSON)).read());
			new CreateItemMaster().execute(executionParameters);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}
