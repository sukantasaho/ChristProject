package com.christ.erp.services.transactions.curriculum.Classes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.AcaScriptsDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassGroupDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassGroupStudentsDBO;
import com.christ.erp.services.dto.curriculum.Classes.AcaClassGroupDTO;
import reactor.core.publisher.Mono;

@Repository
public class ClassGroupTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<AcaScriptsDBO> getAcaStudentList(Integer courseId, List<Integer> classListId, Integer activityId, Integer sessionGroupId, Integer academicYearId) {
		String str1 = " select distinct dbo from AcaClassGroupStudentsDBO dbo"
				+" inner join AcaClassGroupDetailsDBO dbos ON dbos.id = dbo.acaClassGroupDetailsDBO.id"
				+" inner join AcaClassGroupDBO gdbo ON gdbo.id = dbos.acaClassGroupDBO.id"
				+" where dbo.recordStatus ='A' and dbos.recordStatus ='A' and gdbo.recordStatus ='A' and "
				+" gdbo.acaDurationDBO.erpAcademicYearDBO.id =:academicYearId and gdbo.acaDurationDBO.acaSessionGroupDBO.id =:sessionGroupId";
		if(!Utils.isNullOrEmpty(classListId)) {
			str1 +=" and dbos.acaClassDBO.id in (:classListId)";
		}
		if(!Utils.isNullOrEmpty(courseId)) {
			str1 +=" and gdbo.acaCourseDBO.id =:courseId";
		}
		if(!Utils.isNullOrEmpty(activityId )) {
			str1 +=" and gdbo.attActivityDBO.id =:activityId";
		}
		String finalStr1 = str1;
		List<AcaClassGroupStudentsDBO> list1 = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaClassGroupStudentsDBO> query = s.createQuery(finalStr1, AcaClassGroupStudentsDBO.class);
			if (!Utils.isNullOrEmpty(classListId)) {
				query.setParameter("classListId", classListId);
			}
			if (!Utils.isNullOrEmpty(courseId)) {
				query.setParameter("courseId", courseId);
			}
			if (!Utils.isNullOrEmpty(activityId)) {
				query.setParameter("activityId", activityId);
			}
			query.setParameter("sessionGroupId", sessionGroupId);
			query.setParameter("academicYearId", academicYearId);

