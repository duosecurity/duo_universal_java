package com.duosecurity.service;

import com.duosecurity.exception.DuoException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Utility class for loading CA certificates and creating custom TrustManagers.
 */
public class CertificateHelper {

  private static final String DEFAULT_CA_CERTS_RESOURCE = "ca_certs.pem";

  /**
   * Loads the bundled ca_certs.pem from classpath and returns an X509TrustManager
   * that trusts only those certificates.
   *
   * @return X509TrustManager configured with bundled CA certificates
   * @throws DuoException if certificates cannot be loaded
   */
  public static X509TrustManager loadDefaultTrustManager() throws DuoException {
    String pemContent = loadResourceAsString(DEFAULT_CA_CERTS_RESOURCE);
    return createTrustManager(pemContent);
  }

  /**
   * Creates an X509TrustManager from PEM-encoded certificate strings.
   *
   * @param pemCerts array of PEM-encoded certificate strings
   * @return X509TrustManager configured with the provided certificates
   * @throws DuoException if certificates cannot be parsed
   */
  public static X509TrustManager createTrustManager(String[] pemCerts) throws DuoException {
    StringBuilder combined = new StringBuilder();
    for (String pem : pemCerts) {
      combined.append(pem).append("\n");
    }
    return createTrustManager(combined.toString());
  }

  /**
   * Creates an X509TrustManager from a String containing PEM-encoded certificates.
   *
   * @param pemContent String containing PEM-encoded certificates
   * @return X509TrustManager configured with the parsed certificates
   * @throws DuoException if certificates cannot be parsed or trust manager cannot be created
   */
  public static X509TrustManager createTrustManager(String pemContent) throws DuoException {
    try {
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, null);

      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      ByteArrayInputStream pemStream = new ByteArrayInputStream(
          pemContent.getBytes(StandardCharsets.UTF_8));
      Collection<? extends java.security.cert.Certificate> certs =
          cf.generateCertificates(pemStream);

      if (certs.isEmpty()) {
        throw new DuoException("No certificates found in provided PEM input");
      }

      int index = 0;
      for (java.security.cert.Certificate cert : certs) {
        String alias = "duo-ca-" + index;
        if (cert instanceof X509Certificate) {
          alias = ((X509Certificate) cert).getSubjectDN().getName() + "-" + index;
        }
        keyStore.setCertificateEntry(alias, cert);
        index++;
      }

      TrustManagerFactory tmf = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(keyStore);

      for (TrustManager tm : tmf.getTrustManagers()) {
        if (tm instanceof X509TrustManager) {
          return (X509TrustManager) tm;
        }
      }
      throw new DuoException("No X509TrustManager found in TrustManagerFactory");
    } catch (KeyStoreException | CertificateException
        | NoSuchAlgorithmException | IOException e) {
      throw new DuoException("Failed to create trust manager: " + e.getMessage(), e);
    }
  }

  /**
   * Creates an SSLSocketFactory using the provided TrustManager.
   *
   * @param trustManager the X509TrustManager to use
   * @return SSLSocketFactory configured with the trust manager
   * @throws DuoException if the SSLContext cannot be initialized
   */
  public static SSLSocketFactory createSslSocketFactory(X509TrustManager trustManager)
      throws DuoException {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[]{trustManager}, null);
      return sslContext.getSocketFactory();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new DuoException("Failed to create SSLSocketFactory: " + e.getMessage(), e);
    }
  }

  private static String loadResourceAsString(String resourceName) throws DuoException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
        CertificateHelper.class.getClassLoader().getResourceAsStream(resourceName),
        StandardCharsets.UTF_8))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      return sb.toString();
    } catch (NullPointerException e) {
      throw new DuoException("Cannot find bundled CA certificates resource: " + resourceName);
    } catch (IOException e) {
      throw new DuoException("Failed to read CA certificates resource: " + e.getMessage(), e);
    }
  }
}
