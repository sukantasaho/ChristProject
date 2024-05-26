package com.christ.erp.services.transactions.employee.common;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.dto.employee.recruitment.EmpProfileComponentMapDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryActivityDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryMainActivityDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDesignationDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistMainDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAdvertisementDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDetailDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleLevelDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpShiftTypesDTO;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import com.christ.erp.services.dto.employee.common.WorkFlowStatusApplicantTimeLineDTO;

import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
@Repository
public class CommonEmployeeTransaction {
    private static volatile CommonEmployeeTransaction commonEmployeeTransaction = null;
    
    @Autowired
	private Mutiny.SessionFactory sessionFactory;

    public static CommonEmployeeTransaction getInstance() {
        if(commonEmployeeTransaction==null) {
            commonEmployeeTransaction = new CommonEmployeeTransaction();
        }
        return commonEmployeeTransaction;
    }

	public List<Tuple> getJobCategory(String employeeCategoryId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " SELECT JCL.emp_employee_job_category_id AS 'ID', JCL.employee_job_name AS 'Text' , is_show_in_appln as showInAppln,JCL.job_category_code as 'Code' "
						   + " FROM emp_employee_job_category AS JCL WHERE record_status='A' and JCL.emp_employee_category_id=:employeeCategoryID ORDER BY JCL.employee_job_name ASC " ;
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("employeeCategoryID", employeeCategoryId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ApiResult<List<LookupItemDTO>> getLeaveCategory() throws Exception {
		ApiResult<List<LookupItemDTO>> leaveCategory = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Utils.getDropdownData(leaveCategory, context, "SELECT emp_leave_category_allotment_id AS ID, emp_leave_category_allotment_name  as Text "
						+ "FROM emp_leave_category_allotment   where record_status='A'", null);
				return leaveCategory;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ApiResult<List<LookupItemDTO>> getTimeZone() throws Exception {
		ApiResult<List<LookupItemDTO>> timeZoneList = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Utils.getDropdownData(timeZoneList, context, "select emp_time_zone_id as ID,time_zone_name as Text "
						+ " from emp_time_zone where record_status='A'", null);
				return timeZoneList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<EmpDocumentChecklistMainDBO> getAllEmpDocumentCheckList() throws Exception {
		 return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDocumentChecklistMainDBO>>() {
			 @Override
	         public List<EmpDocumentChecklistMainDBO> onRun(EntityManager context) throws Exception {
	        	 String str="from EmpDocumentChecklistMainDBO dbo where dbo.recordStatus='A' and dbo.isForeignNationalDocumentChecklist=false";
	        	 Query query = context.createQuery(str.toString(), EmpDocumentChecklistMainDBO.class);
	             List<EmpDocumentChecklistMainDBO> mappings = query.getResultList();
	             return mappings;
	         }
	         @Override
	         public void onError(Exception error) throws Exception {
	            throw error;
	         }
	     });
	}

	public List<EmpDocumentChecklistMainDBO> getAllEmpDocumentCheckListwithForeign() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDocumentChecklistMainDBO>>() {
			 @Override
	         public List<EmpDocumentChecklistMainDBO> onRun(EntityManager context) throws Exception {
	        	 String str="from EmpDocumentChecklistMainDBO dbo where dbo.recordStatus='A' and dbo.isForeignNationalDocumentChecklist=true";
	        	 Query query = context.createQuery(str.toString(), EmpDocumentChecklistMainDBO.class);
	             List<EmpDocumentChecklistMainDBO> mappings = query.getResultList();
	             return mappings;
	         }
	         @Override
	         public void onError(Exception error) throws Exception {
	            throw error;
	         }
	     });
	}

	public Tuple getApplicationNumberLength() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			 @Override
	         public Tuple onRun(EntityManager context) throws Exception {
	        	 String str="select e.emp_appln_number_generation_id as ID , e.appln_number_from as 'Text' from emp_appln_number_generation e where e.is_current_range=true and e.record_status='A'";	 
				 Query query = context.createNativeQuery(str.toString(), Tuple.class);
				 Tuple mappings=null;
				 try {
					mappings = (Tuple) Utils.getUniqueResult(query.getResultList());
				 } catch (Exception e) {
					e.printStackTrace();
				 }
	             return mappings;
	         }
	         @Override
	         public void onError(Exception error) throws Exception {
	            throw error;
	         }
	     });
	}

	public Tuple applicantPersonalData(String applicantId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			 @Override
	        public Tuple onRun(EntityManager context) throws Exception {
	       	String str="select DISTINCT e.emp_appln_entries_id as ApplicantId,e.personal_email_id as email, e.mobile_no as mobile, e.applicant_name as ApplicantName , ecat.employee_category_name as PostApplied, e1.campus_name as Campus,e2.country_name as Country,ecat.emp_employee_category_id as CategoryId from emp_appln_entries e " + 
	     			"	inner join emp_employee_category ecat on ecat.emp_employee_category_id=e.emp_employee_category_id" + 
	    			"	left join erp_campus e1 on e1.erp_campus_id=e.erp_campus_id" + 
	    			"	inner join emp_appln_personal_data per on per.emp_appln_entries_id=e.emp_appln_entries_id" + 
	    			"	inner join erp_country e2 on e2.erp_country_id=per.erp_country_id where e.application_no=:applicantNumber and e.record_status='A'";
	       	Query query = context.createNativeQuery(str.toString(), Tuple.class);
			query.setParameter("applicantNumber", Integer.parseInt(applicantId));
			Tuple mappings=null;
			try {
				mappings = (Tuple)  Utils.getUniqueResult(query.getResultList());
			} catch (Exception e) {
				e.printStackTrace();
			}
	            return mappings;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	           throw error;
	        }
	       });
	}
	
	public List<Tuple> getCellValueByCategoryId(String employeeCategoryID) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select matrixDetails.emp_pay_scale_matrix_detail_id as ID, matrixDetails.level_cell_value AS 'Text' , max(e.pay_scale_revised_year) as RevisedYear "
						+ "  from emp_pay_scale_matrix_detail matrixDetails "
						+ "  inner join emp_pay_scale_grade_mapping_detail mappingDetails ON mappingDetails.emp_pay_scale_grade_mapping_detail_id = matrixDetails.emp_pay_scale_grade_mapping_detail_id "
						+ "  inner join emp_pay_scale_grade_mapping e ON e.emp_pay_scale_grade_mapping_id = mappingDetails.emp_pay_scale_grade_mapping_id "
						+ "  inner join emp_pay_scale_grade e1 on e.emp_pay_scale_grade_id=e1.emp_pay_scale_grade_id "
						+ "  where e1.emp_employee_category_id=:employeeCategoryID "
						+ "  AND e.record_status='A' AND e1.record_status='A' AND mappingDetails.record_status = 'A' AND matrixDetails.record_status = 'A' "
						+ "  group by e1.emp_pay_scale_grade_id, e1.grade_name,mappingDetails.emp_pay_scale_grade_mapping_detail_id, "
						+ "  mappingDetails.emp_pay_scale_level_id,matrixDetails.emp_pay_scale_matrix_detail_id,matrixDetails.level_cell_value "; // query changed
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("employeeCategoryID", Integer.parseInt(employeeCategoryID));
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getEmployeeDlyWageDetails(String employeeCategoryId, String employeeJobCategoryId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select emp_daily_wage_slab.emp_daily_wage_slab_id as dly_wg_slab_Id , emp_daily_wage_slab.emp_employee_category_id as emp_cat_id , "
						+ "  emp_employee_category.employee_category_name as emp_cat_name , emp_daily_wage_slab.emp_employee_job_category_id as emp_job_cat_id , "
						+ "  emp_employee_job_category.employee_job_name as emp_job_cat_name ,emp_daily_wage_slab.daily_wage_slab_from as dly_wge_from , "
						+ "  emp_daily_wage_slab.daily_wage_slab_to as dly_wge_to , emp_daily_wage_slab.daily_wage_basic as dly_wge_bsc  from emp_daily_wage_slab "
						+ "  inner join emp_employee_category on emp_daily_wage_slab.emp_employee_category_id = emp_employee_category.emp_employee_category_id "
						+ "  inner join emp_employee_job_category on emp_daily_wage_slab.emp_employee_job_category_id = emp_employee_job_category.emp_employee_job_category_id "
						+ "  where emp_daily_wage_slab.record_status = 'A' and emp_daily_wage_slab.emp_employee_category_id =:empCatId and emp_daily_wage_slab.emp_employee_job_category_id =:empJobCatId ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empCatId", Integer.parseInt(employeeCategoryId));
				query.setParameter("empJobCatId", Integer.parseInt(employeeJobCategoryId));
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	
	public List<Tuple> getEmployeetitles() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select mst.emp_title_id as 'ID', mst.title_name as 'Text' from emp_title as mst where record_status= 'A' order by mst.title_name asc";
				Query query = context.createNativeQuery(str,Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	    
	public List<Tuple> getEmployees() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select mst.emp_id AS 'ID', mst.emp_name as 'Text' from emp as mst where record_status= 'A' order BY mst.emp_name asc";
				Query query = context.createNativeQuery(str,Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<ErpCampusDepartmentMappingDBO> getCampusDepartmentMappings(String locId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusDepartmentMappingDBO>>() {
			@Override
			public List <ErpCampusDepartmentMappingDBO> onRun(EntityManager context) throws Exception  {
				List<ErpCampusDepartmentMappingDBO> dboList = null;
				 Query query = context.createQuery("select distinct E FROM ErpCampusDepartmentMappingDBO E WHERE E.recordStatus='A' and E.erpCampusDBO.recordStatus='A' and E.erpCampusDBO.erpLocationDBO.id=:locId");
				query.setParameter("locId", Integer.parseInt(locId));
				dboList = (List<ErpCampusDepartmentMappingDBO>) query.getResultList();
				return dboList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});   
	}

	public List<EmpDBO> getEmpDetails(HolidaysAndEventsEntryDTO data, Set <Integer> campusDeanearyDeptIds)  throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDBO>>() {
			@Override
			public List <EmpDBO> onRun(EntityManager context) throws Exception  {
				List<EmpDBO> dboList = null;
				String locId = null;
				String empCatId = null;
				if (data.location.id != null && !Utils.isNullOrEmpty(data.location.id)) {
					locId = data.location.id;
				}
				if(data.empCategory.id!=null && !Utils.isNullOrEmpty(data.empCategory.id)) {
					empCatId = data.empCategory.id;
				}
				StringBuffer q = new StringBuffer("FROM EmpDBO emp WHERE emp.recordStatus='A' ");
				if (data.location.id != null) {
					q = q.append(" and emp.erpCampusDepartmentMappingDBO.recordStatus='A' and emp.erpCampusDepartmentMappingDBO.erpCampusDBO.recordStatus='A'"
							+ " and emp.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO.id = :locId ");
				}
				if(data.empCategory.id!=null && !Utils.isNullOrEmpty(data.empCategory.id)) {
					q = q.append(" and emp.empEmployeeCategoryDBO.id = :empCatId ");
				}
				if(campusDeanearyDeptIds!=null && !Utils.isNullOrEmpty(campusDeanearyDeptIds)) {
					q = q.append(" and emp.erpCampusDepartmentMappingDBO.id in :campusDeanearyDeptIds ");
				}
				Query queri = context.createQuery(q.toString());
				if (data.location.id != null && !Utils.isNullOrEmpty(data.location.id)) {
					queri.setParameter("locId", Integer.parseInt(locId));
				}
				if(data.empCategory.id!=null && !Utils.isNullOrEmpty(data.empCategory.id)) {
					queri.setParameter("empCatId", Integer.parseInt(empCatId));
				}
				if(campusDeanearyDeptIds!=null && !Utils.isNullOrEmpty(campusDeanearyDeptIds)) {
					queri.setParameter("campusDeanearyDeptIds", campusDeanearyDeptIds);
				}
				dboList = queri.getResultList();
				return dboList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});   
	}
	
	public List<Tuple> getEmpPayScaleComponents() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select SC.emp_pay_scale_components_id AS ID, SC.salary_component_name AS salaryComponentName , SC.salary_component_short_name AS salaryComponentShortName, "
						+ "  SC.salary_component_display_order  AS salaryComponentDisplayOrder,is_component_basic AS isComponentBasic,SC.percentage  AS percentage,SC.is_caculation_type_percentage AS isCaculationTypePercentage,SC.pay_scale_type AS payScaleType "
						+ "  FROM emp_pay_scale_components AS SC where SC.record_status='A' order by salaryComponentDisplayorder  ";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ApiResult<List<LookupItemDTO>> getAppliedSubject(String applicationNumber) throws Exception {
		ApiResult<List<LookupItemDTO>> subjectPref = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Map<String, Object> args = new HashMap<String, Object>();
	       		args.put("applicationNumber", applicationNumber);
				Utils.getDropdownData(subjectPref, context, "select Distinct subjectCategory.emp_appln_subject_category_id AS 'ID' ,subjectCategory.subject_category_name AS 'Text'  "
						+ "  from emp_appln_subj_specialization_pref subjectPref "
						+ "  inner join emp_appln_entries e ON e.emp_appln_entries_id = subjectPref.emp_appln_entries_id "
						+ "  inner join emp_appln_subject_category subjectCategory ON subjectCategory.emp_appln_subject_category_id = subjectPref.emp_appln_subject_category_id "
						+ "  where subjectCategory.record_status='A' and subjectPref.record_status='A' "
						+ "  and e.application_no=:applicationNumber and e.record_status='A' ", args);
				return subjectPref;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ApiResult<List<LookupItemDTO>> getAppliedSubjectSpecialization(String applicationNumber) throws Exception {
		ApiResult<List<LookupItemDTO>> subjectSpecializationPref = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Map<String, Object> args = new HashMap<String, Object>();
	       		args.put("applicationNumber", applicationNumber);
				Utils.getDropdownData(subjectSpecializationPref, context, "select  subjectCategorySpecialization.emp_appln_subject_category_specialization_id AS 'ID' ,subjectCategorySpecialization.subject_category_specialization_name AS 'Text' "
						+ "  from emp_appln_subj_specialization_pref subjectPref "
						+ "  inner join emp_appln_entries e ON e.emp_appln_entries_id = subjectPref.emp_appln_entries_id "
						+ "  inner join emp_appln_subject_category_specialization subjectCategorySpecialization ON subjectCategorySpecialization.emp_appln_subject_category_specialization_id = subjectPref.emp_appln_subject_category_specialization_id "
						+ "  where subjectCategorySpecialization.record_status='A' and subjectPref.record_status='A' "
						+ "  and e.application_no=:applicationNumber and e.record_status='A' ", args);
				return subjectSpecializationPref;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ApiResult<List<LookupItemDTO>> getSubjectCategorySpecialization(String subjectCategory) throws Exception {
		ApiResult<List<LookupItemDTO>> subjectCategorySpecialization = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Map<String, Object> args = new HashMap<String, Object>();
	       		args.put("subjectCategory", subjectCategory);
				Utils.getDropdownData(subjectCategorySpecialization, context, "select subjectCategorySpecialization.emp_appln_subject_category_specialization_id AS 'ID',subjectCategorySpecialization.subject_category_specialization_name AS 'Text' "
						+ "  from emp_appln_subject_category_specialization subjectCategorySpecialization "
						+ "  inner join emp_appln_subject_category subjectCategory ON subjectCategory.emp_appln_subject_category_id = subjectCategorySpecialization.emp_appln_subject_category_id "
						+ "  where subjectCategorySpecialization.emp_appln_subject_category_id = :subjectCategory "
						+ "  and subjectCategory.record_status = 'A' and subjectCategorySpecialization.record_status = 'A' ", args);
				return subjectCategorySpecialization;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public Tuple getApproverIdByEmployeeId(Integer empId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String queryString = " select a.leave_approver_id as approverId from emp_approvers a " + 
						" where a.emp_id=:empId and a.record_status='A' ";
				Query query = context.createNativeQuery(queryString, Tuple.class);
				query.setParameter("empId", empId);
				return (Tuple) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<ErpCampusDepartmentMappingDBO> getEmpCampusDeaneryDepartment() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusDepartmentMappingDBO>>() {
			@Override
			public List<ErpCampusDepartmentMappingDBO> onRun(EntityManager context) throws Exception {
				List<ErpCampusDepartmentMappingDBO> dboList = null;				
				Query query = context.createQuery("FROM ErpCampusDepartmentMappingDBO E WHERE E.recordStatus='A' ORDER BY E.erpCampusDBO.id ASC");
				dboList = (List<ErpCampusDepartmentMappingDBO>) query.getResultList();	
				return dboList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpShiftTypesDTO> getEmployeeWorkShifts(String campusId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpShiftTypesDTO>>() {
			@Override
			public List<EmpShiftTypesDTO> onRun(EntityManager context) throws Exception  {
				String queryString = "select emp_shift_types_id as id, shift_name as shiftName, shift_short_name as shiftShortName, is_weekly_off as isWeeklyOff from emp_shift_types " + 
					"where erp_campus_id=:campusId and record_status='A'";
				Query query = context.createNativeQuery(queryString, Tuple.class);
				query.setParameter("campusId", Integer.parseInt(campusId));
				List<Tuple> mappings = query.getResultList();
				List<EmpShiftTypesDTO> dto = new ArrayList<>();
		        if(mappings != null && mappings.size() > 0) {
		            for(Tuple mapping : mappings) {
		            	EmpShiftTypesDTO shiftDTO = new EmpShiftTypesDTO();
		            	shiftDTO.value = mapping.get("id").toString();
		            	shiftDTO.label = !Utils.isNullOrEmpty(mapping.get("shiftShortName")) ? mapping.get("shiftShortName").toString() : "";
		            	shiftDTO.shiftName = !Utils.isNullOrEmpty(mapping.get("shiftName")) ? mapping.get("shiftName").toString() : "";
		            	shiftDTO.shiftShortName = !Utils.isNullOrEmpty(mapping.get("shiftShortName")) ? mapping.get("shiftShortName").toString() : "";
		            	shiftDTO.isWeeklyOff = !Utils.isNullOrEmpty(mapping.get("isWeeklyOff")) ? (Boolean.valueOf(mapping.get("isWeeklyOff").toString()) ? true : false ): false;
		            	shiftDTO.colorText = !Utils.isNullOrEmpty(mapping.get("isWeeklyOff")) ? (Boolean.valueOf(mapping.get("isWeeklyOff").toString()) ? "red" : "black" ): "black";
		                dto.add(shiftDTO);
		            }
		        }
		        return dto;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});   
	}
	
	public List<Tuple> getEmployeeLetterRequestType() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "SELECT  MST.emp_letter_request_type_id AS 'ID', MST.letter_type_name AS 'Text' FROM emp_letter_request_type AS MST WHERE MST.record_status='A' and MST.is_available_online = 1 ORDER BY MST.letter_type_name ASC";
				Query query = context.createNativeQuery(str,Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmployeeLetterRequestReasons() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "SELECT  MST.emp_letter_request_reason_id AS 'ID', MST.letter_request_reason_name AS 'Text' FROM emp_letter_request_reason AS MST WHERE MST.record_status='A' ORDER BY MST.letter_request_reason_name ASC";
				Query query = context.createNativeQuery(str,Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getDepartmentOnCampus(String campusId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select dpt.erp_department_id as ID , dpt.department_name as Text from erp_department dpt " 
						   + " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_department_id = dpt.erp_department_id "
						   + " where dpt.record_status = 'A' and erp_campus_department_mapping.record_status = 'A' and erp_campus_department_mapping.erp_campus_id=:campusId " ;
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("campusId", campusId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

    public List<Tuple> getApplnNonAvailability(String interviewRound) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select eana.emp_appln_non_availability_id as empApplnNonAvailabilityId ,eana.non_availability_name as nonAvailabilityName," +
						" eana.is_reschedulable as isReschedulable,eana.interview_round as interviewRound,eana.is_final_selection as isFinalSelection " +
						" from emp_appln_non_availability eana where eana.record_status='A' ";
				if("selected".equalsIgnoreCase(interviewRound)){
					str += " and eana.is_final_selection=1";
				}else {
					str += " and eana.interview_round=:interviewRound ";
				}
				Query query = context.createNativeQuery(str, Tuple.class);
				if(!"selected".equalsIgnoreCase(interviewRound)){
					query.setParameter("interviewRound", Integer.parseInt(interviewRound));
				}
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
    }

	public Tuple getApplnInterviewScheduleDetails(Integer empApplnEntriesId, String interviewRound) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String str = "select eais.interview_date_time as interviewDateTime,eais.interview_venue as interviewVenue,eais.point_of_contact_users_id as point_of_contact_users_id, " +
					" ifnull(emp.emp_name, eu.erp_users_name) as userName " +
					" from emp_appln_interview_schedules eais "+
					" inner join erp_users eu on eais.point_of_contact_users_id = eu.erp_users_id"
					+ " left join emp on eu.emp_id  = emp.emp_id  "+
					" where eais.record_status='A' and eais.emp_appln_entries_id=:empApplnEntriesId and eais.interview_round=:interviewRound limit 1";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empApplnEntriesId", empApplnEntriesId);
				query.setParameter("interviewRound", Integer.parseInt(interviewRound));
				return (Tuple) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {

			}
		});
	}
	
	public ApiResult<List<LookupItemDTO>> getScorecard() throws Exception {
		ApiResult<List<LookupItemDTO>> scorecardName = new ApiResult<List<LookupItemDTO>>();
		return DBGateway.runJPA(new ISelectGenericTransactional<ApiResult<List<LookupItemDTO>>>() {
			@Override
			public ApiResult<List<LookupItemDTO>> onRun(EntityManager context) {
				Utils.getDropdownData(scorecardName, context,
						"select adm_scorecard_id as ID,scorecard_template_name as Text from adm_scorecard where record_status='A'",
						null);
				return scorecardName;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmployeeAppraisalElementOption() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select eaeo.emp_appraisal_elements_option_id as empAppraisalElementsOptionId ,eaeo.option_group_name as optionGroupName " +
						" from emp_appraisal_elements_option eaeo where eaeo.record_status='A' ";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getJobCategories() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " SELECT JCL.emp_employee_job_category_id AS 'ID', JCL.employee_job_name AS 'Text' , is_show_in_appln as showInAppln"
						   + " FROM emp_employee_job_category AS JCL WHERE JCL.record_status='A' ORDER BY JCL.employee_job_name ASC " ;
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmpTitleList() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "SELECT  T.emp_title_id AS 'ID', T.title_name AS 'Text', T.is_general_for_university AS 'UnivercityLevel' FROM emp_title AS T WHERE T.record_status='A' ORDER BY T.title_name ASC";
				Query query = context.createNativeQuery(str,Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Mono<List<EmpWorkDiaryMainActivityDBO>> getActivityType() {
		String str = "from EmpWorkDiaryMainActivityDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpWorkDiaryMainActivityDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	
	public Mono<List<EmpWorkDiaryActivityDBO>> getActivity(Integer userId) {
	    String str1= "select emp_employee_category.is_employee_category_academic as isEmployeeCategoryAcademic,erp_campus_department_mapping.erp_department_id as deptId from erp_users"
	           		+ " inner join emp on erp_users.emp_id = emp.emp_id"
	           		+ " left join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = erp_users.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status = 'A'"
	           		+ " inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id and emp_employee_category.record_status ='A'"
	           		+ " where erp_users.erp_users_id =:userId";
	     Tuple tuple  = sessionFactory.withSession(s->s.createNativeQuery(str1, Tuple.class).setParameter("userId", userId).getSingleResultOrNull()).await().indefinitely(); 
	     String isAcademicOrNot = tuple.get("isEmployeeCategoryAcademic").toString();
	     if(isAcademicOrNot.equals("1")) {
	    	 String str = "from EmpWorkDiaryActivityDBO dbo where dbo.recordStatus = 'A' and dbo.isForTeaching =1";
		     Mono<List<EmpWorkDiaryActivityDBO>> list = Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, EmpWorkDiaryActivityDBO.class).getResultList()).subscribeAsCompletionStage());
		     return list;
		 } 
	     else {
			 if(!Utils.isNullOrEmpty(tuple.get("deptId"))) { 
		        String str = "from EmpWorkDiaryActivityDBO dbo where dbo.recordStatus ='A' and dbo.isForTeaching=0 and dbo.erpDepartmentDBO.id =: deptId";
	            Mono<List<EmpWorkDiaryActivityDBO>> list =Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str,EmpWorkDiaryActivityDBO.class).setParameter("deptId", tuple.get("deptId")).getResultList()).subscribeAsCompletionStage());
			    return list;
			 }
         }
	     return null;
     }	 
	
	public  Mono<List<Tuple>> getLeaveTypeForLeaveApplication(Map<String, String> requestParams) {
		String str = "";
		if(!Utils.isNullOrEmpty(requestParams)) {
			
			if(!Utils.isNullOrEmpty(requestParams.get("leaveApplication")) && requestParams.get("leaveApplication").equalsIgnoreCase("online"))
			   str = "select ltype.emp_leave_type_id  AS 'ID', ltype.leave_type_name  AS 'Text', ltype.is_leave_type_document_required As 'DOCUMENTREQUIRED' from emp_leave_type ltype where ltype.record_status = 'A' and ltype.is_apply_online=1";
			else if(requestParams.get("leaveApplication").equalsIgnoreCase("offline") && requestParams.get("isExemption").equalsIgnoreCase("false"))
				str= "select ltype.emp_leave_type_id  AS 'ID', ltype.leave_type_name  AS 'Text', ltype.is_leave_type_document_required As 'DOCUMENTREQUIRED' from emp_leave_type ltype where ltype.record_status = 'A' and ltype.is_leave_exemption=0 ";
			else if(requestParams.get("leaveApplication").equalsIgnoreCase("offline") && requestParams.get("isExemption").equalsIgnoreCase("true"))
				str ="select ltype.emp_leave_type_id  AS 'ID', ltype.leave_type_name  AS 'Text', ltype.is_leave_type_document_required As 'DOCUMENTREQUIRED' from emp_leave_type ltype where ltype.record_status = 'A' and ltype.is_leave_exemption=1 ";
			String finalstr = str; 
			return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(finalstr, Tuple.class).getResultList()).subscribeAsCompletionStage());
		}
		else {
			return Mono.empty();
		}
	
	}
	
	public boolean duplicateCheck(int applnentriesid) {
		String str = "from EmpDBO dbo where dbo.empApplnEntriesDBO.id = :applnentriesid and  dbo.recordStatus != 'D'";
		List<EmpDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpDBO> query = s.createQuery(str, EmpDBO.class);
			query.setParameter("applnentriesid",applnentriesid );
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public EmpApplnEntriesDBO getApplnData(int applnentriesid) {
		EmpApplnEntriesDBO empApplnEntriesDBO = sessionFactory.withSession(s -> s.createQuery("select dbo from EmpApplnEntriesDBO dbo "
				+ " left join fetch dbo.empApplnEducationalDetailsDBOs as eaed left join fetch eaed.documentsDBOSet as edudoc"
				+ " left join fetch dbo.empApplnEligibilityTestDBOs as eaet left join fetch eaet.eligibilityTestDocumentDBOSet as elidoc"
				+ " left join fetch dbo.empApplnWorkExperienceDBOs as eawe left join fetch eawe.workExperienceDocumentsDBOSet as workdoc"
				+ " left join fetch dbo.empApplnPersonalDataDBO as empAppl"
				+ " left join fetch empAppl.empFamilyDetailsAddtnlDBOS "
				+ " left join dbo.empJobDetailsDBO"
				+ " where dbo.id=:empapplnentriesid "
				+ " and dbo.recordStatus='A'"
//				+ " and  empAppl.recordStatus='A' and eaed.recordStatus='A'  "
//				+ " and edudoc.recordStatus='A' and eaet.recordStatus='A' and elidoc.recordStatus='A' and eawe.recordStatus='A' and workdoc.recordStatus='A'"
				+ "", EmpApplnEntriesDBO.class).setParameter("empapplnentriesid", applnentriesid).getSingleResultOrNull()).await().indefinitely();
		return empApplnEntriesDBO;
	}

	public int mergeEmployee(EmpDBO dbo) {
		return sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely().getId();
	}

	public List<ErpCampusProgrammeMappingDBO> getCampusLevelProgramme() throws Exception {

    	return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusProgrammeMappingDBO>>() {
			@Override
			public List<ErpCampusProgrammeMappingDBO> onRun(EntityManager context) throws Exception {
				List<ErpCampusProgrammeMappingDBO> dboList = null;
				Query query = context.createQuery("FROM ErpCampusProgrammeMappingDBO E WHERE E.recordStatus='A'");
				dboList = (List<ErpCampusProgrammeMappingDBO>) query.getResultList();
				return dboList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	
	}	 
	
	public Mono<List<EmpDBO>> getEmployeeWithDepartment() {
		String str = "from EmpDBO dbo where dbo.recordStatus = 'A' order by dbo.empName";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<ErpCampusDepartmentMappingDBO>> getCampusDepartmentConcat() {
		String str = "from ErpCampusDepartmentMappingDBO dbo where dbo.recordStatus = 'A' ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpCampusDepartmentMappingDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpCampusDepartmentMappingDBO>> getCampusByDepartmentId(String deptId) {
		String str = "from ErpCampusDepartmentMappingDBO dbo where dbo.recordStatus = 'A'  and dbo.erpDepartmentDBO.id = :deptId ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpCampusDepartmentMappingDBO.class).setParameter("deptId", Integer.parseInt(deptId)).getResultList()).subscribeAsCompletionStage());
	}
	

	public Mono<EmpApplnAdvertisementDBO> getEmployeeAapplicationAdvertisement(LocalDate localDate) {
		String str = "from EmpApplnAdvertisementDBO dbo "
				+ " left join fetch dbo.empApplnAdvertisementImagesSet empimg"
				+ " where  dbo.recordStatus = 'A' and empimg.recordStatus = 'A' and dbo.advertisementStartDate = :localDate"
				+ " or (:localDate >= dbo.advertisementStartDate and :localDate <= dbo.advertisementEndDate and dbo.recordStatus = 'A')";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpApplnAdvertisementDBO.class).setParameter("localDate", localDate).getSingleResultOrNull()).subscribeAsCompletionStage());
	}
	public Mono<List<EmpDBO>> getEmployeeNameBySearch(String employeeName){
		String query = "from EmpDBO dbo where dbo.recordStatus='A' and dbo.empName like :empName";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query,EmpDBO.class).setParameter("empName", employeeName+"%").getResultList()).subscribeAsCompletionStage());
		
	}
	public Mono<List<EmpDBO>> getEmployeeIdBySearch(String employeeId){
		String query = "from EmpDBO dbo where dbo.recordStatus='A' and dbo.empNumber like :employeeId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query,EmpDBO.class).setParameter("employeeId", employeeId+"%").getResultList()).subscribeAsCompletionStage());
		
	}

	public Mono<List<EmpPayScaleLevelDBO>> getPayScaleLevel() {
		String str = " select dbo from EmpPayScaleLevelDBO dbo where dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpPayScaleLevelDBO.class).getResultList()).subscribeAsCompletionStage());	
	}


	public List<EmpPayScaleGradeMappingDetailDBO> empPayScaleByEmpCategory(String empCategoryId) {
		String str = " select DISTINCT dbo from EmpPayScaleGradeMappingDetailDBO as dbo "
				+ " where dbo.empPayScaleGradeMappingDBO.recordStatus = 'A' and "
				+ " dbo.empPayScaleLevelDBO.recordStatus = 'A' and "
				+ " dbo.empPayScaleGradeMappingDBO.empPayScaleGradeDBO.recordStatus = 'A' and "
				+ " dbo.empPayScaleGradeMappingDBO.empPayScaleGradeDBO.empEmployeeCategoryDBO.recordStatus = 'A' and "
				+ " dbo.empPayScaleGradeMappingDBO.empPayScaleGradeDBO.empEmployeeCategoryDBO.id =: empCategoryId ";
		String finalStr = str ;
		List<EmpPayScaleGradeMappingDetailDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpPayScaleGradeMappingDetailDBO> query = s.createQuery(finalStr,EmpPayScaleGradeMappingDetailDBO.class);
			if(!Utils.isNullOrEmpty(empCategoryId)) {
				query.setParameter("empCategoryId", Integer.parseInt(empCategoryId));
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

//	public Mono<EmpApplnAdvertisementDBO> getEmpApplicationAdvertisement(LocalDate localDate) {
//		String str = " select distinct dbo from EmpApplnAdvertisementDBO dbo "
//				+" left join fetch dbo.empApplnAdvertisementImagesSet dbos"
//				+" where dbo.recordStatus = 'A' and dbos.recordStatus = 'A' and "
//				+" ((dbo.advertisementStartDate = :localDate and dbo.advertisementEndDate =:localDate) OR "
//				+" (:localDate between dbo.advertisementStartDate and dbo.advertisementEndDate))";
//		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpApplnAdvertisementDBO.class).setParameter("localDate", localDate).getSingleResultOrNull()).subscribeAsCompletionStage());
//	}
	
	public EmpApplnAdvertisementDBO getEmpApplicationAdvertisement(LocalDate localDate) {
		String str = " select distinct dbo from EmpApplnAdvertisementDBO dbo "
		+" left join fetch dbo.empApplnAdvertisementImagesSet dbos"
		+" where dbo.recordStatus = 'A' and dbos.recordStatus = 'A' and "
		+" ((dbo.advertisementStartDate = :localDate and dbo.advertisementEndDate =:localDate) OR "
		+" (:localDate between dbo.advertisementStartDate and dbo.advertisementEndDate))";
		EmpApplnAdvertisementDBO empApplnAdvertisementDBO = sessionFactory.withSession(s -> s.createQuery(str, EmpApplnAdvertisementDBO.class).setParameter("localDate", localDate).getSingleResultOrNull()).await().indefinitely();
		return empApplnAdvertisementDBO;
	}

//	public Mono<EmpApplnAdvertisementDBO> getEmpAdvertisements() {
//		String str = " select distinct dbo from EmpApplnAdvertisementDBO dbo"
//				+" where dbo.recordStatus ='A' and dbo.isCommonAdvertisement = 1";
//		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpApplnAdvertisementDBO.class).getSingleResultOrNull()).subscribeAsCompletionStage());		
//	}
	
	public EmpApplnAdvertisementDBO getEmpAdvertisements() {
		String str = " select distinct dbo from EmpApplnAdvertisementDBO dbo"
		+" where dbo.recordStatus ='A' and dbo.isCommonAdvertisement = 1";
		EmpApplnAdvertisementDBO empApplnAdvertisementDBO = 
		sessionFactory.withSession(s -> s.createQuery(str, EmpApplnAdvertisementDBO.class).getSingleResultOrNull()).await().indefinitely();		
	return empApplnAdvertisementDBO;
	}
	
	public List<Tuple> getEmployeeWithTitle() {
		String str = " SELECT DISTINCT emp_id, emp_name,  "
				+ "CASE WHEN emp.emp_title_id IS NOT NULL  "
				+ "THEN emp_title.title_name  "
				+ "ELSE NULL  "
				+ "END AS title_name "
				+ "FROM emp "
				+ "LEFT JOIN emp_title ON emp.emp_title_id = emp_title.emp_title_id AND emp_title.record_status = 'A' "
				+ "WHERE emp.record_status = 'A' ORDER BY emp.emp_name ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public List<Tuple> getEmployeeIdOrName(String employeeIdOrName) {
		String query = "select e.emp_id as id ,e.emp_name as name ,e.emp_no as empId from emp e where e.record_status = 'A' and (e.emp_no like :employeeId or e.emp_name like :empName)";
		return sessionFactory.withSession(s -> s.createNativeQuery(query,Tuple.class).setParameter("empName", employeeIdOrName+"%")
				.setParameter("employeeId", employeeIdOrName+"%").getResultList()).await().indefinitely();
	}
		
	public Mono<List<EmpDesignationDBO>> getEmpDesignation() {
		String str = " select distinct dbo from EmpDesignationDBO dbo  where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpDesignationDBO.class).getResultList()).subscribeAsCompletionStage());		
	}
	
	public Mono<List<ErpDepartmentDBO>> getDepartmentsByDeanery(String deaneryId) {
		String str = " select distinct dbo from ErpDepartmentDBO dbo  where dbo.recordStatus = 'A' and dbo.erpDeaneryDBO.id = :deaneryId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpDepartmentDBO.class).setParameter("deaneryId", Integer.parseInt(deaneryId)).getResultList()).subscribeAsCompletionStage());		
	}

	public Mono<List<SelectDTO>> getResignationReason() {
		String str = " select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, dbo.resignationName) from EmpResignationReasonDBO dbo where dbo.recordStatus = 'A' ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).getResultList()).subscribeAsCompletionStage());		
	}
	
	public Mono<String> duplicateCheckForEmployee(Integer applnNo) {
        String str = "select  cast(dbo.recordStatus as string) from EmpDBO dbo where dbo.empApplnEntriesDBO.recordStatus like 'A' and  dbo.empApplnEntriesDBO.applicationNo = :applnNo and  dbo.recordStatus != 'D'";
        Mono<String> status = Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<String> query = s.createQuery(str,String.class);
            query.setParameter("applnNo",applnNo);
            return query.getSingleResultOrNull();
        }).subscribeAsCompletionStage());
        return status;
    }
	
	public Mono<List<SelectDTO>> getRevisedYear(Integer gradeId) {
		String str = " select distinct new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.payScaleRevisedYear, cast(dbo.payScaleRevisedYear as string)) from EmpPayScaleGradeMappingDBO dbo where dbo.recordStatus = 'A' and dbo.empPayScaleGradeDBO.id =: gradeId";
		return	Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).setParameter("gradeId", gradeId).getResultList()).subscribeAsCompletionStage());		
	}

