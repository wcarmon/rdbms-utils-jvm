package io.github.wcarmon.rdbms.parse;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public final class SQLSplitter {

    private static final Pattern SQL_COMMENT_PATTERN =
            Pattern.compile("--[^\\n]*|/\\*.*?\\*/", Pattern.DOTALL);

    public static String stripTrailingSpaces(@Nullable String raw) {
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
     * @param raw
     * @return
     */
    public static String withoutComments(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        return SQL_COMMENT_PATTERN.matcher(raw).replaceAll("");
    }

    /**
     * @param raw
     * @return
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
