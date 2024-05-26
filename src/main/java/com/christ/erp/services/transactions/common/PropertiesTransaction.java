package com.christ.erp.services.transactions.common;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.SysPropertiesDBO;
import com.christ.erp.services.dto.common.SysPropertiesDTO;

import reactor.core.publisher.Mono;

@Repository
public class PropertiesTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<SysPropertiesDBO>> getGridData() {
		String str = "select DISTINCT dbo from SysPropertiesDBO dbo left join fetch dbo.sysPropertiesDetailsDBOSet spds where dbo.recordStatus = 'A' ORDER BY dbo.id ASC";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, SysPropertiesDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(SysPropertiesDBO.class, id)
				.chain(dbo1 -> session.fetch(dbo1.getSysPropertiesDetailsDBOSet())
						.invoke(subSet -> {
							if(!Utils.isNullOrEmpty(subSet)) {
							subSet.forEach(subDbo -> {
								subDbo.setRecordStatus('D');
								subDbo.setModifiedUsersId(userId);
							});
							}
							dbo1.setRecordStatus('D');
							dbo1.setModifiedUsersId(userId);
						})
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
	
	public boolean duplicateCheck(SysPropertiesDTO dto) {
		String str = "from SysPropertiesDBO dbo where dbo.recordStatus = 'A' and dbo.propertyName =: propertyName ";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += "and dbo.id !=: id";
		}
		String finalStr = str;
		SysPropertiesDBO dbo = sessionFactory.withSession(s -> {
			Mutiny.Query<SysPropertiesDBO> query = s.createQuery(finalStr, SysPropertiesDBO.class);
			if(!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("propertyName", dto.getPropertyName().trim());
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(dbo) ? false : true;
	}
	
	public void update(SysPropertiesDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(SysPropertiesDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(SysPropertiesDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}
	
	public SysPropertiesDBO edit(int id) {
		String str = "from SysPropertiesDBO dbo"
				+ " left join fetch  dbo.sysPropertiesDetailsDBOSet spds "
				+ " where dbo.recordStatus ='A' and dbo.id =: id ";
		return sessionFactory.withSession(s -> s.createQuery(str,SysPropertiesDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}
}