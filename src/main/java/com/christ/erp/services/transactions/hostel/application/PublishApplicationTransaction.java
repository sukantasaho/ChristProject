package com.christ.erp.services.transactions.hostel.application;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelPublishApplicationDBO;

public class PublishApplicationTransaction {

    private static volatile PublishApplicationTransaction publishApplicationTransaction = null;

    public static PublishApplicationTransaction getInstance() {
        if(publishApplicationTransaction==null) {
            publishApplicationTransaction = new PublishApplicationTransaction();
        }
        return  publishApplicationTransaction;
    }

    public List<HostelPublishApplicationDBO> getGridData() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelPublishApplicationDBO>>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<HostelPublishApplicationDBO> onRun(EntityManager context) throws Exception {
                String str =" from HostelPublishApplicationDBO where recordStatus='A' order by id" ;
                Query qry = context.createQuery(str, HostelPublishApplicationDBO.class);
                return qry.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean delete(String id, Integer userId) throws Exception {
        return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) {
                boolean flag = false;
                HostelPublishApplicationDBO dbo;
                if(!Utils.isNullOrEmpty(id) && !Utils.isNullOrWhitespace(id)){
                    dbo = context.find(HostelPublishApplicationDBO.class,Integer.parseInt(id));
                    if(dbo.id != 0){
                        dbo.recordStatus = 'D';
                        dbo.modifiedUsersId = userId;
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

    public boolean saveOrUpdate(HostelPublishApplicationDBO dbo)throws Exception {
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
