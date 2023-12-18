package io.github.wcarmon.rdbms;

/** All supported DAO operations. */
public enum DAOOperation {
    BULK_CREATE,
    CREATE,
    DELETE,
    FIND_BY_ID,
    HAS,
    LIST,
    PATCH,
    UPDATE,
    UPSERT,
}
