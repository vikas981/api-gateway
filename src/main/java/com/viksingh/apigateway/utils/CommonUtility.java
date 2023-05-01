package com.viksingh.apigateway.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class CommonUtility {

  private static String decode(String encodedString) {
    return new String(Base64.getUrlDecoder().decode(encodedString));
  }

  @SneakyThrows
  public static JSONObject getPayload(String token) {
    String[] parts = token.split("\\.");
    return new JSONObject(decode(parts[1]));
  }

  @SneakyThrows
  public static String getNameFromToken(String token){
    String name = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      name = payload.getString("name");
    }
    return name;
  }

  @SneakyThrows
  public static String getEmail(String token){
    String name = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      name = payload.getString("email");
    }
    return name;
  }

  @SneakyThrows
  public static String getUserName(String token){
    String userName = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      userName = payload.getString("preferred_username");
    }
    return userName;
  }

  @SneakyThrows
  public static Long getExpireTime(String token){
    Long exp = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      exp = payload.getLong("exp");
    }
    return exp;
  }

  public static boolean isTokenExpired(String token){

    long timestamp = getExpireTime(token) *  1000;
    //LocalDateTime dateTime = Instant.ofEpochMilli(epoch)
      //  .atZone(ZoneId.systemDefault()).toLocalDateTime();
    Date expiration = new Date(timestamp);
    log.info("Token Expiration Time : {}",expiration);
    return expiration.before(new Date());
  }

  @SneakyThrows
  public static String getTokenExpiration(String token){
    String userName = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      userName = payload.getString("preferred_username");
    }
    return userName;
  }

  @SneakyThrows
  public static UUID getUserIdFromToken(String token){
    UUID userId = null;
    JSONObject payload = getPayload(token);
    if(!ObjectUtils.isEmpty(payload)){
      userId = UUID.fromString(payload.getString("sub"));
    }
    return userId;
  }
}
