package com.christ.erp.services.transactions.account.settings;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccBillReceiptDBO;
import com.christ.erp.services.dto.account.BillOrReceiptNoDTO;

public class BillOrReceiptNoTransaction {
	
    private static volatile BillOrReceiptNoTransaction  billOrReceiptNumberTransaction=null;
	
	public static BillOrReceiptNoTransaction getInstance() {
        if(billOrReceiptNumberTransaction==null) {
        	billOrReceiptNumberTransaction = new BillOrReceiptNoTransaction();
        }
        return billOrReceiptNumberTransaction;
    }
	
	public List<AccBillReceiptDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AccBillReceiptDBO>>() {
			@Override
	        public List<AccBillReceiptDBO> onRun(EntityManager context) throws Exception {
				String str ="from AccBillReceiptDBO where recordStatus='A'" ;
			    Query qry = context.createQuery(str.toString(),AccBillReceiptDBO.class);
				@SuppressWarnings("unchecked")
				List<AccBillReceiptDBO> authors = qry.getResultList();
	            return authors;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}

	public AccBillReceiptDBO edit(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AccBillReceiptDBO>() {
			@Override
	        public AccBillReceiptDBO onRun(EntityManager context) throws Exception {
        		Query qry=context.createQuery("from AccBillReceiptDBO ab where ab.id=:id and ab.recordStatus='A'");
        		qry.setParameter("id", Integer.parseInt(id));
        		@SuppressWarnings("unchecked")
				AccBillReceiptDBO list=(AccBillReceiptDBO) Utils.getUniqueResult(qry.getResultList());
	            return list;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	            throw error;
	        }
	    });
	}

	public Boolean saveOrUpdate(AccBillReceiptDBO header) throws Exception {
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

	public Boolean delete(AccBillReceiptDBO obj) throws Exception {
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

	public Boolean duplicateCheck(BillOrReceiptNoDTO data, String id) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
            public Boolean onRun(EntityManager context) throws Exception {
				Boolean duplicate = false;
				StringBuffer sb = new StringBuffer();
		        sb.append("SELECT * FROM acc_bill_receipt e where e.acc_financial_year_id=:FinancialYearID and e.type=:Type and e.record_status='A'");
		        if(data.id != null && !data.id.isEmpty()) {
		        	sb.append(" and e.acc_bill_rceipt_id not in (:id) ");
		        }
		        Query q = context.createNativeQuery(sb.toString(), AccBillReceiptDBO.class);
		        q.setParameter("FinancialYearID", data.finanicalyear.id);
		        q.setParameter("Type", data.type.text);
		        if (data.id != null && !data.id.isEmpty()) {
		        	q.setParameter("id", data.id);
		        }
		        AccBillReceiptDBO accBillReceiptDBO = null;
		        accBillReceiptDBO = (AccBillReceiptDBO) Utils.getUniqueResult(q.getResultList());
		        if(accBillReceiptDBO != null) {
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
