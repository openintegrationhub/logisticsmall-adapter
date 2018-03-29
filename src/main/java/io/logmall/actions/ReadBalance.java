package io.logmall.actions;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.InventoryBalanceService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.ShowInventoryBalance;
import de.fraunhofer.ccl.bo.model.bod.builder.get.GetByJpqlBODBuilder;
import de.fraunhofer.ccl.bo.model.bod.verb.Get;
import de.fraunhofer.ccl.bo.model.entity.common.Quantity;
import de.fraunhofer.ccl.bo.model.entity.inventorybalance.InventoryBalance;
import de.fraunhofer.ccl.bo.model.entity.inventorybalance.InventoryBalanceLine;
import de.fraunhofer.ccl.bo.model.entity.itemmaster.ItemMaster;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.Constants;
import io.logmall.bod.BalanceMinimal;
import io.logmall.bod.BalanceMinimalItem;

public class ReadBalance implements Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadBalance.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */
	@Override
	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Read InventoryBalance data");
		try {
			// contains action's configuration
			final JsonObject configuration = parameters.getConfiguration();
			JsonString serverURL = configuration.getJsonString(Constants.URL_CONFIGURATION_KEY);
			LOGGER.info("App Server URL: " + serverURL.getString());

			GetByJpqlBODBuilder.Builder<InventoryBalance> bodBuilder = GetByJpqlBODBuilder
					.newInstance(InventoryBalance.class);
			bodBuilder.withFirstResult(0);
			bodBuilder.withMaxResults(1);
			bodBuilder.withQuery("SELECT entity FROM InventoryBalance entity ORDER BY entity.creationDateTime DESC");
			BusinessObjectDocument<Get, InventoryBalance> requestBod = bodBuilder.build();

			InventoryBalanceService restService = ResteasyIntegration.newInstance()
					.createClientProxy(InventoryBalanceService.class, serverURL.getString());
			ShowInventoryBalance resultBod = (ShowInventoryBalance) restService.get(requestBod);
			if (!resultBod.hasNouns()) {
				return;
			}

			InventoryBalance mallBalance = resultBod.getNounsForIteration().get(0);

			List<BalanceMinimalItem> externalBalanceItems = new ArrayList<>();

			if (mallBalance.getItemLines() != null) {
				for (InventoryBalanceLine mallBalanceItem : mallBalance.getItemLines()) {
					if (mallBalanceItem == null) {
						continue;
					}
					Quantity availableQuantity = mallBalanceItem.getAvailableQuantity();
					ItemMaster itemMaster = mallBalanceItem.getItem().getMasterData();
					String itemNumber = itemMaster.getDisplayIdentifierId();
					BigDecimal quantityValue = availableQuantity.getValue();
					String quantityUnit = availableQuantity.getUnitName();

					BalanceMinimalItem externalBalanceItem = new BalanceMinimalItem();
					externalBalanceItem.setItemMaster(itemNumber);
					externalBalanceItem.setQuantity(quantityValue);
					externalBalanceItem.setUnit(quantityUnit);
					externalBalanceItems.add(externalBalanceItem);
				}
			}
			LOGGER.info("Read InventoryBalance data. size=" + externalBalanceItems.size());

			BalanceMinimal externalBalance = new BalanceMinimal();
			externalBalance.setItems(externalBalanceItems);

			Marshaller marshaller = JAXBContext.newInstance(BalanceMinimal.class).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(externalBalance, stringWriter);

			JsonReader jsonParser = Json.createReader(new StringReader(stringWriter.toString()));
			JsonObject jsonObject = jsonParser.readObject();

			final Message data = new Message.Builder().body(jsonObject).build();

			// emitting the message to the platform
			parameters.getEventEmitter().emitData(data);

		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());

		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		}
	}

}
