package com.christ.erp.services.handlers.employee.leave;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDetailsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.EmpHolidayDTO;
import com.christ.erp.services.dto.employee.EmpHolidayListDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveTypeDTO;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveApplicationTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LeaveApplicationHandler {
	
	@Autowired
	LeaveApplicationTransaction leaveApplicationTransaction;
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();
	
	@Autowired
	CommonApiTransaction commonApiTransaction1;
	
	@Autowired
	private CommonApiHandler commonApiHandler;
	@Autowired AWSS3FileStorageService aWSS3FileStorageService;
	
	@Autowired
	AWSS3FileStorageServiceHandler awss3FileStorageServiceHandler;
	
	private static Map<Integer, String> MonthMap = null;
	static {
		MonthMap = new HashMap<Integer, String>();
		MonthMap.put(1, "January");
		MonthMap.put(2, "February");
		MonthMap.put(3, "March");
		MonthMap.put(4, "April");
		MonthMap.put(5, "May");
		MonthMap.put(6, "June");
		MonthMap.put(7, "July");
		MonthMap.put(8, "August");
		MonthMap.put(9, "September");
		MonthMap.put(10, "October");
		MonthMap.put(11, "November");
		MonthMap.put(12, "December");
	}

	public List<EmpLeaveEntryDTO> getEmployeeDetails(String value) throws Exception {
		List<Tuple> list = leaveApplicationTransaction.getEmployeeDetails(value);
		List<EmpLeaveEntryDTO> gridList = null;
		if (!Utils.isNullOrEmpty(list)) {
			gridList = new ArrayList<EmpLeaveEntryDTO>();
			for (Tuple mapping : list) {
				EmpLeaveEntryDTO mappingInfo = new EmpLeaveEntryDTO();
				if (!Utils.isNullOrEmpty(mapping.get("name"))) {
					mappingInfo.name = mapping.get("name").toString();
				}
				if (!Utils.isNullOrEmpty(mapping.get("department"))) {
					mappingInfo.department = (String) mapping.get("department");
				}
				if (!Utils.isNullOrEmpty(mapping.get("designation"))) {
					mappingInfo.designation = (String) mapping.get("designation");
				}
				if (!Utils.isNullOrEmpty(mapping.get("campus"))) {
					mappingInfo.campus = (String) mapping.get("campus");
				}
				if (!Utils.isNullOrEmpty(mapping.get("campusId"))) {
					mappingInfo.locationId = mapping.get("campusId").toString();
				}			
				gridList.add(mappingInfo);
			}
		}
		return gridList;
	}

	public boolean saveOrUpdate(EmpLeaveEntryDTO data, String userId, Boolean isLeave, Integer year, EmpLeaveTypeDBO leaveType) throws Exception {
		try {
			if (!Utils.isNullOrEmpty(data)) {
				EmpLeaveEntryDBO dbo = new EmpLeaveEntryDBO();
				if (!Utils.isNullOrEmpty(data.employeeId)) {
					if (!Utils.isNullOrEmpty(data.id)) {
						dbo.id = data.id;
						dbo.modifiedUsersId = Integer.parseInt(userId);
					}
					EmpDBO emp = new EmpDBO();
					emp.id = Integer.parseInt(data.employeeId);
					dbo.empID = emp;
					dbo.startDate = data.startDate;
					dbo.endDate =data.endDate;
					dbo.reason = data.reason;
					dbo.setLeaveYear(year);
					dbo.setPending(true);
					EmpLeaveTypeDBO empLeaveType = new EmpLeaveTypeDBO();
					if (!Utils.isNullOrEmpty(data.leaveTypecategory)) {
						empLeaveType.id = Integer.parseInt(data.leaveTypecategory.id);
					}					
					dbo.leaveTypecategory = empLeaveType;
					if (!Utils.isNullOrEmpty(data.fromSession)) {
						if (data.fromSession.tag.equalsIgnoreCase("Forenoon"))
							dbo.fromSession = "FN";
						else if (data.fromSession.tag.equalsIgnoreCase("Afternoon"))
							dbo.fromSession = "AN";
						else
							dbo.fromSession = "FD";
					}	
					if (!Utils.isNullOrEmpty(data.toSession)) {
						if (data.toSession.tag.equalsIgnoreCase("Forenoon"))
							dbo.toSession = "FN";
						else if (data.toSession.tag.equalsIgnoreCase("Afternoon"))
							dbo.toSession = "AN";
						else
							dbo.toSession = "FD";
					}				
					if (!Utils.isNullOrEmpty(data.totalDays))
					dbo.totalDays=new BigDecimal(data.totalDays) ;
					if (!Utils.isNullOrEmpty(data.leaveDocumentUrl))
						dbo.leaveDocumentUrl = data.leaveDocumentUrl;
					if (data.leaveApplication.equalsIgnoreCase("offline"))
						dbo.offline = true;
					if (data.leaveApplication.equalsIgnoreCase("supervisor")) {
						dbo.isSupervisor = true;
						EmpDBO emp1 = new EmpDBO();
						Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
						emp1.setId(empId);
						dbo.setApproverId(emp1);	
					}
					if (Utils.isNullOrEmpty(data.id)) {
						if (data.leaveApplication.equalsIgnoreCase("offline")) {
							dbo.offline = true;
							Tuple erpWorkFlowProcessOffline = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode("LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED");
							if (erpWorkFlowProcessOffline != null) {
								if (!Utils.isNullOrEmpty(erpWorkFlowProcessOffline.get("applicant_status_display_text"))) {
									ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
									e.id = Integer.parseInt(erpWorkFlowProcessOffline.get("erp_work_flow_process_id").toString());
									dbo.erpApplicantWorkFlowProcessDBO = e;
								}
								if (!Utils.isNullOrEmpty(erpWorkFlowProcessOffline.get("application_status_display_text"))) {
									ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
									e.id = Integer.parseInt(erpWorkFlowProcessOffline.get("erp_work_flow_process_id").toString());
									dbo.erpApplicationWorkFlowProcessDBO = e;
								}
							}
						} else if(data.leaveApplication.equalsIgnoreCase("supervisor")) {
								Tuple erpWorkFlowProcessOffline = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode("LEAVE_APPLICATION_APPROVER_APPROVED");
							if (erpWorkFlowProcessOffline != null) {
								if (!Utils.isNullOrEmpty(erpWorkFlowProcessOffline.get("applicant_status_display_text"))) {
									ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
									e.id = Integer.parseInt(erpWorkFlowProcessOffline.get("erp_work_flow_process_id").toString());
									dbo.erpApplicantWorkFlowProcessDBO = e;
								}
								if (!Utils.isNullOrEmpty(erpWorkFlowProcessOffline.get("application_status_display_text"))) {
									ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
									e.id = Integer.parseInt(erpWorkFlowProcessOffline.get("erp_work_flow_process_id").toString());
									dbo.erpApplicationWorkFlowProcessDBO = e;
								}
							}
						}
						else {
							dbo.offline = false;
							Tuple erpWorkFlowProcessOnline = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode("LEAVE_APPLICATION_SUBMISSION");
							if (!Utils.isNullOrEmpty(erpWorkFlowProcessOnline.get("applicant_status_display_text"))) {
								ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
								if (!Utils.isNullOrEmpty(erpWorkFlowProcessOnline.get("erp_work_flow_process_id"))) {
									e.id = Integer.parseInt(erpWorkFlowProcessOnline.get("erp_work_flow_process_id").toString());
								}							
								dbo.erpApplicantWorkFlowProcessDBO = e;
							}
							if (!Utils.isNullOrEmpty(erpWorkFlowProcessOnline.get("application_status_display_text"))) {
								ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
								if (!Utils.isNullOrEmpty(erpWorkFlowProcessOnline.get("erp_work_flow_process_id"))) {
									e.id = Integer.parseInt(erpWorkFlowProcessOnline.get("erp_work_flow_process_id").toString());
								}
								dbo.erpApplicationWorkFlowProcessDBO = e;
							}
						}
					} else {
						if (!Utils.isNullOrEmpty(data.applicantWorkFlowProcessId)) {
							ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
							e.id = data.applicantWorkFlowProcessId;
							dbo.erpApplicantWorkFlowProcessDBO = e;
						}
						if (!Utils.isNullOrEmpty(data.applicationWorkFlowProcessId)) {
							ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
							e.id = data.applicationWorkFlowProcessId;
							dbo.erpApplicationWorkFlowProcessDBO = e;
						}
						if (!Utils.isNullOrEmpty(data.leaveApplication) && data.leaveApplication.equalsIgnoreCase("offline"))
							resettingPreviousLeavesApplied(data, userId, isLeave, year);
					}
				}
				dbo.createdUsersId = Integer.parseInt(userId);
				dbo.recordStatus = 'A';
				dbo.schedulerStatus="NA";
				dbo.isPending = true;
				if (!Utils.isNullOrEmpty(data.leaveApplication) && data.leaveApplication.equalsIgnoreCase("offline"))
					calculatingTotalNumberOfLeavesApplied(data, userId, isLeave, year);		
				else if(!Utils.isNullOrEmpty(data.leaveApplication) && data.leaveApplication.equalsIgnoreCase("online"))
					updatePendingCountEmpLeaveAllocation(data, userId, isLeave, year,leaveType);
				else if(!Utils.isNullOrEmpty(data.leaveApplication) && data.leaveApplication.equalsIgnoreCase("supervisor"))
					updatePendingCountEmpLeaveAllocation(data, userId, isLeave, year,leaveType);
				 List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
				
				 
				 // saving data to emp_leave_entry_details table
				 Set<EmpLeaveEntryDetailsDBO> empLeaveEntryDetailsDBOList=new LinkedHashSet<EmpLeaveEntryDetailsDBO>();
					LocalDate startDate=dbo.startDate;
					List<EmpHolidayEventsDBO> holidayList = leaveApplicationTransaction.getEmpHolidayEventsDBO(data,year);
					Map<LocalDate,EmpHolidayEventsDBO> holidayListMap= new LinkedHashMap<LocalDate, EmpHolidayEventsDBO>();
					    for (EmpHolidayEventsDBO empHolidayEventsDBO : holidayList) {
					    	holidayListMap.put(empHolidayEventsDBO.holidayEventsStartDate, empHolidayEventsDBO);
						}
					 while (startDate.isBefore(dbo.endDate) || startDate.isEqual(dbo.endDate)) {
						boolean sundayorNot = Utils.checkISSunday(startDate);
					//	if ((data.sundayWorkingDay && sundayorNot) || (data.holidayWorkingDay && holidayListMap.containsKey(startDate)) || (!sundayorNot && !holidayListMap.containsKey(startDate))) {
							EmpLeaveEntryDetailsDBO empLeaveEntryDetailsDBO = new EmpLeaveEntryDetailsDBO();
							empLeaveEntryDetailsDBO.setEmpId(dbo.empID);
							empLeaveEntryDetailsDBO.setEmpLeaveEntryId(dbo);
							empLeaveEntryDetailsDBO.setLeaveTypeId(dbo.leaveTypecategory);
							empLeaveEntryDetailsDBO.setDate(startDate);
							if (dbo.startDate.isEqual(startDate)) {
								empLeaveEntryDetailsDBO.setSession(dbo.fromSession);
								if (dbo.fromSession.equalsIgnoreCase("FN")) {
									empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
								}else if (dbo.fromSession.equalsIgnoreCase("AN")) {
									empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(0.5));
								}else {
									dbo.fromSession = "FD";
								 empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
								}
							} else if (dbo.endDate.isEqual(startDate)) {
								empLeaveEntryDetailsDBO.setSession(dbo.toSession);
								if (dbo.toSession.equalsIgnoreCase("FN")) {
									empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(0.5));
								}else if (dbo.toSession.equalsIgnoreCase("AN")) {
									empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
								}else {
									dbo.fromSession = "FD";
								 empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
								}
							} else {
								empLeaveEntryDetailsDBO.setSession("FD");
								empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
							}
							empLeaveEntryDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
							empLeaveEntryDetailsDBO.setRecordStatus('A');
							
							if(sundayorNot)
								empLeaveEntryDetailsDBO.isSunday=true;
							if(holidayListMap.containsKey(startDate))
								empLeaveEntryDetailsDBO.holidayEventId=holidayListMap.get(startDate).id;
								
							empLeaveEntryDetailsDBOList.add(empLeaveEntryDetailsDBO);
						//}
						startDate=startDate.plusDays(1);
					}
					 dbo.setEmpLeaveEntryDetails(empLeaveEntryDetailsDBOList);
					 EmpLeaveEntryDBO empLeaveEntryDBO  =null;
					 int userId1 = Integer.parseInt(userId);
					 setUploadDocumentUrl(data.getDocumentUrl(), dbo, userId1, uniqueFileNameList);
				//	 leaveApplicationTransaction.leaveEntryDetailsDBOSave(empLeaveEntryDetailsDBOList);
					// empLeaveEntryDBO
					 Tuple approversDeatils = commonApiTransaction1.getApproversIdByEmployeeId(Integer.parseInt(data.employeeId));
					 if(approversDeatils!=null && approversDeatils.get("emp_id")!=null) {
						// self Approval
						 String approverEmpId = approversDeatils.get("emp_id").toString();
						 if(approverEmpId.equals(data.employeeId)) {
							 Tuple erpWorkFlowProcessOnline = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode("LEAVE_APPLICATION_APPROVER_APPROVED");
							 ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
							 e.id = Integer.parseInt(erpWorkFlowProcessOnline.get("erp_work_flow_process_id").toString());
							 dbo.setErpApplicantWorkFlowProcessDBO(e);
							 dbo.setErpApplicationWorkFlowProcessDBO(e);
						 }
					 }
					 
					 if (!Utils.isNullOrEmpty(dbo.id)) {
						 empLeaveEntryDBO= leaveApplicationTransaction.update(dbo);
					 } else {
						 empLeaveEntryDBO=leaveApplicationTransaction.save(dbo);
					 }
					 if((!Utils.isNullOrEmpty(uniqueFileNameList)) ) {
						 awss3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList).subscribe();
						}

					 
					 //  saving data to erp_status_log table
						if (empLeaveEntryDBO.erpApplicantWorkFlowProcessDBO != null) {
							if (!Utils.isNullOrEmpty(empLeaveEntryDBO.erpApplicantWorkFlowProcessDBO.id)
									|| !Utils.isNullOrEmpty(empLeaveEntryDBO.erpApplicationWorkFlowProcessDBO.id)) {
								ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
								erpWorkFlowProcessStatusLogDBO.entryId = empLeaveEntryDBO.id;
								ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
								if (!Utils.isNullOrEmpty(empLeaveEntryDBO.erpApplicantWorkFlowProcessDBO.id))
									e.id = empLeaveEntryDBO.erpApplicantWorkFlowProcessDBO.id;
								else
									e.id = empLeaveEntryDBO.erpApplicationWorkFlowProcessDBO.id;
								erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = e;
								erpWorkFlowProcessStatusLogDBO.createdUsersId = Integer.parseInt(userId);
								erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
								return commonApiTransaction.saveErpWorkFlowProcessStatusLogDBO(erpWorkFlowProcessStatusLogDBO);
							}
						}
					
					// sending Notification and Email to approver.
				 if (data.leaveApplication.equalsIgnoreCase("online")) {
					 Set<Integer> approversIdSet = new LinkedHashSet<Integer>();
					 List<ErpNotificationsDBO> notificationList = new ArrayList<ErpNotificationsDBO>();
					 List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
					 
					 if(approversDeatils!=null && approversDeatils.get("usersId")!=null)
						 approversIdSet.add(Integer.parseInt(approversDeatils.get("usersId").toString()));
					 ErpNotificationsDBO erpNotifications = new ErpNotificationsDBO();
					 erpNotifications.entryId = empLeaveEntryDBO.id;
					 ErpUsersDBO erpUsersDBO= new ErpUsersDBO();
					 if(approversDeatils!=null && approversDeatils.get("usersId")!=null)
						 erpUsersDBO.id=Integer.parseInt(approversDeatils.get("usersId").toString());
					 erpNotifications.erpUsersDBO = erpUsersDBO;
					 erpNotifications.createdUsersId = Integer.parseInt(userId);
					 erpNotifications.recordStatus = 'A';
					 notificationList.add(erpNotifications);

					 ErpTemplateDBO erpTemplateDBO=commonApiTransaction.getErpTemplateByTemplateCodeAndTemplateType("Mail","EMP_LEAVE_SUBMISSION_MAIL_TO_APPROVER");
						if (!Utils.isNullOrEmpty(erpTemplateDBO)) {
							ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
							erpEmailsDBO.entryId = empLeaveEntryDBO.id;
							ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
							if (approversDeatils != null && approversDeatils.get("usersId").toString() != null)
								erpUsersDBO1.id = Integer.parseInt(approversDeatils.get("usersId").toString());
							erpEmailsDBO.erpUsersDBO = erpUsersDBO1;
							if (!Utils.isNullOrEmpty(erpTemplateDBO)
									&& !Utils.isNullOrEmpty(erpTemplateDBO.getTemplateContent())) {
								String msgBody = erpTemplateDBO.getTemplateContent();
								msgBody = msgBody.replace("[EMPLOYEE_NAME]", data.name);
								msgBody = msgBody.replace("[EMPLOYEE_ID] ", data.employeeId);
								msgBody = msgBody.replace("[LEAVE_TYPE]", data.leaveTypecategory.tag);
								msgBody = msgBody.replace("[FROM_DATE]",Utils.convertLocalDateToStringDate(data.startDate));
								msgBody = msgBody.replace("[TO_DATE]",Utils.convertLocalDateToStringDate(data.endDate));
								msgBody = msgBody.replace("[FROM_SESSION]",data.fromSession.tag);
								msgBody = msgBody.replace("[TO_SESSION]",data.toSession.tag);
								erpEmailsDBO.emailContent = msgBody;
							}
							if (!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
								erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
							if (!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
								erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
							if (approversDeatils != null && approversDeatils.get("personalEmailId").toString() != null)
								erpEmailsDBO.recipientEmail = approversDeatils.get("personalEmailId").toString();
							erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
							erpEmailsDBO.recordStatus = 'A';
							emailsList.add(erpEmailsDBO);
						}
					 commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(empLeaveEntryDBO.erpApplicantWorkFlowProcessDBO.id,"LEAVE_APPLICATION_SUBMISSION",approversIdSet,notificationList,null,emailsList);
				 }else if (data.leaveApplication.equalsIgnoreCase("supervisor")) {
					 // send Email and notification to authorizer
				 }
				 
				return true;
			}
		} catch (Exception error) {
			throw error;
		}
		return false;
	}

	private void updatePendingCountEmpLeaveAllocation(EmpLeaveEntryDTO data, String userId, Boolean isLeave,
			Integer year, EmpLeaveTypeDBO leaveType) throws Exception {
		float i = calculateTotalLeavesAppliedDateDifferences(data, Utils.convertLocalDateToStringDate(data.startDate), Utils.convertLocalDateToStringDate(data.endDate), data.fromSession.tag,data.toSession.tag);
		EmpLeaveAllocationDBO list = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(data, year);
		if (list != null) {
			
			BigDecimal b = new BigDecimal(i);
			if (list.leavesPending == null)
				list.leavesPending = b;
			else
				list.leavesPending = list.leavesPending.add(b);
			list.setModifiedUsersId(Integer.parseInt(userId));
			 if (!Utils.isNullOrEmpty(list.id)) {
				  leaveApplicationTransaction.update(list);
			 }
		}else {
			if(!leaveType.getIsLeave()) {
				initilizeEmpLeaveAllocationNew(data, userId, year);
			}
			//check Leave Type initialize
		} 
		
	}

	private float calculatingTotalNumberOfLeavesApplied(EmpLeaveEntryDTO data, String userId, Boolean isLeave, Integer year)
			throws Exception {
		float i = calculateTotalLeavesAppliedDateDifferences(data, Utils.convertLocalDateToStringDate(data.startDate), Utils.convertLocalDateToStringDate(data.endDate), data.fromSession.tag,data.toSession.tag);
		EmpLeaveAllocationDBO list = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(data, year);
		if (list != null) {
			BigDecimal b = new BigDecimal(i);
			if (list.sanctionedLeaves == null)
				list.sanctionedLeaves = b;
			else
				list.sanctionedLeaves = list.sanctionedLeaves.add(b);
			if (isLeave) {
				if (list.leavesRemaining != null)
					list.leavesRemaining = list.leavesRemaining.subtract(b);
			}
			list.modifiedUsersId = Integer.parseInt(userId);
			 if (!Utils.isNullOrEmpty(list.id)) {
				  leaveApplicationTransaction.update(list);
			 } else {
				 leaveApplicationTransaction.save(list);
			 }
		} 
//		else {
//			EmpLeaveAllocationDBO list1 = new EmpLeaveAllocationDBO();
//			list1.empDBO.id = Integer.parseInt(data.employeeId);
//			list1.leaveType.id = Integer.parseInt(data.leaveTypecategory.id);
//			BigDecimal b = new BigDecimal(i);
//			if (list1.sanctionedLeaves == null)
//				list1.sanctionedLeaves = b;
//			else
//				list1.sanctionedLeaves = list1.sanctionedLeaves.add(b);
//			list1.createdUsersId = Integer.parseInt(userId);
//			list1.recordStatus = 'A';
//			list1.year = year;
//			 if (!Utils.isNullOrEmpty(list1.id)) {
//				  leaveApplicationTransaction.update(list1);
//			 } else {
//				 leaveApplicationTransaction.save(list1);
//			 }
//		}
		return i;
	}

	float calculateTotalLeavesAppliedDateDifferences(EmpLeaveEntryDTO data, String startDate, String endDate,String fromSession, String toSession) throws Exception {
		float daysBetweenCount = 0;
		int sundayCount = 0;
		int holidayCount = 0;
		boolean sundayWorkingDay = false;
		boolean holidayWorkingDay = false;
		boolean sundayCountsasLeaveByLeaveType = false;
		boolean holidayCountsasLeaveByLeaveType = false;
	//	Boolean isContinousDays = false;

		daysBetweenCount = getselectedDatesDifference(data, startDate, endDate, fromSession, toSession);
		
		EmpLeaveTypeDBO empLeaveTypeDBO = leaveApplicationTransaction.getDataOnLeaveType1(data.leaveTypecategory.id);
		
		if(!Utils.isNullOrEmpty(empLeaveTypeDBO.isSundayCounted)) {
			sundayCountsasLeaveByLeaveType = empLeaveTypeDBO.isSundayCounted;
		}
		if(!Utils.isNullOrEmpty(empLeaveTypeDBO.isHolidayCounted)) {
			holidayCountsasLeaveByLeaveType = empLeaveTypeDBO.isHolidayCounted;
		}
		Tuple isSundayWorkingDay = leaveApplicationTransaction.getIsSundayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isSundayWorkingDay)) {
			if (!Utils.isNullOrEmpty(isSundayWorkingDay.get("sundayWorking")) && isSundayWorkingDay.get("sundayWorking").toString().equalsIgnoreCase("false"))
				sundayWorkingDay = true;
			 data.sundayWorkingDay=sundayWorkingDay;
		}
		Tuple isHolidayWorkingDay = leaveApplicationTransaction.getIsHolidayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isHolidayWorkingDay)) {
			if (!Utils.isNullOrEmpty(isHolidayWorkingDay.get("holidayWorking")) && isHolidayWorkingDay.get("holidayWorking").toString().equalsIgnoreCase("true"))
				holidayWorkingDay = true;
				data.holidayWorkingDay=holidayWorkingDay;
		}
		sundayCount = calculateSundaysForDateRange(startDate, endDate);
		List<Tuple> list = leaveApplicationTransaction.getEmployeeDetails(data.employeeId);
		if(!Utils.isNullOrEmpty(list)) {
		for (Tuple mapping : list) {
			if (!Utils.isNullOrEmpty(mapping.get("locationId"))) {
				data.locationId = mapping.get("locationId").toString();
			}
		}
		}else {
			throw new Exception("Employee Location is not alloted.");
		}
		holidayCount = calculateHolidayForDateRange(startDate, endDate, data.locationId);

		if(!holidayWorkingDay && !holidayCountsasLeaveByLeaveType) {
			if (holidayCount > 0 && daysBetweenCount > 0.5) {
				daysBetweenCount = daysBetweenCount - holidayCount;
			}
		}
		if(!sundayWorkingDay && !sundayCountsasLeaveByLeaveType) {
			if (holidayCount > 0 && daysBetweenCount > 0.5) {
				daysBetweenCount = daysBetweenCount - sundayCount;
			}
		}
		return daysBetweenCount;
	}

	private float getselectedDatesDifference(EmpLeaveEntryDTO data, String startDate, String endDate,String fromSession, String toSession) throws ParseException {
		float z = 0;
		long diffInDays1 = 1L;
		if (startDate.contains("Z") && endDate.contains("Z"))
			diffInDays1 = Duration.between(Utils.convertStringDateTimeToLocalDate(startDate).atStartOfDay(),
					Utils.convertStringDateTimeToLocalDate(endDate).atStartOfDay()).toDays();
		else
			diffInDays1 = Duration.between(Utils.convertStringDateToLocalDate(startDate).atStartOfDay(),
					Utils.convertStringDateToLocalDate(endDate).atStartOfDay()).toDays();
		if (diffInDays1 == 0) {
			if ((fromSession.equalsIgnoreCase("Forenoon") && toSession.equalsIgnoreCase("Forenoon"))
					|| (fromSession.equalsIgnoreCase("Afternoon") && toSession.equalsIgnoreCase("Afternoon"))) {
				z = (float) 0.5;
			} else {
				z = 1;
			}
		} else {
			if ((fromSession.equalsIgnoreCase("Afternoon") && toSession.equalsIgnoreCase("Afternoon"))
					|| (fromSession.equalsIgnoreCase("Afternoon") && toSession.equalsIgnoreCase("Full Day"))) {
				z = diffInDays1 + (float) 0.5;
			} else if ((fromSession.equalsIgnoreCase("Full Day") && toSession.equalsIgnoreCase("Full Day"))
					|| (fromSession.equalsIgnoreCase("Full Day") && toSession.equalsIgnoreCase("Afternoon")))
				z = diffInDays1 + 1;
			else if ((fromSession.equalsIgnoreCase("Full Day") && toSession.equalsIgnoreCase("Forenoon"))
					|| (fromSession.equalsIgnoreCase("Afternoon") && toSession.equalsIgnoreCase("Full Day")))
				z = (float) (diffInDays1 + .5);
			else
				z = diffInDays1;
		}
		return z;
	}

	public void resettingPreviousLeavesApplied(EmpLeaveEntryDTO data, String userId, Boolean isLeave, Integer year)
			throws Exception {
		float i = calculateTotalLeavesAppliedDateDifferences(data,  Utils.convertLocalDateToStringDate(data.tempStartDate),  Utils.convertLocalDateToStringDate(data.tempEndDate),data.tempFormSession.tag, data.tempToSession.tag);
		EmpLeaveAllocationDBO list = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(data, year);
		if (list != null) {
			BigDecimal b = new BigDecimal(i);
			if (list.sanctionedLeaves == null)
				list.sanctionedLeaves = b;
			else
				list.sanctionedLeaves = list.sanctionedLeaves.subtract(b);
			if (isLeave) {
				if (list.leavesRemaining != null)
					list.leavesRemaining = list.leavesRemaining.add(b);
			}
			list.modifiedUsersId = Integer.parseInt(userId);
			 if (!Utils.isNullOrEmpty(list.id)) {
				  leaveApplicationTransaction.update(list);
			 } else {
				 leaveApplicationTransaction.save(list);
			 }
		}
	}

	public EmpLeaveAllocationDBO initilizeEmpLeaveAllocation(EmpLeaveEntryDTO data, String userId, Integer year)
			throws Exception {
		List<Tuple> mappings = leaveApplicationTransaction.getEmpLeaveCategoryDetails(data);
		EmpLeaveAllocationDBO list1 = new EmpLeaveAllocationDBO();
		for (Tuple mapping : mappings) {
			EmpDBO e = new EmpDBO();
			e.id = Integer.parseInt(data.employeeId);
			list1.empDBO = e;
			EmpLeaveTypeDBO lt = new EmpLeaveTypeDBO();
			lt.id = Integer.parseInt(data.leaveTypecategory.id);
			list1.leaveType = lt;
			BigDecimal b = new BigDecimal(mapping.get("sanctionedLeaves").toString());
			list1.allottedLeaves = b;
			list1.leavesRemaining = b;
			list1.sanctionedLeaves = new BigDecimal(0);
			list1.createdUsersId = Integer.parseInt(userId);
			list1.year = year;
			list1.recordStatus = 'A';
			 if (!Utils.isNullOrEmpty(list1.id)) {
				  leaveApplicationTransaction.update(list1);
			 } else {
				 leaveApplicationTransaction.save(list1);
			 }
		}
		return list1;
	}
	public EmpLeaveAllocationDBO initilizeEmpLeaveAllocationNew(EmpLeaveEntryDTO data, String userId, Integer year)
			throws Exception {
		//List<Tuple> mappings = leaveApplicationTransaction.getEmpLeaveCategoryDetails(data);
		EmpLeaveAllocationDBO list1 = new EmpLeaveAllocationDBO();
		//for (Tuple mapping : mappings) {
			EmpDBO e = new EmpDBO();
			e.id = Integer.parseInt(data.employeeId);
			list1.empDBO = e;
			EmpLeaveTypeDBO lt = new EmpLeaveTypeDBO();
			lt.id = Integer.parseInt(data.leaveTypecategory.id);
			list1.leaveType = lt;
			BigDecimal b = new BigDecimal(data.getTotalDays());
			list1.allottedLeaves = new BigDecimal(0);
			list1.leavesRemaining = new BigDecimal(0);
			list1.sanctionedLeaves = new BigDecimal(0);;
			list1.createdUsersId = Integer.parseInt(userId);
			list1.leavesPending = b;
			list1.year = year;
			list1.recordStatus = 'A';
			 if (!Utils.isNullOrEmpty(list1.id)) {
				  leaveApplicationTransaction.update(list1);
			 } else {
				 leaveApplicationTransaction.save(list1);
			 }
		//}
		return list1;
	}

	public ApiResult<List<EmpLeaveEntryDTO>> getGridData(String userId, String year, String leaveApplication, String empID) throws Exception {
		ApiResult<List<EmpLeaveEntryDTO>> result = new ApiResult<List<EmpLeaveEntryDTO>>();
		try {
			Integer empId = 0;
			if(leaveApplication.equalsIgnoreCase("Online")) {
				 empId = commonApiTransaction1.getEmployeesByUserId(userId);
			}else {
				 empId = Integer.parseInt(empID);
			}
			
			List<Tuple> mappings = leaveApplicationTransaction.getGridData(empId,year,leaveApplication);
			if (mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for (Tuple mapping : mappings) {
					EmpLeaveEntryDTO mappingInfo = new EmpLeaveEntryDTO();
					mappingInfo.id = (int) mapping.get("ID");
					mappingInfo.employeeId = String.valueOf(mapping.get("EmpID"));
					mappingInfo.leaveTypeName = (String) mapping.get("LeaveTypeName");
					mappingInfo.leaveTypecategory = new ExModelBaseDTO();
					mappingInfo.leaveTypecategory.id = String.valueOf(mapping.get("LeaveTypeID"));
					mappingInfo.leaveTypecategory.tag = (String) mapping.get("LeaveTypeName");
					mappingInfo.reason = (String) mapping.get("reason");
					
//					if (!Utils.isNullOrEmpty(mapping.get("FromSession")))
//						mappingInfo.startSession = (String) mapping.get("FromSession");
//					if (!Utils.isNullOrEmpty(mapping.get("ToSession")))
//						mappingInfo.endSession = (String) mapping.get("ToSession");
					if (!Utils.isNullOrEmpty(mapping.get("StartDate"))) {
						mappingInfo.startDate = Utils.convertStringDateToLocalDate(mapping.get("StartDate").toString());
						mappingInfo.tempStartDate = Utils.convertStringDateToLocalDate(mapping.get("StartDate").toString());
					}
					if (!Utils.isNullOrEmpty(mapping.get("EndDate"))) {
						mappingInfo.endDate = Utils.convertStringDateToLocalDate(mapping.get("EndDate").toString());
						mappingInfo.tempEndDate = Utils.convertStringDateToLocalDate(mapping.get("EndDate").toString());
					}
					mappingInfo.fromSession = new ExModelBaseDTO();
					mappingInfo.tempFormSession = new ExModelBaseDTO();
					if (mapping.get("FromSession") != null && mapping.get("FromSession").toString().equalsIgnoreCase("FN")) {
						mappingInfo.fromSession.id = "2";
						mappingInfo.fromSession.tag = "Forenoon";
						mappingInfo.tempFormSession.id = "2";
						mappingInfo.tempFormSession.tag = "Forenoon";
					} else if (mapping.get("FromSession") != null && mapping.get("FromSession").toString().equalsIgnoreCase("AN")) {
						mappingInfo.fromSession.id = "3";
						mappingInfo.fromSession.tag = "Afternoon";
						mappingInfo.tempFormSession.id = "3";
						mappingInfo.tempFormSession.tag = "Afternoon";
					} else if (mapping.get("FromSession") != null && mapping.get("FromSession").toString().equalsIgnoreCase("FD")) {
						mappingInfo.fromSession.id = "1";
						mappingInfo.fromSession.tag = "Full Day";
						mappingInfo.tempFormSession.id = "1";
						mappingInfo.tempFormSession.tag = "Full Day";
					}
					mappingInfo.toSession = new ExModelBaseDTO();
					mappingInfo.tempToSession = new ExModelBaseDTO();
					if (mapping.get("toSession") != null && mapping.get("toSession").toString().equalsIgnoreCase("FN")) {
						mappingInfo.toSession.id = "2";
						mappingInfo.toSession.tag = "Forenoon";
						mappingInfo.tempToSession.id = "2";
						mappingInfo.tempToSession.tag = "Forenoon";
					} else if (mapping.get("toSession") != null && mapping.get("toSession").toString().equalsIgnoreCase("AN")) {
						mappingInfo.toSession.id = "3";
						mappingInfo.toSession.tag = "Afternoon";
						mappingInfo.tempToSession.id = "3";
						mappingInfo.tempToSession.tag = "Afternoon";
					} else if (mapping.get("toSession") != null && mapping.get("toSession").toString().equalsIgnoreCase("FD")) {
						mappingInfo.toSession.id = "1";
						mappingInfo.toSession.tag = "Full Day";
						mappingInfo.tempToSession.id = "1";
						mappingInfo.tempToSession.tag = "Full Day";
					}
					if ((boolean) mapping.get("mode") == true) {
						mappingInfo.mode = "offline";
						if (!Utils.isNullOrEmpty(mapping.get("applicationStatusDisplayText")))
							mappingInfo.status = mapping.get("applicationStatusDisplayText").toString();
					} else {
						mappingInfo.mode = "online";
						if (!Utils.isNullOrEmpty(mapping.get("applicantStatusDisplayText")))
							mappingInfo.status = mapping.get("applicantStatusDisplayText").toString();
					}
					if (!Utils.isNullOrEmpty(mapping.get("processCode")))
						mappingInfo.processCode = mapping.get("processCode").toString();
					if (!Utils.isNullOrEmpty(mapping.get("approverLatestComment")))
						mappingInfo.comment = mapping.get("approverLatestComment").toString();
					if (!Utils.isNullOrEmpty(mapping.get("authorizerLatestComment")))
						mappingInfo.comment = mapping.get("authorizerLatestComment").toString();
					if (!Utils.isNullOrEmpty(mapping.get("totalNumofDays")))
						mappingInfo.totalDays = mapping.get("totalNumofDays").toString();
					
//					mappingInfo.approverForwardedEmployee.label = String.valueOf(mapping.get("LeaveTypeID"));
//					mappingInfo.approverForwardedEmployee.value = (String) mapping.get("LeaveTypeName");
//					
//					mappingInfo.approverForwarded2Employee.label = String.valueOf(mapping.get("LeaveTypeID"));
//					mappingInfo.approverForwarded2Employee.value = (String) mapping.get("LeaveTypeName");
					
					  if(!Utils.isNullOrEmpty(mapping.get("doc_file_name_unique")) && !Utils.isNullOrEmpty(mapping.get("doc_upload_process_code")) && !Utils.isNullOrEmpty(mapping.get("doc_file_name_original"))) {
				            FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
				            fileUploadDownloadDTO.setActualPath(mapping.get("doc_file_name_unique").toString());
				            fileUploadDownloadDTO.setProcessCode(mapping.get("doc_upload_process_code").toString());
				            fileUploadDownloadDTO.setOriginalFileName(mapping.get("doc_file_name_original").toString());
				            mappingInfo.setFileUploadDownloadDTO(fileUploadDownloadDTO);
				          //  aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
				         //   mappingInfo.setLeaveDocumentUrl(fileUploadDownloadDTO.getPreSignedUrl());
				        }
					result.dto.add(mappingInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public ApiResult<EmpLeaveEntryDTO> editLeaveApplication(String id) {
		ApiResult<EmpLeaveEntryDTO> result = new ApiResult<EmpLeaveEntryDTO>();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				EmpLeaveEntryDBO MappingInfo = context.find(EmpLeaveEntryDBO.class, Integer.parseInt(id));
				if (MappingInfo != null) {
					result.success = true;
					result.dto = new EmpLeaveEntryDTO();
					result.dto.id = MappingInfo.id;
					result.dto.employeeId = MappingInfo.empID.id.toString();
					try {
						result.dto.startDate =MappingInfo.startDate;
						result.dto.endDate =MappingInfo.endDate;
						result.dto.tempStartDate =MappingInfo.startDate;
						result.dto.tempEndDate =MappingInfo.endDate;
						result.dto.reason = MappingInfo.reason;
						result.dto.isExempted = MappingInfo.leaveTypecategory.isExemption.toString();
						result.dto.leaveTypecategory = new ExModelBaseDTO();
						result.dto.leaveTypecategory.id = String.valueOf(MappingInfo.leaveTypecategory.id);
						result.dto.leaveTypecategory.tag = String.valueOf(MappingInfo.leaveTypecategory.leaveTypeName);
						result.dto.fromSession = new ExModelBaseDTO();
						result.dto.tempFormSession = new ExModelBaseDTO();
						if (MappingInfo.fromSession != null && MappingInfo.fromSession.equalsIgnoreCase("FN")) {
							result.dto.fromSession.id = "2";
							result.dto.fromSession.tag = "Forenoon";
							result.dto.tempFormSession.id = "2";
							result.dto.tempFormSession.tag = "Forenoon";
						} else if (MappingInfo.fromSession != null && MappingInfo.fromSession.equalsIgnoreCase("AN")) {
							result.dto.fromSession.id = "3";
							result.dto.fromSession.tag = "Afternoon";
							result.dto.tempFormSession.id = "3";
							result.dto.tempFormSession.tag = "Afternoon";
						} else if (MappingInfo.fromSession != null && MappingInfo.fromSession.equalsIgnoreCase("FD")) {
							result.dto.fromSession.id = "1";
							result.dto.fromSession.tag = "Full Day";
							result.dto.tempFormSession.id = "1";
							result.dto.tempFormSession.tag = "Full Day";
						}
						result.dto.toSession = new ExModelBaseDTO();
						result.dto.tempToSession = new ExModelBaseDTO();
						if (MappingInfo.toSession != null && MappingInfo.toSession.equalsIgnoreCase("FN")) {
							result.dto.toSession.id = "2";
							result.dto.toSession.tag = "Forenoon";
							result.dto.tempToSession.id = "2";
							result.dto.tempToSession.tag = "Forenoon";
						} else if (MappingInfo.toSession != null && MappingInfo.toSession.equalsIgnoreCase("AN")) {
							result.dto.toSession.id = "3";
							result.dto.toSession.tag = "Afternoon";
							result.dto.tempToSession.id = "3";
							result.dto.tempToSession.tag = "Afternoon";
						} else if (MappingInfo.toSession != null && MappingInfo.toSession.equalsIgnoreCase("FD")) {
							result.dto.toSession.id = "1";
							result.dto.toSession.tag = "Full Day";
							result.dto.tempToSession.id = "1";
							result.dto.tempToSession.tag = "Full Day";
						}
						if (!Utils.isNullOrEmpty(MappingInfo.erpApplicantWorkFlowProcessDBO) && !Utils.isNullOrEmpty(MappingInfo.erpApplicantWorkFlowProcessDBO.id))
							result.dto.applicantWorkFlowProcessId = MappingInfo.erpApplicantWorkFlowProcessDBO.id;
						if (!Utils.isNullOrEmpty(MappingInfo.erpApplicationWorkFlowProcessDBO) && !Utils.isNullOrEmpty(MappingInfo.erpApplicationWorkFlowProcessDBO.id))
							result.dto.applicationWorkFlowProcessId = MappingInfo.erpApplicationWorkFlowProcessDBO.id;
						if (MappingInfo.offline)
							result.dto.leaveApplication = "offline";
						else
							result.dto.leaveApplication = "online";
						if (!Utils.isNullOrEmpty(MappingInfo.leaveDocumentUrl))
							result.dto.leaveDocumentUrl = MappingInfo.leaveDocumentUrl;
						Tuple list = leaveApplicationTransaction.getDataOnLeaveType(String.valueOf(MappingInfo.leaveTypecategory.id));
						if (!Utils.isNullOrEmpty(list)) {
							if (!Utils.isNullOrEmpty(list.get("isLeaveTypeDocumentRequired"))) {
								result.dto.isLeaveTypeDocumentRequired = (boolean) list.get("isLeaveTypeDocumentRequired");
							}
							if (!Utils.isNullOrEmpty(list.get("leavePolicy"))) {
								result.dto.leavePolicy = list.get("leavePolicy").toString();
							}							
						}
						if(!Utils.isNullOrEmpty(MappingInfo.totalDays))
						result.dto.totalDays= MappingInfo.totalDays.toString();
						
						  if(!Utils.isNullOrEmpty(MappingInfo.leaveDocumentUrlDBO) && !Utils.isNullOrEmpty(MappingInfo.leaveDocumentUrlDBO.getFileNameUnique()) &&
								  !Utils.isNullOrEmpty(MappingInfo.getLeaveDocumentUrlDBO().getUrlFolderListDBO()) && !Utils.isNullOrEmpty(MappingInfo.getLeaveDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode()) 
								  && !Utils.isNullOrEmpty(MappingInfo.leaveDocumentUrlDBO.getFileNameOriginal())) {
					            FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					            fileUploadDownloadDTO.setActualPath(MappingInfo.leaveDocumentUrlDBO.getFileNameUnique());
					            fileUploadDownloadDTO.setProcessCode(MappingInfo.getLeaveDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
					            fileUploadDownloadDTO.setOriginalFileName(MappingInfo.leaveDocumentUrlDBO.getFileNameOriginal());
					            result.dto.setFileUploadDownloadDTO(fileUploadDownloadDTO);
					         //   aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					          //  result.dto.setLeaveDocumentUrl(fileUploadDownloadDTO.getPreSignedUrl());
					        }
						  
						} catch (Exception e) {
						e.printStackTrace();
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
		return result;
	}

	public boolean checkLeavesAvailableForLeaveType(EmpLeaveEntryDTO data, Integer year, EmpLeaveAllocationDBO dbo)
			throws Exception {
		boolean leaveAvailable = false;
		float daysBetweenCount =0;
		try {
			 daysBetweenCount = calculateTotalLeavesAppliedDateDifferences(data, Utils.convertLocalDateToStringDate(data.startDate), Utils.convertLocalDateToStringDate(data.endDate),data.fromSession.tag, data.toSession.tag);
			if(data.id>0) {
				float daysBetweenCount1 = calculateTotalLeavesAppliedDateDifferences(data, Utils.convertLocalDateToStringDate(data.tempStartDate),Utils.convertLocalDateToStringDate(data.tempEndDate),data.tempFormSession.tag, data.tempToSession.tag);
				if(daysBetweenCount>=daysBetweenCount1)
					daysBetweenCount=daysBetweenCount-daysBetweenCount1;
				else {
					leaveAvailable = true;
					return leaveAvailable;
				}
			}
			float leaveBalance = dbo.getAllottedLeaves().floatValue() - dbo.getSanctionedLeaves().floatValue() - dbo.getLeavesPending().floatValue();
			if (leaveBalance >= daysBetweenCount) {
				leaveAvailable = true;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return leaveAvailable;
	}

	private int calculateHolidayForDateRange(String startDate1, String endDate1, String locationId) throws Exception {
		int count = 0;
		Map<String, String> holidaysMap = null;
		List<Tuple> holidays = leaveApplicationTransaction.getHolidays(startDate1, endDate1, locationId);
		if(!Utils.isNullOrEmpty(holidays)) {
			for (Tuple holiday : holidays) {
				holidaysMap = new HashMap<String, String>();
				LocalDate startDate = !Utils.isNullOrEmpty(holiday.get("startDate"))? Utils.convertStringDateToLocalDate(holiday.get("startDate").toString()) : null;
				LocalDate endDate = !Utils.isNullOrEmpty(holiday.get("endDate"))? Utils.convertStringDateToLocalDate(holiday.get("endDate").toString()) : null;
	
				long diffInDays1 = 1l;
				if (startDate1.contains("Z") && endDate1.contains("Z"))
					diffInDays1 = Duration.between(Utils.convertStringDateTimeToLocalDate(holiday.get("endDate").toString()).atStartOfDay(),
									Utils.convertStringDateTimeToLocalDate(holiday.get("startDate").toString()).atStartOfDay()).toDays();
				else
					diffInDays1 = Duration.between(Utils.convertStringDateToLocalDate(holiday.get("endDate").toString()).atStartOfDay(),
									Utils.convertStringDateToLocalDate(holiday.get("startDate").toString()).atStartOfDay()).toDays();
				System.out.println("diffInDays1 " + diffInDays1);
				LocalDate startDatetemp = startDate;
				for (int i = 1; i <= diffInDays1 + 1; i++) {
					if (endDate.compareTo(startDatetemp) >= 0) {
						holidaysMap.put(Utils.convertLocalDateToStringDate(startDate),Utils.convertLocalDateToStringDate(startDate));
						startDatetemp = startDatetemp.plusDays(1);
					}
				}
			}
		}

		if (startDate1.contains("Z") && endDate1.contains("Z")) {
			LocalDate stratDate = Utils.convertStringDateTimeToLocalDate(startDate1);
			while (stratDate.isBefore(Utils.convertStringDateTimeToLocalDate(endDate1))
					|| stratDate.equals(Utils.convertStringDateTimeToLocalDate(endDate1))) {
				if (holidaysMap != null && holidaysMap.containsKey(
						Utils.convertLocalDateToStringDate(Utils.convertStringDateTimeToLocalDate(startDate1)))) {
					count++;
				}
				stratDate = stratDate.plusDays(1);
			}
		} else {
			LocalDate stratDate = Utils.convertStringDateToLocalDate(startDate1);
			while (stratDate.isBefore(Utils.convertStringDateToLocalDate(endDate1))
					|| stratDate.equals(Utils.convertStringDateToLocalDate(endDate1))) {
				if (holidaysMap != null && holidaysMap.containsKey(
						Utils.convertLocalDateToStringDate(stratDate))) {
					count++;
				}
				stratDate = stratDate.plusDays(1);
			}
		}
		return count;
	}

	private int calculateSundaysForDateRange(String startDate1, String endDate1) throws ParseException {
		LocalDate startDate =LocalDate.now();
		LocalDate endDate =LocalDate.now(); 
		
		if (startDate1.contains("Z") && endDate1.contains("Z")) {
			startDate = Utils.convertStringDateTimeToLocalDate(startDate1);
			endDate = Utils.convertStringDateTimeToLocalDate(endDate1);
		} else {
			startDate = Utils.convertStringDateToLocalDate(startDate1);
			endDate = Utils.convertStringDateToLocalDate(endDate1);
		}
		return Utils.calculateSundaysForDateRange(startDate,endDate);
	}

	public boolean leaveEntryDuplicateCheck(EmpLeaveEntryDTO data) throws Exception {
		boolean isExist = false;
		List<Tuple> mappings = leaveApplicationTransaction.duplicateCheckForLeaveEntry(data);
		for (Tuple mapping : mappings) {
			if ((mapping.get("endSession").toString().equalsIgnoreCase("FN") && (data.fromSession.tag.equalsIgnoreCase("Forenoon") || data.toSession.tag.equalsIgnoreCase("Forenoon")))
					|| (mapping.get("endSession").toString()).equalsIgnoreCase("AN") && (data.fromSession.tag.equalsIgnoreCase("Afternoon") || data.toSession.tag.equalsIgnoreCase("Afternoon"))
					|| (mapping.get("endSession").toString()).equalsIgnoreCase("FD") && (data.fromSession.tag.equalsIgnoreCase("Forenoon") || data.toSession.tag.equalsIgnoreCase("Forenoon")
							|| data.fromSession.tag.equalsIgnoreCase("Afternoon") || data.toSession.tag.equalsIgnoreCase("Afternoon")
							||  data.fromSession.tag.equalsIgnoreCase("Full Day") || data.toSession.tag.equalsIgnoreCase("Full Day"))) {
				isExist = true;
			} else {
				isExist = false;
			}
		}
		return isExist;
	}

	public boolean leaveEntryDuplicateCheckForUpdate(EmpLeaveEntryDTO data) throws Exception {
		boolean isExist = false;
		List<Tuple> mappings = leaveApplicationTransaction.duplicateCheckForLeaveEntry(data);
		for (Tuple mapping : mappings) {
			if (!Utils.isNullOrEmpty(mapping.get("ID")) && (data.id) == Integer.parseInt(mapping.get("ID").toString())) {
				isExist = false;
			} else {
				if ((mapping.get("endSession").toString().equalsIgnoreCase("FN") && (data.fromSession.tag.equalsIgnoreCase("Forenoon") || data.toSession.tag.equalsIgnoreCase("Forenoon")))
						|| (mapping.get("endSession").toString()).equalsIgnoreCase("AN") && (data.fromSession.tag.equalsIgnoreCase("Afternoon") || data.toSession.tag.equalsIgnoreCase("Afternoon"))
						|| (mapping.get("endSession").toString()).equalsIgnoreCase("FD") && (data.fromSession.tag.equalsIgnoreCase("Forenoon") || data.toSession.tag.equalsIgnoreCase("Forenoon")
								|| data.fromSession.tag.equalsIgnoreCase("Afternoon") || data.toSession.tag.equalsIgnoreCase("Afternoon")
								||  data.fromSession.tag.equalsIgnoreCase("Full Day") || data.toSession.tag.equalsIgnoreCase("Full Day"))) {
					isExist = false;
				} else {
					isExist = true;
				}
			}
		}
		return isExist;
	}

	public String validateApplyLeave(EmpLeaveEntryDTO data, int year) throws Exception {
		String error = null;
		
		// checking crossing of years based on initilize month of employee
		boolean isCrossingLeaveYear = false;
		List<LocalDate> dateList = new ArrayList<LocalDate>();
		List<String> crossingMonths = new ArrayList<String>();
		String initializeMonth = "";
		int month = 0;
		dateList.add((data.endDate));
		LocalDate stratDate = (data.startDate);
		while (stratDate.isBefore((data.endDate))) {
			dateList.add(stratDate);
			stratDate = stratDate.plusDays(1);
		}
		Tuple initiMonth = leaveApplicationTransaction.getInitilizeMonthForEmployee(Integer.parseInt(data.employeeId));
		month = Integer.parseInt(initiMonth.get("month").toString());
		if (month == 1)
			initializeMonth = "January";
		else if (month == 6)
			initializeMonth = "June";
		
		String startDateOfInitilizeYear =null;
		String endDateOfInitilizeYear=null;
		if (!Utils.isNullOrEmpty(initializeMonth)) {
			data.initilizeMonth = initializeMonth;
			if (MonthMap.containsValue(initializeMonth)) {
				int initMonth = 0;
				int prevMonth = 0;
				for (Entry<Integer, String> map : MonthMap.entrySet()) {
					if (map.getValue().equalsIgnoreCase(initializeMonth)) {
						initMonth = map.getKey();
						prevMonth = map.getKey() - 1;
						if (initMonth == 1) {
							initMonth = map.getKey();
							prevMonth = 12;
							break;
						}
						break;
					}
				}
				String day1 = "01";
				String day2 = "31";
				if(initMonth==1) {
					startDateOfInitilizeYear = day1 + "/" + (initMonth<10 ? "0" + initMonth : initMonth) + "/" + year;
					endDateOfInitilizeYear = day2 + "/" + (prevMonth<10 ? "0" + prevMonth : prevMonth) + "/" + ( year+1);
				}else {
					startDateOfInitilizeYear = day1 + "/" + (initMonth<10 ? "0" + initMonth : initMonth)  + "/" + year;
					endDateOfInitilizeYear = day2 + "/" + (prevMonth<10 ? "0" + prevMonth : prevMonth)  + "/" +( year+1);
				}
				crossingMonths.add(startDateOfInitilizeYear);
				crossingMonths.add(endDateOfInitilizeYear);
			}
		}
		
		for (LocalDate lodDate : dateList) {
			 if(!(lodDate.isAfter(Utils.convertStringDateToLocalDate(startDateOfInitilizeYear)) && lodDate.isBefore((Utils.convertStringDateToLocalDate(endDateOfInitilizeYear))))){
					isCrossingLeaveYear = true;
					break;
			}
		}
		if (isCrossingLeaveYear == true) {
			if (data.initilizeMonth.equalsIgnoreCase("june"))
				return error = "Leave application for the month of May and June cannot be submitted together. Please submit separate leave applications";
			else if (data.initilizeMonth.equalsIgnoreCase("January"))
				return error = "Leave application for the month of December and January cannot be submitted together. Please submit separate leave applications";

		}
		
		// checking either sunday and holiday is working day for employee. 
		boolean sundayWorkingDay = false;
		boolean holidayWorkingDay = false;
		boolean sundayStartDate = false;
		boolean sundayEndDate = false;
		Tuple isSundayWorkingDay = leaveApplicationTransaction.getIsSundayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isSundayWorkingDay)) {
			if (isSundayWorkingDay.get("sundayWorking")!=null && isSundayWorkingDay.get("sundayWorking").toString().equalsIgnoreCase("false")) {
				sundayWorkingDay = true;
			    data.sundayWorkingDay=sundayWorkingDay;
			}
		}

		if (!sundayWorkingDay) {
			sundayStartDate = Utils.checkISSunday(data.startDate);
			sundayEndDate = Utils.checkISSunday(data.endDate);
			if (sundayStartDate && sundayEndDate) {
				return error = "Sunday is not a working day.";
			}
		}
		
		Tuple isHolidayWorkingDay = leaveApplicationTransaction.getIsHolidayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isHolidayWorkingDay)) {
			if (isHolidayWorkingDay.get("holidayWorking")!=null && isHolidayWorkingDay.get("holidayWorking").toString().equalsIgnoreCase("true")) {
				holidayWorkingDay = true;
				data.holidayWorkingDay=holidayWorkingDay;
			}
		}
		
		List<Tuple> list = leaveApplicationTransaction.getEmployeeDetails(data.employeeId);
		for (Tuple list1 : list) {
			data.locationId = list1.get("locationId").toString();
		}
		List<EmpHolidayEventsDBO> holidayList = leaveApplicationTransaction.getEmpHolidayEventsDBO(data,year);
		Map<LocalDate,EmpHolidayEventsDBO> holidayListMap= new LinkedHashMap<LocalDate, EmpHolidayEventsDBO>();
	    for (EmpHolidayEventsDBO empHolidayEventsDBO : holidayList) {
	    	holidayListMap.put(empHolidayEventsDBO.holidayEventsStartDate, empHolidayEventsDBO);
		}
		if(!holidayWorkingDay) {
			//if either start date or end date is holiday display error message.
			//return error = "Selected Holiday. which is not working day for employee.";
			
			if(holidayListMap.containsKey(data.startDate) && holidayListMap.containsKey(data.endDate))
				return error = "Selected holiday. Which is not working day for employee.";
		}
		return error;
	}

	public String validateMultiMonthLeaveApply(EmpLeaveEntryDTO data)  throws Exception{
		String error = "";
		boolean sundayWorkingDay = false;
		boolean holidayWorkingDay = false;
		boolean countSundayForLeaveType = false;
		boolean countHolidayForLeaveType = false;
		int maxLeaveAllowedPerMonth = 0;
		int maxOnlineLeavePermittedInMonth = 0;
		int maxOnlineLeaveWithProof = 0;
		String warning = "";
		Tuple isSundayWorkingDay = leaveApplicationTransaction.getIsSundayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isSundayWorkingDay)) {
			if (isSundayWorkingDay.get("sundayWorking") != null
					&& isSundayWorkingDay.get("sundayWorking").toString().equalsIgnoreCase("false"))
				sundayWorkingDay = true;
		}
		Tuple isHolidayWorkingDay = leaveApplicationTransaction.getIsHolidayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isHolidayWorkingDay)) {
			if (isHolidayWorkingDay.get("holidayWorking") != null
					&& isHolidayWorkingDay.get("holidayWorking").toString().equalsIgnoreCase("true"))
				holidayWorkingDay = true;
		}
		EmpLeaveTypeDBO empLeaveTypeDBO = leaveApplicationTransaction.getDataOnLeaveType1(data.leaveTypecategory.id);
		countSundayForLeaveType = empLeaveTypeDBO.getIsSundayCounted();
		countHolidayForLeaveType = empLeaveTypeDBO.getIsHolidayCounted();
		maxLeaveAllowedPerMonth = empLeaveTypeDBO.getMaxOnlineLeaveInMonth();
		maxOnlineLeavePermittedInMonth = empLeaveTypeDBO.getMaxOnlineLeavePermittedInMonth() != null? empLeaveTypeDBO.getMaxOnlineLeavePermittedInMonth(): 0;
		maxOnlineLeaveWithProof = empLeaveTypeDBO.getMaxOnlineLeaveWithProof() != null ? empLeaveTypeDBO.getMaxOnlineLeaveWithProof() : 0;
		
		float previouslyAppliedLeaveCount = 0;
		float daysSelectedCount = 0;
		int monthDiff = getMonthDiff(data.startDate,data.endDate); 
		
	//	if(monthDiff > 0) {
			LocalDate startDate  = data.startDate;
			int month = startDate.getMonthValue();
			int year = startDate.getYear();
			String startSession = "";
			String endSession = "";
			daysSelectedCount = 0;
			for (int k = 0; k <= monthDiff; k++) {
				
				if(k == 0 && monthDiff ==  0) {
					startSession = data.getFromSession().tag;
					endSession = data.getToSession().tag;
				}else if(k == 0 && monthDiff > 0) {
					startSession = data.getFromSession().tag;
					endSession = "Full Day";
				}else if(k > 0 && k < monthDiff) {
					startSession = "Full Day";
					endSession = "Full Day";
				}else if(k == monthDiff) {
					startSession = "Full Day";
					endSession = data.getToSession().tag;
				}
				daysSelectedCount = calculateTotalLeavesAppliedDateDifferences(data,
						Utils.convertLocalDateToStringDate(data.startDate), Utils.convertLocalDateToStringDate(data.endDate),
						startSession, endSession);
				
				List<Tuple> mappings = leaveApplicationTransaction.getEmpLeavesAppliedForOnlineNew(data, month ,year);
				month = month == 12 ? month = 1: ++month;
				year =  month == 12 ? ++year: year;
				previouslyAppliedLeaveCount = getPreviousLeavesofMonth(mappings, sundayWorkingDay, holidayWorkingDay, data, countSundayForLeaveType, countHolidayForLeaveType);
				float totalLeavesinMonth =  previouslyAppliedLeaveCount + daysSelectedCount;
				
				if(maxLeaveAllowedPerMonth < totalLeavesinMonth) {
					if(maxOnlineLeavePermittedInMonth >0) {
						if(totalLeavesinMonth <= maxOnlineLeavePermittedInMonth) {
							// Warning, but allow to save
							warning = "You are about to exceed the maximum leaves allowed for the selected leave type this month. Would you still like to continue?";
						}else if(totalLeavesinMonth > maxOnlineLeavePermittedInMonth){
							if(maxOnlineLeaveWithProof >0) {
								if(totalLeavesinMonth <= maxOnlineLeaveWithProof) {
									// Warning with FIle Proof
									warning = "You are exceeding the maximum leaves allowed for the selected leave type this month.";
									// check File Available if not show error
								}else {
									// Error
									error = "You are restricted from applying for more leaves than the permitted maximum.";
								}
							}else {
								// Error
								error = "You are restricted from applying for more leaves than the permitted maximum.";
							}
						}
					}else {
						error = "You are restricted from applying for more leaves than the permitted maximum.";
					}
				}
			}
			data.setAlertMsg(warning);
		return error;
	}
	private float getPreviousLeavesofMonth(List<Tuple> mappings, boolean  sundayWorkingDay, boolean holidayWorkingDay, EmpLeaveEntryDTO data, boolean countSundayForLeaveType, boolean countHolidayForLeaveType) throws Exception {
		float previouslyAppliedLeaveCount = 0;
		int sundayCount = 0;
		int holidayCount = 0;
		for (Tuple mapping : mappings) {
			BigDecimal leav = new BigDecimal(mapping.get("leaves_in_month").toString());
			previouslyAppliedLeaveCount = leav.floatValue();
//			long tempDiffInDays = 1L;
//			if (mapping.get("startDate").toString().contains("Z") && mapping.get("endDate").toString().contains("Z"))
//				tempDiffInDays = Duration.between(
//						Utils.convertStringDateTimeToLocalDate(mapping.get("startDate").toString()).atStartOfDay(),
//						Utils.convertStringDateTimeToLocalDate(mapping.get("endDate").toString()).atStartOfDay())
//						.toDays();
//			else
//				tempDiffInDays = Duration
//						.between(Utils.convertStringDateToLocalDate(mapping.get("startDate").toString()).atStartOfDay(),
//								Utils.convertStringDateToLocalDate(mapping.get("endDate").toString()).atStartOfDay())
//						.toDays();
//			System.out.println("diffInDays1 for online " + tempDiffInDays);
//
//			float i = 0;
//			if (tempDiffInDays == 0) {
//				if ((mapping.get("startSession").toString().equalsIgnoreCase("FN")
//						&& mapping.get("endSession").toString().equalsIgnoreCase("FN"))
//						|| (mapping.get("startSession").toString().equalsIgnoreCase("AN")
//								&& mapping.get("endSession").toString().equalsIgnoreCase("AN"))) {
//					i = (float) 0.5;
//				} else {
//					i = 1;
//				}
//			} else {
//				if ((mapping.get("startSession").toString().equalsIgnoreCase("AN")
//						&& mapping.get("endSession").toString().equalsIgnoreCase("AN"))
//						|| (mapping.get("startSession").toString().equalsIgnoreCase("AN")
//								&& mapping.get("endSession").toString().equalsIgnoreCase("FD"))) {
//					i = tempDiffInDays + (float) 0.5;
//				} else if ((mapping.get("startSession").toString().equalsIgnoreCase("FD")
//						&& mapping.get("endSession").toString().equalsIgnoreCase("FD"))
//						|| (mapping.get("startSession").toString().equalsIgnoreCase("FD")
//								&& mapping.get("endSession").toString().equalsIgnoreCase("AN")))
//					i = tempDiffInDays + 1;
//				else if ((mapping.get("startSession").toString().equalsIgnoreCase("FD")
//						&& mapping.get("endSession").toString().equalsIgnoreCase("FN"))
//						|| (mapping.get("startSession").toString().equalsIgnoreCase("AN")
//								&& mapping.get("endSession").toString().equalsIgnoreCase("FD")))
//					i = (float) (tempDiffInDays + .5);
//				else
//					i = tempDiffInDays;
//			}
//			previouslyAppliedLeaveCount = previouslyAppliedLeaveCount + i;
//
//			sundayCount = calculateSundaysForDateRange(mapping.get("StartDate").toString(),
//					mapping.get("endDate").toString());
//			System.out.println("sundayCount=" + sundayCount);
//			List<Tuple> list = leaveApplicationTransaction.getEmployeeDetails(data.employeeId);
//			for (Tuple list1 : list) {
//				data.locationId = list1.get("campusId").toString();
//			}
//			holidayCount = calculateHolidayForDateRange(mapping.get("StartDate").toString(),
//					mapping.get("endDate").toString(), data.locationId);
//			System.out.println("holidayCount=" + holidayCount);
//			if(!countHolidayForLeaveType) {//holiday true
//				previouslyAppliedLeaveCount = previouslyAppliedLeaveCount - holidayCount;
//			}else {
//				if (holidayWorkingDay) {
//					previouslyAppliedLeaveCount = previouslyAppliedLeaveCount - holidayCount;
//				}
//			}
//			
//			if(!countSundayForLeaveType) {//sunday true
//				previouslyAppliedLeaveCount = previouslyAppliedLeaveCount - sundayCount;
//			}else {
//				if (sundayWorkingDay) {
//					previouslyAppliedLeaveCount = previouslyAppliedLeaveCount - sundayCount;
//				}
//			}
		}
		
		return previouslyAppliedLeaveCount;
	}

	private int getMonthDiff(LocalDate startDate, LocalDate endDate) {
		int monthofStartDate = startDate.getMonthValue();
		int monthofEndDate = endDate.getMonthValue();
		return monthofEndDate - monthofStartDate;
	}

	public boolean checkMonthDifference(LocalDate stDate, LocalDate endDate) {
		int monthofStartDate = stDate.getMonthValue();
		int monthofEndDate = endDate.getMonthValue();
		boolean hasMultiMonths = false;
		int monthDiff = 0; 
		monthDiff = monthofEndDate - monthofStartDate;
		if(monthDiff > 0) {
			hasMultiMonths = true;
		}
		return hasMultiMonths;
	}
	public String validateApplyLeaveForOnline(EmpLeaveEntryDTO data) throws Exception {
		String error = null;
		boolean maxOnlineleaveapplyExceeded = false;
		List<Tuple> mappings = leaveApplicationTransaction.getEmpLeavesAppliedForOnline(data);
		float previouslyAppliedLeaveCount = 0;
		int maxLeaveAllowedPerMonth = 0;
		float daysSelectedCount = 0;
		int sundayCount = 0;
		int holidayCount = 0;
		boolean sundayWorkingDay = false;
		boolean holidayWorkingDay = false;

		Tuple isSundayWorkingDay = leaveApplicationTransaction.getIsSundayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isSundayWorkingDay)) {
			if (isSundayWorkingDay.get("sundayWorking") != null
					&& isSundayWorkingDay.get("sundayWorking").toString().equalsIgnoreCase("false"))
				sundayWorkingDay = true;
		}
		Tuple isHolidayWorkingDay = leaveApplicationTransaction.getIsHolidayWorkingDay(data);
		if (!Utils.isNullOrEmpty(isHolidayWorkingDay)) {
			if (isHolidayWorkingDay.get("holidayWorking") != null
					&& isHolidayWorkingDay.get("holidayWorking").toString().equalsIgnoreCase("true"))
				holidayWorkingDay = true;
		}
		int monthofStartDate = data.startDate.getMonthValue();
		int monthofEndDate = data.endDate.getMonthValue();
		
		int monthDiff = 0; 
		monthDiff = monthofEndDate - monthofStartDate;
		if(monthDiff > 0) {
			LocalDate startDate  = data.startDate;
			int month = startDate.getMonthValue();
			int year = startDate.getYear();
			for (int i = 0; i <= monthDiff; i++) {
				
				List<Tuple> mappings1 = leaveApplicationTransaction.getEmpLeavesAppliedForOnlineNew(data, month ,year);
				month = month == 12 ? month = 1: ++month;
				year =  month == 12 ? ++year: year;
			}
			// firstMonth
			LocalDate  endDateOfMonth = getEndDate(data.startDate);
			float noOfDaysinFirstMonth = calculateTotalLeavesAppliedDateDifferences(new EmpLeaveEntryDTO(), Utils.convertLocalDateToStringDate(data.startDate), Utils.convertLocalDateToStringDate(endDateOfMonth), "Full Day", "Full Day");
			
			System.out.println("No of Days in first Month " + noOfDaysinFirstMonth );
			// secondMonth
			LocalDate stDate = getStartDate(data.endDate);
			float noOfDaysinSecondMonth = calculateTotalLeavesAppliedDateDifferences(new EmpLeaveEntryDTO(), Utils.convertLocalDateToStringDate(stDate), Utils.convertLocalDateToStringDate(data.endDate), "Full Day", "Full Day");
			System.out.println("No of Days in Second Month  " + noOfDaysinSecondMonth );
			
		}
		for (Tuple mapping : mappings) {
			long tempDiffInDays = 1L;
			if (mapping.get("startDate").toString().contains("Z") && mapping.get("endDate").toString().contains("Z"))
				tempDiffInDays = Duration.between(
						Utils.convertStringDateTimeToLocalDate(mapping.get("startDate").toString()).atStartOfDay(),
						Utils.convertStringDateTimeToLocalDate(mapping.get("endDate").toString()).atStartOfDay())
						.toDays();
			else
				tempDiffInDays = Duration
						.between(Utils.convertStringDateToLocalDate(mapping.get("startDate").toString()).atStartOfDay(),
								Utils.convertStringDateToLocalDate(mapping.get("endDate").toString()).atStartOfDay())
						.toDays();
			System.out.println("diffInDays1 for online " + tempDiffInDays);

			float i = 0;
			if (tempDiffInDays == 0) {
				if ((mapping.get("startSession").toString().equalsIgnoreCase("FN")
						&& mapping.get("endSession").toString().equalsIgnoreCase("FN"))
						|| (mapping.get("startSession").toString().equalsIgnoreCase("AN")
								&& mapping.get("endSession").toString().equalsIgnoreCase("AN"))) {
					i = (float) 0.5;
				} else {
					i = 1;
				}
			} else {
				if ((mapping.get("startSession").toString().equalsIgnoreCase("AN")
						&& mapping.get("endSession").toString().equalsIgnoreCase("AN"))
						|| (mapping.get("startSession").toString().equalsIgnoreCase("AN")
								&& mapping.get("endSession").toString().equalsIgnoreCase("FD"))) {
					i = tempDiffInDays + (float) 0.5;
				} else if ((mapping.get("startSession").toString().equalsIgnoreCase("FD")
						&& mapping.get("endSession").toString().equalsIgnoreCase("FD"))
						|| (mapping.get("startSession").toString().equalsIgnoreCase("FD")
								&& mapping.get("endSession").toString().equalsIgnoreCase("AN")))
					i = tempDiffInDays + 1;
				else if ((mapping.get("startSession").toString().equalsIgnoreCase("FD")
						&& mapping.get("endSession").toString().equalsIgnoreCase("FN"))
						|| (mapping.get("startSession").toString().equalsIgnoreCase("AN")
								&& mapping.get("endSession").toString().equalsIgnoreCase("FD")))
					i = (float) (tempDiffInDays + .5);
				else
					i = tempDiffInDays;
			}
			previouslyAppliedLeaveCount = previouslyAppliedLeaveCount + i;

			sundayCount = calculateSundaysForDateRange(mapping.get("StartDate").toString(),
					mapping.get("endDate").toString());
			System.out.println("sundayCount=" + sundayCount);
			List<Tuple> list = leaveApplicationTransaction.getEmployeeDetails(data.employeeId);
			for (Tuple list1 : list) {
				data.locationId = list1.get("campusId").toString();
			}
			holidayCount = calculateHolidayForDateRange(mapping.get("StartDate").toString(),
					mapping.get("endDate").toString(), data.locationId);
			System.out.println("holidayCount=" + holidayCount);

			// if (!isContinousDays) {
			if (!holidayWorkingDay) {
				if (holidayCount > 0 && previouslyAppliedLeaveCount > 0.5) {
					previouslyAppliedLeaveCount = previouslyAppliedLeaveCount - holidayCount;
				}
			}
			if (!sundayWorkingDay && previouslyAppliedLeaveCount > 0.5) {
				if (sundayCount > 0) {
					previouslyAppliedLeaveCount = previouslyAppliedLeaveCount - sundayCount;
				}
			}
		}

		daysSelectedCount = calculateTotalLeavesAppliedDateDifferences(data,
				Utils.convertLocalDateToStringDate(data.startDate), Utils.convertLocalDateToStringDate(data.endDate),
				data.fromSession.tag, data.toSession.tag);

		float totalNumberofDays = previouslyAppliedLeaveCount + daysSelectedCount;
		Tuple list = leaveApplicationTransaction.getDataOnLeaveType(data.leaveTypecategory.id);
		if (!Utils.isNullOrEmpty(list)) {
			maxLeaveAllowedPerMonth = (int) list.get("maxOnlineLeaveMonth");
		}
		if (totalNumberofDays > maxLeaveAllowedPerMonth && totalNumberofDays != maxLeaveAllowedPerMonth)
			maxOnlineleaveapplyExceeded = true;
		else
			maxOnlineleaveapplyExceeded = false;

		if (maxOnlineleaveapplyExceeded) {
			return error = "Online Maximun leaves for month exceeded for selected Leave Type: "
					+ data.leaveTypecategory.tag;
		}
		return error;
	}
	LocalDate getStartDate(LocalDate date){
		int year = date.getYear();
		int month = date.getMonthValue();
		LocalDate stDate =  LocalDate.of(year, month, 1);
		return stDate;
	}
	LocalDate getEndDate(LocalDate date){
		int year = date.getYear();
		int month = date.getMonthValue();
		LocalDate endDate =  LocalDate.of(year, month, date.lengthOfMonth());
		return endDate;
	}
	public EmpLeaveEntryDTO getTotalLeaves(EmpLeaveEntryDTO data, String userId, boolean isLeave, Integer year) throws Exception {
		float totaldays = calculateTotalLeavesAppliedDateDifferences(data,Utils.convertLocalDateToStringDate(data.startDate),Utils.convertLocalDateToStringDate(data.endDate),data.fromSession.tag,data.toSession.tag);
		data.totalDays=BigDecimal.valueOf(totaldays).toString();
		return data;
	}

	public ErpNotificationsDBO removeNotifications(Integer erpWorkFlowProcessNotificationsId, Integer userId, Integer EmpLeaveEntryId) {
		ErpNotificationsDBO erpNotificationsDBO =leaveApplicationTransaction.removeNotifications(erpWorkFlowProcessNotificationsId,userId,EmpLeaveEntryId);
		return erpNotificationsDBO;
		
	}

	public int getYearOnInitilizeMonthofLeaveType(EmpLeaveEntryDTO dto) {
		int year = 0;
		int month = 0;
		int empId = dto.getEmployeeId() != null ?Integer.parseInt(dto.getEmployeeId()): Integer.parseInt(dto.getEmpId());
		Tuple initilizeMonth = leaveApplicationTransaction.getInitilizeMonthForEmployee(empId);
		if(!Utils.isNullOrEmpty(initilizeMonth)) {
			if(!Utils.isNullOrEmpty(initilizeMonth.get("month"))) {
				month = Integer.parseInt(initilizeMonth.get("month").toString());
			}
		}
		String[] date = {};
		
		String refDate= "";
		if(!Utils.isNullOrEmpty(dto.startDate)) {
			refDate = Utils.convertLocalDateToStringDate2(dto.startDate);
		}else {
			LocalDate now =  LocalDate.now();

			refDate = Utils.convertLocalDateToStringDate2(now);
		}
		
		if (refDate.contains("Z"))
			date = (Utils.convertLocalDateToStringDate(Utils.convertStringDateTimeToLocalDate(refDate))).split("/");
		else
			date = (Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(refDate))).split("/");
		
		if (Integer.parseInt(date[1]) == month || Integer.parseInt(date[1]) > month) {
			return year = Integer.parseInt(date[2]);
		} else {
			return year = Integer.parseInt(date[2]) - 1;
		}
	}

	public ErpWorkFlowProcessStatusLogDBO getErpWorkFlowProcessStatusLogDBO(Integer entryId, EmpLeaveEntryDBO dbo, Integer userId) {
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.entryId =entryId;
		ErpWorkFlowProcessDBO e1 = new ErpWorkFlowProcessDBO();
		if (!Utils.isNullOrEmpty(dbo.erpApplicantWorkFlowProcessDBO.id))
			e1.id = dbo.erpApplicantWorkFlowProcessDBO.id;
		else
			e1.id = dbo.erpApplicationWorkFlowProcessDBO.id;
		erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = e1;
		erpWorkFlowProcessStatusLogDBO.createdUsersId = userId;
		erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
		return erpWorkFlowProcessStatusLogDBO;
	}

	public ErpEmailsDBO getEmailDBO(ErpTemplateDBO erpTemplateDBO, Integer entryId, Integer userId, EmpLeaveEntryDTO dto) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id = userId;
		erpEmailsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = null;
		if (!Utils.isNullOrEmpty(erpTemplateDBO)) {
			msgBody = erpTemplateDBO.getTemplateContent();
			if (!Utils.isNullOrEmpty(msgBody)) {
				msgBody = msgBody.replace("[EMPLOYEE_NAME]", dto.name);
				msgBody = msgBody.replace("[EMPLOYEE_ID] ", dto.employeeId);
				msgBody = msgBody.replace("[LEAVE_TYPE]", dto.leaveTypecategory.tag);
				msgBody = msgBody.replace("[FROM_DATE]",Utils.convertLocalDateToStringDate(dto.startDate));
				msgBody = msgBody.replace("[TO_DATE]", Utils.convertLocalDateToStringDate(dto.endDate));
				if(!Utils.isNullOrEmpty(dto.fromSession))
					msgBody = msgBody.replace("[FROM_SESSION]",dto.fromSession.tag);
				if(!Utils.isNullOrEmpty(dto.toSession))
					msgBody = msgBody.replace("[TO_SESSION]", dto.toSession.tag);
				if(!Utils.isNullOrEmpty(dto.campus))
					msgBody = msgBody.replace("[CAMPUS]", dto.campus);
				if(!Utils.isNullOrEmpty(dto.department))
					msgBody = msgBody.replace("[DEPARTMENT]", dto.department);	
				if(!Utils.isNullOrEmpty(dto.totalDays))
					msgBody = msgBody.replace("[TOTAL_NUMBER_OF_LEAVES]", dto.totalDays);	
				if(!Utils.isNullOrEmpty(dto.processCode))
				{
					if(dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_FORWARDED") && !Utils.isNullOrEmpty(dto.getApproverForwardedEmployeeComments()) && !Utils.isNullOrEmpty(dto.getApproverForwardedEmployee())) {
						msgBody = msgBody.replace("[FORWARDER1_REASON]", dto.getApproverForwardedEmployeeComments());
						msgBody = msgBody.replace("[FORWARDER1_NAME]", dto.getApproverForwardedEmployee().getLabel());
					}
					else if(dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_FORWARDED") && !Utils.isNullOrEmpty(dto.getApproverForwarded2EmployeeComments()) && !Utils.isNullOrEmpty(dto.getApproverForwarded2Employee())){
						msgBody = msgBody.replace("[FORWARDER2_REASON]", dto.getApproverForwarded2EmployeeComments());
						msgBody = msgBody.replace("[FORWARDER2_NAME]", dto.getApproverForwarded2Employee().getLabel());
					}	
					else if(!Utils.isNullOrEmpty(dto.getApproverComments()))
						msgBody = msgBody.replace("[APPROVER_REASON]", dto.getApproverComments());
				}		
			}
		}
		erpEmailsDBO.emailContent = msgBody;
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
			erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
			erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
		erpEmailsDBO.recipientEmail = dto.email;
		erpEmailsDBO.createdUsersId = userId;
		erpEmailsDBO.recordStatus = 'A';
		return erpEmailsDBO;
	}

	public ErpNotificationsDBO getNotificationsDBO(Integer entryId, Integer userId) {
		ErpNotificationsDBO erpNotifications = new ErpNotificationsDBO();
		erpNotifications.entryId = entryId;
		ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
		erpUsersDBO.id = userId;
		erpNotifications.erpUsersDBO = erpUsersDBO;
		erpNotifications.createdUsersId = userId;
		erpNotifications.recordStatus = 'A';
		return erpNotifications;
	}

	public Mono<ApiResult<EmpLeaveTypeDTO>> getEmpLeaveType(String value) {
		EmpLeaveTypeDBO empType = leaveApplicationTransaction.getEmpLeaveType(value);
		return this.convertLeaveTypeDboToDto(empType);
	}

	private Mono<ApiResult<EmpLeaveTypeDTO>> convertLeaveTypeDboToDto(EmpLeaveTypeDBO dbleavetypeMappingInfo) {
		ApiResult<EmpLeaveTypeDTO> result = new ApiResult<EmpLeaveTypeDTO>();
		EmpLeaveTypeDTO empLeaveTypeDTO = new EmpLeaveTypeDTO();
		if (dbleavetypeMappingInfo != null) {
            empLeaveTypeDTO.id = dbleavetypeMappingInfo.id;
            empLeaveTypeDTO.leavetype = dbleavetypeMappingInfo.leaveTypeName;
            empLeaveTypeDTO.leavecode = dbleavetypeMappingInfo.leaveTypeCode;
            empLeaveTypeDTO.leaveTypeColorCodeHexvalue = dbleavetypeMappingInfo.leaveTypeColorCodeHexvalue;
            empLeaveTypeDTO.isApplyOnline = dbleavetypeMappingInfo.isApplyOnline;
            empLeaveTypeDTO.partialDaysAllowed = dbleavetypeMappingInfo.partialDaysAllowed;
            empLeaveTypeDTO.autoApprovedDays = dbleavetypeMappingInfo.autoApprovedDays;
            //empLeaveTypeDTO.continousDays = dbleavetypeMappingInfo.continousDays;
            empLeaveTypeDTO.maxOnlineLeaveInMonth = dbleavetypeMappingInfo.maxOnlineLeaveInMonth;
            empLeaveTypeDTO.isExemption = dbleavetypeMappingInfo.isExemption;
            empLeaveTypeDTO.supportingDoc = dbleavetypeMappingInfo.supportingDoc;
            empLeaveTypeDTO.isLeave = dbleavetypeMappingInfo.isLeave;
            empLeaveTypeDTO.autoApproveLeave = dbleavetypeMappingInfo.autoApproveLeave;
            empLeaveTypeDTO.isLeaveAdvance=dbleavetypeMappingInfo.isLeaveAdvance;
            empLeaveTypeDTO.leavePolicy = dbleavetypeMappingInfo.leavePolicy;
            empLeaveTypeDTO.maxOnlineLeavePermittedInMonth=dbleavetypeMappingInfo.maxOnlineLeavePermittedInMonth;
            empLeaveTypeDTO.maxOnlineLeaveWithProof=dbleavetypeMappingInfo.maxOnlineLeaveWithProof;
            empLeaveTypeDTO.isHolidayCounted=dbleavetypeMappingInfo.isHolidayCounted;
            empLeaveTypeDTO.isSundayCounted=dbleavetypeMappingInfo.isSundayCounted;
            result.success = true;
            result.dto = empLeaveTypeDTO;
            
        }else {
        	result.success = false;
        	result.failureMessage = "Data Not Found";
        }
		return Mono.just(result);
	}

	public String validateByLeaveType(EmpLeaveEntryDTO data, int year) throws ParseException {
		EmpLeaveTypeDBO empLeaveType = leaveApplicationTransaction.getEmpLeaveType(data.leaveTypecategory.id);
		String error = null;
		//can apply online
		if(!empLeaveType.isApplyOnline) {
			return error = "Selected Leave Type can not be applied Online. ";
		}
		
		//can apply continous dates
//		if(!empLeaveType.continousDays) {
//			if(!(data.startDate.equals(data.endDate)))
//				return error = "Selected Leave Type can not be applied Continous days. ";
//		}
		
		//can apply partial leave
		if(!empLeaveType.partialDaysAllowed) {
			if(!(data.fromSession.tag.equalsIgnoreCase("Full Day")) || !(data.toSession.tag.equalsIgnoreCase("Full Day")))
				return error = "Selected Leave Type can not be applied Partial day. ";
		}
		
		//can apply Leave in Advance
		if(!empLeaveType.isLeaveAdvance) {
			if(data.startDate.isAfter(LocalDate.now())) {
				return error = "Selected Leave Type can not be applied in Advance. ";
			}
		}
		
		// document required
		
		//Leave should be applied within 2 working days from leave date.
		
		return error;
	}

	public void setUploadDocumentUrl(CommonUploadDownloadDTO  uploadDocumentDto, EmpLeaveEntryDBO empLeaveEntryDBO, Integer userId, List<FileUploadDownloadDTO> uniqueFileNameList) {
	    UrlAccessLinkDBO urlDocumentDbo = null;
	    if (Utils.isNullOrEmpty(empLeaveEntryDBO.getLeaveDocumentUrlDBO())) {
	       if (!Utils.isNullOrEmpty(uploadDocumentDto) && !Utils.isNullOrEmpty(uploadDocumentDto.getNewFile()) && uploadDocumentDto.getNewFile() &&
	             !Utils.isNullOrEmpty(uploadDocumentDto.getOriginalFileName())) {
	    	   urlDocumentDbo = new UrlAccessLinkDBO();
	    	   urlDocumentDbo.setCreatedUsersId(userId);
	    	   urlDocumentDbo.setRecordStatus('A');
	       }
	    } else {
	    	urlDocumentDbo = empLeaveEntryDBO.getLeaveDocumentUrlDBO();
	       if(Utils.isNullOrEmpty(uploadDocumentDto)){
	    	   urlDocumentDbo.setRecordStatus('D');
	       }
	       urlDocumentDbo.setModifiedUsersId(userId);
	    }
	    if (!Utils.isNullOrEmpty(uploadDocumentDto) && !Utils.isNullOrEmpty(uploadDocumentDto.getNewFile()) && uploadDocumentDto.getNewFile()) {
	    	urlDocumentDbo = aWSS3FileStorageService.createURLAccessLinkDBO(urlDocumentDbo, uploadDocumentDto.getProcessCode(), uploadDocumentDto.getUniqueFileName(), uploadDocumentDto.getOriginalFileName(), userId);
	    }
	    if (!Utils.isNullOrEmpty(uploadDocumentDto) && !Utils.isNullOrEmpty(uploadDocumentDto.getProcessCode()) && !Utils.isNullOrEmpty(uploadDocumentDto.getUniqueFileName()) && !Utils.isNullOrEmpty(uploadDocumentDto.getNewFile()) && uploadDocumentDto.getNewFile()) {
	       uniqueFileNameList.addAll(aWSS3FileStorageService.createFileListForActualCopy(uploadDocumentDto.getProcessCode(), uploadDocumentDto.getUniqueFileName()));
	    }
	    empLeaveEntryDBO.setLeaveDocumentUrlDBO(urlDocumentDbo);
	}

	public ApiResult<EmpHolidayDTO> getHolidayList(String empId, String leaveType,ApiResult<EmpHolidayDTO> result) throws Exception {
		List<Tuple> holidayList = leaveApplicationTransaction.getHolidaysList(empId, leaveType);
		List<EmpHolidayListDTO> holidayList1 = new ArrayList<EmpHolidayListDTO>();
		EmpHolidayDTO holidayDto = new EmpHolidayDTO();
		EmpHolidayListDTO dto = null;
		for (Tuple tuple : holidayList) {
			dto = new EmpHolidayListDTO();
			dto.setDate(tuple.get("startDate").toString());
			dto.setReason(tuple.get("reason").toString());
			holidayList1.add(dto);
		}
		holidayDto.setHolidayList(holidayList1);
		if(!leaveType.equals("Restricted Holidays")) {
			holidayDto.setListDisable(true);
		}
		result.setDto(holidayDto);	
		return result;
	}
	
	public Flux<SelectDTO> getDefaultIntializationyear(String userId,String empId) throws Exception {
		if(Utils.isNullOrEmpty(empId) || empId.equals("0") ) {
			empId = String.valueOf(commonApiTransaction1.getEmployeesByUserId(userId));
		}
		EmpLeaveEntryDTO dto = new EmpLeaveEntryDTO();
		dto.setEmpId(empId);
		int year = getYearOnInitilizeMonthofLeaveType(dto);
		SelectDTO yearDto = new SelectDTO();
		if(!Utils.isNullOrEmpty(year)) {
			yearDto.setValue(String.valueOf(year));
			yearDto.setLabel(String.valueOf(year));
		}
		return Flux.just(yearDto);
	}
	
	public void updateEmpLeaveAllocation(String userId,EmpLeaveEntryDTO dto, EmpLeaveEntryDBO dbo) throws Exception {
		int year = getYearOnInitilizeMonthofLeaveType(dto);
		EmpLeaveAllocationDBO list1 = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(dto, year);
	    if (list1 != null) {
	        BigDecimal totalDays = list1.leavesPending ;
	        list1.leavesPending = totalDays.subtract(dbo.totalDays);
	        list1.modifiedUsersId = Integer.parseInt(userId);
	        if (!Utils.isNullOrEmpty(list1.id)) {
	            leaveApplicationTransaction.update(list1);
	        }
	    }
	}
	public Mono<EmpLeaveEntryDTO> getEmployeeDetails(String empId, String userId) {
		Tuple data = leaveApplicationTransaction.getEmployeeDetails(empId, userId);	
		return this.convertDBOToDTO(data);
	}
	
	public Mono<EmpLeaveEntryDTO> convertDBOToDTO (Tuple empDetails ){
		EmpLeaveEntryDTO mappingInfo = null;
		if (!Utils.isNullOrEmpty(empDetails)) {
			mappingInfo = new EmpLeaveEntryDTO();
				mappingInfo.employeeId = empDetails.get("id").toString();
				mappingInfo.empNo = empDetails.get("empNo").toString();
				mappingInfo.name = empDetails.get("name").toString();
				mappingInfo.department =  (String) empDetails.get("department");
				mappingInfo.designation = (String) empDetails.get("designation");
				mappingInfo.campus =  (String) empDetails.get("campus");
				mappingInfo.sundayWorking =  Boolean.parseBoolean(!Utils.isNullOrEmpty(empDetails.get("isSundayWorking")) ? empDetails.get("isSundayWorking").toString():"0");
				mappingInfo.holidayWorking =   Boolean.parseBoolean(!Utils.isNullOrEmpty(empDetails.get("isHolidayWorking")) ? empDetails.get("isHolidayWorking").toString():"0");
		}
		return Mono.just(mappingInfo);
	}
	
}