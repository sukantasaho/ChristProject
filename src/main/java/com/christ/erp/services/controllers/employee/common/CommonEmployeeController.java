package com.christ.erp.services.controllers.employee.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoHeadingDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoParameterDBO;
import com.christ.erp.services.dbqueries.employee.RecruitmentQueries;
import com.christ.erp.services.dto.employee.common.*;
import com.christ.erp.services.dto.employee.recruitment.EmpProfileComponentMapDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleSubDBO;
import com.christ.erp.services.dbqueries.employee.CommonEmployeeQueries;
import com.christ.erp.services.dbqueries.employee.SettingsQueries;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.EmpPositionRoleDTO;
import com.christ.erp.services.dto.employee.EmpPositionRoleSubDTO;
import com.christ.erp.services.dto.employee.attendance.EmpShiftTypesDTO;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import com.christ.erp.services.dto.employee.letter.EmpLetterRequestDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistMainDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewSchedulesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnNonAvailabilityDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDetailDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleMatrixDetailDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.common.CommonEmployeeHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Protected/Employee/CommonEmployee")
@SuppressWarnings("unchecked")
public class CommonEmployeeController extends BaseApiController {

	//CommonEmployeeHandler commonEmployeeHandler = CommonEmployeeHandler.getInstance();
	@Autowired
	CommonEmployeeHandler commonEmployeeHandler;

	@RequestMapping(value = "/getEmployeeCategory", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getEmployeeCategory() {
		ApiResult<List<EmployeeApplicationDTO>> categories = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_EMPLOYEE_CATEGORY, Tuple.class);
					List<Tuple> mappings = query.getResultList();
					if(mappings != null && mappings.size() > 0) {
						categories.success = true;
						categories.dto = new ArrayList<>();
						for(Tuple mapping : mappings) {
							if(!Utils.isNullOrEmpty(mapping.get("ID")) && !Utils.isNullOrEmpty(mapping.get("Text"))) {
								EmployeeApplicationDTO common = new EmployeeApplicationDTO();
								common.value = mapping.get("ID").toString();
								common.label = mapping.get("Text").toString();
								common.isEmployeeCategoryAcademic = !Utils.isNullOrEmpty(mapping.get("isEmployeeCategoryAcademic")) ? Boolean.valueOf(mapping.get("isEmployeeCategoryAcademic").toString()) : false;
								common.isShowInAppln = !Utils.isNullOrEmpty(mapping.get("isShowInAppln")) ? Boolean.valueOf(mapping.get("isShowInAppln").toString()) : false;
								categories.dto.add(common);
							}
						}
					}
				}
				@Override
				public void onError(Exception error) {
					categories.success = false;
					categories.dto = null;
					categories.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(categories);
	}

