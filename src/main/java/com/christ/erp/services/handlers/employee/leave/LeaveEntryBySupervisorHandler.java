package com.christ.erp.services.handlers.employee.leave;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.transactions.employee.leave.LeaveEntryBySupervisorTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LeaveEntryBySupervisorHandler {
	
	@Autowired 
	LeaveEntryBySupervisorTransaction leaveEntryBySupervisorTransaction;

	public Flux<EmpLeaveEntryDTO> getGridData(Integer empId) {
		return leaveEntryBySupervisorTransaction.getGridData(empId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	public Flux<EmpLeaveEntryDTO> getEmployeeLeaveHistory(Integer empId) {
		return leaveEntryBySupervisorTransaction.getEmployeeLeaveHistory(empId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	
	public EmpLeaveEntryDTO convertDboToDto(EmpLeaveEntryDBO dbo) {
		EmpLeaveEntryDTO dto = new EmpLeaveEntryDTO();
		dto.setName(dbo.getEmpID().getEmpName());
		dto.setLeaveTypeName(dbo.getLeaveTypecategory().getLeaveTypeName());
		dto.setStartDate(dbo.getStartDate());
		dto.setEndDate(dbo.getEndDate());
		ExModelBaseDTO to = new ExModelBaseDTO();
		if(dbo.getFromSession() != null && dbo.getFromSession().equalsIgnoreCase("FD")) {
		to.setId("1");
		to.setTag("Full Day");			
		}else if(dbo.getFromSession() != null && dbo.getFromSession().equalsIgnoreCase("FN")) {
		to.setId("2");
		to.setTag("Forenoon");			
		} else if (dbo.getFromSession() != null && dbo.getFromSession().equalsIgnoreCase("AN")) {
		to.setId("3");
		to.setTag("Afternoon");			
		}
		dto.setFromSession(to);
		ExModelBaseDTO to1 = new ExModelBaseDTO();
		if(dbo.getToSession() != null && dbo.getToSession().equalsIgnoreCase("FD")) {
		to1.setId("1");
		to1.setTag("Full Day");			
		}else if(dbo.getToSession() != null && dbo.getToSession().equalsIgnoreCase("FN")) {
		to1.setId("2");
		to1.setTag("Forenoon");			
		} else if (dbo.getToSession() != null && dbo.getToSession().equalsIgnoreCase("AN")) {
		to1.setId("3");
		to1.setTag("Afternoon");			
		}
		dto.setToSession(to1);
		dto.setTotalDays(String.valueOf(dbo.getTotalDays()));
		if(dbo.getErpApplicationWorkFlowProcessDBO() != null && dbo.getErpApplicationWorkFlowProcessDBO().getApplicationStatusDisplayText() != null) {
			dto.setStatus(dbo.getErpApplicationWorkFlowProcessDBO().getApplicationStatusDisplayText());
		}
		
		dto.setReason(dbo.getReason());
		return dto;
	}
	
	
	public Mono<List<SelectDTO>> getEmployeeIdOrName(String employeeIdOrName, Boolean isNumber, Integer empId) {
		 List<Tuple> values = leaveEntryBySupervisorTransaction.getEmployeeIdOrName(employeeIdOrName, empId);
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
	
	

}
