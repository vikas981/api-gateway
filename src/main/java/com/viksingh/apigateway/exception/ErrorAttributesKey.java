package com.viksingh.apigateway.exception;

import lombok.Getter;

@Getter
public enum ErrorAttributesKey{
  CODE("code"),
  MESSAGE("message"),
  TIME("timestamp");

  private final String key;
  ErrorAttributesKey(String key) {
    this.key = key;
  }
}