//	public void getApplicantDataAndTimeLine(Integer applnNo) {
//		String str = "select com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO  from EmpApplnEntriesDBO dbo"
//				+ "(dbo.applicantName, dbo.applicationNo,dbo.empApplnPersonalDataDBO.profilePhotoUrlDBO.fileNameUnique, dbo.empEmployeeCategoryDBO.employeeCategoryName, dbo.empEmployeeCategoryDBO.isEmployeeCategoryAcademic,"
//				+ ")"
//				+ "left join ";
//		
//	}
	public Tuple getApplicantDataAndTimeLine(Integer applnNo) {
		String str ="select  "
				+ "distinct GROUP_CONCAT(DISTINCT ed.erp_department_id), dbo.emp_appln_entries_id, dbo.application_no, dbo.applicant_name , ual.file_name_unique, ual.file_name_original, folder.upload_process_code, "
				+ "ec.employee_category_name, "
				+ "ec.is_employee_category_academic, eql.qualification_level_name, "
				+ "concat(qual.ex_year,'  years ',qual.exp_month, ' months') as experience, "
				+ "cast(GROUP_CONCAT(DISTINCT eascs.subject_category_specialization_name) as char) AS subjectCategorySpecializationName, eap.emp_appln_personal_data_id,  "
				+ "cast(GROUP_CONCAT(DISTINCT easc.subject_category_name) as char) AS subject_category_name, cast(GROUP_CONCAT(DISTINCT el.location_name) as char) AS location_name, cast(GROUP_CONCAT(DISTINCT ed.department_name) as char) AS department_name "
				+ "from emp_appln_entries dbo "
				+ "left join emp_appln_personal_data eap on dbo.emp_appln_entries_id = eap.emp_appln_entries_id  "
				+ "left join url_access_link ual ON ual.url_access_link_id = eap.profile_photo_url_id  "
				+ "left join url_folder_list folder on ual.url_folder_list_id = folder.url_folder_list_id "
				+ "left join emp_employee_category ec ON ec.emp_employee_category_id = dbo.emp_employee_category_id  "
				+ "left join emp_appln_subj_specialization_pref ssp on ssp.emp_appln_entries_id = dbo.emp_appln_entries_id  "
				+ "left join emp_appln_subject_category easc ON easc.emp_appln_subject_category_id = ssp.emp_appln_subject_category_id  "
				+ "left join emp_appln_subject_category_specialization eascs ON eascs.emp_appln_subject_category_specialization_id = ssp.emp_appln_subject_category_specialization_id "
				+ "left join erp_qualification_level eql on eql.erp_qualification_level_id = dbo.highest_qualification_level "
				+ "left join (select  "
				+ "emp_appln_entries.emp_appln_entries_id "
				+ ",floor((sum(emp_appln_work_experience.work_experience_years)*12 + sum(emp_appln_work_experience.work_experience_month))/12) as ex_year "
				+ ",(sum(emp_appln_work_experience.work_experience_years)*12 + sum(emp_appln_work_experience.work_experience_month))%12 as exp_month "
				+ "from emp_appln_work_experience "
				+ "inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_work_experience.emp_appln_entries_id "
				+ "where emp_appln_work_experience.record_status='A' "
				+ "and emp_appln_entries.record_status='A' "
				+ "group by emp_appln_entries.emp_appln_entries_id "
				+ ") as qual ON qual.emp_appln_entries_id = dbo.emp_appln_entries_id "
				+ "left join erp_department ed ON ed.erp_department_id = dbo.shortlisted_department_id  "
				+ "left join emp_appln_location_pref ealp on dbo.emp_appln_entries_id = ealp.emp_appln_entries_id "
				+ "left join erp_location el ON el.erp_location_id = ealp.erp_location_id  "
				+ "where dbo.record_status like 'A' and dbo.application_no = :applnNo "
				+ "group by dbo.emp_appln_entries_id, eap.emp_appln_personal_data_id ";
	return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applnNo", applnNo).getSingleResultOrNull()).await().indefinitely();
	}

	public List<WorkFlowStatusApplicantTimeLineDTO> getWorkFlowStatusLog(Integer applnId) {
		String str = "select new com.christ.erp.services.dto.employee.common.WorkFlowStatusApplicantTimeLineDTO "
				+ "(dbo.erpWorkFlowProcessDBO.id, dbo.erpWorkFlowProcessDBO.applicationStatusDisplayText, min(dbo.createdTime), max(dbo.createdTime), "
				+ "CASE WHEN ead.applicationCurrentProcessStatus.id =dbo.erpWorkFlowProcessDBO.id THEN true ELSE false END) "
				+ "from ErpWorkFlowProcessStatusLogDBO dbo "
				+ "inner join  EmpApplnEntriesDBO ead on ead.id = dbo.entryId "
				+	"where dbo.recordStatus like 'A' and dbo.entryId = :applnId "
				+ "group by dbo.entryId,dbo.erpWorkFlowProcessDBO.id";
		return sessionFactory.withSession(s -> s.createQuery(str,WorkFlowStatusApplicantTimeLineDTO.class).setParameter("applnId", applnId).getResultList()).await().indefinitely();
	}

	public List<ErpWorkFlowProcessNotificationsDBO> getWorkFlowNotificationByWorkflowIds(Integer workFlowId) {
		String query = " select distinct dbo from ErpWorkFlowProcessNotificationsDBO dbo  where dbo.recordStatus = 'A' and dbo.erpWorkFlowProcessDBO.id = :workFlowId";
		return sessionFactory.withSession(s->s.createQuery(query,ErpWorkFlowProcessNotificationsDBO.class).setParameter("workFlowId", workFlowId).getResultList()).await().indefinitely();
	}
	
	public List<ErpCampusDBO> getCampusForLocation(List<Integer> locId) {
		String str = " from ErpCampusDBO dbo where dbo.recordStatus = 'A' and dbo.erpLocationDBO.recordStatus = 'A' and "
				+ " dbo.erpLocationDBO.id in (:locId)";
		List<ErpCampusDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCampusDBO> query = s.createQuery(str, ErpCampusDBO.class);
			query.setParameter("locId", locId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getCampusForLocationsAndUser(List<Integer> locId, String userId) {
		String query = "select erp_campus.erp_campus_id as id,erp_campus.campus_name as name from emp"
				+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status = 'A'"
				+ " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status = 'A'"
				+ " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id and erp_location.record_status = 'A'"
				+ " where  emp.record_status = 'A' and  emp.emp_id = :userId and erp_location.erp_location_id in(:locId)";
		return sessionFactory.withSession(s -> s.createNativeQuery(query,Tuple.class).setParameter("userId", Integer.parseInt(userId)).setParameter("locId", locId)
				.getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getDepartmentsByCampusIds(List<Integer> campusIds) {
		String query = "select erp_department.erp_department_id as id, erp_department.department_name as departName from erp_campus_department_mapping"
				+ " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status = 'A'"
				+ " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id and erp_department.record_status = 'A'"
				+ " where  erp_campus_department_mapping.erp_campus_id in (:campusIds) and erp_campus_department_mapping.record_status = 'A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(query,Tuple.class).setParameter("campusIds", campusIds)
				.getResultList()).await().indefinitely();
	}
	public Mono<List<SelectDTO>> getAllSubjectAndCategory() {
		String str = " select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, dbo.subjectCategory) from EmpApplnSubjectCategoryDBO dbo where dbo.recordStatus = 'A' order by dbo.subjectCategory";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).getResultList()).subscribeAsCompletionStage());		
	}
	public Mono<List<SelectDTO>> getAllSubjectCategorySpecialisation() {
		String str = " select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.empApplnSubjectCategorySpecializationId, dbo.subjectCategorySpecializationName) from EmpApplnSubjectCategorySpecializationDBO dbo "
				+ "	where dbo.recordStatus = 'A' order by dbo.subjectCategorySpecializationName";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<SelectDTO>> getLetterTypes() {
		String str = " select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (dbo.id, dbo.empLetterTypeName) from EmpLetterTypeDBO dbo "
				+ "	where dbo.recordStatus = 'A' order by dbo.empLetterTypeName";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).getResultList()).subscribeAsCompletionStage());
	}
	public Integer getEmpId(Integer id) {
		String str = "select emp.emp_id  "
				+ " from erp_users "
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " where erp_users.record_status='A' and erp_users.erp_users_id = :id ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<SelectDTO>> getTimeZoneByCategory(int categoryId,String isSelected) {
		String str = null;
		if(!Utils.isNullOrEmpty(isSelected)){
			if(isSelected.trim().equalsIgnoreCase("general")){
				str = " select new com.christ.erp.services.dto.common.SelectDTO "
						+ " (dbo.id, dbo.timeZoneName) from EmpTimeZoneDBO dbo "
						+ "	where dbo.recordStatus = 'A' and dbo.empEmployeeCategoryDBO.id =:categoryId and dbo.isGeneralTimeZone = true";
			}else if(isSelected.trim().equalsIgnoreCase("holiday")){
				str = " select new com.christ.erp.services.dto.common.SelectDTO "
						+ " (dbo.id, dbo.timeZoneName) from EmpTimeZoneDBO dbo "
						+ "	where dbo.recordStatus = 'A' and dbo.empEmployeeCategoryDBO.id =:categoryId and dbo.isHolidayTimeZone = 1";
			}
		}
		String finalStr = str;
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(finalStr, SelectDTO.class).setParameter("categoryId",categoryId).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<EmpProfileComponentMapDTO>> getJobCategoryComponentMapping(Integer jobCategoryId) {
		String queryString = "select new com.christ.erp.services.dto.employee.recruitment.EmpProfileComponentMapDTO"
				+ " (sysComponentDBO.componentName, dbo.displayStatus)"
				+ " from EmpProfileComponentMappingDBO dbo"
				+ " left join dbo.sysComponentDBO sysComponentDBO "
				+ " where dbo.empEmployeeJobCategoryDBO.id = :jobCategoryId and dbo.recordStatus = 'A' and sysComponentDBO is not null";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, EmpProfileComponentMapDTO.class).setParameter("jobCategoryId", jobCategoryId).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<EmpProfileComponentMapDTO>> getJobCategoryComponentGroupMapping(Integer jobCategoryId) {
		String queryString = "select new com.christ.erp.services.dto.employee.recruitment.EmpProfileComponentMapDTO"
				+ " (sysComponentGroup.componentGroupName, dbo.displayStatus)"
				+ " from EmpProfileComponentMappingDBO dbo"
				+ " left join dbo.sysComponentGroup sysComponentGroup "
				+ " where dbo.empEmployeeJobCategoryDBO.id = :jobCategoryId and dbo.recordStatus = 'A' and sysComponentGroup is not null";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, EmpProfileComponentMapDTO.class).setParameter("jobCategoryId", jobCategoryId).getResultList()).subscribeAsCompletionStage());
	}
	public Mono<Integer> getEmpIdByUserId(Integer userId) {
		String str = "select empDBO.id from ErpUsersDBO dbo "
				+ " left join dbo.empDBO empDBO on empDBO.recordStatus = 'A' "
				+ "	where dbo.recordStatus = 'A' and dbo.id = :userId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, Integer.class).setParameter("userId", userId).getSingleResultOrNull()).subscribeAsCompletionStage());
	}
}