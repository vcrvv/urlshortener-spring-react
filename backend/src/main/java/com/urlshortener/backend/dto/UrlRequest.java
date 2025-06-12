package com.urlshortener.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlRequest(
        @NotBlank(message = "URL não pode ser vazia.")
        @Pattern(regexp = "^(https?|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "URL inválida.")
        String longUrl,
        @Min(value = 1, message = "A expiração deve ser de no mínimo 1 hora.")
        int expiresAt) {

}
