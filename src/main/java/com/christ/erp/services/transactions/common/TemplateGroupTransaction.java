package com.christ.erp.services.transactions.common;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateGroupDBO;
import com.christ.erp.services.dto.common.ErpTemplateGroupDTO;

import reactor.core.publisher.Mono;

@Repository
public class TemplateGroupTransaction 
{
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<ErpTemplateGroupDBO>> getGridData() {
		String str = "from ErpTemplateGroupDBO bo where bo.recordStatus='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpTemplateGroupDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public void update(ErpTemplateGroupDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpTemplateGroupDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}
	
	public void save(ErpTemplateGroupDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}
	
	public Mono<ErpTemplateGroupDBO> edit(int id) {
		return Mono.fromFuture(sessionFactory.withSession(s -> s.find(ErpTemplateGroupDBO.class, id )).subscribeAsCompletionStage());
	}
	
	public boolean duplicateCheck(ErpTemplateGroupDTO dto) {
		String str = "from ErpTemplateGroupDBO where  recordStatus='A' and  templateGroupCode=:templateGroupCode";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and id != :id";
	    }
		String finalStr = str;
		List <ErpTemplateGroupDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpTemplateGroupDBO> query = s.createQuery(finalStr, ErpTemplateGroupDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());		
			}
			query.setParameter("templateGroupCode", dto.getTemplateGroupCode().trim());	
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}
	
	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpTemplateGroupDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
}
