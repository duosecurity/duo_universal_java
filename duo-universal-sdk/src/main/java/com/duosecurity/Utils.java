package com.duosecurity;

import static java.lang.String.format;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.AccessDevice;
import com.duosecurity.model.Application;
import com.duosecurity.model.AuthContext;
import com.duosecurity.model.AuthDevice;
import com.duosecurity.model.AuthResult;
import com.duosecurity.model.Location;
import com.duosecurity.model.Token;
import com.duosecurity.model.User;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class Utils {
  private Utils(){/*This is a utility class*/}

  private static final int ONE_HOUR_IN_MILLISECONDS = 3600000;

  private static final String HTTPS = "https";

  private static final Map<String, Object> HEADERS = Collections.singletonMap("alg", "HS512");

  private static String getString(String key, Map<String,Object> map){
    return map.containsKey(key) && map.get(key) != null ? map.get(key).toString() : null;
  }

  private static Map<String,Object> getAndCast(String key, Map<String,Object> map){
    return map.containsKey(key) && map.get(key) != null? (Map<String,Object>) map.get(key) : Collections.emptyMap();
  }

  static String createJwt(String clientId, String clientSecret, String aud) {
    Date expiration = new Date();
    expiration.setTime(expiration.getTime() + ONE_HOUR_IN_MILLISECONDS);
    return JWT.create()
              .withHeader(HEADERS)
              .withIssuer(clientId)
              .withSubject(clientId)
              .withAudience(aud)
              .withExpiresAt(expiration)
              .withJWTId(generateJwtId(32))
              .sign(Algorithm.HMAC512(clientSecret));
  }

  static String createJwtForAuthUrl(String clientId, String clientSecret, String redirectUri,
                                    String state, String username,
                                    Boolean useDuoCodeAttribute) {
    Date expiration = new Date();
    expiration.setTime(expiration.getTime() + ONE_HOUR_IN_MILLISECONDS);
    return JWT.create()
              .withHeader(HEADERS)
              .withExpiresAt(expiration)
              .withClaim("scope", "openid")
              .withClaim("client_id", clientId)
              .withClaim("redirect_uri", redirectUri)
              .withClaim("state", state)
              .withClaim("duo_uname", username)
              .withClaim("response_type", "code")
              .withClaim("use_duo_code_attribute", useDuoCodeAttribute)
              .sign(Algorithm.HMAC512(clientSecret));
  }

  static Token transformDecodedJwtToToken(DecodedJWT decodedJwt) {
    Token token = new Token();
    token.setIat(decodedJwt.getClaim("iat").asDouble());
    token.setIss(decodedJwt.getIssuer());
    token.setAud(decodedJwt.getClaim("aud").asString());
    token.setPreferred_username(decodedJwt.getClaim("preferred_username").asString());

    Map<String, Object> authContextMap = decodedJwt.getClaim("auth_context").asMap();
    token.setAuth_context(authContextMap != null ? getAuthContext(authContextMap) : null);

    Map<String, Object> authResultMap = decodedJwt.getClaim("auth_result").asMap();
    token.setAuth_result(authResultMap != null ? getAuthResult(authResultMap) : null);

    token.setAuth_time(decodedJwt.getClaim("auth_time").asInt());
    token.setExp(decodedJwt.getClaim("exp").asInt());
    token.setSub(decodedJwt.getClaim("sub").asString());
    return token;
  }

  static boolean validateCaCert(String[] userCaCerts) {
      return userCaCerts != null && userCaCerts.length != 0;
  }

  /**
   * Validates that the host is not empty or null.
   *
   * @param host    The api host provided by Duo in the admin panel.
   *
   * @throws DuoException   For invalid hosts
   */
  public static void validateHost(String host) throws DuoException {
    if (host == null || host.isEmpty()) {
      throw new DuoException(format("Invalid host: %s", host));
    }
  }

  /**
   * Creates and validates URL made from host.
   *
   * @param host    The api host provided by Duo in the admin panel.
   * @param file    Endpoint to append to API Host
   *
   * @return URL    A URL made from the host and the file
   *
   * @throws DuoException   For malformed URL
   */
  public static URL getAndValidateUrl(String host, String file) throws DuoException {
    try {
      validateHost(host);
      return new URL(HTTPS, host, file);
    } catch (MalformedURLException e) {
      throw new DuoException(e.getMessage(), e);
    }
  }

  static String generateJwtId(Integer length) {
    SecureRandom secureRandom = new SecureRandom();
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(Integer.toHexString(secureRandom.nextInt()));
    }
    return sb.substring(0, length);
  }

  private static AuthContext getAuthContext(Map<String, Object> authContextMap) {
    return new AuthContext()
      .setResult(getString("result",authContextMap))
      .setTimestamp(authContextMap.containsKey("timestamp") && authContextMap.get("timestamp") != null ? (Integer) authContextMap.get("timestamp") : null)
      .setAuth_device(getAuthDevice(authContextMap))
      .setTxid(getString("txid",authContextMap))
      .setEvent_type(getString("event_type",authContextMap))
      .setReason(getString("reason",authContextMap))
      .setAccess_device(getAccessDevice(authContextMap))
      .setApplication(getApplication(authContextMap))
      .setFactor(getString("factor",authContextMap))
      .setUser(getUser(authContextMap));
  }

  private static AuthResult getAuthResult(Map<String, Object> authResultMap) {
    return new AuthResult()
      .setStatus_msg(getString("status_msg",authResultMap))
      .setStatus(getString("status",authResultMap))
      .setResult(getString("result",authResultMap));
  }

  private static User getUser(Map<String, Object> authContextMap) {
    User user = new User();
    Map<String,Object> userMap = getAndCast("user",authContextMap);
    if (userMap != null) {
      user.setKey(getString("key",userMap));
      user.setName(getString("name",userMap));
    }
    return user;
  }

  private static Application getApplication(Map<String, Object> authContextMap) {
    Application application = new Application();
    Map<String,Object> applicationMap = getAndCast("application",authContextMap);
    if (applicationMap != null) {
      application.setKey(getString("key",applicationMap));
      application.setName(getString("name",applicationMap));
    }
    return application;
  }

  private static AccessDevice getAccessDevice(Map<String, Object> authContextMap) {
    AccessDevice accessDevice = new AccessDevice();

    Map<String,Object> accessDeviceMap = getAndCast("access_device",authContextMap);
    if (accessDeviceMap != null) {
      accessDevice.setIp(getString("ip",accessDeviceMap));
      accessDevice.setHostname(getString("hostname",accessDeviceMap));
      if (accessDeviceMap.containsKey("location")
          && accessDeviceMap.get("location") != null) {
        Map<String,Object> accessDeviceLocationMap = getAndCast("location",accessDeviceMap);
        Location accessDeviceLocation = new Location();
        accessDeviceLocation.setCity(getString("city",accessDeviceLocationMap));
        accessDeviceLocation.setState(getString("state",accessDeviceLocationMap));
        accessDeviceLocation.setCountry(getString("country",accessDeviceLocationMap));
        accessDevice.setLocation(accessDeviceLocation);
      }
    }
    return accessDevice;
  }

  private static AuthDevice getAuthDevice(Map<String, Object> authContextMap) {
    AuthDevice authDevice = new AuthDevice();
    Map<String,Object> authDeviceMap = getAndCast("auth_device",authContextMap);
    if (authDeviceMap != null) {
      authDevice.setIp(getString("ip",authDeviceMap));
      authDevice.setName(getString("name",authDeviceMap));
      if (authDeviceMap.containsKey("location") && authDeviceMap.get("location") != null) {
        Map<String,Object> authDeviceLocationMap = getAndCast("location",authDeviceMap);
        Location authDeviceLocation = new Location();
        authDeviceLocation.setCity(getString("city",authDeviceLocationMap));
        authDeviceLocation.setState(getString("state",authDeviceLocationMap));
        authDeviceLocation.setCountry(getString("country",authDeviceLocationMap));
        authDevice.setLocation(authDeviceLocation);
      }
    }
    return authDevice;
  }
}
