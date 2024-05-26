package com.christ.erp.services.transactions.curriculum.Classes;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dto.curriculum.settings.AcaClassDTO;
import reactor.core.publisher.Mono;

@Repository
public class ClassCreationTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<AcaClassDBO> getGridData(String academicYearId, String campusId, String sessionType) {
		String str = " select distinct dbo from AcaClassDBO dbo"
				+" inner join dbo.acaDurationDetailDBO adb"
				+" inner join adb.acaDurationDBO ad"
				+" where dbo.recordStatus ='A' and ad.erpAcademicYearDBO.id =:academicYearId";
		if(sessionType.equalsIgnoreCase("TERM")) {
			str += " and adb.acaBatchDBO.recordStatus ='A' and adb.acaBatchDBO.erpCampusProgrammeMappingDBO.recordStatus ='A'"
					+" and adb.acaBatchDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id =:campusId";
		}
		if(sessionType.equalsIgnoreCase("CREDIT") || sessionType.equalsIgnoreCase("SUBMISSION")) {
			str += " and adb.erpCampusProgrammeMappingDBO.erpCampusDBO.id =:campusId and adb.erpCampusProgrammeMappingDBO.recordStatus ='A'";
		} 
		String finalStr = str;
		List<AcaClassDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaClassDBO> query = s.createQuery(finalStr, AcaClassDBO.class);
			query.setParameter("academicYearId", Integer.parseInt(academicYearId));
			if(!Utils.isNullOrEmpty(campusId)) {
				query.setParameter("campusId", Integer.parseInt(campusId));
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public void update(AcaClassDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}

	public void save(AcaClassDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}
	
	public void saveOrUpdate(List<AcaClassDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
	}
	
	public AcaClassDBO edit(int id) {
		String str = " select dbo from AcaClassDBO dbo "
				+" where dbo.id =:id and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaClassDBO.class).setParameter("id",id).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(AcaClassDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public List<AcaClassDBO> isDuplicate(AcaClassDTO dto) {
		String str = " select distinct dbo from AcaClassDBO dbo where dbo.recordStatus = 'A' and "
				+" inner join dbo.acaDurationDetailDBO adbo"
				+" and dbo.acaDurationDetailDBO.acaDurationDBO.erpAcademicYearDBO.id =:academicYearId and dbo.className =:className ";
		if(!Utils.isNullOrEmpty(dto.getId())) {
			str += " and id !=: id";
		}
		String finalStr = str;
		List<AcaClassDBO> dbo = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaClassDBO> query = s.createQuery(finalStr, AcaClassDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("className", dto.getClassName().trim());
			query.setParameter("academicYearId", Integer.parseInt(dto.getAcademicYear().getValue()));
			return query.getResultList();
		}).await().indefinitely();
		return dbo;
	}

