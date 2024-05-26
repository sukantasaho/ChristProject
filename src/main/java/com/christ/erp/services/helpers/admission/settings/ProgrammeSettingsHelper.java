package com.christ.erp.services.helpers.admission.settings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.*;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeDocumentSettingsDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeFeePaymentModeDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammePreferenceSettingsDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeQualificationSettingsDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeQualificationSubjectEligibilityDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.admission.settings.ProgrammeSettingsTransaction;
import org.springframework.stereotype.Service;

@Service
public class ProgrammeSettingsHelper {
	
	public static volatile ProgrammeSettingsHelper programmeSettingsHelper = null;
	public static ProgrammeSettingsHelper getInstance() {
		if(programmeSettingsHelper==null) {
			programmeSettingsHelper = new ProgrammeSettingsHelper();
		}
		return programmeSettingsHelper;
	}
	ProgrammeSettingsTransaction transaction = ProgrammeSettingsTransaction.getInstance();
	
	public AdmProgrammeDocumentSettingsDBO getAdmProgrammeDocumentSettingsDBO(AdmProgrammeDocumentSettingsDTO documentSettingsDTO,
			AdmProgrammeDocumentSettingsDBO documentSettingsDBO, AdmProgrammeSettingsDBO dbo, String userId) {
		if(!Utils.isNullOrEmpty(documentSettingsDBO)) {
			if(!Utils.isNullOrEmpty(dbo)) {
				documentSettingsDBO.admProgrammeSettingsDBO = dbo;
			}
			if(Utils.isNullOrEmpty(documentSettingsDTO.id)) {
				documentSettingsDBO.createdUsersId = Integer.parseInt(userId);
			}else {
				documentSettingsDBO.modifiedUsersId = Integer.parseInt(userId);
			}
			if(!Utils.isNullOrEmpty(documentSettingsDTO.displayorder) && !Utils.isNullOrEmpty(documentSettingsDTO.displayorder.trim())) {
				documentSettingsDBO.documentOrder = Integer.parseInt(documentSettingsDTO.displayorder.trim());
			}else {
				documentSettingsDBO.documentOrder = null;
			}
			if(!Utils.isNullOrEmpty(documentSettingsDTO.aditionaldocument) && !Utils.isNullOrEmpty(documentSettingsDTO.aditionaldocument.id) 
					&& !Utils.isNullOrEmpty(documentSettingsDTO.aditionaldocument.id.trim())) {
				AdmQualificationListDBO admQualificationListDBO = new AdmQualificationListDBO();
				admQualificationListDBO.id = Integer.parseInt(documentSettingsDTO.aditionaldocument.id.trim());
				documentSettingsDBO.admQualificationListDBO = admQualificationListDBO;
			}
			else {
				documentSettingsDBO.admQualificationListDBO = null;
			}
//			if(!Utils.isNullOrEmpty(documentSettingsDTO.tobesubmittedmandatory) && !Utils.isNullOrEmpty(documentSettingsDTO.tobesubmittedmandatory.trim())) {
//				documentSettingsDBO.isToBeSubmitted = Boolean.parseBoolean(documentSettingsDTO.tobesubmittedmandatory.trim());
//			}
			if(!Utils.isNullOrEmpty(documentSettingsDTO.onlineuploadrequired)) {
				documentSettingsDBO.isOnlineUploadRequired = documentSettingsDTO.onlineuploadrequired;
			}
			if(!Utils.isNullOrEmpty(documentSettingsDTO.uploadmandatory)) {
				documentSettingsDBO.isUploadMandatory = documentSettingsDTO.uploadmandatory;
			}
//			if(!Utils.isNullOrEmpty(documentSettingsDTO.additionalforforeignnationalrequired) && !Utils.isNullOrEmpty(documentSettingsDTO.additionalforforeignnationalrequired.trim())) {
//				documentSettingsDBO.isAdditionalForForeignNational = Boolean.parseBoolean(documentSettingsDTO.additionalforforeignnationalrequired.trim());
//			}
			documentSettingsDBO.recordStatus = 'A';
		}
		return documentSettingsDBO;
	}
	public AdmProgrammeQualificationSettingsDBO getAdmProgrammQualificationSettingsDBO(AdmProgrammeQualificationSettingsDTO qualificationSettingsDTO,AdmProgrammeQualificationSettingsDBO qualificationSettingsDBO, AdmProgrammeSettingsDBO dbo, String userId) throws Exception {
		if(!Utils.isNullOrEmpty(qualificationSettingsDBO)) {
			if(!Utils.isNullOrEmpty(dbo)) {
				qualificationSettingsDBO.admProgrammeSettingsDBO = dbo;
			}
			if(Utils.isNullOrEmpty(qualificationSettingsDTO.id)) {
				qualificationSettingsDBO.createdUsersId = Integer.parseInt(userId);
			}else {
				qualificationSettingsDBO.modifiedUsersId = Integer.parseInt(userId);
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.displayorder) && !Utils.isNullOrEmpty(qualificationSettingsDTO.displayorder.trim())) {
				qualificationSettingsDBO.qualificationOrder = Integer.parseInt(qualificationSettingsDTO.displayorder.trim());
			}else {
				qualificationSettingsDBO.qualificationOrder = null;
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.qualificationlevel) && !Utils.isNullOrEmpty(qualificationSettingsDTO.qualificationlevel.id) 
					&& !Utils.isNullOrEmpty(qualificationSettingsDTO.qualificationlevel.id.trim())) {
				AdmQualificationListDBO admQualificationListDBO = new AdmQualificationListDBO();
				admQualificationListDBO.id = Integer.parseInt(qualificationSettingsDTO.qualificationlevel.id.trim());
				qualificationSettingsDBO.admQualificationListDBO = admQualificationListDBO;
			}else {
				qualificationSettingsDBO.admQualificationListDBO = null;
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.qualificationlevelmandatory) ) {
				qualificationSettingsDBO.isQualificationLevelMandatory = qualificationSettingsDTO.qualificationlevelmandatory;
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.uploadrequired) ) {
				qualificationSettingsDBO.isUploadRequired = qualificationSettingsDTO.uploadrequired;
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.examnamerequired)) {
				qualificationSettingsDBO.isExamnameRequired = qualificationSettingsDTO.examnamerequired;
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.getExamRegisterNumberRequired())){
				qualificationSettingsDBO.isExamRegiNumbRequired = qualificationSettingsDTO.getExamRegisterNumberRequired();
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.uploadmandatory)) {
				qualificationSettingsDBO.isUploadMandatory = qualificationSettingsDTO.uploadmandatory;
			}

			//removed board
