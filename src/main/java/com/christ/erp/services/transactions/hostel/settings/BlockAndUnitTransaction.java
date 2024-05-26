package com.christ.erp.services.transactions.hostel.settings;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockUnitDTO;

public class BlockAndUnitTransaction {
	
	private static volatile BlockAndUnitTransaction blockAndUnitTransaction = null;

    public static BlockAndUnitTransaction getInstance() {
        if(blockAndUnitTransaction==null) {
        	blockAndUnitTransaction = new BlockAndUnitTransaction();
        }
        return  blockAndUnitTransaction;
    }
    
    public List<HostelBlockUnitDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelBlockUnitDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
	        public List<HostelBlockUnitDBO> onRun(EntityManager context) throws Exception {
				String str ="from HostelBlockUnitDBO where recordStatus='A' order by id" ;
			    Query qry = context.createQuery(str,HostelBlockUnitDBO.class);
				return qry.getResultList();
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}
    
    public HostelBlockUnitDBO getHostelBlockUnitDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelBlockUnitDBO>() {
			@Override
			public HostelBlockUnitDBO onRun(EntityManager context) throws Exception {
				return context.find(HostelBlockUnitDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public boolean saveOrUpdate(HostelBlockUnitDBO hostelBlockUnitDBO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if(Utils.isNullOrEmpty(hostelBlockUnitDBO) || Utils.isNullOrEmpty(hostelBlockUnitDBO.id) || hostelBlockUnitDBO.id==0) {
                    context.persist(hostelBlockUnitDBO);
                }
                else {
                    context.merge(hostelBlockUnitDBO);
                } 
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
    
    public Boolean isDuplicate(HostelBlockUnitDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@Override
	        public Boolean onRun(EntityManager context) throws Exception {
				String str ="from HostelBlockUnitDBO where recordStatus='A' and hostelUnit=:unit  and hostelBlockDBO.id=:blockId" ;
				if(data.id != null && !data.id.isEmpty()) {
					str += " and id!=:id ";
				}
			    Query qry = context.createQuery(str,HostelBlockUnitDBO.class);
			    qry.setParameter("unit", data.hostelUnit.trim());
			    qry.setParameter("blockId", Integer.parseInt(data.block.value));
			    if(data.id != null && !data.id.isEmpty()) {
				    qry.setParameter("id", Integer.parseInt(data.id));
				}
			    if(qry.getResultList().size()>0) {
			    	return true;
			    }
		        return false;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}
}
