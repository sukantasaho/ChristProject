package com.christ.erp.services.transactions.employee.recruitment;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewSchedulesDBO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

import reactor.core.publisher.Mono;

@Repository
public class InterviewProcessTransaction {

	private static volatile InterviewProcessTransaction shortlistingOfApplicationTransaction = null;

	public static InterviewProcessTransaction getInstance() {
		if(shortlistingOfApplicationTransaction==null) {
			shortlistingOfApplicationTransaction = new InterviewProcessTransaction();
		}
		return  shortlistingOfApplicationTransaction;
	}

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	//	public List<Tuple> getApplicationEntries(String campusDepartmentId,String stage,String userId) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
	//			@SuppressWarnings("unchecked")
	//			@Override                                                             
	//			public List<Tuple> onRun(EntityManager context) throws Exception {
	//				String queryString = "select  ecdm.erp_department_id,eae.emp_appln_entries_id, eae.applicant_name, eae.application_no,  "
	//						+ "	DATE(eae.submission_date_time) as submission_date ,IF(ewfp.process_code like '%rescheduled%', 'Rescheduled', 'Scheduled') status,  "
	//						+ "	easc.subject_category_name,erp_department.department_name,erp_campus.campus_name,erp_campus.erp_campus_id,   "
	//						+ "	GROUP_CONCAT(DISTINCT eascs.subject_category_specialization_name) as subject_category_specialization_name,  "
	//						+ "	eapd.orcid_no,GROUP_CONCAT(DISTINCT el.location_name) as location_name,eql.qualification_level_name,eae.application_current_process_status,eec.employee_category_name ,  "
	//						+ "	eae.is_contacted_by_hod,eae.is_shortlisted_for_interview,ewfp.process_code,eae.job_rejection_reason,  "
	//						+ "	IF(ewfp.process_code like '%EMP_JOB_OFFER_REJECTED%', 'Rejected', 'Accepted') offer_status,eais.interview_date_time,"
	//						+ " eae.offer_letter_generated_date as offerLetterdate,"
	//						+ " eae.regret_letter_generated_date as regretLetterDate  "
	//						+ "	from  emp_appln_entries as eae    "
	//						+ "	inner join emp_appln_subj_specialization_pref on emp_appln_subj_specialization_pref.emp_appln_entries_id = eae.emp_appln_entries_id   "
	//						+ "	and emp_appln_subj_specialization_pref.record_status like 'A'  "
	//						+ "	inner join emp_appln_subject_category as easc on easc.emp_appln_subject_category_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_id and easc.record_status = 'A' "
	//						+ "	left join emp_appln_subject_category_specialization as eascs on eascs.emp_appln_subject_category_specialization_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_specialization_id and eascs.record_status = 'A' "
	//						+ "	inner join erp_campus_department_mapping as ecdm on ecdm.erp_department_id=eae.erp_campus_department_mapping_id and ecdm.record_status = 'A' "
	//						+ "	inner join erp_department on ecdm.erp_department_id = erp_department.erp_department_id and erp_department.record_status = 'A' and "
	//						+ " if(:campusDepartmentId is not null,ecdm.erp_department_id in   "
	//						+ "	  	((select DISTINCT  ed.erp_department_id "
	//						+ "     from erp_campus_department_user_title ecdu    "
	//						+ "	  	inner join erp_campus_department_mapping ecdm on ecdm.erp_campus_department_mapping_id = ecdu.erp_campus_department_mapping_id    "
	//						+ "	  	inner join erp_department ed on ed.erp_department_id=ecdm.erp_department_id and ed.record_status='A' "
	//						+ "	  	where ecdu.erp_users_id=:userId and ecdu.record_status = 'A' )),1=1)    "
	//						+ "	inner join erp_campus on erp_campus.erp_campus_id=ecdm.erp_campus_id and erp_campus.record_status = 'A' "
	//						+ "	inner join emp_employee_category as eec on eec.emp_employee_category_id = eae.emp_employee_category_id and eec.record_status = 'A'  "
	//						+ "	inner join emp_appln_personal_data as eapd on eapd.emp_appln_entries_id = eae.emp_appln_entries_id and eapd.record_status = 'A'  "
	//						+ "	left join erp_qualification_level as eql on eql.erp_qualification_level_id = eae.highest_qualification_level and eql.record_status = 'A'   "
	//						+ "	left join emp_appln_location_pref as ealp on ealp.emp_appln_entries_id = eae.emp_appln_entries_id and ealp.record_status like 'A'   "
	//						+ "	left join erp_location as el on ealp.erp_location_id=el.erp_location_id and el.record_status = 'A' "
	//						+ "	left join erp_work_flow_process as ewfp on eae.application_current_process_status=ewfp.erp_work_flow_process_id and ewfp.record_status = 'A'"
	//						+ "	inner join emp_appln_interview_schedules eais on eais.emp_appln_entries_id = eae.emp_appln_entries_id   "
	//						+ "	where eae.submission_date_time > DATE_SUB(now(), INTERVAL 6 MONTH) and eae.record_status = 'A' ";
	//				if(stage.equals("interviewedRoundOne")) {
	//					queryString += " and ewfp.process_code='EMP_R1_INTERVIEW_COMPLETED' and eais.interview_round=1 ";
	//				}
	//				else if(stage.equals("shortlistedToRoundTwo")) {
	//					queryString += " and ewfp.process_code='EMP_FORWARDED_BY_HOD' or ewfp.process_code='EMP_R2_APPLICANT_REQUEST_RESCHEDULE'";
	//				}
	//				else if(stage.equals("selected")) {
	//					queryString += " and ewfp.process_code='EMP_SELECTED' and eais.interview_round=2 ";
	//				}
	//				else if(stage.equals("rejected")) {
	//					queryString += " and ewfp.process_code='R2_NOT_SELECTED' and eais.interview_round=2 ";
	//				}
	//				else {
	//					queryString += " and (ewfp.process_code='EMP_JOB_OFFER_ACCEPTED' or ewfp.process_code='EMP_JOB_OFFER_REJECTED') and eais.interview_round=2 ";
	//				}
	//				queryString		+= " group by eae.emp_appln_entries_id,ecdm.erp_department_id,erp_campus.erp_campus_id, easc.subject_category_name,erp_department.department_name, "
	//						+ " erp_campus.campus_name,eapd.emp_appln_personal_data_id,eais.interview_date_time ";
	//				Query query = context.createNativeQuery(queryString, Tuple.class);
	//				query.setParameter("campusDepartmentId", campusDepartmentId);
	//				query.setParameter("userId", Integer.parseInt(userId));
	//				return query.getResultList();
	//			}
	//			@Override
	//			public void onError(Exception error) throws Exception {
	//				throw error;
	//			}
	//		});
	//	}

