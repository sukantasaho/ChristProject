package com.christ.erp.services.transactions.support.settings;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpUserGroupDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryCampusDetailsDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryCampusDetailsEmployeeDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportRoleDBO;
import com.christ.erp.services.dto.support.settings.SupportCategoryDTO;

import reactor.core.publisher.Mono;

@Repository
public class SupportCategoryTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public boolean duplicateCheck(SupportCategoryDTO supportCategoryDTO) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from SupportCategoryDBO bo where bo.recordStatus='A' and bo.supportCategoryName = :supCatName and bo.supportCategoryGroupDBO.id = :supCatGroupId");
		if(!Utils.isNullOrEmpty(supportCategoryDTO.getId())) {
			queryString.append(" and bo.id <> :supportCategoryId");	
		}
   		List<SupportCategoryDBO> supportCategoryList =
   				sessionFactory.withSession(s->{
   					Mutiny.Query<SupportCategoryDBO> query = s.createQuery(queryString.toString(),SupportCategoryDBO.class);
   					query.setParameter("supCatName", supportCategoryDTO.getSupportCategoryName());
   					query.setParameter("supCatGroupId", Integer.parseInt(supportCategoryDTO.getSupportCategoryGroupDTO().getValue()));
   					if(!Utils.isNullOrEmpty(supportCategoryDTO.getId())) {
   						query.setParameter("supportCategoryId", supportCategoryDTO.getId());
   					}
	        return query.getResultList();
        }).await().indefinitely();
        return Utils.isNullOrEmpty(supportCategoryList) ? false : true;
    }
	
    public void saveSupportCategory(SupportCategoryDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
    }
  
    public void updateSupportCategory(SupportCategoryDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
    }
   
    public Mono<List<SupportCategoryDBO>> getAllCategories(){
    	String queryString = "select distinct bo from SupportCategoryDBO bo "
			     +" left join fetch bo.supportCategoryCampusDBOSet campusBo"
				 +" left join fetch bo.supportCategoryUserGroupDBOSet usrGrpBo"
			     +" left join fetch campusBo.supportCategoryCampusDetailsDBOs campusDetbo"
				 +" left join fetch campusDetbo.supportCategoryCampusDetailsEmployeeDBOs empBo"
			     +" where bo.recordStatus = 'A' and campusBo.recordStatus = 'A' and "
			     +" campusDetbo.recordStatus = 'A' and empBo.recordStatus = 'A' and usrGrpBo.recordStatus = 'A' order by bo.supportCategoryGroupDBO.id, bo.supportCategoryName";
    	
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, SupportCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}
   
    public SupportCategoryDBO getSupportCategoryDataById(int id){
		String queryString = "select bo from SupportCategoryDBO bo "
						     +" left join fetch bo.supportCategoryCampusDBOSet campusBo"
							 +" left join fetch bo.supportCategoryUserGroupDBOSet usrGrpBo"
						     +" left join fetch campusBo.supportCategoryCampusDetailsDBOs campusDetbo"
							 +" left join fetch campusDetbo.supportCategoryCampusDetailsEmployeeDBOs empBo"
						     +" where bo.id = :supportCategoryId and bo.recordStatus = 'A' ";
						     /*
						     + " and campusBo.recordStatus = 'A' and "
						     +" campusDetbo.recordStatus = 'A' and empBo.recordStatus = 'A' and usrGrpBo.recordStatus = 'A'";*/
  		return sessionFactory.withSession(s -> s.createQuery(queryString,SupportCategoryDBO.class).setParameter("supportCategoryId", id).getSingleResultOrNull()).await().indefinitely();

	}
   
    public Mono<Boolean> delete(int id, String userId) {
		sessionFactory.withTransaction((session, tx) -> session.createQuery( "select bo from SupportCategoryDBO bo "
				   +" left join fetch bo.supportCategoryCampusDBOSet campusBo"
				   +" left join fetch  campusBo.supportCategoryCampusDetailsDBOs campusDetbo" 
				   +" left join fetch campusDetbo.supportCategoryCampusDetailsEmployeeDBOs" 				   
			       + " where bo.id=:id", SupportCategoryDBO.class).setParameter("id", id).getSingleResultOrNull()
					.chain(dbo1 -> session.fetch(dbo1.getSupportCategoryCampusDBOSet())
					.invoke(supCatCampusSet -> {
						supCatCampusSet.forEach(supCatCampusBo->{
							supCatCampusBo.setRecordStatus('D');
							supCatCampusBo.setModifiedUsersId(Integer.parseInt(userId));
							Set<SupportCategoryCampusDetailsDBO>  supCatCampDetSet = supCatCampusBo.getSupportCategoryCampusDetailsDBOs().stream().map(supCatCampDetDbo->{
								supCatCampDetDbo.setRecordStatus('D');
								supCatCampDetDbo.setModifiedUsersId(Integer.parseInt(userId));
								Set<SupportCategoryCampusDetailsEmployeeDBO>  supCatEmpSet = supCatCampDetDbo.getSupportCategoryCampusDetailsEmployeeDBOs().stream().map(empBo->{
									empBo.setRecordStatus('D');
									empBo.setModifiedUsersId(Integer.parseInt(userId));
				            		return empBo;
				            	}).collect(Collectors.toSet());
								supCatCampDetDbo.setSupportCategoryCampusDetailsEmployeeDBOs(supCatEmpSet);
			            		return supCatCampDetDbo;
			            	}).collect(Collectors.toSet());
							supCatCampusBo.setSupportCategoryCampusDetailsDBOs(supCatCampDetSet);
						});
						dbo1.setRecordStatus('D');
						dbo1.setModifiedUsersId(Integer.parseInt(userId));
					})
					.chain(dbo2 -> session.fetch(dbo1.getSupportCategoryUserGroupDBOSet()))
					.invoke(supUserGrpSet -> {
						supUserGrpSet.forEach(supUserGrpBo -> {
							supUserGrpBo.setRecordStatus('D');
							supUserGrpBo.setModifiedUsersId(Integer.parseInt(userId));
						});
					})
					)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
  
    public List<SupportRoleDBO> getSupportRole(){
		String queryString = "from SupportRoleDBO bo where bo.recordStatus = 'A' order by bo.executionLevel";
		return sessionFactory.withSession(s->s.createQuery(queryString, SupportRoleDBO.class).getResultList()).await().indefinitely();
	}
    
    public List<ErpUserGroupDBO> getErpUserGroup(){
		String queryString = "from ErpUserGroupDBO bo where bo.recordStatus = 'A' order by bo.userGroupName";
		return sessionFactory.withSession(s->s.createQuery(queryString, ErpUserGroupDBO.class).getResultList()).await().indefinitely();
	}
    
    public Mono<List<ErpCampusDBO>> getCampus() {
		String queryString = "select dbo from ErpCampusDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpCampusDBO.class).getResultList()).subscribeAsCompletionStage());
	}
}
