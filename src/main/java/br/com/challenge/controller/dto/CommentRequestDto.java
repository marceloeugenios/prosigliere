package br.com.challenge.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CommentRequestDto(
    @Schema(example = "Great post, very insightful!") @NotBlank String content,
    @Schema(example = "01963f5e-4a00-7e8b-a3c2-53f98c7b1234") @NotNull UUID author) {}
