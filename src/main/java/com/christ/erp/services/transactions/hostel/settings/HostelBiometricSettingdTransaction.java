package com.christ.erp.services.transactions.hostel.settings;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBiometricSettingsDBO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class HostelBiometricSettingdTransaction {

    public static volatile HostelBiometricSettingdTransaction hostelBiometricSettingdTransaction = null;

    public static HostelBiometricSettingdTransaction getInstance(){
        if (hostelBiometricSettingdTransaction == null){
            hostelBiometricSettingdTransaction = new HostelBiometricSettingdTransaction();
        }
        return hostelBiometricSettingdTransaction;
    }

    public List<HostelBiometricSettingsDBO> getGridData()throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelBiometricSettingsDBO>>() {
            @Override
            public List<HostelBiometricSettingsDBO> onRun(EntityManager context) {
                Query query = context.createQuery("select bm from HostelBiometricSettingsDBO bm where bm.recordStatus='A'" +
                        " order by bm.hostelBlockUnitDBO.hostelBlockDBO.hostelDBO.hostelName,bm.hostelBlockUnitDBO.hostelBlockDBO.blockName,bm.hostelBlockUnitDBO.hostelUnit");
                return (List<HostelBiometricSettingsDBO>) query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean delete(String id) throws Exception {
        return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) {
                boolean flag = false;
                HostelBiometricSettingsDBO dbo;
                if(!Utils.isNullOrEmpty(id) && !Utils.isNullOrWhitespace(id)){
                    dbo = context.find(HostelBiometricSettingsDBO.class,Integer.parseInt(id));
                    if(dbo.id != 0){
                        dbo.recordStatus = 'D';
                        context.merge(dbo);
                        flag = true;
                    }
                }
                return flag;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean saveOrUpdate(HostelBiometricSettingsDBO dbo) throws  Exception {
        return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) {
                if(Utils.isNullOrEmpty(dbo.id)){
                    context.persist(dbo);
                }else{
                    context.merge(dbo);
                }
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
}
