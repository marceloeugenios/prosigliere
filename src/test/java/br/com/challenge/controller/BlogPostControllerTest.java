package br.com.challenge.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.challenge.config.BaseTest;
import br.com.challenge.controller.dto.BlogPostDetailDto;
import br.com.challenge.controller.dto.BlogPostRequestDto;
import br.com.challenge.controller.dto.BlogPostSummaryDto;
import br.com.challenge.controller.dto.CommentDto;
import br.com.challenge.controller.dto.CommentRequestDto;
import br.com.challenge.controller.dto.PagedResponse;
import br.com.challenge.exception.response.ErrorResponseDto;
import br.com.challenge.objectmother.BlogPostRequestDtoMother;
import br.com.challenge.objectmother.CommentRequestDtoMother;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

class BlogPostControllerTest extends BaseTest {

  private static final UUID NON_EXISTENT_ID =
      UUID.fromString("00000000-0000-7000-8000-000000000001");

  private static final String BASE_URL = "/api/v1/posts";

  @Test
  void createPost_returns201_whenRequestIsValid() throws Exception {

    final var blogPostRequestDto = BlogPostRequestDtoMother.random();

    final var response = saveBlogPost(blogPostRequestDto);

    assertThat(response.id()).isNotNull();
    assertThat(response.title()).isEqualTo(blogPostRequestDto.title());
    assertThat(response.content()).isEqualTo(blogPostRequestDto.content());
    assertThat(response.comments()).isNull();
  }

