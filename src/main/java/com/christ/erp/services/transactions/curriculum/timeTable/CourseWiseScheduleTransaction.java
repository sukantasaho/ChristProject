package com.christ.erp.services.transactions.curriculum.timeTable;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.common.AcaCourseDBO;
import com.christ.erp.services.dbobjects.common.AcaCourseSpecificScheduleDBO;
import com.christ.erp.services.dbobjects.curriculum.timeTable.TimeTableTemplateDBO;
import reactor.core.publisher.Mono;

@Repository
public class CourseWiseScheduleTransaction {

	@Autowired
	SessionFactory sessionFactory;
	
	public Mono<List<Tuple>> getCampusesByUser(String userId) {
		String queryString =" select distinct erp_campus.erp_campus_id as id , erp_campus.campus_name as campusName from  erp_users"
				+ "	inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status = 'A'"
				+ "	inner join  erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id"
				+ "	or erp_campus_department_mapping.erp_campus_department_mapping_id = emp.deputation_erp_campus_department_mapping_id and   erp_campus_department_mapping.record_status = 'A'"
				+ "	inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status = 'A'"
				+ "	where erp_users.record_status = 'A'  and erp_users.erp_users_id = :userId order by campusName";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Tuple>> getSession(String yearId) {
		String queryString =" select distinct aca_session.aca_session_id as id ,aca_session.session_name as sessionName from aca_duration"
				+ " inner join aca_duration_detail on aca_duration.aca_duration_id = aca_duration_detail.aca_duration_detail_id and aca_duration_detail.record_status = 'A'"
				+ " inner join aca_session ON aca_session.aca_session_id = aca_duration_detail.aca_session_id and aca_session.record_status = 'A'"
				+ " where aca_duration.record_status = 'A' and aca_duration.is_current_session = 1 and aca_duration.erp_academic_year_id = :yearId order by id";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("yearId", Integer.parseInt(yearId)).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<AcaCourseDBO>> getCourseNameAndCourseCode() {
		String queryString =" select dbo from AcaCourseDBO dbo where dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, AcaCourseDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<AcaCourseSpecificScheduleDBO>> getGridData() {
		String query = " ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, AcaCourseSpecificScheduleDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public AcaCourseSpecificScheduleDBO edit(int timeTableTemplateId) {
		String query = " ";
		return sessionFactory.withSession(s -> s.createQuery(query, AcaCourseSpecificScheduleDBO.class).setParameter("timeTableTemplateId", timeTableTemplateId).getSingleResultOrNull()).await().indefinitely();
	}
	
	public void update(AcaCourseSpecificScheduleDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(TimeTableTemplateDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(AcaCourseSpecificScheduleDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}
	
	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(AcaCourseSpecificScheduleDBO.class, id)
				.chain(dbo -> session.fetch(dbo.getAcaCourseSpecificScheduleTeachersDBOSet())
						.invoke(subSet -> {
							subSet.forEach( scheduleTeachers-> {
								scheduleTeachers.setRecordStatus('D');
								scheduleTeachers.setModifiedUsersId(userId);
							});
							dbo.setRecordStatus('D');
							dbo.setModifiedUsersId(userId);
						})		
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
	
}
