package com.christ.erp.services.handlers.employee.leave;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveDetailsDisplayTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LeaveDetailsDisplayHandler {
	
	@Autowired 
	LeaveDetailsDisplayTransaction leaveDetailsDisplayTransaction;
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();
	
	
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> adminPrivilege() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> approverPrivilege() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> hodPrivilege() {
		ApiResult result = new ApiResult();
		result.setSuccess(false);
		return Mono.just(result);
	}
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> deanPrivilege() {
		ApiResult result = new ApiResult();
		result.setSuccess(false);
		return Mono.just(result);
	}
	public Flux<EmpLeaveEntryDTO> getGridData(EmpLeaveEntryDTO data,String userId) throws Exception {
		Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
		return leaveDetailsDisplayTransaction.getGridData(data,empId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	public EmpLeaveEntryDTO convertDboToDto(Tuple dbo) {
		EmpLeaveEntryDTO dto = new EmpLeaveEntryDTO();
		dto.setName(String.valueOf(dbo.get("emp_name")));
		if(!Utils.isNullOrEmpty(dbo.get("leave_type_name"))) {
			dto.setLeaveTypeName(String.valueOf(dbo.get("leave_type_name")));
		}	
		if(!Utils.isNullOrEmpty(dbo.get("leave_start_date"))) {
			String sdate = String.valueOf(dbo.get("leave_start_date"));
			dto.setStartDate(Utils.convertStringDateToLocalDate(sdate));
		}
		if(!Utils.isNullOrEmpty(dbo.get("leave_end_date"))) {
			String edate = String.valueOf(dbo.get("leave_end_date"));
			dto.setEndDate(Utils.convertStringDateToLocalDate(edate));
		}

		ExModelBaseDTO to = new ExModelBaseDTO();
		if(!Utils.isNullOrEmpty(dbo.get("leave_start_session")) && String.valueOf(dbo.get("leave_start_session")).equalsIgnoreCase("FD")) {  
		to.setId("1");
		to.setTag("Full Day");			
		}else if(!Utils.isNullOrEmpty(dbo.get("leave_start_session")) && String.valueOf(dbo.get("leave_start_session")).equalsIgnoreCase("FN")) {
		to.setId("2");
		to.setTag("Forenoon");			
		} else if (!Utils.isNullOrEmpty(dbo.get("leave_start_session")) && String.valueOf(dbo.get("leave_start_session")).equalsIgnoreCase("AN")) {
		to.setId("3");
		to.setTag("Afternoon");			
		}
		dto.setFromSession(to);
		ExModelBaseDTO to1 = new ExModelBaseDTO();
		if(!Utils.isNullOrEmpty(dbo.get("leave_end_session")) && String.valueOf(dbo.get("leave_end_session")).equalsIgnoreCase("FD")) {  
		to1.setId("1");
		to1.setTag("Full Day");			
		}else if(!Utils.isNullOrEmpty(dbo.get("leave_end_session")) && String.valueOf(dbo.get("leave_end_session")).equalsIgnoreCase("FN")) {
		to1.setId("2");
		to1.setTag("Forenoon");			
		} else if (!Utils.isNullOrEmpty(dbo.get("leave_end_session")) && String.valueOf(dbo.get("leave_end_session")).equalsIgnoreCase("AN")) {
		to1.setId("3");
		to1.setTag("Afternoon");			
		}
		dto.setToSession(to1);
		dto.setDepartment(String.valueOf(dbo.get("department_name")));	
		dto.setTotalDays(String.valueOf(dbo.get("number_of_days_leave")));
		//dto.setStatus(String.valueOf(dbo.get("")));
		dto.setReason(String.valueOf(dbo.get("leave_reason")));
		return dto;
	}
	
	public Flux<EmpLeaveEntryDTO> getEmpCampus(EmpLeaveEntryDTO data,String userId) throws Exception {
		return leaveDetailsDisplayTransaction.getEmpCampus(data,userId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	public Flux<EmpLeaveEntryDTO> getEmpDepartment(EmpLeaveEntryDTO data,String userId) throws Exception {		
		return leaveDetailsDisplayTransaction.getEmpDepartment(data,userId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

}
