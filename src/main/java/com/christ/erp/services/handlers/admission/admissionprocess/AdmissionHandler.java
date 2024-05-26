package com.christ.erp.services.handlers.admission.admissionprocess;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ModeOfStudy;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeQualificationSubjectEligibilityDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpBloodGroupDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dbobjects.common.ErpDifferentlyAbledDBO;
import com.christ.erp.services.dbobjects.common.ErpExtraCurricularDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionReferenceDBO;
import com.christ.erp.services.dbobjects.common.ErpMotherToungeDBO;
import com.christ.erp.services.dbobjects.common.ErpOccupationDBO;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpReligionDBO;
import com.christ.erp.services.dbobjects.common.ErpReservationCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpSalutationDBO;
import com.christ.erp.services.dbobjects.common.ErpSecondLanguageDBO;
import com.christ.erp.services.dbobjects.common.ErpSportsDBO;
import com.christ.erp.services.dbobjects.common.ErpSportsLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dbobjects.common.ErpUniversityBoardDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dbobjects.student.common.StudentEducationalDetailsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentEducationalDetailsDocumentsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentEducationalMarkDetailsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentPersonalDataAddressDBO;
import com.christ.erp.services.dbobjects.student.common.StudentPersonalDataDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentExtraCurricularDetailsDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentWorkExperienceDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentWorkExperienceDocumentDBO;
import com.christ.erp.services.dto.student.common.StudentApplicationEditDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.StudentApplicationEditHandler;
import com.christ.erp.services.transactions.admission.admissionprocess.AdmissionTransaction;
import com.christ.erp.services.transactions.admission.applicationprocess.StudentApplicationEditTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

import reactor.core.publisher.Mono;

@Service
@SuppressWarnings("rawtypes")
public class AdmissionHandler {

	@Autowired
	AdmissionTransaction admissionTransaction;

	@Autowired
	private StudentApplicationEditHandler  studentApplicationEditHandler ;

	@Autowired
	private StudentApplicationEditTransaction  studentApplicationEditTransaction ;
	
	@Autowired
	CommonApiTransaction commonApiTransaction1;

	public Mono<StudentApplicationEditDTO> edit(Integer studentEntriesId) {
		Integer studentId = admissionTransaction.getStudentId(studentEntriesId);
		return Utils.isNullOrEmpty(studentId) ?  studentApplicationEditHandler.getStudentDetails(studentEntriesId): Mono.error(new NotFoundException("This applicant has already admitted Please use edit/view option"));
	}

