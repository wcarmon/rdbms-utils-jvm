package io.github.wcarmon.rdbms.config;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.Builder;
import org.apache.commons.text.StringSubstitutor;

/**
 * Enough config required to connect to a postgres database
 *
 * @param jdbcUrl  https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html#db_connection_url
 * @param password credential
 * @param user     credential
 */
@Builder
public record PostgresConfig(
        String jdbcUrl,
        String password,
        String user) {

    public static final String KEY_JDBC_URL = "pg.jdbcUrl";
    public static final String KEY_PASSWORD = "pg.password";
    public static final String KEY_USER = "pg.user";

    /** Defaults and Validation */
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

        if (!jdbcUrl.startsWith("jdbc:")) {
            throw new IllegalArgumentException("jdbcUrl must start with 'jdbc:'");
        }

        if (jdbcUrl.contains("${")) {
            final String before = jdbcUrl;

            final StringSubstitutor sub = new StringSubstitutor(System.getenv());
            jdbcUrl = sub.replace(before);
        }
    }

    /**
     * throws when input invalid
     *
     * @param props key-value pairs, see constants above
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
