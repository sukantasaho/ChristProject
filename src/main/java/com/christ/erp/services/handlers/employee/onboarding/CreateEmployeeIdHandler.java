package com.christ.erp.services.handlers.employee.onboarding;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.SysProperties;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDetailsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDetailsDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentListDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveCategoryAllotmentDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveCategoryAllotmentDetailsDTO;
import com.christ.erp.services.dto.employee.onboarding.CreateEmployeeIdDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.onboarding.CreateEmployeeIdTransaction;
@Service
public class CreateEmployeeIdHandler {
	
	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;
	
	
	private static volatile CreateEmployeeIdHandler createEmployeeIdHandler = null;
	CreateEmployeeIdTransaction createEmployeeIdTransaction = CreateEmployeeIdTransaction.getInstance();

	public static CreateEmployeeIdHandler getInstance() {
		if(createEmployeeIdHandler==null) {
			createEmployeeIdHandler = new CreateEmployeeIdHandler();
		}
		return createEmployeeIdHandler;
	}

	@Autowired
	CreateEmployeeIdTransaction createEmployeeIdTransaction1;
	
	@Autowired
	CommonApiHandler commonApiHandler;
	
	@Autowired
	CommonApiTransaction commonApiTransaction1;

	public boolean saveOrUpdate(CreateEmployeeIdDTO data, String userId) throws Exception {
		List<Object> objList = new ArrayList<Object>();
		if (!Utils.isNullOrEmpty(data)) {
			EmpDBO emp = createEmployeeIdTransaction1.getEmp(data.id);
			try {
				if (Utils.isNullOrWhitespace(data.employeeNo) == false) {
					emp.empNumber = data.employeeNo;
				}
				emp.modifiedUsersId = Integer.parseInt(userId);
				emp.empName = data.applicantName;
				EmpJobDetailsDBO empJobDetailsDBO = emp.empJobDetailsDBO;
				if (Utils.isNullOrEmpty(empJobDetailsDBO)) {
					empJobDetailsDBO = new EmpJobDetailsDBO();
					empJobDetailsDBO.recordStatus = 'A';
					empJobDetailsDBO.setEmpApplnEntriesId(new EmpApplnEntriesDBO());
					empJobDetailsDBO.setEmpApplnEntriesId(emp.getEmpApplnEntriesDBO());
					empJobDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					emp.empJobDetailsDBO = empJobDetailsDBO;
				} else {
					empJobDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
				}
				empJobDetailsDBO.empDBO = emp;
				empJobDetailsDBO.isVacationApplicable = data.getIsVacationApplicable();
				emp.erpCampusDepartmentMappingDBO = createEmployeeIdTransaction1.getCampusDepartmentMapping(data.campus.id, data.department.id);
				emp.recordStatus = 'A';
				emp.empDOJ = data.joiningLocalDate;
				if (!Utils.isNullOrEmpty(data.timeZone)) {
					EmpTimeZoneDBO empTimeZoneDBO = new EmpTimeZoneDBO();
					empTimeZoneDBO.setId(Integer.valueOf(data.timeZone.id));
					emp.empTimeZoneDBO = empTimeZoneDBO;
				}
				if (!Utils.isNullOrEmpty(data.leaveApprover) || !Utils.isNullOrEmpty(data.leaveAuthoriser) ||
						!Utils.isNullOrEmpty(data.workDiaryApprover) || !Utils.isNullOrEmpty(data.levelOneAppraiser) || !Utils.isNullOrEmpty(data.levelTwoAppraiser) ) {				
					EmpApproversDBO empApproversDBO = new EmpApproversDBO();
					empApproversDBO.empDBO = new EmpDBO();
					empApproversDBO.empDBO.id = Integer.parseInt(data.id);
					if (!Utils.isNullOrEmpty(data.leaveApprover)) {
						EmpDBO leaveApproverDbo = new EmpDBO();
						leaveApproverDbo.id = Integer.parseInt(data.leaveApprover.id);
						empApproversDBO.leaveApproverId = leaveApproverDbo;
					}
					if (!Utils.isNullOrEmpty(data.leaveAuthoriser)) {
						EmpDBO leaveAuthoriserDbo = new EmpDBO();
						leaveAuthoriserDbo.id = Integer.parseInt(data.leaveAuthoriser.id);
						empApproversDBO.leaveAuthorizerId = leaveAuthoriserDbo;
					}
					Set<EmpApproversDetailsDBO> empApproversDetailsDBOSet = new HashSet<EmpApproversDetailsDBO>();
					if(!Utils.isNullOrEmpty(data.leaveApprover) && !Utils.isNullOrEmpty(data.leaveAuthoriser)) {
						EmpApproversDetailsDBO empApproversDetailsDBO = new EmpApproversDetailsDBO();
						empApproversDetailsDBO.setApprovalType("Leave");
						empApproversDetailsDBO.setEmpApproversId(empApproversDBO);
						empApproversDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						empApproversDetailsDBO.setRecordStatus('A');
						EmpDBO empLeaveApprover = new EmpDBO();
						empLeaveApprover.id = Integer.parseInt(data.leaveApprover.id);
						empApproversDetailsDBO.setLeaveApproverId(empLeaveApprover);
						EmpDBO empLeaveAuthorizer = new EmpDBO();
						empLeaveAuthorizer.id = Integer.parseInt(data.leaveAuthoriser.id);
						empApproversDetailsDBO.setLeaveAuthorizerId(empLeaveAuthorizer);	
						empApproversDetailsDBOSet.add(empApproversDetailsDBO);
						empApproversDBO.setEmpApproversDetailsDBOSet(empApproversDetailsDBOSet);
					}			
					if(!Utils.isNullOrEmpty(data.workDiaryApprover)) {
						EmpDBO workDiaryApproverDbo = new EmpDBO();
						workDiaryApproverDbo.id = Integer.parseInt(data.workDiaryApprover.id);
						empApproversDBO.workDairyApproverId = workDiaryApproverDbo;
						EmpApproversDetailsDBO empApproversDetailsDBO = new EmpApproversDetailsDBO();
						empApproversDetailsDBO.setApprovalType("Work Dairy");
						empApproversDetailsDBO.setEmpApproversId(empApproversDBO);
						empApproversDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						empApproversDetailsDBO.setRecordStatus('A');
						EmpDBO empWorkDiaryApprover = new EmpDBO();
						empWorkDiaryApprover.id = Integer.parseInt(data.workDiaryApprover.id);
						empApproversDetailsDBO.setWorkDairyApproverId(empWorkDiaryApprover);
						empApproversDetailsDBOSet.add(empApproversDetailsDBO);
						empApproversDBO.setEmpApproversDetailsDBOSet(empApproversDetailsDBOSet);
					}
					if(!Utils.isNullOrEmpty(data.levelOneAppraiser)) {
						EmpDBO levelOneAppraiserDbo = new EmpDBO();
						levelOneAppraiserDbo.id = Integer.valueOf(data.levelOneAppraiser.id);
						empApproversDBO.levelOneAppraiserId = levelOneAppraiserDbo;
					}
					if(!Utils.isNullOrEmpty(data.levelTwoAppraiser)) {
						EmpDBO levelTwoAppraiserDbo = new EmpDBO();
						levelTwoAppraiserDbo.id = Integer.parseInt(data.levelTwoAppraiser.id);
						empApproversDBO.levelTwoAppraiserId = levelTwoAppraiserDbo; 
					}
					if(!Utils.isNullOrEmpty(data.levelOneAppraiser) && !Utils.isNullOrEmpty(data.levelTwoAppraiser)) {
						EmpApproversDetailsDBO empApproversDetailsDBO = new EmpApproversDetailsDBO();
						empApproversDetailsDBO.setApprovalType("Appraiser");
						empApproversDetailsDBO.setEmpApproversId(empApproversDBO);
						empApproversDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						empApproversDetailsDBO.setRecordStatus('A');
						EmpDBO LevelOneAppraiser = new EmpDBO();
						LevelOneAppraiser.id = Integer.parseInt(data.levelOneAppraiser.id);
						empApproversDetailsDBO.setLevelOneAppraiserId(LevelOneAppraiser);
						EmpDBO LevelTwoAppraiser = new EmpDBO();
						LevelTwoAppraiser.id = Integer.parseInt(data.levelTwoAppraiser.id);
						empApproversDetailsDBO.setLevelTwoAppraiserId(LevelTwoAppraiser);
						empApproversDetailsDBOSet.add(empApproversDetailsDBO);
						empApproversDBO.setEmpApproversDetailsDBOSet(empApproversDetailsDBOSet);
					}
					empApproversDBO.createdUsersId = Integer.parseInt(userId);
					empApproversDBO.recordStatus = 'A';
					objList.add(empApproversDBO);
				}
				if(!Utils.isNullOrEmpty(data.leaveAllotmentList)) {
					for (EmpLeaveAllotmentListDTO empLeaveAllotmentListDTO : data.leaveAllotmentList) {
						EmpLeaveAllocationDBO empLeaveAllocationDBO = new EmpLeaveAllocationDBO();
						EmpDBO empDBO = new EmpDBO();
						empDBO.id = Integer.parseInt(data.id);
						empLeaveAllocationDBO.empDBO = empDBO;
						EmpLeaveTypeDBO empLeaveTypeDBO = new EmpLeaveTypeDBO();
						empLeaveTypeDBO.id = empLeaveAllotmentListDTO.leavetypeId;
						empLeaveAllocationDBO.leaveType = empLeaveTypeDBO;
						BigDecimal leavesAllocated = new BigDecimal(empLeaveAllotmentListDTO.allottedLeaves);
						empLeaveAllocationDBO.allottedLeaves = leavesAllocated;
						empLeaveAllocationDBO.createdUsersId = Integer.parseInt(userId);
						empLeaveAllocationDBO.recordStatus = 'A';
						objList.add(empLeaveAllocationDBO);
					}
				}

			} catch (Exception e) {
			}
			if (!Utils.isNullOrEmpty(emp)) {
				sentEmailAndSms(emp,userId,data.campus.id);  
				Integer erpWorkFlowProcessId = commonApiTransaction1.getWorkFlowProcessId("EMP_CREATED");
				EmpApplnEntriesDBO empApplnEntriesDBO = emp.empApplnEntriesDBO;
				empApplnEntriesDBO.applicantCurrentProcessStatus = new ErpWorkFlowProcessDBO();
				empApplnEntriesDBO.applicantCurrentProcessStatus.id = erpWorkFlowProcessId;
				empApplnEntriesDBO.applicationCurrentProcessStatus = new ErpWorkFlowProcessDBO();
				empApplnEntriesDBO.applicationCurrentProcessStatus.id = erpWorkFlowProcessId;
				empApplnEntriesDBO.applicantStatusTime=LocalDateTime.now();
				empApplnEntriesDBO.applicationStatusTime = LocalDateTime.now();
				empApplnEntriesDBO.modifiedUsersId = Integer.parseInt(userId);
				objList.add(empApplnEntriesDBO);
				objList.add(emp);
				ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
				erpWorkFlowProcessStatusLogDBO.entryId = emp.empApplnEntriesDBO.id;
				erpWorkFlowProcessStatusLogDBO.createdUsersId = Integer.parseInt(userId);
				erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
				erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO.id = erpWorkFlowProcessId;
				erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
				objList.add(erpWorkFlowProcessStatusLogDBO);
				return createEmployeeIdTransaction1.saveOrUpdate(objList);
			}
		}
		return false;
	}