	public Mono<ApiResult> saveOrUpdate(Mono<StudentApplicationEditDTO> data, String userId) {
		return data.handle((studentApplicationEditDTO, synchronousSink) -> {
			synchronousSink.next(studentApplicationEditDTO);
		}).cast(StudentApplicationEditDTO.class)
				.map(data1 -> convertDtoToDbo(data1, userId))
				.flatMap( s -> {
					admissionTransaction.save(s,userId);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public StudentDBO convertDtoToDbo(StudentApplicationEditDTO dto, String userId) {
		Tuple admAdmissionDataEntry = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_ADMISSION_DATA_ENTRY");
		StudentApplnEntriesDBO studentApplnEntriesDBO = studentApplicationEditTransaction.getStudentDetails(dto.getStudentApplnEntriesId());
		StudentDBO studentDBO = null;
		if(!Utils.isNullOrEmpty(studentApplnEntriesDBO)) {
			studentDBO = new StudentDBO();
			studentDBO.setStudentName(studentApplnEntriesDBO.getApplicantName());
			studentDBO.setStudentDob(studentApplnEntriesDBO.getDob());
			studentDBO.setErpGenderDBO(new ErpGenderDBO());
			studentDBO.getErpGenderDBO().setErpGenderId(studentApplnEntriesDBO.getErpGenderDBO().getErpGenderId());
			studentDBO.setStudentPersonalEmailId(studentApplnEntriesDBO.getPersonalEmailId());
			studentDBO.setStudentMobileNoCountryCode(studentApplnEntriesDBO.getMobileNoCountryCode());
			studentDBO.setStudentMobileNo(studentApplnEntriesDBO.getMobileNo());
			studentDBO.setAdmittedYearId(new ErpAcademicYearDBO());
			studentDBO.getAdmittedYearId().setId(studentApplnEntriesDBO.getAppliedAcademicYear().getId());
			studentDBO.setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDBO());
			studentDBO.getErpCampusProgrammeMappingId().setId(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getId());
			studentDBO.setModeOfStudy(ModeOfStudy.valueOf(dto.getModeOfStudy()));
			studentDBO.setErpAdmissionCategoryDBO(new ErpAdmissionCategoryDBO());
			studentDBO.getErpAdmissionCategoryDBO().setId(Integer.parseInt(dto.getAdmissionCategory().getValue()));
			if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentPersonalDataDBO().getStudentUniversityEmailId())) {	
				studentDBO.setStudentUniversityEmailId(studentApplnEntriesDBO.getStudentPersonalDataDBO().getStudentUniversityEmailId());		
			}
			studentDBO.setRecordStatus('A');
			studentDBO.setCreatedUsersId(Integer.parseInt(userId));



			//Personal Data
			StudentPersonalDataDBO studentPersonalDataDBO = studentApplnEntriesDBO.getStudentPersonalDataDBO();
			studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setModifiedUsersId(Integer.parseInt(userId));
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getErpCountry())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpCountryDBO(new ErpCountryDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpCountryDBO().setId(Integer.parseInt(dto.getPersonalData().getErpCountry().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getPassportNo())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setPassportNo(dto.getPersonalData().getPassportNo());
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getPassportDateOfExpiry())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setPassportDateOfExpiry(dto.getPersonalData().getPassportDateOfExpiry());
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getPassportIssuedCountry())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setPassportIssuedCountry(new ErpCountryDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportIssuedCountry().setId(Integer.parseInt(dto.getPersonalData().getPassportIssuedCountry().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getErpBloodGroup())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpBloodGroupDBO(new ErpBloodGroupDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpBloodGroupDBO().setId(Integer.parseInt(dto.getPersonalData().getErpBloodGroup().getValue()));			
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getErpMotherTounge())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpMotherToungeDBO(new ErpMotherToungeDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpMotherToungeDBO().setId(Integer.valueOf(dto.getPersonalData().getErpMotherTounge().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getErpReligionId())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpReligionDBO(new ErpReligionDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReligionDBO().setId(Integer.parseInt(dto.getPersonalData().getErpReligionId().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getErpReservationCategory())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpReservationCategoryDBO(new ErpReservationCategoryDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReservationCategoryDBO().setId(Integer.parseInt(dto.getPersonalData().getErpReservationCategory().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getDifferentlyAbled())) {
				if(dto.getPersonalData().getDifferentlyAbled()) {
					studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setIsDifferentlyAbled(dto.getPersonalData().getDifferentlyAbled());
					studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpDifferentlyAbledDBO(new ErpDifferentlyAbledDBO());
					studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpDifferentlyAbledDBO().setId(Integer.parseInt(dto.getPersonalData().getErpDifferentlyAbled().getValue()));
				} else {
					studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setIsDifferentlyAbled(dto.getPersonalData().getDifferentlyAbled());
				}
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getAadharNoShared())) {
				if(dto.getPersonalData().getAadharNoShared()) {
					studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setAadharNoShared(true);
					studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setAadharCardNo(dto.getPersonalData().getAadharCardNo());
				} else {
					studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setAadharNoShared(false);
					if(!Utils.isNullOrEmpty(dto.getPersonalData().getAadharEnrolled()) &&  dto.getPersonalData().getAadharEnrolled()) {
						studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setAadharEnrolled(true);
						studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setAadharEnrolmentNumber(dto.getPersonalData().getAadharEnrolmentNumber());
					} else {
						studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setAadharEnrolled(false);
					}
				}
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getErpInstitutionReference())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpInstitutionReferenceDBO(new ErpInstitutionReferenceDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpInstitutionReferenceDBO().setId(Integer.parseInt(dto.getPersonalData().getErpInstitutionReference().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getErpSecondLanguage())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setErpSecondLanguageDBO(new ErpSecondLanguageDBO());
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpSecondLanguageDBO().setId(Integer.parseInt(dto.getPersonalData().getErpSecondLanguage().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getResearchTopicDetails())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setResearchTopicDetails(dto.getPersonalData().getResearchTopicDetails());
			}
			if(!Utils.isNullOrEmpty(dto.getPersonalData().getAppliedUnderOverseasNri())) {
				studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().setAppliedUnderOverseasNri(dto.getPersonalData().getAppliedUnderOverseasNri());
			}

			Set<StudentExtraCurricularDetailsDBO> studentExtraCurricularDetailsDBOsUpdate = new HashSet<StudentExtraCurricularDetailsDBO>();
			Map<Integer,StudentExtraCurricularDetailsDBO> studentCurricularExistDataMap = 
					!Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentExtraCurricularDetailsDBOS()) ? studentApplnEntriesDBO.getStudentExtraCurricularDetailsDBOS().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) : null;
			dto.getPersonalData().getExtraCurricularDetailsDTOS().forEach( studentCurricularDTO -> {
				StudentExtraCurricularDetailsDBO studentExtraCurricularDetailsDBO = null;
				if(!Utils.isNullOrEmpty(studentCurricularDTO.getStudentExtraCurricularDetailsId()) && studentCurricularExistDataMap.containsKey(studentCurricularDTO.getStudentExtraCurricularDetailsId())) {
					studentExtraCurricularDetailsDBO = studentCurricularExistDataMap.get(studentCurricularDTO.getStudentExtraCurricularDetailsId());
					studentExtraCurricularDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
					studentCurricularExistDataMap.remove(studentCurricularDTO.getStudentExtraCurricularDetailsId());
				} else {
					studentExtraCurricularDetailsDBO = new StudentExtraCurricularDetailsDBO();
					studentExtraCurricularDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				studentExtraCurricularDetailsDBO.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
				studentExtraCurricularDetailsDBO.getStudentApplnEntriesDBO().setId(studentCurricularDTO.getStudentApplnEntriesId());
				studentExtraCurricularDetailsDBO.setErpExtraCurricularDBO(new ErpExtraCurricularDBO());
				studentExtraCurricularDetailsDBO.getErpExtraCurricularDBO().setId(Integer.parseInt(studentCurricularDTO.getErpExtraCurricular().getValue()));
				studentExtraCurricularDetailsDBO.setRecordStatus('A');
				studentExtraCurricularDetailsDBOsUpdate.add(studentExtraCurricularDetailsDBO);
			});

			dto.getPersonalData().getSportsDetailsDTOS().forEach( studentSportsDTO -> {
				StudentExtraCurricularDetailsDBO studentExtraCurricularDetailsDBO = null;
				if(!Utils.isNullOrEmpty(studentSportsDTO.getStudentExtraCurricularDetailsId())  && studentCurricularExistDataMap.containsKey(studentSportsDTO.getStudentExtraCurricularDetailsId())) {
					studentExtraCurricularDetailsDBO = studentCurricularExistDataMap.get(studentSportsDTO.getStudentExtraCurricularDetailsId());
					studentExtraCurricularDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
					studentCurricularExistDataMap.remove(studentSportsDTO.getStudentExtraCurricularDetailsId());
				} else {
					studentExtraCurricularDetailsDBO = new StudentExtraCurricularDetailsDBO();
					studentExtraCurricularDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				studentExtraCurricularDetailsDBO.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
				studentExtraCurricularDetailsDBO.getStudentApplnEntriesDBO().setId(studentSportsDTO.getStudentApplnEntriesId());
				studentExtraCurricularDetailsDBO.setErpSportsDBO(new ErpSportsDBO());
				studentExtraCurricularDetailsDBO.getErpSportsDBO().setId(Integer.parseInt(studentSportsDTO.getErpSports().getValue()));
				if(Utils.isNullOrEmpty(studentSportsDTO.getErpSportsLevel())) {
					studentExtraCurricularDetailsDBO.setErpSportsLevelDBO(new ErpSportsLevelDBO());
					studentExtraCurricularDetailsDBO.getErpSportsLevelDBO().setId(Integer.parseInt(studentSportsDTO.getErpSportsLevel().getValue()));
				}
				studentExtraCurricularDetailsDBO.setRecordStatus('A');
				studentExtraCurricularDetailsDBOsUpdate.add(studentExtraCurricularDetailsDBO);
			});
			if(!Utils.isNullOrEmpty(studentCurricularExistDataMap)) {
				studentCurricularExistDataMap.forEach((key,value) -> {
					value.setRecordStatus('D');
					value.setModifiedUsersId(Integer.parseInt(userId));
					studentExtraCurricularDetailsDBOsUpdate.add(value);
				});
			}
			studentApplnEntriesDBO.setStudentExtraCurricularDetailsDBOS(studentExtraCurricularDetailsDBOsUpdate);
			studentDBO.setStudentPersonalDataId(studentPersonalDataDBO);



			//studentAddressAndParentdetails
			StudentPersonalDataAddressDBO studentPersonalDataAddressDBO = studentApplnEntriesDBO.getStudentPersonalDataDBO().getStudentPersonalDataAddressDBO();
			studentPersonalDataAddressDBO.setModifiedUsersId(Integer.parseInt(userId));
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getCurrentAddressLine1())) {
				studentPersonalDataAddressDBO.setCurrentAddressLine1(dto.getStudentAddressAndParentdetails().getCurrentAddressLine1());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getCurrentAddressLine2())) {
				studentPersonalDataAddressDBO.setCurrentAddressLine2(dto.getStudentAddressAndParentdetails().getCurrentAddressLine2());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getCurrentCountry())) {
				studentPersonalDataAddressDBO.setErpCountryDBO(new ErpCountryDBO());
				studentPersonalDataAddressDBO.getErpCountryDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getCurrentCountry().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getCurrentState())) {
				studentPersonalDataAddressDBO.setErpStateDBO(new ErpStateDBO());
				studentPersonalDataAddressDBO.getErpStateDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getCurrentState().getValue()));
				studentPersonalDataAddressDBO.setCurrentStateOthers(null);
			} else {
				studentPersonalDataAddressDBO.setCurrentStateOthers(dto.getStudentAddressAndParentdetails().getCurrentStateOthers());
				studentPersonalDataAddressDBO.setErpStateDBO(null);
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getCurrentCity())) {
				studentPersonalDataAddressDBO.setErpCityDBO(new ErpCityDBO());
				studentPersonalDataAddressDBO.getErpCityDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getCurrentCity().getValue()));
				studentPersonalDataAddressDBO.setCurrentCityOthers(null);
			} else {
				studentPersonalDataAddressDBO.setCurrentCityOthers(dto.getStudentAddressAndParentdetails().getCurrentCityOthers());
				studentPersonalDataAddressDBO.setErpCityDBO(null);
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getCurrentPincode())) {
				studentPersonalDataAddressDBO.setCurrentPincode(dto.getStudentAddressAndParentdetails().getCurrentPincode());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getIsPermanentEqualsCurrent())) {
				studentPersonalDataAddressDBO.setPermanentEqualsCurrent(dto.getStudentAddressAndParentdetails().getIsPermanentEqualsCurrent());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getPermanentAddressLine1())) {
				studentPersonalDataAddressDBO.setPermanentAddressLine1(dto.getStudentAddressAndParentdetails().getPermanentAddressLine1());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getPermanentAddressLine2())) {
				studentPersonalDataAddressDBO.setPermanentAddressLine2(dto.getStudentAddressAndParentdetails().getPermanentAddressLine2());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getPermanentCountry())) {
				studentPersonalDataAddressDBO.setPermanentCountryDBO(new ErpCountryDBO());
				studentPersonalDataAddressDBO.getPermanentCountryDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getPermanentCountry().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getPermanentState())) {
				studentPersonalDataAddressDBO.setPermanentStateDBO(new ErpStateDBO());
				studentPersonalDataAddressDBO.getPermanentStateDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getPermanentState().getValue()));
				studentPersonalDataAddressDBO.setPermanentStateOthers(null);
			} else {
				studentPersonalDataAddressDBO.setPermanentStateOthers(dto.getStudentAddressAndParentdetails().getPermanentStateOthers());
				studentPersonalDataAddressDBO.setPermanentStateDBO(null);
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getPermanentCity())) {
				studentPersonalDataAddressDBO.setPermanentCityDBO(new ErpCityDBO());
				studentPersonalDataAddressDBO.getPermanentCityDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getPermanentCity().getValue()));
				studentPersonalDataAddressDBO.setPermanentCityOthers(null);
			} else {
				studentPersonalDataAddressDBO.setPermanentCityOthers(dto.getStudentAddressAndParentdetails().getPermanentCityOthers());
				studentPersonalDataAddressDBO.setPermanentCityDBO(null);
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getPermanentPincode())) {
				studentPersonalDataAddressDBO.setPermanentPincode(dto.getStudentAddressAndParentdetails().getPermanentPincode());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherSalutation())) {
				studentPersonalDataAddressDBO.setFatherErpSalutationDBO(new ErpSalutationDBO());
				studentPersonalDataAddressDBO.getFatherErpSalutationDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getFatherSalutation().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherName())) {
				studentPersonalDataAddressDBO.setFatherName(dto.getStudentAddressAndParentdetails().getFatherName());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherErpQualificationLevel())) {
				studentPersonalDataAddressDBO.setFatherErpQualificationLevelDBO(new ErpQualificationLevelDBO());
				studentPersonalDataAddressDBO.getFatherErpQualificationLevelDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getFatherErpQualificationLevel().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherOccupation())) {
				studentPersonalDataAddressDBO.setFatherErpOccupationDBO(new ErpOccupationDBO());
				studentPersonalDataAddressDBO.getFatherErpOccupationDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getFatherOccupation().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherYearlyIncomeRangeFrom())) {
				studentPersonalDataAddressDBO.setFatherYearlyIncomeRangeFrom(dto.getStudentAddressAndParentdetails().getFatherYearlyIncomeRangeFrom());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherYearlyIncomeRangeTo())) {
				studentPersonalDataAddressDBO.setFatherYearlyIncomeRangeTo(dto.getStudentAddressAndParentdetails().getFatherYearlyIncomeRangeTo());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherIncomeCurrency())) {
				studentPersonalDataAddressDBO.setFatherErpCurrencyDBO(new ErpCurrencyDBO());
				studentPersonalDataAddressDBO.getFatherErpCurrencyDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getFatherIncomeCurrency().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherEmail())) {
				studentPersonalDataAddressDBO.setFatherEmail(dto.getStudentAddressAndParentdetails().getFatherEmail());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherMobileNoCountryCode())) {
				studentPersonalDataAddressDBO.setFatherMobileNoCountryCode(dto.getStudentAddressAndParentdetails().getFatherMobileNoCountryCode());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getFatherMobileNo())) {
				studentPersonalDataAddressDBO.setFatherMobileNo(dto.getStudentAddressAndParentdetails().getFatherMobileNo());
			}

			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherSalutation())) {
				studentPersonalDataAddressDBO.setMotherErpSalutationDBO(new ErpSalutationDBO());
				studentPersonalDataAddressDBO.getMotherErpSalutationDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getMotherSalutation().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherName())) {
				studentPersonalDataAddressDBO.setMotherName(dto.getStudentAddressAndParentdetails().getMotherName());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherErpQualificationLevel())) {
				studentPersonalDataAddressDBO.setMotherErpQualificationLevelDBO(new ErpQualificationLevelDBO());
				studentPersonalDataAddressDBO.getMotherErpQualificationLevelDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getMotherErpQualificationLevel().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherOccupation())) {
				studentPersonalDataAddressDBO.setMotherErpOccupationDBO(new ErpOccupationDBO());
				studentPersonalDataAddressDBO.getMotherErpOccupationDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getMotherOccupation().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherYearlyIncomeRangeFrom())) {
				studentPersonalDataAddressDBO.setMotherYearlyIncomeRangeFrom(dto.getStudentAddressAndParentdetails().getMotherYearlyIncomeRangeFrom());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherYearlyIncomeRangeTo())) {
				studentPersonalDataAddressDBO.setMotherYearlyIncomeRangeTo(dto.getStudentAddressAndParentdetails().getMotherYearlyIncomeRangeTo());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherIncomeCurrency())) {
				studentPersonalDataAddressDBO.setMotherErpCurrencyDBO(new ErpCurrencyDBO());
				studentPersonalDataAddressDBO.getMotherErpCurrencyDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getMotherIncomeCurrency().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherEmail())) {
				studentPersonalDataAddressDBO.setMotherEmail(dto.getStudentAddressAndParentdetails().getMotherEmail());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherMobileNoCountryCode())) {
				studentPersonalDataAddressDBO.setMotherMobileNoCountryCode(dto.getStudentAddressAndParentdetails().getMotherMobileNoCountryCode());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getMotherMobileNo())) {
				studentPersonalDataAddressDBO.setMotherMobileNo(dto.getStudentAddressAndParentdetails().getMotherMobileNo());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianSalutation())) {
				studentPersonalDataAddressDBO.setGuardianErpSalutationDBO(new ErpSalutationDBO());
				studentPersonalDataAddressDBO.getGuardianErpSalutationDBO().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getGuardianSalutation().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianName())) {
				studentPersonalDataAddressDBO.setGuardianName(dto.getStudentAddressAndParentdetails().getGuardianName());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianEmail())) {
				studentPersonalDataAddressDBO.setGuardianEmail(dto.getStudentAddressAndParentdetails().getGuardianEmail());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianMobileNoCountryCode())) {
				studentPersonalDataAddressDBO.setGuardianMobileNoCountryCode(dto.getStudentAddressAndParentdetails().getGuardianMobileNoCountryCode());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianMobileNo())) {
				studentPersonalDataAddressDBO.setGuardianMobileNo(dto.getStudentAddressAndParentdetails().getGuardianMobileNo());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianAddressLine1())) {
				studentPersonalDataAddressDBO.setGuardianAddressLine1(dto.getStudentAddressAndParentdetails().getGuardianAddressLine1());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianAddressLine2())) {
				studentPersonalDataAddressDBO.setGuardianAddressLine2(dto.getStudentAddressAndParentdetails().getGuardianAddressLine2());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianCountry())) {
				studentPersonalDataAddressDBO.setGuardianCountry(new ErpCountryDBO());
				studentPersonalDataAddressDBO.getGuardianCountry().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getGuardianCountry().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianPincode())) {
				studentPersonalDataAddressDBO.setGuardianPincode(dto.getStudentAddressAndParentdetails().getGuardianPincode());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianState())) {
				studentPersonalDataAddressDBO.setGuardianState(new ErpStateDBO()); 
				studentPersonalDataAddressDBO.getGuardianState().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getGuardianState().getValue()));
				studentPersonalDataAddressDBO.setGuardianStateOthers(null);
			} else {
				studentPersonalDataAddressDBO.setGuardianState(null);
				studentPersonalDataAddressDBO.setGuardianStateOthers(dto.getStudentAddressAndParentdetails().getGuardianStateOthers());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getGuardianCity())) {
				studentPersonalDataAddressDBO.setGuardianCity(new ErpCityDBO());
				studentPersonalDataAddressDBO.getGuardianCity().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getGuardianCity().getValue()));
				studentPersonalDataAddressDBO.setGuardianCityOthers(null);
			} else {
				studentPersonalDataAddressDBO.setGuardianCity(null);
				studentPersonalDataAddressDBO.setGuardianCityOthers(dto.getStudentAddressAndParentdetails().getGuardianCityOthers());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getBirthCountry())) {
				studentPersonalDataAddressDBO.setBirthCountry(new ErpCountryDBO());
				studentPersonalDataAddressDBO.getBirthCountry().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getBirthCountry().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getBirthPincode())) {
				studentPersonalDataAddressDBO.setBirthPincode(dto.getStudentAddressAndParentdetails().getBirthPincode());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getBirthState())) {
				studentPersonalDataAddressDBO.setBirthState(new ErpStateDBO());
				studentPersonalDataAddressDBO.getBirthState().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getBirthState().getValue()));
				studentPersonalDataAddressDBO.setBirthStateOthers(null);
			} else {
				studentPersonalDataAddressDBO.setBirthStateOthers(dto.getStudentAddressAndParentdetails().getBirthStateOthers());
				studentPersonalDataAddressDBO.setBirthState(null);
			}
			if(!Utils.isNullOrEmpty(dto.getStudentAddressAndParentdetails().getBirthCity())) {
				studentPersonalDataAddressDBO.setBirthCity(new ErpCityDBO());
				studentPersonalDataAddressDBO.getBirthCity().setId(Integer.parseInt(dto.getStudentAddressAndParentdetails().getBirthCity().getValue()));
				studentPersonalDataAddressDBO.setBirthCityOthers(null);
			} else {
				studentPersonalDataAddressDBO.setBirthCityOthers(dto.getStudentAddressAndParentdetails().getBirthCityOthers());
				studentPersonalDataAddressDBO.setBirthCity(null);
			}


			//Educational Details
			Set<StudentEducationalDetailsDBO> studentEducationalDetailsDBOsUpdate = new HashSet<StudentEducationalDetailsDBO>();
			Map<Integer,StudentEducationalDetailsDBO> studentEducationalDetailsDBOMap = !Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentEducationalDetailsDBOS())?
					studentApplnEntriesDBO.getStudentEducationalDetailsDBOS().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)):null;
			//			StudentDBO studentDBORef = studentDBO;
			dto.getStudentEducationalDetailsDTOList().forEach( studentEducation -> {
				StudentEducationalDetailsDBO studentEducationalDetailsDBO = null;
				if(!Utils.isNullOrEmpty(studentEducation.getId())  && studentEducationalDetailsDBOMap.containsKey(studentEducation.getId())) {
					studentEducationalDetailsDBO = studentEducationalDetailsDBOMap.get(studentEducation.getId());
					studentEducationalDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
					studentEducationalDetailsDBOMap.remove(studentEducation.getId());
				}else {
					studentEducationalDetailsDBO = new StudentEducationalDetailsDBO();
					studentEducationalDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				studentEducationalDetailsDBO.setRecordStatus('A');
				//				studentEducationalDetailsDBO.setStudentDBO(studentDBORef);
				if(!Utils.isNullOrEmpty(studentEducation.getStudentApplnEntriesDTO())) {
					studentEducationalDetailsDBO.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
					studentEducationalDetailsDBO.getStudentApplnEntriesDBO().setId(studentEducation.getStudentApplnEntriesDTO().getId());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getAdmQualificationListDTO())) {
					studentEducationalDetailsDBO.setAdmQualificationListDBO(new AdmQualificationListDBO());
					studentEducationalDetailsDBO.getAdmQualificationListDBO().setId(studentEducation.getAdmQualificationListDTO().getId());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getAdmQualificationDegreeListDTO())) {
					studentEducationalDetailsDBO.setAdmQualificationDegreeListDBO(new AdmQualificationDegreeListDBO());
					studentEducationalDetailsDBO.getAdmQualificationDegreeListDBO().setId(studentEducation.getAdmQualificationDegreeListDTO().getId());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getUniversityBoard())) {
					studentEducationalDetailsDBO.setErpUniversityBoardDBO(new ErpUniversityBoardDBO());
					studentEducationalDetailsDBO.getErpUniversityBoardDBO().setId(Integer.parseInt(studentEducation.getUniversityBoard().getValue()));
				}
				if(!Utils.isNullOrEmpty(studentEducation.getErpInstitutionDTO())) {
					studentEducationalDetailsDBO.setErpInstitutionDBO(new ErpInstitutionDBO());
					studentEducationalDetailsDBO.getErpInstitutionDBO().setId(studentEducation.getErpInstitutionDTO().getId());
				} else {
					studentEducationalDetailsDBO.setInstitutionOthers(studentEducation.getInstitutionOthers());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getCountry())) {
					studentEducationalDetailsDBO.setInstitutionCountry(new ErpCountryDBO());
					studentEducationalDetailsDBO.getInstitutionCountry().setId(Integer.parseInt(studentEducation.getCountry().getValue()));
				}
				if(!Utils.isNullOrEmpty(studentEducation.getState())) {
					studentEducationalDetailsDBO.setInstitutionState(new ErpStateDBO());
					studentEducationalDetailsDBO.getInstitutionState().setId(Integer.parseInt(studentEducation.getState().getValue()));
				} else {
					studentEducationalDetailsDBO.setInstitutionOthersState(studentEducation.getInstitutionOthersState());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getNoOfPendingBacklogs())) {
					studentEducationalDetailsDBO.setNoOfPendingBacklogs(studentEducation.getNoOfPendingBacklogs());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getYearOfPassing())) {
					studentEducationalDetailsDBO.setYearOfPassing(studentEducation.getYearOfPassing());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getMonthOfPassing())) {
					studentEducationalDetailsDBO.setMonthOfPassing(studentEducation.getMonthOfPassing());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getExamRegisterNo())) {
					studentEducationalDetailsDBO.setExamRegisterNo(studentEducation.getExamRegisterNo());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getIsLevelCompleted())) {
					studentEducationalDetailsDBO.setIsLevelCompleted(studentEducation.getIsLevelCompleted());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getErpSessionType())) {
					studentEducationalDetailsDBO.setAcaSessionTypeId(new AcaSessionTypeDBO());
					studentEducationalDetailsDBO.getAcaSessionTypeId().setId(studentEducation.getErpSessionType().getId());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getTotalSemesters())) {
					studentEducationalDetailsDBO.setTotalSemesters(studentEducation.getTotalSemesters());
				}
				if(!Utils.isNullOrEmpty(studentEducation.getConsolidatedMarksObtained()) && !Utils.isNullOrEmpty(studentEducation.getConsolidatedMaximumMarks())){
					studentEducationalDetailsDBO.setConsolidatedMarksObtained(studentEducation.getConsolidatedMarksObtained());
					studentEducationalDetailsDBO.setConsolidatedMaximumMarks(studentEducation.getConsolidatedMaximumMarks());				
				}
				if(!Utils.isNullOrEmpty(studentEducation.getIsResultDeclared())) {
					studentEducationalDetailsDBO.setIsResultDeclared(studentEducation.getIsResultDeclared());
				}

				//marksDetails
				Set<StudentEducationalMarkDetailsDBO> studentEducationalMarkDetailsDBOUpdates = new HashSet<StudentEducationalMarkDetailsDBO>();
				Map<Integer,StudentEducationalMarkDetailsDBO> StudentEducationalMarkDetailsDBOMap = 
						!Utils.isNullOrEmpty(studentEducationalDetailsDBO.getStudentEducationalMarkDetailsDBOSet()) ? 
								studentEducationalDetailsDBO.getStudentEducationalMarkDetailsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) :null;
				studentEducation.getStudentEducationalMarkDetailS().forEach( marksDetials -> {
					StudentEducationalMarkDetailsDBO studentEducationalMarkDetailsDBO = null;
					if(!Utils.isNullOrEmpty(marksDetials.getId()) && StudentEducationalMarkDetailsDBOMap.containsKey(marksDetials.getId())) {
						studentEducationalMarkDetailsDBO = StudentEducationalMarkDetailsDBOMap.get(marksDetials.getId());
						studentEducationalMarkDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						StudentEducationalMarkDetailsDBOMap.remove(marksDetials.getId());
					} else {
						studentEducationalMarkDetailsDBO = new StudentEducationalMarkDetailsDBO();
						studentEducationalMarkDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					studentEducationalMarkDetailsDBO.setRecordStatus('A');
					if(!Utils.isNullOrEmpty(marksDetials.getAdmProgrammeQualificationSubjectEligibilityDTO())) {
						studentEducationalMarkDetailsDBO.setAdmProgrammeQualificationSubjectEligibilityDBO(new AdmProgrammeQualificationSubjectEligibilityDBO());
						studentEducationalMarkDetailsDBO.getAdmProgrammeQualificationSubjectEligibilityDBO().setId(Integer.parseInt(marksDetials.getAdmProgrammeQualificationSubjectEligibilityDTO().getId()));
					} else {
						studentEducationalMarkDetailsDBO.setSemesterName(marksDetials.getSemesterName());
					}
					if(!Utils.isNullOrEmpty(marksDetials.getDisplayOrder())) {
						studentEducationalMarkDetailsDBO.setDisplayOrder(marksDetials.getDisplayOrder());
					}
					if(!Utils.isNullOrEmpty(marksDetials.getMarksObtained())) {
//						studentEducationalMarkDetailsDBO.setMarksObtained(marksDetials.getMarksObtained());
					}
					if(!Utils.isNullOrEmpty(marksDetials.getMaximumMarks())) {
//						studentEducationalMarkDetailsDBO.setMaximumMarks(marksDetials.getMaximumMarks());
					}
					if(!Utils.isNullOrEmpty(marksDetials.getCgpa())) {
						studentEducationalMarkDetailsDBO.setCgpa(marksDetials.getCgpa());
					}
					if(!Utils.isNullOrEmpty(marksDetials.getSgpa())) {
						studentEducationalMarkDetailsDBO.setSgpa(marksDetials.getSgpa());
					}
					if(!Utils.isNullOrEmpty(marksDetials.getTotalPendingBacklogs())) {
						studentEducationalMarkDetailsDBO.setTotalPendingBacklogs(marksDetials.getTotalPendingBacklogs());
					}
					if(!Utils.isNullOrEmpty(marksDetials.getIsResultDeclared())) {
						studentEducationalMarkDetailsDBO.setIsResultDeclared(marksDetials.getIsResultDeclared()? true : false);
					} 
					studentEducationalMarkDetailsDBOUpdates.add(studentEducationalMarkDetailsDBO);
				});
				if(!Utils.isNullOrEmpty(StudentEducationalMarkDetailsDBOMap)) {
					StudentEducationalMarkDetailsDBOMap.forEach((key,value) -> {
						value.setRecordStatus('D');
						value.setModifiedUsersId(Integer.parseInt(userId));
						studentEducationalMarkDetailsDBOUpdates.add(value);
					});
				}
				studentEducationalDetailsDBO.setStudentEducationalMarkDetailsDBOSet(studentEducationalMarkDetailsDBOUpdates);


				//marksDetailsDocumentsUpload
				Set<StudentEducationalDetailsDocumentsDBO> StudentEducationalDetailsDocumentsDBOUpdates = new HashSet<StudentEducationalDetailsDocumentsDBO>();
				Map<Integer,StudentEducationalDetailsDocumentsDBO> StudentEducationalDetailsDocumentsDBOMap = 
						!Utils.isNullOrEmpty(studentEducationalDetailsDBO.getStudentEducationalDetailsDocumentsDBOSet()) ? 
								studentEducationalDetailsDBO.getStudentEducationalDetailsDocumentsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) :null;
				StudentEducationalDetailsDBO studentEducationalDetailsDBO1 = studentEducationalDetailsDBO;

				if(!Utils.isNullOrEmpty(studentEducation.getStudentEducationalDetailsDocuments())) {
					studentEducation.getStudentEducationalDetailsDocuments().forEach(document -> {
						File file = new File("StudentEducationalDocumentFiles//"+document.getFileName()+"."+document.getExtension());
						StudentEducationalDetailsDocumentsDBO studentEducationalDetailsDocumentsDBO = null;
						if(!Utils.isNullOrEmpty(StudentEducationalDetailsDocumentsDBOMap)) {
							if(Utils.isNullOrEmpty(document.getId()) && !StudentEducationalDetailsDocumentsDBOMap.containsKey(document.getId())) {
								studentEducationalDetailsDocumentsDBO = new StudentEducationalDetailsDocumentsDBO();
								studentEducationalDetailsDocumentsDBO.setCreatedUsersId(Integer.parseInt(userId));
							} 	else {
								studentEducationalDetailsDocumentsDBO = StudentEducationalDetailsDocumentsDBOMap.get(document.getId());
								studentEducationalDetailsDocumentsDBO.setModifiedUsersId(Integer.parseInt(userId));
								StudentEducationalDetailsDocumentsDBOMap.remove(document.getId());
							}		
						} else {
							studentEducationalDetailsDocumentsDBO = new StudentEducationalDetailsDocumentsDBO();
							studentEducationalDetailsDocumentsDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						studentEducationalDetailsDocumentsDBO.setStudentEducationalDetailsDBO(studentEducationalDetailsDBO1);
						studentEducationalDetailsDocumentsDBO.setDocumentsUrl(file.getAbsolutePath());
						studentEducationalDetailsDocumentsDBO.setRecordStatus('A');
						StudentEducationalDetailsDocumentsDBOUpdates.add(studentEducationalDetailsDocumentsDBO);
					});
					if(!Utils.isNullOrEmpty(StudentEducationalDetailsDocumentsDBOMap)) {
						StudentEducationalDetailsDocumentsDBOMap.forEach((key,value) -> {
							value.setRecordStatus('D');
							value.setModifiedUsersId(Integer.parseInt(userId));
							File file = new File(value.getDocumentsUrl());
							if(file.exists()) {
								file.delete();
							}
							StudentEducationalDetailsDocumentsDBOUpdates.add(value);
						});
					}	
				}
				studentEducationalDetailsDBO.setStudentEducationalDetailsDocumentsDBOSet(StudentEducationalDetailsDocumentsDBOUpdates);
				studentEducationalDetailsDBOsUpdate.add(studentEducationalDetailsDBO);		
			});
			if(!Utils.isNullOrEmpty(studentEducationalDetailsDBOMap)) {
				studentEducationalDetailsDBOMap.forEach((key,value) -> {
					value.setRecordStatus('D');
					value.setModifiedUsersId(Integer.parseInt(userId));
					studentEducationalDetailsDBOsUpdate.add(value);
				});
			}
			studentApplnEntriesDBO.setStudentEducationalDetailsDBOS(studentEducationalDetailsDBOsUpdate);

			//studentWorkExperience
			if(!Utils.isNullOrEmpty(dto.getIsHavingWorkExperience())) {
				studentApplnEntriesDBO.setIsHavingWorkExperience(studentApplnEntriesDBO.getIsHavingWorkExperience());
			} 

			Set<StudentWorkExperienceDBO> studentWorkExperienceDBOUpdates = new HashSet<StudentWorkExperienceDBO>();
			Map<Integer,StudentWorkExperienceDBO> studentWorkExperienceDBOMap = !Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentWorkExperienceDBOS()) ?
					studentApplnEntriesDBO.getStudentWorkExperienceDBOS().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)): null;
			dto.getStudentWorkExperienceDTOList().forEach(studentWorkExperience -> {
				StudentWorkExperienceDBO studentWorkExperienceDBO =null;
				if(!Utils.isNullOrEmpty(studentWorkExperience.getStudentWorkExperienceId()) && studentWorkExperienceDBOMap.containsKey(studentWorkExperience.getStudentWorkExperienceId())) {
					studentWorkExperienceDBO = studentWorkExperienceDBOMap.get(studentWorkExperience.getStudentWorkExperienceId());
					studentWorkExperienceDBO.setModifiedUsersId(Integer.parseInt(userId));
					studentWorkExperienceDBOMap.remove(studentWorkExperience.getStudentWorkExperienceId());
				}else {
					studentWorkExperienceDBO = new StudentWorkExperienceDBO();
					studentWorkExperienceDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				studentWorkExperienceDBO.setRecordStatus('A');
				if(!Utils.isNullOrEmpty(studentWorkExperience.getStudentApplnEntries())) {
					studentWorkExperienceDBO.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
					studentWorkExperienceDBO.getStudentApplnEntriesDBO().setId(studentWorkExperience.getStudentApplnEntries().getId());
				} 
				if(!Utils.isNullOrEmpty(studentWorkExperience.getOrganizationName())) {
					studentWorkExperienceDBO.setOrganizationName(studentWorkExperience.getOrganizationName());
				}
				if(!Utils.isNullOrEmpty(studentWorkExperience.getOrganizationAddress())) {
					studentWorkExperienceDBO.setOrganizationAddress(studentWorkExperience.getOrganizationAddress());
				}
				if(!Utils.isNullOrEmpty(studentWorkExperience.getDesignation())) {
					studentWorkExperienceDBO.setDesignation(studentWorkExperience.getDesignation());
				}
				if(!Utils.isNullOrEmpty(studentWorkExperience.getWorkExperienceFromDate())) {
					studentWorkExperienceDBO.setWorkExperienceFromDate(studentWorkExperience.getWorkExperienceFromDate());
				}
				if(!Utils.isNullOrEmpty(studentWorkExperience.getWorkExperienceToDate())) {
					studentWorkExperienceDBO.setWorkExperienceToDate(studentWorkExperience.getWorkExperienceToDate());
				}

				//WorkExperience Document Upload
				Set<StudentWorkExperienceDocumentDBO> StudentWorkExperienceDocumentDBOSet = new HashSet<StudentWorkExperienceDocumentDBO>();
				Map<Integer, StudentWorkExperienceDocumentDBO> studentWorkExperienceDocumentDBOMap = !Utils.isNullOrEmpty(studentWorkExperienceDBO.getStudentWorkExperienceDocumentDBOSet())?
						studentWorkExperienceDBO.getStudentWorkExperienceDocumentDBOSet().stream().filter(s -> s.getRecordStatus() == 'A')
						.collect(Collectors.toMap(s -> s.getId(), s -> s)):null;
				StudentWorkExperienceDBO studentWorkExperienceDBO1 = studentWorkExperienceDBO;
				if(!Utils.isNullOrEmpty(studentWorkExperience.getStudentWorkExperienceDocumentDTOList())) {
					studentWorkExperience.getStudentWorkExperienceDocumentDTOList().forEach( document -> {
						StudentWorkExperienceDocumentDBO studentWorkExperienceDocumentDBO = null;
						File file = new File("StudentWorkExperienceDocumentFiles//"+document.getFileName()+"."+document.getExtension());
						if(!Utils.isNullOrEmpty(studentWorkExperienceDocumentDBOMap)) {
							if(Utils.isNullOrEmpty(document.getId()) && !studentWorkExperienceDocumentDBOMap.containsKey(document.getId())) {
								studentWorkExperienceDocumentDBO = new StudentWorkExperienceDocumentDBO();
								studentWorkExperienceDocumentDBO.setCreatedUsersId(Integer.parseInt(userId));
							} 	else {
								studentWorkExperienceDocumentDBO = studentWorkExperienceDocumentDBOMap.get(document.getId());
								studentWorkExperienceDocumentDBO.setModifiedUsersId(Integer.parseInt(userId));
								studentWorkExperienceDocumentDBOMap.remove(document.getId());
							}
						} else {
							studentWorkExperienceDocumentDBO = new StudentWorkExperienceDocumentDBO();
							studentWorkExperienceDocumentDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						studentWorkExperienceDocumentDBO.setStudentWorkExperienceDBO(studentWorkExperienceDBO1);
						studentWorkExperienceDocumentDBO.setExperienceDocumentsUrl(file.getAbsolutePath());
						studentWorkExperienceDocumentDBO.setRecordStatus('A');
						StudentWorkExperienceDocumentDBOSet.add(studentWorkExperienceDocumentDBO);
					});
				}
				if(!Utils.isNullOrEmpty(studentWorkExperienceDocumentDBOMap)) {
					studentWorkExperienceDocumentDBOMap.forEach((key,value) -> {
						value.setRecordStatus('D');
						value.setModifiedUsersId(Integer.parseInt(userId));
						File file = new File(value.getExperienceDocumentsUrl());
						if(file.exists()) {
							file.delete();
						}
						StudentWorkExperienceDocumentDBOSet.add(value);
					});
				}
				studentWorkExperienceDBO.setStudentWorkExperienceDocumentDBOSet(StudentWorkExperienceDocumentDBOSet);
				studentWorkExperienceDBOUpdates.add(studentWorkExperienceDBO);
			});
			if(!Utils.isNullOrEmpty(studentWorkExperienceDBOMap)) {
				studentWorkExperienceDBOMap.forEach((key,value) -> {
					value.setRecordStatus('D');
					value.setModifiedUsersId(Integer.parseInt(userId));
					studentWorkExperienceDBOUpdates.add(value);
				});
			}
			studentApplnEntriesDBO.setStudentWorkExperienceDBOS(studentWorkExperienceDBOUpdates);
			studentApplnEntriesDBO.getApplicationCurrentProcessStatus().setId(Integer.parseInt(admAdmissionDataEntry.get("erp_work_flow_process_id").toString()));
			studentDBO.setStudentApplnEntriesDBO(studentApplnEntriesDBO);
		}		
		return studentDBO;
	}

}
