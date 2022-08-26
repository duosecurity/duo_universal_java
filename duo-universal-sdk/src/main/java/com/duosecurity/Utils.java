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

  private static final int ONE_HOUR_IN_MILLISECONDS = 3600000;

  private static final String HTTPS = "https";

  private static final Map<String, Object> HEADERS = Collections.singletonMap("alg", "HS512");

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
    token.setIss(decodedJwt.getClaim("iss").asString());
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
    if (userCaCerts == null || userCaCerts.length == 0) {
      return false;
    }
    return true;
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
    AuthContext authContext = new AuthContext();
    authContext.setResult(authContextMap.containsKey("result")
                          && authContextMap.get("result") != null
                          ? authContextMap.get("result").toString() : null);
    authContext.setTimestamp(authContextMap.containsKey("timestamp")
                             && authContextMap.get("timestamp") != null
                             ? (Integer) authContextMap.get("timestamp") : null);
    authContext.setAuth_device(getAuthDevice(authContextMap));
    authContext.setTxid(authContextMap.containsKey("txid")
                        && authContextMap.get("txid") != null
                        ? authContextMap.get("txid").toString() : null);
    authContext.setEvent_type(authContextMap.containsKey("event_type")
                              && authContextMap.get("event_type") != null
                              ? authContextMap.get("event_type").toString() : null);
    authContext.setReason(authContextMap.containsKey("reason")
                          && authContextMap.get("reason") != null
                          ? authContextMap.get("reason").toString() : null);
    authContext.setAccess_device(getAccessDevice(authContextMap));
    authContext.setApplication(getApplication(authContextMap));
    authContext.setFactor(authContextMap.containsKey("factor")
                          && authContextMap.get("factor") != null
                          ? authContextMap.get("factor").toString() : null);
    authContext.setUser(getUser(authContextMap));
    return authContext;
  }

  private static AuthResult getAuthResult(Map<String, Object> authResultMap) {
    AuthResult authResult = new AuthResult();
    authResult.setStatus_msg(authResultMap.containsKey("status_msg")
                             && authResultMap.get("status_msg") != null
                             ? authResultMap.get("status_msg").toString() : null);
    authResult.setStatus(authResultMap.containsKey("status")
                         && authResultMap.get("status") != null
                         ? authResultMap.get("status").toString() : null);
    authResult.setResult(authResultMap.containsKey("result")
                         && authResultMap.get("result") != null
                         ? authResultMap.get("result").toString() : null);
    return authResult;
  }

  private static User getUser(Map<String, Object> authContextMap) {
    User user = new User();
    Map userMap = authContextMap.containsKey("user") && authContextMap.get("user") != null
                   ? (Map) authContextMap.get("user") : null;
    if (userMap != null) {
      user.setKey(userMap.containsKey("key") && userMap.get("key") != null
                  ? userMap.get("key").toString() : null);
      user.setName(userMap.containsKey("name") && userMap.get("name") != null
                   ? userMap.get("name").toString() : null);
    }
    return user;
  }

  private static Application getApplication(Map<String, Object> authContextMap) {
    Application application = new Application();
    Map applicationMap = authContextMap.containsKey("application")
                          && authContextMap.get("application") != null
                          ? (Map) authContextMap.get("application") : null;
    if (applicationMap != null) {
      application.setKey(applicationMap.containsKey("key")
                         && applicationMap.get("key") != null
                         ? applicationMap.get("key").toString() : null);
      application.setName(applicationMap.containsKey("name")
                          && applicationMap.get("name") != null
                          ? applicationMap.get("name").toString() : null);
    }
    return application;
  }

  private static AccessDevice getAccessDevice(Map<String, Object> authContextMap) {
    AccessDevice accessDevice = new AccessDevice();
    Map accessDeviceMap = authContextMap.containsKey("access_device")
                            && authContextMap.get("access_device") != null
                            ? (Map) authContextMap.get("access_device") : null;
    if (accessDeviceMap != null) {
      accessDevice.setIp(accessDeviceMap.containsKey("ip")
                         && accessDeviceMap.get("ip") != null
                         ? accessDeviceMap.get("ip").toString() : null);
      accessDevice.setHostname(accessDeviceMap.containsKey("hostname")
                               && accessDeviceMap.get("hostname") != null
                               ? accessDeviceMap.get("hostname").toString() : null);
      if (accessDeviceMap.containsKey("location")
          && accessDeviceMap.get("location") != null) {
        Map accessDeviceLocationMap = (Map) accessDeviceMap.get("location");
        Location accessDeviceLocation = new Location();
        accessDeviceLocation.setCity(accessDeviceLocationMap.containsKey("city")
                                 && accessDeviceLocationMap.get("city") != null
                                 ? accessDeviceLocationMap.get("city").toString() : null);
        accessDeviceLocation.setState(accessDeviceLocationMap.containsKey("state")
                                 && accessDeviceLocationMap.get("state") != null
                                 ? accessDeviceLocationMap.get("state").toString() : null);
        accessDeviceLocation.setCountry(accessDeviceLocationMap.containsKey("country")
                               && accessDeviceLocationMap.get("country") != null
                               ? accessDeviceLocationMap.get("country").toString() : null);
        accessDevice.setLocation(accessDeviceLocation);
      }
    }
    return accessDevice;
  }

  private static AuthDevice getAuthDevice(Map<String, Object> authContextMap) {
    AuthDevice authDevice = new AuthDevice();
    Map authDeviceMap = authContextMap.containsKey("auth_device")
                          && authContextMap.get("auth_device") != null
                          ? (Map) authContextMap.get("auth_device") : null;
    if (authDeviceMap != null) {
      authDevice.setIp(authDeviceMap.containsKey("ip") && authDeviceMap.get("ip") != null
                       ? authDeviceMap.get("ip").toString() : null);
      authDevice.setName(authDeviceMap.containsKey("name") && authDeviceMap.get("name") != null
                         ? authDeviceMap.get("name").toString() : null);
      if (authDeviceMap.containsKey("location") && authDeviceMap.get("location") != null) {
        Map authDeviceLocationMap = (Map) authDeviceMap.get("location");
        Location authDeviceLocation = new Location();
        authDeviceLocation.setCity(authDeviceLocationMap.containsKey("city")
                                   && authDeviceLocationMap.get("city") != null
                                   ? authDeviceLocationMap.get("city").toString() : null);
        authDeviceLocation.setState(authDeviceLocationMap.containsKey("state")
                                    && authDeviceLocationMap.get("state") != null
                                    ? authDeviceLocationMap.get("state").toString() : null);
        authDeviceLocation.setCountry(authDeviceLocationMap.containsKey("country")
                                      && authDeviceLocationMap.get("country") != null
                                      ? authDeviceLocationMap.get("country").toString() : null);
        authDevice.setLocation(authDeviceLocation);
      }
    }
    return authDevice;
  }
}
