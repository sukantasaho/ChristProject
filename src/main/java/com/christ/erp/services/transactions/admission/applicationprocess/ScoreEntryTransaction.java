package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import reactor.core.publisher.Mono;

@Repository
public class ScoreEntryTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	 
	public Mono<List<Tuple>> getSelectionProcessList(int applicationNumber,String mode) {
			String queryString = "select adm_selection_process_type.adm_selection_process_type_id as adm_selection_process_type_id," +
	 		       " adm_selection_process_type.selection_stage_name as selection_stage_name," + 
	 		       " student_appln_entries.application_no as application_no,"+
	 		       " student_appln_entries.student_appln_entries_id as student_appln_entries_id," + 
	 		       " student_appln_entries.applicant_name as applicant_name," + 
	 		       " erp_programme.programme_name as programme_name," + 
	 		       " erp_location.location_name as location_name," + 
	 		       " erp_campus.campus_name as campus_name," + 
	 		       " adm_selection_process.adm_selection_process_id as adm_selection_process_id," + 
	 		       " student_personal_data_addtnl.profile_photo_url as profile_photo_url," + 
	 		       " adm_selection_process_plan_detail.selection_process_date as selectionDate, adm_selection_process_plan_detail.process_order as processOrder," +
			       " url.file_name_unique, url.file_name_original, folder.upload_process_code" +
	 		       " from adm_selection_process " + 
	 		       " inner join student_appln_entries ON student_appln_entries.student_appln_entries_id = adm_selection_process.student_appln_entries_id" + 
	 		       " inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id" + 
	 		       " inner join student_personal_data ON student_personal_data.student_personal_data_id = student_appln_entries.student_personal_data_id" + 
	 		       " inner join student_personal_data_addtnl ON student_personal_data_addtnl.student_personal_data_addtnl_id = student_personal_data.student_personal_data_addtnl_id" + 
	 		       " left join erp_programme  ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id" + 
	 		       " left join erp_campus  ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id" + 
	 		       " left join erp_location  ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id" + 
	 		       " inner join adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process.adm_selection_process_plan_detail_id" + 
	 		       " inner join adm_selection_process_type ON adm_selection_process_type.adm_selection_process_type_id = adm_selection_process_plan_detail.adm_selection_process_type_id" +
		           " left join url_access_link url ON student_appln_entries.profile_photo_url_id = url.url_access_link_id" +
			       " left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"+
	 		       " where student_appln_entries.application_no=:appNo and adm_selection_process_type.mode=:mode" + 
	 		       " and adm_selection_process.record_status = 'A' and student_appln_entries.record_status = 'A'and erp_campus_programme_mapping.record_status = 'A'" + 
	 		       " and adm_selection_process_plan_detail.record_status = 'A' and adm_selection_process_type.record_status = 'A'"; // MySql query
            Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory
            		.withSession(s->s.createNativeQuery(queryString, Tuple.class).setParameter("appNo", applicationNumber).setParameter("mode", mode).getResultList()).subscribeAsCompletionStage());	        
            return list;
    }
	 
	public Mono<List<AdmSelectionProcessTypeDBO>> getSubprocess(int typeId) {
	       String str = "select distinct  bo from AdmSelectionProcessTypeDBO bo "
	       		+ " left join fetch bo.admissionSelectionProcessTypeDetailsDBOSet cbo "
	       		+ " left join fetch cbo.admissionScoreCardDBO card"
	       		+ " left join fetch card.admScoreCardQuantitativeParameterDBO ascqt"
	       		+ " left join fetch card.admScoreCardQualitativeParameterDBO ascql"
	       		+ " left join fetch ascql.admQualitativeParamterDBO aqp"
	       		+ " left join fetch aqp.admQualitativeParameterOptionSet aqpo"
	       		+ " where bo.recordStatus = 'A' and bo.id = :id order by cbo.order ";
	       return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, AdmSelectionProcessTypeDBO.class).setParameter("id", typeId).getResultList()).subscribeAsCompletionStage()); 
	}

	public void save (List<AdmSelectionProcessScoreDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.persistAll(dbos.toArray())).subscribeAsCompletionStage();
	}
	
	public List<Tuple> duplicateCheck(Integer processId, Integer typeDetailsId, String userId) {
		String queryString = " select adm_selection_process_score_entry.score_entered_time as score_entered_time , adm_selection_process_score.adm_selection_process_score_id as adm_selection_process_score_id from  adm_selection_process_score"
	           + "  inner join adm_selection_process_score_entry on  adm_selection_process_score.adm_selection_process_score_id = adm_selection_process_score_entry.adm_selection_process_score_id"
		 	   + "  where adm_selection_process_score.adm_selection_process_id = :processId"
		 	   + "  and adm_selection_process_score.adm_selection_process_type_details_id = :typeDetailsId"
		 	   + "  and adm_selection_process_score_entry.erp_users_id = :userId"
		 	   + "  and adm_selection_process_score.record_status = 'A' "
		 	   + "  and adm_selection_process_score_entry.record_status = 'A'";
	      return sessionFactory.withSession(s->s.createNativeQuery(queryString, Tuple.class).setParameter("processId", processId).setParameter("typeDetailsId", typeDetailsId)
	        	  .setParameter("userId", Integer.parseInt(userId)).getResultList()).await().indefinitely();        
	}
	 
	public List<Tuple> getTypeDetailsId(Integer typeId) {
		String queryString = " select distinct adm_selection_process_type_details.adm_selection_process_type_details_id as adm_selection_process_type_details_id,adm_selection_process_plan_detail.selection_process_date as selection_process_date from adm_selection_process_type"
		 	   + " inner join  adm_selection_process_type_details on adm_selection_process_type.adm_selection_process_type_id = adm_selection_process_type_details.adm_selection_process_type_id"
		 	   + " inner join  adm_selection_process_plan_detail on  adm_selection_process_plan_detail.adm_selection_process_type_id = adm_selection_process_type_details.adm_selection_process_type_id"
		 	   + " inner join adm_selection_process on  adm_selection_process.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail.adm_selection_process_plan_detail_id"
		 	   + " where adm_selection_process_type.adm_selection_process_type_id = :typeId"
		 	   + " and adm_selection_process_type.record_status = 'A'"
		 	   + " and adm_selection_process_type_details.record_status = 'A' ";
	      return sessionFactory.withSession(s->s.createNativeQuery(queryString, Tuple.class).setParameter("typeId", typeId).getResultList()).await().indefinitely();    
	}
	
	public List<Tuple> getGroupSelectionProcessList(int timeId) {
		String queryString =" select distinct adm_selection_process_type.adm_selection_process_type_id as adm_selection_process_type_id, adm_selection_process_type.selection_stage_name as selection_stage_name from adm_selection_process_plan_detail_allotment"
				+ "  inner join adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_id"
				+ "  inner join adm_selection_process_type on adm_selection_process_plan_detail.adm_selection_process_type_id = adm_selection_process_type.adm_selection_process_type_id"
				+ "  where adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_allotment_id = :timeId  and  adm_selection_process_type.mode = 'Group Process' ";
	    return sessionFactory.withSession(s->s.createNativeQuery(queryString, Tuple.class).setParameter("timeId", timeId).getResultList()).await().indefinitely();	
	}

	public List<Tuple> getGroupSubProcessList(int typeId) {
		String queryString ="select adm_selection_process_type_details_id as adm_selection_process_type_details_id,sub_process_name as sub_process_name  from adm_selection_process_type_details "
				+ "where adm_selection_process_type_id = :typeId  ";
		return sessionFactory.withSession(s->s.createNativeQuery(queryString, Tuple.class).setParameter("typeId", typeId).getResultList()).await().indefinitely();		
	}

	public Mono<List<AdmSelectionProcessTypeDBO>> getGroupSubProcessData(int subProcessId) {
		 String str = "select bo from AdmSelectionProcessTypeDBO bo left join fetch bo.admissionSelectionProcessTypeDetailsDBOSet cbo where bo.recordStatus = 'A' and cbo.id = :id order by cbo.order";
		 return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, AdmSelectionProcessTypeDBO.class).setParameter("id", subProcessId).getResultList()).subscribeAsCompletionStage()); 
	}
	
	public Tuple getSubProcessDate(Integer typeId) {
		String queryString = " select distinct adm_selection_process_plan_detail.selection_process_date as date from adm_selection_process_type"
				+ " inner join  adm_selection_process_type_details on adm_selection_process_type.adm_selection_process_type_id = adm_selection_process_type_details.adm_selection_process_type_id"
				+ " inner join  adm_selection_process_plan_detail on  adm_selection_process_plan_detail.adm_selection_process_type_id = adm_selection_process_type_details.adm_selection_process_type_id"
				+ " where adm_selection_process_type_details.adm_selection_process_type_details_id = :typeId"
				+ " and adm_selection_process_type.record_status = 'A' and adm_selection_process_plan_detail.record_status = 'A'"
				+ " and adm_selection_process_type_details.record_status = 'A'  ";
	      return sessionFactory.withSession(s->s.createNativeQuery(queryString, Tuple.class).setParameter("typeId", typeId).getSingleResultOrNull()).await().indefinitely();    
	}

	public Tuple duplicateGroupScoreCheck(int groupId, int subProcessId, String userId) {
		String queryString ="select distinct  adm_selection_process_score_entry.score_entered_time as score_entered_time ,sub_process_name as sub_process_name from adm_selection_process_score"
				+" inner join adm_selection_process_score_entry on adm_selection_process_score.adm_selection_process_score_id = adm_selection_process_score_entry.adm_selection_process_score_id"
				+" inner join adm_selection_process_type_details on adm_selection_process_score.adm_selection_process_type_details_id = adm_selection_process_type_details.adm_selection_process_type_details_id"
				+" where adm_selection_process_score.adm_selection_process_group_id = :groupId and adm_selection_process_score.adm_selection_process_type_details_id = :subProcessId"
				+" and adm_selection_process_score_entry.erp_users_id = :userId";
		return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("groupId", groupId).setParameter("subProcessId", subProcessId)
				.setParameter("userId", userId).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<Tuple> getGroupApplicantsData(String groupId) {
		String queryString = " select student_appln_entries.student_appln_entries_id as student_appln_entries_id ,student_appln_entries.application_no as application_no ,student_appln_entries.applicant_name as applicant_name ,"
				+ "  concat(erp_programme.programme_name, ' (', ifnull(erp_campus.campus_name, erp_location.location_name), ')') as campus_program_name,"
				+ "  student_personal_data_addtnl.profile_photo_url as profile_photo_url,"
				+ "  adm_selection_process.adm_selection_process_id as adm_selection_process_id"
				+ "  from adm_selection_process_group"
				+ "  inner join adm_selection_process_group_detail on  adm_selection_process_group_detail.adm_selection_process_group_id = adm_selection_process_group.adm_selection_process_group_id"
				+ "  inner join student_appln_entries on adm_selection_process_group_detail.student_appln_entries_id = student_appln_entries.student_appln_entries_id"
				+ "  inner join adm_selection_process  on adm_selection_process.student_appln_entries_id = student_appln_entries.student_appln_entries_id"
				+ "  inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+ "  inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id"
				+ "  left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id"
				+ "  left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id"
				+ "  inner join student_personal_data on student_appln_entries.student_personal_data_id = student_personal_data.student_personal_data_id"
				+ "  inner join student_personal_data_addtnl on  student_personal_data_addtnl.student_personal_data_addtnl_id = student_personal_data.student_personal_data_addtnl_id"
				+ "  where adm_selection_process_group_detail.adm_selection_process_group_id = :id and adm_selection_process_group.record_status = 'A'"
				+ "  and  adm_selection_process_group_detail.record_status = 'A' and student_appln_entries.record_status = 'A'"
				+ "  and erp_campus_programme_mapping.record_status = 'A' and erp_programme.record_status = 'A' and adm_selection_process.record_status = 'A'";
		return  sessionFactory.withSession(s-> {
			 Mutiny.Query<Tuple> query = s.createNativeQuery(queryString,Tuple.class).setParameter("id", groupId);
			 return query.getResultList();
		 }).await().indefinitely();
	}
	
	 
}