package com.christ.erp.services.transactions.employee.recruitment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.dbobjects.employee.common.*;
import com.christ.erp.services.dbobjects.employee.recruitment.*;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.*;
import com.christ.erp.services.dto.employee.common.EmpMajorAchievementsDTO;
import com.christ.erp.services.dto.employee.profile.*;
import com.christ.erp.services.dto.support.settings.SupportCategoryDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.common.EmpDTO;

import reactor.core.publisher.Mono;
@Repository
public class EmployeeProfileTransaction {
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	public List<Tuple> searchEmployees(EmpDTO empDTO) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String query = "select DISTINCT emp.emp_id,emp.emp_name,emp.emp_no,emp.emp_university_email,ec.campus_name,edep.department_name,"
						+ " ejc.employee_job_name, ed.emp_designation_name,epd.profile_photo_url,emp.record_status "
						+ " from emp "
						+ " left join erp_campus_department_mapping ecdm on ecdm.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id "
						+ " left join emp_personal_data epd on epd.emp_personal_data_id=emp.emp_personal_data_id " 
						+ " left join erp_campus ec on ec.erp_campus_id=ecdm.erp_campus_id "
						+ " left join erp_department edep on edep.erp_department_id=ecdm.erp_department_id "
						+ " left join emp_employee_job_category ejc on ejc.emp_employee_job_category_id=emp.emp_employee_job_category_id "
						+ " left join emp_designation ed on ed.emp_designation_id=emp.emp_designation_id "
						+ " where  emp.record_status=:recordStatus  ";
				if(!Utils.isNullOrEmpty(empDTO.empNo)) {
					query += " and emp.emp_no=:empNo ";
				}
				if(!Utils.isNullOrEmpty(empDTO.empName)) {
					query += " and emp.emp_name like  CONCAT('%',:empName, '%') ";
				}
				if(!Utils.isNullOrEmpty(empDTO.campusId)) {
					query += " and ecdm.erp_campus_id = :campusId ";
				}
				if(!Utils.isNullOrEmpty(empDTO.departmentId)) {
					query += " and  ecdm.erp_department_id = :departmentId ";
				}
				if(!Utils.isNullOrEmpty(empDTO.jobCategoryId)) {
					query += " and emp.emp_employee_job_category_id=:jobCategoryId ";
				}
				if(!Utils.isNullOrEmpty(empDTO.employeeCategoryId)) {
					query += " and emp.emp_employee_category_id=:employeeCategoryId ";
				}
				query += "order by emp.emp_no";
				Query q = context.createNativeQuery(query, Tuple.class);
				if(!Utils.isNullOrEmpty(empDTO.empNo)) {
					q.setParameter("empNo", Integer.parseInt(empDTO.empNo));
				}
				if(!Utils.isNullOrEmpty(empDTO.empName)) {
					q.setParameter("empName", empDTO.empName);
				}
				if(!Utils.isNullOrEmpty(empDTO.campusId)) {
					q.setParameter("campusId", Integer.parseInt(empDTO.campusId));
				}
				if(!Utils.isNullOrEmpty(empDTO.departmentId)) {
					q.setParameter("departmentId", Integer.parseInt(empDTO.departmentId));
				}
				if(!Utils.isNullOrEmpty(empDTO.jobCategoryId)) {
					q.setParameter("jobCategoryId", Integer.parseInt(empDTO.jobCategoryId));
				}
				if(!Utils.isNullOrEmpty(empDTO.employeeCategoryId)) {
					q.setParameter("employeeCategoryId", Integer.parseInt(empDTO.employeeCategoryId));
				}
				if(!Utils.isNullOrEmpty(empDTO.isActive)) {
					if(empDTO.isActive) {
						q.setParameter("recordStatus", 'A');
					}
					else {
						q.setParameter("recordStatus", 'I');
					}
				}
				return q.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Tuple getEmployee(String empId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@SuppressWarnings("unchecked")
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String query = "select " +
						" e.emp_no,e.emp_name,e.deputation_start_date,e.mobile_no,e.moble_no_country_code,e.emp_university_email, " +
						" e.emp_personal_email,e.emp_id,e.dob,e.doj,IF(e.record_status='A', 'Yes', 'No') is_employee_active,eae.application_no,epd.emp_personal_data_id, " +
						"	IF(epd.is_differently_abled=true, 'Yes', 'No') is_differently_abled,epd.highest_qualification_album,epd.current_address_line_1,epd.current_address_line_2, " +
						"	epd.current_pincode,epd.is_permanent_equals_current " +
						"	,epd.permanent_address_line_1,epd.permanent_address_line_2, epd.orcid_no as orcid_no , epd.scopus_no as scopus_no , epd.vidwan_no as vidwan_no ," +
						"	epd.permanent_pincode,epd.profile_photo_url,epd.current_city_others as current_city_others,epd.permanent_city_others as permanent_city_others,epd.current_city_id as current_city_id,epd.permanent_city_id as permanent_city_id,currentCity.city_name as currentCityname,permenantCity.city_name as permenantCityname, " +
						"	eapd.aadhar_no as aadhar_no,eapd.adhar_upload_url as adhar_upload_url,eapd.pan_no,eapd.pan_upload_url as pan_upload_url ,eapd.four_wheeler_no,eapd.two_wheeler_no,eapd.passport_no,eapd.passport_issued_place,eapd.passport_status,\n" +
						"	eapd.passport_issued_date,eapd.passport_date_of_expiry,eapd.passport_upload_url,eapd.passport_comments, " +
						"	eapd.visa_no,eapd.visa_status,eapd.visa_issued_date,eapd.visa_date_of_expiry,eapd.visa_upload_url,eapd.visa_comments, " +
						"	eapd.frro_no,eapd.frro_status,eapd.frro_issued_date,eapd.frro_date_of_expiry,eapd.frro_upload_url,eapd.frro_comments, " +
						"	eapd.emergency_contact_name,eapd.emergency_contact_address,eapd.emergency_contact_relatonship,eapd.emergency_mobile_no, " +
						"	eapd.emergency_contact_home,eapd.emergency_contact_work,eapd.four_wheeler_document_url,eapd.two_wheeler_document_url,eapd.family_background_brief, " +
						"	eg.erp_gender_id,eg.gender_name, " +
						"	ems.erp_marital_status_id,ems.marital_status_name,ebg.erp_blood_group_id,ebg.blood_group_name, " +
						"	er.erp_religion_id,er.religion_name, er.is_minority, " +
						"	erc.erp_reservation_category_id,erc.reservation_category_name,epc.is_employee_category_academic, " +
						"	eda.erp_differently_abled_id,eda.differently_abled_name,ejd.joining_date, " +
						"	ejd.emp_job_details_id,ejd.gratuity_date,ejd.gratuity_no,ejd.recognised_exp_years,ejd.recognised_exp_months, " +
						"	ejd.smart_card_no,ejd.branch_ifsc_code,IF(ejd.is_vacation_applicable=true, 'Yes', 'No') is_vacation_applicable,IF(ejd.is_display_website=true, 'Yes', 'No')  is_display_website,ejd.retirement_date,\n" +
						"	ejd.pf_account_no,ejd.pf_date,ejd.uan_no,ejd.sib_account_bank, " +
						"   ejd.contract_start_date as contract_start_date,ejd.contract_end_date as contract_end_date,ejd.contract_remarks as contract_remarks, "+
						"	IF(ejd.is_duty_roster_applicable=true, 'Yes', 'No') is_duty_roster_applicable, " +
						"	IF(ejd.is_holiday_time_zone_applicable=true, 'Yes', 'No') is_holiday_time_zone_applicable,IF(ejd.is_vacation_time_zone_applicable=true, 'Yes', 'No') is_vacation_time_zone_applicable\n" +
						"	,etz.emp_time_zone_id e_time_id,etz.time_zone_name e_time_name,htz.emp_time_zone_id h_time_id,htz.time_zone_name h_time_name,  " +
						"	vtz.emp_time_zone_id v_time_id,vtz.time_zone_name v_time_name, " +
						"	epc.emp_employee_category_id,epc.employee_category_name, " +
						"	eejc.emp_employee_job_category_id,eejc.employee_job_name,eejc.job_category_code, " +
						"	eeg.emp_employee_group_id,eeg.employee_group_name, " +
						"	eca.erp_campus_id,eca.campus_name,ed.erp_department_id,ed.department_name, " +
						"	et.emp_title_id,et.title_name, " +
						"	eb.erp_block_id,eb.block_name, " +
						"	ef.erp_floors_id,ef.floor_name, " +
						"	erooms.room_no,erooms.erp_rooms_id, " +
						"	erm.telephone_number,erm.telephone_extension, " +
						"	eres.date_of_leaving,eres.relieving_date,eres.submission_date,eres.hod_recommended_relieving_date,eresig.resignation_name,eresig.emp_resignation_reason_id, " +
						"	eres.is_serving_notice_period,eres.reference_no,eres.emp_resignation_id, " +
						"	eres.is_po_recommendation,eres.vc_accepted_date,eres.po_remarks,eapp.emp_approvers_id, " +
						"	eapp.leave_approver_id, " +
						"	eapp.leave_authoriser_id, " +
						"	if(eapp.leave_approver_id = null,\"\",(select emp_name from emp where emp.emp_id = eapp.leave_approver_id)) as leave_approver,  " +
						"	if(eapp.leave_authoriser_id = null,\"\",(select emp_name from emp where emp.emp_id = eapp.leave_authoriser_id)) as leave_authriser, " +
						"	if(eapp.level_one_appraiser_id = null,\"\",(select emp_name from emp where emp.emp_id = eapp.level_one_appraiser_id)) as level1_appraiser, " +
						"	if(eapp.level_two_appraiser_id = null,\"\",(select emp_name from emp where emp.emp_id = eapp.level_two_appraiser_id)) as leve12_appraiser, " +
						"	if(eapp.work_diary_approver_id = null,\"\",(select emp_name from emp where emp.emp_id = eapp.work_diary_approver_id)) as workdiary_approver, " +
						"	eapp.level_one_appraiser_id, " +
						"	eapp.level_two_appraiser_id, " +
						"	eapp.work_diary_approver_id, " +
						"	edes.emp_designation_id,edes.emp_designation_name, "+
						//						" 	egcd.emp_guest_contract_details_id,egcd.contract_emp_start_date,\n" +
						//						"	egcd.contract_emp_end_date,egcd.guest_contract_remarks,egcd.guest_reffered_by,egcd.guest_subject_specilization,\n" +
						"	eql.erp_qualification_level_id,eql.qualification_level_name " +
						"	,epsd.pay_scale_type,epsd.wage_rate_per_type, epsd.gross_pay,epsg.emp_pay_scale_grade_id,epsg.grade_name, epsgmd.emp_pay_scale_level_id,epsmd.level_cell_no,epsgmd.pay_scale, " +
						"	pd_country.erp_country_id pd_country_id,pd_country.country_name pd_country_name,pd_country.nationality_name as pd_nationality_name, " +
						"	ec_current.erp_country_id as ec_current_id,ec_current.country_name as ec_current_name,ec_permanent.erp_country_id as ec_permanent_id,ec_permanent.country_name as ec_permanent_name, " +
						"	es_current.erp_state_id as es_current_id,es_current.state_name as es_current_name,es_permanent.erp_state_id as es_permanent_id,es_permanent.state_name as es_permanent_name, " +
						"	elca.emp_leave_category_allotment_id,elca.emp_leave_category_allotment_name,sa.emp_designation_id as sa_id,sa.emp_designation_name as sa_name,eeasc.emp_appln_subject_category_id,  eeasc.subject_category_name,eascs.emp_appln_subject_category_specialization_id,eascs.subject_category_specialization_name,epsmd.emp_pay_scale_matrix_detail_id as cell_id,empPayScaleGradeMapping.pay_scale_revised_year, " +
						"	epsgmd.emp_pay_scale_grade_mapping_detail_id as level_id " +
						"	from  emp e " +
						"   left join emp_appln_entries eae on eae.emp_appln_entries_id = e.emp_appln_entries_id and eae.record_status = 'A' " +
						"	left join emp_personal_data epd on epd.emp_personal_data_id=e.emp_personal_data_id and epd.record_status = 'A' " +
						"	left join erp_gender eg on eg.erp_gender_id=e.erp_gender_id and eg.record_status = 'A' " +
						"	left join erp_marital_status ems on ems.erp_marital_status_id=epd.erp_marital_status_id and ems.record_status = 'A' " +
						"	left join erp_blood_group ebg on ebg.erp_blood_group_id=epd.erp_blood_group_id and ebg.record_status = 'A' " +
						"	left join erp_religion er on er.erp_religion_id=epd.erp_religion_id and er.record_status = 'A' " +
						"	left join emp_addtnl_personal_data eapd on eapd.emp_personal_data_id=epd.emp_personal_data_id and eapd.record_status = 'A' " +
						"	left join erp_reservation_category erc on erc.erp_reservation_category_id=epd.erp_reservation_category_id and erc.record_status = 'A' " +
						"	left join erp_differently_abled eda on eda.erp_differently_abled_id = epd.erp_differently_abled_id and eda.record_status = 'A' " +
						"	left join emp_job_details ejd on ejd.emp_job_details_id = e.emp_job_details_id and ejd.record_status = 'A' " +
						"	left join erp_country pd_country on pd_country.erp_country_id=epd.erp_country_id and pd_country.record_status = 'A' " +
						"	left join erp_country ec_current on ec_current.erp_country_id=epd.current_country_id and ec_current.record_status = 'A' " +
						"	left join erp_country ec_permanent  on ec_permanent.erp_country_id=epd.permanent_country_id and ec_permanent.record_status = 'A' " +
						"	left join erp_state es_current on es_current.erp_state_id=epd.current_state_id and es_current.record_status = 'A' " +
						"	left join erp_state es_permanent  on es_permanent.erp_state_id=epd.permanent_state_id and es_permanent.record_status = 'A' " +
						"	left join emp_time_zone etz on etz.emp_time_zone_id=e.emp_time_zone_id and etz.record_status = 'A' " +
						"	left join emp_time_zone vtz on vtz.emp_time_zone_id = ejd.vacation_time_zone_id and vtz.record_status = 'A' " +
						"	left join emp_time_zone htz on htz.emp_time_zone_id = ejd.holiday_time_zone_id and htz.record_status = 'A' " +
						"	left join emp_employee_category epc on epc.emp_employee_category_id = e.emp_employee_category_id and epc.record_status = 'A' " +
						"	left join emp_employee_job_category eejc on eejc.emp_employee_job_category_id = e.emp_employee_job_category_id and eejc.record_status = 'A' " +
						"	left join emp_employee_group eeg on eeg.emp_employee_group_id=e.emp_group_id and eeg.record_status = 'A' " +
						"	left join erp_campus_department_mapping ecdm on ecdm.erp_campus_department_mapping_id = e.erp_campus_department_mapping_id and ecdm.record_status = 'A' " +
						"	left join erp_campus eca on eca.erp_campus_id=ecdm.erp_campus_id and eca.record_status = 'A' " +
						"	left join erp_department ed on ed.erp_department_id = ecdm.erp_department_id and ed.record_status = 'A' " +
						"	left join emp_designation edes on edes.emp_designation_id=e.emp_designation_id and edes.record_status = 'A' " +
						"   left join emp_designation sa on sa.emp_designation_id = e.emp_album_designation_id and sa.record_status = 'A' " +
						"	left join emp_title et on et.emp_title_id = e.emp_title_id and et.record_status = 'A' " +
						"	left join erp_room_emp_mapping erm on erm.emp_id=e.emp_id and erm.record_status = 'A' " +
						"	left join erp_rooms erooms on erooms.erp_rooms_id = erm.erp_rooms_id and erooms.record_status = 'A' " +
						"	left join erp_floors ef on ef.erp_floors_id = erooms.erp_floors_id and ef.record_status = 'A' " +
						"	left join erp_block eb on eb.erp_block_id=erooms.erp_block_id and eb.record_status = 'A' " +
						"	left join emp_resignation eres on eres.emp_id=e.emp_id and eres.record_status = 'A' " +
						"	left join emp_resignation_reason eresig on eresig.emp_resignation_reason_id=eres.emp_resignation_reason_id and eresig.record_status = 'A' " +
						//						"	left join emp_guest_contract_details egcd on egcd.emp_id=e.emp_id and egcd.record_status = 'A'\n" +
						"	left join emp_approvers eapp on eapp.emp_id=e.emp_id and eapp.record_status = 'A'  " +
						"	left join erp_qualification_level eql on eql.erp_qualification_level_id=epd.erp_qualification_level_id and eql.record_status = 'A' " +
						"	left join emp_pay_scale_details epsd on epsd.emp_pay_scale_details_id=ejd.emp_pay_scale_details_id and epsd.record_status = 'A' " +
						"	left join emp_pay_scale_matrix_detail  epsmd on epsmd.emp_pay_scale_matrix_detail_id=epsd.emp_pay_scale_matrix_detail_id and epsmd.record_status = 'A' " +
						"	left join emp_pay_scale_grade_mapping_detail epsgmd on epsgmd.emp_pay_scale_grade_mapping_detail_id=epsmd.emp_pay_scale_grade_mapping_detail_id and epsgmd.record_status = 'A' " +
						"	left join emp_pay_scale_grade_mapping epsgm on epsgm.emp_pay_scale_grade_mapping_id=epsgmd.emp_pay_scale_grade_mapping_id and epsgm.record_status = 'A' " +
						"	left join emp_pay_scale_grade epsg on epsg.emp_pay_scale_grade_id=epsgm.emp_pay_scale_grade_id and epsg.record_status = 'A' " +
						"   left join emp_pay_scale_grade_mapping as empPayScaleGradeMapping ON empPayScaleGradeMapping.emp_pay_scale_grade_mapping_id= epsgmd.emp_pay_scale_grade_mapping_id and empPayScaleGradeMapping.record_status = 'A' " +
						"	left join emp_leave_category_allotment  elca on elca.emp_leave_category_allotment_id=ejd.emp_leave_category_allotment_id and elca.record_status = 'A' " +
						"   left join emp_appln_subject_category eeasc on eeasc.emp_appln_subject_category_id = e.emp_appln_subject_category_id  and eeasc.record_status = 'A' " +
						"	left join emp_appln_subject_category_specialization eascs on eascs.emp_appln_subject_category_specialization_id = e.emp_appln_subject_category_specialization_id and eascs.record_status = 'A' " +
						"	left join erp_city as currentCity on currentCity.erp_city_id = epd.current_city_id and currentCity.record_status = 'A' "+
						"   left join erp_city as permenantCity on permenantCity.erp_city_id = epd.permanent_city_id and permenantCity.record_status = 'A' "+
						" where e.emp_id=:empId";
				Query q = context.createNativeQuery(query, Tuple.class);
				q.setParameter("empId", Integer.parseInt(empId));
				return (Tuple)Utils.getUniqueResult(q.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmployeeFamilyDetails(String personalDataId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String query = "select efda.emp_family_details_addtnl_id,efda.relationship,efda.dependent_name,efda.dependent_dob, "
						+ " efda.dependent_qualification,efda.dependent_profession " 
						+ " FROM emp_family_details_addtnl efda where efda.emp_personal_data_id=:personalDataId and efda.record_status='A'";
				Query q = context.createNativeQuery(query, Tuple.class);
				q.setParameter("personalDataId", Integer.parseInt(personalDataId));
				return q.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpEducationalDetailsDBO> getEmployeeEducationalDetails(String empId){
		List<EmpEducationalDetailsDBO> empEducationalDetailsDBOList = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpEducationalDetailsDBO dbo  left join fetch dbo.documentsDBOSet as documentSet where dbo.recordStatus='A' and dbo.empDBO.id=:empId ", EmpEducationalDetailsDBO.class).setParameter("empId", Integer.parseInt(empId)).getResultList()).await().indefinitely();
		return empEducationalDetailsDBOList; 
	}

	public List<EmpEligibilityTestDBO> getEmployeeEligibilityTestDetails(String empId){
		List<EmpEligibilityTestDBO> empEligibilityTestDBOList = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpEligibilityTestDBO dbo  where dbo.recordStatus='A' and dbo.empDBO.id=:empId ", EmpEligibilityTestDBO.class).setParameter("empId", Integer.parseInt(empId)).getResultList()).await().indefinitely();
		return empEligibilityTestDBOList; 
	}

	public List<EmpWorkExperienceDBO> getEmployeeExperienceDetails(String empId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpWorkExperienceDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpWorkExperienceDBO> onRun(EntityManager context) throws Exception {
				String str ="from EmpWorkExperienceDBO where recordStatus='A' and empDBO.id=:empId" ;
				Query qry = context.createQuery(str.toString(),EmpWorkExperienceDBO.class);
				qry.setParameter("empId", Integer.parseInt(empId));
				return qry.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpPfGratuityNomineesDBO> getEmpPfNomineesDBOList(String jobDetailsId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpPfGratuityNomineesDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpPfGratuityNomineesDBO> onRun(EntityManager context) throws Exception {
				String str ="from EmpPfGratuityNomineesDBO where recordStatus='A' and empJobDetailsDBO.id=:jobDetailsId and isPf=1" ;
				Query qry = context.createQuery(str.toString(),EmpPfGratuityNomineesDBO.class);
				qry.setParameter("jobDetailsId", Integer.parseInt(jobDetailsId));
				return qry.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	//	public List<EmpPfGratuityNomineesDBO> getEmpGratuityNomineesDBOList(String jobDetailsId) throws Exception {
	//		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpPfGratuityNomineesDBO>>() {
	//			@SuppressWarnings("unchecked")
	//			@Override
	//	        public List<EmpPfGratuityNomineesDBO> onRun(EntityManager context) throws Exception {
	//				String str ="from EmpPfGratuityNomineesDBO where recordStatus='A' and empJobDetailsDBO.id=:jobDetailsId and isGratuity=true" ;
	//			    Query qry = context.createQuery(str.toString(),EmpPfGratuityNomineesDBO.class);
	//			    qry.setParameter("jobDetailsId", Integer.parseInt(jobDetailsId));
	//				return qry.getResultList();
	//	        }
	//	        @Override
	//	        public void onError(Exception error) throws Exception {
	//	        	throw error;
	//	        }
	//	    });
	//	}

	public List<EmpPfGratuityNomineesDBO> getEmpGratuityNomineesDBOList(String jobDetailsId){
		List<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOList = sessionFactory.withSession(s -> s.createQuery("from EmpPfGratuityNomineesDBO where recordStatus='A' and empJobDetailsDBO.id = :jobDetailsId and isGratuity=true", EmpPfGratuityNomineesDBO.class).setParameter("jobDetailsId", Integer.parseInt(jobDetailsId)).getResultList()).await().indefinitely();
		return empPfGratuityNomineesDBOList;
	}

	public ErpAcademicYearDBO getCurrentAcademicYear() {
		ErpAcademicYearDBO erpAcademicYearDBO = sessionFactory.withSession(s -> s.createQuery("from ErpAcademicYearDBO  where recordStatus='A' and isCurrentAcademicYear=1 ", ErpAcademicYearDBO.class).getSingleResultOrNull()).await().indefinitely();
		return erpAcademicYearDBO;
	}

	public List<EmpLeaveAllocationDBO> getEmployeeLeaveAllocation(int empId) {
		List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpLeaveAllocationDBO dbo where dbo.recordStatus = 'A' and dbo.empDBO.id = :empId", EmpLeaveAllocationDBO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
		return empLeaveAllocationDBOList;
	}

	public List<ErpQualificationLevelDBO> getErpQualificationLevel(){
		List<ErpQualificationLevelDBO> erpQualificationLevelDBOList = sessionFactory.withSession(s -> s.createQuery("select dbo from ErpQualificationLevelDBO dbo  where dbo.recordStatus='A' ", ErpQualificationLevelDBO.class).getResultList()).await().indefinitely();
		return erpQualificationLevelDBOList; 
	}

	public List<EmpPayScaleDetailsComponentsDBO> getEmpPayScaleDetailsComponents(String empId){
		List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOList = sessionFactory.withSession(s -> s.createQuery("from EmpPayScaleDetailsComponentsDBO  where recordStatus='A' and empPayScaleDetailsDBO.empDBO.id = :empId", EmpPayScaleDetailsComponentsDBO.class).setParameter("empId", Integer.parseInt(empId)).getResultList()).await().indefinitely();
		return empPayScaleDetailsComponentsDBOList;
	}

	public List<EmpRemarksDetailsDBO> getEmployeeRemarksDetails(String empId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpRemarksDetailsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpRemarksDetailsDBO> onRun(EntityManager context) throws Exception {
				String str ="from EmpRemarksDetailsDBO  where recordStatus='A' and empDBO.id=:empId" ;
				Query qry = context.createQuery(str.toString(),EmpRemarksDetailsDBO.class);
				qry.setParameter("empId", Integer.parseInt(empId));
				return qry.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveData(EmpDBO empDBO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(Utils.isNullOrEmpty(empDBO) || Utils.isNullOrEmpty(empDBO.id) || empDBO.id==0) {
					context.persist(empDBO);
				}
				else {
					context.merge(empDBO);
				} 
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpMajorAchievementsDBO> getEmpMajorAchievemnts(String empId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpMajorAchievementsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpMajorAchievementsDBO> onRun(EntityManager context) throws Exception {
				String str ="from EmpMajorAchievementsDBO  where recordStatus='A' and empDBO.id=:empId" ;
				Query qry = context.createQuery(str.toString(),EmpMajorAchievementsDBO.class);
				qry.setParameter("empId", Integer.parseInt(empId));
				return qry.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpRemarksDetailsDBO> getRemarksDetails(String empId){
		List<EmpRemarksDetailsDBO> empRemarksDetailsDBOList = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpRemarksDetailsDBO dbo  where dbo.recordStatus='A' and dbo.empDBO.id = :empId and dbo.isForOfficeUse=true", EmpRemarksDetailsDBO.class).setParameter("empId", Integer.parseInt(empId)).getResultList()).await().indefinitely();
		return empRemarksDetailsDBOList; 
	}

	public List<EmpGuestContractDetailsDBO> getGuestContractDetails(String empId){
		List<EmpGuestContractDetailsDBO> empGuestContractDetailsDBO = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpGuestContractDetailsDBO dbo  where dbo.recordStatus='A' and dbo.empDBO.id = :empId ", EmpGuestContractDetailsDBO.class).setParameter("empId", Integer.parseInt(empId)).getResultList()).await().indefinitely();
		return empGuestContractDetailsDBO; 
	}

	public List<EmpEmployeeLetterDetailsDBO> getEmployeeLetterDetails(String empId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpEmployeeLetterDetailsDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpEmployeeLetterDetailsDBO> onRun(EntityManager context) throws Exception {
				String str ="from EmpEmployeeLetterDetailsDBO  where recordStatus='A' and empDBO.id=:empId" ;
				Query qry = context.createQuery(str,EmpEmployeeLetterDetailsDBO.class);
				qry.setParameter("empId", Integer.parseInt(empId));
				return qry.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	//	public List<EmpEmployeeLetterDetailsDBO> getEmployeeLetterDetails(String empId) {
	//		List<EmpEmployeeLetterDetailsDBO> empEmployeeLetterDetailsDBO = sessionFactory.withSession(s -> s.createQuery("from EmpEmployeeLetterDetailsDBO  where recordStatus='A' and empDBO.id=:empId", EmpEmployeeLetterDetailsDBO.class).setParameter("empId", Integer.parseInt(empId)).getResultList()).await().indefinitely();
	//		return empEmployeeLetterDetailsDBO;
	//	}

	public ErpCampusDepartmentMappingDBO getCampusDepartmentMapping(String campusId,String departmentId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpCampusDepartmentMappingDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public ErpCampusDepartmentMappingDBO onRun(EntityManager context) throws Exception {
				String str ="from ErpCampusDepartmentMappingDBO dbo where dbo.recordStatus='A' and dbo.erpCampusDBO.id=:campusId and dbo.erpDepartmentDBO.id=:departmentId" ;
				Query qry = context.createQuery(str,ErpCampusDepartmentMappingDBO.class);
				qry.setParameter("campusId", Integer.parseInt(campusId));
				qry.setParameter("departmentId", Integer.parseInt(departmentId));
				return (ErpCampusDepartmentMappingDBO) Utils.getUniqueResult(qry.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpApplnEntriesDBO getApplicantDetails(String applicationNo) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnEntriesDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public EmpApplnEntriesDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from EmpApplnEntriesDBO e where e.recordStatus='A' and e.applicationNo=:applicationNo");
				query.setParameter("applicationNo", Integer.parseInt(applicationNo));
				return (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList()) ;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public ApiResult<List<LookupItemDTO>> getCampusByDepartment(String departmentId) throws Exception {
		ApiResult<List<LookupItemDTO>> campusList = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("departmentId", departmentId);
				Utils.getDropdownData( campusList, context, "select erp_campus.erp_campus_id as ID,erp_campus.campus_name as text from erp_campus_department_mapping "  
						+"inner join erp_campus on erp_campus.erp_campus_id=erp_campus_department_mapping.erp_campus_id "
						+"where erp_campus_department_mapping.erp_department_id=:departmentId and erp_campus_department_mapping.record_status='A'",args);
				return  campusList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean duplicateCheck(String applicationNo) {
		String str = "select emp.emp_id from emp  \n"
				+ "	left join emp_appln_entries on emp_appln_entries. emp_appln_entries_id=emp.emp_appln_entries_id \n"
				+ " where  emp_appln_entries.application_no=:applicationNo and emp.record_status='A'; ";

		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("applicationNo", applicationNo.trim());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public List<EmpEmployeeLetterDetailsDBO> empEmployeeLetterDetails(String empId,String type) {
		List<EmpEmployeeLetterDetailsDBO> empEmployeeLetterDetailsDBO = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpEmployeeLetterDetailsDBO dbo where dbo.letterType = :type and "
				+ "dbo.empDBO.id = :empId and dbo.recordStatus = 'A' ", EmpEmployeeLetterDetailsDBO.class).setParameter("empId", Integer.parseInt(empId)).setParameter("type", type.trim()).getResultList()).await().indefinitely();
		return empEmployeeLetterDetailsDBO;
	}

	public EmpResignationDBO resignationDetails(String empId) {
		EmpResignationDBO empResignationDBO = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpResignationDBO dbo where "
				+ "dbo.empDBO.id = :empId and dbo.recordStatus = 'A' ", EmpResignationDBO.class).setParameter("empId", Integer.parseInt(empId)).getSingleResultOrNull()).await().indefinitely();
		return empResignationDBO;
	}

	public EmpPayScaleDetailsDBO getEmpPayScaleDetails(String empId) {
		EmpPayScaleDetailsDBO empResignationDBO = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpPayScaleDetailsDBO dbo where "
				+ "dbo.empDBO.id = :empId and dbo.recordStatus = 'A' and dbo.current=1", EmpPayScaleDetailsDBO.class).setParameter("empId", Integer.parseInt(empId)).getSingleResultOrNull()).await().indefinitely();
		return empResignationDBO;
	}

	public List<EmpPayScaleDetailsDBO> getPayScaledetails(List<Integer> empPayScaleIds) {
		List<EmpPayScaleDetailsDBO> empPayScaleDetailsDBOList = sessionFactory.withSession(s -> s.createQuery("from EmpPayScaleDetailsDBO dbo where dbo.recordStatus = 'A' and dbo.id IN (:empPayScaleIds)", EmpPayScaleDetailsDBO.class).setParameter("empPayScaleIds", empPayScaleIds).getResultList()).await().indefinitely();
		return empPayScaleDetailsDBOList;
	}

	public List<EmpEducationalDetailsDocumentsDBO> getEmployeeEducationalDocumentsDetails(List<Integer> educationIds) {
		List<EmpEducationalDetailsDocumentsDBO> empEducationalDetailsDocumentsDBOList = sessionFactory.withSession(s -> s.createQuery("from EmpEducationalDetailsDocumentsDBO dbo where dbo.recordStatus ='A' and dbo.empEducationalDetailsDBO.id IN (:educationIds)",EmpEducationalDetailsDocumentsDBO.class).setParameter("educationIds", educationIds).getResultList()).await().indefinitely();
		return empEducationalDetailsDocumentsDBOList;
	}

	public EmpPayScaleDetailsDBO getEmpGuestPayScaleDetails(String payscaleDetailsId) {
		EmpPayScaleDetailsDBO empPayScaleDetailsDBO = sessionFactory.withSession(s -> s.createQuery("from EmpPayScaleDetailsDBO dbo where dbo.recordStatus ='A' and dbo.id = :payscaleDetailsId",EmpPayScaleDetailsDBO.class).setParameter("payscaleDetailsId", Integer.parseInt(payscaleDetailsId)).getSingleResultOrNull()).await().indefinitely();
		return empPayScaleDetailsDBO;
	}

	public Mono<Boolean> duplicateCheckEmpNo(String empNO) {
		String str = "from EmpDBO dbo where (dbo.recordStatus = 'A' or dbo.recordStatus = 'I') and dbo.empNumber = :empNO";
		var list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpDBO> query = s.createQuery(str, EmpDBO.class);
			query.setParameter("empNO", empNO.trim());
			return query.getResultList();
		}).await().indefinitely();
		return list.isEmpty() ? Mono.just(Boolean.TRUE) : Mono.just(Boolean.FALSE);
	}

	public List<EmpLevelsAndPromotionsDBO> getLevelAndPromotionDetails(String empId) {
		String str = " select dbo from EmpLevelsAndPromotionsDBO dbo where dbo.recordStatus ='A' and dbo.empDBO.id =:empId ";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpLevelsAndPromotionsDBO.class).setParameter("empId", Integer.parseInt(empId)).getResultList()).await().indefinitely();
	}

	public List<EmpDBO> getEmployeeList(String campusId) {
		String str = " select dbo from EmpDBO dbo where dbo.recordStatus ='A' "
				+ " and dbo.erpCampusDepartmentMappingDBO.recordStatus ='A' "
				+ " and dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.recordStatus = 'A' "
				+ " and dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.id = :campusId "
				+ " ORDER BY dbo.empName ASC ";
		String finalStr = str;
		List<EmpDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpDBO> query = s.createQuery(finalStr,EmpDBO.class)
					.setParameter("campusId", Integer.parseInt(campusId));
			return query.getResultList();
		}).await().indefinitely();
		return list;		
	}

	public List<EmpPayScaleDetailsComponentsDBO> getEmpPayScaleDetailsComponentsDBOs(Integer id) {
		String str = " select dbo from EmpPayScaleDetailsComponentsDBO dbo where dbo.recordStatus ='A' and dbo.empPayScaleDetailsDBO.id =:id ";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpPayScaleDetailsComponentsDBO.class).setParameter("id", id).getResultList()).await().indefinitely();
	}
	
	public void saveOrUpdateEmpPay(EmpPayScaleDetailsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
	}
	
	public Mono<List<EmpProfileGridDTO>> getEmpProfileGridData() {
		String query = " select new com.christ.erp.services.dto.employee.EmpProfileGridDTO"
				+ " (dbo.id, dbo.empName, dbo.empNumber, dbo.empUniversityEmail, erpCampusDBO.campusName, erpDepartmentDBO.departmentName, "
				+ " dbo.empDesignationDBO.empDesignationName, photoDocumentUrlDBO.fileNameUnique, photoDocumentUrlDBO.fileNameOriginal,"
				+ " urlFolderListDBO.uploadProcessCode)"
				+ " from EmpDBO dbo"
				+ " left join dbo.erpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpDepartmentDBO erpDepartmentDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpCampusDBO erpCampusDBO"
				+ " left join dbo.empDesignationDBO empDesignationDBO"
				+ " left join dbo.empPersonalDataDBO empPersonalDataDBO on empPersonalDataDBO.recordStatus = 'A'"
				+ " left join empPersonalDataDBO.photoDocumentUrlDBO photoDocumentUrlDBO on photoDocumentUrlDBO.recordStatus = 'A'"
				+ " left join photoDocumentUrlDBO.urlFolderListDBO urlFolderListDBO"
				+ " where dbo.recordStatus  = 'A' order by dbo.empName";
		
		return  Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(query,EmpProfileGridDTO.class).getResultList()).subscribeAsCompletionStage());
	}
	public EmpPersonalDataTabDTO getEmployeePersonalDataTabDetails(int empId) {
		String query = " select new com.christ.erp.services.dto.employee.EmpPersonalDataTabDTO"
				+ " (dbo.id, empPersonalDataDBO.empPersonalDataId, empJobDetailsDBO.id, dbo.empNumber, dbo.empName, dbo.erpGenderDBO.id, dbo.erpGenderDBO.genderName, dbo.empDOB, dbo.empDOJ, dbo.countryCode,dbo.empMobile,"
				+ " dbo.empPersonalEmail, empPersonalDataDBO.alternateNo, empPersonalDataDBO.isDifferentlyAbled, "
				+ " empPersonalDataDBO.erpDifferentlyAbledDBO.id, erpDifferentlyAbledDBO.differentlyAbledName, empPersonalDataDBO.differentlyAbledDetails, "
				+ " erpBloodGroupDBO.id, erpBloodGroupDBO.bloodGroupName, erpCountryDBO.id, erpCountryDBO.countryName, erpMaritalStatusDBO.id, erpMaritalStatusDBO.maritalStatusName,"
				+ " erpQualificationLevelDBO.id, erpQualificationLevelDBO.qualificationLevelName, erpReligionDBO.id, erpReligionDBO.religionName, erpReservationCategoryDBO.id,"
				+ " erpReservationCategoryDBO.reservationCategoryName, empAddtnlPersonalDataDBO.panNo, empAddtnlPersonalDataDBO.fourWheelerNo, empPersonalDataDBO.currentAddressLine1, "
				+ " empPersonalDataDBO.currentAddressLine2, currentCity.id, currentCity.cityName, empPersonalDataDBO.currentCityOthers, currentCountry.id, currentCountry.countryName, "
				+ " empPersonalDataDBO.currentPincode, currentState.id, currentState.stateName, empPersonalDataDBO.currentStateOthers, empPersonalDataDBO.isPermanentEqualsCurrent,"
				+ " empPersonalDataDBO.permanentAddressLine1, empPersonalDataDBO.permanentAddressLine2, permanentCity.id, permanentCity.cityName, empPersonalDataDBO.permanentCityOthers, "
				+ " permanentCountry.id, permanentCountry.countryName, empPersonalDataDBO.permanentPincode, permanentState.id, permanentState.stateName, "
				+ " empPersonalDataDBO.permanentStateOthers, empAddtnlPersonalDataDBO.familyBackgroundBrief, empAddtnlPersonalDataDBO.emergencyContactName,"
				+ " empAddtnlPersonalDataDBO.emergencyContactRelationship, empAddtnlPersonalDataDBO.emergencyContactHome, empAddtnlPersonalDataDBO.emergencyContactWork,"
				+ " empAddtnlPersonalDataDBO.emergencyContactAddress, empAddtnlPersonalDataDBO.passportNo, empAddtnlPersonalDataDBO.passportIssuedDate, "
				+ " empAddtnlPersonalDataDBO.passportIssuedPlace, empAddtnlPersonalDataDBO.passportStatus, empAddtnlPersonalDataDBO.passportDateOfExpiry, "
				+ " empAddtnlPersonalDataDBO.passportComments,"
				+ " photoDocumentUrlDBO.fileNameUnique, photoDocumentUrlDBO.fileNameOriginal, urlFolderListDBO.uploadProcessCode, erpReligionDBO.isMinority, "
				+ " empAddtnlPersonalDataDBO.aadharNo, empAddtnlPersonalDataDBO.twoWheelerNo, empAddtnlPersonalDataDBO.isAadharAvailable, empAddtnlPersonalDataDBO.isAadharEnrolled,"
				+ " empAddtnlPersonalDataDBO.aadharEnrolledNo, empAddtnlPersonalDataDBO.emergencyMobileNo, empAddtnlPersonalDataDBO.visaNo, empAddtnlPersonalDataDBO.visaIssuedDate, "
				+ " empAddtnlPersonalDataDBO.visaStatus, empAddtnlPersonalDataDBO.visaDateOfExpiry,"
				+ " empAddtnlPersonalDataDBO.visaComments, empAddtnlPersonalDataDBO.frroNo, empAddtnlPersonalDataDBO.frroIssuedDate, empAddtnlPersonalDataDBO.frroStatus, "
				+ " empAddtnlPersonalDataDBO.frroDateOfExpiry, empAddtnlPersonalDataDBO.frroComments,"
				+ " adharUploadUrlDBO.fileNameUnique, adharUploadUrlDBO.fileNameOriginal, adharUrlFolderListDBO.uploadProcessCode,"
				+ " panUploadUrlDBO.fileNameUnique, panUploadUrlDBO.fileNameOriginal, panUrlFolderListDBO.uploadProcessCode,"
				+ " twoWheelerDocumentUrlDBO.fileNameUnique, twoWheelerDocumentUrlDBO.fileNameOriginal, twoWhUrlFolderListDBO.uploadProcessCode,"
				+ " fourWheelerDocumentUrlDBO.fileNameUnique, fourWheelerDocumentUrlDBO.fileNameOriginal, fourWhUrlFolderListDBO.uploadProcessCode,"
				+ " passportUploadUrlDBO.fileNameUnique, passportUploadUrlDBO.fileNameOriginal, passportlFolderListDBO.uploadProcessCode,"
				+ " visaUploadUrlDBO.fileNameUnique, visaUploadUrlDBO.fileNameOriginal, vislFolderListDBO.uploadProcessCode, "
				+ " frroUploadUrlDBO.fileNameUnique, frroUploadUrlDBO.fileNameOriginal, frrFolderListDBO.uploadProcessCode,"
				+ " empSignatureUrlDBO.fileNameUnique, empSignatureUrlDBO.fileNameOriginal, sigFolderListDBO.uploadProcessCode, "
				+ " permanentCountry.nationalityName, currentCountry.nationalityName)"
				+ " from EmpDBO dbo"
				+ " left join dbo.empPersonalDataDBO empPersonalDataDBO on empPersonalDataDBO.recordStatus = 'A'"
				+ " left join dbo.empJobDetailsDBO empJobDetailsDBO on empJobDetailsDBO.recordStatus = 'A'"
				+ " left join empPersonalDataDBO.erpDifferentlyAbledDBO erpDifferentlyAbledDBO"
				+ " left join empPersonalDataDBO.erpBloodGroupDBO erpBloodGroupDBO"
				+ " left join empPersonalDataDBO.erpCountryDBO erpCountryDBO"
				+ " left join empPersonalDataDBO.currentCountry currentCountry"
				+ " left join empPersonalDataDBO.erpMaritalStatusDBO erpMaritalStatusDBO"
				+ " left join empPersonalDataDBO.erpQualificationLevelDBO erpQualificationLevelDBO"
				+ " left join empPersonalDataDBO.erpReligionDBO erpReligionDBO"
				+ " left join empPersonalDataDBO.currentCity currentCity"
				+ " left join empPersonalDataDBO.erpReservationCategoryDBO erpReservationCategoryDBO"
				+ " left join empPersonalDataDBO.empAddtnlPersonalDataDBO empAddtnlPersonalDataDBO on empAddtnlPersonalDataDBO.recordStatus = 'A'"
				+ " left join empPersonalDataDBO.currentState currentState"
				+ " left join empPersonalDataDBO.permanentCity permanentCity"
				+ " left join empPersonalDataDBO.permanentCountry permanentCountry"
				+ " left join empPersonalDataDBO.permanentState permanentState"
				+ " left join empPersonalDataDBO.photoDocumentUrlDBO photoDocumentUrlDBO on photoDocumentUrlDBO.recordStatus = 'A'"
				+ " left join photoDocumentUrlDBO.urlFolderListDBO urlFolderListDBO"
				+ " left join empAddtnlPersonalDataDBO.adharUploadUrlDBO adharUploadUrlDBO on adharUploadUrlDBO.recordStatus = 'A'"
				+ " left join adharUploadUrlDBO.urlFolderListDBO adharUrlFolderListDBO"
				+ " left join empAddtnlPersonalDataDBO.panUploadUrlDBO panUploadUrlDBO on panUploadUrlDBO.recordStatus = 'A'"
				+ " left join panUploadUrlDBO.urlFolderListDBO panUrlFolderListDBO"
				+ " left join empAddtnlPersonalDataDBO.twoWheelerDocumentUrlDBO twoWheelerDocumentUrlDBO on twoWheelerDocumentUrlDBO.recordStatus = 'A'"
				+ " left join twoWheelerDocumentUrlDBO.urlFolderListDBO twoWhUrlFolderListDBO"
				+ " left join empAddtnlPersonalDataDBO.fourWheelerDocumentUrlDBO fourWheelerDocumentUrlDBO on fourWheelerDocumentUrlDBO.recordStatus = 'A'"
				+ " left join fourWheelerDocumentUrlDBO.urlFolderListDBO fourWhUrlFolderListDBO"
				+ " left join empAddtnlPersonalDataDBO.passportUploadUrlDBO passportUploadUrlDBO on passportUploadUrlDBO.recordStatus = 'A'"
				+ " left join passportUploadUrlDBO.urlFolderListDBO passportlFolderListDBO"
				+ " left join empAddtnlPersonalDataDBO.visaUploadUrlDBO visaUploadUrlDBO on visaUploadUrlDBO.recordStatus = 'A'"
				+ " left join visaUploadUrlDBO.urlFolderListDBO vislFolderListDBO"
				+ " left join empAddtnlPersonalDataDBO.frroUploadUrlDBO frroUploadUrlDBO on frroUploadUrlDBO.recordStatus = 'A'"
				+ " left join frroUploadUrlDBO.urlFolderListDBO frrFolderListDBO"
				+ " left join dbo.empSignatureUrlDBO empSignatureUrlDBO on empSignatureUrlDBO.recordStatus = 'A'"
				+ " left join empSignatureUrlDBO.urlFolderListDBO sigFolderListDBO"
				+ " where dbo.recordStatus  = 'A' and dbo.id = :empId";
		
		return sessionFactory.withSession(s->s.createQuery(query,EmpPersonalDataTabDTO.class).setParameter("empId", empId).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<EmpProfileFamilyDependentDTO> getEmpProfileFamilyDependentDetails(Integer personalDataId) {
		String query =" select new com.christ.erp.services.dto.employee.EmpProfileFamilyDependentDTO"
				+ "(dbo.id, empPersonalDataDBO.empPersonalDataId, dbo.relationship, dbo.dependentName, dbo.dependentDob, dbo.dependentQualification, "
				+ "dbo.dependentProfession, dbo.otherDependentRelationship)"
				+ " from EmpFamilyDetailsAddtnlDBO dbo "
				+ " left join dbo.empPersonalDataDBO empPersonalDataDBO"
				+ " where  dbo.recordStatus  = 'A' and empPersonalDataDBO.empPersonalDataId = :personalDataId and (dbo.relationship is not null or dbo.otherDependentRelationship is not null) ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpProfileFamilyDependentDTO.class).setParameter("personalDataId", personalDataId).getResultList()).await().indefinitely();
	}
	public List<EmpProfilePFandGratuityDTO> getPFandGratuityDetails(Integer jobDetailsId) {
		String query = " select new com.christ.erp.services.dto.employee.EmpProfilePFandGratuityDTO"
				+ " (dbo.id, empJobDetailsDBO.id, dbo.nominee, dbo.nomineeAddress, dbo.nomineeRelationship,"
				+ " dbo.nomineeDob, dbo.sharePercentage, dbo.under18GuardName, dbo.under18GuardianAddress, dbo.isPf, dbo.isGratuity)"
				+ " from EmpPfGratuityNomineesDBO dbo "
				+ " left join dbo.empJobDetailsDBO empJobDetailsDBO"
				+ " where  dbo.recordStatus  = 'A' and empJobDetailsDBO.id = :jobDetailsId";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpProfilePFandGratuityDTO.class).setParameter("jobDetailsId", jobDetailsId).getResultList()).await().indefinitely();
	}
	public EmpDBO getEmployeeDBO(int empId) {
		String query =" from EmpDBO dbo"
				+ " left join fetch dbo.empPersonalDataDBO empPersonalDataDBO"
				+ " left join fetch dbo.empJobDetailsDBO empJobDetailsDBO"
				+ " left join fetch empJobDetailsDBO.empPfGratuityNomineesDBOS"
				+ " left join fetch empPersonalDataDBO.empFamilyDetailsAddtnlDBOS"
				+ " left join fetch empPersonalDataDBO.empAddtnlPersonalDataDBO empAddtnlPersonalDataDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.id = :empId ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpDBO.class).setParameter("empId", empId).getSingleResult()).await().indefinitely();
	}	
	public void updateEmpDBO(EmpDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
	}
	public EmpPersonalDataDBO getPersonalDataDBO(int id) {
		return sessionFactory.withSession(s->s.find(EmpPersonalDataDBO.class, id)).await().indefinitely();
	}
	public EmpProfileSidePanelDTO getEmployeeSidePanelDetails(int empId) {
		String query = " select new com.christ.erp.services.dto.employee.EmpProfileSidePanelDTO"
				+ " (dbo.id, dbo.empNumber, dbo.empName,"
				+ " dbo.empPersonalEmail, dbo.empUniversityEmail, dbo.recordStatus, empDesignationDBO.empDesignationName, erpDepartmentDBO.departmentName,"
				+ " photoDocumentUrlDBO.fileNameUnique, photoDocumentUrlDBO.fileNameOriginal, urlFolderListDBO.uploadProcessCode, erpRoomEmpMappingDBO.telephoneExtension)"
				+ " from EmpDBO dbo"
				+ " left join dbo.empPersonalDataDBO empPersonalDataDBO on empPersonalDataDBO.recordStatus = 'A'"				
				+ " left join dbo.empDesignationDBO empDesignationDBO on empDesignationDBO.recordStatus = 'A'"
				+ " left join empPersonalDataDBO.photoDocumentUrlDBO photoDocumentUrlDBO on photoDocumentUrlDBO.recordStatus = 'A'"
				+ " left join photoDocumentUrlDBO.urlFolderListDBO urlFolderListDBO on urlFolderListDBO.recordStatus = 'A'"
				+ " left join dbo.erpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO on  erpCampusDepartmentMappingDBO.recordStatus = 'A'"
				+ " left join erpCampusDepartmentMappingDBO.erpDepartmentDBO erpDepartmentDBO on erpDepartmentDBO.recordStatus = 'A'"
				+ " left join dbo.erpRoomEmpMappingDBO erpRoomEmpMappingDBO on erpRoomEmpMappingDBO.recordStatus = 'A'"
				+ " where dbo.id = :empId";
		return sessionFactory.withSession(s->s.createQuery(query,EmpProfileSidePanelDTO.class).setParameter("empId", empId).getSingleResultOrNull()).await().indefinitely();
	}
	public EmpEmploymentTabDTO getEmploymentDetails(int empId) {
		String query = " select new com.christ.erp.services.dto.employee.EmpEmploymentTabDTO"
				+ " (dbo.id, dbo.empNumber, empEmployeeCategoryDBO.id, empEmployeeCategoryDBO.employeeCategoryName, empEmployeeJobCategoryDBO.id, empEmployeeJobCategoryDBO.employeeJobName,"
				+ " empEmployeeGroupDBO.id, empEmployeeGroupDBO.employeeGroupName, empDesignationDBO.id, empDesignationDBO.empDesignationName,"
				+ " erpEmployeeTitleDBO.id, erpEmployeeTitleDBO.titleName, empApplnSubjectCategoryDBO.id, empApplnSubjectCategoryDBO.subjectCategory,"
				+ " deputationErpCampusDepartmentMappingDBO.id, deputedDepartmentDBO.departmentName,deputedCampusDBO.id, deputedCampusDBO.campusName,"
				+ "  erpCampusDepartmentMappingDBO.id, erpDepartmentDBO.departmentName, "
				+ " erpCampusDBO.id, erpCampusDBO.campusName, erpLocationDBO.id, erpLocationDBO.locationName, empAlbumDesignationDBO.id, empAlbumDesignationDBO.empDesignationName,"
				+ " dbo.recordStatus, empLeaveCategoryDBO.empLeaveCategoryAllotmentId, empLeaveCategoryDBO.empLeaveCategoryAllotmentName, dbo.empDOJ, empresignationDBO.submissionDate,"
				+ "	empJobDetailsDBO.isVacationApplicable, empJobDetailsDBO.isDisplayWebsite, erpRoomEmpMappingDBO.telephoneExtension, dbo.empUniversityEmail, erpBlockDBO.id, erpBlockDBO.blockName, "
				+ " erpFloorsDBO.id, erpFloorsDBO.floorNo, erpRoomsDBO.id, erpRoomsDBO.roomNo, empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId, "
				+ " empApplnSubjectCategorySpecializationDBO.subjectCategorySpecializationName, leaveApprover.id, leaveApprover.empName,"
				+ " leaveAuthorizer.id, leaveAuthorizer.empName, levelOneAppraiser.id, levelOneAppraiser.empName,"
				+ " levelTwoAppraiser.id, levelTwoAppraiser.empName, workDairyApprover.id, workDairyApprover.empName,"
				+ " empresignationDBO.dateOfLeaving, empresignationDBO.vcAcceptedDate, empresignationDBO.relievingDate, empResignationReasonDBO.id, empResignationReasonDBO.resignationName, "
				+ " empresignationDBO.reasonOther, empresignationDBO.noticePeriodServedDays,empTimeZoneDBO.id, empTimeZoneDBO.timeZoneName,"
				+ " empJobDetailsDBO.isHolidayWorking, empJobDetailsDBO.isHolidayTimeZoneApplicable, holidayTimeZoneDBO.id,"
				+ " holidayTimeZoneDBO.timeZoneName, empJobDetailsDBO.isDutyRosterApplicable, erpRoomEmpMappingDBO.cabinNo, empJobDetailsDBO.isPunchingExempted, empJobDetailsDBO.retirementDate,"
				+ " empresignationDBO.poRemarks, empresignationDBO.isExitInterviewCompleted, empLeaveCategoryDBO.leaveIinitializeMonth)"
				+ " from EmpDBO dbo"
				+ " left join dbo.empEmployeeCategoryDBO empEmployeeCategoryDBO"
				+ " left join dbo.empEmployeeJobCategoryDBO empEmployeeJobCategoryDBO"
				+ " left join dbo.empEmployeeGroupDBO empEmployeeGroupDBO"
				+ " left join dbo.empDesignationDBO empDesignationDBO"
				+ " left join dbo.erpEmployeeTitleDBO erpEmployeeTitleDBO"
				+ " left join dbo.empApplnSubjectCategoryDBO empApplnSubjectCategoryDBO"
				+ " left join dbo.deputationErpCampusDepartmentMappingDBO deputationErpCampusDepartmentMappingDBO"
				+ " left join deputationErpCampusDepartmentMappingDBO.erpDepartmentDBO deputedDepartmentDBO"
				+ " left join deputationErpCampusDepartmentMappingDBO.erpCampusDBO deputedCampusDBO"
				+ " left join dbo.erpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpDepartmentDBO erpDepartmentDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpCampusDBO erpCampusDBO"
				+ " left join erpCampusDBO.erpLocationDBO erpLocationDBO"
				+ " left join dbo.empAlbumDesignationDBO empAlbumDesignationDBO"
				+ " left join dbo.empJobDetailsDBO empJobDetailsDBO on empJobDetailsDBO.recordStatus = 'A'"
				+ " left join empJobDetailsDBO.empLeaveCategoryAllotmentId empLeaveCategoryDBO"
				+ " left join dbo.empresignationDBO empresignationDBO on empresignationDBO.recordStatus = 'A'"
				+ " left join dbo.erpRoomEmpMappingDBO erpRoomEmpMappingDBO"
				+ " left join erpRoomEmpMappingDBO.erpRoomsDBO erpRoomsDBO"
				+ " left join erpRoomsDBO.erpBlockDBO erpBlockDBO"
				+ " left join erpRoomsDBO.erpFloorsDBO erpFloorsDBO"
				+ "	left join dbo.empApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO"
				+ " left join dbo.empApproversDBO empApproversDBO on empApproversDBO.recordStatus = 'A'"
				+ " left join empApproversDBO.leaveApproverId leaveApprover"
				+ " left join empApproversDBO.leaveAuthorizerId leaveAuthorizer"
				+ " left join empApproversDBO.levelOneAppraiserId levelOneAppraiser"
				+ " left join empApproversDBO.levelTwoAppraiserId levelTwoAppraiser"
				+ " left join empApproversDBO.workDairyApproverId workDairyApprover"
				+ " left join empresignationDBO.empResignationReasonDBO empResignationReasonDBO"
				+ " left join dbo.empTimeZoneDBO empTimeZoneDBO"
				+ " left join empJobDetailsDBO.holidayTimeZoneDBO holidayTimeZoneDBO"
				+ " where dbo.id = :empId";
		return sessionFactory.withSession(s->s.createQuery(query,EmpEmploymentTabDTO.class).setParameter("empId", empId).getSingleResultOrNull()).await().indefinitely();
	}
	public List<EmpRemarksDetailsDTO> getRemarksDetails(int empId) {
		String query = " select new com.christ.erp.services.dto.employee.EmpRemarksDetailsDTO"
				+ " (dbo.id, dbo.remarksDate, dbo.remarksDetails, remarksUploadUrlDBO.fileNameUnique, remarksUploadUrlDBO.fileNameOriginal, "
				+ " urlFolderListDBO.uploadProcessCode, erpUsersDBO.userName, dbo.isForOfficeUse)"
				+ " from EmpRemarksDetailsDBO dbo "
				+ " left join dbo.remarksUploadUrlDBO remarksUploadUrlDBO on remarksUploadUrlDBO.recordStatus = 'A'"
				+ " left join remarksUploadUrlDBO.urlFolderListDBO urlFolderListDBO "
				+ " left join ErpUsersDBO erpUsersDBO on dbo.createdUsersId = erpUsersDBO.id"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query,EmpRemarksDetailsDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public EmpDBO getEmployeeDBOForEmploymentTab(int empId) {
		String query =" from EmpDBO dbo"
				+ " left join fetch dbo.empPersonalDataDBO empPersonalDataDBO"
				+ " left join fetch dbo.empJobDetailsDBO empJobDetailsDBO"
				+ " left join fetch dbo.erpRoomEmpMappingDBO erpRoomEmpMappingDBO"
				+ " left join fetch dbo.empApproversDBO empApproversDBO"
				+ " left join fetch dbo.empresignationDBO empresignationDBO"
				+ " left join fetch dbo.empRemarksDetailsDBOSet empRemarksDetailsDBOSet"
				+ " left join fetch dbo.empDepartmentDesignationHistoryDBOSet empDepartmentDesignationHistoryDBOSet"
				+ " left join fetch dbo.empEmployeeLetterDetailsDBOSet empEmployeeLetterDetailsDBOSet"
				+ " left join fetch dbo.empGuestContractDetailsDBOSet empGuestContractDetailsDBOSet"
					+ " left join fetch dbo.empLeaveAllocationDBOSet empLeaveAllocationDBOSet"
				+ " where dbo.id = :empId ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpDBO.class).setParameter("empId", empId).getSingleResult()).await().indefinitely();
	}
	public List<EmpEmploymentHistoryDTO> getEmploymentHistory(int empId) {
		String query = " select new com.christ.erp.services.dto.employee.EmpEmploymentHistoryDTO"
				+ " (empEmployeeCategoryId.employeeCategoryName, empEmployeeJobCategoryDBO.employeeJobName, erpDepartmentDBO.departmentName, erpCampusDBO.campusName, dbo.empTitleDBO.titleName,"
				+ " empDesignationDBO.empDesignationName, dbo.fromDate, dbo.toDate )"
				+ " from EmpDepartmentDesignationHistoryDBO dbo "
				+ " left join dbo.empDesignationDBO empDesignationDBO"
				+ " left join dbo.erpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpDepartmentDBO erpDepartmentDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpCampusDBO erpCampusDBO"
				+ " left join dbo.empTitleDBO empTitleDBO"
				+ " left join dbo.empEmployeeJobCategoryDBO empEmployeeJobCategoryDBO"
				+ " left join empEmployeeJobCategoryDBO.empEmployeeCategoryId empEmployeeCategoryId"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A' and (dbo.isCurrent is null or dbo.isCurrent = 0)";
		return sessionFactory.withSession(s->s.createQuery(query, EmpEmploymentHistoryDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}

	public List<EmpLetterDetailsDTO> getLetterDetails(int empId) {
		String query = " select new com.christ.erp.services.dto.employee.EmpLetterDetailsDTO"
				+ " (dbo.id, dbo.empLetterTypeDBO.id, empLetterTypeDBO.empLetterTypeName,"
				+ " dbo.letterRefNo, dbo.letterDate,"
				+ " letterUrlDBO.fileNameUnique, "
				+ " letterUrlDBO.fileNameOriginal, urlFolderListDBO.uploadProcessCode)"
				+ " from EmpEmployeeLetterDetailsDBO dbo "
				+ " left join dbo.empLetterTypeDBO empLetterTypeDBO"
				+ " left join dbo.letterUrlDBO letterUrlDBO on letterUrlDBO.recordStatus = 'A'"
				+ " left join letterUrlDBO.urlFolderListDBO urlFolderListDBO "
				+ " left join ErpUsersDBO erpUsersDBO on dbo.createdUsersId = erpUsersDBO.id"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query,EmpLetterDetailsDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public EmpQualificationTabDTO getQualificationTabDetails(Integer empId) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpQualificationTabDTO"
				+ " (dbo.id, erpQualificationLevelDBO.id, erpQualificationLevelDBO.qualificationLevelName,"
				+ " empPersonalDataDBO.highestQualificationAlbum)"
				+ " from EmpDBO dbo "
				+ " left join dbo.empPersonalDataDBO empPersonalDataDBO on empPersonalDataDBO.recordStatus = 'A' "
				+ " left join empPersonalDataDBO.erpQualificationLevelDBO erpQualificationLevelDBO"
				+ " where dbo.id = :empId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query,EmpQualificationTabDTO.class).setParameter("empId", empId).getSingleResultOrNull()).await().indefinitely();
	}
	public List<EmpProfileEducationalDetailsDTO> getEducationDetails(Integer empId) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileEducationalDetailsDTO"
				+ " (dbo.id, erpQualificationLevelDBO.id, erpQualificationLevelDBO.qualificationLevelName,"
				+ " dbo.currentStatus, dbo.course, dbo.specialization, dbo.yearOfCompletion, erpCountryDBO.id, erpCountryDBO.countryName, erpStateDBO.id, "
				+ " erpStateDBO.stateName, dbo.boardOrUniversity, erpInstitutionDBO.id, erpInstitutionDBO.institutionName, highQualificationLevelDBO.id,"
				+ " highQualificationLevelDBO.qualificationLevelName, empPersonalDataDBO.highestQualificationAlbum,"
				+ " erpUniversityBoardDBO.id, erpUniversityBoardDBO.universityBoardName, dbo.institute, dbo.stateOthers, dbo.qualificationOthers, dbo.gradeOrPercentage)"
				+ " from EmpEducationalDetailsDBO dbo "
				+ " left join dbo.empDBO empDBO"
				+ " left join empDBO.empPersonalDataDBO empPersonalDataDBO"
				+ " left join dbo.erpQualificationLevelDBO erpQualificationLevelDBO"
				+ " left join dbo.erpCountryDBO erpCountryDBO"
				+ " left join dbo.erpStateDBO erpStateDBO"
				+ " left join dbo.erpInstitutionDBO erpInstitutionDBO"
				+ " left join empPersonalDataDBO.erpQualificationLevelDBO highQualificationLevelDBO"
				+ " left join dbo.erpUniversityBoardDBO erpUniversityBoardDBO"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, EmpProfileEducationalDetailsDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public List<EmpEligibilityTestDTO> getEligibilityDetails(Integer empId) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpEligibilityTestDTO"
				+ " (dbo.empEligibilityTestId, empEligibilityExamListDBO.empEligibilityExamListId, empEligibilityExamListDBO.eligibilityExamName,"
				+ " dbo.testYear)"
				+ " from EmpEligibilityTestDBO dbo "
				+ " left join dbo.empDBO empDBO"
				+ " left join dbo.empEligibilityExamListDBO empEligibilityExamListDBO"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, EmpEligibilityTestDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public List<EmpProfileEdnDetailsDocumentsDTO> getEducationDetailsDocuments(Set<Integer> docIds) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileEdnDetailsDocumentsDTO"
				+ " (dbo.id, dbo.empEducationalDetailsDBO.id, educationalDocumentsUrlDBO.fileNameUnique, educationalDocumentsUrlDBO.fileNameOriginal, folderListDBO.uploadProcessCode)"
				+ " from EmpEducationalDetailsDocumentsDBO dbo "
				+ " left join dbo.educationalDocumentsUrlDBO educationalDocumentsUrlDBO on educationalDocumentsUrlDBO.recordStatus = 'A'"
				+ " left join educationalDocumentsUrlDBO.urlFolderListDBO folderListDBO"
				+ " where dbo.empEducationalDetailsDBO.id in (:docIds) and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, EmpProfileEdnDetailsDocumentsDTO.class).setParameter("docIds", docIds).getResultList()).await().indefinitely();
	}
	public EmpDBO getEmpDBOForQualificationTab(int empId) {
		String query =" from EmpDBO dbo"
				+ " left join fetch dbo.empPersonalDataDBO empPersonalDataDBO"
				+ " left join fetch dbo.empEducationalDetailsDBOSet empEducationalDetailsDBOSet"
				+ " left join fetch empEducationalDetailsDBOSet.documentsDBOSet documentsDBOSet"
				+ " left join fetch documentsDBOSet.educationalDocumentsUrlDBO educationalDocumentsUrlDBO"
				+ " left join fetch dbo.empEligibilityTestDBOSet empEligibilityTestDBOSet"
				+ " left join fetch empEligibilityTestDBOSet.empEligibilityTestDocumentDBOSet empEligibilityTestDocumentDBOSet"
				+ " left join fetch empEligibilityTestDocumentDBOSet.eligibilityDocumentUrlDBO eligibilityDocumentUrlDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.id = :empId ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpDBO.class).setParameter("empId", empId).getSingleResult()).await().indefinitely();
	}
	public List<EmpProfileGuestContractDetailsDTO> getContractDetails(Integer empId) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileGuestContractDetailsDTO"
				+ " (dbo.id, dbo.guestSubjectSpecialization, dbo.guestTutoringSemester, dbo.guestWorkingHoursWeek, dbo.guestReferredBy, dbo.contractEmpStartDate,"
				+ " dbo.contractEmpEndDate, dbo.contractEmpLetterNo, "
				+ " contractEmpDocumentUrlDBO.fileNameUnique, contractEmpDocumentUrlDBO.fileNameOriginal, folderListDBO.uploadProcessCode, dbo.guestContractRemarks,"
				+ "  erpCampusDepartmentMappingDBO.id, erpDepartmentDBO.departmentName, "
				+ " erpCampusDBO.id, erpCampusDBO.campusName, dbo.isCurrent, dbo.payScaleType, dbo.payAmount)"
				+ " from EmpGuestContractDetailsDBO dbo "
				+ " left join dbo.erpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpDepartmentDBO erpDepartmentDBO"
				+ " left join erpCampusDepartmentMappingDBO.erpCampusDBO erpCampusDBO"
				+ " left join dbo.contractEmpDocumentUrlDBO contractEmpDocumentUrlDBO on contractEmpDocumentUrlDBO.recordStatus = 'A'"
				+ " left join contractEmpDocumentUrlDBO.urlFolderListDBO folderListDBO"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A'";
			return sessionFactory.withSession(s->s.createQuery(query, EmpProfileGuestContractDetailsDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public SelectDTO getJobCategoryCode(Integer jobCategoryId) {
		String query = " select new com.christ.erp.services.dto.common.SelectDTO"
				+ " (dbo.jobCategoryCode, dbo.employeeJobName  )"
				+ " from EmpEmployeeJobCategoryDBO dbo "
				+ " where dbo.id = :jobCategoryId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, SelectDTO.class).setParameter("jobCategoryId", jobCategoryId).getSingleResultOrNull()).await().indefinitely();
	}
	public List<SalaryDetailsDTO> getSalaryDetails(Integer empId) {
		String query = " select new com.christ.erp.services.dto.employee.profile.SalaryDetailsDTO"
				+ " (dbo.id, dbo.current, dbo.payScaleType , empPayScaleGradeDBO.id, empPayScaleGradeDBO.gradeName, empPayScaleGradeMappingDBO.payScaleRevisedYear, "
				+ " empPayScaleGradeMappingDetailDBO.id, empPayScaleGradeMappingDetailDBO.payScale,"
				+ " empPayScaleMatrixDetailDBO.id, empPayScaleMatrixDetailDBO.levelCellNo, empPayScaleLevelDBO.id, empPayScaleLevelDBO.empPayScaleLevel,"
				+ " dbo.wageRatePerType, dbo.grossPay )"
				+ " from EmpPayScaleDetailsDBO dbo "
				+ " left join dbo.empPayScaleMatrixDetailDBO empPayScaleMatrixDetailDBO"
				+ " left join empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO empPayScaleGradeMappingDetailDBO"
				+ " left join empPayScaleGradeMappingDetailDBO.empPayScaleGradeMappingDBO empPayScaleGradeMappingDBO"
				+ " left join empPayScaleGradeMappingDBO.empPayScaleGradeDBO empPayScaleGradeDBO"
				+ " left join empPayScaleGradeMappingDetailDBO.empPayScaleLevelDBO empPayScaleLevelDBO"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, SalaryDetailsDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public List<PayScaleDetailsDTO> getPayScaleDetailsComponents(Set<Integer> payscaleIds) {
		String query = " select new com.christ.erp.services.dto.employee.profile.PayScaleDetailsDTO"
				+ " (dbo.id, dbo.empPayScaleDetailsDBO.id, empPayScaleComponentsDBO.id, empPayScaleComponentsDBO.salaryComponentShortName, empPayScaleComponentsDBO.payScaleType,"
				+ " empPayScaleComponentsDBO.isComponentBasic, empPayScaleComponentsDBO.salaryComponentDisplayOrder, empPayScaleComponentsDBO.isCalculationTypePercentage, "
				+ " empPayScaleComponentsDBO.percentage, dbo.empSalaryComponentValue)"
				+ " from EmpPayScaleDetailsComponentsDBO dbo "
				+ " left join dbo.empPayScaleComponentsDBO empPayScaleComponentsDBO"
				+ " where dbo.empPayScaleDetailsDBO.id in (:payscaleIds) and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, PayScaleDetailsDTO.class).setParameter("payscaleIds", payscaleIds).getResultList()).await().indefinitely();
	}
	public List<PayScaleDetailsDTO> getSalaryComponents() {
		String str = " select new com.christ.erp.services.dto.employee.profile.PayScaleDetailsDTO "
				+ " (dbo.id, dbo.salaryComponentShortName, dbo.payScaleType, dbo.isComponentBasic, dbo.salaryComponentDisplayOrder, dbo.isCalculationTypePercentage, " +
				" dbo.percentage,dbo.salaryComponentDisplayOrder) from EmpPayScaleComponentsDBO dbo "
				+ "	where dbo.recordStatus = 'A' order by dbo.salaryComponentDisplayOrder";
		return sessionFactory.withSession(s -> s.createQuery(str, PayScaleDetailsDTO.class).getResultList()).await().indefinitely();
	}
	public PayScaleDetailsDTO getMatrixDetailsForAmount(Integer matrixId) {
		String str = " select new com.christ.erp.services.dto.employee.profile.PayScaleDetailsDTO "
				+ " (dbo.id, dbo.levelCellValue) from EmpPayScaleMatrixDetailDBO dbo "
				+ "	where dbo.id = :matrixId and  dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str, PayScaleDetailsDTO.class).setParameter("matrixId", matrixId).getSingleResultOrNull()).await().indefinitely();
	}
	public EmpDBO getEmpDBOForSalaryTab(Integer empId) {
		String query =" from EmpDBO dbo"
				+ " left join fetch dbo.empPayScaleDetailsDBOSet empPayScaleDetailsDBOSet"
				+ " left join fetch empPayScaleDetailsDBOSet.empPayScaleDetailsComponentsDBOs empPayScaleDetailsComponentsDBOs"
				+ " left join fetch dbo.empJobDetailsDBO empJobDetailsDBO "
				+ " left join fetch empJobDetailsDBO.empPfGratuityNomineesDBOS empPfGratuityNomineesDBOS"
				+ " where  dbo.recordStatus  = 'A' and dbo.id = :empId ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpDBO.class).setParameter("empId", empId).getSingleResult()).await().indefinitely();
	}
	public List<EmpEligibilityTestDocumentDTO> getEligibilityTestDocuments(Set<Integer> docIds) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpEligibilityTestDocumentDTO"
				+ " (dbo.id, dbo.empEligibilityTestDBO.id, eligibilityDocumentUrlDBO.fileNameUnique, eligibilityDocumentUrlDBO.fileNameOriginal, folderListDBO.uploadProcessCode)"
				+ " from EmpEligibilityTestDocumentDBO dbo "
				+ " left join dbo.eligibilityDocumentUrlDBO eligibilityDocumentUrlDBO on eligibilityDocumentUrlDBO.recordStatus = 'A'"
				+ " left join eligibilityDocumentUrlDBO.urlFolderListDBO folderListDBO"
				+ " where dbo.empEligibilityTestDBO.id in (:docIds) and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, EmpEligibilityTestDocumentDTO.class).setParameter("docIds", docIds).getResultList()).await().indefinitely();
	}
	public EmpPFandGratuityDTO getPFGratuityDetails(Integer empId) {
		String str = " select new com.christ.erp.services.dto.employee.profile.EmpPFandGratuityDTO "
				+ " (dbo.id, dbo.isWithPf, dbo.isWithGratuity, dbo.isEsiApplicable, dbo.isUanNoAvailable, "
				+ " dbo.pfAccountNo, dbo.pfDate, dbo.gratuityNo, dbo.gratuityDate, dbo.uanNo, "
				+ " dbo.isSibAccountAvailable, dbo.sibAccountBank, dbo.branchIfscCode, dbo.esiInsuranceNo) from EmpJobDetailsDBO dbo "
				+ "	where dbo.empDBO.id = :empId and  dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str, EmpPFandGratuityDTO.class).setParameter("empId", empId).getSingleResultOrNull()).await().indefinitely();
	}
	public List<EmpProfileWorkExperienceDTO> getWorkExperienceDetails(Integer empId) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileWorkExperienceDTO"
				+ " (dbo.empWorkExperienceId, empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId, empApplnWorkExperienceTypeDBO.workExperienceTypeName, " +
				" empApplnSubjectCategoryDBO.id, empApplnSubjectCategoryDBO.subjectCategory,"
				+ " dbo.isPartTime, dbo.empDesignation, dbo.workExperienceFromDate, dbo.workExperienceToDate,"
				+ " dbo.workExperienceYears, dbo.workExperienceMonth, dbo.institution, empApplnWorkExperienceTypeDBO.isExperienceTypeAcademic)"
				+ " from EmpWorkExperienceDBO dbo "
				+ " left join dbo.empDBO empDBO"
				+ " left join dbo.empApplnWorkExperienceTypeDBO empApplnWorkExperienceTypeDBO"
				+ " left join dbo.empApplnSubjectCategoryDBO empApplnSubjectCategoryDBO"
				+ " where dbo.empDBO.id = :empId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, EmpProfileWorkExperienceDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public List<EmpProfileWorkExpDocDTO> getWorkExperienceDocuments(Set<Integer> docIds) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileWorkExpDocDTO"
				+ " (dbo.id"
				+ ", dbo.empWorkExperienceDBO.empWorkExperienceId"
				+ ", experienceDocumentsUrlDBO.fileNameUnique, experienceDocumentsUrlDBO.fileNameOriginal, folderListDBO.uploadProcessCode)"
				+ " from EmpWorkExperienceDocumentDBO dbo "
				+ " left join dbo.experienceDocumentsUrlDBO experienceDocumentsUrlDBO on experienceDocumentsUrlDBO.recordStatus = 'A'"
				+ " left join experienceDocumentsUrlDBO.urlFolderListDBO folderListDBO"
				+ " where dbo.empWorkExperienceDBO.empWorkExperienceId in (:docIds) and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query, EmpProfileWorkExpDocDTO.class).setParameter("docIds", docIds).getResultList()).await().indefinitely();
	}
	public List<EmpProfileMajorAchievementsDTO> getMajorAchievements(Integer empId) {
		String query =" select new com.christ.erp.services.dto.employee.profile.EmpProfileMajorAchievementsDTO"
				+ " (dbo.id, dbo.empDBO.id, dbo.achievements)"
				+ " from EmpMajorAchievementsDBO dbo "
				+ " where dbo.recordStatus  = 'A' and dbo.empDBO.id = :empId";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpProfileMajorAchievementsDTO.class).setParameter("empId", empId).getResultList()).await().indefinitely();
	}
	public EmpDBO getEmpDBOForExperienceTab(int empId) {
		String query =" from EmpDBO dbo"
				+ " left join fetch dbo.empWorkExperienceDBOSet empWorkExperienceDBOSet"
				+ " left join fetch empWorkExperienceDBOSet.workExperienceDocumentsDBOSet"
				+ " left join fetch dbo.empMajorAchievementsDBOSet empMajorAchievementsDBOSet"
				+ " where  dbo.recordStatus  = 'A' and dbo.id = :empId ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpDBO.class).setParameter("empId", empId).getSingleResult()).await().indefinitely();
	}
	public EmpProfileCreateEmployeeDTO getApplicantDetForNewEmployee(Integer applicationNo) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileCreateEmployeeDTO"
				+ " (dbo.id, dbo.applicationNo, dbo.applicantName, erpCampusDBO.id, erpCampusDBO.campusName,"
				+ " dbo.empEmployeeCategoryDBO.id, empEmployeeCategoryDBO.employeeCategoryName, "
				+ " empEmployeeJobCategoryDBO.id, empEmployeeJobCategoryDBO.employeeJobName, empApplnSubjectCategorySpecializationDBO.id,"
				+ " empApplnSubjectCategorySpecializationDBO.subjectCategorySpecializationName)"
				+ " from EmpApplnEntriesDBO dbo"
				+ " left join dbo.erpCampusDBO erpCampusDBO"
				+ " left join dbo.erpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO"
				+ " left join dbo.empEmployeeCategoryDBO empEmployeeCategoryDBO"
				+ " left join dbo.empEmployeeJobCategoryDBO empEmployeeJobCategoryDBO"
				+ " left join dbo.empApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.applicationNo = :applicationNo ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpProfileCreateEmployeeDTO.class).setParameter("applicationNo", applicationNo).getSingleResult()).await().indefinitely();
	}
	public boolean empDuplicateCheck(String empNo) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from EmpDBO bo where bo.recordStatus='A' and bo.empNumber = :empNumber");
		List<EmpDBO> employeeList =
				sessionFactory.withSession(s->{
					Mutiny.Query<EmpDBO> query = s.createQuery(queryString.toString(),EmpDBO.class);
					query.setParameter("empNumber", empNo);
					return query.getResultList();
				}).await().indefinitely();
		return Utils.isNullOrEmpty(employeeList) ? false : true;
	}
	public void saveOrUpdateEmp(EmpDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}
	public List<EmpProfileLeaveAllotmentDTO> getLeaveAllotmentDetails(Integer leaveCategoryId) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileLeaveAllotmentDTO"
				+ " (empLeaveTypeDBO.id, empLeaveTypeDBO.leaveTypeName, dbo.allottedLeaves, empLeaveCategoryAllotmentDBO.leaveIinitializeMonth, dbo.displayOrder)"
				+ " from EmpLeaveCategoryAllotmentDetailsDBO dbo"
				+ " left join dbo.empLeaveTypeDBO empLeaveTypeDBO"
				+ " left join dbo.empLeaveCategoryAllotmentDBO empLeaveCategoryAllotmentDBO"
				+ " where  dbo.recordStatus  = 'A' and empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentId = :leaveCategoryId and dbo.isApplicable = 1"
				+ " order by dbo.displayOrder";
		return  sessionFactory.withSession(s->s.createQuery(query, EmpProfileLeaveAllotmentDTO.class).setParameter("leaveCategoryId", leaveCategoryId).getResultList()).await().indefinitely();
	}
	public List<EmpProfileLeaveAllotmentDTO> getAssignedLeaves(Integer empId, Integer year) {
		String query = " select new com.christ.erp.services.dto.employee.profile.EmpProfileLeaveAllotmentDTO"
				+ " (dbo.id, leaveType.id, leaveType.leaveTypeName, dbo.allottedLeaves, dbo.sanctionedLeaves, dbo.leavesRemaining, dbo.leavesPending,"
				+ " dbo.year, dbo.month)"
				+ " from EmpLeaveAllocationDBO dbo"
				+ " left join dbo.leaveType leaveType"
				+ " where dbo.recordStatus  = 'A' and dbo.empDBO.id = :empId and dbo.year = :year";
		return  sessionFactory.withSession(s->s.createQuery(query, EmpProfileLeaveAllotmentDTO.class).setParameter("empId", empId).setParameter("year", year).getResultList()).await().indefinitely();
	}
}