	public List<Tuple> getApplicationsSubmitted(String departmentId,String locationId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String queryString = " SELECT GROUP_CONCAT(DISTINCT eascd.erp_department_id) AS erp_department_id,"
						+ " eae.emp_appln_entries_id,"
						+ " eae.applicant_name,"
						+ " eae.application_no,"
						+ " DATE(eae.submission_date_time) AS submission_date,"
						+ " IF(ewfp.process_code LIKE '%rescheduled%', 'Rescheduled', 'Scheduled') status,"
						+ " GROUP_CONCAT(DISTINCT easc.subject_category_name) AS subject_category_name,"
						+ " GROUP_CONCAT(DISTINCT erp_department.department_name) AS department_name,"
						+ " GROUP_CONCAT(DISTINCT eascs.subject_category_specialization_name) AS subject_category_specialization_name,"
						+ " eapd.orcid_no,"
						+ " GROUP_CONCAT(DISTINCT el.location_name, '') AS location_name,"
						+ " eql.qualification_level_name,"
						+ " eae.application_current_process_status,"
						+ " eec.employee_category_name,"
						+ " eae.is_shortlisted_for_interview,"
						+ " ewfp.process_code,"
						+ " eae.total_previous_experience_years AS total_previous_experience_years,"
						+ " eae.total_previous_experience_months AS total_previous_experience_months,"
						+ " url.file_name_unique, url.file_name_original, folder.upload_process_code,"
						+ "  erp_gender.gender_name"
						+ " FROM emp_appln_entries AS eae"
						+ " INNER JOIN emp_appln_subj_specialization_pref ON  emp_appln_subj_specialization_pref.emp_appln_entries_id = eae.emp_appln_entries_id AND emp_appln_subj_specialization_pref.record_status = 'A'"
						+ " INNER JOIN emp_appln_subject_category AS easc ON  easc.emp_appln_subject_category_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_id AND easc.record_status = 'A'"
						+ " LEFT JOIN emp_appln_subject_category_specialization AS eascs ON  eascs.emp_appln_subject_category_specialization_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_specialization_id AND eascs.record_status = 'A'"
//						+ " INNER JOIN emp_appln_subject_category_department AS eascd ON  eascd.emp_appln_subject_category_id = easc.emp_appln_subject_category_id AND eascd.record_status = 'A' AND if(:departmentId IS NOT NULL AND :departmentId != '', eascd.erp_department_id IN (:departmentId), 1 = 1)"
//						+ " INNER JOIN erp_department ON  eascd.erp_department_id = erp_department.erp_department_id AND erp_department.record_status = 'A'"
						+ " LEFT JOIN emp_appln_subject_category_department AS eascd ON  eascd.emp_appln_subject_category_id = easc.emp_appln_subject_category_id AND eascd.record_status = 'A' "
						+ " LEFT JOIN erp_department ON  eascd.erp_department_id = erp_department.erp_department_id AND erp_department.record_status = 'A' "
						+ " INNER JOIN emp_employee_category AS eec ON  eec.emp_employee_category_id = eae.emp_employee_category_id AND eec.record_status = 'A'"
						+ " INNER JOIN emp_appln_personal_data AS eapd ON  eapd.emp_appln_entries_id = eae.emp_appln_entries_id AND eapd.record_status = 'A'"
						+ " LEFT JOIN erp_qualification_level AS eql ON  eql.erp_qualification_level_id = eae.highest_qualification_level AND eql.record_status = 'A'"
						+ " INNER JOIN emp_appln_location_pref AS ealp ON  ealp.emp_appln_entries_id = eae.emp_appln_entries_id AND ealp.record_status = 'A' AND if(:locationId IS NOT NULL AND :locationId != '', ealp.erp_location_id IN (:locationId), 1 = 1)"
						+ " INNER JOIN erp_location AS el ON  ealp.erp_location_id = el.erp_location_id AND el.record_status = 'A'"
						+ " LEFT JOIN erp_work_flow_process AS ewfp ON  eae.application_current_process_status = ewfp.erp_work_flow_process_id AND ewfp.record_status = 'A'"
//						+ " INNER JOIN erp_location ON  ealp.erp_location_id = erp_location.erp_location_id AND erp_location.record_status = 'A'"
						+ " LEFT JOIN url_access_link url ON eapd.profile_photo_url_id = url.url_access_link_id"
						+ " LEFT JOIN url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
						+ " LEFT JOIN erp_gender ON erp_gender.erp_gender_id = eae.erp_gender_id "
						+ " WHERE  eae.submission_date_time > DATE_SUB(now(), INTERVAL 6 MONTH)"
						+ " AND ewfp.process_code = 'EMP_APPLICATION_SUBMITTED'"
						+ " AND eae.record_status = 'A'"
						+ " AND (eascd.emp_appln_subject_category_id is null or if(:departmentId IS NOT NULL AND :departmentId != '', eascd.erp_department_id IN (:departmentId), 1 = 1))"
						+ " GROUP BY eae.emp_appln_entries_id,eapd.emp_appln_personal_data_id";
				Query query = context.createNativeQuery(queryString, Tuple.class);
				query.setParameter("departmentId", departmentId);
				query.setParameter("locationId", locationId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getApplicants(EmpApplnEntriesDTO empApplnEntriesDTO,List<Integer> idList) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String query = "SELECT DISTINCT "
						+ " GROUP_CONCAT(DISTINCT eascd.erp_department_id) AS erp_department_id,"
						+ " eae.emp_appln_entries_id as Id,"
						+ " eae.applicant_name as name,"
						+ " eae.application_no as applicationNumber,"
						+ " DATE(eae.submission_date_time) AS submissionDate,"
						+ " IF(ewfp.process_code LIKE '%rescheduled%', 'Rescheduled', 'Scheduled') AS status,"
						+ " GROUP_CONCAT(DISTINCT easc.subject_category_name) AS subjectCategoryName,"
						+ " GROUP_CONCAT(DISTINCT easc.emp_appln_subject_category_id) AS subjectCategoryId,"
						+ " GROUP_CONCAT(DISTINCT erp_department.department_name) AS departmentName,"
						+ " GROUP_CONCAT(DISTINCT eascs.subject_category_specialization_name) AS subjectCategorySpecializationName,"
						+ " GROUP_CONCAT(DISTINCT el.location_name) AS location_name,"
						+ " eapd.orcid_no as orchidNo,"
						+ " eql.qualification_level_name as qualificationLevelName,"
						+ " eae.application_current_process_status as applicationProcess,"
						+ " eec.employee_category_name as employeeCategory,"
						+ " eql.erp_qualification_level_id AS qualificationId,"
						+ " ewfp.process_code processcode,"
						+ " eae.total_previous_experience_years AS totalPreviousExperienceYears,"
						+ " eae.total_previous_experience_months AS totalPreviousExperienceMonths,"
						+ " emp_eligibility_exam_list.emp_eligibility_exam_list_id AS ecamId,"
						+ " emp_eligibility_exam_list.eligibility_exam_name AS examName,"
						+ " emp_appln_addtnl_info_entries.emp_appln_addtnl_info_parameter_id as reserchId,"
						+ " emp_appln_addtnl_info_entries.addtnl_info_value as userEnteredValue, "
						+ " url.file_name_unique, url.file_name_original, folder.upload_process_code "
						+ " FROM emp_appln_entries AS eae"
						+ " INNER JOIN emp_appln_subj_specialization_pref ON emp_appln_subj_specialization_pref.emp_appln_entries_id = eae.emp_appln_entries_id AND emp_appln_subj_specialization_pref.record_status = 'A'"
						+ " INNER JOIN emp_appln_subject_category AS easc ON easc.emp_appln_subject_category_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_id AND easc.record_status = 'A'"
						+ " LEFT JOIN emp_appln_subject_category_specialization AS eascs ON eascs.emp_appln_subject_category_specialization_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_specialization_id AND eascs.record_status = 'A'"
//						+ " INNER JOIN emp_appln_subject_category_department AS eascd ON eascd.emp_appln_subject_category_id = easc.emp_appln_subject_category_id AND eascd.record_status = 'A'"
//						+ " AND IF(:departmentId IS NOT NULL AND :departmentId != '', eascd.erp_department_id IN (:departmentId), 1 = 1)"
//						+ " INNER JOIN erp_department ON eascd.erp_department_id = erp_department.erp_department_id AND erp_department.record_status = 'A'"
						+ " LEFT JOIN emp_appln_subject_category_department AS eascd ON  eascd.emp_appln_subject_category_id = easc.emp_appln_subject_category_id AND eascd.record_status = 'A' "
						+ " LEFT JOIN erp_department ON  eascd.erp_department_id = erp_department.erp_department_id AND erp_department.record_status = 'A' "
						+ " INNER JOIN emp_employee_category AS eec ON eec.emp_employee_category_id = eae.emp_employee_category_id AND eec.record_status = 'A'"
						+ " INNER JOIN emp_appln_personal_data AS eapd ON eapd.emp_appln_entries_id = eae.emp_appln_entries_id AND eapd.record_status = 'A'"
						+ " LEFT JOIN erp_qualification_level AS eql ON eql.erp_qualification_level_id = eae.highest_qualification_level AND eql.record_status = 'A'"
						+ " INNER JOIN emp_appln_location_pref AS ealp ON ealp.emp_appln_entries_id = eae.emp_appln_entries_id AND ealp.record_status = 'A'"
					//	+ " AND IF(:locationId IS NOT NULL AND :locationId != '', ealp.erp_location_id IN (:locationId), 1 = 1)"
						+ " INNER JOIN erp_location AS el ON ealp.erp_location_id = el.erp_location_id AND el.record_status = 'A'"
						+ " LEFT JOIN erp_work_flow_process AS ewfp ON eae.application_current_process_status = ewfp.erp_work_flow_process_id AND ewfp.record_status = 'A'"
//						+ " INNER JOIN erp_location ON ealp.erp_location_id = erp_location.erp_location_id AND erp_location.record_status = 'A'"
						+ " LEFT JOIN emp_appln_eligibility_test ON eae.emp_appln_entries_id = emp_appln_eligibility_test.emp_appln_entries_id AND emp_appln_eligibility_test.record_status = 'A'"
						+ " LEFT JOIN emp_eligibility_exam_list ON emp_appln_eligibility_test.emp_eligibility_exam_list_id = emp_eligibility_exam_list.emp_eligibility_exam_list_id AND emp_eligibility_exam_list.record_status = 'A'"
						+ " LEFT JOIN emp_appln_addtnl_info_entries ON emp_appln_addtnl_info_entries.emp_appln_entries_id = eae.emp_appln_entries_id and emp_appln_addtnl_info_entries.record_status = 'A'"
						+ " LEFT JOIN emp_appln_addtnl_info_parameter ON emp_appln_addtnl_info_parameter.emp_appln_addtnl_info_parameter_id = emp_appln_addtnl_info_entries.emp_appln_addtnl_info_parameter_id and emp_appln_addtnl_info_parameter.record_status = 'A'"
						+ " LEFT JOIN url_access_link url ON eapd.profile_photo_url_id = url.url_access_link_id"
						+ " LEFT JOIN url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
						+ " WHERE eae.record_status = 'A'"
						+ " AND ewfp.process_code = 'EMP_APPLICATION_SUBMITTED' ";
					//	+ " AND (eascd.emp_appln_subject_category_id is null or if(:departmentId IS NOT NULL AND :departmentId != '', eascd.erp_department_id IN (:departmentId), 1 = 1))";

				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getFromDate())) {
					query +=  " AND DATE(eae.submission_date_time) >= :fromDate  ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getToDate())) {
					query += " AND DATE(eae.submission_date_time) <= :toDate  ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getApplicantNumber())) {
					query += "AND eae.application_no LIKE CONCAT( :applicantNumber, '%') ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getApplicantName())) {
					query += " AND eae.applicant_name LIKE CONCAT( :applicantName, '%') ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getSubjectCategoryId())) {
					query += " AND easc.emp_appln_subject_category_id = :subjectCategoryId ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getHighestQualificationLevel())) {
					query += " AND eql.erp_qualification_level_id = :highestQualificationLevel ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getTotalFullTimeExperience())) {
					query += " AND eae.total_previous_experience_years >=:totalFullTimeExperience ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getEligibilityId())) {
					query += " AND emp_eligibility_exam_list.emp_eligibility_exam_list_id = :eligibilityId ";
				}
				if(!Utils.isNullOrEmpty(idList)) {
					query += " AND emp_appln_addtnl_info_entries.emp_appln_addtnl_info_parameter_id IN (:idList) ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getSubjectCategorySpecializationName())) {
					query += " AND eascs.emp_appln_subject_category_specialization_id = :subjectCategorySpecializationName  ";
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getCategory())) {
					if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getCategory().id)) {
						query += " AND  eec.emp_employee_category_id = :empCategoryId";
					}
				}
				query +=  " GROUP BY eae.emp_appln_entries_id, eapd.emp_appln_personal_data_id, emp_eligibility_exam_list.emp_eligibility_exam_list_id,emp_appln_addtnl_info_entries.emp_appln_addtnl_info_entries_id  ";

				Query q = context.createNativeQuery(query, Tuple.class);
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getFromDate())) {
					q.setParameter("fromDate", empApplnEntriesDTO.getFromDate().toLocalDate());
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getToDate())) {
					q.setParameter("toDate", empApplnEntriesDTO.getToDate().toLocalDate());
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getApplicantNumber())) {
					q.setParameter("applicantNumber", Integer.parseInt(empApplnEntriesDTO.getApplicantNumber()));
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getApplicantName())) {
					q.setParameter("applicantName", empApplnEntriesDTO.getApplicantName());
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getSubjectCategoryId())) {
					q.setParameter("subjectCategoryId", Integer.parseInt(empApplnEntriesDTO.getSubjectCategoryId()));
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getHighestQualificationLevel())) {
					q.setParameter("highestQualificationLevel", Integer.parseInt(empApplnEntriesDTO.getHighestQualificationLevel()));
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getTotalFullTimeExperience())) {
					q.setParameter("totalFullTimeExperience", Integer.parseInt(empApplnEntriesDTO.getTotalFullTimeExperience()));
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getEligibilityId())) {
					q.setParameter("eligibilityId", Integer.parseInt(empApplnEntriesDTO.getEligibilityId()));
				}
				if(!Utils.isNullOrEmpty(idList)) {
					q.setParameter("idList", idList);
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getSubjectCategorySpecializationName())) {
					q.setParameter("subjectCategorySpecializationName", Integer.parseInt(empApplnEntriesDTO.getSubjectCategorySpecializationName()));
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getCategory().id)) {
					q.setParameter("empCategoryId", Integer.parseInt(empApplnEntriesDTO.getCategory().id));
				}
