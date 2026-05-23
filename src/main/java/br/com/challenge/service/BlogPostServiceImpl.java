package br.com.challenge.service;

import br.com.challenge.domain.BlogPost;
import br.com.challenge.domain.Comment;
import br.com.challenge.exception.NotFoundException;
import br.com.challenge.repository.BlogPostRepository;
import br.com.challenge.repository.CommentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService {

  private static final String BLOG_POST_CACHE_V1 = "blog-post-cache-v1";
  private final BlogPostRepository blogPostRepository;
  private final CommentRepository commentRepository;

  @Override
  @Transactional
  public BlogPost createPost(final BlogPost post) {
    return blogPostRepository.save(post);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BlogPost> findAll(final int page, final int size) {
    return blogPostRepository.findAll(
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = BLOG_POST_CACHE_V1, key = "#id", cacheManager = "redisCacheManagerWith10mTtl")
  public BlogPost findById(final UUID id) {
    return blogPostRepository
        .fetchById(id)
        .orElseThrow(() -> new NotFoundException("BlogPost with id " + id + " not found"));
  }

  @Override
  @Transactional
  @CacheEvict(value = BLOG_POST_CACHE_V1, key = "#postId")
  public Comment saveComment(final UUID postId, final Comment comment) {
    final var post =
        blogPostRepository
            .findById(postId)
            .orElseThrow(() -> new NotFoundException("BlogPost with id " + postId + " not found"));

    final var toSave = new Comment();
    toSave.setBlogPost(post);
    toSave.setContent(comment.getContent());
    toSave.setAuthor(comment.getAuthor());
    return commentRepository.save(toSave);
  }
}
