package com.christ.erp.services.transactions.employee.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryEntriesDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryEntriesDetailsDBO;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryEntriesDTO;
import reactor.core.publisher.Mono;

@Repository
public class WorkDiaryEntryTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public ErpAcademicYearDBO getAcademicYearId(LocalDate localDate1 , int campusId) {
		String str = " select dbo from ErpAcademicYearDBO dbo"
				+ " left join fetch dbo.academicYearDetails dbos where dbo.recordStatus ='A' and dbos.campus.id =:campusId  and (:localDate) between dbos.academicYearStartDate and dbos.academicYearEndDate and dbos.recordStatus ='A'";
		return sessionFactory.withSession(s-> s.createQuery(str, ErpAcademicYearDBO.class).setParameter("localDate", localDate1).setParameter("campusId", campusId).getSingleResultOrNull()).await().indefinitely();
	}

	public void update(EmpWorkDiaryEntriesDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpWorkDiaryEntriesDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(EmpWorkDiaryEntriesDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo).chain(session::flush).map(s-> {
			return convertDbo(dbo);
		}).flatMap(s-> session.persist(s))).await().indefinitely();
	}
		
public ErpWorkFlowProcessStatusLogDBO convertDbo(EmpWorkDiaryEntriesDBO dbo) {
	ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
	erpWorkFlowProcessStatusLogDBO.entryId = dbo.getId();
	ErpWorkFlowProcessDBO workFlowProcessDBO = new ErpWorkFlowProcessDBO();
	if(!Utils.isNullOrEmpty(dbo.erpApplicantWorkFlowProcessDBO.id))
		workFlowProcessDBO.id = dbo.erpApplicantWorkFlowProcessDBO.id;
	else
		workFlowProcessDBO.id = dbo.erpApplicationWorkFlowProcessDBO.id;
	erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = workFlowProcessDBO;
	erpWorkFlowProcessStatusLogDBO.createdUsersId = dbo.getCreatedUsersId();
	erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
	return erpWorkFlowProcessStatusLogDBO;
}
	
	public List<EmpWorkDiaryEntriesDBO> getWorkDiaryEntryData( Map<String,String> requestParams, int empId) {
		LocalDate startDate = Utils.convertStringDateToLocalDate(requestParams.get("startDate"));
		LocalDate endDate =  Utils.convertStringDateToLocalDate(requestParams.get("endDate"));
		String str = " select distinct dbo from EmpWorkDiaryEntriesDBO dbo "
				+ " left join fetch dbo.empWorkDiaryEntriesDetailsDBOSet dbos where dbo.empDBO.id=: empId and dbo.erpWorkEntryDate between (:startDate) and (:endDate) and dbo.recordStatus='A' order by dbo.erpWorkEntryDate ASC";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpWorkDiaryEntriesDBO.class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("empId", empId).getResultList()).await().indefinitely();
	}

	public Integer getEmployeeId(String usersId) {
		String str = " select emp.emp_id as empId from erp_users"
				+" inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'"
				+" where erp_users.erp_users_id=:userId and erp_users.record_status='A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("userId", Integer.parseInt(usersId)).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpWorkDiaryEntriesDetailsDBO.class, id).invoke(dbo-> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE) ;
	}

	public Boolean isDateExists(EmpWorkDiaryEntriesDTO dto) {
		String str= "from EmpWorkDiaryEntriesDBO where recordStatus='A' and erpWorkEntryDate =:erpWorkEntryDate";
		List<EmpWorkDiaryEntriesDBO > list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpWorkDiaryEntriesDBO > query = s.createQuery(str, EmpWorkDiaryEntriesDBO.class);
			query.setParameter("erpWorkEntryDate", Utils.convertStringDateToLocalDate(dto.getDate()));  
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public List<EmpWorkDiaryEntriesDBO> isTimeRangeExists(EmpWorkDiaryEntriesDTO dto) {
		String str =  " select distinct dbo from EmpWorkDiaryEntriesDBO dbo"
				+ " left join fetch dbo.empWorkDiaryEntriesDetailsDBOSet dbos where dbo.recordStatus='A' and dbos.recordStatus='A'";
		str += " and dbo.id =:id";
		String finalStr =str;
		List<EmpWorkDiaryEntriesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpWorkDiaryEntriesDBO> query = s.createQuery(finalStr, EmpWorkDiaryEntriesDBO.class);
			if(!Utils.isNullOrEmpty(dto.getId()))	{
				query.setParameter("id", dto.getId());	
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Tuple getEmployeesId(String userId) {
		String str = " select emp.emp_id as empId from erp_users"
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'"
				+ " where erp_users.erp_users_id=:userId and erp_users.record_status='A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("userId", Integer.parseInt(userId)).getSingleResultOrNull()).await().indefinitely();
	}

	public Integer getApproversIdByEmployeeId(int empId) {
		String str = "  select emp.emp_id as emp_id from emp "
				+" inner join emp_approvers on emp_approvers.work_diary_approver_id = emp.emp_id and emp_approvers.record_status ='A' "
				+" where  emp_approvers.emp_id =:empId  and emp.record_status ='A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("empId", empId).getSingleResultOrNull()).await().indefinitely();					
	}

	public EmpWorkDiaryEntriesDBO getData(int id) {
		String str = " select distinct dbo from EmpWorkDiaryEntriesDBO dbo"
				+ " left join fetch dbo.empWorkDiaryEntriesDetailsDBOSet dbos where dbo.id=:id and dbo.recordStatus='A' and dbos.recordStatus='A'";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpWorkDiaryEntriesDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}
}









