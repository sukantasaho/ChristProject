package com.christ.erp.services.transactions.employee.attendance;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryActivityDBO;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryActivityDTO;

import reactor.core.publisher.Mono;

@Repository
public class WorkDiaryActivityTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<EmpWorkDiaryActivityDBO>> getGridData() {
		String str = "from EmpWorkDiaryActivityDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpWorkDiaryActivityDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public boolean duplicateCheck(EmpWorkDiaryActivityDTO dto) {
		String str;
		if(dto.isForTeaching()) {
			str = "from EmpWorkDiaryActivityDBO dbo where dbo.recordStatus = 'A' "+
					 " and dbo.isForTeaching = 1 and activityName=:activityName ";
		} else {
			str = "from EmpWorkDiaryActivityDBO dbo where dbo.recordStatus = 'A'"+
				     " and dbo.isForTeaching = 0 and activityName=:activityName and dbo.erpDepartmentDBO.id =: deptId ";
		}
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += "and id !=: id";
		}
		String finalStr = str;
		List<EmpWorkDiaryActivityDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpWorkDiaryActivityDBO> query = s.createQuery(finalStr, EmpWorkDiaryActivityDBO.class);
			if(!dto.isForTeaching()) {
				query.setParameter("deptId", Integer.parseInt(dto.getDepatment().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("activityName", dto.getActivityName().trim());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public void update(EmpWorkDiaryActivityDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpWorkDiaryActivityDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(EmpWorkDiaryActivityDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public Mono<EmpWorkDiaryActivityDBO> edit(int id) {
		return Mono.fromFuture(sessionFactory.withSession(s ->s.find(EmpWorkDiaryActivityDBO.class, id)).subscribeAsCompletionStage());
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpWorkDiaryActivityDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
}