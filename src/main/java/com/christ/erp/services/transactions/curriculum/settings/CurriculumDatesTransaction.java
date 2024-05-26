package com.christ.erp.services.transactions.curriculum.settings;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;



@Repository
public class CurriculumDatesTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<AcaDurationDetailDBO> getAcaDurationdetail(Integer academicYearId, List<Integer> sessionIdList,List<Integer> campusIdList) {
		String sessionTypeName = null;
		if (!Utils.isNullOrEmpty(campusIdList) && !Utils.isNullOrEmpty(sessionIdList)) {
			String str1= "select distinct dbo.acaSessionType.curriculumCompletionType from AcaSessionDBO dbo"
					+ " where dbo.id =: id ";
			sessionTypeName = sessionFactory.withSession(s -> s.createQuery(str1, String.class).setParameter("id",sessionIdList.get(0)).getSingleResultOrNull()).await().indefinitely();			
		}
		String str = "select distinct dbo from AcaDurationDetailDBO dbo"
				+ " inner join dbo.acaDurationDBO ad"
				+ " inner join dbo.acaSessionDBO ac"
				+ " where dbo.recordStatus = 'A' and ad.recordStatus = 'A' and ac.recordStatus = 'A' and ad.erpAcademicYearDBO.id =: academicYearId and ac.id in (:sessionIdList)";	
		if (!Utils.isNullOrEmpty(campusIdList)) {
			if(sessionTypeName.equals("TERM")) {
				str += " and dbo.acaBatchDBO.recordStatus = 'A' and dbo.acaBatchDBO.erpCampusProgrammeMappingDBO.recordStatus = 'A'"
						+ " and dbo.acaBatchDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id in (:campusIdList)";
			} else {
				str += " and dbo.erpCampusProgrammeMappingDBO.recordStatus = 'A' and dbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id in (:campusIdList)";
			}
		}
		String finalStr = str;
		List<AcaDurationDetailDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaDurationDetailDBO> query = s.createQuery(finalStr, AcaDurationDetailDBO.class);
			if (!Utils.isNullOrEmpty(campusIdList)) {
				query.setParameter("campusIdList", campusIdList);
			}
			query.setParameter("sessionIdList",sessionIdList);
			query.setParameter("academicYearId", academicYearId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<AcaDurationDetailDBO> getAcaDurationDetailByIds(List<Integer> list) {
		String str = "select distinct dbo from AcaDurationDetailDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.id in (:list)";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaDurationDetailDBO.class).setParameter("list", list).getResultList()).await().indefinitely();
	}

	public void updateDurationList(List<AcaDurationDetailDBO> durationList) {		
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(durationList.toArray())).subscribeAsCompletionStage();	
	}
}