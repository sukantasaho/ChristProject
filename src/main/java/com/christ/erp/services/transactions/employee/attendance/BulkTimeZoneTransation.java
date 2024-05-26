package com.christ.erp.services.transactions.employee.attendance;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpDatewiseTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.employee.attendance.EmpDatewiseTimeZoneDTO;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class BulkTimeZoneTransation {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public List<EmpDatewiseTimeZoneDTO> getData() {
        String str = "select new com.christ.erp.services.dto.employee.attendance.EmpDatewiseTimeZoneDTO " +
                "(dbo.id,dbo.description, dbo.timeZoneStartDate, dbo.timeZoneEndDate, " +
                "dbo.empTimeZoneDBO.isGeneralTimeZone, dbo.empTimeZoneDBO.isHolidayTimeZone, " +
                "dbo.empTimeZoneDBO.id, dbo.empTimeZoneDBO.timeZoneName, " +
                "dbo.empDBO.id, dbo.empDBO.empName, " +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO.id, " +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO.locationName," +
                "dbo.empDBO.empEmployeeCategoryDBO.id," +
                "dbo.empDBO.empEmployeeCategoryDBO.employeeCategoryName," +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.id," +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.id, " +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id," +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO.erpDeaneryDBO.id) " +
                "from EmpDatewiseTimeZoneDBO dbo " +
                "inner join dbo.empTimeZoneDBO " +
                "inner join dbo.empDBO " +
                "inner join dbo.empDBO.empEmployeeCategoryDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO.erpDeaneryDBO " +
                "where dbo.recordStatus = 'A'";
        return sessionFactory.withSession(s -> s.createQuery(str, EmpDatewiseTimeZoneDTO.class).getResultList()).await().indefinitely();
    }

    public EmpDatewiseTimeZoneDTO edit(int id) {
        String str = "select new com.christ.erp.services.dto.employee.attendance.EmpDatewiseTimeZoneDTO " +
                "(dbo.id,dbo.description, dbo.timeZoneStartDate, dbo.timeZoneEndDate, " +
                "dbo.empTimeZoneDBO.isGeneralTimeZone, dbo.empTimeZoneDBO.isHolidayTimeZone, " +
                "dbo.empTimeZoneDBO.id, dbo.empTimeZoneDBO.timeZoneName, " +
                "dbo.empDBO.id, dbo.empDBO.empName, " +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO.id, " +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO.locationName," +
                "dbo.empDBO.empEmployeeCategoryDBO.id," +
                "dbo.empDBO.empEmployeeCategoryDBO.employeeCategoryName," +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.id," +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.id, " +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id," +
                "dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO.erpDeaneryDBO.id) " +
                "from EmpDatewiseTimeZoneDBO dbo " +
                "inner join dbo.empTimeZoneDBO " +
                "inner join dbo.empDBO " +
                "inner join dbo.empDBO.empEmployeeCategoryDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO " +
                "inner join dbo.empDBO.erpCampusDepartmentMappingDBO.erpDepartmentDBO.erpDeaneryDBO " +
                "where dbo.recordStatus = 'A' and dbo.id =: id";
        return sessionFactory.withSession(s -> s.createQuery(str, EmpDatewiseTimeZoneDTO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
    }

    public EmpDatewiseTimeZoneDBO existData(int id) {
        String str = " select dbo from EmpDatewiseTimeZoneDBO dbo" +
                " left join fetch dbo.empDBO " +
                " where dbo.recordStatus = 'A' and dbo.id =: id ";
        EmpDatewiseTimeZoneDBO empDatewiseTimeZoneDBO = sessionFactory.withSession(s -> s.createQuery(str, EmpDatewiseTimeZoneDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
        return empDatewiseTimeZoneDBO;
    }

    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(EmpDatewiseTimeZoneDBO.class, id).invoke(dbo -> {
            dbo.setModifiedUsersId(userId);
            dbo.setRecordStatus('D');
        })).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public boolean update(List<EmpDatewiseTimeZoneDBO> dboList) {
        sessionFactory.withTransaction((session, tx) -> session.mergeAll(dboList.toArray())).await().indefinitely();
        return true;
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

    public List<EmpDatewiseTimeZoneDBO> duplicateCheck(EmpDatewiseTimeZoneDTO empDatewiseTimeZoneDTO, List<Integer> empCampusDept, List<Integer> empList) {
        String str = " Select dbo from EmpDatewiseTimeZoneDBO dbo " +
                " left join fetch dbo.empDBO emp " +
                " left join dbo.empDBO.erpCampusDepartmentMappingDBO camDept " +
                " where dbo.recordStatus = 'A' and " +
                " emp.id IN (:empList) and camDept.id IN (:empCampusDept) ";
        if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getId()))
            str += "and dbo.id !=: id ";
        String finalStr = str;
        List<EmpDatewiseTimeZoneDBO> list = sessionFactory.withSession(s -> {
            Mutiny.Query<EmpDatewiseTimeZoneDBO> finalquery = s.createQuery(finalStr, EmpDatewiseTimeZoneDBO.class);
            if (!Utils.isNullOrEmpty(empList))
                finalquery.setParameter("empList", empList);
            if (!Utils.isNullOrEmpty(empCampusDept))
                finalquery.setParameter("empCampusDept", empCampusDept);
            if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getId()))
                finalquery.setParameter("id", empDatewiseTimeZoneDTO.getId());
            return finalquery.getResultList();
        }).await().indefinitely();
        return list;
    }

    public List<Tuple> getEmpDetails(EmpDatewiseTimeZoneDTO data, Set<Integer> campusDeanearyDeptIds) {
        String str = " select emp.emp_id empId,emp.emp_name empName from emp ";
        if (!Utils.isNullOrEmpty(data.getLocationSelect()) || !Utils.isNullOrEmpty(campusDeanearyDeptIds)) {
            if (!Utils.isNullOrEmpty(data.getLocationSelect().getValue()))
                str += " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " +
                        " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
                        " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id ";
        }
        if (!Utils.isNullOrEmpty(data.getEmpCategorySelect())) {
            if (!Utils.isNullOrEmpty(data.getEmpCategorySelect().getValue()))
                str += " inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp.emp_employee_category_id ";
        }
        str += " where emp.record_status = 'A'  ";
        if (!Utils.isNullOrEmpty(data.getLocationSelect())) {
            if (!Utils.isNullOrEmpty(data.getLocationSelect().getValue()))
                str += " and erp_location.erp_location_id =:locationId ";
        }
        if (!Utils.isNullOrEmpty(data.getEmpCategorySelect())) {
            if (!Utils.isNullOrEmpty(data.getEmpCategorySelect().getValue()))
                str += " and emp_employee_category.emp_employee_category_id =:empCategoryId ";
        }
        if (!Utils.isNullOrEmpty(campusDeanearyDeptIds))
            str += " and erp_campus_department_mapping.erp_campus_department_mapping_id IN (:campusDepartmentId) ";
        str += " ORDER BY empName ASC ";
        String finalStr = str;
        List<Tuple> list = sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
            if (!Utils.isNullOrEmpty(data.getLocationSelect())) {
                if (!Utils.isNullOrEmpty(data.getLocationSelect().getValue()))
                    query.setParameter("locationId", Integer.parseInt(data.getLocationSelect().getValue()));
            }
            if (!Utils.isNullOrEmpty(data.getEmpCategorySelect())) {
                if (!Utils.isNullOrEmpty(data.getEmpCategorySelect().getValue()))
                    query.setParameter("empCategoryId", Integer.parseInt(data.getEmpCategorySelect().getValue()));
            }
            if (!Utils.isNullOrEmpty(campusDeanearyDeptIds))
                query.setParameter("campusDepartmentId", campusDeanearyDeptIds);
            return query.getResultList();
        }).await().indefinitely();
        return list;
    }

    public List<EmpDatewiseTimeZoneDBO> duplicateCheck1(EmpDatewiseTimeZoneDTO empDatewiseTimeZoneDTO, List<Integer> empList) {
        String str = " Select dbo from EmpDatewiseTimeZoneDBO dbo " +
                " left join fetch dbo.empDBO emp " +
                " where dbo.recordStatus = 'A' and " +
                " emp.id IN (:empList) and dbo.timeZoneStartDate >= :startDate AND dbo.timeZoneEndDate <= :endtDate ";
        if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getId()))
            str += "and dbo.id !=: id ";
        String finalStr = str;
        List<EmpDatewiseTimeZoneDBO> list = sessionFactory.withSession(s -> {
            Mutiny.Query<EmpDatewiseTimeZoneDBO> finalquery = s.createQuery(finalStr, EmpDatewiseTimeZoneDBO.class);
            if (!Utils.isNullOrEmpty(empList))
                finalquery.setParameter("empList", empList);
            if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getTimeZoneStartDate()))
                finalquery.setParameter("startDate", empDatewiseTimeZoneDTO.getTimeZoneStartDate());
            if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getTimeZoneEndDate()))
                finalquery.setParameter("endtDate", empDatewiseTimeZoneDTO.getTimeZoneEndDate());
            if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getId()))
                finalquery.setParameter("id", empDatewiseTimeZoneDTO.getId());
            return finalquery.getResultList();
        }).await().indefinitely();
        return list;
    }

    public List<Integer> getEmployee(List<Integer> empCampusDept) {
        String str = " select DISTINCT emp.emp_id " +
                "from emp " +
                "inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id  " +
                "where erp_campus_department_mapping.erp_campus_department_mapping_id IN (:empCampusDept) and emp.record_status = 'A' ";
        List<Integer> list = sessionFactory.withSession(s -> {
            Mutiny.Query<Integer> finalquery = s.createNativeQuery(str, Integer.class);
            if (!Utils.isNullOrEmpty(empCampusDept))
                finalquery.setParameter("empCampusDept", empCampusDept);
            return finalquery.getResultList();
        }).await().indefinitely();
        return list;
    }

    public List<EmpDBO> getEmp(List<Integer> empList) {
        String str = "from EmpDBO dbo " +
                "left join fetch dbo.empJobDetailsDBO " +
                " where dbo.recordStatus = 'A' and dbo.id IN (:empList)";
        List<EmpDBO> list = sessionFactory.withSession(s -> {
            Mutiny.Query<EmpDBO> finalquery = s.createQuery(str, EmpDBO.class);
            if (!Utils.isNullOrEmpty(empList))
                finalquery.setParameter("empList", empList);
            return finalquery.getResultList();
        }).await().indefinitely();
        return list;
    }

    public boolean updateEmp(List<EmpDBO> empDBOS) {
        sessionFactory.withTransaction((session, tx) -> session.mergeAll(empDBOS.toArray())).await().indefinitely();
        return true;
    }
}