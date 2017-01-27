package io.elastic.petstore;


import io.elastic.api.CredentialsVerifier;
import io.elastic.api.InvalidCredentialsException;

import javax.json.JsonObject;

/**
 * Implementation of {@link CredentialsVerifier} used to verfy that credentials provide by user
 * are valid. This is accomplished by sending a simple request to the Petstore API.
 * In case of a successful response (HTTP 200) we assume credentials are valid. Otherweise invalid.
 */
public class ApiKeyVerifier implements CredentialsVerifier {

    @Override
    public void verify(final JsonObject configuration) throws InvalidCredentialsException {
        try {
            HttpClientUtils.getSingle("/user/me", configuration);
        } catch (Exception e) {
            throw new InvalidCredentialsException("Failed to verify credentials", e);
        }
    }
}
