package com.urlshortener.backend.service;

import java.net.MalformedURLException;
import java.net.URI;
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
            URI.create(urlRequest.longUrl()).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL inválida");
        }

        String shortUrl;
        do {
            shortUrl = generateShortUrl();
        } while (Boolean.TRUE.equals(redisTemplate.hasKey(URL_KEY_PREFIX + shortUrl)));

        Url url = new Url(
                shortUrl,
                urlRequest.longUrl(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(urlRequest.expiresAt())
        );
        redisTemplate.opsForValue().set(URL_KEY_PREFIX + shortUrl, url, urlRequest.expiresAt(), TimeUnit.HOURS);
        return url;
    }

    public String getUrl(String shortUrl) {
        Url url = redisTemplate.opsForValue().get(URL_KEY_PREFIX + shortUrl);
        if (url == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL não encontrada ou expirada");
        }
        if (url.getExpiresAt().isBefore(LocalDateTime.now())) {
            redisTemplate.delete(URL_KEY_PREFIX + shortUrl);
            throw new ResponseStatusException(HttpStatus.GONE, "URL expirada");
        }
        return url.getLongUrl();
    }

    private String generateShortUrl() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes()).substring(0, 8);
    }
}
