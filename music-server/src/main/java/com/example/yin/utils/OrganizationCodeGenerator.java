package com.example.yin.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

@Component
public class OrganizationCodeGenerator {
    private static final int MAX_LEN = 64;
    private static final int DEFAULT_SUFFIX_LEN = 5;

    public String generateUniqueCode(String name, Predicate<String> exists) {
        String base = slugify(name);
        if (!StringUtils.hasText(base)) {
            base = "org";
        }
        base = trimBase(base, DEFAULT_SUFFIX_LEN);

        for (int i = 0; i < 20; i++) {
            String suffix = RandomUtils.code().toLowerCase(Locale.ROOT);
            String candidate = trimToMax(base + "-" + suffix);
            if (exists == null || !exists.test(candidate)) {
                return candidate;
            }
        }

        String fallbackSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        return trimToMax(base + "-" + fallbackSuffix);
    }

    static String slugify(String input) {
        if (!StringUtils.hasText(input)) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKD);
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase(Locale.ROOT).trim();
        normalized = normalized.replaceAll("[\\s_]+", "-");
        normalized = normalized.replaceAll("[^a-z0-9-]", "");
        normalized = normalized.replaceAll("-{2,}", "-");
        normalized = normalized.replaceAll("^-|-$", "");
        return normalized;
    }

    private static String trimBase(String base, int suffixLen) {
        int maxBaseLen = MAX_LEN - 1 - suffixLen;
        if (maxBaseLen < 1) {
            maxBaseLen = 1;
        }
        return base.length() <= maxBaseLen ? base : base.substring(0, maxBaseLen);
    }

    private static String trimToMax(String value) {
        return value.length() <= MAX_LEN ? value : value.substring(0, MAX_LEN);
    }
}
