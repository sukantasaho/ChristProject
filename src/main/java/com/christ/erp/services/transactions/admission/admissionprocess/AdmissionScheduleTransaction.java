package com.christ.erp.services.transactions.admission.admissionprocess;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmAdmissionScheduleDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmAdmissionScheduleDatesDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmAdmissionScheduleTimeDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;

import reactor.core.publisher.Mono;

@Repository
public class AdmissionScheduleTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<AdmAdmissionScheduleDBO>> getGridData() {
		String str = " select dbo from AdmAdmissionScheduleDBO dbo where dbo.recordStatus = 'A' and "
				+ " dbo.erpAcademicYearDBO.recordStatus = 'A' and dbo.erpCampusDBO.recordStatus = 'A' "
				+ " ORDER BY dbo.erpAcademicYearDBO.academicYearName";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, AdmAdmissionScheduleDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public void update(AdmAdmissionScheduleDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(AdmAdmissionScheduleDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public boolean save(AdmAdmissionScheduleDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
		return true;
	}
	
	public AdmAdmissionScheduleDBO getData(Integer academicYearId,Integer campusId) {
		String str = "select dbo from AdmAdmissionScheduleDBO dbo "
				+ "	left join fetch dbo.admAdmissionScheduleDatesDBOSet AS aasdbs "
				+ "	left join fetch aasdbs.admAdmissionScheduleTimeDBOSet AS aastbs "
				+ "	where dbo.recordStatus = 'A' "
				+ " and dbo.erpAcademicYearDBO.recordStatus = 'A' "
				+ " and dbo.erpCampusDBO.recordStatus = 'A' "
				+ " and dbo.erpAcademicYearDBO.id = :academicYearId "
				+ " and dbo.erpCampusDBO.id = :campusId "
				+ " ORDER BY dbo.id ";
		return sessionFactory.withSession(s->s.createQuery(str, AdmAdmissionScheduleDBO.class).setParameter("academicYearId", academicYearId).setParameter("campusId", campusId).getSingleResultOrNull()).await().indefinitely();
	}

//	public AdmAdmissionScheduleDBO getData1(Integer id) {
//		String str = "select dbo from AdmAdmissionScheduleDBO dbo "
//				+ "	left join fetch dbo.admAdmissionScheduleDatesDBOSet AS aasdbs "
//				+ "	left join fetch aasdbs.admAdmissionScheduleTimeDBOSet AS aastbs "
//				+ "	where dbo.recordStatus = 'A' "
//				+ " and dbo.erpAcademicYearDBO.recordStatus = 'A' "
//				+ " and dbo.erpCampusDBO.recordStatus = 'A' "
//				+ " and dbo.id = :id "
//				+ " ORDER BY dbo.id ";
//		return sessionFactory.withSession(s->s.createQuery(str, AdmAdmissionScheduleDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
//	}
//	
	
	
	public AdmAdmissionScheduleDBO getData2(Integer id) {
		String query1 = " select dbo from AdmAdmissionScheduleDBO dbo" +
				" left join fetch dbo.erpAcademicYearDBO eay" +
				" where dbo.recordStatus='A' and dbo.id = :id" ;
		AdmAdmissionScheduleDBO admAdmissionScheduleDBO = sessionFactory.withSession(s->s.createQuery(query1,AdmAdmissionScheduleDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();

		String query2 = " select dbo from AdmAdmissionScheduleDatesDBO dbo " +
				" where dbo.recordStatus='A' and dbo.admAdmissionScheduleDBO.id =:id";
		List<AdmAdmissionScheduleDatesDBO> admAdmissionScheduleDatesDBOList = sessionFactory.withSession(s->s.createQuery(query2, AdmAdmissionScheduleDatesDBO.class).setParameter("id", id).getResultList()).await().indefinitely();

		Set<Integer> admAdmissionScheduleDatesIdSet = admAdmissionScheduleDatesDBOList.stream().map(s->s.getId()).collect(Collectors.toSet());
		String query3 = "select dbo from AdmAdmissionScheduleTimeDBO dbo" +
				" left join fetch dbo.studentApplnEntriesDBOs sae" +
				" where dbo.recordStatus='A'  and dbo.admAdmissionScheduleDatesDBO.id in (:ids)"; //and  sae.recordStatus='A'
		List<AdmAdmissionScheduleTimeDBO> admAdmissionScheduleTimeDBOList = sessionFactory.withSession(s->s.createQuery(query3, AdmAdmissionScheduleTimeDBO.class).setParameter("ids", admAdmissionScheduleDatesIdSet).getResultList()).await().indefinitely();
	
//		Set<Integer> admAdmissionScheduleTimeIdSet = admAdmissionScheduleTimeDBOList.stream().map(s->s.getId()).collect(Collectors.toSet());
//		String query4 = "select dbo from StudentApplnEntriesDBO dbo" +
//				" where dbo.recordStatus='A' and dbo.admAdmissionScheduleTimeDBO.id in (:ids)";
//		List<StudentApplnEntriesDBO> studentApplnEntriesDBOList = sessionFactory.withSession(s->s.createQuery(query4, StudentApplnEntriesDBO.class).setParameter("ids", admAdmissionScheduleTimeIdSet).getResultList()).await().indefinitely();
//	
		
		
		Map<Integer, Set<AdmAdmissionScheduleTimeDBO>> admAdmissionScheduleTimeDBOMap = admAdmissionScheduleTimeDBOList.stream().collect(Collectors.groupingBy(s->s.getAdmAdmissionScheduleDatesDBO().getId(), Collectors.toSet()));
//		Map<Integer, Set<StudentApplnEntriesDBO>> studentApplnEntriesDBOMap = studentApplnEntriesDBOList.stream().collect(Collectors.groupingBy(s->s.getAdmAdmissionScheduleTimeDBO().getId(), Collectors.toSet()));
//		
//		admAdmissionScheduleTimeDBOList.forEach(s->{
//			s.setStudentApplnEntriesDBOs(new HashSet<>());
//			if (!Utils.isNullOrEmpty(studentApplnEntriesDBOMap)) {
//				s.setStudentApplnEntriesDBOs(studentApplnEntriesDBOMap.get(s.getId()));
//			}
//		});
//		admAdmissionScheduleDatesDBOList.setAdmAdmissionScheduleDatesDBOSet(new HashSet<>());
//		admAdmissionScheduleDatesDBOList.setAdmAdmissionScheduleDatesDBOSet(admAdmissionScheduleDatesDBOList.stream().collect(Collectors.toSet()));
//		
		
		admAdmissionScheduleDatesDBOList.forEach(s->{
			s.setAdmAdmissionScheduleTimeDBOSet(new HashSet<>());
			if (!Utils.isNullOrEmpty(admAdmissionScheduleTimeDBOMap)) {
				s.setAdmAdmissionScheduleTimeDBOSet(admAdmissionScheduleTimeDBOMap.get(s.getId()));
			}
		});
		admAdmissionScheduleDBO.setAdmAdmissionScheduleDatesDBOSet(new HashSet<>());
		admAdmissionScheduleDBO.setAdmAdmissionScheduleDatesDBOSet(admAdmissionScheduleDatesDBOList.stream().collect(Collectors.toSet()));
		return admAdmissionScheduleDBO;
		
	}
	
}
