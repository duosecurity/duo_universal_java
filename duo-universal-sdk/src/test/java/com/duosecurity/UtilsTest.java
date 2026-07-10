package com.duosecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.*;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    private static final String CLIENT_SECRET = "my_client_secret";

    private static String createTestJWT() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("algorithm", "HS512");
        headers.put("type", "JWT");

        AuthContext authContext = new AuthContext();
        AuthDevice authDevice = new AuthDevice();
        authDevice.setLocation(new Location());
        AccessDevice accessDevice = new AccessDevice();
        accessDevice.setLocation(new Location());
        authContext.setAccess_device(accessDevice);
        authContext.setAuth_device(authDevice);
        authContext.setApplication(new Application());
        authContext.setUser(new User());

        String jwt = JWT.create()
            .withHeader(headers)
            .withIssuer("issuer")
            .withSubject("test")
            .withAudience("aud")
            .withExpiresAt(new Date())
            .withIssuedAt(new Date())
            .withClaim("auth_time", 1572969826)
            .withClaim("auth_context", String.valueOf(authContext))
            .withClaim("auth_result", String.valueOf(new AuthResult()))
            .sign(Algorithm.HMAC512(CLIENT_SECRET));

        return jwt;
    }

    @Test
    void createJWT() throws DuoException {
        String jwt = Utils.createJwt("my_client_id", CLIENT_SECRET, "my_aud");
        // Just testing the transform logic so a simple decode is sufficient
        DecodedJWT decodedJWT = JWT.decode(jwt);
        assertEquals(decodedJWT.getClaim("iss").asString(), "my_client_id");
        assertEquals(decodedJWT.getClaim("aud").asString(), "my_aud");
    }

    @Test
    void createJWTForAuthURL() throws DuoException {
        String jwt = Utils.createJwtForAuthUrl("my_client_id", CLIENT_SECRET, "my_redirect_uri", "my_state", "my_username", true);
        // Just testing the transform logic so a simple decode is sufficient
        DecodedJWT decodedJWT = JWT.decode(jwt);
        assertEquals(decodedJWT.getClaim("client_id").asString(), "my_client_id");
        assertEquals(decodedJWT.getClaim("redirect_uri").asString(), "my_redirect_uri");
        assertEquals(decodedJWT.getClaim("state").asString(), "my_state");
        assertEquals(decodedJWT.getClaim("duo_uname").asString(), "my_username");
    }

    @Test
    void transformDecodedJwtToToken() {
        String jwt = createTestJWT();
        // Just testing the transform logic so a simple decode is sufficient
        DecodedJWT decodedJWT =  JWT.decode(jwt);
        Token token = Utils.transformDecodedJwtToToken(decodedJWT);

        assertEquals(token.getIss(), "issuer");
        assertEquals(token.getSub(), "test");
        assertEquals(token.getAud(), "aud");
        // amr claim is optional; when absent, the field should be null.
        assertNull(token.getAmr());
    }

    @Test
    void transformDecodedJwtToTokenWithAmr() {
        List<String> amr = Arrays.asList("mfa", "otp");
        String jwt = JWT.create()
            .withIssuer("issuer")
            .withSubject("test")
            .withAudience("aud")
            .withArrayClaim("amr", amr.toArray(new String[0]))
            .sign(Algorithm.HMAC512(CLIENT_SECRET));
        DecodedJWT decodedJWT = JWT.decode(jwt);

        Token token = Utils.transformDecodedJwtToToken(decodedJWT);

        assertEquals(amr, token.getAmr());
    }

    @Test
    void transformDecodedJwtToTokenWithEmptyAmr() {
        String jwt = JWT.create()
            .withIssuer("issuer")
            .withSubject("test")
            .withAudience("aud")
            .withArrayClaim("amr", new String[0])
            .sign(Algorithm.HMAC512(CLIENT_SECRET));
        DecodedJWT decodedJWT = JWT.decode(jwt);

        Token token = Utils.transformDecodedJwtToToken(decodedJWT);

        assertEquals(Collections.emptyList(), token.getAmr());
    }

    @Test
    void transformDecodedJwtToTokenWithNullAmr() {
        String jwt = JWT.create()
            .withIssuer("issuer")
            .withSubject("test")
            .withAudience("aud")
            .withNullClaim("amr")
            .sign(Algorithm.HMAC512(CLIENT_SECRET));
        DecodedJWT decodedJWT = JWT.decode(jwt);

        Token token = Utils.transformDecodedJwtToToken(decodedJWT);

        assertNull(token.getAmr());
    }

    @Test
    void transformDecodedJwtToTokenWithNonArrayAmr() {
        String jwt = JWT.create()
            .withIssuer("issuer")
            .withSubject("test")
            .withAudience("aud")
            .withClaim("amr", "mfa")
            .sign(Algorithm.HMAC512(CLIENT_SECRET));
        DecodedJWT decodedJWT = JWT.decode(jwt);

        Token token = Utils.transformDecodedJwtToToken(decodedJWT);

        assertNull(token.getAmr());
    }

    @Test
    void transformDecodedJwtToTokenWithNumericAmrElements() {
        // Jackson coerces numeric elements to their string form when the target
        // type is String, so this does not throw and yields ["1", "2"].
        // The try/catch in extractAmr is defense-in-depth for genuinely
        // non-coercible element types.
        String jwt = JWT.create()
            .withIssuer("issuer")
            .withSubject("test")
            .withAudience("aud")
            .withArrayClaim("amr", new Integer[]{1, 2})
            .sign(Algorithm.HMAC512(CLIENT_SECRET));
        DecodedJWT decodedJWT = JWT.decode(jwt);

        Token token = assertDoesNotThrow(() -> Utils.transformDecodedJwtToToken(decodedJWT));

        assertEquals(Arrays.asList("1", "2"), token.getAmr());
    }

    @Test
    void tokenEqualityRespectsAmrField() {
        Token a = new Token();
        a.setAmr(Arrays.asList("mfa"));
        Token b = new Token();
        b.setAmr(Arrays.asList("mfa"));
        Token c = new Token();
        c.setAmr(Arrays.asList("otp"));
        Token d = new Token();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, d);
    }

    @Test
    void getAndValidateUrl() throws DuoException {
        URL result = Utils.getAndValidateUrl("my_host", "/file");
        assertEquals(result.getHost(), "my_host");
        assertEquals(result.getFile(), "/file");
    }

    @Test
    void generateJWTId() {
        String jwtId = Utils.generateJwtId(32);
        assertEquals(jwtId.length(), 32);
        for (Character c : jwtId.toCharArray()) {
            assertTrue(Character.isLetterOrDigit(c));
        }
    }
}
