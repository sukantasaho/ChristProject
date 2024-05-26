package com.christ.erp.services.transactions.admission.applicationprocess;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionGeneralDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionLocationCampusDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class WeightageDefinitionSettingsTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

    public Mono<List<AdmWeightageDefinitionDBO>> getGridData() {
        String str = "select distinct bo from AdmWeightageDefinitionDBO bo left join fetch bo.admWeightageDefinitionLocationCampusDBOsSet where bo.recordStatus = 'A'";
        return Mono.fromFuture(sessionFactory.withSession(session->session.createQuery(str, AdmWeightageDefinitionDBO.class).getResultList()).subscribeAsCompletionStage());
    }

	public Mono<AdmWeightageDefinitionDBO> edit(int id) {
       String str = "select bo from AdmWeightageDefinitionDBO bo "
       		+ " left join fetch bo.admWeightageDefinitionDetailDBOsSet "
       		+ " left join fetch bo.admWeightageDefinitionGeneralDBOsSet "
       		+ " left join fetch bo.admWeightageDefinitionLocationCampusDBOsSet "
       		+ " where bo.recordStatus = 'A' and bo.id=:id ";
       return Mono.fromFuture(sessionFactory.withSession(session->session.createQuery(str, AdmWeightageDefinitionDBO.class)
    		   .setParameter("id", id).getSingleResultOrNull()).subscribeAsCompletionStage());
	}
	
	public Mono<Boolean> delete(int id, String userId) {
		sessionFactory.withTransaction((session, tx) -> session.createQuery(" select bo from AdmWeightageDefinitionDBO bo " +
			" left join fetch bo.admWeightageDefinitionDetailDBOsSet " +
			" left join fetch bo.admWeightageDefinitionGeneralDBOsSet " +
			" left join fetch bo.admWeightageDefinitionLocationCampusDBOsSet " +
			" where bo.id=:id", AdmWeightageDefinitionDBO.class).setParameter("id", id).getSingleResultOrNull()
			.invoke(admWeightageDefinitionDBO -> {
				admWeightageDefinitionDBO.setRecordStatus('D');
				admWeightageDefinitionDBO.setModifiedUsersId(Integer.parseInt(userId));
				Set<AdmWeightageDefinitionDetailDBO> admWeightageDefinitionDetailDBOS = admWeightageDefinitionDBO.getAdmWeightageDefinitionDetailDBOsSet().stream().peek(admWeightageDefinitionDetailDBO -> {
					admWeightageDefinitionDetailDBO.setRecordStatus('D');
					admWeightageDefinitionDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
				}).collect(Collectors.toSet());
				Set<AdmWeightageDefinitionGeneralDBO> admWeightageDefinitionGeneralDBOS = admWeightageDefinitionDBO.getAdmWeightageDefinitionGeneralDBOsSet().stream().peek(admWeightageDefinitionGeneralDBO -> {
					admWeightageDefinitionGeneralDBO.setRecordStatus('D');
					admWeightageDefinitionGeneralDBO.setModifiedUsersId(Integer.parseInt(userId));
				}).collect(Collectors.toSet());
				Set<AdmWeightageDefinitionLocationCampusDBO> admWeightageDefinitionLocationCampusDBOS = admWeightageDefinitionDBO.getAdmWeightageDefinitionLocationCampusDBOsSet().stream().peek(admWeightageDefinitionLocationCampusDBO -> {
					admWeightageDefinitionLocationCampusDBO.setRecordStatus('D');
					admWeightageDefinitionLocationCampusDBO.setModifiedUsersId(Integer.parseInt(userId));
				}).collect(Collectors.toSet());
				admWeightageDefinitionDBO.setAdmWeightageDefinitionDetailDBOsSet(admWeightageDefinitionDetailDBOS);
				admWeightageDefinitionDBO.setAdmWeightageDefinitionGeneralDBOsSet(admWeightageDefinitionGeneralDBOS);
				admWeightageDefinitionDBO.setAdmWeightageDefinitionLocationCampusDBOsSet(admWeightageDefinitionLocationCampusDBOS);
			})).await().indefinitely();
        return Mono.just(Boolean.TRUE);
	}

	public boolean duplicateCheck(AdmWeightageDefinitionDTO dto) {		
        StringBuilder str1 =  new StringBuilder("from AdmWeightageDefinitionDBO bo"
        		+ " inner join fetch bo.admWeightageDefinitionLocationCampusDBOsSet cbo "
        		+ " where bo.recordStatus='A' and bo.erpAcademicYearDBO.id =:yearId and cbo.erpCampusProgrammeMappingDBO.id in (:campusMappingIds)");
        if (!Utils.isNullOrEmpty(dto.getId())) {
            str1.append(" and bo.id != :id");
        }
        Set<Integer> mappingIds = dto.getCampusOrlocationsMappping().stream().map(s->Integer.parseInt(s.value)).collect(Collectors.toSet());
        List<AdmWeightageDefinitionDBO> list = sessionFactory.withSession(s->{
   				Mutiny.Query<AdmWeightageDefinitionDBO> query = s.createQuery(str1.toString(),AdmWeightageDefinitionDBO.class);
   			      query.setParameter("campusMappingIds", mappingIds);
   			      query.setParameter("yearId", Integer.parseInt(dto.getErpAcademicYearDTO().getValue()));
   	              if (!Utils.isNullOrEmpty(dto.getId())) {
   	            	 query.setParameter("id", dto.getId());
   	              }
   	        	  return query.getResultList();
   	        }).await().indefinitely();
        return !Utils.isNullOrEmpty(list);
	}

	public void update(AdmWeightageDefinitionDBO dbo, Set<Integer> admWeightageDefinitionDetailDBOIds, Set<Integer> admWeightageDefinitionGeneralDBOIds, String userId) {
 	   	sessionFactory.withTransaction((session, tx) ->  session.merge(dbo)).subscribeAsCompletionStage();
		sessionFactory.withTransaction((session, tx) -> session.createQuery(" select bo from AdmWeightageDefinitionDBO bo " +
			" left join fetch bo.admWeightageDefinitionDetailDBOsSet detailDBO" +
			" left join fetch bo.admWeightageDefinitionGeneralDBOsSet generalDBO" +
			" where detailDBO.id not in (:admWeightageDefinitionDetailDBOIds) and generalDBO.id not in (:admWeightageDefinitionGeneralDBOIds) and bo.id=:id", AdmWeightageDefinitionDBO.class)
				.setParameter("id", dbo.getId())
				.setParameter("admWeightageDefinitionDetailDBOIds", admWeightageDefinitionDetailDBOIds).setParameter("admWeightageDefinitionGeneralDBOIds", admWeightageDefinitionGeneralDBOIds).getSingleResultOrNull()
				.invoke(admWeightageDefinitionDBO -> {
					if(!Utils.isNullOrEmpty(admWeightageDefinitionDBO)) {
					Set<AdmWeightageDefinitionDetailDBO> admWeightageDefinitionDetailDBOS = admWeightageDefinitionDBO.getAdmWeightageDefinitionDetailDBOsSet().stream().peek(admWeightageDefinitionDetailDBO -> {
						admWeightageDefinitionDetailDBO.setRecordStatus('D');
						admWeightageDefinitionDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
					}).collect(Collectors.toSet());
					Set<AdmWeightageDefinitionGeneralDBO> admWeightageDefinitionGeneralDBOS = admWeightageDefinitionDBO.getAdmWeightageDefinitionGeneralDBOsSet().stream().peek(admWeightageDefinitionGeneralDBO -> {
						admWeightageDefinitionGeneralDBO.setRecordStatus('D');
						admWeightageDefinitionGeneralDBO.setModifiedUsersId(Integer.parseInt(userId));
					}).collect(Collectors.toSet());
					admWeightageDefinitionDBO.setAdmWeightageDefinitionDetailDBOsSet(admWeightageDefinitionDetailDBOS);
					admWeightageDefinitionDBO.setAdmWeightageDefinitionGeneralDBOsSet(admWeightageDefinitionGeneralDBOS);
					}
				})).await().indefinitely();
 	}

    public void save(AdmWeightageDefinitionDBO dbo) {
    	sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
    }
}
