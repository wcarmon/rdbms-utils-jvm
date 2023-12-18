package io.github.wcarmon.rdbms.paging;

import java.util.Locale;

/** Ascending or descending. */
public enum SortDirection {
    ASC,
    DESC;

    /**
     * lenient parse from a string
     *
     * @param raw a string like "asc"
     * @return SortDirection or throw
     */
    public static SortDirection fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("raw is required");
        }

        final var end = Math.min(4, raw.length());
        final var clean = raw.toLowerCase(Locale.ROOT).strip().substring(0, end);

        return switch (clean) {
            case "asc" -> ASC;
            case "desc" -> DESC;
            default -> throw new IllegalArgumentException("Unknown sort direction '" + raw + "'");
        };
    }

    public boolean isAscending() {
        return ASC.equals(this);
    }

    public boolean isDescending() {
        return DESC.equals(this);
    }

    public String toString() {
        return this.name().toUpperCase(Locale.ROOT);
    }
}
