package com.duosecurity.service;

import static com.duosecurity.Utils.getAndValidateUrl;

import com.duosecurity.exception.DuoException;
import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.TokenResponse;
import java.io.IOException;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class DuoConnector {

  protected Retrofit retrofit;

  private static final int SUCCESS_STATUS_CODE = 200;

  /**
   * DuoConnector Constructor.
   *
   * @param apiHost         This value is the api host provided by Duo in the admin panel.
   * @param caCerts         CA Certificates used to connect to Duo
   *
   * @throws DuoException   For issues getting and validating the URL
   */
  public DuoConnector(String apiHost, String[] caCerts) throws DuoException {
    CertificatePinner duoCertificatePinner = new CertificatePinner.Builder()
        .add(apiHost, caCerts)
        .build();

    OkHttpClient client = new OkHttpClient.Builder().certificatePinner(
                            duoCertificatePinner).build();

    retrofit = new Retrofit.Builder()
            .baseUrl(getAndValidateUrl(apiHost, "").toString())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(client)
            .build();
  }

  /**
   * Send Health Check request.
   *
   * @param clientId                The client id provided by Duo in the admin panel
   * @param clientAssertion         A JWT the Duo Health Check endpoint needs
   *
   * @return HealthCheckResponse    Returns result from the Health Check
   *
   * @throws DuoException           For issues sending or receiving the request
   */
  public HealthCheckResponse duoHealthcheck(String clientId, String clientAssertion)
      throws DuoException {
    DuoService service = retrofit.create(DuoService.class);
    Call<HealthCheckResponse> callSync = service.duoHealthCheck(clientId, clientAssertion);
    try {
      Response<HealthCheckResponse> response = callSync.execute();
      return response.body();
    } catch (IOException e) {
      throw new DuoException(e.getMessage(), e);
    }
  }

  /**
   * Send request to exchange duoCode for an encoded JWT.
   *
   * @param userAgent           A user agent string
   * @param grantType           A string that tells what type of exchange that will occur
   * @param duoCode             An authentication session transaction id
   * @param redirectUri         The URL to redirect back to after a successful auth
   * @param clientAssertionType The type of client assertion used
   * @param clientAssertion     JWT that holds information to verify that the owner of the duoCode
   *                            is authorized to have it
   *
   * @return TokenResponse  Returns resulting response containing the JWT
   *
   * @throws DuoException   For issues sending or receiving the request,
                            or failing to exchange a token
   */
  public TokenResponse exchangeAuthorizationCodeFor2FAResult(String userAgent, String grantType,
                                                             String duoCode, String redirectUri,
                                                             String clientAssertionType,
                                                             String clientAssertion)
      throws DuoException {
    DuoService service = retrofit.create(DuoService.class);
    Call<TokenResponse> callSync = service.exchangeAuthorizationCodeFor2FAResult(userAgent,
                            grantType, duoCode, redirectUri, clientAssertionType, clientAssertion);
    try {
      Response<TokenResponse> response = callSync.execute();
      if (response.code() != SUCCESS_STATUS_CODE || response.body() == null) {
        String message = response.message();
        if (response.errorBody() != null) {
          throw new DuoException(String.format("msg=%s, msg_detail=%s",
                  message, response.errorBody().string()));
        }
        throw new DuoException(message);
      }
      return response.body();
    } catch (IOException e) {
      throw new DuoException(e.getMessage(), e);
    }
  }
}
