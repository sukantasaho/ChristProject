package com.christ.erp.services.transactions.curriculum.settings;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpApprovalLevelsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCourseCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;

import reactor.core.publisher.Mono;

@Repository
public class ProgrammeMasterTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<ErpAcademicYearDBO> getAdmissionYear() {
		String str = "from ErpAcademicYearDBO e where e.recordStatus = 'A' and e.academicYear <= ((select ea.academicYear from ErpAcademicYearDBO ea where ea.isCurrentAdmissionYear = 1 and ea.recordStatus = 'A') + 1) order by e.academicYearName desc";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpAcademicYearDBO.class).getResultList()).await().indefinitely();
	}

	public Mono<List<ErpCourseCategoryDBO>> getAddOnCourses() {
		String str = "from ErpCourseCategoryDBO dbo where dbo.recordStatus = 'A' and isAddOn = 1";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpCourseCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<ErpApprovalLevelsDBO> getProgrammeApprovers(String programmeOrExternal) {
		String str = null;
		if (programmeOrExternal.equalsIgnoreCase("programme")) {
			str = "from ErpApprovalLevelsDBO dbo where dbo.recordStatus = 'A' and isForProgramme = 1 and isExternal = 0";
		} else if (programmeOrExternal.equalsIgnoreCase("external")) {
			str = "from ErpApprovalLevelsDBO dbo where dbo.recordStatus = 'A' and isForProgramme = 1 and isExternal = 1";
		}
		String finalStr = str;
		return sessionFactory.withSession(s -> s.createQuery(finalStr, ErpApprovalLevelsDBO.class).getResultList()).await().indefinitely();
	}

	public Mono<List<ErpProgrammeDBO>> getGridData(Integer academicYear) {
		String str = "from ErpProgrammeDBO e left join fetch e.erpProgrammeAddtnlDetailsDBOSet as ead"
				+ " left join fetch e.erpProgrammeBatchwiseSettingsDBOSet"
				+ " where e.recordStatus = 'A' and ead.recordStatus = 'A'and (:academicYear) between ead.changedFromYear and ead.changedToYear order by ead.changedFromYear";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpProgrammeDBO.class).setParameter("academicYear", academicYear).getResultList())
				.subscribeAsCompletionStage());
	}

	public ErpProgrammeDBO editProgramme(int id, Integer changedYear) {
		String str = "from ErpProgrammeDBO e left join fetch e.erpProgrammeAddtnlDetailsDBOSet as ead"
				+ " left join fetch e.erpProgrammeDepartmentMappingDBOSet as edm"
				+ " left join fetch e.erpCampusProgrammeMappingDBOSet as ecm"
				+ " left join fetch e.erpProgrammeRBTDomainsMappingDBOSet as rbt"
				+ " left join fetch e.erpProgrammeApprovalLevelMappingDBOSet as alm"				
				+ " left join fetch alm.erpProgrammeApprovalLevelDocumentsDBOSet as ald"
				+ " where e.recordStatus = 'A' "
				+ " and e.id= :id and (:changedYear) between ead.changedFromYear and ead.changedToYear";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpProgrammeDBO.class).setParameter("id", id).setParameter("changedYear", changedYear).getSingleResultOrNull()).await().indefinitely();
	}

	public Integer academicYear(Integer academicYearId) {
		String str1 = "select eay.academicYear from ErpAcademicYearDBO eay where eay.recordStatus = 'A' and eay.id = :academicYearId";
		return sessionFactory.withSession(s -> s.createQuery(str1, Integer.class).setParameter("academicYearId", academicYearId).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean duplicateCheck(ErpProgrammeDTO dto) {
		String str = "from ErpProgrammeDBO ep where ep.recordStatus='A' and"
				+ " (replace(replace(replace(trim(ep.programmeName),' ','<>'),'><',''),'<>',' ') like :programmeName"
				+ " or replace(replace(replace(trim(ep.programmeCode),' ','<>'),'><',''),'<>',' ') like :programmeCode)";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and ep.id != :id";
		}
		String finalStr = str;
		List<ErpProgrammeDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpProgrammeDBO> query = s.createQuery(finalStr, ErpProgrammeDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("programmeName", dto.getProgrammeName().replaceAll("\\s+"," ").trim());
			query.setParameter("programmeCode", dto.getProgrammeCode().replaceAll("\\s+"," ").trim());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public SelectDTO update(ErpProgrammeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getProgrammeName());
		return dto;
	}

	public SelectDTO save(ErpProgrammeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getProgrammeName());
		return dto;
	}

	public ErpProgrammeBatchwiseSettingsDBO editBatch(int id, Integer batchYearId) {
		String str2 = "select distinct epb from ErpProgrammeBatchwiseSettingsDBO as epb"
				+ " left join fetch epb.erpProgrammeSecondLanguageSessionDBOSet as sl"
				+ " left join fetch epb.erpProgrammeAccreditationMappingDBOSet as pa"
				+ " left join fetch epb.erpProgrammeSpecializationMappingDBOSet as sm"
				+ " left join fetch epb.erpCampusProgrammeMappingDetailsDBOSet as cm"
				+ "	left join fetch epb.erpProgrammeAddOnCoursesDBOSet as pc"
				+ " left join fetch epb.erpProgrammeSpecializationSessionMappingDBOSet as ssm"
				+ " left join fetch epb.acaBatchDBOSet as ab"
				+ " left join fetch ab.acaDurationDetailDBO as acad"
				+ " left join fetch epb.obeProgrammeOutcomeDBOSet as eo"
//				+ " left join fetch eo.obeProgrammeOutcomeUploadDetailsDBOSet as oud"
//				+ " left join fetch eo.obeProgrammeOutcomeDetailsDBOSet as eod"				
//				+ " left join fetch eod.erpProgrammePeoMissionMatrixDBOSet as pmm"
				+ " where epb.recordStatus = 'A'"
//				+ " and (sl is null or sl.recordStatus = 'A')"
//				+ " and (pa is null or pa.recordStatus = 'A')"
//				+ " and (sm  is null or sm.recordStatus = 'A')"
//				+ " and (cm is null or cm.recordStatus = 'A')"
//				+ " and (pc is null or pc.recordStatus = 'A')"
//				+ " and (ssm is null or ssm.recordStatus = 'A')"
//				+ " and (ab is null or ab.recordStatus = 'A')"
//				+ " and (acad is null or acad.recordStatus = 'A')"
//				+ " and (eo is null or eo.recordStatus = 'A')"
//				+ " and (oud is null or oud.recordStatus = 'A')" 
//				+ " and (eod is null or eod.recordStatus = 'A')"
//				+ " and (pmm is null or pmm.recordStatus = 'A')"
				+ " and epb.erpProgrammeDBO.id= :id  and epb.erpAcademicYearDBO.id = :batchYearId";
		return sessionFactory.withSession(s -> s.createQuery(str2, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("id", id)
				.setParameter("batchYearId", batchYearId).getSingleResultOrNull()).await().indefinitely();				 
	}

	public ErpProgrammeDBO getProgramme(int id) {
		String str = "from ErpProgrammeDBO e "
				+ " left join fetch e.erpProgrammeAddtnlDetailsDBOSet as ead"
				+ " left join fetch e.erpProgrammeDepartmentMappingDBOSet as edm"
				+ " left join fetch e.erpCampusProgrammeMappingDBOSet as ecm"
				+ " left join fetch e.erpProgrammeRBTDomainsMappingDBOSet as rbt"
				+ " left join fetch e.erpProgrammeApprovalLevelMappingDBOSet as alm"
				+ " left join fetch alm.erpProgrammeApprovalLevelDocumentsDBOSet as ald"
				+ " where e.recordStatus = 'A'"
				+ " and e.id= :id";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpProgrammeDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

//	public boolean duplicateCheck(ErpProgrammeBatchwiseSettingsDTO dto, Integer programmeId) {	
//		String str = "from ErpProgrammeBatchwiseSettingsDBO epb "
//				+ " where epb.recordStatus='A' and epb.erpAcademicYearDBO.id = :batchYearId and epb.erpProgrammeDBO.id = :programmeId ";
//		if (!Utils.isNullOrEmpty(dto.getId())) {
//			str += " and epb.id != :id";
//		}
//		String finalStr = str;
//		List<ErpProgrammeBatchwiseSettingsDBO> list = sessionFactory.withSession(s -> {
//			Mutiny.Query<ErpProgrammeBatchwiseSettingsDBO> query = s.createQuery(finalStr, ErpProgrammeBatchwiseSettingsDBO.class);
//			if (!Utils.isNullOrEmpty(dto.getId())) {
//				query.setParameter("id", dto.getId());
//			}
//			query.setParameter("batchYearId",Integer.parseInt(dto.getBatchYear().getValue()));
//			query.setParameter("programmeId", programmeId);
//			return query.getResultList();
//		}).await().indefinitely();
//		return Utils.isNullOrEmpty(list) ? false : true;
//	}

	public void update(ErpProgrammeBatchwiseSettingsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
	}

	public void save(ErpProgrammeBatchwiseSettingsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();		 
	}

	public List<Integer> getBatchYears(Integer programmeId) {
		String str = "select dbo.erpAcademicYearDBO.academicYear from ErpProgrammeBatchwiseSettingsDBO dbo inner join dbo.erpAcademicYearDBO bo on bo.recordStatus = 'A'"
				+ " where dbo.recordStatus = 'A' and dbo.erpProgrammeDBO.id =: programmeId";
		return sessionFactory.withSession(s -> s.createQuery(str, Integer.class).setParameter("programmeId", programmeId).getResultList()).await().indefinitely();		
	}

	public Boolean isBatchWisePresented(int programmeId) {
		String str = "from ErpProgrammeBatchwiseSettingsDBO epb "
				+ " where epb.recordStatus='A' and epb.erpProgrammeDBO.id = :programmeId ";
		String finalStr = str;
		List<ErpProgrammeBatchwiseSettingsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpProgrammeBatchwiseSettingsDBO> query = s.createQuery(finalStr, ErpProgrammeBatchwiseSettingsDBO.class);
			query.setParameter("programmeId", programmeId);
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public List<AcaDurationDBO> saveAcaDuration(List<AcaDurationDBO> acaDurationDboList) {
		sessionFactory.withTransaction((session, tx) -> session.persistAll(acaDurationDboList.toArray())).await().indefinitely();	
		return acaDurationDboList;
	}

	public Long countOfYears(List<Integer> yearList) {
		String str = "select count(distinct dbo.academicYear) from ErpAcademicYearDBO dbo where dbo.academicYear in (:yearList) and dbo.recordStatus='A'";
		return sessionFactory.withSession(s -> s.createQuery(str, Long.class).setParameter("yearList", yearList).getSingleResultOrNull()).await().indefinitely();		
	}	

	public List<ErpAcademicYearDBO> listOfYears(List<Integer> yearList) {
		String str = "select distinct dbo from ErpAcademicYearDBO dbo where dbo.recordStatus='A' and dbo.academicYear in (:yearList) order by dbo.academicYear";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpAcademicYearDBO.class).setParameter("yearList", yearList).getResultList()).await().indefinitely();		
	}

	public List<AcaDurationDBO> getDuration(List<Integer> yearIdList,List<Integer> sessionGroupIdList){
		String str = "select distinct dbo from AcaDurationDBO dbo where dbo.recordStatus ='A' and (dbo.erpAcademicYearDBO.id in (:yearIdList) and dbo.acaSessionGroupDBO.id in (:sessionGroupIdList))";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaDurationDBO.class).setParameter("yearIdList", yearIdList).setParameter("sessionGroupIdList", sessionGroupIdList).getResultList()).await().indefinitely();
	}

	public List<AcaDurationDetailDBO> getDurationDetail(List<Integer> campusProgrammeIdList,List<Integer> durationIdList) {
		String str = "select dbo from AcaDurationDetailDBO dbo where dbo.recordStatus ='A' and (dbo.erpCampusProgrammeMappingDBO.id in (:campusProgrammeId) and dbo.acaDurationDBO.id in (:durationIdList))"; 
		return sessionFactory.withSession(s -> s.createQuery(str, AcaDurationDetailDBO.class).setParameter("campusProgrammeId", campusProgrammeIdList).setParameter("durationIdList", durationIdList).getResultList()).await().indefinitely();
	}

	public void updateDiscontiued(List<ErpProgrammeBatchwiseSettingsDBO> batchWiseYearList) {		
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(batchWiseYearList.toArray())).subscribeAsCompletionStage();	
	}

	public void updateDetail(List<AcaDurationDetailDBO> durationDetail) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(durationDetail.toArray())).await().indefinitely();			
	}

	public List<ErpCampusDBO> getLocationOfCampus(List<Integer> campusIds) {
		String str = "select ec from ErpCampusDBO ec where ec.recordStatus= 'A' and ec.id in (:campusIds)";
		return sessionFactory.withSession(s-> s.createQuery(str,ErpCampusDBO.class).setParameter("campusIds",campusIds).getResultList()).await().indefinitely();

	}

	public Mono<List<ErpProgrammeBatchwiseSettingsDBO>> isBatchCreated(Boolean isDiscontinued,Integer campusProgrammeId,Integer year){
		String str ="select epb from ErpProgrammeBatchwiseSettingsDBO epb"
				+ " left join fetch epb.erpCampusProgrammeMappingDetailsDBOSet as ecm"
				+ " inner join epb.erpAcademicYearDBO as eay on eay.recordStatus='A'"
				+ " where epb.recordStatus='A'"
				+ " and (ecm is null or (ecm.recordStatus='A' and ecm.erpCampusProgrammeMappingDBO.id = :campusProgrammeId))";	
		if(!isDiscontinued) {
			str+= " and eay.academicYear <> :year";			
		} else if(isDiscontinued) {
			str+= " and eay.academicYear > :year";
		}
		String finalStr = str;		
		var lis = Mono.fromFuture(sessionFactory.withSession(s-> s.createQuery(finalStr,ErpProgrammeBatchwiseSettingsDBO.class).setParameter("campusProgrammeId", campusProgrammeId).setParameter("year", year).getResultList()).subscribeAsCompletionStage());
		return lis;
	}

	public List<ErpProgrammeBatchwiseSettingsDBO> getBatchwiseListsProgramme(List<Integer> yearList, Integer programmeId) {
		String str1 = "select ea.id from ErpAcademicYearDBO ea where ea.recordStatus='A' and ea.academicYear in (:yearList)";
		var yearIdList = sessionFactory.withSession(s-> s.createQuery(str1,Integer.class).setParameter("yearList",yearList).getResultList()).await().indefinitely();
		String str = "select distinct epb from ErpProgrammeBatchwiseSettingsDBO as epb"				
				+ " left join fetch epb.erpProgrammeSpecializationMappingDBOSet as epsm" 
				+ " left join fetch epb.erpProgrammeSpecializationSessionMappingDBOSet as epssm" 
				+ " left join fetch epb.erpCampusProgrammeMappingDetailsDBOSet as ecpd" 
				+ " left join fetch epb.acaBatchDBOSet as ab" 		 
				+ " left join fetch ab.acaDurationDetailDBO as ad"
				+ " where epb.recordStatus = 'A'"
//				+ " and (epsm is null or epsm.recordStatus = 'A')"
//				+ " and (epssm is null or epssm.recordStatus = 'A')"
//				+ " and (ecpd is null or ecpd.recordStatus = 'A')"
//				+ " and (ab is null or ab.recordStatus = 'A')"
//				+ " and (ad is null or ad.recordStatus = 'A')"
				+ " and epb.erpProgrammeDBO.id= :programmeId and epb.erpAcademicYearDBO.id in (:yearIdList)";
		var list= sessionFactory.withSession(s-> s.createQuery(str,ErpProgrammeBatchwiseSettingsDBO.class).setParameter("programmeId", programmeId).setParameter("yearIdList",yearIdList).getResultList()).await().indefinitely();
		return list;
	}
	
	public Boolean isLatestProgramme(Integer programmeId, Integer academicYear) {
		String str ="select max(dbo.changedFromYear) from ErpProgrammeAddtnlDetailsDBO dbo where dbo.recordStatus='A' and dbo.erpProgrammeDBO.id= :programmeId";
		var latestFromYear =sessionFactory.withSession(s-> s.createQuery(str,Integer.class).setParameter("programmeId",programmeId).getSingleResultOrNull()).await().indefinitely();		
		return academicYear >= latestFromYear? true:false;
	}
