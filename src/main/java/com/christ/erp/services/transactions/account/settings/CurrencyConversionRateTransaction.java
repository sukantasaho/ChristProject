package com.christ.erp.services.transactions.account.settings;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCurrencyConversionRateDBO;
import com.christ.erp.services.dto.common.CurrencyConversionRateDTO;

public class CurrencyConversionRateTransaction {
	
	private static volatile CurrencyConversionRateTransaction  currencyConversionRateTransaction=null;
	
	public static CurrencyConversionRateTransaction getInstance() {
        if(currencyConversionRateTransaction==null) {
        	currencyConversionRateTransaction = new CurrencyConversionRateTransaction();
        }
        return currencyConversionRateTransaction;
    }
	
	public List<ErpCurrencyConversionRateDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCurrencyConversionRateDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
	        public List<ErpCurrencyConversionRateDBO> onRun(EntityManager context) throws Exception {
				String str ="from ErpCurrencyConversionRateDBO where recordStatus='A'" ;
			    Query qry = context.createQuery(str.toString(),ErpCurrencyConversionRateDBO.class);
				List<ErpCurrencyConversionRateDBO> authors = qry.getResultList();
	            return authors;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}

	public ErpCurrencyConversionRateDBO edit(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpCurrencyConversionRateDBO>() {
        	@SuppressWarnings("unchecked")
			@Override
	        public ErpCurrencyConversionRateDBO onRun(EntityManager context) throws Exception {
        		Query qry=context.createQuery("from ErpCurrencyConversionRateDBO ab where ab.id=:id and ab.recordStatus='A'");
        		qry.setParameter("id", Integer.parseInt(id));
        		ErpCurrencyConversionRateDBO list=(ErpCurrencyConversionRateDBO) Utils.getUniqueResult(qry.getResultList());
	            return list;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	            throw error;
	        }
	    });
	}

	public Boolean saveOrUpdate(ErpCurrencyConversionRateDBO header) throws Exception {
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

	public Boolean delete(ErpCurrencyConversionRateDBO obj) throws Exception {
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

	public Boolean duplicateCheck(CurrencyConversionRateDTO data, String id, LocalDateTime dateandtime) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
            @SuppressWarnings("unchecked")
			@Override
            public Boolean onRun(EntityManager context) throws Exception {
				Boolean duplicate = false;
				StringBuffer sb = new StringBuffer();
		        sb.append("SELECT * FROM erp_currency_conversion_rate e where e.erp_currency_id=:CurrencyID and e.exchange_time=:DateandTime and e.record_status='A'");
		        if(data.id != null && !data.id.isEmpty()) {
		        	sb.append(" and e.erp_currency_conversion_rate_id not in (:id) ");
		        }
		        Query q = context.createNativeQuery(sb.toString(), ErpCurrencyConversionRateDBO.class);
		        q.setParameter("CurrencyID", id);
		        q.setParameter("DateandTime", dateandtime);
		        if (data.id != null && !data.id.isEmpty()) {
		        	q.setParameter("id", data.id);
		        }
		        ErpCurrencyConversionRateDBO erpCurrencyConversionRateDBO = null;
		        erpCurrencyConversionRateDBO = (ErpCurrencyConversionRateDBO) Utils.getUniqueResult(q.getResultList());
		        if(erpCurrencyConversionRateDBO != null) {
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
