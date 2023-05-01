package com.viksingh.apigateway.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class APIException extends RuntimeException {
  private HttpStatus status;
  private String message;
}
