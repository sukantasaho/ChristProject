package com.christ.erp.services.transactions.admission.settings;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dto.admission.settings.AdmQualificationDegreeListDTO;

import reactor.core.publisher.Mono;

@Repository
public class QualificationDegreeListTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<AdmQualificationDegreeListDBO>> getGridData() {
		String str = "from AdmQualificationDegreeListDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, AdmQualificationDegreeListDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<AdmQualificationDegreeListDBO> edit(int id) {
		return Mono.fromFuture(sessionFactory.withSession(s->s.find(AdmQualificationDegreeListDBO.class, id)).subscribeAsCompletionStage());
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(AdmQualificationDegreeListDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public void update(AdmQualificationDegreeListDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(AdmQualificationDegreeListDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(AdmQualificationDegreeListDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public boolean duplicateCheck(AdmQualificationDegreeListDTO dto) {
		String str1 = "from AdmQualificationDegreeListDBO dbo where dbo.recordStatus = 'A'"+
					  "and dbo.degreeName =: degreeName and dbo.admQualificationListDBO.id =: qualificationId ";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str1 += "and id !=: id";
		}
		String finalStr = str1;
		List<AdmQualificationDegreeListDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AdmQualificationDegreeListDBO> query = s.createQuery(finalStr, AdmQualificationDegreeListDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("degreeName", dto.getDegreeName().trim());
			query.setParameter("qualificationId", Integer.parseInt(dto.getQualification().getValue()));
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}
}

