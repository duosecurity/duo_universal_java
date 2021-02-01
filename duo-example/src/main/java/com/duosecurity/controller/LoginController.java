package com.duosecurity.controller;


import com.duosecurity.Client;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

  @Value("${duo.api.host}")
  private String apiHost;

  @Value("${duo.clientId}")
  private String clientId;

  @Value("${duo.clientSecret}")
  private String clientSecret;

  @Value("${duo.redirect.uri}")
  private String redirectUri;

  @Value("${duo.failmode}")
  private String failmode;

  private Map<String, String> stateMap;

  private Client duoClient;

  /**
   * Create and initialize the Duo Client.
   *
   * @throws DuoException For problems creating the Clients
   */
  @PostConstruct
  public void initializeDuoClient() throws DuoException {
    stateMap = new HashMap<>();
    duoClient = new Client.Builder(clientId, clientSecret, apiHost, redirectUri).build();

    /* Example of setting optional fields
    duoClient = new Client.Builder(clientId, clientSecret, apiHost, redirectUri)
            .setUseDuoCodeAttribute(false)
            .setCACerts(customCerts)
            .appendUserAgentInfo("custom string")
            .build();
    */
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index() {
    return "index";
  }

  /**
   * POST handler for login page.
   *
   * @param username        The username of the user trying to authenticate
   * @param password        The password of the user trying to authenticate
   *
   * @return ModelAndView   A model that contains information about where to redirect next
   */
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ModelAndView login(@RequestParam String username, @RequestParam String password)
      throws DuoException {
    // Perform fake primary authentication
    if (!validateUser(username, password)) {
      ModelAndView model = new ModelAndView("/index");
      model.addObject("message", "Invalid Credentials");
      return model;
    }

    // Step 2: Call Duo health check
    try {
      duoClient.healthCheck();
    } catch (DuoException exception) {
      // If the health check failed AND the integration is configured to fail open then render
      // the welcome page.  If the integarion is configured to fail closed return an error
      if ("OPEN".equalsIgnoreCase(failmode)) {
        ModelAndView model = new ModelAndView("/welcome");
        model.addObject("token", "Login Successful, but 2FA Not Performed."
                + "Confirm application.properties values are correct and that Duo is reachable");
        return model;
      } else {
        ModelAndView model = new ModelAndView("/index");
        model.addObject("message", "2FA Unavailable."
                + "Confirm application.properties values are correct and that Duo is reachable");
        return model;
      }
    }

    // Step 3: Generate and save a state variable
    String state = duoClient.generateState();
    // Store the state to remember the session and username
    stateMap.put(state, username);

    // Step 4: Create the authUrl and redirect to it
    String authUrl = duoClient.createAuthUrl(username, state);
    ModelAndView model = new ModelAndView("/redirect");
    model.addObject("authURL", authUrl);
    return model;
  }

  /**
   * GET handler for duo-callback page.
   *
   * @param duoCode    An authentication session transaction id
   * @param state   A random string returned from Duo used to maintain state
   *
   * @return ModelAndView   A model that contains information about where to redirect next
   */
  @RequestMapping(value = "/duo-callback", method = RequestMethod.GET)
  public ModelAndView duoCallback(@RequestParam("duo_code") String duoCode,
                                  @RequestParam("state") String state) throws DuoException {
    // Step 5: Validate state returned from Duo is the same as the one saved previously.
    // If it isn't return an error
    if (!stateMap.containsKey(state)) {
      ModelAndView model = new ModelAndView("/index");
      model.addObject("message", "Session Expired");
      return model;
    }
    // Remove state from the list of valid sessions
    String username = stateMap.remove(state);

    // Step 6: Exchange the auth duoCode for a Token object
    Token token = duoClient.exchangeAuthorizationCodeFor2FAResult(duoCode, username);

    // If the auth was successful, render the welcome page otherwise return an error
    if (authWasSuccessful(token)) {
      ModelAndView model = new ModelAndView("/welcome");
      model.addObject("token", tokenToJson(token));
      return model;
    } else {
      ModelAndView model = new ModelAndView("/index");
      model.addObject("message", "2FA Failed");
      return model;
    }
  }

  private static String tokenToJson(Token token) throws DuoException {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(token);
    } catch (JsonProcessingException jpe) {
      throw new DuoException("Could not convert token to JSON");
    }
  }

  /**
   * Exception handler.
   *
   * @param ex  A DuoException
   *
   * @return ModelAndView   A model object that contains the error message from ex
   */
  @ExceptionHandler({DuoException.class})
  public ModelAndView handleException(DuoException ex) {
    ModelAndView model = new ModelAndView("/index");
    model.addObject("message", ex.getMessage());
    return model;
  }

  private boolean validateUser(String username, String password) {
    if (username == null || username.isEmpty()
        || password == null || password.isEmpty()) {
      return false;
    }
    return true;
  }

  private boolean authWasSuccessful(Token token) {
    if (token != null && token.getAuth_result() != null) {
      return "ALLOW".equalsIgnoreCase(token.getAuth_result().getStatus());
    }
    return false;
  }
}
