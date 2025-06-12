package com.urlshortener.backend.service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.urlshortener.backend.dto.UrlRequest;
import com.urlshortener.backend.model.Url;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private RedisTemplate<String, Url> redisTemplate;

    @Mock
    private ValueOperations<String, Url> valueOperations;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Deve criar uma URL curta com sucesso")
    void deveCriarUrlCurtaComSucesso() {
        UrlRequest urlRequest = new UrlRequest("https://www.example.com", 10);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        Url result = urlService.createUrl(urlRequest);

        assertNotNull(result);
        assertEquals(urlRequest.longUrl(), result.getLongUrl());
        assertNotNull(result.getShortUrl());
        verify(valueOperations, times(1)).set(anyString(), any(Url.class));
        verify(redisTemplate, times(1)).expire(anyString(), eq(10L), eq(TimeUnit.HOURS));
    }

    @Test
    @DisplayName("Deve lançar exceção para formato de URL inválido")
    void deveLancarExcecaoParaFormatoDeUrlInvalido() {
        UrlRequest urlRequest = new UrlRequest("invalid-url", 10);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> urlService.createUrl(urlRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("URL deve começar com http ou https", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção para URL com caracteres inválidos")
    void deveLancarExcecaoParaUrlComCaracteresInvalidos() {
        UrlRequest urlRequest = new UrlRequest("http://inválido com espaço", 10);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> urlService.createUrl(urlRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Formato de URL inválido", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar exceção para URL que não começa com http ou https")
    void deveLancarExcecaoParaUrlSemHttpOuHttps() {
        UrlRequest urlRequest = new UrlRequest("blabla://www.example.com", 10);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> urlService.createUrl(urlRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("URL deve começar com http ou https", exception.getReason());
    }

    @Test
    @DisplayName("Deve recuperar URL com sucesso")
    void deveRecuperarUrlComSucesso() {
        String shortUrl = "testShort";
        Url expectedUrl = new Url(shortUrl, "https://www.example.com", LocalDateTime.now(), LocalDateTime.now().plusHours(10));
        when(valueOperations.get("url:" + shortUrl)).thenReturn(expectedUrl);

        Url result = urlService.getUrl(shortUrl);

        assertNotNull(result);
        assertEquals(expectedUrl, result);
    }

    @Test
    @DisplayName("Deve lançar exceção NOT_FOUND para URL inexistente")
    void deveLancarExcecaoNotFoundParaUrlInexistente() {
        String shortUrl = "nonExistent";
        when(valueOperations.get("url:" + shortUrl)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> urlService.getUrl(shortUrl));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("URL não encontrada", exception.getReason());
    }
}
