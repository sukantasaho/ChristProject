package com.christ.erp.services.handlers.employee.recruitment;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDesignationDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeGroupDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.ErpEmployeeTitleDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.AppointmentApprovalDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.handlers.employee.common.CommonEmployeeHandler;
import com.christ.erp.services.helpers.employee.recruitment.AppointmentApprovalHelper;
import com.christ.erp.services.transactions.employee.common.CommonEmployeeTransaction;
import com.christ.erp.services.transactions.employee.recruitment.AppointmentApprovalTransaction;

@Service
public class AppointmentApprovalHandler {
//	private static volatile AppointmentApprovalHandler appointmentApprovalHandler = null;
//	public static AppointmentApprovalHandler getInstance() {
//        if(appointmentApprovalHandler==null) {
//        	appointmentApprovalHandler = new AppointmentApprovalHandler();
//        }
//        return appointmentApprovalHandler;
//	}
//    AppointmentApprovalTransaction appointmentApprovalTransaction = AppointmentApprovalTransaction.getInstance();
    //AppointmentApprovalHelper appointmentApprovalHelper = AppointmentApprovalHelper.getInstance();    
	@Autowired
	CommonEmployeeHandler commonEmployeeHandler;
	@Autowired
	AppointmentApprovalHelper appointmentApprovalHelper;
	@Autowired
	AppointmentApprovalTransaction appointmentApprovalTransaction;
	@Autowired
	CommonEmployeeTransaction commonEmployeeTransaction;
	
	public AppointmentApprovalDTO getApplicationEntriesDetails(String applicationNumber, String applicantName) {
		AppointmentApprovalDTO dto = null;
		EmpApplnEntriesDBO dbo = null;
        EmpPayScaleDetailsDBO payScaleDetailsDBO = null;
		List<EmpPayScaleDetailsComponentsDBO>  detailsComponentsDBO = null;
		if(!Utils.isNullOrEmpty(applicationNumber) || !Utils.isNullOrEmpty(applicantName)) {
		    try {
				dbo = appointmentApprovalTransaction.getApplicationEntriesDetails(applicationNumber, applicantName);
				if(!Utils.isNullOrEmpty(dbo)) {
					dto = appointmentApprovalHelper.setEmpApplnEntriesDBOtoDTO(dto,dbo);
	        		if(!Utils.isNullOrEmpty(dbo.applicationNo)) {
						payScaleDetailsDBO = appointmentApprovalTransaction.getEmpPayScaleDetails(dbo.applicationNo);
					    if(!Utils.isNullOrEmpty(payScaleDetailsDBO)) {
					        appointmentApprovalHelper.setEmpPayScaleDetailsDBOtoDTO(dto,payScaleDetailsDBO);
					    }
					    if(!Utils.isNullOrEmpty(payScaleDetailsDBO)) {
					    	detailsComponentsDBO = appointmentApprovalTransaction.getEmpPayScaleDetailsComponentsDBO(payScaleDetailsDBO.id);	
					    	if(!Utils.isNullOrEmpty(detailsComponentsDBO)) {
					    		appointmentApprovalHelper.setEmpPayScaleDetailsComponentsDBOstoDTO(dto,detailsComponentsDBO);
					    	}
					    }  
	        		}
				 }
		    }catch (Exception e) {
			    e.printStackTrace();
			}
		}
		return dto;
	}

