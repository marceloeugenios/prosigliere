package br.com.challenge.exception;

public class NotAuthorizedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NotAuthorizedException() {
    super(NotAuthorizedException.class.getName());
  }

  public NotAuthorizedException(String msg) {
    super(msg);
  }

  public NotAuthorizedException(Exception e) {
    super(e);
  }
}
