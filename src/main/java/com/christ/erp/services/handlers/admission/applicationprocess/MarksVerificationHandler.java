package com.christ.erp.services.handlers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeQualificationSubjectEligibilityDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.*;
import com.christ.erp.services.dbobjects.student.common.*;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnPrerequisiteDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.admission.applicationprocess.MarksVerificationTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MarksVerificationHandler {

	@Autowired
	private MarksVerificationTransaction marksVerificationTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	public Mono<StudentApplnEntriesDTO> getStudentDetails(int applicationNumber) {
		Tuple tuple = marksVerificationTransaction.getStudentDetails(applicationNumber);
		StudentApplnEntriesDTO dto = new StudentApplnEntriesDTO();
		if(!Utils.isNullOrEmpty(tuple)) {
			if(!Utils.isNullOrEmpty(tuple.get("applicant_name"))) {
				dto.setApplicantName(tuple.get("applicant_name").toString());
			}
			dto.setProgram(new SelectDTO());
			if(!Utils.isNullOrEmpty(tuple.get("erp_programme_id"))) {
				dto.getProgram().setValue(tuple.get("erp_programme_id").toString());
				dto.getProgram().setLabel(tuple.get("programme_name").toString());
			}
			if(!Utils.isNullOrEmpty(tuple.get("campus_name"))) {
				dto.setCampusOrLocation(tuple.get("campus_name").toString());
			} else {
				dto.setCampusOrLocation(tuple.get("location_name").toString());
			} 
			if(!Utils.isNullOrEmpty(tuple.get("application_verification_status"))) {
				if(tuple.get("application_verification_status").toString().equals("VE")) {
					dto.setApplicationVerificationStatus("Application Verified");
				}
				else if(tuple.get("application_verification_status").toString().equals("NE")) {
					dto.setApplicationVerificationStatus("Not Eligible");
				}
				else if(tuple.get("application_verification_status").toString().equals("NV")) {
					dto.setApplicationVerificationStatus("Application Not Verified");	
				}
			} else {
				dto.setApplicationVerificationStatus("Application Not Verified");
			}
		}
		return Mono.just(dto);

	}

	public Flux<SelectDTO> getRemarkDetails() {
		return marksVerificationTransaction.getRemarkDetails().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(StudentApplnVerificationRemarksDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getVerificationRemarksName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getVerificationRemarksName());
		}
		return dto;
	}

	public Mono<StudentApplnEntriesDTO> getEducationalDetails(int applicationNumber) {
		StudentApplnEntriesDBO list = marksVerificationTransaction.getEducationalDetails(applicationNumber);
		return this.convertDboToDto(list);
	}

	private Mono<StudentApplnEntriesDTO> convertDboToDto(StudentApplnEntriesDBO dbo) {
		StudentApplnEntriesDTO stdAppDto = new StudentApplnEntriesDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			stdAppDto.setId(dbo.getId());
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				stdAppDto.setApplicationNumber(dbo.getApplicationNo().toString());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
				stdAppDto.setApplicantName(dbo.getApplicantName().toString());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnVerificationsId())) {
				stdAppDto.setStudentApplnVerificationRemarksId(new SelectDTO());
				stdAppDto.getStudentApplnVerificationRemarksId().setValue(dbo.getStudentApplnVerificationsId().getId().toString());
				stdAppDto.getStudentApplnVerificationRemarksId().setLabel(dbo.getStudentApplnVerificationsId().getVerificationRemarksName());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationVerificationAddtlRemarks())) {
				stdAppDto.setApplicationVerificationAddtlRemarks(dbo.getApplicationVerificationAddtlRemarks());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationVerificationStatus())) {
				stdAppDto.setApplicationVerificationStatus(dbo.getApplicationVerificationStatus().toString());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationVerifiedUserId())) {
				stdAppDto.setApplicationVerifiedUserId(dbo.getApplicationVerifiedUserId().toString());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationVerifiedDate())) {
				stdAppDto.setApplicationVerifiedDate(dbo.getApplicationVerifiedDate());
			}
