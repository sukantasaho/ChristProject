package com.christ.erp.services.controllers.employee.leave;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dbqueries.employee.LeaveQueries;
import com.christ.erp.services.dto.common.AttendanceCumulativeDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave/OfflineLeaveApplication")
public class OfflineLeaveApplicationController extends BaseApiController{
	
	@RequestMapping(value = "/getEmployeeDetails", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLeaveEntryDTO>>> getEmployeeDetails(@RequestParam("value") String value) {
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
					//	mappingInfo.employeeID = mapping.get("employeeID").toString();
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

	
	@RequestMapping(value = "/getLeaveDetails", method = RequestMethod.POST)
	public Mono<ApiResult<List<AttendanceCumulativeDTO>>> getLeaveDetails(@RequestParam("value") String value) {
		ApiResult<List<AttendanceCumulativeDTO>> result = new ApiResult<List<AttendanceCumulativeDTO>>();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				Query query =null;
				if(value.matches("-?\\d+")) {
				 query = context.createNativeQuery(LeaveQueries.LEAVE_EMPLOYEE_LEAVE_DETAILS,Tuple.class);
				query.setParameter("empId", value);
				}else {
					 query = context.createNativeQuery(LeaveQueries.LEAVE_EMPLOYEE_LEAVE_DETAILS1,Tuple.class);
					query.setParameter("empName", value);
				}
				
				List<Tuple> mappings = query.getResultList();
				if (mappings != null && mappings.size() > 0) {
					result.success = true;
					result.dto = new ArrayList<>();
					for (Tuple mapping : mappings) {
						AttendanceCumulativeDTO mappingInfo = new AttendanceCumulativeDTO();
						mappingInfo.name = mapping.get("LeaveTypeName").toString();
						mappingInfo.value =  (Float) mapping.get("LeaveRemaining");
						mappingInfo.leaveTypeName = (String) mapping.get("LeaveTypeCode");
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
}