//			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.getUniversityBoard())) {
//				qualificationSettingsDBO.universityOrBoard = qualificationSettingsDTO.getUniversityBoard().getValue();
//			}else {
//				qualificationSettingsDBO.universityOrBoard = null;
//			}

			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.marksentrytype) && !Utils.isNullOrEmpty(qualificationSettingsDTO.marksentrytype.text) 
					&& !Utils.isNullOrEmpty(qualificationSettingsDTO.marksentrytype.text.trim())) {
				qualificationSettingsDBO.marksEntryType = qualificationSettingsDTO.marksentrytype.text.trim();
			}else {
				qualificationSettingsDBO.marksEntryType = null;
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.subjecteligibilityrequired) ) {
				qualificationSettingsDBO.isSubjectEligibilityRequired = qualificationSettingsDTO.subjecteligibilityrequired;
			}
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.minsubjectforeligibility)) {
				qualificationSettingsDBO.minSubjectsForEligibility = qualificationSettingsDTO.minsubjectforeligibility;
			}
			else {
				qualificationSettingsDBO.minSubjectsForEligibility = null;
			}
			
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.aggregatesubjectspercentage)) {
				qualificationSettingsDBO.aggregateSubjectsPercentage = new BigDecimal(qualificationSettingsDTO.aggregatesubjectspercentage);
			}
			else {
				qualificationSettingsDBO.aggregateSubjectsPercentage = null;
			}

			Map<Integer, AdmProgrammeQualificationSubjectEligibilityDBO> existingSubject = new HashMap<Integer, AdmProgrammeQualificationSubjectEligibilityDBO>();
			Set<AdmProgrammeQualificationSubjectEligibilityDBO> admProgrammeQualificationSubjectEligibilityDBOs = new HashSet<AdmProgrammeQualificationSubjectEligibilityDBO>();


			if(qualificationSettingsDTO.subjecteligibilityrequired) {
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.admProgrammeQualificationSubjectEligibilityDBO)) {
					for (AdmProgrammeQualificationSubjectEligibilityDBO subDbo : qualificationSettingsDBO.admProgrammeQualificationSubjectEligibilityDBO) {
						if(subDbo.recordStatus == 'A') {
							existingSubject.put(subDbo.id,subDbo);
						}
					}
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDTO.subject)) {
					for(AdmProgrammeQualificationSubjectEligibilityDTO subjectDto : qualificationSettingsDTO.subject ) {
						AdmProgrammeQualificationSubjectEligibilityDBO subjectDbo = null;
						if (!Utils.isNullOrEmpty(existingSubject) && !Utils.isNullOrEmpty(subjectDto.id) && existingSubject.containsKey(Integer.parseInt(subjectDto.id)) ) {
							subjectDbo = existingSubject.get(Integer.parseInt(subjectDto.id));
							subjectDbo.modifiedUsersId = Integer.parseInt(userId);
							existingSubject.remove(Integer.parseInt(subjectDto.id));
						}
						else {
							subjectDbo = new AdmProgrammeQualificationSubjectEligibilityDBO();
							subjectDbo.createdUsersId = Integer.parseInt(userId);
						}
						subjectDbo.admProgrammeQualificationSettingsDBO = qualificationSettingsDBO; 
						subjectDbo.subjectName = subjectDto.subjectname;
						if(!Utils.isNullOrEmpty(subjectDto.eligibilitypercentage) || !Utils.isNullOrWhitespace(subjectDto.eligibilitypercentage))
							subjectDbo.eligibilityPercentage = new BigDecimal(subjectDto.eligibilitypercentage);
						subjectDbo.recordStatus = 'A';
						admProgrammeQualificationSubjectEligibilityDBOs.add(subjectDbo);
					}
				}
			}
				if(!Utils.isNullOrEmpty(existingSubject)){
					for(Entry<Integer, AdmProgrammeQualificationSubjectEligibilityDBO> entry : existingSubject.entrySet()) {
						AdmProgrammeQualificationSubjectEligibilityDBO delDBO = new AdmProgrammeQualificationSubjectEligibilityDBO();
						delDBO = entry.getValue();
						delDBO.recordStatus = 'D';
						delDBO.modifiedUsersId = Integer.parseInt(userId);
						admProgrammeQualificationSubjectEligibilityDBOs.add(delDBO);
					}
				}

			qualificationSettingsDBO.admProgrammeQualificationSubjectEligibilityDBO = admProgrammeQualificationSubjectEligibilityDBOs;
			if(!Utils.isNullOrEmpty(qualificationSettingsDTO.backlogRequired) ) {
				qualificationSettingsDBO.backlogRequired = qualificationSettingsDTO.backlogRequired;
			}
			qualificationSettingsDBO.recordStatus = 'A';
		}
		return qualificationSettingsDBO;
	}
	public AdmProgrammePreferenceSettingsDBO getAdmProgrammPreferenceSettingsDBO(AdmProgrammePreferenceSettingsDTO programmepreferences,
			AdmProgrammePreferenceSettingsDBO preferenceSettingsDBO, AdmProgrammeSettingsDBO dbo, String userId) {
		if(!Utils.isNullOrEmpty(preferenceSettingsDBO)) {
			if(!Utils.isNullOrEmpty(dbo)) {
				preferenceSettingsDBO.admProgrammeSettingsDBO = dbo;
			}
			if(Utils.isNullOrEmpty(programmepreferences.id)) {
				preferenceSettingsDBO.createdUsersId = Integer.parseInt(userId);
			}else {
				preferenceSettingsDBO.modifiedUsersId = Integer.parseInt(userId);
			}
			if(!Utils.isNullOrEmpty(programmepreferences.campusMappingId)) {
				ErpCampusProgrammeMappingDBO campusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
				campusProgrammeMappingDBO.id = Integer.parseInt(programmepreferences.campusMappingId);
				preferenceSettingsDBO.erpCampusProgrammeMappingDBO = campusProgrammeMappingDBO;
			}
			if(!Utils.isNullOrEmpty(programmepreferences.isSpecialisationRequired) && !Utils.isNullOrEmpty(programmepreferences.isSpecialisationRequired.trim())) {
				preferenceSettingsDBO.isSpecialisationRequired = Boolean.parseBoolean(programmepreferences.isSpecialisationRequired.trim());
			}
			preferenceSettingsDBO.recordStatus = 'A';
		}
		return preferenceSettingsDBO;	
	}

	public AdmProgrammeSettingsDTO setDboToDto(AdmProgrammeSettingsDBO dbo, AdmProgrammeSettingsDTO dto) throws Exception {
		dto = new AdmProgrammeSettingsDTO();
		if(!Utils.isNullOrEmpty(dbo.id)) {
			dto.id = String.valueOf(dbo.id);
		}
//		if(!Utils.isNullOrEmpty(dbo.erpCurrencyDBO) && !Utils.isNullOrEmpty(dbo.erpCurrencyDBO.id)) { // sambath
//			ExModelBaseDTO currency = new ExModelBaseDTO();
//			currency.id = String.valueOf(dbo.erpCurrencyDBO.id);
//			dto.currency = currency;
//		}
		if(!Utils.isNullOrEmpty(dbo.erpProgrammeDBO)) {
			SelectDTO programme = new SelectDTO();
			programme.setValue(String.valueOf(dbo.erpProgrammeDBO.id));
			programme.setLabel(String.valueOf(dbo.erpProgrammeDBO.programmeNameForApplication));
			dto.programme = programme;
		}
//		if(!Utils.isNullOrEmpty(dbo.feeForIndianApplicants)) { // sambath
//			dto.indianapplicant = String.valueOf(dbo.feeForIndianApplicants.intValue());
//		}
		if(!Utils.isNullOrEmpty(dbo.noOfPreferenceRequired)) {
			dto.noOfPreferenceRequiredInApplication = String.valueOf(dbo.noOfPreferenceRequired);
		}
		if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())){
			dto.setErpAcademicYear(new ErpAcademicYearDTO());
			dto.getErpAcademicYear().setValue(String.valueOf(dbo.getErpAcademicYearDBO().getId()));
			dto.getErpAcademicYear().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
			dto.getErpAcademicYear().setAcademicYear(dbo.getErpAcademicYearDBO().getAcademicYear());
		}
		if(!Utils.isNullOrEmpty(dbo.getAdmAdmissionTypeDBO())){
			dto.setAdmissionType(new SelectDTO());
			dto.getAdmissionType().setValue(String.valueOf(dbo.getAdmAdmissionTypeDBO().getId()));
			dto.getAdmissionType().setLabel(dbo.getAdmAdmissionTypeDBO().getAdmissionType());
		}
		if(!Utils.isNullOrEmpty(dbo.getAdmIntakeBatchDBO())){
			dto.setIntakeBatch(new SelectDTO());
			dto.getIntakeBatch().setValue(String.valueOf(dbo.getAdmIntakeBatchDBO().getId()));
			dto.getIntakeBatch().setLabel(dbo.getAdmIntakeBatchDBO().getAdmIntakeBatchName());
		}
		if(!Utils.isNullOrEmpty(dbo.getIsHavingOtherProgrammePreferences())){
			dto.setOtherProgrammePref(dbo.getIsHavingOtherProgrammePreferences());
		}
		if(!Utils.isNullOrEmpty(dbo.preferenceOption)) {
			dto.preferenceBasedOn = dbo.preferenceOption;
		}
