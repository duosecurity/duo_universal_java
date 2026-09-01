package com.duosecurity.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenTest {

    @Test
    void tokens_with_different_nonces_are_not_equal() {
        Token token = new Token();
        token.setNonce("a_nonce");
        Token other = new Token();
        other.setNonce("a_different_nonce");

        assertNotEquals(token, other);
        assertNotEquals(token.hashCode(), other.hashCode());
    }

    @Test
    void tokens_with_the_same_nonce_are_equal() {
        Token token = new Token();
        token.setNonce("a_nonce");
        Token other = new Token();
        other.setNonce("a_nonce");

        assertEquals(token, other);
        assertEquals(token.hashCode(), other.hashCode());
    }

    @Test
    void toString_includes_nonce() {
        Token token = new Token();
        token.setNonce("a_nonce");

        assertTrue(token.toString().contains("nonce=a_nonce"));
    }
}
