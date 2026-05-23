package br.com.challenge.objectmother;

import br.com.challenge.controller.dto.CommentRequestDto;
import java.util.UUID;

public class CommentRequestDtoMother {

  public static final UUID DEFAULT_AUTHOR = UUID.fromString("00000000-0000-7000-8000-000000000001");

  public static CommentRequestDto random() {
    return new CommentRequestDto(UUID.randomUUID().toString(), UUID.randomUUID());
  }

  public static CommentRequestDto valid() {
    return new CommentRequestDto("Great post!", DEFAULT_AUTHOR);
  }

  public static CommentRequestDto withContent(final String content) {
    return new CommentRequestDto(content, DEFAULT_AUTHOR);
  }

  public static CommentRequestDto withAuthor(final UUID author) {
    return new CommentRequestDto("Great post!", author);
  }

  public static CommentRequestDto withBlankContent() {
    return new CommentRequestDto("", DEFAULT_AUTHOR);
  }

  public static CommentRequestDto withNullAuthor() {
    return new CommentRequestDto("Great post!", null);
  }
}
