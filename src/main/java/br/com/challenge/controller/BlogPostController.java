package br.com.challenge.controller;

import br.com.challenge.controller.dto.BlogPostDetailDto;
import br.com.challenge.controller.dto.BlogPostRequestDto;
import br.com.challenge.controller.dto.BlogPostSummaryDto;
import br.com.challenge.controller.dto.CommentDto;
import br.com.challenge.controller.dto.CommentRequestDto;
import br.com.challenge.controller.dto.PagedResponse;
import br.com.challenge.controller.mapper.BlogPostMapper;
import br.com.challenge.controller.mapper.CommentMapper;
import br.com.challenge.domain.BlogPost;
import br.com.challenge.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blog Posts", description = "Manage blog posts and their comments")
@Validated
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class BlogPostController {

  private final BlogPostService blogPostService;
  private final BlogPostMapper blogPostMapper;
  private final CommentMapper commentMapper;

  @Operation(summary = "Create a blog post")
  @ApiResponse(responseCode = "201", description = "Post created")
  @ApiResponse(responseCode = "400", description = "Validation error")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BlogPostDetailDto createPost(@Valid @RequestBody final BlogPostRequestDto request) {

    final BlogPost entity = blogPostMapper.toEntity(request);

    final BlogPost saved = blogPostService.createPost(entity);

    return blogPostMapper.toDetailDto(saved);
  }

  @Operation(
      summary = "List blog posts",
      description = "Returns posts ordered by creation date descending, newest first.")
  @ApiResponse(responseCode = "200", description = "Paginated list of posts")
  @GetMapping
  public PagedResponse<BlogPostSummaryDto> listPosts(
      @Parameter(description = "Page number (zero-based)", example = "0")
          @RequestParam(defaultValue = "0")
          final int page,
      @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10")
          final int size) {

    final Page<BlogPost> result = blogPostService.findAll(page, size);

    final List<BlogPostSummaryDto> content =
        result.getContent().stream().map(blogPostMapper::toSummaryDto).toList();

    return new PagedResponse<>(
        content, result.getTotalElements(), result.getTotalPages(), result.getNumber(), size);
  }

  @Operation(summary = "Get a blog post with its comments")
  @ApiResponse(responseCode = "200", description = "Post found")
  @ApiResponse(responseCode = "404", description = "Post not found")
  @GetMapping("/{id}")
  public BlogPostDetailDto getPost(
      @Parameter(description = "Blog post ID", example = "01963f5e-4a00-7e8b-a3c2-53f98c7b1234")
          @PathVariable
          final UUID id) {

    BlogPost post = blogPostService.findById(id);

    return blogPostMapper.toDetailDto(post);
  }

  @Operation(summary = "Add a comment to a blog post")
  @ApiResponse(responseCode = "201", description = "Comment added")
  @ApiResponse(responseCode = "400", description = "Validation error")
  @ApiResponse(responseCode = "404", description = "Post not found")
  @PostMapping("/{postId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CommentDto addComment(
      @Parameter(description = "Blog post ID", example = "01963f5e-4a00-7e8b-a3c2-53f98c7b1234")
          @PathVariable
          final UUID postId,
      @Valid @RequestBody final CommentRequestDto request) {

    final var comment = commentMapper.toEntity(request);

    final var savedComment = blogPostService.saveComment(postId, comment);

    return commentMapper.toDto(savedComment);
  }
}
