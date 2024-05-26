package com.christ.erp.services.handlers.employee.onboarding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Tuple;
import javax.validation.Valid;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistSubDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentVerificationDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentVerificationDetailsDBO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentVerificationDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentVerificationDetailsDTO;
import com.christ.erp.services.transactions.employee.onboarding.DocumentVerificationTransaction;

public class DocumentVerificationHandler {

	private static volatile DocumentVerificationHandler documentVerificationHandler = null;

	DocumentVerificationTransaction documentVerificationTransaction = DocumentVerificationTransaction.getInstance();

	public static DocumentVerificationHandler getInstance() {
		if(documentVerificationHandler==null) {
			documentVerificationHandler = new DocumentVerificationHandler();
		}
		return documentVerificationHandler;
	}

	public  EmpDocumentVerificationDTO editDocumentverifiedbyApplicationnumber(String id) throws Exception {
		List<Tuple>  authors=documentVerificationTransaction.editDocumentverifiedbyApplicationnumber(id);
		EmpDocumentVerificationDTO empDocumentVerificationDTO=null;
		for (Tuple a : authors) {
			if (empDocumentVerificationDTO==null) {
				empDocumentVerificationDTO=new EmpDocumentVerificationDTO();
				empDocumentVerificationDTO.id= !Utils.isNullOrEmpty( a.get("ParentId")) ?  a.get("ParentId").toString() : ""  ;  
				empDocumentVerificationDTO.applicantid=!Utils.isNullOrEmpty(a.get("ApplicantId")) ?  a.get("ApplicantId").toString() : ""  ;  
				empDocumentVerificationDTO.empcampus=!Utils.isNullOrEmpty( a.get("Campus")) ?  a.get("Campus").toString() : "" ;
				empDocumentVerificationDTO.empname=!Utils.isNullOrEmpty( a.get("ApplicantName")) ?  a.get("ApplicantName").toString() : ""  ;  
				empDocumentVerificationDTO.applicantnumber=id;
				empDocumentVerificationDTO.postapplied=!Utils.isNullOrEmpty(a.get("PostApplied")) ? a.get("PostApplied").toString() : ""  ; 
				empDocumentVerificationDTO.empcountry=!Utils.isNullOrEmpty(a.get("Country")) ? a.get("Country").toString() : ""  ;  
				empDocumentVerificationDTO.remarks=!Utils.isNullOrEmpty(a.get("WaitForRemarks")) ? a.get("WaitForRemarks").toString() : ""  ; 
				empDocumentVerificationDTO.waitfordocument= !Utils.isNullOrEmpty(a.get("WaitForDoc")) ? a.get("WaitForDoc").toString(): ""  ;  
				empDocumentVerificationDTO.isInDraftMode=!Utils.isNullOrEmpty(a.get("IsInDraftMode")) ? a.get("IsInDraftMode").toString() : ""  ; 
//				if (!Utils.isNullOrEmpty(a.get("SubmissionDueDate"))) {
//					empDocumentVerificationDTO.submissionduedate = a.get("SubmissionDueDate").toString();
//				}else {
//					empDocumentVerificationDTO.submissionduedate="";
//				}
				if (!Utils.isNullOrEmpty(a.get("SubmissionDueDate"))) {
					empDocumentVerificationDTO.setSubmissionduedate(Utils.convertStringDateToLocalDate(a.get("SubmissionDueDate").toString()));
				} else {
					empDocumentVerificationDTO.setSubmissionduedate(null);
				}
				empDocumentVerificationDTO.empDocumentVerificationDetailsDTO=new HashSet<EmpDocumentVerificationDetailsDTO>();	
			}
			EmpDocumentVerificationDetailsDTO empDocumentVerificationDetailsDTO=new EmpDocumentVerificationDetailsDTO();
			empDocumentVerificationDetailsDTO.chickListSubId= a.get("ChickList").toString(); 
			empDocumentVerificationDetailsDTO.id=a.get("ID").toString(); 
			empDocumentVerificationDetailsDTO.verifyStatus=!Utils.isNullOrEmpty( a.get("VerifyStatus")) ?  a.get("VerifyStatus").toString() : ""  ;   
			empDocumentVerificationDetailsDTO.verifyRemarks=!Utils.isNullOrEmpty(a.get("VerifyRemarks")) ? a.get("VerifyRemarks").toString() : ""  ;  
			empDocumentVerificationDTO.empDocumentVerificationDetailsDTO.add(empDocumentVerificationDetailsDTO);
		}
		return empDocumentVerificationDTO;
	}

