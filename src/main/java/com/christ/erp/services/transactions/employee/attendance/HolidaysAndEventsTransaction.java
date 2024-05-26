package com.christ.erp.services.transactions.employee.attendance;
import com.christ.erp.services.common.*;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsCddMapDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsEmployeewiseDBO;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Set;

@Repository
public class HolidaysAndEventsTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<EmpHolidayEventsDBO> getGridData(Integer academicYearId) {
		String str = " Select bo from EmpHolidayEventsDBO bo where bo.recordStatus='A' and bo.erpAcademicYearId.id =:academicYearId order by bo.holidayEventsStartDate DESC ";
		List<EmpHolidayEventsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpHolidayEventsDBO> query = s.createQuery(str,EmpHolidayEventsDBO.class);
			if(!Utils.isNullOrEmpty(academicYearId))
				query.setParameter("academicYearId",  academicYearId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Tuple edit(Integer id) {
		String str = " select emp_holiday_events.emp_holiday_events_id as id, emp_holiday_events.emp_holiday_events_type_name empHolidayEventTypeName, " +
				"erp_academic_year.erp_academic_year_id holidayAcademicYearId,erp_academic_year.academic_year_name academicYearName, " +
				"emp_holiday_events.is_employeewise_exemption isEmployeeWiseException,emp_holiday_events.holiday_events_start_date holiayEventStartDate, " +
				"emp_holiday_events.holiday_events_end_date holidayEventEndDate,emp_holiday_events.holiday_events_session holidayEventsSession,emp_holiday_events.is_one_time_signing isOneTimeSign, " +
				"emp_holiday_events.is_exemption isException,emp_holiday_events.holiday_events_description holidayEventDescription, " +
				"erp_location.erp_location_id locationId,erp_location.location_name locationName, " +
				"emp_employee_category.emp_employee_category_id empCategoryId,emp_employee_category.employee_category_name employeCategoryName " +
				"from emp_holiday_events " +
				"left join erp_location ON erp_location.erp_location_id = emp_holiday_events.erp_location_id  " +
				"left join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_holiday_events.erp_academic_year_id " +
				"left join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp_holiday_events.emp_employee_category_id " +
				"where emp_holiday_events.emp_holiday_events_id =:id and emp_holiday_events.record_status = 'A'";
		Tuple list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			if(!Utils.isNullOrEmpty(id))
				query.setParameter("id",  id);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getEmployeeIdByLoc(int holidayEventId,int locationId) {
		String str = " select emp_holiday_events_employeewise.emp_holiday_events_employeewise_id emplloyeeWiseId, " +
				"emp.emp_id empId,emp.emp_name empName " +
				"from emp_holiday_events_employeewise " +
				"inner join emp_holiday_events ON emp_holiday_events.emp_holiday_events_id = emp_holiday_events_employeewise.emp_holiday_events_id and emp_holiday_events.record_status ='A' " +
				"inner join emp ON emp.emp_id = emp_holiday_events_employeewise.emp_id and emp.record_status ='A' " +
				"inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id  " +
				"inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
				"inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id " +
				"where emp_holiday_events_employeewise.record_status = 'A' and emp_holiday_events.emp_holiday_events_id =:holidayEventId " +
				"and erp_location.erp_location_id =:locationId";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			if(!Utils.isNullOrEmpty(holidayEventId))
				query.setParameter("holidayEventId",  holidayEventId);
			if(!Utils.isNullOrEmpty(locationId))
				query.setParameter("locationId",locationId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getEmployeeIds(int locationId) {
		String str = " Select emp.emp_id empId,emp.emp_name empName " +
				"from emp " +
				"inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id  " +
				"inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
				"inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id  " +
				"where erp_location.erp_location_id =:locationId AND emp.record_status='A' ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			if(!Utils.isNullOrEmpty(locationId))
				query.setParameter("locationId",locationId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getCddList(Integer empHolidayAndEventId){
		String str = " Select erp_campus_department_mapping.erp_campus_department_mapping_id as campusDepartmentId,erp_campus.erp_campus_id campusId, " +
				"erp_department.erp_department_id departmentId,erp_deanery.erp_deanery_id deanaryId " +
				"from emp_holiday_events_cd_map " +
				"inner join emp_holiday_events ON emp_holiday_events.emp_holiday_events_id = emp_holiday_events_cd_map.emp_holiday_events_id and emp_holiday_events_cd_map.record_status = 'A' " +
				"inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp_holiday_events_cd_map.erp_campus_department_mapping_id " +
				"inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
				"inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id " +
				"inner join erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id " +
				"where emp_holiday_events_cd_map.record_status = 'A' and emp_holiday_events.emp_holiday_events_id =:empHolidayAndEventId ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			if(!Utils.isNullOrEmpty(empHolidayAndEventId))
				query.setParameter("empHolidayAndEventId",empHolidayAndEventId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpHolidayEventsDBO.class, id)
				.chain(dbo1 -> session.fetch(dbo1.getEmpHolidayEventsCddMapDBOSet())
						.invoke(subSet -> {
							subSet.forEach(subDbo -> {
								subDbo.setRecordStatus('D');
								subDbo.setModifiedUsersId(userId);
							});
						})
						.chain(dbo2 -> session.fetch(dbo1.getEmpHolidayEventsEmployeewiseDBOSet()))
						.invoke(subSet2 -> {
							subSet2.forEach(subDbo2 -> {
								subDbo2.setRecordStatus('D');
								subDbo2.setModifiedUsersId(userId);
							});
							dbo1.setRecordStatus('D');
							dbo1.setModifiedUsersId(userId);
							if(!Utils.isNullOrEmpty(dbo1.getEmpHolidayEventsTypeName())){
								if(!dbo1.getEmpHolidayEventsTypeName().trim().equalsIgnoreCase("Restricted holiday")){
									dbo1.setSchedulerStatus("pending");
								}
							}
						})
				)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public List<EmpHolidayEventsDBO> getDuplicate(HolidaysAndEventsEntryDTO holidaysAndEventsEntryDTO) {
		String str = "select bo from EmpHolidayEventsDBO bo where bo.erpAcademicYearId.id =:yearId " + //and bo.empHolidayEventsTypeName =:type  " +
				"  and bo.erpLocationId.id=:locId  and   bo.holidayEventsStartDate =:date and bo.recordStatus='A'";
		if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId())) {
			str += " and bo.id !=: id";
		}
		String finalStr = str;
		List<EmpHolidayEventsDBO> list = sessionFactory.withSession(s-> { Mutiny.Query<EmpHolidayEventsDBO> finalquery = s.createQuery(finalStr,EmpHolidayEventsDBO.class);
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getAcademicYear().id))
				finalquery.setParameter("yearId", Integer.parseInt(holidaysAndEventsEntryDTO.getAcademicYear().id));
			String types = null;
//			if(holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("1"))
//				types = "Holiday";
//			else
//				types = "Restricted holiday";
//			if(!Utils.isNullOrEmpty(types))
//				finalquery.setParameter("type", types.trim());
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getLocation().id))
				finalquery.setParameter("locId", Integer.parseInt(holidaysAndEventsEntryDTO.getLocation().id));
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getDate()))
				finalquery.setParameter("date", holidaysAndEventsEntryDTO.getDate());
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId()))
				finalquery.setParameter("id", Integer.parseInt(holidaysAndEventsEntryDTO.getId()));
			return finalquery.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpHolidayEventsDBO> getDuplicateforEmployeeWiseNo(HolidaysAndEventsEntryDTO holidaysAndEventsEntryDTO,Set<Integer> campusDeanearyDeptIds) {
		String str = "SELECT bo  " +
				" FROM EmpHolidayEventsDBO bo " +
				" LEFT JOIN FETCH bo.empHolidayEventsCddMapDBOSet cddMap " +
				" LEFT JOIN FETCH cddMap.erpCampusDeaneryDeptId campusDep " +
				" WHERE bo.erpAcademicYearId.id = :yearId " +
				" AND bo.empHolidayEventsTypeName = :type " +
				" AND bo.holidayEventsStartDate <= :endDate " +
				" AND bo.holidayEventsEndDate >= :startDate " +
				" AND bo.recordStatus = 'A' " +
				" AND campusDep.id IN (:campusDeaneryDeptIds)";
		if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId())) {
			str += " and bo.id !=: id";
		}
		String finalStr = str;
		List<EmpHolidayEventsDBO> list = sessionFactory.withSession(s-> { Mutiny.Query<EmpHolidayEventsDBO> finalquery = s.createQuery(finalStr,EmpHolidayEventsDBO.class);
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getAcademicYear().id)) {
				finalquery.setParameter("yearId", Integer.parseInt(holidaysAndEventsEntryDTO.getAcademicYear().id));
			}
			String types = null;
			if(holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("3")) {
				types = "Event";
			}
			else {
				types = "Vacation";
			}
			if(!Utils.isNullOrEmpty(types)) {
				finalquery.setParameter("type", types);
			}
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getStartDate())) {
				finalquery.setParameter("startDate", holidaysAndEventsEntryDTO.getStartDate());
			}
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getEndDate())) {
				finalquery.setParameter("endDate", holidaysAndEventsEntryDTO.getEndDate());
			}
			if(!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId()))
				finalquery.setParameter("id", Integer.parseInt(holidaysAndEventsEntryDTO.getId()));
			if(!Utils.isNullOrEmpty(campusDeanearyDeptIds))
				finalquery.setParameter("campusDeaneryDeptIds", campusDeanearyDeptIds);
			return finalquery.getResultList();
		}).await().indefinitely();
		return list;
	}

	public void update(EmpHolidayEventsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpHolidayEventsDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(EmpHolidayEventsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}

	public EmpHolidayEventsDBO searchById(String id){
		return sessionFactory.withSession(s -> s.createQuery("Select dbo from EmpHolidayEventsDBO dbo where dbo.recordStatus ='A' and dbo.id =:id", EmpHolidayEventsDBO.class).setParameter("id", Integer.parseInt(id)).getSingleResultOrNull()).await().indefinitely();
	}

	public List<EmpHolidayEventsEmployeewiseDBO> getEmpHolidayEventsEmployeewiseDBO(Integer id) {
		String str = "select distinct dbo FROM EmpHolidayEventsEmployeewiseDBO dbo " +
				" left join fetch dbo.empDBO " +
				" WHERE dbo.recordStatus='A' and dbo.empHolidayEventsDBO.id=:id";
		List<EmpHolidayEventsEmployeewiseDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpHolidayEventsEmployeewiseDBO> query = s.createQuery(str, EmpHolidayEventsEmployeewiseDBO.class);
			if(!Utils.isNullOrEmpty(id))
				query.setParameter("id",id);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpHolidayEventsCddMapDBO> getEmpHolidayEventsCddMap(Integer id) {
		String str = "select dbo from EmpHolidayEventsCddMapDBO dbo where dbo.recordStatus='A' and dbo.empHolidayEventsId.id=:id";
		List<EmpHolidayEventsCddMapDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpHolidayEventsCddMapDBO> query = s.createQuery(str, EmpHolidayEventsCddMapDBO.class);
			if(!Utils.isNullOrEmpty(id))
				query.setParameter("id",id);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getEmpDetails(HolidaysAndEventsEntryDTO data, Set<Integer> campusDeanearyDeptIds) {
		String str = " select emp.emp_id empId,emp.emp_name empName from emp ";
		if(!Utils.isNullOrEmpty(data.getLocation()) || !Utils.isNullOrEmpty(campusDeanearyDeptIds)){
			if(!Utils.isNullOrEmpty(data.getLocation().id))
				str += " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " +
						" inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
						" inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id ";
		}
		if(!Utils.isNullOrEmpty(data.getEmpCategory())){
			if(!Utils.isNullOrEmpty(data.getEmpCategory().id))
				str += " inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id ";
		}
		str += " where emp.record_status = 'A'  ";
		if(!Utils.isNullOrEmpty(data.getLocation())){
			if(!Utils.isNullOrEmpty(data.getLocation().id))
				str += " and erp_location.erp_location_id =:locationId ";
		}
		if(!Utils.isNullOrEmpty(data.getEmpCategory())){
			if(!Utils.isNullOrEmpty(data.getEmpCategory().id))
				str += " and emp_employee_category.emp_employee_category_id =:empCategoryId ";
		}
		if(!Utils.isNullOrEmpty(campusDeanearyDeptIds))
			str += " and erp_campus_department_mapping.erp_campus_department_mapping_id IN (:campusDepartmentId) ";
		str += " ORDER BY emp.emp_name ";
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			if(!Utils.isNullOrEmpty(data.getLocation())){
				if(!Utils.isNullOrEmpty(data.getLocation().id))
					query.setParameter("locationId",Integer.parseInt(data.getLocation().id));
			}
			if(!Utils.isNullOrEmpty(data.getEmpCategory())){
				if(!Utils.isNullOrEmpty(data.getEmpCategory().id))
					query.setParameter("empCategoryId", Integer.parseInt(data.getEmpCategory().id));
			}
			if(!Utils.isNullOrEmpty(campusDeanearyDeptIds))
				query.setParameter("campusDepartmentId",campusDeanearyDeptIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpHolidayEventsDBO> getDuplicateforEmployeeWiseYes(HolidaysAndEventsEntryDTO holidaysAndEventsEntryDTO, List<Integer> empList) {
		String str = "SELECT bo  " +
				" FROM EmpHolidayEventsDBO bo " +
				" LEFT JOIN FETCH bo.empHolidayEventsEmployeewiseDBOSet empSet " +
				" LEFT JOIN FETCH empSet.empDBO emp " +
				" WHERE bo.erpAcademicYearId.id = :yearId " +
				" AND bo.empHolidayEventsTypeName = :type " +
				" AND bo.holidayEventsStartDate <= :endDate " +
				" AND bo.holidayEventsEndDate >= :startDate " +
				" AND bo.recordStatus = 'A' " +
				" AND emp.id IN (:empList)";
		if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId())) {
			str += " and bo.id !=: id";
		}
		String finalStr = str;
		List<EmpHolidayEventsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpHolidayEventsDBO> finalquery = s.createQuery(finalStr, EmpHolidayEventsDBO.class);
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getAcademicYear().id)) {
				finalquery.setParameter("yearId", Integer.parseInt(holidaysAndEventsEntryDTO.getAcademicYear().id));
			}
			String types = null;
			if (holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("3")) {
				types = "Event";
			} else {
				types = "Vacation";
			}
			if (!Utils.isNullOrEmpty(types)) {
				finalquery.setParameter("type", types);
			}
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getStartDate())) {
				finalquery.setParameter("startDate", holidaysAndEventsEntryDTO.getStartDate());
			}
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getEndDate())) {
				finalquery.setParameter("endDate", holidaysAndEventsEntryDTO.getEndDate());
			}
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId()))
				finalquery.setParameter("id", Integer.parseInt(holidaysAndEventsEntryDTO.getId()));
			if (!Utils.isNullOrEmpty(empList))
				finalquery.setParameter("empList", empList);
			return finalquery.getResultList();
		}).await().indefinitely();
		return list;

	}

	public List<Integer> getEmpCampusDepartmentMappingList(Set<Integer> empList) {
		String str = " select erp_campus_department_mapping.erp_campus_department_mapping_id as dept " +
				"from emp " +
				"inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " +
				"where emp.emp_id IN (:empList) ";
		List<Integer> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Integer> finalquery = s.createNativeQuery(str, Integer.class);
			if (!Utils.isNullOrEmpty(empList))
				finalquery.setParameter("empList", empList);
			return finalquery.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpHolidayEventsDBO> getDuplicateForEmployee(HolidaysAndEventsEntryDTO holidaysAndEventsEntryDTO, List<Integer> campusDeptIds) {
		String str = "SELECT bo  " +
				" FROM EmpHolidayEventsDBO bo " +
				" LEFT JOIN FETCH bo.empHolidayEventsCddMapDBOSet cddMap " +
				" LEFT JOIN FETCH cddMap.erpCampusDeaneryDeptId campusDep " +
				" WHERE bo.erpAcademicYearId.id = :yearId " +
				" AND bo.empHolidayEventsTypeName = :type " +
				" AND bo.holidayEventsStartDate <= :endDate " +
				" AND bo.holidayEventsEndDate >= :startDate " +
				" AND bo.recordStatus = 'A' " +
				" AND campusDep.id IN (:campusDeptIds) AND bo.isEmployeewiseExemption = 0";
		if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId())) {
			str += " and bo.id !=: id";
		}
		String finalStr = str;
		List<EmpHolidayEventsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpHolidayEventsDBO> finalquery = s.createQuery(finalStr, EmpHolidayEventsDBO.class);
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getAcademicYear().id)) {
				finalquery.setParameter("yearId", Integer.parseInt(holidaysAndEventsEntryDTO.getAcademicYear().id));
			}
			String types = null;
			if (holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("3")) {
				types = "Event";
			} else {
				types = "Vacation";
			}
			if (!Utils.isNullOrEmpty(types)) {
				finalquery.setParameter("type", types);
			}
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getStartDate())) {
				finalquery.setParameter("startDate", holidaysAndEventsEntryDTO.getStartDate());
			}
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getEndDate())) {
				finalquery.setParameter("endDate", holidaysAndEventsEntryDTO.getEndDate());
			}
			if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getId()))
				finalquery.setParameter("id", Integer.parseInt(holidaysAndEventsEntryDTO.getId()));
			if (!Utils.isNullOrEmpty(campusDeptIds))
				finalquery.setParameter("campusDeptIds", campusDeptIds);
			return finalquery.getResultList();
		}).await().indefinitely();
		return list;
	}
}
