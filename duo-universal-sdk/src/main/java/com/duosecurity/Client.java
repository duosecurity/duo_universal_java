package com.duosecurity;

import static com.duosecurity.Utils.createJwt;
import static com.duosecurity.Utils.createJwtForAuthUrl;
import static com.duosecurity.Utils.getAndValidateUrl;
import static com.duosecurity.Utils.transformDecodedJwtToToken;
import static com.duosecurity.Utils.validateCaCert;
import static com.duosecurity.Validator.validateClientParams;
import static com.duosecurity.Validator.validateState;
import static com.duosecurity.Validator.validateUsername;
import static java.lang.String.format;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.Token;
import com.duosecurity.model.TokenResponse;
import com.duosecurity.service.DuoConnector;


/**
 * Client serves as the entry point for this library. Instantiating this class
 * gives access to four public methods required for Duo's Universal Prompt 2FA.
 */
public class Client {

  // **************************************************
  // Constants
  // **************************************************
  private static final String OAUTH_V_1_HEALTH_CHECK_ENDPOINT = "/oauth/v1/health_check";

  private static final String OAUTH_V_1_AUTHORIZE_ENDPOINT = "/oauth/v1/authorize";

  private static final String OAUTH_V_1_TOKEN_ENDPOINT = "/oauth/v1/token";

  private static final String CLIENT_ASSERTION_TYPE
        = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

  private static final String USER_AGENT_LIB = "duo_universal_java";

  private static final String USER_AGENT_VERSION = "1.0.4-SNAPSHOT";

  // **************************************************
  // Fields
  // **************************************************
  private final String clientId;

  private final String clientSecret;

  private final String apiHost;

  private final String redirectUri;

  protected DuoConnector duoConnector;

  private String userAgent;

  private static final String[] DEFAULT_CA_CERTS = {
    //C=US, O=DigiCert Inc, OU=www.digicert.com, CN=DigiCert Assured ID Root CA
    "sha256/I/Lt/z7ekCWanjD0Cvj5EqXls2lOaThEA0H2Bg4BT/o=",
    //C=US, O=DigiCert Inc, OU=www.digicert.com, CN=DigiCert Global Root CA
    "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=",
    //C=US, O=DigiCert Inc, OU=www.digicert.com, CN=DigiCert High Assurance EV Root CA
    "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=",
    //C=US, O=SecureTrust Corporation, CN=SecureTrust CA
    "sha256/dykHF2FLJfEpZOvbOLX4PKrcD2w2sHd/iA/G3uHTOcw=",
    //C=US, O=SecureTrust Corporation, CN=Secure Global CA
    "sha256/JZaQTcTWma4gws703OR/KFk313RkrDcHRvUt6na6DCg=",
    //C=US, O=Amazon, CN=Amazon Root CA 1
    "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=",
    //C=US, O=Amazon, CN=Amazon Root CA 2
    "sha256/f0KW/FtqTjs108NpYj42SrGvOB2PpxIVM8nWxjPqJGE=",
    //C=US, O=Amazon, CN=Amazon Root CA 3
    "sha256/NqvDJlas/GRcYbcWE8S/IceH9cq77kg0jVhZeAPXq8k=",
    //C=US, O=Amazon, CN=Amazon Root CA 4
    "sha256/9+ze1cZgR9KO1kZrVDxA4HQ6voHRCSVNz4RdTCx4U8U=",
    //C=BM, O=QuoVadis Limited, CN=QuoVadis Root CA 2
    "sha256/j9ESw8g3DxR9XM06fYZeuN1UB4O6xp/GAIjjdD/zM3g="
  };

  // **************************************************
  // Constructors
  // **************************************************
  /**
   * Main constructor.
   *
   * @param clientId This value is the client id provided by Duo in the admin panel.
   * @param clientSecret This value is the client secret provided by Duo in the admin panel.
   * @param apiHost This value is the api host provided by Duo in the admin panel.
   * @param redirectUri This value is the uri which Duo should redirect to after 2FA is completed.
   * @param userCaCerts This value is a list of CA Certificates used to validate connections to Duo.
   *
   * @throws DuoException For problems validating the client parameters
   */
  public Client(String clientId, String clientSecret, String apiHost,
                String redirectUri, String[] userCaCerts) throws DuoException {
    validateClientParams(clientId, clientSecret, apiHost, redirectUri);
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.apiHost = apiHost;
    this.redirectUri = redirectUri;
    this.userAgent = computeUserAgent();
    String[] caCerts = validateCaCert(userCaCerts) ? userCaCerts : DEFAULT_CA_CERTS;
    duoConnector = new DuoConnector(apiHost, caCerts);
  }

  public Client(String clientId, String clientSecret, String apiHost, String redirectUri)
      throws DuoException {
    this(clientId, clientSecret, apiHost, redirectUri, DEFAULT_CA_CERTS);
  }

