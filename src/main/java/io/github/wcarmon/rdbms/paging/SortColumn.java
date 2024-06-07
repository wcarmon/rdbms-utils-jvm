package io.github.wcarmon.rdbms.paging;

import static java.util.Objects.requireNonNull;

/**
 * A column used for sorting query results
 *
 * @param name      snake_case
 * @param direction see docs
 */
public record SortColumn(String name, SortDirection direction) {

    /** Create new instance */
    public SortColumn {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        requireNonNull(direction, "direction is required and null.");
    }

    /**
     * Build a new SortColumn with passed name and direction.
     *
     * @param name of the column in snake_case
     * @return an ascending sort column with passed name
     */
    public static SortColumn of(String name) {
        return new SortColumn(name, SortDirection.ASC);
    }

    public String toString() {
        return name + " " + direction;
    }
}
