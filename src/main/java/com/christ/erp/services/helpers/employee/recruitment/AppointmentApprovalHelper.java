package com.christ.erp.services.helpers.employee.recruitment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDesignationDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeGroupDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.ErpEmployeeTitleDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleMatrixDetailDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.AppointmentApprovalDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.handlers.employee.common.CommonEmployeeHandler;
import com.christ.erp.services.transactions.employee.recruitment.AppointmentApprovalTransaction;
@Service
public class AppointmentApprovalHelper {
//	public static volatile AppointmentApprovalHelper appointmentApprovalHelper = null;
//	public static AppointmentApprovalHelper getInstance() {
//		if(appointmentApprovalHelper== null) 
//			appointmentApprovalHelper =  new AppointmentApprovalHelper();
//		return appointmentApprovalHelper;
//	}
	@Autowired
	CommonEmployeeHandler commonEmployeeHandler;
	@Autowired
	AppointmentApprovalTransaction  appointmentApprovalTransaction;
	
	public void setEmpPayScaleDetailsDBOtoDTO(AppointmentApprovalDTO dto, EmpPayScaleDetailsDBO payScaleDetailsDBO) {
		if(!Utils.isNullOrEmpty(payScaleDetailsDBO.payScaleType)) {
			dto.radioButton = payScaleDetailsDBO.payScaleType;
			if(payScaleDetailsDBO.payScaleType.equals("SCALE PAY")) {
				if(!Utils.isNullOrEmpty(payScaleDetailsDBO.empPayScaleMatrixDetailDBO)) {
					ExModelBaseDTO  cell= new ExModelBaseDTO();
					cell.id = !Utils.isNullOrEmpty(payScaleDetailsDBO.empPayScaleMatrixDetailDBO) ? String.valueOf(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.id) : "";
					dto.cell = cell;
					if(!Utils.isNullOrEmpty(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO) 
							&& !Utils.isNullOrEmpty(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO.empPayScaleGradeMappingDBO)){
								ExModelBaseDTO  level= new ExModelBaseDTO();
								level.id = !Utils.isNullOrEmpty(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO.id) 
										     ? String.valueOf(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO.id) : "";
								dto.level = level;
								if(!Utils.isNullOrEmpty(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO.empPayScaleGradeMappingDBO.empPayScaleGradeDBO)) {
									ExModelBaseDTO  grade= new ExModelBaseDTO();
									grade.id = !Utils.isNullOrEmpty(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO.empPayScaleGradeMappingDBO.empPayScaleGradeDBO.id) 
											       ? String.valueOf(payScaleDetailsDBO.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO.empPayScaleGradeMappingDBO.empPayScaleGradeDBO.id) : "";
									dto.grade = grade;
							   }
					}
				}
				if(!Utils.isNullOrEmpty(payScaleDetailsDBO.grossPay)) {
					dto.grossyPay = !Utils.isNullOrEmpty(payScaleDetailsDBO.grossPay) ? String.valueOf(payScaleDetailsDBO.grossPay):"";
				}
			}
			else if(payScaleDetailsDBO.payScaleType.equals("PER HOUR")) {
				dto.perHour =!Utils.isNullOrEmpty(payScaleDetailsDBO.wageRatePerType) ? String.valueOf(payScaleDetailsDBO.wageRatePerType) : "";
			}
			else if(payScaleDetailsDBO.payScaleType.equals("PER COURSE")) {
				dto.perCourse = !Utils.isNullOrEmpty(payScaleDetailsDBO.wageRatePerType) ? String.valueOf(payScaleDetailsDBO.wageRatePerType) : "";
			}
			else if(payScaleDetailsDBO.payScaleType.equals("DAILY")) {
				dto.dailyAmount =!Utils.isNullOrEmpty(payScaleDetailsDBO.wageRatePerType) ? String.valueOf(payScaleDetailsDBO.wageRatePerType) : "";
				dto.grossyPay = !Utils.isNullOrEmpty(payScaleDetailsDBO.grossPay) ? String.valueOf(payScaleDetailsDBO.grossPay):"";
			}
			else if(payScaleDetailsDBO.payScaleType.equals("CONSOLIDATED")) {
				dto.consolidateAmount =!Utils.isNullOrEmpty(payScaleDetailsDBO.wageRatePerType) ? String.valueOf(payScaleDetailsDBO.wageRatePerType) : "";
				if(!Utils.isNullOrEmpty(payScaleDetailsDBO.grossPay)) {
					dto.grossyPay = !Utils.isNullOrEmpty(payScaleDetailsDBO.grossPay) ? String.valueOf(payScaleDetailsDBO.grossPay):"";
				}
			}
		}	
	}
	
