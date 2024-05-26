package com.christ.erp.services.helpers.employee.recruitment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleSubDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDesignationDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;
import com.christ.erp.services.dbobjects.employee.common.ErpEmployeeTitleDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnDignitariesFeedbackDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategoryDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategorySpecializationDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpDailyWageSlabDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleMatrixDetailDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.EmpPositionRoleDTO;
import com.christ.erp.services.dto.employee.EmpPositionRoleSubDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnEducationalDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewTemplateGroupDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewTemplateGroupDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.FinalInterviewCommentsDTO;
import com.christ.erp.services.dto.employee.recruitment.InteviewScoreEntryGroupDetailsDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDetailsDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.recruitment.FinalInterviewCommentsTransaction;

@Service
public class FinalInterviewCommentsHelper {

	public static volatile FinalInterviewCommentsHelper finalInterviewCommentsHelper = null;
	public static FinalInterviewCommentsHelper getInstance() {
		if(finalInterviewCommentsHelper== null) 
			finalInterviewCommentsHelper =  new FinalInterviewCommentsHelper();
		return finalInterviewCommentsHelper;
	}

	@Autowired
	FinalInterviewCommentsTransaction finalInterviewCommentsTransaction1;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	@Autowired
	CommonApiHandler commonApiHandler;

