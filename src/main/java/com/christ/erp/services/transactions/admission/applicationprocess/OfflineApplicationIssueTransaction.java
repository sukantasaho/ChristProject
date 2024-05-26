package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmOfflineApplicationIssueDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ErpNumberGenerationDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ErpReceiptsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmApplnNumberGenDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationIssueDTO;

@Repository
public class OfflineApplicationIssueTransaction {
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	/*
	private static volatile OfflineApplicationIssueTransaction offlineApplicationIssueTransaction = null;

    public static OfflineApplicationIssueTransaction getInstance() {
        if(offlineApplicationIssueTransaction==null) {
        	offlineApplicationIssueTransaction = new OfflineApplicationIssueTransaction();
        }
        return  offlineApplicationIssueTransaction;
    }*/
    
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
    
    public AccFinancialYearDBO getFinancialYear() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AccFinancialYearDBO>() {
			@Override
			public AccFinancialYearDBO onRun(EntityManager context) throws Exception {
				AccFinancialYearDBO dbo = null;
				try{
					 Query query = context.createQuery(" from AccFinancialYearDBO accFinancialYear where "+
							 						   "accFinancialYear.recordStatus = 'A' and accFinancialYear.isCurrentForFee = 1 "+
							                           "and curdate() between accFinancialYear.financialYearStartDate and accFinancialYear.financialYearEndDate  ");
					 dbo = (AccFinancialYearDBO) Utils.getUniqueResult(query.getResultList());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
    
    public ErpNumberGenerationDBO getErpNumberGeneration(Integer accFinalcialYearId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpNumberGenerationDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public ErpNumberGenerationDBO onRun(EntityManager context) throws Exception {
				ErpNumberGenerationDBO erpNumberGenerationDBO = null;
				try{
					Query query = context.createQuery("select ar from ErpNumberGenerationDBO ar where ar.accFinancialYearDBO.recordStatus='A' "+
	 						  "and ar.accFinancialYearDBO.isCurrentForFee=1 and ar.accFinancialYearDBO.id=:pAccFinalcialYearId "+
							  "and ar.recordStatus='A' and numberType=:pNumberType");
					query.setParameter("pAccFinalcialYearId", accFinalcialYearId);
					query.setParameter("pNumberType", "OFFLINE_APPLICATION_ISSUE");
					erpNumberGenerationDBO = (ErpNumberGenerationDBO)Utils.getUniqueResult(query.getResultList());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return erpNumberGenerationDBO;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
    
    /* method is not using and one column from the related table removed 
    public List<Tuple> getAccountHead(String offlineApplnNoPrefix,String offlineApplnNo) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_UNIT = "select acc_fee_heads.acc_fee_heads_id as ID ,acc_fee_heads.fee_heads_type as Text FROM acc_fee_heads " +
									"inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = acc_fee_heads.erp_programme_degree_id " +  // need to remove
									"inner join erp_programme on erp_programme.erp_programme_degree_id = erp_programme_degree.erp_programme_degree_id " +
									"inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id " +
									"inner join adm_appln_number_gen_details on adm_appln_number_gen_details.erp_campus_programme_mapping_id = erp_campus_programme_mapping.erp_campus_programme_mapping_id " +
									"inner join adm_appln_number_generation ON adm_appln_number_generation.adm_appln_number_generation_id = adm_appln_number_gen_details.adm_appln_number_generation_id " +
									"where acc_fee_heads.record_status='A' and " +
									"adm_appln_number_generation.record_status='A' and " +
									"adm_appln_number_gen_details.record_status='A' and " +
									"erp_programme.record_status='A' and " +
									"erp_programme_degree.record_status='A' and " +
									"adm_appln_number_generation.offline_appln_no_prefix=:prefixCode and " +
									"(:offlineApplnNo >= adm_appln_number_generation.offline_appln_no_from AND :offlineApplnNo <= adm_appln_number_generation.offline_appln_no_to) ";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				query.setParameter("prefixCode", offlineApplnNoPrefix);
				query.setParameter("offlineApplnNo", offlineApplnNo);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	*/
    
    public boolean saveOrUpdate(Integer accFinalcialYearId,ErpReceiptsDBO erpReceiptsDBO,AdmOfflineApplicationIssueDBO admOfflineApplicationIssueDBO,OfflineApplicationIssueDTO offlineApplicationIssueDTO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	Integer receiptNumber=0;
            	boolean isSaveOrUpdate = false;
            	if(erpReceiptsDBO.id==0 && admOfflineApplicationIssueDBO.id==0) {
	            	Query query = context.createQuery("select ar from ErpNumberGenerationDBO ar where ar.accFinancialYearDBO.recordStatus='A' "+
		 						  "and ar.accFinancialYearDBO.isCurrentForFee=1 and ar.accFinancialYearDBO.id=:pAccFinalcialYearId "+
								  "and ar.recordStatus='A' and numberType=:pNumberType");
	          		query.setParameter("pAccFinalcialYearId", accFinalcialYearId);
	          		query.setParameter("pNumberType", "OFFLINE_APPLICATION_ISSUE");
	          		query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
	          		ErpNumberGenerationDBO erpNumberGenerationDBO = (ErpNumberGenerationDBO) Utils.getUniqueResult(query.getResultList());
	          		receiptNumber=erpNumberGenerationDBO.currentNumber;
	          		receiptNumber=receiptNumber+1;
	          		erpNumberGenerationDBO.currentNumber = receiptNumber;
	          		context.merge(erpNumberGenerationDBO);
	          		if(receiptNumber>0){
	          			erpReceiptsDBO.receiptNo = receiptNumber.toString();
	          			context.persist(erpReceiptsDBO);
	          			admOfflineApplicationIssueDBO.erpReceiptsDBO = erpReceiptsDBO;
	          			context.persist(admOfflineApplicationIssueDBO);
	          			offlineApplicationIssueDTO.receiptNumber = erpNumberGenerationDBO.numberPrefix+receiptNumber.toString();
	          			isSaveOrUpdate =true;
	          		}
            	}else {
            		context.merge(erpReceiptsDBO);
            		context.merge(admOfflineApplicationIssueDBO);
            		isSaveOrUpdate =true;
                }
                return isSaveOrUpdate;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
    
    public AdmOfflineApplicationIssueDBO getAdmOfflineApplicationIssueDBO(String applnNo, String academicYearID) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<AdmOfflineApplicationIssueDBO>() {
        	@SuppressWarnings("unchecked")
			@Override
			public AdmOfflineApplicationIssueDBO onRun(EntityManager context) throws Exception {
        		AdmOfflineApplicationIssueDBO applnEntriesDBO = null;
				Query query = context.createQuery("select ar from AdmOfflineApplicationIssueDBO ar where ar.erpAcademicYearDBO.recordStatus='A' "+
						  "and ar.erpAcademicYearDBO.id=:pAcademicYearID "+
						  "and ar.recordStatus='A' and ar.applnNo=:pApplnNo");
				query.setParameter("pAcademicYearID", Integer.parseInt(academicYearID));
				query.setParameter("pApplnNo", applnNo);
				applnEntriesDBO = (AdmOfflineApplicationIssueDBO)Utils.getUniqueResult(query.getResultList());
				return  applnEntriesDBO;
			}
        	@Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
		});
    }
    
    public List<Tuple> getGridData(String applicationNumber,String academicYearId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String Query = "select adm_offline_application_issue.adm_offline_application_issue_id as admOfflineApplnIssueId," +
						" erp_receipts.receipt_no as receiptNo,erp_receipts.receipts_date as receiptsDate,"+
						" erp_receipts.receipts_amount as receiptsAmount,"+
						" adm_offline_application_issue.applicant_name as applicantName,"+
						" erp_number_generation.number_prefix as numberPrefix,"+
						" acc_financial_year.acc_financial_year_id as financialYearId,"+
						" acc_financial_year.financial_year as financialYear"+
						" from adm_offline_application_issue" + 
						" inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = adm_offline_application_issue.erp_academic_year_id" + 
						" inner join erp_receipts ON erp_receipts.erp_receipts_id = adm_offline_application_issue.erp_receipts_id" + 
						" inner join erp_number_generation ON erp_number_generation.erp_number_generation_id = erp_receipts.erp_number_generation_id"+
						" inner join acc_financial_year ON acc_financial_year.acc_financial_year_id = erp_receipts.acc_financial_year_id"+
						" where adm_offline_application_issue.erp_academic_year_id=:pAcademicYearId" + 
						" and adm_offline_application_issue.appln_no=:pApplicationNumber" + 
						" and adm_offline_application_issue.record_status='A'" + 
						" and erp_academic_year.record_status='A'"+
						" and erp_receipts.record_status='A'"+
						" and erp_number_generation.record_status='A'"+
						" and acc_financial_year.record_status='A'";
				Query query = context.createNativeQuery(Query, Tuple.class);				
				query.setParameter("pApplicationNumber", applicationNumber);
				query.setParameter("pAcademicYearId", academicYearId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public AdmOfflineApplicationIssueDBO getDetailsByReceiptNo(String receiptNumber,String receiptDate,String financialYearId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <AdmOfflineApplicationIssueDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public AdmOfflineApplicationIssueDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from AdmOfflineApplicationIssueDBO bo where bo.erpReceiptsDBO.receiptNo=:pReceiptNumber " +
												 //+ " and bo.erpReceiptsDBO.receiptsDate=:pReceiptDate"+
												" and bo.erpReceiptsDBO.accFinancialYearDBO.id=:pFinancialYearId and bo.erpReceiptsDBO.accFinancialYearDBO.recordStatus='A' "+
												" and bo.erpReceiptsDBO.erpAcademicYearDBO.recordStatus='A' and bo.erpReceiptsDBO.recordStatus='A' and bo.recordStatus='A' ");
				AdmOfflineApplicationIssueDBO dbo = new AdmOfflineApplicationIssueDBO();		
				query.setParameter("pReceiptNumber", receiptNumber);
				//query.setParameter("pReceiptDate",Utils.convertStringDateTimeToLocalDateTime1(receiptDate));
				query.setParameter("pFinancialYearId", Integer.parseInt(financialYearId));
				dbo = (AdmOfflineApplicationIssueDBO) Utils.getUniqueResult(query.getResultList());								
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public ErpUsersDBO getUser(String userId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional <ErpUsersDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public ErpUsersDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpUsersDBO bo where bo.recordStatus='A' and bo.id=:pUserId ");
				ErpUsersDBO dbo = new ErpUsersDBO();	
				query.setParameter("pUserId", Integer.parseInt(userId));				
				dbo = (ErpUsersDBO) Utils.getUniqueResult(query.getResultList());								
				return  dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public List<Tuple> getReceiptDatesByReceiptNumber(String receiptNumber) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_UNIT = "select acc_financial_year.acc_financial_year_id as ID,erp_receipts.receipts_date as Text from erp_receipts "+
									"inner join acc_financial_year ON acc_financial_year.acc_financial_year_id = erp_receipts.acc_financial_year_id "+
									"where erp_receipts.receipt_no=:pReceiptNo and erp_receipts.record_status='A' and acc_financial_year.record_status='A'" +
									" and erp_receipts.referenced_erp_table_name = 'adm_offline_application_issue'";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				query.setParameter("pReceiptNo", receiptNumber);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public ErpTemplateDBO getTemplate(String templateCode) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public ErpTemplateDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpTemplateDBO where templateCode=:templateCode and recordStatus='A'");
				query.setParameter("templateCode", templateCode);
				return (ErpTemplateDBO) Utils.getUniqueResult(query.getResultList()) ;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

    public AdmApplnNumberGenDetailsDBO checkApplicationNoExists(String offlineApplnNoPrefix,String offlineApplnNo,String academicYearId ) {
		StringBuffer queryString = new StringBuffer();
			queryString.append("from AdmApplnNumberGenDetailsDBO bo where bo.recordStatus='A' and bo.admApplnNumberGenerationDBO.recordStatus='A' "
					+ " and bo.admApplnNumberGenerationDBO.academicYearDBO.id = :academicYearId and"
					+ " bo.admApplnNumberGenerationDBO.offlineApplnNoPrefix = :offlineApplnNoPrefix and  :offlineApplnNo "
					+ " between bo.admApplnNumberGenerationDBO.offlineApplnNoFrom and bo.admApplnNumberGenerationDBO.offlineApplnNoTo order by bo.id");
			List<AdmApplnNumberGenDetailsDBO> admApplnNumberGenerationDBOList =
					sessionFactory.withSession(s->{
						Mutiny.Query<AdmApplnNumberGenDetailsDBO> query = s.createQuery(queryString.toString(),AdmApplnNumberGenDetailsDBO.class);
						query.setParameter("academicYearId", Integer.parseInt(academicYearId));
						query.setParameter("offlineApplnNoPrefix", offlineApplnNoPrefix);
						query.setParameter("offlineApplnNo", Integer.parseInt(offlineApplnNo));
					return query.getResultList();
	    }).await().indefinitely();
	    return !Utils.isNullOrEmpty(admApplnNumberGenerationDBOList) ? admApplnNumberGenerationDBOList.get(0) : null;
	}
	public AdmProgrammeSettingsDBO getProgramSettings(int programmeId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("select bo from AdmProgrammeSettingsDBO bo"
				+" left join fetch bo.accFeeHeadsDBO feeHead"
				+ " left join fetch feeHead.accFeeHeadsAccountList accountDboList "
				+ " where bo.recordStatus='A' and bo.erpProgrammeDBO.id = :programmeId ");
		List<AdmProgrammeSettingsDBO> admProgrammeSettingsDBOList =
   				sessionFactory.withSession(s->{
   					Mutiny.Query<AdmProgrammeSettingsDBO> query = s.createQuery(queryString.toString(),AdmProgrammeSettingsDBO.class);
					query.setParameter("programmeId", programmeId);
					return query.getResultList();
        }).await().indefinitely();
        return !Utils.isNullOrEmpty(admProgrammeSettingsDBOList) ? admProgrammeSettingsDBOList.get(0) : null;
    }
	
}