	public boolean saveOrUpdate(AppointmentApprovalDTO dto, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
		//    	boolean saveOrUpdate = false;
		List<EmpDBO> dboList = null;
		EmpDBO dbo = null;
		EmpPayScaleDetailsDBO  empPayScaleDetailsDBO = null;
		List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsList = null;
		List<Integer> PayscaleComponentsIds = null;
		if(dto!=null) {
			//			dboList = appointmentApprovalTransaction.checkIsEmployee(Integer.parseInt(dto.applicationNumber.trim())); 			
			dbo = commonEmployeeHandler.copyApplnDataToEmpData(Integer.parseInt(dto.id),userId);
			if(!Utils.isNullOrEmpty(dbo)) {			
				//			if(Utils.isNullOrEmpty(dboList)) {
				//				dbo = commonEmployeeHandler.copyApplnDataToEmpData(Integer.parseInt(dto.id),userId);
				dbo.setCreatedUsersId(Integer.parseInt(userId));
				dbo.setRecordStatus('I');
				if(!Utils.isNullOrEmpty(dto.category)) {
					if(!Utils.isNullOrEmpty(dto.category.id)) {			
						EmpEmployeeCategoryDBO categoryDBO = new EmpEmployeeCategoryDBO();
						categoryDBO.id = Integer.parseInt(dto.category.id);
						dbo.empEmployeeCategoryDBO = categoryDBO;
					}
				}
				if(!Utils.isNullOrEmpty(dto.jobCategorie)) {
					if(!Utils.isNullOrEmpty(dto.jobCategorie.id)) {
						EmpEmployeeJobCategoryDBO jobCategoryDBO = new EmpEmployeeJobCategoryDBO();					
						jobCategoryDBO.id = Integer.parseInt(dto.jobCategorie.id);
						dbo.empEmployeeJobCategoryDBO = jobCategoryDBO;
					}
				}
				if(!Utils.isNullOrEmpty(dto.department)) {
					if(!Utils.isNullOrEmpty(dto.department.id)) {
						ErpCampusDepartmentMappingDBO campusDepartmentMappingDBO =  new ErpCampusDepartmentMappingDBO();					
						campusDepartmentMappingDBO.id = Integer.parseInt(dto.department.id);
						dbo.erpCampusDepartmentMappingDBO = campusDepartmentMappingDBO;
					}
				}
				if(!Utils.isNullOrEmpty(dto.designation)) {
					if(!Utils.isNullOrEmpty(dto.designation.id)) {
						EmpDesignationDBO designationDBO = new EmpDesignationDBO();					
						designationDBO.id = Integer.parseInt(dto.designation.id);
						dbo.empDesignationDBO = designationDBO;
					}
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
				if(!Utils.isNullOrEmpty(dbo)) {
					int empId = commonEmployeeTransaction.mergeEmployee(dbo);
					if(!Utils.isNullOrEmpty(dto.radioButton)) {
						if(!Utils.isNullOrEmpty(dto.id)) {
							empPayScaleDetailsDBO = appointmentApprovalTransaction.getEmpPayScaleDetails(Integer.parseInt(dto.applicationNumber.trim()));
						}
						if(!Utils.isNullOrEmpty(empPayScaleDetailsDBO)) {
							if(!Utils.isNullOrEmpty(empPayScaleDetailsDBO.getId())) {
								empPayScaleDetailsComponentsList = appointmentApprovalTransaction.getEmpPayScaleDetailsComponentsDBO(empPayScaleDetailsDBO.id);	
							}
						}
						empPayScaleDetailsDBO = appointmentApprovalHelper.PayScaleDTOtoDBO(empId,empPayScaleDetailsDBO, dto, userId,empPayScaleDetailsComponentsList);
						if(!Utils.isNullOrEmpty(empPayScaleDetailsDBO)) { 
							appointmentApprovalTransaction.saveOrUpdatePayScaleDetails(empPayScaleDetailsDBO);
						}
						//						if(!Utils.isNullOrEmpty(empPayScaleDetailsDBO)) { 
						//							appointmentApprovalTransaction.saveOrUpdatePayScaleDetails(empPayScaleDetailsDBO);
						//							if(!Utils.isNullOrEmpty(empPayScaleDetailsDBO)) {
						//								PayscaleComponentsIds = new ArrayList<Integer>();
						//								if(!Utils.isNullOrEmpty(dto.payScaleDetailsComponents)) {
						//									for(SalaryComponentDTO salaryComponentDTO : dto.payScaleDetailsComponents) { 
						//										PayscaleComponentsIds.add(Integer.parseInt(salaryComponentDTO.id));
						//									}
						//								}
						//								empPayScaleDetailsComponentsList = appointmentApprovalTransaction.getEmpPayScaleDetailsComponentsDBO(empPayScaleDetailsDBO.id);								
						//								empPayScaleDetailsComponentsList = appointmentApprovalHelper.setEmpPayScaleDetailsComponentsDTOtoDBO(dto,empPayScaleDetailsDBO, empPayScaleDetailsComponentsList, userId);
						//							}
						//							if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsList)) {
						//								appointmentApprovalTransaction.saveOrUpdatePayScaleDeatials(empPayScaleDetailsComponentsList);
						//							}
						//						}
					}
				}
			}
//			else {
//				result.failureMessage = "Employee already created";
//				return false;
//			}
		}	
		return true;
	}
}