  // **************************************************
  // Public methods
  // **************************************************
  /**
   * Checks if Duo is healthy and available for 2FA.
   *
   * @return {@link HealthCheckResponse}
   *
   * @throws DuoException For health check errors
   */
  public HealthCheckResponse healthCheck() throws DuoException {
    String aud = getAndValidateUrl(apiHost, OAUTH_V_1_HEALTH_CHECK_ENDPOINT).toString();
    HealthCheckResponse response = duoConnector.duoHealthcheck(clientId,
                createJwt(clientId, clientSecret, aud));
    if (!response.wasSuccess()) {
      throw new DuoException(response.getMessage());
    }
    return response;
  }


  /**
   * Constructs a string which can be used to redirect the client browser to Duo for 2FA.
   *
   * @param username The user to be authenticated by Duo.
   * @param state A randomly generated String with at least 22 characters
   *              This value will be returned to the integration post 2FA
   *              and should be validated. {@link #generateState} exists as a utility function to
   *              generate this param.
   * @return String
   *
   * @throws DuoException For problems creating the auth url
   */
  public String createAuthUrl(String username, String state) throws DuoException {
    validateUsername(username);
    validateState(state);
    String request = createJwtForAuthUrl(clientId, clientSecret, redirectUri, state, username);
    String query = format(
            "?scope=openid&response_type=code&redirect_uri=%s&client_id=%s&request=%s",
            redirectUri, clientId, request);
    return getAndValidateUrl(apiHost, OAUTH_V_1_AUTHORIZE_ENDPOINT + query).toString();
  }


  /**
   * Verifies the duoCode returned by Duo and exchanges it for a {@link Token} which contains
   * information pertaining to the auth.  Uses the default token validator defined in
   * DuoIdTokenValidator.
   * To use a custom validator, see exchangeAuthorizationCodeFor2FAResult(String, TokenValidator)
   *
   * @param duoCode This string is an identifier for the auth and should be exchanged with Duo for a
   *             token to determine if the auth was successful as well as obtain meta-data about
   *             about the auth.
   *
   * @param username The user to be authenticated by Duo
   *
   * @return {@link Token}
   *
   * @throws DuoException For errors exchanging duoCode for 2FA results
   */
  public Token exchangeAuthorizationCodeFor2FAResult(String duoCode, String username)
      throws DuoException {
    TokenValidator validator = new DuoIdTokenValidator(clientSecret, username, clientId, apiHost);
    return exchangeAuthorizationCodeFor2FAResult(duoCode, validator);
  }

  /**
   * Verifies the duoCode returned by Duo and exchanges it for a {@link Token} which contains
   * information pertaining to the auth.  This version of the method allows the use of a
   * custom JWT token validator.
   * If you use a custom validator:
   *   You MUST confirm the integrity of the token by using the client secret to validate the
   *   SHA512 HMAC
   *   You MUST check the claims for
   *     Issuer
   *     Audience
   *     Issued at / Expiration
   *     Username (preferred_username)
   *
   * @param duoCode This string is an identifier for the auth and should be exchanged with Duo for a
   *                token to determine if the auth was successful as well as obtain meta-data about
   *                the auth.
   *
   * @param validator A TokenValidator that will validate and decode the JWT ID Token provided
   *                  by Duo.
   *
   * @return {@link Token}
   *
   * @throws DuoException For errors exchanging duoCode for 2FA results
   *
   */
  public Token exchangeAuthorizationCodeFor2FAResult(String duoCode, TokenValidator validator)
      throws DuoException {
    String aud = getAndValidateUrl(apiHost, OAUTH_V_1_TOKEN_ENDPOINT).toString();
    TokenResponse response = duoConnector.exchangeAuthorizationCodeFor2FAResult(userAgent,
            "authorization_code", duoCode, redirectUri, CLIENT_ASSERTION_TYPE,
            createJwt(clientId, clientSecret, aud));
    String idToken = response.getId_token();
    DecodedJWT decodedJwt = validator.validateAndDecode(idToken);
    return transformDecodedJwtToToken(decodedJwt);
  }

  /**
   * Generates a 36 character random identifier to be used as the state variable in the
   * createAuthUrl method.  This value should be stored in a variable and validated against
   * the state returned by Duo.
   *
   * @return String
   */
  public String generateState() {
    return Utils.generateJwtId(36);
  }

  private String computeUserAgent() {
    String duoAgent = format("%s/%s", USER_AGENT_LIB, USER_AGENT_VERSION);
    String javaAgent = format("%s/%s", 
                              System.getProperty("java.vendor"), 
                              System.getProperty("java.version"));
    String osAgent = format("%s/%s/%s", 
                            System.getProperty("os.name"), 
                            System.getProperty("os.version"), 
                            System.getProperty("os.arch"));

    return format("%s %s %s", duoAgent, javaAgent, osAgent);
  }

  /**
   * Appends string to userAgent.
   *
   * @param newUserAgent        Additional info that will be added to the end
   *                            of the user agent string
   */
  public void appendUserAgentInfo(String newUserAgent) {
    userAgent = format("%s %s", userAgent, newUserAgent);
  }
}
