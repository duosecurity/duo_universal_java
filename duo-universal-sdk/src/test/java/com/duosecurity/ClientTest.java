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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

class ClientTest {

    private static final String CLIENT_ID = "DIEN9ZH50WBGER236YT5";
    private static final String CLIENT_SECRET = "IZjstQj23454IB2H1qhoQj23Ws2ddZfIOGSxOGSx";
    private static final String API_HOST = "api-host.com";
    private static final String HTTPS_REDIRECT_URI = "https://redirect-uri.com";
    private static final String STATE = "abcdefghijklmnopqrstuvwxyz123456";
    private static final String USERNAME = "username";

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
            assertEquals("error", e.getMessage());
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
            assertEquals("invalid client secret", e.getMessage());
        }
    }

    @Test
    void createAuthUrl_success() throws DuoException {
        String urlString = client.createAuthUrl(USERNAME, STATE);
        try {
            URL authUrl = new URL(urlString);
            assertEquals(API_HOST, authUrl.getHost());
            assertTrue(authUrl.getQuery().contains("redirect_uri=" + URLEncoder.encode(HTTPS_REDIRECT_URI, StandardCharsets.UTF_8)));
            assertTrue(authUrl.getQuery().contains("client_id=" + URLEncoder.encode(CLIENT_ID,StandardCharsets.UTF_8)));
            assertEquals("https",authUrl.getProtocol());
        } catch (MalformedURLException e) {
            Assertions.fail();
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_username() {
        try {
            client.createAuthUrl("", STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Missing username", e.getMessage());
        }

    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_state() {
        try {
            client.createAuthUrl(USERNAME, "");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Invalid state",e.getMessage());
        }

    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_client_id() throws DuoException {
        try {
            Client badClient = new Client.Builder("", CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI).build();
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Invalid client id",e.getMessage());
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_client_secret() throws DuoException {
        try {
            Client badClient = new Client.Builder(CLIENT_ID, "", API_HOST, HTTPS_REDIRECT_URI).build();
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Invalid client secret",e.getMessage());
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
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(tokenResponse);

        TokenValidator stubValidator = new TokenValidator() {
            @Override
            public DecodedJWT validateAndDecode(String jwt) throws DuoException {
                return JWT.decode(jwt);
            }
        };

        Token result = client.exchangeAuthorizationCodeFor2FAResult("duo_code", stubValidator);
        assertEquals("1234567890", result.getSub() );
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
        verify(client.duoConnector).exchangeAuthorizationCodeFor2FAResult(stringCaptor.capture(), anyString(), anyString(), anyString(), anyString(), anyString());
        String sentUserAgent = stringCaptor.getValue();
        assertTrue(sentUserAgent.startsWith("duo_universal_java") && sentUserAgent.endsWith(appendedUserAgent));
    }

}
