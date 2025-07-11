package com.urlshortener.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.lang.NonNull;

public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int RATE_LIMIT_PER_MINUTE = 20;
    private static final String REDIS_KEY_PREFIX = "rate-limit:";

    public RateLimitFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = getClientIpAddress(request);
        if (ipAddress == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String redisKey = REDIS_KEY_PREFIX + ipAddress;
        Long currentRequests = redisTemplate.opsForValue().increment(redisKey);

        if (currentRequests != null) {
            if (currentRequests == 1) {
                redisTemplate.expire(redisKey, 1, TimeUnit.MINUTES);
            }

            if (currentRequests > RATE_LIMIT_PER_MINUTE) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || xForwardedForHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xForwardedForHeader.split(",")[0].trim();
    }
}