	public CreateEmployeeIdDTO getApplicantDetails(String applicationNo) {
		EmpDBO employeeDetails;
		CreateEmployeeIdDTO dto =null;
		try {
			employeeDetails = createEmployeeIdTransaction1.getApplicantDetails(applicationNo);
			if (!Utils.isNullOrEmpty(employeeDetails)) {
				dto =  new CreateEmployeeIdDTO();
				if (employeeDetails.getRecordStatus() == 'I') {
					if(!Utils.isNullOrEmpty(employeeDetails.id)) {
						dto.id = String.valueOf(employeeDetails.id);
					}
					if(!Utils.isNullOrEmpty(employeeDetails.empName)) {
						dto.applicantName = employeeDetails.empName;
					}
					if(!Utils.isNullOrEmpty(employeeDetails.erpCampusDepartmentMappingDBO)) {
						dto.erpCampusDepartmentMappingId = String.valueOf(employeeDetails.erpCampusDepartmentMappingDBO.id);
						if(!Utils.isNullOrEmpty(employeeDetails.erpCampusDepartmentMappingDBO.erpCampusDBO)) {
							dto.campus = new ExModelBaseDTO();
							if(!Utils.isNullOrEmpty(employeeDetails.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName)) {
								dto.campus.text = employeeDetails.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName;
							}
							if(!Utils.isNullOrEmpty(employeeDetails.erpCampusDepartmentMappingDBO.erpCampusDBO.id)) {
								dto.campus.id = String.valueOf(employeeDetails.erpCampusDepartmentMappingDBO.erpCampusDBO.id);
							}
						}
						if(!Utils.isNullOrEmpty(employeeDetails.erpCampusDepartmentMappingDBO.erpDepartmentDBO)) {
							dto.department = new ExModelBaseDTO();
							if(!Utils.isNullOrEmpty(employeeDetails.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName)) {
								dto.department.text = employeeDetails.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName;
							}
							if(!Utils.isNullOrEmpty(employeeDetails.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id)) {
								dto.department.id = String.valueOf(employeeDetails.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id);
							}
						}
					}				
					if(!Utils.isNullOrEmpty(employeeDetails.recordStatus))
						dto.recordStatus = employeeDetails.recordStatus;
					if(!Utils.isNullOrEmpty(employeeDetails.empApplnEntriesDBO)) {
						if(!Utils.isNullOrEmpty(employeeDetails.empApplnEntriesDBO.empJobDetailsDBO)) {
							if(!Utils.isNullOrEmpty(employeeDetails.empApplnEntriesDBO.empJobDetailsDBO.joiningDate)) {
								dto.joiningLocalDate = employeeDetails.empApplnEntriesDBO.empJobDetailsDBO.joiningDate.toLocalDate();
							}
						}
					}				
				} else if(employeeDetails.getRecordStatus() == 'A') {
					dto.setRecordStatus('A');
				} else {
					dto= null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	public ApiResult<List<LookupItemDTO>> getDepartmentByCampus(String campusId) {
		ApiResult<List<LookupItemDTO>> departmentList = new ApiResult<List<LookupItemDTO>>();
		List<LookupItemDTO> listLookUpDTO = new ArrayList<LookupItemDTO>();
		try {
			List<Tuple> list = createEmployeeIdTransaction1.getDepartmentByCampus(campusId);
			if(!Utils.isNullOrEmpty(list)) {
				list.forEach(data -> {
					LookupItemDTO lookupItemDTO = new LookupItemDTO();
					if(!Utils.isNullOrEmpty(data.get("ID"))) {
						lookupItemDTO.setValue(data.get("ID").toString());
					}
					if(!Utils.isNullOrEmpty(data.get("text"))) {
						lookupItemDTO.setLabel(data.get("text").toString());
					}
					listLookUpDTO.add(lookupItemDTO);
				});
			}
			departmentList.setDto(listLookUpDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return departmentList;
	}

	public void sentEmailAndSms(EmpDBO emp,String userId,String campusId) {
		Tuple tuple = createEmployeeIdTransaction1.getTemplate();
		if(!Utils.isNullOrEmpty(tuple)) {
			List<ErpEmailsDBO> emailList = new ArrayList<ErpEmailsDBO>();
			Integer workFlowProcessId = commonApiTransaction1.getWorkFlowProcessId("EMP_CREATED");
			String email = redisSysPropertiesData.getSysProperties(SysProperties.EMPLOYEE_JOINING_INTIMATION_EMAIL.name(), "C",Integer.parseInt(campusId));
			if(!Utils.isNullOrEmpty(email)) {
				if(email.contains(",")) {
					String[] objects = email.split(",");
					for (String obj : objects) {
						ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
						if(!Utils.isNullOrEmpty(tuple.get("emailTemplateId"))) {
							erpEmailsDBO.entryId = Integer.parseInt(String.valueOf(tuple.get("emailTemplateId")));
						}
						erpEmailsDBO.erpUsersDBO = null;
						String msgBody= null;
						Object content = tuple.get("emailTemplateContent");
						if (content instanceof String) {
							msgBody = (String) content;
						} else if (content instanceof Blob) {
							Blob blob = (Blob) content;
							byte[] bytes;
							try {
								bytes = blob.getBytes(1, (int) blob.length());
								String msg = new String(bytes);
								msgBody = msg.replace("[Name]", emp.getEmpName().toString());
								if(!Utils.isNullOrEmpty(emp.erpCampusDepartmentMappingDBO)) {
									if(!Utils.isNullOrEmpty(emp.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO())) {
										msgBody = msgBody.replace("[Department] ", String.valueOf(emp.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName));
									}
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}					
						}
						erpEmailsDBO.emailContent = msgBody;
						erpEmailsDBO.emailSubject = !Utils.isNullOrEmpty(tuple.get("mailSubject")) ? tuple.get("mailSubject").toString() : "";
						erpEmailsDBO.senderName = !Utils.isNullOrEmpty(tuple.get("mailFromName")) ? tuple.get("mailFromName").toString() : "";
						erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
						erpEmailsDBO.recordStatus = 'A';
						erpEmailsDBO.recipientEmail = obj.trim();	
						emailList.add(erpEmailsDBO);
					}
				}else {
					ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
					erpEmailsDBO.entryId = !Utils.isNullOrEmpty(tuple.get("emailTemplateId")) ? Integer.parseInt(tuple.get("emailTemplateId").toString()) : null;
					erpEmailsDBO.erpUsersDBO = null;
					String msgBody= null;					
					Object content = tuple.get("emailTemplateContent");
					if (content instanceof String) {
						msgBody = (String) content;
					} else if (content instanceof Blob) {
						Blob blob = (Blob) content;
						byte[] bytes;
						try {
							bytes = blob.getBytes(1, (int) blob.length());
							String msg = new String(bytes);
							msgBody =!Utils.isNullOrEmpty(emp.getEmpName())? msg.replace("[Name]", emp.getEmpName().toString()) :null ;
							if(!Utils.isNullOrEmpty(emp.erpCampusDepartmentMappingDBO)) {
								if(!Utils.isNullOrEmpty(emp.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO())) {
									msgBody = msgBody.replace("[Department] ", String.valueOf(emp.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName));
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}					
					}
					erpEmailsDBO.emailContent = msgBody;
					erpEmailsDBO.emailSubject = !Utils.isNullOrEmpty(tuple.get("mailSubject")) ? tuple.get("mailSubject").toString() : "";
					erpEmailsDBO.senderName = !Utils.isNullOrEmpty(tuple.get("mailFromName")) ? tuple.get("mailFromName").toString() : "";
					erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
					erpEmailsDBO.recordStatus = 'A';
					erpEmailsDBO.recipientEmail = email;
					emailList.add(erpEmailsDBO);
				}
			}
			commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workFlowProcessId,"EMP_CREATED",null,null,null,emailList);
		}
	}

	public EmpLeaveCategoryAllotmentDTO getleavetypeByCategory(String leaveCategoryId){
		EmpLeaveCategoryAllotmentDTO leaveCatgoryAllotment = new EmpLeaveCategoryAllotmentDTO();
		EmpLeaveCategoryAllotmentDBO empLeaveCategoryAllotmentDBO;
		try {
			empLeaveCategoryAllotmentDBO = createEmployeeIdTransaction1.getLeaveTypeByCategory(leaveCategoryId);
			if(!Utils.isNullOrEmpty(empLeaveCategoryAllotmentDBO)) {
				if(!Utils.isNullOrEmpty(empLeaveCategoryAllotmentDBO.leaveIinitializeMonth))
					leaveCatgoryAllotment.leaveInitialiseMonth = empLeaveCategoryAllotmentDBO.leaveIinitializeMonth.toString();
				leaveCatgoryAllotment.empleavecategoryAllotmentDetails = new ArrayList<>();
				if(!Utils.isNullOrEmpty(empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentDetailsDBO)) {
					for(EmpLeaveCategoryAllotmentDetailsDBO item:empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentDetailsDBO) {
						if(item.isApplicable==true && item.recordStatus == 'A') {
							EmpLeaveCategoryAllotmentDetailsDTO empLeaveCategoryAllotmentDetailsDTO = new EmpLeaveCategoryAllotmentDetailsDTO();
							if(!Utils.isNullOrEmpty(item.empLeaveTypeDBO)) {
								if(!Utils.isNullOrEmpty(item.empLeaveTypeDBO.id))
									empLeaveCategoryAllotmentDetailsDTO.leavetypeId = item.empLeaveTypeDBO.id;
								if(!Utils.isNullOrEmpty(item.empLeaveTypeDBO.leaveTypeName))
									empLeaveCategoryAllotmentDetailsDTO.leavetypeName = item.empLeaveTypeDBO.leaveTypeName;
							}
							if(!Utils.isNullOrEmpty(item.allottedLeaves))
								empLeaveCategoryAllotmentDetailsDTO.allottedLeaves = item.allottedLeaves;
							leaveCatgoryAllotment.empleavecategoryAllotmentDetails.add(empLeaveCategoryAllotmentDetailsDTO);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return leaveCatgoryAllotment;
	}

}