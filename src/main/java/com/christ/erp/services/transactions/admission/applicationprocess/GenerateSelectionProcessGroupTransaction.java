package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDetailDBO;
import com.christ.erp.services.dto.admission.applicationprocess.GenerateSelectionProcessGrouprCeateDTO;

import reactor.core.publisher.Mono;

@Repository
public class GenerateSelectionProcessGroupTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<Object[]>> getStudentsForGenerateSPGroup(GenerateSelectionProcessGrouprCeateDTO dto) {
		System.out.println(dto.getSelectionProcessDate().text);
			StringBuffer queryString = new StringBuffer(" select spdAllot.adm_selection_process_plan_detail_allotment_id, sp.student_appln_entries_id  from adm_selection_process sp  " + 
			 		" inner join adm_selection_process_plan_detail_allotment spdAllot ON spdAllot.adm_selection_process_plan_detail_allotment_id = sp.adm_selection_process_plan_detail_allotment_id  " + 
			 		" inner join adm_selection_process_plan_detail spd ON spd.adm_selection_process_plan_detail_id = spdAllot.adm_selection_process_plan_detail_id  " + 
			 		" inner join adm_selection_process_plan_detail_prog  spp on spp.adm_selection_process_plan_detail_id = spd.adm_selection_process_plan_detail_id  "+
			        " where sp.record_status='A' "+
			 		" and spd.adm_selection_process_venue_city_id=:venue "+
			 		" and spd.selection_process_date=:selectionProcessDate ");
			if(!Utils.isNullOrEmpty(dto.getStartTime().getText()) && !Utils.isNullOrEmpty(dto.getEndTime().getText())){
			queryString.append(" and spdAllot.selection_process_time between :startTime and :endTime ");
			}
			if(!Utils.isNullOrEmpty(dto.getProgram().id)) {
				queryString.append(" and spp.erp_campus_programme_mapping_id = CampusProgramId ");
			}
			queryString.append("order by spdAllot.adm_selection_process_plan_detail_allotment_id ");
				 return  Mono.fromFuture(sessionFactory.withSession(s-> {
					 Mutiny.Query<Object[]> query = s.createNativeQuery(queryString.toString(),Object[].class);
					 query.setParameter("venue", dto.getVenue().id);
					 query.setParameter("selectionProcessDate", Utils.convertStringDateToLocalDate(dto.getSelectionProcessDate().text));
					if(!Utils.isNullOrEmpty(dto.getStartTime().getText()) && !Utils.isNullOrEmpty(dto.getEndTime().getText())){
						query.setParameter("startTime", dto.getStartTime().getText());
						query.setParameter("endTime", dto.getEndTime().getText());
					}
					if(!Utils.isNullOrEmpty(dto.getProgram().id)) {
						query.setParameter("CampusProgramId", dto.getProgram().id);
					}
					 return query.getResultList();
				 }).subscribeAsCompletionStage());
			
	}

	public void save(AdmSelectionProcessGroupDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}
	
	public void merge(AdmSelectionProcessGroupDBO dbo) {	
		//temp working
		 if(dbo.getId()==0) {
			 save(dbo);
		 }
		 //end
//		 not working
//		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}
	
	public void deleteOld(Set<Integer> set) {
		 sessionFactory.withTransaction((session, tx) -> 
         session.createQuery("select bo from AdmSelectionProcessGroupDBO bo"
         		+ " left join fetch bo.admSelectionProcessGroupDetailDBOsSet cbo"
         		+ "  where bo.recordStatus = 'A'"
         		+ " and bo.id in (:id)"
         		+ " and cbo.recordStatus = 'A'", AdmSelectionProcessGroupDBO.class)
        .setParameter("id", set)
        .getResultList()
        .invoke(data2->{ 
        	data2.stream().map(data->{
        	 data.setRecordStatus('D');
            	Set<AdmSelectionProcessGroupDetailDBO>  li = data.getAdmSelectionProcessGroupDetailDBOsSet().stream().map(i->{
            		 i.setRecordStatus('D');
            		 System.out.println("child");
            		 return i;
            	 }).collect(Collectors.toSet());
            	 data.setAdmSelectionProcessGroupDetailDBOsSet(li);
            	 return data;
        }).collect(Collectors.toSet());
        	
        })
).subscribeAsCompletionStage();
	}
	
	public Mono<List<AdmSelectionProcessGroupDBO>> getGroupAndGropDetails(Set<Integer> listAllotedIDs) {
		 String str = "select bo from AdmSelectionProcessGroupDBO bo "
		 		+ " left join fetch bo.admSelectionProcessPlanDetailAllotmentDBO "
		 		+ " left join fetch bo.admSelectionProcessGroupDetailDBOsSet cbo "
		 		+ " left join fetch cbo.studentApplnEntriesDBO "
		 		+ " left join fetch cbo.admSelectionProcessGroupDBO "
	            + " where bo.recordStatus = 'A' "
	            + " and bo.admSelectionProcessPlanDetailAllotmentDBO.id in (:allotedIds) "
	            + " and cbo.recordStatus = 'A' ";
	       return Mono.fromFuture(sessionFactory.withSession(session->session.createQuery(str, AdmSelectionProcessGroupDBO.class)
	    		   .setParameter("allotedIds", listAllotedIDs)
	    		   .getResultList()).subscribeAsCompletionStage());
	}


}
