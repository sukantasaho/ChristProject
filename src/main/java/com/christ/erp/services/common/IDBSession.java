package com.christ.erp.services.common;

public interface IDBSession {
    Object getConnection();
    boolean isOpen();
    void rollback();
}
