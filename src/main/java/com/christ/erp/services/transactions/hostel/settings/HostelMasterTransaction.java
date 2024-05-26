package com.christ.erp.services.transactions.hostel.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.christ.erp.services.common.*;
import com.christ.erp.services.dbobjects.employee.common.HostelProgrammeDetailsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;

public class HostelMasterTransaction {

    public static volatile HostelMasterTransaction hostelMasterTransaction = null;

    public static HostelMasterTransaction getInstance(){
        if (hostelMasterTransaction == null){
            hostelMasterTransaction = new HostelMasterTransaction();
        }
        return hostelMasterTransaction;
    }

    public HostelDBO edit(String id) {
        try {
            return DBGateway.runJPA(new ISelectGenericTransactional<HostelDBO>() {
                @SuppressWarnings("unchecked")
                @Override
                public HostelDBO onRun(EntityManager context) throws Exception {
                    HostelDBO hm = context.find(HostelDBO.class, Integer.parseInt(id));
                    return hm;
                }
                @Override
                public void onError(Exception error) throws Exception {
                    throw error;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  List<HostelDBO> getGridData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelDBO>>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<HostelDBO> onRun(EntityManager context) throws Exception {
                Query query = context.createQuery("select hm from HostelDBO hm where hm.recordStatus='A'");
                List<HostelDBO> mapping = query.getResultList();
                return mapping;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<HostelDBO> getDuplicate(HostelDTO data) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelDBO>>() {
            @Override
            public List<HostelDBO> onRun(EntityManager context) throws Exception  {
                Query q = context.createQuery("select bo from HostelDBO bo where bo.id=:id and bo.recordStatus='A'");
                q.setParameter("id", Integer.parseInt(data.id));
                return q.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public HostelDBO saveOrUpdate(HostelDBO dbo) throws Exception {
        return DBGateway.runJPA(new ICommitGenericTransactional<HostelDBO>() {
            @Override
            public HostelDBO onRun(EntityManager context) throws Exception{
                if(Utils.isNullOrEmpty(dbo.id)){
                    context.persist(dbo);
                }else{
                    context.merge(dbo);
                }
                return dbo;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean delete(String hostelId) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                boolean flag = false;
                HostelDBO hostel = null;
                if(!Utils.isNullOrEmpty(hostelId) && !Utils.isNullOrWhitespace(hostelId)){
                   hostel = context.find(HostelDBO.class,Integer.parseInt(hostelId));
                   if(hostel.id != 0){
                       hostel.recordStatus = 'D';
                       hostel.getHostelImagesDBOSet().forEach(s-> {
                    	   if(!Utils.isNullOrEmpty(s)) {
                    	 s.setRecordStatus('D');
                    	   }
                       });
                       context.merge(hostel);
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

    public List<HostelProgrammeDetailsDBO> getCampusLevelProgrammeByHostelId(String hostelId)throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelProgrammeDetailsDBO>>() {
            @Override
            public List<HostelProgrammeDetailsDBO> onRun(EntityManager context) throws Exception {
                Query query = context.createQuery("FROM HostelProgrammeDetailsDBO bo WHERE bo.hostelDBO.id =:id AND bo.recordStatus ='A'");
                query.setParameter("id", Integer.parseInt(hostelId));
                return  query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
}
