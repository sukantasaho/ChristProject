package com.christ.erp.services.transactions.administraton.academicCalendar;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarCategoryDBO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarCategoryDTO;

import reactor.core.publisher.Mono;

@Repository
public class AcademicCalendarCategoryTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<ErpCalendarCategoryDBO>> getGridData() {
		String str = " select distinct dbo from ErpCalendarCategoryDBO dbo  left join fetch dbo.erpCalendarCategoryRecipientsDBOSet as bo where dbo.recordStatus = 'A' and bo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpCalendarCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public boolean duplicateCheck(ErpCalendarCategoryDTO dto) {
		String str = "from ErpCalendarCategoryDBO dbo where dbo.recordStatus='A' and "
				+ " replace(replace(replace(trim(dbo.categoryName),' ','<>'),'><',''),'<>',' ') like :categoryName";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and dbo.id != :id";
		}
		String finalStr = str;
		List<ErpCalendarCategoryDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarCategoryDBO> query = s.createQuery(finalStr, ErpCalendarCategoryDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("categoryName", dto.getCategoryName().replaceAll("\\s+"," ").trim());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public void update(ErpCalendarCategoryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
	}

	public void save(ErpCalendarCategoryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public ErpCalendarCategoryDBO edit(int id) {
		String str = "from ErpCalendarCategoryDBO dbo  left join fetch dbo.erpCalendarCategoryRecipientsDBOSet as bo where dbo.recordStatus = 'A' and bo.recordStatus = 'A' and dbo.id= :id";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpCalendarCategoryDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}
	
	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction(
				session -> session.find(ErpCalendarCategoryDBO.class, id)
				.chain(bo -> session.fetch(bo.getErpCalendarCategoryRecipientsDBOSet())
						.invoke(subDBOSet -> {
							subDBOSet.forEach(subDBO -> {	                            	
								subDBO.setRecordStatus('D');
								subDBO.setModifiedUsersId(userId);
							});
							bo.setRecordStatus('D');
							bo.setModifiedUsersId(userId);
						})
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
}
