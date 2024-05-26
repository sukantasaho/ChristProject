package com.christ.erp.services.handlers.employee.leave;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDetailsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.leave.BulkLeaveUploadTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveApplicationTransaction;

import reactor.core.publisher.Mono;

@Service
public class BulkLeaveUploadHandler {

	@Autowired
	BulkLeaveUploadTransaction bulkLeaveUploadTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

	@Autowired
	private LeaveApplicationHandler leaveApplicationHandler;

	@Autowired
	private LeaveApplicationTransaction leaveApplicationTransaction;

	public Mono<ApiResult> bulkLeaveUpload(Mono<EmpApplnAdvertisementImagesDTO> data, MultipartFile file, String userId) {
		Map<Integer, List<String>> map1 = new HashMap<>();
		List<Integer> empIdList = new ArrayList<Integer>();
		List<Tuple> empList = bulkLeaveUploadTransaction.getEmployeesList();
		Map<Integer, Integer> fingerPrintIdMap = new LinkedHashMap<Integer, Integer>();
		for (Tuple emp : empList) {
			if (!Utils.isNullOrEmpty(emp.get("fingerPrintId")) && !Utils.isNullOrEmpty(emp.get("empId"))
					&& !Utils.isNullOrEmpty(emp.get("fingerPrintId").toString())
					&& !Utils.isNullOrEmpty(emp.get("empId").toString()))
				fingerPrintIdMap.put(Integer.parseInt(emp.get("fingerPrintId").toString()),Integer.parseInt(emp.get("empId").toString()));
		}
		List<Tuple> leaveTypeList = bulkLeaveUploadTransaction.getLeaveTypeList();
		Map<String, Integer> leaveTypeMap = new LinkedHashMap<String, Integer>();
		Map<String, String> canLeaveAllotMap = new LinkedHashMap<String, String>();
		for (Tuple leaveType : leaveTypeList) {
			leaveTypeMap.put(leaveType.get("leaveTypeName").toString(),Integer.parseInt(leaveType.get("leaveTypeId").toString()));
			canLeaveAllotMap.put(leaveType.get("leaveTypeName").toString(),  leaveType.get("canAllotLeave").toString());
		}

		return data.handle((data1, synchronousSink) -> {
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(file.getInputStream());
			//	workbook = new XSSFWorkbook("D://BulkLeaveUploadExcel//" + data1.fileName + "." + data1.extension);
//				 File file =new File("D://BulkLeaveUploadExcel//" + data1.fileName + "." + data1.extension);
//				 file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			XSSFSheet sheet = workbook.getSheetAt(0);
			if (!Utils.isNullOrEmpty(sheet.getRow(0))) {
				int rowLength = sheet.getRow(0).getLastCellNum();
				Integer p = 1;
				for (Row row : sheet) {
					if (p != 1) {
						map1.put(p, new ArrayList<String>());
						for (int cn = 0; cn < rowLength; cn++) {
							Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
							if (cell == null) {
								String cellValue = null;
								map1.get(p).add(cellValue);
							} else if (cell.getCellType() == CellType.NUMERIC) {
								if (DateUtil.isCellDateFormatted(cell)) {
									String[] s = cell.getLocalDateTimeCellValue().toLocalTime().toString().split(":");
									if (Integer.parseInt(s[0]) != 00) {
										map1.get(p).add(cell.getLocalDateTimeCellValue().toLocalTime() + "");
									} else {
										map1.get(p).add(cell.getLocalDateTimeCellValue() + "");
									}
								} else {
									map1.get(p).add(String.valueOf((int) cell.getNumericCellValue()));
								}
							} else {
								String cellValue = cell.toString();
								map1.get(p).add(cellValue.trim());
							}
						}
					}
					p++;
				}
			}
			List<String> fingerPrintIdEmpty = new ArrayList<String>();
			List<String> dataEmptyforFingerPrintId = new ArrayList<String>();
			List<String> invalidFingerPrintIds = new ArrayList<String>();
			List<String> invalidLeaveType = new ArrayList<String>();
			List<String> invalidDates = new ArrayList<String>();
			List<String> endDateGreaterStartDate = new ArrayList<String>();
			List<String> invalidSessions = new ArrayList<String>();
			List<String> duplicateData = new ArrayList<String>();
			List<String> dataAlreadyExists = new ArrayList<String>();
			Map<Integer,String> fingerPritnIdData=new LinkedHashMap<Integer,String>();

			map1.forEach((k, v) -> {
				EmpLeaveEntryDTO dto = new EmpLeaveEntryDTO();
				if (Utils.isNullOrEmpty(v.get(0))) {
					fingerPrintIdEmpty.add(k.toString());
				} else if (!Utils.isNullOrEmpty(v.get(0)) && (Utils.isNullOrEmpty(v.get(1)) || Utils.isNullOrEmpty(v.get(2))
								|| Utils.isNullOrEmpty(v.get(3)) || Utils.isNullOrEmpty(v.get(4))) || Utils.isNullOrEmpty(v.get(5))) {
					dataEmptyforFingerPrintId.add(v.get(0));
				}else if (!Utils.isNullOrEmpty(v.get(0)) && !fingerPrintIdMap.containsKey(Integer.parseInt(v.get(0)))) {
					invalidFingerPrintIds.add(v.get(0));
				}else if (!Utils.isNullOrEmpty(v.get(1)) && !leaveTypeMap.containsKey(v.get(1))) {
					invalidLeaveType.add(v.get(0));
				}
				
//				else if(!Utils.isNullOrEmpty(v.get(2)) &&  !Utils.isNullOrEmpty(v.get(4))) {
//					if( !isValidFormat("dd-MM-yyyy",Utils.convertLocalDateToStringDate2((Utils.convertStringDateTimeToLocalDate(v.get(2).toString())))) ||
//					!isValidFormat("dd-MM-yyyy", Utils.convertLocalDateToStringDate2(Utils.convertStringDateTimeToLocalDate(v.get(4).toString()))));
//					invalidDates.add(v.get(0));
//				}
				 else if (Utils.convertStringDateTimeToLocalDate(v.get(2).toString()).isAfter(Utils.convertStringDateTimeToLocalDate(v.get(4).toString()))) {
					endDateGreaterStartDate.add(v.get(0));
				} else if (!Utils.isNullOrEmpty(v.get(3)) && !v.get(3).equalsIgnoreCase("AN") && !v.get(3).equalsIgnoreCase("FN") && !v.get(3).equalsIgnoreCase("FD")) {
					invalidSessions.add(v.get(0));
				} else if (!Utils.isNullOrEmpty(v.get(5)) && !v.get(5).equalsIgnoreCase("AN") && !v.get(5).equalsIgnoreCase("FN") && !v.get(5).equalsIgnoreCase("FD")) {
					invalidSessions.add(v.get(0));
				}else if(fingerPritnIdData.containsKey(Integer.parseInt(v.get(0))) && (!Utils.isNullOrEmpty(v.get(0)) && !Utils.isNullOrEmpty(v.get(1)) && !Utils.isNullOrEmpty(v.get(2))  && !Utils.isNullOrEmpty(v.get(3))
						 && !Utils.isNullOrEmpty(v.get(4))  && !Utils.isNullOrEmpty(v.get(5)))) {
					String[] s=fingerPritnIdData.get(Integer.parseInt(v.get(0))).split("@");
					if(s[0].equalsIgnoreCase(v.get(1)) &&  (Utils.convertStringDateTimeToLocalDate(v.get(2)).isAfter(Utils.convertStringDateTimeToLocalDate(s[1])) && Utils.convertStringDateTimeToLocalDate(v.get(2)).isBefore(Utils.convertStringDateTimeToLocalDate(s[3]))) 
							|| Utils.convertStringDateTimeToLocalDate(v.get(2)).isEqual(Utils.convertStringDateTimeToLocalDate(s[1]))) {
						duplicateData.add(Utils.convertLocalDateToStringDate2(Utils.convertStringDateTimeToLocalDate(v.get(2)))+ " & " + Utils.convertLocalDateToStringDate2(Utils.convertStringDateTimeToLocalDate(v.get(4)))+ " For FingerprintId: "
								+ Integer.parseInt(v.get(0)));
					}
				} 
				
				
				if (!Utils.isNullOrEmpty(v.get(1)) && !Utils.isNullOrEmpty(v.get(2))
						&& !Utils.isNullOrEmpty(v.get(3)) && !Utils.isNullOrEmpty(v.get(4))
						&& !Utils.isNullOrEmpty(v.get(5))){
						dto.employeeId = String.valueOf(fingerPrintIdMap.get(Integer.parseInt(v.get(0))));
						dto.startDate = Utils.convertStringDateTimeToLocalDate(v.get(2));
						dto.endDate = Utils.convertStringDateTimeToLocalDate(v.get(4));
						dto.fromSession = new ExModelBaseDTO();
						if (v.get(3).equalsIgnoreCase("FN")) {
							dto.fromSession.id = "2";
							dto.fromSession.tag = "Forenoon";
						} else if (v.get(3).equalsIgnoreCase("AN")) {
							dto.fromSession.id = "3";
							dto.fromSession.tag = "Afternoon";
						} else {
							dto.fromSession.id = "1";
							dto.fromSession.tag = "Full Day";
						}

						dto.toSession = new ExModelBaseDTO();
						if (v.get(3).equalsIgnoreCase("FN")) {
							dto.toSession.id = "2";
							dto.toSession.tag = "Forenoon";
						} else if (v.get(3).equalsIgnoreCase("AN")) {
							dto.toSession.id = "3";
							dto.toSession.tag = "Afternoon";
						} else {
							dto.toSession.id = "1";
							dto.toSession.tag = "Full Day";
						}
						try {
							boolean recordAlreadyExist = leaveApplicationHandler.leaveEntryDuplicateCheck(dto);
							if (recordAlreadyExist)
								dataAlreadyExists.add(Utils.convertLocalDateToStringDate2(dto.startDate)   + " & " + Utils.convertLocalDateToStringDate2(dto.endDate) + " For FingerprintId: "
										+ Integer.parseInt(v.get(0)));
						} catch (Exception e) {
							e.printStackTrace();
						}
				}

				if (!Utils.isNullOrEmpty(v.get(0)) && !Utils.isNullOrEmpty(v.get(1)) && !Utils.isNullOrEmpty(v.get(2))  && !Utils.isNullOrEmpty(v.get(3))
						 && !Utils.isNullOrEmpty(v.get(4))  && !Utils.isNullOrEmpty(v.get(5))) {
					empIdList.add(fingerPrintIdMap.get(Integer.parseInt(v.get(0))));
					fingerPritnIdData.put(Integer.parseInt(v.get(0)), v.get(1)+"@"+v.get(2)+"@"+v.get(3)+"@"+v.get(4)+"@"+v.get(5));
				}
			});

			if (Utils.isNullOrEmpty(map1)) {
				synchronousSink.error(new GeneralException("Warning  Excel Sheet is Empty."));
			} else if (!Utils.isNullOrEmpty(fingerPrintIdEmpty)) {
				synchronousSink.error(new GeneralException("Warning  Finger Print Id is Empty."));
			}else if (!Utils.isNullOrEmpty(dataEmptyforFingerPrintId)) {
				synchronousSink.error(new GeneralException("Warning LeaveType or Start Date or End Date or Start Session or End Session is empty for Finger Print Id "
								+ dataEmptyforFingerPrintId));
			}else if (!Utils.isNullOrEmpty(invalidFingerPrintIds)) {
				synchronousSink.error(new GeneralException("Warning Invalid Finger Print Id "+ invalidFingerPrintIds));
			}else if (!Utils.isNullOrEmpty(invalidLeaveType)) {
				synchronousSink.error(new GeneralException("Warning Invalid Leave Type for Finger Print Id "+ invalidLeaveType));
			}else if (!Utils.isNullOrEmpty(invalidDates)) {
				synchronousSink.error(new GeneralException("Invalid Start Date and End Date formate for Finger Print Id " + invalidDates));
			} else if (!Utils.isNullOrEmpty(endDateGreaterStartDate)) {
				synchronousSink.error(new GeneralException("End Date is greather than Start Date for Finger Print Id " + endDateGreaterStartDate));
			} else if (!Utils.isNullOrEmpty(invalidSessions)) {
				synchronousSink.error(new GeneralException("Invalid Start and End Session for Finger Print Id " + invalidSessions));
			}else if (!Utils.isNullOrEmpty(duplicateData)) {
				synchronousSink.error(new GeneralException("Duplicate Data in Excel, for Same Date Range " + duplicateData));
			}else if (!Utils.isNullOrEmpty(dataAlreadyExists)) {
				synchronousSink.error(new GeneralException("Leave already applied between selected date range: " + dataAlreadyExists));
			}
			else {
				synchronousSink.next(map1);
			}
		}).map(data2 -> convertDtoToDbo(map1, userId, fingerPrintIdMap, empIdList,leaveTypeMap,canLeaveAllotMap)).flatMap(s -> {
			//bulkLeaveUploadTransaction.update(s);
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);

	}

	boolean isValidFormat(String format, String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return date != null;
	}

	List<Object> bulkUploadLeavedata =null;
	private List<Object> convertDtoToDbo(Map<Integer, List<String>> map1, String userId,
			Map<Integer, Integer> fingerPrintIdMap, List<Integer> empIdList,Map<String, Integer> leaveTypeMap,Map<String, String> canLeaveAllotMap ) {
		try {
		Tuple erpWorkFlowProcessOffline = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode("LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED");
		Map<String, EmpLeaveAllocationDBO> empLeaveAllocationDBOMap = bulkLeaveUploadTransaction.getEmployeeLeaveAllocationDetails();
		
//		 List<Tuple> empSundayandHolidayList = bulkLeaveUploadTransaction.getEmployeeSundayandHolidayList(empIdList);
//		 Map<Integer,String> empSundayandHolidayMap=new LinkedHashMap<Integer, String>();
//		 for (Tuple empSundayandHoliday : empSundayandHolidayList) {
//			 empSundayandHolidayMap.put(Integer.parseInt(empSundayandHoliday.get("empId").toString()),empSundayandHoliday.get("sundayWorking")+"-"+empSundayandHoliday.get("holidayWorking"));
//		 	}
		
		map1.forEach((k, v) -> {
			bulkUploadLeavedata = new ArrayList<Object>();
			List<EmpLeaveEntryDBO> empLeaveEntryDBOList = new ArrayList<EmpLeaveEntryDBO>();
			List<EmpLeaveAllocationDBO> empLeaveAllocationDBOList = new ArrayList<EmpLeaveAllocationDBO>();
			List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
			EmpLeaveEntryDTO dto = new EmpLeaveEntryDTO();
			EmpLeaveEntryDBO empLeaveEntryDBO = new EmpLeaveEntryDBO();
			EmpDBO emp = new EmpDBO();
			emp.id = fingerPrintIdMap.get(Integer.parseInt(v.get(0)));
			empLeaveEntryDBO.empID = emp;
			dto.employeeId = String.valueOf(fingerPrintIdMap.get(Integer.parseInt(v.get(0))));

			empLeaveEntryDBO.startDate = Utils.convertStringDateTimeToLocalDate(v.get(2)); // Utils.convertStringLocalDateTimeToLocalDateTime()
			dto.startDate = Utils.convertStringDateTimeToLocalDate(v.get(2));
			empLeaveEntryDBO.endDate = Utils.convertStringDateTimeToLocalDate(v.get(4));
			dto.endDate = Utils.convertStringDateTimeToLocalDate(v.get(4));
			empLeaveEntryDBO.reason = v.get(6);

			EmpLeaveTypeDBO empLeaveType = new EmpLeaveTypeDBO();
			empLeaveType.id = leaveTypeMap.get(v.get(1));
			empLeaveEntryDBO.leaveTypecategory = empLeaveType;
			dto.leaveTypecategory = new ExModelBaseDTO();
			dto.leaveTypecategory.id = String.valueOf(leaveTypeMap.get(v.get(1)));
			dto.leaveTypecategory.tag = v.get(1).toString();
			
			empLeaveEntryDBO.fromSession = v.get(3);
			dto.fromSession = new ExModelBaseDTO();
			if (v.get(3).equalsIgnoreCase("FN")) {
				dto.fromSession.id = "2";
				dto.fromSession.tag = "Forenoon";
			} else if (v.get(3).equalsIgnoreCase("AN")) {
				dto.fromSession.id = "3";
				dto.fromSession.tag = "Afternoon";
			} else {
				dto.fromSession.id = "1";
				dto.fromSession.tag = "Full Day";
			}

			empLeaveEntryDBO.toSession = v.get(5);
			dto.toSession = new ExModelBaseDTO();
			if (v.get(3).equalsIgnoreCase("FN")) {
				dto.toSession.id = "2";
				dto.toSession.tag = "Forenoon";
			} else if (v.get(3).equalsIgnoreCase("AN")) {
				dto.toSession.id = "3";
				dto.toSession.tag = "Afternoon";
			} else {
				dto.toSession.id = "1";
				dto.toSession.tag = "Full Day";
			}
			empLeaveEntryDBO.offline = true;
				
			try {
					empLeaveEntryDBO.totalDays = new BigDecimal(Float.toString(leaveApplicationHandler.calculateTotalLeavesAppliedDateDifferences(dto,
									Utils.convertLocalDateToStringDate(dto.startDate),Utils.convertLocalDateToStringDate(dto.endDate), dto.fromSession.tag,dto.toSession.tag)));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				if (erpWorkFlowProcessOffline != null) {
					if (!Utils.isNullOrEmpty(erpWorkFlowProcessOffline.get("applicant_status_display_text"))) {
						ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
						e.id = (Integer) erpWorkFlowProcessOffline.get("erp_work_flow_process_id");
						empLeaveEntryDBO.erpApplicantWorkFlowProcessDBO = e;
					}
					if (!Utils.isNullOrEmpty(erpWorkFlowProcessOffline.get("application_status_display_text"))) {
						ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
						e.id = (Integer) erpWorkFlowProcessOffline.get("erp_work_flow_process_id");
						empLeaveEntryDBO.erpApplicationWorkFlowProcessDBO = e;
					}
				}
			
			empLeaveEntryDBO.createdUsersId = Integer.parseInt(userId);
			empLeaveEntryDBO.recordStatus = 'A';
			empLeaveEntryDBO.schedulerStatus = "NA";

			// saving data to emp_leave_entry_details table
			Set<EmpLeaveEntryDetailsDBO> empLeaveEntryDetailsDBOList = new LinkedHashSet<EmpLeaveEntryDetailsDBO>();
			LocalDate startDate = empLeaveEntryDBO.startDate;
			int year = getYearofEmployee(dto);
			empLeaveEntryDBO.leaveYear=year;
			List<EmpHolidayEventsDBO> holidayList = leaveApplicationTransaction.getEmpHolidayEventsDBO(dto, year);
			Map<LocalDate, EmpHolidayEventsDBO> holidayListMap = new LinkedHashMap<LocalDate, EmpHolidayEventsDBO>();
			for (EmpHolidayEventsDBO empHolidayEventsDBO : holidayList) {
				holidayListMap.put(empHolidayEventsDBO.holidayEventsStartDate, empHolidayEventsDBO);
			}
			while (startDate.isBefore(empLeaveEntryDBO.endDate) || startDate.isEqual(empLeaveEntryDBO.endDate)) {
				boolean sundayorNot = Utils.checkISSunday(startDate);
			//	if ((dto.sundayWorkingDay && sundayorNot) || (dto.holidayWorkingDay && holidayListMap.containsKey(startDate)) ||
			//			(!sundayorNot && !holidayListMap.containsKey(startDate))) {
					EmpLeaveEntryDetailsDBO empLeaveEntryDetailsDBO = new EmpLeaveEntryDetailsDBO();
					empLeaveEntryDetailsDBO.setEmpId(empLeaveEntryDBO.empID);
					empLeaveEntryDetailsDBO.setEmpLeaveEntryId(empLeaveEntryDBO);
					empLeaveEntryDetailsDBO.setLeaveTypeId(empLeaveEntryDBO.leaveTypecategory);
					empLeaveEntryDetailsDBO.setDate(startDate);
					if (empLeaveEntryDBO.startDate.isEqual(startDate)) {
						empLeaveEntryDetailsDBO.setSession(empLeaveEntryDBO.fromSession);
						if (empLeaveEntryDBO.fromSession.equalsIgnoreCase("FN")) {
							empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
						} else if (empLeaveEntryDBO.fromSession.equalsIgnoreCase("AN")) {
							empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(0.5));
						} else {
							empLeaveEntryDBO.fromSession = "FD";
							empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
						}
					} else if (empLeaveEntryDBO.endDate.isEqual(startDate)) {
						empLeaveEntryDetailsDBO.setSession(empLeaveEntryDBO.toSession);
						if (empLeaveEntryDBO.toSession.equalsIgnoreCase("FN")) {
							empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(0.5));
						} else if (empLeaveEntryDBO.toSession.equalsIgnoreCase("AN")) {
							empLeaveEntryDetailsDBO.setTotalDays(new BigDecimal(1.0));
						} else {
							empLeaveEntryDBO.fromSession = "FD";
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
				startDate = startDate.plusDays(1);
			}
			empLeaveEntryDBO.setEmpLeaveEntryDetails(empLeaveEntryDetailsDBOList);
			
			boolean leaveAvailable=false;
			//System.out.println( (Boolean) canLeaveAllotMap.get( v.get(1).toString()));
			//EmpLeaveAllocationDBO list = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(dto, year);
			EmpLeaveAllocationDBO empLeaveAllocationDBO =  empLeaveAllocationDBOMap.get(dto.employeeId+"-"+dto.leaveTypecategory.id+"-"+year);
			System.out.println( empLeaveAllocationDBOMap.get(dto.employeeId+"-"+dto.leaveTypecategory.id+"-"+year));
			if (!Utils.isNullOrEmpty(empLeaveAllocationDBO) && canLeaveAllotMap.get( v.get(1).toString()).equalsIgnoreCase("1")
					&& empLeaveAllocationDBO.leavesRemaining.compareTo(empLeaveEntryDBO.totalDays) >= 0) {
				leaveAvailable = true;
				BigDecimal b = empLeaveEntryDBO.totalDays;
				if (empLeaveAllocationDBO.sanctionedLeaves == null)
					empLeaveAllocationDBO.sanctionedLeaves = b;
				else
					empLeaveAllocationDBO.sanctionedLeaves = empLeaveAllocationDBO.sanctionedLeaves.add(b);
				if (empLeaveAllocationDBO.leavesRemaining != null)
					empLeaveAllocationDBO.leavesRemaining = empLeaveAllocationDBO.leavesRemaining.subtract(b);
				empLeaveAllocationDBO.modifiedUsersId = Integer.parseInt(userId);
				empLeaveAllocationDBOList.add(empLeaveAllocationDBO);
				empLeaveEntryDBOList.add(empLeaveEntryDBO);
			} else if (Utils.isNullOrEmpty(empLeaveAllocationDBO) && !(canLeaveAllotMap.get( v.get(1).toString()).equalsIgnoreCase("1"))) {
				leaveAvailable = true;
				EmpLeaveAllocationDBO list1 = new EmpLeaveAllocationDBO();
				list1.empDBO.id = Integer.parseInt(dto.employeeId);
				list1.leaveType.id = Integer.parseInt(dto.leaveTypecategory.id);
				BigDecimal b = empLeaveEntryDBO.totalDays;
				if (list1.sanctionedLeaves == null)
					list1.sanctionedLeaves = b;
				else
					list1.sanctionedLeaves = list1.sanctionedLeaves.add(b);
				list1.createdUsersId = Integer.parseInt(userId);
				list1.recordStatus = 'A';
				list1.year = year;
				empLeaveAllocationDBOList.add(list1);
				empLeaveEntryDBOList.add(empLeaveEntryDBO);
			} else if (!Utils.isNullOrEmpty(empLeaveAllocationDBO) && !(canLeaveAllotMap.get( v.get(1).toString()).equalsIgnoreCase("1"))) {
				leaveAvailable = true;
				BigDecimal b = empLeaveEntryDBO.totalDays;
				if (empLeaveAllocationDBO.sanctionedLeaves == null)
					empLeaveAllocationDBO.sanctionedLeaves = b;
				else
					empLeaveAllocationDBO.sanctionedLeaves = empLeaveAllocationDBO.sanctionedLeaves.add(b);
				if (empLeaveAllocationDBO.leavesRemaining != null)
					empLeaveAllocationDBO.leavesRemaining = empLeaveAllocationDBO.leavesRemaining.subtract(b);
				empLeaveAllocationDBO.modifiedUsersId = Integer.parseInt(userId);
				empLeaveAllocationDBOList.add(empLeaveAllocationDBO);
				empLeaveEntryDBOList.add(empLeaveEntryDBO);
			}else if(!Utils.isNullOrEmpty(empLeaveAllocationDBO)  && canLeaveAllotMap.get( v.get(1).toString()).equalsIgnoreCase("1")  && !(empLeaveAllocationDBO.leavesRemaining.compareTo(empLeaveEntryDBO.totalDays) >= 0)) {
				leaveAvailable = false;
				System.out.println("Leave Balance is less, so the applied Leave not submitted for finger Print Id : "+Integer.parseInt(v.get(0)) + " for applied Date "  +Utils.convertLocalDateToStringDate2(dto.startDate)   + " to " + Utils.convertLocalDateToStringDate2(dto.endDate) );
			}
			
			if (leaveAvailable) {
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
				statusLogList.add(erpWorkFlowProcessStatusLogDBO);
			}
			
			if (leaveAvailable) {
				bulkUploadLeavedata.addAll(empLeaveEntryDBOList);
				bulkUploadLeavedata.addAll(empLeaveAllocationDBOList);
				bulkUploadLeavedata.addAll(statusLogList);
				bulkLeaveUploadTransaction.update(bulkUploadLeavedata);
			}
			
		});
		
//		data.addAll(empLeaveEntryDBOList);
//		data.addAll(empLeaveAllocationDBOList);
//		data.addAll(statusLogList);
		
		return bulkUploadLeavedata;
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private int getYearofEmployee(EmpLeaveEntryDTO dto) {
		int year = 0;
		int month = 0;
		int empId = dto.getEmployeeId() != null ? Integer.parseInt(dto.getEmployeeId())
				: Integer.parseInt(dto.getEmpId());
		Tuple initilizeMonth = leaveApplicationTransaction.getInitilizeMonthForEmployee(empId);
		if (!Utils.isNullOrEmpty(initilizeMonth)) {
			if (!Utils.isNullOrEmpty(initilizeMonth.get("month"))) {
				month = Integer.parseInt(initilizeMonth.get("month").toString());
			}
		}
		String[] date = {};

		String refDate = "";
		if (!Utils.isNullOrEmpty(dto.startDate)) {
			refDate = Utils.convertLocalDateToStringDate2(dto.startDate);
		} else {
			LocalDate now = LocalDate.now();
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

	public Mono<FileUploadDownloadDTO> bulkLeaveUploadDownloadFormat() {
		Tuple tuple = bulkLeaveUploadTransaction.bulkLeaveUploadDownloadFormat();
		var apiResult = new FileUploadDownloadDTO();
		apiResult.setActualPath(tuple.get("fileNameUnique").toString());
		apiResult.setProcessCode(tuple.get("uploadProcessCode").toString());
		apiResult.setOriginalFileName(tuple.get("fileNameOriginal").toString());
		return Mono.just(apiResult);
	}

	
//	Map<String, Integer> start = allDatas.stream().collect(Collectors.toMap(s -> (s.getErpProgrammeDBO().getProgrammeCode()+s.getErpCampusDBO().getShortName()),
//    s -> !Utils.isNullOrEmpty(s.getProgrammeCommenceYear()) ? s.getProgrammeCommenceYear(): 0));		
//<String, Integer> end = allDatas.stream().collect(Collectors.toMap(s -> (s.getErpProgrammeDBO().getProgrammeCode()+s.getErpCampusDBO().getShortName()),
//  s -> !Utils.isNullOrEmpty(s.getProgrammeInactivatedYear())  ? s.getProgrammeInactivatedYear() : 0));
//
	
}
