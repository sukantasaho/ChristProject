package com.christ.erp.services.handlers.admission.applicationprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeQualificationSubjectEligibilityDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpBloodGroupDBO;
import com.christ.erp.services.dbobjects.common.ErpCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dbobjects.common.ErpDifferentlyAbledDBO;
import com.christ.erp.services.dbobjects.common.ErpExtraCurricularDBO;
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
import com.christ.erp.services.dbobjects.student.common.StudentEducationalDetailsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentEducationalDetailsDocumentsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentEducationalMarkDetailsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentPersonalDataAddressDBO;
import com.christ.erp.services.dbobjects.student.common.StudentPersonalDataDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnSelectionProcessDatesDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentExtraCurricularDetailsDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentWorkExperienceDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentWorkExperienceDocumentDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeQualificationSubjectEligibilityDTO;
import com.christ.erp.services.dto.admission.settings.AdmQualificationDegreeListDTO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.ErpInstitutionDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionTypeDTO;
import com.christ.erp.services.dto.student.common.StudentApplicationEditDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentApplnSelectionProcessDatesDTO;
import com.christ.erp.services.dto.student.common.StudentBasicProfileDTO;
import com.christ.erp.services.dto.student.common.StudentEducationalDetailsDTO;
import com.christ.erp.services.dto.student.common.StudentEducationalDetailsDocumentsDTO;
import com.christ.erp.services.dto.student.common.StudentEducationalMarkDetailsDTO;
import com.christ.erp.services.dto.student.common.StudentExtraCurricularDetailsDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddressDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddtnlDTO;
import com.christ.erp.services.dto.student.common.StudentWorkExperienceDTO;
import com.christ.erp.services.dto.student.common.StudentWorkExperienceDocumentDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.admission.applicationprocess.StudentApplicationEditTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@Service
public class StudentApplicationEditHandler {

	@Autowired
	private StudentApplicationEditTransaction  studentApplicationEditTransaction;