//		if(!Utils.isNullOrEmpty(dbo.feeForInternationalApplicants)) { // sambath
//			dto.internationalapplicant = String.valueOf(dbo.feeForInternationalApplicants.intValue());
//		}
		if(!Utils.isNullOrEmpty(dbo.isSecondLanguage)) {
			dto.secondlanguage = dbo.isSecondLanguage;
		}
		if(!Utils.isNullOrEmpty(dbo.isResearchTopicRequired)) {
			dto.researchtopicrequired = dbo.isResearchTopicRequired;
		}
		if(!Utils.isNullOrEmpty(dbo.isWorExperienceRequired)) {
			dto.workexperiencerequired = dbo.isWorExperienceRequired;
		}
		if(!Utils.isNullOrEmpty(dbo.isWorkExperienceMandatory)) {
			dto.workexperiencemandatory = dbo.isWorkExperienceMandatory;
		}
		if(!Utils.isNullOrEmpty(dbo.getIsProgrammeModeDisplayed())) {
			dto.setIsProgrammeModeDisplayed(dbo.getIsProgrammeModeDisplayed());
		}
		if(!Utils.isNullOrEmpty(dbo.applicationMode)) {
			dto.modeofapplication = String.valueOf(dbo.applicationMode);
		}
		if(!Utils.isNullOrEmpty(dbo.erpTemplateDBO)) {
			ExModelBaseDTO template = new ExModelBaseDTO();
			template.id =  String.valueOf(dbo.erpTemplateDBO.id);
			template.text = dbo.erpTemplateDBO.getTemplateContent();
			dto.termsCondition = template;
		}
		if(!Utils.isNullOrEmpty(dbo.minNoOfMonthsExperience)) {
			dto.minimumnoofmonthsexperiencerequired = String.valueOf(dbo.minNoOfMonthsExperience);
		}
		if(!Utils.isNullOrEmpty(dbo.getAccFeeHeadsDBO())) {
			dto.setAccFeesHeads(new SelectDTO());
			dto.getAccFeesHeads().setValue(String.valueOf(dbo.getAccFeeHeadsDBO().getId()));
			dto.getAccFeesHeads().setLabel(dbo.getAccFeeHeadsDBO().getHeading());
		}
		dto.setLocationList(new ArrayList<>());
		dto.setCampusList(new ArrayList<>());
		dto.setSpecializationList(new ArrayList<>());
		if(!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBOSet())){
			for (AdmProgrammeBatchDBO admProgrammeBatchDBO:dbo.getAdmProgrammeBatchDBOSet()) {
				if(admProgrammeBatchDBO.getRecordStatus() == 'A'){
					if(!Utils.isNullOrEmpty(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO()) && !Utils.isNullOrEmpty(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().getErpLocationDBO())){
						SelectDTO selectDTO = new SelectDTO();
						selectDTO.setValue(String.valueOf(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().getId()));
						selectDTO.setLabel(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
						dto.getLocationList().add(selectDTO);
						if(admProgrammeBatchDBO.getIsSpecialisationRequired()){
							dto.setSpecializationRequired(true);
							dto.getSpecializationList().add(selectDTO);
						} else {
							dto.setSpecializationRequired(false);
						}

					} else {
						SelectDTO selectDTO = new SelectDTO();
						selectDTO.setValue(String.valueOf(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().getId()));
						selectDTO.setLabel(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
						dto.getCampusList().add(selectDTO);
						if(admProgrammeBatchDBO.getIsSpecialisationRequired()){
							dto.setSpecializationRequired(true);
							dto.getSpecializationList().add(selectDTO);
						} else {
							dto.setSpecializationRequired(false);
						}
					}
				}

			}
		}
//		if(!Utils.isNullOrEmpty(dbo.getOnlinePaymentModes())) {
//			List<String> modes = new ArrayList<String>();
//			String[] b = dbo.getOnlinePaymentModes().split("");
//			 for (String c : b) {
//				 modes.add(c);
//			 }
//			 dto.setPaymentModes(modes);
//		}
		//preference adding is in grid
//		if(!Utils.isNullOrEmpty(dbo.admProgrammePreferenceSettingsSetDbos) && dbo.admProgrammePreferenceSettingsSetDbos.size()>0) {
//			setaAdmProgrammePreferenceSettingsDbosToDtos(dbo.admProgrammePreferenceSettingsSetDbos, dto);
//		}
		if(!Utils.isNullOrEmpty(dbo.admProgrammeDocumentSettingSetDbos) ) {
			setaAdmProgrammeDocumentSettingSetDbosToDtos(dbo.admProgrammeDocumentSettingSetDbos, dto);
		}
		if(!Utils.isNullOrEmpty(dbo.admProgrammeQualificationSettingsSetDbos)) {
			setAdmProgrammeQualificationSettingsSetDbosToDtos(dbo.admProgrammeQualificationSettingsSetDbos, dto);
		}
//		if(!Utils.isNullOrEmpty(dbo.erpApplnPrintTemplate)) {
//			ExModelBaseDTO template = new ExModelBaseDTO();
//			template.id =  String.valueOf(dbo.erpApplnPrintTemplate.id);
//			dto.printTemplate = template;
//		}
		if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeFeePaymentModeDBOSet())) {
			var paymentModeList = new ArrayList<SelectDTO>();
			dbo.getAdmProgrammeFeePaymentModeDBOSet().forEach(departmentSet -> {
				if (!Utils.isNullOrEmpty(departmentSet.getAccFeePaymentModeDBO())) {
					if(departmentSet.getRecordStatus()=='A') {
						var selectDto = new SelectDTO();
						selectDto.setValue(String.valueOf(departmentSet.getAccFeePaymentModeDBO().getId()));
						selectDto.setLabel(departmentSet.getAccFeePaymentModeDBO().getPaymentMode());
						paymentModeList.add(selectDto);
					}
				}
			});
			if(!Utils.isNullOrEmpty(dbo.getModeOfStudy())){
				dto.setProgrammeMode(new SelectDTO());
				dto.getProgrammeMode().setValue(String.valueOf(dbo.getModeOfStudy()));
				dto.getProgrammeMode().setLabel(String.valueOf(dbo.getModeOfStudy()).replace("_"," "));
			}
			dto.setAdmProgrammeFeePaymentModeDTOList(paymentModeList);
		}
		return dto;
	}

	private void setAdmProgrammeQualificationSettingsSetDbosToDtos(Set<AdmProgrammeQualificationSettingsDBO> admProgrammeQualificationSettingsSetDbos, AdmProgrammeSettingsDTO dto) {
		AdmProgrammeQualificationSettingsDTO qualificationSettingsDTO = null;
		dto.qualificationsettings = new ArrayList<AdmProgrammeQualificationSettingsDTO>();
		for (AdmProgrammeQualificationSettingsDBO qualificationSettingsDBO : admProgrammeQualificationSettingsSetDbos) {
			if(!Utils.isNullOrEmpty(qualificationSettingsDBO.recordStatus) && qualificationSettingsDBO.recordStatus == 'A') {
				qualificationSettingsDTO = new AdmProgrammeQualificationSettingsDTO();
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.id)) {
					qualificationSettingsDTO.id = String.valueOf(qualificationSettingsDBO.id);
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.qualificationOrder)) {
					qualificationSettingsDTO.displayorder = String.valueOf(qualificationSettingsDBO.qualificationOrder);
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.admQualificationListDBO) && !Utils.isNullOrEmpty(qualificationSettingsDBO.admQualificationListDBO.id)) {
					ExModelBaseDTO qualificationLevel = new ExModelBaseDTO();
					qualificationLevel.id = String.valueOf(qualificationSettingsDBO.admQualificationListDBO.id);
					qualificationSettingsDTO.qualificationlevel = qualificationLevel;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.isQualificationLevelMandatory)) {
					qualificationSettingsDTO.qualificationlevelmandatory = qualificationSettingsDBO.isQualificationLevelMandatory;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.isUploadRequired)) {
					qualificationSettingsDTO.uploadrequired = qualificationSettingsDBO.isUploadRequired;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.isUploadMandatory)) {
					qualificationSettingsDTO.uploadmandatory = qualificationSettingsDBO.isUploadMandatory;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.universityOrBoard)) {
//					ExModelBaseDTO qualification = new ExModelBaseDTO();
//					qualification.text = String.valueOf(qualificationSettingsDBO.universityOrBoard);
//					qualificationSettingsDTO.universityrequired = qualification;

//					qualificationSettingsDTO.setUniversityBoard(new SelectDTO());
//					qualificationSettingsDTO.getUniversityBoard().setValue(qualificationSettingsDBO.universityOrBoard);
//					qualificationSettingsDTO.getUniversityBoard().setLabel(qualificationSettingsDBO.universityOrBoard.replace("_"," "));
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.isExamnameRequired)) {
					qualificationSettingsDTO.examnamerequired = qualificationSettingsDBO.isExamnameRequired;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.getIsExamRegiNumbRequired())) {
					qualificationSettingsDTO.setExamRegisterNumberRequired(qualificationSettingsDBO.getIsExamRegiNumbRequired());
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.marksEntryType)) {
					ExModelBaseDTO marks = new ExModelBaseDTO();
					marks.id = String.valueOf( qualificationSettingsDBO.marksEntryType);
					marks.text =  String.valueOf( qualificationSettingsDBO.marksEntryType);
					qualificationSettingsDTO.marksentrytype = marks;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.isSubjectEligibilityRequired)) {
					qualificationSettingsDTO.subjecteligibilityrequired = qualificationSettingsDBO.isSubjectEligibilityRequired;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.minSubjectsForEligibility)) {
					qualificationSettingsDTO.minsubjectforeligibility = qualificationSettingsDBO.minSubjectsForEligibility;
				}
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.aggregateSubjectsPercentage)) {
					qualificationSettingsDTO.aggregatesubjectspercentage = qualificationSettingsDBO.aggregateSubjectsPercentage.doubleValue();
				}
				qualificationSettingsDTO.subject = new ArrayList<AdmProgrammeQualificationSubjectEligibilityDTO>();
				if(!Utils.isNullOrEmpty(qualificationSettingsDBO.admProgrammeQualificationSubjectEligibilityDBO)) {
					for(AdmProgrammeQualificationSubjectEligibilityDBO subjectDBO : qualificationSettingsDBO.admProgrammeQualificationSubjectEligibilityDBO) {
						if(subjectDBO.recordStatus == 'A') {
							AdmProgrammeQualificationSubjectEligibilityDTO subjectDTO = new AdmProgrammeQualificationSubjectEligibilityDTO();
							subjectDTO.id = String.valueOf(subjectDBO.id);
							subjectDTO.subjectname = subjectDBO.subjectName;
							if(!Utils.isNullOrEmpty(subjectDBO.eligibilityPercentage))
							subjectDTO.eligibilitypercentage =String.valueOf(subjectDBO.eligibilityPercentage);							
							qualificationSettingsDTO.subject.add(subjectDTO);
						}
					}					
				}
				else {
					AdmProgrammeQualificationSubjectEligibilityDTO subjectDTO = new AdmProgrammeQualificationSubjectEligibilityDTO();
					qualificationSettingsDTO.subject.add(subjectDTO);
				}
				qualificationSettingsDTO.backlogRequired = qualificationSettingsDBO.backlogRequired;
				dto.qualificationsettings.add(qualificationSettingsDTO);
			}
		}
	}

	private void setaAdmProgrammeDocumentSettingSetDbosToDtos(Set<AdmProgrammeDocumentSettingsDBO> admProgrammeDocumentSettingSetDbos, AdmProgrammeSettingsDTO dto) {
		AdmProgrammeDocumentSettingsDTO documentSettingsDTO = null;
		dto.additionaldocumenttemplate = new ArrayList<AdmProgrammeDocumentSettingsDTO>();
		for (AdmProgrammeDocumentSettingsDBO documentSettingsDBO : admProgrammeDocumentSettingSetDbos) {
			if(!Utils.isNullOrEmpty(documentSettingsDBO.recordStatus) && documentSettingsDBO.recordStatus == 'A') {
				documentSettingsDTO = new AdmProgrammeDocumentSettingsDTO();		 
				 if(!Utils.isNullOrEmpty(documentSettingsDBO.id))	{
					 documentSettingsDTO.id = String.valueOf(documentSettingsDBO.id);
				 }
				 if(!Utils.isNullOrEmpty(documentSettingsDBO.documentOrder))	{
					 documentSettingsDTO.displayorder = String.valueOf(documentSettingsDBO.documentOrder);
				 }
				 if(!Utils.isNullOrEmpty(documentSettingsDBO.admQualificationListDBO) && !Utils.isNullOrEmpty(documentSettingsDBO.admQualificationListDBO.id))	{
					 ExModelBaseDTO addtional = new ExModelBaseDTO();
					 addtional.id = String.valueOf(documentSettingsDBO.admQualificationListDBO.id);
					 documentSettingsDTO.aditionaldocument = addtional;
				 }
//				 if(!Utils.isNullOrEmpty(documentSettingsDBO.isToBeSubmitted))	{
//					 documentSettingsDTO.tobesubmittedmandatory = documentSettingsDBO.isToBeSubmitted);
//				 }
				 if(!Utils.isNullOrEmpty(documentSettingsDBO.isOnlineUploadRequired))	{
					 documentSettingsDTO.onlineuploadrequired = documentSettingsDBO.isOnlineUploadRequired;
				 }
				 if(!Utils.isNullOrEmpty(documentSettingsDBO.isUploadMandatory))	{
					 documentSettingsDTO.uploadmandatory = documentSettingsDBO.isUploadMandatory;
				 }
//				 if(!Utils.isNullOrEmpty(documentSettingsDBO.isAdditionalForForeignNational))	{
//					 documentSettingsDTO.additionalforforeignnationalrequired = String.valueOf(documentSettingsDBO.isAdditionalForForeignNational);
//				 }
				 dto.additionaldocumenttemplate.add(documentSettingsDTO);
			}
		}
	}

	//preference adding is in grid
