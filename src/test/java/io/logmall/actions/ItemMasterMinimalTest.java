package io.logmall.actions;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import io.logmall.bod.ItemMasterMinimal;
import io.logmall.mapper.ItemMasterMinimalJsonMapper;
import io.logmall.res.ResourceResolver;

public class ItemMasterMinimalTest {

	private static final String RESOURCE = "ChangeMinimalItemMaster.json";
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
			String changeMinimalItemMaster = fileContents.toString();
			ItemMasterMinimalJsonMapper itemMasterMinimalJsonMapper = new ItemMasterMinimalJsonMapper();
			ItemMasterMinimal itemMasterMinimal = itemMasterMinimalJsonMapper
					.fromJson((JsonObject) Json.createReader(new StringReader(changeMinimalItemMaster)).read());

			assertEquals(" ", "5", itemMasterMinimal.getBaseQuantityClassificationUnit());

			assertEquals(" ", "1", itemMasterMinimal.getStatusCode());
			assertEquals(" ", "Zahnpasta 324F", itemMasterMinimal.getIdentifier());
			assertEquals(" ", "Samt und super!", itemMasterMinimal.getDescription());

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
		}

	}

}