	public  EmpDocumentVerificationDTO  editDocumentVerifiedbyEmployeeId(String id) throws Exception {
		EmpDocumentVerificationDTO empDocumentVerificationDTO=null;
		List<Tuple>  authors=documentVerificationTransaction.editDocumentVerifiedbyEmployeeId(id);
		for (Tuple a : authors) {
			if (empDocumentVerificationDTO==null) {
				empDocumentVerificationDTO=new EmpDocumentVerificationDTO();
				empDocumentVerificationDTO.id= !Utils.isNullOrEmpty( a.get("ParentId")) ?  a.get("ParentId").toString() : "" ;
				empDocumentVerificationDTO.employeeid=!Utils.isNullOrEmpty( a.get("EmpId")) ?  a.get("EmpId").toString() : "" ;
				empDocumentVerificationDTO.empcampus=!Utils.isNullOrEmpty( a.get("Campus")) ?  a.get("Campus").toString() : "" ;
				empDocumentVerificationDTO.empname=!Utils.isNullOrEmpty( a.get("EmpName")) ?  a.get("EmpName").toString() : "" ;
				empDocumentVerificationDTO.employeenumber=id;
				empDocumentVerificationDTO.postapplied=!Utils.isNullOrEmpty( a.get("PostApplied")) ?  a.get("PostApplied").toString() : "" ;
				empDocumentVerificationDTO.empcountry=!Utils.isNullOrEmpty(a.get("Country")) ? a.get("Country").toString() : "" ;
				empDocumentVerificationDTO.remarks=!Utils.isNullOrEmpty(a.get("WaitForRemarks")) ? a.get("WaitForRemarks").toString() : ""  ; 
				empDocumentVerificationDTO.waitfordocument=!Utils.isNullOrEmpty(a.get("WaitForDoc")) ? a.get("WaitForDoc").toString() : ""  ;
				empDocumentVerificationDTO.isInDraftMode=!Utils.isNullOrEmpty(a.get("IsInDraftMode")) ? a.get("IsInDraftMode").toString() : ""  ;  
			//	empDocumentVerificationDTO.submissionduedate=!Utils.isNullOrEmpty(a.get("SubmissionDueDate")) ? a.get("SubmissionDueDate").toString() : "";
				empDocumentVerificationDTO.setSubmissionduedate(!Utils.isNullOrEmpty(a.get("SubmissionDueDate")) ? Utils.convertStringDateToLocalDate(a.get("SubmissionDueDate").toString()): null);
				empDocumentVerificationDTO.empDocumentVerificationDetailsDTO=new HashSet<EmpDocumentVerificationDetailsDTO>();	
			}
			EmpDocumentVerificationDetailsDTO empDocumentVerificationDetailsDTO=new EmpDocumentVerificationDetailsDTO();
			empDocumentVerificationDetailsDTO.chickListSubId= a.get("ChickList").toString(); 
			empDocumentVerificationDetailsDTO.id=a.get("ID").toString(); 
			empDocumentVerificationDetailsDTO.verifyStatus= !Utils.isNullOrEmpty(a.get("VerifyStatus").toString()) ? a.get("VerifyStatus").toString() : ""  ;
			empDocumentVerificationDetailsDTO.verifyRemarks= !Utils.isNullOrEmpty(a.get("VerifyRemarks").toString()) ? a.get("VerifyRemarks").toString() : ""  ;
			empDocumentVerificationDTO.empDocumentVerificationDetailsDTO.add(empDocumentVerificationDetailsDTO);
		}
		return empDocumentVerificationDTO;
	}

