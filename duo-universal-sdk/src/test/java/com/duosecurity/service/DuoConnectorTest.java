package com.duosecurity.service;

import com.duosecurity.exception.DuoException;
import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.TokenResponse;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DuoConnectorTest {

    private static final String API_HOST = "my_api_host.com";

    @Test
    void duoHealthcheck() throws IOException, DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
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
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<HealthCheckResponse> callSync = Mockito.mock(Call.class);
        HealthCheckResponse healthCheckResponse = new HealthCheckResponse();
        healthCheckResponse.setMessage("success");
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.duoHealthCheck("client_id", "client_assertion")).thenReturn(callSync);
        when(callSync.execute()).thenThrow(new IOException("Timeout"));

        try {
            duoConnector.duoHealthcheck("client_id", "client_assertion");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Timeout", e.getMessage());
        }
    }

    @Test
    void constructor_with_null_ssl_uses_system_defaults() throws Exception {
        DuoConnector duoConnector = new DuoConnector(API_HOST, null, null);
        OkHttpClient client = getOkHttpClient(duoConnector);
        // When null is passed, we don't call builder.sslSocketFactory(),
        // so OkHttp uses its default (OS trust store).
        // Verify it's the same as a default OkHttpClient would have.
        OkHttpClient defaultClient = new OkHttpClient();
        assertEquals(defaultClient.sslSocketFactory().getClass(),
                client.sslSocketFactory().getClass());
    }

    @Test
    void constructor_with_null_ssl_and_proxy_uses_system_defaults() throws Exception {
        DuoConnector duoConnector = new DuoConnector(API_HOST, "proxy.example.com", 8080,
                null, null);
        OkHttpClient client = getOkHttpClient(duoConnector);
        assertNotNull(client.sslSocketFactory());
        assertNotNull(client.proxy());
    }

    @Test
    void constructor_with_custom_ssl_uses_provided_factory() throws Exception {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
        OkHttpClient client = getOkHttpClient(duoConnector);
        assertSame(sf, client.sslSocketFactory());
        // Verify our trust store contains exactly 15 bundled CA certs
        assertEquals(15, tm.getAcceptedIssuers().length);
    }

    @Test
    void constructor_with_custom_ssl_and_proxy_uses_provided_factory() throws Exception {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, "proxy.example.com", 8080, sf, tm);
        OkHttpClient client = getOkHttpClient(duoConnector);
        assertSame(sf, client.sslSocketFactory());
        assertNotNull(client.proxy());
    }

    private OkHttpClient getOkHttpClient(DuoConnector connector) throws Exception {
        Field retrofitField = DuoConnector.class.getDeclaredField("retrofit");
        retrofitField.setAccessible(true);
        Retrofit retrofit = (Retrofit) retrofitField.get(connector);
        Field clientField = Retrofit.class.getDeclaredField("callFactory");
        clientField.setAccessible(true);
        return (OkHttpClient) clientField.get(retrofit);
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult() throws IOException, DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setId_token("token");
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion", "client_id")).thenReturn(callSync);
        when(callSync.execute()).thenReturn(Response.success(tokenResponse));

        TokenResponse result = duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion", "client_id");
        assertEquals("token", result.getId_token());
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_without_client_id_omits_the_field() throws IOException, DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        when(retrofit.create(DuoService.class)).thenReturn(duoService);

        try {
            duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                    "client_assertion_type", "client_assertion");
        } catch (Exception e) {
            // The unstubbed service call returns a null Call, so executing it fails. We only care
            // about the arguments the connector forwarded, so this can be ignored.
        }

        // Retrofit drops a null @Field from the form body, so this overload sends exactly what it
        // sent before client_id was added.
        Mockito.verify(duoService).exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code",
                "redirect_uri", "client_assertion_type", "client_assertion", null);
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_network_failure() throws IOException, DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setId_token("token");
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion", "client_id")).thenReturn(callSync);
        when(callSync.execute()).thenThrow(new IOException("Timeout"));

        try {
            duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                    "client_assertion_type", "client_assertion", "client_id");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Timeout", e.getMessage());
        }
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_error_code() throws IOException, DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion", "client_id")).thenReturn(callSync);

        okhttp3.ResponseBody body = okhttp3.ResponseBody.create(null, "");
        when(callSync.execute()).thenReturn(Response.error(400, body));

        try {
            duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                    "client_assertion_type", "client_assertion", "client_id");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("msg=Response.error(), msg_detail=", e.getMessage());
        }
    }

    @Test
    void exchangeAuthorizationCodeFor2FAResult_null_body() throws IOException, DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        DuoConnector duoConnector = new DuoConnector(API_HOST, sf, tm);
        Retrofit retrofit = Mockito.mock(Retrofit.class);
        duoConnector.retrofit = retrofit;
        DuoService duoService = Mockito.mock(DuoService.class);
        Call<TokenResponse> callSync = Mockito.mock(Call.class);
        when(retrofit.create(DuoService.class)).thenReturn(duoService);
        when(duoService.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                "client_assertion_type", "client_assertion", "client_id")).thenReturn(callSync);

        when(callSync.execute()).thenReturn(Response.success(200, null));

        try {
            duoConnector.exchangeAuthorizationCodeFor2FAResult("user-agent", "grant_type", "duo_code", "redirect_uri",
                    "client_assertion_type", "client_assertion", "client_id");
            Assertions.fail();
        } catch (DuoException e) {
            assertEquals("Response.success()", e.getMessage());
        }
    }
}
