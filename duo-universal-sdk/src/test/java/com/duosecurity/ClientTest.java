package com.duosecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.Token;
import com.duosecurity.model.TokenResponse;
import com.duosecurity.service.DuoConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

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
        this.client = new Client(CLIENT_ID, CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI);
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
            Client badClient = new Client("", CLIENT_SECRET, API_HOST, HTTPS_REDIRECT_URI);
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().equals("Invalid client id"));
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_client_secret() throws DuoException {
        try {
            Client badClient = new Client(CLIENT_ID, "", API_HOST, HTTPS_REDIRECT_URI);
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().equals("Invalid client secret"));
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_api_host() throws DuoException {
        try {
            Client badClient = new Client(CLIENT_ID, CLIENT_SECRET, "", HTTPS_REDIRECT_URI);
            badClient.createAuthUrl(USERNAME, STATE);
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("Invalid host"));
        }
    }

    @Test
    void createAuthUrl_throws_exception_for_invalid_redirect_uri() throws DuoException {
        try {
            Client badClient = new Client(CLIENT_ID, CLIENT_SECRET, API_HOST, "");
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

        Token result = client.exchangeAuthorizationCodeFor2FAResult("code", stubValidator);
        assertEquals(result.getSub(), "1234567890");
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_throws_exception_for_invalid_api_host() throws DuoException {
        try {
            Client badClient = new Client(CLIENT_ID, CLIENT_SECRET, "", HTTPS_REDIRECT_URI);
            badClient.exchangeAuthorizationCodeFor2FAResult("code", "username");
            Assertions.fail();
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("Invalid host"));
        }
    }
}
