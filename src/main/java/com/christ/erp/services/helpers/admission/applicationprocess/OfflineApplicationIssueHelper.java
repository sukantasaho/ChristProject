package com.christ.erp.services.helpers.admission.applicationprocess;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccFeePaymentModeDBO;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmOfflineApplicationIssueDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ErpNumberGenerationDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ErpReceiptsDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationIssueDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class OfflineApplicationIssueHelper {

	public static volatile OfflineApplicationIssueHelper offlineApplicationIssueHelper = null;
	public static OfflineApplicationIssueHelper getInstance() {
		if(offlineApplicationIssueHelper==null) {
			offlineApplicationIssueHelper = new OfflineApplicationIssueHelper();
		}
		return offlineApplicationIssueHelper;
	}
	
	public ErpReceiptsDBO convertDataDTOToErpReceiptDBO(OfflineApplicationIssueDTO offlineApplicationIssueDTO,ErpNumberGenerationDBO erpNumberGenerationDBO,AccFinancialYearDBO accFinancialYearDBO,ErpUsersDBO erpUsersDBO,ErpReceiptsDBO erpReceiptsDBO) throws ParseException {
		if(!Utils.isNullOrEmpty(erpReceiptsDBO)) {
			erpReceiptsDBO.modifiedUsersId = erpUsersDBO.id ;
			erpReceiptsDBO.id = erpReceiptsDBO.id;
		}else {
			erpReceiptsDBO = new ErpReceiptsDBO();
			erpReceiptsDBO.createdUsersId = erpUsersDBO.id ;
			erpReceiptsDBO.modifiedUsersId = erpUsersDBO.id ;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.academicYear) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.academicYear.id) 
				&& !Utils.isNullOrEmpty(offlineApplicationIssueDTO.academicYear.id.trim())) {
			ErpAcademicYearDBO erpAcademicYearDBO = new ErpAcademicYearDBO();
			erpAcademicYearDBO.id = Integer.parseInt(offlineApplicationIssueDTO.academicYear.id.trim());
			erpReceiptsDBO.erpAcademicYearDBO = erpAcademicYearDBO;
		}
		else {
			erpReceiptsDBO.erpAcademicYearDBO = null;
		}
		if(!Utils.isNullOrEmpty(accFinancialYearDBO)) {
			erpReceiptsDBO.accFinancialYearDBO = accFinancialYearDBO;
		}
		else {
			erpReceiptsDBO.accFinancialYearDBO = null;
		}
		if(!Utils.isNullOrEmpty(erpNumberGenerationDBO)) {
			erpReceiptsDBO.erpNumberGenerationDBO = erpNumberGenerationDBO;
		}
		else {
			erpReceiptsDBO.erpNumberGenerationDBO = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.acctHead) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.acctHead.id) 
				&& !Utils.isNullOrEmpty(offlineApplicationIssueDTO.acctHead.id.trim())) {
			AccFeeHeadsDBO accFeeHeadsDBO = new AccFeeHeadsDBO();
			accFeeHeadsDBO.id = Integer.parseInt(offlineApplicationIssueDTO.acctHead.id.trim());
			erpReceiptsDBO.accFeeHeadsDBO = accFeeHeadsDBO;
		}
		else {
			erpReceiptsDBO.accFeeHeadsDBO = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.accountName) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.accountName.id) 
				&& !Utils.isNullOrEmpty(offlineApplicationIssueDTO.accountName.id.trim())) {
			AccAccountsDBO accAccountsDBO = new AccAccountsDBO();
			accAccountsDBO.id = Integer.parseInt(offlineApplicationIssueDTO.accountName.id.trim());
			erpReceiptsDBO.accAccountsDBO = accAccountsDBO;
		}
		else {
			erpReceiptsDBO.accAccountsDBO = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.payementMode) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.payementMode.id) 
				&& !Utils.isNullOrEmpty(offlineApplicationIssueDTO.payementMode.id.trim())) {
			AccFeePaymentModeDBO accFeePaymentModeDBO = new AccFeePaymentModeDBO();
			accFeePaymentModeDBO.id = Integer.parseInt(offlineApplicationIssueDTO.payementMode.id.trim());
			erpReceiptsDBO.accFeePaymentModeDBO = accFeePaymentModeDBO;
		}
		else {
			erpReceiptsDBO.accFeePaymentModeDBO = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.ddNo) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.ddNo.trim())) {
			erpReceiptsDBO.DdNumber = offlineApplicationIssueDTO.ddNo.trim();
		}else {
			erpReceiptsDBO.DdNumber = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.amount) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.amount.trim())) {
			erpReceiptsDBO.receiptsAmount = !Utils.isNullOrEmpty(offlineApplicationIssueDTO.amount)?new BigDecimal(offlineApplicationIssueDTO.amount):null;
		}else {
			erpReceiptsDBO.receiptsAmount = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.receiptDate) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.receiptDate.trim())) {
			erpReceiptsDBO.receiptsDate = Utils.convertStringDateTimeToLocalDateTime(offlineApplicationIssueDTO.receiptDate);
		}else {
			erpReceiptsDBO.receiptsDate = null;
		}
		if(!Utils.isNullOrEmpty(erpUsersDBO.empDBO)) {
			erpReceiptsDBO.empDBO = new EmpDBO();
			erpReceiptsDBO.empDBO.id = erpUsersDBO.empDBO.getId();
		}else
			erpReceiptsDBO.empDBO = null;
		erpReceiptsDBO.referencedErpTableName = "adm_offline_application_issue";
		erpReceiptsDBO.isCancelled = offlineApplicationIssueDTO.isCancelled;
		if(erpReceiptsDBO.isCancelled) {
			erpReceiptsDBO.cancelComments = offlineApplicationIssueDTO.cancelledReason;	
		}
		erpReceiptsDBO.recordStatus = 'A';
		return erpReceiptsDBO;
	}
	
	public AdmOfflineApplicationIssueDBO convertDataDTOToAdmOfflineApplicationIssueDBO(OfflineApplicationIssueDTO offlineApplicationIssueDTO,String userId,AdmOfflineApplicationIssueDBO admOfflineApplicationIssueDBO) {
		if(!Utils.isNullOrEmpty(admOfflineApplicationIssueDBO)) {
			admOfflineApplicationIssueDBO.modifiedUsersId = Integer.parseInt(userId) ;
		}else {
			admOfflineApplicationIssueDBO = new AdmOfflineApplicationIssueDBO();
			admOfflineApplicationIssueDBO.createdUsersId = Integer.parseInt(userId);
			admOfflineApplicationIssueDBO.modifiedUsersId = Integer.parseInt(userId) ;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.academicYear) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.academicYear.id) 
				&& !Utils.isNullOrEmpty(offlineApplicationIssueDTO.academicYear.id.trim())) {
			ErpAcademicYearDBO erpAcademicYearDBO = new ErpAcademicYearDBO();
			erpAcademicYearDBO.id = Integer.parseInt(offlineApplicationIssueDTO.academicYear.id.trim());
			admOfflineApplicationIssueDBO.erpAcademicYearDBO = erpAcademicYearDBO;
		}
		else {
			admOfflineApplicationIssueDBO.erpAcademicYearDBO = null;
		}
		if(Utils.isNullOrEmpty(admOfflineApplicationIssueDBO.erpReceiptsDBO)) {
			admOfflineApplicationIssueDBO.erpReceiptsDBO = null;
		}	
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.applicationNo) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.applicationNo.trim())) {
			admOfflineApplicationIssueDBO.applnNo = offlineApplicationIssueDTO.applicationNo.trim();
		}else {
			admOfflineApplicationIssueDBO.applnNo = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.appicantName) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.appicantName.trim())) {
			admOfflineApplicationIssueDBO.applicantName = offlineApplicationIssueDTO.appicantName.trim();
		}else {
			admOfflineApplicationIssueDBO.applicantName = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.code) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.code.trim())) {
			admOfflineApplicationIssueDBO.mobileNoCountryCode =  offlineApplicationIssueDTO.code.trim();
		}else {
			admOfflineApplicationIssueDBO.mobileNoCountryCode = null;
		}
		if(!Utils.isNullOrEmpty(offlineApplicationIssueDTO.mobileNo) && !Utils.isNullOrEmpty(offlineApplicationIssueDTO.mobileNo.trim())) {
			admOfflineApplicationIssueDBO.mobileNo =  offlineApplicationIssueDTO.mobileNo.trim();
		}else {
			admOfflineApplicationIssueDBO.mobileNo = null;
		}
		admOfflineApplicationIssueDBO.recordStatus = 'A';
		return admOfflineApplicationIssueDBO;
	}
	
	public OfflineApplicationIssueDTO convertDBOToDTO(AdmOfflineApplicationIssueDBO admOfflineApplicationIssueDBO,String templateDescription) throws ParseException {
		OfflineApplicationIssueDTO offlineApplicationIssueDTO = new OfflineApplicationIssueDTO();
		offlineApplicationIssueDTO.id = String.valueOf(admOfflineApplicationIssueDBO.id);
		offlineApplicationIssueDTO.receiptNumber = admOfflineApplicationIssueDBO.erpReceiptsDBO.erpNumberGenerationDBO.numberPrefix+admOfflineApplicationIssueDBO.erpReceiptsDBO.receiptNo;
		offlineApplicationIssueDTO.academicYear = new ExModelBaseDTO();
		offlineApplicationIssueDTO.academicYear.id =  admOfflineApplicationIssueDBO.erpAcademicYearDBO.id.toString();
		offlineApplicationIssueDTO.academicYear.text = admOfflineApplicationIssueDBO.erpAcademicYearDBO.academicYearName;
		offlineApplicationIssueDTO.acctHead = new ExModelBaseDTO();
		offlineApplicationIssueDTO.acctHead.id =  String.valueOf(admOfflineApplicationIssueDBO.erpReceiptsDBO.accFeeHeadsDBO.id);
		offlineApplicationIssueDTO.acctHead.text = admOfflineApplicationIssueDBO.erpReceiptsDBO.accFeeHeadsDBO.feeHeadsType;//+ "("+admOfflineApplicationIssueDBO.erpReceiptsDBO.accFeeHeadsDBO.code+")";
		offlineApplicationIssueDTO.accountName = new ExModelBaseDTO();
		offlineApplicationIssueDTO.accountName.id = String.valueOf(admOfflineApplicationIssueDBO.erpReceiptsDBO.accAccountsDBO.id);
		offlineApplicationIssueDTO.accountName.text = admOfflineApplicationIssueDBO.erpReceiptsDBO.accAccountsDBO.accountName;
		offlineApplicationIssueDTO.financialYear = new ExModelBaseDTO();
		offlineApplicationIssueDTO.financialYear.id = String.valueOf(admOfflineApplicationIssueDBO.erpReceiptsDBO.accFinancialYearDBO.id);
		offlineApplicationIssueDTO.financialYear.text = admOfflineApplicationIssueDBO.erpReceiptsDBO.accFinancialYearDBO.financialYear;
		offlineApplicationIssueDTO.receiptDate = Utils.convertLocalDateTimeToStringDateTime(admOfflineApplicationIssueDBO.erpReceiptsDBO.receiptsDate);
		offlineApplicationIssueDTO.payementMode = new ExModelBaseDTO();
		offlineApplicationIssueDTO.payementMode.id =String.valueOf(admOfflineApplicationIssueDBO.erpReceiptsDBO.accFeePaymentModeDBO.id);
		offlineApplicationIssueDTO.payementMode.text = admOfflineApplicationIssueDBO.erpReceiptsDBO.accFeePaymentModeDBO.paymentMode;
		offlineApplicationIssueDTO.amount = admOfflineApplicationIssueDBO.erpReceiptsDBO.receiptsAmount.toString();
		offlineApplicationIssueDTO.applicationNo = admOfflineApplicationIssueDBO.applnNo;
		offlineApplicationIssueDTO.appicantName = admOfflineApplicationIssueDBO.applicantName;
	    offlineApplicationIssueDTO.mobileNo = admOfflineApplicationIssueDBO.mobileNo;
	    offlineApplicationIssueDTO.code = admOfflineApplicationIssueDBO.mobileNoCountryCode;
	    offlineApplicationIssueDTO.isCancelled = admOfflineApplicationIssueDBO.erpReceiptsDBO.isCancelled;
	    offlineApplicationIssueDTO.cancelledReason = admOfflineApplicationIssueDBO.erpReceiptsDBO.cancelComments;
	    offlineApplicationIssueDTO.printReceiptDate = offlineApplicationIssueDTO.receiptDate;
	    if(!Utils.isNullOrEmpty(admOfflineApplicationIssueDBO.erpReceiptsDBO.DdNumber)) {
			offlineApplicationIssueDTO.ddNo = admOfflineApplicationIssueDBO.erpReceiptsDBO.DdNumber.toString();
		}
	    offlineApplicationIssueDTO.templateMsg = templateDescription;
		return offlineApplicationIssueDTO;
	}
	
	public List<OfflineApplicationIssueDTO> getGridList(List<Tuple> gridList,List<OfflineApplicationIssueDTO> list) throws ParseException {
		if(!Utils.isNullOrEmpty(gridList)) {
			list = new ArrayList<>();	
			for (Tuple tuple : gridList) {
				OfflineApplicationIssueDTO offlineApplicationIssueDTO = new OfflineApplicationIssueDTO();
				offlineApplicationIssueDTO.id =tuple.get("admOfflineApplnIssueId").toString();
				offlineApplicationIssueDTO.appicantName = tuple.get("applicantName").toString();
	      	    offlineApplicationIssueDTO.receiptDate = Utils.convertLocalDateTimeToStringDateTime(Utils.convertStringDateTimeToLocalDateTime(tuple.get("receiptsDate").toString()));
				offlineApplicationIssueDTO.amount = tuple.get("receiptsAmount").toString();
				offlineApplicationIssueDTO.receiptNumber = tuple.get("receiptNo").toString();	
				offlineApplicationIssueDTO.financialYear = new ExModelBaseDTO();
				offlineApplicationIssueDTO.financialYear.id = tuple.get("financialYearId").toString();
				offlineApplicationIssueDTO.financialYear.text = tuple.get("financialYear").toString();
				list.add(offlineApplicationIssueDTO);
			}
		}
		return list;
	}
}
