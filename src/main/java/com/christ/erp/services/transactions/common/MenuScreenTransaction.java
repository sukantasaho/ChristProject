package com.christ.erp.services.transactions.common;

import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpScreenConfigMastDBO;
import com.christ.erp.services.dbobjects.common.SysMenuDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleSubDBO;
import com.christ.erp.services.dto.common.MenuScreenDTO;
import reactor.core.publisher.Mono;

@Repository
public class MenuScreenTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<SysMenuModuleDBO>> getModule() {
		String query = " select dbo from SysMenuModuleDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, SysMenuModuleDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public List<SysMenuModuleSubDBO> getSubModule(String moduleId) {
		String query = " select dbo from SysMenuModuleSubDBO dbo where dbo.recordStatus = 'A' and dbo.sysMenuModuleDBO.id = :moduleId";
		return sessionFactory.withSession(s -> s.createQuery(query, SysMenuModuleSubDBO.class)
				.setParameter("moduleId", Integer.parseInt(moduleId)).getResultList()).await().indefinitely();
	}
	
	public Mono<List<ErpScreenConfigMastDBO>> getMasterScreenReference() {
		String query = " select dbo from ErpScreenConfigMastDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, ErpScreenConfigMastDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<SysMenuDBO> getGridData() {
		String query = " select dbo from SysMenuDBO dbo where dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(query, SysMenuDBO.class).getResultList()).await().indefinitely();
	}

	public SysMenuDBO edit(int id) {
		String query = " select dbo from SysMenuDBO dbo "
				+ " left join fetch dbo.sysFunctionDBOSet sf"
    			+ " where dbo.id = :id and dbo.recordStatus = 'A' and sf.recordStatus = 'A'";
		return  sessionFactory.withSession(s->s.createQuery(query,SysMenuDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
    }
	public Integer getDisplayOrder(int subModuleId) {
		String query = " select max(menu_screen_display_order) from sys_menu where  sys_menu.record_status = 'A' and  sys_menu_module_sub_id = :subModuleId ";
		return sessionFactory.withSession(s -> s.createNativeQuery(query, Integer.class).setParameter("subModuleId", subModuleId).getSingleResultOrNull()).await().indefinitely();
	}
    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(SysMenuDBO.class, id)
        		.chain(dbo -> session.fetch(dbo.getSysFunctionDBOSet())
        		.invoke(subSet -> {
        			subSet.forEach(subDbo -> {
        				subDbo.setRecordStatus('D');
        				subDbo.setModifiedUsersId(userId);
        			});
        			dbo.setRecordStatus('D');
        			dbo.setModifiedUsersId(userId);
        		})	
        		)).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }
    
	public void update(SysMenuDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(SysMenuDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(SysMenuDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}
	
    public boolean duplicateCheck(MenuScreenDTO  dto) {
    	String query = " select dbo from SysMenuDBO dbo  where dbo.recordStatus = 'A' and dbo.sysMenuModuleSubDBO.id = :subId and( dbo.menuScreenDisplayOrder = :displayOrder "
    			+ " or dbo.menuScreenName = :menuScreenName) and dbo.id != :id";
		 List<SysMenuDBO> list = sessionFactory.withSession(s->s.createQuery(query,SysMenuDBO.class)
				 .setParameter("subId",Integer.parseInt(dto.getSubModule().getValue())).setParameter("displayOrder", Integer.parseInt(dto.getDisplayOrderNo()))
				 .setParameter("id", dto.getMenuId())
				 .setParameter("menuScreenName", dto.getMenuName().trim())
				 .getResultList()).await().indefinitely();
        return Utils.isNullOrEmpty(list) ? false : true;
    } 	    

}