//	private void setaAdmProgrammePreferenceSettingsDbosToDtos(Set<AdmProgrammePreferenceSettingsDBO> admProgrammePreferenceSettingsSetDbos, AdmProgrammeSettingsDTO dto) throws Exception {
//		dto.programmepreferences = new ArrayList<AdmProgrammePreferenceSettingsDTO>();
//		for (AdmProgrammePreferenceSettingsDBO preferenceSettingsDBO : admProgrammePreferenceSettingsSetDbos) {
//			if(!Utils.isNullOrEmpty(preferenceSettingsDBO.recordStatus) && preferenceSettingsDBO.recordStatus == 'A') {
//				AdmProgrammePreferenceSettingsDTO preferenceSettingsDTO = new AdmProgrammePreferenceSettingsDTO();
//				if(!Utils.isNullOrEmpty(preferenceSettingsDBO.id))	{
//					preferenceSettingsDTO.id = String.valueOf(preferenceSettingsDBO.id);
//				}
//				if(!Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO))	{
//					preferenceSettingsDTO.campusMappingId = String.valueOf(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.id);
//					ExModelBaseDTO programme = new ExModelBaseDTO();
//					programme.id = String.valueOf(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id);
//					programme.text = String.valueOf(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeName);
//					preferenceSettingsDTO.programmepreference = programme;
//				}
//				if(!Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO))	{
//					ExModelBaseDTO campus = new ExModelBaseDTO();
//					campus.id =  String.valueOf(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
//					campus.text = !Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName)?preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName:"";
//					preferenceSettingsDTO.campus =	campus;
//					if(!Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
//						List<ErpCampusProgrammeMappingDBO> mappings = transaction.getLocationByProgramme(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id);
//						if(!Utils.isNullOrEmpty(mappings) && mappings.size()>0) {
//							List<LookupItemDTO> campusList = new ArrayList<LookupItemDTO>();
//							List<LookupItemDTO> locationList = new ArrayList<LookupItemDTO>();
//							List<Integer> idscampus = new ArrayList<Integer>(), idsLocations= new ArrayList<Integer>();
//							for(ErpCampusProgrammeMappingDBO dbo : mappings) {
//								if(!Utils.isNullOrEmpty(dbo.erpProgrammeDBO) && !Utils.isNullOrEmpty(dbo.erpCampusDBO) && !idscampus.contains(dbo.erpCampusDBO.id)) {
//									idscampus.add(dbo.erpCampusDBO.id);
//									LookupItemDTO campusDto = new LookupItemDTO();
//									campusDto.value = !Utils.isNullOrEmpty(dbo.erpCampusDBO.id) ? String.valueOf(dbo.erpCampusDBO.id) : "";
//									campusDto.label = !Utils.isNullOrEmpty(dbo.erpCampusDBO.campusName) ? String.valueOf(dbo.erpCampusDBO.campusName) : "";
//									campusList.add(campusDto);
//								}
//								if(!Utils.isNullOrEmpty(dbo.erpProgrammeDBO) && !Utils.isNullOrEmpty(dbo.erpLocationDBO)
//										&& !idsLocations.contains(dbo.erpLocationDBO.id)) {
//									idsLocations.add(dbo.erpLocationDBO.id);
//									LookupItemDTO locationDto = new LookupItemDTO();
//				            		locationDto.value = !Utils.isNullOrEmpty(dbo.erpLocationDBO.id) ? String.valueOf(dbo.erpLocationDBO.id) : "";
//				            		locationDto.label = !Utils.isNullOrEmpty(dbo.erpLocationDBO.locationName) ? String.valueOf(dbo.erpLocationDBO.locationName) : "";
//				            		locationList.add(locationDto);
//								}
//				            }
//							preferenceSettingsDTO.campusList = campusList;
//							preferenceSettingsDTO.locations = locationList;
//						}
//					}
//	            }
//				if(!Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO))	{
//					ExModelBaseDTO location = new ExModelBaseDTO();
//					location.id =  String.valueOf(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.id);
//					location.text = !Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.locationName) ?
//							preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.locationName:"";
//					preferenceSettingsDTO.location =	location;
//					if(!Utils.isNullOrEmpty(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
//						List<ErpCampusProgrammeMappingDBO> mappings = transaction.getLocationByProgramme(preferenceSettingsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id);
//						if(!Utils.isNullOrEmpty(mappings) && mappings.size()>0) {
//							List<LookupItemDTO> campusList = new ArrayList<LookupItemDTO>();
//							List<LookupItemDTO> locationList = new ArrayList<LookupItemDTO>();
//							List<Integer> idscampus = new ArrayList<Integer>(), idsLocations= new ArrayList<Integer>();
//							for(ErpCampusProgrammeMappingDBO dbo : mappings) {
//								if(!Utils.isNullOrEmpty(dbo.erpProgrammeDBO) && !Utils.isNullOrEmpty(dbo.erpLocationDBO) && !idsLocations.contains(dbo.erpLocationDBO.id)) {
//									idsLocations.add(dbo.erpLocationDBO.id);
//									LookupItemDTO locationDto = new LookupItemDTO();
//				            		locationDto.value = !Utils.isNullOrEmpty(dbo.erpLocationDBO.id) ? String.valueOf(dbo.erpLocationDBO.id) : "";
//				            		locationDto.label = !Utils.isNullOrEmpty(dbo.erpLocationDBO.locationName) ? String.valueOf(dbo.erpLocationDBO.locationName) : "";
//				            		locationList.add(locationDto);
//								}
//								if(!Utils.isNullOrEmpty(dbo.erpProgrammeDBO) && !Utils.isNullOrEmpty(dbo.erpCampusDBO) && !idscampus.contains(dbo.erpCampusDBO.id)) {
//									idscampus.add(dbo.erpCampusDBO.id);
//									LookupItemDTO campusDto = new LookupItemDTO();
//									campusDto.value = !Utils.isNullOrEmpty(dbo.erpCampusDBO.id) ? String.valueOf(dbo.erpCampusDBO.id) : "";
//									campusDto.label = !Utils.isNullOrEmpty(dbo.erpCampusDBO.campusName) ? String.valueOf(dbo.erpCampusDBO.campusName) : "";
//									campusList.add(campusDto);
//								}
//				            }
//							preferenceSettingsDTO.locations = locationList;
//							preferenceSettingsDTO.campusList = campusList;
//						}
//					}
//	            }
//				if(!Utils.isNullOrEmpty(preferenceSettingsDBO.isSpecialisationRequired)) {
//					preferenceSettingsDTO.isSpecialisationRequired = String.valueOf(preferenceSettingsDBO.isSpecialisationRequired);
//				}
//				dto.programmepreferences.add(preferenceSettingsDTO);
//			}
//		}
//		Collections.sort(dto.programmepreferences);
//	}
	public AdmProgrammeFeePaymentModeDBO getAdmProgrammeFeePaymentModeDBO(
			AdmProgrammeFeePaymentModeDTO feePaymentModeDTO, AdmProgrammeFeePaymentModeDBO feePaymentModeDBO,
			AdmProgrammeSettingsDBO dbo, String userId) {
		// TODO Auto-generated method stub
		return null;
	}
}
