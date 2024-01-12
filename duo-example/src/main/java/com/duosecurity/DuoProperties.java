package com.duosecurity;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Duo Configuration Properties.
 *
 * @param clientId The duo client_id.
 * @param clientSecret The duo client_secret.
 * @param apiHost The duo api host.
 * @param redirectUrl The redirect url.
 * @param failMode This is used to decide what to do if Duo is unavailable
 */
@ConfigurationProperties(prefix = "duo")
public record DuoProperties(
    String clientId,
    String clientSecret,
    String apiHost,
    String redirectUrl,
    String failMode) {
}
