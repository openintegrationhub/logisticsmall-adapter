package io.logmall.actions;

import java.math.BigDecimal;

import javax.json.Json;
import javax.json.JsonObject;
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
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.logmall.bod.ConfigurationParameters;
import io.logmall.bod.InventoryBalanceLineMinimal;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.triggers.TriggerInventoryBalanceLine;

public class GetInventoryBalanceLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(TriggerInventoryBalanceLine.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */

	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Read InventoryBalance data");

		// contains action's configuration
		ConfigurationParameters configuration = null;
		try { 
			configuration = new ParametersJsonMapper<>(ConfigurationParameters.class)
					.fromJson(parameters.getConfiguration());
			LOGGER.info("App Server URL: " + configuration.getServerURLd());
			String jpql = "SELECT entity FROM InventoryBalance entity ORDER BY entity.creationDateTime DESC";

			GetByJpqlBODBuilder.Builder<InventoryBalance> bodBuilder = GetByJpqlBODBuilder
					.newInstance(InventoryBalance.class);
			bodBuilder.withFirstResult(0);
			bodBuilder.withMaxResults(1);
			bodBuilder.withQuery(jpql);
			BusinessObjectDocument<Get, InventoryBalance> requestBod = bodBuilder.build();

			InventoryBalanceService restService = null;
			restService = ResteasyIntegration.newInstance().createClientProxy(InventoryBalanceService.class,
					configuration.getServerURLd());
			ShowInventoryBalance resultBod = (ShowInventoryBalance) restService.get(requestBod);

			ParametersJsonMapper<InventoryBalanceLineMinimal> mapper = null;
			mapper = new ParametersJsonMapper<>(InventoryBalanceLineMinimal.class);

			if (resultBod.hasNouns()) {
				InventoryBalance mallBalance = resultBod.getNounsForIteration().get(0);

				JsonObject actualCreationDate = Json.createObjectBuilder()
						.add("creationDate", mallBalance.getCreationDateTime().toString()).build();
				LOGGER.info("SNAPSHOT VALUE: " + parameters.getSnapshot().toString());
				if (this.hasInventoryBalanceBeenUpdated(mallBalance, actualCreationDate, parameters.getSnapshot())) {
					for (InventoryBalanceLine mallBalanceItem : mallBalance.getItemLines()) {
						if (hasInvalidItemOrQuantity(mallBalanceItem)) {
							continue;
						}

						Quantity availableQuantity = mallBalanceItem.getAvailableQuantity();
						BigDecimal quantityValue = availableQuantity.getValue();
						String quantityUnit = availableQuantity.getUnitName();

						InventoryBalanceLineMinimal balanceItem = null;
						balanceItem = new InventoryBalanceLineMinimal();
						balanceItem.setItemMaster(mallBalanceItem.getItem().getMasterData().getDisplayIdentifierId());
						balanceItem.setQuantity(quantityValue);
						balanceItem.setUnit(quantityUnit);

						JsonObject responseBody = null;
						try {
							responseBody = mapper.toJson(balanceItem);
						} catch (JAXBException e) {
							LOGGER.error(e.getMessage(), e);
						}
						Message data = new Message.Builder().body(responseBody).build();
						LOGGER.info("inventory Balances: " + responseBody);
						parameters.getEventEmitter().emitData(data);
					}
					parameters.getEventEmitter().emitSnapshot(actualCreationDate);
				} else {
					emitDefaultItem(parameters, mapper);
				}
			} else {
				emitDefaultItem(parameters, mapper);
			}

		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		}
	}

	private static void emitDefaultItem(final ExecutionParameters parameters,
			ParametersJsonMapper<InventoryBalanceLineMinimal> mapper) {
		InventoryBalanceLineMinimal balanceItem = new InventoryBalanceLineMinimal();
		balanceItem.setItemMaster("noItem");
		balanceItem.setQuantity(new BigDecimal("0.0"));
		balanceItem.setUnit("noUnit");
		JsonObject responseBody = null;
		try {
			responseBody = mapper.toJson(balanceItem);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		}
		Message data = new Message.Builder().body(responseBody).build();
		parameters.getEventEmitter().emitData(data);
	}

	private boolean hasInventoryBalanceBeenUpdated(InventoryBalance mallBalance, JsonObject actualCreationDate,
			JsonObject snapshotCreationDate) {
		return mallBalance.getItemLines() != null && !mallBalance.getItemLines().isEmpty()
				&& !actualCreationDate.equals(snapshotCreationDate);
	}

	private static boolean hasInvalidItemOrQuantity(InventoryBalanceLine mallBalanceItem) {
		return mallBalanceItem == null || mallBalanceItem.getAvailableQuantity() == null
				|| mallBalanceItem.getItem() == null || mallBalanceItem.getItem().getMasterData() == null
				|| mallBalanceItem.getItem().getMasterData().getDisplayIdentifierId() == null;
	}
}
