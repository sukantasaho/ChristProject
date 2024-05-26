package com.christ.erp.services.transactions.account.fee;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.FeeCashCollectionDBO;
import com.christ.erp.services.dbobjects.account.settings.AccBillReceiptDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.account.settings.AccGSTPercentageDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;

public class CashCollectionTransaction {
	private static volatile CashCollectionTransaction cashCollectionTransaction = null;

	public static CashCollectionTransaction getInstance() {
		if (cashCollectionTransaction == null) {
			cashCollectionTransaction = new CashCollectionTransaction();
		}
		return cashCollectionTransaction;
	}	
	
	public Boolean saveOrUpdateFeeCashCollection(FeeCashCollectionDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
			@Override
			public Boolean onRun(EntityManager context) throws Exception {
				if(Utils.isNullOrEmpty(dbo.id)) {
					Integer receiptNo = null;
					Query query = context.createQuery("select dbo from AccBillReceiptDBO dbo where dbo.recordStatus='A' and dbo.type='Cash Collection' and dbo.accFinancialYearDBO.id=:financialYearId");
					query.setParameter("financialYearId", dbo.accFinancialYearDBO.id);
					query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
					AccBillReceiptDBO accBillReceiptDBO = (AccBillReceiptDBO) Utils.getUniqueResult(query.getResultList());		
					if(Utils.isNullOrEmpty(accBillReceiptDBO) || (!Utils.isNullOrEmpty(accBillReceiptDBO) && Utils.isNullOrEmpty(accBillReceiptDBO.billReceiptCurrentNo))) {
						return null;
					}
					receiptNo = accBillReceiptDBO.billReceiptCurrentNo;
					//accBillReceiptDBO.modifiedUsersId = userId;
					//accBillReceiptDBO.recordStatus = 'A';
					dbo.receiptNo = receiptNo;
					accBillReceiptDBO.billReceiptCurrentNo = receiptNo + 1;
					context.merge(accBillReceiptDBO);
					context.persist(dbo);
				}
				else {
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

	
	public List<Tuple> getGridData(String registerNo,String financialYearId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String Query = "select fee_cash_collection.fee_cash_collection_id as id,student.student_id as studentId," + 
						" student.student_name as studentName,fee_cash_collection.cash_collection_date_time as dateandtime," + 
						" fee_cash_collection.total_amount as totalAmount,fee_cash_collection.receipt_no as receiptNo" + 
						" from fee_cash_collection" + 
						" inner join student on fee_cash_collection.student_id = student.student_id" + 
						" where student.register_no =:registerNo and" + 
						" fee_cash_collection.acc_financial_year_id =:financialYearId" + 
						" and fee_cash_collection.record_status = 'A'";
				Query query = context.createNativeQuery(Query, Tuple.class);				
				query.setParameter("registerNo", registerNo);
				query.setParameter("financialYearId", financialYearId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	
	public ErpAcademicYearDBO getCurrentAcademicYear() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <ErpAcademicYearDBO>() {
			@Override
			public ErpAcademicYearDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpAcademicYearDBO bo   where  bo.recordStatus='A' and bo.isCurrentAcademicYear=1");
				ErpAcademicYearDBO dbo = new ErpAcademicYearDBO();
				dbo = (ErpAcademicYearDBO) Utils.getUniqueResult(query.getResultList());
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public AccFinancialYearDBO getCurrentFinancialYear() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <AccFinancialYearDBO>() {
			@Override
			public AccFinancialYearDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from AccFinancialYearDBO bo   where  bo.recordStatus='A' and bo.isCurrentForCashCollection=1");
				AccFinancialYearDBO dbo = new AccFinancialYearDBO();
				dbo = (AccFinancialYearDBO) Utils.getUniqueResult(query.getResultList());
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public StudentDBO getApplicantDetails(String registerNo) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <StudentDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public StudentDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from StudentDBO bo   where  bo.recordStatus='A' and bo.registerNo=:registerNo");
				query.setParameter("registerNo", registerNo);
				StudentDBO dbo = new StudentDBO();
				dbo = (StudentDBO) Utils.getUniqueResult(query.getResultList());
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public FeeCashCollectionDBO getDetailsByReceiptNo(String receiptNo,String finanacialYearId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <FeeCashCollectionDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public FeeCashCollectionDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from FeeCashCollectionDBO bo where bo.receiptNo=:receiptNo and bo.accFinancialYearDBO.id=:financialId");
				FeeCashCollectionDBO dbo = new FeeCashCollectionDBO();		
				query.setParameter("receiptNo", Integer.parseInt(receiptNo));
				query.setParameter("financialId", Integer.parseInt(finanacialYearId));				
					dbo = (FeeCashCollectionDBO) Utils.getUniqueResult(query.getResultList());								
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getFeeHeadByCampusId(String campusId,String financialYearId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String Query = "select distinct acc_fee_heads.acc_fee_heads_id as ID,acc_fee_heads.heading as Text from acc_fee_heads" + 
						" inner join acc_fee_heads_account on acc_fee_heads_account.acc_fee_heads_id = acc_fee_heads.acc_fee_heads_id" + 
						" where acc_fee_heads.fee_heads_type = 'Cash Collection' and acc_fee_heads_account.erp_campus_id =:campusId and acc_fee_heads.record_status = 'A' "
						//+ " and acc_fee_heads_account.acc_financial_year_id = :financialYearId" + 
						+" and acc_fee_heads_account.record_status = 'A'";
				Query query = context.createNativeQuery(Query, Tuple.class);				
				query.setParameter("campusId", campusId);
				//query.setParameter("financialYearId", financialYearId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public AccFeeHeadsDBO getFeeHeadDetails(String feeHeadId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <AccFeeHeadsDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public AccFeeHeadsDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from AccFeeHeadsDBO bo where  bo.recordStatus='A' and bo.id=:feeHeadId");
				AccFeeHeadsDBO dbo = new AccFeeHeadsDBO();		
				query.setParameter("feeHeadId", Integer.parseInt(feeHeadId));
				dbo = (AccFeeHeadsDBO) Utils.getUniqueResult(query.getResultList());			
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}	

	
	public AccGSTPercentageDBO getCurrentGstDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <AccGSTPercentageDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public AccGSTPercentageDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from AccGSTPercentageDBO bo where  bo.recordStatus='A' and bo.isCurrent=1");
				AccGSTPercentageDBO dbo = new AccGSTPercentageDBO();
				dbo = (AccGSTPercentageDBO) Utils.getUniqueResult(query.getResultList());
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}	
	
	public synchronized Integer getReceiptNumber(Integer userId,Integer financialYearId) throws Exception{
    	return DBGateway.runJPA(new ICommitGenericTransactional<Integer>() {
			@Override
			public Integer onRun(EntityManager context) throws Exception {
				Integer applnNo = null;
				try {
					Query query = context.createQuery("select dbo from AccBillReceiptDBO dbo where dbo.recordStatus='A' and dbo.type='Cash Collection' and dbo.accFinancialYearDBO.id=:financialYearId");
					query.setParameter("financialYearId", financialYearId);
					AccBillReceiptDBO accBillReceiptDBO = (AccBillReceiptDBO) Utils.getUniqueResult(query.getResultList());				
					if(Utils.isNullOrEmpty(accBillReceiptDBO) || (!Utils.isNullOrEmpty(accBillReceiptDBO) && Utils.isNullOrEmpty(accBillReceiptDBO.billReceiptCurrentNo))) {
						return null;
					}
					applnNo = accBillReceiptDBO.billReceiptCurrentNo;
					accBillReceiptDBO.modifiedUsersId = userId;
					accBillReceiptDBO.recordStatus = 'A';
					accBillReceiptDBO.billReceiptCurrentNo = applnNo + 1;
					context.merge(accBillReceiptDBO);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return !Utils.isNullOrEmpty(applnNo) ? applnNo : null;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getAccName(int feeHeadsAccId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_Query = "select acc_fee_heads_account.acc_accounts_id as accId, acc_accounts.account_name as accName" + 
						" from acc_fee_heads_account inner join" + 
						" acc_accounts on acc_accounts.acc_accounts_id = acc_fee_heads_account.acc_accounts_id where" + 
						" acc_fee_heads_account.acc_fee_heads_account_id =:id";
				Query query = context.createNativeQuery(SELECT_Query, Tuple.class);				
				query.setParameter("id", feeHeadsAccId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
