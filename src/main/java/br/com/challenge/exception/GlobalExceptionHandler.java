package br.com.challenge.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import br.com.challenge.exception.response.ErrorResponseDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<ErrorResponseDto> handleInternalServerError(
      Exception ex, WebRequest request) {
    LOGGER.error("InternalServerError", ex);
    return response(INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler({NotAuthorizedException.class})
  public ResponseEntity<ErrorResponseDto> handleInternalUnauthorized(
      Exception ex, WebRequest request) {
    return response(UNAUTHORIZED, ex.getMessage());
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<ErrorResponseDto> handleNotFoundException(
      Exception ex, WebRequest request) {
    return response(NOT_FOUND, ex.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
      Exception ex, WebRequest request) {
    return response(BAD_REQUEST, ex.getMessage());
  }

  @ResponseBody
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    List<String> validations =
        ex.getBindingResult().getAllErrors().stream()
            .map(
                error ->
                    error instanceof FieldError fe
                        ? fe.getField() + " " + fe.getDefaultMessage()
                        : error.getDefaultMessage())
            .toList();
    String errors = String.join(", ", validations);
    return response(BAD_REQUEST, errors);
  }

  private ResponseEntity<ErrorResponseDto> response(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorResponseDto(status.value(), message));
  }
}
