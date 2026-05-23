package br.com.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.challenge.domain.BlogPost;
import br.com.challenge.domain.Comment;
import br.com.challenge.exception.NotFoundException;
import br.com.challenge.repository.BlogPostRepository;
import br.com.challenge.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceImplTest {

  @Mock private BlogPostRepository blogPostRepository;
  @Mock private CommentRepository commentRepository;
  @InjectMocks private BlogPostServiceImpl service;

  @Test
  void createPost_savesAndReturnsPost() {
    final var post = new BlogPost();
    when(blogPostRepository.save(post)).thenReturn(post);

    final var result = service.createPost(post);

    assertThat(result).isSameAs(post);
    verify(blogPostRepository).save(post);
  }

  @Test
  void findAll_usesDescCreatedAtSort_withCorrectPageParams() {
    final Page<BlogPost> page = new PageImpl<>(List.of());
    final ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
    when(blogPostRepository.findAll(captor.capture())).thenReturn(page);

    service.findAll(2, 15);

    final PageRequest captured = captor.getValue();
    assertThat(captured.getPageNumber()).isEqualTo(2);
    assertThat(captured.getPageSize()).isEqualTo(15);
    assertThat(captured.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdAt"));
  }

  @Test
  void findById_returnsBlogPost_whenExists() {
    final var id = UUID.randomUUID();
    final var post = new BlogPost();
    when(blogPostRepository.fetchById(id)).thenReturn(Optional.of(post));

    assertThat(service.findById(id)).isSameAs(post);
  }

  @Test
  void findById_throwsNotFoundException_whenPostDoesNotExist() {
    final var id = UUID.randomUUID();
    when(blogPostRepository.fetchById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(id))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("BlogPost with id " + id + " not found");
  }

  @Test
  void saveComment_persistsCommentWithCorrectAssociations() {
    final var postId = UUID.randomUUID();
    final var post = new BlogPost();

    final var incoming = new Comment();
    incoming.setContent("Great post!");
    incoming.setAuthor(UUID.randomUUID());

    final var saved = new Comment();
    when(blogPostRepository.findById(postId)).thenReturn(Optional.of(post));
    when(commentRepository.save(any(Comment.class))).thenReturn(saved);

    final var result = service.saveComment(postId, incoming);

    final ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
    verify(commentRepository).save(captor.capture());

    final var persisted = captor.getValue();
    assertThat(persisted.getBlogPost()).isSameAs(post);
    assertThat(persisted.getContent()).isEqualTo(incoming.getContent());
    assertThat(persisted.getAuthor()).isEqualTo(incoming.getAuthor());
    assertThat(result).isSameAs(saved);
  }

  @Test
  void saveComment_throwsNotFoundException_whenPostDoesNotExist() {
    final var postId = UUID.randomUUID();
    when(blogPostRepository.findById(postId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.saveComment(postId, new Comment()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("BlogPost with id " + postId + " not found");
  }
}
