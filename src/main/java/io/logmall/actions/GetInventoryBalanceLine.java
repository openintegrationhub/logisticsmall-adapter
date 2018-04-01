package io.logmall.actions;

import java.math.BigDecimal;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBException;

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
import io.logmall.bod.InventoryBalanceLineMinimal;
import io.logmall.bod.InventoryBalanceParameters;
import io.logmall.mapper.ParametersJsonMapper;

public class GetInventoryBalanceLine implements Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetInventoryBalanceLine.class);

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

			ParametersJsonMapper<InventoryBalanceParameters> parametersJsonMapper = new ParametersJsonMapper<>(
					InventoryBalanceParameters.class);
			InventoryBalanceParameters balanceParameters;
			balanceParameters = parametersJsonMapper.fromJson(parameters.getMessage().getBody());

			String jpql = "SELECT entity FROM InventoryBalance entity ORDER BY entity.creationDateTime DESC";

			GetByJpqlBODBuilder.Builder<InventoryBalance> bodBuilder = GetByJpqlBODBuilder
					.newInstance(InventoryBalance.class);
			bodBuilder.withFirstResult(0);
			bodBuilder.withMaxResults(1);
			bodBuilder.withQuery(jpql);
			BusinessObjectDocument<Get, InventoryBalance> requestBod = bodBuilder.build();

			InventoryBalanceService restService = ResteasyIntegration.newInstance()
					.createClientProxy(InventoryBalanceService.class, serverURL.getString());
			ShowInventoryBalance resultBod = (ShowInventoryBalance) restService.get(requestBod);

			JsonObject responseBody = getEventBody(resultBod, balanceParameters.getItemMaster());
			Message data;
			data = new Message.Builder().body(responseBody).build();
			parameters.getEventEmitter().emitData(data);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		} 
	}

	private JsonObject getEventBody(ShowInventoryBalance resultBod, String itemMasterStr) throws JAXBException, NotFoundException {

		InventoryBalanceLineMinimal balanceItem = null;

		if (resultBod.hasNouns() == false)
			throw new NotFoundException("No Inventory Balance found");

		InventoryBalance mallBalance = resultBod.getNounsForIteration().get(0);

		if (mallBalance.getItemLines() == null || mallBalance.getItemLines().isEmpty()) {
			throw new NotFoundException("No Inventory Balance Item found");
		}
		
		for (InventoryBalanceLine mallBalanceItem : mallBalance.getItemLines()) {
			if (mallBalanceItem == null) {
				continue;
			} else if (mallBalanceItem.getItem().getDisplayIdentifierId().equals(itemMasterStr) == false ) {
				continue;
			}
			Quantity availableQuantity = mallBalanceItem.getAvailableQuantity();
			ItemMaster itemMaster = mallBalanceItem.getItem().getMasterData();
			String itemNumber = itemMaster.getDisplayIdentifierId();
			BigDecimal quantityValue = availableQuantity.getValue();
			String quantityUnit = availableQuantity.getUnitName();

			balanceItem = new InventoryBalanceLineMinimal();
			balanceItem.setItemMaster(itemNumber);
			balanceItem.setQuantity(quantityValue);
			balanceItem.setUnit(quantityUnit);
			break;
		}

		if (balanceItem == null)
			throw new NotFoundException("No Inventory Balance Item found for Item Master " + itemMasterStr);

		ParametersJsonMapper<InventoryBalanceLineMinimal> mapper = new ParametersJsonMapper<>(InventoryBalanceLineMinimal.class);
		return mapper.toJson(balanceItem);
	}

}
