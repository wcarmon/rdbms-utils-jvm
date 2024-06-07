package io.github.wcarmon.rdbms.config;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.Builder;

@Builder
public record PostgresConfig(
        String jdbcUrl,
        String password,
        String user) {

    public static final String KEY_JDBC_URL = "pg.jdbcUrl";
    public static final String KEY_PASSWORD = "pg.password";
    public static final String KEY_USER = "pg.user";

    public PostgresConfig {
        jdbcUrl = (jdbcUrl == null || jdbcUrl.isBlank() ? "" : jdbcUrl).strip();
        user = (user == null || user.isBlank() ? "" : user).strip();

        // TODO: .strip() here if safe for passwords
        password = (password == null || password.isBlank() ? "" : password);

        if (jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("jdbcUrl is required");
        }
        if (user.isBlank()) {
            throw new IllegalArgumentException("user is required");
        }
    }

    /**
     * throws when input invalid
     *
     * @param props
     * @return new PostgresConfig
     */
    public static PostgresConfig fromProperties(Map<String, Object> props) {
        requireNonNull(props, "props is required and null.");

        Object value;
        String key;

        key = KEY_JDBC_URL;
        value = props.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("missing required property: " + key);
        }
        final String jdbcUrl = value.toString().strip();


        key = KEY_USER;
        value = props.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("missing required property: " + key);
        }
        final String user = value.toString().strip();


        key = KEY_PASSWORD;
        value = props.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("missing required property: " + key);
        }
        // TODO: .strip() here if safe for passwords
        final String password = value.toString();

        return builder()
                .jdbcUrl(jdbcUrl)
                .password(password)
                .user(user)
                .build();
    }

    /**
     * throws when input invalid
     *
     * @param properties key-value pairs, see constants above
     * @return new PostgresConfig
     */
    public static PostgresConfig fromProperties(Properties properties) {
        requireNonNull(properties, "properties is required and null.");

        final Map<String, Object> m = new HashMap<>(properties.size());
        for (final var entry : properties.entrySet()) {
            // -- Assumption: java.util.Properties keys are always Strings
            m.put((String) entry.getKey(), entry.getValue());
        }

        return fromProperties(m);
    }
}
