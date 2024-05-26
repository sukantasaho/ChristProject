package com.christ.erp.services.helpers.account.fee;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeCategoryDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandAdjustmentDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandAdjustmentDetailDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandCombinationDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandCombinationTypeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDetailDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDetailLogDBO;
import com.christ.erp.services.dbobjects.account.fee.AdjustmentType;
import com.christ.erp.services.dbobjects.account.fee.LoggingStatus;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.transactions.account.fee.DemandSlipGenerationTransaction;

@Service

public class DemandSlipGenertaionHelper {
	@Autowired
	DemandSlipGenerationTransaction demandSlipGenerationTransaction;
	
	public Set<AccFeeDemandDBO> copyAccBatchFeeToDemandDBO( AccBatchFeeCategoryDBO accBatchFeeCategoryDBO, String userId,
			   List<AccFeeHeadsAccountDBO> accFeeHeadsAccountDBOList, AccFeeDemandCombinationTypeDBO accFeeDemandCombinationTypeDBO, StudentDBO studentDBO, StudentApplnEntriesDBO studentApplnEntriesDBO){
		Set<AccFeeDemandDBO> accFeeDemandDBOSet = new HashSet<AccFeeDemandDBO>();
		AccFinancialYearDBO curFeeFinYearDBO = demandSlipGenerationTransaction.getCurrentFeeFinancialYearIdAndName();
		Integer workFlowProcessId = demandSlipGenerationTransaction.getWorkFlowProcessId("ADJUSTMENT_APPROVED");
	
		AccFeeDemandDBO accFeeDemandDBO = new AccFeeDemandDBO();
		ErpAcademicYearDBO erpAcademicYearDBO = new ErpAcademicYearDBO();
		erpAcademicYearDBO.setId(accBatchFeeCategoryDBO.getAccBatchFeeDurationsDetailDBO().getAccBatchFeeDurationsDBO().getAcaDurationDBO().getErpAcademicYearDBO().getId());
		accFeeDemandDBO.setErpAcademicYearDBO(erpAcademicYearDBO);
		accFeeDemandDBO.setAccBatchFeeCategoryDBO(accBatchFeeCategoryDBO);
		accFeeDemandDBO.setRecordStatus('A');
		accFeeDemandDBO.setCreatedUsersId(Integer.parseInt(userId));
		accFeeDemandDBO.setModifiedUsersId(Integer.parseInt(userId));
		accFeeDemandDBO.setIsTuitionFee(true);
		//demand generated with current financial year
		if(!Utils.isNullOrEmpty(curFeeFinYearDBO)) {
			AccFinancialYearDBO accFinancialYearDBO = new AccFinancialYearDBO();
			accFinancialYearDBO.setId(accBatchFeeCategoryDBO.getAccBatchFeeDurationsDetailDBO().getAccFinancialYearDBO().getId());
			accFeeDemandDBO.setAccFinancialYearDBO(accFinancialYearDBO);
		}
		if(!Utils.isNullOrEmpty(studentApplnEntriesDBO)) {
			accFeeDemandDBO.setStudentApplnEntriesDBO(studentApplnEntriesDBO);	
		}
		else if(!Utils.isNullOrEmpty(studentDBO)) {
			accFeeDemandDBO.setStudentDBO(studentDBO);
		}
		Set<AccFeeDemandDetailDBO> accFeeDemandDetailDBOSet = new HashSet<AccFeeDemandDetailDBO>();
		Set<AccFeeDemandAdjustmentDBO> accFeeDemandAdjustmentDBOSet = new HashSet<AccFeeDemandAdjustmentDBO>();
		AccFeeDemandAdjustmentDBO accFeeDemandAdjustmentDBO = new AccFeeDemandAdjustmentDBO();
		
		AtomicReference< BigDecimal> totalAdjustmentAmt = new AtomicReference<>((BigDecimal) new BigDecimal(0));
		if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBO.getAccBatchFeeHeadDBOSet())) {
			Set<AccFeeDemandAdjustmentDetailDBO> accFeeDemandAdjustmentDetailDBOSet = new HashSet<AccFeeDemandAdjustmentDetailDBO>();
			accBatchFeeCategoryDBO.getAccBatchFeeHeadDBOSet().forEach(head->{
				if(!Utils.isNullOrEmpty(head.getAccBatchFeeAccountDBOSet())) {
					head.getAccBatchFeeAccountDBOSet().forEach(account->{
						AccFeeDemandDetailDBO accDemandDetailDBO = new AccFeeDemandDetailDBO();
						AccFeeDemandDetailLogDBO accFeeDemandDetailLogDBO = new AccFeeDemandDetailLogDBO();
						Set<AccFeeDemandDetailLogDBO> accFeeDemandDetailLogDBOSet = new HashSet<AccFeeDemandDetailLogDBO>();
						accDemandDetailDBO.setAccBatchFeeAccountDBO(account);
						accDemandDetailDBO.setAmountToBePaidInAccount(account.getFeeAccountAmount());
						accDemandDetailDBO.setAccFeeDemandDBO(accFeeDemandDBO);
						accDemandDetailDBO.setRecordStatus('A');
						accDemandDetailDBO.setCreatedUsersId(Integer.parseInt(userId));
						accDemandDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
						if(!Utils.isNullOrEmpty(account.getErpCurrencyDBO())) {
							accDemandDetailDBO.setErpCurrencyDBO(account.getErpCurrencyDBO());
						}
					
						BigDecimal totalAmt = new BigDecimal(0);
						if(!Utils.isNullOrEmpty(account.getFeeAccountAmount())) {
							totalAmt = account.getFeeAccountAmount();
						}
						if(account.getFeeScholarshipAmount()!=null && account.getFeeScholarshipAmount().compareTo(BigDecimal.ZERO) > 0) {
							AccFeeDemandAdjustmentDetailDBO accFeeDemandAdjustmentDetailDBO = new AccFeeDemandAdjustmentDetailDBO();
							accFeeDemandAdjustmentDetailDBO.setAdjustmentAmount(account.getFeeScholarshipAmount());
							totalAdjustmentAmt.set(totalAdjustmentAmt.get().add(account.getFeeScholarshipAmount()));
							accFeeDemandAdjustmentDetailDBO.setAccFeeDemandAdjustmentDBO(accFeeDemandAdjustmentDBO);
							accFeeDemandAdjustmentDetailDBO.setAccFeeDemandDetailDBO(accDemandDetailDBO);
							if(!Utils.isNullOrEmpty(account.getAccFeeDemandAdjustmentCategoryDBO())) {
								accFeeDemandAdjustmentDetailDBO.setAccFeeDemandAdjustmentCategory(account.getAccFeeDemandAdjustmentCategoryDBO());
							}
							accDemandDetailDBO.setScholarshipInAccount(account.getFeeScholarshipAmount());
							accDemandDetailDBO.setDeductionsInAccount(account.getFeeScholarshipAmount());
							
							if(!Utils.isNullOrEmpty(account.getFeeScholarshipAmount())) {
								totalAmt = totalAmt.subtract(account.getFeeScholarshipAmount());
							}
							accFeeDemandAdjustmentDetailDBO.setRecordStatus('A');
							accFeeDemandAdjustmentDetailDBO.setCreatedUsersId(Integer.parseInt(userId));
							accFeeDemandAdjustmentDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
							accFeeDemandAdjustmentDetailDBOSet.add(accFeeDemandAdjustmentDetailDBO);
						}
						accDemandDetailDBO.setAmountToBePaidAfterDeductions(totalAmt);
						accDemandDetailDBO.setBalanceAmountInAccount(totalAmt);
						BeanUtils.copyProperties(accDemandDetailDBO, accFeeDemandDetailLogDBO);
						accFeeDemandDetailLogDBO.setLoggingStatus(LoggingStatus.DEMAND_GENERATED);
						accFeeDemandDetailLogDBO.setAccFeeDemandDetailDBO(accDemandDetailDBO);
						accFeeDemandDetailLogDBOSet.add(accFeeDemandDetailLogDBO);
						accDemandDetailDBO.setAccFeeDemandDetailLogDBOSet(accFeeDemandDetailLogDBOSet);
						accFeeDemandDetailDBOSet.add(accDemandDetailDBO);
					});
				}
				if(accFeeDemandAdjustmentDetailDBOSet!=null && accFeeDemandAdjustmentDetailDBOSet.size() > 0) {
					accFeeDemandAdjustmentDBO.setAdjustmentType(AdjustmentType.S);
					accFeeDemandAdjustmentDBO.setAccFeeDemandDBO(accFeeDemandDBO);
					//setting workflow process id as scholarship approved as there is no approval for this scholarship
					if(!Utils.isNullOrEmpty(workFlowProcessId)) {
						ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
						erpWorkFlowProcessDBO.setId(workFlowProcessId);
						accFeeDemandAdjustmentDBO.setErpWorkFlowProcessDBO(erpWorkFlowProcessDBO);
					}
					accFeeDemandAdjustmentDBO.setRecordStatus('A');
					accFeeDemandAdjustmentDBO.setCreatedUsersId(Integer.parseInt(userId));
					accFeeDemandAdjustmentDBO.setModifiedUsersId(Integer.parseInt(userId));
					accFeeDemandAdjustmentDBO.setAdjustmentApplied(true);
					accFeeDemandAdjustmentDBO.setAccFeeDemandAdjustmentDetailDBOSet(accFeeDemandAdjustmentDetailDBOSet);
					accFeeDemandAdjustmentDBOSet.add(accFeeDemandAdjustmentDBO);
				}
				
			});
			if(!Utils.isNullOrEmpty(accFeeDemandDetailDBOSet)) {
				accFeeDemandDBO.setAccFeeDemandDetailDBOSet(accFeeDemandDetailDBOSet);
			}
			if(!Utils.isNullOrEmpty(accFeeDemandAdjustmentDBOSet)) {
				accFeeDemandDBO.setAccFeeDemandAdjustmentDBOSet(accFeeDemandAdjustmentDBOSet);
			}
			accFeeDemandDBOSet.add(accFeeDemandDBO);
		};
		accFeeDemandAdjustmentDBO.setTotalAdjustment( totalAdjustmentAmt.get());
 		 
		if(!Utils.isNullOrEmpty(accFeeDemandCombinationTypeDBO) && !Utils.isNullOrEmpty(accFeeDemandCombinationTypeDBO.getDemandCombinationType())) {
			if(!Utils.isNullOrEmpty(accFeeHeadsAccountDBOList)) {
					AccFeeDemandDBO accFeeDemandDBOAdditional = new AccFeeDemandDBO();
					accFeeDemandDBOAdditional.setErpAcademicYearDBO(erpAcademicYearDBO);
					if(!Utils.isNullOrEmpty(studentApplnEntriesDBO)) {
						accFeeDemandDBOAdditional.setStudentApplnEntriesDBO(studentApplnEntriesDBO);	
					}
					else if(!Utils.isNullOrEmpty(studentDBO)) {
						accFeeDemandDBOAdditional.setStudentDBO(studentDBO);
					}
					//demand generated with current financial year
					AccFinancialYearDBO accFinancialYearDBO = new AccFinancialYearDBO();
					accFinancialYearDBO.setId(curFeeFinYearDBO.getId());
					accFeeDemandDBOAdditional.setAccFinancialYearDBO(accFinancialYearDBO);
					accFeeDemandDBOAdditional.setRecordStatus('A');
					accFeeDemandDBOAdditional.setCreatedUsersId(Integer.parseInt(userId));
					accFeeDemandDBOAdditional.setModifiedUsersId(Integer.parseInt(userId));
					if(accFeeDemandCombinationTypeDBO.getDemandCombinationType().equalsIgnoreCase("STUDENT_APPLICATION")) {
						Set<AccFeeDemandCombinationDBO> accFeeDemandCombinationDBOSet = new HashSet<AccFeeDemandCombinationDBO>();
						AccFeeDemandCombinationDBO accFeeDemandCombinationDBO = new AccFeeDemandCombinationDBO();
						accFeeDemandCombinationDBO.setAccFeeDemandCombinationTypeDBO(accFeeDemandCombinationTypeDBO);
						accFeeDemandCombinationDBO.setAccFeeDemandDBO(accFeeDemandDBOAdditional);
						accFeeDemandCombinationDBO.setRecordStatus('A');
						accFeeDemandCombinationDBO.setCreatedUsersId(Integer.parseInt(userId));
						accFeeDemandCombinationDBO.setModifiedUsersId(Integer.parseInt(userId));
						if(!Utils.isNullOrEmpty(studentApplnEntriesDBO)) {
							accFeeDemandCombinationDBO.setEntriesId(studentApplnEntriesDBO.getId());
						}
						accFeeDemandCombinationDBOSet.add(accFeeDemandCombinationDBO);
						accFeeDemandDBOAdditional.setAccFeeDemandCombinationDBOSet(accFeeDemandCombinationDBOSet);
					}
					Set<AccFeeDemandDetailDBO> accFeeDemandDetailDBOSetAdditional = new HashSet<AccFeeDemandDetailDBO>();
					accFeeHeadsAccountDBOList.forEach(account->{
						AccFeeDemandDetailDBO accDemandDetailDBO = new AccFeeDemandDetailDBO();
						AccFeeDemandDetailLogDBO accFeeDemandDetailLogDBO = new AccFeeDemandDetailLogDBO();
						Set<AccFeeDemandDetailLogDBO> accFeeDemandDetailLogDBOSet = new HashSet<AccFeeDemandDetailLogDBO>();
						accDemandDetailDBO.setAccFeeHeadsAccountDBO(account);
						accDemandDetailDBO.setAmountToBePaidInAccount(account.getAmount());
						accDemandDetailDBO.setAmountToBePaidAfterDeductions(account.getAmount());
						accDemandDetailDBO.setBalanceAmountInAccount(account.getAmount());
						accDemandDetailDBO.setAccFeeDemandDBO(accFeeDemandDBOAdditional);
						accDemandDetailDBO.setRecordStatus('A');
						accDemandDetailDBO.setCreatedUsersId(Integer.parseInt(userId));
						accDemandDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
						BeanUtils.copyProperties(accDemandDetailDBO, accFeeDemandDetailLogDBO);
						accFeeDemandDetailLogDBO.setLoggingStatus(LoggingStatus.DEMAND_GENERATED);
						accFeeDemandDetailLogDBO.setAccFeeDemandDetailDBO(accDemandDetailDBO);
						accFeeDemandDetailLogDBOSet.add(accFeeDemandDetailLogDBO);
						accDemandDetailDBO.setAccFeeDemandDetailLogDBOSet(accFeeDemandDetailLogDBOSet);
						accFeeDemandDetailDBOSetAdditional.add(accDemandDetailDBO);
					});
					accFeeDemandDBOAdditional.setAccFeeDemandDetailDBOSet(accFeeDemandDetailDBOSetAdditional);
					accFeeDemandDBOSet.add(accFeeDemandDBOAdditional);
				 
			}	
		}
		return accFeeDemandDBOSet;
	}
}
