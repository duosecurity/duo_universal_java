package com.duosecurity;

import static com.duosecurity.Utils.createJwt;
import static com.duosecurity.Utils.createJwtForAuthUrl;
import static com.duosecurity.Utils.getAndValidateUrl;
import static com.duosecurity.Utils.transformDecodedJwtToToken;
import static com.duosecurity.Utils.validateCaCert;
import static com.duosecurity.Validator.validateClientParams;
import static com.duosecurity.Validator.validateNonce;
import static com.duosecurity.Validator.validateState;
import static com.duosecurity.Validator.validateUsername;
import static java.lang.String.format;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.Token;
import com.duosecurity.model.TokenResponse;
import com.duosecurity.service.CertificateHelper;
import com.duosecurity.service.DuoConnector;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;


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

  private static final String USER_AGENT_VERSION = "1.3.3-SNAPSHOT";

  private static final String CA_BUNDLE_VERSION = "1.0";

  // **************************************************
  // Fields
  // **************************************************
  private String clientId;

  private String clientSecret;

  private String apiHost;

  private String redirectUri;

  private Boolean useDuoCodeAttribute;

  private String proxyHost;

  private Integer proxyPort;

  protected DuoConnector duoConnector;

  private String userAgent;

  // **************************************************
  // Constructors
  // This class uses the "Builder" pattern and should not be directly instantiated.
  // Use Client.Builder to generate the Client.
  // **************************************************

  private Client() {
  }

  /**
   * Legacy simple constructor.
   * @param clientId This value is the client id provided by Duo in the admin panel.
   * @param clientSecret This value is the client secret provided by Duo in the admin panel.
   * @param apiHost This value is the api host provided by Duo in the admin panel.
   * @param redirectUri This value is the uri which Duo should redirect to after 2FA is completed.
   *
   * @throws DuoException For problems building the client
   * @deprecated The constructors are deprecated.
   *     Prefer the {@link Client.Builder} for instantiating Clients
   */
  @Deprecated
  public Client(String clientId, String clientSecret, String apiHost, String redirectUri)
      throws DuoException {
    this(clientId, clientSecret, apiHost, redirectUri, null);
  }

  /**
   * Legacy constructor which allows specifying custom CaCerts.
   * @param clientId This value is the client id provided by Duo in the admin panel.
   * @param clientSecret This value is the client secret provided by Duo in the admin panel.
   * @param apiHost This value is the api host provided by Duo in the admin panel.
   * @param redirectUri This value is the uri which Duo should redirect to after 2FA is completed.
   * @param userCaCerts This value is a list of CA Certificates used to validate connections to Duo
   *
   * @throws DuoException For problems building the client
   * 
   * @deprecated The constructors are deprecated.
   *     Prefer the {@link Client.Builder} for instantiating Clients
   */
  @Deprecated
  public Client(String clientId, String clientSecret, String apiHost,
              String redirectUri, String[] userCaCerts) throws DuoException {
    Client client = new Builder(clientId, clientSecret, apiHost, redirectUri)
            .setCACerts(userCaCerts)
            .build();
    this.clientId = client.clientId;
    this.clientSecret = client.clientSecret;
    this.apiHost = client.apiHost;
    this.redirectUri = client.redirectUri;
    this.useDuoCodeAttribute = client.useDuoCodeAttribute;
    this.duoConnector = client.duoConnector;
    this.userAgent = client.userAgent;
  }

  /**
   * Legacy constructor which allows specifying custom CaCerts.
   *
   * @param clientId     This value is the client id provided by Duo in the admin
   *                     panel.
   * @param clientSecret This value is the client secret provided by Duo in the
   *                     admin panel.
   * @param apiHost      This value is the api host provided by Duo in the admin panel.
   * @param redirectUri  This value is the uri which Duo should redirect to after
   *                     2FA is completed.
   * @param proxyHost    This value is the hostname of the proxy server
   * @param proxyPort    This value is the port number of the proxy server
   * @param userCaCerts  This value is a list of CA Certificates used to validate connections to Duo
   *
   * @throws DuoException For problems building the client
   *
   * @deprecated The constructors are deprecated. Prefer the
   *             {@link Client.Builder} for instantiating Clients
   */
  @Deprecated
  public Client(String clientId, String clientSecret, String apiHost,
                String redirectUri, String proxyHost, Integer proxyPort,
                String[] userCaCerts) throws DuoException {
    Client client = new Builder(clientId, clientSecret, proxyHost, proxyPort, apiHost, redirectUri)
            .setCACerts(userCaCerts)
            .build();
    this.clientId = client.clientId;
    this.clientSecret = client.clientSecret;
    this.apiHost = client.apiHost;
    this.redirectUri = client.redirectUri;
    this.useDuoCodeAttribute = client.useDuoCodeAttribute;
    this.duoConnector = client.duoConnector;
    this.userAgent = client.userAgent;
    this.proxyHost = client.proxyHost;
    this.proxyPort = client.proxyPort;
  }

  public static class Builder {
    private final String clientId;
    private final String clientSecret;
    private final String apiHost;
    private String proxyHost;
    private Integer proxyPort;
    private final String redirectUri;
    private Boolean useDuoCodeAttribute;
    private String[] customPemCerts;
    private boolean caPinningDisabled;
    private String userAgent;

    /**
     * Builder.
     *
     * @param clientId This value is the client id provided by Duo in the admin panel.
     * @param clientSecret This value is the client secret provided by Duo in the admin panel.
     * @param apiHost This value is the api host provided by Duo in the admin panel.
     * @param redirectUri This value is the uri which Duo should redirect to after 2FA is completed.
     */
    public Builder(String clientId, String clientSecret, String apiHost,
                   String redirectUri) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      this.apiHost = apiHost;
      this.redirectUri = redirectUri;
      this.useDuoCodeAttribute = true;
      this.userAgent = computeUserAgent();
    }

    /**
     * Builder.
     *
     * @param clientId     This value is the client id provided by Duo in the admin
     *                     panel.
     * @param clientSecret This value is the client secret provided by Duo in the
     *                     admin panel.
     * @param proxyHost    This value is the hostname of the proxy server
     * @param proxyPort    This value is the port number of the proxy server
     * @param apiHost      This value is the api host provided by Duo in the admin
     *                     panel.
     * @param redirectUri  This value is the uri which Duo should redirect to after
     *                     2FA is completed.
     */
    public Builder(String clientId, String clientSecret, String proxyHost,
                   Integer proxyPort, String apiHost, String redirectUri) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      this.apiHost = apiHost;
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
      this.redirectUri = redirectUri;
      this.useDuoCodeAttribute = true;
      this.userAgent = computeUserAgent();
    }

    /**
     * Build the client object.
     *
     * @return {@link Client}
     *
     * @throws DuoException For problems building the client
     */
    public Client build() throws DuoException {
      validateClientParams(clientId, clientSecret, apiHost, redirectUri);

      if (caPinningDisabled && customPemCerts != null) {
        throw new DuoException(
            "Cannot both disable CA pinning and provide custom certificates");
      }

      SSLSocketFactory sslSocketFactory = null;
      X509TrustManager trustManager = null;

      if (!caPinningDisabled) {
        if (customPemCerts != null) {
          trustManager = CertificateHelper.createTrustManager(customPemCerts);
        } else {
          trustManager = CertificateHelper.loadDefaultTrustManager();
        }
        sslSocketFactory = CertificateHelper.createSslSocketFactory(trustManager);
      }

      Client client = new Client();
      client.clientId = clientId;
      client.clientSecret = clientSecret;
      client.apiHost = apiHost;
      client.redirectUri = redirectUri;
      client.useDuoCodeAttribute = useDuoCodeAttribute;
      String caPinningStatus = caPinningDisabled ? "disabled" : "enabled";
      client.userAgent = format("%s ca_bundle/%s (ca_pinning=%s)",
              userAgent, CA_BUNDLE_VERSION, caPinningStatus);
      client.duoConnector = new DuoConnector(apiHost, proxyHost, proxyPort,
              sslSocketFactory, trustManager);

      return client;
    }

    /**
     * Optionally use custom CA Certificates when validating connections to Duo.
     * Each string should be a PEM-encoded certificate.
     *
     * @param userCaCerts List of PEM-encoded CA Certificates to use
     *
     * @return the Builder
     */
    public Builder setCACerts(String[] userCaCerts) {
      if (validateCaCert(userCaCerts)) {
        this.customPemCerts = userCaCerts;
      }
      return this;
    }

    /**
     * Disable certificate pinning for connections to Duo.
     * When disabled, TLS verification still occurs using the system's default
     * trusted certificate authorities, but the bundled CA pin set is not enforced.
     *
     * <p>This option is mutually exclusive with {@link #setCACerts(String[])}.
     *
     * @return the Builder
     */
    public Builder disableCaPinning() {
      this.caPinningDisabled = true;
      return this;
    }

    /**
     * Optionally toggle the returned authorization parameter to use duo_code vs code.
     * Defaults true to use duo_code.
     *
     * @param useDuoCodeAttribute true/false toggle
     * 
     * @return the Builder
     */
    public Builder setUseDuoCodeAttribute(boolean useDuoCodeAttribute) {
      this.useDuoCodeAttribute = useDuoCodeAttribute;
      return this;
    }

    /**
     * Optionally appends string to userAgent.
     *
     * @param newUserAgent Additional info that will be added to the end of the user agent string
     * 
     * @return the Builder
     */
    public Builder appendUserAgentInfo(String newUserAgent) {
      userAgent = format("%s %s", userAgent, newUserAgent);
      return this;
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
    return createAuthUrl(username, state, null);
  }

  /**
   * Constructs a string which can be used to redirect the client browser to Duo for 2FA,
   * additionally binding the resulting ID token to this authorization request with a nonce.
   *
   * @param username The user to be authenticated by Duo.
   * @param state A randomly generated String with at least 22 characters
   *              This value will be returned to the integration post 2FA
   *              and should be validated. {@link #generateState} exists as a utility function to
   *              generate this param.
   * @param nonce A randomly generated String of 16 to 1024 characters, or null for no nonce.
   *              The same value must be passed to
   *              {@link #exchangeAuthorizationCodeFor2FAResult(String, String, String)}, which
   *              will reject an ID token that does not carry it.
   * @return String
   *
   * @throws DuoException For problems creating the auth url
   */
  public String createAuthUrl(String username, String state, String nonce) throws DuoException {
    validateUsername(username);
    validateState(state);
    validateNonce(nonce);
    String request = createJwtForAuthUrl(clientId, clientSecret, redirectUri,
            state, username, useDuoCodeAttribute, apiHost);
    String query = format(
            "?scope=openid&response_type=code&redirect_uri=%s&client_id=%s&request=%s",
            redirectUri, clientId, request);
    if (nonce != null) {
      query = format("%s&nonce=%s", query, urlEncode(nonce));
    }
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
    return exchangeAuthorizationCodeFor2FAResult(duoCode, username, null);
  }

  /**
   * Verifies the duoCode returned by Duo and exchanges it for a {@link Token} which contains
   * information pertaining to the auth.  Uses the default token validator defined in
   * DuoIdTokenValidator, which additionally requires the ID Token to carry the given nonce.
   *
   * @param duoCode This string is an identifier for the auth and should be exchanged with Duo for a
   *             token to determine if the auth was successful as well as obtain meta-data about
   *             about the auth.
   *
   * @param username The user to be authenticated by Duo
   *
   * @param nonce The same nonce passed to {@link #createAuthUrl(String, String, String)}, or null
   *              if no nonce was sent.  A non-null value that does not match the nonce claim in
   *              the ID Token will fail validation.
   *
   * @return {@link Token}
   *
   * @throws DuoException For errors exchanging duoCode for 2FA results
   */
  public Token exchangeAuthorizationCodeFor2FAResult(String duoCode, String username, String nonce)
      throws DuoException {
    TokenValidator validator = new DuoIdTokenValidator(clientSecret, username, clientId, apiHost,
            nonce);
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
            createJwt(clientId, clientSecret, aud), clientId);
    String idToken = response.getId_token();
    DecodedJWT decodedJwt = validator.validateAndDecode(idToken);
    return transformDecodedJwtToToken(decodedJwt);
  }

  private static String urlEncode(String value) throws DuoException {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new DuoException(e.getMessage(), e);
    }
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

}
