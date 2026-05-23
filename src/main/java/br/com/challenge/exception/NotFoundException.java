package br.com.challenge.exception;

public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NotFoundException() {
    this("Registro não encontrado");
  }

  public NotFoundException(String message) {
    super(message);
  }
}
