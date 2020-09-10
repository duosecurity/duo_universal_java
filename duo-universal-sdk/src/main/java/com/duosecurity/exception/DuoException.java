package com.duosecurity.exception;

public class DuoException extends Exception {

  public DuoException(String message) {
    super(message);
  }

  public DuoException(String message, Throwable e) {
    super(message, e);
  }
}
