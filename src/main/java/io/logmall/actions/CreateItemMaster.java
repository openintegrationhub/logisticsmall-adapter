package io.logmall.actions;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.ccl.bo.instancerepository.boundary.rest.api.ItemMasterService;
import de.fraunhofer.ccl.bo.integration.resteasy.ResteasyIntegration;
import de.fraunhofer.ccl.bo.model.bod.BusinessObjectDocument;
import de.fraunhofer.ccl.bo.model.bod.builder.change.CreateOrReplaceBODBuilder;
import de.fraunhofer.ccl.bo.model.bod.verb.Change;
import de.fraunhofer.ccl.bo.model.bod.verb.Respond;
import de.fraunhofer.ccl.bo.model.entity.common.QuantityClassification;
import de.fraunhofer.ccl.bo.model.entity.common.Status;
import de.fraunhofer.ccl.bo.model.entity.itemmaster.ItemMaster;
import de.fraunhofer.ccl.bo.model.entity.shipment.Shipment;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Module;
import io.logmall.Constants;
import io.logmall.bod.ItemMasterMinimal;
import io.logmall.mapper.ItemMasterMinimalJsonMapper;

public class CreateItemMaster implements Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateItemMaster.class);

	/**
	 * Executes the actions's logic by sending a request to the logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */

	@Override
	public void execute(final ExecutionParameters parameters) {
		LOGGER.info("Going to create new Minimal ItemMaster");
		try {
			//------ setup data -----
			// body contains the mapped data
			final JsonObject body = parameters.getMessage().getBody();
			ItemMasterMinimalJsonMapper itemMasterMinimalJsonMapper = new ItemMasterMinimalJsonMapper();
			ItemMasterMinimal itemMasterMinimal = itemMasterMinimalJsonMapper.fromJson(body);

			//Only Stk available, therefore, no retrieval is done out of the configuration
			QuantityClassification quantityClassification = new QuantityClassification();
			quantityClassification.setUnit("Stk");
			String status = "inactive";
			if("1".equals(itemMasterMinimal.getStatusCode())) {
				status = "active";
			}

			ItemMaster itemMaster = new ItemMaster();
			itemMaster.setDisplayIdentifier(itemMasterMinimal.getIdentifier());
			itemMaster.setBaseQuantityClassification(quantityClassification);
			itemMaster.setDescription(itemMasterMinimal.getDescription());
			itemMaster.setStatus(new Status(status));
			
            CreateOrReplaceBODBuilder.Builder<ItemMaster> createBODBuilderItemMaster = CreateOrReplaceBODBuilder
                    .newInstance(ItemMaster.class);
            createBODBuilderItemMaster.forCreation();
            createBODBuilderItemMaster.withNoun(itemMaster);
            BusinessObjectDocument<Change, ItemMaster> requestBod = createBODBuilderItemMaster.build();	
         
            //----- setup communication -----
            // contains action's configuration
         	final JsonObject configuration = parameters.getConfiguration();
         	JsonString serverURL = configuration.getJsonString(Constants.URL_CONFIGURATION_KEY);
         	LOGGER.info("App Server URL: " + serverURL.getString());
            ItemMasterService itemMasterService = ResteasyIntegration.newInstance()
					.createClientProxy(ItemMasterService.class, serverURL.getString());
            BusinessObjectDocument<Respond, ItemMaster> response = itemMasterService.put(requestBod);			
			LOGGER.info("MinimalItemMaster successfully created:\t"+response.toString());
			
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());

		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Exception during API call: " + e.getMessage());
		}
	}

}
