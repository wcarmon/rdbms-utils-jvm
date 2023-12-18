package io.github.wcarmon.rdbms.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class SQLSplitterTest {

    // TODO: handle "\nGO\n" for mssql

    static String normalize(String sql) {
        return StringUtils.removeEnd(sql.strip(), ";");
    }

    @Test
    void splitSQLStatements() {

        final var expectedSize = 8;

        final var want = new ArrayList<String>(16);

        want.add("""
                CREATE TABLE employees (
                    employee_id SERIAL PRIMARY KEY,
                    first_name VARCHAR(50),
                    last_name VARCHAR(50),
                    birth_date DATE,
                    hire_date DATE
                );""");

        want.add("""
                INSERT INTO employees (first_name, last_name, birth_date, hire_date)
                                VALUES ('John', 'Doe', '1990-01-15', '2015-03-20');""");

        want.add("""
                SELECT * FROM employees WHERE birth_date > '1990-01-01';""");

        want.add("""
                                
                                
                                
                UPDATE employees
                SET hire_date = '2020-01-01'
                WHERE employee_id = 1;""");

        want.add("""
                ;;;
                DELETE FROM employees WHERE employee_id = 2;  -- == ** ^^;""");

        want.add("""
                -- ;
                CREATE INDEX idx_birth_date ON employees(birth_date);""");

        want.add("""
                -- zzzzzz
                CREATE VIEW employee_view AS
                SELECT employee_id, first_name, last_name  /* a b c d e */
                FROM employees
                WHERE hire_date > '2019-01-01';""");

        want.add("""
                /* foo bar */
                SELECT employees.first_name, employees.last_name, departments.department_name
                FROM employees
                JOIN departments ON employees.department_id = departments.department_id;""");

        final var raw = String.join("", want) + "\nGO\n";
        assumeTrue(expectedSize == want.size());

        // TODO: a query that has ; in a literal (WHERE col1 like "%;")
        // \;  <--- escaped with slash (negative lookbehind)

        // -- Act
        final var got = new SQLSplitter().splitSQLStatements(raw);

        // -- Assert
        assertEquals(expectedSize, got.size());

        for (int i = 0; i < want.size(); i++) {
            if (i == 4) {
                assertEquals(
                        "DELETE FROM employees WHERE employee_id = 2",
                        got.get(i));
                continue;
            }

            if (i == 5) {
                assertEquals(
                        "CREATE INDEX idx_birth_date ON employees(birth_date)",
                        got.get(i));
                continue;
            }

            if (i == 6) {
                assertEquals("""
                                CREATE VIEW employee_view AS
                                SELECT employee_id, first_name, last_name
                                FROM employees
                                WHERE hire_date > '2019-01-01'""",
                        got.get(i));
                continue;
            }

            if (i == 7) {
                assertEquals("""
                                SELECT employees.first_name, employees.last_name, departments.department_name
                                FROM employees
                                JOIN departments ON employees.department_id = departments.department_id""",
                        got.get(i));
                continue;
            }

            assertTrue(
                    normalize(want.get(i)).startsWith(normalize(got.get(i))),
                    "failed on: i=" + i + "\n\nwant:\n" + want.get(i) + "\n\ngot:\n" + got.get(i));
        }
    }
}