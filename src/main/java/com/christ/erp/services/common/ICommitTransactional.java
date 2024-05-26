package com.christ.erp.services.common;

import javax.persistence.EntityManager;

public interface ICommitTransactional {
    boolean onRun(EntityManager context) throws Exception;
    void onError(Exception error) throws Exception;
}
