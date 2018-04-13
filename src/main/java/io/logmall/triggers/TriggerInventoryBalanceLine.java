package io.logmall.triggers;

import java.math.BigDecimal;

import javax.json.JsonObject;
import javax.xml.bind.JAXBException;

import org.joda.time.DateTime;
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
import io.elastic.api.Module;
import io.logmall.bod.ConfigurationParameters;
import io.logmall.bod.InventoryBalanceLineMinimal;
import io.logmall.mapper.ParametersJsonMapper;

public class TriggerInventoryBalanceLine implements Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(TriggerInventoryBalanceLine.class);
	private DateTime lastModifiedDateTime;
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
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("App Server URL: " + configuration.getServerURLd());
		String jpql = "SELECT entity FROM InventoryBalance entity ORDER BY entity.creationDateTime DESC";

		GetByJpqlBODBuilder.Builder<InventoryBalance> bodBuilder = GetByJpqlBODBuilder
				.newInstance(InventoryBalance.class);
		bodBuilder.withFirstResult(0);
		bodBuilder.withMaxResults(1);
		bodBuilder.withQuery(jpql);
		BusinessObjectDocument<Get, InventoryBalance> requestBod = bodBuilder.build();

		InventoryBalanceService restService = null;
		try {
			restService = ResteasyIntegration.newInstance().createClientProxy(InventoryBalanceService.class,
					configuration.getServerURLd());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ShowInventoryBalance resultBod = (ShowInventoryBalance) restService.get(requestBod);

		ParametersJsonMapper<InventoryBalanceLineMinimal> mapper = null;
		try {
			mapper = new ParametersJsonMapper<>(InventoryBalanceLineMinimal.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO Muss noch abgesichert werden, da es auch KEINE InventoryBalances geben kann
		InventoryBalance mallBalance = resultBod.getNounsForIteration().get(0);				
		if (hasInventoryBalanceBeenUpdated(resultBod, mallBalance)) {			
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message data = new Message.Builder().body(responseBody).build();
				parameters.getEventEmitter().emitData(data);
			}
			this.lastModifiedDateTime = mallBalance.getModificationDateTime();
		} else {
			InventoryBalanceLineMinimal balanceItem = new InventoryBalanceLineMinimal();
			balanceItem.setItemMaster("noItem");
			balanceItem.setQuantity(new BigDecimal("0.0"));
			balanceItem.setUnit("noUnit");
			JsonObject responseBody = null;
			try {
				responseBody = mapper.toJson(balanceItem);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message data = new Message.Builder().body(responseBody).build();
			parameters.getEventEmitter().emitData(data);
		}
	}

	private boolean hasInventoryBalanceBeenUpdated(ShowInventoryBalance resultBod, InventoryBalance mallBalance) {
		return resultBod.hasNouns() == true && mallBalance.getItemLines() != null
				&& !mallBalance.getItemLines().isEmpty() && !mallBalance.getModificationDateTime().equals(this.lastModifiedDateTime);
	}

	private static boolean hasInvalidItemOrQuantity(InventoryBalanceLine mallBalanceItem) {
		return mallBalanceItem == null || mallBalanceItem.getAvailableQuantity() == null
				|| mallBalanceItem.getItem() == null || mallBalanceItem.getItem().getMasterData() == null
				|| mallBalanceItem.getItem().getMasterData().getDisplayIdentifierId() == null;
	}
	


}
