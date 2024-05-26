package com.christ.erp.services.transactions.admission;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanProgrammeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmAdmissionTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmIntakeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionWorkExperienceDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpUniversityBoardDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultApprovalDTO;
import reactor.core.publisher.Mono;

@Repository
public class CommonAdmissionTransaction {
	
	private static volatile CommonAdmissionTransaction commonAdmissionTransaction = null;

    public static CommonAdmissionTransaction getInstance() {
        if(commonAdmissionTransaction==null) {
        	commonAdmissionTransaction = new CommonAdmissionTransaction();
        }
        return commonAdmissionTransaction;
    }
    

	@Autowired
	private Mutiny.SessionFactory sessionFactory;
    
	/* methods are not in use
    public List<Tuple> getAccountHead(String offlineApplnNoPrefix,String offlineApplnNo,String academicYear) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_UNIT = "select acc_fee_heads.acc_fee_heads_id as ID ,acc_fee_heads.fee_heads_type as Text FROM acc_fee_heads " + // query changed
									"inner join adm_programme_settings on adm_programme_settings.acc_fee_heads_id = acc_fee_heads.acc_fee_heads_id " + // query changed 
									"inner join erp_programme on erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id " + // query changed
									"inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id " + // query changed
									"inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id " +
									"inner join adm_appln_number_gen_details on adm_appln_number_gen_details.erp_campus_programme_mapping_id = erp_campus_programme_mapping.erp_campus_programme_mapping_id " +
									"inner join adm_appln_number_generation ON adm_appln_number_generation.adm_appln_number_generation_id = adm_appln_number_gen_details.adm_appln_number_generation_id " +
									"inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = adm_appln_number_generation.erp_academic_year_id "+
									"where acc_fee_heads.record_status='A' and " +
									"adm_appln_number_generation.record_status='A' and " +
									"adm_appln_number_gen_details.record_status='A' and " +
									"erp_programme.record_status='A' and " +
									"erp_programme_degree.record_status='A' and " +
									"erp_academic_year.record_status='A' and "+
									"adm_appln_number_generation.erp_academic_year_id=:pAcademicYear and "+
									"adm_appln_number_generation.offline_appln_no_prefix=:prefixCode and " +
									"(:offlineApplnNo >= adm_appln_number_generation.offline_appln_no_from AND :offlineApplnNo <= adm_appln_number_generation.offline_appln_no_to) ";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				query.setParameter("prefixCode", offlineApplnNoPrefix);
				query.setParameter("offlineApplnNo", offlineApplnNo);
				query.setParameter("pAcademicYear", academicYear);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getAmountByAccountHead(String accountHeadId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_UNIT = "select adm_programme_settings.adm_programme_settings_id as ID ,acc_fee_heads.fee_heads_type as Text FROM acc_fee_heads  " + // query changed
						"inner join adm_programme_settings on adm_programme_settings.acc_fee_heads_id = acc_fee_heads.acc_fee_heads_id " + // query changed 
						"inner join erp_programme on erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id " + // query changed
						"inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id " + // query changed
						"inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id " +
						"inner join adm_appln_number_gen_details on adm_appln_number_gen_details.erp_campus_programme_mapping_id = erp_campus_programme_mapping.erp_campus_programme_mapping_id " +
						"inner join adm_appln_number_generation ON adm_appln_number_generation.adm_appln_number_generation_id = adm_appln_number_gen_details.adm_appln_number_generation_id " +
						"where acc_fee_heads.record_status='A' and " +
						"adm_appln_number_generation.record_status='A' and " +
						"adm_appln_number_gen_details.record_status='A' and " +
						"erp_programme.record_status='A' and " +
						"erp_programme_degree.record_status='A' and " +
						"acc_fee_heads.acc_fee_heads_id=:accFeeHeadId ";
						Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
						query.setParameter("accFeeHeadId", accountHeadId);
						return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	*/
	