	public  EmpDocumentVerificationDTO editDocumentverified(String id) throws Exception { 
		EmpDocumentVerificationDTO empDocumentVerificationDTO=null;
		List<Tuple> typlelist=	documentVerificationTransaction.editDocumentverified(id);
		for (Tuple a : typlelist) {
			if (empDocumentVerificationDTO==null) {
				empDocumentVerificationDTO = new EmpDocumentVerificationDTO();
				empDocumentVerificationDTO.id= !Utils.isNullOrEmpty( a.get("ParentId")) ?  a.get("ParentId").toString() : "" ;
				empDocumentVerificationDTO.employeeid=!Utils.isNullOrEmpty( a.get("EmpId")) ?  a.get("EmpId").toString() : "" ;
				empDocumentVerificationDTO.empcampus=!Utils.isNullOrEmpty( a.get("Campus")) ?  a.get("Campus").toString() : "" ;
				empDocumentVerificationDTO.empname=!Utils.isNullOrEmpty( a.get("EmpName")) ?  a.get("EmpName").toString() : "" ;
				empDocumentVerificationDTO.employeenumber=id;
				empDocumentVerificationDTO.postapplied=!Utils.isNullOrEmpty( a.get("PostApplied")) ?  a.get("PostApplied").toString() : "" ;
				empDocumentVerificationDTO.empcountry=!Utils.isNullOrEmpty(a.get("Country")) ? a.get("Country").toString() : "" ;
				empDocumentVerificationDTO.remarks=!Utils.isNullOrEmpty(a.get("WaitForRemarks")) ? a.get("WaitForRemarks").toString() : ""  ; 
				empDocumentVerificationDTO.waitfordocument=!Utils.isNullOrEmpty(a.get("WaitForDoc")) ? a.get("WaitForDoc").toString() : ""  ;
				empDocumentVerificationDTO.isInDraftMode=!Utils.isNullOrEmpty(a.get("IsInDraftMode")) ? a.get("IsInDraftMode").toString() : ""  ;  
				// empDocumentVerificationDTO.submissionduedate=!Utils.isNullOrEmpty(a.get("SubmissionDueDate")) ? a.get("SubmissionDueDate").toString() : "";
				empDocumentVerificationDTO.setSubmissionduedate(!Utils.isNullOrEmpty(a.get("SubmissionDueDate")) ? Utils.convertStringDateToLocalDate(a.get("SubmissionDueDate").toString()) :null);
				empDocumentVerificationDTO.empDocumentVerificationDetailsDTO=new HashSet<EmpDocumentVerificationDetailsDTO>();	
			}
			EmpDocumentVerificationDetailsDTO empDocumentVerificationDetailsDTO=new EmpDocumentVerificationDetailsDTO();
			empDocumentVerificationDetailsDTO.chickListSubId= a.get("ChickList").toString(); 
			empDocumentVerificationDetailsDTO.id=a.get("ID").toString(); 
			empDocumentVerificationDetailsDTO.verifyStatus= !Utils.isNullOrEmpty(a.get("VerifyStatus").toString()) ? a.get("VerifyStatus").toString() : ""  ;
			empDocumentVerificationDetailsDTO.verifyRemarks= !Utils.isNullOrEmpty(a.get("VerifyRemarks").toString()) ? a.get("VerifyRemarks").toString() : ""  ;
			empDocumentVerificationDTO.empDocumentVerificationDetailsDTO.add(empDocumentVerificationDetailsDTO);
		}
		return empDocumentVerificationDTO;
	}

	public  EmpDocumentVerificationDBO get(String id) throws Exception {
		return documentVerificationTransaction.get(id);
	}