	@RequestMapping(value = "/getPayScaleGrade", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getPayScaleGrade(@RequestParam("employeeCategoryID") String employeeCategoryId) {
		ApiResult<List<LookupItemDTO>> grades = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Map<String, Object> args = new HashMap<String, Object>();
					if(!Utils.isNullOrEmpty(employeeCategoryId)) {
						args.put("employeeCategoryID", employeeCategoryId);
						Utils.getDropdownData(grades, context, CommonEmployeeQueries.SELECT_PAY_SCALE_GRADE, args);
					}
				}
				@Override
				public void onError(Exception error) {
					grades.success = false;
					grades.dto = null;
					grades.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(grades);
	}

//	@RequestMapping(value = "/getRevisedYear", method = RequestMethod.POST)
//	public Mono<List<LookupItemDTO>> getRevisedYear() {
//		List<LookupItemDTO> years = new ArrayList<>();
//		try {
//			years.add(new LookupItemDTO("2018","2018"));
//			years.add(new LookupItemDTO("2019","2019"));
//			years.add(new LookupItemDTO("2020","2020"));
//			years.add(new LookupItemDTO("2021","2021"));
//		}
//		catch(Exception error) {
//			Utils.log(error.getMessage());
//		}
//		return Utils.monoFromObject(years);
//	}

	@RequestMapping(value = "/getApplnSubjectCategory", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getApplnSubjectCategory(@RequestParam("isAcademic") Boolean isAcademic) {
		ApiResult<List<LookupItemDTO>> subjectCategories = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("IsAcademic", isAcademic);
					Utils.getDropdownData(subjectCategories, context, CommonEmployeeQueries.SELECT_APPLN_SUBJECT_CATEGORY, args);
				}
				@Override
				public void onError(Exception error) {
					subjectCategories.success = false;
					subjectCategories.dto = null;
					subjectCategories.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(subjectCategories);
	}

	@RequestMapping(value = "/getApplnSubjectCategorySpecialization", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getApplnSubjectCategorySpecialization(@RequestParam("subjectCategoryId") String subjectCategoryId) {
		ApiResult<List<EmployeeApplicationDTO>> specializations = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					if(!Utils.isNullOrEmpty(subjectCategoryId)) {
						Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_APPLN_SUBJECT_CATEGORY_SPECIALIZATION, Tuple.class);
						List<Integer> categoryIdList = new ArrayList<>();
						String[] categoryIds = subjectCategoryId.split(",");
						for(String categoryId : categoryIds){
							categoryIdList.add(Integer.parseInt(categoryId));
						}
						query.setParameter("categoryIds", categoryIdList);
						List<Tuple> mappings = query.getResultList();
						specializations.success = true;
						if(mappings != null && mappings.size() > 0) {
							specializations.dto = new ArrayList<>();
							for(Tuple mapping : mappings) {
								EmployeeApplicationDTO common = new EmployeeApplicationDTO();
								common.value = String.valueOf(mapping.get("ID"));
								common.label = String.valueOf(mapping.get("Text"));
								common.subjectCategoryId = !Utils.isNullOrEmpty(mapping.get("subjectCategoryId")) ? mapping.get("subjectCategoryId").toString() : null;
								specializations.dto.add(common);
							}
						}
					}
				}
				@Override
				public void onError(Exception error) {
					specializations.success = false;
					specializations.dto = null;
					specializations.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(specializations);
	}

	@RequestMapping(value = "/getTitle", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getTitle() {
		ApiResult<List<LookupItemDTO>> titleList = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Utils.getDropdownData(titleList, context, CommonEmployeeQueries.SELECT_TITLE, null);
				}
				@Override
				public void onError(Exception error) {
					titleList.success = false;
					titleList.dto = null;
					titleList.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(titleList);
	}

	@RequestMapping(value = "/getLeaveType", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getLeaveTypeList() {
		ApiResult<List<LookupItemDTO>> leaveTypeList = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Utils.getDropdownData(leaveTypeList, context, CommonEmployeeQueries.SELECT_LEAVE_TYPE_LIST, null);
				}
				@Override
				public void onError(Exception error) {
					leaveTypeList.success = false;
					leaveTypeList.dto = null;
					leaveTypeList.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(leaveTypeList);	   	
	}

	@RequestMapping(value = "/getEmployeeJobCategory" ,  method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getemployeeJobCategory(@RequestParam("employeeCategoryID") String employeeCategoryId){
		ApiResult<List<EmployeeApplicationDTO>> result = new ApiResult<>();
		try {
			List<EmployeeApplicationDTO> jobCatgry = commonEmployeeHandler.getJobCategory(employeeCategoryId);
			if(!Utils.isNullOrEmpty(jobCatgry)) {
				result.success = true;
				result.dto = jobCatgry;
			}else {
				result.success = false;
			}
		}
		catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getCategory", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getCaterogysList() {
		ApiResult<List<LookupItemDTO>> categories = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Utils.getDropdownData(categories, context, CommonEmployeeQueries.SELECT_CATEGORY_LIST, null);
				}
				@Override
				public void onError(Exception error) {
					categories.success = false;
					categories.dto = null;
					categories.failureMessage = error.getMessage();
				}
			}, true);  
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(categories);
	}

	@RequestMapping(value = "/getEmployeeTitleList", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getEmployeeTitleList() {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			ApiResult<List<EmployeeApplicationDTO>> list = commonEmployeeHandler.getEmployeetitles();
			if(!Utils.isNullOrEmpty(list)) {
				emp.success = true;
				emp.dto = list.dto;
			}
			else {
				emp.success = false;
			}	         
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(emp);
	}

	@RequestMapping(value = "/getEmployeeList", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getEmployeeList() {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			ApiResult<List<EmployeeApplicationDTO>> list = commonEmployeeHandler.getEmployees();
			if(!Utils.isNullOrEmpty(list)) {
				emp.success = true;
				emp.dto = list.dto;
			}
			else {
				emp.success = false;
			}	         ;
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(emp);
	}

	@RequestMapping(value = "/getProcessType", method = RequestMethod.POST)
	public Mono<List<LookupItemDTO>> getProcessType() {
		List<LookupItemDTO> processTypes = new ArrayList<>();
		try {
			processTypes = commonEmployeeHandler.getProcessType();
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(processTypes);
	} 

	@RequestMapping(value="/getDocumentCheckList", method=RequestMethod.POST)
	public Mono<ApiResult<List<EmpDocumentChecklistMainDTO>>> getAllEmpDocumentCheckList() {
		ApiResult<List<EmpDocumentChecklistMainDTO>> result = new ApiResult<List<EmpDocumentChecklistMainDTO>>();
		try {
			List<EmpDocumentChecklistMainDTO> mappings = commonEmployeeHandler.getAllEmpDocumentCheckList();
			result.success = true;
			result.dto = new ArrayList<EmpDocumentChecklistMainDTO>();
			result.dto =	 mappings;
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}	

	@RequestMapping(value="/getApplicantDetails", method=RequestMethod.POST)
	public Mono<ApiResult<EmpApplnEntriesDTO>> applicantPersonalData(@RequestParam("applicantId") String applicantId) {
		ApiResult<EmpApplnEntriesDTO> result = new ApiResult<EmpApplnEntriesDTO>();
		try {
			EmpApplnEntriesDTO mappings = commonEmployeeHandler.applicantPersonalData(applicantId);
			result.success = true;
			result.dto = mappings;
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}


	@RequestMapping(value="/getDocumentCheckListForeign", method=RequestMethod.POST)
	public Mono<ApiResult<List<EmpDocumentChecklistMainDTO>>> getAllEmpDocumentCheckListwithForeign() {
		ApiResult<List<EmpDocumentChecklistMainDTO>> result = new ApiResult<List<EmpDocumentChecklistMainDTO>>();
		try {
			List<EmpDocumentChecklistMainDTO> mappings = commonEmployeeHandler.getAllEmpDocumentCheckListwithForeign();
			result.success = true;
			result.dto = mappings;
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getApplicationNumbers", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getApplicationNumbersList(@RequestParam("applicationid") String applicationId) {
		ApiResult<List<LookupItemDTO>> categories = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("applicationId", applicationId+"%");
					String str="select e.emp_appln_entries_id as ID , e.application_no as 'Text' from emp_appln_entries e where e.application_no like :applicationId  and e.record_status='A'";
					Utils.getDropdownData(categories, context, str.toString(), args );
				}
				@Override
				public void onError(Exception error) {
					categories.success = false;
					categories.dto = null;
					categories.failureMessage = error.getMessage();
				}
			}, true);  
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(categories);
	}

	@RequestMapping(value="/getApplicationNumberLength", method=RequestMethod.POST)
	public Mono<ApiResult<LookupItemDTO>> getApplicationNumberLength() {
		ApiResult<LookupItemDTO> categories = new ApiResult<LookupItemDTO>();
		try {
			Tuple mappings =commonEmployeeHandler.getApplicationNumberLength();
			if(mappings != null ) {
				categories.success = true;
				categories.dto = new LookupItemDTO();
				categories.dto.value=String.valueOf(mappings.get("ID"));
				categories.dto.label= String.valueOf(mappings.get("Text"));
			}
		}
		catch (Exception error) {
			categories.success = false;
			categories.dto = null;
			categories.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(categories);
	}

	@RequestMapping(value = "/getEmpEligibilityExamList", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getEmpEligibilityExamList() {
		ApiResult<List<LookupItemDTO>> empEligibilityExamList = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Utils.getDropdownData(empEligibilityExamList, context, CommonEmployeeQueries.GET_EMP_ELIGIBILITY_EXAM_LIST, null);
				}
				@Override
				public void onError(Exception error) {
					empEligibilityExamList.success = false;
					empEligibilityExamList.dto = null;
					empEligibilityExamList.failureMessage = error.getMessage();
				}
			}, true);
		} catch (Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(empEligibilityExamList);
	}

	@RequestMapping(value = "/getDesignation", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getDesignationList(@RequestParam("employeeCategoryID") String employeeCategoryId) {
		ApiResult<List<LookupItemDTO>> designations = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("employeeCategoryId", employeeCategoryId);
					Utils.getDropdownData(designations, context, CommonEmployeeQueries.SELECT_DESIGNATION_LIST, args);
				}
				@Override
				public void onError(Exception error) {
					designations.success = false;
					designations.dto = null;
					designations.failureMessage = error.getMessage();
				}
			}, true);  
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(designations);
	}

	@RequestMapping(value = "/getGradesAndRevisedYearsByEmployeeCategoryID", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpPayScaleGradeMappingDTO>>> getGradesAndRevisedYearsByEmployeeCategoryID(@RequestParam("employeeCategoryId") String categoryId) {  		  		
		ApiResult<List<EmpPayScaleGradeMappingDTO>> result = new ApiResult<List<EmpPayScaleGradeMappingDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_GRADES_AND_REVISED_YEAR_BY_CATAGORY_ID, Tuple.class);
					query.setParameter("employeeCategoryID", categoryId);              	   
					List<Tuple> mappings = query.getResultList();
					if(mappings != null && mappings.size() > 0) {
						result.success = true;
						result.dto = new ArrayList<>();
						for(Tuple mapping : mappings) {
							EmpPayScaleGradeMappingDTO dto = new EmpPayScaleGradeMappingDTO();
							ExModelBaseDTO grade = new ExModelBaseDTO();
							grade.id = String.valueOf(mapping.get("ID"));
							grade.text = String.valueOf(mapping.get("Text"));
							dto.grade = grade;
							ExModelBaseDTO revisedYear = new ExModelBaseDTO();
							revisedYear.id = String.valueOf(mapping.get("ID"));
							revisedYear.text = String.valueOf(mapping.get("RevisedYear"));
							dto.revisedYear = revisedYear;
							result.dto.add(dto);
						}                    	                     	   
					}
				}
				@Override
				public void onError(Exception error) {
					result.success = false;
					result.dto = null;
					result.failureMessage = error.getMessage();
				}
			}, true);
		} catch (Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getLevelsAndScaleByGradeIdAndRevisedYear", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpPayScaleGradeMappingDetailDTO>>> getLevelsAndScaleByGradeIdAndRevisedYear(@RequestParam("gradeID") String gradeId, @RequestParam("revisedYear") String revisedYear) {
		ApiResult<List<EmpPayScaleGradeMappingDetailDTO>> levels = new ApiResult<List<EmpPayScaleGradeMappingDetailDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {          	   
					Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_LEVEL_BY_GRADE_ID_AND_REVISED_YEAR, Tuple.class);
					query.setParameter("GradeID", gradeId);       
					query.setParameter("RevisedYear", revisedYear);       
					List<Tuple> mappings = query.getResultList();
					if(mappings != null && mappings.size() > 0) {
						levels.success = true;
						levels.dto = new ArrayList<>();
						for(Tuple mapping : mappings) {
							EmpPayScaleGradeMappingDetailDTO dto = new EmpPayScaleGradeMappingDetailDTO();
							dto.id = String.valueOf(mapping.get("ID"));
							dto.payScaleLevel = String.valueOf(mapping.get("ScaleLevel"));
//							dto.payScaleLevel = mapping.get("Text").toString(); // sambath
							dto.payScale = String.valueOf(mapping.get("PayScale"));                  	  
							levels.dto.add(dto);
						}                    	                     	   
					}
				}
				@Override
				public void onError(Exception error) {
					levels.success = false;
					levels.dto = null;
					levels.failureMessage = error.getMessage();
				}
			}, true);
		} catch (Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(levels);
	}

	@RequestMapping(value = "/getPayScaleMatrixDetailByPayScaleGradeMappingDetailId", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpPayScaleMatrixDetailDTO>>> getCellsAndPayScaleByLevelId(@RequestParam("levelId") String levelId) {
		ApiResult<List<EmpPayScaleMatrixDetailDTO>> cells = new ApiResult<List<EmpPayScaleMatrixDetailDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_CELLS_AND_PAY_SCALE_BY_LEVEL_ID, Tuple.class);
					query.setParameter("LevelId", levelId);              	   
					List<Tuple> mappings = query.getResultList();
					if(mappings != null && mappings.size() > 0) {
						cells.success = true;
						cells.dto = new ArrayList<>();
						for(Tuple mapping : mappings) {
							EmpPayScaleMatrixDetailDTO common = new EmpPayScaleMatrixDetailDTO();
							common.id = (Integer) mapping.get("ID");
							common.levelCellNo = (Integer) mapping.get("Text");
							common.levelCellValue = String.valueOf(mapping.get("CellValue"));   
							cells.dto.add(common);
						}
					}
				}
				@Override
				public void onError(Exception error) {
					cells.success = false;
					cells.dto = null;
					cells.failureMessage = error.getMessage();
				}
			}, true);
		} catch (Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(cells);
	}
	@RequestMapping(value = "/getEmployeeGroupByEmployeeCategoryID", method = RequestMethod.POST)
	public  Mono<ApiResult<List<LookupItemDTO>>> getEmployeeGroupList(@RequestParam("employeeCategoryID") String employeeCategoryID) {
		ApiResult<List<LookupItemDTO>> employeeGroup = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("employeeCategoryID", employeeCategoryID);
					Utils.getDropdownData(employeeGroup, context, CommonEmployeeQueries.SELECT_EMPLOYEE_GROUP_LIST, args);
				}
				@Override
				public void onError(Exception error) {
					employeeGroup.success = false;
					employeeGroup.dto = null;
					employeeGroup.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(employeeGroup);
	}

	@RequestMapping(value = "/getDesignationByEmployeeCategoryID", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getDepartmentsByEmployeeCategoryID(@RequestParam("employeeCategoryId") String categoryId) {
		ApiResult<List<LookupItemDTO>> designation = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("EmployeeCategoryID", categoryId);
					Utils.getDropdownData(designation, context, CommonEmployeeQueries.SELECT_DESIGNATION_BY_CATAGORY_ID, args);
				}
				@Override
				public void onError(Exception error) {
					designation.success = false;
					designation.dto = null;
					designation.failureMessage = error.getMessage();
				}
			}, true);
		} catch (Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(designation);
	}


	@RequestMapping(value = "/getType", method = RequestMethod.POST)
	public Mono<List<LookupItemDTO>> getType() {
		List<LookupItemDTO> types = new ArrayList<>();
		try {
			types = commonEmployeeHandler.getType();
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(types);
	} 

	@RequestMapping(value = "/getCampusDeaneryDepartmentByLocId", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpCampusDeaneryDepartmentDTO>>> getCampusDeaneryDepartmentByLocId() {
		ApiResult< List<EmpCampusDeaneryDepartmentDTO>> campusDeaneryDepartment = new ApiResult<List<EmpCampusDeaneryDepartmentDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				public void onRun(EntityManager context) {
					List<ErpCampusDepartmentMappingDBO> dboList = null;				
					Query query = context.createQuery(CommonEmployeeQueries.SELECT_CAMPUS_BY_LOC);
					dboList = (List<ErpCampusDepartmentMappingDBO>) query.getResultList();	
					campusDeaneryDepartment.dto = new ArrayList <EmpCampusDeaneryDepartmentDTO> ();
					if(dboList!=null && dboList.size()>0) {	
						Set<Integer> campusIds = new HashSet<Integer>();
						for (ErpCampusDepartmentMappingDBO dbo : dboList) {
							EmpCampusDeaneryDepartmentDTO dto = new EmpCampusDeaneryDepartmentDTO(); 
							if(!Utils.isNullOrEmpty(dbo.erpCampusDBO) && !Utils.isNullOrEmpty(dbo.erpCampusDBO.id) && !Utils.isNullOrEmpty(dbo.erpCampusDBO.campusName)) {
								dto.value =dbo.id+"-"+String.valueOf(dbo.erpCampusDBO.id);		
								dto.label = dbo.erpCampusDBO.campusName; 								     
								if (!campusIds.contains(dbo.erpCampusDBO.id)) {
									campusIds.add(dbo.erpCampusDBO.id);
									dto.children = new ArrayList <EmpCampusDeaneryDTO>();	
									Set<Integer> deaneryIds = new HashSet<Integer>();
									for (ErpCampusDepartmentMappingDBO dbo2 : dboList) {
										if( !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) &&  !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO)  &&
												!deaneryIds.contains(dbo2.erpDepartmentDBO.erpDeaneryDBO.id)){
											EmpCampusDeaneryDTO deanery = new EmpCampusDeaneryDTO();	
											if (!Utils.isNullOrEmpty(dbo.erpCampusDBO) && !Utils.isNullOrEmpty(dbo2.erpCampusDBO)   
													&& dbo.erpCampusDBO.id == dbo2.erpCampusDBO.id && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO)
													&& !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO.id) && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO.deaneryName)) {
												deanery.value = dbo2.id+"-"+dbo.erpCampusDBO.id+"-"+dbo2.erpDepartmentDBO.erpDeaneryDBO.id;						 
												deanery.label = dbo2.erpDepartmentDBO.erpDeaneryDBO.deaneryName;
												dto.children.add(deanery);
												deaneryIds.add(dbo2.erpDepartmentDBO.erpDeaneryDBO.id);
												deanery.children = new ArrayList<EmpDeaneryDepartmentDTO>();											  
												for (ErpCampusDepartmentMappingDBO dbo3 : dboList) {								        	  
													EmpDeaneryDepartmentDTO department = new EmpDeaneryDepartmentDTO();
													if (!Utils.isNullOrEmpty(dbo2.erpCampusDBO) && !Utils.isNullOrEmpty(dbo3.erpCampusDBO)  && dbo2.erpCampusDBO.id == dbo3.erpCampusDBO.id 
															&& !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO) 
															&& !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO.erpDeaneryDBO) && dbo2.erpDepartmentDBO.erpDeaneryDBO.id == dbo3.erpDepartmentDBO.erpDeaneryDBO.id
															&& !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO) && !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO.id) && !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO.departmentName)){
														department.value = dbo3.id+"-"+dbo.erpCampusDBO.id+"-"+dbo2.erpDepartmentDBO.erpDeaneryDBO.id+"-"+dbo3.erpDepartmentDBO.id;
														department.label = dbo3.erpDepartmentDBO.departmentName;
														deanery.children.add(department);
													}
												}
											}	
										}
									}								 							 
									campusDeaneryDepartment.dto.add(dto);
								}
							} 				     							     
						}							   
					}					
					campusDeaneryDepartment.success = true;
				}
				@Override
				public void onError(Exception error) {
					campusDeaneryDepartment.success = false;
					campusDeaneryDepartment.dto = null;
					campusDeaneryDepartment.failureMessage = error.getMessage();
				}
			});
			return Utils.monoFromObject(campusDeaneryDepartment);
		}catch (Exception e) {}
		return Utils.monoFromObject(campusDeaneryDepartment);
	}

	@RequestMapping(value = "/getCampusDeaneryDepartmentByLocationId", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpCampusDeaneryDepartmentDTO>>> getCampusDeaneryDepartmentByLocationId(
			@RequestParam("locId") String locId
			) {
		ApiResult< List<EmpCampusDeaneryDepartmentDTO>> campusDeaneryDepartment = new ApiResult<List<EmpCampusDeaneryDepartmentDTO>>();
		try {
			ApiResult< List<EmpCampusDeaneryDepartmentDTO>> list  = commonEmployeeHandler.getCampusDeaneryDeptByLocId(locId);
			if(!Utils.isNullOrEmpty(list)) {
				campusDeaneryDepartment.success = true;
				campusDeaneryDepartment.dto = list.dto;
			}
			else {
				campusDeaneryDepartment.success = false;
			}	         

		} 
		catch (Exception e) {			
		}
		return Utils.monoFromObject(campusDeaneryDepartment);
	}   


	@RequestMapping(value = "/getEmployeeByIds", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getEmployeeByIds(@RequestBody HolidaysAndEventsEntryDTO data) {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Set <Integer> campusDeanearyDeptIds = new HashSet<>();
					if (!Utils.isNullOrEmpty(data.checked)) {
						for (String ids : data.checked) {
							if (ids!=null && !ids.isEmpty()) {
								StringBuffer id = new StringBuffer();
								for (int i=0;i<ids.length();i++){
									char ch = ids.charAt(i);
									if(ch=='-') {
										break;
									}
									id.append(ch);
								}
								if (!Utils.isNullOrEmpty(id)) {
									campusDeanearyDeptIds.add(Integer.valueOf(id.toString().trim()));
								}
							}
						}
					}
					Query query=null;

					if(campusDeanearyDeptIds.isEmpty() && data.location.id.isEmpty()) {
						query = context.createNativeQuery(CommonEmployeeQueries.SELECT_EMPLOYEE_LIST, Tuple.class);
					}
					else {
						query = context.createNativeQuery(CommonEmployeeQueries.GET_EMPLOYEE_LIST_BY_DEAN, Tuple.class);
						if(campusDeanearyDeptIds!=null && !campusDeanearyDeptIds.isEmpty()) {
							query.setParameter("ids",campusDeanearyDeptIds);
						}
						else {
							query.setParameter("ids",0);
						}
						if(data.location.id!=null && !data.location.id.isEmpty()) {
							query.setParameter("locIds",data.location.id);
						}
						else {
							query.setParameter("locIds",0);
						}
					}


					List<Tuple> mappings = query.getResultList();
					if(mappings != null && mappings.size() > 0) {
						emp.success = true;
						emp.dto = new ArrayList<>();
						for(Tuple mapping : mappings) {
							EmployeeApplicationDTO common = new EmployeeApplicationDTO();
							common.value = String.valueOf(mapping.get("ID"));
							common.label = String.valueOf(mapping.get("Text"));
							emp.dto.add(common);
						}
					}
				}
				@Override
				public void onError(Exception error) {
					emp.success = false;
					emp.dto = null;
					emp.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(emp);
	}

	@RequestMapping(value = "/getEmployeeByIdsByEmpCategory", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getEmployeeByIdsByEmpCategory(@RequestBody HolidaysAndEventsEntryDTO data) {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			ApiResult<List<EmployeeApplicationDTO>> list = commonEmployeeHandler.getEmployeeByIdsByEmpCategory(data);
			if(!Utils.isNullOrEmpty(list)) {
				emp.success = true;
				emp.dto = list.dto;
			}
			else {
				emp.success = false;
			}		     
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(emp);
	}
	@RequestMapping(value = "/getEmployeeNameandIDList", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpDTO>>> getEmployeeNameandIDList() {
		ApiResult<List<EmpDTO>> emp = new ApiResult<List<EmpDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_EMPLOYEE_LIST1, Tuple.class);
					List<Tuple> mappings = query.getResultList();
					if(mappings != null && mappings.size() > 0) {
						emp.success = true;
						emp.dto = new ArrayList<>();
						for(Tuple mapping : mappings) {
							EmpDTO common = new EmpDTO();
							common.value = String.valueOf(mapping.get("ID"));
							common.label = String.valueOf(mapping.get("Text"));
							common.campusName=String.valueOf(mapping.get("campus"));
							common.departmentName=String.valueOf(mapping.get("department"));
							emp.dto.add(common);
						}
						for(Tuple mapping : mappings) {
							EmpDTO common = new EmpDTO();
							common.value = String.valueOf(mapping.get("Text"));
							common.label = String.valueOf(mapping.get("ID"));
							common.campusName=String.valueOf(mapping.get("campus"));
							common.departmentName=String.valueOf(mapping.get("department"));
							emp.dto.add(common);
						}
					}
				}
				@Override
				public void onError(Exception error) {
					emp.success = false;
					emp.dto = null;
					emp.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(emp);
	}
	@RequestMapping(value = "/getLeaveType1", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getLeaveTypeList1() {
		ApiResult<List<LookupItemDTO>> leaveTypeList = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Utils.getDropdownData(leaveTypeList, context, CommonEmployeeQueries.SELECT_LEAVE_TYPE_LIST1, null);
				}
				@Override
				public void onError(Exception error) {
					leaveTypeList.success = false;
					leaveTypeList.dto = null;
					leaveTypeList.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(leaveTypeList);	   	
	}
	@RequestMapping(value = "/getEmpPositionRoleSub", method = RequestMethod.POST)
	public Mono<ApiResult<EmpPositionRoleDTO>> getEmpPositionRoleSub(@RequestParam("employeeCampusID") String campusId) {
		ApiResult<EmpPositionRoleDTO> result = new ApiResult<EmpPositionRoleDTO>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					EmpPositionRoleDBO pos = null;
					Query query = context.createQuery(SettingsQueries.PROCESS_AND_POSITION_MAPPING_DUPLICATE_CHECK);
					query.setParameter("campusId", Integer.parseInt(campusId));
					query.setParameter("processType", "Employee final interview comments");
					try {
						pos = (EmpPositionRoleDBO) Utils.getUniqueResult(query.getResultList());
					} catch (Exception e) {
						result.success = false;
						result.dto = null;
						result.failureMessage = e.getMessage();
					}
					if(pos != null) {
						result.success = true;
						result.dto = new EmpPositionRoleDTO();
						result.success = true;
						result.dto = new EmpPositionRoleDTO();
						result.dto.id = String.valueOf(pos.id);
						result.dto.processType = String.valueOf(pos.processType);
						result.dto.campus = new ExModelBaseDTO();
						result.dto.campus.id = String.valueOf(pos.erpCampusId.id);
						result.dto.Levels = new ArrayList<>();
						if(pos.empPositionSubAssignmentDBOSet != null && pos.empPositionSubAssignmentDBOSet.size() > 0) {
							for(EmpPositionRoleSubDBO item : pos.empPositionSubAssignmentDBOSet) {
								EmpPositionRoleSubDTO levelInfo = new EmpPositionRoleSubDTO();
								if(item.recordStatus == 'A') {
									levelInfo.id = String.valueOf(item.id);
									levelInfo.empTitle = new ExModelBaseDTO();
									levelInfo.empTitle.id = String.valueOf(item.empTitleId.id);
									levelInfo.empTitle.text = String.valueOf(item.empTitleId.titleName);
									levelInfo.employee = new ExModelBaseDTO();
									levelInfo.employee.id = String.valueOf(item.empDBO.id);
									levelInfo.employee.text = String.valueOf(item.empDBO.empName);
									levelInfo.order = String.valueOf(item.displayOrder);
									result.dto.Levels.add(levelInfo);
								}
							}
							Collections.sort(result.dto.Levels, new Comparator<EmpPositionRoleSubDTO>() {
								@Override
								public int compare(EmpPositionRoleSubDTO o1, EmpPositionRoleSubDTO o2) {
									return Integer.compare(Integer.parseInt(o1.order), Integer.parseInt(o2.order));
								}
							});
						}
					}
				}
				@Override
				public void onError(Exception error) {
					result.success = false;
					result.dto = null;
					result.failureMessage = error.getMessage();
				}
			}, true);  
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getApplicantNames", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getApplicantNamesLIst(@RequestParam("applicantName") String applicantName) {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_APPLICANT_NAME,Tuple.class);
					query.setParameter("applicantName", applicantName+"%");				
					List<Tuple> mappings = query.getResultList();
					result.dto = new ArrayList<>();
					var valueList = new ArrayList<LookupItemDTO>();
					mappings.forEach(s->{
						LookupItemDTO itemInfo = new LookupItemDTO();
						if (!Utils.isNullOrEmpty(s.get("ID")) && !Utils.isNullOrEmpty(s.get("Text")) && !Utils.isNullOrEmpty(s.get("application_no"))) {
							itemInfo.setValue(s.get("ID").toString());
							itemInfo.setLabel(s.get("Text").toString() +" (" + s.get("application_no").toString() +")" );
							result.dto.add(itemInfo);
						}			
					});
					
/* It is commented because this api is using by 4 or more screens but appointment approval need list of names with concat application number. */		
					
//					if(mappings != null && mappings.size() > 0) {
//						result.success = true;
//						result.dto = new ArrayList<>();
//						List<String> abc = new ArrayList<String>();
//						List<String> duplicate = new ArrayList<String>();
//						for(Tuple mapping : mappings) {
//							String label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
//							abc.add(label); 
//						}
//						java.util.List<String> duplicates = abc;
//						@SuppressWarnings("rawtypes")
//						java.util.HashSet unique=new HashSet();
//						for (String s:duplicates){
//							if(!unique.add(s)){   
//								duplicate.add(s);
//							}
//						}
//						for(Tuple mapping : mappings) {
//							LookupItemDTO itemInfo = new LookupItemDTO();
//							String label = "";
//							String applicationNum = "";
//							itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
//							label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
//							applicationNum = !Utils.isNullOrEmpty(mapping.get("application_no")) ? mapping.get("application_no").toString() : "";
//							if(duplicate.contains(label)) {
//								label = label + " ("+applicationNum + ")";
//							}
//							itemInfo.label = label;
//
//							result.dto.add(itemInfo);
//						}
//					}
				}
				@Override
				public void onError(Exception error) {
					result.success = false;
					result.dto = null;
					result.failureMessage = error.getMessage();
				}
			}, true);  
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(result);
	}
	@RequestMapping( value = "/getApplicationNumber", method = RequestMethod.POST)
	public Mono<ApiResult<EmpApplnEntriesDTO>> getApplicationNumber(@RequestParam("applicantName") String applicantName){
		ApiResult<EmpApplnEntriesDTO> result = new ApiResult<EmpApplnEntriesDTO>();
		try {
			if(applicantName != null) {
				DBGateway.runJPA(new ITransactional() {
					@Override
					public void onRun(EntityManager context) {
						String num = "";
						if(applicantName.contains(")")) {
							String s = applicantName;
							s = s.replace("(", "-");
							s = s.replace(")", "");
							String[] names  = s.split("-");
							num = names[1].replace(")", "");
						}
						Query query= null; 
						if(num.isEmpty()) {
							query = context.createNativeQuery(CommonEmployeeQueries.SELECT_APPLICANT_DETAILS_BY_NAME, Tuple.class);
							query.setParameter("applicant_name", applicantName);
						}else {
							query = context.createNativeQuery(CommonEmployeeQueries.SELECT_APPLICANT_DETAILS, Tuple.class);
							query.setParameter("applicantNumber", Integer.parseInt(num));
						}
						Tuple mappings = null;
						try {
							mappings = (Tuple) Utils.getUniqueResult(query.getResultList());
						} catch (Exception e1) {
							result.failureMessage = "Error";
							result.success=false;
							result.dto = null;
						}
						if(mappings != null) {
							result.success = true;
							EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
							if(!num.isEmpty()){
								empApplnEntriesDTO.applicantNumber = num;	
							}else {
								try {
									empApplnEntriesDTO.applicantNumber = String.valueOf(mappings.get("application_no"));
								} catch (Exception e) {
								}
							}
							empApplnEntriesDTO.applicantId= String.valueOf(mappings.get("ApplicantId"));
							empApplnEntriesDTO.applicantName= String.valueOf(mappings.get("ApplicantName"));
							if(!Utils.isNullOrEmpty(mappings.get("Campus")))
								empApplnEntriesDTO.applicantCampus = String.valueOf(mappings.get("Campus"));
							empApplnEntriesDTO.applicantAppliedFor = String.valueOf(mappings.get("PostApplied"));
							empApplnEntriesDTO.applicantCountry= String.valueOf(mappings.get("Country"));
							empApplnEntriesDTO.category = new ExModelBaseDTO();
							empApplnEntriesDTO.category.id = String.valueOf(mappings.get("CategoryId"));
							empApplnEntriesDTO.category.text = String.valueOf(mappings.get("PostApplied"));
							result.dto=empApplnEntriesDTO;
						}  
					}
					@Override
					public void onError(Exception ex) {
						result.success = false;
						result.dto = null;
						result.failureMessage = ex.getMessage();
					}
				});
			}
		} catch (Exception e) {	
			result.failureMessage = "Error";;
			result.success=false;
			result.dto = null;
		}
		return Utils.monoFromObject(result);
	}
	@RequestMapping(value = "/getPayScaleType", method = RequestMethod.POST)
	public Mono<List<LookupItemDTO>> getPayScaleType() {
		List<LookupItemDTO> payScaleType = new ArrayList<>();
		try {
			payScaleType.add(new LookupItemDTO("SCALE PAY","SCALE PAY"));
			payScaleType.add(new LookupItemDTO("CONSOLIDATED","CONSOLIDATED"));
			payScaleType.add(new LookupItemDTO("DAILY","DAILY"));
			payScaleType.add(new LookupItemDTO("PER HOUR","PER HOUR"));
			payScaleType.add(new LookupItemDTO("PER COURSE","PER COURSE"));
			payScaleType.add(new LookupItemDTO("NO PAY","NO PAY"));
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(payScaleType);
	} 

	@RequestMapping(value = "/getLeaveCategory", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getLeaveCategory() {
		ApiResult<List<LookupItemDTO>> leaveCategory = new ApiResult<List<LookupItemDTO>>();
		try {
			leaveCategory = commonEmployeeHandler.getLeavecategory();
			if (!Utils.isNullOrEmpty(leaveCategory)) {
				leaveCategory.success = true;
			} else {
				leaveCategory.success = false;
			}
		} catch (Exception error) {
			leaveCategory.success = false;
			leaveCategory.dto = null;
			leaveCategory.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(leaveCategory);
	}

	@RequestMapping(value = "/getTimeZone", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getTimeZone() {
		ApiResult<List<LookupItemDTO>> timeZoneList = new ApiResult<List<LookupItemDTO>>();
		try {
			timeZoneList = commonEmployeeHandler.getTimeZone();
			if (!Utils.isNullOrEmpty(timeZoneList)) {
				timeZoneList.success = true;
			} else {
				timeZoneList.success = false;
			}
		} catch (Exception error) {
			timeZoneList.success = false;
			timeZoneList.dto = null;
			timeZoneList.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(timeZoneList);
	}

	@RequestMapping(value = "/getCellValueByCategoryId", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpPayScaleMatrixDetailDTO>>> getCellValueByCategoryId(@RequestParam("employeeCategoryID") String employeeCategoryID) {
		ApiResult<List<EmpPayScaleMatrixDetailDTO>> result = new ApiResult<List<EmpPayScaleMatrixDetailDTO>>();
		try {
			List<EmpPayScaleMatrixDetailDTO>  cellValues = commonEmployeeHandler.getCellValueByCategoryId(employeeCategoryID);
			if(!Utils.isNullOrEmpty(cellValues)) {
				result.success = true;
				result.dto = cellValues;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getDailyWagePaymentAndBasic", method = RequestMethod.POST)
	public Mono<ApiResult<EmpDailyWageSlabDTO>> getDailyWagePaymentAndBasic(@RequestParam("employeeCategoryID") String employeeCategoryId,@RequestParam("employeeJobCategoryID") String employeeJobCategoryId) {
		ApiResult<EmpDailyWageSlabDTO> result = new ApiResult<>();
		try {
			EmpDailyWageSlabDTO DlyWgData = commonEmployeeHandler.getDlyWgData(employeeCategoryId,employeeJobCategoryId);
			if(!Utils.isNullOrEmpty(DlyWgData)) {
				result.success = true;
				result.dto = DlyWgData;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getEmpPayScaleComponents", method = RequestMethod.POST)
	public Mono<ApiResult<List<SalaryComponentDTO>>> getEmpPayScaleComponents() {
		ApiResult<List<SalaryComponentDTO>> result = new ApiResult<List<SalaryComponentDTO>>();
		try {
			List<SalaryComponentDTO>  salaryComponentDTOs = commonEmployeeHandler.getEmpPayScaleComponents();
			if(!Utils.isNullOrEmpty(salaryComponentDTOs)) {
				result.success = true;
				result.dto = salaryComponentDTOs;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getAppliedSubject", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getAppliedSubject(@RequestParam("applicationNumber") String applicationNumber) {
		ApiResult<List<LookupItemDTO>> subjectPref = new ApiResult<List<LookupItemDTO>>();
		try {
			subjectPref = commonEmployeeHandler.getAppliedSubject(applicationNumber);
			if (!Utils.isNullOrEmpty(subjectPref)) {
				subjectPref.success = true;
			} else {
				subjectPref.success = false;
			}
		} catch (Exception error) {
			subjectPref.success = false;
			subjectPref.dto = null;
			subjectPref.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(subjectPref);
	}

	@RequestMapping(value = "/getAppliedSubjectSpecialization", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getAppliedSubjectSpecialization(@RequestParam("applicationNumber") String applicationNumber) {
		ApiResult<List<LookupItemDTO>> subjectSpecializationPref = new ApiResult<List<LookupItemDTO>>();
		try {
			subjectSpecializationPref = commonEmployeeHandler.getAppliedSubjectSpecialization(applicationNumber);
			if (!Utils.isNullOrEmpty(subjectSpecializationPref)) {
				subjectSpecializationPref.success = true;
			} else {
				subjectSpecializationPref.success = false;
			}
		} catch (Exception error) {
			subjectSpecializationPref.success = false;
			subjectSpecializationPref.dto = null;
			subjectSpecializationPref.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(subjectSpecializationPref);
	}

	@RequestMapping(value = "/getSubjectCategorySpecialization", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getSubjectCategorySpecialization(@RequestParam("subjectCategoryId") String subjectCategory)  {
		ApiResult<List<LookupItemDTO>> subjectCategorySpecialization = new ApiResult<List<LookupItemDTO>>();
		try {
			subjectCategorySpecialization = commonEmployeeHandler.getSubjectCategorySpecialization(subjectCategory);
			if (!Utils.isNullOrEmpty(subjectCategorySpecialization)) {
				subjectCategorySpecialization.success = true;
			} else {
				subjectCategorySpecialization.success = false;
			}
		} catch (Exception error) {
			subjectCategorySpecialization.success = false;
			subjectCategorySpecialization.dto = null;
			subjectCategorySpecialization.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(subjectCategorySpecialization);
	}

	@RequestMapping(value = "/getLeaveTypeForLeaveApplication", method = RequestMethod.POST)
	public Flux<SelectDTO> getLeaveTypeForLeaveApplication(@RequestParam Map<String, String> requestParams) {
		return commonEmployeeHandler.getLeaveTypeForLeaveApplication(requestParams).switchIfEmpty(Mono.error(new NotFoundException(null)));	   	
	}

	@RequestMapping(value = "/getTemplateList", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLetterRequestDTO>>> getTemplateList() {
		ApiResult<List<EmpLetterRequestDTO>> emp = new ApiResult<List<EmpLetterRequestDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Query query = context.createNativeQuery(CommonEmployeeQueries.SELECT_TEMPLATE_LIST, Tuple.class);
					List<Tuple> mappings = query.getResultList();
					if(mappings != null && mappings.size() > 0) {
						emp.success = true;
						emp.dto = new ArrayList<>();
						for(Tuple mapping : mappings) {
							EmpLetterRequestDTO common = new EmpLetterRequestDTO();
							common.value = String.valueOf(mapping.get("ID"));
							common.label = String.valueOf(mapping.get("Text"));
							emp.dto.add(common);
						}
					}
				}
				@Override
				public void onError(Exception error) {
					emp.success = false;
					emp.dto = null;
					emp.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(emp);
	}

	@RequestMapping(value = "/getCampusDeaneryDepartment", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpCampusDeaneryDepartmentDTO>>> getCampusDeaneryDepartment() {	
		ApiResult< List<EmpCampusDeaneryDepartmentDTO>> result = new ApiResult<List<EmpCampusDeaneryDepartmentDTO>>();
		try {
			List<EmpCampusDeaneryDepartmentDTO>  dto = commonEmployeeHandler.getEmpCampusDeaneryDepartment();
			if(!Utils.isNullOrEmpty(dto)) {
				result.success = true;
				result.dto = dto;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);

	}

	@RequestMapping (value = "/getCampusLevelProgramme", method = RequestMethod.POST)
	public Mono<ApiResult<List<HostelProgrammeDetailsDTO>>> getCampusLevelProgramme(){
		ApiResult<List<HostelProgrammeDetailsDTO>> result = new ApiResult<>();
		try {
			List<HostelProgrammeDetailsDTO> dto = commonEmployeeHandler.getCampusLevelProgramme();
			if(!Utils.isNullOrEmpty(dto)){
				result.success = true;
				result.dto = dto;
			}else{
				result.success =false;
			}
		}catch (Exception error){
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getEmployeeWorkShifts", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpShiftTypesDTO>>> getEmployeeWorkShifts(@RequestParam("campusId") String campusId)  {
		ApiResult<List<EmpShiftTypesDTO>> employeeWorkShifts = new ApiResult<List<EmpShiftTypesDTO>>();
		try {
			List<EmpShiftTypesDTO> employeeWorkShiftsList = commonEmployeeHandler.getEmployeeWorkShifts(campusId);
			if(!Utils.isNullOrEmpty(employeeWorkShifts)) {
				employeeWorkShifts.success = true;
				employeeWorkShifts.dto = employeeWorkShiftsList;
			}else {
				employeeWorkShifts.success = false;
			}
		} catch (Exception error) {
			employeeWorkShifts.success = false;
			employeeWorkShifts.dto = null;
			employeeWorkShifts.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(employeeWorkShifts);
	}

	@RequestMapping(value = "/getDepartmentCampus", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpCampusDeaneryDepartmentDTO>>> getDepartmentCampus() {	
		ApiResult< List<EmpCampusDeaneryDepartmentDTO>> result = new ApiResult<List<EmpCampusDeaneryDepartmentDTO>>();
		try {
			List<EmpCampusDeaneryDepartmentDTO>  dto = commonEmployeeHandler.getDepartmentCampus();
			if(!Utils.isNullOrEmpty(dto)) {
				result.success = true;
				result.dto = dto;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);

	}	

	@RequestMapping(value = "/getEmployeeLetterRequestType", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getEmployeeLetterRequestType() {
		ApiResult<List<EmployeeApplicationDTO>> letterType = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			ApiResult<List<EmployeeApplicationDTO>> list = commonEmployeeHandler.getEmployeeLetterRequestType();
			if(!Utils.isNullOrEmpty(list)) {
				letterType.success = true;
				letterType.dto = list.dto;
			}
			else {
				letterType.success = false;
			}	         
		}
		catch(Exception error) {
		}
		return Utils.monoFromObject(letterType);
	}

	@RequestMapping(value = "/getEmployeeLetterRequestReasons", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getEmployeeLetterRequestReasons() {
		ApiResult<List<EmployeeApplicationDTO>> letterType = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			ApiResult<List<EmployeeApplicationDTO>> list = commonEmployeeHandler.getEmployeeLetterRequestReasons();
			if(!Utils.isNullOrEmpty(list)) {
				letterType.success = true;
				letterType.dto = list.dto;
			}
			else {
				letterType.success = false;
			}	         
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(letterType);
	}


	@RequestMapping(value = "/getDepartmentOnCampus" ,  method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getDepartmentOnCampus(@RequestParam("campusId") String campusId){
		ApiResult<List<EmployeeApplicationDTO>> result = new ApiResult<>();
		try {
			List<EmployeeApplicationDTO> deptOnCampus = commonEmployeeHandler.getDepartmentOnCampus(campusId);
			if(!Utils.isNullOrEmpty(deptOnCampus)) {
				result.success = true;
				result.dto = deptOnCampus;
			}else {
				result.success = false;
			}
		}
		catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getApplnNonAvailability" ,  method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpApplnNonAvailabilityDTO>>> getApplnNonAvailability(@RequestParam("interviewRound") String interviewRound){
		ApiResult<List<EmpApplnNonAvailabilityDTO>> result = new ApiResult<>();
		try {
			if(!Utils.isNullOrEmpty(interviewRound)){
				List<EmpApplnNonAvailabilityDTO> nonAvailabilityDTOS = commonEmployeeHandler.getApplnNonAvailability(interviewRound);
				if(!Utils.isNullOrEmpty(nonAvailabilityDTOS)) {
					result.success = true;
					result.dto = nonAvailabilityDTOS;
				}else {
					result.success = false;
				}
			}
		}catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getApplnInterviewScheduleDetails" ,  method = RequestMethod.POST)
	public Mono<ApiResult<EmpApplnInterviewSchedulesDTO>> getApplnInterviewScheduleDetails(@RequestParam("empApplnEntriesId") Integer empApplnEntriesId
			, @RequestParam("interviewRound") String interviewRound){
		ApiResult<EmpApplnInterviewSchedulesDTO> result = new ApiResult<>();
		try {
			if(!Utils.isNullOrEmpty(empApplnEntriesId) && !Utils.isNullOrEmpty(interviewRound)){
				EmpApplnInterviewSchedulesDTO empApplnInterviewSchedulesDTOS = commonEmployeeHandler.getApplnInterviewScheduleDetails(empApplnEntriesId,interviewRound);
				if(!Utils.isNullOrEmpty(empApplnInterviewSchedulesDTOS)) {
					result.success = true;
					result.dto = empApplnInterviewSchedulesDTOS;
				}else {
					result.success = false;
				}
			}
		}catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getScorecard", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getScorecard() {
		ApiResult<List<LookupItemDTO>> scorecardList = new ApiResult<List<LookupItemDTO>>();
		try {
			scorecardList = commonEmployeeHandler.getScorecard();
			if (!Utils.isNullOrEmpty(scorecardList)) {
				scorecardList.success = true;
			} else {
				scorecardList.success = false;
			}
		} catch (Exception error) {
			scorecardList.success = false;
			scorecardList.dto = null;
			scorecardList.failureMessage = error.getMessage();
		}
		scorecardList.dto.sort(Comparator.comparing((LookupItemDTO dto) -> dto.getLabel().toLowerCase()));
		return Utils.monoFromObject(scorecardList);
	}

	@RequestMapping(value = "/getMode", method = RequestMethod.POST)
	public Mono<List<LookupItemDTO>> getMode() {
		List<LookupItemDTO> types = new ArrayList<>();
		try {
			types = commonEmployeeHandler.getMode();
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(types);
	}

	@RequestMapping(value = "/getEmployeeAppraisalElementOption", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getEmployeeAppraisalElementOption() {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			List<LookupItemDTO> appraisalElementOptions = commonEmployeeHandler.getEmployeeAppraisalElementOption();
			if(!Utils.isNullOrEmpty(appraisalElementOptions)){
				result.dto = appraisalElementOptions;
				result.success = true;
			}
		}catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getEmployeeJobCategories" ,  method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getEmployeeJobCategories(){
		ApiResult<List<LookupItemDTO>> result = new ApiResult<>();
		try {
			List<LookupItemDTO> lookupItemDTO = commonEmployeeHandler.getJobCategories();
			if(!Utils.isNullOrEmpty(lookupItemDTO)) {
				result.success = true;
				result.dto = lookupItemDTO;
			}else {
				result.success = false;
			}
		}
		catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getPaymentTypes", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getPaymentTypes() {
		ApiResult<List<LookupItemDTO>> paymentTypes = new ApiResult<List<LookupItemDTO>>();
		try {
			paymentTypes = commonEmployeeHandler.getPaymentTypes();
			if (!Utils.isNullOrEmpty(paymentTypes)) {
				paymentTypes.success = true;
			} else {
				paymentTypes.success = false;
			}
		} catch (Exception error) {
			paymentTypes.success = false;
			paymentTypes.dto = null;
			paymentTypes.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(paymentTypes);
	}

	@RequestMapping(value = "/getTitleData", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpTitleDTO>>> getTitleData() {
		ApiResult<List<EmpTitleDTO>> result = new ApiResult<List<EmpTitleDTO>>();
		try {
			List<EmpTitleDTO> titleList = commonEmployeeHandler.getEmpTitleList();
			if(!Utils.isNullOrEmpty(titleList)){
				result.dto = titleList;
				result.success = true;
			}else{
				result.dto = null;
				result.failureMessage = "";
			}
		}catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getResearchDetails", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getResearchDetails() {
		ApiResult<List<EmployeeApplicationDTO>> researchDetails = new ApiResult<List<EmployeeApplicationDTO>>();
		try {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					Query qry = context.createQuery(RecruitmentQueries.SELECT_RESEARCH_DETAILS_ALL);
					List<EmpApplnAddtnlInfoHeadingDBO> researchHeadingList = qry.getResultList();
					if(!Utils.isNullOrEmpty(researchHeadingList)){
						researchDetails.success = true;
						researchDetails.dto = new ArrayList<>();
						for(EmpApplnAddtnlInfoHeadingDBO heading : researchHeadingList){
							EmployeeApplicationDTO headDTO = new EmployeeApplicationDTO();
							headDTO.value = !Utils.isNullOrEmpty(heading.id) ? String.valueOf(heading.id) : null;
							headDTO.label = !Utils.isNullOrEmpty(heading.addtnlInfoHeadingName) ? String.valueOf(heading.addtnlInfoHeadingName) : "";
							headDTO.headingDisplayOrder = !Utils.isNullOrEmpty(heading.headingDisplayOrder) ? String.valueOf(heading.headingDisplayOrder) : "";
							headDTO.isTypeResearch = !Utils.isNullOrEmpty(heading.isTypeResearch) ? Boolean.valueOf(heading.isTypeResearch) : false;
							headDTO.employeeCategoryId = !Utils.isNullOrEmpty(heading.empEmployeeCategoryId) && !Utils.isNullOrEmpty(heading.empEmployeeCategoryId.id)
									? String.valueOf(heading.empEmployeeCategoryId.id) : null;
							headDTO.detailsDTO = new ArrayList<>();
							if(!Utils.isNullOrEmpty(heading.empApplnAddtnlInfoParameterMap)) {
								for(EmpApplnAddtnlInfoParameterDBO parameters : heading.empApplnAddtnlInfoParameterMap) {
									if(parameters.recordStatus == 'A') {
										EmployeeApplicationDetailsDTO parameterDTO = new EmployeeApplicationDetailsDTO();
										parameterDTO.value = !Utils.isNullOrEmpty(parameters.id) ? String.valueOf(parameters.id) : null;
										parameterDTO.label = !Utils.isNullOrEmpty(parameters.addtnlInfoParameterName) ? String.valueOf(parameters.addtnlInfoParameterName) : "";
										parameterDTO.empApplnAddtnlInfoHeadingId = (!Utils.isNullOrEmpty(parameters.empApplnAddtnlInfoHeading) && !Utils.isNullOrEmpty(parameters.empApplnAddtnlInfoHeading.id)) ? String.valueOf(parameters.empApplnAddtnlInfoHeading.id) : null;
										parameterDTO.parameterDisplayOrder = !Utils.isNullOrEmpty(parameters.parameterDisplayOrder) ? String.valueOf(parameters.parameterDisplayOrder) : "";
										parameterDTO.isDisplayInApplication = !Utils.isNullOrEmpty(parameters.isDisplayInApplication) ? Boolean.valueOf(parameters.isDisplayInApplication) : false;
										headDTO.detailsDTO.add(parameterDTO);
									}
								}
								Collections.sort(headDTO.detailsDTO, new Comparator<EmployeeApplicationDetailsDTO>() {
									@Override
									public int compare(EmployeeApplicationDetailsDTO o1, EmployeeApplicationDetailsDTO o2) {
										return Integer.compare(Integer.parseInt(o1.parameterDisplayOrder), Integer.parseInt(o2.parameterDisplayOrder));
									}
								});
							}
							researchDetails.dto.add(headDTO);
						}
					}
				}
				@Override
				public void onError(Exception error) {
					researchDetails.success = false;
					researchDetails.dto = null;
					researchDetails.failureMessage = error.getMessage();
				}
			}, true);
		}
		catch(Exception error) {
			Utils.log(error.getMessage());
		}
		return Utils.monoFromObject(researchDetails);
	}

	@PostMapping(value = "/getActivityType")
	public Flux<SelectDTO> getActivityType() {
		return commonEmployeeHandler.getActivityType().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getActivity")
	public Flux<SelectDTO> getActivity( @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return commonEmployeeHandler.getActivity(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "getEmployeeListWithDepartment")
	public Flux<SelectDTO> getEmployeeListWithDepartment() {
		return commonEmployeeHandler.getEmployeeListWithDepartment().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getCampusDepartmentConcat")
	public Flux<SelectDTO> getCampusDepartmentConcat() {
		return commonEmployeeHandler.getCampusDepartmentConcat().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getCampusByDepartmentId")
	public Flux<SelectDTO> getCampusByDepartmentId(String deptId) {
		return commonEmployeeHandler.getCampusByDepartmentId(deptId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "getEmployeeAapplicationAdvertisement")
	public Mono<EmpApplnAdvertisementDTO> getEmployeeAapplicationAdvertisement() {
		return commonEmployeeHandler.getEmployeeAapplicationAdvertisement().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getPayScaleLevel")
	public Flux<SelectDTO> getPayScaleLevel() {
		return commonEmployeeHandler.getPayScaleLevel().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@RequestMapping(value = "/getEmployeeNamesListBySearch", method = RequestMethod.POST)
	public Flux<SelectDTO> getEmployeeNamesListBySearch(@RequestParam("employeeName") String employeeName) {    
		return commonEmployeeHandler.getEmployeeNameBySearch(employeeName).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@RequestMapping(value = "/getEmployeeIDListBySearch", method = RequestMethod.POST)
	public Flux<SelectDTO> getEmployeeIdListBySearch(@RequestParam("employeeId") String employeeId) {    
		return commonEmployeeHandler.getEmployeeIdBySearch(employeeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping (value = "/empPayScaleByEmpCategory")
	public Flux<List<SelectDTO>> empPayScaleByEmpCategory(@RequestParam String empCategoryId) {
		return commonEmployeeHandler.empPayScaleByEmpCategory(empCategoryId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	 
	@PostMapping(value = "/getEmpApplicationAdvertisement")
	public Mono<EmpApplnAdvertisementDTO> getEmpApplicationAdvertisement() {
		return commonEmployeeHandler.getEmpApplicationAdvertisement().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping("/getEmployeeWithTitle")
	public Flux<SelectDTO> getEmployeeWithTitle(){
		return  commonEmployeeHandler.getEmployeeWithTitle().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@RequestMapping(value = "/getEmployeeIdsOrNames", method = RequestMethod.POST)
	public Mono<List<SelectDTO>> getEmployeeIdOrName(@RequestParam("employeeIdOrName") String employeeIdOrName,Boolean isNumber) {    
		return commonEmployeeHandler.getEmployeeIdOrName(employeeIdOrName,isNumber).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping("/getEmpDesignation")
	public Flux<SelectDTO> getEmpDesignation(){
		return  commonEmployeeHandler.getEmpDesignation().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping("/getDepartmentsByDeanery")
	public Flux<SelectDTO> getDepartmentsByDeanery(@RequestParam String deaneryId){
		return  commonEmployeeHandler.getDepartmentsByDeanery(deaneryId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping("/getResignationReason")
	public Flux<SelectDTO> getResignationReason(){
		return commonEmployeeHandler.getResignationReason().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@RequestMapping(value = "/getRevisedYear", method = RequestMethod.POST)
	public Flux<SelectDTO> getRevisedYear(String gradeId){
		return commonEmployeeHandler.getRevisedYear(gradeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	} 	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/duplicateCheckForEmployee", method = RequestMethod.POST)
	public Mono<ApiResult> duplicateCheckForEmployee(@RequestParam Integer applnNo){
		return commonEmployeeHandler.duplicateCheckForEmployee(applnNo).switchIfEmpty(Mono.error(new NotFoundException(null)));
	} 	
	
	@RequestMapping(value = "/getApplicantDataAndTimeLine", method = RequestMethod.POST)
	public Mono<EmpApplnEntriesDTO> getApplicantDataAndTimeLine(@RequestParam Integer applnNo) {
		return commonEmployeeHandler.getApplicantDataAndTimeLine(applnNo).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getCampusForLocations")
	public Flux<SelectDTO> getCampusForLocation(@RequestParam List<Integer> locIds){
        return commonEmployeeHandler.getCampusForLocation(locIds).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getCampusForLocationsAndUser")
	public Flux<SelectDTO> getCampusForLocationsAndUser(@RequestParam List<Integer> locIds,@RequestHeader(Constants.HEADER_JWT_USER_ID)String userId){
        return commonEmployeeHandler.getCampusForLocationsAndUser(locIds,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getDepartmentsByCampusIds")
	public Flux<SelectDTO> getDepartmentsByCampusIds(@RequestParam List<Integer> campusIds){
        return commonEmployeeHandler.getDepartmentsByCampusIds(campusIds).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	@PostMapping("/getAllSubjectCategory")
	public Flux<SelectDTO> getAllSubjectAndCategory(){
		return commonEmployeeHandler.getAllSubjectAndCategory().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping("/getAllSubjectCategorySpecialisation")
	public Flux<SelectDTO> getAllSubjectCategorySpecialisation(){
		return commonEmployeeHandler.getAllSubjectCategorySpecialisation().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping("/getLetterTypes")
	public Flux<SelectDTO> getLetterTypes(){
		return commonEmployeeHandler.getLetterTypes().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping("/getTimeZoneByCategory")
	public Flux<SelectDTO> getTimeZoneByCategory(@RequestParam String categoryId,@RequestParam String isSelected){
		return commonEmployeeHandler.getTimeZoneByCategory(Integer.parseInt(categoryId),isSelected).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value = "/getJobCategoryComponentMapping")
	public Mono<List<EmpProfileComponentMapDTO>> getJobCategoryComponentMapping(@RequestParam Integer jobCategoryId) throws Exception {
		return commonEmployeeHandler.getJobCategoryComponentMapping(jobCategoryId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	@PostMapping(value = "/getJobCategoryComponentGroupMapping")
	public Mono<List<EmpProfileComponentMapDTO>> getJobCategoryComponentGroupMapping(@RequestParam Integer jobCategoryId) throws Exception {
		return commonEmployeeHandler.getJobCategoryComponentGroupMapping(jobCategoryId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	@PostMapping(value = "/getEmpIdByUserId")
	public Mono<Integer> getEmpIdByUserId(@RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) throws Exception {
		return commonEmployeeHandler.getEmpIdByUserId(Integer.parseInt(userId)).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
}