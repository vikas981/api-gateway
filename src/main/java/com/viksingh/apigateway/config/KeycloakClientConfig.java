package com.viksingh.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class KeycloakClientConfig {
    @Value("${keycloak.credentials.secret}")
    private String secretKey;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.auth-server-url}")
    private String authUrl;
    @Value("${keycloak.realm}")
    private String realm;

    @Bean
    public Keycloak keycloak(){
        return KeycloakBuilder.builder() .
                serverUrl(authUrl).realm(realm).
                grantType(OAuth2Constants.CLIENT_CREDENTIALS).
                clientId(clientId).clientSecret(secretKey) .
                resteasyClient(new ResteasyClientBuilder().
                connectionPoolSize(10).build())
            .build();
    }
}