//			if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO())) {
//				if(dbo.getStudentApplnPrerequisiteDBO().getRecordStatus()=='A') {
//					stdAppDto.setStudentApplnPrerequisite(new StudentApplnPrerequisiteDTO());
//					stdAppDto.getStudentApplnPrerequisite().setId(dbo.getStudentApplnPrerequisiteDBO().getId());
//					if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO().getExamRollNo())) {
//						stdAppDto.getStudentApplnPrerequisite().setExamRollNo(dbo.getStudentApplnPrerequisiteDBO().getExamRollNo());
//					}
//					if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO().getMarksObtained())) {
//						stdAppDto.getStudentApplnPrerequisite().setMarksObtained(dbo.getStudentApplnPrerequisiteDBO().getMarksObtained());
//					}
//					AdmPreRequisiteSettingPeriodDTO periodDto = new AdmPreRequisiteSettingPeriodDTO();
//					if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO())) {
//						periodDto.setId(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getId().toString());
//						periodDto.setMonth(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().examMonth.toString());
//						periodDto.setYear(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().examYear.toString());
//						stdAppDto.getStudentApplnPrerequisite().setAdmPreRequisiteSettingPeriodDTO(periodDto);
//					}
//					AdmPrerequisiteSettingsDTO settingsDto = new AdmPrerequisiteSettingsDTO();
//					if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().admPrerequisiteSettingsDBO)) {
//						settingsDto.setAcademicYear(new ExModelBaseDTO());
//						settingsDto.getAcademicYear().id = String.valueOf(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().admPrerequisiteSettingsDBO.erpAcademicYearDBO.id);
//						settingsDto.setProgramme(new ExModelBaseDTO());
//						settingsDto.getProgramme().id = String.valueOf(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().admPrerequisiteSettingsDBO.erpProgrammeDBO.id);
//						settingsDto.setId(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().admPrerequisiteSettingsDBO.id);
//						settingsDto.setAdmPrerequisiteExamDTO(new SelectDTO());
////						if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().admPrerequisiteSettingsDBO.admPrerequisiteExamDBO)) {
////							settingsDto.getAdmPrerequisiteExamDTO().setValue(String.valueOf(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().admPrerequisiteSettingsDBO.admPrerequisiteExamDBO.id));
////							settingsDto.getAdmPrerequisiteExamDTO().setLabel(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().admPrerequisiteSettingsDBO.admPrerequisiteExamDBO.examName);
////							stdAppDto.getStudentApplnPrerequisite().getAdmPreRequisiteSettingPeriodDTO().setAdmPreRequisiteSettingDTO(settingsDto);
////						}
//					}
//				}
//				List<StudentEducationalDetailsDTO> educationalList = new ArrayList<StudentEducationalDetailsDTO>();
//				dbo.getStudentEducationalDetailsDBOS().forEach(eduDbo -> {
//					StudentEducationalDetailsDTO educationalDto = new StudentEducationalDetailsDTO();
//					if(eduDbo.getRecordStatus()=='A') {
//						if(!Utils.isNullOrEmpty(eduDbo.getAdmQualificationListDBO())) {
//							educationalDto.setAdmQualificationListDTO(new AdmQualificationListDTO());
//							educationalDto.getAdmQualificationListDTO().setId(eduDbo.getAdmQualificationListDBO().getId());
//							educationalDto.getAdmQualificationListDTO().setQualificationName(eduDbo.getAdmQualificationListDBO().getQualificationName());
//							educationalDto.getAdmQualificationListDTO().setQualificationOrder(eduDbo.getAdmQualificationListDBO().getQualificationOrder());
//							educationalDto.setId(eduDbo.getId());
//							if(!Utils.isNullOrEmpty(eduDbo.getExamRegisterNo())) {
//								educationalDto.setExamRegisterNo(eduDbo.getExamRegisterNo());
//							}
//							if(!Utils.isNullOrEmpty(eduDbo.getMonthOfPassing())) {
//								educationalDto.setMonthOfPassing(eduDbo.getMonthOfPassing());
//							}
//							if(!Utils.isNullOrEmpty(eduDbo.getYearOfPassing())) {
//								educationalDto.setYearOfPassing(eduDbo.getYearOfPassing());
//							}
//							educationalDto.setNoOfPendingBacklogs(eduDbo.getNoOfPendingBacklogs());
//							if(!Utils.isNullOrEmpty(eduDbo.getConsolidatedMarksObtained())) {
//								educationalDto.setConsolidatedMarksObtained(eduDbo.getConsolidatedMarksObtained());
//							}
//							if(!Utils.isNullOrEmpty(eduDbo.getConsolidatedMaximumMarks())) {
//								educationalDto.setConsolidatedMaximumMarks(eduDbo.getConsolidatedMaximumMarks());
//							}
//							if(!Utils.isNullOrEmpty(eduDbo.getPercentage())) {
//								educationalDto.setPercentage(String.valueOf(eduDbo.getPercentage()));
//							}
//							if(!Utils.isNullOrEmpty(eduDbo.getAdmQualificationDegreeListDBO())) {
//								educationalDto.setAdmQualificationDegreeListDTO(new AdmQualificationDegreeListDTO());
//								educationalDto.getAdmQualificationDegreeListDTO().setId(eduDbo.getAdmQualificationDegreeListDBO().getId());
//								educationalDto.getAdmQualificationDegreeListDTO().setDegreeName(eduDbo.getAdmQualificationDegreeListDBO().getDegreeName());
//							}
//							List<StudentEducationalMarkDetailsDTO> markDetails = new ArrayList<StudentEducationalMarkDetailsDTO>();
//							eduDbo.getStudentEducationalMarkDetailsDBOSet().forEach(markDetailsDbo -> {
//								StudentEducationalMarkDetailsDTO  marksDetailsDto = new StudentEducationalMarkDetailsDTO();
//								if(markDetailsDbo.getRecordStatus()=='A') {
//									marksDetailsDto.setId(markDetailsDbo.getId());
//									if(!Utils.isNullOrEmpty(markDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO())) {
//										marksDetailsDto.setAdmProgrammeQualificationSubjectEligibilityDTO(new AdmProgrammeQualificationSubjectEligibilityDTO());
//										marksDetailsDto.getAdmProgrammeQualificationSubjectEligibilityDTO().setId(String.valueOf(markDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO().id));
//										marksDetailsDto.getAdmProgrammeQualificationSubjectEligibilityDTO().setSubjectname(markDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO().subjectName);
//										marksDetailsDto.getAdmProgrammeQualificationSubjectEligibilityDTO().setEligibilitypercentage(String.valueOf(markDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO().getEligibilityPercentage()));
//										if(!Utils.isNullOrEmpty(markDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO().getAdmProgrammeQualificationSettingsDBO())) {
//											marksDetailsDto.getAdmProgrammeQualificationSubjectEligibilityDTO().setAdmProgrammeQualificationSettingsDTO(new AdmProgrammeQualificationSettingsDTO());
//											marksDetailsDto.getAdmProgrammeQualificationSubjectEligibilityDTO().getAdmProgrammeQualificationSettingsDTO().setMinsubjectforeligibility(markDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO().getAdmProgrammeQualificationSettingsDBO().minSubjectsForEligibility);
//											marksDetailsDto.getAdmProgrammeQualificationSubjectEligibilityDTO().getAdmProgrammeQualificationSettingsDTO().setAggregatesubjectspercentage(Double.parseDouble(String.valueOf(markDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO().getAdmProgrammeQualificationSettingsDBO().aggregateSubjectsPercentage)));
//										}
//									} else {
//										marksDetailsDto.setSemesterName(markDetailsDbo.getSemesterName());
//										if(!Utils.isNullOrEmpty(markDetailsDbo.getDisplayOrder())) {
//											marksDetailsDto.setDisplayOrder(markDetailsDbo.getDisplayOrder());
//										}
//									}
//									if(!Utils.isNullOrEmpty(markDetailsDbo.getMaximumMarks())) {
//										marksDetailsDto.setMaximumMarks(markDetailsDbo.getMaximumMarks());
//									}
//									if(!Utils.isNullOrEmpty(markDetailsDbo.getMarksObtained())) {
//										marksDetailsDto.setMarksObtained(markDetailsDbo.getMarksObtained());
//									}
//									markDetails.add(marksDetailsDto);
//								}
//							});
//							markDetails.stream().filter(s -> s.getAdmProgrammeQualificationSubjectEligibilityDTO() != null).sorted(Comparator.comparing(s -> s.getAdmProgrammeQualificationSubjectEligibilityDTO().getSubjectname())).collect(Collectors.toList());
//							educationalDto.setStudentEducationalMarkDetailS(markDetails);
//						}
//					}
//					if(!Utils.isNullOrEmpty(eduDbo.getErpInstitutionDBO()))  {
//						educationalDto.setErpInstitutionDTO(new ErpInstitutionDTO());
//						educationalDto.getErpInstitutionDTO().setId(eduDbo.getErpInstitutionDBO().getId());
//						educationalDto.getErpInstitutionDTO().setInstitutionName(eduDbo.getErpInstitutionDBO().getInstitutionName());
//					} else {
//						educationalDto.setInstitutionOthers(eduDbo.getInstitutionOthers());
//					}
//					if(!Utils.isNullOrEmpty(eduDbo.getErpUniversityBoardDBO())) {
//						educationalDto.setUniversityBoard(new SelectDTO());
//						educationalDto.getUniversityBoard().setValue(String.valueOf(eduDbo.getErpUniversityBoardDBO().getId()));
//						educationalDto.getUniversityBoard().setLabel(eduDbo.getErpUniversityBoardDBO().getUniversityBoardName());
//					}
//					if(!Utils.isNullOrEmpty(eduDbo.getInstitutionCountry())) {
//						educationalDto.setCountry(new SelectDTO());
//						educationalDto.getCountry().setValue(String.valueOf(eduDbo.getInstitutionCountry().getId()));
//						educationalDto.getCountry().setLabel(eduDbo.getInstitutionCountry().getCountryName());
//					}
//					if(!Utils.isNullOrEmpty(eduDbo.getInstitutionState())) {
//						educationalDto.setState(new SelectDTO());
//						educationalDto.getState().setValue(String.valueOf(eduDbo.getInstitutionState().getId()));
//						educationalDto.getState().setLabel(eduDbo.getInstitutionState().getStateName());
//					} else {
//						educationalDto.setInstitutionOthersState(eduDbo.getInstitutionOthersState());
//					}
//					List<StudentEducationalDetailsDocumentsDTO> documentList = new ArrayList<StudentEducationalDetailsDocumentsDTO>();
//					eduDbo.getStudentEducationalDetailsDocumentsDBOSet().forEach(documentsDbo -> {
//						StudentEducationalDetailsDocumentsDTO documentDetailsDto = new StudentEducationalDetailsDocumentsDTO();
//						if(documentsDbo.getRecordStatus()=='A') {
//							documentDetailsDto.setId(documentsDbo.getId());
//							documentDetailsDto.setDocumentsUrl(documentsDbo.getDocumentsUrl());
//							documentList.add(documentDetailsDto);
//						}
//					});
//					educationalDto.setStudentEducationalDetailsDocuments(documentList);
//					educationalList.add(educationalDto);
//				});
//				stdAppDto.setStudentEducationalDetails(educationalList);
//			}
		}
		return !Utils.isNullOrEmpty(dbo) ? Mono.just(stdAppDto) : Mono.error(new NotFoundException(null));
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> marksVerificationUpdate(Mono<StudentApplnEntriesDTO> dto, int userId) {
		return dto.map(data -> convertDtoToDbo(data ,userId))
				.flatMap(s -> {
					marksVerificationTransaction.merge(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo(StudentApplnEntriesDTO dto, int userId) {
		List<Object> list = new ArrayList<Object>();
		List<ErpWorkFlowProcessStatusLogDBO> statusList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		StudentApplnEntriesDBO dbo = null;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo = marksVerificationTransaction.getEmployeeDetailsForUpdate(dto.getId());
			dbo.setModifiedUsersId(userId);
		}
		dbo.setId(dto.getId());
		dbo.setApplicationVerifiedDate(LocalDate.now());
		dbo.setApplicationVerifiedUserId(new ErpUsersDBO());
		dbo.getApplicationVerifiedUserId().setId(userId);
		if(!Utils.isNullOrEmpty(dto.getApplicationVerificationAddtlRemarks())) {
			dbo.setApplicationVerificationAddtlRemarks(dto.getApplicationVerificationAddtlRemarks().toString());
		}
		if(!Utils.isNullOrEmpty(dto.getApplicationVerificationStatus()) && !Utils.isNullOrEmpty(dto.getApplicationVerificationStatus().trim())) {
			dbo.applicationVerificationStatus = ApplicationVerificationStatus.valueOf(dto.getApplicationVerificationStatus());					
		}
		dbo.setStudentApplnVerificationsId(new StudentApplnVerificationRemarksDBO());
		dbo.getStudentApplnVerificationsId().setId(Integer.parseInt(dto.getStudentApplnVerificationRemarksId().getValue()));
		dbo.setStudentApplnPrerequisiteDBO(new StudentApplnPrerequisiteDBO());
		dbo.getStudentApplnPrerequisiteDBO().setStudentApplnEntriesDBO(dbo);
		dbo.getStudentApplnPrerequisiteDBO().setId(dto.getStudentApplnPrerequisite().getId());
		dbo.getStudentApplnPrerequisiteDBO().setExamRollNo(dto.getStudentApplnPrerequisite().getExamRollNo());
		dbo.getStudentApplnPrerequisiteDBO().setMarksObtained(dto.getStudentApplnPrerequisite().getMarksObtained());
//		Integer prerequisiteId = marksVerificationTransaction.getPrerequisiteId(dto.getStudentApplnPrerequisite().getAdmPreRequisiteSettingPeriodDTO().getYear(), dto.getStudentApplnPrerequisite().getAdmPreRequisiteSettingPeriodDTO().getMonth(), dto.getStudentApplnPrerequisite().getAdmPreRequisiteSettingPeriodDTO().getAdmPreRequisiteSettingDTO().getAdmPrerequisiteExamDTO().getValue());
//		if(!Utils.isNullOrEmpty(prerequisiteId)) {
//			dbo.getStudentApplnPrerequisiteDBO().setAdmPrerequisiteSettingPeriodDBO(new AdmPrerequisiteSettingPeriodDBO());
//			dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().setId(prerequisiteId);
//		}
//		if(!Utils.isNullOrEmpty(dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getAdmPrerequisiteSettingsDBO())) {
//			dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getAdmPrerequisiteSettingsDBO().setAdmPrerequisiteExamDBO(new AdmPrerequisiteExamDBO());
//			dbo.getStudentApplnPrerequisiteDBO().getAdmPrerequisiteSettingPeriodDBO().getAdmPrerequisiteSettingsDBO().getAdmPrerequisiteExamDBO().setId(Integer.parseInt(dto.getStudentApplnPrerequisite().getAdmPreRequisiteSettingPeriodDTO().getAdmPreRequisiteSettingDTO().getAdmPrerequisiteExamDTO().getValue()));
//		}
		dbo.getStudentApplnPrerequisiteDBO().setModifiedUsersId(userId);
		dbo.getStudentApplnPrerequisiteDBO().setRecordStatus('A');
		StudentApplnEntriesDBO dbo1 = dbo;
		if(!Utils.isNullOrEmpty(dto.getStudentEducationalDetails())) {
			Set<StudentEducationalDetailsDBO> studEducationsDbo = new HashSet<StudentEducationalDetailsDBO>();  
			dto.getStudentEducationalDetails().forEach(eduDto -> {
				StudentEducationalDetailsDBO edDbo = new StudentEducationalDetailsDBO();
				edDbo.setId(eduDto.getId());
				edDbo.setStudentApplnEntriesDBO(dbo1);
				edDbo.setExamRegisterNo(eduDto.getExamRegisterNo());
				edDbo.setMonthOfPassing(eduDto.getMonthOfPassing());
//				edDbo.setPercentage(String.valueOf(eduDto.getPercentage()));
				edDbo.setPercentage(new BigDecimal(eduDto.getPercentage()));
				edDbo.setYearOfPassing(eduDto.getYearOfPassing());
				edDbo.setNoOfPendingBacklogs(eduDto.getNoOfPendingBacklogs());
				edDbo.setConsolidatedMarksObtained(eduDto.getConsolidatedMarksObtained());
				edDbo.setConsolidatedMaximumMarks(eduDto.getConsolidatedMaximumMarks());
				edDbo.setAdmQualificationListDBO(new AdmQualificationListDBO());
				edDbo.getAdmQualificationListDBO().setId(eduDto.getAdmQualificationListDTO().getId());
				edDbo.setAdmQualificationDegreeListDBO(new AdmQualificationDegreeListDBO());
				edDbo.getAdmQualificationDegreeListDBO().setId(eduDto.getAdmQualificationDegreeListDTO().getId());
				if(!Utils.isNullOrEmpty(eduDto.getErpInstitutionDTO().getId())) {
					edDbo.setErpInstitutionDBO(new ErpInstitutionDBO());
					edDbo.getErpInstitutionDBO().setId(eduDto.getErpInstitutionDTO().getId());
				} else {
					edDbo.setInstitutionOthers(eduDto.getInstitutionOthers());
				}
				edDbo.setErpUniversityBoardDBO(new ErpUniversityBoardDBO());
				edDbo.getErpUniversityBoardDBO().setId(Integer.parseInt(eduDto.getUniversityBoard().getValue()));
				edDbo.setInstitutionCountry(new ErpCountryDBO());
				edDbo.getInstitutionCountry().setId(Integer.parseInt(eduDto.getCountry().getValue()));
				if(!Utils.isNullOrEmpty(eduDto.getState())) {
					edDbo.setInstitutionState(new ErpStateDBO());
					edDbo.getInstitutionState().setId(Integer.parseInt(eduDto.getState().getValue()));
				} else {
					edDbo.setInstitutionOthersState(eduDto.getInstitutionOthersState());
				}
				edDbo.setModifiedUsersId(userId);
				edDbo.setRecordStatus('A');
				if(!Utils.isNullOrEmpty(eduDto.getStudentEducationalMarkDetailS())) {
					Set<StudentEducationalMarkDetailsDBO> markDetails = new HashSet<StudentEducationalMarkDetailsDBO>();
					eduDto.getStudentEducationalMarkDetailS().forEach(educaDto -> {
						StudentEducationalMarkDetailsDBO  marksDetailsDbo = new StudentEducationalMarkDetailsDBO();
						marksDetailsDbo.setStudentEducationalDetailsDBO(edDbo);
						marksDetailsDbo.setId(educaDto.getId());
						marksDetailsDbo.setSemesterName(educaDto.getSemesterName());
						if(!Utils.isNullOrEmpty(educaDto.getDisplayOrder())) {
							marksDetailsDbo.setDisplayOrder(educaDto.getDisplayOrder());
						}
//						marksDetailsDbo.setMaximumMarks(educaDto.getMaximumMarks());
//						marksDetailsDbo.setMarksObtained(educaDto.getMarksObtained());
						if(!Utils.isNullOrEmpty(educaDto.getAdmProgrammeQualificationSubjectEligibilityDTO())) { 
							marksDetailsDbo.setAdmProgrammeQualificationSubjectEligibilityDBO(new AdmProgrammeQualificationSubjectEligibilityDBO());
							marksDetailsDbo.getAdmProgrammeQualificationSubjectEligibilityDBO().setId(Integer.parseInt(educaDto.getAdmProgrammeQualificationSubjectEligibilityDTO().getId()));
						}
						marksDetailsDbo.setModifiedUsersId(userId);
						marksDetailsDbo.setRecordStatus('A');
						markDetails.add(marksDetailsDbo);
					});
					edDbo.setStudentEducationalMarkDetailsDBOSet(markDetails);
				}
				if(!Utils.isNullOrEmpty(eduDto.getStudentEducationalDetailsDocuments())) {
					Set<StudentEducationalDetailsDocumentsDBO> documentList = new HashSet<StudentEducationalDetailsDocumentsDBO>();
					eduDto.getStudentEducationalDetailsDocuments().forEach(documentsDto -> {
						StudentEducationalDetailsDocumentsDBO documentDetailsDbo = new StudentEducationalDetailsDocumentsDBO();
						documentDetailsDbo.setStudentEducationalDetailsDBO(edDbo);
						documentDetailsDbo.setId(documentsDto.getId());
						documentDetailsDbo.setDocumentsUrl(documentsDto.getDocumentsUrl());
						documentDetailsDbo.setModifiedUsersId(userId);
						documentDetailsDbo.setRecordStatus('A');
						documentList.add(documentDetailsDbo);
					});
					edDbo.setStudentEducationalDetailsDocumentsDBOSet(documentList);
				}
				studEducationsDbo.add(edDbo);
			});
			dbo.setStudentEducationalDetailsDBOS(studEducationsDbo);
		}
		if(!Utils.isNullOrEmpty(dto.getApplicationVerificationStatus())) {
			if(dto.getApplicationVerificationStatus().equals("VE")) {
				Tuple verified = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_MARKS_VERIFIED");
				if(!Utils.isNullOrEmpty(verified)) {
					ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
					erpWorkFlowProcessStatusLogDBO.entryId = dto.getId();
					ErpWorkFlowProcessDBO workFlowProcessDBO = new ErpWorkFlowProcessDBO();
					workFlowProcessDBO.setId(Integer.parseInt(verified.get("erp_work_flow_process_id").toString()));
					erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(workFlowProcessDBO);
					erpWorkFlowProcessStatusLogDBO.createdUsersId = userId;
					erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
					statusList.add(erpWorkFlowProcessStatusLogDBO);
					//	dbo.setApplicationCurrentProcessStatus(workFlowProcessDBO);
				}
			} 
			else if(dto.getApplicationVerificationStatus().equals("NV")) {
				Tuple notVerified = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_MARKS_NOT_VERIFIED");
				if(!Utils.isNullOrEmpty(notVerified)) {
					ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
					erpWorkFlowProcessStatusLogDBO.entryId = dto.getId();
					ErpWorkFlowProcessDBO workFlowProcessDBO = new ErpWorkFlowProcessDBO();
					workFlowProcessDBO.setId(Integer.parseInt(notVerified.get("erp_work_flow_process_id").toString()));
					erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(workFlowProcessDBO);
					erpWorkFlowProcessStatusLogDBO.createdUsersId = userId;
					erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
					statusList.add(erpWorkFlowProcessStatusLogDBO);
				}
			}
			else if(dto.getApplicationVerificationStatus().equals("NE")) {
				Tuple notEligible = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_NOT_ELIGIBLE");
				if(!Utils.isNullOrEmpty(notEligible)) {
					ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
					erpWorkFlowProcessStatusLogDBO.entryId = dto.getId();
					ErpWorkFlowProcessDBO workFlowProcessDBO = new ErpWorkFlowProcessDBO();
					workFlowProcessDBO.setId(Integer.parseInt(notEligible.get("erp_work_flow_process_id").toString()));
					erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(workFlowProcessDBO);
					erpWorkFlowProcessStatusLogDBO.createdUsersId = userId;
					erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
					statusList.add(erpWorkFlowProcessStatusLogDBO);	
				}	
			}
			list.addAll(statusList);
		}
		list.add(dbo);
		return list;	
	}	
}
