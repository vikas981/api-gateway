package com.viksingh.apigateway.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
public class ExceptionRule {
  Class<?> exceptionClass;
  HttpStatus status;
}
