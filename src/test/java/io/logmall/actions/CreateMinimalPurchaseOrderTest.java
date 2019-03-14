package io.logmall.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.ExecutionParameters;
import io.logmall.bod.PurchaseOrderLineMinimal;
import io.logmall.bod.PurchaseOrderMinimal;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.res.ResourceResolver;
import io.logmall.util.ExecutionParametersUtil;

@Ignore
public class CreateMinimalPurchaseOrderTest {
	private static final String RESOURCE = "MinimalPurchaseOrder.json";
	Scanner scanner = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateMinimalPurchaseOrderTest.class);

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
			String changeminimalPurchaseOrderJSON = fileContents.toString();

			JsonObject jsonObject;
			jsonObject = (JsonObject) Json.createReader(new StringReader(changeminimalPurchaseOrderJSON)).read();

			ParametersJsonMapper<PurchaseOrderMinimal> purchaseOrderMinimalJsonMapper = new ParametersJsonMapper<>(
					PurchaseOrderMinimal.class);
			PurchaseOrderMinimal purchaseOrderMinimal = purchaseOrderMinimalJsonMapper.fromJson(jsonObject);

			assertEquals("196615_NEU", purchaseOrderMinimal.getPurchaseOrderIdentifier());
			String randomOrderIdentifier = purchaseOrderMinimal.getPurchaseOrderIdentifier();
			// uncomment for creating new purchase order (In database!!!!)
			// randomOrderIdentifier=
			// purchaseOrderMinimal.getPurchaseOrderIdentifier().replaceAll("_NEU", "_" +
			// UUID.randomUUID().toString());
			// purchaseOrderMinimal.setPurchaseOrderIdentifier(randomOrderIdentifier);
			assertEquals("2018-02-15T11:01:12.369+01:00", purchaseOrderMinimal.getOrderDateTime().toString());
			assertEquals("Mustermann", purchaseOrderMinimal.getName());
			assertEquals("Max", purchaseOrderMinimal.getFirstName());
			assertEquals("Westring", purchaseOrderMinimal.getAddress().getStreet());
			assertEquals("23", purchaseOrderMinimal.getAddress().getNumber());
			assertEquals("41256", purchaseOrderMinimal.getAddress().getPostalCode());
			assertEquals("Dortmund", purchaseOrderMinimal.getAddress().getCity());
			assertEquals("DE", purchaseOrderMinimal.getAddress().getCountryCode());
			assertEquals("max@mustermann.com", purchaseOrderMinimal.getAddress().getEmail());
			assertEquals("01739862889520", purchaseOrderMinimal.getAddress().getPhone());

			for (PurchaseOrderLineMinimal purchaseOrderLine : purchaseOrderMinimal.getLines()) {
				assertEquals("Stk", purchaseOrderLine.getQuantityUnit());
				assertEquals("10", purchaseOrderLine.getOrderedQuantity().toString());
			}
			CallbackListener<PurchaseOrderMinimal> callbackListener = new CallbackListener<>();

			jsonObject = purchaseOrderMinimalJsonMapper.toJson(purchaseOrderMinimal);
			ExecutionParameters parameters = ExecutionParametersUtil.getExecutionParameters(jsonObject,
					callbackListener.getCallBack());

			new CreatePurchaseOrder().execute(parameters);
			PurchaseOrderMinimal responsePurchaseOrderMinimal = callbackListener.wait(PurchaseOrderMinimal.class);

			assertNotNull(responsePurchaseOrderMinimal);
			assertNotNull(responsePurchaseOrderMinimal.getAddress().getEmail());
			assertEquals(randomOrderIdentifier, responsePurchaseOrderMinimal.getPurchaseOrderIdentifier());
			assertEquals("max@mustermann.com", responsePurchaseOrderMinimal.getAddress().getEmail());

		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}
