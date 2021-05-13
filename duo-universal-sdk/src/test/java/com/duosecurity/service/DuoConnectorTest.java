package com.duosecurity.service;

import com.duosecurity.exception.DuoException;
import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.TokenResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DuoConnectorTest {

    private static final String API_HOST = "my_api_host.com";
    private static final String[] CA_CERT = {"sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                                             "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="};

    private DuoConnector duoConnector;

    @Test
    void duoHealthcheck() throws IOException, DuoException {
        DuoConnector duoConnector = new DuoConnector(API_HOST, CA_CERT);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<HealthCheckResponse> callSync = Mockito.mock(Call.class);
        HealthCheckResponse healthCheckResponse = new HealthCheckResponse();
        healthCheckResponse.setMessage("success");
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.duoHealthCheck("client_id", "client_assertion")).thenReturn(callSync);
        when(callSync.execute()).thenReturn(Response.success(healthCheckResponse));

        HealthCheckResponse result = duoConnector.duoHealthcheck("client_id", "client_assertion");
        assertEquals("success", result.getMessage());
    }

    @Test
    void duoHealthcheck_network_failure() throws IOException, DuoException {
        DuoConnector duoConnector = new DuoConnector(API_HOST, CA_CERT);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<HealthCheckResponse> callSync = Mockito.mock(Call.class);
        HealthCheckResponse healthCheckResponse = new HealthCheckResponse();
        healthCheckResponse.setMessage("success");
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.duoHealthCheck("client_id", "client_assertion")).thenReturn(callSync);
        when(callSync.execute()).thenThrow(new IOException("Timeout"));

        HealthCheckResponse result = null;
        try {
            result = duoConnector.duoHealthcheck("client_id", "client_assertion");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Timeout", e.getMessage());
        }
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult() throws IOException, DuoException {
        DuoConnector duoConnector = new DuoConnector(API_HOST, CA_CERT);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setId_token("token");
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion")).thenReturn(callSync);
        when(callSync.execute()).thenReturn(Response.success(tokenResponse));

        TokenResponse result = duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion");
        assertEquals("token", result.getId_token());
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_network_failure() throws IOException, DuoException {
        DuoConnector duoConnector = new DuoConnector(API_HOST, CA_CERT);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setId_token("token");
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion")).thenReturn(callSync);
        when(callSync.execute()).thenThrow(new IOException("Timeout"));

        TokenResponse result = null;
        try {
            result = duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                    "client_assertion_type", "client_assertion");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Timeout", e.getMessage());
        }
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_error_code() throws IOException, DuoException {
        DuoConnector duoConnector = new DuoConnector(API_HOST, CA_CERT);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion")).thenReturn(callSync);

        // Create a 400 response (body doesn't matter)
        okhttp3.ResponseBody body = okhttp3.ResponseBody.create(null, "");
        when(callSync.execute()).thenReturn(Response.error(400, body));

        try {
            duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                    "client_assertion_type", "client_assertion");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Response.error()", e.getMessage());
        }
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_null_body() throws IOException, DuoException {
        DuoConnector duoConnector = new DuoConnector(API_HOST, CA_CERT);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion")).thenReturn(callSync);

        // Create a successful (200) response with a null body
        when(callSync.execute()).thenReturn(Response.success(200, null));

        try {
            duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                    "client_assertion_type", "client_assertion");
            Assertions.fail();
        } catch (DuoException e) {
            // Response.success() is the error message because that's the default message when manually crafting
            // a successful (200) response. This still verifies we're properly creating the DuoExpection.
            assertEquals("Response.success()", e.getMessage());
        }
    }
}