	public void setEmpPayScaleDetailsComponentsDBOstoDTO(AppointmentApprovalDTO dto, List<EmpPayScaleDetailsComponentsDBO> detailsComponentsDBO) {
		SalaryComponentDTO componentDTO = null;
		List<SalaryComponentDTO> componentDTOList = new ArrayList<SalaryComponentDTO>();
		for (EmpPayScaleDetailsComponentsDBO empPayScaleDetailsComponentsDBO : detailsComponentsDBO) {
			componentDTO = new SalaryComponentDTO();
			if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO)) {
				componentDTO.id = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.id) ? String.valueOf(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.id) : "";
				componentDTO.allowanceType = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.salaryComponentName) ? empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.salaryComponentName : "";
				componentDTO.mentionPercentage = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.percentage) ? String.valueOf(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.percentage) : "";	
				componentDTO.shortName = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.salaryComponentShortName) ? empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.salaryComponentShortName : "";
				componentDTO.displayOrder = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.salaryComponentDisplayOrder) ? String.valueOf(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.salaryComponentDisplayOrder) : "";
				componentDTO.isBasic = empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.isComponentBasic;
				componentDTO.calculationType = empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.isCalculationTypePercentage;
				componentDTO.payScaleType = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.payScaleType) ? empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.payScaleType : "";
