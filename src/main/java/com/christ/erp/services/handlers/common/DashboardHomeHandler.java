package com.christ.erp.services.handlers.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.DashBoardCampusDepartmentAttendanceDTO;
import com.christ.erp.services.dto.common.NestedSelectDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.common.DashboardHomeTransaction;

import reactor.core.publisher.Flux;

@Service
public class DashboardHomeHandler {
	
	private static volatile DashboardHomeHandler dashboardHomeHandler = null;
	DashboardHomeTransaction dashboardHomeTransaction = DashboardHomeTransaction.getInstance();
	public static DashboardHomeHandler getInstance() {
		if (dashboardHomeHandler == null) {
			dashboardHomeHandler = new DashboardHomeHandler();
		}
		return dashboardHomeHandler;
	}
	
	@Autowired
	private DashboardHomeTransaction dashboardHomeTransaction1;

//	public ApiResult<List<ViewEmployeeAttendanceDTO>> getAttendanceTypeForEmployees(@RequestParam Map<String,String> requestParams, ApiResult<List<ViewEmployeeAttendanceDTO>> result) throws Exception {
//		List<Tuple> employeeIds =dashboardHomeTransaction.getEmployeesByUserIdForDepartmentCampusDashBoard(requestParams.get("userId").toString());
//		List<String> empIds=new ArrayList<String>();
//		employeeIds.forEach(item->empIds.add(item.get("Employee Id").toString()));
//		List<Tuple> mappings =dashboardHomeTransaction.getAttendanceTypesDataForEmployees(empIds,requestParams.get("startDate").toString(),requestParams.get("endDate").toString());
//		if (mappings != null && mappings.size() > 0) {
//		Map<Integer,Map<Integer,DashBoardCampusDepartmentAttendanceDTO>> mainMap=new LinkedHashMap<>();
//		Map<Integer,String> dptMap = new HashMap<>();
//		Map<Integer,String> campusMap = new HashMap<>();
//		for (Tuple mapping : mappings) {
//			if(mainMap.containsKey(mapping.get("departmentId"))) {
//				Map<Integer,DashBoardCampusDepartmentAttendanceDTO> subMap = mainMap.get(mapping.get("departmentId"));
//				if(subMap.containsKey(mapping.get("campusId"))) {
//					calculateAttendanceTypesforEmployees(mapping,subMap.get(mapping.get("campusId")));
//				}
//				else {
//					Map<Integer,DashBoardCampusDepartmentAttendanceDTO> subMap1 = mainMap.get(mapping.get("departmentId"));
//					DashBoardCampusDepartmentAttendanceDTO dto =calculateAttendanceTypesforEmployees(mapping,null);
//					subMap1.put((Integer) mapping.get("campusId"),dto);
//					campusMap.put((Integer) mapping.get("campusId"), mapping.get("campusName").toString());
//				}
//			}
//			else {
//				Map<Integer, DashBoardCampusDepartmentAttendanceDTO> subMap = new LinkedHashMap<>();
//				DashBoardCampusDepartmentAttendanceDTO dto =calculateAttendanceTypesforEmployees(mapping,null);
//				subMap.put((Integer) mapping.get("campusId"), dto);
//				mainMap.put((Integer) mapping.get("departmentId"), subMap);
//				dptMap.put((Integer) mapping.get("departmentId"), mapping.get("departmentName").toString());
//				campusMap.put((Integer) mapping.get("campusId"), mapping.get("campusName").toString());
//			}
//		}
//		result.success = true;
//		result.dto = new ArrayList<>();
//		mainMap.forEach((key, value) -> {
//			ViewEmployeeAttendanceDTO mappingInfo = new ViewEmployeeAttendanceDTO();
//			List<String> campusNames = new ArrayList<String>();
//			List<Integer> present = new ArrayList<Integer>();
//			List<Integer> absent = new ArrayList<Integer>();
//			List<Integer> halfDay = new ArrayList<Integer>();
//			List<Integer> lateEntry = new ArrayList<Integer>();
//			List<Integer> earlyExit = new ArrayList<Integer>();
//
//			mappingInfo.departmentName=dptMap.get(key);
//		    value.forEach((key1, value1) -> {
//		    	campusNames.add(campusMap.get(key1).replace("Campus", ""));
//			    present.add(value1.present);
//			    absent.add(value1.absent);
//			    halfDay.add(value1.halfDay);
//			    lateEntry.add(value1.lateEntry);
//			    earlyExit.add(value1.earlyExit);
//			});
//		    mappingInfo.campusNames=campusNames;
//		    mappingInfo.present=present;
//		    mappingInfo.absent=absent;
//		    mappingInfo.halfDay=halfDay;
//		    mappingInfo.lateEntry=lateEntry;
//		    mappingInfo.earlyExit=earlyExit;
//		    if(Utils.isNullOrEmpty(mappingInfo.present) && Utils.isNullOrEmpty(mappingInfo.absent) &&Utils.isNullOrEmpty(mappingInfo.halfDay)
//		    	&&	Utils.isNullOrEmpty(mappingInfo.lateEntry) && Utils.isNullOrEmpty(mappingInfo.earlyExit))
//		    	result.failureMessage = "No data available for this Date Range";
//		    result.dto.add(mappingInfo);
//		});
//		}else {
//			result.failureMessage = "No data available for this Date Range";
//		}
//		return result;
//	}

