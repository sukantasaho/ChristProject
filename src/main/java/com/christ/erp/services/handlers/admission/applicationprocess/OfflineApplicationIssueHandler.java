package com.christ.erp.services.handlers.admission.applicationprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
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
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.helpers.admission.applicationprocess.OfflineApplicationIssueHelper;
import com.christ.erp.services.transactions.admission.applicationprocess.OfflineApplicationIssueTransaction;

import reactor.core.publisher.Mono;
@Service
public class OfflineApplicationIssueHandler {
	@Autowired
	OfflineApplicationIssueTransaction offlineApplicationIssueTransaction;
	
	//private static volatile OfflineApplicationIssueHandler offlineApplicationIssueHandler = null;
	//OfflineApplicationIssueTransaction offlineApplicationIssueTransaction = OfflineApplicationIssueTransaction.getInstance();
	OfflineApplicationIssueHelper helper = OfflineApplicationIssueHelper.getInstance();
	/*
    public static OfflineApplicationIssueHandler getInstance() {
        if(offlineApplicationIssueHandler==null) {
        	offlineApplicationIssueHandler = new OfflineApplicationIssueHandler();
        }
        return offlineApplicationIssueHandler;
    }
    */
    public ApiResult<OfflineApplicationIssueDTO> getCurrent() {
		ApiResult<OfflineApplicationIssueDTO> current = new ApiResult<OfflineApplicationIssueDTO>();
		try {
			ErpAcademicYearDBO dbo_academic = offlineApplicationIssueTransaction.getCurrentAcademicYear();
			OfflineApplicationIssueDTO dto = new OfflineApplicationIssueDTO();
			dto.academicYear = new ExModelBaseDTO();
			dto.academicYear.id = String.valueOf(dbo_academic.id);
			dto.academicYear.text = dbo_academic.academicYearName;
			current.dto = dto;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return current;
	}
    
    public boolean saveOrUpdate(OfflineApplicationIssueDTO data, ApiResult<OfflineApplicationIssueDTO> result,String userId) throws NumberFormatException, Exception {
		boolean isSaved = false;
		ErpReceiptsDBO erpReceiptsDBO = null;
		AdmOfflineApplicationIssueDBO admOfflineApplicationIssueDBO = null;
		AccFinancialYearDBO accFinancialYearDBO = null;
		ErpNumberGenerationDBO erpNumberGenerationDBO = null;
		admOfflineApplicationIssueDBO = offlineApplicationIssueTransaction.getAdmOfflineApplicationIssueDBO(data.applicationNo, data.academicYear.id);
    	if(!Utils.isNullOrEmpty(admOfflineApplicationIssueDBO) && Utils.isNullOrEmpty(data.id)) {
    		result.success = false;
    		result.failureMessage = "Already exist Offline Application Issue for this application number and batch year.";
    	}else {
    		if(!Utils.isNullOrEmpty(admOfflineApplicationIssueDBO)) {
        		erpReceiptsDBO = admOfflineApplicationIssueDBO.erpReceiptsDBO;
        		accFinancialYearDBO = admOfflineApplicationIssueDBO.erpReceiptsDBO.accFinancialYearDBO;
        		erpNumberGenerationDBO = admOfflineApplicationIssueDBO.erpReceiptsDBO.erpNumberGenerationDBO;
        	}else {
        		accFinancialYearDBO = offlineApplicationIssueTransaction.getFinancialYear();
        		erpNumberGenerationDBO = offlineApplicationIssueTransaction.getErpNumberGeneration(accFinancialYearDBO.id);
        	}
        	if(!Utils.isNullOrEmpty(accFinancialYearDBO) && !Utils.isNullOrEmpty(erpNumberGenerationDBO)) {
        		ErpUsersDBO erpUsersDBO = offlineApplicationIssueTransaction.getUser(userId);
        		erpReceiptsDBO = helper.convertDataDTOToErpReceiptDBO(data, erpNumberGenerationDBO, accFinancialYearDBO, erpUsersDBO,erpReceiptsDBO);
        		admOfflineApplicationIssueDBO = helper.convertDataDTOToAdmOfflineApplicationIssueDBO(data, userId,admOfflineApplicationIssueDBO);
        		isSaved = offlineApplicationIssueTransaction.saveOrUpdate(accFinancialYearDBO.id,erpReceiptsDBO,admOfflineApplicationIssueDBO,data);
        		if(isSaved) {      			
                  	data.printReceiptDate =  Utils.convertLocalDateToStringDate(Utils.convertStringDateTimeToLocalDate(data.receiptDate));
                  	ErpTemplateDBO templateDBOForEmail = offlineApplicationIssueTransaction.getTemplate("OFFLINE_APPLICATION_ISSUE");
                  	if(!Utils.isNullOrEmpty(templateDBOForEmail)) {
                  		data.templateMsg = templateDBOForEmail.templateDescription;
                  	}
        		}
        	}
    	}
		return isSaved;
	}
    
    public ApiResult<OfflineApplicationIssueDTO> getApplicantDetails(String applnNo,String academicYearID) {
		ApiResult<OfflineApplicationIssueDTO> result = new ApiResult<OfflineApplicationIssueDTO>();
		try {
			  AdmOfflineApplicationIssueDBO dbo = new AdmOfflineApplicationIssueDBO();
			  dbo= offlineApplicationIssueTransaction.getAdmOfflineApplicationIssueDBO(applnNo, academicYearID);
			  if(dbo == null) {
				result.dto = null;
			  }else {
				  OfflineApplicationIssueDTO dto = new OfflineApplicationIssueDTO();
				  dto.id= String.valueOf(dbo.id);
				  dto.appicantName = dbo.applicantName;
				  dto.financialYear = new ExModelBaseDTO();
				  dto.financialYear.id = String.valueOf(dbo.erpReceiptsDBO.accFinancialYearDBO.id);
				  dto.financialYear.text = dbo.erpReceiptsDBO.accFinancialYearDBO.financialYear;
				  dto.academicYear = new ExModelBaseDTO();
				  dto.academicYear.id = dbo.erpReceiptsDBO.erpAcademicYearDBO.id.toString();
				  dto.academicYear.text = dbo.erpReceiptsDBO.erpAcademicYearDBO.academicYearName;
				  dto.receiptDate = Utils.convertLocalDateTimeToStringDate(dbo.erpReceiptsDBO.receiptsDate);
				  dto.printReceiptDate = Utils.convertLocalDateTimeToStringDate(dbo.erpReceiptsDBO.receiptsDate);
				  dto.receiptNumber = dbo.erpReceiptsDBO.receiptNo;
				  dto.amount = String.valueOf(dbo.erpReceiptsDBO.receiptsAmount);
				  dto.applicationNo = dbo.applnNo;
				  dto.mobileNo = dbo.mobileNo;
				  dto.code = dbo.mobileNoCountryCode;
				  dto.isCancelled = dbo.erpReceiptsDBO.isCancelled;
				  if(!Utils.isNullOrEmpty(dbo.erpReceiptsDBO.cancelComments)) {
					  dto.cancelledReason = dbo.erpReceiptsDBO.cancelComments;
				  }

				  if(!Utils.isNullOrEmpty(dbo.erpReceiptsDBO.accFeePaymentModeDBO)) {
					  ExModelBaseDTO paymentModeDTO = new ExModelBaseDTO();
					  paymentModeDTO.id = Integer.toString(dbo.erpReceiptsDBO.accFeePaymentModeDBO.id);
					  paymentModeDTO.text = dbo.erpReceiptsDBO.accFeePaymentModeDBO.paymentMode;
					  dto.payementMode = paymentModeDTO;
				  }
				  if(!Utils.isNullOrEmpty(dbo.erpReceiptsDBO.DdNumber)) {
					  dto.setDdNo(dbo.erpReceiptsDBO.DdNumber);
				  }
				  
				  if(!Utils.isNullOrEmpty(dbo.erpReceiptsDBO.accFeeHeadsDBO)) {
					  ExModelBaseDTO accHeadDTO = new ExModelBaseDTO();
					  accHeadDTO.id = Integer.toString(dbo.erpReceiptsDBO.accFeeHeadsDBO.getId());
					  accHeadDTO.text = dbo.erpReceiptsDBO.accFeeHeadsDBO.getHeading();
					  dto.acctHead = accHeadDTO;
				  }
				  if(!Utils.isNullOrEmpty(dbo.erpReceiptsDBO.accAccountsDBO)) {
					  ExModelBaseDTO accountDTO = new ExModelBaseDTO();
					  accountDTO.id = Integer.toString(dbo.erpReceiptsDBO.accAccountsDBO.id);
					  accountDTO.text = dbo.erpReceiptsDBO.accAccountsDBO.accountNo;
					  dto.accountName = accountDTO;
				  }
				  result.dto = dto;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    
    public List<OfflineApplicationIssueDTO> getApplicantDetailsList(String applicationNumber,String academicYearId) {
		List<OfflineApplicationIssueDTO> list  = null;
		try {
			List<Tuple> gridList = offlineApplicationIssueTransaction.getGridData(applicationNumber,academicYearId);
			list = helper.getGridList(gridList, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
    
    public ApiResult<OfflineApplicationIssueDTO> getDetailsByReceiptNo(String receiptNumber, String receiptDate,String financialYearId) {
		ApiResult<OfflineApplicationIssueDTO> result = new ApiResult<OfflineApplicationIssueDTO>();
		try {
			ErpTemplateDBO templateDBO = offlineApplicationIssueTransaction.getTemplate("OFFLINE_APPLICATION_ISSUE");
          	if(!Utils.isNullOrEmpty(templateDBO)) {
          		AdmOfflineApplicationIssueDBO admOfflineApplicationIssueDBO = new AdmOfflineApplicationIssueDBO();
          		admOfflineApplicationIssueDBO = offlineApplicationIssueTransaction.getDetailsByReceiptNo(receiptNumber,receiptDate,financialYearId);
    			if(!Utils.isNullOrEmpty(admOfflineApplicationIssueDBO))
    				result.dto = helper.convertDBOToDTO(admOfflineApplicationIssueDBO,templateDBO.templateDescription);
          	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    
    public void getReceiptDatesByReceiptNumber(ApiResult<List<LookupItemDTO>> result,String receiptNumber)throws Exception {
		List<Tuple> mappings = offlineApplicationIssueTransaction.getReceiptDatesByReceiptNumber(receiptNumber);
		if(!Utils.isNullOrEmpty(mappings)) {
			result.success = true;
			result.dto = new ArrayList<>();
			for(Tuple mapping : mappings) {
				LookupItemDTO itemInfo = new LookupItemDTO();
				itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
				if(!Utils.isNullOrEmpty(mapping.get("Text"))) {
				    itemInfo.label = Utils.convertLocalDateTimeToStringDateTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("Text").toString()));
				}
				result.dto.add(itemInfo);
			}
		}
	}
    
    public Mono<OfflineApplicationIssueDTO> getAmountForOfflineApplication(String offlineApplnNoPrefix,String offlineApplnNo,String academicYearId) {
    	OfflineApplicationIssueDTO offlineApplicationIssueDTO = new OfflineApplicationIssueDTO();
    	AdmApplnNumberGenDetailsDBO admpplnNumberGenDetailsDBO = offlineApplicationIssueTransaction.checkApplicationNoExists(offlineApplnNoPrefix, offlineApplnNo, academicYearId);
    	if(!Utils.isNullOrEmpty(admpplnNumberGenDetailsDBO)){
    		offlineApplicationIssueDTO.setApplicationNo(offlineApplnNoPrefix + offlineApplnNo);
        	AdmProgrammeSettingsDBO admProgrammeSettingsDBO =  offlineApplicationIssueTransaction.getProgramSettings(admpplnNumberGenDetailsDBO.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
        	if(!Utils.isNullOrEmpty(admProgrammeSettingsDBO)) {
        		if(!Utils.isNullOrEmpty(admProgrammeSettingsDBO.getAccFeeHeadsDBO()) && admProgrammeSettingsDBO.getAccFeeHeadsDBO().getRecordStatus() == 'A') {
	        		List<AccFeeHeadsAccountDBO> accFeeHeadsAccountDBOList = admProgrammeSettingsDBO.getAccFeeHeadsDBO().getAccFeeHeadsAccountList().stream().filter(a->a.erpCampusDBO.id == admpplnNumberGenDetailsDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId()).collect(Collectors.toList());
	        		if(!Utils.isNullOrEmpty(accFeeHeadsAccountDBOList)) {
	        			AccFeeHeadsAccountDBO accFeeHeadsAccountDBO = accFeeHeadsAccountDBOList.get(0);
	        			ExModelBaseDTO accFeeHeadDTO =  new ExModelBaseDTO();
	        			accFeeHeadDTO.setText(accFeeHeadsAccountDBO.getAccFeeHeadsDBO().getHeading());
	        			accFeeHeadDTO.id = Integer.toString(accFeeHeadsAccountDBO.getAccFeeHeadsDBO().getId());
	        			offlineApplicationIssueDTO.setAcctHead(accFeeHeadDTO);
	        			ExModelBaseDTO accountDTO =  new ExModelBaseDTO();
	        			accountDTO.setText(accFeeHeadsAccountDBO.getAccAccountsDBO().getAccountName());
	        			accountDTO.id = Integer.toString(accFeeHeadsAccountDBO.getAccAccountsDBO().getId());
	        			accountDTO.tag = accFeeHeadsAccountDBO.getAccAccountsDBO().getLogoFileName();
	        			offlineApplicationIssueDTO.setAccountName(accountDTO);
	        			offlineApplicationIssueDTO.setAmount(accFeeHeadsAccountDBO.getAmount().toString());
	        		}
	        	}
        	}
    	}
    	return Mono.just(offlineApplicationIssueDTO);
	}
	
}
