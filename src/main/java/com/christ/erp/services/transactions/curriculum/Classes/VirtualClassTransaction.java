package com.christ.erp.services.transactions.curriculum.Classes;

import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassVirtualClassMapDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dto.curriculum.settings.AcaClassDTO;

import reactor.core.publisher.Mono;

@Repository
public class VirtualClassTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<Tuple> getProgrammeClassList(String academicYearID, String campusID, String levelID,String sessionGroupID) {
		String str = " select  ac.aca_class_id as aca_class_id,ac.class_name as class_name, "
				+ " ep.programme_name as programme_name,ep.erp_programme_id as erp_programme_id, "
				+ " ab.aca_batch_id as aca_batch_id "
				+ " from aca_class as ac "
				+ " left join aca_duration_detail as addn ON addn.aca_duration_detail_id = ac.aca_duration_detail_id and ac.record_status = 'A' "
				+ " left join aca_duration as ad ON ad.aca_duration_id = addn.aca_duration_id and ad.record_status = 'A' "
				+ " left join aca_session_group as asg ON asg.aca_session_group_id = ad.aca_session_group_id and asg.record_status='A' "
				+ " left join erp_academic_year as eay ON eay.erp_academic_year_id = ad.erp_academic_year_id and eay.record_status = 'A' "
				+ " left join aca_batch as ab ON ab.aca_batch_id = addn.aca_batch_id and ab.record_status ='A' "
				+ " left join erp_campus_programme_mapping as ecpm ON if(ab.erp_campus_programme_mapping_id is not null,ab.erp_campus_programme_mapping_id,addn.erp_campus_programme_mapping_id) =ecpm.erp_campus_programme_mapping_id "
				+ " and ecpm.record_status='A' "
				+ " left join erp_campus as ec ON ec.erp_campus_id = ecpm.erp_campus_id and ec.record_status='A' "
				+ " left join erp_programme as ep ON ep.erp_programme_id = ecpm.erp_programme_id and ep.record_status='A' "
				+ " left join erp_programme_degree as epd ON epd.erp_programme_degree_id = ep.erp_programme_degree_id and epd.record_status='A' "
				+ " left join erp_programme_level as epl ON epl.erp_programme_level_id = epd.erp_programme_level_id and epl.record_status='A' "
				+ " where ac.record_status='A' and eay.erp_academic_year_id=:academicYearID "
				+ " and ec.erp_campus_id =:campusID and epl.erp_programme_level_id =:levelID and ad.aca_session_group_id=:sessionGroupID "
				+ " group by  ac.aca_class_id,ep.erp_programme_id,ab.aca_batch_id ";
		String finalStr = str;
		List<Tuple>  dboList = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			query.setParameter("academicYearID", Integer.parseInt(academicYearID));
			query.setParameter("campusID",Integer.parseInt(campusID));
			query.setParameter("levelID",Integer.parseInt(levelID));
			query.setParameter("sessionGroupID",Integer.parseInt(sessionGroupID));
			return query.getResultList();
		}).await().indefinitely();
		return dboList;
	}

	public Integer getCampusDepartmentMapping(String campusId,String departmentId) {
		String str =" select erp_campus_department_mapping_id from erp_campus_department_mapping "
				+ " where erp_campus_id = :campusId and erp_department_id = :departmentId and record_status = 'A' " ;
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("campusId", Integer.parseInt(campusId)).setParameter("departmentId", Integer.parseInt(departmentId)).getSingleResultOrNull()).await().indefinitely();
	}

	public AcaClassDBO edit(int id) {
		String str = " select dbo from AcaClassDBO dbo "
				+ " left join fetch dbo.acaClassVirtualClassMapDBOSet "
				+ " where dbo.id = : id and dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,AcaClassDBO.class).setParameter("id",id).getSingleResultOrNull()).await().indefinitely();
	}

	public List<AcaClassDBO> getAcaClassDBOList(List<Integer> subIds) {
		String str = " select dbo from AcaClassDBO dbo "
				+ " left join fetch dbo.acaClassVirtualClassMapDBOSet "
				+ " where dbo.id IN (:subIds) and dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,AcaClassDBO.class).setParameter("subIds",subIds).getResultList()).await().indefinitely();
	}

	public void saveList(List<AcaClassDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
	}

	public void update(AcaClassDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}

	public void save(AcaClassDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(AcaClassDBO.class, id)
				.chain(dbo1 -> session.fetch(dbo1.getAcaClassVirtualClassMapDBOSet())
						.invoke(subSet -> {
							subSet.forEach(subDbo -> {
								subDbo.setRecordStatus('D');
								subDbo.setModifiedUsersId(userId);
							});
							dbo1.setRecordStatus('D');
							dbo1.setModifiedUsersId(userId);
						})
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public Integer getAcaDurationId(String academicId, String sessionGroupId) {
		String str =" select aca_duration_id from aca_duration "
				+ " where erp_academic_year_id = :academicId and aca_session_group_id = :sessionGroupId and record_status = 'A' " ;
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("academicId", Integer.parseInt(academicId)).setParameter("sessionGroupId", Integer.parseInt(sessionGroupId)).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean duplicateCheck(AcaClassDTO dto) {
		String str = " select dbo from AcaClassDBO dbo where dbo.recordStatus = 'A' and "
				+ " dbo.acaDurationDBO.recordStatus = 'A' and dbo.acaDurationDBO.erpAcademicYearDBO.recordStatus = 'A' "
				+ " and dbo.acaDurationDBO.erpAcademicYearDBO.id = :academicYearId and dbo.className = :className ";
		if(!Utils.isNullOrEmpty(dto.getId())) {
			str += " and id !=: id";
		}
		String finalStr = str;
		AcaClassDBO dbo = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaClassDBO> query = s.createQuery(finalStr, AcaClassDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("className", dto.getClassName().trim());
			query.setParameter("academicYearId", Integer.parseInt(dto.getAcademicYear().getValue()));
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(dbo) ? false : true;
	}

	public List<AcaClassDBO> getGridData(String academicYearId, String campusId) {
		String str = " select DISTINCT dbo from AcaClassDBO dbo "
				+ " left join fetch dbo.acaClassVirtualClassMapDBOSet as acvcms "
				+ " where dbo.recordStatus = 'A' and "
				+ " dbo.acaDurationDBO.recordStatus = 'A' and dbo.erpCampusDepartmentMappingDBO.recordStatus = 'A' and "
				+ " dbo.acaDurationDBO.erpAcademicYearDBO.recordStatus = 'A' and dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.recordStatus = 'A' and "
				+ " dbo.acaDurationDBO.erpAcademicYearDBO.id = :academicYearId and dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.id = :campusId ";
		String finalStr = str;
		List<AcaClassDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<AcaClassDBO> query = s.createQuery(finalStr,AcaClassDBO.class);
			if(!Utils.isNullOrEmpty(academicYearId)) {
				query.setParameter("academicYearId", Integer.parseInt(academicYearId));
			}
			if(!Utils.isNullOrEmpty(campusId)) {
				query.setParameter("campusId", Integer.parseInt(campusId));
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<AcaClassVirtualClassMapDBO> getAcaVirtualClassDBOList(List<Integer> subIds) {
		String str = " select dbo from AcaClassVirtualClassMapDBO dbo "
				+ " where dbo.acaBaseClassDBO.id IN (:subIds) and dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,AcaClassVirtualClassMapDBO.class).setParameter("subIds",subIds).getResultList()).await().indefinitely();
	}
}