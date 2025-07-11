package com.urlshortener.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.backend.dto.UrlRequest;
import com.urlshortener.backend.model.Url;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class UrlControllerIntegrationTest {

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> rateLimitRedisTemplate;

    @Autowired
    private RedisTemplate<String, Url> urlRedisTemplate;

    @BeforeEach
    void setUp() {
        rateLimitRedisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @Test
    void shouldCreateShortUrlAndRedirect() throws Exception {
        // Given
        UrlRequest urlRequest = new UrlRequest("https://www.google.com", 24);

        // When: Create Short URL
        MvcResult result = mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.longUrl").value(urlRequest.longUrl()))
                .andExpect(jsonPath("$.shortUrl").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Url createdUrl = objectMapper.readValue(responseBody, Url.class);
        String shortUrl = createdUrl.getShortUrl();

        // Then: Verify it was saved in Redis
        Url savedUrl = urlRedisTemplate.opsForValue().get("url:" + shortUrl);
        assertThat(savedUrl).isNotNull();
        assertThat(savedUrl.getLongUrl()).isEqualTo(urlRequest.longUrl());

        // When: Redirect
        mockMvc.perform(get("/" + shortUrl))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", urlRequest.longUrl()));
    }

    @Test
    void shouldReturnNotFoundForNonExistentUrl() throws Exception {
        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidUrl() throws Exception {
        // Given
        UrlRequest urlRequest = new UrlRequest("not-a-valid-url", 24);

        // When/Then
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTooManyRequestsWhenRateLimitIsExceeded() throws Exception {
        // Given
        UrlRequest urlRequest = new UrlRequest("https://www.google.com", 24);
        String content = objectMapper.writeValueAsString(urlRequest);

        // When: Make 20 successful requests
        for (int i = 0; i < 20; i++) {
            mockMvc.perform(post("/api/shorten")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isCreated());
        }

        // Then: The 21st request should be blocked
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isTooManyRequests());
    }
}
