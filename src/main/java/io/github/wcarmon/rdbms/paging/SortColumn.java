package io.github.wcarmon.rdbms.paging;

import static java.util.Objects.requireNonNull;

/**
 * A column used for sorting query results
 *
 * @param name
 * @param direction
 */
public record SortColumn(
        /* snake_case */
        String name, SortDirection direction) {

    public SortColumn {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        requireNonNull(direction, "direction is required and null.");
    }

    public static SortColumn of(String name) {
        return new SortColumn(name, SortDirection.ASC);
    }

    public String toString() {
        return name + " " + direction;
    }
}