//				componentDTO.amount = String.valueOf(empPayScaleDetailsComponentsDBO.empSalaryComponentValue);
			}
			componentDTO.amount = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empSalaryComponentValue) ? String.valueOf(empPayScaleDetailsComponentsDBO.empSalaryComponentValue) : "";
	        componentDTOList.add(componentDTO);
		}	
		if(!Utils.isNullOrEmpty(componentDTOList)) {
		    dto.payScaleDetailsComponents = componentDTOList;
		}
	}
	
	public AppointmentApprovalDTO setEmpApplnEntriesDBOtoDTO(AppointmentApprovalDTO dto, EmpApplnEntriesDBO dbo) {
		dto = new AppointmentApprovalDTO();	
		dto.id = !Utils.isNullOrEmpty(dbo.id) ? String.valueOf(dbo.id) : "";		
		dto.name = !Utils.isNullOrEmpty(dbo.applicantName) ? dbo.applicantName : "";	
		dto.applicationNumber = !Utils.isNullOrEmpty(dbo.applicationNo) ? String.valueOf(dbo.applicationNo) : "";	
		if(!Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO)){
			ExModelBaseDTO category = new ExModelBaseDTO();
		    category.id = !Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO.id) ? String.valueOf((dbo.empEmployeeCategoryDBO.id)) : "";
		    dto.category = category;
		}
		if(!Utils.isNullOrEmpty(dbo.empEmployeeJobCategoryDBO)) {
			ExModelBaseDTO jobCategory = new ExModelBaseDTO();
			jobCategory.id = !Utils.isNullOrEmpty(dbo.empEmployeeJobCategoryDBO.id) ? String.valueOf((dbo.empEmployeeJobCategoryDBO.id)) : "";
		    dto.jobCategorie = jobCategory;
		}
		if(!Utils.isNullOrEmpty(dbo.erpCampusDBO)) {
			ExModelBaseDTO campus = new ExModelBaseDTO();
		    campus.id = !Utils.isNullOrEmpty(dbo.erpCampusDBO.id) ? String.valueOf(dbo.erpCampusDBO.id) : "";
	        dto.campus = campus;
		}
	    if(!Utils.isNullOrEmpty(dbo.empDesignationDBO)) {
	    	ExModelBaseDTO designation = new ExModelBaseDTO();
            designation.id = !Utils.isNullOrEmpty(dbo.empDesignationDBO.id) ? String.valueOf(dbo.empDesignationDBO.id) : "";
            dto.designation = designation;
	    }
	    if(!Utils.isNullOrEmpty(dbo.empDesignationDBO)) {
	    	ExModelBaseDTO base = new ExModelBaseDTO();
	        base.id = !Utils.isNullOrEmpty(dbo.empDesignationDBO.id) ? String.valueOf(dbo.empDesignationDBO.id) : "";
	        dto.designationForStaffAlbum =  base;
	    }
	    if(!Utils.isNullOrEmpty(dbo.titleId)) {
	    	ExModelBaseDTO title = new ExModelBaseDTO();
            title.id =  !Utils.isNullOrEmpty(dbo.titleId) ?  String.valueOf(dbo.titleId.id) : "";
            dto.title = title;
	    }
	    if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO())) {
	    	dto.setDepartment(new ExModelBaseDTO());
	    	dto.getDepartment().setId(String.valueOf(dbo.getErpCampusDepartmentMappingDBO().getId()));
	    	   if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO())) {
	   	    	dto.getDepartment().setText(dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName());
	    	   }
	    }
		return dto;	
	}

	public EmpDBO createNewEmployeeDTOtoDBO(EmpDBO dbo, AppointmentApprovalDTO dto, String userId) {
		dbo = new EmpDBO();
		if(!Utils.isNullOrEmpty(dto.id)) {
			EmpApplnEntriesDBO applnEntriesDBO =  new EmpApplnEntriesDBO();
			applnEntriesDBO.id = Integer.parseInt(dto.id);
			dbo.empApplnEntriesDBO = applnEntriesDBO;
		}	
		if(!Utils.isNullOrEmpty(dto.name)) {
		 dbo.empName = dto.name.trim();
		}
		if(!Utils.isNullOrEmpty(dto.category.id)) {			
			EmpEmployeeCategoryDBO categoryDBO = new EmpEmployeeCategoryDBO();
			categoryDBO.id = Integer.parseInt(dto.category.id);
			dbo.empEmployeeCategoryDBO = categoryDBO;
		}
		if(!Utils.isNullOrEmpty(dto.jobCategorie.id)) {
			EmpEmployeeJobCategoryDBO jobCategoryDBO = new EmpEmployeeJobCategoryDBO();					
			jobCategoryDBO.id = Integer.parseInt(dto.jobCategorie.id);
			dbo.empEmployeeJobCategoryDBO = jobCategoryDBO;
		}
		if(!Utils.isNullOrEmpty(dto.department.id)) {
			ErpCampusDepartmentMappingDBO campusDepartmentMappingDBO =  new ErpCampusDepartmentMappingDBO();					
			campusDepartmentMappingDBO.id = Integer.parseInt(dto.department.id);
			dbo.erpCampusDepartmentMappingDBO = campusDepartmentMappingDBO;
		}
		if(!Utils.isNullOrEmpty(dto.designation.id)) {
			EmpDesignationDBO designationDBO = new EmpDesignationDBO();					
			designationDBO.id = Integer.parseInt(dto.designation.id);
			dbo.empDesignationDBO = designationDBO;
		}
		if(!Utils.isNullOrEmpty(dto.designationForStaffAlbum.id)) {
			EmpDesignationDBO empAlbumDesignationDBO = new EmpDesignationDBO();					
			empAlbumDesignationDBO.id = Integer.parseInt(dto.designationForStaffAlbum.id);
			dbo.empAlbumDesignationDBO = empAlbumDesignationDBO;
		}
		if(!Utils.isNullOrEmpty(dto.title.id)) {
			ErpEmployeeTitleDBO erpEmployeeTitleDBO = new ErpEmployeeTitleDBO();					
			erpEmployeeTitleDBO.id = Integer.parseInt(dto.title.id);
			dbo.erpEmployeeTitleDBO = erpEmployeeTitleDBO;
		}
		if(!Utils.isNullOrEmpty(dto.employeeGroup.id)) {
			EmpEmployeeGroupDBO empEmployeeGroupDBO = new EmpEmployeeGroupDBO();					
			empEmployeeGroupDBO.id = Integer.parseInt(dto.employeeGroup.id);
			dbo.empEmployeeGroupDBO = empEmployeeGroupDBO;
		}
		dbo.createdUsersId = Integer.parseInt(userId);
		dbo.recordStatus = 'I';
		return dbo;
	}

	public EmpPayScaleDetailsDBO PayScaleDTOtoDBO(int empId, EmpPayScaleDetailsDBO empPayScaleDetailsDBO, AppointmentApprovalDTO dto, String userId,List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsList) throws Exception {
		EmpPayScaleDetailsDBO empPayScaleDetailsDBO1 = new EmpPayScaleDetailsDBO();
		if(Utils.isNullOrEmpty(empPayScaleDetailsDBO)) {
			empPayScaleDetailsDBO1.createdUsersId = Integer.parseInt(userId);
			empPayScaleDetailsDBO1.recordStatus = 'A';
			empPayScaleDetailsDBO1.current = true;
			empPayScaleDetailsDBO1 = convertEmpPayScaleDetailsDTOToDBO(empId,dto,empPayScaleDetailsDBO1,empPayScaleDetailsComponentsList,userId);
		}else if(!Utils.isNullOrEmpty(dto.radioButton) && !Utils.isNullOrEmpty(empPayScaleDetailsDBO)){
			if(dto.radioButton.equalsIgnoreCase(empPayScaleDetailsDBO.payScaleType)) {
				empPayScaleDetailsDBO.modifiedUsersId = Integer.parseInt(userId);
				empPayScaleDetailsDBO = convertEmpPayScaleDetailsDTOToDBO(empId,dto,empPayScaleDetailsDBO,empPayScaleDetailsComponentsList,userId);
				empPayScaleDetailsDBO1 = empPayScaleDetailsDBO;
			}else {
				empPayScaleDetailsDBO.current = false;
				Set<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOs = new HashSet<EmpPayScaleDetailsComponentsDBO>();
				if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsList)) {
					if(empPayScaleDetailsComponentsList != null) {
						empPayScaleDetailsComponentsList.forEach(empPaySub -> {
							empPaySub.recordStatus = 'D';
							empPaySub.modifiedUsersId = Integer.parseInt(userId);
							empPayScaleDetailsComponentsDBOs.add(empPaySub);
						});
						empPayScaleDetailsDBO.empPayScaleDetailsComponentsDBOs = empPayScaleDetailsComponentsDBOs;
					}
				}
				empPayScaleDetailsDBO.recordStatus = 'D';
				appointmentApprovalTransaction.saveOrUpdatePayScaleDetails(empPayScaleDetailsDBO);
				empPayScaleDetailsDBO1.createdUsersId = Integer.parseInt(userId);
				empPayScaleDetailsDBO1.current = true;
				empPayScaleDetailsDBO1 = convertEmpPayScaleDetailsDTOToDBO(empId,dto,empPayScaleDetailsDBO,empPayScaleDetailsComponentsList,userId);
			}
		}
		return empPayScaleDetailsDBO1;
	}
	

	private EmpPayScaleDetailsDBO convertEmpPayScaleDetailsDTOToDBO(int empId,AppointmentApprovalDTO dto,EmpPayScaleDetailsDBO empPayScaleDetailsDBO,List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsList, String userId) {
		if(dto.radioButton.equals("SCALE PAY")) {				
			if(!Utils.isNullOrEmpty(dto.id)) {
				EmpApplnEntriesDBO applnEntriesDBO =  new EmpApplnEntriesDBO();
				applnEntriesDBO.id = Integer.parseInt(dto.id);
				empPayScaleDetailsDBO.empApplnEntriesDBO = applnEntriesDBO;
			}
			if(!Utils.isNullOrEmpty(dto.cell.id)) {
				EmpPayScaleMatrixDetailDBO empPayScaleMatrixDetailDBO = new EmpPayScaleMatrixDetailDBO();
				empPayScaleMatrixDetailDBO.id = Integer.parseInt(dto.cell.id);
				empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = empPayScaleMatrixDetailDBO;
			}
			if(!Utils.isNullOrEmpty(dto.grossyPay)) {
				empPayScaleDetailsDBO.grossPay = new BigDecimal(dto.grossyPay);
			}
			empPayScaleDetailsDBO.wageRatePerType  = null;
		}else if(dto.radioButton.equals("DAILY")){	
			if(!Utils.isNullOrEmpty(dto.dailyAmount)) {
				empPayScaleDetailsDBO.wageRatePerType = new BigDecimal(dto.dailyAmount);
			}
			if(!Utils.isNullOrEmpty(dto.grossyPay)) {
				empPayScaleDetailsDBO.grossPay = new BigDecimal(dto.grossyPay);
			}
		}else if(dto.radioButton.equals("PER HOUR")) {
			if(!Utils.isNullOrEmpty(dto.perHour)) {
				empPayScaleDetailsDBO.wageRatePerType = new BigDecimal(dto.perHour);
			}
			empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
			empPayScaleDetailsDBO.grossPay = null;
		}else if(dto.radioButton.equals("PER COURSE")) {
			if(!Utils.isNullOrEmpty(dto.perCourse)) {
				empPayScaleDetailsDBO.wageRatePerType = new BigDecimal(dto.perCourse);
			}
			empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
			empPayScaleDetailsDBO.grossPay = null;
		}else if(dto.radioButton.equals("CONSOLIDATED")) {
			if(!Utils.isNullOrEmpty(dto.grossyPay)) {
				empPayScaleDetailsDBO.grossPay = new BigDecimal(dto.grossyPay);
			} else {
				empPayScaleDetailsDBO.grossPay  = null;
			}
			if(!Utils.isNullOrEmpty(dto.getConsolidateAmount())) {
				empPayScaleDetailsDBO.setWageRatePerType(new BigDecimal(dto.getConsolidateAmount()));
			}
		}else if(dto.radioButton.equals("NO PAY")) {
			empPayScaleDetailsDBO.empPayScaleMatrixDetailDBO = null;
			empPayScaleDetailsDBO.wageRatePerType = null;
			empPayScaleDetailsDBO.grossPay = null;
		}
		empPayScaleDetailsDBO.setEmpDBO(new EmpDBO());
		empPayScaleDetailsDBO.getEmpDBO().setId(empId);
		empPayScaleDetailsDBO.payScaleType = dto.radioButton;
		empPayScaleDetailsDBO.recordStatus = 'A';
		Set<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOs = new HashSet<>();
		Set<EmpPayScaleDetailsComponentsDBO> deleteSet1 = !Utils.isNullOrEmpty(empPayScaleDetailsComponentsList)? new HashSet<EmpPayScaleDetailsComponentsDBO>(empPayScaleDetailsComponentsList) : null;
		if(!Utils.isNullOrEmpty(dto.payScaleDetailsComponents)) {
			for(SalaryComponentDTO item :dto.payScaleDetailsComponents) {
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

//	public List<EmpPayScaleDetailsComponentsDBO> setEmpPayScaleDetailsComponentsDTOtoDBO(AppointmentApprovalDTO dto, EmpPayScaleDetailsDBO empPayScaleDetailsDBO,List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsList, String userId) {
//		if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsList)) {
//			List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOsOld = new ArrayList<EmpPayScaleDetailsComponentsDBO>();
//			for (EmpPayScaleDetailsComponentsDBO empPayScaleDetailsComponentsDBO : empPayScaleDetailsComponentsList) {
//				for(SalaryComponentDTO salaryComponentDTO : dto.payScaleDetailsComponents) {  
//					if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO)) {
//						if(!Utils.isNullOrEmpty(salaryComponentDTO.id)) {
//							if(empPayScaleDetailsComponentsDBO.empPayScaleComponentsDBO.id == Integer.parseInt(salaryComponentDTO.id.trim())) {
//								empPayScaleDetailsComponentsDBO.empSalaryComponentValue = new BigDecimal(salaryComponentDTO.amount);
//								empPayScaleDetailsComponentsDBO.modifiedUsersId = Integer.parseInt(userId);
//								empPayScaleDetailsComponentsDBOsOld.add(empPayScaleDetailsComponentsDBO);
//							}
//						}
//					}
//				}
//			}
//			if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsDBOsOld)) {
//			  return empPayScaleDetailsComponentsDBOsOld;
//			}else {
//				List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOsOldAndNew = new ArrayList<EmpPayScaleDetailsComponentsDBO>();
//				for (EmpPayScaleDetailsComponentsDBO empPayScaleDetailsComponentsDBO : empPayScaleDetailsComponentsList) {
//					empPayScaleDetailsComponentsDBO.recordStatus ='D';
//					empPayScaleDetailsComponentsDBO.modifiedUsersId = Integer.parseInt(userId);
//					empPayScaleDetailsComponentsDBOsOldAndNew.add(empPayScaleDetailsComponentsDBO);
//				}
//				if(!Utils.isNullOrEmpty(dto.payScaleDetailsComponents)) {
//					for(SalaryComponentDTO salaryComponentDTO : dto.payScaleDetailsComponents) { 
//						 EmpPayScaleComponentsDBO componentsDBO = new EmpPayScaleComponentsDBO();
//			    		 EmpPayScaleDetailsComponentsDBO payScaleDetailsComponents = new EmpPayScaleDetailsComponentsDBO();
//				    	 payScaleDetailsComponents.empPayScaleDetailsDBO = empPayScaleDetailsDBO;
//				    	 componentsDBO.id =  Integer.parseInt(salaryComponentDTO.id);
//				    	 payScaleDetailsComponents.empPayScaleComponentsDBO = componentsDBO;
//				    	 payScaleDetailsComponents.empSalaryComponentValue = new BigDecimal(salaryComponentDTO.amount);
//				    	 payScaleDetailsComponents.createdUsersId = Integer.parseInt(userId);
//				    	 payScaleDetailsComponents.recordStatus = 'A';
//				    	 empPayScaleDetailsComponentsDBOsOldAndNew.add(payScaleDetailsComponents);
//					}
//				}
//				return empPayScaleDetailsComponentsDBOsOldAndNew;
//			}
//		}else {
//			List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsListNew = new ArrayList<EmpPayScaleDetailsComponentsDBO>();
//	    	 for(SalaryComponentDTO salaryComponentDTO : dto.payScaleDetailsComponents) {  
//	    		 EmpPayScaleComponentsDBO componentsDBO = new EmpPayScaleComponentsDBO();
//	    		 EmpPayScaleDetailsComponentsDBO payScaleDetailsComponents = new EmpPayScaleDetailsComponentsDBO();
//		    	 payScaleDetailsComponents.empPayScaleDetailsDBO = empPayScaleDetailsDBO;
//		    	 componentsDBO.id =  Integer.parseInt(salaryComponentDTO.id);
//		    	 payScaleDetailsComponents.empPayScaleComponentsDBO = componentsDBO;
//		    	 payScaleDetailsComponents.empSalaryComponentValue = new BigDecimal(salaryComponentDTO.amount);
//		    	 payScaleDetailsComponents.createdUsersId = Integer.parseInt(userId);
//		    	 payScaleDetailsComponents.recordStatus = 'A';
//		    	 empPayScaleDetailsComponentsListNew.add(payScaleDetailsComponents);
//	    	 }
//			return empPayScaleDetailsComponentsListNew;
//		}		
//	}
}
