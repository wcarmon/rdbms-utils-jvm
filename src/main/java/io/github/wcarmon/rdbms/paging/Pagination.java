package io.github.wcarmon.rdbms.paging;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a "page" of results from a query
 *
 * @param offset  from zero
 * @param limit   page size
 * @param columns for sorting rows
 */
public record Pagination(long offset, long limit, List<SortColumn> columns) {

    public static long DEFAULT_LIMIT = 100L;
    public static long DEFAULT_OFFSET = 0;

    @Builder
    public Pagination {
        checkArgument(limit > 0, "limit must be positive");
        checkArgument(offset >= 0, "offset must be non-negative");
        requireNonNull(columns, "columns is required and null.");

        checkArgument(!columns.isEmpty(), "Pagination requires at least one column");
    }

    /**
     * eg. ?limit=10&offset=0&columns=+name,-age
     *
     * @param queryParams
     * @return Pagination or null
     */
    @Nullable
    public static Pagination fromQueryParams(Map<String, List<String>> queryParams) {
        requireNonNull(queryParams, "queryParams is required and null.");

        if (queryParams.isEmpty()) {
            return null;
        }

        // -- case insensitive lookup
        final var m = new HashMap<String, List<String>>(queryParams.size());
        queryParams.forEach((k, v) -> m.put(k.toLowerCase(Locale.ROOT), v));

        return new Pagination(
                parseLongQueryParam(m, "offset", 0),
                parseLongQueryParam(m, "limit", DEFAULT_LIMIT),
                parseSortColumns(m, "columns"));
    }

    private static void checkArgument(boolean expr, String msg) {
        if (!expr) {
            throw new IllegalArgumentException(msg);
        }
    }

    private static long parseLongQueryParam(
            Map<String, List<String>> queryParams, String key, long defaultValue) {

        requireNonNull(queryParams, "queryParams is required and null.");

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key is required");
        }

        final var values = queryParams.get(key);
        if (values == null || values.isEmpty()) {
            return defaultValue;
        }

        checkArgument(values.size() == 1, "exactly one value required for " + key);
        final var s = values.get(0);
        if (s == null || s.isBlank()) {
            return defaultValue;
        }

        final var clean = s.strip().toLowerCase(Locale.ROOT);
        return Long.parseLong(clean);
    }

    private static SortColumn parseSortColumn(String part) {
        requireNonNull(part, "column sort info required");

        final var clean = part.strip();
        checkArgument(
                clean.length() >= 2, "column sort info requires at least 2 characters: " + part);
        checkArgument(
                clean.startsWith("+") || clean.startsWith("-"),
                "part must start with + or -: " + part);

        final var dir = clean.startsWith("+") ? SortDirection.ASC : SortDirection.DESC;

        return new SortColumn(clean.substring(1), dir);
    }

    private static List<SortColumn> parseSortColumns(
            Map<String, List<String>> queryParams, String key) {

        requireNonNull(queryParams, "queryParams is required and null.");
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key is required");
        }

        final var values = queryParams.get("columns");
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        checkArgument(values.size() == 1, "exactly one queryParam required for columns");

        final var s = values.get(0);
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException("sorting column(s) required");
        }

        return Arrays.stream(s.split(","))
                .map(Pagination::parseSortColumn)
                .collect(Collectors.toList());
    }

    public Pagination firstPage() {
        return new Pagination(0, limit, List.copyOf(columns));
    }
}
