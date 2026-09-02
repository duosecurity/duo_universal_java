package com.duosecurity;

/**
 * The set of values that describe a single authorization request.
 *
 * <p>Instances are created with {@link AuthUrlOptions.Builder} and passed to
 * {@link Client#createAuthUrl(AuthUrlOptions)}.
 */
public class AuthUrlOptions {

  private final String username;
  private final String state;
  private final String nonce;
  private final String destAppName;
  private final String destAppId;
  private final String displayUsername;

  private AuthUrlOptions(Builder builder) {
    this.username = builder.username;
    this.state = builder.state;
    this.nonce = builder.nonce;
    this.destAppName = builder.destAppName;
    this.destAppId = builder.destAppId;
    this.displayUsername = builder.displayUsername;
  }

  public String getUsername() {
    return username;
  }

  public String getState() {
    return state;
  }

  public String getNonce() {
    return nonce;
  }

  public String getDestAppName() {
    return destAppName;
  }

  public String getDestAppId() {
    return destAppId;
  }

  public String getDisplayUsername() {
    return displayUsername;
  }

  /**
   * Builds an {@link AuthUrlOptions}.
   */
  public static class Builder {
    private final String username;
    private final String state;
    private String nonce;
    private String destAppName;
    private String destAppId;
    private String displayUsername;

    /**
     * Builder.
     *
     * @param username The user to be authenticated by Duo.
     * @param state A randomly generated String of 16 to 1024 characters. This value will be
     *              returned to the integration post 2FA and should be validated.
     *              {@link Client#generateState} exists as a utility function to generate it.
     */
    public Builder(String username, String state) {
      this.username = username;
      this.state = state;
    }

    /**
     * Optionally bind the resulting ID token to this authorization request with a nonce.
     * The same value must be passed to
     * {@link Client#exchangeAuthorizationCodeFor2FAResult(String, String, String)}, which will
     * reject an ID token that does not carry it.
     *
     * @param nonce A randomly generated String of 16 to 1024 characters
     *
     * @return the Builder
     */
    public Builder setNonce(String nonce) {
      this.nonce = nonce;
      return this;
    }

    /**
     * Optionally set the user-facing name of the application the user is authenticating to.
     * Duo shows this name in Duo Mobile and records it in the authentication log.
     *
     * @param destAppName The name of the destination application
     *
     * @return the Builder
     */
    public Builder setDestAppName(String destAppName) {
      this.destAppName = destAppName;
      return this;
    }

    /**
     * Optionally set a long-lived unique identifier for the destination application.
     * This value is not shown to users.
     *
     * @param destAppId The identifier of the destination application
     *
     * @return the Builder
     */
    public Builder setDestAppId(String destAppId) {
      this.destAppId = destAppId;
      return this;
    }

    /**
     * Optionally set the username shown in the Duo Mobile "user" field for Duo Push.
     * Duo shows the authenticating username when this is not set. This does not change which
     * user Duo authenticates.
     *
     * @param displayUsername The username to display to the user
     *
     * @return the Builder
     */
    public Builder setDisplayUsername(String displayUsername) {
      this.displayUsername = displayUsername;
      return this;
    }

    /**
     * Build the options object.
     *
     * @return {@link AuthUrlOptions}
     */
    public AuthUrlOptions build() {
      return new AuthUrlOptions(this);
    }
  }
}
