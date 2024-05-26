package com.christ.erp.services.transactions.account.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dto.account.AccFinancialYearDTO;

public class FinancialYearsTransaction {
	private static volatile FinancialYearsTransaction financialYearsTransaction = null;

	public static FinancialYearsTransaction getInstance() {
		if (financialYearsTransaction == null) {
			financialYearsTransaction = new FinancialYearsTransaction();
		}
		return financialYearsTransaction;
	}

	public Boolean duplicateCheck(AccFinancialYearDTO data, String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
			public Boolean onRun(EntityManager context) throws Exception {
				Boolean duplicate = false;				
				String query = "select * from acc_financial_year a where a.financial_year=:financialYear and a.record_status='A' ";
				Query q = context.createNativeQuery(query, AccFinancialYearDBO.class);
				q.setParameter("financialYear", data.financialYear.text);
				AccFinancialYearDBO accFinancialYearDBO = null;
				accFinancialYearDBO = (AccFinancialYearDBO) Utils.getUniqueResult(q.getResultList());
				if (accFinancialYearDBO != null) {
					duplicate = true;
				} else {
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

	public Boolean saveOrUpdate(AccFinancialYearDBO dbo) throws Exception {
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

	public List<AccFinancialYearDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AccFinancialYearDBO>>() {
			@Override
			public List<AccFinancialYearDBO> onRun(EntityManager context) throws Exception {
				String str = "from AccFinancialYearDBO where recordStatus='A' order by financialYear asc";
				Query qry = context.createQuery(str.toString(), AccFinancialYearDBO.class);
				@SuppressWarnings("unchecked")
				List<AccFinancialYearDBO> dbo = qry.getResultList();
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public AccFinancialYearDBO edit(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AccFinancialYearDBO>() {
			@Override
			public AccFinancialYearDBO onRun(EntityManager context) throws Exception {
				Query qry = context.createQuery("from AccFinancialYearDBO ab where ab.id=:id and ab.recordStatus='A'");
				qry.setParameter("id", Integer.parseInt(id));
				@SuppressWarnings("unchecked")
				AccFinancialYearDBO list = (AccFinancialYearDBO) Utils.getUniqueResult(qry.getResultList());
				return list;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Boolean delete(AccFinancialYearDBO obj) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				context.merge(obj);
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean duplicateCheckCurrentYear(AccFinancialYearDTO data, String userId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
			public Boolean onRun(EntityManager context) throws Exception {
				Boolean duplicate = false;
				String query = " select * from acc_financial_year a where a.is_current_for_fee=1 and a.record_status='A' ";
				Query q = context.createNativeQuery(query, AccFinancialYearDBO.class);
				AccFinancialYearDBO accFinancialYearDBO = null;
				accFinancialYearDBO = (AccFinancialYearDBO) Utils.getUniqueResult(q.getResultList());
				if (data.id == null || data.id.toString().isEmpty()) {
					if (accFinancialYearDBO != null && data.currentYearForFee.equals(true)) {
						duplicate = true;
						data.currentYearForFeeDate=accFinancialYearDBO.financialYear;
					} else {
						duplicate = false;
					}
				} else {
					if (accFinancialYearDBO != null  && accFinancialYearDBO.id != Integer.parseInt(data.id) && accFinancialYearDBO.isCurrentForFee.equals(data.currentYearForFee)) {
						duplicate = true;
						data.currentYearForFeeDate=accFinancialYearDBO.financialYear;
					} else {
						duplicate = false;
					}
				}
				return duplicate;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	
	}

	public boolean duplicateCheckCurrentYearforCashCollection(AccFinancialYearDTO data, String userId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
			public Boolean onRun(EntityManager context) throws Exception {
				Boolean duplicate = false;
				String query = " select * from acc_financial_year a where a.is_current_for_cash_collection=1 and a.record_status='A' ";
				Query q = context.createNativeQuery(query, AccFinancialYearDBO.class);
				AccFinancialYearDBO accFinancialYearDBO = null;
				accFinancialYearDBO = (AccFinancialYearDBO) Utils.getUniqueResult(q.getResultList());
				if (data.id == null || data.id.toString().isEmpty()) {
					if (accFinancialYearDBO != null && data.currentYearForCashCollection.equals(true)) {
						duplicate = true;
						data.currentYearForCashCollectionDate=accFinancialYearDBO.financialYear;
					} else {
						duplicate = false;
					}
				} else {
					if (accFinancialYearDBO != null && accFinancialYearDBO.id != Integer.parseInt(data.id) && accFinancialYearDBO.isCurrentForFee.equals(data.currentYearForCashCollection)) {
						duplicate = true;
						data.currentYearForCashCollectionDate=accFinancialYearDBO.financialYear;
					} else {
						duplicate = false;
					}
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
