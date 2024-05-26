package com.christ.erp.services.common;

import javax.persistence.EntityManager;

public interface ITransactional {
    void onRun(EntityManager context);
    void onError(Exception error);
}
