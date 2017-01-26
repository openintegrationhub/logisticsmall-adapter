package io.elastic.petstore.providers;


import io.elastic.api.SelectModelProvider;
import io.elastic.petstore.HttpClientUtils;

import javax.json.*;

public class PetStatusModelProvider implements SelectModelProvider {

    @Override
    public JsonObject getSelectModel(final JsonObject configuration) {

        final JsonArray statuses = HttpClientUtils.getMany("/pet/statuses", configuration);

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        statuses.getValuesAs(JsonString.class).stream().forEach(s -> {
            final String key = s.getString();
            builder.add(key, key.substring(0, 1).toUpperCase() + key.substring(1));
        });

        return builder.build();
    }
}