//				q.setParameter("departmentId", !Utils.isNullOrEmpty(departmentId) ? departmentId : null);
//				q.setParameter("locationId", !Utils.isNullOrEmpty(locationId) ?  locationId : null);
				return q.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveData(EmpApplnEntriesDBO empApplnEntriesDBO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(Utils.isNullOrEmpty(empApplnEntriesDBO) || Utils.isNullOrEmpty(empApplnEntriesDBO.id) || empApplnEntriesDBO.id==0) {
					context.persist(empApplnEntriesDBO);
				}
				else {
					context.merge(empApplnEntriesDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	//	public List<Tuple> getUserId(String applicantId,String round) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
	//			@SuppressWarnings("unchecked")
	//			@Override
	//			public List<Tuple> onRun(EntityManager context) throws Exception {
	//				String query = " select eaip.erp_users_id  "
	//						+ " from emp_appln_interview_panel as eaip "
	//						+ " inner join emp_appln_interview_schedules as eais on eais.emp_appln_interview_schedules_id = eaip.emp_appln_interview_schedules_id and eais.record_status = 'A' "
	//						+ " and eais.emp_appln_entries_id=:applicantId and eais.interview_round=:round "
	//						+ " where eaip.record_status = 'A' and eaip.is_internal_panel=1 limit 1 ";
	//				Query q = context.createNativeQuery(query, Tuple.class);
	//				q.setParameter("applicantId", Integer.parseInt(applicantId));
	//				q.setParameter("round", Integer.parseInt(round));
	//				return q.getResultList();
	//			}
	//			@Override
	//			public void onError(Exception error) throws Exception {
	//				throw error;
	//			}
	//		});
	//	}

	//	public List<Tuple> getSchedule(String applicantId) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
	//			@SuppressWarnings("unchecked")
	//			@Override
	//			public List<Tuple> onRun(EntityManager context) throws Exception {
	//				String query = "  select  distinct eais.interview_date_time,eais.emp_appln_interview_schedules_id, "
	//						+ " eais.point_of_contact_users_id,eais.interview_venue, eaip.erp_users_id,eaip.emp_interview_university_externals_id,eaip.is_internal_panel "
	//						+ " from  emp_appln_interview_schedules as eais "
	//						+ " inner join emp_appln_interview_panel as eaip on eaip.emp_appln_interview_schedules_id = eais.emp_appln_interview_schedules_id and eaip.record_status = 'A' "
	//						+ " where   eais.emp_appln_entries_id=:applicantId and eais.record_status like 'A' ";                                       
	//				Query q = context.createNativeQuery(query, Tuple.class);
	//				q.setParameter("applicantId", applicantId);
	//				return q.getResultList();
	//			}
	//			@Override
	//			public void onError(Exception error) throws Exception {
	//				throw error;
	//			}
	//		});
	//	}

	//	public List<Tuple> getInterviewScore(String applicantId,int round) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
	//			@SuppressWarnings("unchecked")
	//			@Override
	//			public List<Tuple> onRun(EntityManager context) throws Exception {
	//				String queryString = "select eais.comments,eaisd.score_entered,eaitgd.parameter_name,eaitgd.parameter_max_score,eas.interview_date_time,   "
	//						+ "	 eaitg.template_group_heading,eaitg.emp_appln_interview_template_group_id,eiue.panelist_name,emp.emp_name   "
	//						+ "	 from  emp_appln_interview_score as eais   "
	//						+ "	 inner join emp_appln_interview_score_details as eaisd on eaisd.emp_appln_interview_score_id=eais.emp_appln_interview_score_id  and eaisd.record_status ='A'  "
	//						+ "	 inner join emp_appln_interview_template_group_details as eaitgd on eaitgd.emp_appln_interview_template_group_details_id=eaisd.emp_appln_interview_template_group_details_id and eaitgd.record_status = 'A'  "
	//						+ "	 inner join emp_appln_interview_template_group as eaitg on eaitg.emp_appln_interview_template_group_id=eaitgd.emp_appln_interview_template_group_id and eaitg.record_status = 'A'   "
	//						+ "	 inner join emp_appln_interview_schedules as eas on eas.emp_appln_entries_id=:applicantId and eas.record_status = 'A'     "
	//						+ "	 left join emp_interview_university_externals as eiue on eiue.emp_interview_university_externals_id=eais.emp_interview_university_externals_id and eiue.record_status = 'A'  "
	//						+ "	 left join erp_users as eu on eu.erp_users_id=eais.erp_users_id and eu.record_status ='A'    "
	//						+ "	 left join emp on emp.emp_id=eu.emp_id and emp.record_status = 'A'   "
	//						+ "  where eais.emp_appln_entries_id=:applicantId   "
	//						+ "	 and eas.interview_round=:round and   eais.record_status like 'A'  "
	//						+ "  order by eais.emp_appln_interview_score_id";
	//				Query query = context.createNativeQuery(queryString, Tuple.class);
	//				query.setParameter("round", round);
	//				query.setParameter("applicantId", Integer.parseInt(applicantId));
	//				return query.getResultList();
	//			}
	//			@Override
	//			public void onError(Exception error) throws Exception {
	//				throw error;
	//			}
	//		});
	//	}

	public List<Tuple> getInternalInterviewPanelists(String userId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception { 
				String queryString = "select emp_interview_panelist.internal_erp_users_id as ID,ifnull(emp_name,erp_users.user_name) as Text"
						+ " from emp_interview_panelist  "
						+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.internal_erp_users_id  "
						+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id  "
						+ " inner join (select "
						+ " max(erp_academic_year.academic_year) as academic_year,"
						+ " ifnull(emp_campus_map.erp_department_id,user_campus_map.erp_department_id) as erp_department_id,"
						+ " ifnull(emp_loc.erp_location_id,user_loc.erp_location_id) as erp_location_id"
						+ " from emp_interview_panelist"
						+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.internal_erp_users_id"
						+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
						+ " left join emp ON emp.emp_id = erp_users.emp_id"
						+ " and emp.record_status='A'"
						+ " left join erp_campus_department_mapping  as emp_campus_map ON emp_campus_map.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id "
						+ " and emp_campus_map.record_status='A'"
						+ " left join erp_campus_department_mapping as user_campus_map ON user_campus_map.erp_campus_department_mapping_id = erp_users.erp_campus_department_mapping_id"
						+ " and user_campus_map.record_status='A'"
						+ " left join erp_campus as emp_campus ON emp_campus.erp_campus_id = emp_campus_map.erp_campus_id  "
						+ " and emp_campus.record_status='A'"
						+ " left join erp_location as emp_loc ON emp_loc.erp_location_id = emp_campus.erp_location_id "
						+ " and emp_loc.record_status='A' "
						+ " left join erp_campus as user_campus ON user_campus.erp_campus_id = user_campus_map.erp_campus_id  "
						+ " and user_campus.record_status='A'"
						+ " left join erp_location as user_loc ON user_loc.erp_location_id = user_campus.erp_location_id "
						+ " and user_loc.record_status='A' "
						+ " where erp_users.erp_users_id=:userId"
						+ " and erp_users.record_status='A' "
						+ " ) as internal_erp_users ON internal_erp_users.erp_department_id = emp_interview_panelist.erp_department_id"
						+ " and internal_erp_users.academic_year = erp_academic_year.academic_year"
						+ " and emp_interview_panelist.erp_location_id=internal_erp_users.erp_location_id"
						+ " left join emp on emp.emp_id=erp_users.emp_id "
						+ " group by emp_interview_panelist.internal_erp_users_id;";
				Query query = context.createNativeQuery(queryString, Tuple.class);				
				query.setParameter("userId", userId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getExternalInterviewPanelists(String userId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception { 
				String queryString = "select  emp_interview_panelist.external_erp_users_id as ID,emp_name as Text,true as status,max(erp_academic_year.academic_year) from emp_interview_panelist "
						+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.external_erp_users_id "
						+ " inner join emp on emp.emp_id=erp_users.emp_id inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id "
						+ " where emp_interview_panelist.erp_department_id=(select erp_campus_department_mapping.erp_department_id from erp_users "
						+ " inner join emp ON emp.emp_id = erp_users.emp_id inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id "
						+ " where erp_campus_department_mapping.record_status='A' and erp_users.record_status='A' and emp.record_status='A' and erp_users.erp_users_id=:userId) "
						+ " and emp_interview_panelist.erp_location_id=(select erp_location.erp_location_id from erp_users "
						+ " inner join emp ON emp.emp_id = erp_users.emp_id "
						+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id "
						+ " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
						+ " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id "
						+ " where erp_users.record_status='A' and emp.record_status='A' and erp_campus_department_mapping.record_status='A' and erp_campus.record_status='A' and erp_location.record_status='A' "
						+ " and erp_users.erp_users_id=:userId) and emp_interview_panelist.record_status='A' and erp_academic_year.record_status='A' group by emp_interview_panelist.external_erp_users_id "
						+ " union  "
						+ " select emp_interview_panelist.emp_interview_university_externals_id as ID,emp_interview_university_externals.panelist_name as Text,false as status,max(erp_academic_year.academic_year) "
						+ " from emp_interview_panelist "
						+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.external_erp_users_id "
						+ " inner join emp_interview_university_externals on emp_interview_university_externals.emp_interview_university_externals_id=emp_interview_panelist.emp_interview_university_externals_id and emp_interview_university_externals.record_status ='A' "
						+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id "
						+ " where emp_interview_panelist.erp_department_id=(select erp_campus_department_mapping.erp_department_id from erp_users "
						+ " inner join emp ON emp.emp_id = erp_users.emp_id "
						+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id "
						+ " where erp_campus_department_mapping.record_status='A' and erp_users.record_status='A' and emp.record_status='A' and erp_users.erp_users_id=:userId) "
						+ " and emp_interview_panelist.erp_location_id=(select erp_location.erp_location_id from erp_users "
						+ " inner join emp ON emp.emp_id = erp_users.emp_id "
						+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id "
						+ " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
						+ " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id "
						+ " where erp_users.record_status='A' and emp.record_status='A' "
						+ " and erp_campus_department_mapping.record_status='A' and erp_campus.record_status='A' and erp_location.record_status='A' "
						+ " and erp_users.erp_users_id=:userId) and emp_interview_panelist.record_status='A' and erp_academic_year.record_status='A' group by emp_interview_panelist.emp_interview_university_externals_id ";
				Query query = context.createNativeQuery(queryString, Tuple.class);				
				query.setParameter("userId", userId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	//	public List<Tuple> getHolidays(String userId) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
	//			@SuppressWarnings("unchecked")
	//			@Override
	//			public List<Tuple> onRun(EntityManager context) throws Exception {
	//				String queryString = "select   a.Date as holidays   from (     select curdate() - INTERVAL (a.a + (10 * b.a) + (100 * c.a)) DAY as Date  "
	//						+ "   from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 "
	//						+ " union all select 6 union all select 7 union all select 8 union all select 9) as a     cross join (select 0 as a union all select 1 union all select 2 "
	//						+ " union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b     "
	//						+ " cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 "
	//						+ " union all select 7 union all select 8 union all select 9) as c   ) a   "
	//						+ " join emp_holiday_events ehe on a.Date between ehe.holiday_events_start_date and ehe.holiday_events_end_date   "
	//						+ " inner join erp_campus on erp_campus.erp_location_id = ehe.erp_location_id  "
	//						+ " inner join erp_campus_department_mapping as ecdm on ecdm.erp_campus_id=erp_campus.erp_campus_id  "
	//						+ " inner join emp on ecdm.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id  "
	//						+ " inner join erp_users on erp_users.emp_id=emp.emp_id   "
	//						+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = ehe.erp_academic_year_id  "
	//						+ " inner join erp_academic_year_detail eayd on eayd.erp_academic_year_id=erp_academic_year.erp_academic_year_id and eayd.erp_campus_id=erp_campus.erp_campus_id "
	//						+ " where erp_users.erp_users_id=:userId and erp_academic_year.is_current_academic_year=1 and ehe.record_status like 'A' and erp_academic_year.record_status like 'A'   order by holidays";
	//				Query query = context.createNativeQuery(queryString, Tuple.class);				
	//				query.setParameter("userId", userId);
	//				return query.getResultList();
	//			}
	//			@Override
	//			public void onError(Exception error) throws Exception {
	//				throw error;
	//			}
	//		});
	//	}

	//	public Integer getCampusDepartmentMappingId(String userId) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<Integer>() {
	//			@SuppressWarnings("unchecked")
	//			@Override
	//			public Integer onRun(EntityManager context) throws Exception {
	//				Integer id = null;				
	//				String query = "select * from emp " + 
	//						"inner join erp_users on erp_users.emp_id = emp.emp_id	and erp_users.record_status = 'A' " + 
	//						"where erp_users.erp_users_id=:userId and emp.record_status like 'A' ";
	//				Query q = context.createNativeQuery(query, EmpDBO.class);
	//				q.setParameter("userId",userId);
	//				EmpDBO empDBO = null;
	//				empDBO = (EmpDBO) Utils.getUniqueResult(q.getResultList());
	//				if (empDBO != null) {
	//					id = empDBO.erpCampusDepartmentMappingDBO.id;
	//				} 
	//				return id;
	//			}
	//			@Override
	//			public void onError(Exception error) throws Exception {
	//				throw error;
	//			}
	//		});
	//	}
	//	public List<Tuple> getCampusDepartments(String userId) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
	//			@SuppressWarnings("unchecked")
	//			@Override
	//			public List<Tuple> onRun(EntityManager context) throws Exception {
	//				String queryString = " select ecdm.erp_campus_department_mapping_id as ID, concat(erp_campus.campus_name,'-',ed.department_name) as Text "
	//						+ " from emp "
	//						+ " inner join erp_users eu on eu.emp_id=emp.emp_id "
	//						+ " inner join erp_campus_department_mapping ecdm on ecdm.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id  "
	//						+ " inner join erp_department ed on ed.erp_department_id=ecdm.erp_department_id "
	//						+ " inner join erp_campus on erp_campus.erp_campus_id = ecdm.erp_campus_id   where eu.erp_users_id=:userId "
	//						+ " union   "
	//						+ " select ecdm.erp_campus_department_mapping_id as ID, concat(erp_campus.campus_name,'-',ed.department_name)  as Text "
	//						+ " from erp_campus_department_user_title ecdut  "
	//						+ " inner join erp_campus_department_mapping ecdm on ecdm.erp_campus_department_mapping_id = ecdut.erp_campus_department_mapping_id "
	//						+ " inner join erp_department ed on ed.erp_department_id=ecdm.erp_department_id "
	//						+ " inner join erp_campus on erp_campus.erp_campus_id = ecdm.erp_campus_id "
	//						+ " inner join emp_title on ecdut.erp_title_id=emp_title.emp_title_id and emp_title.title_name like 'hod' "
	//						+ " where ecdut.erp_users_id=:userId and ecdm.record_status like 'A'";
	//				Query query = context.createNativeQuery(queryString, Tuple.class);				
	//				query.setParameter("userId", userId);
	//				return query.getResultList();
	//			}
	//			@Override
	//			public void onError(Exception error) throws Exception {
	//				throw error;
	//			}
	//		});
	//	}

	public EmpApplnEntriesDBO getApplicantData(Integer id) {
		String str = " select dbo from EmpApplnEntriesDBO dbo "
				+ " left join fetch dbo.empApplnInterviewSchedulesDBOs as eaisd "
				+ " left join fetch eaisd.empApplnInterviewPanelDBO "
				+ " where dbo.recordStatus='A' and dbo.id=:id ";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpApplnEntriesDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public Integer getEmpId(Integer id) {
		String str = "select emp.emp_id  "
				+ " from erp_users "
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " where erp_users.record_status='A' and erp_users.erp_users_id = :id ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean saveOrUpdate(List<Object> objects) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(objects.toArray())).await().indefinitely();
		return true;
	}

	public void save(EmpApplnEntriesDBO dbo, Integer empR1HodContacted, Integer empR1HodShortListed,String userId,Integer previousWorkFlowProcess,Boolean hodContact) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo).chain(session::flush).map(s -> {
			return convertDbo(dbo,empR1HodContacted,empR1HodShortListed,userId,previousWorkFlowProcess,hodContact);
		}).flatMap(s-> session.mergeAll(s.toArray()))
				).await().indefinitely();
	}

	private List<Object> convertDbo(EmpApplnEntriesDBO dbo,Integer empR1HodContacted, Integer empR1HodShortListed,String userId,Integer previousWorkFlowProcess,Boolean hodContact) {
		List<Object> list = new ArrayList<Object>();
		if(!Utils.isNullOrEmpty(dbo.getIsContactedByHod()) && !Utils.isNullOrEmpty(empR1HodContacted)) {
			if(hodContact != dbo.getIsContactedByHod()) {
				ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
				erpWorkFlowProcessStatusLogDBO.entryId= dbo.getId();
				erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
				erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO.id = empR1HodContacted;
				erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
				erpWorkFlowProcessStatusLogDBO.createdUsersId = Integer.parseInt(userId);
				list.add(erpWorkFlowProcessStatusLogDBO);
			}
		}
		if(!Utils.isNullOrEmpty(empR1HodShortListed) && dbo.getApplicantCurrentProcessStatus().getId() != previousWorkFlowProcess) {
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.entryId= dbo.getId();
			erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
			erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO.id = empR1HodShortListed;
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.createdUsersId = Integer.parseInt(userId);
			list.add(erpWorkFlowProcessStatusLogDBO);
		}
		return list;
	}

	public EmpApplnEntriesDBO getempApplnInterviewSchedules(Integer id) {
		String str = " select dbo from EmpApplnEntriesDBO dbo "
				+ " left join fetch dbo.empApplnInterviewSchedulesDBOs as dbo1"
				+ " left join fetch dbo1.empApplnInterviewPanelDBO "
				+ " where dbo.recordStatus='A' and dbo.id=:id ";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpApplnEntriesDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public EmpApplnInterviewSchedulesDBO getEmpApplnInterviewSchedulesDBO(int id) {
		String str = " select dbo from EmpApplnInterviewSchedulesDBO dbo "
				+ " left join fetch dbo.empApplnInterviewPanelDBO "
				+ " where dbo.recordStatus='A' and dbo.id=:id ";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpApplnInterviewSchedulesDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public List<EmpApplnEntriesDBO> getApplicantsDetails(List<Integer> applicationIds) {
		String str = " select DISTINCT dbo from EmpApplnEntriesDBO dbo "
				+ " left join fetch dbo.empApplnInterviewSchedulesDBOs as eaisd "
				+ " left join fetch eaisd.empApplnInterviewPanelDBO as panel "
				+ " where dbo.recordStatus='A' and dbo.id IN (:applicationIds) ";
		String finalStr = str;
		List<EmpApplnEntriesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnEntriesDBO> query = s.createQuery(finalStr, EmpApplnEntriesDBO.class);
			query.setParameter("applicationIds", applicationIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnEntriesDBO> getEmpApplnEntriesDBO(List<Integer> applicationIds) {
		String str = " select dbo from EmpApplnEntriesDBO dbo "
				+ " where dbo.recordStatus='A' and dbo.id IN (:applicationIds) ";
		String finalStr = str;
		List<EmpApplnEntriesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnEntriesDBO> query = s.createQuery(finalStr, EmpApplnEntriesDBO.class);
			query.setParameter("applicationIds", applicationIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnInterviewSchedulesDBO> getInterviewScheduleDetails(List<Integer> interviewScheduleIds) {
		String str = " select DISTINCT dbo from EmpApplnInterviewSchedulesDBO dbo "
				+ " left join fetch dbo.empApplnInterviewPanelDBO as panel "
				+ " where dbo.recordStatus='A' and dbo.id IN (:interviewScheduleIds) and dbo.interviewRound = 1";
		String finalStr = str;
		List<EmpApplnInterviewSchedulesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnInterviewSchedulesDBO> query = s.createQuery(finalStr, EmpApplnInterviewSchedulesDBO.class);
			query.setParameter("interviewScheduleIds", interviewScheduleIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getOfferStatus(String departmentId, String processCode, String locationId) {
		String str = "  select erp_department.erp_department_id,eae.emp_appln_entries_id,eae.applicant_name,erp_department.department_name, "
				+ " eae.application_no,ewfp.process_code,emp_appln_personal_data.profile_photo_url,ewfp.application_status_display_text as text, "
				+ " url.file_name_unique, url.file_name_original, folder.upload_process_code,"
				+ " emp_job_details.joining_date, "
				+ " emp_job_details.prefered_joining_date, emp_job_details.joining_date_reject_reason, emp_job_details.is_joining_date_confirmed"
				+ " from  emp_appln_entries as eae  "
				+ " left join emp_appln_personal_data on eae.emp_appln_entries_id = emp_appln_personal_data.emp_appln_entries_id and emp_appln_personal_data.record_status ='A' "
				+ " inner join erp_department on eae.shortlisted_department_id = erp_department.erp_department_id and erp_department.record_status = 'A' and  "
				+ " if(:departmentId is not null and  :departmentId != '',eae.shortlisted_department_id in (:departmentId),1=1)  "
				+ " inner join erp_location ON erp_location.erp_location_id = eae.shortlisted_location_id and erp_location.record_status = 'A' and  "
				+ " if(:locationId is not null and  :locationId != '',eae.shortlisted_location_id in (:locationId),1=1)    "
				+ " left join erp_work_flow_process as ewfp on eae.application_current_process_status = ewfp.erp_work_flow_process_id and ewfp.record_status = 'A'  "
				+ " left join url_access_link url ON emp_appln_personal_data.profile_photo_url_id = url.url_access_link_id"
				+ " left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
				+ " left join emp_job_details on eae.emp_appln_entries_id = emp_job_details.emp_appln_entries_id"
				+ " where eae.record_status = 'A'";
		if(!Utils.isNullOrEmpty(processCode)) {
			if(processCode.trim().equalsIgnoreCase("Offer Accepted")) {
				str += " and ewfp.process_code='EMP_OFFER_ACCEPTED'";
			}
			if(processCode.trim().equalsIgnoreCase("Offer Declined")) {
				str += " and ewfp.process_code='EMP_OFFER_DECLINED'";
			}
			if(processCode.trim().equalsIgnoreCase("Letter Generated /Selected")) {
				str += " and  ewfp.process_code IN ('EMP_OFFER_LETTER_GENERATED','EMP_OFFER_LETTER_REGENERATE','EMP_STAGE3_SELECTED')";
			}
			if(processCode.trim().equalsIgnoreCase("All")) {
				str += " and (ewfp.process_code='EMP_OFFER_ACCEPTED' or ewfp.process_code='EMP_OFFER_DECLINED' or ewfp.process_code IN ('EMP_OFFER_LETTER_GENERATED','EMP_OFFER_LETTER_REGENERATE','EMP_STAGE3_SELECTED')) ";
			}
		}else {
			str += " and ewfp.process_code='EMP_OFFER_ACCEPTED'";
		}
		str +=" group by erp_department.erp_department_id,eae.emp_appln_entries_id,eae.applicant_name,erp_department.department_name, "
				+ " eae.application_no,ewfp.process_code,emp_appln_personal_data.emp_appln_personal_data_id, emp_job_details.joining_date,emp_job_details.prefered_joining_date, emp_job_details.joining_date_reject_reason, emp_job_details.is_joining_date_confirmed ";
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr,Tuple.class);
			query.setParameter("departmentId", !Utils.isNullOrEmpty(departmentId)? departmentId:null);
			query.setParameter("locationId", !Utils.isNullOrEmpty(locationId)? locationId:null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnEntriesDBO> getScheduleStageOne(String departmentId, String processCode,String locationId) {
		String hqlQuery = "SELECT dbo FROM EmpApplnEntriesDBO dbo "
				+ "LEFT JOIN dbo.shortlistedDepartmentId AS department "
				+ "LEFT JOIN dbo.shortistedLocationId AS location "
				+ "LEFT JOIN FETCH dbo.empApplnInterviewSchedulesDBOs AS interviewSchedules "
				+ "LEFT JOIN FETCH interviewSchedules.empApplnInterviewPanelDBO AS panel "
				+ "LEFT JOIN dbo.empApplnPersonalDataDBO AS eapd "
				+ "LEFT JOIN eapd.photoDocumentUrlDBO AS photoUrl "
				+ "LEFT JOIN photoUrl.urlFolderListDBO AS urlFloder "
				+ "WHERE dbo.recordStatus = 'A' ";
		if (!Utils.isNullOrEmpty(processCode)) {
			if (processCode.trim().equalsIgnoreCase("Shortlisted (Pending to Schedule)")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_HOD_SHORTLISTED') ";
			}
			if (processCode.trim().equalsIgnoreCase("Scheduled")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_HOD_SCHEDULED') AND interviewSchedules.interviewRound = 1 ";
			}
			if (processCode.trim().equalsIgnoreCase("Schedule Approved")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_PO_APPROVES_SCHEDULE','EMP_STAGE1_HOD_RESCHEDULED') AND interviewSchedules.interviewRound = 1 ";
			}
		}else {
			hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_HOD_SHORTLISTED') ";
		}
		if(!Utils.isNullOrEmpty(departmentId))
			hqlQuery += " AND dbo.shortlistedDepartmentId.id IN (:departmentId) ";
		if(!Utils.isNullOrEmpty(locationId))
			hqlQuery += "AND dbo.shortistedLocationId.id IN (:locationId) ";
		String finalStr = hqlQuery;
		List<EmpApplnEntriesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnEntriesDBO> query = s.createQuery(finalStr,EmpApplnEntriesDBO.class);
			if(!Utils.isNullOrEmpty(departmentId))
				query.setParameter("departmentId",Integer.parseInt(departmentId));
			if(!Utils.isNullOrEmpty(locationId))
				query.setParameter("locationId",Integer.parseInt(locationId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnEntriesDBO> getSheduledForApprovalStageOne(String departmentId,String locationId) {
		String hqlQuery = "SELECT dbo FROM EmpApplnEntriesDBO dbo "
				+ "LEFT JOIN dbo.shortlistedDepartmentId AS department "
				+ "LEFT JOIN dbo.shortistedLocationId AS location "
				+ "LEFT JOIN FETCH dbo.empApplnInterviewSchedulesDBOs AS interviewSchedules "
				+ "LEFT JOIN FETCH interviewSchedules.empApplnInterviewPanelDBO AS panel "
				+ "LEFT JOIN dbo.empApplnPersonalDataDBO AS eapd "
				+ "LEFT JOIN eapd.photoDocumentUrlDBO AS photoUrl "
				+ "LEFT JOIN photoUrl.urlFolderListDBO AS urlFloder "
				+ "WHERE dbo.recordStatus = 'A' AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_HOD_SCHEDULED') AND interviewSchedules.interviewRound = 1";
		if(!Utils.isNullOrEmpty(departmentId))
			hqlQuery += " AND dbo.shortlistedDepartmentId.id IN (:departmentId) ";
		if(!Utils.isNullOrEmpty(locationId))
			hqlQuery += "AND dbo.shortistedLocationId.id IN (:locationId) ";
		String finalStr = hqlQuery;
		List<EmpApplnEntriesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnEntriesDBO> query = s.createQuery(finalStr,EmpApplnEntriesDBO.class);
			if(!Utils.isNullOrEmpty(departmentId))
				query.setParameter("departmentId",Integer.parseInt(departmentId));
			if(!Utils.isNullOrEmpty(locationId))
				query.setParameter("locationId",Integer.parseInt(locationId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnEntriesDBO> getInterviewStatusStageOne(String departmentId, String locationId,String processCode) {
		String hqlQuery = "SELECT dbo FROM EmpApplnEntriesDBO dbo "
				+ "LEFT JOIN dbo.shortlistedDepartmentId AS department "
				+ "LEFT JOIN dbo.shortistedLocationId AS location "
				+ "LEFT JOIN FETCH dbo.empApplnInterviewSchedulesDBOs AS interviewSchedules "
				+ "LEFT JOIN FETCH interviewSchedules.empApplnInterviewPanelDBO AS panel "
				+ "LEFT JOIN dbo.empApplnPersonalDataDBO AS eapd "
				+ "LEFT JOIN eapd.photoDocumentUrlDBO AS photoUrl "
				+ "LEFT JOIN photoUrl.urlFolderListDBO AS urlFloder "
				+ "WHERE dbo.recordStatus = 'A' AND eapd.recordStatus = 'A' AND interviewSchedules.interviewRound = 1";
		if(!Utils.isNullOrEmpty(processCode)) {
			if(processCode.trim().equalsIgnoreCase("Schedule Accepted")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_APPLICANT_ACCEPTED') ";
			}
			if(processCode.trim().equalsIgnoreCase("Schedule Declined")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_APPLICANT_DECLINED') ";
			}
			if(processCode.trim().equalsIgnoreCase("Schedule Pending")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_PO_APPROVES_SCHEDULE','EMP_STAGE1_HOD_RESCHEDULED') ";
			}
			if(processCode.trim().equalsIgnoreCase("Stage 1 In Progress")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_PO_APPROVES_SCHEDULE','EMP_STAGE1_HOD_RESCHEDULED','EMP_STAGE1_INTERVIEW_COMPLETED','EMP_STAGE1_APPLICANT_ACCEPTED') ";
			}
			if(processCode.trim().equalsIgnoreCase("Forwarded to PO")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_SELECTED') ";
			}
			if(processCode.trim().equalsIgnoreCase("Rejected")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_REJECTED') ";
			}
			if(processCode.trim().equalsIgnoreCase("On Hold")) {
				hqlQuery += "AND dbo.applicationCurrentProcessStatus.processCode IN ('EMP_STAGE1_ONHOLD') ";
			}
		}else {
			processCode = "EMP_STAGE1_APPLICANT_ACCEPTED";
		}
		if(!Utils.isNullOrEmpty(departmentId))
			hqlQuery += " AND dbo.shortlistedDepartmentId.id IN (:departmentId) ";
		if(!Utils.isNullOrEmpty(locationId))
			hqlQuery += "AND dbo.shortistedLocationId.id IN (:locationId) ";
		String finalStr = hqlQuery;
		List<EmpApplnEntriesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnEntriesDBO> query = s.createQuery(finalStr,EmpApplnEntriesDBO.class);
			if(!Utils.isNullOrEmpty(departmentId))
				query.setParameter("departmentId",Integer.parseInt(departmentId));
			if(!Utils.isNullOrEmpty(locationId))
				query.setParameter("locationId",Integer.parseInt(locationId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getScheduleStageTwo(String departmentId, String processCode, String locationId) {
		String str = " select emp_appln_entries.emp_appln_entries_id as id,emp_appln_entries.application_no as applicationNo,emp_appln_entries.applicant_name as name,  "
				+ " emp_appln_entries.personal_email_id as email, emp_appln_entries.mobile_no_country_code as mobileCountryCode, emp_appln_entries.mobile_no as mobileNo, "
				+ " emp_appln_personal_data.profile_photo_url as photoUrl,emp_appln_interview_schedules.interview_date_time as interviewDateTime,emp_appln_interview_schedules.interview_venue as interviewvenue,emp_appln_interview_schedules.point_of_contact_users_id as pointOfContactUsersId, "
				+ " erp_work_flow_process.application_status_display_text as status,erp_work_flow_process.process_code as processCode,emp_appln_interview_schedules.emp_appln_interview_schedules_id as interviewId,  "
				+ " url.file_name_unique, url.file_name_original, folder.upload_process_code , emp_appln_interview_schedules_r1.comments as hodComments, "
				+ " emp_appln_entries.shortlisted_department_id as departmentId ,erp_department.department_name as departmentName"
				+ " from emp_appln_entries  "
				+ " left join  emp_appln_personal_data on emp_appln_entries.emp_appln_entries_id = emp_appln_personal_data.emp_appln_entries_id and emp_appln_personal_data.record_status = 'A'"
				+ " left join erp_work_flow_process on emp_appln_entries.application_current_process_status = erp_work_flow_process.erp_work_flow_process_id  and erp_work_flow_process.record_status = 'A'"
				+ " left join emp_appln_interview_schedules on emp_appln_interview_schedules.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_interview_schedules.interview_round = 2 and emp_appln_interview_schedules.record_status = 'A' "
				+ " left join emp_appln_interview_schedules as emp_appln_interview_schedules_r1 on emp_appln_interview_schedules_r1.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_interview_schedules_r1.interview_round =1 and emp_appln_interview_schedules_r1.record_status = 'A' "
				+ " inner join erp_department on emp_appln_entries.shortlisted_department_id = erp_department.erp_department_id and erp_department.record_status = 'A' and  "
				+ " if(:departmentId is not null and  :departmentId != '',emp_appln_entries.shortlisted_department_id in (:departmentId),1=1) "
				+ " inner join erp_location ON erp_location.erp_location_id = emp_appln_entries.shortlisted_location_id and erp_location.record_status = 'A' and  "
				+ " if(:locationId is not null and  :locationId != '',emp_appln_entries.shortlisted_location_id in (:locationId),1=1)     "
				+ " left join url_access_link url ON emp_appln_personal_data.profile_photo_url_id = url.url_access_link_id "
				+ " left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id "
				+ " where emp_appln_entries.record_status = 'A' ";
		if(!Utils.isNullOrEmpty(processCode)) {
			if(processCode.trim().equalsIgnoreCase("Schedule Accepted")) {
				str += "AND erp_work_flow_process.process_code IN  ('EMP_STAGE2_SCHEDULE_APPLICANT_ACCEPTED') ";
			}
			if(processCode.trim().equalsIgnoreCase("Schedule Declined")) {
				str += "AND erp_work_flow_process.process_code IN  ('EMP_STAGE2_SCHEDULE_APPLICANT_DECLINED') ";
			}
			if(processCode.trim().equalsIgnoreCase("Scheduled")) {
				str += "AND erp_work_flow_process.process_code IN  ('EMP_STAGE2_PO_SCHEDULED','EMP_STAGE2_PO_RESCHEDULED') ";
			}
			if(processCode.trim().equalsIgnoreCase("To be Scheduled")) {
				str += "AND erp_work_flow_process.process_code IN  ('EMP_STAGE1_SELECTED') ";
			}
		}else {
			str += "AND erp_work_flow_process.process_code IN  ('EMP_STAGE1_SELECTED') ";
		}
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr,Tuple.class);
			query.setParameter("departmentId",!Utils.isNullOrEmpty(departmentId) ? departmentId : null);
			query.setParameter("locationId", !Utils.isNullOrEmpty(locationId) ? locationId : null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpWorkFlowProcessDBO> getWorkFlowProcess() {
		String str = " select dbo from ErpWorkFlowProcessDBO dbo "
				+ " where dbo.recordStatus ='A' and dbo.processCode IN ('EMP_STAGE1_HOD_SCHEDULED','EMP_STAGE1_PO_APPROVES_SCHEDULE','EMP_STAGE1_HOD_SHORTLISTED','EMP_STAGE1_HOD_RESCHEDULED','EMP_STAGE1_APPLICANT_ACCEPTED','EMP_STAGE1_APPLICANT_DECLINED')";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpWorkFlowProcessDBO.class).getResultList()).await().indefinitely();		
	}

	public List<ErpWorkFlowProcessDBO> getWorkFlowProcess1() {
		String str = " select dbo from ErpWorkFlowProcessDBO dbo "
				+ " where dbo.recordStatus ='A' and dbo.processCode IN ('EMP_STAGE1_SELECTED','EMP_STAGE2_PO_SCHEDULED','EMP_STAGE2_PO_RESCHEDULED','EMP_STAGE2_SCHEDULE_APPLICANT_DECLINED','EMP_STAGE2_SCHEDULE_APPLICANT_ACCEPTED')";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpWorkFlowProcessDBO.class).getResultList()).await().indefinitely();		
	}

	public List<ErpWorkFlowProcessDBO> getWorkFlowProcess3() {
		String str = " select dbo from ErpWorkFlowProcessDBO dbo "
				+ " where dbo.recordStatus ='A' and dbo.processCode IN ('EMP_STAGE1_SELECTED','EMP_STAGE1_REJECTED','EMP_STAGE1_ONHOLD')";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpWorkFlowProcessDBO.class).getResultList()).await().indefinitely();	
	}

	public List<Tuple> getCount(String departmentId, String locationId) {
		String str = " SELECT erp_work_flow_process.process_code as processCode,emp_appln_entries.application_current_process_status ,COUNT(erp_work_flow_process.process_code) AS countt "
				+ " FROM emp_appln_entries "
				+ " INNER JOIN erp_work_flow_process on erp_work_flow_process.erp_work_flow_process_id = emp_appln_entries.application_current_process_status "
				+ " and erp_work_flow_process.record_status='A' "
				+ " LEFT JOIN erp_department ON erp_department.erp_department_id = emp_appln_entries.shortlisted_department_id "
				+ " AND erp_department.record_status='A' "
				+ " LEFT JOIN erp_location ON erp_location.erp_location_id = emp_appln_entries.shortlisted_location_id "
				+ " AND erp_location.record_status='A' "
				+ " WHERE emp_appln_entries.record_status='A' "
				+ " and if(:departmentId is not null and  :departmentId != '',erp_department.erp_department_id=:departmentId,1=1) "
				+ " and if(:locationId is not null and  :locationId != '',erp_location.erp_location_id=:locationId,1=1) "
				+ " group by erp_work_flow_process.process_code, emp_appln_entries.application_current_process_status ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("departmentId",!Utils.isNullOrEmpty(departmentId) ? departmentId :null);
			query.setParameter("locationId",!Utils.isNullOrEmpty(locationId) ? locationId : null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public ErpWorkFlowProcessNotificationsDBO getErpWorkFlowProcessNotification(int workflowProcessId, String code) {
		String str = " select dbo from ErpWorkFlowProcessNotificationsDBO dbo where dbo.erpWorkFlowProcessDBO.id=:workflowProcessId and dbo.notificationCode=:code and dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpWorkFlowProcessNotificationsDBO.class).setParameter("workflowProcessId", workflowProcessId).setParameter("code", code).getSingleResultOrNull()).await().indefinitely();	

	}

	public List<Tuple> getTotalScore(List<Integer> empApplnEntryId) {
		String str = " select emp_appln_entries.emp_appln_entries_id as Ids, sum(emp_appln_interview_score.total_score) as summ from emp_appln_interview_score  "
				+ "inner join emp_appln_interview_schedules  ON emp_appln_interview_schedules.emp_appln_interview_schedules_id = emp_appln_interview_score.emp_appln_interview_schedules_id and emp_appln_interview_schedules.record_status='A' "
				+ "inner join emp_appln_entries  ON emp_appln_entries.emp_appln_entries_id = emp_appln_interview_schedules.emp_appln_entries_id and emp_appln_entries.record_status='A' "
				+ "where emp_appln_interview_score.record_status='A' and emp_appln_entries.emp_appln_entries_id IN (:empApplnEntryId) GROUP BY emp_appln_entries.emp_appln_entries_id ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("empApplnEntryId", empApplnEntryId);
			return query.getResultList();
		}).await().indefinitely();
		return list;	
	}

	public List<Tuple> getContactAndInternal(List<Integer> contactAndInternal) {
		String str = " select emp.mobile_no as mobileNo,emp.moble_no_country_code as mobileCountry,emp.emp_university_email as email,erp_users.erp_users_id as userId,emp.emp_name as empName   "
				+ " from erp_users "
				+ " inner join emp ON emp.emp_id = erp_users.emp_id "
				+ " where erp_users.record_status = 'A' and emp.record_status = 'A' and erp_users.erp_users_id In(:contactAndInternal) ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("contactAndInternal", contactAndInternal);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}	

	public List<Tuple> getPanelMemberCount(List<Integer> empApplnEntryId) {
		String str = "select DISTINCT emp_enties.emp_appln_entries_id as Ids, COUNT(panel.emp_appln_interview_schedules_id) as countt"
				+" from emp_appln_interview_panel panel "
				+" inner join emp_appln_interview_schedules sch ON sch.emp_appln_interview_schedules_id = panel.emp_appln_interview_schedules_id "
				+" inner join emp_appln_entries emp_enties ON emp_enties.emp_appln_entries_id = sch.emp_appln_entries_id "
				+" where panel.record_status='A' and sch.record_status='A' and emp_enties.record_status='A' and "                                          
				+" emp_enties.emp_appln_entries_id IN (:empApplnEntryId) group by panel.emp_appln_interview_schedules_id ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("empApplnEntryId", empApplnEntryId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getScoreEnteredPanelCount(List<Integer> empApplnEntryId) {
		String str = " select DISTINCT emp_entries.emp_appln_entries_id as Ids, COUNT(sc.emp_appln_interview_schedules_id) as countt from emp_appln_interview_score sc "
				+" inner join emp_appln_interview_schedules sch ON sch.emp_appln_interview_schedules_id = sc.emp_appln_interview_schedules_id "
				+" inner join emp_appln_entries emp_entries ON emp_entries.emp_appln_entries_id = sch.emp_appln_entries_id "
				+" where emp_entries.emp_appln_entries_id IN (:empApplnEntryId) and sc.record_status='A' and sch.record_status='A' and emp_entries.record_status='A'"
				+" group by sc.emp_appln_interview_schedules_id ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("empApplnEntryId", empApplnEntryId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public BigInteger getCountOfApplicationSubmission(String departmentId,String locationId) {
		String str = " SELECT DISTINCT COUNT(*) AS result_count"
				+ " FROM( SELECT "
				+ " GROUP_CONCAT(DISTINCT eascd.erp_department_id),"
				+ " GROUP_CONCAT(DISTINCT easc.subject_category_name),"
				+ " GROUP_CONCAT(DISTINCT erp_department.department_name),"
				+ " GROUP_CONCAT(DISTINCT eascs.subject_category_specialization_name),"
				+ " GROUP_CONCAT(DISTINCT el.location_name, '')"
				+ " FROM emp_appln_entries AS eae"
				+ " INNER JOIN emp_appln_subj_specialization_pref ON  emp_appln_subj_specialization_pref.emp_appln_entries_id = eae.emp_appln_entries_id AND emp_appln_subj_specialization_pref.record_status = 'A'"
				+ " INNER JOIN emp_appln_subject_category AS easc ON  easc.emp_appln_subject_category_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_id AND easc.record_status = 'A'"
				+ " LEFT JOIN emp_appln_subject_category_specialization AS eascs ON  eascs.emp_appln_subject_category_specialization_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_specialization_id AND eascs.record_status = 'A'"
				+ " LEFT JOIN emp_appln_subject_category_department AS eascd ON  eascd.emp_appln_subject_category_id = easc.emp_appln_subject_category_id AND eascd.record_status = 'A'"
				+ " LEFT JOIN erp_department ON  eascd.erp_department_id = erp_department.erp_department_id AND erp_department.record_status = 'A' "
				+ " INNER JOIN emp_employee_category AS eec ON  eec.emp_employee_category_id = eae.emp_employee_category_id AND eec.record_status = 'A'"
				+ " INNER JOIN emp_appln_personal_data AS eapd ON  eapd.emp_appln_entries_id = eae.emp_appln_entries_id AND eapd.record_status = 'A'"
				+ " LEFT JOIN erp_qualification_level AS eql ON  eql.erp_qualification_level_id = eae.highest_qualification_level AND eql.record_status = 'A'"
				+ " INNER JOIN emp_appln_location_pref AS ealp ON  ealp.emp_appln_entries_id = eae.emp_appln_entries_id AND ealp.record_status = 'A' AND if(:locationId IS NOT NULL AND :locationId != '', ealp.erp_location_id IN (:locationId), 1 = 1)"
				+ " INNER JOIN erp_location AS el ON  ealp.erp_location_id = el.erp_location_id AND el.record_status = 'A'"
				+ " LEFT JOIN erp_work_flow_process AS ewfp ON  eae.application_current_process_status = ewfp.erp_work_flow_process_id AND ewfp.record_status = 'A'"
//				+ " INNER JOIN erp_location ON  ealp.erp_location_id = erp_location.erp_location_id AND erp_location.record_status = 'A'"
				+ " LEFT JOIN url_access_link url ON eapd.profile_photo_url_id = url.url_access_link_id"
				+ " LEFT JOIN url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
				+ " WHERE  eae.submission_date_time > DATE_SUB(now(), INTERVAL 6 MONTH)"
				+ " AND ewfp.process_code = 'EMP_APPLICATION_SUBMITTED'"
				+ " AND eae.record_status = 'A'"
				+ " AND (eascd.emp_appln_subject_category_id is null or if(:departmentId IS NOT NULL AND :departmentId != '', eascd.erp_department_id IN (:departmentId), 1 = 1))"
				+ " GROUP BY eae.emp_appln_entries_id,eapd.emp_appln_personal_data_id)as count";
		BigInteger value = sessionFactory.withSession(s -> {
			Mutiny.Query<BigInteger> query = s.createNativeQuery(str,BigInteger.class);
			query.setParameter("departmentId",!Utils.isNullOrEmpty(departmentId) ? departmentId :null);
			query.setParameter("locationId",!Utils.isNullOrEmpty(locationId) ? locationId : null);
			return query.getSingleResult();
		}).await().indefinitely();
		return value;
	}

	public List<Tuple> getStageTwoInterviewStatus(String departmentId, String locationId, String filterStatus) {
		String queryString = "select distinct emp_appln_entries.emp_appln_entries_id as id,emp_appln_entries.application_no as applicationNo,emp_appln_entries.applicant_name as name,"
				+ " emp_appln_personal_data.profile_photo_url as photoUrl,emp_appln_interview_schedules.interview_date_time as interviewDateTime,"
				+ " erp_work_flow_process.application_status_display_text as status,"
				+ " emp_appln_interview_schedules.interview_venue as venue,"
				+ " emp_appln_interview_schedules.emp_appln_interview_schedules_id as scheduleId,"
				+ "	emp_appln_interview_schedules.point_of_contact_users_id as pointOfContactId,"
				+ " url.file_name_unique, url.file_name_original, folder.upload_process_code,"
				+ " cast(emp_appln_entries.stage2_onhold_rejected_comments as char) as stage2_onhold_rejected_comments ,"
				+ " emp_appln_entries.shortlisted_department_id as departmentId ,erp_department.department_name as departmentName "
				+ " from emp_appln_entries"
				+ " inner join  emp_appln_personal_data on emp_appln_entries.emp_appln_entries_id = emp_appln_personal_data.emp_appln_entries_id  and emp_appln_personal_data.record_status = 'A'"
				+ " inner join erp_work_flow_process on emp_appln_entries.application_current_process_status = erp_work_flow_process.erp_work_flow_process_id and erp_work_flow_process.record_status = 'A'"
				+ " left join emp_appln_interview_schedules on emp_appln_interview_schedules.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_interview_schedules.record_status = 'A' and emp_appln_interview_schedules.interview_round = 2"
				+ " left join erp_users ON erp_users.erp_users_id = emp_appln_interview_schedules.point_of_contact_users_id  "//and erp_users.record_status = 'A'"
				+ " left join emp ON emp.emp_id = erp_users.emp_id " //and emp.record_status = 'A'"
				+ " left join erp_department on emp_appln_entries.shortlisted_department_id = erp_department.erp_department_id and erp_department.record_status = 'A' and"
				+ " if(:departmentId is not null and  :departmentId != '',emp_appln_entries.shortlisted_department_id in (:departmentId),1=1)"
				+ " left join erp_location ON erp_location.erp_location_id = emp_appln_entries.shortlisted_location_id and erp_location.record_status = 'A' and"
				+ " if(:locationId is not null and  :locationId != '',emp_appln_entries.shortlisted_location_id in (:locationId),1=1)  "
				+ " left join url_access_link url ON emp_appln_personal_data.profile_photo_url_id = url.url_access_link_id"
				+ " left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
				+ " where emp_appln_entries.record_status = 'A'";
		if(filterStatus.equalsIgnoreCase("Schedule Accepted")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE2_SCHEDULE_APPLICANT_ACCEPTED'";
		} else if(filterStatus.equalsIgnoreCase("Schedule Declined")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE2_SCHEDULE_APPLICANT_DECLINED'"; 
		} 
		//		else if(filterStatus.equalsIgnoreCase("Pending")) {
		//			queryString += " and erp_work_flow_process.process_code in('EMP_STAGE2_PO_SCHEDULED','EMP_STAGE2_RESHEDULED')";
		//		}
		else if(filterStatus.equalsIgnoreCase("Forwarded to Stage3")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE2_SELECTED'";
		} else if(filterStatus.equalsIgnoreCase("Stage 2 In Progress")) {
			queryString += " and erp_work_flow_process.process_code in ('EMP_STAGE2_PO_SCHEDULED','EMP_STAGE2_PO_RESCHEDULED')";
		} else if(filterStatus.equalsIgnoreCase("Rejected")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE2_REJECTED'";
		} else if(filterStatus.equalsIgnoreCase("On Hold")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE2_ONHOLD'";
		}
		String finalquery = queryString;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalquery,Tuple.class);
			query.setParameter("departmentId", !Utils.isNullOrEmpty(departmentId)? departmentId:null);
			query.setParameter("locationId", !Utils.isNullOrEmpty(locationId)? locationId:null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getStageThreeApplicants(String departmentId, String locationId, String filterStatus) {
		String queryString = "select distinct emp_appln_entries.emp_appln_entries_id as id,emp_appln_entries.application_no as applicationNo,emp_appln_entries.applicant_name as name,"
				+ " emp_appln_personal_data.profile_photo_url as photoUrl,emp_appln_interview_schedules.interview_date_time as interviewDateTime,"
				+ " erp_work_flow_process.application_status_display_text as status,"
				+ " emp_appln_entries.personal_email_id as mail, emp_appln_entries.mobile_no_country_code as mobileCode,emp_appln_entries.mobile_no as mobileNumber, erp_work_flow_process.process_code as processCode,"
				+ " emp_appln_interview_schedules.interview_venue as venue,"
				+ " emp_appln_interview_schedules.emp_appln_interview_schedules_id as scheduleId,"
				+ "	emp_appln_interview_schedules.point_of_contact_users_id as pointOfContactId, "
				+ " url.file_name_unique, url.file_name_original, folder.upload_process_code,"
				+ " emp_appln_entries.shortlisted_department_id as departmentId ,erp_department.department_name as departmentName "
				+ " from emp_appln_entries"
				+ " inner join  emp_appln_personal_data on emp_appln_entries.emp_appln_entries_id = emp_appln_personal_data.emp_appln_entries_id and emp_appln_personal_data.record_status = 'A'"
				+ " inner join erp_work_flow_process on emp_appln_entries.application_current_process_status = erp_work_flow_process.erp_work_flow_process_id and erp_work_flow_process.record_status = 'A'"
				+ " left join emp_appln_interview_schedules on emp_appln_interview_schedules.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_interview_schedules.record_status = 'A' and emp_appln_interview_schedules.interview_round = 3"
				+ " left join erp_users ON erp_users.erp_users_id = emp_appln_interview_schedules.point_of_contact_users_id and erp_users.record_status = 'A' "
				+ "	left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status = 'A'"
				+ " inner join erp_department on emp_appln_entries.shortlisted_department_id = erp_department.erp_department_id and erp_department.record_status = 'A' and"
				+ " if(:departmentId is not null and  :departmentId != '',emp_appln_entries.shortlisted_department_id in (:departmentId),1=1)"
				+ " inner join erp_location ON erp_location.erp_location_id = emp_appln_entries.shortlisted_location_id and erp_location.record_status = 'A' and"
				+ " if(:locationId is not null and  :locationId != '',emp_appln_entries.shortlisted_location_id in (:locationId),1=1)  "
				+ " left join url_access_link url ON emp_appln_personal_data.profile_photo_url_id = url.url_access_link_id"
				+ " left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
				+ " where emp_appln_entries.record_status = 'A'";
		if(filterStatus.equalsIgnoreCase("Schedule Accepted")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED'";
		} else if(filterStatus.equalsIgnoreCase("Schedule Declined")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED'";
		} else if(filterStatus.equalsIgnoreCase("To be Scheduled")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE2_SELECTED'";
		} else if(filterStatus.equalsIgnoreCase("Scheduled")) {
			queryString += " and erp_work_flow_process.process_code in('EMP_STAGE3_PO_SCHEDULED','EMP_STAGE3_PO_RESCHEDULED')";
		}
		String finalquery = queryString;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalquery,Tuple.class);
			query.setParameter("departmentId", !Utils.isNullOrEmpty(departmentId)? departmentId:null);
			query.setParameter("locationId", !Utils.isNullOrEmpty(locationId)? locationId:null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnEntriesDBO> getEmpApplnEntriesDbos(List<Integer> entriesIds) {
		String query = "select dbo from EmpApplnEntriesDBO where dbo.recordStatus = 'A' and dbo.id in (:entriesIds)";
		return sessionFactory.withSession(s -> s.createQuery(query,EmpApplnEntriesDBO.class).setParameter("entriesIds", entriesIds).getResultList()).await().indefinitely();
	}

	public List<ErpWorkFlowProcessNotificationsDBO> getErpNotifications(List<Integer> processCodeList) {
		String query = " select distinct dbo from ErpWorkFlowProcessNotificationsDBO dbo  where dbo.recordStatus = 'A' and dbo.erpWorkFlowProcessDBO.id in (:code)";
		return sessionFactory.withSession(s->s.createQuery(query,ErpWorkFlowProcessNotificationsDBO.class).setParameter("code", processCodeList).getResultList()).await().indefinitely();
	}

	public Boolean update(List<Object> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;
	}

	public List<EmpApplnInterviewSchedulesDBO> getInterviewSchedules(List<Integer> empApplnEntriesId) {
		String query = "select dbo from EmpApplnInterviewSchedulesDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.empApplnEntriesDBO.id in (:empApplnEntriesId) and dbo.interviewRound = 3";
		return sessionFactory.withSession(s->s.createQuery(query,EmpApplnInterviewSchedulesDBO.class).setParameter("empApplnEntriesId", empApplnEntriesId).getResultList()).await().indefinitely();
	}

	public List<Tuple> getStageThreeInterviewStatus(String departmentId, String locationId, String filterStatus) {
		String queryString = "select distinct emp_appln_entries.emp_appln_entries_id as id,emp_appln_entries.application_no as applicationNo,emp_appln_entries.applicant_name as name,"
				+ " emp_appln_personal_data.profile_photo_url as photoUrl,emp_appln_interview_schedules.interview_date_time as interviewDateTime,"
				+ " erp_work_flow_process.application_status_display_text as status,"
				+ " emp_appln_interview_schedules.interview_venue as venue,"
				+ " emp_appln_interview_schedules.emp_appln_interview_schedules_id as scheduleId,"
				+ " emp_appln_interview_schedules.point_of_contact_users_id as pointOfContactId, "
				+ " url.file_name_unique, url.file_name_original, folder.upload_process_code,"
				+ " emp_job_details.stage3_comments,"
				+ " emp_appln_entries.shortlisted_department_id as departmentId ,erp_department.department_name as departmentName  "
				+ " from emp_appln_entries"
				+ " inner join  emp_appln_personal_data on emp_appln_entries.emp_appln_entries_id = emp_appln_personal_data.emp_appln_entries_id and emp_appln_personal_data.record_status = 'A'"
				+ " inner join erp_work_flow_process on emp_appln_entries.application_current_process_status = erp_work_flow_process.erp_work_flow_process_id and erp_work_flow_process.record_status = 'A'"
				+ " left join emp_appln_interview_schedules on emp_appln_interview_schedules.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_interview_schedules.record_status = 'A' and emp_appln_interview_schedules.interview_round = 3"
				+ " left join erp_users ON erp_users.erp_users_id = emp_appln_interview_schedules.point_of_contact_users_id and erp_users.record_status = 'A'"
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status = 'A'"
				+ " left join erp_department on emp_appln_entries.shortlisted_department_id = erp_department.erp_department_id and erp_department.record_status = 'A' and"
				+ " if(:departmentId is not null and  :departmentId != '',emp_appln_entries.shortlisted_department_id in (:departmentId),1=1)"
				+ " left join erp_location ON erp_location.erp_location_id = emp_appln_entries.shortlisted_location_id and erp_location.record_status = 'A' and"
				+ " if(:locationId is not null and  :locationId != '',emp_appln_entries.shortlisted_location_id in (:locationId),1=1)   "
				+ " left join url_access_link url ON emp_appln_personal_data.profile_photo_url_id = url.url_access_link_id"
				+ " left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
				+ " left join emp_job_details on emp_appln_entries.emp_appln_entries_id = emp_job_details.emp_appln_entries_id and emp_job_details.record_status = 'A'"
				+ " where emp_appln_entries.record_status = 'A'";
		if(filterStatus.equalsIgnoreCase("Schedule Accepted")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED'";
		} else if(filterStatus.equalsIgnoreCase("Schedule Declined")) {
			queryString += " and erp_work_flow_process.process_code = 'EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED'";
		}
		//		else if(filterStatus.equalsIgnoreCase("Pending")) {
		//			queryString += " and erp_work_flow_process.process_code in ('EMP_STAGE3_PO_SCHEDULED','EMP_STAGE3_PO_RESHEDULED')";
		//		} 
		else if(filterStatus.equalsIgnoreCase("Stage 3 In Progress")) {
			queryString += " and erp_work_flow_process.process_code in ('EMP_STAGE3_PO_SCHEDULED','EMP_STAGE3_PO_RESCHEDULED')";
		} else if(filterStatus.equalsIgnoreCase("Selected")) {
			queryString += " and erp_work_flow_process.process_code in ('EMP_STAGE3_SELECTED','EMP_OFFER_LETTER_GENERATED','EMP_OFFER_LETTER_REGENERATE')";
		} else if(filterStatus.equalsIgnoreCase("Rejected")) {
			queryString += " and erp_work_flow_process.process_code in ('EMP_STAGE3_REJECTED','EMP_REGRET_LETTER_GENERATED')";
		} else if(filterStatus.equalsIgnoreCase("On Hold")) {
			queryString += " and erp_work_flow_process.process_code in ('EMP_STAGE3_ONHOLD')";
		}
		String finalquery = queryString;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalquery,Tuple.class);
			query.setParameter("departmentId", !Utils.isNullOrEmpty(departmentId)? departmentId:null);
			query.setParameter("locationId", !Utils.isNullOrEmpty(locationId)? locationId:null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public EmpApplnInterviewSchedulesDBO getApplicantsInterviewScoreDetails(String empApplnEntriesId) {
		String query = "select distinct dbo from EmpApplnInterviewSchedulesDBO dbo"
				+ " left join fetch dbo.empApplnInterviewScoreDBO eais"
				+ " left join fetch eais.empApplnInterviewScoreDetailsMap eaisd"
				+ " left join fetch dbo.empApplnInterviewPanelDBO eaip "
				+ " where dbo.recordStatus = 'A' and dbo.empApplnEntriesDBO.id = :empApplnEntriesId and dbo.interviewRound = 1";
		return sessionFactory.withSession(s->s.createQuery(query,EmpApplnInterviewSchedulesDBO.class).setParameter("empApplnEntriesId", Integer.parseInt(empApplnEntriesId)).getSingleResultOrNull()).await().indefinitely();
	}

	public List<EmpApplnInterviewSchedulesDBO> getAvergeScore(List<Integer> empApplnEntriesId) {
		String query = "select distinct dbo from EmpApplnInterviewSchedulesDBO dbo"
				+ " left join fetch dbo.empApplnInterviewScoreDBO eais"
				+ " where dbo.recordStatus = 'A' and dbo.empApplnEntriesDBO.id in (:empApplnEntriesId) and dbo.interviewRound = 1";
		return sessionFactory.withSession(s->s.createQuery(query,EmpApplnInterviewSchedulesDBO.class).setParameter("empApplnEntriesId", empApplnEntriesId).getResultList()).await().indefinitely();
	}

	public List<Tuple> getEmpName(List<Integer> empIds) {
		String query = "select distinct emp.emp_id as empId,emp.emp_name as empName from erp_users"
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status = 'A'"
				+ " where emp.emp_id in (:empIds)";
		return sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}

	public List<EmpApplnInterviewSchedulesDBO> getInterviewScheduleDetailsTwo(List<Integer> interviewScheduleIds) {
		String str = " select DISTINCT dbo from EmpApplnInterviewSchedulesDBO dbo "
				+ " left join fetch dbo.empApplnInterviewPanelDBO as panel "
				+ " where dbo.recordStatus='A' and dbo.id IN (:interviewScheduleIds) and dbo.interviewRound = 2";
		String finalStr = str;
		List<EmpApplnInterviewSchedulesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnInterviewSchedulesDBO> query = s.createQuery(finalStr, EmpApplnInterviewSchedulesDBO.class);
			query.setParameter("interviewScheduleIds", interviewScheduleIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getInternalPanel(String departmentId, String locationId) {
		String str = "select  emp_interview_panelist.internal_erp_users_id as ID,ifnull(emp.emp_name,erp_users.user_name) as Text "
				+ " from emp_interview_panelist"
				+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.internal_erp_users_id"
				+ " and erp_users.record_status='A'"
				+ " left join emp on emp.emp_id=erp_users.emp_id"
				+ " and emp.record_status='A'"
				+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
				+ " inner join (select "
				+ "  emp_interview_panelist.erp_department_id,emp_interview_panelist.erp_location_id,max(erp_academic_year.academic_year) as academic_year"
				+ " from emp_interview_panelist"
				+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
				+ " where emp_interview_panelist.erp_department_id=:departmentId"
				+ " and emp_interview_panelist.erp_location_id=:locationId"
				+ " and emp_interview_panelist.record_status='A'"
				+ " and erp_academic_year.record_status='A'"
				+ " and internal_erp_users_id is not null"
				+ " group by emp_interview_panelist.erp_department_id,emp_interview_panelist.erp_location_id) as internal_panelist ON internal_panelist.academic_year = erp_academic_year.academic_year"
				+ " and internal_panelist.erp_department_id = emp_interview_panelist.erp_department_id"
				+ " and internal_panelist.erp_location_id = emp_interview_panelist.erp_location_id"
				+ " where emp_interview_panelist.record_status='A'"
				+ " and erp_academic_year.record_status='A'"
				+ " group by emp_interview_panelist.internal_erp_users_id";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("departmentId",  !Utils.isNullOrEmpty(departmentId)? Integer.parseInt(departmentId):null);
			query.setParameter("locationId",  !Utils.isNullOrEmpty(locationId)? Integer.parseInt(locationId):null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}	

	public List<Tuple> getExternalPanel(String departmentId, String locationId) {
		String str = " select  emp_interview_panelist.external_erp_users_id as ID,ifnull(emp.emp_name,erp_users.user_name) as Text,true as status"
				+ " from emp_interview_panelist"
				+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.external_erp_users_id"
				+ " and erp_users.record_status='A'"
				+ " left join emp on emp.emp_id=erp_users.emp_id"
				+ " and emp.record_status='A'"
				+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
				+ " inner join (select "
				+ " emp_interview_panelist.erp_department_id,emp_interview_panelist.erp_location_id,max(erp_academic_year.academic_year) as academic_year"
				+ " from emp_interview_panelist "
				+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
				+ " where emp_interview_panelist.erp_department_id= :departmentId"
				+ " and emp_interview_panelist.erp_location_id= :locationId"
				+ " and emp_interview_panelist.record_status='A'"
				+ " and erp_academic_year.record_status='A'"
				+ " and external_erp_users_id is not null"
				+ " group by emp_interview_panelist.erp_department_id,emp_interview_panelist.erp_location_id"
				+ " ) as external_panelist ON external_panelist.academic_year = erp_academic_year.academic_year"
				+ " and external_panelist.erp_department_id = emp_interview_panelist.erp_department_id"
				+ " and external_panelist.erp_location_id = emp_interview_panelist.erp_location_id"
				+ " where emp_interview_panelist.record_status='A'"
				+ " and erp_academic_year.record_status='A'"
				+ " group by emp_interview_panelist.external_erp_users_id"
				+ " UNION"
				+ " select emp_interview_panelist.emp_interview_university_externals_id as ID,emp_interview_university_externals.panelist_name as Text,false as status"
				+ " from emp_interview_panelist "
				+ " inner join emp_interview_university_externals on emp_interview_university_externals.emp_interview_university_externals_id=emp_interview_panelist.emp_interview_university_externals_id"
				+ " and emp_interview_university_externals.record_status ='A'"
				+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
				+ " inner join (select "
				+ " emp_interview_panelist.erp_department_id,emp_interview_panelist.erp_location_id,max(erp_academic_year.academic_year) as academic_year"
				+ " from emp_interview_panelist "
				+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
				+ " where emp_interview_panelist.erp_department_id= :departmentId"
				+ " and emp_interview_panelist.erp_location_id= :locationId"
				+ " and emp_interview_panelist.record_status='A'"
				+ " and erp_academic_year.record_status='A'"
				+ " and emp_interview_university_externals_id is not null"
				+ " group by emp_interview_panelist.erp_department_id,emp_interview_panelist.erp_location_id"
				+ " ) as university_external ON university_external.academic_year = erp_academic_year.academic_year"
				+ " and university_external.erp_department_id = emp_interview_panelist.erp_department_id"
				+ " and university_external.erp_location_id = emp_interview_panelist.erp_location_id"
				+ " where emp_interview_panelist.record_status='A'"
				+ " and erp_academic_year.record_status='A'"
				+ " group by emp_interview_panelist.emp_interview_university_externals_id ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("departmentId", !Utils.isNullOrEmpty(departmentId)? Integer.parseInt(departmentId):null);
			query.setParameter("locationId", !Utils.isNullOrEmpty(locationId)? Integer.parseInt(locationId):null);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getEmpFromUser(Set<Integer> userIds) {
		String str = " Select user.erp_users_id as erp_users_id,emp.emp_name as name "
				+ " from erp_users as user "
				+ " inner join emp on emp.emp_id = user.emp_id "
				+ " where  user.erp_users_id in (:userIds)";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("userIds", userIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
	public Tuple getInterview_template(String applicationNo) {
		String query = "select emp_appln_interview_template.emp_appln_interview_template_id as templateId"
				+ "	 from emp_appln_entries"
				+ "  inner join emp_employee_category on emp_appln_entries.emp_employee_category_id = emp_employee_category.emp_employee_category_id"
				+ "  inner join emp_appln_interview_template on emp_appln_interview_template.emp_employee_category_id = emp_employee_category.emp_employee_category_id"
				+ "	 where emp_appln_entries.application_no = :applicationNo and emp_appln_entries.record_status='A'  and emp_employee_category.record_status = 'A'"
				+ "  and emp_appln_interview_template.record_status = 'A'";
		return sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("applicationNo", Integer.parseInt(applicationNo)).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<Tuple> getInterviewParameters(String interviewTemplateId) {
		String query = " select parameter_name as parameterName,parameter_max_score as maxScore,is_auto_calculate as acal, parameter_order_no as orderNo from emp_appln_interview_template_group_details"
				+ " inner join emp_appln_interview_template_group ON emp_appln_interview_template_group.emp_appln_interview_template_group_id = emp_appln_interview_template_group_details.emp_appln_interview_template_group_id"
				+ " inner join emp_appln_interview_template ON emp_appln_interview_template.emp_appln_interview_template_id = emp_appln_interview_template_group.emp_appln_interview_template_id"
				+ " where emp_appln_interview_template_group_details.record_status = 'A' and emp_appln_interview_template_group.record_status = 'A' and emp_appln_interview_template.record_status = 'A'"
				+ " and emp_appln_interview_template.emp_appln_interview_template_id = :interviewTemplateId";
		return sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("interviewTemplateId", Integer.parseInt(interviewTemplateId)).getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getExternal(List<Integer> contactExternal) {
		String query = " select emp_interview_university_externals.emp_interview_university_externals_id as emp_interview_university_externals_id,emp_interview_university_externals.panelist_name as empName,emp_interview_university_externals.panelist_email as email,"
				+ " emp_interview_university_externals.panelist_mobile_no_country_code as mobileCountry,emp_interview_university_externals.panelist_mobile_no as mobileNo"
				+ " from emp_interview_university_externals"
				+ " where emp_interview_university_externals.record_status = 'A' and emp_interview_university_externals.emp_interview_university_externals_id in (:contactExternal)";
		return sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("contactExternal",contactExternal).getResultList()).await().indefinitely();
	}

	public List<Tuple> getPointOfContact(List<Integer> point) {
		String str = " SELECT erp_users.erp_users_id as erpUserId,emp.emp_name AS name, CONCAT('(', erp_department.department_name, ')') AS department "
				+ "FROM erp_users "
				+ " LEFT JOIN emp ON emp.emp_id = IFNULL(erp_users.emp_id, erp_users.erp_users_name) "
				+ " LEFT JOIN erp_campus_department_mapping ON emp.erp_campus_department_mapping_id = erp_campus_department_mapping.erp_campus_department_mapping_id "
				+ " LEFT JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id "
				+ " WHERE erp_users.erp_users_id IN (:point) ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("point", point);
			return query.getResultList();
		}).await().indefinitely();
		return list; 
	}

	public List<Tuple> getSubSpe(List<Integer> entriesIdList) {
		String str = " Select emp_appln_entries.emp_appln_entries_id as emp_appln_entries_id,emp_appln_subject_category_specialization.subject_category_specialization_name as subject_category_specialization_name,"
				+ " emp_appln_subject_category.subject_category_name as subject_category_name,emp_appln_subject_category.emp_appln_subject_category_id as emp_appln_subject_category_id "
				+ " from emp_appln_subj_specialization_pref "
				+ " left join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_subj_specialization_pref.emp_appln_entries_id and emp_appln_entries.record_status = 'A'"
				+ " left join emp_appln_subject_category_specialization ON emp_appln_subject_category_specialization.emp_appln_subject_category_specialization_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_specialization_id"
				+ " left join emp_appln_subject_category ON emp_appln_subject_category.emp_appln_subject_category_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_id"
				+ " where emp_appln_entries.emp_appln_entries_id in (:entriesIdList) and emp_appln_subj_specialization_pref.record_status = 'A' ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("entriesIdList", entriesIdList);
			return query.getResultList();
		}).await().indefinitely();
		return list; 
	}
	
	public EmpApplnEntriesDBO getData(Integer entriesId) {
		return sessionFactory.withSession(session->session.createQuery("select dbo from EmpApplnEntriesDBO dbo " +
				" where dbo.recordStatus='A' " +
				" and dbo.id=:entriesId", EmpApplnEntriesDBO.class)
				.setParameter("entriesId", entriesId).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<Tuple>> getPanelList() {
		String str = " select erp_users.erp_users_id as ID, CONCAT(emp.emp_name, ' (', erp_department.department_name, ')') as Text "
				+ " from erp_users "
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " inner join erp_campus_department_mapping on emp.erp_campus_department_mapping_id = erp_campus_department_mapping.erp_campus_department_mapping_id "
				+ " inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
				+ " inner join erp_department on erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id "
				+ " where  erp_users.record_status='A' order by emp.emp_name ASC";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());		
	}
}
