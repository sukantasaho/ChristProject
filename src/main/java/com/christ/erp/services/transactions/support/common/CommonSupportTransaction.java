package com.christ.erp.services.transactions.support.common;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.common.ErpUserGroupDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportAreaDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryGroupDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportRoleDBO;

import reactor.core.publisher.Mono;

@Repository
public class CommonSupportTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<SupportAreaDBO>> getSupportArea(){
		String queryString = "from SupportAreaDBO bo where bo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SupportAreaDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<SupportCategoryGroupDBO>> getSupportCategoryGroupByArea(int supportAreaId){
		String queryString = "from SupportCategoryGroupDBO bo where bo.supportAreaDBO.id = :areaId and bo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SupportCategoryGroupDBO.class).setParameter("areaId", supportAreaId)
				.getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<ErpUserGroupDBO>> getErpUserGroup(){
		String queryString = "from ErpUserGroupDBO bo where bo.recordStatus = 'A' order by bo.userGroupName";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, ErpUserGroupDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<SupportRoleDBO>> getSupportRole(){
		String queryString = "from SupportRoleDBO bo where bo.recordStatus = 'A' order by bo.executionLevel";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SupportRoleDBO.class).getResultList()).subscribeAsCompletionStage());
	}

}
