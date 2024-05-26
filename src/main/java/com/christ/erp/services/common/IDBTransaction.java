package com.christ.erp.services.common;

public interface IDBTransaction<T> {
    T onExecute(IDBSession session);
}