			return query.getResultList();
		}).await().indefinitely();
		var studList = list1.stream().map(s -> s.getStudentDBO().getId()).collect(Collectors.toList());
		String str =" select distinct dbo from AcaScriptsDBO dbo";
		if(!Utils.isNullOrEmpty(courseId)) {
			str +=" inner join dbo.acaCourseSessionwiseDBO cdbo"
					+" inner join cdbo.acaCourseYearwiseDBO acy"
					+" inner join acy.acaCourseDBO ac";
		}
		str +=" inner join dbo.acaStudentSessionwiseDBO wdbo"
				+" inner join wdbo.acaStudentYearwiseDBO ydbo "
				+" inner join ydbo.studentDBO sdbo "
				+" where dbo.recordStatus ='A' and wdbo.recordStatus ='A' and ydbo.recordStatus ='A'";
		if(!Utils.isNullOrEmpty(classListId)) {
			str +=" and (wdbo.acaVirtualClassDBO.id in (:classListId) or wdbo.acaClassDBO.id in (:classListId))";
		}
		if(!Utils.isNullOrEmpty(courseId)) {
			str +=" and cdbo.recordStatus ='A' and acy.recordStatus ='A' and ac.id =:courseId";
		}
		if(!Utils.isNullOrEmpty(studList)) {
			str +=" and sdbo.id not in (:studList)";
		}
		String finalStr = str;
		List<AcaScriptsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaScriptsDBO> query = s.createQuery(finalStr, AcaScriptsDBO.class);
			if (!Utils.isNullOrEmpty(classListId)) {
				query.setParameter("classListId", classListId);
			}
			if(!Utils.isNullOrEmpty(studList)) {
				query.setParameter("studList", studList);
			}
			if(!Utils.isNullOrEmpty(courseId)) {
				query.setParameter("courseId", courseId);
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<AcaClassGroupDBO> getGridData(String academicYearId, String sessionId, String campusId) {
		String str = "select distinct dbo from AcaClassGroupDBO dbo "
				+" left join fetch dbo.acaClassGroupDetailsDBOSet as dbos"
				+" where dbo.recordStatus ='A' and dbo.erpCampusDBO.id =:campusId and dbo.acaDurationDBO.erpAcademicYearDBO.id =:academicYearId and"
				+" dbo.acaDurationDBO.acaSessionGroupDBO.id =:sessionId ";
		return sessionFactory.withSession(s-> s.createQuery(str, AcaClassGroupDBO.class).setParameter("academicYearId", Integer.parseInt(academicYearId))
				.setParameter("sessionId", Integer.parseInt(sessionId)).setParameter("campusId", Integer.parseInt(campusId))
				.getResultList()).await().indefinitely();
	}

	public AcaClassGroupDBO edit(int id) {
		String str = " select dbo from AcaClassGroupDBO dbo "
				+" left join fetch dbo.acaClassGroupDetailsDBOSet as dbos"
				+" left join fetch dbos.acaClassGroupStudentDBOSet as sdbos"
				+ " where dbo.id =:id and dbo.recordStatus = 'A' and dbos.recordStatus ='A' and sdbos.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaClassGroupDBO.class).setParameter("id",id).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean duplicateCheck(AcaClassGroupDTO dto) {
		String str = " select dbo from AcaClassGroupDBO dbo"
				+" where dbo.recordStatus = 'A' and dbo.acaDurationDBO.erpAcademicYearDBO.id =:academicYearId and dbo.acaDurationDBO.acaSessionGroupDBO.id =:sessionGroupId"
				+" and dbo.classGroupName =:groupName";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and dbo.id !=:id";
		}
		String finalStr = str;
		List<AcaClassGroupDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaClassGroupDBO> query = s.createQuery(finalStr, AcaClassGroupDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("academicYearId", Integer.parseInt(dto.getAcademicYearId().getValue())); 
			query.setParameter("sessionGroupId" ,Integer.parseInt(dto.getSessionGroupId().getValue()));
			query.setParameter("groupName", dto.getClassGroupName());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public void update(AcaClassGroupDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(AcaClassGroupDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(AcaClassGroupDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, int userId) {
		sessionFactory.withTransaction((session, tx) -> session.createQuery( "select dbo from AcaClassGroupDBO dbo "
				+" left join fetch dbo.acaClassGroupDetailsDBOSet as dbos "
				+" left join fetch dbos.acaClassGroupStudentDBOSet as sdbos "
				+" where dbo.id =:id", AcaClassGroupDBO.class).setParameter("id", id).getSingleResultOrNull()
				.chain(dbo -> session.fetch(dbo.getAcaClassGroupDetailsDBOSet())
						.invoke(classGroupDetailsDBOS -> {
							classGroupDetailsDBOS.forEach(classGroupDetailsDBO -> {
								classGroupDetailsDBO.setRecordStatus('D');
								classGroupDetailsDBO.setModifiedUsersId(userId);
								Set<AcaClassGroupStudentsDBO> acaClassGroupStudentsDBOSet = classGroupDetailsDBO.getAcaClassGroupStudentDBOSet().stream().map(acaClassGroupStudentsDBO -> {
									acaClassGroupStudentsDBO.setModifiedUsersId(userId);
									acaClassGroupStudentsDBO.setRecordStatus('D');
									return acaClassGroupStudentsDBO;
								}).collect(Collectors.toSet());
								classGroupDetailsDBO.setAcaClassGroupStudentDBOSet(acaClassGroupStudentsDBOSet);
							});
							dbo.setRecordStatus('D');
							dbo.setModifiedUsersId(userId);
						}))).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public Integer getDuration(String academicYearId, String sessionGroupId) {
		String str = " select aca_duration.aca_duration_id as durationId from aca_duration "
				+" where aca_duration.erp_academic_year_id =:academicYearId and aca_duration.aca_session_group_id =:sessionGroupId ";
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("academicYearId", Integer.parseInt(academicYearId))
				.setParameter("sessionGroupId", Integer.parseInt(sessionGroupId))
				.getSingleResultOrNull()).await().indefinitely();	
	}

	public List<AcaClassGroupDBO> getAcaClassDBOList(List<Integer> classGroupIds) {
		String str = " select dbo from AcaClassGroupDBO dbo "
				+ " left join fetch dbo.acaClassGroupDetailsDBOSet"
				+ " where dbo.id IN (:classGroupIds) and dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,AcaClassGroupDBO.class).setParameter("classGroupIds",classGroupIds).getResultList()).await().indefinitely();
	}

	public List<AcaScriptsDBO> getCourseSessionId(List<Integer> classList) {
	 String str = " select distinct dbo from AcaScriptsDBO dbo"
			    +" inner join dbo.acaStudentSessionwiseDBO wdbo"
			    +" where dbo.recordStatus ='A' and wdbo.recordStatus ='A' and (wdbo.acaVirtualClassDBO.id IN (:classList) or wdbo.acaClassDBO.id IN (:classList))";
		return sessionFactory.withSession(s -> s.createQuery(str,AcaScriptsDBO.class).setParameter("classList",classList).getResultList()).await().indefinitely();
	}
}
