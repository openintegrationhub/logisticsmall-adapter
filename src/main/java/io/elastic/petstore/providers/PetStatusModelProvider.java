package io.elastic.petstore.providers;


import io.elastic.api.SelectModelProvider;
import io.elastic.petstore.HttpClientUtils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PetStatusModelProvider implements SelectModelProvider {

    @Override
    public JsonObject getSelectModel(final JsonObject configuration) {

        final JsonArray statuses = HttpClientUtils.getMany("/pet/statuses", configuration);

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        statuses.stream().forEach(s -> {
            final String key = s.toString();
            builder.add(key, key.substring(0, 1).toUpperCase() + key.substring(1));
        });

        return builder.build();
    }
}