	public Integer getCampusDepartmentMapping(String campusId,String departmentId) {
		String str =" select erp_campus_department_mapping_id from erp_campus_department_mapping "
				+" where erp_campus_id =:campusId and erp_department_id =:departmentId and record_status ='A' " ;
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("campusId", Integer.parseInt(campusId)).setParameter("departmentId", Integer.parseInt(departmentId)).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<Tuple>> getBatchName(String batchYearId, String campusId, String programId) {
		String str = " select aca_batch.aca_batch_id as aca_batch_id, aca_batch.batch_name as batch_name,"
				+" erp_campus_programme_mapping_details.approved_intake as intake from aca_batch"
				+" inner join erp_programme_batchwise_settings ON erp_programme_batchwise_settings.erp_programme_batchwise_settings_id = aca_batch.erp_programme_batchwise_settings_id"
				+" and erp_programme_batchwise_settings.record_status ='A'"
				+" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = aca_batch.erp_campus_programme_mapping_id"
				+" and erp_campus_programme_mapping.record_status ='A'"
				+" inner join erp_campus_programme_mapping_details on erp_campus_programme_mapping.erp_campus_programme_mapping_id = erp_campus_programme_mapping_details.erp_campus_programme_mapping_id"
				+" and erp_programme_batchwise_settings.erp_programme_batchwise_settings_id = erp_campus_programme_mapping_details.erp_programme_batchwise_settings_id"
				+" and erp_campus_programme_mapping_details.record_status ='A'"
				+" where erp_programme_batchwise_settings.batch_year_id =:batchYearId and erp_programme_batchwise_settings.erp_programme_id =:programId and erp_campus_programme_mapping.erp_campus_id =:campusId"
				+" and aca_batch.record_status ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class)
				.setParameter("batchYearId", Integer.parseInt(batchYearId)).setParameter("campusId", Integer.parseInt(campusId))
				.setParameter("programId", Integer.parseInt(programId)).getResultList()).subscribeAsCompletionStage());		
	}

	public List<Tuple> getDurationDetailForSubmission( String campusId, String yearId, String typeId, String levelId) {
		String str = " select aca_duration.erp_academic_year_id as erp_academic_year_id, erp_academic_year.academic_year as academic_year,"
				+" erp_programme.programme_name as programme_name, erp_programme.programme_code as programme_code, erp_campus.short_name as campusCode, "
				+" aca_session.session_name as session, erp_programme.erp_programme_id as erp_programme_id, aca_duration_detail.aca_duration_detail_id as aca_duration_detail_id"
				+" from aca_duration_detail "
				+" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = aca_duration_detail.erp_campus_programme_mapping_id"
				+" and erp_campus_programme_mapping.record_status ='A'"
				+" inner join aca_duration ON aca_duration.aca_duration_id = aca_duration_detail.aca_duration_id "
				+" and aca_duration.record_status ='A'"
				+" inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = aca_duration.erp_academic_year_id and erp_academic_year.record_status ='A'"
				+" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id "
				+" and erp_programme.record_status ='A'"
				+" inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id "
				+" and erp_campus.record_status ='A'"
				+" inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id "
				+" and erp_programme_degree.record_status ='A'"
				+" inner join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme_degree.erp_programme_level_id"
				+" and erp_programme_level.record_status ='A'"
				+" inner join aca_session ON aca_session.aca_session_id = aca_duration_detail.aca_session_id"
				+" and aca_session.record_status ='A'"
				+" inner join aca_session_type ON aca_session_type.aca_session_type_id = aca_session.aca_session_type_id"
				+" and aca_session_type.record_status ='A'"
				+" where erp_programme_level.erp_programme_level_id =:levelId"
				+" and erp_campus.erp_campus_id =:campusId "
				+" and aca_duration.erp_academic_year_id =:yearId"
				+" and aca_session_type.aca_session_type_id =:typeId and aca_duration_detail.record_status ='A'";
		String finalStr = str;
		List<Tuple> dboList = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			query.setParameter("yearId", Integer.parseInt(yearId));
			query.setParameter("campusId",Integer.parseInt(campusId));
			query.setParameter("typeId",Integer.parseInt(typeId));
			query.setParameter("levelId",Integer.parseInt(levelId));
			return query.getResultList();
		}).await().indefinitely();
		return dboList;
	}

	public List<AcaDurationDetailDBO> getDurationDetailForTerm(String batchId) {
		String str = " select distinct dbo from AcaDurationDetailDBO dbo "
				+" where dbo.acaBatchDBO.id =:batchId and dbo.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaDurationDetailDBO.class)
				.setParameter("batchId", Integer.parseInt(batchId)).getResultList()).await().indefinitely();
	}

	public AcaClassDBO isClassCreated(String yearId, String campusCode, String section, String programId ) {
		String str = " select DISTINCT dbo from AcaClassDBO dbo where dbo.recordStatus ='A' and "
				+" dbo.campusCode =:campusCode and dbo.section =:section and dbo.acaDurationDetailDBO.acaDurationDBO.erpAcademicYearDBO.id =:yearId"
				+" and dbo.acaDurationDetailDBO.acaBatchDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id =:programId";
		String finalStr = str;
		AcaClassDBO dbo = sessionFactory.withSession(s-> {
			Mutiny.Query<AcaClassDBO> query = s.createQuery(finalStr, AcaClassDBO.class);
			if(!Utils.isNullOrEmpty(yearId)) { 
				query.setParameter("yearId", Integer.parseInt(yearId)); 
			}
			if(!Utils.isNullOrEmpty(campusCode)) { 
				query.setParameter("campusCode", campusCode); 
			}
			if(!Utils.isNullOrEmpty(section)) {
				query.setParameter("section", section);
			}
			if(!Utils.isNullOrEmpty(programId)) {
				query.setParameter("programId", Integer.parseInt(programId));
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return dbo;	
	}

	public List<AcaClassDBO> edit1(List<Integer> ids) {
		String str = " select dbo from AcaClassDBO dbo "
				+" where dbo.id IN (:ids) and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaClassDBO.class).setParameter("ids",ids).getResultList()).await().indefinitely();
	}

//	public void saveOrUpdate(List<AcaClassDBO> db) {
//		sessionFactory.withTransaction((session, tx) ->
//		session.mergeAll(db.stream().filter(p->!Utils.isNullOrEmpty(p.getId())).collect(Collectors.toList()).toArray())
//		.flatMap(q->session.persistAll(db.stream().filter(p->Utils.isNullOrEmpty(p.getId())).collect(Collectors.toList())))
//				).await().indefinitely();
//	}
}
