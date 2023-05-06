package com.viksingh.apigateway.filter;

import com.viksingh.apigateway.exception.APIException;
import com.viksingh.apigateway.helper.KeycloakHelper;
import com.viksingh.apigateway.utils.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
      ServerHttpRequest request = null;
      if(routeValidator.isAllowed.test(exchange.getRequest())){
        if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
          throw new APIException(HttpStatus.UNAUTHORIZED,"You are not authorized to access this resource.");
        }
        String bearerToken = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
          keycloakHelper.validateToken(bearerToken);
          String emailFromJwtToken = CommonUtility.getUserName(bearerToken);
          boolean isTokenExpired = CommonUtility.isTokenExpired(bearerToken);
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
          request = exchange.getRequest().mutate().header("userName",CommonUtility.getUserIdFromToken(bearerToken).toString()).build();
        }
      }
      return chain.filter(exchange.mutate().request(request).build());
    });
  }



  public static class Config {
    // ...
  }

}
