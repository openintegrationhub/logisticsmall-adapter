package io.logmall.actions;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.PurchaseOrderService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.ChangePurchaseOrder;
import de.fraunhofer.ccl.bo.model.bod.RespondPurchaseOrder;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.logmall.bod.ConfigurationParameters;
import io.logmall.mapper.ParametersJsonMapper;
import io.logmall.mapper.StandaloneBusinessObjectDocumentJsonMapper;

public class SendPurchaseOrderBOD implements Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendPurchaseOrderBOD.class);

	@Override
	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Going to create new Purchase Order");
		try {
			// body contains the mapped data
			final JsonObject body = parameters.getMessage().getBody();

			ConfigurationParameters configuration = new ParametersJsonMapper<>(ConfigurationParameters.class)
					.fromJson(parameters.getConfiguration());
			LOGGER.info("App Server URL: " + configuration.getServerURLd());

			StandaloneBusinessObjectDocumentJsonMapper<ChangePurchaseOrder> changePurchaseOrderJsonMapper = new StandaloneBusinessObjectDocumentJsonMapper<>(
					ChangePurchaseOrder.class);

			ChangePurchaseOrder changePurchaseOrder = changePurchaseOrderJsonMapper.fromJson(body);

			LOGGER.info("Change action code: " + changePurchaseOrder.getVerb().getActionCode());

			PurchaseOrderService purchaseOrderService = ResteasyIntegration.newInstance()
					.createClientProxy(PurchaseOrderService.class, configuration.getServerURLd());

			RespondPurchaseOrder response = (RespondPurchaseOrder) purchaseOrderService.put(changePurchaseOrder);
			LOGGER.info("Purchase Order successfully created");
			LOGGER.info("Emitting data: " + response);

			StandaloneBusinessObjectDocumentJsonMapper<RespondPurchaseOrder> respondPurchaseOrderJsonMapper = new StandaloneBusinessObjectDocumentJsonMapper<>(
					RespondPurchaseOrder.class);
			Message responseMessage = new Message.Builder().body(respondPurchaseOrderJsonMapper.toJson(response))
					.build();
			parameters.getEventEmitter().emitData(responseMessage);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			parameters.getEventEmitter().emitException(e);
		}
	}
}
