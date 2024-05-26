package com.christ.erp.services.transactions.account.settings;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeGroupDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dto.account.AccFeeHeadsDTO;

import reactor.core.publisher.Mono;

@Repository
public class FeeHeadsTransaction {
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

    public Mono<List<AccFeeHeadsDBO>> getGridData(){
    	String queryString = "select bo from AccFeeHeadsDBO bo "
			     +" where bo.recordStatus = 'A' order by bo.feeHeadsType, bo.heading";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AccFeeHeadsDBO.class).getResultList()).subscribeAsCompletionStage());
	}

    public List<AccFeeHeadsAccountDBO> duplicateCheckCode(List<String> codeList, int accId ) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from AccFeeHeadsAccountDBO bo where bo.recordStatus='A' and bo.sapCode in (:accCodeList)");
		if(!Utils.isNullOrEmpty(accId)) {
			queryString.append(" and bo.accFeeHeadsDBO.id <> :accFeeHeadId");	
		}
   		List<AccFeeHeadsAccountDBO> accFeeHeadBoList =
   				sessionFactory.withSession(s->{
   					Mutiny.Query<AccFeeHeadsAccountDBO> query = s.createQuery(queryString.toString(),AccFeeHeadsAccountDBO.class);
   					query.setParameter("accCodeList", codeList);
   					if(!Utils.isNullOrEmpty(accId)) {
   						query.setParameter("accFeeHeadId", accId);
   					}
	        return query.getResultList();
        }).await().indefinitely();
   		
        return !Utils.isNullOrEmpty(accFeeHeadBoList) ? accFeeHeadBoList : null;
    }
   
    public AccFeeHeadsDBO duplicateCheckWithTypeAndName(AccFeeHeadsDTO accFeeHeadsDTO ) {
  		StringBuffer queryString = new StringBuffer();
  		queryString.append("from AccFeeHeadsDBO bo where bo.recordStatus='A' and bo.feeHeadsType = :type and bo.heading = :head");
  		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
  			queryString.append(" and bo.id <> :accFeeHeadId");	
  		}
 		List<AccFeeHeadsDBO> accFeeHeadBoList =
			sessionFactory.withSession(s->{
				Mutiny.Query<AccFeeHeadsDBO> query = s.createQuery(queryString.toString(),AccFeeHeadsDBO.class);
				query.setParameter("type", accFeeHeadsDTO.getFeeHeadsTypeDTO().getValue());
				query.setParameter("head", accFeeHeadsDTO.getHeading());
				if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
					query.setParameter("accFeeHeadId", accFeeHeadsDTO.getId());
				}
  	        return query.getResultList();
          }).await().indefinitely();
     		
          return !Utils.isNullOrEmpty(accFeeHeadBoList) ? accFeeHeadBoList.get(0) : null;
    }
    
    public void save(AccFeeHeadsDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
    }
   
    public void update(AccFeeHeadsDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
    }
   
    public Mono<List<AccAccountsDBO>> getCampusAndAccountNumberBoList(){
    	String queryString = "select bo from AccAccountsDBO bo "
			     +" where bo.recordStatus = 'A' and  bo.isUniversityAccount = 1 order by bo.erpCampusDBO.campusName";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AccAccountsDBO.class).getResultList()).subscribeAsCompletionStage());
	}
    
    public AccFeeHeadsDBO getAccFeeHeadBoByIdForEdit(int id){
		String queryString = "select bo from AccFeeHeadsDBO bo "
						     +" left join fetch bo.accFeeHeadsAccountList feeHeadAccount"
							 +" where bo.recordStatus = 'A' and bo.id = :accFeeHeadsId";
		return sessionFactory.withSession(s->s.createQuery(queryString, AccFeeHeadsDBO.class).setParameter("accFeeHeadsId", id).getSingleResultOrNull()).await().indefinitely();
	}

    public Mono<Boolean> delete(int id, String userId) {
		sessionFactory.withTransaction((session, tx) -> session.createQuery( "select bo from AccFeeHeadsDBO bo "
			 +" left join fetch bo.accFeeHeadsAccountList feeHeadAccount"
		     + " where bo.id=:id", AccFeeHeadsDBO.class).setParameter("id", id).getSingleResultOrNull()
			.chain(dbo1 -> session.fetch(dbo1.getAccFeeHeadsAccountList())
			.invoke(accFeeHeadsAccSet -> {
				accFeeHeadsAccSet.forEach(accFeeHeadAccBo->{
					accFeeHeadAccBo.setRecordStatus('D');
					accFeeHeadAccBo.setModifiedUsersId(Integer.parseInt(userId));
				});
				dbo1.setRecordStatus('D');
				dbo1.setModifiedUsersId(Integer.parseInt(userId));
			})
		)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
   
    public List<AccAccountsDBO> getAccountNumberAndCampusListForEdit(){
		String queryString = "select bo from AccAccountsDBO bo "
			     +" where bo.recordStatus = 'A' and  bo.isUniversityAccount = 1 order by bo.erpCampusDBO.campusName";
		return sessionFactory.withSession(s->s.createQuery(queryString, AccAccountsDBO.class).getResultList()).await().indefinitely();
		
	}
    
    public Mono<List<AccFeeGroupDBO>> getFeeGroupData(){
    	String queryString = "select bo from AccFeeGroupDBO bo "
			     +" where bo.recordStatus = 'A' order by bo.groupName";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AccFeeGroupDBO.class).getResultList()).subscribeAsCompletionStage());
	}
  
    public Mono<List<AccAccountsDBO>> getAccAccountsByHostel(int hostelId){
    	String queryString = " select accBO from HostelDBO bo inner join AccAccountsDBO accBO"
    			+ " on accBO.erpCampusDBO.id = bo.erpCampusDBO.id where bo.id = :hostelId "
    			+ " and bo.recordStatus = 'A' and accBO.recordStatus = 'A' ";
    	return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AccAccountsDBO.class)
    			.setParameter("hostelId", hostelId).getResultList()).subscribeAsCompletionStage());
	}
  
    public boolean duplicateCheckTypeAndHostel(AccFeeHeadsDTO accFeeHeadsDTO ) {
  		StringBuffer queryString = new StringBuffer();
  		queryString.append("from AccFeeHeadsDBO bo where bo.recordStatus='A' and bo.feeHeadsType = :type and bo.hostelDBO.id = :hostelId");
  		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
  			queryString.append(" and bo.id <> :accFeeHeadId");	
  		}
 		List<AccFeeHeadsDBO> accFeeHeadBoList =
			sessionFactory.withSession(s->{
				Mutiny.Query<AccFeeHeadsDBO> query = s.createQuery(queryString.toString(),AccFeeHeadsDBO.class);
				query.setParameter("type", accFeeHeadsDTO.getFeeHeadsTypeDTO().getValue());
				query.setParameter("hostelId", Integer.parseInt(accFeeHeadsDTO.getHostelDTO().getValue()));
				if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
					query.setParameter("accFeeHeadId", accFeeHeadsDTO.getId());
				}
  	        return query.getResultList();
          }).await().indefinitely();
 			return Utils.isNullOrEmpty(accFeeHeadBoList) ? false : true;
    }
 
    public AccFeeHeadsDBO roomTypeAndHostelduplicateCheck(AccFeeHeadsDTO accFeeHeadsDTO ) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from AccFeeHeadsDBO bo where bo.recordStatus ='A' "
				+ " and bo.feeHeadsType = 'Hostel Fee' and bo.hostelDBO.id = :hostelId and bo.hostelRoomTypeDBO.id = :rooomTypeId");
		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
  			queryString.append(" and bo.id <> :accFeeHeadId");	
  		}
   		List<AccFeeHeadsDBO> accFeeHeadBoList = sessionFactory.withSession(s->{
				Mutiny.Query<AccFeeHeadsDBO> query = s.createQuery(queryString.toString(),AccFeeHeadsDBO.class);
				query.setParameter("hostelId", Integer.parseInt(accFeeHeadsDTO.getHostelDTO().getValue()));
				String hostelRoomType = accFeeHeadsDTO.getHostelRoomTypeDTO().getValue();
				query.setParameter("rooomTypeId", Integer.parseInt(hostelRoomType));
				if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
					query.setParameter("accFeeHeadId", accFeeHeadsDTO.getId());
				}
			return query.getResultList();
        }).await().indefinitely();
   		
        return !Utils.isNullOrEmpty(accFeeHeadBoList) ? accFeeHeadBoList.get(0) : null;
    }
  /*
    public AccFeeHeadsDBO applicationFeeAndDegreeDuplicateCheck(AccFeeHeadsDTO accFeeHeadsDTO ) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from AccFeeHeadsDBO bo where bo.recordStatus='A'  and bo.feeHeadsType = 'Application Fee' and bo.erpProgrammeDegreeDBO.id = :degreeId"); // need to change
		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
  			queryString.append(" and bo.id <> :accFeeHeadId");	
  		}
   		List<AccFeeHeadsDBO> accFeeHeadBoList = sessionFactory.withSession(s->{
				Mutiny.Query<AccFeeHeadsDBO> query = s.createQuery(queryString.toString(),AccFeeHeadsDBO.class);
				query.setParameter("degreeId", Integer.parseInt(accFeeHeadsDTO.getErpProgrammeDegreeDTO().getValue()));
				if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
					query.setParameter("accFeeHeadId", accFeeHeadsDTO.getId());
				}
				return query.getResultList();
        }).await().indefinitely();
   		
        return !Utils.isNullOrEmpty(accFeeHeadBoList) ? accFeeHeadBoList.get(0) : null;
    }
    */
    
    public AccFeeHeadsDBO duplicateCheckForAdmProcessFee(AccFeeHeadsDTO accFeeHeadsDTO ) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from AccFeeHeadsDBO bo where bo.recordStatus='A' and bo.feeHeadsType = :type");
		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
			queryString.append(" and bo.id <> :accFeeHeadId");	
		}
   		List<AccFeeHeadsDBO> accFeeHeadBoList =
   				sessionFactory.withSession(s->{
   					Mutiny.Query<AccFeeHeadsDBO> query = s.createQuery(queryString.toString(),AccFeeHeadsDBO.class);
					query.setParameter("type", accFeeHeadsDTO.getFeeHeadsTypeDTO().getLabel());
   					if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getId())) {
   						query.setParameter("accFeeHeadId", accFeeHeadsDTO.getId());
   					}
	        return query.getResultList();
        }).await().indefinitely();
   		
        return !Utils.isNullOrEmpty(accFeeHeadBoList) ? accFeeHeadBoList.get(0) : null;
    }
}
