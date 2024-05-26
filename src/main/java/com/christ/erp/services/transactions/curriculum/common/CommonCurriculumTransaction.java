package com.christ.erp.services.transactions.curriculum.common;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.AcaCourseDBO;
import com.christ.erp.services.dbobjects.common.AcaCourseTypeDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionGroupDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeRoleDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeLevelDBO;
import com.christ.erp.services.dbobjects.common.ObeProgrammeOutcomeTypesDBO;
import com.christ.erp.services.dbobjects.common.ErpRBTDomainsDBO;
import com.christ.erp.services.dbobjects.common.ErpSpecializationDBO;
import com.christ.erp.services.dbobjects.curriculum.common.AttActivityDBO;
import com.christ.erp.services.dbobjects.curriculum.common.ErpAccreditationAffiliationTypeDBO;
import com.christ.erp.services.dbobjects.curriculum.common.ErpExternalsCategoryDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import reactor.core.publisher.Mono;

@Repository
public class CommonCurriculumTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<ErpExternalsCategoryDBO>> getExternalsCategory() {
		String str = "from ErpExternalsCategoryDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpExternalsCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<StudentDBO>> getStudentDetails(Integer admittedYear) {		
		if(!Utils.isNullOrEmpty(admittedYear)) {
			String str = "select dbo from StudentDBO dbo"
					+ " inner join ErpAcademicYearDBO eay on dbo.admittedYearId = eay.id"
					+ " where dbo.recordStatus='A' and eay.recordStatus='A' and  eay.academicYear= :admittedYear";
			return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, StudentDBO.class).setParameter("admittedYear",admittedYear).getResultList()).subscribeAsCompletionStage());
		}
		else {
			return Mono.empty();
		}
	}

	public Mono<List<EmpDBO>> getEmpsByDepartment(String  departmentId) {
		String queryString =" select dbo from EmpDBO dbo"
				+ " inner join EmpEmployeeCategoryDBO eec on dbo.empEmployeeCategoryDBO.id = eec.id";
		if(!Utils.isNullOrEmpty(departmentId)) {
			queryString += " inner join ErpCampusDepartmentMappingDBO ecdm on dbo.erpCampusDepartmentMappingDBO.id = ecdm.id"
					+ " inner join ErpDepartmentDBO ed on ecdm.erpDepartmentDBO.id = ed.id "
					+ " where dbo.recordStatus = 'A' and ecdm.recordStatus ='A' and ed.recordStatus = 'A'"
					+ " and  eec.employeeCategoryName = 'Teaching' and ed.id = :departmentId";
		} else {
			queryString +=" where dbo.recordStatus = 'A' and  eec.employeeCategoryName = 'Teaching' ";
		}
		String finalquery = queryString;
		Mono<List<EmpDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<EmpDBO> query = s.createQuery(finalquery, EmpDBO.class);
		if(!Utils.isNullOrEmpty(departmentId)) {
			query.setParameter("departmentId",Integer.parseInt(departmentId));
		}
		return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;	
	}

	public Mono<List<ExternalsDBO>> getExternalMembersByCategory(int categoryId) {
		String queryString = "select dbo from ExternalsDBO dbo"
				+ " inner join  ErpExternalsCategoryDBO eec on eec.id = dbo.erpExternalsCategoryDBO.id"
				+ " where dbo.recordStatus = 'A' and eec.recordStatus = 'A' and dbo.erpExternalsCategoryDBO.id = :categoryId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ExternalsDBO.class).setParameter("categoryId", categoryId).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpCommitteeRoleDBO>> getExternalMembers() {
		String queryString = "select dbo from ErpCommitteeRoleDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpCommitteeRoleDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpProgrammeDBO>> getProgrammeByDepartment(int departId) {
		String queryString = " select distinct dbo from ErpProgrammeDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.coordinatingDepartment.id = :departId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpProgrammeDBO.class).setParameter("departId",departId).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpProgrammeDepartmentMappingDBO>> getProgrammeByDepartmentMapping(int departId) {
		String queryString = " select distinct dbo from ErpProgrammeDepartmentMappingDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.erpDepartmentDBO.id = :departId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpProgrammeDepartmentMappingDBO.class).setParameter("departId", departId)
				.getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpRBTDomainsDBO>> getRBTDomains() {
		String str = "from ErpRBTDomainsDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpRBTDomainsDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<ErpCommitteeDBO>  getImportYearList(String committeeType) {
		String str = "select distinct dbo from ErpCommitteeDBO dbo"
				+ " inner join ErpCommitteeTypeDBO ct on  dbo.erpCommitteeTypeDBO.id = ct.id"
				+ " where  ct.committeeType = :committeeType and  dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str, ErpCommitteeDBO.class).setParameter("committeeType", committeeType).getResultList()).await().indefinitely();	 
	}

	public Mono<List<ErpCampusDepartmentMappingDBO>> getCampusByDepartment(int departmentId) {
		String queryString = " select dbo from ErpCampusDepartmentMappingDBO dbo"
				+ " where dbo.erpDepartmentDBO.id =:departmentId and dbo.recordStatus='A' ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpCampusDepartmentMappingDBO.class).setParameter("departmentId", departmentId).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<AcaCourseTypeDBO>> getAcaCourseType() {
		String query = " select dbo from AcaCourseTypeDBO dbo where dbo.recordStatus = 'A' and dbo.isForAssessmentDisplay = true";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, AcaCourseTypeDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpSpecializationDBO>> getSpecialization() {
		String query = " select dbo from ErpSpecializationDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, ErpSpecializationDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<AcaSessionTypeDBO>> getAcaSessionType() {
		String query = "select dbo from AcaSessionTypeDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, AcaSessionTypeDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<AcaSessionDBO> getAcaSession(Integer sessionTypeId,Integer sessionNumber,Boolean isTerm) {
		String query = "select dbo from AcaSessionDBO dbo "
				+ " inner join dbo.acaSessionType "
				+ " where dbo.recordStatus = 'A' and dbo.acaSessionType.id = :sessionTypeId";
		if(!Utils.isNullOrEmpty(sessionNumber) && isTerm) {
			query += " and dbo.termNumber <= :sessionNumber order by dbo.termNumber";
		}
		String finalstr =query;
		List<AcaSessionDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaSessionDBO> query1 = s.createQuery(finalstr, AcaSessionDBO.class);
			//return sessionFactory.withSession(s -> s.createQuery(finalstr, AcaSessionDBO.class).setParameter("sessionTypeId",sessionTypeId).setParameter("sessionNumber", sessionNumber).getResultList()).await().indefinitely();
			query1.setParameter("sessionTypeId",sessionTypeId);
			if(!Utils.isNullOrEmpty(sessionNumber) && isTerm) {
				query1.setParameter("sessionNumber", sessionNumber);
			} 
			return query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Mono<List<ObeProgrammeOutcomeTypesDBO>> getObeProgrammeOutcomeTypes() {
		String query = " select dbo from ObeProgrammeOutcomeTypesDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, ObeProgrammeOutcomeTypesDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<AcaSessionGroupDBO> getAcaSessionGroup(String sessionTypeId) {
		String query = "select dbo from AcaSessionGroupDBO dbo "
				+ " inner join dbo.acaSessionType"
				+ " where dbo.recordStatus = 'A' ";
		if(!Utils.isNullOrEmpty(sessionTypeId)) {
			query += " and dbo.acaSessionType.id = :sessionTypeId";
		}
		query += " order by dbo.acaSessionType.id, dbo.sessionNumber ";
		//	return sessionFactory.withSession(s -> s.createQuery(query, AcaSessionGroupDBO.class).setParameter("sessionTypeId",sessionTypeId).getResultList()).await().indefinitely();
		String finalstr =query;
		List<AcaSessionGroupDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaSessionGroupDBO> query1 = s.createQuery(finalstr, AcaSessionGroupDBO.class);
			if(!Utils.isNullOrEmpty(sessionTypeId)) {
				query1.setParameter("sessionTypeId",Integer.parseInt(sessionTypeId));
			} 
			return query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Mono<List<ErpAccreditationAffiliationTypeDBO>> getAccreditationType() {
		String query = "select dbo from ErpAccreditationAffiliationTypeDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, ErpAccreditationAffiliationTypeDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public  Mono<List<ErpProgrammeLevelDBO>> getProgrammeLevel() {
		String str = " select dbo from ErpProgrammeLevelDBO dbo where dbo.recordStatus='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpProgrammeLevelDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<AcaSessionDBO> getAcaSessionByGroup(String sessionGroupId) {
		String str = "select dbo from AcaSessionDBO dbo "
				+ " inner join dbo.acaSessionGroup "
				+ " where dbo.recordStatus = 'A' and dbo.acaSessionGroup.id = :sessionGroupId";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaSessionDBO.class).setParameter("sessionGroupId",Integer.parseInt(sessionGroupId)).getResultList()).await().indefinitely();
	}

	public Mono<List<ErpDepartmentDBO>> getDepartmentByCampusId(String campusId) {
		String str = " select dbo from ErpDepartmentDBO dbo "
				+" inner join ErpCampusDepartmentMappingDBO edbo ON edbo.erpDepartmentDBO.id = dbo.id and edbo.recordStatus ='A'"
				+" where dbo.recordStatus ='A' and dbo.erpDepartmentCategoryDBO.isCategoryAcademic = 1 and edbo.erpCampusDBO.id =:campusId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpDepartmentDBO.class).setParameter("campusId", Integer.parseInt(campusId)).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<AttActivityDBO>> getAttActivity() {
		String str = " from AttActivityDBO dbo where dbo.recordStatus ='A' and dbo.attTypeDBO.isNonCourseActivity = 1";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, AttActivityDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpAcademicYearDBO>> getAcademicYearFromDuration() {
		String str = "select distinct e from ErpAcademicYearDBO e "
				+ " inner join AcaDurationDBO bo on e.id = bo.erpAcademicYearDBO.id "
				+ " where e.recordStatus = 'A' and  bo.recordStatus = 'A' order by e.academicYearName desc";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpAcademicYearDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<AcaCourseDBO> getAcaCourse(String academicYearId, String departmentId, String levelId, String sessionGroupId, String campusId) {
		String str = "select distinct bo from AcaCourseDBO bo"
				+" inner join AcaCourseYearwiseDBO acy on acy.acaCourseDBO.id = bo.id"
				+" inner join AcaCourseSessionwiseDBO dbo on dbo.acaCourseYearwiseDBO.id = acy.id"				
				+" inner join dbo.acaDurationDBO ad "
				+" inner join dbo.erpProgrammeDBO ep "
				+" inner join ErpCampusProgrammeMappingDBO cdbo on cdbo.erpProgrammeDBO.id = ep.id"
				+" where dbo.recordStatus ='A' and ad.recordStatus = 'A' and ad.erpAcademicYearDBO.id =:academicYearId and ad.acaSessionGroupDBO.id =:sessionGroupId"
				+" and cdbo.recordStatus ='A' and cdbo.erpCampusDBO.id =:campusId"
				+" and bo.recordStatus = 'A' and bo.erpDepartmentDBO.id =:departmentId";
		if (!Utils.isNullOrEmpty(levelId)) {
			str += " and ep.erpProgrammeDegreeDBO.recordStatus = 'A' and ep.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id =:levelId ";
		}
		String finalStr = str;
		List<AcaCourseDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaCourseDBO> query = s.createQuery(finalStr, AcaCourseDBO.class);
			if (!Utils.isNullOrEmpty(levelId)) {
				query.setParameter("levelId", Integer.parseInt(levelId));
			}
			query.setParameter("sessionGroupId", Integer.parseInt(sessionGroupId));
			query.setParameter("academicYearId", Integer.parseInt(academicYearId));
			query.setParameter("departmentId", Integer.parseInt(departmentId));
			query.setParameter("campusId", Integer.parseInt(campusId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<AcaClassDBO> getAcaClass(String academicYearId, String courseId, String levelId, String sessionGroupId, String campusId) {
		String sessionTypeName = null;
		if (!Utils.isNullOrEmpty(sessionGroupId)) {
			String str2= "select distinct dbo.acaSessionType.curriculumCompletionType from AcaSessionGroupDBO dbo"
					+ " where dbo.id =: id ";
			sessionTypeName = sessionFactory.withSession(s -> s.createQuery(str2, String.class).setParameter("id", Integer.parseInt(sessionGroupId)).getSingleResultOrNull()).await().indefinitely();
		}
		String str = " select distinct ac from AcaClassDBO ac";
		if (!Utils.isNullOrEmpty(courseId)) {
			str += " inner join AcaCourseSessionwiseDBO dbo on ac.acaDurationDetailDBO.id = dbo.acaDurationDetailDBO.id"
				+" inner join dbo.acaCourseYearwiseDBO acyw"
				+" inner join acyw.acaCourseDBO cdbo ";
		}
		str += " left join ac.acaDurationDetailDBO adb"
				+" left join adb.acaDurationDBO ad"
				+" left join AcaClassVirtualClassMapDBO acv on acv.acaVirtualClassDBO.id = ac.id and acv.recordStatus ='A'"
				+" where ad.erpAcademicYearDBO.id =: academicYearId and ad.acaSessionGroupDBO.id =: sessionGroupId";
		if (!Utils.isNullOrEmpty(courseId)) {
			str += " and cdbo.id = :courseId";
		}
		if(sessionTypeName.equals("TERM")) {
			str += " and adb.acaBatchDBO.recordStatus = 'A' and adb.acaBatchDBO.erpCampusProgrammeMappingDBO.recordStatus = 'A'"
				  +" and adb.acaBatchDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id =: campusId";
			if (!Utils.isNullOrEmpty(levelId)) {
				str += " and adb.acaBatchDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id =:levelId ";
			}
		} else {
			str +=" and adb.erpCampusProgrammeMappingDBO.recordStatus = 'A' and adb.erpCampusProgrammeMappingDBO.erpCampusDBO.id =:campusId";
			if (!Utils.isNullOrEmpty(levelId)) {
				str += " and adb.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id =:levelId ";
			}
		}
		String str1 = str + " and ac.virtualClass = 0 and ac.havingVirtualClass = 0";
		String str2 = str + " and ac.havingVirtualClass = 1";
		List<AcaClassDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaClassDBO> query = s.createQuery(str1, AcaClassDBO.class);
			if (!Utils.isNullOrEmpty(levelId)) {
				query.setParameter("levelId", Integer.parseInt(levelId));
			}
			if (!Utils.isNullOrEmpty(courseId)) {
				query.setParameter("courseId", Integer.parseInt(courseId));
			}
			query.setParameter("campusId", Integer.parseInt(campusId));
			query.setParameter("sessionGroupId", Integer.parseInt(sessionGroupId));
			query.setParameter("academicYearId", Integer.parseInt(academicYearId));
			return query.getResultList();
		}).await().indefinitely();
		List<AcaClassDBO> list1 = sessionFactory.withSession(s -> {
			Mutiny.Query<AcaClassDBO> query = s.createQuery(str2, AcaClassDBO.class);
			if (!Utils.isNullOrEmpty(levelId)) {
				query.setParameter("levelId", Integer.parseInt(levelId));
			}
			if (!Utils.isNullOrEmpty(courseId)) {
				query.setParameter("courseId", Integer.parseInt(courseId));
			}
			query.setParameter("campusId", Integer.parseInt(campusId));
			query.setParameter("sessionGroupId", Integer.parseInt(sessionGroupId));
			query.setParameter("academicYearId", Integer.parseInt(academicYearId));
			return query.getResultList();
		}).await().indefinitely();
		List<AcaClassDBO> list2 = null;
		if (!Utils.isNullOrEmpty(list1)) {
			var classIds  = list1.stream().map(s-> s.getId()).collect(Collectors.toList());
			String str3 = "select distinct ac from AcaClassDBO ac"
					+" inner join AcaClassVirtualClassMapDBO acv on acv.acaVirtualClassDBO.id = ac.id and acv.recordStatus ='A'"
					+" where ac.recordStatus ='A' and acv.acaBaseClassDBO.id in (:list1)";
			list2 = sessionFactory.withSession(s -> s.createQuery(str3, AcaClassDBO.class).setParameter("list1" , classIds).getResultList()).await().indefinitely();
			if (!Utils.isNullOrEmpty(list2)) {
				list.addAll(list2);
			}
		}
		return list;
	}

	public Mono<List<ErpProgrammeBatchwiseSettingsDBO>> getProgrammeByCampus(Integer campusId, Integer yearId) {
		String str = " select distinct dbo from ErpProgrammeBatchwiseSettingsDBO dbo"
				     +" inner join ErpCampusProgrammeMappingDetailsDBO cdbo on cdbo.erpProgrammeBatchwiseSettingsDBO.id = dbo.id and cdbo.recordStatus ='A'"
				     +" inner join ErpCampusProgrammeMappingDBO pdbo on pdbo.id = cdbo.erpCampusProgrammeMappingDBO.id and pdbo.recordStatus ='A'"
				     +" inner join ErpProgrammeDBO edbo on edbo.id = pdbo.erpProgrammeDBO.id and edbo.recordStatus ='A'"
				     +" where pdbo.erpCampusDBO.id =:campusId and dbo.erpAcademicYearDBO.id =:yearId and dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpProgrammeBatchwiseSettingsDBO.class).setParameter("campusId", campusId).setParameter("yearId", yearId).getResultList()).subscribeAsCompletionStage());	
	}

	public Mono<List<ErpAcademicYearDBO>> getBatchYear() {
		String str = " select distinct dbo from ErpAcademicYearDBO dbo "
				     +" inner join ErpProgrammeBatchwiseSettingsDBO bdbo on bdbo.erpAcademicYearDBO.id =dbo.id and bdbo.recordStatus = 'A'"
				     +" where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpAcademicYearDBO.class).getResultList()).subscribeAsCompletionStage());		
	}

	public Mono<List<ErpAcademicYearDBO>> getAcademicYearFromDurationAndSession(String typeId) {
		String str = " select distinct dbo from ErpAcademicYearDBO dbo "
				    +" inner join AcaDurationDBO adbo on adbo.erpAcademicYearDBO.id = dbo.id and adbo.recordStatus ='A'"
				    +" left join AcaDurationDetailDBO ddbo on ddbo.acaDurationDBO.id = adbo.id and ddbo.recordStatus ='A'"
				    +" inner join AcaSessionDBO adbo on adbo.id = ddbo.acaSessionDBO.id and adbo.recordStatus ='A'"
				    +" inner join AcaSessionTypeDBO tdbo on tdbo.id = adbo.acaSessionType.id and tdbo.recordStatus ='A'"
				    +" where dbo.recordStatus ='A' and tdbo.id =:typeId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpAcademicYearDBO.class).setParameter("typeId", Integer.parseInt(typeId)).getResultList()).subscribeAsCompletionStage());		
	}

	public List<AcaDurationDetailDBO> getProgrammeLevelByCampus(String yearId, String campusId, String typeId) {
		String str= "select distinct dbo from AcaDurationDetailDBO dbo "
				+" inner join dbo.acaSessionDBO sdbo "
				+" inner join dbo.acaDurationDBO ad "
				+" inner join dbo.erpCampusProgrammeMappingDBO ecp "
				+" inner join ecp.erpProgrammeDBO ep "
				+" inner join ep.erpProgrammeDegreeDBO epd "
				+" inner join epd.erpProgrammeLevelDBO epl "
				+" where dbo.recordStatus ='A' and sdbo.recordStatus ='A' and ad.recordStatus ='A' and ecp.recordStatus ='A' and ep.recordStatus ='A' and epd.recordStatus ='A'"
				+" and epl.recordStatus ='A' and ad.erpAcademicYearDBO.id =:yearId and sdbo.acaSessionType.id =:typeId and ecp.erpCampusDBO.id =:campusId ";
		return sessionFactory.withSession(s -> s.createQuery(str, AcaDurationDetailDBO.class)
				.setParameter("yearId", Integer.parseInt(yearId))
				.setParameter("typeId", Integer.parseInt(typeId)).setParameter("campusId", Integer.parseInt(campusId))
				.getResultList()).await().indefinitely();		
	}
}