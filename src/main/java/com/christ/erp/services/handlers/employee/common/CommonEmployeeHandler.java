package com.christ.erp.services.handlers.employee.common;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Tuple;

import com.christ.erp.services.dto.employee.recruitment.*;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryActivityDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryMainActivityDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDesignationDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistMainDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistSubDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAdvertisementDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnPersonalDataDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEducationalDetailsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEducationalDetailsDocumentsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligibilityTestDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligibilityTestDocumentDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpFamilyDetailsAddtnlDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpPersonalDataDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpWorkExperienceDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpWorkExperienceDocumentDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDetailDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleLevelDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpShiftTypesDTO;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import com.christ.erp.services.dto.employee.common.EmpCampusDeaneryDTO;
import com.christ.erp.services.dto.employee.common.EmpCampusDeaneryDepartmentDTO;
import com.christ.erp.services.dto.employee.common.EmpDeaneryDepartmentDTO;
import com.christ.erp.services.dto.employee.common.EmpTitleDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import com.christ.erp.services.dto.employee.common.HostelProgrammeDetailsDTO;
import com.christ.erp.services.dto.employee.common.WorkFlowStatusApplicantTimeLineDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistMainDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistSubDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDetailsDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleMatrixDetailDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.helpers.employee.common.CommonEmployeeHelper;
import com.christ.erp.services.transactions.employee.common.CommonEmployeeTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommonEmployeeHandler {

	private static volatile CommonEmployeeHandler commonEmployeeHandler = null;
	//CommonEmployeeHelper commonEmployeeHelper = CommonEmployeeHelper.getInstance();
	//CommonEmployeeTransaction commonEmployeeTransaction = CommonEmployeeTransaction.getInstance();

	@Autowired
	CommonEmployeeTransaction commonEmployeeTransaction;

	@Autowired
	CommonEmployeeHelper commonEmployeeHelper;

	//    public static CommonEmployeeHandler getInstance() {
	//        if(commonEmployeeHandler==null) {
	//            commonEmployeeHandler = new CommonEmployeeHandler();
	//        }
	//        return commonEmployeeHandler;
	//    }
	
	@Autowired
	AWSS3FileStorageService aWSS3FileStorageService;

	public List<EmployeeApplicationDTO> getJobCategory(String employeeCategoryId) throws Exception {
		List<Tuple> mappings = commonEmployeeTransaction.getJobCategory(employeeCategoryId);
		List<EmployeeApplicationDTO> jobCatgry = null;
		if(mappings != null && mappings.size() > 0) {
			jobCatgry = new ArrayList<>();
			for(Tuple mapping : mappings) {
				EmployeeApplicationDTO common = new EmployeeApplicationDTO();
				common.value = String.valueOf(mapping.get("ID"));
				common.label = String.valueOf(mapping.get("Text"));
				common.showInAppln = !Utils.isNullOrEmpty(mapping.get("showInAppln")) ? Boolean.valueOf(String.valueOf(mapping.get("showInAppln"))) : false;
				common.jobCategoryCode = String.valueOf(mapping.get("Code"));
				jobCatgry.add(common);
			}
		}
		return jobCatgry;
	}

	public ApiResult<List<LookupItemDTO>> getLeavecategory() {
		ApiResult<List<LookupItemDTO>> leaveCategory = new ApiResult<List<LookupItemDTO>>();
		try {
			leaveCategory = commonEmployeeTransaction.getLeaveCategory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return leaveCategory;
	}

	public ApiResult<List<LookupItemDTO>> getTimeZone() {
		ApiResult<List<LookupItemDTO>> timeZoneList = new ApiResult<List<LookupItemDTO>>();
		try {
			timeZoneList = commonEmployeeTransaction.getTimeZone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeZoneList;
	}

	public List<EmpDocumentChecklistMainDTO> getAllEmpDocumentCheckList() throws Exception {
		List<EmpDocumentChecklistMainDBO> mappings=commonEmployeeTransaction.getAllEmpDocumentCheckList();
		List<EmpDocumentChecklistMainDTO> list=new ArrayList<>();
		if(mappings != null && mappings.size() > 0) {
			for(EmpDocumentChecklistMainDBO mapping : mappings) {
				EmpDocumentChecklistMainDTO documentChecklistMainDTO= new EmpDocumentChecklistMainDTO();
				documentChecklistMainDTO.id=mapping.id;
				documentChecklistMainDTO.headingName=mapping.documentChecklistName;
				documentChecklistMainDTO.headingOrder=String.valueOf(mapping.documentChecklistDisplayOrder);
				documentChecklistMainDTO.isForeignNationalDocumentChecklist=String.valueOf(mapping.isForeignNationalDocumentChecklist);
				documentChecklistMainDTO.checklistDocuments=new ArrayList<EmpDocumentChecklistSubDTO>();
				if(mapping.subChecklist.size()>0) {
					for(EmpDocumentChecklistSubDBO  setobj :mapping.subChecklist) {
						if(setobj.recordStatus=='A') {
							EmpDocumentChecklistSubDTO documentChecklistSubDTO=new EmpDocumentChecklistSubDTO();
							documentChecklistSubDTO.id=setobj.id;
							documentChecklistSubDTO.documentName=setobj.documentChecklistSubName;
							documentChecklistSubDTO.documentOrder=String.valueOf(setobj.documentChecklistSubDisplayOrder);
							documentChecklistMainDTO.checklistDocuments.add(documentChecklistSubDTO);
						}
					}
				}
				list.add(documentChecklistMainDTO);	
			}
		}
		return list;		
	}

	public List<EmpDocumentChecklistMainDTO> getAllEmpDocumentCheckListwithForeign() throws Exception {
		List<EmpDocumentChecklistMainDBO> mappings=commonEmployeeTransaction.getAllEmpDocumentCheckListwithForeign();
		List<EmpDocumentChecklistMainDTO> list=new ArrayList<>();
		if(mappings != null && mappings.size() > 0) {
			for(EmpDocumentChecklistMainDBO mapping : mappings) {
				EmpDocumentChecklistMainDTO documentChecklistMainDTO= new EmpDocumentChecklistMainDTO();
				documentChecklistMainDTO.id=mapping.id;
				documentChecklistMainDTO.headingName=mapping.documentChecklistName;
				documentChecklistMainDTO.headingOrder=String.valueOf(mapping.documentChecklistDisplayOrder);
				documentChecklistMainDTO.isForeignNationalDocumentChecklist=String.valueOf(mapping.isForeignNationalDocumentChecklist);
				documentChecklistMainDTO.checklistDocuments=new ArrayList<EmpDocumentChecklistSubDTO>();
				if(mapping.subChecklist.size()>0) {
					for(EmpDocumentChecklistSubDBO  setobj :mapping.subChecklist) {
						if(setobj.recordStatus=='A') {
							EmpDocumentChecklistSubDTO documentChecklistSubDTO=new EmpDocumentChecklistSubDTO();
							documentChecklistSubDTO.id=setobj.id;
							documentChecklistSubDTO.documentName=setobj.documentChecklistSubName;
							documentChecklistSubDTO.documentOrder=String.valueOf(setobj.documentChecklistSubDisplayOrder);
							documentChecklistMainDTO.checklistDocuments.add(documentChecklistSubDTO);
						}
					}
				}
				list.add(documentChecklistMainDTO);	
			}
		}
		return list;	
	}

	public  Tuple getApplicationNumberLength() throws Exception {
		return commonEmployeeTransaction.getApplicationNumberLength();
	}

	public EmpApplnEntriesDTO applicantPersonalData(String applicantId) throws Exception {
		Tuple mappings=commonEmployeeTransaction.applicantPersonalData(applicantId);
		EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
		if(mappings != null) {
			empApplnEntriesDTO.applicantId= mappings.get("ApplicantId").toString();
			empApplnEntriesDTO.applicantName=mappings.get("ApplicantName").toString();
			if(!Utils.isNullOrEmpty(mappings.get("Campus")))
				empApplnEntriesDTO.applicantCampus=mappings.get("Campus").toString();
			empApplnEntriesDTO.applicantAppliedFor=mappings.get("PostApplied").toString();
			empApplnEntriesDTO.applicantCountry=mappings.get("Country").toString();
			empApplnEntriesDTO.category = new ExModelBaseDTO();
			empApplnEntriesDTO.category.id = mappings.get("CategoryId").toString();
			empApplnEntriesDTO.category.text = mappings.get("PostApplied").toString();
		}  
		return empApplnEntriesDTO;
	}

	public List<EmpPayScaleMatrixDetailDTO> getCellValueByCategoryId(String employeeCategoryID) throws Exception {
		List<EmpPayScaleMatrixDetailDTO> empPayScaleMatrixDetailDTOs = new ArrayList<EmpPayScaleMatrixDetailDTO>();
		List<Tuple> empPayScaleMatrixDetailList = commonEmployeeTransaction.getCellValueByCategoryId(employeeCategoryID);
		if(!Utils.isNullOrEmpty(empPayScaleMatrixDetailList)) {
			if(empPayScaleMatrixDetailList != null && empPayScaleMatrixDetailList.size() > 0) {
				for(Tuple mapping : empPayScaleMatrixDetailList) {
					EmpPayScaleMatrixDetailDTO empPayScaleMatrixDetailDTO = new EmpPayScaleMatrixDetailDTO();
					empPayScaleMatrixDetailDTO.id = (Integer) mapping.get("ID");
					empPayScaleMatrixDetailDTO.levelCellValue = mapping.get("Text").toString();
					empPayScaleMatrixDetailDTOs.add(empPayScaleMatrixDetailDTO);
				}
			}
		}
		return empPayScaleMatrixDetailDTOs;
	}

	public EmpDailyWageSlabDTO getDlyWgData(String employeeCategoryId,String employeeJobCategoryId) throws Exception {
		List<Tuple> dlyWgDataList = commonEmployeeTransaction.getEmployeeDlyWageDetails(employeeCategoryId, employeeJobCategoryId);
		EmpDailyWageSlabDTO dailywageInfo = new EmpDailyWageSlabDTO();
		if(!Utils.isNullOrEmpty(dlyWgDataList)) {
			EmpDailyWageSlabDetailsDTO dailywageDetlInfo = null;
			ArrayList<EmpDailyWageSlabDetailsDTO> detailList = new ArrayList<>();
			for(Tuple slabDetailDTO : dlyWgDataList) {
				dailywageDetlInfo = new EmpDailyWageSlabDetailsDTO();
				dailywageInfo.empCategory = new ExModelBaseDTO();
				dailywageInfo.empCategory.id = slabDetailDTO.get("emp_cat_id").toString();
				dailywageInfo.empCategory.text = slabDetailDTO.get("emp_cat_name").toString();
				dailywageInfo.jobCategory = new ExModelBaseDTO();
				dailywageInfo.jobCategory.id = slabDetailDTO.get("emp_job_cat_id").toString();
				dailywageInfo.jobCategory.text = slabDetailDTO.get("emp_job_cat_name").toString();
				dailywageDetlInfo.id = (Integer) slabDetailDTO.get("dly_wg_slab_Id");
				dailywageDetlInfo.dailyWageSlabfrom = (Integer) slabDetailDTO.get("dly_wge_from");
				dailywageDetlInfo.dailyWageSlabto = (Integer) slabDetailDTO.get("dly_wge_to");
				dailywageDetlInfo.dailyWageSlabbasic = (Integer) slabDetailDTO.get("dly_wge_bsc");
				detailList.add(dailywageDetlInfo);
				dailywageInfo.empDailyWageDetails = detailList;
			}
		}
		return dailywageInfo;
	}

	public ApiResult<List<EmployeeApplicationDTO>> getEmployeetitles() throws Exception {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		List<Tuple> list = commonEmployeeTransaction.getEmployeetitles();
		if(!Utils.isNullOrEmpty(list)) {
			if(list != null && list.size() > 0) {
				emp.dto = new ArrayList<>();
				for(Tuple mapping : list) {
					if(!Utils.isNullOrEmpty(mapping.get("ID").toString()) && !Utils.isNullOrEmpty(mapping.get("Text").toString())) {
						EmployeeApplicationDTO common = new EmployeeApplicationDTO();
						common.value = mapping.get("ID").toString();
						common.label = mapping.get("Text").toString();
						emp.dto.add(common);
					}
				}
			}
		} 
		return emp;
	}	

	public ApiResult<List<EmployeeApplicationDTO>> getEmployees() throws Exception {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		List<Tuple> list = commonEmployeeTransaction.getEmployees();
		if(!Utils.isNullOrEmpty(list)) {
			if(list != null && list.size() > 0) {
				emp.dto = new ArrayList<>();
				for(Tuple mapping : list) {
					EmployeeApplicationDTO common = new EmployeeApplicationDTO();
					common.value = mapping.get("ID").toString();
					common.label = mapping.get("Text").toString();
					emp.dto.add(common);
				}
			}
		} 
		return emp;
	}

	public List<LookupItemDTO> getProcessType() throws Exception {
		List<LookupItemDTO> processTypes = new ArrayList<>();
		processTypes.add(new LookupItemDTO("Employee interview schedule approval","Employee interview schedule approval"));
		processTypes.add(new LookupItemDTO("Employee final interview comments","Employee final interview comments"));
		processTypes.add(new LookupItemDTO("Employee On-boarding","Employee On-boarding"));
		processTypes.add(new LookupItemDTO("Employee Late entry","Employee Late entry"));
		processTypes.add(new LookupItemDTO("Employee Absence","Employee Absence"));
		processTypes.add(new LookupItemDTO("Employee Resignation","Employee Resignation"));
		return processTypes;
	}

	public List<LookupItemDTO> getType() throws Exception {
		List<LookupItemDTO> type = new ArrayList<>();
		type.add(new LookupItemDTO("1","Holiday"));
		type.add(new LookupItemDTO("2","Restricted holiday"));
		type.add(new LookupItemDTO("3","Event"));
		type.add(new LookupItemDTO("4","Vacation"));
		return type;
	}

	public ApiResult<List<EmpCampusDeaneryDepartmentDTO>> getCampusDeaneryDeptByLocId(String locId) throws Exception {
		ApiResult< List<EmpCampusDeaneryDepartmentDTO>> campusDeaneryDepartment = new ApiResult<List<EmpCampusDeaneryDepartmentDTO>>();
		List<ErpCampusDepartmentMappingDBO> dboList = null;
		dboList = commonEmployeeTransaction.getCampusDepartmentMappings(locId);
		campusDeaneryDepartment.dto = new ArrayList<EmpCampusDeaneryDepartmentDTO>();
		if (dboList != null && dboList.size() > 0) {
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
							if( !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) &&  !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO)  && !deaneryIds.contains(dbo2.erpDepartmentDBO.erpDeaneryDBO.id)){
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
		return 	campusDeaneryDepartment;
	}

	public ApiResult<List<EmployeeApplicationDTO>> getEmployeeByIdsByEmpCategory(HolidaysAndEventsEntryDTO data) throws Exception {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
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
		List<EmpDBO> dboList = null;
		dboList = commonEmployeeTransaction.getEmpDetails(data,campusDeanearyDeptIds);		
		if(dboList!=null) {
			emp.dto = new ArrayList<>();
			for(EmpDBO e : dboList) {
				EmployeeApplicationDTO common = new EmployeeApplicationDTO();
				common.value = e.id.toString();
				common.label = e.empName.toString();
				emp.dto.add(common);
			}
		}
		return emp;
	}

	public List<SalaryComponentDTO> getEmpPayScaleComponents() throws Exception {
		List<SalaryComponentDTO> salaryComponentDTOs = new ArrayList<SalaryComponentDTO>();
		List<Tuple> empPayScaleComponentsList = commonEmployeeTransaction.getEmpPayScaleComponents();
		if(!Utils.isNullOrEmpty(empPayScaleComponentsList)) {
			if(empPayScaleComponentsList != null && empPayScaleComponentsList.size() > 0) {
				for(Tuple mapping : empPayScaleComponentsList) {
					SalaryComponentDTO mappingInfo = new SalaryComponentDTO();
					mappingInfo.id = mapping.get("ID").toString();
					mappingInfo.allowanceType = !Utils.isNullOrEmpty(mapping.get("salaryComponentName")) ? mapping.get("salaryComponentName").toString() : "";
					mappingInfo.shortName = !Utils.isNullOrEmpty(mapping.get("salaryComponentShortName")) ? mapping.get("salaryComponentShortName").toString() : "";
					mappingInfo.displayOrder = !Utils.isNullOrEmpty(mapping.get("salaryComponentDisplayOrder")) ? mapping.get("salaryComponentDisplayOrder").toString() : "";
					mappingInfo.isBasic = (Boolean)mapping.get("isComponentBasic");
					if(!Utils.isNullOrEmpty(mapping.get("percentage")))
						mappingInfo.mentionPercentage = mapping.get("percentage").toString();
					mappingInfo.calculationType =(Boolean) mapping.get("isCaculationTypePercentage");
					mappingInfo.payScaleType = mapping.get("payScaleType").toString();
					mappingInfo.amount = "";
					salaryComponentDTOs.add(mappingInfo);
				}
			}
		}
		return salaryComponentDTOs;
	}

	public ApiResult<List<LookupItemDTO>> getAppliedSubject(String applicationNumber) {
		ApiResult<List<LookupItemDTO>> subjectPref = new ApiResult<List<LookupItemDTO>>();
		try {
			subjectPref = commonEmployeeTransaction.getAppliedSubject(applicationNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subjectPref;
	}

	public ApiResult<List<LookupItemDTO>> getAppliedSubjectSpecialization(String applicationNumber) {
		ApiResult<List<LookupItemDTO>> subjectSpecializationPref = new ApiResult<List<LookupItemDTO>>();
		try {
			subjectSpecializationPref = commonEmployeeTransaction.getAppliedSubjectSpecialization(applicationNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subjectSpecializationPref;
	}

	public ApiResult<List<LookupItemDTO>> getSubjectCategorySpecialization(String subjectCategory) {
		ApiResult<List<LookupItemDTO>> subjectCategorySpecialization = new ApiResult<List<LookupItemDTO>>();
		try {
			subjectCategorySpecialization = commonEmployeeTransaction.getSubjectCategorySpecialization(subjectCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subjectCategorySpecialization;
	}

	public List<EmpCampusDeaneryDepartmentDTO> getEmpCampusDeaneryDepartment() throws Exception {
		List<EmpCampusDeaneryDepartmentDTO> listDTOs = null;
		List<ErpCampusDepartmentMappingDBO> dboList = commonEmployeeTransaction.getEmpCampusDeaneryDepartment();
		if(!Utils.isNullOrEmpty(dboList)) {
			listDTOs= commonEmployeeHelper.setEmpCampusDeaneryDepartmentDBOsToDTOs(dboList);
		}
		return listDTOs;
	}

	public List<HostelProgrammeDetailsDTO> getCampusLevelProgramme() throws Exception {
		List<HostelProgrammeDetailsDTO> list = null;
		List<ErpCampusProgrammeMappingDBO> dbo = commonEmployeeTransaction.getCampusLevelProgramme();
		if(!Utils.isNullOrEmpty(dbo)){
			list = commonEmployeeHelper.setCampusLevelProgrammeDBOToDTO1(dbo);
		}
		return list;
	}

	public List<EmpShiftTypesDTO> getEmployeeWorkShifts(String campusId) throws Exception{
		List<EmpShiftTypesDTO> workShiftList = commonEmployeeTransaction.getEmployeeWorkShifts(campusId);
		return workShiftList;
	}

	public List<EmpCampusDeaneryDepartmentDTO> getDepartmentCampus() throws Exception {
		List<EmpCampusDeaneryDepartmentDTO> listDTOs = null;
		List<ErpCampusDepartmentMappingDBO> dboList = commonEmployeeTransaction.getEmpCampusDeaneryDepartment();
		if(!Utils.isNullOrEmpty(dboList)) {
			listDTOs= commonEmployeeHelper.setTreeDepartmentCampusDBOsToDTOs(dboList);
		}
		return listDTOs;
	}

	public ApiResult<List<EmployeeApplicationDTO>> getEmployeeLetterRequestType() throws Exception {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		List<Tuple> list = commonEmployeeTransaction.getEmployeeLetterRequestType();
		if(!Utils.isNullOrEmpty(list)) {
			if(list != null && list.size() > 0) {
				emp.dto = new ArrayList<>();
				for(Tuple mapping : list) {
					EmployeeApplicationDTO common = new EmployeeApplicationDTO();
					common.value = mapping.get("ID").toString();
					common.label = mapping.get("Text").toString();
					emp.dto.add(common);
				}
			}
		} 
		return emp;
	}

	public ApiResult<List<EmployeeApplicationDTO>> getEmployeeLetterRequestReasons() throws Exception {
		ApiResult<List<EmployeeApplicationDTO>> emp = new ApiResult<List<EmployeeApplicationDTO>>();
		List<Tuple> list = commonEmployeeTransaction.getEmployeeLetterRequestReasons();
		if(!Utils.isNullOrEmpty(list)) {
			if(list != null && list.size() > 0) {
				emp.dto = new ArrayList<>();
				for(Tuple mapping : list) {
					EmployeeApplicationDTO common = new EmployeeApplicationDTO();
					common.value = mapping.get("ID").toString();
					common.label = mapping.get("Text").toString();
					emp.dto.add(common);
				}
			}
		} 
		return emp;
	}

	public List<EmployeeApplicationDTO> getDepartmentOnCampus(String campusId) throws Exception {
		List<Tuple> mappings = commonEmployeeTransaction.getDepartmentOnCampus(campusId);
		List<EmployeeApplicationDTO> deptOnCampus = null;
		if(mappings != null && mappings.size() > 0) {
			deptOnCampus = new ArrayList<>();
			for(Tuple mapping : mappings) {
				EmployeeApplicationDTO common = new EmployeeApplicationDTO();
				common.value = mapping.get("ID").toString();
				common.label = mapping.get("Text").toString();
				deptOnCampus.add(common);
			}
		}
		Collections.sort(deptOnCampus, Comparator.comparing(EmployeeApplicationDTO::getLabel));
		return deptOnCampus;
	}

	public List<EmpApplnNonAvailabilityDTO> getApplnNonAvailability(String interviewRound) throws Exception {
		List<Tuple> nonAvailabilityList = commonEmployeeTransaction.getApplnNonAvailability(interviewRound);
		List<EmpApplnNonAvailabilityDTO> nonAvailabilityDTOS = null;
		if(!Utils.isNullOrEmpty(nonAvailabilityList)) {
			nonAvailabilityDTOS = new ArrayList<>();
			for(Tuple tuple : nonAvailabilityList) {
				EmpApplnNonAvailabilityDTO empApplnNonAvailabilityDTO = new EmpApplnNonAvailabilityDTO();
				empApplnNonAvailabilityDTO.setValue(tuple.get("empApplnNonAvailabilityId").toString());
				empApplnNonAvailabilityDTO.setLabel(tuple.get("nonAvailabilityName").toString());
				empApplnNonAvailabilityDTO.isReschedulable = !Utils.isNullOrEmpty(tuple.get("isReschedulable")) && Boolean.valueOf(tuple.get("isReschedulable").toString())
						? "true" : "false";
				empApplnNonAvailabilityDTO.interviewRound = !Utils.isNullOrEmpty(tuple.get("interviewRound")) ? tuple.get("interviewRound").toString() : null;
				empApplnNonAvailabilityDTO.isFinalSelection = !Utils.isNullOrEmpty(tuple.get("isFinalSelection")) && Boolean.valueOf(tuple.get("isFinalSelection").toString())
						? "true" : "false";
				nonAvailabilityDTOS.add(empApplnNonAvailabilityDTO);
			}
			 Collections.sort(nonAvailabilityDTOS, Comparator.comparing(EmpApplnNonAvailabilityDTO::getLabel));
		}
		return nonAvailabilityDTOS;
	}

	public EmpApplnInterviewSchedulesDTO getApplnInterviewScheduleDetails(Integer empApplnEntriesId, String interviewRound) throws Exception {
		Tuple tuple = commonEmployeeTransaction.getApplnInterviewScheduleDetails(empApplnEntriesId,interviewRound);
		EmpApplnInterviewSchedulesDTO empApplnInterviewSchedulesDTO = null;
		//SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
		if(!Utils.isNullOrEmpty(tuple)) {
			empApplnInterviewSchedulesDTO = new EmpApplnInterviewSchedulesDTO();
			empApplnInterviewSchedulesDTO.interviewVenue = !Utils.isNullOrEmpty(tuple.get("interviewVenue")) ? tuple.get("interviewVenue").toString() : "";
			empApplnInterviewSchedulesDTO.setInterviewDateTime1(!Utils.isNullOrEmpty(tuple.get("interviewDateTime")) ? Utils.convertStringDateTimeToLocalDateTime(String.valueOf(tuple.get("interviewDateTime"))) : null);
			empApplnInterviewSchedulesDTO.pointOfContactUsersId = !Utils.isNullOrEmpty(tuple.get("userName")) ? tuple.get("userName").toString() : "";
		}
		return empApplnInterviewSchedulesDTO;
	}

	public ApiResult<List<LookupItemDTO>> getScorecard() {
		ApiResult<List<LookupItemDTO>> scorecardName = new ApiResult<List<LookupItemDTO>>();
		try {
			scorecardName = commonEmployeeTransaction.getScorecard();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scorecardName;
	}

	public List<LookupItemDTO> getMode() throws Exception {
		List<LookupItemDTO> modes = new ArrayList<>();
		modes.add(new LookupItemDTO("1","Online Entrance"));
		modes.add(new LookupItemDTO("2","Center Based Entrance"));
		modes.add(new LookupItemDTO("3","Group Process"));
		modes.add(new LookupItemDTO("4","Individual Process"));
		modes.sort(Comparator.comparing(LookupItemDTO::getLabel));
		return modes;
	}

	public List<LookupItemDTO> getEmployeeAppraisalElementOption() throws Exception{
		List<Tuple> appraisalElementOptionList = commonEmployeeTransaction.getEmployeeAppraisalElementOption();
		List<LookupItemDTO> appraisalElementOptions = null;
		if(!Utils.isNullOrEmpty(appraisalElementOptionList)) {
			appraisalElementOptions = new ArrayList<>();
			for(Tuple tuple : appraisalElementOptionList) {
				LookupItemDTO lookupItemDTO = new LookupItemDTO();
				lookupItemDTO.value = tuple.get("empAppraisalElementsOptionId").toString();
				lookupItemDTO.label = tuple.get("optionGroupName").toString();
				appraisalElementOptions.add(lookupItemDTO);
			}
		}
		return appraisalElementOptions;
	}

	public List<LookupItemDTO> getJobCategories() throws Exception {
		List<Tuple> mappings = commonEmployeeTransaction.getJobCategories();
		List<LookupItemDTO> lookupItemDTOs = null;
		if(mappings != null && mappings.size() > 0) {
			lookupItemDTOs = new ArrayList<>();
			for(Tuple mapping : mappings) {
				LookupItemDTO lookupItemDTO = new LookupItemDTO();
				lookupItemDTO.value = mapping.get("ID").toString();
				lookupItemDTO.label = mapping.get("Text").toString();
				lookupItemDTOs.add(lookupItemDTO);
			}
		}
		return lookupItemDTOs;
	}

	public ApiResult<List<LookupItemDTO>> getPaymentTypes() {
		ApiResult<List<LookupItemDTO>> paymentTypes = new ApiResult<List<LookupItemDTO>>();
		try {
			String [] paymentTypeList = {"SCALE PAY","CONSOLIDATED","DAILY","PER HOUR","PER COURSE","NO PAY"};
			paymentTypes.dto = new ArrayList<>();
			for (int i = 0; i < paymentTypeList.length; i++) {
				LookupItemDTO itemInfo = new LookupItemDTO();
				itemInfo.value = String.valueOf(i);
				itemInfo.label = paymentTypeList[i];
				paymentTypes.dto.add(itemInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paymentTypes;
	}

	public List<EmpTitleDTO> getEmpTitleList() throws Exception {
		List<EmpTitleDTO> empTitleList =	null;
		List<Tuple> list = commonEmployeeTransaction.getEmpTitleList();
		if(!Utils.isNullOrEmpty(list)){
			empTitleList = new ArrayList<EmpTitleDTO>();
			for (Tuple tuple : list) {
				if(!Utils.isNullOrEmpty(tuple.get("Text")) && !Utils.isNullOrWhitespace(tuple.get("Text").toString())) {
					EmpTitleDTO empTitle = new EmpTitleDTO();
					empTitle.id = tuple.get("ID").toString();
					empTitle.titleName = tuple.get("Text").toString();
					empTitle.isGeneralForUniversity = !Utils.isNullOrEmpty(tuple.get("UnivercityLevel")) ? tuple.get("UnivercityLevel").toString(): "";
					empTitleList.add(empTitle);
				}
			}
		}
		return empTitleList;
	}

	public Flux<SelectDTO> getActivityType() {
		return commonEmployeeTransaction.getActivityType().flatMapMany(Flux::fromIterable).map(this::convertEmpWorkDiaryMainDboToDto);
	}

	public SelectDTO convertEmpWorkDiaryMainDboToDto(EmpWorkDiaryMainActivityDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getMainActivityName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getMainActivityName());
		}
		return dto;
	}

	public Flux<SelectDTO> getActivity(String userId) {
		return commonEmployeeTransaction.getActivity(Integer.parseInt(userId)).flatMapMany(Flux::fromIterable).map(this::convertEmpWorkDiaryActivityDboToDto);
	}	

	public SelectDTO convertEmpWorkDiaryActivityDboToDto(EmpWorkDiaryActivityDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getActivityName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getActivityName());
		}
		return dto;
	}
	public Flux<SelectDTO> getLeaveTypeForLeaveApplication(Map<String, String> requestParams) {
		return commonEmployeeTransaction.getLeaveTypeForLeaveApplication(requestParams).flatMapMany(Flux::fromIterable).map(this::convertdboToDto);
	}
	public SelectDTO convertdboToDto(Tuple mapping) {
		SelectDTO itemInfo = new SelectDTO();
		itemInfo.setValue( !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "");
		itemInfo.setLabel( !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "");
		return itemInfo;
	} 	

	public EmpDBO copyApplnDataToEmpData(int applnentriesid, String userId) {
		EmpApplnEntriesDBO empApplnEntriesDBO;
		EmpDBO empDBO = new EmpDBO();
//		boolean isTrue = commonEmployeeTransaction.duplicateCheck(applnentriesid);
//		if(isTrue) {
//			ApiResult result = new ApiResult();
//			result.success = false;
//			result.failureMessage = "Employee Already Exists ";
//			EmpDBO empDB = null;
//			return empDB;
//		} else {
			empApplnEntriesDBO = commonEmployeeTransaction.getApplnData(applnentriesid);
			if(!Utils.isNullOrEmpty(empApplnEntriesDBO)) {
				empDBO.setEmpName(empApplnEntriesDBO.getApplicantName());
				empDBO.setEmpApplnEntriesDBO(empApplnEntriesDBO);
				if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getDob())){
					empDBO.setEmpDOB(empApplnEntriesDBO.getDob());
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getErpGenderDBO())) {
					empDBO.setErpGenderDBO(empApplnEntriesDBO.getErpGenderDBO());
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getPersonalEmailId())) {
					empDBO.setEmpPersonalEmail(empApplnEntriesDBO.getPersonalEmailId());
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getMobileNoCountryCode())) {
					empDBO.setCountryCode(empApplnEntriesDBO.getMobileNoCountryCode());
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getMobileNo())) {
					empDBO.setEmpMobile(empApplnEntriesDBO.getMobileNo());
				}
			}
			if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnEducationalDetailsDBOs())) {
				empApplnEntriesDBO.getEmpApplnEducationalDetailsDBOs().forEach(applnEducationDetails -> {
					if(applnEducationDetails.getRecordStatus()== 'A') {
						EmpEducationalDetailsDBO empEducationaldetailsDBO = new EmpEducationalDetailsDBO();
						BeanUtils.copyProperties(applnEducationDetails, empEducationaldetailsDBO);
						empEducationaldetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						empEducationaldetailsDBO.setModifiedUsersId(null);
						empEducationaldetailsDBO.setEmpDBO(empDBO);
						empEducationaldetailsDBO.setEmpApplnEntriesDBO(empApplnEntriesDBO);
						if(!Utils.isNullOrEmpty(applnEducationDetails.getDocumentsDBOSet())) {
							EmpEducationalDetailsDocumentsDBO empdocumentdbo = new EmpEducationalDetailsDocumentsDBO();
							applnEducationDetails.getDocumentsDBOSet().forEach(applndocumentDbo -> {
								if(applndocumentDbo.getRecordStatus()== 'A') {
									empdocumentdbo.setEmpEducationalDetailsDBO(empEducationaldetailsDBO);
									if(!Utils.isNullOrEmpty(applndocumentDbo.getEducationalDocumentsUrl())) {
										empdocumentdbo.setEducationalDocumentsUrl(applndocumentDbo.getEducationalDocumentsUrl());
									}
									//-------jismy
									if(!Utils.isNullOrEmpty(applndocumentDbo.getEducationalDocumentsUrlDBO())) {
										empdocumentdbo.setEducationalDocumentsUrlDBO(applndocumentDbo.getEducationalDocumentsUrlDBO());
									}
									empdocumentdbo.setCreatedUsersId(Integer.parseInt(userId));
									empdocumentdbo.setModifiedUsersId(null);
									empdocumentdbo.setRecordStatus('A');
									empEducationaldetailsDBO.getDocumentsDBOSet().add(empdocumentdbo);
								}
							});
						} 
						empDBO.getEmpEducationalDetailsDBOSet().add(empEducationaldetailsDBO);
					}
				});
			}
			if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnEligibilityTestDBOs())) {
				empApplnEntriesDBO.getEmpApplnEligibilityTestDBOs().forEach(applnEligibilityDetails -> {
					if(applnEligibilityDetails.getRecordStatus()== 'A') {
						EmpEligibilityTestDBO empEligibilityTestDBO = new EmpEligibilityTestDBO();
						BeanUtils.copyProperties(applnEligibilityDetails, empEligibilityTestDBO);
						empEligibilityTestDBO.setCreatedUsersId(Integer.parseInt(userId));
						empEligibilityTestDBO.setModifiedUsersId(null);
						empEligibilityTestDBO.setEmpDBO(empDBO);
						empEligibilityTestDBO.setEmpApplnEntriesDBO(empApplnEntriesDBO);
						if(!Utils.isNullOrEmpty(applnEligibilityDetails.getEligibilityTestDocumentDBOSet())) {
							applnEligibilityDetails.getEligibilityTestDocumentDBOSet().forEach(applnEligibilityDocumentDBO ->{
								if(applnEligibilityDocumentDBO.getRecordStatus()== 'A') {
									EmpEligibilityTestDocumentDBO empEligibilityTestDocumentDBO = new EmpEligibilityTestDocumentDBO();
									empEligibilityTestDocumentDBO.setEmpEligibilityTestDBO(empEligibilityTestDBO);
									if(!Utils.isNullOrEmpty(applnEligibilityDocumentDBO.getEligibilityDocumentUrl())) {
										empEligibilityTestDocumentDBO.setEligibilityDocumentUrl(applnEligibilityDocumentDBO.getEligibilityDocumentUrl());
									}
									if(!Utils.isNullOrEmpty(applnEligibilityDocumentDBO.getEligibilityDocumentUrlDBO())) {
										empEligibilityTestDocumentDBO.setEligibilityDocumentUrlDBO(applnEligibilityDocumentDBO.getEligibilityDocumentUrlDBO());
									}
									empEligibilityTestDocumentDBO.setCreatedUsersId(Integer.parseInt(userId));
									empEligibilityTestDocumentDBO.setModifiedUsersId(null);
									empEligibilityTestDocumentDBO.setRecordStatus('A');
									empEligibilityTestDBO.getEmpEligibilityTestDocumentDBOSet().add(empEligibilityTestDocumentDBO);
								}
							});
						}
						empDBO.getEmpEligibilityTestDBOSet().add(empEligibilityTestDBO);
					}
				});
			}
			if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnWorkExperienceDBOs())) {
				empApplnEntriesDBO.getEmpApplnWorkExperienceDBOs().forEach(applnWorkExperienceDetails -> {
					if(applnWorkExperienceDetails.getRecordStatus()== 'A') {
						EmpWorkExperienceDBO empWorkExperienceDBO = new EmpWorkExperienceDBO();
						BeanUtils.copyProperties(applnWorkExperienceDetails, empWorkExperienceDBO);
						empWorkExperienceDBO.setCreatedUsersId(Integer.parseInt(userId));
						empWorkExperienceDBO.setModifiedUsersId(null);
						empWorkExperienceDBO.setEmpDBO(empDBO);
						empWorkExperienceDBO.setEmpApplnEntriesDBO(empApplnEntriesDBO);
						if(!Utils.isNullOrEmpty(applnWorkExperienceDetails.getWorkExperienceDocumentsDBOSet())) {
							applnWorkExperienceDetails.getWorkExperienceDocumentsDBOSet().forEach(applnWorkExperienceDocumentDBO -> {
								if(applnWorkExperienceDocumentDBO.getRecordStatus() == 'A') {
									EmpWorkExperienceDocumentDBO empWorkExperienceDocumentDBO = new EmpWorkExperienceDocumentDBO();
									empWorkExperienceDocumentDBO.setEmpWorkExperienceDBO(empWorkExperienceDBO);
									if(!Utils.isNullOrEmpty(applnWorkExperienceDocumentDBO.getExperienceDocumentsUrl())) {
										empWorkExperienceDocumentDBO.setExperienceDocumentsUrl(applnWorkExperienceDocumentDBO.getExperienceDocumentsUrl());
									}
									if(!Utils.isNullOrEmpty(applnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
										empWorkExperienceDocumentDBO.setExperienceDocumentsUrlDBO(applnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO());
									}
									empWorkExperienceDocumentDBO.setCreatedUsersId(Integer.parseInt(userId));
									empWorkExperienceDocumentDBO.setModifiedUsersId(null);
									empWorkExperienceDocumentDBO.setRecordStatus('A');
									empWorkExperienceDBO.getWorkExperienceDocumentsDBOSet().add(empWorkExperienceDocumentDBO);
								}
							});
						}
						empDBO.getEmpWorkExperienceDBOSet().add(empWorkExperienceDBO);
					}
				});
			}
			if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO())) {
				EmpApplnPersonalDataDBO empApplnPersonalDataDBO = empApplnEntriesDBO.getEmpApplnPersonalDataDBO();
				if(empApplnPersonalDataDBO.getRecordStatus()== 'A') {
					EmpPersonalDataDBO empPersonalDataDBO = new EmpPersonalDataDBO();
					BeanUtils.copyProperties(empApplnPersonalDataDBO, empPersonalDataDBO);
					empPersonalDataDBO.setCreatedUsersId(Integer.parseInt(userId));
					empPersonalDataDBO.setModifiedUsersId(null);
					if(!Utils.isNullOrEmpty(empApplnPersonalDataDBO.getEmpFamilyDetailsAddtnlDBOS())) {
						empPersonalDataDBO.setEmpFamilyDetailsAddtnlDBOS(new HashSet<EmpFamilyDetailsAddtnlDBO>());
						empApplnPersonalDataDBO.getEmpFamilyDetailsAddtnlDBOS().forEach(applnfamilyDetailsAddtnl -> {
							if(applnfamilyDetailsAddtnl.getRecordStatus() == 'A') {
								EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
								BeanUtils.copyProperties(applnfamilyDetailsAddtnl, empFamilyDetailsAddtnlDBO);
								empFamilyDetailsAddtnlDBO.setEmpApplnPersonalDataDBO(new EmpApplnPersonalDataDBO());
								empFamilyDetailsAddtnlDBO.setEmpApplnPersonalDataDBO(empApplnPersonalDataDBO);
								empFamilyDetailsAddtnlDBO.setEmpPersonalDataDBO(new EmpPersonalDataDBO());
								empFamilyDetailsAddtnlDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
								empFamilyDetailsAddtnlDBO.setCreatedUsersId(Integer.parseInt(userId));
								empFamilyDetailsAddtnlDBO.setModifiedUsersId(null);
								empFamilyDetailsAddtnlDBO.setRecordStatus('A');
								empPersonalDataDBO.getEmpFamilyDetailsAddtnlDBOS().add(empFamilyDetailsAddtnlDBO);	
							}
						});
					}
					empPersonalDataDBO.setRecordStatus('A');
					empDBO.setEmpPersonalDataDBO(empPersonalDataDBO);
				}
			}
			if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpJobDetailsDBO())) {
				EmpJobDetailsDBO empJobDetailsDBO = empApplnEntriesDBO.getEmpJobDetailsDBO();
				if(empJobDetailsDBO.getRecordStatus()== 'A') {
					empJobDetailsDBO.setEmpDBO(empDBO);
					empJobDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
					empJobDetailsDBO.setEmpApplnEntriesId(empApplnEntriesDBO);
					empDBO.setEmpJobDetailsDBO(empJobDetailsDBO);					
				}
//			}
		}
		return empDBO;
	}
	public Flux<SelectDTO> getEmployeeListWithDepartment() {
		return commonEmployeeTransaction.getEmployeeWithDepartment().flatMapMany(Flux::fromIterable).map(this::convertEmpDepDboToDto);
	}
	public SelectDTO convertEmpDepDboToDto(EmpDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		StringBuffer empDepString = new StringBuffer("");
		empDepString.append(dbo.getEmpName());
		if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO()) && !Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO())) {
			empDepString.append("(" + dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName() + ")");
		}
		dto.setLabel(empDepString.toString());
		return dto;
	}

	public  Flux<SelectDTO> getCampusDepartmentConcat() {
		return commonEmployeeTransaction.getCampusDepartmentConcat().flatMapMany(Flux::fromIterable).map(this::campusDepartmentConcat);
	}

	public SelectDTO campusDepartmentConcat(ErpCampusDepartmentMappingDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getErpCampusDBO().getCampusName().concat(" (").concat(dbo.getErpDepartmentDBO().getDepartmentName()).concat(")"));
		return dto;
	}

	public Flux<SelectDTO> getCampusByDepartmentId(String deptId) {
		return commonEmployeeTransaction.getCampusByDepartmentId(deptId).flatMapMany(Flux::fromIterable).map(this::campusDepartmentId);
	}

	public SelectDTO campusDepartmentId(ErpCampusDepartmentMappingDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.erpCampusDBO.getId()));
		dto.setLabel(dbo.getErpCampusDBO().getCampusName());
		return dto;
	}

	public Mono<EmpApplnAdvertisementDTO> getEmployeeAapplicationAdvertisement() {
		return commonEmployeeTransaction.getEmployeeAapplicationAdvertisement(LocalDate.now()).map(this::convertEmpDepDboToDto);
	}

	public EmpApplnAdvertisementDTO convertEmpDepDboToDto(EmpApplnAdvertisementDBO dbo) {
		EmpApplnAdvertisementDTO dto = new EmpApplnAdvertisementDTO();
		dto.setId(dbo.getId().toString());
		dto.setAdvertisementNo(dbo.getAdvertisementNo());
		dto.setAdvertisementContent(dbo.getAdvertisementContent());
		dto.setOtherInfo(dbo.getOtherInfo());
		dto.setEmpApplnAdvertisementImages(new ArrayList<EmpApplnAdvertisementImagesDTO>());
		dbo.getEmpApplnAdvertisementImagesSet().forEach( img -> {
			EmpApplnAdvertisementImagesDTO imgDto = new EmpApplnAdvertisementImagesDTO();
			imgDto.setId(img.getId().toString());
			imgDto.setUrl(img.getUploadAdvertisementUrl());
			dto.getEmpApplnAdvertisementImages().add(imgDto);
		});
		return dto;
	}
	
	public Flux<SelectDTO> getEmployeeIdBySearch(String employeeName){
		return commonEmployeeTransaction.getEmployeeIdBySearch(employeeName).flatMapMany(Flux::fromIterable).map(this::convertEmpDBOToDTO2);
	}
	
	public SelectDTO convertEmpDBOToDTO1(EmpDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getEmpName());
		return dto;
	}

	public Flux<SelectDTO> getEmployeeNameBySearch(String employeeName){
		return commonEmployeeTransaction.getEmployeeNameBySearch(employeeName).flatMapMany(Flux::fromIterable).map(this::convertEmpDBOToDTO1);
	}
	
	public SelectDTO convertEmpDBOToDTO2(EmpDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getEmpNumber()));
		dto.setLabel(String.valueOf(dbo.getEmpNumber()));
		return dto;
	}

	public Flux<SelectDTO> getPayScaleLevel() {
		return commonEmployeeTransaction.getPayScaleLevel().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto (EmpPayScaleLevelDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getEmpPayScaleLevel());
		return dto;
	}

	public Flux<List<SelectDTO>> empPayScaleByEmpCategory(String empCategoryId) {
		List<EmpPayScaleGradeMappingDetailDBO> empPayScaleGradeMappingDetailDBO = commonEmployeeTransaction.empPayScaleByEmpCategory(empCategoryId);
		return this.convertEmpPayScaleDboToDto(empPayScaleGradeMappingDetailDBO);
	}

	public Flux<List<SelectDTO>> convertEmpPayScaleDboToDto(List<EmpPayScaleGradeMappingDetailDBO> empPayScaleGradeMappingDetailDBO) {
		List<SelectDTO> list = new ArrayList<SelectDTO>();
		Map<Integer,String> map = new HashedMap<Integer, String>();
		if(!Utils.isNullOrEmpty(empPayScaleGradeMappingDetailDBO)) {
			empPayScaleGradeMappingDetailDBO.forEach(ele -> {
				SelectDTO selectDTO = new SelectDTO();
				if(!Utils.isNullOrEmpty(ele.getEmpPayScaleLevelDBO())) {
					if(!map.containsKey(ele.getId())) {
					selectDTO.setValue(ele.getId().toString());
					selectDTO.setLabel(ele.getEmpPayScaleLevelDBO().getEmpPayScaleLevel());
					map.put(ele.getId(), ele.getEmpPayScaleLevelDBO().getEmpPayScaleLevel());
					list.add(selectDTO);
					}
				}	
			});	
		}
		return Flux.just(list);		
	}

	public Mono<EmpApplnAdvertisementDTO> getEmpApplicationAdvertisement() {
		EmpApplnAdvertisementDBO list = null;
		list = commonEmployeeTransaction.getEmpApplicationAdvertisement(LocalDate.now());
		if(Utils.isNullOrEmpty(list)) {
			list = commonEmployeeTransaction.getEmpAdvertisements();
		}
		return this.convertEmpDboToDto(list);
	}

	public Mono<EmpApplnAdvertisementDTO> convertEmpDboToDto(EmpApplnAdvertisementDBO dbo) {
		EmpApplnAdvertisementDTO dto = new EmpApplnAdvertisementDTO();
		if(!Utils.isNullOrEmpty(dbo)){
			dto.setId(dbo.getId().toString());
			if(!Utils.isNullOrEmpty(dbo.getAdvertisementNo())) {
				dto.setAdvertisementNo(dbo.getAdvertisementNo());
			}
			if(!Utils.isNullOrEmpty(dbo.getAdvertisementContent())) {
				dto.setAdvertisementContent(dbo.getAdvertisementContent());
			}
			if(!Utils.isNullOrEmpty(dbo.getOtherInfo())) {
				dto.setOtherInfo(dbo.getOtherInfo());
			}
		}
		return Mono.just(dto);
	}
	
	public Flux<SelectDTO> getEmployeeWithTitle(){
		List<Tuple> list = commonEmployeeTransaction.getEmployeeWithTitle();
		return this.convertEmpDBOToDTO(list);
	}

	private Flux<SelectDTO> convertEmpDBOToDTO(List<Tuple> list) {
		List<SelectDTO> empSelectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
			list.forEach(empList -> {
				SelectDTO selectDTO = new SelectDTO();
				if(!Utils.isNullOrEmpty(empList.get("title_name"))) {
					selectDTO.setValue(empList.get("emp_id").toString());
					selectDTO.setLabel(empList.get("emp_name").toString() + " (" +empList.get("title_name").toString()+ " )");
					selectDTOList.add(selectDTO);
				}
			});
			empSelectDTOList.addAll(selectDTOList);
		}
		return Flux.fromIterable(empSelectDTOList);
	}
	

	public Mono<List<SelectDTO>> getEmployeeIdOrName(String employeeIdOrName, Boolean isNumber) {
		 List<Tuple> values = commonEmployeeTransaction.getEmployeeIdOrName(employeeIdOrName);
		return this.convertDboToDto(values,isNumber);
	}
	
	public Mono<List<SelectDTO>> convertDboToDto (List<Tuple> dbos, Boolean isNumber) {
		List<SelectDTO> dtos = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(dbos)) {
			dbos.forEach(dbo -> {
				if(isNumber) {
					SelectDTO dto = new SelectDTO();
					dto.setValue(dbo.get("id").toString());
					dto.setLabel(dbo.get("empId").toString()+"("+dbo.get("name")+")");
					dtos.add(dto);
				} else {
					SelectDTO dto = new SelectDTO();
					dto.setValue(dbo.get("id").toString());
					dto.setLabel(dbo.get("name")+"("+dbo.get("empId")+")");
					dtos.add(dto);
				}
			});
		}
		return Mono.just(dtos);
	}
	
	public Flux<SelectDTO> getEmpDesignation() {
		return commonEmployeeTransaction.getEmpDesignation().flatMapMany(Flux::fromIterable).map(this::convertTemplateDBOToDTO);
	}
	
	public SelectDTO convertTemplateDBOToDTO(EmpDesignationDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getEmpDesignationName());
		return dto;
	}
	
	public Flux<SelectDTO> getDepartmentsByDeanery(String deaneryId) {
		return commonEmployeeTransaction.getDepartmentsByDeanery(deaneryId).flatMapMany(Flux::fromIterable).map(this::convertTemplateDBOToDTO);
	}
	
	public SelectDTO convertTemplateDBOToDTO(ErpDepartmentDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getDepartmentName());
		return dto;
	}

	public Flux<SelectDTO> getResignationReason() {
		return commonEmployeeTransaction.getResignationReason().flatMapMany(Flux::fromIterable);
	}
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> duplicateCheckForEmployee(Integer applnNo) {
		return commonEmployeeTransaction.duplicateCheckForEmployee(applnNo).map(this::duplicateStatus);
	}
	
	@SuppressWarnings("rawtypes")
	public ApiResult duplicateStatus(String status) {
		var apiResult = new ApiResult();
		if(!Utils.isNullOrEmpty(status)) {
			if(status.equalsIgnoreCase("I")) {
				apiResult.setSuccess(false);
				apiResult.setFailureMessage("Employee is already created and is inactive. Go to 'Create Employee ID'");
			} else if(status.equalsIgnoreCase("A")) {
				apiResult.setSuccess(false);
				apiResult.setFailureMessage("Employee is already created. Go to 'Employee profile'");
			}		
		} else {
			apiResult.setSuccess(true);
		}		
		return apiResult;
	}

	public Flux<SelectDTO> getRevisedYear(String gradeId) {
		return commonEmployeeTransaction.getRevisedYear(Integer.valueOf(gradeId)).flatMapMany(Flux::fromIterable).filter(s->!Utils.isNullOrEmpty(s.getLabel()) || !Utils.isNullOrEmpty(s.getValue()));
	}

	public Mono<EmpApplnEntriesDTO> getApplicantDataAndTimeLine(Integer applnNo) {
		var data = commonEmployeeTransaction.getApplicantDataAndTimeLine(applnNo);
		var dto = !Utils.isNullOrEmpty(data)? convertTupleToDto(data) : null;		
		return !Utils.isNullOrEmpty(dto) ? Mono.just(dto) : Mono.empty();
	}
	
	private EmpApplnEntriesDTO convertTupleToDto(Tuple tuple) {
		EmpApplnEntriesDTO dto = new EmpApplnEntriesDTO();
		if(!Utils.isNullOrEmpty(tuple)) {
			List<WorkFlowStatusApplicantTimeLineDTO>  statusLog = null;
			if(!Utils.isNullOrEmpty(tuple.get("emp_appln_entries_id"))) {
				statusLog = commonEmployeeTransaction.getWorkFlowStatusLog(Integer.valueOf(String.valueOf(tuple.get("emp_appln_entries_id"))));
			}
			if(!Utils.isNullOrEmpty(tuple.get("application_no"))) {
				dto.setApplicantNumber(String.valueOf(tuple.get("application_no")));
			}
			if(!Utils.isNullOrEmpty(tuple.get("applicant_name"))) {
				dto.setApplicantName(String.valueOf(tuple.get("applicant_name")));
			}
			if(!Utils.isNullOrEmpty(tuple.get("file_name_unique")) && !Utils.isNullOrEmpty(tuple.get("file_name_original")) && !Utils.isNullOrEmpty(tuple.get("upload_process_code"))) {
//				dto.setProfilePhoto(new FileUploadDownloadDTO());
//				dto.getProfilePhoto().setOriginalFileName(String.valueOf(tuple.get("file_name_original")));
//				dto.getProfilePhoto().setUniqueFileName(String.valueOf(tuple.get("file_name_unique")));
				FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
				fileUploadDownloadDTO.setActualPath(tuple.get("file_name_unique").toString());
				fileUploadDownloadDTO.setProcessCode(tuple.get("upload_process_code").toString());
				fileUploadDownloadDTO.setOriginalFileName(tuple.get("file_name_original").toString());
				aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
				dto.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
			}
			if(!Utils.isNullOrEmpty(tuple.get("employee_category_name"))) {
				dto.setEmployeeCategoryName((String.valueOf(tuple.get("employee_category_name"))));
			}
			if(!Utils.isNullOrEmpty(tuple.get("is_employee_category_academic"))) {
				dto.setIsCategoryAcademic(Boolean.valueOf(String.valueOf(tuple.get("is_employee_category_academic"))));
			}
			if(!Utils.isNullOrEmpty(tuple.get("qualification_level_name"))) {
				dto.setHighestQualificationLevel(String.valueOf(tuple.get("qualification_level_name")));
			}
			if(!Utils.isNullOrEmpty(tuple.get("experience"))) {
				dto.setTotalFullTimeExperience(String.valueOf(tuple.get("experience")));
			}
			if(!Utils.isNullOrEmpty(tuple.get("subjectCategorySpecializationName"))) {
				dto.setSubjectCategorySpecializationName(String.valueOf(tuple.get("subjectCategorySpecializationName")));
			}
			if(!Utils.isNullOrEmpty(tuple.get("subject_category_name"))) {
				dto.setSubjectCategoryName(String.valueOf(tuple.get("subject_category_name")));
			}
			if(!Utils.isNullOrEmpty(tuple.get("department_name"))) {
				dto.setShortlistedDepartment(new SelectDTO());
				dto.getShortlistedDepartment().setLabel(String.valueOf(tuple.get("department_name")));
			}
			if(!Utils.isNullOrEmpty(tuple.get("location_name"))) {
				dto.setLocationPreference(String.valueOf(tuple.get("location_name")));
			}
			if(!Utils.isNullOrEmpty(statusLog)) {
				//statusLog.stream().sorted(null)
				Collections.sort(statusLog, Comparator.comparing(s -> s.getStatusLogCreatedTime(),
		                Comparator.nullsLast(Comparator.naturalOrder()))
		        );
//				Collections.sort(statusLog, Comparator.comparing(s-> !Utils.isNullOrEmpty(s.getStatusLogCreatedTime())? s.getStatusLogCreatedTime() :  Long.MIN_VALUE));
			dto.setWorkFlowTimeLine(statusLog);
			}
		}		
		return dto;
	}
	
	public Flux<SelectDTO> getCampusForLocation(List<Integer> locId) {
		List<ErpCampusDBO> list = commonEmployeeTransaction.getCampusForLocation(locId); 
		return convertDboToDto(list);
	}
	private Flux<SelectDTO> convertDboToDto(List<ErpCampusDBO> list ) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setLabel(data.campusName);
				selectDTO.setValue(String.valueOf(data.getId()));
				selectDTOList.add(selectDTO);
			});
		}
		return Flux.fromIterable(selectDTOList);
	}

	public Flux<SelectDTO> getCampusForLocationsAndUser(List<Integer> locId,String userId) {
		List<Tuple> list = commonEmployeeTransaction.getCampusForLocationsAndUser(locId,userId); 
		return convertDboToDtoCampus(list);
	}
	
	private Flux<SelectDTO> convertDboToDtoCampus(List<Tuple> list ) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(String.valueOf(data.get("id")));
				selectDTO.setLabel(String.valueOf(data.get("name")));
				selectDTOList.add(selectDTO);
			});
		}
		
		return Flux.fromIterable(selectDTOList);
	}
	
	public Flux<SelectDTO> getDepartmentsByCampusIds(List<Integer> campusIds) {
		List<Tuple> list = commonEmployeeTransaction.getDepartmentsByCampusIds(campusIds); 
		return convertDboToDtoDepart(list);
	}
	
	private Flux<SelectDTO> convertDboToDtoDepart(List<Tuple> list ) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(String.valueOf(data.get("id")));
				selectDTO.setLabel(String.valueOf(data.get("departName")));
				selectDTOList.add(selectDTO);
			});
		}
		
		return Flux.fromIterable(selectDTOList);
	}
	public Flux<SelectDTO> getAllSubjectAndCategory() {
		return commonEmployeeTransaction.getAllSubjectAndCategory().flatMapMany(Flux::fromIterable);
	}
	public Flux<SelectDTO> getAllSubjectCategorySpecialisation() {
		return commonEmployeeTransaction.getAllSubjectCategorySpecialisation().flatMapMany(Flux::fromIterable);
	}
	public Flux<SelectDTO> getLetterTypes() {
		return commonEmployeeTransaction.getLetterTypes().flatMapMany(Flux::fromIterable);
	}
	public Flux<SelectDTO> getTimeZoneByCategory(int categoryId,String isSelected) {
		return commonEmployeeTransaction.getTimeZoneByCategory(categoryId,isSelected).flatMapMany(Flux::fromIterable);

	}
	public Mono<List<EmpProfileComponentMapDTO>> getJobCategoryComponentMapping(Integer jobCategoryId) {
		return commonEmployeeTransaction.getJobCategoryComponentMapping(jobCategoryId);
	}
	public Mono<List<EmpProfileComponentMapDTO>> getJobCategoryComponentGroupMapping(Integer jobCategoryId) {
		return commonEmployeeTransaction.getJobCategoryComponentGroupMapping(jobCategoryId);
	}
	public Mono<Integer> getEmpIdByUserId(Integer userId) {
		return commonEmployeeTransaction.getEmpIdByUserId(userId);
	}
}