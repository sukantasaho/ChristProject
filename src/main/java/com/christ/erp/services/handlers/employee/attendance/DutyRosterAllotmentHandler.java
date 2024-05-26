package com.christ.erp.services.handlers.employee.attendance;

import static java.util.stream.Collectors.toMap;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.Tuple;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpRosterAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpShiftTypesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.attendance.DutyRosterAllotmentDTO;
import com.christ.erp.services.dto.employee.attendance.EmpRosterAllotmentDTO;
import com.christ.erp.services.dto.employee.attendance.EmpShiftTypesDTO;
import com.christ.erp.services.transactions.employee.attendance.DutyRosterAllotmentTransaction;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

public class DutyRosterAllotmentHandler {

	private static volatile DutyRosterAllotmentHandler dutyRosterAllotmentHandler = null;
	DutyRosterAllotmentTransaction dutyRosterAllotmentTransaction = DutyRosterAllotmentTransaction.getInstance();

	public static DutyRosterAllotmentHandler getInstance() {
		if(dutyRosterAllotmentHandler==null) {
			dutyRosterAllotmentHandler = new DutyRosterAllotmentHandler();
		}
		return dutyRosterAllotmentHandler;
	}

	public DutyRosterAllotmentDTO getDutyRosterData(Map<String, String> data) throws Exception{
		DutyRosterAllotmentDTO rosterDTO = new DutyRosterAllotmentDTO();
	
		try {
			List<EmpRosterAllotmentDTO> weekList = new ArrayList<EmpRosterAllotmentDTO>();
			List<EmpRosterAllotmentDTO> rosterAllotmentList = new ArrayList<EmpRosterAllotmentDTO>();
			rosterDTO.campus = new ExModelBaseDTO();
			rosterDTO.campus.id = data.get("campusId");

			//	        rosterDTO.fromDate = Utils.convertLocalDateToStringDate(Utils.convertStringDateTimeToLocalDate(data.get("fromDate").toString()));
			//	        rosterDTO.toDate = Utils.convertLocalDateToStringDate(Utils.convertStringDateTimeToLocalDate(data.get("toDate").toString()));
			rosterDTO.fromDate = data.get("fromDate").toString();
			rosterDTO.toDate = data.get("toDate").toString();

			//	        LocalDate fromDate = Utils.convertStringDateTimeToLocalDate(data.get("fromDate"));
			LocalDate fromDate = Utils.convertStringDateToLocalDate(data.get("fromDate"));
			LocalDate toDate = Utils.convertStringDateToLocalDate(data.get("toDate"));


			//long daysDiff = Utils.getDaysDifference(data.get("fromDate"), data.get("toDate"));
			//			long daysDiff = Utils.getDaysDifference(data.get("toDate"), data.get("fromDate"));
			long daysDiff = Utils.getDaysDifference(toDate.atStartOfDay().toString(), fromDate.atStartOfDay().toString());

//			String formattedDate = Utils.convertLocalDateToStringDate4(fromDate);
			String formattedDate = Utils.convertLocalDateToStringDate(fromDate);
			LocalDate fDate = fromDate;
			List<Tuple> empList = dutyRosterAllotmentTransaction.getDutyRosterData(data.get("campusId"), fromDate ,fDate.plusDays(9));
			for(int i=0; i<10; i++) {
				EmpRosterAllotmentDTO empRosterAllotmentDTO = new EmpRosterAllotmentDTO();
				if(i == 0) {
					 empRosterAllotmentDTO.day = fDate.getDayOfWeek().toString();
					empRosterAllotmentDTO.rosterDate = formattedDate;
				}else {					
					LocalDate dtOrg = fDate;
					LocalDate dtPlusOne = dtOrg.plusDays(1);
					fDate = dtPlusOne;
					 empRosterAllotmentDTO.day = dtPlusOne.getDayOfWeek().toString();
					empRosterAllotmentDTO.rosterDate =  Utils.convertLocalDateToStringDate(dtPlusOne);
				}
				if(i > (int) daysDiff) {
					empRosterAllotmentDTO.disabled = true;
				}else {
					empRosterAllotmentDTO.disabled = false;
				}
				weekList.add(empRosterAllotmentDTO);
			}

			if(!Utils.isNullOrEmpty(empList)) {
				Map<Integer, Map<String, Tuple3<String, String, String>>> empRosterMap = new HashMap<>();
				Map<Integer, String> empNameMap = new HashMap<>();
				for(Tuple tuple : empList) {
					if(!Utils.isNullOrEmpty(tuple.get("empId")) && empRosterMap.containsKey(Integer.parseInt(tuple.get("empId").toString()))) {
						if(!empNameMap.containsKey(Integer.parseInt(tuple.get("empId").toString())))
							empNameMap.put(Integer.parseInt(tuple.get("empId").toString()), tuple.get("empName").toString()+" ("+tuple.get("empId").toString()+")");
						Map<String, Tuple3<String, String, String>> rosterDateMap = empRosterMap.get(Integer.parseInt(tuple.get("empId").toString()));
						if(!rosterDateMap.containsKey(tuple.get("rosterDate").toString())) {
							Tuple3<String, String, String> empDetailsTuple = Tuples.of(tuple.get("empName").toString(), tuple.get("empShiftTypesId").toString(),tuple.get("empRosterAllotmentId").toString());
							rosterDateMap.put(tuple.get("rosterDate").toString(), empDetailsTuple);
							rosterDateMap = sortMapByKey(rosterDateMap);
							empRosterMap.put(Integer.parseInt(tuple.get("empId").toString()), rosterDateMap);
						}
					}else {
						if(!empNameMap.containsKey(Integer.parseInt(tuple.get("empId").toString())))
							empNameMap.put(Integer.parseInt(tuple.get("empId").toString()), tuple.get("empName").toString()+" ("+tuple.get("empId").toString()+")");
						Map<String, Tuple3<String, String, String>> rosterDateMap = new HashMap<>();
						if(!Utils.isNullOrEmpty(tuple.get("rosterDate"))) {
							Tuple3<String, String, String> empDetailsTuple = Tuples.of(tuple.get("empName").toString(), tuple.get("empShiftTypesId").toString(),tuple.get("empRosterAllotmentId").toString());
							rosterDateMap.put(tuple.get("rosterDate").toString(), empDetailsTuple);
							rosterDateMap = sortMapByKey(rosterDateMap);
						}
						empRosterMap.put(Integer.parseInt(tuple.get("empId").toString()), rosterDateMap);
					}
				}
  
				if(!Utils.isNullOrEmpty(empRosterMap)) {
					for(Entry<Integer, Map<String, Tuple3<String, String, String>>> entry : empRosterMap.entrySet()) {
						LocalDate rosterDt = fromDate;
						Map<LocalDate,EmpShiftTypesDTO> existData = new HashMap<LocalDate, EmpShiftTypesDTO>();
						EmpRosterAllotmentDTO empRosterAllotmentDTO = new EmpRosterAllotmentDTO();
						empRosterAllotmentDTO.empId = entry.getKey();
						empRosterAllotmentDTO.empName = empNameMap.get(entry.getKey());
						Map<String, Tuple3<String, String, String>> rosterDateMap = entry.getValue();
						Integer count = 0;
						List<EmpShiftTypesDTO> shyptTypeList = new ArrayList<>();
						if(!Utils.isNullOrEmpty(rosterDateMap)) {
							for(Entry<String , Tuple3<String, String, String>> dateEntry : rosterDateMap.entrySet()) {
								//Date rosterDate = rosterDateFormat.parse(dateEntry.getKey());
								LocalDate rosterDate = Utils.convertStringDateToLocalDate(dateEntry.getKey());           
								Tuple3<String, String, String> empDetails = dateEntry.getValue();
								EmpShiftTypesDTO empShiftTypesDTO = new EmpShiftTypesDTO();
								if(!Utils.isNullOrEmpty(empDetails.getT2()))
									empShiftTypesDTO.id = empDetails.getT2();
								if(!Utils.isNullOrEmpty(empDetails.getT3()))
									empShiftTypesDTO.empRosterAllotmentId = Integer.parseInt(empDetails.getT3());
								//	DateTime rDate = new DateTime(rosterDate);
								//	LocalDate rDate = rosterDate;
								//	empShiftTypesDTO.rosterDate = dtfOut.print(rDate);
								LocalDate rDate = rosterDate;
								empShiftTypesDTO.rosterDate = Utils.convertLocalDateToStringDate(rDate);
								//	empShiftTypesDTO.rosterDate = rDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
								if(rDate.isAfter(fromDate) && rDate.isBefore(toDate) || rDate.equals(fromDate) || rDate.equals(toDate)) {
//								if(count > (int) daysDiff) {
									empShiftTypesDTO.disabled = false;
								}else {
									empShiftTypesDTO.disabled = true;
								}
//								shyptTypeList.add(empShiftTypesDTO);
								existData.put(rDate, empShiftTypesDTO);
								//								rosterDt = rosterDate;
								count++;
							}
						}
						for(int i=0; i<10; i++) {
							EmpShiftTypesDTO empShiftTypesDTO = null;
							//	DateTime dtOrg = new DateTime(rosterDt);
							//	DateTime dtPlusOne = dtOrg.plusDays(1);
							//		rosterDt = dtPlusOne.toDate();
							LocalDate dtOrg = rosterDt;
							if(existData.containsKey(dtOrg)) {
								empShiftTypesDTO = existData.get(dtOrg);
							} else {
								empShiftTypesDTO = new EmpShiftTypesDTO();
								if(i == 0) {
									//					empShiftTypesDTO.rosterDate = dtfOut.print(dtOrg);
									empShiftTypesDTO.rosterDate = Utils.convertLocalDateToStringDate(dtOrg);
								}else {
									//					empShiftTypesDTO.rosterDate = dtfOut.print(dtPlusOne);
									empShiftTypesDTO.rosterDate = Utils.convertLocalDateToStringDate(dtOrg);
								}
								if(dtOrg.isAfter(fromDate) && dtOrg.isBefore(toDate) || dtOrg.equals(fromDate) || dtOrg.equals(toDate)) {
//								if(i > (int) daysDiff) {
									empShiftTypesDTO.disabled = false;
								}else {
									empShiftTypesDTO.disabled = true;
								}
							}
							shyptTypeList.add(empShiftTypesDTO);
							LocalDate dtPlusOne = dtOrg.plusDays(1);
							rosterDt = dtPlusOne;

						}
						empRosterAllotmentDTO.shiftTypeList = shyptTypeList;
						rosterAllotmentList.add(empRosterAllotmentDTO);
					}
				}
			}
			rosterDTO.weekList = weekList;
			rosterDTO.dutyRosterAllotmentList = rosterAllotmentList;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rosterDTO;
	}

	public boolean saveOrUpdate(DutyRosterAllotmentDTO data, String userId) throws Exception{
		if(!Utils.isNullOrEmpty(data)) {
			if(!Utils.isNullOrEmpty(data.dutyRosterAllotmentList)) {
				List<EmpRosterAllotmentDBO> dboList = new ArrayList<>();
				for(EmpRosterAllotmentDTO dto : data.dutyRosterAllotmentList) {
					if(!dto.disabled && !Utils.isNullOrEmpty(dto.shiftTypeList)) {
						for(EmpShiftTypesDTO shiftDTO : dto.shiftTypeList) {
							if(!shiftDTO.disabled) {
								EmpRosterAllotmentDBO dbo = new EmpRosterAllotmentDBO();
								if(!Utils.isNullOrEmpty(shiftDTO.empRosterAllotmentId)) {
									dbo.id = shiftDTO.empRosterAllotmentId;
									dbo.modifiedUsersId = Integer.parseInt(userId);
								}else {
									dbo.createdUsersId = Integer.parseInt(userId);
								}
								dbo.empDBO = new EmpDBO();
								dbo.empDBO.id = dto.empId;
								dbo.empShiftTypeDBO = new EmpShiftTypesDBO();
								if(!Utils.isNullOrEmpty(shiftDTO.id))
									dbo.empShiftTypeDBO.id = Integer.parseInt(shiftDTO.id);
								dbo.rosterDate = Utils.convertStringDateToLocalDate(shiftDTO.rosterDate);
								dbo.recordStatus='A';
								dboList.add(dbo);
							}
						}
					}
				}
				return !Utils.isNullOrEmpty(dboList) ? dutyRosterAllotmentTransaction.saveOrUpdate(dboList) : false;
			}
		}
		return false;
	}

	public Map<String, Tuple3<String, String, String>> sortMapByKey(Map<String, Tuple3<String, String, String>> map) {
		return map
				.entrySet() 
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(
						toMap(Map.Entry::getKey, Map.Entry::getValue,
								(e1, e2) -> e2, LinkedHashMap::new));
	}
}
