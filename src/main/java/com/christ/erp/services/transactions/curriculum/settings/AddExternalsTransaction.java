package com.christ.erp.services.transactions.curriculum.settings;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsDBO;
import com.christ.erp.services.dto.curriculum.settings.ExternalsDTO;

import reactor.core.publisher.Mono;

@Repository
public class AddExternalsTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;	

	public Mono<List<ExternalsDBO>> getGridData() {
		String str = " from ExternalsDBO e  left join fetch e.externalsAdditionalDetailsDBO as ead where e.recordStatus = 'A' and ead.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ExternalsDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public void update(ExternalsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ExternalsDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(ExternalsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public boolean duplicateCheck(ExternalsDTO dto) {
		String str = "from ExternalsDBO ex where ex.recordStatus='A' and ex.externalName = :externalName and ex.erpDepartmentDBO.id= :departmentId and ex.dob= :dob";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and ex.id != :id";
		}
		String finalStr = str;
		List<ExternalsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ExternalsDBO> query = s.createQuery(finalStr, ExternalsDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("externalName", dto.getExternalName());
			query.setParameter("departmentId", Integer.parseInt(dto.getDepartment().getValue()));
			query.setParameter("dob", dto.getDob());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public Mono<ExternalsDBO> edit(int id) {
		String str = "select e from ExternalsDBO e left join fetch e.externalsAdditionalDetailsDBO as ead  where e.recordStatus = 'A' and ead.recordStatus = 'A' and e.id= :id";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ExternalsDBO.class).setParameter("id", id).getSingleResultOrNull()).subscribeAsCompletionStage());
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(ExternalsDBO.class, id)
				.chain(bo -> session.fetch(bo.getExternalsAdditionalDetailsDBO())
						.invoke(subDbo -> {
						subDbo.setRecordStatus('D');
						subDbo.setModifiedUsersId(userId);
						bo.setRecordStatus('D');
						bo.setModifiedUsersId(userId);						
				}))).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
}