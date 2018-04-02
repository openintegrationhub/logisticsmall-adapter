package io.logmall.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
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
import io.logmall.bod.ConfigurationParameters;
import io.logmall.bod.InventoryBalanceLineMinimal;
import io.logmall.bod.InventoryBalanceMinimal;
import io.logmall.bod.InventoryBalanceParameters;
import io.logmall.mapper.ParametersJsonMapper;

public class GetInventoryBalance implements Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetInventoryBalance.class);

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
			ConfigurationParameters configuration = new ParametersJsonMapper<>(ConfigurationParameters.class).fromJson(parameters.getConfiguration());
			LOGGER.info("App Server URL: " + configuration.getServerURLd());
			ParametersJsonMapper<InventoryBalanceParameters> parametersJsonMapper = new ParametersJsonMapper<>(
					InventoryBalanceParameters.class);
			if (parameters.getMessage().getBody() != null) {
				InventoryBalanceParameters balanceParameters = parametersJsonMapper.fromJson(parameters.getMessage().getBody());
				// do somethin
			}

			String jpql = "SELECT entity FROM InventoryBalance entity ORDER BY entity.creationDateTime DESC";

			GetByJpqlBODBuilder.Builder<InventoryBalance> bodBuilder = GetByJpqlBODBuilder
					.newInstance(InventoryBalance.class);
			bodBuilder.withFirstResult(0);
			bodBuilder.withMaxResults(1);
			bodBuilder.withQuery(jpql);
			BusinessObjectDocument<Get, InventoryBalance> requestBod = bodBuilder.build();

			InventoryBalanceService restService = ResteasyIntegration.newInstance()
					.createClientProxy(InventoryBalanceService.class, configuration.getServerURLd());
			ShowInventoryBalance resultBod = (ShowInventoryBalance) restService.get(requestBod);

			JsonObject responseBody = getEventBody(resultBod);

			Message data;
			if (responseBody != null)
				data = new Message.Builder().body(responseBody).build();
			else
				data = new Message.Builder().build();
			// emitting the message to the platform
			parameters.getEventEmitter().emitData(data);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		}
	}

	private JsonObject getEventBody(ShowInventoryBalance resultBod) throws JAXBException, NotFoundException {

		if (resultBod.hasNouns() == false)
			throw new NotFoundException("No Inventory Balance found");

		InventoryBalance mallBalance = resultBod.getNounsForIteration().get(0);

		List<InventoryBalanceLineMinimal> externalBalanceItems = new ArrayList<>();

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

				InventoryBalanceLineMinimal externalBalanceItem = new InventoryBalanceLineMinimal();
				externalBalanceItem.setItemMaster(itemNumber);
				externalBalanceItem.setQuantity(quantityValue);
				externalBalanceItem.setUnit(quantityUnit);
				externalBalanceItems.add(externalBalanceItem);
			}
		}

		InventoryBalanceMinimal externalBalance = new InventoryBalanceMinimal();
		externalBalance.setItems(externalBalanceItems);

		ParametersJsonMapper<InventoryBalanceMinimal> mapper = new ParametersJsonMapper<>(InventoryBalanceMinimal.class);
		return mapper.toJson(externalBalance);
	}

}
