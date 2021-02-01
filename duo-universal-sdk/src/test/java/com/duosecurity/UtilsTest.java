package com.duosecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.*;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    private static final String CLIENT_SECRET = "my_client_secret";

    private static String createTestJWT() throws UnsupportedEncodingException {
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
    void transformDecodedJwtToToken() throws UnsupportedEncodingException {
        String jwt = createTestJWT();    
        // Just testing the transform logic so a simple decode is sufficient
        DecodedJWT decodedJWT =  JWT.decode(jwt);
        Token token = Utils.transformDecodedJwtToToken(decodedJWT);

        assertEquals(token.getIss(), "issuer");
        assertEquals(token.getSub(), "test");
        assertEquals(token.getAud(), "aud");
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
