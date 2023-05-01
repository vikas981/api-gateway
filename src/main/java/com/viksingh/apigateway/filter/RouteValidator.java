package com.viksingh.apigateway.filter;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public static final List<String> allowedRoutes = List.of(
          "/catalog-service/categories",
          "/catalog-service/sub_categories",
          "/auth-service/user/do_login",
          "/auth-service/user/sign_up",
          "/catalog-service/products",
          "/eureka"
  );

  public Predicate<ServerHttpRequest> isAllowed = request -> allowedRoutes.stream().noneMatch(uri -> request.getURI().getPath().equals(uri));

}
