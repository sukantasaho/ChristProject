package com.christ.erp.services.transactions.common;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.common.GUIMenuShortcutLinkDBO;

import reactor.core.publisher.Mono;

@Repository
public class GUITransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public void deleteFavouriteAndRecent(List<Integer> ids) {
		String str ="DELETE FROM GUIMenuShortcutLinkDBO  WHERE id IN (:ids)";
		sessionFactory.withSession(s -> s.createQuery(str).setParameter("ids", ids).executeUpdate()).await().indefinitely();
	}
	
	public List<Integer> getGUIMenuShortcutLinkDBOByUserIdAndType(Integer userId, String linkType ) {
		String str = "select dbo.id from GUIMenuShortcutLinkDBO dbo where dbo.recordStatus like 'A' and dbo.erpUsersDBO.id = :userId and dbo.quickLinkType =:linkType order by dbo.id asc";
		return sessionFactory.withSession(s -> s.createQuery(str, Integer.class).setParameter("userId",userId).setParameter("linkType", linkType).getResultList()).await().indefinitely();
	}
	
	public void save(GUIMenuShortcutLinkDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}
	
	public Mono<List<GUIMenuShortcutLinkDBO>> getGUIMenuShortcutLinkDBOByUserId(Integer userId ) {
		String str = "select dbo from GUIMenuShortcutLinkDBO dbo left join fetch dbo.sysMenuDBO where dbo.recordStatus = 'A' and dbo.erpUsersDBO.id = :userId order by dbo.id desc ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, GUIMenuShortcutLinkDBO.class).setParameter("userId",userId).getResultList()).subscribeAsCompletionStage());
	}
	
//	@EventListener(ApplicationReadyEvent.class)
//	public GUIUserViewPreferenceDBO getUserMeuLog() {
//		String str = "select dbo from GUIUserViewPreferenceDBO dbo  where dbo.recordStatus like 'A' and if(dbo.erpUsersDBO.id = :userId is not null dbo.erpUsersDBO.id = 1,   dbo.erpUsersDBO.id  =null)";
//		var dbo = sessionFactory.withSession(s -> s.createQuery(str, GUIUserViewPreferenceDBO.class).getSingleResultOrNull()).await().indefinitely();
//		return dbo;
//	}
	
//	public Integer getGUIMenuShortcutLinkDBOByUserIdAndTypeAndMenuId(Integer userId, String linkType, Integer menuId) {
//		String str = "select dbo.id from GUIMenuShortcutLinkDBO dbo where dbo.recordStatus like 'A' and dbo.erpUsersDBO.id = :userId and dbo.quickLinkType =:linkType and dbo.sysMenuDBO.id =:menuId";
//		return sessionFactory.withSession(s -> s.createQuery(str, Integer.class).setParameter("userId",userId).setParameter("linkType", linkType).setParameter("menuId", menuId).getSingleResultOrNull()).await().indefinitely();
//	}
	
}
