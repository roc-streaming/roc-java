package org.rocstreaming.roctoolkit;

import java.time.Duration;

class Check {
    private Check() {
    }

    static <T> T notNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid " + name + ": must not be null");
        }
        return value;
    }

    static String notEmpty(String value, String name) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Invalid " + name + ": must not be empty");
        }
        return value;
    }

    static Duration notNegative(Duration value, String name) {
        // Null ("unset") duration is fine, it's treated as zero on JNI side.
        if (value != null && value.isNegative()) {
            throw new IllegalArgumentException("Invalid " + name + ": must not be negative");
        }
        return value;
    }

    static int notNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid " + name + ": must not be negative");
        }
        return value;
    }

    static long notNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid " + name + ": must not be negative");
        }
        return value;
    }

    static int inRange(int value, int min_value, int max_value, String name) {
        if (value < min_value || value > max_value) {
            throw new IllegalArgumentException("Invalid " + name + ": must be in range [" + min_value + "; " + max_value + "]");
        }
        return value;
    }

    static long inRange(long value, long min_value, long max_value, String name) {
        if (value < min_value || value > max_value) {
            throw new IllegalArgumentException("Invalid " + name + ": must be in range [" + min_value + "; " + max_value + "]");
        }
        return value;
    }
}