	public EmpApplnEntriesDBO convertEmpApplnEntriesDTOToDBO(EmpApplnEntriesDBO applnEntriesDBO,FinalInterviewCommentsDTO finalInterviewCommentsDTO,List<EmpApplnDignitariesFeedbackDBO> applnDignitariesFeedbackDBOs,String userId,List<Object> objects) {
		//		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.isSelected)) {
		//			applnEntriesDBO.isSelected = finalInterviewCommentsDTO.isSelected;
		//			if(finalInterviewCommentsDTO.isSelected) {
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.campus.id)) {
			ErpCampusDBO campusDBO = new ErpCampusDBO();
			campusDBO.id = Integer.parseInt(finalInterviewCommentsDTO.campus.id);
			applnEntriesDBO.erpCampusDBO = campusDBO;
		}else {
			applnEntriesDBO.erpCampusDBO = null;
		}
		if (!Utils.isNullOrEmpty(finalInterviewCommentsDTO.getCampusDeptDto())) {
			if (!Utils.isNullOrEmpty(finalInterviewCommentsDTO.getCampusDeptDto().getValue())) {
				applnEntriesDBO.setErpCampusDepartmentMappingDBO(new ErpCampusDepartmentMappingDBO());  
				applnEntriesDBO.getErpCampusDepartmentMappingDBO().setId(Integer.valueOf(finalInterviewCommentsDTO.getCampusDeptDto().getValue()));
				if (Utils.isNullOrEmpty(applnEntriesDBO.getShortlistedDepartmentId()) && Utils.isNullOrEmpty(applnEntriesDBO.getShortistedLocationId()) ) {
					Tuple locationDeptId = finalInterviewCommentsTransaction1.getLocationDept(Integer.valueOf(finalInterviewCommentsDTO.getCampusDeptDto().getValue()));
					if (!Utils.isNullOrEmpty(locationDeptId)) {
						if (!Utils.isNullOrEmpty(locationDeptId.get("erp_department_id"))) {
							applnEntriesDBO.setShortlistedDepartmentId(new ErpDepartmentDBO());
							applnEntriesDBO.getShortlistedDepartmentId().setId(Integer.valueOf(locationDeptId.get("erp_department_id").toString()));
						}
						if (!Utils.isNullOrEmpty(locationDeptId.get("erp_location_id"))) {
							applnEntriesDBO.setShortistedLocationId(new ErpLocationDBO());
							applnEntriesDBO.getShortistedLocationId().setId(Integer.valueOf(locationDeptId.get("erp_location_id").toString()));
						}
					}
				}
			}
		}
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.category.id)) {
			EmpEmployeeCategoryDBO empEmployeeCategoryDBO = new EmpEmployeeCategoryDBO();
			empEmployeeCategoryDBO.id = Integer.parseInt(finalInterviewCommentsDTO.category.id);
			applnEntriesDBO.empEmployeeCategoryDBO = empEmployeeCategoryDBO;
		}else {
			applnEntriesDBO.empEmployeeCategoryDBO = null;
		}
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.jobCategory.id)) {
			EmpEmployeeJobCategoryDBO employeeJobCategoryDBO = new EmpEmployeeJobCategoryDBO();
			employeeJobCategoryDBO.id = Integer.parseInt(finalInterviewCommentsDTO.jobCategory.id);
			applnEntriesDBO.empEmployeeJobCategoryDBO = employeeJobCategoryDBO;
		}else {
			applnEntriesDBO.empEmployeeJobCategoryDBO = null;
		}
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.designation.id)) {
			EmpDesignationDBO empDesignationDBO = new EmpDesignationDBO();
			empDesignationDBO.id = Integer.parseInt(finalInterviewCommentsDTO.designation.id);
			applnEntriesDBO.empDesignationDBO = empDesignationDBO;
		}else {
			applnEntriesDBO.empDesignationDBO = null;
		}
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.empTitle.id)) {
			ErpEmployeeTitleDBO employeeTitleDBO = new ErpEmployeeTitleDBO();
			employeeTitleDBO.id = Integer.parseInt(finalInterviewCommentsDTO.empTitle.id);
			applnEntriesDBO.titleId = employeeTitleDBO;
		}else {
			applnEntriesDBO.titleId = null;
		}
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.subjectCategory.id)) {
			EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
			empApplnSubjectCategoryDBO.id = Integer.parseInt(finalInterviewCommentsDTO.subjectCategory.id);
			applnEntriesDBO.empApplnSubjectCategoryDBO =empApplnSubjectCategoryDBO;
		}else {
			applnEntriesDBO.empApplnSubjectCategoryDBO = null;
		}
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.subjectCategorySpecialization.id)) {
			EmpApplnSubjectCategorySpecializationDBO applnSubjectCategorySpecializationDBO = new EmpApplnSubjectCategorySpecializationDBO();
			applnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId = Integer.parseInt(finalInterviewCommentsDTO.subjectCategorySpecialization.id);
			applnEntriesDBO.empApplnSubjectCategorySpecializationDBO = applnSubjectCategorySpecializationDBO;
		}else {
			applnEntriesDBO.empApplnSubjectCategorySpecializationDBO = null;
		}
		Set<EmpApplnDignitariesFeedbackDBO> dignitariesFeedbackDBOs = new HashSet<>();
		Set<EmpApplnDignitariesFeedbackDBO> deleteSet = null;
		if(!Utils.isNullOrEmpty(applnDignitariesFeedbackDBOs))
			deleteSet = new HashSet<EmpApplnDignitariesFeedbackDBO>(applnDignitariesFeedbackDBOs);
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.processAndPosition))
			for(EmpPositionRoleSubDTO item : finalInterviewCommentsDTO.processAndPosition) {
				EmpApplnDignitariesFeedbackDBO detail = null;
				if(deleteSet != null) {
					for(EmpApplnDignitariesFeedbackDBO bo : deleteSet) {
						if(Utils.isNullOrWhitespace(item.id) == false) {
							if(Integer.parseInt(item.id) == bo.id) {
								detail = bo;
								deleteSet.remove(bo);
								break;
							}
						} else if(Utils.isNullOrWhitespace(item.id)) {
							deleteSet.add(bo);
							break;
						}
					}
				}
				if(detail == null) {
					detail = new EmpApplnDignitariesFeedbackDBO();
					detail.createdUsersId = Integer.parseInt(userId);
					detail.recordStatus = 'A';
				} else {
					detail.modifiedUsersId = Integer.parseInt(userId);
				}
				detail.empApplnEntriesDBO = applnEntriesDBO;
				EmpDBO empDBO = new EmpDBO();
				empDBO.id = Integer.parseInt(item.employee.id);
				detail.empId = empDBO;
				ErpEmployeeTitleDBO title = new ErpEmployeeTitleDBO();
				title.id = Integer.parseInt(item.empTitle.id);
				detail.empTitleId = title;
				detail.dignitariesFeedback = item.comment;
				detail.recordStatus = 'A';
				dignitariesFeedbackDBOs.add(detail);
			}
		if(deleteSet != null) {
			for(EmpApplnDignitariesFeedbackDBO bo : deleteSet) {
				bo.recordStatus = 'D';
				bo.modifiedUsersId = Integer.parseInt(userId);
				dignitariesFeedbackDBOs.add(bo);
			}
		}
		applnEntriesDBO.applnDignitariesFeedbackDBOs = dignitariesFeedbackDBOs;
		//				applnEntriesDBO = this.templateContent(finalInterviewCommentsDTO, applnEntriesDBO,userId,objects);
		//			}else if(!Utils.isNullOrEmpty(applnEntriesDBO.offerLetterUrl)){
		//				File deleteOfferLetterFile = new File(applnEntriesDBO.offerLetterUrl); 
		//				deleteOfferLetterFile.delete();
		//				applnEntriesDBO.offerLetterUrl = null;
		//				applnEntriesDBO.offerLetterGeneratedDate = null;
		//			}else if(!finalInterviewCommentsDTO.isSelected) {
		//				if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.getOfferLetterTemplate())) {
		//					applnEntriesDBO = this.templateContent(finalInterviewCommentsDTO, applnEntriesDBO,userId,objects);
		//				}
		//			}
		//		}
		return applnEntriesDBO;
	}

	public EmpJobDetailsDBO convertEmpJobDetailsDTOToDBO(EmpApplnEntriesDBO applnEntriesDBO,EmpJobDetailsDBO jobDetailsDBO,FinalInterviewCommentsDTO finalInterviewCommentsDTO,String userId) {
		if(jobDetailsDBO == null) {
			jobDetailsDBO = new EmpJobDetailsDBO();
			jobDetailsDBO.createdUsersId = Integer.parseInt(userId);
		}else {
			jobDetailsDBO.modifiedUsersId = Integer.parseInt(userId);
		}
		jobDetailsDBO.empApplnEntriesId = applnEntriesDBO;
		jobDetailsDBO.isWithPf = !Utils.isNullOrEmpty(finalInterviewCommentsDTO.withPF)?finalInterviewCommentsDTO.withPF: null;
		jobDetailsDBO.isWithGratuity = !Utils.isNullOrEmpty(finalInterviewCommentsDTO.withGratuity)?finalInterviewCommentsDTO.withGratuity:null;
		jobDetailsDBO.isEsiApplicable = !Utils.isNullOrEmpty(finalInterviewCommentsDTO.isESI)?finalInterviewCommentsDTO.isESI:null;
		//		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.vcComments)) {
		//			jobDetailsDBO.vcComments = finalInterviewCommentsDTO.vcComments;
		//		}else {
		//			jobDetailsDBO.vcComments = null;
		//		}
		jobDetailsDBO.recognisedExpYears = !Utils.isNullOrEmpty(finalInterviewCommentsDTO.recognisedExpYear) && finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("Yes")?Integer.parseInt(finalInterviewCommentsDTO.recognisedExpYear):null;
		jobDetailsDBO.recognisedExpMonths = !Utils.isNullOrEmpty(finalInterviewCommentsDTO.recognisedExpMonth) && finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("Yes")?Integer.parseInt(finalInterviewCommentsDTO.recognisedExpMonth):null;
		jobDetailsDBO.recordStatus = (finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("Yes") || finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("Onhold"))?'A':'D';
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.joiningDateAndTime)) {
			LocalDateTime join = finalInterviewCommentsDTO.joiningDateAndTime;
			jobDetailsDBO.joiningDate = join.withSecond(0);
		}
		else
			jobDetailsDBO.joiningDate = null;
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.reportingDateAndTime)) {
			LocalDateTime report = finalInterviewCommentsDTO.reportingDateAndTime;
			jobDetailsDBO.reportingDate = report.withSecond(0);	  	
		}else 
			jobDetailsDBO.reportingDate = null;
		return jobDetailsDBO;
	}

	public EmpPayScaleDetailsDBO convertEmpPayScaleDetailsDTOToDBO(EmpApplnEntriesDBO applnEntriesDBO,EmpPayScaleDetailsDBO empPayScaleDetailsDBO,List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOsList,FinalInterviewCommentsDTO finalInterviewCommentsDTO,String userId) {
		EmpPayScaleDetailsDBO empPayScaleDetailsDBO1 = new EmpPayScaleDetailsDBO();
		if(empPayScaleDetailsDBO == null) {
			empPayScaleDetailsDBO1.createdUsersId = Integer.parseInt(userId);
			empPayScaleDetailsDBO1.current = true;
			empPayScaleDetailsDBO1 = convertEmpPayScaleDetailsDTOToDBO1(applnEntriesDBO,empPayScaleDetailsDBO1,empPayScaleDetailsComponentsDBOsList,finalInterviewCommentsDTO,userId);
		} else if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO) && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.selectedPayScaleType) && !Utils.isNullOrEmpty(empPayScaleDetailsDBO)){
			if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase(empPayScaleDetailsDBO.payScaleType)) {
				empPayScaleDetailsDBO.modifiedUsersId = Integer.parseInt(userId);
				empPayScaleDetailsDBO = convertEmpPayScaleDetailsDTOToDBO1(applnEntriesDBO,empPayScaleDetailsDBO,empPayScaleDetailsComponentsDBOsList,finalInterviewCommentsDTO,userId);
				empPayScaleDetailsDBO1 = empPayScaleDetailsDBO;
			}else {
				empPayScaleDetailsDBO.current = false;
				Set<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOs = new HashSet<EmpPayScaleDetailsComponentsDBO>();
				if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBOsList)) {
					if(empPayScaleDetailsComponentsDBOsList != null) {
						empPayScaleDetailsComponentsDBOsList.forEach(empPaySub -> {
							empPaySub.recordStatus = 'D';
							empPaySub.modifiedUsersId = Integer.parseInt(userId);
							empPayScaleDetailsComponentsDBOs.add(empPaySub);
						});
						empPayScaleDetailsDBO.empPayScaleDetailsComponentsDBOs = empPayScaleDetailsComponentsDBOs;
					}
				}
				empPayScaleDetailsDBO.recordStatus = 'D';
				finalInterviewCommentsTransaction1.saveOrUpdateEmpPay(empPayScaleDetailsDBO);
				empPayScaleDetailsDBO1.createdUsersId = Integer.parseInt(userId);
				empPayScaleDetailsDBO1.current = true;
				empPayScaleDetailsDBO1 = convertEmpPayScaleDetailsDTOToDBO1(applnEntriesDBO,empPayScaleDetailsDBO1,empPayScaleDetailsComponentsDBOsList,finalInterviewCommentsDTO,userId);
			}
		}
		return empPayScaleDetailsDBO1;
	}

	public EmpPayScaleDetailsDBO convertEmpPayScaleDetailsDTOToDBO1(EmpApplnEntriesDBO applnEntriesDBO,EmpPayScaleDetailsDBO empPayScaleDetailsDBO,List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOsList,FinalInterviewCommentsDTO finalInterviewCommentsDTO,String userId) {
		empPayScaleDetailsDBO.empApplnEntriesDBO = applnEntriesDBO;
		empPayScaleDetailsDBO.payScaleType = !Utils.isNullOrEmpty(finalInterviewCommentsDTO.selectedPayScaleType)?finalInterviewCommentsDTO.selectedPayScaleType.toUpperCase():null;
		empPayScaleDetailsDBO.grossPay =  !Utils.isNullOrEmpty(finalInterviewCommentsDTO.grossPay)?new BigDecimal(finalInterviewCommentsDTO.grossPay):null;
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO)) {
			if(finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("Yes")) {
				if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.selectedPayScaleType)) {
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("SCALE PAY")) {
						EmpPayScaleMatrixDetailDBO empPayScaleMatrixDetailDBO = new EmpPayScaleMatrixDetailDBO();
						empPayScaleMatrixDetailDBO.id = Integer.parseInt(finalInterviewCommentsDTO.cell.id);
						empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = empPayScaleMatrixDetailDBO;
						empPayScaleDetailsDBO.wageRatePerType = null;
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("CONSOLIDATED") && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.consolidatedAmount)) {
						empPayScaleDetailsDBO.wageRatePerType =  new BigDecimal(finalInterviewCommentsDTO.consolidatedAmount);
						if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.cellValue) && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.cellValue.id)) {
							EmpPayScaleMatrixDetailDBO empPayScaleMatrixDetailDBO = new EmpPayScaleMatrixDetailDBO();
							empPayScaleMatrixDetailDBO.id = Integer.parseInt(finalInterviewCommentsDTO.cellValue.id);
							empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = empPayScaleMatrixDetailDBO;
						}else
							empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
						empPayScaleDetailsDBO.empDailyWageSlabDBO = null;
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("DAILY") && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.dailyAmount)) {
						empPayScaleDetailsDBO.wageRatePerType = new BigDecimal(finalInterviewCommentsDTO.dailyAmount);
						if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.empDailyWageDetailsId)) {
							EmpDailyWageSlabDBO empDailyWageSlabDBO = new EmpDailyWageSlabDBO();
							empDailyWageSlabDBO.id = finalInterviewCommentsDTO.empDailyWageDetailsId;
							empPayScaleDetailsDBO.empDailyWageSlabDBO = empDailyWageSlabDBO;
						} else
							empPayScaleDetailsDBO.empDailyWageSlabDBO = null;
						empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("PER HOUR") && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.perHourAmount)) {
						empPayScaleDetailsDBO.wageRatePerType =  new BigDecimal(finalInterviewCommentsDTO.perHourAmount);
						empPayScaleDetailsDBO.empDailyWageSlabDBO = null;
						empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("PER COURSE") && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.perCourseAmount)) {
						empPayScaleDetailsDBO.wageRatePerType =  new BigDecimal(finalInterviewCommentsDTO.perCourseAmount);
						empPayScaleDetailsDBO.empDailyWageSlabDBO = null;
						empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
					}
					empPayScaleDetailsDBO.recordStatus = 'A';
				}
			} 
		}else {
			empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
			empPayScaleDetailsDBO.wageRatePerType = null;
			empPayScaleDetailsDBO.empDailyWageSlabDBO = null;
			empPayScaleDetailsDBO.recordStatus = 'D';
		}
		Set<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOs = new HashSet<>();
		Set<EmpPayScaleDetailsComponentsDBO> deleteSet1 = new HashSet<EmpPayScaleDetailsComponentsDBO>(empPayScaleDetailsComponentsDBOsList);
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.empPayScaleComponents)) {
			for(SalaryComponentDTO item :finalInterviewCommentsDTO.empPayScaleComponents) {
				EmpPayScaleDetailsComponentsDBO detail = null;
				if(deleteSet1 != null) {
					for(EmpPayScaleDetailsComponentsDBO bo : deleteSet1) {
						if(Utils.isNullOrWhitespace(item.id) == false) {
							if(Integer.parseInt(item.id) == bo.id) {
								detail = bo;
								deleteSet1.remove(bo);
								break;
							}
						} else if(Utils.isNullOrWhitespace(item.id)) {
							deleteSet1.add(bo);
							break;
						}
					}
				}
				if(detail == null) {
					detail = new EmpPayScaleDetailsComponentsDBO();
					detail.createdUsersId = Integer.parseInt(userId);
					detail.recordStatus = 'A';
				} else {
					detail.modifiedUsersId = Integer.parseInt(userId);
				}
				detail.empPayScaleDetailsDBO=empPayScaleDetailsDBO;
				EmpPayScaleComponentsDBO empPayScaleComponentsDBO = new EmpPayScaleComponentsDBO();
				if(item.id!=null && !item.id.isEmpty() && item.amount!=null && !item.amount.isEmpty()) {
					empPayScaleComponentsDBO.id = Integer.parseInt(item.id);
					detail.empPayScaleComponentsDBO =empPayScaleComponentsDBO;
					detail.empSalaryComponentValue = new BigDecimal(item.amount);
					detail.recordStatus = 'A';
				}else if(item.id!=null && !item.id.isEmpty()){
					empPayScaleComponentsDBO.id = Integer.parseInt(item.id);
					detail.empPayScaleComponentsDBO =empPayScaleComponentsDBO;
					detail.empSalaryComponentValue = null;
					detail.recordStatus = 'A';
				}
				empPayScaleDetailsComponentsDBOs.add(detail);
			}
		}
		if(deleteSet1 != null) {
			for(EmpPayScaleDetailsComponentsDBO bo : deleteSet1) {
				bo.recordStatus = 'D';
				bo.modifiedUsersId = Integer.parseInt(userId);
				empPayScaleDetailsComponentsDBOs.add(bo);
			}
		}
		empPayScaleDetailsDBO.empPayScaleDetailsComponentsDBOs = empPayScaleDetailsComponentsDBOs;
		return empPayScaleDetailsDBO;
	}

	public FinalInterviewCommentsDTO convertEmpPositionRoleSubDBOToDTO(List<EmpPositionRoleSubDBO> empPositionRoleSubDBOs,EmpPositionRoleDBO empPositionRoleDBO,FinalInterviewCommentsDTO finalInterviewCommentsDTO)  {
		EmpPositionRoleDTO empPositionRoleDTO = new EmpPositionRoleDTO();
		empPositionRoleDTO.id = empPositionRoleDBO.id.toString();
		empPositionRoleDTO.processType = empPositionRoleDBO.processType.toString();
		empPositionRoleDTO.campus = new ExModelBaseDTO();
		empPositionRoleDTO.campus.id = empPositionRoleDBO.erpCampusId.id.toString();
		empPositionRoleDTO.Levels = new ArrayList<>();
		for(EmpPositionRoleSubDBO bo:empPositionRoleSubDBOs) {
			EmpPositionRoleSubDTO levelInfo = new EmpPositionRoleSubDTO();
			if(bo.recordStatus == 'A') {
				levelInfo.id = bo.id.toString();
				levelInfo.empTitle = new ExModelBaseDTO();
				levelInfo.empTitle.id = bo.empTitleId.id.toString();
				levelInfo.empTitle.text = bo.empTitleId.titleName.toString();
				levelInfo.employee = new ExModelBaseDTO();
				levelInfo.employee.id = bo.empDBO.id.toString();
				levelInfo.employee.text = bo.empDBO.empName.toString();
				levelInfo.order = bo.displayOrder.toString();
				levelInfo.comment = "";
				empPositionRoleDTO.Levels.add(levelInfo);
			}
		}
		Collections.sort(empPositionRoleDTO.Levels, new Comparator<EmpPositionRoleSubDTO>() {
			@Override
			public int compare(EmpPositionRoleSubDTO o1, EmpPositionRoleSubDTO o2) {
				return Integer.compare(Integer.parseInt(o1.order), Integer.parseInt(o2.order));
			}
		});
		finalInterviewCommentsDTO.processAndPosition = empPositionRoleDTO.Levels;
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertEmpDailyWageSlabDetailsDBOToDTO(String dailyAmount,List<Tuple> dlyWgDataList,FinalInterviewCommentsDTO finalInterviewCommentsDTO) {
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
				if(Float.parseFloat(dailyAmount) >= Float.parseFloat( slabDetailDTO.get("dly_wge_from").toString())
						&& Float.parseFloat(dailyAmount) <= Float.parseFloat( slabDetailDTO.get("dly_wge_to").toString())){
					finalInterviewCommentsDTO.minAmount = slabDetailDTO.get("dly_wge_from").toString();
					finalInterviewCommentsDTO.maxAmount = slabDetailDTO.get("dly_wge_to").toString();
					finalInterviewCommentsDTO.empDailyWageDetailsId = (Integer) slabDetailDTO.get("dly_wg_slab_Id");
				}
				detailList.add(dailywageDetlInfo);
				dailywageInfo.empDailyWageDetails = detailList;
			}
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertEmpApplnEntrieToDTO(Tuple applnEntrie,FinalInterviewCommentsDTO finalInterviewCommentsDTO){
		finalInterviewCommentsDTO.applicationId= applnEntrie.get("applnId").toString();
		finalInterviewCommentsDTO.applicationNumber=applnEntrie.get("applnNo").toString();
		finalInterviewCommentsDTO.applicantName=applnEntrie.get("applnName").toString();
		if(!Utils.isNullOrEmpty(applnEntrie.get("camDeptId")) && !Utils.isNullOrEmpty(applnEntrie.get("deptName"))) {
			finalInterviewCommentsDTO.setCampusDeptDto(new SelectDTO());
			finalInterviewCommentsDTO.getCampusDeptDto().setLabel(String.valueOf(applnEntrie.get("deptName")));
			finalInterviewCommentsDTO.getCampusDeptDto().setValue(String.valueOf(applnEntrie.get("camDeptId")));
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("employeeCategoryId")) && !Utils.isNullOrEmpty(applnEntrie.get("empCategoryRecordStatus"))){
			if(applnEntrie.get("empCategoryRecordStatus").toString().equalsIgnoreCase("A")) {
				finalInterviewCommentsDTO.category = new ExModelBaseDTO();
				finalInterviewCommentsDTO.category.id = applnEntrie.get("employeeCategoryId").toString();
				finalInterviewCommentsDTO.category.text = applnEntrie.get("employeeCategoryName").toString();
				finalInterviewCommentsDTO.categoryAcademic = new ExModelBaseDTO();
				finalInterviewCommentsDTO.categoryAcademic.id =  applnEntrie.get("employeeCategoryId").toString();
				finalInterviewCommentsDTO.categoryAcademic.text = applnEntrie.get("isEmployeeCategoryAcademic").toString();
				finalInterviewCommentsDTO.isEmployeeCategoryAcademic = applnEntrie.get("isEmployeeCategoryAcademic").toString();
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("email"))) {
			finalInterviewCommentsDTO.personalEmail = applnEntrie.get("email").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("mobile"))) {
			finalInterviewCommentsDTO.mobileNumber = applnEntrie.get("mobile").toString();
		}
		//		finalInterviewCommentsDTO.offerLetterTemplate = new ExModelBaseDTO();
		//		if(!Utils.isNullOrEmpty(applnEntrie.get("regretId"))) {
		//			finalInterviewCommentsDTO.offerLetterTemplate.id = applnEntrie.get("regretId").toString();
		//			if(!Utils.isNullOrEmpty(applnEntrie.get("regreteLetterName")))
		//				finalInterviewCommentsDTO.offerLetterTemplate.text = applnEntrie.get("regreteLetterName").toString();
		//		}
		//		if(!Utils.isNullOrEmpty(applnEntrie.get("offerId"))) {
		//			finalInterviewCommentsDTO.offerLetterTemplate.id = applnEntrie.get("offerId").toString();
		//			if(!Utils.isNullOrEmpty(applnEntrie.get("offerletterName")))
		//				finalInterviewCommentsDTO.offerLetterTemplate.text = applnEntrie.get("offerletterName").toString();
		//		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("erpCampusId")) && !Utils.isNullOrEmpty(applnEntrie.get("erpCampusRecordStatus"))) {
			if(applnEntrie.get("erpCampusRecordStatus").toString().equalsIgnoreCase("A")) {
				finalInterviewCommentsDTO.campus = new ExModelBaseDTO();
				finalInterviewCommentsDTO.campus.id = applnEntrie.get("erpCampusId").toString();
				finalInterviewCommentsDTO.campus.text = applnEntrie.get("erpCampusName").toString();
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("employeeJobCategoryId")) && !Utils.isNullOrEmpty(applnEntrie.get("empJobCategoryRecordStatus"))) {
			if(applnEntrie.get("empJobCategoryRecordStatus").toString().equalsIgnoreCase("A")) {
				finalInterviewCommentsDTO.jobCategory = new ExModelBaseDTO();
				finalInterviewCommentsDTO.jobCategory.id = applnEntrie.get("employeeJobCategoryId").toString();
				finalInterviewCommentsDTO.jobCategory.text = applnEntrie.get("employeeJobCategoryName").toString();
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("empDesignationId")) && !Utils.isNullOrEmpty(applnEntrie.get("empDesignationRecordStatus"))) {
			if(applnEntrie.get("empDesignationRecordStatus").toString().equalsIgnoreCase("A")) {
				finalInterviewCommentsDTO.designation = new ExModelBaseDTO();
				finalInterviewCommentsDTO.designation.id = applnEntrie.get("empDesignationId").toString();
				finalInterviewCommentsDTO.designation.text = applnEntrie.get("empDesignationName").toString();
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("empTitleId")) && !Utils.isNullOrEmpty(applnEntrie.get("empTitleRecordStatus"))) {
			if(applnEntrie.get("empTitleRecordStatus").toString().equalsIgnoreCase("A")) {
				finalInterviewCommentsDTO.empTitle = new ExModelBaseDTO();
				finalInterviewCommentsDTO.empTitle.id = applnEntrie.get("empTitleId").toString();
				finalInterviewCommentsDTO.empTitle.text = applnEntrie.get("empTitleName").toString();
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("empApplnSubjectCategoryId")) && !Utils.isNullOrEmpty(applnEntrie.get("empApplnSubjectCategoryRecordStatus"))) {
			if(applnEntrie.get("empApplnSubjectCategoryRecordStatus").toString().equalsIgnoreCase("A")) {
				finalInterviewCommentsDTO.subjectCategory = new ExModelBaseDTO();
				finalInterviewCommentsDTO.subjectCategory.id = applnEntrie.get("empApplnSubjectCategoryId").toString();
				finalInterviewCommentsDTO.subjectCategory.text = applnEntrie.get("empApplnSubjectCategoryName").toString();
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("empApplnSubjectCategorySpecializationId")) && !Utils.isNullOrEmpty(applnEntrie.get("empApplnSubjectCategorySpecializationRecordStatus"))) {
			if(applnEntrie.get("empApplnSubjectCategorySpecializationRecordStatus").toString().equalsIgnoreCase("A")) {
				finalInterviewCommentsDTO.subjectCategorySpecialization = new ExModelBaseDTO();
				finalInterviewCommentsDTO.subjectCategorySpecialization.id = applnEntrie.get("empApplnSubjectCategorySpecializationId").toString();
				finalInterviewCommentsDTO.subjectCategorySpecialization.text = applnEntrie.get("empApplnSubjectCategorySpecializationName").toString();
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicationCode"))) {
			String processCode = String.valueOf(applnEntrie.get("erpWorkFlowProcessApplicationCode"));
			if(processCode.equalsIgnoreCase("EMP_STAGE2_SELECTED")
					|| processCode.equalsIgnoreCase("EMP_OFFER_DECLINED" )
					|| processCode.equalsIgnoreCase("EMP_OFFER_ACCEPTED")
					|| processCode.equalsIgnoreCase("EMP_OFFER_LETTER_GENERATED")
					|| processCode.equalsIgnoreCase("EMP_OFFER_LETTER_REGENERATE")
					|| processCode.equalsIgnoreCase("EMP_STAGE3_ONHOLD")
					|| processCode.equalsIgnoreCase("EMP_STAGE3_REJECTED")
					|| processCode.equalsIgnoreCase("EMP_STAGE3_SELECTED")
					|| processCode.equalsIgnoreCase("EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED")
					|| processCode.equalsIgnoreCase("EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED")
					|| processCode.equalsIgnoreCase("EMP_STAGE3_PO_RESCHEDULED")
					|| processCode.equalsIgnoreCase("EMP_STAGE3_PO_SCHEDULED")
					|| processCode.equalsIgnoreCase("EMP_REGRET_LETTER_GENERATED")) {
				finalInterviewCommentsDTO.setSelectionStatus("Yes");
				finalInterviewCommentsDTO.mode="edit";
			}
			if(String.valueOf(applnEntrie.get("erpWorkFlowProcessApplicationCode")).equalsIgnoreCase("EMP_STAGE2_REJECTED")) {
				finalInterviewCommentsDTO.setSelectionStatus("No");
			}
			if(String.valueOf(applnEntrie.get("erpWorkFlowProcessApplicationCode")).equalsIgnoreCase("EMP_STAGE2_ONHOLD")) {
				finalInterviewCommentsDTO.setSelectionStatus("Onhold");
			}
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("satge2Comments"))) {
			finalInterviewCommentsDTO.setStage2OnholdRejectedComments(applnEntrie.get("satge2Comments").toString());
		}
		//		if((!Utils.isNullOrEmpty(applnEntrie.get("offerLetterUrl")) && !Utils.isNullOrEmpty(applnEntrie.get("offerLetterGeneratedDate")))) {
		//			finalInterviewCommentsDTO.isOfferLetterGenerated = Boolean.TRUE;
		//			finalInterviewCommentsDTO.isOfferLetterRegenerated = true;
		//			if(!Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicantId")) || !Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicationId"))) {
		//				if(!Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicantText")) || !Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicationText"))) {
		//					if(!Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicationText")))
		//						finalInterviewCommentsDTO.applicationStatus = applnEntrie.get("erpWorkFlowProcessApplicationText").toString();
		//					if(!Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicantCode")) || !Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicationCode"))) {
		//						if(!Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicantCode")))
		//							if(applnEntrie.get("erpWorkFlowProcessApplicantCode").toString().equalsIgnoreCase("EMP_SELECTED") || !Utils.isNullOrEmpty(applnEntrie.get("offerletterUrl")) ||
		//									applnEntrie.get("erpWorkFlowProcessApplicantCode").toString().equalsIgnoreCase("EMP_OFFER_LETTER_REGENERATED") ||
		//									applnEntrie.get("erpWorkFlowProcessApplicantCode").toString().equalsIgnoreCase("EMP_DECLINED_REPORTING_TIME_PREFFERED")) 
		//								finalInterviewCommentsDTO.isOfferLetterRegenerated = true;
		//							else
		//								finalInterviewCommentsDTO.isOfferLetterRegenerated = false;
		//						if(!Utils.isNullOrEmpty(applnEntrie.get("erpWorkFlowProcessApplicationCode")))
		//							if(applnEntrie.get("erpWorkFlowProcessApplicationCode").toString().equalsIgnoreCase("EMP_SELECTED") || !Utils.isNullOrEmpty(applnEntrie.get("offerletterUrl")) ||
		//									applnEntrie.get("erpWorkFlowProcessApplicantCode").toString().equalsIgnoreCase("EMP_OFFER_LETTER_REGENERATED") ||
		//									applnEntrie.get("erpWorkFlowProcessApplicantCode").toString().equalsIgnoreCase("EMP_DECLINED_REPORTING_TIME_PREFFERED")) 
		//								finalInterviewCommentsDTO.isOfferLetterRegenerated = true;
		//							else
		//								finalInterviewCommentsDTO.isOfferLetterRegenerated = false;
		//					}
		//				}
		//			}
		//			if(!Utils.isNullOrEmpty(applnEntrie.get("empApplnNonAvailabilityId"))) 
		//				if(!Utils.isNullOrEmpty(applnEntrie.get("empApplnNonAvailabilityName"))) 
		//					finalInterviewCommentsDTO.jobRejectionReason = applnEntrie.get("empApplnNonAvailabilityName").toString();
		//			if(!Utils.isNullOrEmpty(applnEntrie.get("jobRejectionReason"))) 
		//				if(applnEntrie.get("jobRejectionReason").toString().equals("{null}")|| applnEntrie.get("jobRejectionReason").toString().equals("")) {
		//					finalInterviewCommentsDTO.jobRejectionReason = null;
		//				}else {
		//					finalInterviewCommentsDTO.jobRejectionReason = applnEntrie.get("jobRejectionReason").toString();
		//				}
		//		}else {
		//			finalInterviewCommentsDTO.isOfferLetterGenerated = Boolean.FALSE;
		//		}
		//		if(!Utils.isNullOrEmpty(applnEntrie.get("isSelected"))) {
		//			finalInterviewCommentsDTO.isSelected = applnEntrie.get("isSelected").toString().equals("1") ? true :false;
		//			if(applnEntrie.get("isSelected").toString().equals("1")) {
		//				finalInterviewCommentsDTO.mode="edit";
		//			}else {
		//				finalInterviewCommentsDTO.mode="edit";
		//			}
		//		}else {
		//			finalInterviewCommentsDTO.mode="";
		//		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertEmpApplnDignitariesFeedbackToDTO(List<Tuple> applnDignitariesList,FinalInterviewCommentsDTO finalInterviewCommentsDTO,List<EmpPositionRoleSubDBO> empPositionRoleSubDBOs,EmpPositionRoleDBO empPositionRoleDBO){
		finalInterviewCommentsDTO.processAndPosition = new ArrayList<EmpPositionRoleSubDTO>();  
		if(!Utils.isNullOrEmpty(applnDignitariesList)) {
			EmpPositionRoleDTO empPositionRoleDTO = new EmpPositionRoleDTO();
			empPositionRoleDTO.id = empPositionRoleDBO.id.toString();
			empPositionRoleDTO.Levels = new ArrayList<>();
			int i =0;
			for(Tuple mappTuple : applnDignitariesList) {
//				for(EmpPositionRoleSubDBO bo:empPositionRoleSubDBOs) {
//					if(bo.recordStatus == 'A'
//							&& mappTuple.get("empTitleId").toString().equalsIgnoreCase(bo.empTitleId.id.toString())) {
				EmpPositionRoleSubDTO levelInfo = new EmpPositionRoleSubDTO();
				if (!Utils.isNullOrEmpty(mappTuple.get("ID"))) {
					levelInfo.id = mappTuple.get("ID").toString();
				}
				levelInfo.empTitle = new ExModelBaseDTO();
				if (!Utils.isNullOrEmpty(mappTuple.get("empTitleId")) && !Utils.isNullOrEmpty(mappTuple.get("empTitleName"))) {
					levelInfo.empTitle.id = mappTuple.get("empTitleId").toString();
					levelInfo.empTitle.text = mappTuple.get("empTitleName").toString();
				}
				levelInfo.employee = new ExModelBaseDTO();
				if (!Utils.isNullOrEmpty(mappTuple.get("empId"))) {
					levelInfo.employee.id = mappTuple.get("empId").toString();
				}
				if (!Utils.isNullOrEmpty(mappTuple.get("empName"))) {
					levelInfo.employee.text = mappTuple.get("empName").toString();
				}
//						if(!Utils.isNullOrEmpty(bo.displayOrder)) {
				levelInfo.order = String.valueOf(++i);
//						}
				if (!Utils.isNullOrEmpty(mappTuple.get("Comment"))) {
					levelInfo.comment = mappTuple.get("Comment").toString();
				}
				empPositionRoleDTO.Levels.add(levelInfo);
//						break;
//					}
//				}
//				Collections.sort(empPositionRoleDTO.Levels, new Comparator<EmpPositionRoleSubDTO>() {
//					@Override
//					public int compare(EmpPositionRoleSubDTO o1, EmpPositionRoleSubDTO o2) {
//						return Integer.compare(Integer.parseInt(o1.order), Integer.parseInt(o2.order));
//					}
//				});
				finalInterviewCommentsDTO.processAndPosition = empPositionRoleDTO.Levels;
			}
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertEmpJobDetailsToDTO(Tuple jobDetails,FinalInterviewCommentsDTO finalInterviewCommentsDTO){
		if(!Utils.isNullOrEmpty(jobDetails.get("isWithGratuity"))) {		
			if(Integer.parseInt(String.valueOf(jobDetails.get("isWithGratuity"))) ==1) {
				finalInterviewCommentsDTO.withGratuity = true;
			} else {
				finalInterviewCommentsDTO.withGratuity = false;
			}
		}
		if(!Utils.isNullOrEmpty(jobDetails.get("isWithPf"))) {
			if(Integer.parseInt(String.valueOf(jobDetails.get("isWithPf").toString())) ==1) {
				finalInterviewCommentsDTO.withPF = true;
			} else {
				finalInterviewCommentsDTO.withPF = false ;
			}
		}
		if(!Utils.isNullOrEmpty(jobDetails.get("isEsiApplicable"))) {
			if(Integer.parseInt(String.valueOf(jobDetails.get("isEsiApplicable"))) ==1) {
				finalInterviewCommentsDTO.isESI = true;
			} else {
				finalInterviewCommentsDTO.isESI = false;
			}
		}
		//		if(!Utils.isNullOrEmpty(jobDetails.get("vcComments"))) {
		//			finalInterviewCommentsDTO.vcComments = jobDetails.get("vcComments").toString();
		//		}
		if(!Utils.isNullOrEmpty(jobDetails.get("reportingDate"))) {
			finalInterviewCommentsDTO.reportingDateAndTime = Utils.convertStringDateTimeToLocalDateTime(jobDetails.get("reportingDate").toString());
		}
		if(!Utils.isNullOrEmpty(jobDetails.get("joiningDate"))) {
			finalInterviewCommentsDTO.joiningDateAndTime = Utils.convertStringDateTimeToLocalDateTime(jobDetails.get("joiningDate").toString());
		}
		//		if(!Utils.isNullOrEmpty(jobDetails.get("joiningDate"))){
		//			String originalString = jobDetails.get("joiningDate").toString();;
		//			//   		 	Date date = new SimpleDateFormat("yyyy-MM-dd K:mm").parse(originalString);
		//			//   		 	String joiningDateAndTime = new SimpleDateFormat("dd/MM/yyyy K:mm").format(date);
		//			LocalDateTime date = Utils.convertStringDateTimeToLocalDateTime(originalString);
		//			String joiningDateAndTime = Utils.convertLocalDateTimeToStringDateTime(date);
		//
		//			Calendar cal = Calendar.getInstance();
		//			//cal.setTime(date);
		//			if(cal.get(Calendar.AM_PM)==0)
		//				joiningDateAndTime=joiningDateAndTime+" AM";
		//			else
		//				joiningDateAndTime=joiningDateAndTime+" PM";
		//			finalInterviewCommentsDTO.joiningDateAndTimeTag = joiningDateAndTime;
		//		}
		//		if(!Utils.isNullOrEmpty(jobDetails.get("reportingDate"))){
		//			String originalString = jobDetails.get("reportingDate").toString();;
		//			LocalDateTime date = Utils.convertStringDateTimeToLocalDateTime(originalString);
		//			String reportingDateAndTime = Utils.convertLocalDateTimeToStringDateTime(date);
		//			Calendar cal = Calendar.getInstance();
		//			if(cal.get(Calendar.AM_PM)==0)
		//				reportingDateAndTime=reportingDateAndTime;
		//			else
		//				reportingDateAndTime=reportingDateAndTime;
		//			finalInterviewCommentsDTO.reportingDateAndTimeTag = reportingDateAndTime;
		//		}
		if(!Utils.isNullOrEmpty(jobDetails.get("recognisedExpYears"))) {
			finalInterviewCommentsDTO.recognisedExpYear = jobDetails.get("recognisedExpYears").toString();
		}
		if(!Utils.isNullOrEmpty(jobDetails.get("recognisedExpMonths"))) {
			finalInterviewCommentsDTO.recognisedExpMonth = jobDetails.get("recognisedExpMonths").toString();
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertEmpPayScaleMatrixDetailToDTO(Tuple empPayScaleMatrixDetail,FinalInterviewCommentsDTO finalInterviewCommentsDTO) {
		if(!Utils.isNullOrEmpty(empPayScaleMatrixDetail.get("gradeId").toString())) {
			finalInterviewCommentsDTO.grade = new ExModelBaseDTO();
			finalInterviewCommentsDTO.grade.id = empPayScaleMatrixDetail.get("gradeId").toString();
			finalInterviewCommentsDTO.grade.text = empPayScaleMatrixDetail.get("gradeName").toString();
		}
		if(!Utils.isNullOrEmpty(empPayScaleMatrixDetail.get("revisedYear").toString())) {
			finalInterviewCommentsDTO.revisedYear = new ExModelBaseDTO();
			finalInterviewCommentsDTO.revisedYear.id = empPayScaleMatrixDetail.get("gradeId").toString();
			finalInterviewCommentsDTO.revisedYear.text = empPayScaleMatrixDetail.get("revisedYear").toString();
		}
		if(!Utils.isNullOrEmpty(empPayScaleMatrixDetail.get("levelId").toString())) {
			finalInterviewCommentsDTO.level = new ExModelBaseDTO();
			finalInterviewCommentsDTO.level.id = empPayScaleMatrixDetail.get("levelId").toString();
			finalInterviewCommentsDTO.level.text = empPayScaleMatrixDetail.get("scaleLevel").toString(); // sambath
		}
		if(!Utils.isNullOrEmpty(empPayScaleMatrixDetail.get("payScale").toString())) {
			finalInterviewCommentsDTO.scale = new ExModelBaseDTO();
			finalInterviewCommentsDTO.scale.id = empPayScaleMatrixDetail.get("levelId").toString();
			finalInterviewCommentsDTO.scale.text = empPayScaleMatrixDetail.get("payScale").toString();
		}
		if(!Utils.isNullOrEmpty(empPayScaleMatrixDetail.get("cellId").toString())) {
			finalInterviewCommentsDTO.cell = new ExModelBaseDTO();
			finalInterviewCommentsDTO.cell.id = empPayScaleMatrixDetail.get("cellId").toString();
			finalInterviewCommentsDTO.cell.text = empPayScaleMatrixDetail.get("cellName").toString();
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertEmpPayScaleDetailsComponentsToDTO(List<Tuple> empPayScaleDetailsComponentsList,FinalInterviewCommentsDTO finalInterviewCommentsDTO) {
		finalInterviewCommentsDTO.empPayScaleComponents = new ArrayList<SalaryComponentDTO>();
		for(Tuple mappTuple:empPayScaleDetailsComponentsList) {
			SalaryComponentDTO dto = new SalaryComponentDTO();
			if(!Utils.isNullOrEmpty(mappTuple.get("empPayScaleComponentsId"))) {
			dto.id =mappTuple.get("empPayScaleComponentsId").toString();
			}
			if(!Utils.isNullOrEmpty(mappTuple.get("salaryComponentName"))) {
			dto.allowanceType = mappTuple.get("salaryComponentName").toString();
			}
			if(!Utils.isNullOrEmpty(mappTuple.get("salaryComponentShortName"))) {
			dto.shortName = mappTuple.get("salaryComponentShortName").toString();
			}
			if(!Utils.isNullOrEmpty(mappTuple.get("isComponentBasic"))) {
				if("1".equals(mappTuple.get("isComponentBasic").toString())) {
					dto.isBasic = true;
					if(!Utils.isNullOrEmpty(mappTuple.get("amount"))) {
					finalInterviewCommentsDTO.basicPayAmount = mappTuple.get("amount").toString();
					}
				} else {
					dto.isBasic = false;
				}
			}
			if(!Utils.isNullOrEmpty(mappTuple.get("isCaculationTypePercentage"))) {
				if("1".equals(mappTuple.get("isCaculationTypePercentage").toString())) {
					dto.calculationType = true;
				} else {
					dto.calculationType = false;
				}
			}
			if(!Utils.isNullOrEmpty(mappTuple.get("amount")))
				dto.amount = mappTuple.get("amount").toString();
			else
				dto.amount = null;
			if(!Utils.isNullOrEmpty(mappTuple.get("percentage"))) {
				dto.mentionPercentage= mappTuple.get("percentage").toString();
			}
//			else if(!Boolean.valueOf(mappTuple.get("isComponentBasic").toString())) {
//				if(!Utils.isNullOrEmpty(mappTuple.get("amount")))
//					dto.amount = mappTuple.get("amount").toString().split("\\.")[0];
//			}else {
//				finalInterviewCommentsDTO.basicPayAmount = mappTuple.get("amount").toString();
//			}
			if(!Utils.isNullOrEmpty(mappTuple.get("payScaleType"))) {
				dto.payScaleType = mappTuple.get("payScaleType").toString();
			}
			if(!Utils.isNullOrEmpty(mappTuple.get("displayOrder"))) {
				dto.displayOrder = mappTuple.get("displayOrder").toString();
			}
			finalInterviewCommentsDTO.empPayScaleComponents.add(dto);
		}
		Collections.sort(finalInterviewCommentsDTO.empPayScaleComponents, new Comparator<SalaryComponentDTO>() {
			@Override
			public int compare(SalaryComponentDTO o1, SalaryComponentDTO o2) {
				return Integer.compare(Integer.parseInt(o1.displayOrder), Integer.parseInt(o2.displayOrder));
			}
		});
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertEmpApplnEntrieToPrintDTO(Tuple applnEntrie,FinalInterviewCommentsDTO finalInterviewCommentsDTO) throws Exception {
		if(!Utils.isNullOrEmpty(applnEntrie.get("application_no"))){
			finalInterviewCommentsDTO.printApplicationNumber = applnEntrie.get("application_no").toString();
		}		
		if(!Utils.isNullOrEmpty(applnEntrie.get("applicant_name"))){
			finalInterviewCommentsDTO.printApplicantName = applnEntrie.get("applicant_name").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("gender_name"))){
			finalInterviewCommentsDTO.genderName = applnEntrie.get("gender_name").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("age"))){
			finalInterviewCommentsDTO.age = applnEntrie.get("age").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("dob"))){
			finalInterviewCommentsDTO.dob = Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(applnEntrie.get("dob").toString()));
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("applied_date"))){
			finalInterviewCommentsDTO.appliedDate = Utils.convertStringDateToLocalDate(applnEntrie.get("applied_date").toString());
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("subject_category_name"))){
			finalInterviewCommentsDTO.printSubjectCategoryName = applnEntrie.get("subject_category_name").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("is_academic"))){
			if(Boolean.valueOf(applnEntrie.get("is_academic").toString()))
				finalInterviewCommentsDTO.printSubjectCategoryLable = "Subject";
			else
				finalInterviewCommentsDTO.printSubjectCategoryLable = "Category";
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("expected_salary"))){
			finalInterviewCommentsDTO.expectedSalary = applnEntrie.get("expected_salary").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("emp_designation_name"))){
			finalInterviewCommentsDTO.printDesignation = applnEntrie.get("emp_designation_name").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("recognised_exp_years"))){
			finalInterviewCommentsDTO.printRecognisedExpYear = applnEntrie.get("recognised_exp_years").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("recognised_exp_months"))){
			finalInterviewCommentsDTO.printRecognisedExpMonth = applnEntrie.get("recognised_exp_months").toString();
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("title_name"))){
			finalInterviewCommentsDTO.setEmpTitle(new ExModelBaseDTO());
			finalInterviewCommentsDTO.getEmpTitle().setText(applnEntrie.get("title_name").toString());
		}
		if(!Utils.isNullOrEmpty(applnEntrie.get("joining_date"))){
			String originalString = applnEntrie.get("joining_date").toString();;
			LocalDateTime date = Utils.convertStringDateTimeToLocalDateTime(originalString);
			String joiningDateAndTime = Utils.convertLocalDateTimeToStringDateTime(date);
//			Calendar cal = Calendar.getInstance();
//			if(cal.get(Calendar.AM_PM)==0)
//				joiningDateAndTime=joiningDateAndTime+" AM";
//			else
//				joiningDateAndTime=joiningDateAndTime+" PM";
			finalInterviewCommentsDTO.printJoiningTime = joiningDateAndTime;
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertQualificationLevelDTO(List<Tuple> qualificationLevelList,FinalInterviewCommentsDTO finalInterviewCommentsDTO) throws Exception {
		finalInterviewCommentsDTO.qualificationLevelList = new ArrayList<EmpApplnEducationalDetailsDTO>();
		for(Tuple mappTuple:qualificationLevelList) {
			EmpApplnEducationalDetailsDTO applnEducationalDetailsDTO = new EmpApplnEducationalDetailsDTO();
			if(!Utils.isNullOrEmpty(mappTuple.get("qualification_level_name"))){
				applnEducationalDetailsDTO.qualificationLevelName =mappTuple.get("qualification_level_name").toString();
			}	
			if(!Utils.isNullOrEmpty(mappTuple.get("grade_or_percentage"))){
				applnEducationalDetailsDTO.gradeOrPercentage = mappTuple.get("grade_or_percentage").toString();
			}		
			if(!Utils.isNullOrEmpty(mappTuple.get("year_of_completion"))){
				applnEducationalDetailsDTO.yearOfCompletion = mappTuple.get("year_of_completion").toString();
			}		
			if(!Utils.isNullOrEmpty( mappTuple.get("board_or_university"))){
				applnEducationalDetailsDTO.boardOrUniversity = mappTuple.get("board_or_university").toString();
			}	
			if(!Utils.isNullOrEmpty(mappTuple.get("qualification_others"))){
				applnEducationalDetailsDTO.setQualificationOthers(mappTuple.get("qualification_others").toString());
			}	
			if(!Utils.isNullOrEmpty(mappTuple.get("current_status"))){
				applnEducationalDetailsDTO.setCurrentStatus(mappTuple.get("current_status").toString());
			}	
			finalInterviewCommentsDTO.qualificationLevelList.add(applnEducationalDetailsDTO);
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertSalaryDetailsDTO(Tuple salaryDetails,Tuple totalExperience,FinalInterviewCommentsDTO finalInterviewCommentsDTO) throws Exception {
		if(!Utils.isNullOrEmpty(salaryDetails.get("gross_pay"))){
			finalInterviewCommentsDTO.printGrossPay = salaryDetails.get("gross_pay").toString();
		}
		if(!Utils.isNullOrEmpty(salaryDetails.get("basic_value"))){
			finalInterviewCommentsDTO.printBasicValue = salaryDetails.get("basic_value").toString();
		}
		if(!Utils.isNullOrEmpty(salaryDetails.get("pay_scale_type"))) {
			if(!Utils.isNullOrEmpty(salaryDetails.get("pay_scale")) && salaryDetails.get("pay_scale_type").toString().equalsIgnoreCase("SCALE PAY")){
				finalInterviewCommentsDTO.printScalePay = salaryDetails.get("pay_scale").toString();
			}
		}
		if(!Utils.isNullOrEmpty(salaryDetails.get("level_cell_no"))){
			finalInterviewCommentsDTO.setCell(new ExModelBaseDTO());
			finalInterviewCommentsDTO.getCell().setText(salaryDetails.get("level_cell_no").toString());
		}
		if(!Utils.isNullOrEmpty(salaryDetails.get("emp_pay_scale_level"))){
			finalInterviewCommentsDTO.setLevel(new ExModelBaseDTO());
			finalInterviewCommentsDTO.getLevel().setText(salaryDetails.get("emp_pay_scale_level").toString());
		}
		if(!Utils.isNullOrEmpty(totalExperience)) {
			if(!Utils.isNullOrEmpty(totalExperience.get("experience_yearss")))
				finalInterviewCommentsDTO.printTotalExpYear = totalExperience.get("experience_yearss").toString();
			if(!Utils.isNullOrEmpty(totalExperience.get("experience_monthss")))
				finalInterviewCommentsDTO.printTotalExpMonth = totalExperience.get("experience_monthss").toString();
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertSalaryDetailsByPayScaleTypeDTO(Tuple salaryDetails,FinalInterviewCommentsDTO finalInterviewCommentsDTO) throws Exception {
		if(!Utils.isNullOrEmpty(salaryDetails.get("pay_scale_type"))) {
			finalInterviewCommentsDTO.setPrintScalePay(salaryDetails.get("pay_scale_type").toString());
			if(!Utils.isNullOrEmpty(salaryDetails.get("wage_rate_per_type"))) {
				if(salaryDetails.get("pay_scale_type").toString().equalsIgnoreCase("PER COURSE")) {
					finalInterviewCommentsDTO.setPrintPerCourseAmount(salaryDetails.get("wage_rate_per_type").toString());
				}
				if(salaryDetails.get("pay_scale_type").toString().equalsIgnoreCase("PER HOUR")) {
					finalInterviewCommentsDTO.printPerHourAmount = salaryDetails.get("wage_rate_per_type").toString();
				}
				if(salaryDetails.get("pay_scale_type").toString().equalsIgnoreCase("PER COURSE")) {
					finalInterviewCommentsDTO.printPerCourseAmount = salaryDetails.get("wage_rate_per_type").toString();
				}	
				if(salaryDetails.get("pay_scale_type").toString().equalsIgnoreCase("DAILY")) {
					finalInterviewCommentsDTO.setDailyAmount(salaryDetails.get("wage_rate_per_type").toString());
				}
				if(salaryDetails.get("pay_scale_type").toString().equalsIgnoreCase("CONSOLIDATED")) {
					finalInterviewCommentsDTO.setConsolidatedAmount(salaryDetails.get("wage_rate_per_type").toString());
				}
			}
		}		
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO convertInterviewScoreDetailsToDTO(List<Tuple> list,FinalInterviewCommentsDTO finalInterviewCommentsDTO) {
		Map<Integer, EmpApplnInterviewTemplateGroupDTO> templateGroupDtoMap =  new HashMap<Integer,EmpApplnInterviewTemplateGroupDTO>();
		Map<Integer, EmpApplnInterviewTemplateGroupDetailsDTO> templateGroupDetailsDtoMap =  new HashMap<Integer,EmpApplnInterviewTemplateGroupDetailsDTO>();
		Map<Integer, String> internalInterviewrs =  new HashMap<Integer,String>();
		Map<Integer, String> externalInterviewrs =  new HashMap<Integer, String>();
		Map<Integer, String> parameterMaxScores =  new HashMap<Integer, String>();
		finalInterviewCommentsDTO.heading = new ArrayList<EmpApplnInterviewTemplateGroupDTO>();
		finalInterviewCommentsDTO.internalInterviewers = new ArrayList<ExModelBaseDTO>();
		finalInterviewCommentsDTO.externalInterviewers = new ArrayList<ExModelBaseDTO>();
		int totalMaxScore = 0;
		for (Tuple tuple : list) {
			String headingId =  !Utils.isNullOrEmpty(tuple.get("group_id")) ? tuple.get("group_id").toString():null;
			String detailId = !Utils.isNullOrEmpty(tuple.get("detail_id")) ? tuple.get("detail_id").toString() : null;
			String headingname = !Utils.isNullOrEmpty( tuple.get("group_heading")) ? tuple.get("group_heading").toString() : null;
			String detailName = !Utils.isNullOrEmpty( tuple.get("detail_parameter")) ? tuple.get("detail_parameter").toString():null;
			String headingOrder =  !Utils.isNullOrEmpty( tuple.get("group_order")) ?  tuple.get("group_order").toString() : null;
			String detailsOrder =  !Utils.isNullOrEmpty(tuple.get("detail_order")) ? tuple.get("detail_order").toString() : null;
			String maxScore =  !Utils.isNullOrEmpty( tuple.get("detail_max_score")) ? tuple.get("detail_max_score").toString() : null;
			String obtainedScore = !Utils.isNullOrEmpty( tuple.get("obtained_score")) ? tuple.get("obtained_score").toString() : null;
			String type =  !Utils.isNullOrEmpty(tuple.get("type")) ? tuple.get("type").toString() : null;
			String universityExternalsId = null;
			String erpUserId = null;
			String empName = null;
			String panelistName = null;
			if(!Utils.isNullOrEmpty(tuple.get("emp_interview_university_externals_id")))
				universityExternalsId = tuple.get("emp_interview_university_externals_id").toString();
			if(!Utils.isNullOrEmpty(tuple.get("erp_users_id")))
				erpUserId = tuple.get("erp_users_id").toString();
			if(!Utils.isNullOrEmpty(tuple.get("emp_name")))
				empName = tuple.get("emp_name").toString();
			if(!Utils.isNullOrEmpty(tuple.get("panelist_name")))
				panelistName = tuple.get("panelist_name").toString();
			parameterMaxScores.put(Integer.parseInt(detailId), maxScore);
			if(templateGroupDtoMap.containsKey(Integer.parseInt(headingId))) {
				EmpApplnInterviewTemplateGroupDTO templateGroupDto = templateGroupDtoMap.get(Integer.parseInt(headingId));
				if(templateGroupDetailsDtoMap.containsKey(Integer.parseInt(detailId))) {
					EmpApplnInterviewTemplateGroupDetailsDTO templateGroupDetailsDto = templateGroupDetailsDtoMap.get(Integer.parseInt(detailId));
					if(obtainedScore != null && !obtainedScore.isEmpty() &&  type!=null && !type.isEmpty()) {
						if(type.equalsIgnoreCase("Internal") && erpUserId!=null && !erpUserId.isEmpty()) {
							InteviewScoreEntryGroupDetailsDTO inteviewScoreEntryGroupDetailsDTO = new InteviewScoreEntryGroupDetailsDTO();
							inteviewScoreEntryGroupDetailsDTO.id = erpUserId;
							inteviewScoreEntryGroupDetailsDTO.obtainedScore = Integer.parseInt(obtainedScore);
							templateGroupDetailsDto.internalInterviewers.add(inteviewScoreEntryGroupDetailsDTO);
							int totalScore = Integer.parseInt(templateGroupDetailsDto.totalScore)+Integer.parseInt(obtainedScore);
							templateGroupDetailsDto.totalScore = String.valueOf(totalScore);
							Collections.sort(templateGroupDetailsDto.internalInterviewers, new Comparator<InteviewScoreEntryGroupDetailsDTO>() {
								@Override
								public int compare(InteviewScoreEntryGroupDetailsDTO o1, InteviewScoreEntryGroupDetailsDTO o2) {
									return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
								}
							});
							if(internalInterviewrs.containsKey(Integer.parseInt(erpUserId))) {
								int overAllTotalScore = Integer.parseInt(internalInterviewrs.get(Integer.parseInt(erpUserId)))+Integer.parseInt(obtainedScore);
								internalInterviewrs.put(Integer.parseInt(erpUserId),String.valueOf(overAllTotalScore));
							}else {
								internalInterviewrs.put(Integer.parseInt(erpUserId),obtainedScore);
								ExModelBaseDTO internalInterviewer = new ExModelBaseDTO();
								internalInterviewer.id = erpUserId;
								internalInterviewer.text = empName;
								finalInterviewCommentsDTO.internalInterviewers.add(internalInterviewer);
							}
						}else if(type.equalsIgnoreCase("External") && ((universityExternalsId!=null && !universityExternalsId.isEmpty()) || !Utils.isNullOrEmpty(erpUserId))){
							String external = !Utils.isNullOrEmpty(universityExternalsId) ? universityExternalsId : erpUserId;
							InteviewScoreEntryGroupDetailsDTO inteviewScoreEntryGroupDetailsDTO = new InteviewScoreEntryGroupDetailsDTO();
							inteviewScoreEntryGroupDetailsDTO.id = external;
							inteviewScoreEntryGroupDetailsDTO.obtainedScore = Integer.parseInt(obtainedScore);
							templateGroupDetailsDto.externalInterviewers.add(inteviewScoreEntryGroupDetailsDTO);
							int totalScore = Integer.parseInt(templateGroupDetailsDto.totalScore)+Integer.parseInt(obtainedScore);
							templateGroupDetailsDto.totalScore = String.valueOf(totalScore);
							Collections.sort(templateGroupDetailsDto.externalInterviewers, new Comparator<InteviewScoreEntryGroupDetailsDTO>() {
								@Override
								public int compare(InteviewScoreEntryGroupDetailsDTO o1, InteviewScoreEntryGroupDetailsDTO o2) {
									return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
								}
							});
							if(externalInterviewrs.containsKey(Integer.parseInt(external)) || externalInterviewrs.containsKey(parameterMaxScores)) {
								int overAllTotalScore = Integer.parseInt(externalInterviewrs.get(Integer.parseInt(external)))+Integer.parseInt(obtainedScore);
								externalInterviewrs.put(Integer.parseInt(external),String.valueOf(overAllTotalScore));
							}else {
								externalInterviewrs.put(Integer.parseInt(external),obtainedScore);
								ExModelBaseDTO externalInterviewer = new ExModelBaseDTO();
								externalInterviewer.id = external;
								externalInterviewer.text = !Utils.isNullOrEmpty(panelistName)? panelistName:empName;
								finalInterviewCommentsDTO.externalInterviewers.add(externalInterviewer);
							}
						}
					}
				}else {
					EmpApplnInterviewTemplateGroupDetailsDTO templateGroupDetailsDto = new EmpApplnInterviewTemplateGroupDetailsDTO();
					templateGroupDto.templateGroupHeading = headingname;
					templateGroupDto.id = headingId;
					templateGroupDto.headingOrderNo = headingOrder;	
					templateGroupDetailsDto.id = detailId;
					templateGroupDetailsDto.parameterName = detailName;
					templateGroupDetailsDto.parameterMaxScore = maxScore;
					templateGroupDetailsDto.parameterOrderNo = detailsOrder;
					templateGroupDetailsDto.internalInterviewers = new ArrayList<InteviewScoreEntryGroupDetailsDTO>();
					templateGroupDetailsDto.externalInterviewers = new ArrayList<InteviewScoreEntryGroupDetailsDTO>();
					templateGroupDetailsDto.totalScore = obtainedScore;
					if(obtainedScore != null && !obtainedScore.isEmpty() && type!=null && !type.isEmpty()) {
						if(type.equalsIgnoreCase("Internal") && erpUserId!=null && !erpUserId.isEmpty()) {
							InteviewScoreEntryGroupDetailsDTO inteviewScoreEntryGroupDetailsDTO = new InteviewScoreEntryGroupDetailsDTO();
							inteviewScoreEntryGroupDetailsDTO.id = erpUserId;
							inteviewScoreEntryGroupDetailsDTO.obtainedScore = Integer.parseInt(obtainedScore);
							templateGroupDetailsDto.internalInterviewers.add(inteviewScoreEntryGroupDetailsDTO);
							Collections.sort(templateGroupDetailsDto.internalInterviewers, new Comparator<InteviewScoreEntryGroupDetailsDTO>() {
								@Override
								public int compare(InteviewScoreEntryGroupDetailsDTO o1, InteviewScoreEntryGroupDetailsDTO o2) {
									return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
								}
							});
							if(internalInterviewrs.containsKey(Integer.parseInt(erpUserId))) {
								int overAllTotalScore = Integer.parseInt(internalInterviewrs.get(Integer.parseInt(erpUserId)))+Integer.parseInt(obtainedScore);
								internalInterviewrs.put(Integer.parseInt(erpUserId),String.valueOf(overAllTotalScore));
							}else {
								internalInterviewrs.put(Integer.parseInt(erpUserId),obtainedScore);
								ExModelBaseDTO internalInterviewer = new ExModelBaseDTO();
								internalInterviewer.id = erpUserId;
								internalInterviewer.text = empName;
								finalInterviewCommentsDTO.internalInterviewers.add(internalInterviewer);
							}
						}else if(type.equalsIgnoreCase("External") && ((universityExternalsId!=null && !universityExternalsId.isEmpty()) || !Utils.isNullOrEmpty(erpUserId))) {
							String external = !Utils.isNullOrEmpty(universityExternalsId) ? universityExternalsId : erpUserId;
							InteviewScoreEntryGroupDetailsDTO inteviewScoreEntryGroupDetailsDTO = new InteviewScoreEntryGroupDetailsDTO();
							inteviewScoreEntryGroupDetailsDTO.id = external;
							inteviewScoreEntryGroupDetailsDTO.obtainedScore = Integer.parseInt(obtainedScore);
							templateGroupDetailsDto.externalInterviewers.add(inteviewScoreEntryGroupDetailsDTO);
							Collections.sort(templateGroupDetailsDto.externalInterviewers, new Comparator<InteviewScoreEntryGroupDetailsDTO>() {
								@Override
								public int compare(InteviewScoreEntryGroupDetailsDTO o1, InteviewScoreEntryGroupDetailsDTO o2) {
									return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
								}
							});
							if(externalInterviewrs.containsKey(Integer.parseInt(external))) {
								int overAllTotalScore = Integer.parseInt(externalInterviewrs.get(Integer.parseInt(external)))+Integer.parseInt(obtainedScore);
								externalInterviewrs.put(Integer.parseInt(external),String.valueOf(overAllTotalScore));
							}else {
								externalInterviewrs.put(Integer.parseInt(external),obtainedScore);
								ExModelBaseDTO externalInterviewer = new ExModelBaseDTO();
								externalInterviewer.id = external;
								externalInterviewer.text = !Utils.isNullOrEmpty(panelistName)? panelistName:empName;
								finalInterviewCommentsDTO.externalInterviewers.add(externalInterviewer);
							}
						}
					}
					templateGroupDto.parameters.add(templateGroupDetailsDto);
					templateGroupDetailsDtoMap.put(Integer.parseInt(detailId), templateGroupDetailsDto);
				}
				templateGroupDtoMap.put(Integer.parseInt(headingId), templateGroupDto);
			}else {
				EmpApplnInterviewTemplateGroupDTO templateGroupDto = new EmpApplnInterviewTemplateGroupDTO();
				templateGroupDto.parameters =  new ArrayList<EmpApplnInterviewTemplateGroupDetailsDTO>();
				EmpApplnInterviewTemplateGroupDetailsDTO templateGroupDetailsDto = new EmpApplnInterviewTemplateGroupDetailsDTO();
				templateGroupDto.templateGroupHeading = headingname;
				templateGroupDto.id = headingId;
				templateGroupDto.headingOrderNo = headingOrder;	
				templateGroupDetailsDto.id = detailId;
				templateGroupDetailsDto.parameterName = detailName;
				templateGroupDetailsDto.parameterMaxScore = maxScore;
				templateGroupDetailsDto.parameterOrderNo = detailsOrder;
				templateGroupDetailsDto.internalInterviewers = new ArrayList<InteviewScoreEntryGroupDetailsDTO>();
				templateGroupDetailsDto.externalInterviewers = new ArrayList<InteviewScoreEntryGroupDetailsDTO>();
				templateGroupDetailsDto.totalScore = obtainedScore;
				if(obtainedScore != null && !obtainedScore.isEmpty() && type!=null && !type.isEmpty()) {
					if(type.equalsIgnoreCase("Internal") && erpUserId!=null && !erpUserId.isEmpty()) {
						InteviewScoreEntryGroupDetailsDTO inteviewScoreEntryGroupDetailsDTO = new InteviewScoreEntryGroupDetailsDTO();
						inteviewScoreEntryGroupDetailsDTO.id = erpUserId;
						inteviewScoreEntryGroupDetailsDTO.obtainedScore = Integer.parseInt(obtainedScore);
						templateGroupDetailsDto.internalInterviewers.add(inteviewScoreEntryGroupDetailsDTO);
						Collections.sort(templateGroupDetailsDto.internalInterviewers, new Comparator<InteviewScoreEntryGroupDetailsDTO>() {
							@Override
							public int compare(InteviewScoreEntryGroupDetailsDTO o1, InteviewScoreEntryGroupDetailsDTO o2) {
								return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
							}
						});
						if(internalInterviewrs.containsKey(Integer.parseInt(erpUserId))) {
							int overAllTotalScore = Integer.parseInt(internalInterviewrs.get(Integer.parseInt(erpUserId)))+Integer.parseInt(obtainedScore);
							internalInterviewrs.put(Integer.parseInt(erpUserId),String.valueOf(overAllTotalScore));
						}else {
							internalInterviewrs.put(Integer.parseInt(erpUserId),obtainedScore);
							ExModelBaseDTO internalInterviewer = new ExModelBaseDTO();
							internalInterviewer.id = erpUserId;
							internalInterviewer.text = empName;
							finalInterviewCommentsDTO.internalInterviewers.add(internalInterviewer);
						}
					}else if(type.equalsIgnoreCase("External") &&((universityExternalsId!=null && !universityExternalsId.isEmpty()) || !Utils.isNullOrEmpty(erpUserId))) {
						String external = !Utils.isNullOrEmpty(universityExternalsId) ? universityExternalsId : erpUserId;
						InteviewScoreEntryGroupDetailsDTO inteviewScoreEntryGroupDetailsDTO = new InteviewScoreEntryGroupDetailsDTO();
						inteviewScoreEntryGroupDetailsDTO.id = external ;
						inteviewScoreEntryGroupDetailsDTO.obtainedScore = Integer.parseInt(obtainedScore);
						templateGroupDetailsDto.externalInterviewers.add(inteviewScoreEntryGroupDetailsDTO);
						Collections.sort(templateGroupDetailsDto.externalInterviewers, new Comparator<InteviewScoreEntryGroupDetailsDTO>() {
							@Override
							public int compare(InteviewScoreEntryGroupDetailsDTO o1, InteviewScoreEntryGroupDetailsDTO o2) {
								return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
							}
						});
						if(externalInterviewrs.containsKey(Integer.parseInt(external))) {
							int overAllTotalScore = Integer.parseInt(externalInterviewrs.get(Integer.parseInt(external)))+Integer.parseInt(obtainedScore);
							externalInterviewrs.put(Integer.parseInt(external),String.valueOf(overAllTotalScore));
						}else {
							externalInterviewrs.put(Integer.parseInt(external),obtainedScore);
							ExModelBaseDTO externalInterviewer = new ExModelBaseDTO();
							externalInterviewer.id = external;
							externalInterviewer.text = !Utils.isNullOrEmpty(panelistName)? panelistName:empName;
							finalInterviewCommentsDTO.externalInterviewers.add(externalInterviewer);
						}
					}
				}
				templateGroupDto.parameters.add(templateGroupDetailsDto);
				templateGroupDetailsDtoMap.put(Integer.parseInt(detailId), templateGroupDetailsDto);
				templateGroupDtoMap.put(Integer.parseInt(headingId), templateGroupDto);
				finalInterviewCommentsDTO.heading.add(templateGroupDto);
			}
		}
		finalInterviewCommentsDTO.internalInterviewersCount = new ArrayList<ExModelBaseDTO>();
		finalInterviewCommentsDTO.externalInterviewersCount = new ArrayList<ExModelBaseDTO>();
		for (Entry<Integer, String> interviewersMap : internalInterviewrs.entrySet()) {
			ExModelBaseDTO dto = new ExModelBaseDTO();
			dto.id = interviewersMap.getKey().toString();
			dto.text = interviewersMap.getValue().toString();
			finalInterviewCommentsDTO.internalInterviewersCount.add(dto);
		}
		for (Entry<Integer, String> externalInterviewersMap : externalInterviewrs.entrySet()) {
			ExModelBaseDTO dto = new ExModelBaseDTO();
			dto.id = externalInterviewersMap.getKey().toString();
			dto.text = externalInterviewersMap.getValue().toString();
			finalInterviewCommentsDTO.externalInterviewersCount.add(dto);
		}
		for (Map.Entry<Integer, String> entry : parameterMaxScores.entrySet()) {
			totalMaxScore = totalMaxScore+ Integer.parseInt(entry.getValue());
		}
		Collections.sort(finalInterviewCommentsDTO.internalInterviewers, new Comparator<ExModelBaseDTO>() {
			@Override
			public int compare(ExModelBaseDTO o1, ExModelBaseDTO o2) {
				return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
			}
		});
		Collections.sort(finalInterviewCommentsDTO.externalInterviewers, new Comparator<ExModelBaseDTO>() {
			@Override
			public int compare(ExModelBaseDTO o1, ExModelBaseDTO o2) {
				return Integer.compare(Integer.parseInt(o1.id), Integer.parseInt(o2.id));
			}
		});
		finalInterviewCommentsDTO.totalMaxScore = String.valueOf(totalMaxScore);
		return finalInterviewCommentsDTO;
	}

	public String replaceTemplateTagData1(String applnNumber, String templateId, String selectionStatus) {
		String template = null;
		if(!Utils.isNullOrEmpty(templateId)) {
			ErpTemplateDBO erpTemplateDBO = finalInterviewCommentsTransaction1.getOfferLetterTemplateById(Integer.parseInt(templateId));
			if(!Utils.isNullOrEmpty(erpTemplateDBO)) {		
				EmpApplnEntriesDBO dbo = finalInterviewCommentsTransaction1.getEmpApplnEntries(Integer.parseInt(applnNumber));
				var payScaleDBO =finalInterviewCommentsTransaction1.getEmpPayScaleDetails(dbo.getId());
				template = erpTemplateDBO.templateContent;
				if(selectionStatus.equalsIgnoreCase("No")) {
					if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
						template = template.replace("[EMP_APPLICANT_NAME]", dbo.getApplicantName());
					}
					if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
						template = template.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
					}
				} else {
					if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
						template = template.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
					}
					if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
						template = template.replace("[EMP_APPLICANT_NAME]", dbo.getApplicantName());
					}
					if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO())) {
							if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName())) {
								template = template.replace("[DEPARTMENT]", dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName());
							}
						}
						if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO())) {
							if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName())) {
								template = template.replace("[CAMPUS]", dbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
							}
						}
					}
					if(!Utils.isNullOrEmpty(dbo.getEmpDesignationDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getEmpDesignationDBO().getEmpDesignationName())) {
							template = template.replace("[DESIGNATION]", dbo.getEmpDesignationDBO().getEmpDesignationName());
						}
					}
					if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO().getJoiningDate())) {
							template = template.replace("[DATE_OF_JOIN]", Utils.convertLocalDateTimeToStringDateTime(dbo.getEmpJobDetailsDBO().getJoiningDate()));
						}
						if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO().getReportingDate())) {
							template = template.replace("[REPORTING_DATE]", Utils.convertLocalDateTimeToStringDateTime(dbo.getEmpJobDetailsDBO().getReportingDate()));
						}
					}
					if(!Utils.isNullOrEmpty(dbo.getEmpApplnSubjectCategoryDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getEmpApplnSubjectCategoryDBO().getSubjectCategory())) {
							template = template.replace("[SUBJECT_CATEGORY_NAME]",dbo.getEmpApplnSubjectCategoryDBO().getSubjectCategory());	
						}
					}
					if(!Utils.isNullOrEmpty(dbo.getEmpApplnSubjectCategorySpecializationDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getEmpApplnSubjectCategorySpecializationDBO().getSubjectCategorySpecializationName())) {
							template = template.replace("[SUBJECT_CATEGORY_SPECIALIZATION]",dbo.getEmpApplnSubjectCategorySpecializationDBO().getSubjectCategorySpecializationName());	
						}
					}
					if(!Utils.isNullOrEmpty(payScaleDBO)) {
						if(!Utils.isNullOrEmpty(payScaleDBO.getPayScaleType())) {
							if(payScaleDBO.getPayScaleType().equalsIgnoreCase("SCALE PAY")) {
								if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO()) && !Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getEmpPayScaleGradeMappingDetailDBO())) {
									if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getEmpPayScaleGradeMappingDetailDBO().getPayScale())) {
										template = template.replace("[SCALE_PAY]",payScaleDBO.getEmpPayScaleMatrixDetailDBO().getEmpPayScaleGradeMappingDetailDBO().getPayScale());
									}
								}
							}
							if(!Utils.isNullOrEmpty(payScaleDBO.getWageRatePerType())) {
								if(payScaleDBO.getPayScaleType().equalsIgnoreCase("PER HOUR")) {
									template = template.replace("[PER_HOUR_AMOUNT]",String.valueOf(payScaleDBO.getWageRatePerType()));	
								}
								if(payScaleDBO.getPayScaleType().equalsIgnoreCase("PER COURSE")) {
									template = template.replace("[PER_COURSE_AMOUNT]",String.valueOf(payScaleDBO.getWageRatePerType()));	
								}
								if(payScaleDBO.getPayScaleType().equalsIgnoreCase("CONSOLIDATED")) {
									template = template.replace("[CONSOLIDATED_AMOUNT]",String.valueOf(payScaleDBO.getWageRatePerType()));	
								}
								if(payScaleDBO.getPayScaleType().equalsIgnoreCase("DAILY")) {
									template = template.replace("[DAILY_AMOUNT]",String.valueOf(payScaleDBO.getWageRatePerType()));	
								}
							}
						}
