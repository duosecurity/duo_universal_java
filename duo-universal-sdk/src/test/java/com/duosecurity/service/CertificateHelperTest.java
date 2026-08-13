package com.duosecurity.service;

import com.duosecurity.exception.DuoException;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;

class CertificateHelperTest {

    private static final int EXPECTED_CERT_COUNT = 15;

    @Test
    void loadDefaultTrustManager_loadsAllCerts() throws DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        assertNotNull(tm);
        X509Certificate[] acceptedIssuers = tm.getAcceptedIssuers();
        assertEquals(EXPECTED_CERT_COUNT, acceptedIssuers.length);
    }

    @Test
    void createTrustManager_withPemArray_succeeds() throws DuoException {
        String pemContent = loadResourceAsString("ca_certs.pem");
        X509TrustManager tm = CertificateHelper.createTrustManager(new String[]{pemContent});
        assertNotNull(tm);
        assertEquals(EXPECTED_CERT_COUNT, tm.getAcceptedIssuers().length);
    }

    @Test
    void createTrustManager_withString_succeeds() throws DuoException {
        String pemContent = loadResourceAsString("ca_certs.pem");
        X509TrustManager tm = CertificateHelper.createTrustManager(pemContent);
        assertNotNull(tm);
        assertEquals(EXPECTED_CERT_COUNT, tm.getAcceptedIssuers().length);
    }

    @Test
    void createTrustManager_withEmptyString_throwsDuoException() {
        try {
            CertificateHelper.createTrustManager("");
            fail("Expected DuoException");
        } catch (DuoException e) {
            assertTrue(e.getMessage().contains("No certificates found"));
        }
    }

    @Test
    void createTrustManager_withInvalidPem_throwsDuoException() {
        String invalidPem = "-----BEGIN CERTIFICATE-----\nNOT_VALID_BASE64!!!\n-----END CERTIFICATE-----\n";
        try {
            CertificateHelper.createTrustManager(invalidPem);
            fail("Expected DuoException");
        } catch (DuoException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void createSslSocketFactory_returnsNonNull() throws DuoException {
        X509TrustManager tm = CertificateHelper.loadDefaultTrustManager();
        SSLSocketFactory sf = CertificateHelper.createSslSocketFactory(tm);
        assertNotNull(sf);
    }

    private static String loadResourceAsString(String resourceName) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(
                    CertificateHelperTest.class.getClassLoader().getResourceAsStream(resourceName),
                    java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + resourceName, e);
        }
    }
}
