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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

  private static final String USER_AGENT_VERSION = "1.3.2-SNAPSHOT";

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
    private String[] caCerts;
    private String userAgent;

    private static final String[] DEFAULT_CA_CERTS = {
        //Source URL: https://www.amazontrust.com/repository/AmazonRootCA1.cer
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=Amazon Root CA 1,O=Amazon,C=US
        //Issuer: CN=Amazon Root CA 1,O=Amazon,C=US
        //Expiration Date: 2038-01-17 00:00:00
        //Serial Number: 66C9FCF99BF8C0A39E2F0788A43E696365BCA
        //SHA256 Fingerprint: 8ecde6884f3d87b1125ba31ac3fcb13d7016de7f57cc904fe1cb97c6ae98196e
        "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=",
        //Source URL: https://www.amazontrust.com/repository/AmazonRootCA2.cer
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=Amazon Root CA 2,O=Amazon,C=US
        //Issuer: CN=Amazon Root CA 2,O=Amazon,C=US
        //Expiration Date: 2040-05-26 00:00:00
        //Serial Number: 66C9FD29635869F0A0FE58678F85B26BB8A37
        //SHA256 Fingerprint: 1ba5b2aa8c65401a82960118f80bec4f62304d83cec4713a19c39c011ea46db4
        "sha256/f0KW/FtqTjs108NpYj42SrGvOB2PpxIVM8nWxjPqJGE=",
        //Source URL: https://www.amazontrust.com/repository/AmazonRootCA3.cer
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=Amazon Root CA 3,O=Amazon,C=US
        //Issuer: CN=Amazon Root CA 3,O=Amazon,C=US
        //Expiration Date: 2040-05-26 00:00:00
        //Serial Number: 66C9FD5749736663F3B0B9AD9E89E7603F24A
        //SHA256 Fingerprint: 18ce6cfe7bf14e60b2e347b8dfe868cb31d02ebb3ada271569f50343b46db3a4
        "sha256/NqvDJlas/GRcYbcWE8S/IceH9cq77kg0jVhZeAPXq8k=",
        //Source URL: https://www.amazontrust.com/repository/AmazonRootCA4.cer
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=Amazon Root CA 4,O=Amazon,C=US
        //Issuer: CN=Amazon Root CA 4,O=Amazon,C=US
        //Expiration Date: 2040-05-26 00:00:00
        //Serial Number: 66C9FD7C1BB104C2943E5717B7B2CC81AC10E
        //SHA256 Fingerprint: e35d28419ed02025cfa69038cd623962458da5c695fbdea3c22b0bfb25897092
        "sha256/9+ze1cZgR9KO1kZrVDxA4HQ6voHRCSVNz4RdTCx4U8U=",
        //Source URL: https://www.amazontrust.com/repository/SFSRootCAG2.cer
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=Starfield Services Root Certificate Authority - G2,
        // O=Starfield Technologies\, Inc.,L=Scottsdale,ST=Arizona,C=US
        //Issuer: CN=Starfield Services Root Certificate Authority - G2,
        // O=Starfield Technologies\, Inc.,L=Scottsdale,ST=Arizona,C=US
        //Expiration Date: 2037-12-31 23:59:59
        //Serial Number: 0
        //SHA256 Fingerprint: 568d6905a2c88708a4b3025190edcfedb1974a606a13c6e5290fcb2ae63edab5
        "sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=",
        //Source URL: https://cacerts.digicert.com/DigiCertHighAssuranceEVRootCA.crt
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=DigiCert High Assurance EV Root CA,OU=www.digicert.com,O=DigiCert Inc,C=US
        //Issuer: CN=DigiCert High Assurance EV Root CA,OU=www.digicert.com,O=DigiCert Inc,C=US
        //Expiration Date: 2031-11-10 00:00:00
        //Serial Number: 2AC5C266A0B409B8F0B79F2AE462577
        //SHA256 Fingerprint: 7431e5f4c3c1ce4690774f0b61e05440883ba9a01ed00ba6abd7806ed3b118cf
        "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=",
        //Source URL: https://cacerts.digicert.com/DigiCertTLSECCP384RootG5.crt
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=DigiCert TLS ECC P384 Root G5,O=DigiCert\, Inc.,C=US
        //Issuer: CN=DigiCert TLS ECC P384 Root G5,O=DigiCert\, Inc.,C=US
        //Expiration Date: 2046-01-14 23:59:59
        //Serial Number: 9E09365ACF7D9C8B93E1C0B042A2EF3
        //SHA256 Fingerprint: 018e13f0772532cf809bd1b17281867283fc48c6e13be9c69812854a490c1b05
        "sha256/oC+voZLIy4HLE0FVT5wFtxzKKokLDRKY1oNkfJYe+98=",
        //Source URL: https://cacerts.digicert.com/DigiCertTLSRSA4096RootG5.crt
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=DigiCert TLS RSA4096 Root G5,O=DigiCert\, Inc.,C=US
        //Issuer: CN=DigiCert TLS RSA4096 Root G5,O=DigiCert\, Inc.,C=US
        //Expiration Date: 2046-01-14 23:59:59
        //Serial Number: 8F9B478A8FA7EDA6A333789DE7CCF8A
        //SHA256 Fingerprint: 371a00dc0533b3721a7eeb40e8419e70799d2b0a0f2c1d80693165f7cec4ad75
        "sha256/ape1HIIZ6T5d7GS61YBs3rD4NVvkfnVwELcCRW4Bqv0=",
        //Source URL: https://secure.globalsign.com/cacert/rootr46.crt
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=GlobalSign Root R46,O=GlobalSign nv-sa,C=BE
        //Issuer: CN=GlobalSign Root R46,O=GlobalSign nv-sa,C=BE
        //Expiration Date: 2046-03-20 00:00:00
        //Serial Number: 11D2BBB9D723189E405F0A9D2DD0DF2567D1
        //SHA256 Fingerprint: 4fa3126d8d3a11d1c4855a4f807cbad6cf919d3a5a88b03bea2c6372d93c40c9
        "sha256/rn+WLLnmp9v3uDP7GPqbcaiRdd+UnCMrap73yz3yu/w=",
        //Source URL: https://secure.globalsign.com/cacert/roote46.crt
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=GlobalSign Root E46,O=GlobalSign nv-sa,C=BE
        //Issuer: CN=GlobalSign Root E46,O=GlobalSign nv-sa,C=BE
        //Expiration Date: 2046-03-20 00:00:00
        //Serial Number: 11D2BBBA336ED4BCE62468C50D841D98E843
        //SHA256 Fingerprint: cbb9c44d84b8043e1050ea31a69f514955d7bfd2e2c6b49301019ad61d9f5058
        "sha256/4EoCLOMvTM8sf2BGKHuCijKpCfXnUUR/g/0scfb9gXM=",
        //Source URL: https://i.pki.goog/r2.crt
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=GTS Root R2,O=Google Trust Services LLC,C=US
        //Issuer: CN=GTS Root R2,O=Google Trust Services LLC,C=US
        //Expiration Date: 2036-06-22 00:00:00
        //Serial Number: 203E5AEC58D04251AAB1125AA
        //SHA256 Fingerprint: 8d25cd97229dbf70356bda4eb3cc734031e24cf00fafcfd32dc76eb5841c7ea8
        "sha256/Vfd95BwDeSQo+NUYxVEEIlvkOlWY2SalKK1lPhzOx78=",
        //Source URL: https://i.pki.goog/r4.crt
        //Certificate #1 Details:
        //Original Format: DER
        //Subject: CN=GTS Root R4,O=Google Trust Services LLC,C=US
        //Issuer: CN=GTS Root R4,O=Google Trust Services LLC,C=US
        //Expiration Date: 2036-06-22 00:00:00
        //Serial Number: 203E5C068EF631A9C72905052
        //SHA256 Fingerprint: 349dfa4058c5e263123b398ae795573c4e1313c83fe68f93556cd5e8031b3c7d
        "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c=",
        //Source URL: https://www.identrust.com/file-download/download/public/5718
        //Certificate #1 Details:
        //Original Format: PKCS7-DER
        //Subject: CN=IdenTrust Commercial Root CA 1,O=IdenTrust,C=US
        //Issuer: CN=IdenTrust Commercial Root CA 1,O=IdenTrust,C=US
        //Expiration Date: 2034-01-16 18:12:23
        //Serial Number: A0142800000014523C844B500000002
        //SHA256 Fingerprint: 5d56499be4d2e08bcfcad08a3e38723d50503bde706948e42f55603019e528ae
        "sha256/B+hU8mp8vTiZJ6oEG/7xts0h3RQ4GK2UfcZVqeWH/og=",
        //Source URL: https://www.identrust.com/file-download/download/public/5842
        //Certificate #1 Details:
        //Original Format: PKCS7-PEM
        //Subject: CN=IdenTrust Commercial Root TLS ECC CA 2,O=IdenTrust,C=US
        //Issuer: CN=IdenTrust Commercial Root TLS ECC CA 2,O=IdenTrust,C=US
        //Expiration Date: 2039-04-11 21:11:10
        //Serial Number: 40018ECF000DE911D7447B73E4C1F82E
        //SHA256 Fingerprint: 983d826ba9c87f653ff9e8384c5413e1d59acf19ddc9c98cecae5fdea2ac229c
        "sha256/uu5PB+MS9L3/ffB/PuTG6A+WjsTtTaF52qqjrcHFXRU=",
        //Source URL: https://ssl-ccp.secureserver.net/repository/sfroot-g2.crt
        //Certificate #1 Details:
        //Original Format: PEM
        //Subject: CN=Starfield Root Certificate Authority - G2,
        // O=Starfield Technologies\, Inc.,L=Scottsdale,ST=Arizona,C=US
        //Issuer: CN=Starfield Root Certificate Authority - G2,
        // O=Starfield Technologies\, Inc.,L=Scottsdale,ST=Arizona,C=US
        //Expiration Date: 2037-12-31 23:59:59
        //Serial Number: 0
        //SHA256 Fingerprint: 2ce1cb0bf9d2f9e102993fbe215152c3b2dd0cabde1c68e5319b839154dbb7f5
        "sha256/gI1os/q0iEpflxrOfRBVDXqVoWN3Tz7Dav/7IT++THQ=",
    };

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
      this.caCerts = DEFAULT_CA_CERTS;
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
      this.caCerts = DEFAULT_CA_CERTS;
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

      Client client = new Client();
      client.clientId = clientId;
      client.clientSecret = clientSecret;
      client.apiHost = apiHost;
      client.redirectUri = redirectUri;
      client.useDuoCodeAttribute = useDuoCodeAttribute;
      client.userAgent = userAgent;
      client.duoConnector = new DuoConnector(apiHost, proxyHost, proxyPort, caCerts);

      return client;
    }

    /**
     * Optionally use custom CA Certificates when validating connections to Duo.
     *
     * @param userCaCerts List of CA Certificates to use
     * 
     * @return the Builder
     */
    public Builder setCACerts(String[] userCaCerts) {
      if (validateCaCert(userCaCerts)) {
        this.caCerts = userCaCerts;
      }
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
    validateUsername(username);
    validateState(state);
    String request = createJwtForAuthUrl(clientId, clientSecret, redirectUri,
            state, username, useDuoCodeAttribute);
    String query;
	try {
		query = format(
		        "?scope=openid&response_type=code&redirect_uri=%s&client_id=%s&request=%s",
		        URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString()), clientId, request);
	} catch (UnsupportedEncodingException e) {
		throw new IllegalStateException("Could not find encoding: " + StandardCharsets.UTF_8.toString());
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

}
