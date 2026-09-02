package com.duosecurity;

import static com.duosecurity.Utils.validateHost;

import com.duosecurity.exception.DuoException;
import java.net.MalformedURLException;
import java.net.URL;

class Validator {

  private static final String ALPHA_NUMERIC_REGEX = "^[a-zA-z0-9]*$";
  private static final int MINIMUM_STATE_LENGTH = 22;
  private static final int MAXMIUM_STATE_LENGTH = 1024;
  private static final int MINIMUM_NONCE_LENGTH = 16;
  private static final int MAXIMUM_NONCE_LENGTH = 1024;

  static void validateClientParams(String clientId, String clientSecret,
                                   String apiHost, String redirectUri) throws DuoException {
    validateHost(apiHost);
    if (clientId.length() != 20 || !clientId.matches(ALPHA_NUMERIC_REGEX)
        || !(clientId.startsWith("DI") || clientId.startsWith("SI"))) {
      throw new DuoException("Invalid client id");
    }
    if (clientSecret.length() != 40 || !clientSecret.matches(ALPHA_NUMERIC_REGEX)) {
      throw new DuoException("Invalid client secret");
    }
    try {
      new URL(redirectUri);
    } catch (MalformedURLException e) {
      throw new DuoException(e.getMessage(), e);
    }
  }

  static void validateState(String state) throws DuoException {
    if (state.length() < MINIMUM_STATE_LENGTH || state.length() > MAXMIUM_STATE_LENGTH) {
      throw new DuoException("Invalid state");
    }
  }

  /**
   * Validates the length of a nonce. The nonce is optional, so a null nonce is valid and
   * simply means no nonce will be sent.
   */
  static void validateNonce(String nonce) throws DuoException {
    if (nonce == null) {
      return;
    }
    if (nonce.length() < MINIMUM_NONCE_LENGTH || nonce.length() > MAXIMUM_NONCE_LENGTH) {
      throw new DuoException("Invalid nonce");
    }
  }

  static void validateUsername(String username) throws DuoException {
    if (username == null || username.isEmpty()) {
      throw new DuoException("Missing username");
    }
  }
}
