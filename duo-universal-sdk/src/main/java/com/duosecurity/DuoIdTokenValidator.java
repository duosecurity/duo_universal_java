package com.duosecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.duosecurity.exception.DuoException;
import java.io.UnsupportedEncodingException;

/**
 * A JWT ID Token Validator that enforces Duo's claim requirements.
 *   Issuer must match expected
 *   Audience must match expected
 *   Subject must match expected username
 *   Allow 1 minutes leeway on Issued at / Expiration
 *   MAC is valid  
 *   [Optional] Nonce must match expected
 */
public final class DuoIdTokenValidator implements TokenValidator {

  private static final long DUO_LEEWAY = 60;  // One minute, in seconds
  private static final String HTTPS = "https://";
  private static final String ISSUER_PATH = "/oauth/v1/token";

  private static final String NONCE_CLAIM = "nonce";
  private static final String USERNAME_CLAIM = "preferred_username";

  private final String clientSecret;
  private final String username;
  private final String audience;
  private final String issuer;
  private final String nonce;
  private long leeway = DUO_LEEWAY;

  public DuoIdTokenValidator(String clientSecret, String username,
                             String audience, String apiHost) {
    this(clientSecret, username, audience, apiHost, null);
  }

  /**
   * Constructor with nonce parameter.
   *
   * @param clientSecret    The client secret provided by Duo in the admin panel.
   * @param username        The name of the user trying to auth
   * @param audience        This value is the client id provided by Duo in the admin panel.
   * @param apiHost         The api host provided by Duo in the admin panel.
   * @param nonce           A string used to associate a client session with an ID token.
   */
  public DuoIdTokenValidator(String clientSecret, String username, String audience,
                             String apiHost, String nonce) {
    this.clientSecret = clientSecret;
    this.username = username;
    this.audience = audience;
    this.issuer = HTTPS + apiHost + ISSUER_PATH;
    this.nonce = nonce;
  }

  /**
   * Validate the provided jwt token against the expected values of the claims, as described above.
   *
   * @return the decoded JWT if all the claims check out, otherwise a DuoException is raised
   */
  @Override
  public DecodedJWT validateAndDecode(String jwt) throws DuoException {
    if (jwt == null) {
      throw new DuoException("ID Token verification failed: Null token");
    }

    try {
      JWTVerifier verifier = buildVerifier();
      return verifier.verify(jwt);
    } catch (JWTVerificationException | UnsupportedEncodingException e) {
      throw new DuoException("ID Token verification failed", e);
    }
  }

  /**
   * Build a JWT verifier that will enforce the claims as described above.
   */
  private JWTVerifier buildVerifier() throws UnsupportedEncodingException {
    Algorithm signingAlgorithm = Algorithm.HMAC512(this.clientSecret);
    Verification verifier = JWT.require(signingAlgorithm)
                               .withIssuer(this.issuer)
                               .withAudience(this.audience)
                               .withClaim(USERNAME_CLAIM, this.username)
                               .acceptLeeway(this.leeway);
    if (nonce != null) {
      verifier.withClaim(NONCE_CLAIM, this.nonce);
    }
    return verifier.build();
  }
}
