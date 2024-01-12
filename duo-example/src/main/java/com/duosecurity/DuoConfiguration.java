package com.duosecurity;

import com.duosecurity.exception.DuoException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Duo Client Configuration class that exposes a DuoClient bean.
 */
@Configuration
public class DuoConfiguration {

  /**
  * Duo Client bean.
  *
  * @param duoProperties The Autowired Duo Properties.
  * @return The Client.
  * @throws DuoException if there is an exception setting up the Client.
  */
  @Bean
  public Client duoClient(DuoProperties duoProperties) throws DuoException {

    /* Example of setting optional fields
      duoClient = new Client.Builder(
        duoProperties.clientId(),
        duoProperties.clientSecret(),
        duoProperties.apiHost(),
        duoProperties.redirectUrl()
      )
      .setUseDuoCodeAttribute(false)
      .setCACerts(customCerts)
      .appendUserAgentInfo("custom string")
      .build();
    */

    return new Client.Builder(
      duoProperties.clientId(),
      duoProperties.clientSecret(),
      duoProperties.apiHost(),
      duoProperties.redirectUrl()
    ).build();
  }
}
