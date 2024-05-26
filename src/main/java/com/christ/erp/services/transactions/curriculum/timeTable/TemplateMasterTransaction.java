package com.christ.erp.services.transactions.curriculum.timeTable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.curriculum.timeTable.TimeTableTemplateDBO;
import com.christ.erp.services.dbobjects.curriculum.timeTable.TimeTableTemplatePeriodDBO;
import reactor.core.publisher.Mono;

@Repository
public class TemplateMasterTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<TimeTableTemplateDBO>> getGridData() {
		String query = " select distinct dbo from TimeTableTemplateDBO dbo"
				+ " inner join fetch dbo.timeTableTemplateCampusDBOSet ttc"
				+ " where dbo.recordStatus = 'A'and ttc.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, TimeTableTemplateDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public TimeTableTemplateDBO edit(int timeTableTemplateId) {
		String query = " select distinct dbo from TimeTableTemplateDBO dbo"
				+ " inner join fetch dbo.timeTableTemplateCampusDBOSet ttc"
				+ " inner join fetch dbo.timeTableTemplateDayDBOSet ttd"
				+ " left join fetch ttd.timeTableTemplatePeriodDBOSet ttp"
				+ " where dbo.recordStatus = 'A' and dbo.id = :timeTableTemplateId  and ttc.recordStatus = 'A' and ttd.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(query, TimeTableTemplateDBO.class).setParameter("timeTableTemplateId", timeTableTemplateId).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.createQuery( "select dbo from TimeTableTemplateDBO dbo "
				+ " inner join fetch dbo.timeTableTemplateCampusDBOSet ttc"
				+ " inner join fetch dbo.timeTableTemplateDayDBOSet ttd"
				+ " left join fetch ttd.timeTableTemplatePeriodDBOSet ttp"
				+" where dbo.id =:id  and dbo.recordStatus = 'A' and ttc.recordStatus = 'A' and ttd.recordStatus = 'A' ", TimeTableTemplateDBO.class).setParameter("id", id).getSingleResultOrNull()
				.chain(dbo -> session.fetch(dbo.getTimeTableTemplateCampusDBOSet())
						.invoke(subSet -> {
							subSet.forEach(campusDbo -> {
								campusDbo.setRecordStatus('D');
								campusDbo.setModifiedUsersId(userId);
							});
						})
						.chain(dbo1 -> session.fetch(dbo.getTimeTableTemplateDayDBOSet()))
						.invoke(subSet1 -> {
							subSet1.forEach(daysDbo -> {
								if(!Utils.isNullOrEmpty(daysDbo.getTimeTableTemplatePeriodDBOSet())) {
									Set<TimeTableTemplatePeriodDBO> timeTableTemplatePeriodDBOSet = daysDbo.getTimeTableTemplatePeriodDBOSet().stream().map(periods -> {
										periods.setModifiedUsersId(userId);
										periods.setRecordStatus('D');
										return periods;
									}).collect(Collectors.toSet());
									daysDbo.setTimeTableTemplatePeriodDBOSet(timeTableTemplatePeriodDBOSet);
								}
								daysDbo.setRecordStatus('D');
								daysDbo.setModifiedUsersId(userId);
							});
							dbo.setRecordStatus('D');
							dbo.setModifiedUsersId(userId);
						})		
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public TimeTableTemplateDBO isDuplicateCheck(int id, String templateName) {
		String query = " select distinct dbo from TimeTableTemplateDBO dbo where dbo.recordStatus = 'A' and replace(dbo.timeTableName,' ','') = :name";
		if(!Utils.isNullOrEmpty(id)) {
			query += " and dbo.id != :id";
		}
		String finalQuery = query;
		TimeTableTemplateDBO value = sessionFactory.withSession(s ->{Mutiny.Query<TimeTableTemplateDBO> str = s.createQuery(finalQuery, TimeTableTemplateDBO.class);
		str.setParameter("name", templateName);
		if(!Utils.isNullOrEmpty(id)) {
			str.setParameter("id", id);
		}
		return str.getSingleResultOrNull();
		}).await().indefinitely();
		return value;
	}

	public void update(TimeTableTemplateDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(TimeTableTemplateDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(TimeTableTemplateDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}

}
