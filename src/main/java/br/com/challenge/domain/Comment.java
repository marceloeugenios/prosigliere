package br.com.challenge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(name = "comments")
public class Comment implements Serializable {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(length = 36)
  private UUID id;

  @Setter
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Setter
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(length = 36, nullable = false)
  private UUID author;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp private LocalDateTime updatedAt;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blog_post_id", updatable = false, nullable = false)
  private BlogPost blogPost;
}
