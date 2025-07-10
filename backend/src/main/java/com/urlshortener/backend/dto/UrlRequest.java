package com.urlshortener.backend.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public record UrlRequest(
        @URL(message = "URL inválida.")
        @NotBlank(message = "URL não pode ser vazia.")
        String longUrl,
        @Min(value = 1, message = "A expiração deve ser de no mínimo 1 hora.")
        int expiresAt) {

}
