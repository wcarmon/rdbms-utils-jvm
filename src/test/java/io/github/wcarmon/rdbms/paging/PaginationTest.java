package io.github.wcarmon.rdbms.paging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

class PaginationTest {

    @Test
    void columnMissingPrefix() {
        final var colsString = "aaa,bbb";

        final var p = new HashMap<String, List<String>>();
        p.put("columns", List.of(colsString));
        p.put("limit", List.of("55"));
        p.put("offset", List.of("77"));

        try {
            Pagination.fromQueryParams(p);
            fail("must throw");

        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().contains("must start with"));
        }
    }

    @Test
    void emptyColumns1() {
        final var colsString = "";

        final var p = new HashMap<String, List<String>>();
        p.put("columns", List.of(colsString));
        p.put("limit", List.of("55"));
        p.put("offset", List.of("77"));

        try {
            Pagination.fromQueryParams(p);
            fail("must throw");

        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().contains("column"));
            assertTrue(iae.getMessage().contains("required"));
        }
    }

    @Test
    void emptyColumns2() {
        final var p = new HashMap<String, List<String>>();
        p.put("columns", List.of());
        p.put("limit", List.of("55"));
        p.put("offset", List.of("77"));

        try {
            Pagination.fromQueryParams(p);
            fail("must throw");

        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().contains("at least one"));
            assertTrue(iae.getMessage().contains("column"));
        }
    }

    @Test
    void happyCase() {
        final var colsString = "+aaa,-bBB";

        final var p = new HashMap<String, List<String>>();
        p.put("columns", List.of(colsString));
        p.put("limit", List.of("55"));
        p.put("offset", List.of("77"));

        final var got = Pagination.fromQueryParams(p);
        assertEquals(55, got.limit());
        assertEquals(77, got.offset());

        assertFalse(got.columns().isEmpty());
        assertEquals(2, got.columns().size());

        assertEquals("aaa", got.columns().get(0).name());
        assertEquals(SortDirection.ASC, got.columns().get(0).direction());

        assertEquals("bBB", got.columns().get(1).name());
        assertEquals(SortDirection.DESC, got.columns().get(1).direction());
    }

    @Test
    void missingColumnsQueryParam() {
        final var p = new HashMap<String, List<String>>();
        p.put("limit", List.of("55"));
        p.put("offset", List.of("77"));

        try {
            Pagination.fromQueryParams(p);
            fail("must throw");

        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().contains("at least one"));
        }
    }

    @Test
    void missingLimit() {
        final var colsString = "-ccc, +bBB";

        final var p = new HashMap<String, List<String>>();
        p.put("columns", List.of(colsString));
        p.put("offset", List.of("77"));

        final var got = Pagination.fromQueryParams(p);
        assertEquals(Pagination.DEFAULT_LIMIT, got.limit());
        assertEquals(77, got.offset());

        assertFalse(got.columns().isEmpty());
        assertEquals(2, got.columns().size());

        assertEquals("ccc", got.columns().get(0).name());
        assertEquals(SortDirection.DESC, got.columns().get(0).direction());

        assertEquals("bBB", got.columns().get(1).name());
        assertEquals(SortDirection.ASC, got.columns().get(1).direction());
    }

    @Test
    void missingOffset() {
        final var colsString = "+aaa,-bBB";

        final var p = new HashMap<String, List<String>>();
        p.put("columns", List.of(colsString));
        p.put("limit", List.of("55"));

        final var got = Pagination.fromQueryParams(p);
        assertEquals(55, got.limit());
        assertEquals(Pagination.DEFAULT_OFFSET, got.offset());

        assertFalse(got.columns().isEmpty());
        assertEquals(2, got.columns().size());

        assertEquals("aaa", got.columns().get(0).name());
        assertEquals(SortDirection.ASC, got.columns().get(0).direction());

        assertEquals("bBB", got.columns().get(1).name());
        assertEquals(SortDirection.DESC, got.columns().get(1).direction());
    }

    @Test
    void tooManyColumnQueryParams() {
        // TODO
    }

    @Test
    void tooManyLimits() {
        // TODO
    }

    @Test
    void tooManyOffsets() {
        // TODO
    }
}