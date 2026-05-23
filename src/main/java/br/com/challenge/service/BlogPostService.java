package br.com.challenge.service;

import br.com.challenge.domain.BlogPost;
import br.com.challenge.domain.Comment;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface BlogPostService {
  BlogPost createPost(BlogPost post);

  Page<BlogPost> findAll(int page, int size);

  BlogPost findById(UUID id);

  Comment saveComment(UUID postId, Comment comment);
}
