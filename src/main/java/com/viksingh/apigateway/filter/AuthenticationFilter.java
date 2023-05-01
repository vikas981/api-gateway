package com.viksingh.apigateway.filter;

import com.viksingh.apigateway.exception.APIException;
import com.viksingh.apigateway.helper.KeycloakHelper;
import com.viksingh.apigateway.utils.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  @Autowired
  private KeycloakHelper keycloakHelper;

  public AuthenticationFilter() {
    super(Config.class);
  }


  @Autowired
  private RouteValidator routeValidator;

  @Override
  public GatewayFilter apply(Config config) {
    return ((exchange, chain) -> {
      if(routeValidator.isAllowed.test(exchange.getRequest())){
        if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
          throw new APIException(HttpStatus.UNAUTHORIZED,"You are not authorized to access this resource.");
        }
        String authorization = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        if(!ObjectUtils.isEmpty(authorization) && authorization.startsWith("Bearer ")){
          keycloakHelper.validateToken(authorization);
          String emailFromJwtToken = CommonUtility.getUserName(authorization);
          boolean isTokenExpired = CommonUtility.isTokenExpired(authorization);
          if(isTokenExpired){
            log.info("Either the authorization is invalid or the session might have expired.");
            throw new APIException(HttpStatus.UNAUTHORIZED,"Either the authorization is invalid or the session might have expired.");
          }else{
            try{
              UserRepresentation userRepresentation = keycloakHelper.getUsersResource()
                  .search(emailFromJwtToken,true).stream().findFirst()
                  .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND,
                      "User doesn't exists"));
              if(ObjectUtils.isEmpty(userRepresentation)){
                throw new APIException(HttpStatus.UNAUTHORIZED,"Unauthorized");
              }
            }catch (Exception e){
              e.printStackTrace();
              throw new APIException(HttpStatus.UNAUTHORIZED,"Unauthorized");
            }
          }
        }
      }
      return chain.filter(exchange);
    });
  }



  public static class Config {
    // ...
  }

}
