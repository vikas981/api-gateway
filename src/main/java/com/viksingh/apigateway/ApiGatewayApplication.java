package com.viksingh.apigateway;

import com.viksingh.apigateway.exception.APIException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableEurekaClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public WebExceptionHandler exceptionHandler() {
		return (ServerWebExchange exchange, Throwable ex) -> {
			if (ex instanceof APIException) {
				exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
				return exchange.getResponse().setComplete();
			}
			return Mono.error(ex);
		};
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
