package com.christ.erp.services.dbqueries.employee;

public class OnBoardingQueries {
	/*--------- DocumentVerificationSettings ---------*/
	public static final String DOCUMENT_VERIFICATION_SETTINGS_GET_GRID_DATA = "from EmpDocumentChecklistMainDBO bo where bo.recordStatus='A' order by bo.isForeignNationalDocumentChecklist,bo.documentChecklistDisplayOrder asc" ;
	public static final String DOCUMENT_VERIFICATION_SETTINGS_DUPICATE_CHECK_CHECKLIST_MAIN = "select bo from EmpDocumentChecklistMainDBO bo where bo.recordStatus='A' and bo.id!=:headingId";
	public static final String DOCUMENT_VERIFICATION_SETTINGS_DUPLICATE_CHECKLIST_SUB = "select bo from EmpDocumentChecklistSubDBO bo where bo.recordStatus='A' ";
	
	/*--------- DocumentVerification ---------*/
	public static final String EMP_DOCUMENT_VERIFICATION_DETAIL_BY_APPLICANT_NUMBER="select  ea.emp_appln_entries_id as ApplicantId, ea.applicant_name as ApplicantName , ecat.employee_category_name as PostApplied ,con.country_name as Country,ec.campus_name as Campus, edoc.wait_for_document as WaitForDoc,edoc.is_in_draft_mode as IsInDraftMode,edoc.wait_remarks as WaitForRemarks, edoc.submission_due_date as SubmissionDueDate, e1.emp_document_checklist_sub_id as ChickList, e1.verification_status as VerifyStatus, e1.verification_remarks as VerifyRemarks, e1.emp_document_verification_details_id as ID, edoc.emp_document_verification_id as ParentId  from emp_document_verification edoc inner join emp_document_verification_details e1 on e1.emp_document_verification_id=edoc.emp_document_verification_id " + 
			"			inner join emp_appln_entries ea on ea.emp_appln_entries_id=edoc.emp_appln_entries_id " + 
			"			inner join emp_employee_category ecat on ecat.emp_employee_category_id=ea.emp_employee_category_id " + 
			"			inner join emp_appln_personal_data per on per.emp_appln_entries_id=ea.emp_appln_entries_id " + 
			"			inner join erp_campus ec on ec.erp_campus_id =ea.erp_campus_id  " + 
			"			inner join erp_country con on  per.erp_country_id=con.erp_country_id where ea.application_no=:applicationNumber and e1.record_status='A'";
	public static final String EMP_DOCUMENT_VERIFICATION_DETAIL_DELETE_DETAIL=" UPDATE emp_document_verification_details SET record_status='D' WHERE emp_document_verification_id = :heading_id AND emp_document_verification_details_id NOT IN (:detail_ids)";
	public static final String EMP_DOCUMENT_VERIFICATION_DETAIL_BY_EMPLOYEE_NUMBER="select e.emp_id as EmpId, e.emp_name as EmpName, ecat.employee_category_name as PostApplied,con.country_name as Country,ec.campus_name as Campus,edoc.wait_for_document as WaitForDoc,edoc.is_in_draft_mode as IsInDraftMode,edoc.wait_remarks as WaitForRemarks, edoc.submission_due_date as SubmissionDueDate, e1.emp_document_checklist_sub_id as ChickList, e1.verification_status as VerifyStatus, e1.verification_remarks as VerifyRemarks, e1.emp_document_verification_details_id as ID, edoc.emp_document_verification_id as ParentId from emp_document_verification edoc \r\n" + 
			"inner join emp_document_verification_details e1 on e1.emp_document_verification_id=edoc.emp_document_verification_id \r\n" + 
			"inner join emp_appln_entries ea on ea.emp_appln_entries_id=edoc.emp_appln_entries_id \r\n" + 
			"inner join emp e on ea.emp_appln_entries_id=e.emp_appln_entries_id \r\n" + 
			"inner join emp_employee_category ecat on ecat.emp_employee_category_id=e.emp_employee_category_id \r\n" + 
			"inner join erp_campus_department_mapping ecdd on ecdd.erp_campus_department_mapping_id=e.erp_campus_department_mapping_id \r\n" + 
			"inner join erp_campus ec on ec.erp_campus_id =ecdd.erp_campus_id \r\n" + 
			"inner join emp_personal_data per on per.emp_personal_data_id=e.emp_personal_data_id \r\n" + 
			"inner join erp_country con on  per.erp_country_id=con.erp_country_id where e.emp_no=:empnumber and e1.record_status='A' and edoc.is_in_draft_mode=false";
	public static final String EMP_DOCUMENT_VERIFICATION_DETAIL_BY_EMPLOYEE_ID="select e1.emp_id as EmpId, e1.emp_name as EmpName, ecat.employee_category_name as PostApplied,con.country_name as Country,ec.campus_name as Campus,e.wait_for_document as WaitForDoc,e.is_in_draft_mode as IsInDraftMode,e.wait_remarks as WaitForRemarks, e.submission_due_date as SubmissionDueDate, e2.emp_document_checklist_sub_id as ChickList, e2.verification_status as VerifyStatus, e2.verification_remarks as VerifyRemarks, e2.emp_document_verification_details_id as ID, e.emp_document_verification_id as ParentId from emp_document_verification e inner join emp e1 on e1.emp_id=e.emp_id\r\n" + 
			"inner join emp_document_verification_details e2 on e2.emp_document_verification_id=e.emp_document_verification_id\r\n" + 
			"inner join emp_employee_category ecat on ecat.emp_employee_category_id=e1.emp_employee_category_id\r\n" + 
			"inner join erp_campus_department_mapping ecdd on ecdd.erp_campus_department_mapping_id=e1.erp_campus_department_mapping_id \r\n" + 
			"inner join erp_campus ec on ec.erp_campus_id =ecdd.erp_campus_id \r\n" + 
			"inner join emp_personal_data per on per.emp_personal_data_id=e1.emp_personal_data_id \r\n" + 
			"inner join erp_country con on  per.erp_country_id=con.erp_country_id  where e1.emp_no=:empnumber and e2.record_status='A' and e.is_in_draft_mode=false";
}
