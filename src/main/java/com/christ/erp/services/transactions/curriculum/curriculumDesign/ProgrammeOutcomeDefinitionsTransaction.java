package com.christ.erp.services.transactions.curriculum.curriculumDesign;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ObeProgrammeOutcomeDBO;
import reactor.core.publisher.Mono;

@Repository
public class ProgrammeOutcomeDefinitionsTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<Tuple> getGridData(String yearId, String departId) {
		String queryString = " select distinct obe_programme_outcome.obe_programme_outcome_id as progOutcomeId,"
				+ " obe_programme_outcome_details.obe_programme_outcome_details_id as progOutComeDetailsId,"
				+ " erp_programme_batchwise_settings.erp_programme_batchwise_settings_id as batchwiseId,"
				+ " erp_programme.programme_name as progName, erp_programme_level.programme_level as progLevel, erp_campus.campus_name as campus, erp_location.location_name as location"
				+ " from erp_programme_batchwise_settings"
				+ " inner join erp_programme ON erp_programme.erp_programme_id = erp_programme_batchwise_settings.erp_programme_id and erp_programme.record_status = 'A'"
				+ " inner join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme.erp_programme_level_id and erp_programme_level.record_status = 'A'"
				+ " inner join erp_campus_programme_mapping on erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_campus_programme_mapping.record_status = 'A'"
				+ " left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id"
				+ " left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id"
				+ " inner join obe_programme_outcome on erp_programme_batchwise_settings.erp_programme_batchwise_settings_id = obe_programme_outcome.erp_programme_batchwise_settings_id and obe_programme_outcome.record_status = 'A'"
				+ " left join obe_programme_outcome_details on obe_programme_outcome_details.obe_programme_outcome_id = obe_programme_outcome.obe_programme_outcome_id and obe_programme_outcome_details.record_status = 'A'"
				+ " where erp_programme.coordinating_department_id = :departId and erp_programme_batchwise_settings.batch_year_id = :yearId ";
		return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("yearId", Integer.parseInt(yearId)).setParameter("departId", Integer.parseInt(departId))
				.getResultList()).await().indefinitely();
	}

	public List<ObeProgrammeOutcomeDBO> edit(int batchwiseSettingId) {
		String query = " select distinct dbo from ObeProgrammeOutcomeDBO dbo"
				+ " left join fetch dbo.obeProgrammeOutcomeDetailsDBOSet opod"
				+ " left join fetch dbo.obeProgrammeOutcomeUploadDetailsDBOSet opoud"
				+ " left join fetch opod.erpProgrammePeoMissionMatrixDBOSet eppmm"
				+ " left join fetch opod.obeProgrammeOutcomeDetailsAttributeDBOSet opoda"
				+ " where dbo.recordStatus = 'A' and dbo.erpProgrammeBatchwiseSettingsDBO.id = :batchwiseSettingId";
		return sessionFactory.withSession(s -> s.createQuery(query, ObeProgrammeOutcomeDBO.class).setParameter("batchwiseSettingId", batchwiseSettingId).getResultList()).await().indefinitely();
	}

	public void update(List<ObeProgrammeOutcomeDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).subscribeAsCompletionStage();
	}

	public Mono<List<Tuple>> getProgrammeListToImport(int departmentId, int fromYearId) {
		String query = " select distinct  erp_programme.erp_programme_id as id, erp_programme.programme_name as programmeName  from erp_programme"
				+ " inner join erp_programme_batchwise_settings on erp_programme.erp_programme_id = erp_programme_batchwise_settings.erp_programme_id"
				+ " inner join obe_programme_outcome on obe_programme_outcome.erp_programme_batchwise_settings_id = erp_programme_batchwise_settings.erp_programme_batchwise_settings_id"
				+ " inner join obe_programme_outcome_details on obe_programme_outcome_details.obe_programme_outcome_id = obe_programme_outcome.obe_programme_outcome_id"
				+ " where erp_programme.coordinating_department_id = :departmentId and erp_programme_batchwise_settings.batch_year_id = :fromYearId and erp_programme.record_status = 'A'"
				+ " and erp_programme_batchwise_settings.record_status = 'A' and obe_programme_outcome.record_status = 'A' and obe_programme_outcome_details.record_status = 'A'"
				+ " order by programmeName";
		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(query, Tuple.class).setParameter("departmentId",departmentId).setParameter("fromYearId", fromYearId)
				.getResultList()).subscribeAsCompletionStage());
	}

	public List<ErpProgrammeBatchwiseSettingsDBO> getBatchwiseSettingsDetails(List<Integer> programmesIds,int toYearId) {
		String query = " select distinct dbo from ErpProgrammeBatchwiseSettingsDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.erpProgrammeDBO.id in(:programmeList) and dbo.erpAcademicYearDBO.id = :toYearId";
		return sessionFactory.withSession(s -> s.createQuery(query, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("programmeList", programmesIds)
				.setParameter("toYearId", toYearId).getResultList()).await().indefinitely();
	}

	public List<Tuple> duplicateCheck(List<Integer> programmesIds,int toYearId) {
		String query = " Select DISTINCT  erp_programme_batchwise_settings.erp_programme_id ,erp_programme.programme_name as programmeName  from  obe_programme_outcome_details"
				+ " inner join obe_programme_outcome  on obe_programme_outcome.obe_programme_outcome_id = obe_programme_outcome_details.obe_programme_outcome_id and obe_programme_outcome.record_status = 'A'"
				+ " inner join erp_programme_batchwise_settings  on  erp_programme_batchwise_settings.erp_programme_batchwise_settings_id = obe_programme_outcome.erp_programme_batchwise_settings_id and"
				+ " erp_programme_batchwise_settings.record_status = 'A' "
				+ " inner join erp_programme  on erp_programme.erp_programme_id = erp_programme_batchwise_settings.erp_programme_id"
				+ "  WHERE erp_programme_batchwise_settings.erp_programme_id in (:programmeList) and erp_programme_batchwise_settings.batch_year_id = :toYearId and obe_programme_outcome_details.record_status = 'A'";
		
		
		
//		String query = " select distinct dbo from ErpProgrammeBatchwiseSettingsDBO dbo"
//				+ " inner join fetch dbo.obeProgrammeOutcomeDBOSet opo"
//				+ " inner join fetch opo.obeProgrammeOutcomeDetailsDBOSet opod"
//				+ " where dbo.recordStatus = 'A' and dbo.erpProgrammeDBO.id in(:programmeList) and dbo.erpAcademicYearDBO.id = :toYearId";
		return sessionFactory.withSession(s -> s.createNativeQuery(query, Tuple.class).setParameter("programmeList", programmesIds).setParameter("toYearId", toYearId)
				.getResultList()).await().indefinitely();
	}
	
	public List<ErpProgrammeBatchwiseSettingsDBO> getPerviousData(List<Integer> programmesIds,int toYearId) {
		String query = " select distinct dbo from ErpProgrammeBatchwiseSettingsDBO dbo"
				+ " inner join fetch dbo.obeProgrammeOutcomeDBOSet opo"
				+ " inner join fetch opo.obeProgrammeOutcomeDetailsDBOSet opod"
				+ " left join fetch opo.obeProgrammeOutcomeUploadDetailsDBOSet opoud"
				+ " left join fetch opod.obeProgrammeOutcomeDetailsAttributeDBOSet opoa"
				+ " left join fetch opod.erpProgrammePeoMissionMatrixDBOSet epmm"
				+ " where dbo.recordStatus = 'A' and dbo.erpProgrammeDBO.id in(:programmeList) and dbo.erpAcademicYearDBO.id = :toYearId";
		return sessionFactory.withSession(s -> s.createQuery(query, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("programmeList", programmesIds).setParameter("toYearId", toYearId)
				.getResultList()).await().indefinitely();
	}


	public List<ErpProgrammeBatchwiseSettingsDBO> getExistData(List<Integer> programmesIds,int toYearId) {
		String query = " select distinct dbo from ErpProgrammeBatchwiseSettingsDBO dbo"
				+ " inner join fetch dbo.obeProgrammeOutcomeDBOSet opo"
				+ " where dbo.recordStatus = 'A' and dbo.erpProgrammeDBO.id in(:programmeList) and dbo.erpAcademicYearDBO.id = :toYearId";
		return sessionFactory.withSession(s -> s.createQuery(query, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("programmeList", programmesIds).setParameter("toYearId", toYearId)
				.getResultList()).await().indefinitely();
	}


	public void merge(List<ObeProgrammeOutcomeDBO> dbos) {
		sessionFactory.withTransaction((session, tx) ->  session.mergeAll(dbos.toArray())).await().indefinitely();
	}

}