//						if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleDetailsComponentsDBOs())) {   emp_pay_scale_matrix_details having cell value.
//							List<BigDecimal> value = new ArrayList<BigDecimal>();
//							payScaleDBO.getEmpPayScaleDetailsComponentsDBOs().stream().filter(s-> 
//							s.getEmpPayScaleComponentsDBO().getIsComponentBasic().equals(true) && s.getRecordStatus().equals("A")).peek(s-> {
//								value.add(s.getEmpSalaryComponentValue());	  				
//							});	
//							if(!value.isEmpty()) {
//								template = template.replace("[BASIC]",String.valueOf(value.get(0)));
//							} 
//						}
						if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO())) {
							if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getLevelCellValue())) {
								template = template.replace("[BASIC]",String.valueOf(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getLevelCellValue()));
							}
						}
						if(!Utils.isNullOrEmpty(payScaleDBO.getGrossPay())) {
							template = template.replace("[GROSS_PAY]",String.valueOf(payScaleDBO.getGrossPay()));	
						}
						if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO()) && !Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getEmpPayScaleGradeMappingDetailDBO())
								&& !Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getEmpPayScaleGradeMappingDetailDBO().getEmpPayScaleLevelDBO())) {
							if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getEmpPayScaleGradeMappingDetailDBO().getEmpPayScaleLevelDBO().getEmpPayScaleLevel())) {
								template = template.replace("[LEVEL]",String.valueOf(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getEmpPayScaleGradeMappingDetailDBO().getEmpPayScaleLevelDBO().getEmpPayScaleLevel()));	
							}
							if(!Utils.isNullOrEmpty(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getLevelCellNo())) {
								template = template.replace("[CELL]",String.valueOf(payScaleDBO.getEmpPayScaleMatrixDetailDBO().getLevelCellNo()));	
							}
						}
					}			
				}
			}
		}
		return template;
	} 

	/*public List<Object> smsEmailSend(List<Object> objects,String str,EmpApplnEntriesDBO applnEntriesDBO,String userId,Set<Integer> userIdSet,List<String> templateTypes,FinalInterviewCommentsDTO finalInterviewCommentsDTO) {
	Integer workFlowProcessId = commonApiTransaction1.getWorkFlowProcessId(str);
	if(!Utils.isNullOrEmpty(workFlowProcessId)) {
		applnEntriesDBO.applicantCurrentProcessStatus = new ErpWorkFlowProcessDBO();
		applnEntriesDBO.applicantCurrentProcessStatus.id = workFlowProcessId;
		applnEntriesDBO.applicationCurrentProcessStatus = new ErpWorkFlowProcessDBO();
		applnEntriesDBO.applicationCurrentProcessStatus.id = workFlowProcessId;
	}
	List<Tuple> templates = null;
	if(str.equalsIgnoreCase("R2_SELECTED")) {
		templates = finalInterviewCommentsTransaction1.getR2SelectTemplate();
	}
	if(str.equalsIgnoreCase("R2_NOT_SELECTED")) {
		boolean value = false;
		templates = finalInterviewCommentsTransaction1.getTempalteForSelectNo(value);
	}
	if(str.equalsIgnoreCase("EMP_SELECTED")) {
		boolean value = true;
		templates = finalInterviewCommentsTransaction1.getTempalteForSelectNo(value);
	}		
	if(!Utils.isNullOrEmpty(templates)) {
		List<Object> objList = new ArrayList<Object>();
		templates.forEach(data -> {
			if(!Utils.isNullOrEmpty(data.get("smsTemplateID"))) {
				templateTypes.add("SMS");
				List<ErpSmsDBO> smsList = new ArrayList<ErpSmsDBO>();
				ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
				erpSmsDBO.entryId = applnEntriesDBO.getId();
				ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
				erpUsersDBO.id = Integer.parseInt(userId);
				userIdSet.add(Integer.parseInt(userId));
				erpSmsDBO.erpUsersDBO = erpUsersDBO;
				String msgBody= null;					
				Object content = data.get("smsTemplateContent");
				if (content instanceof String) {
					msgBody = (String) content;
				} else if (content instanceof Blob) {
					Blob blob = (Blob) content;
					byte[] bytes;
					try {
						bytes = blob.getBytes(1, (int) blob.length());
						String msg = new String(bytes);
						String name = applnEntriesDBO.applicantName.toString();
						msgBody = msg.replace("[EMP_APPLICANT_NAME]", name);
						msgBody = msgBody.replace("[APPLICATION_NO]", applnEntriesDBO.getApplicationNo().toString());
					} catch (SQLException e) {
						e.printStackTrace();
					}					
				}
				erpSmsDBO.smsContent = msgBody;
				erpSmsDBO.recipientMobileNo = applnEntriesDBO.getMobileNo();
				erpSmsDBO.createdUsersId = Integer.parseInt(userId);
				erpSmsDBO.recordStatus = 'A';
				objList.add(erpSmsDBO);
				smsList.add(erpSmsDBO);
				if(str.equalsIgnoreCase("R2_SELECTED")) 
					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(applnEntriesDBO.getApplicationCurrentProcessStatus().getId(),"EMP_APPLN_R2_SELECTED",userIdSet,null,smsList,null);
				if(str.equalsIgnoreCase("R2_NOT_SELECTED")) 
					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(applnEntriesDBO.getApplicationCurrentProcessStatus().getId(),"EMP_APPLN_R2_NOT_SELECTED",userIdSet,null,smsList,null);
				if(str.equalsIgnoreCase("EMP_SELECTED")) 
					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(applnEntriesDBO.getApplicationCurrentProcessStatus().getId(),"EMP_SELECTED",userIdSet,null,smsList,null);
			}
			if(!Utils.isNullOrEmpty(data.get("emailTemplateId"))) {
				templateTypes.add("EMAIL");
				List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
				ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
				erpEmailsDBO.entryId = applnEntriesDBO.getId();
				ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
				erpUsersDBO.id = Integer.parseInt(userId);
				userIdSet.add(Integer.parseInt(userId));
				erpEmailsDBO.erpUsersDBO = erpUsersDBO;
				String msgBody= null;					
				Object content = data.get("emailTemplateContent");
				if (content instanceof String) {
					msgBody = (String) content;
				} else if (content instanceof Blob) {
					Blob blob = (Blob) content;
					byte[] bytes;
					try {
						bytes = blob.getBytes(1, (int) blob.length());
						String msg = new String(bytes);
						msgBody = msg.replace("[EMP_APPLICANT_NAME]", applnEntriesDBO.getApplicantName().toString());
						msgBody = msg.replace("[APPLICATION_NO] ", applnEntriesDBO.getApplicationNo().toString());
					} catch (SQLException e) {
						e.printStackTrace();
					}					
				}
				erpEmailsDBO.emailContent = msgBody;
				erpEmailsDBO.emailSubject = !Utils.isNullOrEmpty(data.get("mailSubject").toString()) ? data.get("mailSubject").toString() : "";
				erpEmailsDBO.senderName = !Utils.isNullOrEmpty(data.get("mailFromName").toString()) ? data.get("mailFromName").toString() : "";
				erpEmailsDBO.recipientEmail=applnEntriesDBO.getPersonalEmailId();
				erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
				erpEmailsDBO.recordStatus = 'A';
				objList.add(erpEmailsDBO);
				emailsList.add(erpEmailsDBO);
				if(str.equalsIgnoreCase("R2_SELECTED")) 
					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(applnEntriesDBO.applicationCurrentProcessStatus.id,"EMP_APPLN_R2_SELECTED",userIdSet,null,null,emailsList);
				if(str.equalsIgnoreCase("R2_NOT_SELECTED")) 
					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(applnEntriesDBO.applicationCurrentProcessStatus.id,"EMP_APPLN_R2_NOT_SELECTED",userIdSet,null,null,emailsList);	
				if(str.equalsIgnoreCase("EMP_SELECTED")) 
					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(applnEntriesDBO.applicationCurrentProcessStatus.id,"EMP_SELECTED",userIdSet,null,null,emailsList);
			}
		});
		if(!Utils.isNullOrEmpty(objList)) {
			if(!Utils.isNullOrEmpty(objList.get(0))){
				objects.add(objList.get(0));
			}
			if(objList.size() == 2){
				objects.add(objList.get(1));
			}
		}

	}
	return objects;
}
	 */
	/* this method is no longer needed it will create as new
public EmpApplnEntriesDBO templateContent(FinalInterviewCommentsDTO finalInterviewCommentsDTO,EmpApplnEntriesDBO applnEntriesDBO,String userId,List<Object> objects) {
	if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.isSelected)) {
		List<String> templateTypes = new ArrayList<String>();
		Set<Integer> userIdSet = new LinkedHashSet<Integer>();
		if(!finalInterviewCommentsDTO.isSelected) {
			String str = "R2_NOT_SELECTED";
			objects = this.smsEmailSend(objects, str, applnEntriesDBO, userId, userIdSet, templateTypes,finalInterviewCommentsDTO);
		}else if(finalInterviewCommentsDTO.isSelected && Utils.isNullOrEmpty(finalInterviewCommentsDTO.vcComments) ){ 
			String str = "R2_SELECTED";
			objects = this.smsEmailSend(objects, str, applnEntriesDBO, userId, userIdSet, templateTypes,finalInterviewCommentsDTO);
		}else if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.vcComments) && finalInterviewCommentsDTO.isSelected) {
			String str = "EMP_SELECTED";
			objects = this.smsEmailSend(objects, str, applnEntriesDBO, userId, userIdSet, templateTypes,finalInterviewCommentsDTO);
		}
	}
	return applnEntriesDBO;
}
	 */
	//	public FinalInterviewCommentsDTO convertTotalExperienceDTO(Tuple totalExperience,FinalInterviewCommentsDTO finalInterviewCommentsDTO) throws Exception {
	//	if(!Utils.isNullOrEmpty(totalExperience)) {
	//		if(!Utils.isNullOrEmpty(totalExperience.get("experience_yearss")))
	//			finalInterviewCommentsDTO.printTotalExpYear = totalExperience.get("experience_yearss").toString();
	//		if(!Utils.isNullOrEmpty(totalExperience.get("experience_monthss")))
	//			finalInterviewCommentsDTO.printTotalExpMonth = totalExperience.get("experience_monthss").toString();
	//	}
	//	return finalInterviewCommentsDTO;
	//}

	//	public String replaceTemplateTagData(String templateContent, FinalInterviewCommentsDTO dto, Tuple applnEntrie,EmpPayScaleDetailsDBO salaryDetails,Tuple jobDetails,Tuple salaryDetailsByPayScaleType) {
	//	String template = templateContent;
	//	if(dto.getSelectionStatus().equalsIgnoreCase("No"))  {
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("applnName")))
	//			template = template.replace("[EMP_APPLICANT_NAME]", applnEntrie.get("applnName").toString());
	//	} else {
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("applnName")))
	//			template = template.replace("[EMP_APPLICANT_NAME]", applnEntrie.get("applnName").toString());
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("applnNo")))
	//			template = template.replace("[APPLICATION_NO]", applnEntrie.get("applnNo").toString());
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("erpCampusName")))
	//			template = template.replace("[CAMPUS]", applnEntrie.get("erpCampusName").toString());
	////		if(!Utils.isNullOrEmpty(applnEntrie.get("erpDepartmentName")))
	////			template = template.replace("[DEPARTMENT]", applnEntrie.get("erpDepartmentName").toString());
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("employeeCategoryName")))
	//			template = template.replace("[EMPLOYEE_CATEGORY]", applnEntrie.get("employeeCategoryName").toString());
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("empDesignationName")))
	//			template = template.replace("[DESIGNATION]", applnEntrie.get("empDesignationName").toString());
	//		if(!Utils.isNullOrEmpty(jobDetails.get("joiningDate"))){
	//			String originalString = jobDetails.get("joiningDate").toString();;
	//			LocalDateTime date = Utils.convertStringDateTimeToLocalDateTime(originalString);
	//			String joiningDateAndTime = Utils.convertLocalDateTimeToStringDateTime(date);
	//			Calendar cal = Calendar.getInstance();
	//			if(cal.get(Calendar.AM_PM)==0)
	//				joiningDateAndTime=joiningDateAndTime+" AM";
	//			else
	//				joiningDateAndTime=joiningDateAndTime+" PM";
	//			template = template.replace("[DATE_OF_JOIN]", joiningDateAndTime);
	//		}
	//		if(!Utils.isNullOrEmpty(jobDetails.get("reportingDate"))){
	//			String originalString = jobDetails.get("reportingDate").toString();;
	//			Calendar cal = Calendar.getInstance();
	//			LocalDateTime date = Utils.convertStringDateTimeToLocalDateTime(originalString);
	//			String reportingDateAndTime = Utils.convertLocalDateTimeToStringDateTime(date);
	//			if(cal.get(Calendar.AM_PM)==0)
	//				reportingDateAndTime=reportingDateAndTime+" AM";
	//			else
	//				reportingDateAndTime=reportingDateAndTime+" PM";
	//			template = template.replace("[REPORTING_DATE]", reportingDateAndTime);
	//		}
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("empApplnSubjectCategoryName")))
	//			template = template.replace("[SUBJECT_CATEGORY_NAME]", applnEntrie.get("empApplnSubjectCategoryName").toString());
	//		if(!Utils.isNullOrEmpty(applnEntrie.get("empApplnSubjectCategorySpecializationName"))) 
	//			template = template.replace("[SUBJECT_CATEGORY_SPECIALIZATION]", applnEntrie.get("empApplnSubjectCategorySpecializationName").toString());
	//		if(!Utils.isNullOrEmpty(salaryDetails)) {
	//			if(!Utils.isNullOrEmpty(salaryDetails.get("basic_value"))) {
	//				template = template.replace("[BASIC]", salaryDetails.get("basic_value").toString());
	//			}	
	//			if(!Utils.isNullOrEmpty(salaryDetails.get("gross_pay"))) {
	//				template = template.replace("[GROSS_PAY]", salaryDetails.get("gross_pay").toString());
	//			}
	//			if(!Utils.isNullOrEmpty(salaryDetails.get("pay_scale_type"))) {
	//				template = template.replace("[PAY_SCALE_TYPE]", salaryDetails.get("pay_scale_type").toString());
	//			}
	//		} else if(!Utils.isNullOrEmpty(salaryDetailsByPayScaleType)){
	//			if(!Utils.isNullOrEmpty(salaryDetailsByPayScaleType.get("pay_scale_type"))) {
	//				template = template.replace("[PAY_SCALE_TYPE]", salaryDetailsByPayScaleType.get("pay_scale_type").toString());
	//				if(salaryDetailsByPayScaleType.get("pay_scale_type").toString().equalsIgnoreCase("PER HOUR") && !Utils.isNullOrEmpty(salaryDetailsByPayScaleType.get("wage_rate_per_type"))) {
	//					template = template.replace("[PER_HOUR_AMOUNT]", salaryDetailsByPayScaleType.get("wage_rate_per_type").toString());
	//				}
	//				if(salaryDetailsByPayScaleType.get("pay_scale_type").toString().equalsIgnoreCase("PER COURSE") && !Utils.isNullOrEmpty(salaryDetailsByPayScaleType.get("wage_rate_per_type"))) {
	//					template = template.replace("[PER_COURSE_AMOUNT]", salaryDetailsByPayScaleType.get("wage_rate_per_type").toString());
	//				}
	//			}
	//		}
	//	}
	//	return template;
	//}
}