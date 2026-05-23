package br.com.challenge.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
    UUID id, String content, UUID author, LocalDateTime createdAt, LocalDateTime updatedAt) {}
