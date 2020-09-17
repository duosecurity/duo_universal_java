package com.duosecurity.service;

import com.duosecurity.model.HealthCheckResponse;
import com.duosecurity.model.TokenResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

interface DuoService {

  @FormUrlEncoded
  @POST("/oauth/v1/health_check")
  Call<HealthCheckResponse> duoHealthCheck(@Field("client_id") String clientId,
                                           @Field("client_assertion") String clientAuthorization);

  @FormUrlEncoded
  @POST("/oauth/v1/token")
  Call<TokenResponse> exchangeAuthorizationCodeFor2FAResult(@Header("user-agent") String userAgent,
                                    @Field("grant_type") String grantType,
                                    @Field("code") String duoCode,
                                    @Field("redirect_uri") String redirectUri,
                                    @Field("client_assertion_type") String clientAssertionType,
                                    @Field("client_assertion") String clientAssertion);

}
