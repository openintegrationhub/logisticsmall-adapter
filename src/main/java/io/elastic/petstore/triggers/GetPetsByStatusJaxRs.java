package io.elastic.petstore.triggers;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.petstore.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 * Trigger to get pets by status.
 */
public class GetPetsByStatusJaxRs implements Module {
    private static final Logger logger = LoggerFactory.getLogger(GetPetsByStatusJaxRs.class);

    /**
     * Executes the trigger's logic by sending a request to the Petstore API and emitting response to the platform.
     *
     * @param parameters execution parameters
     */
    @Override
    public void execute(final ExecutionParameters parameters) {
        final JsonObject configuration = parameters.getConfiguration();

        // access the value of the status field defined in trigger's fields section of component.json
        final JsonString status = configuration.getJsonString("status");
        if (status == null) {
            throw new IllegalStateException("status field is required");
        }
        // access the value of the apiKey field defined in credentials section of component.json
        final JsonString apiKey = configuration.getJsonString("apiKey");
        if (apiKey == null) {
            throw new IllegalStateException("apiKey is required");
        }

        logger.info("About to find pets by status {}", status.getString());

        final JsonArray pets = ClientBuilder.newClient()
                .target(Constants.PETSTORE_API_BASE_URL)
                .path(Constants.FIND_PETS_BY_STATUS_PATH)
                .queryParam("status", status.getString())
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(Constants.API_KEY_HEADER, apiKey.getString())
                .get(JsonArray.class);

        logger.info("Got {} pets", pets.size());

        // emitting naked arrays is forbidden by the platform
        final JsonObject body = Json.createObjectBuilder()
                .add("pets", pets)
                .build();

        final Message data
                = new Message.Builder().body(body).build();

        logger.info("Emitting data");

        // emitting the message to the platform
        parameters.getEventEmitter().emitData(data);
    }
}
