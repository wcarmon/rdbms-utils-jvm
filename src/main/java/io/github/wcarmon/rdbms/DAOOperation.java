package io.github.wcarmon.rdbms;

/** All supported DAO operations. */
public enum DAOOperation {

    /** Efficiently insert multiple rows */
    BULK_CREATE,

    /** Insert new row */
    CREATE,

    /** Delete/Remove */
    DELETE,

    /** Retrieve at most one result by some unique row id */
    FIND_BY_ID,

    /** Efficiently return boolean indicating presence or absence of a row */
    HAS,

    /** Retrieve zero or more rows (possibly user driven with filtering) */
    LIST,

    /** Mutate at most one column on one row */
    PATCH,

    /** Mutate at multiple columns on one row */
    UPDATE,

    /** Insert when absent, Update when present */
    UPSERT,
}