	public Boolean saveOrUpdate(@Valid EmpDocumentVerificationDTO empDocumentVerificationDTO, String userId) throws Exception {
		EmpDocumentVerificationDBO empDocumentVerificationDBO=null;
		if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.id)) {
			empDocumentVerificationDBO=	documentVerificationTransaction.get(empDocumentVerificationDTO.id);
			if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.employeeid)) {
				empDocumentVerificationDBO.empDBO=new EmpDBO();
				empDocumentVerificationDBO.empDBO.id=Integer.parseInt(empDocumentVerificationDTO.employeeid);	
			}
			if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.applicantid)) {
				empDocumentVerificationDBO.empApplyEntryDBO=new EmpApplnEntriesDBO();
				empDocumentVerificationDBO.empApplyEntryDBO.id=Integer.parseInt(empDocumentVerificationDTO.applicantid);		
			}
			if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.waitfordocument)) {
				empDocumentVerificationDBO.waitForDocument=Boolean.valueOf(empDocumentVerificationDTO.waitfordocument);
			}
			if(!Utils.isNullOrEmpty(empDocumentVerificationDTO.isInDraftMode)) {
				empDocumentVerificationDBO.isInDraftMode=Boolean.valueOf(empDocumentVerificationDTO.isInDraftMode);
			}else {
				empDocumentVerificationDBO.isInDraftMode=false;
			}

			if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.getSubmissionduedate())) {
				empDocumentVerificationDBO.submissionDueDate = empDocumentVerificationDTO.getSubmissionduedate();
			} else {
				empDocumentVerificationDBO.submissionDueDate = null;
			}
			if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.remarks)) {
				empDocumentVerificationDBO.remarks=empDocumentVerificationDTO.remarks;
			} else {
				empDocumentVerificationDBO.remarks = null;
			}
			empDocumentVerificationDBO.empDocumentVerificationDetailsDBO=new HashSet<EmpDocumentVerificationDetailsDBO>();
			List<Integer> detailsId=new ArrayList<Integer>();
			for ( EmpDocumentVerificationDetailsDTO empDocumentVerificationDetailsDTO : empDocumentVerificationDTO.empDocumentVerificationDetailsDTO) {
				EmpDocumentVerificationDetailsDBO empDocumentVerificationDetailsDBO=null;
				if (Utils.isNullOrWhitespace(empDocumentVerificationDetailsDTO.id) == false) {
					empDocumentVerificationDetailsDBO = documentVerificationTransaction.getDocuemntVerificationDetails(empDocumentVerificationDetailsDTO.id);		
				}
				if (Utils.isNullOrEmpty(empDocumentVerificationDetailsDBO)) {
					empDocumentVerificationDetailsDBO = new EmpDocumentVerificationDetailsDBO();
					empDocumentVerificationDetailsDBO.createdUsersId = Integer.parseInt(userId);
					empDocumentVerificationDetailsDBO.recordStatus='A';
				}
				else {
					empDocumentVerificationDetailsDBO.modifiedUsersId=Integer.parseInt(userId);
				}
				detailsId.add(empDocumentVerificationDetailsDBO.id);
				empDocumentVerificationDetailsDBO.empDocumentChecklistSubDBO=new EmpDocumentChecklistSubDBO();
				empDocumentVerificationDetailsDBO.empDocumentChecklistSubDBO.id =Integer.parseInt(empDocumentVerificationDetailsDTO.chickListSubId);
				empDocumentVerificationDetailsDBO.verificationStatus=empDocumentVerificationDetailsDTO.verifyStatus;
				empDocumentVerificationDetailsDBO.verificationRemarks=empDocumentVerificationDetailsDTO.verifyRemarks;
				empDocumentVerificationDetailsDBO.empDocumentVerificationDBO=new EmpDocumentVerificationDBO();
				empDocumentVerificationDetailsDBO.empDocumentVerificationDBO.id=Integer.valueOf(empDocumentVerificationDTO.id);
				empDocumentVerificationDetailsDBO.recordStatus='A';
				empDocumentVerificationDBO.empDocumentVerificationDetailsDBO.add(empDocumentVerificationDetailsDBO);
			}
			empDocumentVerificationDBO.modifiedUsersId=Integer.parseInt(userId);
			Boolean isTrueorFalse=documentVerificationTransaction.saveOrUpdate(empDocumentVerificationDBO,detailsId,userId);
			return isTrueorFalse;
		}
		else {
			empDocumentVerificationDBO=new EmpDocumentVerificationDBO();
			empDocumentVerificationDBO.empApplyEntryDBO=new EmpApplnEntriesDBO();
			empDocumentVerificationDBO.empApplyEntryDBO.id=Integer.parseInt(empDocumentVerificationDTO.applicantid);			
			empDocumentVerificationDBO.waitForDocument=Boolean.valueOf(empDocumentVerificationDTO.waitfordocument);
			if(!Utils.isNullOrEmpty(empDocumentVerificationDTO.isInDraftMode)) {
				empDocumentVerificationDBO.isInDraftMode=Boolean.valueOf(empDocumentVerificationDTO.isInDraftMode);
			}else {
				empDocumentVerificationDBO.isInDraftMode=false;
			}
			if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.getSubmissionduedate())) {
				empDocumentVerificationDBO.submissionDueDate = empDocumentVerificationDTO.getSubmissionduedate();	
			} else {
				empDocumentVerificationDBO.submissionDueDate = null;
			}
			empDocumentVerificationDBO.empDocumentVerificationDetailsDBO=new HashSet<EmpDocumentVerificationDetailsDBO>();
			if (!Utils.isNullOrEmpty(empDocumentVerificationDTO.remarks)) {
				empDocumentVerificationDBO.remarks = empDocumentVerificationDTO.remarks;
			} else {
				empDocumentVerificationDBO.remarks = null;
			}
			empDocumentVerificationDBO.createdUsersId=Integer.parseInt(userId);
			empDocumentVerificationDBO.recordStatus='A';
			List<Integer> detailsId=new ArrayList<Integer>();
			for ( EmpDocumentVerificationDetailsDTO empDocumentVerificationDetailsDTO : empDocumentVerificationDTO.empDocumentVerificationDetailsDTO) {
				EmpDocumentVerificationDetailsDBO empDocumentVerificationDetailsDBO=new EmpDocumentVerificationDetailsDBO();
				empDocumentVerificationDetailsDBO.empDocumentChecklistSubDBO=new EmpDocumentChecklistSubDBO();
				empDocumentVerificationDetailsDBO.empDocumentChecklistSubDBO.id =Integer.parseInt(empDocumentVerificationDetailsDTO.chickListSubId);
				empDocumentVerificationDetailsDBO.verificationStatus=empDocumentVerificationDetailsDTO.verifyStatus;
				empDocumentVerificationDetailsDBO.verificationRemarks=empDocumentVerificationDetailsDTO.verifyRemarks;
				empDocumentVerificationDetailsDBO.createdUsersId=Integer.parseInt(userId);
				empDocumentVerificationDetailsDBO.empDocumentVerificationDBO=empDocumentVerificationDBO;
				empDocumentVerificationDetailsDBO.recordStatus='A';
				empDocumentVerificationDBO.empDocumentVerificationDetailsDBO.add(empDocumentVerificationDetailsDBO);
			}
			Boolean isTrueorFalse=documentVerificationTransaction.saveOrUpdate(empDocumentVerificationDBO, detailsId,userId);
			return isTrueorFalse;
		}  	
	}
}
