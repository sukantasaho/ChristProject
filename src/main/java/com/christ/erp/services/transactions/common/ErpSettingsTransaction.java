package com.christ.erp.services.transactions.common;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.common.ErpSettingsDBO;

import javax.persistence.EntityManager;
import java.util.List;

public class ErpSettingsTransaction {
    private static volatile ErpSettingsTransaction erpSettingsTransaction = null;

    public static ErpSettingsTransaction getInstance() {
        if(erpSettingsTransaction==null) {
            erpSettingsTransaction = new ErpSettingsTransaction();
        }
        return erpSettingsTransaction;
    }

    public List<ErpSettingsDBO> getErpSettingsData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpSettingsDBO>>() {
            @Override
            public List<ErpSettingsDBO> onRun(EntityManager context) throws Exception {
                String str = "from ErpSettingsDBO bo where bo.recordStatus='A'";
                return context.createQuery(str).getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {

            }
        });
    }
}
