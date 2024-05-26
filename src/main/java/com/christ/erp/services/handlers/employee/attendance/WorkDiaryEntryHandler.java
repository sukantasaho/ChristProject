package com.christ.erp.services.handlers.employee.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryActivityDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryEntriesDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryEntriesDetailsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryEntriesDTO;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryEntriesDetailsDTO;
import com.christ.erp.services.dto.employee.attendance.EmpAttendanceDTO;
import com.christ.erp.services.dto.employee.common.EmpDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.attendance.ViewEmployeeAttendanceTransaction;
import com.christ.erp.services.transactions.employee.attendance.WorkDiaryEntryTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WorkDiaryEntryHandler {
	@Autowired
	private CommonApiTransaction commonApiTransaction;

	@Autowired
	WorkDiaryEntryTransaction workDiaryEntryTransaction;

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<EmpWorkDiaryEntriesDTO> dto, String userId) {
		return dto
				.handle((empWorkDiaryEntriesDTO, synchronousSink) -> 
				{
					List<String> values = new ArrayList<String>();	 
					boolean result = false;
					if(!Utils.isNullOrEmpty(empWorkDiaryEntriesDTO.getId())) {
						empWorkDiaryEntriesDTO.getEmpWorkDiaryEntriesDetails().forEach(dtos-> {
							if(!Utils.isNullOrEmpty(dtos.getFromTime()) && !Utils.isNullOrEmpty(dtos.getToTime())) {
								LocalTime fromTime = Utils.convertStringTimeToLocalTime(dtos.getFromTime());
								LocalTime toTime = Utils.convertStringTimeToLocalTime(dtos.getToTime());	
								List<EmpWorkDiaryEntriesDBO> dbo = workDiaryEntryTransaction.isTimeRangeExists(empWorkDiaryEntriesDTO);
								dbo.forEach(detailsDBO-> {
									detailsDBO.getEmpWorkDiaryEntriesDetailsDBOSet().forEach(dbos -> {
										if(Utils.isNullOrEmpty(dtos.getId())) { 
											if(!Utils.isNullOrEmpty(dbos.getFromTime()) && !Utils.isNullOrEmpty(dbos.getToTime())) {
												if(dbos.getFromTime().equals(fromTime) || (dbos.getFromTime().isAfter(fromTime) && dbos.getToTime().isBefore(fromTime)) || dbos.getToTime().equals(toTime) || (dbos.getFromTime().isAfter(toTime) && dbos.getToTime().isBefore(toTime))) {
													values.add("Data already exist for given time range");
												} 
											}
										}
									});   

								});	
							}
						});
					} else {
						result = workDiaryEntryTransaction.isDateExists(empWorkDiaryEntriesDTO); 
					}
					if(result) {
						synchronousSink.error(new DuplicateException("Date Already Exists"));	
					}
					else if(!Utils.isNullOrEmpty(values) && !Utils.isNullOrEmpty(empWorkDiaryEntriesDTO.getId())) {
						synchronousSink.error(new DuplicateException("Data already exist for given time range"));
					} else {
						synchronousSink.next(empWorkDiaryEntriesDTO); 
					} 

				}).cast(EmpWorkDiaryEntriesDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						workDiaryEntryTransaction.update(s);
					} else {
						workDiaryEntryTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);	
	}

	private EmpWorkDiaryEntriesDBO convertDtoToDbo(EmpWorkDiaryEntriesDTO dto, String userId) {
		EmpWorkDiaryEntriesDBO dbo = null;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo = workDiaryEntryTransaction.getData(dto.getId());
		}
		if(Utils.isNullOrEmpty(dbo)) {
			dbo = new EmpWorkDiaryEntriesDBO();
			dbo.setCreatedUsersId(Integer.parseInt(userId));
		} else {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		LocalDate localDate = Utils.convertStringDateToLocalDate(dto.getDate());
		dbo.setId(dto.getId());
		dbo.setErpWorkEntryDate(localDate);
		Integer empId = workDiaryEntryTransaction.getEmployeeId(userId);
		if(!Utils.isNullOrEmpty(empId)) {
			Integer approversId =  workDiaryEntryTransaction.getApproversIdByEmployeeId((empId));
		if(!Utils.isNullOrEmpty(approversId)) {
			dbo.setWorkdDiaryApproverId(new EmpDBO());
			dbo.getWorkdDiaryApproverId().id = approversId;
		}
	   }
		dbo.setApplicantStatusLogTime(LocalDateTime.now());
		dbo.setApplicationStatusLogTime(LocalDateTime.now());
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		dbo.setEmpDBO(new EmpDBO());
		dbo.getEmpDBO().setId(empId);
		if(!Utils.isNullOrEmpty(dto.getDate())) {
			String date = dto.getDate();
			LocalDate localDate1 = Utils.convertStringDateToLocalDate(date);
			Integer campusId = commonApiTransaction.getCampusIdByUserId(Integer.parseInt(userId)); 
			if(!Utils.isNullOrEmpty(campusId)) {
				ErpAcademicYearDBO academicYearId = workDiaryEntryTransaction.getAcademicYearId(localDate1, campusId);
				if(!Utils.isNullOrEmpty(academicYearId)) {
					dbo.setErpAcademicYearId(new ErpAcademicYearDBO());
					dbo.getErpAcademicYearId().setId(academicYearId.getId());
				}
			}
		}
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		Tuple tuple = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode1("WORK_DIARY_SUBMITTED");
		if(tuple.get("applicant_status_display_text")!=null && !Utils.isNullOrWhitespace(tuple.get("applicant_status_display_text").toString())) {
			ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
			applicant.id =Integer.parseInt(tuple.get("erp_work_flow_process_id").toString());
			dbo.erpApplicantWorkFlowProcessDBO = applicant;
		}
		if(tuple.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(tuple.get("application_status_display_text").toString())) {
			ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
			application.id =Integer.parseInt(tuple.get("erp_work_flow_process_id").toString());
			dbo.erpApplicationWorkFlowProcessDBO = application;				
		}
		Set<EmpWorkDiaryEntriesDetailsDBO> existDBOSet= dbo.getEmpWorkDiaryEntriesDetailsDBOSet();
		Map<Integer, EmpWorkDiaryEntriesDetailsDBO> map = new HashMap<Integer, EmpWorkDiaryEntriesDetailsDBO>();
		if (!Utils.isNullOrEmpty(existDBOSet)) {
			existDBOSet.forEach(dbos-> {
				if (dbos.getRecordStatus()=='A') {
					map.put(dbos.getId(), dbos);
				}
			});
		}
		Set<EmpWorkDiaryEntriesDetailsDBO> empWorkDiaryEntriesDbo = new HashSet<EmpWorkDiaryEntriesDetailsDBO>();  
		if(!Utils.isNullOrEmpty(dto.getEmpWorkDiaryEntriesDetails())) {
			dto.getEmpWorkDiaryEntriesDetails().forEach(subdtos -> {
				EmpWorkDiaryEntriesDetailsDBO subdbos = null;
				if(!Utils.isNullOrEmpty(subdtos.getId()) && map.containsKey(subdtos.getId())) {	
					subdbos = map.get(subdtos.getId());
					subdbos.setModifiedUsersId(Integer.parseInt(userId));
					map.remove(subdtos.getId());
				} else {
					subdbos = new EmpWorkDiaryEntriesDetailsDBO();
					subdbos.setCreatedUsersId(Integer.parseInt(userId));
				}
			});
			EmpWorkDiaryEntriesDBO dbo1 = dbo;
			if(!Utils.isNullOrEmpty(dto.getEmpWorkDiaryEntriesDetails())) {
				dto.getEmpWorkDiaryEntriesDetails().forEach(dtos-> {
					EmpWorkDiaryEntriesDetailsDBO detailsDBO = new EmpWorkDiaryEntriesDetailsDBO();
					BeanUtils.copyProperties(dtos, detailsDBO);
					detailsDBO.setEmpWorkDiaryEntriesDBO(dbo1);
					detailsDBO.setId(dtos.getId());
					if(!Utils.isNullOrEmpty(dtos.getFromTime())) {
						detailsDBO.setFromTime(Utils.convertStringTimeToLocalTime(dtos.getFromTime()));
					}
					if(!Utils.isNullOrEmpty(dtos.getToTime())) {
						detailsDBO.setToTime(Utils.convertStringTimeToLocalTime(dtos.getToTime()));
					} 
					if(!Utils.isNullOrEmpty(dtos.getTotalTime())) {
						detailsDBO.setTotalTime(Utils.convertStringTimeToLocalTime(dtos.getTotalTime()));
					}
					if(!Utils.isNullOrEmpty(dtos.getActivity().getValue())) {
						detailsDBO.setEmpWorkDiaryActivityDBO(new EmpWorkDiaryActivityDBO());
						detailsDBO.getEmpWorkDiaryActivityDBO().setId(Integer.parseInt((dtos.getActivity().getValue())));
					}
					detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					if(!Utils.isNullOrEmpty(dtos.getId()))
						detailsDBO.setModifiedUsersId(Integer.parseInt(userId)); 
					detailsDBO.setRecordStatus('A');
					empWorkDiaryEntriesDbo.add(detailsDBO);
				});
				dbo.setEmpWorkDiaryEntriesDetailsDBOSet(empWorkDiaryEntriesDbo);	 
			}
		}
		if(!Utils.isNullOrEmpty(map)) {
			map.forEach((entry, value)-> {
				value.setModifiedUsersId(Integer.parseInt(userId));
				value.setRecordStatus('D');
				empWorkDiaryEntriesDbo.add(value);
			});
		}
		return dbo;
	}

	public Flux<EmpWorkDiaryEntriesDTO> getWorkDiaryEntryData (Map<String, String> requestParams, String empId, String userId) {
		Map<String, EmpAttendanceDTO> map = new LinkedHashMap<String, EmpAttendanceDTO>();
		List<EmpWorkDiaryEntriesDTO> dtos = new  ArrayList<EmpWorkDiaryEntriesDTO>();
		Integer empIds = 0;
		if(!Utils.isNullOrEmpty(empId)) {
			empIds = Integer.parseInt(empId);
		} else {
			empIds = workDiaryEntryTransaction.getEmployeeId(userId);
		}
		List<EmpWorkDiaryEntriesDBO>  workDiaryList =  workDiaryEntryTransaction.getWorkDiaryEntryData(requestParams, empIds);
		Tuple empsId = workDiaryEntryTransaction.getEmployeesId(userId);
		requestParams.put("empId", empsId.get("empId").toString());
		ViewEmployeeAttendanceTransaction viewEmployeeAttendanceTransaction = new  ViewEmployeeAttendanceTransaction(); 
//		List<Tuple> mappings = viewEmployeeAttendanceTransaction.getAttendanceDetailsForEmployee(requestParams); // this api is changed from the screen.
//		if(!Utils.isNullOrEmpty(mappings)) {
//			mappings.forEach(attendancedbo -> {
//				EmpAttendanceDTO attendanceDto = new EmpAttendanceDTO();
//				if(!Utils.isNullOrEmpty(attendancedbo.get("date"))) {
//					attendanceDto.setDate(Utils.convertLocalDateTimeToStringDate(Utils.convertStringDateTimeToLocalDateTime(attendancedbo.get("date").toString())));
//				}
//				if(!Utils.isNullOrEmpty(attendancedbo.get("timeIn")))
//					attendanceDto.setTimeIn(Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(attendancedbo.get("timeIn").toString())));
//				if(!Utils.isNullOrEmpty(attendancedbo.get("timeOut")))
//					attendanceDto.setTimeOut(Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(attendancedbo.get("timeOut").toString())));
//				if(!Utils.isNullOrEmpty(attendancedbo.get("empHolidayEventsType")))
//					attendanceDto.setEmpHolidayEventsType(attendancedbo.get("empHolidayEventsType").toString());
//				if(!Utils.isNullOrEmpty(attendancedbo.get("holidayEventsDescription")))
//					attendanceDto.setHolidayEventsDescription(attendancedbo.get("holidayEventsDescription").toString());
//				if(!Utils.isNullOrEmpty(attendancedbo.get("leaveType")))
//					attendanceDto.setLeaveType(attendancedbo.get("leaveType").toString());
//				if(!Utils.isNullOrEmpty(attendancedbo.get("leaveSession")))
//					attendanceDto.setLeaveSession(attendancedbo.get("leaveSession").toString());
//				if(!Utils.isNullOrEmpty(attendancedbo.get("leaveTypeColor")))
//					attendanceDto.leaveTypeColor=attendancedbo.get("leaveTypeColor").toString();
//				map.put(Utils.convertLocalDateTimeToStringDate(Utils.convertStringDateTimeToLocalDateTime(attendancedbo.get("date").toString())), attendanceDto);
//			});
//		}
		workDiaryList.forEach(dbo -> {
			List<EmpAttendanceDTO> viewEmployeeAttendanceDTO = new ArrayList<EmpAttendanceDTO>();
			EmpWorkDiaryEntriesDTO dto = new EmpWorkDiaryEntriesDTO();
			dto.setId(dbo.getId());
			if(!Utils.isNullOrEmpty(dbo.getEmpDBO())) {
				dto.setEmpDTO(new EmpDTO());
				dto.getEmpDTO().setEmpId(dbo.getEmpDBO().getId().toString());
				dto.getEmpDTO().setEmpNo(dbo.getEmpDBO().getEmpNumber().toString());
				dto.getEmpDTO().setEmpName(dbo.getEmpDBO().getEmpName());
			}
			if(!Utils.isNullOrEmpty(dbo.getErpWorkEntryDate())) {
				dto.setDate(Utils.convertLocalDateToStringDate(dbo.getErpWorkEntryDate())); 
			}
			dto.setWorkFlowStatus(dbo.getErpApplicationWorkFlowProcessDBO().getProcessCode());
			List<EmpWorkDiaryEntriesDetailsDTO> workDiaryDetailsList = new ArrayList<EmpWorkDiaryEntriesDetailsDTO>();
			dbo.getEmpWorkDiaryEntriesDetailsDBOSet().forEach(subdbo -> {
				if(subdbo.getRecordStatus() == 'A') {
					EmpWorkDiaryEntriesDetailsDTO subdto = new EmpWorkDiaryEntriesDetailsDTO();
					subdto.setId(subdbo.getId());
					if(!Utils.isNullOrEmpty(subdbo.getFromTime())) { 
						subdto.setFromTime(Utils.convertLocalTimeToStringTime(subdbo.getFromTime()));
					}
					if(!Utils.isNullOrEmpty(subdbo.getToTime())) { 	
						subdto.setToTime(Utils.convertLocalTimeToStringTime(subdbo.getToTime()));
					}
					if(!Utils.isNullOrEmpty(subdbo.getTotalTime())) { 	
						subdto.setTotalTime(Utils.convertLocalTimeToStringTime1(subdbo.getTotalTime()));
					}
					if(!Utils.isNullOrEmpty(subdbo.getEmpWorkDiaryActivityDBO()))  {    
						subdto.setActivity(new SelectDTO());
						subdto.getActivity().setValue(String.valueOf(subdbo.getEmpWorkDiaryActivityDBO().getId()));
						subdto.getActivity().setLabel(subdbo.getEmpWorkDiaryActivityDBO().getActivityName().toString());
					} else {
						subdto.setOtherActivity(subdbo.getOtherActivity());
					}
					subdto.setRemarks(subdbo.getRemarks());
					workDiaryDetailsList.add(subdto);
				}
			});
			dto.setEmpWorkDiaryEntriesDetails(workDiaryDetailsList);	
			if(map.containsKey(Utils.convertLocalDateToStringDate3(dbo.getErpWorkEntryDate()))){
				viewEmployeeAttendanceDTO.add(map.get(Utils.convertLocalDateToStringDate3(dbo.getErpWorkEntryDate())));
				map.remove(Utils.convertLocalDateToStringDate3(dbo.getErpWorkEntryDate()));
				dto.setViewEmployeeAttendanceDto(viewEmployeeAttendanceDTO);
			} else {
				dto.setViewEmployeeAttendanceDto(null);	
			}
			dtos.add(dto);
		});
		if(!Utils.isNullOrEmpty(map)) {
			map.forEach((entry, value)-> {
				EmpWorkDiaryEntriesDTO dto = new EmpWorkDiaryEntriesDTO();
				if(!Utils.isNullOrEmpty(entry)) {
					dto.setDate((entry)); 
					List<EmpAttendanceDTO> attendanceList = new ArrayList<EmpAttendanceDTO>();
					attendanceList.add(value);
					dto.setViewEmployeeAttendanceDto(attendanceList);
					dtos.add(dto);
				}
			});
		}
		return Flux.fromIterable(dtos);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return workDiaryEntryTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}
}	





