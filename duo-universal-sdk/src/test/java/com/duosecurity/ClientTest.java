package com.duosecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.Token;
import com.duosecurity.model.TokenResponse;
import com.duosecurity.service.DuoConnector;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

class ClientTest {

    private static final String CLIENT_ID = "DIEN9ZH50WBGER236YT5";
    private static final String CLIENT_SECRET = "IZjstQj23454IB2H1qhoQj23Ws2ddZfIOGSxOGSx";
    private static final String API_HOST = "api-host.com";
    private static final String HTTPS_REDIRECT_URI = "https://redirect-uri.com";
    private static final String STATE = "abcdefghijklmnopqrstuvwxyz123456";
    private static final String USERNAME = "username";
    private static final String NONCE = "abcdefghijklmnopqrstuvwxyz789012";

    private Client client;

    @BeforeEach
    void setUp() throws DuoException {
        this.client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI).build();
        client.duoConnector = Mockito.mock(DuoConnector.class);
    }

    @Test
    void healthCheck_success() throws DuoException {
        HealthCheckResponse healthCheckResponse = new HealthCheckResponse();
        healthCheckResponse.setStat("OK");
        Mockito.when(client.duoConnector.duoHealthcheck(anyString(), any())).thenReturn(healthCheckResponse);

        HealthCheckResponse result = client.healthCheck();

        assertEquals(healthCheckResponse, result);
    }

    @Test
    void healthCheck_throws_exception() throws DuoException {
        Mockito.when(client.duoConnector.duoHealthcheck(anyString(), any())).thenThrow(new DuoException("error"));
        HealthCheckResponse result = null;
        try {
            result = client.healthCheck();
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals(e.getMessage(), "error");
        }
    }

    @Test
    void healthCheck_throws_exception_when_stat_not_OK() throws DuoException {
        HealthCheckResponse healthCheckResponse = new HealthCheckResponse();
        healthCheckResponse.setStat("FAIL");
        healthCheckResponse.setMessage("invalid client secret");
        Mockito.when(client.duoConnector.duoHealthcheck(anyString(), any())).thenReturn(healthCheckResponse);
        HealthCheckResponse result = null;
        try {
            result = client.healthCheck();
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals(e.getMessage(), "invalid client secret");
        }
    }

    @Test
    void createAuthUrl_success() throws DuoException {
        String urlString = client.createAuthUrl(USERNAME, STATE);
        try {
            URL authUrl = new URL(urlString);
            assertEquals(authUrl.getHost(), API_HOST);
            assertTrue(authUrl.getQuery().contains("redirect_uri=" + HTTPS_REDIRECT_URI));
            assertTrue(authUrl.getQuery().contains("client_id=" + CLIENT_ID));
            assertTrue(authUrl.getProtocol().equals("https"));
        } catch (MalformedURLException e) {
            Assertions.fail();
        }
    }

    @Test
    void createAuthUrl_includes_nonce_in_query() throws DuoException {
        String urlString = client.createAuthUrl(USERNAME, STATE, NONCE);
        HttpUrl url = HttpUrl.parse(urlString);
        assertEquals(NONCE, url.queryParameter("nonce"));
    }

    @Test
    void createAuthUrl_omits_nonce_when_not_supplied() throws DuoException {
        String urlString = client.createAuthUrl(USERNAME, STATE);
        HttpUrl url = HttpUrl.parse(urlString);
        assertNull(url.queryParameter("nonce"));
    }

    @Test
    void createAuthUrl_encodes_nonce() throws DuoException {
        // A nonce is caller supplied, so reserved characters in it must not be able to
        // introduce additional query parameters.
        String urlString = client.createAuthUrl(USERNAME, STATE, "nonce&redirect_uri=evil");
        HttpUrl url = HttpUrl.parse(urlString);
        assertEquals("nonce&redirect_uri=evil", url.queryParameter("nonce"));
        assertEquals(HTTPS_REDIRECT_URI, url.queryParameter("redirect_uri"));
    }

    @Test
    void createAuthUrl_throws_exception_for_short_nonce() {
        try {
            client.createAuthUrl(USERNAME, STATE, "123456789012345");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Invalid nonce", e.getMessage());
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_long_nonce() {
        try {
            client.createAuthUrl(USERNAME, STATE, repeat("a", 1025));
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Invalid nonce", e.getMessage());
        }
    }

    @Test
    void createAuthUrl_accepts_nonce_at_length_boundaries() throws DuoException {
        // Duo documents the nonce as 16-1024 characters, inclusive on both ends.
        assertNotNull(client.createAuthUrl(USERNAME, STATE, repeat("a", 16)));
        assertNotNull(client.createAuthUrl(USERNAME, STATE, repeat("a", 1024)));
    }

    private static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Test
    void createAuthUrl_with_options_sends_username_and_state() throws DuoException {
        String urlString = client.createAuthUrl(new AuthUrlOptions.Builder(USERNAME, STATE).build());

        DecodedJWT jwt = decodeRequestJwt(urlString);
        assertEquals(USERNAME, jwt.getClaim("duo_uname").asString());
        assertEquals(STATE, jwt.getClaim("state").asString());
    }

    @Test
    void createAuthUrl_sends_dest_app_name() throws DuoException {
        String urlString = client.createAuthUrl(
                new AuthUrlOptions.Builder(USERNAME, STATE).setDestAppName("Acme VPN").build());

        assertEquals("Acme VPN", decodeRequestJwt(urlString).getClaim("dest_app_name").asString());
    }

    @Test
    void createAuthUrl_sends_dest_app_id() throws DuoException {
        String urlString = client.createAuthUrl(
                new AuthUrlOptions.Builder(USERNAME, STATE).setDestAppId("vpn-prod-1").build());

        assertEquals("vpn-prod-1", decodeRequestJwt(urlString).getClaim("dest_app_id").asString());
    }

    @Test
    void createAuthUrl_sends_display_username() throws DuoException {
        String urlString = client.createAuthUrl(new AuthUrlOptions.Builder(USERNAME, STATE)
                .setDisplayUsername("a.smith@acme.com").build());

        DecodedJWT jwt = decodeRequestJwt(urlString);
        assertEquals("a.smith@acme.com", jwt.getClaim("display_username").asString());
        // display_username only changes what Duo shows the user; the username Duo authenticates
        // and later returns as preferred_username must be unaffected.
        assertEquals(USERNAME, jwt.getClaim("duo_uname").asString());
    }

    @Test
    void createAuthUrl_omits_optional_claims_that_were_not_set() throws DuoException {
        String urlString = client.createAuthUrl(new AuthUrlOptions.Builder(USERNAME, STATE).build());

        DecodedJWT jwt = decodeRequestJwt(urlString);
        // Duo treats an absent claim differently from one present with a null value, so an
        // unset option must leave the claim out of the JWT entirely.
        assertTrue(jwt.getClaim("dest_app_name").isMissing());
        assertTrue(jwt.getClaim("dest_app_id").isMissing());
        assertTrue(jwt.getClaim("display_username").isMissing());
    }

    @Test
    void createAuthUrl_throws_exception_for_null_options() {
        try {
            client.createAuthUrl((AuthUrlOptions) null);
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Missing options", e.getMessage());
        }
    }

    private static DecodedJWT decodeRequestJwt(String urlString) {
        return JWT.decode(HttpUrl.parse(urlString).queryParameter("request"));
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_username() {
        try {
            client.createAuthUrl("", STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().equals("Missing username"));
        }

    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_state() {
        try {
            client.createAuthUrl(USERNAME, "");
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().equals("Invalid state"));
        }

    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_client_id() throws DuoException {
        try {
            Client badClient = new Client.Builder("", CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI).build();
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().equals("Invalid client id"));
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_client_secret() throws DuoException {
        try {
            Client badClient = new Client.Builder(CLIENT_ID, "", API_HOST, HTTPS_REDIRECT_URI).build();
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().equals("Invalid client secret"));
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_api_host() throws DuoException {
        try {
            Client badClient = new Client.Builder(CLIENT_ID, CLIENT_SECRET, "", HTTPS_REDIRECT_URI).build();
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("Invalid host"));
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_redirect_uri() throws DuoException {
        try {
            Client badClient = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, "").build();
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("no protocol: "));
        }
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_success() throws DuoException {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setId_token("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.qKLZNpctaGsuqLr6KkPiM7_9jG5sEEaLPLakrA1kjk7z0lF3HX_RTRS3c4wVFWMEV_jGg72KIjlBpsWrqMxSNg");
        Mockito.when(client.duoConnector.exchangeAuthorizationCodeFor2FAResult(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(tokenResponse);

        TokenValidator stubValidator = new TokenValidator() {
            @Override
            public DecodedJWT validateAndDecode(String jwt) throws DuoException {
                return JWT.decode(jwt);
            }
        };

        Token result = client.exchangeAuthorizationCodeFor2FAResult("duo_code", stubValidator);
        assertEquals(result.getSub(), "1234567890");
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_sends_client_id() throws DuoException {
        try {
            client.exchangeAuthorizationCodeFor2FAResult("duo_code", Mockito.mock(TokenValidator.class));
        } catch (Exception e) {
            // The call fails due to the incomplete mocking, but we only care about the
            // arguments passed to the connector, so this is fine and can be ignored.
        }

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(client.duoConnector).exchangeAuthorizationCodeFor2FAResult(anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), stringCaptor.capture());
        assertEquals(CLIENT_ID, stringCaptor.getValue());
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_rejects_mismatched_nonce() throws DuoException {
        stubIdToken(createIdToken(NONCE));

        try {
            client.exchangeAuthorizationCodeFor2FAResult("duo_code", USERNAME, "a_different_nonce");
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("ID Token verification failed"));
        }
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_accepts_matching_nonce() throws DuoException {
        stubIdToken(createIdToken(NONCE));

        Token result = client.exchangeAuthorizationCodeFor2FAResult("duo_code", USERNAME, NONCE);

        assertEquals(NONCE, result.getNonce());
    }

    private void stubIdToken(String idToken) throws DuoException {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setId_token(idToken);
        Mockito.when(client.duoConnector.exchangeAuthorizationCodeFor2FAResult(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(tokenResponse);
    }

    private String createIdToken(String nonce) {
        return JWT.create()
                .withIssuer("https://" + API_HOST + "/oauth/v1/token")
                .withSubject("duo_subject")
                .withAudience(CLIENT_ID)
                .withIssuedAt(new java.util.Date())
                .withExpiresAt(new java.util.Date())
                .withClaim("preferred_username", USERNAME)
                .withClaim("nonce", nonce)
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC512(CLIENT_SECRET));
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_throws_exception_for_invalid_api_host() throws DuoException {
        try {
            Client badClient = new Client.Builder(CLIENT_ID, CLIENT_SECRET, "", HTTPS_REDIRECT_URI).build();
            badClient.exchangeAuthorizationCodeFor2FAResult("duo_code", "username");
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("Invalid host"));
        }
    }

    @Test
    void useDuoCodeAttribute_defaults_true() throws DuoException {
        // Don't rely on setUp Client just in case that ever changes to longform constructor
        Client client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI).build();
        String urlString = client.createAuthUrl(USERNAME, STATE);
        HttpUrl url = HttpUrl.parse(urlString);
        String jwtString = url.queryParameter("request");
        DecodedJWT jwt = JWT.decode(jwtString);
        Boolean use_code = jwt.getClaim("use_duo_code_attribute").asBoolean();
        assertTrue(use_code);
    }

    @Test
    void useDuoCodeAttribute_set_false() throws DuoException {
        Client client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI)
                .setUseDuoCodeAttribute(false)
                .build();
        String urlString = client.createAuthUrl(USERNAME, STATE);
        HttpUrl url = HttpUrl.parse(urlString);
        String jwtString = url.queryParameter("request");
        DecodedJWT jwt = JWT.decode(jwtString);
        Boolean use_code = jwt.getClaim("use_duo_code_attribute").asBoolean();
        assertFalse(use_code);
    }

    @Test
    void custom_useragent() throws DuoException {
        String appendedUserAgent = "foobar";

        Client client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI)
                .appendUserAgentInfo(appendedUserAgent)
                .build();

        client.duoConnector = Mockito.mock(DuoConnector.class);

        try {
            client.exchangeAuthorizationCodeFor2FAResult("duo_code", Mockito.mock(TokenValidator.class));
        }
        catch(Exception e) {
            // The calls will fail due to the incomplete mocking, but we're only interesting
            // in the calling arguments, so this is fine and can be ignored.
        }

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(client.duoConnector).exchangeAuthorizationCodeFor2FAResult(stringCaptor.capture(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        String sentUserAgent = stringCaptor.getValue();
        assertTrue(sentUserAgent.startsWith("duo_universal_java") && sentUserAgent.contains(appendedUserAgent));
    }

    @Test
    void userAgent_includes_ca_bundle_version() throws DuoException {
        Client client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI).build();
        client.duoConnector = Mockito.mock(DuoConnector.class);

        try {
            client.exchangeAuthorizationCodeFor2FAResult("duo_code", Mockito.mock(TokenValidator.class));
        } catch (Exception e) {
            // ignored
        }

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(client.duoConnector).exchangeAuthorizationCodeFor2FAResult(stringCaptor.capture(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        String sentUserAgent = stringCaptor.getValue();
        assertTrue(sentUserAgent.contains("ca_bundle/1.0"));
    }

    @Test
    void userAgent_includes_ca_pinning_enabled() throws DuoException {
        Client client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI).build();
        client.duoConnector = Mockito.mock(DuoConnector.class);

        try {
            client.exchangeAuthorizationCodeFor2FAResult("duo_code", Mockito.mock(TokenValidator.class));
        } catch (Exception e) {
            // ignored
        }

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(client.duoConnector).exchangeAuthorizationCodeFor2FAResult(stringCaptor.capture(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        String sentUserAgent = stringCaptor.getValue();
        assertTrue(sentUserAgent.contains("(ca_pinning=enabled)"));
    }

    @Test
    void userAgent_includes_ca_pinning_disabled() throws DuoException {
        Client client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI)
                .disableCaPinning()
                .build();
        client.duoConnector = Mockito.mock(DuoConnector.class);

        try {
            client.exchangeAuthorizationCodeFor2FAResult("duo_code", Mockito.mock(TokenValidator.class));
        } catch (Exception e) {
            // ignored
        }

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(client.duoConnector).exchangeAuthorizationCodeFor2FAResult(stringCaptor.capture(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        String sentUserAgent = stringCaptor.getValue();
        assertTrue(sentUserAgent.contains("(ca_pinning=disabled)"));
    }

    @Test
    void disableCaPinning_builds_successfully() throws DuoException {
        Client client = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI)
                .disableCaPinning()
                .build();
        assertNotNull(client);
    }

    @Test
    void disableCaPinning_with_custom_certs_throws_exception() {
        try {
            new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI)
                    .setCACerts(new String[]{"sha256/test"})
                    .disableCaPinning()
                    .build();
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("Cannot both disable CA pinning and provide custom certificates"));
        }
    }

    @Test
    void disableCaPinning_then_custom_certs_throws_exception() {
        try {
            new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI)
                    .disableCaPinning()
                    .setCACerts(new String[]{"sha256/test"})
                    .build();
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("Cannot both disable CA pinning and provide custom certificates"));
        }
    }

    @Test
    void legacy_constructors_match() throws DuoException {
        // Create clients using the old deprecated constructors and check that their fields are the same as one created using the builder.
        // This should help prevent adding a new field to the class and forgetting to update the legacy constructors.
        // Unfortunately duoConnector is an object entity that can't be compared.

        Client builderClient = new Client.Builder(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI).build();
        Client shortConstructorClient = new Client(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI);
        Client longConstructorClient = new Client(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI, null);

        assertTrue(new ReflectionEquals(builderClient, "duoConnector").matches(shortConstructorClient));
        assertTrue(new ReflectionEquals(builderClient, "duoConnector").matches(longConstructorClient));
    }

}
