package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Tuple;

import com.christ.erp.services.dto.common.SelectDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.ErpSportsDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentEducationalDetailsDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentWorkExperienceDBO;
import com.christ.erp.services.dto.admission.applicationprocess.ApplicationListDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentApplnPreferenceDTO;
import com.christ.erp.services.dto.student.common.StudentApplnPrerequisiteDTO;
import com.christ.erp.services.dto.student.common.StudentApplnSelectionProcessDatesDTO;
import com.christ.erp.services.dto.student.common.StudentEducationalDetailsDTO;
import com.christ.erp.services.dto.student.common.StudentExtraCurricularDetailsDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddressDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddtnlDTO;
import com.christ.erp.services.dto.student.common.StudentWorkExperienceDTO;

@Repository
public class ApplicationListTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<ErpWorkFlowProcessDBO> getStatusDetails() {
		String query = " select  dbo FROM ErpWorkFlowProcessDBO dbo where  dbo.recordStatus  = 'A'";
		return  sessionFactory.withSession(s->s.createQuery(query,ErpWorkFlowProcessDBO.class).getResultList()).await().indefinitely();
	}

	public ErpStatusDBO getErpStatus(String statusCode) {
		String str = " select dbo from ErpStatusDBO dbo where dbo.recordStatus = 'A' and dbo.statusCode = :statusCode";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpStatusDBO.class).setParameter("statusCode", statusCode).getSingleResultOrNull()).await().indefinitely();
	}

	public Integer getPreferences(List<Integer> studIds) {
		String str = " select max(preference_order) from student_appln_preference where record_status = 'A' and student_appln_entries_id in (:studIds)";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("studIds", studIds).getSingleResultOrNull()).await().indefinitely();
	}

	public Tuple getSports(List<Integer> studIds) {
		String str = "select max(erp_sports_id) from student_extra_curricular_details where record_status = 'A' and student_appln_entries_id in (:studIds)";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("studIds", studIds).getSingleResultOrNull()).await().indefinitely();
	}

	public Tuple getEducationalCount(List<Integer> studIds) {
		String str = " select max(adm_qualification_list.qualification_order) from student_educational_details\n" +
				" inner join adm_qualification_list ON adm_qualification_list.adm_qualification_list_id = student_educational_details.adm_qualification_list_id and adm_qualification_list.record_status = 'A'\n" +
				" where student_educational_details.record_status = 'A' and  student_appln_entries_id in (:studIds)";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("studIds", studIds).getSingleResultOrNull()).await().indefinitely();
	}

	public List<AdmQualificationListDBO> getQualificationDetails() {
		String query = " select  dbo FROM AdmQualificationListDBO dbo where  dbo.recordStatus  = 'A' and dbo.isAdditionalDocument = 0";
		return  sessionFactory.withSession(s->s.createQuery(query,AdmQualificationListDBO.class).getResultList()).await().indefinitely();
	}

	public List<ErpSportsDBO> getSportsDetails() {
		String query = " select  dbo FROM ErpSportsDBO dbo where  dbo.recordStatus  = 'A'";
		return  sessionFactory.withSession(s->s.createQuery(query,ErpSportsDBO.class).getResultList()).await().indefinitely();
	}

	public List<StudentWorkExperienceDBO> getWorkExperience(List<Integer> studIds) {
		String query = " select  dbo FROM StudentWorkExperienceDBO dbo where  dbo.recordStatus  = 'A' and studentApplnEntriesDBO.id in (:studIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,StudentWorkExperienceDBO.class).setParameter("studIds", studIds).getResultList()).await().indefinitely();
	}

	public List<StudentEducationalDetailsDBO> getEducationalDetails() {
		String str = " select dbo from StudentEducationalDetailsDBO dbo where dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(str,StudentEducationalDetailsDBO.class).getResultList()).await().indefinitely();
	}

	public List<Integer> getWorkExp(List<Integer> studIds) {
		String str = " select count(student_work_experience.student_appln_entries_id) as countname"
				+ " from student_work_experience where record_status = 'A' and student_appln_entries_id in (:studIds)"
				+ " group by student_work_experience.student_appln_entries_id";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("studIds", studIds).getResultList()).await().indefinitely();
	}
	
	//
	public  List<Tuple> getStudentApplicationData1(ApplicationListDTO dto, Integer statusId, Integer cancelId) {
		String query =" select distinct student_appln_entries.student_appln_entries_id as id ,student_appln_entries.application_no as applicationNo,"
				+ "	student_appln_entries.applicant_name as name, student_appln_entries.dob as dob, erp_programme.programme_name as progName,"
				+ " erp_campus.campus_name as campusName, erp_location.location_name as locationName,"
				+ " url.file_name_unique, url.file_name_original, folder.upload_process_code "
				+ " from student_appln_entries "
				+ " left join student_appln_selection_process_dates on student_appln_entries.student_appln_entries_id = student_appln_selection_process_dates.student_appln_entries_id"
				+ " left join adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = student_appln_selection_process_dates.adm_selection_process_plan_detail_id"
				+ " left join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id"
				+ " left join adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = student_appln_entries.adm_programme_batch_id"
				+ " left join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_programme_batch.erp_campus_programme_mapping_id"
				+ " left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id"
				+ " left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id"
				+ " left join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id"
				+ " left join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme.erp_programme_level_id"
				+ " left join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id"
				+ " left join  student on student_appln_entries.student_appln_entries_id = student.student_appln_entries_id"
				+ " left join erp_status ON erp_status.erp_status_id = student.erp_status_id"
				+ " inner join student_personal_data ON student_personal_data.student_personal_data_id = student_appln_entries.student_personal_data_id and student_personal_data.record_status = 'A' "
//				+ " left join student_personal_data_addtnl on student_personal_data_addtnl.student_personal_data_addtnl_id = student_personal_data.student_personal_data_addtnl_id"
				+ "	left join url_access_link url ON student_appln_entries.profile_photo_url_id = url.url_access_link_id"
				+ "	left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id"
				+ " where student_appln_entries.record_status = 'A' and student_appln_entries.applied_academic_year_id = :yearId";
		if(Utils.isNullOrEmpty(dto.getStatus()) || !dto.getStatus().replace(" ", "").equals("Draft")){
			query += " and student_appln_entries.application_no is not null ";
		}
		if(!Utils.isNullOrEmpty(dto.getStatus()) &&  !dto.getStatus().replace(" ", "").equals("Cancelled") && !Utils.isNullOrEmpty(statusId)) {
			query += " and student_appln_entries.application_current_process_status = :statusId ";
		}
		if(!Utils.isNullOrEmpty(dto.getStatus()) &&  dto.getStatus().replace(" ", "").equals("Cancelled")) {
			query +=" and erp_status.erp_status_id = :cancelId";
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessSession())) {
			query += " and adm_selection_process_plan.adm_selection_process_plan_id =  :sessionId";
		}
		if(!Utils.isNullOrEmpty(dto.getLocation())) {
			query += "  and (erp_location.erp_location_id in (:locationId) or erp_campus.erp_location_id in (:locationId)) ";
		}
		if(!Utils.isNullOrEmpty(dto.getCampusName())) {
			query += " and erp_campus.erp_campus_id in (:campusId)";
		}
		if(!Utils.isNullOrEmpty(dto.getLevel())) {
			query += " and erp_programme_level.erp_programme_level_id = :levelId";
		}
		if(!Utils.isNullOrEmpty(dto.getDegree())) {
			query += " and erp_programme_degree.erp_programme_degree_id = :degreeId";
		}
		if(!Utils.isNullOrEmpty(dto.getProgrammeName())) {
			query += " and erp_programme.erp_programme_id = :programmeId";
		}
		if(!Utils.isNullOrEmpty(dto.getSubmittedFrom())) {
			query += " and Date(student_appln_entries.submission_date_time) >= :fromDate";
		}
		if(!Utils.isNullOrEmpty(dto.getSubmittedTill())) {
			query += " and Date(student_appln_entries.submission_date_time) <= :tillDate";
		}

		String finalquery = query;
		List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalquery, Tuple.class);
		query1.setParameter("yearId",Integer.parseInt(dto.getAcademicYear().getValue()));
		if(!Utils.isNullOrEmpty(dto.getStatus()) &&  !dto.getStatus().replace(" ", "").equals("Cancelled") && !Utils.isNullOrEmpty(statusId)) {
			query1.setParameter("statusId", statusId);
		}
		if(!Utils.isNullOrEmpty(dto.getStatus()) &&  dto.getStatus().replace(" ", "").equals("Cancelled")) {
			query1.setParameter("cancelId", cancelId);
		}
		if(!Utils.isNullOrEmpty(dto.getSelectionProcessSession())) {
			query1.setParameter("sessionId", Integer.parseInt(dto.getSelectionProcessSession().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getLocation())) {
			Set<Integer> loctionsIds = new HashSet<>();
			for (SelectDTO selectDTO : dto.getLocation()) {
				loctionsIds.add(Integer.parseInt(selectDTO.getValue()));
			}
			query1.setParameter("locationId", loctionsIds);
		}
		if(!Utils.isNullOrEmpty(dto.getCampusName())) {
			Set<Integer> campusIds = new HashSet<>();
			for (SelectDTO selectDTO : dto.getCampusName()) {
				campusIds.add(Integer.parseInt(selectDTO.getValue()));
			}
			query1.setParameter("campusId", campusIds);
		}
		if(!Utils.isNullOrEmpty(dto.getLevel())) {
			query1.setParameter("levelId", Integer.parseInt(dto.getLevel().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getDegree())) {
			query1.setParameter("degreeId", Integer.parseInt(dto.getDegree().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getProgrammeName())) {
			query1.setParameter("programmeId", Integer.parseInt(dto.getProgrammeName().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getSubmittedFrom())) {
			query1.setParameter("fromDate", dto.getSubmittedFrom().atStartOfDay());
		}
		if(!Utils.isNullOrEmpty(dto.getSubmittedTill())) {
			query1.setParameter("tillDate", dto.getSubmittedTill().atStartOfDay());
		}
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public List<StudentApplnEntriesDTO> getStudentAppEntries(List<Integer> studEntriesIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO"
				+ "(dbo.id,spda.id,spdad.id,dbo.applicationNo,dbo.applicantName,dbo.dob,dbo.erpGenderDBO.genderName,dbo.mobileNoCountryCode,dbo.mobileNo,dbo.personalEmailId,dbo.submissionDateTime,ep.programmeName"
				+ " ,ec.campusName,el.locationName,es.specializationName,erc.residentCategoryName,eay.academicYear,aat.admissionType,aib.admIntakeBatchName,dbo.applicationVerificationStatus,dbo.applicationVerifiedDate"
				+ " ,avu.userName,sv.verificationRemarksName, dbo.applicationVerificationAddtlRemarks,dbo.totalWeightage,dbo.feePaymentFinalDateTime,ewf.applicationStatusDisplayText,dbo.selectionStatusRemarks"
				+ " ,dbo.applicationStatusTime,acs.applicantStatusDisplayText,dbo.applicantStatusTime,s.registerNo,dbo.totalPartTimePreviousExperienceMonths,dbo.totalPartTimePreviousExperienceYears"
				+ " ,dbo.totalPreviousExperienceMonths,dbo.totalPreviousExperienceYears,apb.campusLocationDisplayName"
				+ " )"
				+ " from StudentApplnEntriesDBO dbo"
				+ " left join dbo.studentPersonalDataDBO spd"
				+ " left join spd.studentPersonalDataAddtnlDBO spda"
				+ " left join spd.studentPersonalDataAddressDBO spdad"
				+ " left join dbo.erpGenderDBO"
				+ " left join dbo.admProgrammeBatchDBO apb"
				+ " left join apb.erpCampusProgrammeMappingDBO ecpm"
				+ " left join ecpm.erpProgrammeDBO ep"
				+ " left join ecpm.erpCampusDBO ec"
				+ " left join ecpm.erpLocationDBO el"
				+ " left join dbo.erpSpecializationDBO es"
				+ " left join dbo.erpResidentCategoryDBO erc"
//				+ " left join dbo.acaBatchDBO ab"
				+ " left join dbo.admProgrammeBatchDBO apb"
				+ " left join apb.admProgrammeSettingsDBO aps"
				+ " left join aps.erpAcademicYearDBO eay"
				+ " left join aps.admAdmissionTypeDBO aat"
				+ " left join aps.admIntakeBatchDBO aib"
				+ " left join dbo.applicationVerifiedUserId avu"
				+ " left join dbo.applicationCurrentProcessStatus ewf"
				+ " left join dbo.studentApplnVerificationsId sv"
				+ " left join dbo.applicantCurrentProcessStatus acs"
				+ " left join dbo.studentApplnRegistrationsDBO sr"
				+ " left join sr.studentDBO s"
				+ " where dbo.recordStatus  = 'A' and dbo.id in (:studEntriesIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentApplnEntriesDTO.class).setParameter("studEntriesIds", studEntriesIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentPersonalDataAddtnlDTO> getStudentPersonalDataAddtnl(List<Integer> studentPersonalDataAddtnlIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentPersonalDataAddtnlDTO"
				+ "(dbo.id,ec.countryName,ebg.bloodGroupName,er.religionName,erc.reservationCategoryName,emt.motherToungeName,dbo.isDifferentlyAbled,eda.differentlyAbledName"
				+ " ,dbo.aadharCardNo,dbo.aadharEnrolmentNumber,dbo.pioOrOci,dbo.pioOrOciCardNo,dbo.birthPlace,dbo.passportNo,dbo.passportIssuedDate"
				+ " ,dbo.passportDateOfExpiry,pic.countryName,esl.secondLanguageName,dbo.researchTopicDetails,air.institutionReferenceName,bs.stateName,dbo.birthStateOthers ,bc.cityName,dbo.birthCityOthers,dbo.birthPincode"
				+ " ,bcn.countryName,dbo.sponsershipName,dbo.sponsershipEmail,dbo.sponsershipPhoneNumber,dbo.sponsershipNoCountryCode,ssc.countryName)"
				+ " from StudentPersonalDataAddtnlDBO dbo"
				+ " left join dbo.erpCountryDBO ec"
				+ " left join dbo.erpBloodGroupDBO ebg"
				+ " left join dbo.erpReligionDBO er"
				+ " left join dbo.erpReservationCategoryDBO erc"
				+ " left join dbo.erpMotherToungeDBO emt"
				+ " left join dbo.erpDifferentlyAbledDBO eda"
				+ " left join dbo.passportIssuedCountry pic"
				+ " left join dbo.erpSecondLanguageDBO esl"
				+ " left join dbo.admInstitutionReferenceDBO air"
				+ " left join dbo.birthStateDBO bs"
				+ " left join dbo.birthCityDBO bc"
				+ " left join dbo.birthCountryDBO bcn"
				+ " left join dbo.sponsershipCountry ssc"
				+ " where dbo.recordStatus  = 'A' and dbo.id in (:studentPersonalDataAddtnlIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentPersonalDataAddtnlDTO.class).setParameter("studentPersonalDataAddtnlIds", studentPersonalDataAddtnlIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentPersonalDataAddressDTO> getStudentPersonalDataAddress(List<Integer> studentPersonalDataAddressIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentPersonalDataAddressDTO"
				+ "(dbo.id,dbo.currentAddressLine1,dbo.currentAddressLine2,ec.cityName,dbo.currentCityOthers,es.stateName,dbo.currentStateOthers,ecd.countryName"
				+ " ,dbo.currentPincode,dbo.permanentAddressLine1,dbo.permanentAddressLine2,pc.cityName,dbo.permanentCityOthers,ps.stateName,dbo.permanentStateOthers"
				+ " ,pcd.countryName,dbo.permanentPincode,fs.erpSalutationName ,dbo.fatherName,dbo.fatherEmail,dbo.fatherMobileNoCountryCode,dbo.fatherMobileNo,fql.qualificationLevelName"
				+ " ,fo.occupationName,dbo.fatherYearlyIncomeRangeFrom "
				+ " , dbo.fatherYearlyIncomeRangeTo,fc.currencyName,ms.erpSalutationName,dbo.motherName,dbo.motherEmail,dbo.motherMobileNoCountryCode,dbo.motherMobileNo"
				+ " ,mql.qualificationLevelName,mo.occupationName,dbo.motherYearlyIncomeRangeFrom,dbo.motherYearlyIncomeRangeTo,mc.currencyName"
				+ " ,gs.erpSalutationName,dbo.guardianName,dbo.guardianEmail,dbo.guardianMobileNoCountryCode,dbo.guardianMobileNo,dbo.familyAnnualIncome,fai.currencyName"
				+ " )"
				+ " from StudentPersonalDataAddressDBO dbo"
				+ " left join dbo.erpCityDBO ec"
				+ " left join dbo.erpStateDBO es"
				+ " left join dbo.erpCountryDBO ecd"
				+ " left join dbo.permanentCityDBO pc"
				+ " left join dbo.permanentStateDBO ps"
				+ " left join dbo.permanentCountryDBO pcd"
				+ " left join dbo.fatherErpSalutationDBO fs"
				+ " left join dbo.fatherErpQualificationLevelDBO fql"
				+ " left join dbo.fatherErpOccupationDBO fo"
				+ " left join dbo.fatherErpCurrencyDBO fc"
				+ " left join dbo.motherErpSalutationDBO ms"
				+ " left join dbo.motherErpQualificationLevelDBO mql"
				+ " left join dbo.motherErpOccupationDBO mo"
				+ " left join dbo.motherErpCurrencyDBO mc"
				+ " left join dbo.guardianErpSalutationDBO  gs"
				+ " left join dbo.familyAnnualIncomeCurrency fai"
				+ " where dbo.recordStatus  = 'A' and dbo.id in (:studentPersonalDataAddressIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentPersonalDataAddressDTO.class).setParameter("studentPersonalDataAddressIds", studentPersonalDataAddressIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentApplnPreferenceDTO> getStudentApplnPreferenceDTO(List<Integer> studEntriesIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentApplnPreferenceDTO"
				+ "(dbo.id,dbo.studentApplnEntriesDBO.id,dbo.preferenceOrder,ep.programmeName,el.locationName,ec.campusName)"
				+ " from StudentApplnPreferenceDBO dbo"
				+ " left join dbo.admProgrammeBatchDBO apb"
				+ " left join apb.erpCampusProgrammeMappingDBO ecpm"
				+ " left join ecpm.erpProgrammeDBO ep"
				+ " left join ecpm.erpLocationDBO el"
				+ " left join ecpm.erpCampusDBO ec "
				+ " where dbo.recordStatus  = 'A' and dbo.studentApplnEntriesDBO.id in (:studEntriesIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentApplnPreferenceDTO.class).setParameter("studEntriesIds", studEntriesIds).getResultList()).await().indefinitely();
	}

	public List<StudentApplnSelectionProcessDatesDTO> getStudentApplnSelectionProcessDatesDTO(List<Integer> studEntriesIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentApplnSelectionProcessDatesDTO"
				+ "(dbo.id,dbo.studentApplnEntriesDBO.id,aspt.selectionStageName,aspd.selectionProcessDate,aspd.selectionProcessTime,aspv.venueName,aspd.processOrder,"
				+ " asptd.selectionStageName,asppd.selectionProcessDate,asppd.selectionProcessTime,aspvd.venueName,asppd.processOrder"
				+ " )"
				+ " from StudentApplnSelectionProcessDatesDBO dbo"
				+ " left join dbo.admSelectionProcessPlanDetailDBO aspd"
				+ " left join dbo.admSelectionProcessPlanCenterBasedDBO sppcb"
				+ " left join sppcb.admSelectionProcessPlanDetailDBO asppd"
				+ " left join aspd.admSelectionProcessTypeDBO aspt"
				+ " left join aspd.admSelectionProcessVenueCityDBO aspv"
				+ " left join asppd.admSelectionProcessTypeDBO asptd"
				+ " left join sppcb.admSelectionProcessVenueCityDBO aspvd"
				+ " where dbo.recordStatus  = 'A' and dbo.studentApplnEntriesDBO.id in (:studEntriesIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentApplnSelectionProcessDatesDTO.class).setParameter("studEntriesIds", studEntriesIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentEducationalDetailsDTO> getStudentEducationalDetailsDTO(List<Integer> studEntriesIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentEducationalDetailsDTO"
				+ "(dbo.id,dbo.studentApplnEntriesDBO.id,aql.qualificationOrder,aqd.degreeName,eub.universityBoardName,ic.countryName,eis.stateName,dbo.institutionOthersState"
				+ " ,ei.institutionName,dbo.institutionOthers,dbo.yearOfPassing,dbo.monthOfPassing,dbo.consolidatedMaximumMarks,dbo.consolidatedMarksObtained,dbo.percentage"
				+ " )"
				+ " from StudentEducationalDetailsDBO dbo"
				+ " left join dbo.admQualificationListDBO aql"
				+ " left join dbo.admQualificationDegreeListDBO aqd"
				+ " left join dbo.erpUniversityBoardDBO eub"
				+ " left join dbo.institutionCountry ic"
				+ " left join dbo.institutionState eis"
				+ " left join dbo.erpInstitutionDBO ei"
				+ " where dbo.recordStatus  = 'A' and dbo.studentApplnEntriesDBO.id in (:studEntriesIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentEducationalDetailsDTO.class).setParameter("studEntriesIds", studEntriesIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentApplnPrerequisiteDTO> getStudentApplnPrerequisiteDTO(List<Integer> studEntriesIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentApplnPrerequisiteDTO"
				+ "(dbo.id,dbo.studentApplnEntriesDBO.id,apsdp.examYear,apsdp.examMonth,apsd.totalMarks,ape.examName,dbo.marksObtained,dbo.examRollNo"
				+ " )"
				+ " from StudentApplnPrerequisiteDBO dbo"
				+ " left join dbo.admPrerequisiteSettingsDetailsPeriodDBO  apsdp"
				+ " left join apsdp.admPrerequisiteSettingsDetailsDBO apsd"
				+ " left join apsd.admPrerequisiteExamDBO ape"
				+ " left join apsd.admPrerequisiteSettingsDBO aps"
				+ " where dbo.recordStatus  = 'A' and dbo.studentApplnEntriesDBO.id in (:studEntriesIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentApplnPrerequisiteDTO.class).setParameter("studEntriesIds", studEntriesIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentExtraCurricularDetailsDTO> getStudentExtraCurricularDetailsDTO(List<Integer> studEntriesIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentExtraCurricularDetailsDTO"
				+ "(dbo.id,dbo.studentApplnEntriesDBO.id,es.id,esl.sportsLevelName"
				+ " )"
				+ " from StudentExtraCurricularDetailsDBO dbo"
				+ " left join dbo.erpSportsDBO es"
				+ " left join dbo.erpSportsLevelDBO esl"
				+ " where dbo.recordStatus  = 'A' and dbo.studentApplnEntriesDBO.id in (:studEntriesIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentExtraCurricularDetailsDTO.class).setParameter("studEntriesIds", studEntriesIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentWorkExperienceDTO> getStudentWorkExperienceDTO(List<Integer> studEntriesIds) {
		String query =" select new com.christ.erp.services.dto.student.common.StudentWorkExperienceDTO"
				+ "(dbo.id,dbo.studentApplnEntriesDBO.id,dbo.organizationName,eo.occupationName,dbo.organizationAddress,dbo.designation,dbo.workExperienceFromDate,dbo.workExperienceToDate"
				+ " ,dbo.workExperienceYears,dbo.workExperienceMonth,dbo.occupationOthers"
				+ " )"
				+ " from StudentWorkExperienceDBO dbo"
				+ " left join dbo.erpOccupationDBO eo"
				+ " where dbo.recordStatus  = 'A' and dbo.studentApplnEntriesDBO.id in (:studEntriesIds)";
		return sessionFactory.withSession(s->s.createQuery(query,StudentWorkExperienceDTO.class).setParameter("studEntriesIds", studEntriesIds).getResultList()).await().indefinitely();
	}
}