	public Flux<StudentApplicationEditDTO> getApplicantListCard(String yearId, String applicationNoOrName, String programmeId) {
		Boolean isNumeric = null;
		if(!Utils.isNullOrEmpty(applicationNoOrName)) {
			isNumeric = applicationNoOrName.chars().allMatch( Character::isDigit );
		}
		return studentApplicationEditTransaction.getApplicantListCard(yearId,applicationNoOrName,programmeId,isNumeric).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public StudentApplicationEditDTO convertDBOToDTO(Tuple dbo) {
		StudentApplicationEditDTO dto = new StudentApplicationEditDTO();
		dto.setStudentApplnEntriesId(Integer.parseInt(dbo.get("studentId").toString()));
		if(!Utils.isNullOrEmpty(dbo.get("applicationNumber"))) {
			dto.setApplicationNo(Integer.parseInt(dbo.get("applicationNumber").toString()));
		}
		dto.setStudentName(dbo.get("name").toString());
		if(!Utils.isNullOrEmpty(dbo.get("photoUrl"))) {
			dto.setPhotoUrl(dbo.get("photoUrl").toString());
		}
		dto.setProgramApplied(new SelectDTO());
		dto.getProgramApplied().setValue(dbo.get("programmeId").toString());
		dto.getProgramApplied().setLabel(dbo.get("programmeName").toString());
		return dto;
	}

	public Mono<StudentApplicationEditDTO> getStudentDetails(Integer studentId) {
		return this.convertDBOToDTO(studentApplicationEditTransaction.getStudentDetails(studentId));
	}

	public Mono<StudentApplicationEditDTO> convertDBOToDTO(StudentApplnEntriesDBO dbo) {
		StudentApplicationEditDTO dto = null;
		if(!Utils.isNullOrEmpty(dbo)){
			dto = new StudentApplicationEditDTO();
			dto.setStudentApplnEntriesId(dbo.getId());
			dto.setApplicationNo(dbo.getApplicationNo());
			dto.setStudentName(dbo.getApplicantName());
			dto.setPhotoUrl(dbo.getStudentPersonalDataDBO().getStudentPersonalDataAddtnlDBO().getProfilePhotoUrl());
			dto.setProgramApplied(new SelectDTO());
			dto.getProgramApplied().setValue(String.valueOf(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId()));
			dto.getProgramApplied().setLabel(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			dto.setAppliedCampus(new SelectDTO());
			dto.getAppliedCampus().setValue(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO()) ? String.valueOf(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId())
					:String.valueOf(dbo.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getId()));
			dto.getAppliedCampus().setLabel(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO()) ? String.valueOf(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName())
					:String.valueOf(dbo.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName()));
			dto.setIsHavingWorkExperience(dbo.getIsHavingWorkExperience()?true:false);

			//Admission Details used for Admission Screen
			if(!Utils.isNullOrEmpty(dbo.getErpAdmissionCategoryDBO())) {
				dto.setAdmissionCategory(new SelectDTO());
				dto.getAdmissionCategory().setValue(String.valueOf(dbo.getErpAdmissionCategoryDBO().getId()));
				dto.getAdmissionCategory().setLabel(dbo.getErpAdmissionCategoryDBO().getAdmissionCategoryName());
			}
			if(!Utils.isNullOrEmpty(dbo.getModeOfStudy())) {	
				dto.setModeOfStudy(dbo.getModeOfStudy().toString());
			}

			//Programme selection Details
			StudentBasicProfileDTO programmeSelectionDetails = new StudentBasicProfileDTO();
			programmeSelectionDetails.setCandidateName(dbo.getStudentApplnRegistrationsDBO().getApplicantName());
			programmeSelectionDetails.setDob(dbo.getStudentApplnRegistrationsDBO().getApplicantDob());
			programmeSelectionDetails.setGender(new SelectDTO());
			programmeSelectionDetails.getGender().setValue(String.valueOf(dbo.getErpGenderDBO().getErpGenderId()));	
			programmeSelectionDetails.getGender().setLabel(dbo.getErpGenderDBO().getGenderName());
			programmeSelectionDetails.setMobileNoCountryCode(dbo.getStudentPersonalDataDBO().getStudentMobileNoCountryCode());
			programmeSelectionDetails.setMobileNo(dbo.getStudentPersonalDataDBO().getStudentMobileNo());
			programmeSelectionDetails.setEmailId(dbo.getStudentPersonalDataDBO().getStudentPersonalEmailId());

			dbo.getStudentApplnPreferenceDBOS().forEach( studentPreference -> {
				if(studentPreference.getRecordStatus() == 'A') {	
					if(studentPreference.getPreferenceOrder() == 1) {
						programmeSelectionDetails.setProgrammePreference1(new SelectDTO());
						programmeSelectionDetails.getProgrammePreference1().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId()));
						programmeSelectionDetails.getProgrammePreference1().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
						programmeSelectionDetails.setLocation1(new SelectDTO());
						if(!Utils.isNullOrEmpty(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO())) {
							programmeSelectionDetails.getLocation1().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getId()));
							programmeSelectionDetails.getLocation1().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
						} else {
							programmeSelectionDetails.getLocation1().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId()));
							programmeSelectionDetails.getLocation1().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
						}
					}
					if(studentPreference.getPreferenceOrder() == 2) {
						programmeSelectionDetails.setProgrammePreference2(new SelectDTO());
						programmeSelectionDetails.getProgrammePreference2().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId()));
						programmeSelectionDetails.getProgrammePreference2().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
						programmeSelectionDetails.setLocation2(new SelectDTO());
						if(!Utils.isNullOrEmpty(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO())) {
							programmeSelectionDetails.getLocation2().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getId()));
							programmeSelectionDetails.getLocation2().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
						} else {
							programmeSelectionDetails.getLocation2().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId()));
							programmeSelectionDetails.getLocation2().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
						}
					}
					if(studentPreference.getPreferenceOrder() == 3) {
						programmeSelectionDetails.setProgrammePreference3(new SelectDTO());
						programmeSelectionDetails.getProgrammePreference3().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId()));
						programmeSelectionDetails.getProgrammePreference3().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
						programmeSelectionDetails.setLocation3(new SelectDTO());
						if(!Utils.isNullOrEmpty(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO())) {
							programmeSelectionDetails.getLocation3().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getId()));
							programmeSelectionDetails.getLocation3().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
						} else {
							programmeSelectionDetails.getLocation3().setValue(String.valueOf(studentPreference.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId()));
							programmeSelectionDetails.getLocation3().setLabel(studentPreference.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
						}
					}
				}
			});
			dto.setBasicProfileDTO(programmeSelectionDetails);

			//Personal Data
			StudentPersonalDataDBO studentPersonalDataDBO = dbo.getStudentPersonalDataDBO();
			StudentPersonalDataAddtnlDTO studentPersonalData = new StudentPersonalDataAddtnlDTO();
			studentPersonalData.setId(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getId());
			studentPersonalData.setErpCountry(new SelectDTO());
			studentPersonalData.getErpCountry().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpCountryDBO().getId()));
			studentPersonalData.getErpCountry().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpCountryDBO().getCountryName());
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportNo())) {
				studentPersonalData.setPassportNo(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportNo());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportDateOfExpiry())) {
				studentPersonalData.setPassportDateOfExpiry(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportDateOfExpiry());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportIssuedCountry())) {
				studentPersonalData.setPassportIssuedCountry(new SelectDTO());
				studentPersonalData.getPassportIssuedCountry().setValue((String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportIssuedCountry().getId())));
				studentPersonalData.getPassportIssuedCountry().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getPassportIssuedCountry().getCountryName());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpBloodGroupDBO())) {
				studentPersonalData.setErpBloodGroup(new SelectDTO());
				studentPersonalData.getErpBloodGroup().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpBloodGroupDBO().getId()));
				studentPersonalData.getErpBloodGroup().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpBloodGroupDBO().getBloodGroupName());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpMotherToungeDBO())) {
				studentPersonalData.setErpMotherTounge(new SelectDTO());
				studentPersonalData.getErpMotherTounge().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpMotherToungeDBO().getId()));
				studentPersonalData.getErpMotherTounge().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpMotherToungeDBO().getMotherToungeName());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReligionDBO())) {
				studentPersonalData.setErpReligionId(new SelectDTO());
				studentPersonalData.getErpReligionId().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReligionDBO().getId()));
				studentPersonalData.getErpReligionId().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReligionDBO().getReligionName());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReservationCategoryDBO())) {
				studentPersonalData.setErpReservationCategory(new SelectDTO());
				studentPersonalData.getErpReservationCategory().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReservationCategoryDBO().getId()));
				studentPersonalData.getErpReservationCategory().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpReservationCategoryDBO().getReservationCategoryName());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getIsDifferentlyAbled())) {
				if(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getIsDifferentlyAbled()) {
					studentPersonalData.setDifferentlyAbled(true);
					studentPersonalData.setErpDifferentlyAbled(new SelectDTO());
					studentPersonalData.getErpDifferentlyAbled().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpDifferentlyAbledDBO().getId()));
					studentPersonalData.getErpDifferentlyAbled().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpDifferentlyAbledDBO().getDifferentlyAbledName());
				} else {
					studentPersonalData.setDifferentlyAbled(false);
				}
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAadharNoShared())) {
				if(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAadharNoShared()) {
					studentPersonalData.setAadharNoShared(true); 
					studentPersonalData.setAadharCardNo(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAadharCardNo());
				} else {
					studentPersonalData.setAadharNoShared(false);
					if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAadharEnrolled()) &&  studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAadharEnrolled()) {
						studentPersonalData.setAadharEnrolled(true);
						studentPersonalData.setAadharEnrolmentNumber(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAadharEnrolmentNumber());
					} else {
						studentPersonalData.setAadharEnrolled(false);
					}
				}
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpInstitutionReferenceDBO())) {
				studentPersonalData.setErpInstitutionReference(new SelectDTO());
				studentPersonalData.getErpInstitutionReference().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpInstitutionReferenceDBO().getId()));
				studentPersonalData.getErpInstitutionReference().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpInstitutionReferenceDBO().getInstitutionReferenceName());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpSecondLanguageDBO())) {
				studentPersonalData.setErpSecondLanguage(new SelectDTO());
				studentPersonalData.getErpSecondLanguage().setValue(String.valueOf(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpSecondLanguageDBO().getId()));
				studentPersonalData.getErpSecondLanguage().setLabel(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getErpSecondLanguageDBO().getSecondLanguageName());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getResearchTopicDetails())) {
				studentPersonalData.setResearchTopicDetails(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getResearchTopicDetails());
			}
			if(!Utils.isNullOrEmpty(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAppliedUnderOverseasNri())) {
				studentPersonalData.setAppliedUnderOverseasNri(studentPersonalDataDBO.getStudentPersonalDataAddtnlDBO().getAppliedUnderOverseasNri());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentExtraCurricularDetailsDBOS())) {
				List<StudentExtraCurricularDetailsDTO> studentSportsDetailsDTOS = new ArrayList<StudentExtraCurricularDetailsDTO>();
				List<StudentExtraCurricularDetailsDTO> studentExtraCurricularDetailsDTOS = new ArrayList<StudentExtraCurricularDetailsDTO>();
				dbo.getStudentExtraCurricularDetailsDBOS().forEach( extraCurricular -> {
					if(extraCurricular.getRecordStatus() == 'A') {
						StudentExtraCurricularDetailsDTO curricularDTO = new StudentExtraCurricularDetailsDTO();
						curricularDTO.setStudentExtraCurricularDetailsId(extraCurricular.getId());
						curricularDTO.setStudentApplnEntriesId(extraCurricular.getStudentApplnEntriesDBO().getId());
						if(!Utils.isNullOrEmpty(extraCurricular.getErpExtraCurricularDBO())) {
							curricularDTO.setErpExtraCurricular(new SelectDTO());
							curricularDTO.getErpExtraCurricular().setValue(String.valueOf(extraCurricular.getErpExtraCurricularDBO().getId()));
							curricularDTO.getErpExtraCurricular().setLabel(extraCurricular.getErpExtraCurricularDBO().getExtraCurricularName());
							studentExtraCurricularDetailsDTOS.add(curricularDTO);
						} else {
							if(!Utils.isNullOrEmpty(extraCurricular.getErpSportsDBO())) {
								curricularDTO.setErpSports(new SelectDTO());
								curricularDTO.getErpSports().setValue(String.valueOf(extraCurricular.getErpSportsDBO().getId()));
								curricularDTO.getErpSports().setLabel(extraCurricular.getErpSportsDBO().getSportsName());
							}
							if(!Utils.isNullOrEmpty(extraCurricular.getErpSportsLevelDBO())) {
								curricularDTO.setErpSportsLevel(new SelectDTO());
								curricularDTO.getErpSportsLevel().setValue(String.valueOf(extraCurricular.getErpSportsLevelDBO().getId()));
								curricularDTO.getErpSportsLevel().setLabel(extraCurricular.getErpSportsLevelDBO().getSportsLevelName());
							}
							studentSportsDetailsDTOS.add(curricularDTO);
						}	
					}
				});
				studentPersonalData.setSportsDetailsDTOS(studentSportsDetailsDTOS);
				studentPersonalData.setExtraCurricularDetailsDTOS(studentExtraCurricularDetailsDTOS);
			}
			dto.setPersonalData(studentPersonalData);

			//studentAddressAndParentdetails
			StudentPersonalDataAddressDBO studentPersonalDataAddressDBO = dbo.getStudentPersonalDataDBO().getStudentPersonalDataAddressDBO();
			if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO) && studentPersonalDataAddressDBO.recordStatus == 'A') {
				StudentPersonalDataAddressDTO StudentPersonalDataAddressDTO = new StudentPersonalDataAddressDTO();
				StudentPersonalDataAddressDTO.setStudentPersonalDataAddressId(studentPersonalDataAddressDBO.getId());
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getCurrentAddressLine1())) {
					StudentPersonalDataAddressDTO.setCurrentAddressLine1(studentPersonalDataAddressDBO.getCurrentAddressLine1());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getCurrentAddressLine2())) {
					StudentPersonalDataAddressDTO.setCurrentAddressLine2(studentPersonalDataAddressDBO.getCurrentAddressLine2());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getErpCountryDBO())) {
					StudentPersonalDataAddressDTO.setCurrentCountry(new SelectDTO());
					StudentPersonalDataAddressDTO.getCurrentCountry().setValue(String.valueOf(studentPersonalDataAddressDBO.getErpCountryDBO().getId()));
					StudentPersonalDataAddressDTO.getCurrentCountry().setLabel(studentPersonalDataAddressDBO.getErpCountryDBO().getCountryName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getErpStateDBO())) {
					StudentPersonalDataAddressDTO.setCurrentState(new SelectDTO());
					StudentPersonalDataAddressDTO.getCurrentState().setValue(String.valueOf(studentPersonalDataAddressDBO.getErpStateDBO().getId()));
					StudentPersonalDataAddressDTO.getCurrentState().setLabel(studentPersonalDataAddressDBO.getErpStateDBO().getStateName());
					StudentPersonalDataAddressDTO.setCurrentStateOthers(null);
				} else {
					StudentPersonalDataAddressDTO.setCurrentStateOthers(studentPersonalDataAddressDBO.getCurrentStateOthers());
					StudentPersonalDataAddressDTO.setCurrentState(null);
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getErpCityDBO())) {
					StudentPersonalDataAddressDTO.setCurrentCity(new SelectDTO());
					StudentPersonalDataAddressDTO.getCurrentCity().setValue(String.valueOf(studentPersonalDataAddressDBO.getErpCityDBO().getId()));
					StudentPersonalDataAddressDTO.getCurrentCity().setLabel(studentPersonalDataAddressDBO.getErpCityDBO().getCityName());
					StudentPersonalDataAddressDTO.setCurrentCityOthers(null);
				} else {
					StudentPersonalDataAddressDTO.setCurrentCityOthers(studentPersonalDataAddressDBO.getCurrentCityOthers());
					StudentPersonalDataAddressDTO.setCurrentCity(null);
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getCurrentPincode())) {
					StudentPersonalDataAddressDTO.setCurrentPincode(studentPersonalDataAddressDBO.getCurrentPincode());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getPermanentEqualsCurrent())) {
					StudentPersonalDataAddressDTO.setIsPermanentEqualsCurrent(studentPersonalDataAddressDBO.getPermanentEqualsCurrent() ? true:false);
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getPermanentAddressLine1())) {
					StudentPersonalDataAddressDTO.setPermanentAddressLine1(studentPersonalDataAddressDBO.getPermanentAddressLine1());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getPermanentAddressLine2())) {
					StudentPersonalDataAddressDTO.setPermanentAddressLine2(studentPersonalDataAddressDBO.getPermanentAddressLine2());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getPermanentCountryDBO())) {
					StudentPersonalDataAddressDTO.setPermanentCountry(new SelectDTO());
					StudentPersonalDataAddressDTO.getPermanentCountry().setValue(String.valueOf(studentPersonalDataAddressDBO.getPermanentCountryDBO().getId()));
					StudentPersonalDataAddressDTO.getPermanentCountry().setLabel(studentPersonalDataAddressDBO.getPermanentCountryDBO().getCountryName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getPermanentStateDBO())) {
					StudentPersonalDataAddressDTO.setPermanentState(new SelectDTO());
					StudentPersonalDataAddressDTO.getPermanentState().setValue(String.valueOf(studentPersonalDataAddressDBO.getPermanentStateDBO().getId()));
					StudentPersonalDataAddressDTO.getPermanentState().setLabel(studentPersonalDataAddressDBO.getPermanentStateDBO().getStateName());
					StudentPersonalDataAddressDTO.setPermanentStateOthers(null);
				} else {
					StudentPersonalDataAddressDTO.setPermanentStateOthers(studentPersonalDataAddressDBO.getPermanentStateOthers());
					StudentPersonalDataAddressDTO.setPermanentState(null);
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getPermanentCityDBO())) {
					StudentPersonalDataAddressDTO.setPermanentCity(new SelectDTO());
					StudentPersonalDataAddressDTO.getPermanentCity().setValue(String.valueOf(studentPersonalDataAddressDBO.getPermanentCityDBO().getId()));
					StudentPersonalDataAddressDTO.getPermanentCity().setLabel(studentPersonalDataAddressDBO.getPermanentCityDBO().getCityName());
					StudentPersonalDataAddressDTO.setPermanentCityOthers(null);
				} else {
					StudentPersonalDataAddressDTO.setPermanentCityOthers(studentPersonalDataAddressDBO.getPermanentCityOthers());
					StudentPersonalDataAddressDTO.setPermanentCity(null);
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getPermanentPincode())) {
					StudentPersonalDataAddressDTO.setPermanentPincode(studentPersonalDataAddressDBO.getPermanentPincode());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherErpSalutationDBO())) {
					StudentPersonalDataAddressDTO.setFatherSalutation(new SelectDTO());
					StudentPersonalDataAddressDTO.getFatherSalutation().setValue(String.valueOf(studentPersonalDataAddressDBO.getFatherErpSalutationDBO().getId()));
					StudentPersonalDataAddressDTO.getFatherSalutation().setLabel(studentPersonalDataAddressDBO.getFatherErpSalutationDBO().getErpSalutationName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherName())) {
					StudentPersonalDataAddressDTO.setFatherName(studentPersonalDataAddressDBO.getFatherName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherErpQualificationLevelDBO())) {
					StudentPersonalDataAddressDTO.setFatherErpQualificationLevel(new SelectDTO());
					StudentPersonalDataAddressDTO.getFatherErpQualificationLevel().setValue(String.valueOf(studentPersonalDataAddressDBO.getFatherErpQualificationLevelDBO().getId()));
					StudentPersonalDataAddressDTO.getFatherErpQualificationLevel().setLabel(studentPersonalDataAddressDBO.getFatherErpQualificationLevelDBO().getQualificationLevelName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherErpOccupationDBO())) {
					StudentPersonalDataAddressDTO.setFatherOccupation(new SelectDTO());
					StudentPersonalDataAddressDTO.getFatherOccupation().setValue(String.valueOf(studentPersonalDataAddressDBO.getFatherErpOccupationDBO().getId()));
					StudentPersonalDataAddressDTO.getFatherOccupation().setLabel(studentPersonalDataAddressDBO.getFatherErpOccupationDBO().getOccupationName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherYearlyIncomeRangeFrom())) {
					StudentPersonalDataAddressDTO.setFatherYearlyIncomeRangeFrom(studentPersonalDataAddressDBO.getFatherYearlyIncomeRangeFrom());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherYearlyIncomeRangeTo())) {
					StudentPersonalDataAddressDTO.setFatherYearlyIncomeRangeTo(studentPersonalDataAddressDBO.getFatherYearlyIncomeRangeTo());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherErpCurrencyDBO())) {
					StudentPersonalDataAddressDTO.setFatherIncomeCurrency(new SelectDTO());
					StudentPersonalDataAddressDTO.getFatherIncomeCurrency().setValue(String.valueOf(studentPersonalDataAddressDBO.getFatherErpCurrencyDBO().getId()));
					StudentPersonalDataAddressDTO.getFatherIncomeCurrency().setLabel(studentPersonalDataAddressDBO.getFatherErpCurrencyDBO().getCurrencyName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherEmail())) {
					StudentPersonalDataAddressDTO.setFatherEmail(studentPersonalDataAddressDBO.getFatherEmail());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherMobileNoCountryCode())) {
					StudentPersonalDataAddressDTO.setFatherMobileNoCountryCode(studentPersonalDataAddressDBO.getFatherMobileNoCountryCode());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getFatherMobileNo())) {
					StudentPersonalDataAddressDTO.setFatherMobileNo(studentPersonalDataAddressDBO.getFatherMobileNo());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherErpSalutationDBO())) {
					StudentPersonalDataAddressDTO.setMotherSalutation(new SelectDTO());
					StudentPersonalDataAddressDTO.getMotherSalutation().setValue(String.valueOf(studentPersonalDataAddressDBO.getMotherErpSalutationDBO().getId()));
					StudentPersonalDataAddressDTO.getMotherSalutation().setLabel(studentPersonalDataAddressDBO.getMotherErpSalutationDBO().getErpSalutationName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherName())) {
					StudentPersonalDataAddressDTO.setMotherName(studentPersonalDataAddressDBO.getMotherName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherErpQualificationLevelDBO())) {
					StudentPersonalDataAddressDTO.setMotherErpQualificationLevel(new SelectDTO());
					StudentPersonalDataAddressDTO.getMotherErpQualificationLevel().setValue(String.valueOf(studentPersonalDataAddressDBO.getMotherErpQualificationLevelDBO().getId()));
					StudentPersonalDataAddressDTO.getMotherErpQualificationLevel().setLabel(studentPersonalDataAddressDBO.getMotherErpQualificationLevelDBO().getQualificationLevelName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherErpOccupationDBO())) {
					StudentPersonalDataAddressDTO.setMotherOccupation(new SelectDTO());
					StudentPersonalDataAddressDTO.getMotherOccupation().setValue(String.valueOf(studentPersonalDataAddressDBO.getMotherErpOccupationDBO().getId()));
					StudentPersonalDataAddressDTO.getMotherOccupation().setLabel(studentPersonalDataAddressDBO.getMotherErpOccupationDBO().getOccupationName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherYearlyIncomeRangeFrom())) {
					StudentPersonalDataAddressDTO.setMotherYearlyIncomeRangeFrom(studentPersonalDataAddressDBO.getMotherYearlyIncomeRangeFrom());
				}
				if( !Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherYearlyIncomeRangeTo())) {
					StudentPersonalDataAddressDTO.setMotherYearlyIncomeRangeTo(studentPersonalDataAddressDBO.getMotherYearlyIncomeRangeTo());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherErpCurrencyDBO())) {
					StudentPersonalDataAddressDTO.setMotherIncomeCurrency(new SelectDTO());
					StudentPersonalDataAddressDTO.getMotherIncomeCurrency().setValue(String.valueOf(studentPersonalDataAddressDBO.getMotherErpCurrencyDBO().getId()));
					StudentPersonalDataAddressDTO.getMotherIncomeCurrency().setLabel(studentPersonalDataAddressDBO.getMotherErpCurrencyDBO().getCurrencyName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherEmail())) {
					StudentPersonalDataAddressDTO.setMotherEmail(studentPersonalDataAddressDBO.getMotherEmail());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherMobileNoCountryCode())) {
					StudentPersonalDataAddressDTO.setMotherMobileNoCountryCode(studentPersonalDataAddressDBO.getMotherMobileNoCountryCode());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getMotherMobileNo())) {
					StudentPersonalDataAddressDTO.setMotherMobileNo(studentPersonalDataAddressDBO.getMotherMobileNo());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianErpSalutationDBO())) {
					StudentPersonalDataAddressDTO.setGuardianSalutation(new SelectDTO());
					StudentPersonalDataAddressDTO.getGuardianSalutation().setValue(String.valueOf(studentPersonalDataAddressDBO.getGuardianErpSalutationDBO().getId()));
					StudentPersonalDataAddressDTO.getGuardianSalutation().setLabel(studentPersonalDataAddressDBO.getGuardianErpSalutationDBO().getErpSalutationName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianName())) {
					StudentPersonalDataAddressDTO.setGuardianName(studentPersonalDataAddressDBO.getGuardianName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianEmail())) {
					StudentPersonalDataAddressDTO.setGuardianEmail(studentPersonalDataAddressDBO.getGuardianEmail());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianMobileNoCountryCode())) {
					StudentPersonalDataAddressDTO.setGuardianMobileNoCountryCode(studentPersonalDataAddressDBO.getGuardianMobileNoCountryCode());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianMobileNo())) {
					StudentPersonalDataAddressDTO.setGuardianMobileNo(studentPersonalDataAddressDBO.getGuardianMobileNo());
				}
				if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianAddressLine1())) {
					StudentPersonalDataAddressDTO.setGuardianAddressLine1(studentPersonalDataAddressDBO.getGuardianAddressLine1());
				}
				if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianAddressLine2())) {
					StudentPersonalDataAddressDTO.setGuardianAddressLine2(studentPersonalDataAddressDBO.getGuardianAddressLine2());
				}
				if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianCountry())) {
					StudentPersonalDataAddressDTO.setGuardianCountry(new SelectDTO());
					StudentPersonalDataAddressDTO.getGuardianCountry().setValue(String.valueOf(studentPersonalDataAddressDBO.getGuardianCountry().getId()));
					StudentPersonalDataAddressDTO.getGuardianCountry().setLabel(studentPersonalDataAddressDBO.getGuardianCountry().getCountryName());
				}
				if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianState())) {
					StudentPersonalDataAddressDTO.setGuardianStateOthers(null);
					StudentPersonalDataAddressDTO.setGuardianState(new SelectDTO());
					StudentPersonalDataAddressDTO.getGuardianState().setValue(String.valueOf(studentPersonalDataAddressDBO.getGuardianState().getId()));
					StudentPersonalDataAddressDTO.getGuardianState().setLabel(studentPersonalDataAddressDBO.getGuardianState().getStateName());
				} else {
					StudentPersonalDataAddressDTO.setGuardianState(null);
					StudentPersonalDataAddressDTO.setGuardianStateOthers(studentPersonalDataAddressDBO.getGuardianStateOthers());
				}
				if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianCity())) {
					StudentPersonalDataAddressDTO.setGuardianCity(new SelectDTO());
					StudentPersonalDataAddressDTO.getGuardianCity().setValue(String.valueOf(studentPersonalDataAddressDBO.getGuardianCity().getId()));
					StudentPersonalDataAddressDTO.getGuardianCity().setLabel(studentPersonalDataAddressDBO.getGuardianCity().getCityName());
					StudentPersonalDataAddressDTO.setGuardianCityOthers(null);
				} else {
					StudentPersonalDataAddressDTO.setGuardianCityOthers(studentPersonalDataAddressDBO.getGuardianCityOthers());
					StudentPersonalDataAddressDTO.setGuardianCity(null);								}
				if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getGuardianPincode())) {
					StudentPersonalDataAddressDTO.setGuardianPincode(studentPersonalDataAddressDBO.getGuardianPincode());
				}
				if (!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getBirthPincode())) {
					StudentPersonalDataAddressDTO.setBirthPincode(studentPersonalDataAddressDBO.getBirthPincode());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getBirthCountry())) {
					StudentPersonalDataAddressDTO.setBirthCountry(new SelectDTO());
					StudentPersonalDataAddressDTO.getBirthCountry().setValue(String.valueOf(studentPersonalDataAddressDBO.getBirthCountry().getId()));
					StudentPersonalDataAddressDTO.getBirthCountry().setLabel(studentPersonalDataAddressDBO.getBirthCountry().getCountryName());
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getBirthState())) {
					StudentPersonalDataAddressDTO.setBirthState(new SelectDTO());
					StudentPersonalDataAddressDTO.getBirthState().setValue(String.valueOf(studentPersonalDataAddressDBO.getBirthState().getId()));
					StudentPersonalDataAddressDTO.getBirthState().setLabel(studentPersonalDataAddressDBO.getBirthState().getStateName());
					StudentPersonalDataAddressDTO.setBirthStateOthers(null);
				} else {
					StudentPersonalDataAddressDTO.setBirthStateOthers(studentPersonalDataAddressDBO.getBirthStateOthers());
					StudentPersonalDataAddressDTO.setBirthState(null);
				}
				if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.getBirthCity())) {
					StudentPersonalDataAddressDTO.setBirthCity(new SelectDTO());
					StudentPersonalDataAddressDTO.getBirthCity().setValue(String.valueOf(studentPersonalDataAddressDBO.getBirthCity().getId()));
					StudentPersonalDataAddressDTO.getBirthCity().setLabel(studentPersonalDataAddressDBO.getBirthCity().getCityName());
				} else {
					StudentPersonalDataAddressDTO.setBirthCityOthers(studentPersonalDataAddressDBO.getBirthCityOthers());
				}
				dto.setStudentAddressAndParentdetails(StudentPersonalDataAddressDTO);
			}

			//Educational Details
			Set<StudentEducationalDetailsDBO> StudentEducationalDetailsDBOSet = dbo.getStudentEducationalDetailsDBOS();
			List<StudentEducationalDetailsDTO> studentEducationalDetailsDTOSList = new ArrayList<StudentEducationalDetailsDTO>();
			StudentEducationalDetailsDBOSet.forEach(StudentEducationalDetails -> {
				if(StudentEducationalDetails.getRecordStatus() == 'A') {
					StudentEducationalDetailsDTO studentEducationalDetailsDTO = new StudentEducationalDetailsDTO();
					studentEducationalDetailsDTO.setId(StudentEducationalDetails.getId());
					studentEducationalDetailsDTO.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
					studentEducationalDetailsDTO.getStudentApplnEntriesDTO().setId(StudentEducationalDetails.getStudentApplnEntriesDBO().getId());
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getAdmQualificationListDBO())) {
						studentEducationalDetailsDTO.setAdmQualificationListDTO(new AdmQualificationListDTO());
						studentEducationalDetailsDTO.getAdmQualificationListDTO().setId(StudentEducationalDetails.getAdmQualificationListDBO().getId());
						studentEducationalDetailsDTO.getAdmQualificationListDTO().setQualificationName(StudentEducationalDetails.getAdmQualificationListDBO().getQualificationName());
						studentEducationalDetailsDTO.getAdmQualificationListDTO().setQualificationOrder(StudentEducationalDetails.getAdmQualificationListDBO().getQualificationOrder());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getAdmQualificationDegreeListDBO())) {
						studentEducationalDetailsDTO.setAdmQualificationDegreeListDTO(new AdmQualificationDegreeListDTO());
						studentEducationalDetailsDTO.getAdmQualificationDegreeListDTO().setId(StudentEducationalDetails.getAdmQualificationDegreeListDBO().getId());
						studentEducationalDetailsDTO.getAdmQualificationDegreeListDTO().setDegreeName(StudentEducationalDetails.getAdmQualificationDegreeListDBO().getDegreeName());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getErpUniversityBoardDBO())) {
						studentEducationalDetailsDTO.setUniversityBoard(new SelectDTO());
						studentEducationalDetailsDTO.getUniversityBoard().setValue(String.valueOf(StudentEducationalDetails.getErpUniversityBoardDBO().getId()));
						studentEducationalDetailsDTO.getUniversityBoard().setLabel(StudentEducationalDetails.getErpUniversityBoardDBO().getUniversityBoardName());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getErpInstitutionDBO())) {
						studentEducationalDetailsDTO.setErpInstitutionDTO(new ErpInstitutionDTO());
						studentEducationalDetailsDTO.getErpInstitutionDTO().setId(StudentEducationalDetails.getErpInstitutionDBO().getId());
						studentEducationalDetailsDTO.getErpInstitutionDTO().setInstitutionName(StudentEducationalDetails.getErpInstitutionDBO().getInstitutionName());
					} else {
						studentEducationalDetailsDTO.setInstitutionOthers(StudentEducationalDetails.getInstitutionOthers());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getInstitutionCountry())) {
						studentEducationalDetailsDTO.setCountry(new SelectDTO());
						studentEducationalDetailsDTO.getCountry().setValue(String.valueOf(StudentEducationalDetails.getInstitutionCountry().getId()));
						studentEducationalDetailsDTO.getCountry().setLabel(StudentEducationalDetails.getInstitutionCountry().getCountryName());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getInstitutionState())) {
						studentEducationalDetailsDTO.setState(new SelectDTO());
						studentEducationalDetailsDTO.getState().setValue(String.valueOf(StudentEducationalDetails.getInstitutionState().getId()));
						studentEducationalDetailsDTO.getState().setLabel(StudentEducationalDetails.getInstitutionState().getStateName());
						studentEducationalDetailsDTO.setInstitutionOthersState(null);
					} else {
						studentEducationalDetailsDTO.setInstitutionOthersState(StudentEducationalDetails.getInstitutionOthersState());
						studentEducationalDetailsDTO.setState(null);
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getNoOfPendingBacklogs())) {
						studentEducationalDetailsDTO.setNoOfPendingBacklogs(StudentEducationalDetails.getNoOfPendingBacklogs());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getYearOfPassing())) {
						studentEducationalDetailsDTO.setYearOfPassing(StudentEducationalDetails.getYearOfPassing());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getMonthOfPassing())) {
						studentEducationalDetailsDTO.setMonthOfPassing(StudentEducationalDetails.getMonthOfPassing());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getExamRegisterNo())) {
						studentEducationalDetailsDTO.setExamRegisterNo(StudentEducationalDetails.getExamRegisterNo());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getIsLevelCompleted())) {
						studentEducationalDetailsDTO.setIsLevelCompleted(StudentEducationalDetails.getIsLevelCompleted());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getAcaSessionTypeId())) {
						studentEducationalDetailsDTO.setErpSessionType(new AcaSessionTypeDTO());
						studentEducationalDetailsDTO.getErpSessionType().setId(StudentEducationalDetails.getAcaSessionTypeId().getId());
						studentEducationalDetailsDTO.getErpSessionType().setSessionTypeName(StudentEducationalDetails.getAcaSessionTypeId().getSessionTypeName());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getTotalSemesters())) {
						studentEducationalDetailsDTO.setTotalSemesters(StudentEducationalDetails.getTotalSemesters());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getConsolidatedMarksObtained()) && !Utils.isNullOrEmpty(StudentEducationalDetails.getConsolidatedMaximumMarks())){
						studentEducationalDetailsDTO.setConsolidatedMarksObtained(StudentEducationalDetails.getConsolidatedMarksObtained());
						studentEducationalDetailsDTO.setConsolidatedMaximumMarks(StudentEducationalDetails.getConsolidatedMaximumMarks());
					}
					if(!Utils.isNullOrEmpty(StudentEducationalDetails.getIsResultDeclared())) {
						studentEducationalDetailsDTO.setIsResultDeclared(StudentEducationalDetails.getIsResultDeclared() ? true : false);
					}

					//marks Details
					Set<StudentEducationalMarkDetailsDBO> markDetailsSet = !Utils.isNullOrEmpty(StudentEducationalDetails.getStudentEducationalMarkDetailsDBOSet())?StudentEducationalDetails.getStudentEducationalMarkDetailsDBOSet():null;
					List<StudentEducationalMarkDetailsDTO> studentEducationalMarkDetailsDTOS = new ArrayList<StudentEducationalMarkDetailsDTO>();
					if(!Utils.isNullOrEmpty(markDetailsSet)) {
						markDetailsSet.forEach(markDetails -> {
							if(markDetails.getRecordStatus() == 'A') {
								StudentEducationalMarkDetailsDTO studentEducationalMarkDetailsDTO = new StudentEducationalMarkDetailsDTO();
								studentEducationalMarkDetailsDTO.setId(markDetails.getId());
								if(!Utils.isNullOrEmpty(markDetails.getAdmProgrammeQualificationSubjectEligibilityDBO())) {
									studentEducationalMarkDetailsDTO.setAdmProgrammeQualificationSubjectEligibilityDTO(new AdmProgrammeQualificationSubjectEligibilityDTO());
									studentEducationalMarkDetailsDTO.getAdmProgrammeQualificationSubjectEligibilityDTO().setId(String.valueOf(markDetails.getAdmProgrammeQualificationSubjectEligibilityDBO().getId()));
									studentEducationalMarkDetailsDTO.getAdmProgrammeQualificationSubjectEligibilityDTO().setSubjectname(markDetails.getAdmProgrammeQualificationSubjectEligibilityDBO().getSubjectName());
									studentEducationalMarkDetailsDTO.getAdmProgrammeQualificationSubjectEligibilityDTO().setEligibilitypercentage(markDetails.getAdmProgrammeQualificationSubjectEligibilityDBO().getEligibilityPercentage().toString());
								} else {
									studentEducationalMarkDetailsDTO.setSemesterName(markDetails.getSemesterName());
								}
								if(!Utils.isNullOrEmpty(markDetails.getDisplayOrder())) {
									studentEducationalMarkDetailsDTO.setDisplayOrder(markDetails.getDisplayOrder());
								}
								if(!Utils.isNullOrEmpty(markDetails.getMarksObtained())) {
//									studentEducationalMarkDetailsDTO.setMarksObtained(markDetails.getMarksObtained());
								}
								if(!Utils.isNullOrEmpty(markDetails.getMaximumMarks())) {
//									studentEducationalMarkDetailsDTO.setMaximumMarks(markDetails.getMaximumMarks());
								}
								if(!Utils.isNullOrEmpty(markDetails.getCgpa())) {
									studentEducationalMarkDetailsDTO.setCgpa(markDetails.getCgpa());
								}
								if(!Utils.isNullOrEmpty(markDetails.getSgpa())) {
									studentEducationalMarkDetailsDTO.setSgpa(markDetails.getSgpa());
								}
								if(!Utils.isNullOrEmpty(markDetails.getTotalPendingBacklogs())) {
									studentEducationalMarkDetailsDTO.setTotalPendingBacklogs(markDetails.getTotalPendingBacklogs());
								}
								if(!Utils.isNullOrEmpty(markDetails.getIsResultDeclared())) {
									studentEducationalMarkDetailsDTO.setIsResultDeclared(markDetails.getIsResultDeclared()? true : false);
								} 
								studentEducationalMarkDetailsDTOS.add(studentEducationalMarkDetailsDTO);
							}
						});
					}
					studentEducationalMarkDetailsDTOS.sort(Comparator.comparing(StudentEducationalMarkDetailsDTO::getDisplayOrder));
					studentEducationalDetailsDTO.setStudentEducationalMarkDetailS(studentEducationalMarkDetailsDTOS);

					//marksDetailsDocumentsUpload
					Set<StudentEducationalDetailsDocumentsDBO> documentsDetailsSet = !Utils.isNullOrEmpty(StudentEducationalDetails.getStudentEducationalDetailsDocumentsDBOSet())?StudentEducationalDetails.getStudentEducationalDetailsDocumentsDBOSet():null;
					List<StudentEducationalDetailsDocumentsDTO> studentEducationalDetailsDocumentsDTOs = new ArrayList<StudentEducationalDetailsDocumentsDTO>();
					if(!Utils.isNullOrEmpty(documentsDetailsSet)) {
						documentsDetailsSet.forEach(doucument -> {
							if(doucument.getRecordStatus() == 'A') {
								StudentEducationalDetailsDocumentsDTO studentEducationalDetailsDocumentsDTO = new StudentEducationalDetailsDocumentsDTO();
								studentEducationalDetailsDocumentsDTO.setId(doucument.getId());
								File file = new File(doucument.getDocumentsUrl());
								if(file.exists() && !file.isDirectory()) { 
									studentEducationalDetailsDocumentsDTO.setExtension(doucument.getDocumentsUrl().substring(doucument.getDocumentsUrl().lastIndexOf(".")+1));
									String fileName = file.getName();
									studentEducationalDetailsDocumentsDTO.setDocumentsUrl(doucument.getDocumentsUrl());
									studentEducationalDetailsDocumentsDTO.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
									studentEducationalDetailsDocumentsDTO.setRecordStatus(doucument.getRecordStatus());
									studentEducationalDetailsDocumentsDTOs.add(studentEducationalDetailsDocumentsDTO);
								}
							}
						});
					}
					studentEducationalDetailsDTO.setStudentEducationalDetailsDocuments(studentEducationalDetailsDocumentsDTOs);
					studentEducationalDetailsDTOSList.add(studentEducationalDetailsDTO);
				}
			});
			studentEducationalDetailsDTOSList.sort(Comparator.comparing(s -> s.getAdmQualificationListDTO().getQualificationOrder()));
			dto.setStudentEducationalDetailsDTOList(studentEducationalDetailsDTOSList);

			//studentWorkExperience
			Set<StudentWorkExperienceDBO> StudentWorkExperienceDBOSet = !Utils.isNullOrEmpty(dbo.getStudentWorkExperienceDBOS()) ? dbo.getStudentWorkExperienceDBOS() : null;
			List<StudentWorkExperienceDTO> studentWorkExperienceDTOSList = new ArrayList<StudentWorkExperienceDTO>();
			if(!Utils.isNullOrEmpty(StudentWorkExperienceDBOSet)) {
				StudentWorkExperienceDBOSet.forEach(workExperience -> {
					if(workExperience.getRecordStatus() == 'A') {
						StudentWorkExperienceDTO studentWorkExperienceDTO = new StudentWorkExperienceDTO();
						studentWorkExperienceDTO.setStudentWorkExperienceId(workExperience.getId());
						studentWorkExperienceDTO.setStudentApplnEntries(new StudentApplnEntriesDTO());
						studentWorkExperienceDTO.getStudentApplnEntries().setId(workExperience.getStudentApplnEntriesDBO().getId());
						if(!Utils.isNullOrEmpty(workExperience.getOrganizationName())) {
							studentWorkExperienceDTO.setOrganizationName(workExperience.getOrganizationName());
						}
						if(!Utils.isNullOrEmpty(workExperience.getOrganizationAddress())) {
							studentWorkExperienceDTO.setOrganizationAddress(workExperience.getOrganizationAddress());
						}
						if(!Utils.isNullOrEmpty(workExperience.getDesignation())) {
							studentWorkExperienceDTO.setDesignation(workExperience.getDesignation());
						}
						if(!Utils.isNullOrEmpty(workExperience.getWorkExperienceFromDate())) {
							studentWorkExperienceDTO.setWorkExperienceFromDate(workExperience.getWorkExperienceFromDate());
						}
						if(!Utils.isNullOrEmpty(workExperience.getWorkExperienceToDate())) {
							studentWorkExperienceDTO.setWorkExperienceToDate(workExperience.getWorkExperienceToDate());
						}

						//workExperienceDocumentsUpload
						Set<StudentWorkExperienceDocumentDBO> workDocumentsDetailsSet = !Utils.isNullOrEmpty(workExperience.getStudentWorkExperienceDocumentDBOSet())?workExperience.getStudentWorkExperienceDocumentDBOSet():null;
						List<StudentWorkExperienceDocumentDTO> studentWorkExperienceDocumentDTOs = new ArrayList<StudentWorkExperienceDocumentDTO>();
						if(!Utils.isNullOrEmpty(workDocumentsDetailsSet)) {
							workDocumentsDetailsSet.forEach(doucument -> {
								if(doucument.getRecordStatus() == 'A') {
									StudentWorkExperienceDocumentDTO studentWorkExperienceDocumentDTO = new StudentWorkExperienceDocumentDTO();
									studentWorkExperienceDocumentDTO.setId(doucument.getId());
									File file = new File(doucument.getExperienceDocumentsUrl());
									if(file.exists() && !file.isDirectory()) { 
										studentWorkExperienceDocumentDTO.setExtension(doucument.getExperienceDocumentsUrl().substring(doucument.getExperienceDocumentsUrl().lastIndexOf(".")+1));
										String fileName = file.getName();
										studentWorkExperienceDocumentDTO.setDocumentsUrl(doucument.getExperienceDocumentsUrl());
										studentWorkExperienceDocumentDTO.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
										studentWorkExperienceDocumentDTO.setRecordStatus(doucument.getRecordStatus());
										studentWorkExperienceDocumentDTOs.add(studentWorkExperienceDocumentDTO);
									}
								}
							});
						}
						studentWorkExperienceDTO.setStudentWorkExperienceDocumentDTOList(studentWorkExperienceDocumentDTOs);
						studentWorkExperienceDTOSList.add(studentWorkExperienceDTO);
					}
				});
			}
			dto.setStudentWorkExperienceDTOList(studentWorkExperienceDTOSList);

			//StudentApplnSelectionProcessDates
			Set<StudentApplnSelectionProcessDatesDBO> studentApplnSelectionProcessDatesDBOSet = !Utils.isNullOrEmpty(dbo.getStudentApplnSelectionProcessDatesDBOS()) ? dbo.getStudentApplnSelectionProcessDatesDBOS() : null;
			List<StudentApplnSelectionProcessDatesDTO> studentApplnSelectionProcessDatesDTOSList = new ArrayList<StudentApplnSelectionProcessDatesDTO>();
			studentApplnSelectionProcessDatesDBOSet.forEach( selectionProcessDates -> {
				if(selectionProcessDates.getRecordStatus() == 'A') {
					StudentApplnSelectionProcessDatesDTO studentApplnSelectionProcessDatesDTO = new StudentApplnSelectionProcessDatesDTO();
					studentApplnSelectionProcessDatesDTO.setStudentApplnSelectionProcessDatesId(selectionProcessDates.getId());
					if(!Utils.isNullOrEmpty(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO())) {
						studentApplnSelectionProcessDatesDTO.setAdmSelectionProcessPlanDetail(new AdmSelectionProcessPlanDetailDTO());
						studentApplnSelectionProcessDatesDTO.getAdmSelectionProcessPlanDetail().setId(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getId());
						if(!Utils.isNullOrEmpty(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO())) {
							studentApplnSelectionProcessDatesDTO.setAdmSelectionProcessVenueCity(new AdmSelectionProcessVenueCityDTO());
							studentApplnSelectionProcessDatesDTO.getAdmSelectionProcessVenueCity().setAdmSelectionProcessVenueCityId(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getId());
							studentApplnSelectionProcessDatesDTO.getAdmSelectionProcessVenueCity().setVenue(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
							studentApplnSelectionProcessDatesDTO.setState(new SelectDTO());
							studentApplnSelectionProcessDatesDTO.getState().setValue(String.valueOf(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getErpStateDBO().getId()));
							studentApplnSelectionProcessDatesDTO.getState().setLabel(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getErpStateDBO().getStateName());
						} else {
							if(!Utils.isNullOrEmpty(selectionProcessDates.getAdmSelectionProcessPlanCenterBasedDBO())) {
								studentApplnSelectionProcessDatesDTO.setAdmSelectionProcessPlanCenterBased(new AdmSelectionProcessPlanCenterBasedDTO());
								studentApplnSelectionProcessDatesDTO.getAdmSelectionProcessPlanCenterBased().setAdmSelectionProcessPlanCenterBased(selectionProcessDates.getAdmSelectionProcessPlanCenterBasedDBO().getId());
								studentApplnSelectionProcessDatesDTO.getAdmSelectionProcessPlanCenterBased().setAdmSelectionProcessVenueCity(new AdmSelectionProcessVenueCityDTO());
								studentApplnSelectionProcessDatesDTO.getAdmSelectionProcessPlanCenterBased().getAdmSelectionProcessVenueCity().setAdmSelectionProcessVenueCityId(selectionProcessDates.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getId());
								studentApplnSelectionProcessDatesDTO.getAdmSelectionProcessPlanCenterBased().getAdmSelectionProcessVenueCity().setVenue(selectionProcessDates.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
								studentApplnSelectionProcessDatesDTO.setState(new SelectDTO());
								studentApplnSelectionProcessDatesDTO.getState().setValue(String.valueOf(selectionProcessDates.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getErpStateDBO().getId()));
								studentApplnSelectionProcessDatesDTO.getState().setLabel(selectionProcessDates.getAdmSelectionProcessPlanCenterBasedDBO().getAdmSelectionProcessVenueCityDBO().getErpStateDBO().getStateName());
							}
						}
						if(!Utils.isNullOrEmpty(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO()) && selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().isConductedInIndia) {
							if(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().isConductedInIndia) {
								studentApplnSelectionProcessDatesDTO.setConductedInIndia(true);
							} else {
								studentApplnSelectionProcessDatesDTO.setConductedOutsideIndia(true);
							}
						}
						if(!Utils.isNullOrEmpty(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getProcessOrder())) {
							studentApplnSelectionProcessDatesDTO.setProcessOrder(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getProcessOrder());
						}
					}
					if(!Utils.isNullOrEmpty(selectionProcessDates.getStudentApplnEntriesDBO())) {
						studentApplnSelectionProcessDatesDTO.setStudentApplnEntries(new StudentApplnEntriesDTO());
						studentApplnSelectionProcessDatesDTO.getStudentApplnEntries().setId(selectionProcessDates.getStudentApplnEntriesDBO().getId());
					}
					if(!Utils.isNullOrEmpty(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate())) {
						studentApplnSelectionProcessDatesDTO.setSelectionProcessDate(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate().atTime(selectionProcessDates.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessTime()));
					}
					studentApplnSelectionProcessDatesDTOSList.add(studentApplnSelectionProcessDatesDTO);
				}
			});
			dto.setStudentApplnSelectionProcessDatesDTOList(studentApplnSelectionProcessDatesDTOSList);
		}
		return Utils.isNullOrEmpty(dto)? Mono.error(new NotFoundException(null)) :Mono.just(dto);
	}

	public Mono<ApiResult> saveOrUpdate(Mono<StudentApplicationEditDTO> data, String userId, String tabName) {
		return data.handle((studentApplicationEditDTO, synchronousSink) -> {
			synchronousSink.next(studentApplicationEditDTO);
		}).cast(StudentApplicationEditDTO.class)
				.map(data1 -> convertDtoToDbo(data1,tabName, userId))
				.flatMap( s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						studentApplicationEditTransaction.update(s);
					} 
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public StudentApplnEntriesDBO convertDtoToDbo(StudentApplicationEditDTO dto, String userId, String tabName) {
		StudentApplnEntriesDBO dbo = studentApplicationEditTransaction.getStudentDetails(dto.getStudentApplnEntriesId());
		if(!Utils.isNullOrEmpty(dbo)) {

			//Personal Data
			if(tabName.equalsIgnoreCase("PersonalDetails")) {
				StudentPersonalDataDBO studentPersonalDataDBO = dbo.getStudentPersonalDataDBO();
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
				
				//ExtraCurricularDetails
				Set<StudentExtraCurricularDetailsDBO> studentExtraCurricularDetailsDBOsUpdate = new HashSet<StudentExtraCurricularDetailsDBO>();
				Map<Integer,StudentExtraCurricularDetailsDBO> studentCurricularExistDataMap = 
						!Utils.isNullOrEmpty(dbo.getStudentExtraCurricularDetailsDBOS()) ?dbo.getStudentExtraCurricularDetailsDBOS().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) : null;
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
					if(!Utils.isNullOrEmpty(studentSportsDTO.getErpSportsLevel())) {
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
				dbo.setStudentExtraCurricularDetailsDBOS(studentExtraCurricularDetailsDBOsUpdate);
			}

			//studentAddressAndParentdetails
			StudentPersonalDataAddressDBO studentPersonalDataAddressDBO = dbo.getStudentPersonalDataDBO().getStudentPersonalDataAddressDBO();
			if(tabName.equalsIgnoreCase("AddressDetails")) {
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
			}
			
			//Parent Details
			if(tabName.equalsIgnoreCase("ParentDetails")) {
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
			}

			//Educational Details
			if(tabName.equalsIgnoreCase("EducationalDetails")) {
				Set<StudentEducationalDetailsDBO> studentEducationalDetailsDBOsUpdate = new HashSet<StudentEducationalDetailsDBO>();
				Map<Integer,StudentEducationalDetailsDBO> studentEducationalDetailsDBOMap = !Utils.isNullOrEmpty(dbo.getStudentEducationalDetailsDBOS()) ?
						dbo.getStudentEducationalDetailsDBOS().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)):null;
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
						studentEducationalDetailsDBO.setInstitutionOthersState(null);
					} else {
						studentEducationalDetailsDBO.setInstitutionOthersState(studentEducation.getInstitutionOthersState());
						studentEducationalDetailsDBO.setInstitutionState(null);
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
						//if(!Utils.isNullOrEmpty(marksDetials.getMarksObtained())) {
//							studentEducationalMarkDetailsDBO.setMarksObtained(marksDetials.getMarksObtained());
						//}
						//if(!Utils.isNullOrEmpty(marksDetials.getMaximumMarks())) {
//							studentEducationalMarkDetailsDBO.setMaximumMarks(marksDetials.getMaximumMarks());
						//}
						//if(!Utils.isNullOrEmpty(marksDetials.getCgpa())) {
							studentEducationalMarkDetailsDBO.setCgpa(marksDetials.getCgpa());
						//}
						//if(!Utils.isNullOrEmpty(marksDetials.getSgpa())) {
							studentEducationalMarkDetailsDBO.setSgpa(marksDetials.getSgpa());
						//}
						//if(!Utils.isNullOrEmpty(marksDetials.getTotalPendingBacklogs())) {
							studentEducationalMarkDetailsDBO.setTotalPendingBacklogs(marksDetials.getTotalPendingBacklogs());
						//}
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
							//File renameFile =new File("StudentEducationalDocumentFiles//"+studentEducation.getAdmQualificationDegreeListDTO().getDegreeName()+"_"+dto.getApplicationNo()+"."+document.getExtension());
							//file.renameTo(renameFile);
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
					}
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
				dbo.setStudentEducationalDetailsDBOS(studentEducationalDetailsDBOsUpdate);
			}

			//studentWorkExperience
			if(tabName.equalsIgnoreCase("WorkExperienceDetails")) {
				if(!Utils.isNullOrEmpty(dto.getIsHavingWorkExperience())) {
					dbo.setIsHavingWorkExperience(dbo.getIsHavingWorkExperience());
				} 
				Set<StudentWorkExperienceDBO> studentWorkExperienceDBOUpdates = new HashSet<StudentWorkExperienceDBO>();
				Map<Integer,StudentWorkExperienceDBO> studentWorkExperienceDBOMap = !Utils.isNullOrEmpty(dbo.getStudentWorkExperienceDBOS())?
						dbo.getStudentWorkExperienceDBOS().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)):null;
				dto.getStudentWorkExperienceDTOList().forEach(studentWorkExperience -> {
					StudentWorkExperienceDBO studentWorkExperienceDBO =null;
					if(!Utils.isNullOrEmpty(studentWorkExperience.getStudentWorkExperienceId()) && studentWorkExperienceDBOMap.containsKey(studentWorkExperience.getStudentWorkExperienceId())) {
						studentWorkExperienceDBO = studentWorkExperienceDBOMap.get(studentWorkExperience.getStudentWorkExperienceId());
						studentWorkExperienceDBO.setModifiedUsersId(Integer.parseInt(userId));
						studentWorkExperienceDBOMap.remove(studentWorkExperience.getStudentWorkExperienceId());
					} else {
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

					//WorkExperienceDocumentUpload
					Set<StudentWorkExperienceDocumentDBO> StudentWorkExperienceDocumentDBOSet = new HashSet<StudentWorkExperienceDocumentDBO>();
					Map<Integer, StudentWorkExperienceDocumentDBO> studentWorkExperienceDocumentDBOMap = !Utils.isNullOrEmpty(studentWorkExperienceDBO.getStudentWorkExperienceDocumentDBOSet()) ?
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
				dbo.setStudentWorkExperienceDBOS(studentWorkExperienceDBOUpdates);
			}
		}
		return dbo;
	}

}
