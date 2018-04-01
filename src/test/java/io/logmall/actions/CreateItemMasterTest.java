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

import org.junit.Ignore;
import org.junit.Test;

import io.elastic.api.ExecutionParameters;
import io.logmall.res.ResourceResolver;
import io.logmall.util.ExecutionParametersUtil;

public class CreateItemMasterTest {

	private static final String RESOURCE = "ChangeItemMaster.json";
	Scanner scanner = null;

	@Ignore
	@Test
	public void testExecute() {
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
			JsonObject jsonObject = (JsonObject) Json.createReader(new StringReader(changeItemMasterJSON)).read();
			ExecutionParameters executionParameters = ExecutionParametersUtil.getExecutionParameters(jsonObject);
			new SendItemMasterBOD().execute(executionParameters);

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
