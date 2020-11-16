package com.duosecurity;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.duosecurity.exception.DuoException;

public interface TokenValidator {
  DecodedJWT validateAndDecode(String jwt) throws DuoException;
}
