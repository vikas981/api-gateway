package com.viksingh.apigateway.helper;

import com.viksingh.apigateway.config.KeycloakClientConfig;
import com.viksingh.apigateway.constants.GatewayConstant;
import com.viksingh.apigateway.exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class KeycloakHelper {

    private final KeycloakClientConfig keycloakClientConfig;
    private final Keycloak keycloak;
    private final RestTemplate restTemplate;

    public KeycloakHelper(KeycloakClientConfig keycloakClientConfig, Keycloak keycloak, RestTemplate restTemplate) {
        this.keycloakClientConfig = keycloakClientConfig;
        this.keycloak = keycloak;
        this.restTemplate = restTemplate;
    }

    public UsersResource getUsersResource(){
       return keycloak.realm(keycloakClientConfig.getRealm()).users();
    }

    public void validateToken(String authToken) {
        String tokenValidationURL = String.format(GatewayConstant.TOKEN_VALIDATION_URL,keycloakClientConfig.getAuthUrl(),keycloakClientConfig.getRealm());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(tokenValidationURL,
                    HttpMethod.GET,entity, new ParameterizedTypeReference<String>() {});
            log.info("Token validation response : {}",response);
        }catch (Exception e){
            log.error("Either the authorization is invalid or the session might have expired.",e);
            throw new APIException(HttpStatus.UNAUTHORIZED,"Either the authorization is invalid or the session might have expired.");
        }
    }
}