	private DashBoardCampusDepartmentAttendanceDTO calculateAttendanceTypesforEmployees(Tuple mapping,DashBoardCampusDepartmentAttendanceDTO dto) {
		if(dto==null) {
			dto=new DashBoardCampusDepartmentAttendanceDTO();
		}
		if ((!Utils.isNullOrEmpty(mapping.get("dayName")) && !mapping.get("dayName").equals("Sunday")) || (!Utils.isNullOrEmpty(mapping.get("dayName")) 
				&& mapping.get("dayName").equals("Sunday") && !Utils.isNullOrEmpty(mapping.get("isSundayWorking")) && mapping.get("isSundayWorking").equals(true))) {
			if (!Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("timeOut")) && Utils.isNullOrEmpty(mapping.get("leaveEntryId"))) {
				dto.present = dto.present + 1;
			}
			if (Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut")) && !Utils.isNullOrEmpty(mapping.get("leaveEntryId"))
					&& Utils.isNullOrEmpty(mapping.get("leaveSession")) && Utils.isNullOrEmpty(mapping.get("holidayEventsId"))) {
				dto.absent = dto.absent + 1;
			}else if (Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut"))) {
				dto.absent = dto.absent + 1;
			}
			if (!Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("timeOut")) && !Utils.isNullOrEmpty(mapping.get("leaveEntryId"))
					&& !Utils.isNullOrEmpty(mapping.get("leaveSession"))) {
				dto.halfDay = dto.halfDay + 1;
			}else if (!Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut"))) {
				dto.halfDay = dto.halfDay + 1;
			}
			if (!Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("lateEntry")) && (boolean) mapping.get("lateEntry") == true) {
				dto.lateEntry = dto.lateEntry + 1;
			}
			if (!Utils.isNullOrEmpty(mapping.get("timeOut")) && !Utils.isNullOrEmpty(mapping.get("earlyExit")) && (boolean) mapping.get("earlyExit") == true) {
				dto.earlyExit = dto.earlyExit + 1;
			}
		}
		return dto;
	}

	public Flux<SelectDTO> getDashBoardEmployeeDiversity() {
		return dashboardHomeTransaction1.getDashBoardEmployeeDiversity().flatMapMany(Flux::fromIterable)
				.filter(tuple -> !Utils.isNullOrEmpty(tuple.get("state")) && !Utils.isNullOrEmpty(tuple.get("emp_count")))
				.map(this::dashBoardEmployeeDiversity);
	}
	
	public SelectDTO dashBoardEmployeeDiversity(Tuple data) {
		SelectDTO selectDTO = new SelectDTO();
		selectDTO.setLabel(data.get("state").toString());
		selectDTO.setValue(data.get("emp_count").toString());
		return selectDTO;
	}

	public Flux<SelectDTO> getDashBoardEmployeeExperience() {
		return dashboardHomeTransaction1.getDashBoardEmployeeExperience().flatMapMany(Flux::fromIterable)
				.filter(tuple -> !Utils.isNullOrEmpty(tuple.get("range_of_years_of_experience")) && !Utils.isNullOrEmpty(tuple.get("emp_count")))
				.map(this::DashBoardEmployeeExperience);
	}

	private SelectDTO DashBoardEmployeeExperience(Tuple dbo) {
		SelectDTO selectDTO = new SelectDTO();
		selectDTO.setValue(dbo.get("range_of_years_of_experience").toString());
		selectDTO.setLabel(dbo.get("emp_count").toString());
		return selectDTO;
	}
	
	public Flux<NestedSelectDTO> getDashBoardEmployeeQualification() {
	    return dashboardHomeTransaction1.getDashBoardEmployeeQualification()
	            .flatMapMany(Flux::fromIterable)
	            .filter(s -> !Utils.isNullOrEmpty(s.get("erp_deanery_id")) && !Utils.isNullOrEmpty(s.get("erp_qualification_level")) && !Utils.isNullOrEmpty(s.get("emp_count")))
	            .collect(Collectors.groupingBy(s -> Integer.parseInt(s.get("erp_deanery_id").toString()),
	                    Collectors.groupingBy(s -> s.get("erp_deanery").toString(), Collectors.toList())))
	            .flatMapMany(this::DashBoardEmployeeQualification);
	}
	
	public Flux<NestedSelectDTO> DashBoardEmployeeQualification( Map<Integer,Map<String,List<Tuple>>> map){
		List<NestedSelectDTO> nestedSelectDTOList = new ArrayList<NestedSelectDTO>();
		if(!Utils.isNullOrEmpty(map)) {
			map.forEach((key,value) -> {
				NestedSelectDTO nestedSelectDTO = new NestedSelectDTO();
				List<SelectDTO> selectDTOs = new ArrayList<SelectDTO>();
				nestedSelectDTO.setValue(key.toString());
				if(!Utils.isNullOrEmpty(value)) {
					value.forEach((key1,value1) -> {
						nestedSelectDTO.setLabel(key1);
						if(!Utils.isNullOrEmpty(value1)) {
							value1.forEach(data -> {
								SelectDTO selectDTO = new SelectDTO();
								selectDTO.setLabel(data.get("erp_qualification_level").toString());
								selectDTO.setValue(data.get("emp_count").toString());
								selectDTOs.add(selectDTO);
							});
							nestedSelectDTO.setList(selectDTOs);
						}
						nestedSelectDTOList.add(nestedSelectDTO);
					});
				}
			});
		}
		return Flux.fromIterable(nestedSelectDTOList);	
	}

	public Flux<SelectDTO> getDashBoardEmployeeApplicationStatus() {
		return dashboardHomeTransaction1.getDashBoardEmployeeApplicationStatus().flatMapMany(Flux::fromIterable)
				.filter(tuple -> !Utils.isNullOrEmpty(tuple.get("appln_count")) && !Utils.isNullOrEmpty(tuple.get("status")))		
				.map(this::DashBoardEmployeeApplicationStatus);
	}
	
	public SelectDTO DashBoardEmployeeApplicationStatus(Tuple tuple) {
		SelectDTO selectDTO = new SelectDTO();
		selectDTO.setValue(tuple.get("appln_count").toString());
		selectDTO.setLabel(tuple.get("status").toString());
		return selectDTO;
	}
	
	public Flux<NestedSelectDTO> getDashBoardEmployeeCount() {
		return dashboardHomeTransaction1.getDashBoardEmployeeCount()
				.flatMapMany(Flux::fromIterable)
				.filter(s -> !Utils.isNullOrEmpty(s.get("parent"))
						&& !Utils.isNullOrEmpty(s.get("erp_deanery_id"))
						&& !Utils.isNullOrEmpty(s.get("erp_deanery"))
						&& !Utils.isNullOrEmpty(s.get("erp_department_id"))
						&& !Utils.isNullOrEmpty(s.get("erp_department"))
						&& !Utils.isNullOrEmpty(s.get("emp_designation_id"))
						&& !Utils.isNullOrEmpty(s.get("emp_designation"))
						&& !Utils.isNullOrEmpty(s.get("emp_count")))
				.collect(Collectors.groupingBy(s -> s.get("parent").toString(),
						Collectors.groupingBy(s -> s.get("erp_deanery").toString(),
								Collectors.groupingBy(s -> s.get("erp_department").toString(),
										Collectors.toList()))))
				.flatMapMany(this::DashBoardEmployeeCount);
	}
	
	public Flux<NestedSelectDTO> DashBoardEmployeeCount(Map<String,Map<String,Map<String,List<Tuple>>>> map){
		List<NestedSelectDTO> nestedSelectDTOs = new ArrayList<NestedSelectDTO>();
		if(!Utils.isNullOrEmpty(map)) {
			map.forEach((key,value) -> { //key christ
				List<NestedSelectDTO> nestedSelectDTODenaryList = new ArrayList<NestedSelectDTO>();
				NestedSelectDTO nestedSelectDTO = new NestedSelectDTO();
				nestedSelectDTO.setLabel(key);
				if(!Utils.isNullOrEmpty(value)) { 
					value.forEach((key1,value1) -> { //key1  denery
						NestedSelectDTO nestedSelectDTODenary = new NestedSelectDTO();
						nestedSelectDTODenary.setLabel(key1);
						if(!Utils.isNullOrEmpty(value1)) {
							List<NestedSelectDTO> nestedSelectDTODepartmentList = new ArrayList<NestedSelectDTO>();
							value1.forEach((key2,value2) -> {// key2 depar
								NestedSelectDTO nestedSelectDTODepartment = new NestedSelectDTO();
								nestedSelectDTODepartment.setLabel(key2);
								nestedSelectDTODepartmentList.add(nestedSelectDTODepartment);
								if(!Utils.isNullOrEmpty(value2)) {
									List<SelectDTO> selectDTOs = new ArrayList<SelectDTO>();
									value2.forEach(data -> {//data count
										SelectDTO selectDTO = new SelectDTO();
										selectDTO.setValue(data.get("emp_count").toString());
										selectDTO.setLabel(data.get("emp_designation").toString());
										selectDTOs.add(selectDTO);
										nestedSelectDTODepartment.setList(selectDTOs);
									});
								}
								nestedSelectDTODenary.setList(nestedSelectDTODepartmentList);
							});
						}
						nestedSelectDTODenaryList.add(nestedSelectDTODenary);
					});
					nestedSelectDTO.setList(nestedSelectDTODenaryList);
				}
				nestedSelectDTOs.add(nestedSelectDTO);
			});
		}
		return Flux.fromIterable(nestedSelectDTOs);
	}
}