  @Test
  void createPost_returns400_whenTitleIsBlank() throws Exception {
    final var blogPostRequestDto = BlogPostRequestDtoMother.withBlankTitle();
    final var body =
        mockMvc
            .perform(
                post(BASE_URL)
                    .header(AUTH_HEADER, VALID_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(blogPostRequestDto)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
    ErrorResponseDto response = jsonMapper.readValue(body, ErrorResponseDto.class);
    assertThat(response.message()).isEqualTo("title must not be blank");
  }

  @Test
  void createPost_returns400_whenContentIsMissing() throws Exception {
    final var blogPostRequestDto = BlogPostRequestDtoMother.withBlankContent();
    final var body =
        mockMvc
            .perform(
                post(BASE_URL)
                    .header(AUTH_HEADER, VALID_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(blogPostRequestDto)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final var response = jsonMapper.readValue(body, ErrorResponseDto.class);
    assertThat(response.message()).isEqualTo("content must not be blank");
  }

  @Test
  void createPost_returns401_whenTokenIsMissing() throws Exception {
    final var blogPostRequestDto = BlogPostRequestDtoMother.random();
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(blogPostRequestDto)))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Ideally we should have some filter in the search and than I would be able to assert the
   * pagination, but since we don't have it, I just want to make sure that the endpoint is working
   * and returning a list of posts.
   */
  @Test
  void listPosts_returns200_withPaginatedResults() throws Exception {

    final var blogPostRequestDto1 = BlogPostRequestDtoMother.random();
    final var blogPostRequestDto2 = BlogPostRequestDtoMother.random();

    saveBlogPost(blogPostRequestDto1);
    saveBlogPost(blogPostRequestDto2);

    String body =
        mockMvc
            .perform(get(BASE_URL).header(AUTH_HEADER, VALID_TOKEN))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    PagedResponse<BlogPostSummaryDto> response =
        jsonMapper.readValue(body, new TypeReference<>() {});

    assertThat(response.content()).isNotEmpty();
  }

  @Test
  void listPosts_returns401_whenTokenIsMissing() throws Exception {
    mockMvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
  }

  @Test
  void getPost_returns200_withComments_whenPostExists() throws Exception {
    final var blogPostRequestDto1 = BlogPostRequestDtoMother.random();

    final var savedBlogPost = saveBlogPost(blogPostRequestDto1);

    final var commentRequestDto = CommentRequestDtoMother.random();

    addCommentIntoPost(savedBlogPost.id(), commentRequestDto);

    final String body =
        mockMvc
            .perform(get(BASE_URL + "/" + savedBlogPost.id()).header(AUTH_HEADER, VALID_TOKEN))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    BlogPostDetailDto response = jsonMapper.readValue(body, BlogPostDetailDto.class);
    assertThat(response.id()).isEqualTo(savedBlogPost.id());
    assertThat(response.title()).isEqualTo(blogPostRequestDto1.title());
    assertThat(response.content()).isEqualTo(blogPostRequestDto1.content());
    assertThat(response.comments()).hasSize(1);
    assertThat(response.comments().getFirst().author()).isEqualTo(commentRequestDto.author());
    assertThat(response.comments().getFirst().content()).isEqualTo(commentRequestDto.content());
  }

  @Test
  void getPost_returns401_whenTokenIsMissing() throws Exception {
    mockMvc.perform(get("/api/v1/posts/1")).andExpect(status().isUnauthorized());
  }

  @Test
  void getPost_returns404_whenPostDoesNotExist() throws Exception {
    final String body =
        mockMvc
            .perform(get("/api/v1/posts/" + NON_EXISTENT_ID).header(AUTH_HEADER, VALID_TOKEN))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponseDto response = jsonMapper.readValue(body, ErrorResponseDto.class);
    assertThat(response.message()).isEqualTo("BlogPost with id " + NON_EXISTENT_ID + " not found");
  }

  @Test
  void addComment_returns201_whenRequestIsValid() throws Exception {

    final var blogPostRequestDto1 = BlogPostRequestDtoMother.random();

    final var savedBlogPost = saveBlogPost(blogPostRequestDto1);

    final var commentRequestDto = CommentRequestDtoMother.random();

    final String body = addCommentIntoPost(savedBlogPost.id(), commentRequestDto);

    CommentDto response = jsonMapper.readValue(body, CommentDto.class);
    assertThat(response.id()).isNotNull();
    assertThat(response.content()).isEqualTo(commentRequestDto.content());
    assertThat(response.author()).isEqualTo(commentRequestDto.author());
  }

  @Test
  void addComment_returns400_whenContentIsBlank() throws Exception {

    final var blogPostRequestDto1 = BlogPostRequestDtoMother.random();

    final var savedBlogPost = saveBlogPost(blogPostRequestDto1);

    mockMvc
        .perform(
            post("/api/v1/posts/" + savedBlogPost.id() + "/comments")
                .header(AUTH_HEADER, VALID_TOKEN)
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(CommentRequestDtoMother.withBlankContent())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addComment_returns401_whenTokenIsMissing() throws Exception {
    final var commentRequestDto = CommentRequestDtoMother.random();
    mockMvc
        .perform(
            post("/api/v1/posts/1/comments")
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(commentRequestDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getPost_returns200_withUpdatedComments_afterCacheEviction() throws Exception {
    final var savedPost = saveBlogPost(BlogPostRequestDtoMother.random());

    addCommentIntoPost(savedPost.id(), CommentRequestDtoMother.random());

    final String cachedBody =
        mockMvc
            .perform(get(BASE_URL + "/" + savedPost.id()).header(AUTH_HEADER, VALID_TOKEN))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(jsonMapper.readValue(cachedBody, BlogPostDetailDto.class).comments()).hasSize(1);

    final String secondCommentBody =
        addCommentIntoPost(savedPost.id(), CommentRequestDtoMother.random());
    assertThat(jsonMapper.readValue(secondCommentBody, CommentDto.class).id()).isNotNull();

    final String body =
        mockMvc
            .perform(get(BASE_URL + "/" + savedPost.id()).header(AUTH_HEADER, VALID_TOKEN))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(jsonMapper.readValue(body, BlogPostDetailDto.class).comments()).hasSize(2);
  }

  @Test
  void createPost_returns400_whenAuthorIsNull() throws Exception {
    mockMvc
        .perform(
            post(BASE_URL)
                .header(AUTH_HEADER, VALID_TOKEN)
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(BlogPostRequestDtoMother.withNullAuthor())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addComment_returns400_whenAuthorIsNull() throws Exception {
    final var savedPost = saveBlogPost(BlogPostRequestDtoMother.random());
    mockMvc
        .perform(
            post("/api/v1/posts/" + savedPost.id() + "/comments")
                .header(AUTH_HEADER, VALID_TOKEN)
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(CommentRequestDtoMother.withNullAuthor())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addComment_returns404_whenPostDoesNotExist() throws Exception {
    final var commentRequestDto = CommentRequestDtoMother.random();
    String body =
        mockMvc
            .perform(
                post("/api/v1/posts/" + NON_EXISTENT_ID + "/comments")
                    .header(AUTH_HEADER, VALID_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(commentRequestDto)))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final var response = jsonMapper.readValue(body, ErrorResponseDto.class);

    assertThat(response.message()).isEqualTo("BlogPost with id " + NON_EXISTENT_ID + " not found");
  }

  private BlogPostDetailDto saveBlogPost(BlogPostRequestDto blogPostRequestDto) throws Exception {
    final var body =
        mockMvc
            .perform(
                post(BASE_URL)
                    .header(AUTH_HEADER, VALID_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(blogPostRequestDto)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final var blogPostDetailDto = jsonMapper.readValue(body, BlogPostDetailDto.class);

    assertThat(blogPostDetailDto.id()).isNotNull();
    assertThat(blogPostDetailDto.title()).isEqualTo(blogPostRequestDto.title());
    assertThat(blogPostDetailDto.content()).isEqualTo(blogPostRequestDto.content());
    assertThat(blogPostDetailDto.comments()).isNull();
    return blogPostDetailDto;
  }

  private @NonNull String addCommentIntoPost(final UUID postId, CommentRequestDto commentRequestDto)
      throws Exception {
    return mockMvc
        .perform(
            post("/api/v1/posts/" + postId + "/comments")
                .header(AUTH_HEADER, VALID_TOKEN)
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(commentRequestDto)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }
}
