package com.christ.erp.services.transactions.account.settings;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccGSTPercentageDBO;
import com.christ.erp.services.dto.account.GSTPercentageDTO;

public class GSTPercentagesTransaction {
	
	private static volatile GSTPercentagesTransaction  gstPercentagesTransaction=null;
	
	public static GSTPercentagesTransaction getInstance() {
        if(gstPercentagesTransaction==null) {
        	gstPercentagesTransaction = new GSTPercentagesTransaction();
        }
        return gstPercentagesTransaction;
    }
	
	public List<AccGSTPercentageDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AccGSTPercentageDBO>>() {
			@Override
	        public List<AccGSTPercentageDBO> onRun(EntityManager context) throws Exception {
				String str ="from AccGSTPercentageDBO where recordStatus='A'" ;
			    Query qry = context.createQuery(str.toString(),AccGSTPercentageDBO.class);
				@SuppressWarnings("unchecked")
				List<AccGSTPercentageDBO> authors = qry.getResultList();
	            return authors;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}

	public AccGSTPercentageDBO edit(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AccGSTPercentageDBO>() {
			@Override
	        public AccGSTPercentageDBO onRun(EntityManager context) throws Exception {
        		Query qry=context.createQuery("from AccGSTPercentageDBO ab where ab.id=:id and ab.recordStatus='A'");
        		qry.setParameter("id", Integer.parseInt(id));
        		@SuppressWarnings("unchecked")
        		AccGSTPercentageDBO list=(AccGSTPercentageDBO) Utils.getUniqueResult(qry.getResultList());
	            return list;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	            throw error;
	        }
	    });
	}

	public Boolean saveOrUpdate(AccGSTPercentageDBO header) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	if(Utils.isNullOrEmpty(header.id)) {
            		context.persist(header);
                }else {
                	context.merge(header);
                }
                return  true ;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });
	}

	public Boolean delete(AccGSTPercentageDBO obj) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	context.merge(obj);
                return  true ;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });
	}

	public Boolean duplicateCheck(GSTPercentageDTO data,LocalDateTime date) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
            public Boolean onRun(EntityManager context) throws Exception {
				Boolean duplicate = false;
				StringBuffer sb = new StringBuffer();
		        sb.append("select * from acc_gst_percentage e where e.applicable_from_date=:Date and e.record_status='A'");
		        if(data.id != null && !data.id.isEmpty()) {
		        	sb.append(" and e.acc_gst_percentage_id not in (:id) ");
		        }
		        Query q = context.createNativeQuery(sb.toString(), AccGSTPercentageDBO.class);
		        q.setParameter("Date", date);
		        if (data.id != null && !data.id.isEmpty()) {
		        	q.setParameter("id", data.id);
		        }
		        AccGSTPercentageDBO accGSTPercentageDBO = null;
		        accGSTPercentageDBO = (AccGSTPercentageDBO) Utils.getUniqueResult(q.getResultList());
		        if(accGSTPercentageDBO != null) {
		        	duplicate = true;
		        }else {
		        	duplicate = false;
		        } 
		        return duplicate;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
}
