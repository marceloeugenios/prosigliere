package br.com.challenge.objectmother;

import br.com.challenge.controller.dto.BlogPostRequestDto;
import java.util.UUID;

public class BlogPostRequestDtoMother {

  public static final UUID DEFAULT_AUTHOR = UUID.fromString("00000000-0000-7000-8000-000000000002");

  public static BlogPostRequestDto random() {
    return new BlogPostRequestDto(
        UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID());
  }

  public static BlogPostRequestDto valid() {
    return new BlogPostRequestDto(
        "My First Post", "This is the content of the post.", DEFAULT_AUTHOR);
  }

  public static BlogPostRequestDto withTitle(final String title) {
    return new BlogPostRequestDto(title, "This is the content of the post.", DEFAULT_AUTHOR);
  }

  public static BlogPostRequestDto withContent(final String content) {
    return new BlogPostRequestDto("My First Post", content, DEFAULT_AUTHOR);
  }

  public static BlogPostRequestDto withAuthor(final UUID author) {
    return new BlogPostRequestDto("My First Post", "This is the content of the post.", author);
  }

  public static BlogPostRequestDto withBlankTitle() {
    return new BlogPostRequestDto("", "This is the content of the post.", DEFAULT_AUTHOR);
  }

  public static BlogPostRequestDto withBlankContent() {
    return new BlogPostRequestDto("My First Post", "", DEFAULT_AUTHOR);
  }

  public static BlogPostRequestDto withNullAuthor() {
    return new BlogPostRequestDto("My First Post", "This is the content of the post.", null);
  }

  public static BlogPostRequestDto withTitleExceedingMaxLength() {
    return new BlogPostRequestDto(
        "A".repeat(256), "This is the content of the post.", DEFAULT_AUTHOR);
  }
}
