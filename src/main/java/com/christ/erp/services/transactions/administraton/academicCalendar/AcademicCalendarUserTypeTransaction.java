package com.christ.erp.services.transactions.administraton.academicCalendar;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarUserTypesDBO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarUserTypesDTO;

import reactor.core.publisher.Mono;

@Repository
public class AcademicCalendarUserTypeTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<ErpCalendarUserTypesDBO>> getGridData() {
		String str = "select dbo from ErpCalendarUserTypesDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpCalendarUserTypesDBO.class).getResultList()).subscribeAsCompletionStage());			
	}
	
	public boolean duplicateCheck(ErpCalendarUserTypesDTO dto) {
		String str = "from ErpCalendarUserTypesDBO dbo where dbo.recordStatus='A' and replace(replace(replace(trim(dbo.userType),' ','<>'),'><',''),'<>',' ') like :userType";
				if (!Utils.isNullOrEmpty(dto.getEmpEmployeeCategoryDTO())) {
					str += " and dbo.empEmployeeCategoryDBO.id =:categoryId";
				} else if(dto.isStudent()) {
					str += " and dbo.isStudent = 1";
				}
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and dbo.id != :id";
		}
		String finalStr = str;
		List<ErpCalendarUserTypesDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCalendarUserTypesDBO> query = s.createQuery(finalStr, ErpCalendarUserTypesDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			if (!Utils.isNullOrEmpty(dto.getEmpEmployeeCategoryDTO())) {
				query.setParameter("categoryId", Integer.parseInt(dto.getEmpEmployeeCategoryDTO().getValue()));
			}
			query.setParameter("userType", dto.getUserType().replaceAll("\\s+"," ").trim());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}
	
	public void update(ErpCalendarUserTypesDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
	}

	public void save(ErpCalendarUserTypesDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}
	
	public ErpCalendarUserTypesDBO edit(int id) {
		String str = "from ErpCalendarUserTypesDBO dbo where dbo.recordStatus = 'A' and dbo.id= :id";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpCalendarUserTypesDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}
	
	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpCalendarUserTypesDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
}
