package com.fruits.ecommerce.services.Utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 3;
    private static final int ATTEMPT_INCREMENT = 1;
    private final LoadingCache<Long, Integer> loginAttemptCache;

    /**
     * Constructs a LoginAttemptService with a configured cache.
     * The cache is currently set to expire entries after 2 minutes and hold a maximum of 100 entries.
     * Note: The expiration time can be adjusted by modifying the expireAfterWrite() parameter.
     * Available time units: DAYS, HOURS, MINUTES, SECONDS (uncomment the desired option).
     */
    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                // Current setting: Entries expire after 2 minutes.
                // We Can Change it into another time period
                .expireAfterWrite(2, TimeUnit.MINUTES)
                // Set maximum cache size
                .maximumSize(100)
                .build(new CacheLoader<>() {
                    @Override
                    @Nonnull
                    public Integer load(@Nonnull Long key) {
                        // Default value for a new key
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(Long userId) {
        loginAttemptCache.invalidate(userId);
    }

    public void addUserToLoginAttemptCache(Long userId) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(userId);
        } catch (ExecutionException e) {
            log.error("Error while getting login attempts for user ID: {}", userId, e);
        }
        loginAttemptCache.put(userId, attempts);
    }

    public boolean hasExceededMaxAttempts(Long userId) {
        try {
            return loginAttemptCache.get(userId) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            log.error("Error while checking login attempts for user ID: {}", userId, e);
        }
        return false;
    }
}


