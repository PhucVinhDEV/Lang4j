package com.example.Chat_Bot_Lang4J.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "intent.classifier")
@Data
public class IntentClassifierConfig {

    /**
     * 📊 Threshold Configuration
     */
    private double baseConfidenceThreshold = 0.72;
    private double highConfidenceThreshold = 0.85;
    private double minConfidenceThreshold = 0.60;

    /**
     * 🎯 Dynamic Threshold Configuration
     */
    private double largeGapThreshold = 0.15; // Gap để reduce threshold
    private double smallGapThreshold = 0.05; // Gap để increase threshold
    private double thresholdAdjustment = 0.05; // Adjustment amount

    /**
     * ⚡ Performance Configuration
     */
    private int maxUserCacheSize = 1000; // Max user embedding cache
    private int maxMessageLength = 500; // Max message length
    private long cacheExpiryMinutes = 60; // Cache expiry time

    /**
     * 🔧 Feature Flags
     */
    private boolean enableDynamicThreshold = true;
    private boolean enableUserCache = true;
    private boolean enableDebugLogging = true;

    /**
     * 📈 Model Configuration
     */
    private int minExamplesPerIntent = 10; // Min examples required
    private double vectorSimilarityThreshold = 0.1; // Min vector similarity

    /**
     * Validation methods
     */
    public boolean isValidThreshold(double threshold) {
        return threshold >= 0.0 && threshold <= 1.0;
    }

    public double getEffectiveThreshold(double dynamicThreshold) {
        if (!enableDynamicThreshold) {
            return baseConfidenceThreshold;
        }

        return Math.max(minConfidenceThreshold,
                Math.min(highConfidenceThreshold, dynamicThreshold));
    }

    public boolean shouldUseCache() {
        return enableUserCache && maxUserCacheSize > 0;
    }

    public boolean isMessageTooLong(String message) {
        return message != null && message.length() > maxMessageLength;
    }

    public String truncateMessage(String message) {
        if (isMessageTooLong(message)) {
            return message.substring(0, maxMessageLength);
        }
        return message;
    }
}