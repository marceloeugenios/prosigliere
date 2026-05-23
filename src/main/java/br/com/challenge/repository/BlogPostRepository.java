package br.com.challenge.repository;

import br.com.challenge.domain.BlogPost;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {

  @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.comments WHERE b.id = :id")
  Optional<BlogPost> fetchById(@Param("id") UUID id);
}