	public List<Tuple> getAccountName(String loggedInUserId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {		
				String erpCampusId = null;
				Query  accountNameQuery = null;
				String SELECT_UNIT_ACCOUNT_NAME = null;
					String SELECT_UNIT_EMP_ID = "select erp_users.emp_id as EmpId from erp_users " +
							"where erp_users.record_status='A' and " +
							"erp_users.erp_users_id=:loggedInUserId ";
					Query query = context.createNativeQuery(SELECT_UNIT_EMP_ID, Tuple.class);
					query.setParameter("loggedInUserId", loggedInUserId);
					Tuple tuple = null;
					tuple = (Tuple) Utils.getUniqueResult(query.getResultList());
					if(!Utils.isNullOrEmpty(tuple.get("EmpId"))) {
						String empId = tuple.get("EmpId").toString();
						String SELECT_UNIT_CAMPUS_ID = "select erp_campus.erp_campus_id as campusId from emp " +
								"inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id "+
								"inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "+
								"where emp.record_status='A' and erp_campus_department_mapping.record_status='A' and erp_campus.record_status='A' and emp.emp_id=:pEmpId ";
						Query employeeQuery = context.createNativeQuery(SELECT_UNIT_CAMPUS_ID, Tuple.class);
						employeeQuery.setParameter("pEmpId", empId);
						Tuple campusIdTuple = null;
						campusIdTuple = (Tuple) Utils.getUniqueResult(employeeQuery.getResultList());
						if(!Utils.isNullOrEmpty(campusIdTuple.get("campusId"))) {
							erpCampusId = campusIdTuple.get("campusId").toString();
							SELECT_UNIT_ACCOUNT_NAME = "select acc_accounts.acc_accounts_id as ID ,acc_accounts.account_name as Text FROM acc_accounts " +
									"where record_status='A' and is_university_account=1 and erp_campus_id=:pCampusId ";
							accountNameQuery = context.createNativeQuery(SELECT_UNIT_ACCOUNT_NAME, Tuple.class);
							accountNameQuery.setParameter("pCampusId", erpCampusId);
						}
					}else {
						SELECT_UNIT_ACCOUNT_NAME = "select acc_accounts.acc_accounts_id as ID ,acc_accounts.account_name as Text FROM acc_accounts " +
								"where record_status='A' and is_university_account=1 ";
						accountNameQuery = context.createNativeQuery(SELECT_UNIT_ACCOUNT_NAME, Tuple.class);
					}
				return accountNameQuery.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getQualitativeParameterLabelList() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_UNIT = "select adm_qualitative_parameter_id AS ID, qualitative_parameter_label AS 'Text' from adm_qualitative_parameter where record_status= 'A' ";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public Mono<List<Object[]>> getAdmPrerequisiteExam(int acadadmicYearId, int programId) {
		 String str =" select distinct bo.adm_prerequisite_exam_id, bo.exam_name  from adm_prerequisite_exam as bo " + 
		 		" inner join adm_prerequisite_settings as cbo on bo.adm_prerequisite_exam_id = cbo.adm_prerequisite_exam_id " + 
		 		" where bo.record_status = 'A' and cbo.record_status = 'A' and " + 
		 		" cbo.erp_academic_year_id=:acadadmicYearId and cbo.erp_programme_id=:programId ";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Object[].class)
	    		 .setParameter("acadadmicYearId", acadadmicYearId)
	    		 .setParameter("programId", programId)
	    		 .getResultList())
	    		 .subscribeAsCompletionStage());
	 }
	
	public Mono<List<Object[]>> getAdmQualificationList(int programId) {
		 String str =" select distinct bo.adm_qualification_list_id, bo.qualification_name, bo.qualification_order from adm_qualification_list as bo " + 
		 		" inner join adm_programme_qualification_settings as setbo on bo.adm_qualification_list_id = setbo.adm_qualification_list_id " + 
		 		" inner join adm_programme_settings as prog ON prog.adm_programme_settings_id = setbo.adm_programme_settings_id " + 
		 		" where bo.record_status = 'A' and setbo.record_status = 'A' and prog.record_status = 'A' and " + 
		 		" prog.erp_programme_id =:programId ";
		     return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Object[].class)
		    		 .setParameter("programId", programId)
		    		 .getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<AdmSelectionProcessPlanDBO>> getAdmSelectionProcessPlanDetail(int acadadmicYearId,
			List<Integer> campusProgramMapingId) {	
		 String str = "select distinct bo from AdmSelectionProcessPlanDBO bo "
		 		+ " inner join fetch bo.admSelectionProcessPlanProgrammeDBOs cbo"
				+ " inner join fetch bo.admSelectionProcessPlanDetailDBO cbo2"
		 		+ " inner join fetch cbo2.admSelectionProcessPlanDetailProgDBOs cboProg"
				+ " inner join fetch cbo2.admSelectionProcessTypeDBO cbo3 "
				+ " inner join fetch cbo3.admissionSelectionProcessTypeDetailsDBOSet"
		 		+ " where bo.recordStatus = 'A' and cbo.recordStatus = 'A' and cbo2.recordStatus = 'A' and cboProg.recordStatus = 'A' "
		 		+ " and bo.erpAcademicYearDBO.id=:acadadmicYearId and cboProg.erpCampusProgrammeMappingDBO.id in (:campusProgramMapingId) ";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, AdmSelectionProcessPlanDBO.class)
	    		 .setParameter("acadadmicYearId", acadadmicYearId).setParameter("campusProgramMapingId", campusProgramMapingId)
	    		 .getResultList()).subscribeAsCompletionStage());	
	}
	
	public Mono<List<AdmWeightageDefinitionWorkExperienceDBO>> getAdmWeightageDefinitionWorkExperience() {
		 String str = "select bo from AdmWeightageDefinitionWorkExperienceDBO bo where bo.recordStatus = 'A'";
	     return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, AdmWeightageDefinitionWorkExperienceDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<AdmQualificationDegreeListDBO>> getAdmQualificationDegreeList(String admQualificationListId) {
		 String str = "select bo from AdmQualificationDegreeListDBO bo where bo.recordStatus = 'A'";
		 if(!Utils.isNullOrEmpty(admQualificationListId)) {
			 str += " and admQualificationListDBO.id = :admQualificationListId ";
		 }
		 String finalStr = str;
		 return  Mono.fromFuture(sessionFactory.withSession(s-> {
			 Mutiny.Query<AdmQualificationDegreeListDBO> query = s.createQuery(finalStr,AdmQualificationDegreeListDBO.class);
			 if (!Utils.isNullOrEmpty(admQualificationListId)) {
				 query.setParameter("admQualificationListId",Integer.parseInt(admQualificationListId));
			 }
			 return query.getResultList();
		 }).subscribeAsCompletionStage());
	}

	public Mono<List<Object>> getSelectionProcessDate(int year, int degreeId, String erpCampusProgramId) {
		 String queryString ="select distinct adm_selection_process_plan_detail.selection_process_date, adm_selection_process_plan_detail.adm_selection_process_plan_detail_id " + 
		 		" from adm_selection_process_plan_detail " + 
		 		" inner join adm_selection_process_plan_detail_prog ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_id " + 
		 		" inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id " + 
		 		" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_selection_process_plan_detail_prog.erp_campus_programme_mapping_id " + 
		 		" inner join adm_selection_process_type ON adm_selection_process_type.adm_selection_process_type_id = adm_selection_process_plan_detail.adm_selection_process_type_id " + 
		 		" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id " + 
		 		" inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id " + 
		 		" where adm_selection_process_type.mode='Group Process'" + 
		 		" and adm_selection_process_plan.erp_academic_year_id=:year" + 
		 		" and adm_selection_process_plan_detail.record_status = 'A' "+
		 		" and erp_programme_degree.erp_programme_degree_id=:degreeId ";
		 if (!Utils.isNullOrEmpty(erpCampusProgramId)) {
			 queryString += "and erp_programme.erp_programme_id = :erpCampusProgramId";
	     }
		 String finalStr = queryString;
		 return  Mono.fromFuture(sessionFactory.withSession(s-> {
			 Mutiny.Query<Object> query = s.createNativeQuery(finalStr,Object.class).setParameter("year", year).setParameter("degreeId", degreeId);
			 if (!Utils.isNullOrEmpty(erpCampusProgramId)) {
				 query.setParameter("erpCampusProgramId", erpCampusProgramId);
			 }
			 return query.getResultList();
		 }).subscribeAsCompletionStage());
	}

	public Mono<List<Object>> getSelectionProcessCenter(String date) {
		 LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    String queryString = "select distinct adm_selection_process_venue_city.adm_selection_process_venue_city_id, adm_selection_process_venue_city.venue_name " +
	    		             "from adm_selection_process_venue_city " + 
	    		             "inner join adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_venue_city_id = adm_selection_process_venue_city.adm_selection_process_venue_city_id " + 
	    		             "where adm_selection_process_plan_detail.selection_process_date = :date and adm_selection_process_venue_city.record_status= 'A'";
	    return  Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(queryString,Object.class).setParameter("date",localDate).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Object>>getCampusProgrammeMapping(int year, int degreeId) {
		 String queryString ="select distinct erp_campus_programme_mapping.erp_campus_programme_mapping_id, " + 
		 		" concat(erp_programme.programme_name, ' (', ifnull(erp_campus.campus_name, erp_location.location_name), ')') as campus_program_name " + 
		 		" from adm_selection_process_plan_detail " + 
		 		" inner join adm_selection_process_plan_detail_prog ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_id " + 
		 		" inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id " + 
		 		" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_selection_process_plan_detail_prog.erp_campus_programme_mapping_id " + 
		 		" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id " + 
		 		" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id " + 
		 		" inner join adm_selection_process_type ON adm_selection_process_type.adm_selection_process_type_id = adm_selection_process_plan_detail.adm_selection_process_type_id " + 
		 		" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id " + 
		 		" inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id " + 
		 		" where adm_selection_process_type.mode='Group Process' " + 
		 		" and adm_selection_process_plan.erp_academic_year_id=:year " + 
		 		" and adm_selection_process_plan_detail.record_status = 'A' " + 
		 		" and erp_programme_degree.erp_programme_degree_id=:degreeId";
		 return  Mono.fromFuture(sessionFactory.withSession(s-> {
			 Mutiny.Query<Object> query = s.createNativeQuery(queryString,Object.class).setParameter("year", year).setParameter("degreeId", degreeId);
			 return query.getResultList();
		 }).subscribeAsCompletionStage());
	}

	public Mono<List<Object[]>> getSelectionProcessTime(String selectionProcessDate, int selectionProcessVenueId) {
	    String queryString = "select  spAlot.adm_selection_process_plan_detail_allotment_id, spAlot.selection_process_time from adm_selection_process_plan_detail_allotment spAlot" + 
	    		" inner join adm_selection_process_plan_detail spd ON " + 
	    		" spd.adm_selection_process_plan_detail_id = spAlot. adm_selection_process_plan_detail_id" + 
	    		" inner join adm_selection_process_venue_city spVenue ON spVenue.adm_selection_process_venue_city_id = spd.adm_selection_process_venue_city_id" + 
	    		" where spAlot.record_status = 'A' and spd.record_status = 'A'" + 
	    		" and spVenue.record_status = 'A' and" + 
	    		" spVenue.adm_selection_process_venue_city_id=:selectionProcessVenueId" + 
	    		" and spd.selection_process_date=:selectionProcessDate";
	    return  Mono.fromFuture(
	    		sessionFactory.withSession(
	    		s->s.createNativeQuery(queryString,Object[].class)
	    		.setParameter("selectionProcessVenueId",selectionProcessVenueId)
	    		.setParameter("selectionProcessDate",LocalDate.parse(selectionProcessDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
	    		.getResultList()).subscribeAsCompletionStage());
	
	}
	
	public Mono<List<AdmQualificationListDBO>> getQualification(){
		String str = "from AdmQualificationListDBO dbo where dbo.isAdditionalDocument = 0 and dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, AdmQualificationListDBO.class).getResultList()).subscribeAsCompletionStage());	
	}

	public Mono<List<Tuple>> getProgrammeBySelectionProcessSession(String selectionProcessSession) {
		String query = "select erp_campus_programme_mapping.erp_campus_programme_mapping_id as mappingId," +
				"  erp_campus_programme_mapping.erp_campus_id as campusId,  erp_campus.campus_name as campusName," +
				"  erp_campus_programme_mapping.erp_location_id as locId, erp_location.location_name as locName," +
				"  erp_programme.erp_programme_id as programId, erp_programme.programme_name as programName, " +
				"  if(erp_campus_programme_mapping.erp_campus_id is not null,'C','L') as campusOrLocation " +
				" from adm_selection_process_plan " +
				" inner join adm_selection_process_plan_programme on adm_selection_process_plan_programme.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id " +
				" inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_selection_process_plan_programme.erp_campus_programme_mapping_id " +
				" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status = 'A'" +
				" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id and erp_location.record_status = 'A'" +
				" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status = 'A' " +
				" where adm_selection_process_plan.adm_selection_process_plan_id=:selectionProcessSession " +
				" and adm_selection_process_plan.record_status = 'A' and adm_selection_process_plan_programme.record_status = 'A' and erp_campus_programme_mapping.record_status = 'A'" +
				" order by programName asc,campusName asc";
		return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(query, Tuple.class)
				.setParameter("selectionProcessSession", Integer.parseInt(selectionProcessSession))
				.getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpUniversityBoardDBO>> getUniversityOrBoard() {
		String str = "from ErpUniversityBoardDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpUniversityBoardDBO.class).getResultList()).subscribeAsCompletionStage());
		
	}
	
	public StudentApplnEntriesDBO getadmApplnDetails(FinalResultApprovalDTO data) {
		String query = " select s from StudentApplnEntriesDBO s where s.recordStatus = 'A' and s.applicationNo= :applicationNo";
		return sessionFactory.withSession(s->s.createQuery(query, StudentApplnEntriesDBO.class)
				.setParameter("applicationNo",Integer.parseInt(data.getApplicationNumber())).getSingleResultOrNull()).await().indefinitely();        
	}

	 public List<Tuple> getPrerequisiteExamMonthsByExam(String erpCampusProgrammeMappingId, String examId, String erpAcademicYearId) throws Exception {
	        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
	            @SuppressWarnings("unchecked")
				@Override
	            public List<Tuple> onRun(EntityManager context) throws Exception {
	               Query query = context.createNativeQuery(" select adm_prerequisite_settings_period.exam_year as examYear," 
	                        +" adm_prerequisite_settings_period.exam_month as examMonth, " 
	                        +" adm_prerequisite_settings.is_exam_mandatory as isExamMandatory,adm_prerequisite_settings_period.adm_prerequisite_settings_period_id as periodId " 
	                        +" from erp_campus_programme_mapping " 
	                        +" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id " 
	                        +" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id " 
	                        +" left join erp_location campus_location ON campus_location.erp_location_id = erp_campus.erp_location_id " 
	                        +" inner join adm_prerequisite_settings on adm_prerequisite_settings.erp_location_id = ifnull(erp_location.erp_location_id,campus_location.erp_location_id) " 
	                        +" and erp_campus_programme_mapping.erp_programme_id=adm_prerequisite_settings.erp_programme_id " 
	                        +" inner join adm_prerequisite_exam ON adm_prerequisite_exam.adm_prerequisite_exam_id = adm_prerequisite_settings.adm_prerequisite_exam_id " 
	                        +" inner join adm_prerequisite_settings_period on adm_prerequisite_settings.adm_prerequisite_settings_id = adm_prerequisite_settings_period.adm_prerequisite_settings_id " 
	                        +" inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = adm_prerequisite_settings.erp_academic_year_id "
	                        +" where erp_campus_programme_mapping.erp_campus_programme_mapping_id=:erpCampusProgrammeMappingId and adm_prerequisite_settings.erp_academic_year_id=:erpAcademicYearId" 
	                        +" and erp_academic_year.is_current_academic_year=1 and erp_academic_year.record_status='A'"
	                        +" and adm_prerequisite_exam.adm_prerequisite_exam_id=:examId " 
	                        +" group by adm_prerequisite_settings.adm_prerequisite_settings_id, adm_prerequisite_settings_period.adm_prerequisite_settings_period_id order by adm_prerequisite_settings_period.exam_month,adm_prerequisite_settings_period.exam_year ", Tuple.class);
	                query.setParameter("erpCampusProgrammeMappingId" , Integer.parseInt(erpCampusProgrammeMappingId));
	                query.setParameter("examId" , Integer.parseInt(examId));
	                query.setParameter("erpAcademicYearId" , Integer.parseInt(erpAcademicYearId));
	                return query.getResultList();
	            }
	            @Override
	            public void onError(Exception error) throws Exception {
	                throw error;
	            }
	        });
	 }	
	 
	public Mono<List<ErpAdmissionCategoryDBO>> getAdmissionCategory() {
			String queryString = "select dbo from ErpAdmissionCategoryDBO dbo where dbo.recordStatus = 'A'";
			return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpAdmissionCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getSelectionProcessSession(String academicYearId) {
		String query = "select adm_selection_process_plan.adm_selection_process_plan_id as admSelectionProcessPlanId,adm_selection_process_plan.selection_process_session as sessionName " +
				" from adm_selection_process_plan" +
				" where adm_selection_process_plan.erp_academic_year_id=:academicYearId and adm_selection_process_plan.record_status='A' " +
				" and curdate() <= adm_selection_process_plan.selection_process_start_date";
		return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(query, Tuple.class)
				.setParameter("academicYearId", Integer.parseInt(academicYearId))
				.getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getSelectionProcessTypeBySession(String selectionProcessSession) {
		String query = "select distinct adm_selection_process_type.adm_selection_process_type_id as admSelectionProcessTypeId,adm_selection_process_type.selection_stage_name as selectionStageName," +
				" adm_selection_process_plan_detail.process_order as processOrder"+
				" from adm_selection_process_plan_detail" +
				" inner join adm_selection_process_type ON adm_selection_process_type.adm_selection_process_type_id = adm_selection_process_plan_detail.adm_selection_process_type_id" +
				" where adm_selection_process_plan_detail.record_status='A' and adm_selection_process_type.record_status='A'" +
				" and adm_selection_process_plan_detail.adm_selection_process_plan_id=:selectionProcessSession order by adm_selection_process_plan_detail.process_order";
		return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(query, Tuple.class)
				.setParameter("selectionProcessSession", Integer.parseInt(selectionProcessSession))
				.getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<AccFeeHeadsDBO>> getAccHeadsForApplicationFees() {
		String queryString = "select dbo from AccFeeHeadsDBO dbo where dbo.recordStatus = 'A' and dbo.feeHeadsType = 'Application Fee'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, AccFeeHeadsDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getSessionForProgramme(Integer programmeId,String yearId) {
		String queryString = " select distinct adm_intake_batch.adm_intake_batch_id as id ,adm_intake_batch.adm_intake_batch_name  as name from erp_programme" +
				" inner join erp_programme_batchwise_settings on erp_programme.erp_programme_id = erp_programme_batchwise_settings.erp_programme_id and erp_programme_batchwise_settings.record_status = 'A'" +
				" inner join  aca_session_group on erp_programme_batchwise_settings.aca_session_type_id = aca_session_group.aca_session_type_id and aca_session_group.record_status = 'A'" +
				" inner join aca_session_type ON aca_session_type.aca_session_type_id = erp_programme_batchwise_settings.aca_session_type_id  and aca_session_type.record_status = 'A'" +
				" inner join adm_intake_batch on adm_intake_batch.aca_session_group_id = aca_session_group.aca_session_group_id and adm_intake_batch.record_status = 'A'" +
				" where erp_programme.record_status = 'A' and erp_programme.erp_programme_id = :programmeId and erp_programme_batchwise_settings.batch_year_id = :yearId " +
				" and aca_session_type.total_session_intakes_in_year > 1 " +
				" order by name";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("programmeId",programmeId)
				.setParameter("yearId",Integer.parseInt(yearId)).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getProgrammeByYear(Integer yearId,Integer yearValue) {
		String queryString = "   select distinct  erp_programme.erp_programme_id as progId,erp_programme.programme_name_for_application as progName from erp_programme" +
				"  inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id" +
				"  inner join erp_programme_batchwise_settings on erp_programme_batchwise_settings.erp_programme_id = erp_programme.erp_programme_id" +
				"  where erp_programme.record_status = 'A' and erp_campus_programme_mapping.record_status = 'A' and erp_programme_batchwise_settings.batch_year_id = :yearId" +
				"  and erp_programme_batchwise_settings.record_status = 'A' and "+
				"  :yearValue >= programme_commence_year and (:yearValue < programme_inactivated_year or programme_inactivated_year = 0 or programme_inactivated_year is null)";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("yearId",yearId).setParameter("yearValue",yearValue)
				.getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<ErpCampusProgrammeMappingDBO>> getLocationOrCampusByProgrammeAndYear(String yearValue, String programId, Boolean isLocation) {
		String str = " select  dbo from ErpCampusProgrammeMappingDBO dbo"
				+" where :yearValue >= programme_commence_year and (:yearValue < programme_inactivated_year or programme_inactivated_year = 0 or programme_inactivated_year is null) and dbo.erpProgrammeDBO.id =:programId and dbo.recordStatus ='A'";
		if(isLocation) {
			str+= " and dbo.erpLocationDBO.recordStatus ='A' order by dbo.erpLocationDBO.locationName";
		} else {
			str+= " and dbo.erpCampusDBO.recordStatus ='A' order by dbo.erpCampusDBO.campusName";
		}
		String finalquery = str;
		Mono<List<ErpCampusProgrammeMappingDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<ErpCampusProgrammeMappingDBO> query = s.createQuery(finalquery, ErpCampusProgrammeMappingDBO.class);
			if(!Utils.isNullOrEmpty(programId)) {
				query.setParameter("programId",Integer.parseInt(programId));
			}
			query.setParameter("yearValue", Integer.parseInt(yearValue));
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public Mono<List<AdmAdmissionTypeDBO>> getAdmissionType() {
		String queryString = "select distinct dbo from AdmAdmissionTypeDBO dbo where dbo.recordStatus = 'A' order by dbo.admissionType";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, AdmAdmissionTypeDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Tuple getProgrammeMode( String yearValue, String programId) {
		String queryString = "select   erp_programme_addtnl_details.mode as mode from erp_programme_addtnl_details " +
				"  where record_status = 'A' and erp_programme_id = :programId " +
				"  and  :yearValue  >= changed_from_year  and :yearValue  <= changed_to_year ";
		return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("programId",Integer.parseInt(programId)).setParameter("yearValue",Integer.parseInt(yearValue))
				.getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<Tuple>> getProgrammeByYearAndIntakeAndType( String yearId, List<Integer> intakeId,String admissionType) {
		String queryString = "select distinct adm_programme_batch.adm_programme_batch_id as id, erp_programme.programme_name_for_application as pname , erp_academic_year.academic_year as batchYear ," +
				" adm_intake_batch.adm_intake_batch_name as intakeBatch,adm_admission_type.admission_type as atype," +
				" erp_campus.erp_campus_id as CampusID,  erp_campus.campus_name as CampusName, erp_campus.short_name as csName," +
				" erp_location.erp_location_id as LocID, erp_location.location_name as LocName,erp_location.location_short_name as locsName" +
				" from adm_programme_settings" +
				" inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = adm_programme_settings.adm_batch_year_id and erp_academic_year.record_status = 'A'" +
				" inner join adm_intake_batch ON adm_intake_batch.adm_intake_batch_id = adm_programme_settings.adm_intake_batch_id and adm_intake_batch.record_status = 'A'" +
				" inner join adm_admission_type ON adm_admission_type.adm_admission_type_id = adm_programme_settings.adm_admission_type_id and adm_admission_type.record_status = 'A'" +
				" inner join adm_programme_batch on adm_programme_settings.adm_programme_settings_id = adm_programme_batch.adm_programme_settings_id and adm_programme_batch.record_status = 'A'" +
				" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_programme_batch.erp_campus_programme_mapping_id and erp_campus_programme_mapping.record_status = 'A'" +
				" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id" +
				" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id" +
				" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status = 'A'" +
				" where adm_programme_settings.record_status = 'A' and adm_programme_settings.erp_academic_year_id = :yearId and adm_programme_settings.adm_intake_batch_id in (:intakeId)" +
				" and adm_programme_settings.adm_admission_type_id = :admissionType  order by pname asc  ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("yearId",Integer.parseInt(yearId))
				.setParameter("intakeId",intakeId).setParameter("admissionType",Integer.parseInt(admissionType))
				.getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<AdmIntakeBatchDBO>> getIntakeBatch() {
		String queryString = "select distinct dbo from AdmIntakeBatchDBO dbo where dbo.recordStatus = 'A' order by dbo.admIntakeBatchName";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, AdmIntakeBatchDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getProgrammeByPlan(Integer planId) {
		String queryString = "select distinct adm_programme_batch.adm_programme_batch_id as id, erp_programme.programme_name_for_application as name," +
				" erp_location.location_short_name as locs, erp_campus.short_name as cmps, adm_admission_type.admission_type as atype" +
				" from adm_selection_process_plan_programme" +
				" inner join adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = adm_selection_process_plan_programme.adm_programme_batch_id and adm_programme_batch.record_status = 'A'" +
				" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_programme_batch.erp_campus_programme_mapping_id and erp_campus_programme_mapping.record_status = 'A'" +
				" left join erp_location on erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id and erp_location.record_status = 'A'" +
				" left join erp_campus on erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status = 'A'" +
				" inner join adm_programme_settings on adm_programme_batch.adm_programme_settings_id = adm_programme_settings.adm_programme_settings_id and adm_programme_settings.record_status = 'A'" +
				" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status = 'A'" +
				" inner join adm_admission_type on adm_admission_type.adm_admission_type_id = adm_programme_settings.adm_admission_type_id and adm_admission_type.record_status = 'A'" +
				" where adm_selection_process_plan_programme.record_status = 'A' and adm_selection_process_plan_programme.adm_selection_process_plan_id = :planId" +
				" order by name";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("planId",planId).getResultList()).subscribeAsCompletionStage());
	}

}

