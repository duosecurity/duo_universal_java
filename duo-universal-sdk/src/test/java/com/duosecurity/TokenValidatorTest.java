package com.duosecurity;

import com.duosecurity.exception.DuoException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;

class TokenValidatorTest {

    private static String API_HOST = "api-12345678.duosecurity.com";
    private static String EXPECTED_ISSUER = "https://" + API_HOST + "/oauth/v1/token";
    private static String EXPECTED_AUDIENCE = "audience";
    private static String EXPECTED_USERNAME = "username";
    private static String GOOD_SECRET = "secret";

    private static long ONE_SECOND_IN_MILLISECONDS = 1000;

    // Happy path
    @Test
    void testValidClaimsSuccess() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, EXPECTED_AUDIENCE, API_HOST);
            String jwt = createClaimsTestJwt();
            validator.validateAndDecode(jwt);
        } catch (DuoException e) {
            Assertions.fail("Valid token should pass validation");
        }
    }

    @Test
    void testValidNonceSuccess() throws UnsupportedEncodingException {
        try {
            String aNonce = "a_nonce";
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, EXPECTED_AUDIENCE, API_HOST, aNonce);
            Map<String, Object> headers = new HashMap<>();
            headers.put("algorithm", "HS512");
            headers.put("type", "JWT");

            String jwt = JWT.create()
                .withHeader(headers)
                .withIssuer(EXPECTED_ISSUER)
                .withSubject("duo_username")
                .withAudience(EXPECTED_AUDIENCE)
                .withExpiresAt(new Date())
                .withIssuedAt(new Date())
                .withClaim("nonce", aNonce)
                .withClaim("preferred_username", EXPECTED_USERNAME)
                .sign(Algorithm.HMAC512(GOOD_SECRET));

            validator.validateAndDecode(jwt);
        } catch (DuoException e) {
            Assertions.fail("Valid token should pass validation");
        }

    }

    @Test
    void nullTokenFailure() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, EXPECTED_AUDIENCE, "api-deadbeef.duosecurity.com");
            String jwt = null;
            validator.validateAndDecode(jwt);
            Assertions.fail("Null jwt should throw DuoException.");
        } catch (DuoException e) {
            // expected
        }
    }

    // Tests to enforce issuer, audience, and preferred_username claims
    @Test
    void issuerMismatchFailure() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, EXPECTED_AUDIENCE, "api-deadbeef.duosecurity.com");
            String jwt = createClaimsTestJwt();
            validator.validateAndDecode(jwt);
            Assertions.fail("Unexpected api_host should fail validation.");
        } catch (DuoException e) {
            // expected
        }
    }

    @Test
    void audienceMismatchFailure() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, "bad_audience", API_HOST);
            String jwt = createClaimsTestJwt();
            validator.validateAndDecode(jwt);
            Assertions.fail("Unexpected audience should fail validation.");
        } catch (DuoException e) {
            // expected
        }
    }

    @Test 
    void usernameMismatchFailure() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, "bad_username", EXPECTED_AUDIENCE, API_HOST);
            String jwt = createClaimsTestJwt();
            validator.validateAndDecode(jwt);
            Assertions.fail("Unexpected username should fail validation.");
        } catch (DuoException e) {
            // expected
        }
    }

    @Test
    void nonceNotInTokenFailure() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, "bad_username", EXPECTED_AUDIENCE, API_HOST, "nonce");
            String jwt = createClaimsTestJwt();
            validator.validateAndDecode(jwt);
            Assertions.fail("Missing nonce should fail validation.");
        } catch (DuoException e) {
            // expected
        }
    }

    // Tests for the allowed leeway on issued at and expiration claims
    @Test
    void withinDuoLeewaySuccess() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, EXPECTED_AUDIENCE, API_HOST);
            // Duo default leeway is 60 seconds; this expired 30 seconds ago
            String jwt = createClaimsTestJwt(secondsAgo(45), secondsAgo(30));
            validator.validateAndDecode(jwt);
        } catch (DuoException e) {
            Assertions.fail("Times within default leeway should pass validation.");
        }
    }

    @Test
    void outsideDuoLeewayFailure() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, EXPECTED_AUDIENCE, API_HOST);
            // Duo default leeway is 60 seconds; this expired 90 seconds ago
            String jwt = createClaimsTestJwt(secondsAgo(120), secondsAgo(90));
            validator.validateAndDecode(jwt);
            Assertions.fail("Token is past leeway and should be expired");
        } catch (DuoException e) {
            // expected
        }
    }

    // Tests for the signature check
    @Test 
    void secretMismatchFailure() throws UnsupportedEncodingException {
        try {
            TokenValidator validator = new DuoIdTokenValidator("bad_secret", EXPECTED_USERNAME, EXPECTED_AUDIENCE, API_HOST, "nonce");
            String jwt = createClaimsTestJwt();
            validator.validateAndDecode(jwt);
            Assertions.fail("Incorrect secret should fail validation.");
        } catch (DuoException e) {
            // expected
        }
    }

    @Test 
    void alteredPayloadFailure() throws UnsupportedEncodingException {
        String jwt1 = createClaimsTestJwt();
        String jwt2 = createClaimsTestJwt(secondsAgo(10), secondsAgo(10));
        String[] jwt1Pieces = jwt1.split("\\.");
        String[] jwt2Pieces = jwt2.split("\\.");
        // Combine the signature of jwt1 with the payload of jwt2
        String alteredJwt = jwt1Pieces[0] + "." + jwt2Pieces[1] + "." + jwt1Pieces[2];

        try {
            TokenValidator validator = new DuoIdTokenValidator(GOOD_SECRET, EXPECTED_USERNAME, EXPECTED_AUDIENCE, API_HOST);
            validator.validateAndDecode(alteredJwt);
            Assertions.fail("Altered payload should fail validation.");
        } catch (DuoException e) {
            // expected
        }
    }
   
    // Helper functions
    private String createClaimsTestJwt() throws UnsupportedEncodingException {
        return this.createClaimsTestJwt(new Date(), new Date());
    }

    private String createClaimsTestJwt(Date issued, Date expiration) throws UnsupportedEncodingException {
        Map<String, Object> headers = new HashMap<>();
        headers.put("algorithm", "HS512");
        headers.put("type", "JWT");

        String jwt = JWT.create()
            .withHeader(headers)
            .withIssuer(EXPECTED_ISSUER)
            .withSubject("duo_username")
            .withAudience(EXPECTED_AUDIENCE)
            .withExpiresAt(expiration)
            .withIssuedAt(issued)
            .withClaim("preferred_username", EXPECTED_USERNAME)
            .sign(Algorithm.HMAC512(GOOD_SECRET));

        return jwt;
    }

    private Date secondsAgo(int seconds) {
        Date a_date = new Date();
        a_date.setTime(a_date.getTime() - (seconds * ONE_SECOND_IN_MILLISECONDS));
        return a_date;
    }
}
