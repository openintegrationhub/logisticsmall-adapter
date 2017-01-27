package io.elastic.petstore.providers;


import io.elastic.api.SelectModelProvider;
import io.elastic.petstore.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;

/**
 * Implementation of {@link SelectModelProvider} providing a select model for the pet status select.
 * The provide sends a HTTP request to the Petstore API and returns a JSON object as shown below.
 *
 * <pre>
 *     {
 *         "available": "Available",
 *         "sold": "Sold",
 *         "pending": "Pending"
 *     }
 * </pre>
 *
 * The value in the returned JSON object are used to display option's labels.
 */
public class PetStatusModelProvider implements SelectModelProvider {

    private static final Logger logger = LoggerFactory.getLogger(PetStatusModelProvider.class);

    @Override
    public JsonObject getSelectModel(final JsonObject configuration) {
        logger.info("About to retrieve pet statuses from the Petstore API");

        final JsonArray statuses = HttpClientUtils.getMany("/pet/statuses", configuration);

        logger.info("Successfully retrieved {} statuses", statuses.size());

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        statuses.getValuesAs(JsonString.class).stream().forEach(s -> {
            final String key = s.getString();
            builder.add(key, key.substring(0, 1).toUpperCase() + key.substring(1));
        });

        return builder.build();
    }
}
