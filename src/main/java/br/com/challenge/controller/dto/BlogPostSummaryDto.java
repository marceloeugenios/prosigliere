package br.com.challenge.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BlogPostSummaryDto(
    UUID id,
    String title,
    UUID author,
    Long commentCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
