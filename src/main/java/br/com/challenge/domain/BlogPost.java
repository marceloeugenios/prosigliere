package br.com.challenge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(name = "blog_posts")
public class BlogPost implements Serializable {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(length = 36)
  private UUID id;

  @Setter
  @Column(nullable = false)
  private String title;

  @Setter
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Setter
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(length = 36, nullable = false)
  private UUID author;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp private LocalDateTime updatedAt;

  @Formula("(SELECT COUNT(*) FROM comments c WHERE c.blog_post_id = id)")
  private long commentCount;

  @OneToMany(mappedBy = "blogPost", fetch = FetchType.LAZY)
  @OrderBy("createdAt ASC")
  private List<Comment> comments;
}
