package io.elastic.petstore.actions;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.petstore.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import javax.json.JsonString;

/**
 * Action to create a pet.
 */
public class CreatePet implements Module {
    private static final Logger logger = LoggerFactory.getLogger(CreatePet.class);

    /**
     * Executes the actions's logic by sending a request to the Petstore API and emitting response to the platform.
     *
     * @param parameters execution parameters
     */
    @Override
    public void execute(final ExecutionParameters parameters) {
        logger.info("About to create new pet");
        // incoming message
        final Message message = parameters.getMessage();

        // body contains the mapped data
        final JsonObject body = message.getBody();

        // contains action's configuration
        final JsonObject configuration = parameters.getConfiguration();

        // access the value of the mapped value into name field of the in-metadata
        final JsonString name = body.getJsonString("name");
        if (name == null) {
            throw new IllegalStateException("Name is required");
        }

        // access the value of the mapped value into name field of the in-metadata
        final JsonString status = body.getJsonString("status");
        if (status == null) {
            throw new IllegalStateException("Status is required");
        }

        final JsonObject pet = HttpClientUtils.post("/pet", configuration, body);

        logger.info("Pet successfully created");

        final Message data
                = new Message.Builder().body(pet).build();

        logger.info("Emitting data");

        // emitting the message to the platform
        parameters.getEventEmitter().emitData(data);
    }
}
