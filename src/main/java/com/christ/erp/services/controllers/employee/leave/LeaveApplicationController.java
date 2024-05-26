package com.christ.erp.services.controllers.employee.leave;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbqueries.employee.LeaveQueries;
import com.christ.erp.services.dto.common.AttendanceCumulativeDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.EmpHolidayDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveTypeDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.leave.LeaveApplicationHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveApplicationTransaction;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave/leaveApplication")
public class LeaveApplicationController {
	
	@Autowired
 	LeaveApplicationTransaction leaveApplicationTransaction;
	
	@Autowired
	CommonApiTransaction commonApiTransaction1;
	
	@Autowired
	LeaveApplicationHandler leaveApplicationHandler;
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();
	
	@RequestMapping(value = "/getLeaveSummary", method = RequestMethod.POST)
	public Mono<ApiResult<List<AttendanceCumulativeDTO>>> getLeaveSummary(
			 @RequestBody EmpLeaveEntryDTO data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestParam Map<String, String> requestParams) {
		ApiResult<List<AttendanceCumulativeDTO>> result = new ApiResult<List<AttendanceCumulativeDTO>>();
		try {
			int year = 0;
			int empId = 0;
			if(!Utils.isNullOrEmpty(requestParams.get("year"))){
				year = Integer.parseInt(requestParams.get("year").toString());
				data.setLeaveInitializationYear(new SelectDTO());
				data.getLeaveInitializationYear().setValue(String.valueOf(year));
			}
			if(!Utils.isNullOrEmpty(data.getEmployeeId()) && !Utils.isNullOrEmpty(data.getEmployeeId())) {
				empId = Integer.parseInt(data.getEmployeeId());
			} else {
				empId = commonApiTransaction.getEmployeesByUserId(userId);
				data.empId = String.valueOf(empId);
			}
			if (!Utils.isNullOrEmpty(data.getLeaveInitializationYear())
					&& !Utils.isNullOrEmpty(data.getLeaveInitializationYear().getValue()) && (!data.getLeaveInitializationYear().getValue().equalsIgnoreCase("0")))
				year = Integer.parseInt(data.getLeaveInitializationYear().getValue());
			else
				year = leaveApplicationHandler.getYearOnInitilizeMonthofLeaveType(data);
			List<Tuple> mappings = leaveApplicationTransaction.getLeaveDetails(empId, year);
			if (mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for (Tuple mapping : mappings) {
					AttendanceCumulativeDTO mappingInfo = new AttendanceCumulativeDTO();
					if (!Utils.isNullOrEmpty(mapping.get("allocatedLeaves")))
						mappingInfo.allocatedLeaves = new BigDecimal(mapping.get("allocatedLeaves").toString())
								.toString();
					else
						mappingInfo.allocatedLeaves = "0.0";
					if (!Utils.isNullOrEmpty(mapping.get("sanctionedLeaves")))
						mappingInfo.sanctionedLeaves = new BigDecimal(mapping.get("sanctionedLeaves").toString())
								.toString();
					else
						mappingInfo.sanctionedLeaves = "0.0";
					if (!Utils.isNullOrEmpty(mapping.get("LeaveRemaining"))) {
						BigDecimal number = new BigDecimal(mapping.get("LeaveRemaining").toString());
						// float rounded = number.setScale(2, RoundingMode.DOWN).floatValue();
						mappingInfo.balanceLeave = new BigDecimal(mapping.get("LeaveRemaining").toString()).toString();
					} else {
						mappingInfo.balanceLeave = "0.0";
					}
					if (!Utils.isNullOrEmpty(mapping.get("pendingLeave")))
						mappingInfo.pendingLeaves = new BigDecimal(mapping.get("pendingLeave").toString()).toString();
					else
						mappingInfo.pendingLeaves = "0.0";

					mappingInfo.leaveTypeName = mapping.get("LeaveTypeName").toString();
					mappingInfo.leaveTypeCode = (String) mapping.get("LeaveTypeCode");
					mappingInfo.colorCode = (String) mapping.get("LeaveTypeColorCodeHexvalue");

					result.dto.add(mappingInfo);
				}
			}else {
				result.failureMessage = "Leave not initialized for the selected year";
			}
			if (!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Mono.justOrEmpty(result);
	}

	@SuppressWarnings({ "rawtypes"})
	@RequestMapping(value = "/saveOrUpdateOnline", method = RequestMethod.POST)
	public Mono<ApiResult> saveOrUpdateOnline(@RequestBody EmpLeaveEntryDTO data,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			String error1 = null;
			boolean recordAlreadyExist = false;
		    Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
			data.employeeId=String.valueOf(empId);
			int year = leaveApplicationHandler.getYearOnInitilizeMonthofLeaveType(data);
			String errorByLeaveTypeConfig = leaveApplicationHandler.validateByLeaveType(data,year);
			String errorByAppliedLeave = leaveApplicationHandler.validateApplyLeave(data,year);
			if (Utils.isNullOrEmpty(errorByLeaveTypeConfig)) {
				if (Utils.isNullOrEmpty(errorByAppliedLeave)) {
					if (Utils.isNullOrEmpty(data.id)) {
						recordAlreadyExist = leaveApplicationHandler.leaveEntryDuplicateCheck(data);
					} else {
						recordAlreadyExist = leaveApplicationHandler.leaveEntryDuplicateCheckForUpdate(data);
					}
					if (!recordAlreadyExist) {
						Tuple list = leaveApplicationTransaction.getDataOnLeaveType(data.leaveTypecategory.id);
						EmpLeaveTypeDBO leaveType = leaveApplicationTransaction.getDataOnLeaveType1(data.leaveTypecategory.id);
						if( !leaveType.isExemption) {//leaveType.getIsApplyOnline() &&
					//	if ((boolean) list.get("isLeave") && data.isExempted.equalsIgnoreCase("false")) 
							EmpLeaveAllocationDBO list1 = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(data, year);
							
							if (!Utils.isNullOrEmpty(list1)) {
								boolean leavesAvailable = leaveApplicationHandler.checkLeavesAvailableForLeaveType(data,year, list1);

								if (leavesAvailable || (!leavesAvailable && !leaveType.getIsLeave())) {
									error1 = leaveApplicationHandler.validateMultiMonthLeaveApply(data);
									if (Utils.isNullOrEmpty(error1)) {
										if (leaveApplicationHandler.saveOrUpdate(data, userId, (boolean)list.get("isLeave"), year,leaveType)) { 
											result.success = true;
										} else {
											result.success = false;
										}
									} else {
										result.failureMessage = error1;
									}
								} else {
									result.failureMessage = "Leaves Remaining are less than Leaves applied for selected Leave Type : "
											+ data.leaveTypecategory.tag;
								}
							}else {
								result.failureMessage = " Leave Initilize is not Done.";
							}
							
						} else {
							if (leaveApplicationHandler.saveOrUpdate(data, userId, (boolean)list.get("isLeave"), year,leaveType)) { 
								result.success = true;
							} else {
								result.success = false;
							}
						}
					} else {
						result.failureMessage = "Leave already applied between selected date range.";
					}
				} else {
					result.failureMessage = errorByAppliedLeave;
				}
			} else {
				result.failureMessage = errorByLeaveTypeConfig;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Mono.justOrEmpty(result);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/saveOrUpdateOffline", method = RequestMethod.POST)
	public Mono<ApiResult> saveOrUpdateOffline(@RequestBody EmpLeaveEntryDTO data,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			boolean recordAlreadyExist = false;
			int year = leaveApplicationHandler.getYearOnInitilizeMonthofLeaveType(data);
			String errorByLeaveTypeConfig = leaveApplicationHandler.validateByLeaveType(data, year);
			String errorByAppliedLeave = leaveApplicationHandler.validateApplyLeave(data, year);
			if (Utils.isNullOrEmpty(errorByLeaveTypeConfig)) {
				if (Utils.isNullOrEmpty(errorByAppliedLeave)) {
					if (Utils.isNullOrEmpty(data.id)) {
						recordAlreadyExist = leaveApplicationHandler.leaveEntryDuplicateCheck(data);
					} else {
						recordAlreadyExist = leaveApplicationHandler.leaveEntryDuplicateCheckForUpdate(data);
					}
					if (!recordAlreadyExist) {
						Tuple list = leaveApplicationTransaction.getDataOnLeaveType(data.leaveTypecategory.id);
						if ((boolean) list.get("isLeave") && data.isExempted.equalsIgnoreCase("false")) {
							EmpLeaveAllocationDBO list1 = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(data, year);

//						if (Utils.isNullOrEmpty(list1))
//							list1 = leaveApplicationHandler.initilizeEmpLeaveAllocation(data, userId, year);
							if (!Utils.isNullOrEmpty(list1)) {
								boolean leavesAvailable = leaveApplicationHandler.checkLeavesAvailableForLeaveType(data,
										year, list1);
								if (leavesAvailable) {
									if (leaveApplicationHandler.saveOrUpdate(data, userId,(boolean) list.get("isLeave"), year,null)) {
										result.success = true;
									} else {
										result.success = false;
									}
								} else {
									result.failureMessage = "Leaves Remaining are less than Leaves applied for selected Leave Type : "
											+ data.leaveTypecategory.tag;
								}

							} else {
								result.failureMessage = " Leave Initilize is not Done.";
							}

						} else {
							if (leaveApplicationHandler.saveOrUpdate(data, userId, (boolean) list.get("isLeave"),year,null)) {
								result.success = true;
							} else {
								result.success = false;
							}
						}
					} else {
						result.failureMessage = "Leave already applied between selected date range.";
					}
				} else {
					result.failureMessage = errorByAppliedLeave;
				}
			} else {
				result.failureMessage = errorByLeaveTypeConfig;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Mono.justOrEmpty(result);
	}

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLeaveEntryDTO>>> getGridData(@RequestParam String leaveApplication,@RequestParam String leaveInitializationYear,@RequestParam String empID,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<List<EmpLeaveEntryDTO>> result = new ApiResult<List<EmpLeaveEntryDTO>>();
		try {
			EmpLeaveEntryDTO dto = new EmpLeaveEntryDTO();
			Integer year = 0;
			if(leaveApplication.equalsIgnoreCase("Online")) {
				dto.setEmpId(String.valueOf(commonApiTransaction.getEmployeesByUserId(userId)));
			}else {
				dto.setEmpId(empID);
			}
			if(Utils.isNullOrEmpty(leaveInitializationYear) || leaveInitializationYear.equals("0")) {
				 year = leaveApplicationHandler.getYearOnInitilizeMonthofLeaveType(dto);
			}else {
				year = Integer.parseInt(leaveInitializationYear);
			}
			result = leaveApplicationHandler.getGridData(userId,year.toString(),leaveApplication, empID);
			
			if (!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} else {
				result.success = false;
				result.failureMessage = "Data not found";
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Mono.justOrEmpty(result);
	}

	@RequestMapping(value = "/editLeaveApplication", method = RequestMethod.POST)
	public Mono<ApiResult<EmpLeaveEntryDTO>> editLeaveApplication(@RequestParam("ID") String id) {
		ApiResult<EmpLeaveEntryDTO> result = new ApiResult<EmpLeaveEntryDTO>();
		result = leaveApplicationHandler.editLeaveApplication(id);
		return Mono.justOrEmpty(result);
	}

	@RequestMapping(value = "/deleteLeaveApplication", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> deleteLeaveApplication(@RequestBody EmpLeaveEntryDTO data,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if (data != null) {
			try {
				int year = leaveApplicationHandler.getYearOnInitilizeMonthofLeaveType(data);
				Tuple list = leaveApplicationTransaction.getDataOnLeaveType(data.leaveTypecategory.id);
				leaveApplicationHandler.resettingPreviousLeavesApplied(data, userId, (boolean) list.get("isLeave"),	year);
				DBGateway.runJPA(new ITransactional() {
					@Override
					public void onRun(EntityManager context) {
						EmpLeaveEntryDBO header = null;
						if (Utils.isNullOrWhitespace(String.valueOf(data.id)) == false) {
							header = context.find(EmpLeaveEntryDBO.class, (data.id));
						}
						if (header != null) {
							header.recordStatus = 'D';
							header.modifiedUsersId = Integer.parseInt(userId);
							if (String.valueOf(header.id) != null) {
								context.merge(header);
							}
							result.success = true;
							result.dto = new ModelBaseDTO();
							result.dto.id = String.valueOf(header.id);
						}
					}

					@Override
					public void onError(Exception error) {
						result.success = false;
						result.dto = null;
						result.failureMessage = error.getMessage();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				Utils.log(e.getMessage());
			}
		}
		return Mono.justOrEmpty(result);
	}

//	private int getYearOnInitilizeMonthofLeaveType(EmpLeaveEntryDTO data) throws Exception {
//		int year = 0;
//		int month = 0;
//		Tuple initilizeMonth = leaveApplicationTransaction.getInitilizeMonthForLeaveType(data);
//		month = Integer.parseInt(initilizeMonth.get("month").toString());
//		String[] date = {};
//		if(data.startDate.toString().contains("Z"))
//			date = (Utils.convertLocalDateToStringDate(Utils.convertStringDateTimeToLocalDate(data.startDate.toString()))).split("/");
//		else
//			date = (Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(data.startDate.toString()))).split("/");
//		if (Integer.parseInt(date[1]) == month || Integer.parseInt(date[1]) > month) {
//			return year = Integer.parseInt(date[2]);
//		} else {
//			return year = Integer.parseInt(date[2]) - 1;
//		}
//	}

//	@SuppressWarnings("rawtypes")
//	@PostMapping("/uploadFiles")
//	public Mono<ApiResult> uploadFiles(@RequestPart("files") Flux<FilePart> data) {
//		File directory = new File("D:\\LeaveApplication");
//		if(!directory.exists()) {
//			directory.mkdir();
//		}
//		return Utils.uploadFiles(data, directory+"//",
//				new String[] { "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword","application/pdf", "text/plain", "png", "jpg" });
//	}
	@SuppressWarnings("rawtypes")
	@PostMapping("/uploadFiles")
    public Mono<ApiResult> uploadFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("ImageUpload");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory+"//",new String[]{"jpg","png","jpeg","pdf"});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/getTotalLeaves", method = RequestMethod.POST)
	public Mono<ApiResult> getTotalLeaves(@RequestBody EmpLeaveEntryDTO data,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			if (data.leaveApplication.equalsIgnoreCase("online")) {
				Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
				data.employeeId = String.valueOf(empId);
			}
			int year = leaveApplicationHandler.getYearOnInitilizeMonthofLeaveType(data);
			Tuple list = leaveApplicationTransaction.getDataOnLeaveType(data.leaveTypecategory.id);
			EmpLeaveEntryDTO mappingInfo = leaveApplicationHandler.getTotalLeaves(data, userId,(boolean) list.get("isLeave"), year);
			if (!Utils.isNullOrEmpty(mappingInfo)) {
				result.success = true;
				result.dto = mappingInfo;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getDetailsBasedOnLeaveType1", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLeaveEntryDTO>>> getDetailsBasedOnLeaveType(@RequestParam("value") String value) {
		ApiResult<List<EmpLeaveEntryDTO>> result = new ApiResult<List<EmpLeaveEntryDTO>>();
		try {
			Tuple mapping = leaveApplicationTransaction.getDataOnLeaveType(value);
			if (mapping != null) {
				result.success = true;
				result.dto = new ArrayList<>();
				EmpLeaveEntryDTO mappingInfo = new EmpLeaveEntryDTO();
				mappingInfo.isLeaveTypeDocumentRequired = (boolean) mapping.get("isLeaveTypeDocumentRequired");
				mappingInfo.leavePolicy = (String) mapping.get("leavePolicy");
				result.dto.add(mappingInfo);
			}
			if (!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/withdrawLeaveApplication", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> withdrawLeaveApplication(@RequestBody EmpLeaveEntryDTO data,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if (data != null) {
			DBGateway.runJPA(new ITransactional() {
				@Override
				public void onRun(EntityManager context) {
					EmpLeaveEntryDBO header = null;
					if (Utils.isNullOrWhitespace(String.valueOf(data.id)) == false) {
						header = context.find(EmpLeaveEntryDBO.class, (data.id));
					}
					if (header != null) {
						Tuple erpWorkFlowProcessOffline;
						try {
							erpWorkFlowProcessOffline = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode("LEAVE_APPLICATION_WITHDRAW");
							ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO = commonApiTransaction.getErpWorkFlowProcessNotificationsByWorkFlowProcessId(
											header.erpApplicantWorkFlowProcessDBO.id, "LEAVE_APPLICATION_SUBMISSION");
							Tuple approversDeatils = commonApiTransaction1.getApproversIdByEmployeeId(Integer.parseInt(data.employeeId));

							if (erpWorkFlowProcessOffline != null) {
								if (!Utils.isNullOrWhitespace(erpWorkFlowProcessOffline.get("applicant_status_display_text").toString())) {
									ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
									e.id = (Integer) erpWorkFlowProcessOffline.get("erp_work_flow_process_id");
									header.erpApplicantWorkFlowProcessDBO = e;
								}
								if (!Utils.isNullOrWhitespace(erpWorkFlowProcessOffline.get("application_status_display_text").toString())) {
									ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
									e.id = (Integer) erpWorkFlowProcessOffline.get("erp_work_flow_process_id");
									header.erpApplicationWorkFlowProcessDBO = e;
								}
							}
							header.recordStatus = 'D';
							header.modifiedUsersId = Integer.parseInt(userId);
							header.isPending = false;
							if (String.valueOf(header.id) != null) {
								context.merge(header);
							}
							leaveApplicationHandler.updateEmpLeaveAllocation(userId, data,header);
							ErpNotificationsDBO erpNotificationsDBO = null;
							if (!Utils.isNullOrEmpty(approversDeatils) &&  !Utils.isNullOrWhitespace(approversDeatils.toString()) && erpWorkFlowProcessNotificationsDBO!=null) {
								erpNotificationsDBO = leaveApplicationHandler.removeNotifications(erpWorkFlowProcessNotificationsDBO.id,
										Integer.parseInt(approversDeatils.get("usersId").toString()), header.id);
								erpNotificationsDBO.recordStatus = 'D';
								erpNotificationsDBO.modifiedUsersId = Integer.parseInt(userId);
								if (String.valueOf(erpNotificationsDBO.id) != null) {
									context.merge(erpNotificationsDBO);
								}
							}
							result.success = true;
							result.dto = new ModelBaseDTO();
							result.dto.id = String.valueOf(header.id);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}

				@Override
				public void onError(Exception error) {
					result.success = false;
					result.dto = null;
					result.failureMessage = error.getMessage();
				}
			});
		}
		return Mono.justOrEmpty(result);
	}

	@RequestMapping(value = "/getDetailsBasedOnLeaveType", method = RequestMethod.POST)
	public Mono<ApiResult<EmpLeaveTypeDTO>> getDetailsBasedOnLeaveTypeNew(@RequestParam("value") String value) {
		 return leaveApplicationHandler.getEmpLeaveType(value).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@RequestMapping(value = "/getEmployeeDetails1", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLeaveEntryDTO>>> getEmployeeDetails1(@RequestParam("value") String value) {
		ApiResult<List<EmpLeaveEntryDTO>> result = new ApiResult<List<EmpLeaveEntryDTO>>();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				Query query =null;
				if(value.matches("-?\\d+")) {
				 query = context.createNativeQuery(LeaveQueries.LEAVE_EMPLOYEE_DETAILS,Tuple.class);
				query.setParameter("empId", value);
				}else {
					 query = context.createNativeQuery(LeaveQueries.LEAVE_EMPLOYEE_DETAILS1,Tuple.class);
					query.setParameter("empName", value);
				}
				List<Tuple> mappings = query.getResultList();
				if (mappings != null && mappings.size() > 0) {
					result.success = true;
					result.dto = new ArrayList<>();
					for (Tuple mapping : mappings) {
						EmpLeaveEntryDTO mappingInfo = new EmpLeaveEntryDTO();
						mappingInfo.employeeId = mapping.get("empNo").toString();
						mappingInfo.name = mapping.get("name").toString();
						mappingInfo.department =  (String) mapping.get("department");
						mappingInfo.designation = (String) mapping.get("designation");
						mappingInfo.campus =  (String) mapping.get("campus");
						result.dto.add(mappingInfo);
					}
				}
				result.success = true;
			}
			@Override
			public void onError(Exception error) {
				result.success = false;
				result.dto = null;
				result.failureMessage = error.getMessage();
			}
		}, true);
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getEmployeeDetails", method = RequestMethod.POST)
	public Mono<EmpLeaveEntryDTO> getEmployeeDetails(@RequestParam("value") String value,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return leaveApplicationHandler.getEmployeeDetails(value, userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@RequestMapping(value = "/getHolidayList", method = RequestMethod.POST)
	public Mono<ApiResult<EmpHolidayDTO>> getHolidayList(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,
			String empId, @RequestParam  String leaveType) {
		ApiResult<EmpHolidayDTO> result = new ApiResult<EmpHolidayDTO>();
		try {
			if(Utils.isNullOrEmpty(empId) ) {
				int employeeId = commonApiTransaction.getEmployeesByUserId(userId);
				empId = String.valueOf(employeeId);
			}
			result = leaveApplicationHandler.getHolidayList(empId,leaveType,result);
			
			if (!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} else {
				result.success = false;
				result.failureMessage = "Data not found";
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Mono.justOrEmpty(result);
	}
	
	@RequestMapping(value = "/getDefaultIntializationyear", method = RequestMethod.POST)
    public Flux<SelectDTO> getDefaultIntializationyear(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestParam String empId) throws Exception {
        return leaveApplicationHandler.getDefaultIntializationyear(userId, empId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }

	
	
}