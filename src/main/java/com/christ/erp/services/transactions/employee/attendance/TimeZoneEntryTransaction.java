package com.christ.erp.services.transactions.employee.attendance;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dto.employee.attendance.EmpTimeZoneDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

@Repository
public class TimeZoneEntryTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public List<Tuple> getGridData() {
        String str = " select emp_time_zone.emp_time_zone_id as ID," +
                " emp_time_zone.time_zone_name as TimeZoneName," +
                " emp_time_zone.is_holiday_time_zone as isholidaytimezone," +
                " emp_time_zone.is_general_time_zone as isgeneraltimezone," +
                " emp_employee_category.emp_employee_category_id as empEmployeeCategoryId," +
                " emp_employee_category.employee_category_name as employeeCategoryName " +
                " from emp_time_zone" +
                " inner join emp_employee_category on emp_time_zone.emp_employee_category_id = emp_employee_category.emp_employee_category_id" +
                " where emp_time_zone.record_status='A'";
        List<Tuple> list = sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
            return query.getResultList();
        }).await().indefinitely();
        return list;
    }

    public EmpTimeZoneDBO edit(int id) {
        String str = " Select dbo From EmpTimeZoneDBO dbo " +
                " left join fetch dbo.empTimeZoneDetailsDBOSet " +
                " Where dbo.recordStatus = 'A' And  dbo.id =:id ";
        EmpTimeZoneDBO empTimeZoneDBO = sessionFactory.withSession(s -> s.createQuery(str, EmpTimeZoneDBO.class).setParameter("id",id).getSingleResultOrNull()).await().indefinitely();
        return empTimeZoneDBO;
    }

    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(EmpTimeZoneDBO.class, id)
                .chain(dbo1 -> session.fetch(dbo1.getEmpTimeZoneDetailsDBOSet())
                        .invoke(subSet -> {
                            subSet.forEach(subDbo -> {
                                subDbo.setRecordStatus('D');
                                subDbo.setModifiedUsersId(userId);
                            });
                            dbo1.setRecordStatus('D');
                            dbo1.setModifiedUsersId(userId);
                        })
                )).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public boolean duplicateCheck(EmpTimeZoneDTO empTimeZoneDTO) {
        String str = "from  EmpTimeZoneDBO  bo where (replace(bo.timeZoneName,' ','' ) =:timeZoneName or bo.timeZoneName=:timeZoneName) and bo.recordStatus='A' and bo.empEmployeeCategoryDBO.id=:empCategoryId";
        if (!Utils.isNullOrEmpty(empTimeZoneDTO.getId())) {
            str += " and bo.id !=: id";
        }
        if(!Utils.isNullOrEmpty(empTimeZoneDTO.getSelected())){
            if(empTimeZoneDTO.getSelected().trim().equalsIgnoreCase("holiday")){
                str += " and bo.isHolidayTimeZone = 1 ";
            }
            if(empTimeZoneDTO.getSelected().trim().equalsIgnoreCase("general")){
                str += " and bo.isGeneralTimeZone = 1 ";
            }
        }
        String finalStr = str;
        List<EmpTimeZoneDBO> list = sessionFactory.withSession( s-> { Mutiny.Query<EmpTimeZoneDBO> finalquery = s.createQuery(finalStr,EmpTimeZoneDBO.class);
            if(!Utils.isNullOrEmpty(empTimeZoneDTO.getTimeZoneName())) {
                finalquery.setParameter("timeZoneName",empTimeZoneDTO.getTimeZoneName().replaceAll(" ","").toLowerCase().trim());
            }
            if(!Utils.isNullOrEmpty(empTimeZoneDTO.getEmployeeCategory().getValue())) {
                finalquery.setParameter("empCategoryId",Integer.parseInt(empTimeZoneDTO.getEmployeeCategory().getValue()));
            }
            if(!Utils.isNullOrEmpty(empTimeZoneDTO.getId())) {
                finalquery.setParameter("id",empTimeZoneDTO.getId());
            }
            return finalquery.getResultList();
        }).await().indefinitely();
        return Utils.isNullOrEmpty(list) ? false : true;
    }

    public void update(EmpTimeZoneDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.find(EmpTimeZoneDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
    }

    public void save(EmpTimeZoneDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
    }
}
