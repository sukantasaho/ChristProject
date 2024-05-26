package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;

import reactor.core.publisher.Mono;

@Repository
public class EntranceResultUploadTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<AdmSelectionProcessTypeDBO>> getEntranceTest() {
		String query = " select dbo from AdmSelectionProcessTypeDBO dbo where dbo.recordStatus = 'A' and dbo.mode in ('Center Based Entrance','Online Entrance')";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, AdmSelectionProcessTypeDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public void save (List<AdmSelectionProcessScoreDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}
	
	public Tuple getScoreCardId(int selectionTypeId) {
    	String query = " select adm_scorecard_id ,adm_selection_process_type_details_id from adm_selection_process_type_details where adm_selection_process_type_id = :selectionTypeId and record_status = 'A'";
        return  sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("selectionTypeId", selectionTypeId).getSingleResultOrNull()).await().indefinitely();
    }
	
	public List<Tuple> scorecardQuantitativeCheck(int scoreCardId) {
    	String query = " select adm_scorecard_quantitative_parameter_id,max_value from adm_scorecard_quantitative_parameter where adm_scorecard_quantitative_parameter.adm_scorecard_id = :scoreCardId and record_status = 'A'";
        return  sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("scoreCardId", scoreCardId).getResultList()).await().indefinitely();
    }
	
	public List<String> scorecardQualitativeCheck(int scoreCardId) {
    	String query = " select adm_scorecard_qualitative_parameter.adm_qualitative_parameter_id from adm_scorecard_qualitative_parameter where adm_scorecard_qualitative_parameter.adm_scorecard_id = :scoreCardId  and record_status = 'A'";
        return  sessionFactory.withSession(s->s.createNativeQuery(query,String.class).setParameter("scoreCardId", scoreCardId).getResultList()).await().indefinitely();
    }
	
	public List<StudentApplnEntriesDBO> getApplicantsDetails(List<Integer> applicationNos) {
    	String query = " select dbo from StudentApplnEntriesDBO dbo where dbo.recordStatus = 'A'  and dbo.applicationNo in( :applicationNos ) ";
        return  sessionFactory.withSession(s->s.createQuery(query,StudentApplnEntriesDBO.class).setParameter("applicationNos", applicationNos).getResultList()).await().indefinitely();
    }
	
	public List<AdmSelectionProcessDBO> getApplicantsSelectionProcessDetails(List<Integer> applicationNos, int yearId) {
    	String query = "select dbo from AdmSelectionProcessDBO dbo "
    			+ "	left join fetch dbo.studentApplnEntriesDBO AS sdbo "
    			+ "	left join fetch dbo.admSelectionProcessPlanDetailDBO AS sppdbo "
    			+ " where dbo.recordStatus = 'A' and  dbo.erpAcademicYearDBO.id = :yearId and sdbo.applicationNo in( :applicationNos )  ";
        return  sessionFactory.withSession(s->s.createQuery(query,AdmSelectionProcessDBO.class).setParameter("applicationNos", applicationNos)
        		.setParameter("yearId", yearId).getResultList()).await().indefinitely();
    }

	public List<AdmSelectionProcessScoreDBO> getAdmSelectionProcessScoreDBODetails(List<Integer> selectionProcessId,Integer selectionProcessTypeDeatilsId) {
		String str = "select dbo from AdmSelectionProcessScoreDBO dbo "
				+ "	left join fetch dbo.AdmSelectionProcessScoreEntryDBOSet AS aasdbs "
				+ "	left join fetch aasdbs.AdmSelectionProcessScoreEntryDetailsDBOSet AS aastbs "
				+ "	where dbo.recordStatus = 'A' and aasdbs.recordStatus = 'A' and  aastbs.recordStatus = 'A'"
				+ " and dbo.admSelectionProcessDBO.id in ( :selectionProcessId) "
				+ " and dbo.admSelectionProcessTypeDetailsDBO.id = :selectionProcessTypeDeatilsId "
				+ " ORDER BY dbo.id ";
		return sessionFactory.withSession(s->s.createQuery(str, AdmSelectionProcessScoreDBO.class).setParameter("selectionProcessId", selectionProcessId).setParameter("selectionProcessTypeDeatilsId", selectionProcessTypeDeatilsId).getResultList()).await().indefinitely();
	
	}

	public Tuple entranceResultUploadDownloadFormat() {
		String str = " select url_access_link.file_name_unique as fileNameUnique, url_access_link.file_name_original as fileNameOriginal,"
				+ "url_folder_list.upload_process_code as uploadProcessCode from url_folder_list "
				+ "inner join url_access_link On url_access_link.url_folder_list_id = url_folder_list.url_folder_list_id "
				+ "inner join erp_bulk_upload_format On erp_bulk_upload_format.bulk_upload_format_url_id = url_access_link.url_access_link_id "
				+ "where erp_bulk_upload_format.bulk_upload_format_name=:name  "
				+ "and url_folder_list.record_status='A' and url_access_link.record_status='A' and erp_bulk_upload_format.record_status='A' ";
		Tuple list = (Tuple) sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("name", "ENTRANCE_RESULT_UPLOAD");
			return query.getSingleResult();
		}).await().indefinitely();
		return list;
	}

}
