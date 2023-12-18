package io.github.wcarmon.rdbms.parse;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

/** Splits a collection (file) of sql statements into single executable statements */
public final class SQLSplitter {

    private static final Pattern SQL_COMMENT_PATTERN =
            Pattern.compile("--[^\\n]*|/\\*.*?\\*/", Pattern.DOTALL);

    private static String stripTrailingSpaces(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        final var lines = raw.split("\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].stripTrailing();
        }

        return String.join("\n", lines);
    }

    /**
     * Removes /* ... * / and -- ... style comments
     *
     * @param raw one or more sql statements
     * @return same as input, but with comments removed
     */
    private static String withoutComments(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        return SQL_COMMENT_PATTERN.matcher(raw).replaceAll("");
    }

    /**
     * Split
     *
     * @param raw one or more sql statements, eg. read from a file
     * @return a collection of executable sql statements, possibly empty, never null
     */
    public List<String> splitSQLStatements(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }

        final var clean = stripTrailingSpaces(withoutComments(raw.strip())).strip();

        return Arrays.stream(clean.split(";"))
                .map(String::strip)
                .filter(stmt -> !"GO".equalsIgnoreCase(stmt)) // mssql
                .filter(statement -> !statement.isBlank())
                .collect(Collectors.toList());
    }
}
