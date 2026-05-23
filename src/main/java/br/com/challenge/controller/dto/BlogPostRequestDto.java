package br.com.challenge.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record BlogPostRequestDto(
    @Schema(example = "My first blog post") @NotBlank @Size(max = 255) String title,
    @Schema(example = "This is the content of my first blog post.") @NotBlank String content,
    @Schema(example = "01963f5e-4a00-7e8b-a3c2-53f98c7b1234") @NotNull UUID author) {}