//	@EventListener(ApplicationReadyEvent.class)
	public ErpProgrammeBatchwiseSettingsDBO editBatch1() {
		int id =596 ;
		Integer batchYearId=83;
		String str2 = "select epb from ErpProgrammeBatchwiseSettingsDBO as epb"
				+ " left join fetch epb.erpProgrammeSecondLanguageSessionDBOSet as sl"
				+ " left join fetch epb.erpProgrammeAccreditationMappingDBOSet as pa"
				+ " left join fetch epb.erpProgrammeSpecializationMappingDBOSet as sm"
				+ " left join fetch epb.erpCampusProgrammeMappingDetailsDBOSet as cm"
				+ " left join fetch epb.erpProgrammeAddOnCoursesDBOSet as pc"
				+ " left join fetch epb.erpProgrammeSpecializationSessionMappingDBOSet as ssm"
				+ " left join fetch epb.acaBatchDBOSet as ab"
				+ " left join fetch ab.acaDurationDetailDBO as acad"
//				+ " left join fetch epb.obeProgrammeOutcomeDBOSet as eo"
//				+ " left join fetch eo.obeProgrammeOutcomeUploadDetailsDBOSet as oud"
//				+ " left join fetch eo.obeProgrammeOutcomeDetailsDBOSet as eod"				
//				+ " left join fetch eod.erpProgrammePeoMissionMatrixDBOSet as pmm"
				+ " where epb.recordStatus = 'A'"
//				+ " and (sl is null or sl.recordStatus = 'A')"
//				+ " and (pa is null or pa.recordStatus = 'A')"
//				+ " and (sm is null or (sm.recordStatus = 'A' is not null)"
//				+ " and (cm is null or cm.recordStatus = 'A')"
//				+ " and (pc is null or pc.recordStatus = 'A')"
//				+ " and (ssm is null or ssm.recordStatus = 'A')"
//				+ " and (ab is null or ab.recordStatus = 'A')"
//				+ " and (acad is null or acad.recordStatus = 'A')"
//				+ " and (eo is null or eo.recordStatus = 'A')"
//				+ " and (oud is null or oud.recordStatus = 'A')" 
//				+ " and (eod is null or eod.recordStatus = 'A')"
//				+ " and (pmm is null or pmm.recordStatus = 'A')"
				+ " and epb.erpProgrammeDBO.id= :id  and epb.erpAcademicYearDBO.id = :batchYearId";
		long startTime = System.nanoTime();
		var list = sessionFactory.withSession(s -> s.createQuery(str2, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("id", id)
				.setParameter("batchYearId", batchYearId).getSingleResultOrNull()).await().indefinitely();	
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("time "+ TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS));
		System.out.println("time "+ TimeUnit.MILLISECONDS.convert(duration, TimeUnit.SECONDS));
		System.out.println("second language------" + list.getErpProgrammeSecondLanguageSessionDBOSet().size());
		System.out.println("Accredatation------" + list.getErpProgrammeAccreditationMappingDBOSet().size());
		System.out.println("specialization mappin------" + list.getErpProgrammeSpecializationMappingDBOSet().size());
		System.out.println("program mapping details------" + list.getErpCampusProgrammeMappingDetailsDBOSet().size());
		System.out.println("program addon course------" + list.getErpProgrammeAddOnCoursesDBOSet().size());
		System.out.println("program specialization course------" + list.getErpProgrammeSpecializationSessionMappingDBOSet().size());
		System.out.println("aca batch------" + list.getAcaBatchDBOSet().size());
		list.getAcaBatchDBOSet().forEach(acaBatchDBO ->  System.out.println("aca batch duration------" + acaBatchDBO.getAcaDurationDetailDBO().size()));
		System.out.println("program outcome------" + list.getObeProgrammeOutcomeDBOSet().size());
//		list.getObeProgrammeOutcomeDBOSet().forEach(out -> { 
//			System.out.println("outcome detail------" + out.getObeProgrammeOutcomeDetailsDBOSet().size());
//			System.out.println("outcome upload------" + out.getObeProgrammeOutcomeUploadDetailsDBOSet().size());
//			 out.getObeProgrammeOutcomeDetailsDBOSet().forEach(matrix-> System.out.println("outcome matrix------" + matrix.getErpProgrammePeoMissionMatrixDBOSet().size()));
//		});
		long startTime1 = System.nanoTime();
		var list1 = sessionFactory.withSession(s -> s.createQuery(str2, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("id", id)
				.setParameter("batchYearId", batchYearId).getResultList()).await().indefinitely();
		long endTime1 = System.nanoTime();
		long duration1 = (endTime1 - startTime1);
		System.out.println("time "+ TimeUnit.MILLISECONDS.convert(duration1, TimeUnit.NANOSECONDS));
		System.out.println("\n\n list size------" + list1.size());
		list1.forEach(batList->{
			System.out.println("\n\n second language------" + batList.getErpProgrammeSecondLanguageSessionDBOSet().size());
			System.out.println("Accredatation------" + batList.getErpProgrammeAccreditationMappingDBOSet().size());
			System.out.println("specialization mappin------" + batList.getErpProgrammeSpecializationMappingDBOSet().size());
			System.out.println("program mapping details------" + batList.getErpCampusProgrammeMappingDetailsDBOSet().size());
			System.out.println("program addon course------" + batList.getErpProgrammeAddOnCoursesDBOSet().size());
			System.out.println("program specialization course------" + batList.getErpProgrammeSpecializationSessionMappingDBOSet().size());
			System.out.println("aca batch------" + batList.getAcaBatchDBOSet().size());
			batList.getAcaBatchDBOSet().forEach(acaBatchDBO ->  System.out.println("aca batch duration------" + acaBatchDBO.getAcaDurationDetailDBO().size()));
			System.out.println("program outcome------" + batList.getObeProgrammeOutcomeDBOSet().size());
//			batList.getObeProgrammeOutcomeDBOSet().forEach(out -> { 
//				System.out.println("outcome detail------" + out.getObeProgrammeOutcomeDetailsDBOSet().size());
//				System.out.println("outcome upload------" + out.getObeProgrammeOutcomeUploadDetailsDBOSet().size());
//				 out.getObeProgrammeOutcomeDetailsDBOSet().forEach(matrix-> System.out.println("outcome matrix------" + matrix.getErpProgrammePeoMissionMatrixDBOSet().size()));
//			});
		});
		return list;
	}

}

