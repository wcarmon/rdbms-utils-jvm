/** Module for RDBMS related utilities. */
module io.github.wcarmon.rdbms {
    exports io.github.wcarmon.rdbms;
    exports io.github.wcarmon.rdbms.config;
    exports io.github.wcarmon.rdbms.util;

    requires static lombok;
    requires org.jetbrains.annotations;
    requires java.sql;
}
