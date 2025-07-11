package com.urlshortener.backend.controller;

import com.urlshortener.backend.dto.UrlRequest;
import com.urlshortener.backend.model.Url;
import com.urlshortener.backend.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "API de encurtamento de URLs", description = "Endpoints para criar e redirecionar URLs encurtadas")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @Operation(summary = "Cria uma nova URL encurtada", description = "Recebe uma URL longa e retorna uma versão encurtada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "URL encurtada criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Entrada inválida, por exemplo, formato de URL inválido"),
        @ApiResponse(responseCode = "429", description = "Muitas requisições do mesmo IP")
    })
    @PostMapping("/api/shorten")
    public ResponseEntity<Url> shortenUrl(@Valid @RequestBody UrlRequest urlRequest) {
        Url url = urlService.createUrl(urlRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @Operation(summary = "Redireciona para a URL original", description = "Redireciona para a URL original associada ao código curto fornecido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirecionado com sucesso para a URL original"),
        @ApiResponse(responseCode = "404", description = "Código curto não encontrado ou expirado"),
        @ApiResponse(responseCode = "429", description = "Muitas requisições do mesmo IP")
    })
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        String longUrl = urlService.getUrl(shortUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(longUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
