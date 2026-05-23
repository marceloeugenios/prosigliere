package br.com.challenge.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BlogPostDetailDto(
    UUID id,
    String title,
    String content,
    UUID author,
    List<CommentDto> comments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
