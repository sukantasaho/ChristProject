package com.christ.erp.services.transactions.account.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;

public class AccountEntryTransaction {

	private static volatile AccountEntryTransaction  accountEntryTransaction=null;
	public static AccountEntryTransaction getInstance() {
        if(accountEntryTransaction==null) {
        	accountEntryTransaction = new AccountEntryTransaction();
        }
        return accountEntryTransaction;
    }
	
	public List<AccAccountsDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AccAccountsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
	        public List<AccAccountsDBO> onRun(EntityManager context) throws Exception {
				String str ="from AccAccountsDBO where recordStatus='A'" ;
			    Query qry = context.createQuery(str.toString(),AccAccountsDBO.class);
				List<AccAccountsDBO> accAccountsDBOs = qry.getResultList();
	            return accAccountsDBOs;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}
	
	public AccAccountsDBO editAccountEntry(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AccAccountsDBO>() {
			@Override
			public AccAccountsDBO onRun(EntityManager context) throws Exception {
				return context.find(AccAccountsDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	public Boolean isDuplicate(String accountNo, String id) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@SuppressWarnings("unchecked")
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String str = "from AccAccountsDBO bo where bo.recordStatus='A' and bo.accountNo=:accountNo";
					if (!Utils.isNullOrEmpty(id)) {
						str += " and id!=:id";
					}
					Query query = context.createQuery(str);
					if (!Utils.isNullOrEmpty(id)) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("accountNo", accountNo);
					List<AccAccountsDBO> accAccountsDBOs = query.getResultList();
					return Utils.isNullOrEmpty(accAccountsDBOs) ? false : true;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean saveOrUpdate(AccAccountsDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (Utils.isNullOrEmpty(dbo.id)) {
					context.persist(dbo);
				} else {
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
	
	public boolean delete(String id, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	AccAccountsDBO dbo = null;
             	if (id != null && !id.isEmpty()) {
					if (Utils.isNullOrWhitespace(id) == false) {
						 dbo  = context.find(AccAccountsDBO.class, Integer.parseInt(id));
						if (!Utils.isNullOrEmpty(dbo)) {
							dbo.recordStatus = 'D';
							dbo.modifiedUsersId = Integer.parseInt(userId);	
							context.merge(dbo);
						}
					}
				}
                return  true ;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	     });
	}
	
	public AccAccountsDBO edit(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AccAccountsDBO>() {
			@Override
			public AccAccountsDBO onRun(EntityManager context) throws Exception {
				AccAccountsDBO dbo =null;
				try {
				String str = " from AccAccountsDBO P where P.recordStatus='A' and P.id=:Id ";
				Query query = context.createQuery(str);
				query.setParameter("Id", id);
				dbo = (AccAccountsDBO) Utils.getUniqueResult(query.getResultList());
				}catch (Exception e) {}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
