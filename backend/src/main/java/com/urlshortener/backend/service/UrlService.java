package com.urlshortener.backend.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.urlshortener.backend.dto.UrlRequest;
import com.urlshortener.backend.model.Url;

@Service
public class UrlService {

    private final RedisTemplate<String, Url> redisTemplate;
    private static final String URL_KEY_PREFIX = "url:";

    public UrlService(RedisTemplate<String, Url> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Url createUrl(UrlRequest urlRequest) {
        try {
            new URI(urlRequest.longUrl());
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL inválida");
        }
        String shortUrl = generateShortUrl();
        Url url = new Url(
                shortUrl,
                urlRequest.longUrl(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(urlRequest.expiresAt())
        );
        redisTemplate.opsForValue().set(URL_KEY_PREFIX + shortUrl, url, urlRequest.expiresAt(), TimeUnit.HOURS);
        return url;
    }

    public Url getUrl(String shortUrl) {
        Url url = redisTemplate.opsForValue().get(URL_KEY_PREFIX + shortUrl);
        if (url == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL não encontrada ou expirada");
        }
        if (url.getExpiresAt().isBefore(LocalDateTime.now())) {
            redisTemplate.delete(URL_KEY_PREFIX + shortUrl);
            throw new ResponseStatusException(HttpStatus.GONE, "URL expirada");
        }
        return url;
    }

    private String generateShortUrl() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes()).substring(0, 8);
    }
}
