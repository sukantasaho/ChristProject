package com.christ.erp.services.handlers.employee.recruitment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.RedisAwsConfig;
import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.SysProperties;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpBloodGroupDBO;
import com.christ.erp.services.dbobjects.common.ErpCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpDifferentlyAbledDBO;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.common.ErpMaritalStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpReligionDBO;
import com.christ.erp.services.dbobjects.common.ErpReservationCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dbobjects.common.ErpUniversityBoardDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.common.UrlFolderListDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpAddtnlPersonalDataDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoEntriesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoHeadingDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoParameterDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnEducationalDetailsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnEducationalDetailsDocumentsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnEligibilityTestDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnEligibilityTestDocumentDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewSchedulesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnLocationPrefDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnNonAvailabilityDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnPersonalDataDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnRegistrationsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjSpecializationPrefDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategoryDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategorySpecializationDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnVacancyInformationDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnWorkExperienceDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnWorkExperienceDocumentDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnWorkExperienceTypeDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligibilityExamListDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpFamilyDetailsAddtnlDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpPfGratuityNomineesDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EducationalDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpAddtnlPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoHeadingDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoParameterDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnEducationalDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnEducationalDetailsDocumentsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnEligibilityTestDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnEligibilityTestDocumentDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnLocationPrefDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnNonAvailabilityDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnSubjectCategoryDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnWorkExperienceDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnWorkExperienceDocumentDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpFamilyDetailsAddtnlDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpJobDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpPfGratuityNomineesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmployeeApplicantDTO;
import com.christ.erp.services.dto.employee.recruitment.EmployeeApplicationStatusDTO;
import com.christ.erp.services.dto.employee.recruitment.JobDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.ProfessionalExperienceDTO;
import com.christ.erp.services.dto.employee.recruitment.ResearchDetailsDTO;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.helpers.employee.recruitment.EmployeeApplicationHelper;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.common.CommonEmployeeTransaction;
import com.christ.erp.services.transactions.employee.recruitment.EmployeeApplicationTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
@Service
@SuppressWarnings({"rawtypes","unchecked", "deprecation"})
public class EmployeeApplicationHandler {

	@Autowired
	EmployeeApplicationHelper employeeApplicationHelper;
	@Autowired
	EmployeeApplicationTransaction employeeApplicationTransaction;
	@Autowired
	CommonApiTransaction commonApiTransaction;
	@Autowired
	AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;
	@Autowired
	RedisAwsConfig redisAwsConfig;
	@Autowired
	AWSS3FileStorageService aWSS3FileStorageService;
	@Autowired
	CommonEmployeeTransaction commonEmployeeTransaction;
	@Autowired
	CommonApiHandler commonApiHandler;
	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;

	public void submitEmployeeApplication(EmployeeApplicantDTO employeeApplicantDTO, Integer userId, String failureMessage, String saveMode, ApiResult result) throws Exception {
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				try {
					boolean isSaved = false;
					boolean isValid = false;
					String userIdCommon = redisSysPropertiesData.getSysProperties(SysProperties.COMMON_RECRUITMENT_USER_ID.name(), null, null); 
//					userId = !Utils.isNullOrEmpty(userIdCommon)?  Integer.valueOf(userIdCommon) : userId;
//					if (!Utils.isNullOrEmpty(userIdCommon)) {
//						userId = Integer.valueOf(userIdCommon);
//					}
					if ("save".equalsIgnoreCase(saveMode)) {
						isValid = employeeApplicationHelper.validateApplicantData(employeeApplicantDTO);
					}
					if ("save draft".equalsIgnoreCase(saveMode) || ("save".equalsIgnoreCase(saveMode) && isValid)) {
						List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
						EmpApplnEntriesDBO empApplnEntriesDBO = new EmpApplnEntriesDBO();
						if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnEntriesId)) {
							//empApplnEntriesDBO = commonApiTransaction.find(EmpApplnEntriesDBO.class, employeeApplicantDTO.empApplnEntriesId);
							empApplnEntriesDBO = context.find(EmpApplnEntriesDBO.class, employeeApplicantDTO.empApplnEntriesId);
							empApplnEntriesDBO.modifiedUsersId = userId;
						} else {
							empApplnEntriesDBO.createdUsersId = userId;
						}
						JobDetailsDTO jobDetailDTO = employeeApplicantDTO.jobDetailDTO;
						EmpApplnPersonalDataDTO empApplnPersonalDataDTO = employeeApplicantDTO.empApplnPersonalDataDTO;
						EmpApplnPersonalDataDTO addressDetailDTO = employeeApplicantDTO.addressDetailDTO;
						EducationalDetailsDTO educationalDetailDTO = employeeApplicantDTO.educationalDetailDTO;
						ProfessionalExperienceDTO professionalExperienceDTO = employeeApplicantDTO.professionalExperienceDTO;
						ResearchDetailsDTO researchDetailDTO = employeeApplicantDTO.researchDetailDTO;
						//EmpApplnEntries data
						if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplicationRegistrationId)) {
							empApplnEntriesDBO.empApplnRegistrationsDBO = new EmpApplnRegistrationsDBO();
							empApplnEntriesDBO.empApplnRegistrationsDBO.id = Integer.parseInt(employeeApplicantDTO.empApplicationRegistrationId);
						}
						if (!Utils.isNullOrEmpty(employeeApplicantDTO.getJobCategoryDTO())) {
							empApplnEntriesDBO.setEmpEmployeeJobCategoryDBO(new EmpEmployeeJobCategoryDBO());
							empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO().setId(Integer.parseInt(employeeApplicantDTO.getJobCategoryDTO().getValue()));
						} else {
							empApplnEntriesDBO.setEmpEmployeeJobCategoryDBO(null);
						}
						if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO)) {
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.applicantName))
								empApplnEntriesDBO.applicantName = empApplnPersonalDataDTO.applicantName;
							else
								empApplnEntriesDBO.applicantName = "";
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.genderId)) {
								empApplnEntriesDBO.erpGenderDBO = new ErpGenderDBO();
								empApplnEntriesDBO.erpGenderDBO.erpGenderId = Integer.parseInt(empApplnPersonalDataDTO.genderId);
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.dateOfBirth)) {
								empApplnEntriesDBO.dob = Utils.convertStringDateTimeToLocalDate(empApplnPersonalDataDTO.dateOfBirth);
							}else
								empApplnEntriesDBO.dob = null;
							empApplnEntriesDBO.personalEmailId = empApplnPersonalDataDTO.emailId;
							empApplnEntriesDBO.mobileNoCountryCode = empApplnPersonalDataDTO.mobileNoCountryCode;
							empApplnEntriesDBO.mobileNo = empApplnPersonalDataDTO.mobileNo;
						} else
							empApplnEntriesDBO.applicantName = "";
						if (!Utils.isNullOrEmpty(jobDetailDTO) && !Utils.isNullOrEmpty(jobDetailDTO.postAppliedFor)) {
							empApplnEntriesDBO.empEmployeeCategoryDBO = new EmpEmployeeCategoryDBO();
							empApplnEntriesDBO.empEmployeeCategoryDBO.id = Integer.parseInt(jobDetailDTO.postAppliedFor);
						}
						if (!Utils.isNullOrEmpty(professionalExperienceDTO)) {
							if (!Utils.isNullOrEmpty(professionalExperienceDTO.isCurrentlyWorking)) {
								if ("Yes".equalsIgnoreCase(professionalExperienceDTO.isCurrentlyWorking)) {
									if (!Utils.isNullOrEmpty(professionalExperienceDTO.currentExperience)) {
										empApplnEntriesDBO.isCurrentlyWorking = true;
										empApplnEntriesDBO.noticePeriod = professionalExperienceDTO.currentExperience.noticePeriod;
										empApplnEntriesDBO.currentMonthlySalary = !Utils.isNullOrEmpty(professionalExperienceDTO.currentExperience.currentSalary) ? new BigDecimal(professionalExperienceDTO.currentExperience.currentSalary) : null;
									}
								} else {
									empApplnEntriesDBO.isCurrentlyWorking = false;
									empApplnEntriesDBO.noticePeriod =null;
									empApplnEntriesDBO.currentMonthlySalary =null;
								}
							}
							empApplnEntriesDBO.totalPreviousExperienceYears = !Utils.isNullOrEmpty(professionalExperienceDTO.totalPreviousExperienceYears) && Integer.parseInt(professionalExperienceDTO.totalPreviousExperienceYears) != 0 ? Integer.parseInt(professionalExperienceDTO.totalPreviousExperienceYears) : null;
							empApplnEntriesDBO.totalPreviousExperienceMonths = !Utils.isNullOrEmpty(professionalExperienceDTO.totalPreviousExperienceMonths) && Integer.parseInt(professionalExperienceDTO.totalPreviousExperienceMonths) != 0 ? Integer.parseInt(professionalExperienceDTO.totalPreviousExperienceMonths) : null;
							empApplnEntriesDBO.totalPartTimePreviousExperienceYears = !Utils.isNullOrEmpty(professionalExperienceDTO.totalPartTimePreviousExperienceYears) && Integer.parseInt(professionalExperienceDTO.totalPartTimePreviousExperienceYears) != 0 ? Integer.parseInt(professionalExperienceDTO.totalPartTimePreviousExperienceYears) : null;
							empApplnEntriesDBO.totalPartTimePreviousExperienceMonths = !Utils.isNullOrEmpty(professionalExperienceDTO.totalPartTimePreviousExperienceMonths) && Integer.parseInt(professionalExperienceDTO.totalPartTimePreviousExperienceMonths) != 0 ? Integer.parseInt(professionalExperienceDTO.totalPartTimePreviousExperienceMonths) : null;
							empApplnEntriesDBO.majorAchievements = professionalExperienceDTO.majorAchievements;
							empApplnEntriesDBO.expectedSalary = !Utils.isNullOrEmpty(professionalExperienceDTO.expectedSalary) ? new BigDecimal(professionalExperienceDTO.expectedSalary) : null;
						}
						if (!Utils.isNullOrEmpty(researchDetailDTO)) {
							if (!Utils.isNullOrEmpty(researchDetailDTO.isResearchExperience)) {
								if ("Yes".equalsIgnoreCase(researchDetailDTO.isResearchExperience)) {
									empApplnEntriesDBO.isResearchExperiencePresent = true;
//									empApplnEntriesDBO.inflibnetVidwanNo = !Utils.isNullOrEmpty(researchDetailDTO.inflibnetVidwanNo) ? Integer.parseInt(researchDetailDTO.inflibnetVidwanNo) : null;
//									empApplnEntriesDBO.scopusId = !Utils.isNullOrEmpty(researchDetailDTO.scopusId) ? researchDetailDTO.scopusId : null;
//									empApplnEntriesDBO.hIndex = !Utils.isNullOrEmpty(researchDetailDTO.hIndex) ? Integer.parseInt(researchDetailDTO.hIndex) : null;
								} else {
									empApplnEntriesDBO.isResearchExperiencePresent = false;
//									empApplnEntriesDBO.inflibnetVidwanNo = null;
//									empApplnEntriesDBO.scopusId = null;
//									empApplnEntriesDBO.hIndex = null;
								}
							}
							if (!Utils.isNullOrEmpty(researchDetailDTO.isInterviewedBefore)) {
								if ("Yes".equalsIgnoreCase(researchDetailDTO.isInterviewedBefore)) {
									empApplnEntriesDBO.isInterviewedBefore = true;
									empApplnEntriesDBO.interviewedBeforeDepartment = !Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeDepartment) ? researchDetailDTO.interviewedBeforeDepartment : null;
									empApplnEntriesDBO.interviewedBeforeYear = !Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeYear) ? Integer.parseInt(researchDetailDTO.interviewedBeforeYear) : null;
									empApplnEntriesDBO.interviewedBeforeApplicationNo = !Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeApplicationNo) ? Integer.parseInt(researchDetailDTO.interviewedBeforeApplicationNo) : null;
									empApplnEntriesDBO.interviewedBeforeSubject = !Utils.isNullOrEmpty(researchDetailDTO.interviewedBeforeSubject) ? researchDetailDTO.interviewedBeforeSubject : null;
								} else {
									empApplnEntriesDBO.isInterviewedBefore = false;
									empApplnEntriesDBO.interviewedBeforeDepartment = null;
									empApplnEntriesDBO.interviewedBeforeYear = null;
									empApplnEntriesDBO.interviewedBeforeApplicationNo = null;
									empApplnEntriesDBO.interviewedBeforeSubject = null;
								}
							}
							if (!Utils.isNullOrEmpty(researchDetailDTO.vacancyInformationId)) {
								empApplnEntriesDBO.empApplnVacancyInformationDBO = new EmpApplnVacancyInformationDBO();
								empApplnEntriesDBO.empApplnVacancyInformationDBO.id = Integer.parseInt(researchDetailDTO.vacancyInformationId);
							} else {
								empApplnEntriesDBO.empApplnVacancyInformationDBO =null;
							}
							empApplnEntriesDBO.aboutVacancyOthers = researchDetailDTO.aboutVacancyOthers;
							empApplnEntriesDBO.otherInformation = researchDetailDTO.otherInformation;
						}
						empApplnEntriesDBO.recordStatus = 'A';
						//Job Details
						Set<EmpApplnSubjSpecializationPrefDBO> applnSubjSpecializationPrefDBOs = new HashSet<>();
						Set<Integer> empApplnSubjSpecializationPrefDBOIdSet = new HashSet<>();
						if (!Utils.isNullOrEmpty(jobDetailDTO)) {
							if (!Utils.isNullOrEmpty(jobDetailDTO.empApplnSubjectCategoryDTO)) {
								for (EmpApplnSubjectCategoryDTO empApplnSubjectCategoryDTO : jobDetailDTO.empApplnSubjectCategoryDTO) {
									if (!Utils.isNullOrEmpty(empApplnSubjectCategoryDTO.subjectCategoryId)) {
										EmpApplnSubjSpecializationPrefDBO empApplnSubjSpecializationPrefDBO = new EmpApplnSubjSpecializationPrefDBO();
										if (!Utils.isNullOrEmpty(empApplnSubjectCategoryDTO.empApplnSubjSpecializationPrefId)) {
											empApplnSubjSpecializationPrefDBO = context.find(EmpApplnSubjSpecializationPrefDBO.class, empApplnSubjectCategoryDTO.empApplnSubjSpecializationPrefId);
											//empApplnSubjSpecializationPrefDBO.empApplnSubjSpecializationPrefId = empApplnSubjectCategoryDTO.empApplnSubjSpecializationPrefId;
											empApplnSubjSpecializationPrefDBO.modifiedUsersId = userId;
											empApplnSubjSpecializationPrefDBOIdSet.add(empApplnSubjectCategoryDTO.empApplnSubjSpecializationPrefId);
										} else {
											empApplnSubjSpecializationPrefDBO.createdUsersId = userId;
										}
										empApplnSubjSpecializationPrefDBO.empApplnEntriesDBO = empApplnEntriesDBO;
										empApplnSubjSpecializationPrefDBO.empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
										empApplnSubjSpecializationPrefDBO.empApplnSubjectCategoryDBO.id = empApplnSubjectCategoryDTO.subjectCategoryId;
										if (!Utils.isNullOrEmpty(empApplnSubjectCategoryDTO.subjectCategorySpecializationId)) {
											empApplnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO = new EmpApplnSubjectCategorySpecializationDBO();
											empApplnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId = empApplnSubjectCategoryDTO.subjectCategorySpecializationId;
										} else {
											empApplnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO = null;
										}
										empApplnSubjSpecializationPrefDBO.recordStatus = 'A';
										applnSubjSpecializationPrefDBOs.add(empApplnSubjSpecializationPrefDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnSubjSpecializationPrefDBOs)) {
								for (EmpApplnSubjSpecializationPrefDBO subjSpecializationPrefDBO : empApplnEntriesDBO.empApplnSubjSpecializationPrefDBOs) {
									if (!empApplnSubjSpecializationPrefDBOIdSet.contains(subjSpecializationPrefDBO.empApplnSubjSpecializationPrefId)) {
										subjSpecializationPrefDBO.recordStatus = 'D';
										subjSpecializationPrefDBO.modifiedUsersId = userId;
										applnSubjSpecializationPrefDBOs.add(subjSpecializationPrefDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(applnSubjSpecializationPrefDBOs))
								empApplnEntriesDBO.empApplnSubjSpecializationPrefDBOs = applnSubjSpecializationPrefDBOs;
							Set<EmpApplnLocationPrefDBO> applnLocationPrefDBOs = new HashSet<>();
							Set<Integer> applnLocationPrefDBOsIdSet = new HashSet<>();
							if (!Utils.isNullOrEmpty(jobDetailDTO.preferredLocationIds)) {
								for (EmpApplnLocationPrefDTO applnLocationPrefDTO : jobDetailDTO.preferredLocationIds) {
									EmpApplnLocationPrefDBO applnLocationPrefDBO = new EmpApplnLocationPrefDBO();
									if (!Utils.isNullOrEmpty(applnLocationPrefDTO.empApplnLocationPrefId)) {
										applnLocationPrefDBO = context.find(EmpApplnLocationPrefDBO.class, applnLocationPrefDTO.empApplnLocationPrefId);
										//applnLocationPrefDBO.empApplnLocationPrefId = applnLocationPrefDTO.empApplnLocationPrefId;
										applnLocationPrefDBO.modifiedUsersId = userId;
										applnLocationPrefDBOsIdSet.add(applnLocationPrefDTO.empApplnLocationPrefId);
									} else {
										applnLocationPrefDBO.createdUsersId = userId;
									}
									applnLocationPrefDBO.empApplnEntriesDBO = empApplnEntriesDBO;
									applnLocationPrefDBO.erpLocationDBO = new ErpLocationDBO();
									applnLocationPrefDBO.erpLocationDBO.id = applnLocationPrefDTO.erpLocationId;
									applnLocationPrefDBO.recordStatus = 'A';
									applnLocationPrefDBOs.add(applnLocationPrefDBO);
								}
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnLocationPrefDBOs)) {
								for (EmpApplnLocationPrefDBO empApplnLocationPrefDBO : empApplnEntriesDBO.empApplnLocationPrefDBOs) {
									if (!applnLocationPrefDBOsIdSet.contains(empApplnLocationPrefDBO.empApplnLocationPrefId)) {
										empApplnLocationPrefDBO.recordStatus = 'D';
										empApplnLocationPrefDBO.modifiedUsersId = userId;
										applnLocationPrefDBOs.add(empApplnLocationPrefDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(applnLocationPrefDBOs))
								empApplnEntriesDBO.empApplnLocationPrefDBOs = applnLocationPrefDBOs;
						}
						//Personal Details
						if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO) || !Utils.isNullOrEmpty(addressDetailDTO)) {
							EmpApplnPersonalDataDBO empApplnPersonalDataDBO = new EmpApplnPersonalDataDBO();
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO) && !Utils.isNullOrEmpty(empApplnPersonalDataDTO.empApplnPersonalDataId)) {
								empApplnPersonalDataDBO = context.find(EmpApplnPersonalDataDBO.class, empApplnPersonalDataDTO.empApplnPersonalDataId);
								empApplnPersonalDataDBO.modifiedUsersId = userId;
								//empApplnPersonalDataDBO.empApplnPersonalDataId = empApplnPersonalDataDTO.empApplnPersonalDataId;
							} else if (!Utils.isNullOrEmpty(addressDetailDTO) && !Utils.isNullOrEmpty(addressDetailDTO.empApplnPersonalDataId)) {
								empApplnPersonalDataDBO = context.find(EmpApplnPersonalDataDBO.class, addressDetailDTO.empApplnPersonalDataId);
								empApplnPersonalDataDBO.modifiedUsersId = userId;
								//empApplnPersonalDataDBO.empApplnPersonalDataId = addressDetailDTO.empApplnPersonalDataId;
							} else {
								empApplnPersonalDataDBO.createdUsersId = userId;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO)) {
								empApplnPersonalDataDBO.empApplnEntriesDBO = empApplnEntriesDBO;
								empApplnPersonalDataDBO.fatherName = empApplnPersonalDataDTO.fatherName;
								empApplnPersonalDataDBO.motherName = empApplnPersonalDataDTO.motherName;
								empApplnPersonalDataDBO.alternateNo = empApplnPersonalDataDTO.alternateNo;
								empApplnPersonalDataDBO.aadharNo = empApplnPersonalDataDTO.aadharNo;
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.maritalStatusId)) {
									empApplnPersonalDataDBO.erpMaritalStatusDBO = new ErpMaritalStatusDBO();
									empApplnPersonalDataDBO.erpMaritalStatusDBO.id = Integer.parseInt(empApplnPersonalDataDTO.maritalStatusId);
								}
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.nationalityId)) {
									empApplnPersonalDataDBO.erpCountryDBO = new ErpCountryDBO();
									empApplnPersonalDataDBO.erpCountryDBO.id = Integer.parseInt(empApplnPersonalDataDTO.nationalityId);
								}
								empApplnPersonalDataDBO.passportNo = empApplnPersonalDataDTO.passportNo;
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.religionId)) {
									empApplnPersonalDataDBO.erpReligionDBO = new ErpReligionDBO();
									empApplnPersonalDataDBO.erpReligionDBO.id = Integer.parseInt(empApplnPersonalDataDTO.religionId);
								}
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.isMinority)) {
									empApplnPersonalDataDBO.isMinority = "Yes".equalsIgnoreCase(empApplnPersonalDataDTO.isMinority);
								}
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.reservationCategoryId)) {
									empApplnPersonalDataDBO.erpReservationCategoryDBO = new ErpReservationCategoryDBO();
									empApplnPersonalDataDBO.erpReservationCategoryDBO.id = Integer.parseInt(empApplnPersonalDataDTO.reservationCategoryId);
								}
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.bloodGroupId)) {
									empApplnPersonalDataDBO.erpBloodGroupDBO = new ErpBloodGroupDBO();
									empApplnPersonalDataDBO.erpBloodGroupDBO.id = Integer.parseInt(empApplnPersonalDataDTO.bloodGroupId);
								}
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.isDifferentlyAbled)) {
									if ("Yes".equalsIgnoreCase(empApplnPersonalDataDTO.isDifferentlyAbled)) {
										empApplnPersonalDataDBO.isDifferentlyAbled = true;
										if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.differentlyAbledId)) {
											empApplnPersonalDataDBO.erpDifferentlyAbledDBO = new ErpDifferentlyAbledDBO();
											empApplnPersonalDataDBO.erpDifferentlyAbledDBO.id = Integer.parseInt(empApplnPersonalDataDTO.differentlyAbledId);
										} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.differentlyAbledDetails)) {
											empApplnPersonalDataDBO.differentlyAbledDetails = empApplnPersonalDataDTO.differentlyAbledDetails;
										}
									} else {
										empApplnPersonalDataDBO.isDifferentlyAbled = false;
										empApplnPersonalDataDBO.erpDifferentlyAbledDBO = null;
										empApplnPersonalDataDBO.differentlyAbledDetails = null;
									}
								}
								empApplnPersonalDataDBO.recordStatus = 'A';
								if (!Utils.isNullOrEmpty(researchDetailDTO)) {
									if (!Utils.isNullOrEmpty(researchDetailDTO.isResearchExperience)) {
										if ("Yes".equalsIgnoreCase(researchDetailDTO.isResearchExperience)) {
											empApplnEntriesDBO.isResearchExperiencePresent = true;
											empApplnPersonalDataDBO.setVidwanNo(!Utils.isNullOrEmpty(researchDetailDTO.inflibnetVidwanNo) ? researchDetailDTO.inflibnetVidwanNo : null);
											empApplnPersonalDataDBO.setScopusNo(!Utils.isNullOrEmpty(researchDetailDTO.scopusId) ? researchDetailDTO.scopusId : null);
											empApplnPersonalDataDBO.setHIndexNo(!Utils.isNullOrEmpty(researchDetailDTO.hIndex) ? Integer.parseInt(researchDetailDTO.hIndex) : null);
										} else {
											empApplnEntriesDBO.isResearchExperiencePresent = false;
											empApplnPersonalDataDBO.setVidwanNo(null);
											empApplnPersonalDataDBO.setScopusNo(null);
											empApplnPersonalDataDBO.setHIndexNo(null);
										}
									}
								}
							}
							/*
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.profilePhotoUrl)) {
								empApplnPersonalDataDBO.profilePhotoUrl = empApplnPersonalDataDTO.profilePhotoUrl;
							}*/
							//---------
							UrlAccessLinkDBO documentsUrlDBO = null;
							if(Utils.isNullOrEmpty(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO())){
								if(!Utils.isNullOrEmpty(empApplnPersonalDataDTO.getOriginalFileName())){
									documentsUrlDBO = new UrlAccessLinkDBO();
									documentsUrlDBO.setCreatedUsersId(userId);
									documentsUrlDBO.setRecordStatus('A');
								}
							}else {
								documentsUrlDBO = empApplnPersonalDataDBO.getPhotoDocumentUrlDBO();
								if(Utils.isNullOrEmpty(empApplnPersonalDataDTO.getOriginalFileName())){
									documentsUrlDBO.setRecordStatus('D');
								}
								documentsUrlDBO.setModifiedUsersId(userId);
							}
							if(!Utils.isNullOrEmpty(empApplnPersonalDataDTO.getNewFile()) && empApplnPersonalDataDTO.getNewFile()) {
								//UrlFolderListDBO urlFolderListDBO = folderListDBOMap.get(empApplnPersonalDataDTO.getProcessCode());
								documentsUrlDBO = createURLAccessLinkDBO(documentsUrlDBO, empApplnPersonalDataDTO.getProcessCode(), empApplnPersonalDataDTO.getUniqueFileName(), empApplnPersonalDataDTO.getOriginalFileName(), userId, saveMode);
							}
							if(!Utils.isNullOrEmpty(empApplnPersonalDataDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnPersonalDataDTO.getUniqueFileName())) {
								uniqueFileNameList.addAll(createFileListForActualCopy(empApplnPersonalDataDTO.getProcessCode(), empApplnPersonalDataDTO.getUniqueFileName()));
							}
							empApplnPersonalDataDBO.setPhotoDocumentUrlDBO(documentsUrlDBO);
							//-------
							//Address Details
							if (!Utils.isNullOrEmpty(addressDetailDTO)) {
								empApplnPersonalDataDBO.currentAddressLine1 = addressDetailDTO.currentAddressLine1;
								empApplnPersonalDataDBO.currentAddressLine2 = addressDetailDTO.currentAddressLine2;
								if (!Utils.isNullOrEmpty(addressDetailDTO.currentCountryId)) {
									empApplnPersonalDataDBO.currentCountry = new ErpCountryDBO();
									empApplnPersonalDataDBO.currentCountry.id = Integer.parseInt(addressDetailDTO.currentCountryId);
								}
								if (!Utils.isNullOrEmpty(addressDetailDTO.currentStateId)) {
									empApplnPersonalDataDBO.currentState = new ErpStateDBO();
									empApplnPersonalDataDBO.currentState.id = Integer.parseInt(addressDetailDTO.currentStateId);
									empApplnPersonalDataDBO.setCurrentStateOthers(null) ;
								} else if (!Utils.isNullOrEmpty(addressDetailDTO.currentStateOthers)) {
									empApplnPersonalDataDBO.currentState = null;
									empApplnPersonalDataDBO.currentStateOthers = addressDetailDTO.currentStateOthers;
								}
								if (!Utils.isNullOrEmpty(addressDetailDTO.currentCityId)) {
									empApplnPersonalDataDBO.currentCity = new ErpCityDBO();
									empApplnPersonalDataDBO.currentCity.id = Integer.parseInt(addressDetailDTO.currentCityId);
									empApplnPersonalDataDBO.currentCityOthers = null;
								} else if (!Utils.isNullOrEmpty(addressDetailDTO.currentCityOthers)) {
									empApplnPersonalDataDBO.currentCity = null;
									empApplnPersonalDataDBO.currentCityOthers = addressDetailDTO.currentCityOthers;
								}
								if (Utils.isNullOrEmpty(addressDetailDTO.currentCityOthers) && Utils.isNullOrEmpty(addressDetailDTO.currentCityId)) {
									empApplnPersonalDataDBO.currentCity = null;
									empApplnPersonalDataDBO.currentCityOthers = null;
								}
								if (Utils.isNullOrEmpty(addressDetailDTO.currentStateOthers) && Utils.isNullOrEmpty(addressDetailDTO.currentStateId)) {
									empApplnPersonalDataDBO.setCurrentStateOthers(null) ;
									empApplnPersonalDataDBO.currentState = null;
								}
								empApplnPersonalDataDBO.currentPincode = addressDetailDTO.currentPincode;
								if (!Utils.isNullOrEmpty(addressDetailDTO.isPermanentEqualsCurrent)) {
									empApplnPersonalDataDBO.isPermanentEqualsCurrent = addressDetailDTO.isPermanentEqualsCurrent.equalsIgnoreCase("Yes");
								}
								empApplnPersonalDataDBO.permanentAddressLine1 = addressDetailDTO.permanentAddressLine1;
								empApplnPersonalDataDBO.permanentAddressLine2 = addressDetailDTO.permanentAddressLine2;
								if (!Utils.isNullOrEmpty(addressDetailDTO.permanentCountryId)) {
									empApplnPersonalDataDBO.permanentCountry = new ErpCountryDBO();
									empApplnPersonalDataDBO.permanentCountry.id = Integer.parseInt(addressDetailDTO.permanentCountryId);
								}
								if (!Utils.isNullOrEmpty(addressDetailDTO.permanentStateId)) {
									empApplnPersonalDataDBO.permanentStateOthers = null;
									empApplnPersonalDataDBO.permanentState = new ErpStateDBO();
									empApplnPersonalDataDBO.permanentState.id = Integer.parseInt(addressDetailDTO.permanentStateId);
								} else if (!Utils.isNullOrEmpty(addressDetailDTO.permanentStateOthers)) {
									empApplnPersonalDataDBO.permanentState = null;
									empApplnPersonalDataDBO.permanentStateOthers = addressDetailDTO.permanentStateOthers;
								}
								if (!Utils.isNullOrEmpty(addressDetailDTO.permanentCityId)) {
									empApplnPersonalDataDBO.permanentCityOthers = null;
									empApplnPersonalDataDBO.permanentCity = new ErpCityDBO();
									empApplnPersonalDataDBO.permanentCity.id = Integer.parseInt(addressDetailDTO.permanentCityId);
								} else if (!Utils.isNullOrEmpty(addressDetailDTO.permanentCityOthers)) {
									empApplnPersonalDataDBO.permanentCity = null;
									empApplnPersonalDataDBO.permanentCityOthers = addressDetailDTO.permanentCityOthers;
								}
								if (Utils.isNullOrEmpty(addressDetailDTO.permanentCityId) && Utils.isNullOrEmpty(addressDetailDTO.permanentCityOthers)) {
									empApplnPersonalDataDBO.permanentCityOthers = null;
									empApplnPersonalDataDBO.permanentCity = null;
								}
								if (Utils.isNullOrEmpty(addressDetailDTO.permanentStateOthers) && Utils.isNullOrEmpty(addressDetailDTO.permanentStateId)) {
									empApplnPersonalDataDBO.permanentState = null;
									empApplnPersonalDataDBO.permanentStateOthers = null;
								}
								empApplnPersonalDataDBO.permanentPincode = addressDetailDTO.permanentPincode;
								empApplnPersonalDataDBO.recordStatus = 'A';
							}
							empApplnEntriesDBO.empApplnPersonalDataDBO = empApplnPersonalDataDBO;
						}
						//Educational Details
						Set<EmpApplnEducationalDetailsDBO> applnEducationalDetailsDBOs = new HashSet<>();
						Set<EmpApplnEligibilityTestDBO> applnEligibilityTestDBOs = new HashSet<>();
						Set<Integer> applnEducationalDetailsDBOsIdSet = new HashSet<>();
						Set<Integer> applnEligibilityTestDBOsIdSet = new HashSet<>();
						if (!Utils.isNullOrEmpty(educationalDetailDTO)) {
							//Set<EmpApplnEducationalDetailsDocumentsDBO> documentsDBOSet = new HashSet<>();
							Set<Integer> educationalDetailsDocumentDBOIds = new HashSet<>();
							if (!Utils.isNullOrEmpty(educationalDetailDTO.qualificationLevelsList)) {
								Set<EmpApplnEducationalDetailsDocumentsDBO> documentsDBOSet = new HashSet<>();
								for (EmpApplnEducationalDetailsDTO empApplnEducationalDetailsDTO : educationalDetailDTO.qualificationLevelsList) {
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.qualificationLevelId)) {
										EmpApplnEducationalDetailsDBO empApplnEducationalDetailsDBO = new EmpApplnEducationalDetailsDBO();
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId)) {
											//empApplnEducationalDetailsDBO = commonApiTransaction.find(EmpApplnEducationalDetailsDBO.class, empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
											empApplnEducationalDetailsDBO = context.find(EmpApplnEducationalDetailsDBO.class, empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
											//empApplnEducationalDetailsDBO.empApplnEducationalDetailsId = empApplnEducationalDetailsDTO.empApplnEducationalDetailsId;
											empApplnEducationalDetailsDBO.modifiedUsersId = userId;
											applnEducationalDetailsDBOsIdSet.add(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
										} else {
											empApplnEducationalDetailsDBO.createdUsersId = userId;
										}
										empApplnEducationalDetailsDBO.empApplnEntriesDBO = empApplnEntriesDBO;
										empApplnEducationalDetailsDBO.erpQualificationLevelDBO = new ErpQualificationLevelDBO();
										empApplnEducationalDetailsDBO.erpQualificationLevelDBO.id = Integer.parseInt(empApplnEducationalDetailsDTO.qualificationLevelId);
										empApplnEducationalDetailsDBO.course = empApplnEducationalDetailsDTO.course;
										empApplnEducationalDetailsDBO.specialization = empApplnEducationalDetailsDTO.specialization;
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.yearOfCompletion))
										empApplnEducationalDetailsDBO.yearOfCompletion = Integer.parseInt(empApplnEducationalDetailsDTO.yearOfCompletion);
										empApplnEducationalDetailsDBO.gradeOrPercentage = empApplnEducationalDetailsDTO.gradeOrPercentage;
										empApplnEducationalDetailsDBO.currentStatus = empApplnEducationalDetailsDTO.currentStatus;
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpBoardOrUniversity())) {
											empApplnEducationalDetailsDBO.setErpUniversityBoardDBO(new ErpUniversityBoardDBO());
											empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().setId(Integer.valueOf(empApplnEducationalDetailsDTO.getErpBoardOrUniversity().getValue()));
											empApplnEducationalDetailsDBO.boardOrUniversity = null;
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.boardOrUniversity)){
											empApplnEducationalDetailsDBO.boardOrUniversity = empApplnEducationalDetailsDTO.boardOrUniversity;
											empApplnEducationalDetailsDBO.setErpUniversityBoardDBO(null);
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpInstitute())) {
											empApplnEducationalDetailsDBO.setErpInstitutionDBO(new ErpInstitutionDBO());
											empApplnEducationalDetailsDBO.getErpInstitutionDBO().setId(Integer.valueOf(empApplnEducationalDetailsDTO.getErpInstitute().getValue()));
											empApplnEducationalDetailsDBO.institute = null;
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.institute)){
											empApplnEducationalDetailsDBO.institute = empApplnEducationalDetailsDTO.institute;
											empApplnEducationalDetailsDBO.setErpInstitutionDBO(null);
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getCountryId())) {
											empApplnEducationalDetailsDBO.setErpCountryDBO(new ErpCountryDBO());
											empApplnEducationalDetailsDBO.getErpCountryDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getCountryId()));										
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getStateId())) {
											empApplnEducationalDetailsDBO.setErpStateDBO(new ErpStateDBO());
											empApplnEducationalDetailsDBO.getErpStateDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getStateId()));	
											empApplnEducationalDetailsDBO.setStateOthers(null);
										}	
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getStateOther())) {
											empApplnEducationalDetailsDBO.setStateOthers(empApplnEducationalDetailsDTO.getStateOther());
											empApplnEducationalDetailsDBO.setErpStateDBO(null);
										}
										documentsDBOSet = new HashSet<EmpApplnEducationalDetailsDocumentsDBO>();
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.documentList)) {
											Map<Integer, EmpApplnEducationalDetailsDocumentsDBO> docMap = !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getDocumentsDBOSet())
													?empApplnEducationalDetailsDBO.getDocumentsDBOSet().stream()
															.collect(Collectors.toMap(emp->emp.getId(), emp->emp)):new HashMap<Integer, EmpApplnEducationalDetailsDocumentsDBO>();
									
											for (EmpApplnEducationalDetailsDocumentsDTO empApplnEducationalDetailsDocumentsDTO : empApplnEducationalDetailsDTO.documentList) {
												//
												/*EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO = null;
												
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId)) {
													empApplnEducationalDetailsDocumentsDBO.id = empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId;
													empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
													educationalDetailsDocumentDBOIds.add(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId);
												} else {
													empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
													empApplnEducationalDetailsDocumentsDBO.createdUsersId = userId;
												}
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId)) {
													empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO = new EmpApplnEducationalDetailsDBO();
													empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO.empApplnEducationalDetailsId = empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId;
												} else {
													empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO = empApplnEducationalDetailsDBO;
												}
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl)) {
													empApplnEducationalDetailsDocumentsDBO.educationalDocumentsUrl = empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl;
												}*/
												//-----------
												EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()) 
														&& !Utils.isNullOrEmpty(docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()))){
													empApplnEducationalDetailsDocumentsDBO = docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
													empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
													educationalDetailsDocumentDBOIds.add(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
												} else {
													empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
													empApplnEducationalDetailsDocumentsDBO.setEmpApplnEducationalDetailsDBO(empApplnEducationalDetailsDBO);
													empApplnEducationalDetailsDocumentsDBO.createdUsersId = userId;
												}
												
												UrlAccessLinkDBO documentsUrlDBO;
												if(Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())){
													documentsUrlDBO = new UrlAccessLinkDBO();
													documentsUrlDBO.setCreatedUsersId(userId);
													documentsUrlDBO.setRecordStatus('A');
												}
												else {
													documentsUrlDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
													documentsUrlDBO.setModifiedUsersId(userId);
												}
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getNewFile()) && empApplnEducationalDetailsDocumentsDTO.getNewFile()) {
													documentsUrlDBO = createURLAccessLinkDBO(documentsUrlDBO, empApplnEducationalDetailsDocumentsDTO.getProcessCode(),empApplnEducationalDetailsDocumentsDTO.getUniqueFileName(), empApplnEducationalDetailsDocumentsDTO.getOriginalFileName(), userId, saveMode);
												}
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getUniqueFileName())) {
													uniqueFileNameList.addAll(createFileListForActualCopy(empApplnEducationalDetailsDocumentsDTO.getProcessCode(), empApplnEducationalDetailsDocumentsDTO.getUniqueFileName()));
												}
												
												empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(documentsUrlDBO);
												//------------
												empApplnEducationalDetailsDocumentsDBO.recordStatus = 'A';
												documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
											}
										}
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.documentsDBOSet)) {
											for (EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO : empApplnEducationalDetailsDBO.documentsDBOSet) {
												if (!educationalDetailsDocumentDBOIds.contains(empApplnEducationalDetailsDocumentsDBO.id)) {
													if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())) {
														UrlAccessLinkDBO urlAccessLinkDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
														if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
															urlAccessLinkDBO.setRecordStatus('D');
															urlAccessLinkDBO.setModifiedUsersId(userId);
															empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(urlAccessLinkDBO);
														}
													}
													empApplnEducationalDetailsDocumentsDBO.recordStatus = 'D';
													empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
													documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
												}
											}
										}
										empApplnEducationalDetailsDBO.documentsDBOSet = documentsDBOSet;
										empApplnEducationalDetailsDBO.recordStatus = 'A';
										applnEducationalDetailsDBOs.add(empApplnEducationalDetailsDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(educationalDetailDTO.eligibilityTestList)) {
								for (EmpApplnEligibilityTestDTO empApplnEligibilityTestDTO : educationalDetailDTO.eligibilityTestList) {
									if (!Utils.isNullOrEmpty(empApplnEligibilityTestDTO.eligibilityTestId)) {
										EmpApplnEligibilityTestDBO empApplnEligibilityTestDBO = new EmpApplnEligibilityTestDBO();
										if (!Utils.isNullOrEmpty(empApplnEligibilityTestDTO.empApplnEligibilityTestId)) {
											//empApplnEligibilityTestDBO = commonApiTransaction.find(EmpApplnEligibilityTestDBO.class, empApplnEligibilityTestDTO.empApplnEligibilityTestId);
											empApplnEligibilityTestDBO = context.find(EmpApplnEligibilityTestDBO.class, empApplnEligibilityTestDTO.empApplnEligibilityTestId);
											//empApplnEligibilityTestDBO.empApplnEligibilityTestId = empApplnEligibilityTestDTO.empApplnEligibilityTestId;
											empApplnEligibilityTestDBO.modifiedUsersId = userId;
											applnEligibilityTestDBOsIdSet.add(empApplnEligibilityTestDTO.empApplnEligibilityTestId);
										} else {
											empApplnEligibilityTestDBO.createdUsersId = userId;
										}
										empApplnEligibilityTestDBO.empApplnEntriesDBO = empApplnEntriesDBO;
										empApplnEligibilityTestDBO.empEligibilityExamListDBO = new EmpEligibilityExamListDBO();
										if (!Utils.isNullOrEmpty(empApplnEligibilityTestDTO.eligibilityTestId)) {
											empApplnEligibilityTestDBO.empEligibilityExamListDBO.empEligibilityExamListId = Integer.parseInt(empApplnEligibilityTestDTO.eligibilityTestId);									
										}
										if (!Utils.isNullOrEmpty(empApplnEligibilityTestDTO.testYear)) {
											empApplnEligibilityTestDBO.testYear = Integer.parseInt(empApplnEligibilityTestDTO.testYear);
										}
										Set<EmpApplnEligibilityTestDocumentDBO> eligibilityTestDocumentDBOSet = new HashSet<>();
										Set<Integer> eligibilityTestDocumentDBOIds = new HashSet<>();
										if (!Utils.isNullOrEmpty(empApplnEligibilityTestDTO.eligibilityTestDocumentsList)) {
											Map<Integer, EmpApplnEligibilityTestDocumentDBO> eligibilityDocMap = !Utils.isNullOrEmpty(empApplnEligibilityTestDBO.getEligibilityTestDocumentDBOSet())
													?empApplnEligibilityTestDBO.getEligibilityTestDocumentDBOSet().stream()
															.collect(Collectors.toMap(elig->elig.getId(), elig->elig)):new HashMap<Integer, EmpApplnEligibilityTestDocumentDBO>();
											
											for (EmpApplnEligibilityTestDocumentDTO empApplnEligibilityTestDocumentDTO : empApplnEligibilityTestDTO.eligibilityTestDocumentsList) {
												
												//-----
												/*
												if (!Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId)) {
													eligibilityTestDocumentDBO.id = empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId;
													eligibilityTestDocumentDBO.modifiedUsersId = userId;
													eligibilityTestDocumentDBOIds.add(empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId);
												} else {
													eligibilityTestDocumentDBO = new EmpApplnEligibilityTestDocumentDBO();
													eligibilityTestDocumentDBO.createdUsersId = userId;
												}
												if (!Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestId)) {
													eligibilityTestDocumentDBO.empApplnEligibilityTestDBO = new EmpApplnEligibilityTestDBO();
													eligibilityTestDocumentDBO.empApplnEligibilityTestDBO.empApplnEligibilityTestId = empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestId;
												} else {
													eligibilityTestDocumentDBO.empApplnEligibilityTestDBO = empApplnEligibilityTestDBO;
												}*/ 
												/*
												if (!Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDTO.eligibilityDocumentUrl)) {
													eligibilityTestDocumentDBO.eligibilityDocumentUrl = empApplnEligibilityTestDocumentDTO.eligibilityDocumentUrl;
												}
												*/
												
												//-----
												EmpApplnEligibilityTestDocumentDBO eligibilityTestDocumentDBO = new EmpApplnEligibilityTestDocumentDBO();
												if(!Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId) 
														&& !Utils.isNullOrEmpty(eligibilityDocMap.get(empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId))){
													eligibilityTestDocumentDBO = eligibilityDocMap.get(empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId);
													eligibilityTestDocumentDBO.modifiedUsersId = userId;
													eligibilityTestDocumentDBOIds.add(empApplnEligibilityTestDocumentDTO.empApplnEligibilityTestDocumentId);
												} else {
													eligibilityTestDocumentDBO = new EmpApplnEligibilityTestDocumentDBO();
													eligibilityTestDocumentDBO.setEmpApplnEligibilityTestDBO(empApplnEligibilityTestDBO);
													eligibilityTestDocumentDBO.createdUsersId = userId;
													eligibilityTestDocumentDBO.modifiedUsersId = userId;
												}
												UrlAccessLinkDBO eligibilityDocumentsUrlDBO;
												if(Utils.isNullOrEmpty(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO())){
													eligibilityDocumentsUrlDBO = new UrlAccessLinkDBO();
													eligibilityDocumentsUrlDBO.setCreatedUsersId(userId);
													eligibilityDocumentsUrlDBO.setRecordStatus('A');
												}
												else {
													eligibilityDocumentsUrlDBO = eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO();
													eligibilityDocumentsUrlDBO.setModifiedUsersId(userId);
												}
												if(!Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDTO.getNewFile()) && empApplnEligibilityTestDocumentDTO.getNewFile()) {
													eligibilityDocumentsUrlDBO = createURLAccessLinkDBO(eligibilityDocumentsUrlDBO, empApplnEligibilityTestDocumentDTO.getProcessCode(),empApplnEligibilityTestDocumentDTO.getUniqueFileName(), empApplnEligibilityTestDocumentDTO.getOriginalFileName(), userId, saveMode);
												}
												if(!Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDTO.getUniqueFileName())) {
													uniqueFileNameList.addAll(createFileListForActualCopy(empApplnEligibilityTestDocumentDTO.getProcessCode(), empApplnEligibilityTestDocumentDTO.getUniqueFileName()));
												}

												eligibilityTestDocumentDBO.setEligibilityDocumentUrlDBO(eligibilityDocumentsUrlDBO);
												//----
												eligibilityTestDocumentDBO.recordStatus = 'A';
												eligibilityTestDocumentDBOSet.add(eligibilityTestDocumentDBO);
											}
										}
										if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.eligibilityTestDocumentDBOSet)) {
											for (EmpApplnEligibilityTestDocumentDBO eligibilityTestDocumentDBO : empApplnEligibilityTestDBO.eligibilityTestDocumentDBOSet) {
												if (!eligibilityTestDocumentDBOIds.contains(eligibilityTestDocumentDBO.id)) {
													eligibilityTestDocumentDBO.recordStatus = 'D';
													eligibilityTestDocumentDBO.modifiedUsersId = userId;
													if(!Utils.isNullOrEmpty(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO())) {
														UrlAccessLinkDBO urlAccessLinkDBO = eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO();
														if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
															urlAccessLinkDBO.setRecordStatus('D');
															urlAccessLinkDBO.setModifiedUsersId(userId);
														}
														eligibilityTestDocumentDBO.setEligibilityDocumentUrlDBO(urlAccessLinkDBO);
													}
													eligibilityTestDocumentDBOSet.add(eligibilityTestDocumentDBO);
												}
											}
										}
										empApplnEligibilityTestDBO.eligibilityTestDocumentDBOSet = eligibilityTestDocumentDBOSet;
										empApplnEligibilityTestDBO.recordStatus = 'A';
										applnEligibilityTestDBOs.add(empApplnEligibilityTestDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(educationalDetailDTO.otherQualificationLevelsList)) {
								for (EmpApplnEducationalDetailsDTO empApplnEducationalDetailsDTO : educationalDetailDTO.otherQualificationLevelsList) {
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.qualificationOthers)) {
										Set<EmpApplnEducationalDetailsDocumentsDBO> documentsDBOSet = new HashSet<>();
										EmpApplnEducationalDetailsDBO empApplnEducationalDetailsDBO = new EmpApplnEducationalDetailsDBO();
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId)) {
											//empApplnEducationalDetailsDBO = commonApiTransaction.find(EmpApplnEducationalDetailsDBO.class, empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
											empApplnEducationalDetailsDBO = context.find(EmpApplnEducationalDetailsDBO.class, empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
											//empApplnEducationalDetailsDBO.empApplnEducationalDetailsId = empApplnEducationalDetailsDTO.empApplnEducationalDetailsId;
											empApplnEducationalDetailsDBO.modifiedUsersId = userId;
											applnEducationalDetailsDBOsIdSet.add(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
										} else {
											empApplnEducationalDetailsDBO.createdUsersId = userId;
										}
										empApplnEducationalDetailsDBO.empApplnEntriesDBO = empApplnEntriesDBO;
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.qualificationOthers))
											empApplnEducationalDetailsDBO.qualificationOthers = empApplnEducationalDetailsDTO.qualificationOthers;
										empApplnEducationalDetailsDBO.course = empApplnEducationalDetailsDTO.course;
										empApplnEducationalDetailsDBO.specialization = empApplnEducationalDetailsDTO.specialization;
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.yearOfCompletion))
											empApplnEducationalDetailsDBO.yearOfCompletion = Integer.parseInt(empApplnEducationalDetailsDTO.yearOfCompletion);
										empApplnEducationalDetailsDBO.gradeOrPercentage = empApplnEducationalDetailsDTO.gradeOrPercentage;
										empApplnEducationalDetailsDBO.currentStatus = empApplnEducationalDetailsDTO.currentStatus;
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpInstitute())) {
											empApplnEducationalDetailsDBO.setErpInstitutionDBO(new ErpInstitutionDBO());
											empApplnEducationalDetailsDBO.getErpInstitutionDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getErpInstitute().getValue()));
											empApplnEducationalDetailsDBO.institute = null;
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.institute)){
											empApplnEducationalDetailsDBO.institute = empApplnEducationalDetailsDTO.institute;
											empApplnEducationalDetailsDBO.setErpInstitutionDBO(null);
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpBoardOrUniversity())) {
											empApplnEducationalDetailsDBO.setErpUniversityBoardDBO(new ErpUniversityBoardDBO());
											empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getErpBoardOrUniversity().getValue()));
											empApplnEducationalDetailsDBO.boardOrUniversity = null;
										}
										if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.boardOrUniversity)){
											empApplnEducationalDetailsDBO.boardOrUniversity = empApplnEducationalDetailsDTO.boardOrUniversity;
											empApplnEducationalDetailsDBO.setErpUniversityBoardDBO(null);
										}
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.documentList)) {
											Map<Integer, EmpApplnEducationalDetailsDocumentsDBO> docMap = !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getDocumentsDBOSet())
													?empApplnEducationalDetailsDBO.getDocumentsDBOSet().stream()
															.collect(Collectors.toMap(emp->emp.getId(), emp->emp)):new HashMap<Integer, EmpApplnEducationalDetailsDocumentsDBO>();
			
											for (EmpApplnEducationalDetailsDocumentsDTO empApplnEducationalDetailsDocumentsDTO : empApplnEducationalDetailsDTO.documentList) {
												/*
												EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO = null;
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId)) {
													empApplnEducationalDetailsDocumentsDBO.id = empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId;
													empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
													educationalDetailsDocumentDBOIds.add(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId);
												} else {
													empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
													empApplnEducationalDetailsDocumentsDBO.createdUsersId = userId;
												}
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId)) {
													empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO = new EmpApplnEducationalDetailsDBO();
													empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO.empApplnEducationalDetailsId = empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId;
												} else {
													empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO = empApplnEducationalDetailsDBO;
												}
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl)) {
													empApplnEducationalDetailsDocumentsDBO.educationalDocumentsUrl = empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl;
												}
												*/
												//---------
												EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()) 
														&& !Utils.isNullOrEmpty(docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()))){
													empApplnEducationalDetailsDocumentsDBO = docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
													empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
													educationalDetailsDocumentDBOIds.add(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
												} else {
													empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
													empApplnEducationalDetailsDocumentsDBO.setEmpApplnEducationalDetailsDBO(empApplnEducationalDetailsDBO);
													empApplnEducationalDetailsDocumentsDBO.createdUsersId = userId;
													empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
												}
												
												UrlAccessLinkDBO documentsUrlDBO;
												if(Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())){
													documentsUrlDBO = new UrlAccessLinkDBO();
													documentsUrlDBO.setCreatedUsersId(userId);
													documentsUrlDBO.setRecordStatus('A');
												}else {
													documentsUrlDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
													documentsUrlDBO.setModifiedUsersId(userId);
													documentsUrlDBO.setRecordStatus('A');
												}
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getNewFile()) && empApplnEducationalDetailsDocumentsDTO.getNewFile()) {
													documentsUrlDBO = createURLAccessLinkDBO(documentsUrlDBO, empApplnEducationalDetailsDocumentsDTO.getProcessCode(),empApplnEducationalDetailsDocumentsDTO.getUniqueFileName(), empApplnEducationalDetailsDocumentsDTO.getOriginalFileName(), userId, saveMode);
												}
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getUniqueFileName())) {
													uniqueFileNameList.addAll(createFileListForActualCopy(empApplnEducationalDetailsDocumentsDTO.getProcessCode(), empApplnEducationalDetailsDocumentsDTO.getUniqueFileName()));
												}

												empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(documentsUrlDBO);
												//----------
												
												empApplnEducationalDetailsDocumentsDBO.recordStatus = 'A';
												documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
											}
										}
										if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.documentsDBOSet)) {
											for (EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO : empApplnEducationalDetailsDBO.documentsDBOSet) {
												if (!educationalDetailsDocumentDBOIds.contains(empApplnEducationalDetailsDocumentsDBO.id)) {
													empApplnEducationalDetailsDocumentsDBO.recordStatus = 'D';
													empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
													if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())) {
														UrlAccessLinkDBO urlAccessLinkDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
														if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
															urlAccessLinkDBO.setRecordStatus('D');
															urlAccessLinkDBO.setModifiedUsersId(userId);
														}
														empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(urlAccessLinkDBO);
													}
													documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
												}
											}
										}
										empApplnEducationalDetailsDBO.documentsDBOSet = documentsDBOSet;
										empApplnEducationalDetailsDBO.recordStatus = 'A';
										applnEducationalDetailsDBOs.add(empApplnEducationalDetailsDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnEducationalDetailsDBOs)) {
								for (EmpApplnEducationalDetailsDBO educationalDetailsDBO : empApplnEntriesDBO.empApplnEducationalDetailsDBOs) {
									if (!applnEducationalDetailsDBOsIdSet.contains(educationalDetailsDBO.empApplnEducationalDetailsId)) {
										if (!Utils.isNullOrEmpty(educationalDetailsDBO.documentsDBOSet)) {
											Set<EmpApplnEducationalDetailsDocumentsDBO> empApplnEducationalDetailsDocumentsDBOSet = new HashSet<>();
											for (EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO : educationalDetailsDBO.documentsDBOSet) {
												empApplnEducationalDetailsDocumentsDBO.recordStatus = 'D';
												empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())) {
													UrlAccessLinkDBO urlAccessLinkDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
													if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
														urlAccessLinkDBO.setRecordStatus('D');
														urlAccessLinkDBO.setModifiedUsersId(userId);
													}
													empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(urlAccessLinkDBO);
												}
												empApplnEducationalDetailsDocumentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
											}
											educationalDetailsDBO.documentsDBOSet = empApplnEducationalDetailsDocumentsDBOSet;
										}
										educationalDetailsDBO.recordStatus = 'D';
										educationalDetailsDBO.modifiedUsersId = userId;
										applnEducationalDetailsDBOs.add(educationalDetailsDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnEligibilityTestDBOs)) {
								for (EmpApplnEligibilityTestDBO eligibilityTestDBO : empApplnEntriesDBO.empApplnEligibilityTestDBOs) {
									if (!applnEligibilityTestDBOsIdSet.contains(eligibilityTestDBO.empApplnEligibilityTestId)) {
										if (!Utils.isNullOrEmpty(eligibilityTestDBO.eligibilityTestDocumentDBOSet)) {
											Set<EmpApplnEligibilityTestDocumentDBO> empApplnEligibilityTestDocumentDBOSSet = new HashSet<>();
											for (EmpApplnEligibilityTestDocumentDBO empApplnEligibilityTestDocumentDBO : eligibilityTestDBO.eligibilityTestDocumentDBOSet) {
												empApplnEligibilityTestDocumentDBO.recordStatus = 'D';
												empApplnEligibilityTestDocumentDBO.modifiedUsersId = userId;
												if(!Utils.isNullOrEmpty(empApplnEligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO())) {
													UrlAccessLinkDBO urlAccessLinkDBO = empApplnEligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO();
													if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
														urlAccessLinkDBO.setRecordStatus('D');
														urlAccessLinkDBO.setModifiedUsersId(userId);
													}
													empApplnEligibilityTestDocumentDBO.setEligibilityDocumentUrlDBO(urlAccessLinkDBO);
												}
												empApplnEligibilityTestDocumentDBOSSet.add(empApplnEligibilityTestDocumentDBO);
											}
											eligibilityTestDBO.eligibilityTestDocumentDBOSet = empApplnEligibilityTestDocumentDBOSSet;
										}
										eligibilityTestDBO.recordStatus = 'D';
										eligibilityTestDBO.modifiedUsersId = userId;
										applnEligibilityTestDBOs.add(eligibilityTestDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(applnEducationalDetailsDBOs)) {
								empApplnEntriesDBO.empApplnEducationalDetailsDBOs = applnEducationalDetailsDBOs;
								if (!Utils.isNullOrEmpty(educationalDetailDTO.highestQualificationLevelId)) {
									empApplnEntriesDBO.highestQualificationLevel = Integer.parseInt(educationalDetailDTO.highestQualificationLevelId);
								}
							}
							if (!Utils.isNullOrEmpty(applnEligibilityTestDBOs))
								empApplnEntriesDBO.empApplnEligibilityTestDBOs = applnEligibilityTestDBOs;
						}
						//Professional Experience
						if (!Utils.isNullOrEmpty(professionalExperienceDTO)) {
							Set<EmpApplnWorkExperienceDBO> professionalExperienceSet = new HashSet<>();
							Set<Integer> professionalExperienceIdsSet = new HashSet<>();
							Set<Integer> workExperienceDocumentDBOIds = new HashSet<>();
							if (!Utils.isNullOrEmpty(professionalExperienceDTO.isCurrentlyWorking) && "Yes".equalsIgnoreCase(professionalExperienceDTO.isCurrentlyWorking)) {
								if (!Utils.isNullOrEmpty(professionalExperienceDTO.currentExperience)) {
									Set<EmpApplnWorkExperienceDocumentDBO> workExperienceDocumentDBOSet = new HashSet<>();
									EmpApplnWorkExperienceDBO empApplnWorkExperienceDBO = new EmpApplnWorkExperienceDBO();
									empApplnWorkExperienceDBO.empApplnEntriesDBO = empApplnEntriesDBO;
									EmpApplnWorkExperienceDTO currentExperience = professionalExperienceDTO.currentExperience;
									if (!Utils.isNullOrEmpty(currentExperience.empApplnWorkExperienceId)) {
										//empApplnWorkExperienceDBO = commonApiTransaction.find(EmpApplnWorkExperienceDBO.class, currentExperience.empApplnWorkExperienceId);
										empApplnWorkExperienceDBO = context.find(EmpApplnWorkExperienceDBO.class, currentExperience.empApplnWorkExperienceId);
										//empApplnWorkExperienceDBO.empApplnWorkExperienceId = currentExperience.empApplnWorkExperienceId;
										empApplnWorkExperienceDBO.modifiedUsersId = userId;
										professionalExperienceIdsSet.add(currentExperience.empApplnWorkExperienceId);
									} else {
										empApplnWorkExperienceDBO.createdUsersId = userId;
									}
									empApplnWorkExperienceDBO.isCurrentExperience = true;
									if (!Utils.isNullOrEmpty(currentExperience.workExperienceTypeId)) {
										empApplnWorkExperienceDBO.empApplnWorkExperienceTypeDBO = new EmpApplnWorkExperienceTypeDBO();
										empApplnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId = Integer.parseInt(currentExperience.workExperienceTypeId);
									}
									if (!Utils.isNullOrEmpty(currentExperience.functionalAreaId)) {
										empApplnWorkExperienceDBO.empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
										empApplnWorkExperienceDBO.empApplnSubjectCategoryDBO.id = Integer.parseInt(currentExperience.functionalAreaId);
										empApplnWorkExperienceDBO.functionalAreaOthers = null;
									} else if (!Utils.isNullOrEmpty(currentExperience.functionalAreaOthers)) {
										empApplnWorkExperienceDBO.functionalAreaOthers = currentExperience.functionalAreaOthers;
										empApplnWorkExperienceDBO.empApplnSubjectCategoryDBO = null;
									}
									if (!Utils.isNullOrEmpty(currentExperience.employmentType))
										empApplnWorkExperienceDBO.isPartTime = "parttime".equalsIgnoreCase(currentExperience.employmentType);
									empApplnWorkExperienceDBO.empDesignation = currentExperience.designation;
									if (!Utils.isNullOrEmpty(currentExperience.getFromDate())) {
										LocalDate fromDate = currentExperience.getFromDate();
										empApplnWorkExperienceDBO.workExperienceFromDate = fromDate;
									}
									/*if (!Utils.isNullOrEmpty(currentExperience.toDate)) {
										Date toDate;
										toDate = inputFormat.parse(currentExperience.toDate);
										String formattedDate = dateFormat.format(toDate);
										empApplnWorkExperienceDBO.workExperienceToDate = new Date(formattedDate);
									}*/
									empApplnWorkExperienceDBO.workExperienceToDate = LocalDate.now();
									if (!Utils.isNullOrEmpty(currentExperience.years))
										empApplnWorkExperienceDBO.workExperienceYears = Integer.parseInt(currentExperience.years);
									if (!Utils.isNullOrEmpty(currentExperience.months))
										empApplnWorkExperienceDBO.workExperienceMonth = Integer.parseInt(currentExperience.months);
									empApplnEntriesDBO.noticePeriod = currentExperience.noticePeriod;
									empApplnWorkExperienceDBO.institution = currentExperience.institution;
									if (!Utils.isNullOrEmpty(currentExperience.experienceDocumentList)) {
										Map<Integer, EmpApplnWorkExperienceDocumentDBO> docMap = !Utils.isNullOrEmpty(empApplnWorkExperienceDBO.getWorkExperienceDocumentsDBOSet())
												?empApplnWorkExperienceDBO.getWorkExperienceDocumentsDBOSet().stream()
														.collect(Collectors.toMap(wrk->wrk.getId(), wrk->wrk)):new HashMap<Integer, EmpApplnWorkExperienceDocumentDBO>();
										for (EmpApplnWorkExperienceDocumentDTO empApplnWorkExperienceDocumentDTO : currentExperience.experienceDocumentList) {
												
											/*EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO = null;
											if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId)) {
												empApplnWorkExperienceDocumentDBO.id = empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId;
												empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
												workExperienceDocumentDBOIds.add(empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId);
											} else {
												empApplnWorkExperienceDocumentDBO = new EmpApplnWorkExperienceDocumentDBO();
												empApplnWorkExperienceDocumentDBO.createdUsersId = userId;
											}
											if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceId)) {
												empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO = new EmpApplnWorkExperienceDBO();
												empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO.empApplnWorkExperienceId = empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceId;
											} else {
												empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO = empApplnWorkExperienceDBO;
											}
											if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.experienceDocumentsUrl)) {
												empApplnWorkExperienceDocumentDBO.experienceDocumentsUrl = empApplnWorkExperienceDocumentDTO.experienceDocumentsUrl;
											}*/
											
											//--
											EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO = new EmpApplnWorkExperienceDocumentDBO();
											if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId()) &&
													!Utils.isNullOrEmpty(docMap.get(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId()))){
												empApplnWorkExperienceDocumentDBO = docMap.get(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId());
												empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
												workExperienceDocumentDBOIds.add(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId());
											} else {
												empApplnWorkExperienceDocumentDBO = new EmpApplnWorkExperienceDocumentDBO();
												empApplnWorkExperienceDocumentDBO.setEmpApplnWorkExperienceDBO(empApplnWorkExperienceDBO);
												empApplnWorkExperienceDocumentDBO.createdUsersId = userId;
												empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
											}
											
											UrlAccessLinkDBO documentsUrlDBO;
											if(Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())){
												documentsUrlDBO = new UrlAccessLinkDBO();
												documentsUrlDBO.setCreatedUsersId(userId);
												documentsUrlDBO.setRecordStatus('A');											}
											else {
												documentsUrlDBO = empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO();
												documentsUrlDBO.setModifiedUsersId(userId);
												documentsUrlDBO.setRecordStatus('A');
											}
											if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getNewFile()) &&  empApplnWorkExperienceDocumentDTO.getNewFile()) {
												documentsUrlDBO = createURLAccessLinkDBO(documentsUrlDBO, empApplnWorkExperienceDocumentDTO.getProcessCode(),empApplnWorkExperienceDocumentDTO.getUniqueFileName(), empApplnWorkExperienceDocumentDTO.getOriginalFileName(), userId, saveMode);
												empApplnWorkExperienceDocumentDBO.setExperienceDocumentsUrlDBO(documentsUrlDBO);
											}
											if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getUniqueFileName())) {
										        uniqueFileNameList.addAll(createFileListForActualCopy(empApplnWorkExperienceDocumentDTO.getProcessCode(), empApplnWorkExperienceDocumentDTO.getUniqueFileName()));
											}

											//------------
											
											empApplnWorkExperienceDocumentDBO.recordStatus = 'A';
											workExperienceDocumentDBOSet.add(empApplnWorkExperienceDocumentDBO);
										}
									}
									if (!Utils.isNullOrEmpty(empApplnWorkExperienceDBO.workExperienceDocumentsDBOSet)) {
										for (EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO : empApplnWorkExperienceDBO.workExperienceDocumentsDBOSet) {
											if (!workExperienceDocumentDBOIds.contains(empApplnWorkExperienceDocumentDBO.id)) {
												empApplnWorkExperienceDocumentDBO.recordStatus = 'D';
												empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
												if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
													UrlAccessLinkDBO urlAccessLinkDBO = empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO();
													if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
														urlAccessLinkDBO.setRecordStatus('D');
														urlAccessLinkDBO.setModifiedUsersId(userId);
													}
													empApplnWorkExperienceDocumentDBO.setEmpApplnWorkExperienceDBO(empApplnWorkExperienceDBO);
												}
												workExperienceDocumentDBOSet.add(empApplnWorkExperienceDocumentDBO);
											}
										}
									}
									empApplnWorkExperienceDBO.workExperienceDocumentsDBOSet = workExperienceDocumentDBOSet;
									empApplnWorkExperienceDBO.recordStatus = 'A';
									professionalExperienceSet.add(empApplnWorkExperienceDBO);
								}
							}
							if (!Utils.isNullOrEmpty(professionalExperienceDTO.professionalExperienceList)) {
								for (EmpApplnWorkExperienceDTO empApplnWorkExperienceDTO : professionalExperienceDTO.professionalExperienceList) {
									if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.functionalAreaId) || !Utils.isNullOrEmpty(empApplnWorkExperienceDTO.functionalAreaOthers)) {
										Set<EmpApplnWorkExperienceDocumentDBO> workExperienceDocumentDBOSet = new HashSet<>();
										EmpApplnWorkExperienceDBO empApplnWorkExperienceDBO = new EmpApplnWorkExperienceDBO();
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.empApplnWorkExperienceId)) {
											//empApplnWorkExperienceDBO = commonApiTransaction.find(EmpApplnWorkExperienceDBO.class, empApplnWorkExperienceDTO.empApplnWorkExperienceId);
											empApplnWorkExperienceDBO = context.find(EmpApplnWorkExperienceDBO.class, empApplnWorkExperienceDTO.empApplnWorkExperienceId);
											//empApplnWorkExperienceDBO.empApplnWorkExperienceId = empApplnWorkExperienceDTO.empApplnWorkExperienceId;
											empApplnWorkExperienceDBO.modifiedUsersId = userId;
											professionalExperienceIdsSet.add(empApplnWorkExperienceDTO.empApplnWorkExperienceId);
										} else {
											empApplnWorkExperienceDBO.createdUsersId = userId;
										}
										empApplnWorkExperienceDBO.empApplnEntriesDBO = empApplnEntriesDBO;
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.workExperienceTypeId)) {
											empApplnWorkExperienceDBO.empApplnWorkExperienceTypeDBO = new EmpApplnWorkExperienceTypeDBO();
											empApplnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.empApplnWorkExperienceTypeId = Integer.parseInt(empApplnWorkExperienceDTO.workExperienceTypeId);
										}
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.functionalAreaId)) {
											empApplnWorkExperienceDBO.empApplnSubjectCategoryDBO = new EmpApplnSubjectCategoryDBO();
											empApplnWorkExperienceDBO.empApplnSubjectCategoryDBO.id = Integer.parseInt(empApplnWorkExperienceDTO.functionalAreaId);
											empApplnWorkExperienceDBO.functionalAreaOthers = null;
										} else if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.functionalAreaOthers)) {
											empApplnWorkExperienceDBO.functionalAreaOthers = empApplnWorkExperienceDTO.functionalAreaOthers;
											empApplnWorkExperienceDBO.empApplnSubjectCategoryDBO = null;
										}
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.employmentType))
											empApplnWorkExperienceDBO.isPartTime = "parttime".equalsIgnoreCase(empApplnWorkExperienceDTO.employmentType);
										empApplnWorkExperienceDBO.empDesignation = empApplnWorkExperienceDTO.designation;
//										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.fromDate)) {
//											LocalDate fromDate = Utils.convertStringDateTimeToLocalDate(empApplnWorkExperienceDTO.fromDate);
//											empApplnWorkExperienceDBO.workExperienceFromDate = fromDate;
//										}
//										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.toDate)) {
//											LocalDate toDate = Utils.convertStringDateTimeToLocalDate(empApplnWorkExperienceDTO.toDate);
//											empApplnWorkExperienceDBO.workExperienceToDate = toDate;
//										}
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.getFromDate())) {
											LocalDate fromDate = empApplnWorkExperienceDTO.getFromDate();
											empApplnWorkExperienceDBO.workExperienceFromDate = fromDate;
										}
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.getToDate())) {
											LocalDate toDate = empApplnWorkExperienceDTO.getToDate();
											empApplnWorkExperienceDBO.workExperienceToDate = toDate;
										}
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.years))
											empApplnWorkExperienceDBO.workExperienceYears = Integer.parseInt(empApplnWorkExperienceDTO.years.trim());
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.months))
											empApplnWorkExperienceDBO.workExperienceMonth = Integer.parseInt(empApplnWorkExperienceDTO.months.trim());
										empApplnWorkExperienceDBO.institution = empApplnWorkExperienceDTO.institution;
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDTO.experienceDocumentList)) {
											Map<Integer, EmpApplnWorkExperienceDocumentDBO> docMap = !Utils.isNullOrEmpty(empApplnWorkExperienceDBO.getWorkExperienceDocumentsDBOSet())
													?empApplnWorkExperienceDBO.getWorkExperienceDocumentsDBOSet().stream()
															.collect(Collectors.toMap(wrk->wrk.getId(), wrk->wrk)):new HashMap<Integer, EmpApplnWorkExperienceDocumentDBO>();
											for (EmpApplnWorkExperienceDocumentDTO empApplnWorkExperienceDocumentDTO : empApplnWorkExperienceDTO.experienceDocumentList) {
												/*empApplnWorkExperienceDocumentDTO.setProcessCode("EMPLOYEE_APP_PROFESSIONAL_EXP");
												empApplnWorkExperienceDocumentDTO.setUniqueFileName("unique.pdf");
												empApplnWorkExperienceDocumentDTO.setOriginalFileName("original.pdf");
												empApplnWorkExperienceDocumentDTO.setNewFile(true);*/
												/*
												EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO = null;
												if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId)) {
													empApplnWorkExperienceDocumentDBO.id = empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId;
													empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
													workExperienceDocumentDBOIds.add(empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceDocumentId);
												} else {
													empApplnWorkExperienceDocumentDBO = new EmpApplnWorkExperienceDocumentDBO();
													empApplnWorkExperienceDocumentDBO.createdUsersId = userId;
												}
												if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceId)) {
													empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO = new EmpApplnWorkExperienceDBO();
													empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO.empApplnWorkExperienceId = empApplnWorkExperienceDocumentDTO.empApplnWorkExperienceId;
												} else {
													empApplnWorkExperienceDocumentDBO.empApplnWorkExperienceDBO = empApplnWorkExperienceDBO;
												}
												if (!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.experienceDocumentsUrl)) {
													empApplnWorkExperienceDocumentDBO.experienceDocumentsUrl = empApplnWorkExperienceDocumentDTO.experienceDocumentsUrl;
												}*/
												
												//---------
												EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO = new EmpApplnWorkExperienceDocumentDBO();
												if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId()) 
														&& !Utils.isNullOrEmpty(docMap.get(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId()))){
													empApplnWorkExperienceDocumentDBO = docMap.get(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId());
													empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
													workExperienceDocumentDBOIds.add(empApplnWorkExperienceDocumentDTO.getEmpApplnWorkExperienceDocumentId());
												} else {
													empApplnWorkExperienceDocumentDBO = new EmpApplnWorkExperienceDocumentDBO();
													empApplnWorkExperienceDocumentDBO.setEmpApplnWorkExperienceDBO(empApplnWorkExperienceDBO);
													empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
													empApplnWorkExperienceDocumentDBO.createdUsersId = userId;
												}
												
												UrlAccessLinkDBO documentsUrlDBO = new UrlAccessLinkDBO();
												if(Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())){
													documentsUrlDBO = new UrlAccessLinkDBO();
													documentsUrlDBO.setCreatedUsersId(userId);
													documentsUrlDBO.setRecordStatus('A');																	}
												else {
													documentsUrlDBO = empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO();
													
												}
												if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getNewFile()) && empApplnWorkExperienceDocumentDTO.getNewFile()) {
													documentsUrlDBO = createURLAccessLinkDBO(documentsUrlDBO, empApplnWorkExperienceDocumentDTO.getProcessCode(),empApplnWorkExperienceDocumentDTO.getUniqueFileName(), empApplnWorkExperienceDocumentDTO.getOriginalFileName(), userId, saveMode);
												}
												if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDTO.getUniqueFileName())) {
													uniqueFileNameList.addAll(createFileListForActualCopy(empApplnWorkExperienceDocumentDTO.getProcessCode(), empApplnWorkExperienceDocumentDTO.getUniqueFileName()));
												}

												empApplnWorkExperienceDocumentDBO.setExperienceDocumentsUrlDBO(documentsUrlDBO);
												//----------
												
												empApplnWorkExperienceDocumentDBO.recordStatus = 'A';
												workExperienceDocumentDBOSet.add(empApplnWorkExperienceDocumentDBO);
											}
										}
										if (!Utils.isNullOrEmpty(empApplnWorkExperienceDBO.workExperienceDocumentsDBOSet)) {
											for (EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO : empApplnWorkExperienceDBO.workExperienceDocumentsDBOSet) {
												if (!workExperienceDocumentDBOIds.contains(empApplnWorkExperienceDocumentDBO.id)) {
													empApplnWorkExperienceDocumentDBO.recordStatus = 'D';
													empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
													if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
														UrlAccessLinkDBO urlAccessLinkDBO = empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO();
														if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
															urlAccessLinkDBO.setRecordStatus('D');
															urlAccessLinkDBO.setModifiedUsersId(userId);
														}
														empApplnWorkExperienceDocumentDBO.setExperienceDocumentsUrlDBO(urlAccessLinkDBO);
													}
													workExperienceDocumentDBOSet.add(empApplnWorkExperienceDocumentDBO);
												}
											}
										}
										empApplnWorkExperienceDBO.workExperienceDocumentsDBOSet = workExperienceDocumentDBOSet;
										empApplnWorkExperienceDBO.recordStatus = 'A';
										professionalExperienceSet.add(empApplnWorkExperienceDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnWorkExperienceDBOs)) {
								for (EmpApplnWorkExperienceDBO workExperienceDBO : empApplnEntriesDBO.empApplnWorkExperienceDBOs) {
									if (!professionalExperienceIdsSet.contains(workExperienceDBO.empApplnWorkExperienceId)) {
										workExperienceDBO.recordStatus = 'D';
										workExperienceDBO.modifiedUsersId = userId;
										if (!Utils.isNullOrEmpty(workExperienceDBO.workExperienceDocumentsDBOSet)) {
											Set<EmpApplnWorkExperienceDocumentDBO> workExperienceDocumentDBOsSet = new HashSet<>();
											for (EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO : workExperienceDBO.workExperienceDocumentsDBOSet) {
												empApplnWorkExperienceDocumentDBO.recordStatus = 'D';
												if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
													UrlAccessLinkDBO urlAccessLinkDBO = empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO();
													if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
														urlAccessLinkDBO.setRecordStatus('D');
														urlAccessLinkDBO.setModifiedUsersId(userId);
													}
													empApplnWorkExperienceDocumentDBO.setExperienceDocumentsUrlDBO(urlAccessLinkDBO);
												}
												empApplnWorkExperienceDocumentDBO.modifiedUsersId = userId;
												workExperienceDocumentDBOsSet.add(empApplnWorkExperienceDocumentDBO);
											}
											workExperienceDBO.workExperienceDocumentsDBOSet = workExperienceDocumentDBOsSet;
										}
										professionalExperienceSet.add(workExperienceDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(professionalExperienceSet))
								empApplnEntriesDBO.empApplnWorkExperienceDBOs = professionalExperienceSet;
						}
						//Research Details
						if (!Utils.isNullOrEmpty(researchDetailDTO)) {
							Set<Integer> addtnlInfoEntriesDBOsIdSet = new HashSet<>();
							Set<EmpApplnAddtnlInfoEntriesDBO> addtnlInfoEntriesDBOSet = new HashSet<>();
							if (!Utils.isNullOrEmpty(researchDetailDTO.isResearchExperience) && "Yes".equalsIgnoreCase(researchDetailDTO.isResearchExperience)) {
								if(Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO)){
									empApplnEntriesDBO.empApplnPersonalDataDBO = new EmpApplnPersonalDataDBO();
								}
								empApplnEntriesDBO.empApplnPersonalDataDBO.orcidNo = researchDetailDTO.orcidId;
								empApplnEntriesDBO.getEmpApplnPersonalDataDBO().setScopusNo(researchDetailDTO.getScopusId());
								empApplnEntriesDBO.getEmpApplnPersonalDataDBO().setVidwanNo(researchDetailDTO.getInflibnetVidwanNo());
								if (!Utils.isNullOrEmpty(researchDetailDTO.getHIndex())) {
									empApplnEntriesDBO.getEmpApplnPersonalDataDBO().setHIndexNo(Integer.valueOf(researchDetailDTO.getHIndex()));
								}
								if (!Utils.isNullOrEmpty(researchDetailDTO.researchEntries)) {
									EmpApplnAddtnlInfoEntriesDTO empApplnAddtnlInfoEntriesDTO = researchDetailDTO.researchEntries;
									if (!Utils.isNullOrEmpty(empApplnAddtnlInfoEntriesDTO.researchEntriesHeadings)) {
										for (EmpApplnAddtnlInfoHeadingDTO empApplnAddtnlInfoHeadingDTO : empApplnAddtnlInfoEntriesDTO.researchEntriesHeadings) {
											if (!Utils.isNullOrEmpty(empApplnAddtnlInfoHeadingDTO.parameters) && !Utils.isNullOrEmpty(empApplnAddtnlInfoHeadingDTO.isTypeResearch) && empApplnAddtnlInfoHeadingDTO.isTypeResearch) {
												for (EmpApplnAddtnlInfoParameterDTO empApplnAddtnlInfoParameterDTO : empApplnAddtnlInfoHeadingDTO.parameters) {
													if (!Utils.isNullOrEmpty(empApplnAddtnlInfoParameterDTO.isDisplayInApplication) && empApplnAddtnlInfoParameterDTO.isDisplayInApplication) {
														EmpApplnAddtnlInfoEntriesDBO empApplnAddtnlInfoEntriesDBO = new EmpApplnAddtnlInfoEntriesDBO();
														if (!Utils.isNullOrEmpty(empApplnAddtnlInfoParameterDTO.empApplnAddtnlInfoEntriesId)) {
															empApplnAddtnlInfoEntriesDBO = context.find(EmpApplnAddtnlInfoEntriesDBO.class, Integer.parseInt(empApplnAddtnlInfoParameterDTO.empApplnAddtnlInfoEntriesId));
															//empApplnAddtnlInfoEntriesDBO.empApplnAddtnlInfoEntriesId = Integer.parseInt(empApplnAddtnlInfoParameterDTO.empApplnAddtnlInfoEntriesId);
															empApplnAddtnlInfoEntriesDBO.modifiedUsersId = userId;
															addtnlInfoEntriesDBOsIdSet.add(Integer.parseInt(empApplnAddtnlInfoParameterDTO.empApplnAddtnlInfoEntriesId));
														} else {
															empApplnAddtnlInfoEntriesDBO.createdUsersId = userId;
														}
														empApplnAddtnlInfoEntriesDBO.empApplnEntriesDBO = empApplnEntriesDBO;
														if (!Utils.isNullOrEmpty(empApplnAddtnlInfoParameterDTO.id)) {
															empApplnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO = new EmpApplnAddtnlInfoParameterDBO();
															empApplnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id = Integer.parseInt(empApplnAddtnlInfoParameterDTO.id);
														}
														// later need to change addtnlInfoValue to research count 
														empApplnAddtnlInfoEntriesDBO.addtnlInfoValue = empApplnAddtnlInfoParameterDTO.parameterValue;
														empApplnAddtnlInfoEntriesDBO.recordStatus = 'A';
														addtnlInfoEntriesDBOSet.add(empApplnAddtnlInfoEntriesDBO);
													}
												}
											}
										}
									}
								}
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs)) {
								for (EmpApplnAddtnlInfoEntriesDBO empApplnWorkExperienceDBO : empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs) {
									if (!addtnlInfoEntriesDBOsIdSet.contains(empApplnWorkExperienceDBO.empApplnAddtnlInfoEntriesId)) {
										empApplnWorkExperienceDBO.modifiedUsersId = userId;
										empApplnWorkExperienceDBO.recordStatus = 'D';
										addtnlInfoEntriesDBOSet.add(empApplnWorkExperienceDBO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(addtnlInfoEntriesDBOSet))
								empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs = addtnlInfoEntriesDBOSet;
						}
						if ("save".equalsIgnoreCase(saveMode)) {
//							if (Utils.isNullOrEmpty(employeeApplicantDTO.applicationNo)) {
//								empApplnEntriesDBO.applicationNo = employeeApplicationTransaction.getApplicationNumber(context, userId);
//							}
							Tuple workFlowProcess = CommonApiTransaction.getInstance().getErpWorkFlowProcessIdbyProcessCode("EMP_APPLICATION_SUBMITTED");
							if (!Utils.isNullOrEmpty(workFlowProcess)) {
								Integer workFlowProcessId = Integer.parseInt(String.valueOf(workFlowProcess.get("erp_work_flow_process_id")));
								if (workFlowProcess.get("applicant_status_display_text") != null && !Utils.isNullOrWhitespace(workFlowProcess.get("applicant_status_display_text").toString())) {
									empApplnEntriesDBO.applicantCurrentProcessStatus = new ErpWorkFlowProcessDBO();
									empApplnEntriesDBO.applicantCurrentProcessStatus.id = workFlowProcessId;
									//empApplnEntriesDBO.applicantStatusTime = new Date();
									empApplnEntriesDBO.applicantStatusTime = LocalDateTime.now();
								}
								if (workFlowProcess.get("application_status_display_text") != null && !Utils.isNullOrWhitespace(workFlowProcess.get("application_status_display_text").toString())) {
									empApplnEntriesDBO.applicationCurrentProcessStatus = new ErpWorkFlowProcessDBO();
									empApplnEntriesDBO.applicationCurrentProcessStatus.id = workFlowProcessId;
									//empApplnEntriesDBO.applicationStatusTime =  new Date();
									empApplnEntriesDBO.applicationStatusTime = LocalDateTime.now();
								}
								Integer id = empApplnEntriesDBO.getId();
								ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
								erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
								erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO.id = workFlowProcessId;
								erpWorkFlowProcessStatusLogDBO.entryId = id;
								erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
								erpWorkFlowProcessStatusLogDBO.createdUsersId = userId;
								context.persist(erpWorkFlowProcessStatusLogDBO);
								//commonApiTransaction.saveErpWorkFlowProcessStatusLogDBO(erpWorkFlowProcessStatusLogDBO);
							}
							empApplnEntriesDBO.submissionDate = LocalDateTime.now();
						}
						//isSaved = employeeApplicationTransaction.submitEmployeeApplication(empApplnEntriesDBO);
						if (Utils.isNullOrEmpty(empApplnEntriesDBO) || Utils.isNullOrEmpty(empApplnEntriesDBO.id) || empApplnEntriesDBO.id == 0) {
							context.persist(empApplnEntriesDBO);
							if((uniqueFileNameList.size() > 0) && saveMode.equalsIgnoreCase("save")) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList)
							    .subscribe(res -> {
							        if (res.success()) {
							            System.out.println("Move operation succeeded");
							        } else {
							            System.out.println("Move operation failed: " + res.message());
							        }
							    });
							}
						} else {
							if (saveMode.equalsIgnoreCase("save") && Utils.isNullOrEmpty(employeeApplicantDTO.applicationNo)) {
								empApplnEntriesDBO.applicationNo = employeeApplicationTransaction.getApplicationNumber(context, userId);
							}
							if (saveMode.equalsIgnoreCase("save") && Utils.isNullOrEmpty(empApplnEntriesDBO.getApplicationNo())) {
								result.failureMessage = "Your Application is not submitted, Please submit again";
							} else {
								context.merge(empApplnEntriesDBO);
							}
							if((uniqueFileNameList.size() > 0) && saveMode.equalsIgnoreCase("save")) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList)
									.subscribe(res -> {
										if (res.success()) {
											System.out.println("Move operation succeeded");
										} else {
											System.out.println("Move operation failed: " + res.message());
										}
									});
							}
						}
//						 notifications
						if(saveMode.equalsIgnoreCase("save")) {
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getApplicationNo())) {	
								var category = context.find(EmpEmployeeCategoryDBO.class, empApplnEntriesDBO.getEmpEmployeeCategoryDBO().getId());
								String categoryName = category.getEmployeeCategoryName();
								var dbo = empApplnEntriesDBO;
								if (!Utils.isNullOrEmpty(dbo)) {	
									if (!Utils.isNullOrEmpty(dbo.getApplicantCurrentProcessStatus())) {		
										String officeMail = redisSysPropertiesData.getSysProperties(SysProperties.COMMON_RECRUITMENT_OFFICE_MAIL_ID.name(), null, null);
										String officeNumber = redisSysPropertiesData.getSysProperties(SysProperties.COMMON_RECRUITMENT_OFFICE_MOB_NO.name(), null, null);
										List<ErpEmailsDBO> emailsListApplicant = new ArrayList<ErpEmailsDBO>();
										List<ErpSmsDBO> smsListApplicant = new ArrayList<ErpSmsDBO>();
										List<ErpNotificationsDBO> notificationListApplicant = new ArrayList<ErpNotificationsDBO>();
										List<ErpEmailsDBO> emailsListOffice = new ArrayList<ErpEmailsDBO>();
										List<ErpSmsDBO> smsListOffice = new ArrayList<ErpSmsDBO>();
										List<ErpNotificationsDBO> notificationListOffice = new ArrayList<ErpNotificationsDBO>();
										var workFlowNotificationList = commonEmployeeTransaction.getWorkFlowNotificationByWorkflowIds(dbo.getApplicantCurrentProcessStatus().getId());
										workFlowNotificationList.forEach(s->{
											if(s.getNotificationCode().equalsIgnoreCase("EMP_APPLICATION_SUBMITTED_CANDIDATE")) {
												if(s.getIsNotificationActivated()) {
													notificationListApplicant.add(getNotificationsDBO(s,dbo.getId(), userIdCommon));
												}
												if(s.getIsSmsActivated() && !Utils.isNullOrEmpty(s.getErpSmsTemplateDBO())){
													smsListApplicant.add(smsTemplateCandidate(s.getErpSmsTemplateDBO(), dbo, userIdCommon,categoryName));
												}
												if(s.getIsEmailActivated() && !Utils.isNullOrEmpty(s.getErpEmailsTemplateDBO())) {
													emailsListApplicant.add(emailTemplateCandidate(s.getErpEmailsTemplateDBO(), dbo, userIdCommon, categoryName));
												}
											} else if (s.getNotificationCode().equalsIgnoreCase("EMP_APPLICATION_SUBMITTED_OFFICE")) {
												if(s.getIsNotificationActivated()) {
													notificationListOffice.add(getNotificationsDBO(s,dbo.getId(), userIdCommon));
												}
												if(s.getIsSmsActivated() && !Utils.isNullOrEmpty(s.getErpSmsTemplateDBO())){
													smsListOffice.add(smsTemplateOffice(s.getErpSmsTemplateDBO(), dbo, userIdCommon, officeNumber, categoryName));
												}
												if(s.getIsEmailActivated() && !Utils.isNullOrEmpty(s.getErpEmailsTemplateDBO())) {
													emailsListOffice.add(emailTemplateOffice(s.getErpEmailsTemplateDBO(), dbo, userIdCommon,officeMail,categoryName));
												}
											}
										});
										Set<Integer> approversIdSet = null;
										commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(dbo.getApplicationCurrentProcessStatus().getId(),"EMP_APPLICATION_SUBMITTED_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);	
										commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(dbo.getApplicationCurrentProcessStatus().getId(),"EMP_APPLICATION_SUBMITTED_OFFICE",approversIdSet,notificationListOffice,smsListOffice,emailsListOffice);
									}
								}
							}
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.id))
							employeeApplicantDTO.empApplnEntriesId = empApplnEntriesDBO.id;
						result.success = true;
					} else {
						result.failureMessage = "Not valid";
					}
				} catch (Exception e) {
					e.printStackTrace();
					result.failureMessage = "Something went wrong";
				}
			}
			@Override
			public void onError(Exception error) {
				error.printStackTrace();
				result.failureMessage = "Something went wrong";
			}
		});
	}

	private ErpEmailsDBO emailTemplateOffice(ErpTemplateDBO erpEmailsTemplateDBO, EmpApplnEntriesDBO dbo, String userIdCommon, String officeMail, String categoryName) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.setEntryId(dbo.getId());
//		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
//		erpUsersDBO1.setId(Integer.parseInt(userId));
//		erpEmailsDBO.setErpUsersDBO(erpUsersDBO1);
		String msgBody = erpEmailsTemplateDBO.getTemplateContent();
		if(!Utils.isNullOrEmpty(msgBody)) {
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(dbo.getEmpEmployeeCategoryDBO())) {
				msgBody = msgBody.replace("[EMPLOYEE_CATEGORY]", categoryName);
				dbo.getApplnDignitariesFeedbackDBOs();
			}
			if(!Utils.isNullOrEmpty(officeMail)) {
				erpEmailsDBO.setRecipientEmail(officeMail);
			}
			erpEmailsDBO.setEmailContent(msgBody);
			if(!Utils.isNullOrEmpty(erpEmailsTemplateDBO.getMailSubject()))
				erpEmailsDBO.setEmailSubject(erpEmailsTemplateDBO.getMailSubject());
			if(!Utils.isNullOrEmpty(erpEmailsTemplateDBO.getMailFromName()))
				erpEmailsDBO.setSenderName(erpEmailsTemplateDBO.getMailFromName());
		}
		if (!Utils.isNullOrEmpty(userIdCommon)) {
			erpEmailsDBO.setCreatedUsersId(Integer.parseInt(userIdCommon));
		}
		erpEmailsDBO.setRecordStatus('A');
		return erpEmailsDBO;
	}
	
	private ErpEmailsDBO emailTemplateCandidate(ErpTemplateDBO erpEmailsTemplateDBO, EmpApplnEntriesDBO dbo, String userIdCommon, String categoryName) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.setEntryId(dbo.getId());
//		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
//		erpUsersDBO1.setId(Integer.parseInt(userId));
//		erpEmailsDBO.setErpUsersDBO(erpUsersDBO1);
		String msgBody = erpEmailsTemplateDBO.getTemplateContent();
		if(!Utils.isNullOrEmpty(msgBody)) {
			if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
				msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dbo.getApplicantName());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(dbo.getEmpEmployeeCategoryDBO())) {
				msgBody = msgBody.replace("[EMPLOYEE_CATEGORY]", categoryName);
			}
			if(!Utils.isNullOrEmpty(dbo.getPersonalEmailId())) {
				erpEmailsDBO.setRecipientEmail(dbo.getPersonalEmailId());
			}
			erpEmailsDBO.setEmailContent(msgBody);
			if(!Utils.isNullOrEmpty(erpEmailsTemplateDBO.getMailSubject()))
				erpEmailsDBO.setEmailSubject(erpEmailsTemplateDBO.getMailSubject());
			if(!Utils.isNullOrEmpty(erpEmailsTemplateDBO.getMailFromName()))
				erpEmailsDBO.setSenderName(erpEmailsTemplateDBO.getMailFromName());
		}
		if (!Utils.isNullOrEmpty(userIdCommon)) {
			erpEmailsDBO.setCreatedUsersId(Integer.parseInt(userIdCommon));
		}
		erpEmailsDBO.setRecordStatus('A');
		return erpEmailsDBO;
	}
	
	private ErpSmsDBO smsTemplateOffice(ErpTemplateDBO erpSmsTemplateDBO,  EmpApplnEntriesDBO dbo, String userIdCommon, String officeNumber, String categoryName) {
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.setEntryId(dbo.getId());
//		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
//		erpUsersDBO1.setId(Integer.parseInt(userId));
//		erpSmsDBO.setErpUsersDBO(erpUsersDBO1);
		String msgBody = erpSmsTemplateDBO.getTemplateContent();
		if(!Utils.isNullOrEmpty(msgBody)) {
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(dbo.getEmpEmployeeCategoryDBO())) {
				msgBody = msgBody.replace("[EMPLOYEE_CATEGORY]", categoryName);
			}
			if(!Utils.isNullOrEmpty(officeNumber)) {
				erpSmsDBO.setRecipientMobileNo(officeNumber);
			}
			erpSmsDBO.setSmsContent(msgBody);
		}
		if(!Utils.isNullOrEmpty(erpSmsTemplateDBO.getTemplateId()))
			erpSmsDBO.setTemplateId(erpSmsTemplateDBO.getTemplateId());
		if (!Utils.isNullOrEmpty(userIdCommon)) {
			erpSmsDBO.setCreatedUsersId(Integer.parseInt(userIdCommon));
		}
		erpSmsDBO.setRecordStatus('A');
		return erpSmsDBO;
	}
	
	private ErpSmsDBO smsTemplateCandidate(ErpTemplateDBO erpSmsTemplateDBO,  EmpApplnEntriesDBO dbo, String userIdCommon, String categoryName) {
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.setEntryId(dbo.getId());
//		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
//		erpUsersDBO1.setId(Integer.parseInt(userId));
//		erpSmsDBO.setErpUsersDBO(erpUsersDBO1);
		String msgBody = erpSmsTemplateDBO.getTemplateContent();
		if(!Utils.isNullOrEmpty(msgBody)) {
			if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
				msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dbo.getApplicantName());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				msgBody = msgBody.replace("[EMPLOYEE_CATEGORY]", categoryName);
			}
			if(!Utils.isNullOrEmpty(dbo.getMobileNo())) {
				erpSmsDBO.setRecipientMobileNo(dbo.getMobileNo());
			}
			erpSmsDBO.setSmsContent(msgBody);
		}
		if(!Utils.isNullOrEmpty(erpSmsTemplateDBO.getTemplateId()))
			erpSmsDBO.setTemplateId(erpSmsTemplateDBO.getTemplateId());
		if (!Utils.isNullOrEmpty(userIdCommon)) {
			erpSmsDBO.setCreatedUsersId(Integer.parseInt(userIdCommon));
		}
		erpSmsDBO.setRecordStatus ('A');
		return erpSmsDBO;
	}
	
	private ErpNotificationsDBO getNotificationsDBO(ErpWorkFlowProcessNotificationsDBO dbo,Integer entryId, String userIdCommon) {
		ErpNotificationsDBO erpNotifications = new ErpNotificationsDBO();
		erpNotifications.setEntryId(entryId) ;
//		ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
//		erpUsersDBO.setId(userIdCommon);
//		erpNotifications.setErpUsersDBO(erpUsersDBO);
		erpNotifications.setErpWorkFlowProcessNotificationsDBO(dbo);
		if (!Utils.isNullOrEmpty(userIdCommon)) {
			erpNotifications.setCreatedUsersId(Integer.valueOf(userIdCommon));
		}
		erpNotifications.setRecordStatus('A');
		return erpNotifications;
	}
	
	public EmployeeApplicantDTO getEmployeeApplication(Integer empApplnEntriesId) throws Exception {
		EmployeeApplicantDTO employeeApplicantDTO = new EmployeeApplicantDTO();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				try {
					Query query = context.createQuery("from EmpApplnEntriesDBO bo where bo.recordStatus='A' and bo.id=:empApplnEntriesId").setParameter("empApplnEntriesId", empApplnEntriesId);
					EmpApplnEntriesDBO empApplnEntriesDBO = (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList());
					//EmpApplnEntriesDBO empApplnEntriesDBO = employeeApplicationTransaction.getEmployeeApplication(applicationNo);
					if (!Utils.isNullOrEmpty(empApplnEntriesDBO) && empApplnEntriesDBO.recordStatus == 'A') {
						employeeApplicantDTO.empApplnEntriesId = empApplnEntriesDBO.id;
						employeeApplicantDTO.jobDetailDTO = new JobDetailsDTO();
						employeeApplicantDTO.empApplnPersonalDataDTO = new EmpApplnPersonalDataDTO();
						employeeApplicantDTO.addressDetailDTO = new EmpApplnPersonalDataDTO();
						employeeApplicantDTO.educationalDetailDTO = new EducationalDetailsDTO();
						employeeApplicantDTO.professionalExperienceDTO = new ProfessionalExperienceDTO();
						employeeApplicantDTO.researchDetailDTO = new ResearchDetailsDTO();
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.applicationNo)) {
							employeeApplicantDTO.applicationNo = empApplnEntriesDBO.applicationNo;
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnRegistrationsDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnRegistrationsDBO.id)) {
							employeeApplicantDTO.empApplicationRegistrationId = String.valueOf(empApplnEntriesDBO.empApplnRegistrationsDBO.id);
						}
						//Job Details
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.id)) {
							employeeApplicantDTO.jobDetailDTO.empApplnEntriesId = empApplnEntriesDBO.id;
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empEmployeeCategoryDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empEmployeeCategoryDBO.id)) {
							employeeApplicantDTO.jobDetailDTO.postAppliedFor = String.valueOf(empApplnEntriesDBO.empEmployeeCategoryDBO.id);
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO())) {
							employeeApplicantDTO.setJobCategoryDTO(new SelectDTO());
							employeeApplicantDTO.getJobCategoryDTO().setLabel(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO().getEmployeeJobName());
							employeeApplicantDTO.getJobCategoryDTO().setValue(String.valueOf(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO().getId()));
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnSubjSpecializationPrefDBOs)) {
							List<Integer> subjectIds = new ArrayList<>();
							List<Integer> specializationIds = new ArrayList<>();
							List<EmpApplnSubjectCategoryDTO> empApplnSubjectCategoryDTOList = new ArrayList<>();
							for (EmpApplnSubjSpecializationPrefDBO applnSubjSpecializationPrefDBO : empApplnEntriesDBO.empApplnSubjSpecializationPrefDBOs) {
								if (applnSubjSpecializationPrefDBO.recordStatus == 'A') {
									EmpApplnSubjectCategoryDTO applnSubjectCategoryDTO = new EmpApplnSubjectCategoryDTO();
									if (!Utils.isNullOrEmpty(applnSubjSpecializationPrefDBO.empApplnSubjSpecializationPrefId)) {
										applnSubjectCategoryDTO.empApplnSubjSpecializationPrefId = applnSubjSpecializationPrefDBO.empApplnSubjSpecializationPrefId;
									}
									if (!Utils.isNullOrEmpty(applnSubjSpecializationPrefDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(applnSubjSpecializationPrefDBO.empApplnEntriesDBO.id)) {
										applnSubjectCategoryDTO.empApplnEntriesId = applnSubjSpecializationPrefDBO.empApplnEntriesDBO.id;
									}
									if (!Utils.isNullOrEmpty(applnSubjSpecializationPrefDBO.empApplnSubjectCategoryDBO) && !Utils.isNullOrEmpty(applnSubjSpecializationPrefDBO.empApplnSubjectCategoryDBO.id)) {
										if (!subjectIds.contains(applnSubjSpecializationPrefDBO.empApplnSubjectCategoryDBO.id)) {
											subjectIds.add(applnSubjSpecializationPrefDBO.empApplnSubjectCategoryDBO.id);
										}
										applnSubjectCategoryDTO.subjectCategoryId = applnSubjSpecializationPrefDBO.empApplnSubjectCategoryDBO.id;
									}
									if (!Utils.isNullOrEmpty(applnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO) && !Utils.isNullOrEmpty(applnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId)) {
										if (!specializationIds.contains(applnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId)) {
											specializationIds.add(applnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId);
										}
										applnSubjectCategoryDTO.subjectCategorySpecializationId = applnSubjSpecializationPrefDBO.empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId;
									}
									empApplnSubjectCategoryDTOList.add(applnSubjectCategoryDTO);
								}
							}
							if (!Utils.isNullOrEmpty(subjectIds))
								employeeApplicantDTO.jobDetailDTO.subjectCategoryIds = subjectIds.toArray(new Object[subjectIds.size()]);
							if (!Utils.isNullOrEmpty(specializationIds))
								employeeApplicantDTO.jobDetailDTO.specializationIds = specializationIds.toArray(new Object[specializationIds.size()]);
							employeeApplicantDTO.jobDetailDTO.empApplnSubjectCategoryDTO = empApplnSubjectCategoryDTOList;
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnLocationPrefDBOs)) {
							List<EmpApplnLocationPrefDTO> locationIds = new ArrayList<>();
							for (EmpApplnLocationPrefDBO applnLocationPrefDBO : empApplnEntriesDBO.empApplnLocationPrefDBOs) {
								if (applnLocationPrefDBO.recordStatus == 'A' && !Utils.isNullOrEmpty(applnLocationPrefDBO.erpLocationDBO) && !Utils.isNullOrEmpty(applnLocationPrefDBO.erpLocationDBO.id)) {
									EmpApplnLocationPrefDTO applnLocationPrefDTO = new EmpApplnLocationPrefDTO();
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.id)) {
										applnLocationPrefDTO.empApplnEntriesId = empApplnEntriesDBO.id;
									}
									if (!Utils.isNullOrEmpty(applnLocationPrefDBO.empApplnLocationPrefId)) {
										applnLocationPrefDTO.empApplnLocationPrefId = applnLocationPrefDBO.empApplnLocationPrefId;
									}
									if (!Utils.isNullOrEmpty(applnLocationPrefDBO.erpLocationDBO) && !Utils.isNullOrEmpty(applnLocationPrefDBO.erpLocationDBO.id)) {
										applnLocationPrefDTO.erpLocationId = applnLocationPrefDBO.erpLocationDBO.id;
									}
									locationIds.add(applnLocationPrefDTO);
								}
							}
							employeeApplicantDTO.jobDetailDTO.preferredLocationIds = locationIds;
						}
						//get applicant details
						employeeApplicationHelper.getApplicantDetails(employeeApplicantDTO,empApplnEntriesDBO,false,context);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception : " + e.getMessage());
				}
			}

			@Override
			public void onError(Exception error) {
				error.printStackTrace();
			}
		});
		return employeeApplicantDTO;
	}

	public Mono<ApiResult> uploadFiles(Flux<FilePart> data, String directory, String[] fileTypeAccept, boolean isHashFileName, String uploadFor) throws Exception {
		List<Tuple2<String, String>> hashedFineNamesList = new ArrayList<>();
		Mono<ApiResult> result = employeeApplicationHelper.uploadFiles(data, directory, fileTypeAccept, isHashFileName, hashedFineNamesList);
		result.subscribe(res -> {
			res.dto = hashedFineNamesList;
			if (!res.success) {
				res.failureMessage = uploadFor;
			}
		});
		return result;
	}

	public void getEmployeeApplicationStatus(String empApplicationRegistrationId, ApiResult result) throws Exception {
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				Query query = context.createQuery("select bo from EmpApplnEntriesDBO bo where bo.empApplnRegistrationsDBO.id=:empApplicationRegistrationId and bo.recordStatus='A'");
				query.setParameter("empApplicationRegistrationId", Integer.parseInt(empApplicationRegistrationId));
				List<EmpApplnEntriesDBO> empApplnEntriesDBOS = query.getResultList();
				//List<EmpApplnEntriesDBO> empApplnEntriesDBOS = employeeApplicationTransaction.getAppliedEmployeeApplications(empApplicationRegistrationId);
				if (!Utils.isNullOrEmpty(empApplnEntriesDBOS)) {
					List<EmployeeApplicationStatusDTO> dto = new ArrayList<>();
					empApplnEntriesDBOS.forEach(empApplnEntriesDBO -> {
						EmployeeApplicationStatusDTO employeeApplicationStatusDTO = new EmployeeApplicationStatusDTO();
						Integer interviewRound = null;
						String schedulesId = null;
						employeeApplicationStatusDTO.empApplnEntriesId = String.valueOf(empApplnEntriesDBO.id);
						employeeApplicationStatusDTO.applicationNo = !Utils.isNullOrEmpty(empApplnEntriesDBO.applicationNo) ? String.valueOf(empApplnEntriesDBO.applicationNo) : null;
						employeeApplicationStatusDTO.applicantName = !Utils.isNullOrEmpty(empApplnEntriesDBO.applicantName) ? empApplnEntriesDBO.applicantName
								: !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnRegistrationsDBO.applicantName) ? empApplnEntriesDBO.empApplnRegistrationsDBO.applicantName : "";
						employeeApplicationStatusDTO.submissionDate = !Utils.isNullOrEmpty(empApplnEntriesDBO.submissionDate) ? Utils.convertLocalDateTimeToStringDateTime(empApplnEntriesDBO.submissionDate) : "";
						employeeApplicationStatusDTO.applicantCurrentProcessStatus = !Utils.isNullOrEmpty(empApplnEntriesDBO.applicantCurrentProcessStatus) && !Utils.isNullOrEmpty(empApplnEntriesDBO.applicantCurrentProcessStatus.applicantStatusDisplayText)
								? empApplnEntriesDBO.applicantCurrentProcessStatus.applicantStatusDisplayText : Utils.isNullOrEmpty(empApplnEntriesDBO.applicationNo) ? "saveDraft" : null;	
						employeeApplicationStatusDTO.applicantProcessCode = !Utils.isNullOrEmpty(empApplnEntriesDBO.applicantCurrentProcessStatus) && !Utils.isNullOrEmpty(empApplnEntriesDBO.applicantCurrentProcessStatus.processCode)
								? empApplnEntriesDBO.applicantCurrentProcessStatus.processCode : null;
						employeeApplicationStatusDTO.applicationCurrentProcessStatus = !Utils.isNullOrEmpty(empApplnEntriesDBO.applicationCurrentProcessStatus) && !Utils.isNullOrEmpty(empApplnEntriesDBO.applicationCurrentProcessStatus.applicationStatusDisplayText)
								? empApplnEntriesDBO.applicationCurrentProcessStatus.applicationStatusDisplayText : null;
						employeeApplicationStatusDTO.applicationProcessCode = !Utils.isNullOrEmpty(empApplnEntriesDBO.applicationCurrentProcessStatus) && !Utils.isNullOrEmpty(empApplnEntriesDBO.applicationCurrentProcessStatus.processCode)
								? empApplnEntriesDBO.applicationCurrentProcessStatus.processCode : null;
						
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnInterviewSchedulesDBOs)) {
							for (EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO : empApplnEntriesDBO.empApplnInterviewSchedulesDBOs) {
								if (empApplnInterviewSchedulesDBO.recordStatus == 'A') {
									if (!Utils.isNullOrEmpty(interviewRound)) {
										if (interviewRound < empApplnInterviewSchedulesDBO.interviewRound) {
											schedulesId = String.valueOf(empApplnInterviewSchedulesDBO.empApplnInterviewSchedulesId);
											interviewRound = empApplnInterviewSchedulesDBO.interviewRound;
										}
									} else {
										interviewRound = empApplnInterviewSchedulesDBO.interviewRound;
										schedulesId = String.valueOf(empApplnInterviewSchedulesDBO.empApplnInterviewSchedulesId);
									}
								}
							}
						}
						employeeApplicationStatusDTO.empApplnInterviewSchedulesId = schedulesId;
						employeeApplicationStatusDTO.interviewRound = !Utils.isNullOrEmpty(interviewRound) ? String.valueOf(interviewRound) : null;
						employeeApplicationStatusDTO.empApplnPersonalDataId = !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empApplnPersonalDataId)
								? String.valueOf(empApplnEntriesDBO.empApplnPersonalDataDBO.empApplnPersonalDataId) : null;
						employeeApplicationStatusDTO.empEmployeeCategoryId = !Utils.isNullOrEmpty(empApplnEntriesDBO.empEmployeeCategoryDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empEmployeeCategoryDBO.id) ? String.valueOf(empApplnEntriesDBO.empEmployeeCategoryDBO.id) : null;
						employeeApplicationStatusDTO.emailId = !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnRegistrationsDBO.email) ? empApplnEntriesDBO.empApplnRegistrationsDBO.email : "";
						dto.add(employeeApplicationStatusDTO);
					});
					result.dto = dto;
				} else {
					var empApplnRegistrationsDBO =  employeeApplicationTransaction.getRegistrationDetails(empApplicationRegistrationId);
					EmployeeApplicationStatusDTO employeeApplicationStatusDTO = new EmployeeApplicationStatusDTO();
					employeeApplicationStatusDTO.setApplicantName(empApplnRegistrationsDBO.getApplicantName());
					employeeApplicationStatusDTO.setEmailId(empApplnRegistrationsDBO.getEmail());
					result.dto = employeeApplicationStatusDTO;
				}
				result.success = true;
			}

			@Override
			public void onError(Exception error) {
				error.printStackTrace();
				result.success = false;
				result.failureMessage = error.getMessage();
			}
		});
	}

	public EmployeeApplicantDTO getEmployeeApplicationPreview(String applicationNo) throws Exception {
		EmployeeApplicantDTO employeeApplicantDTO = new EmployeeApplicantDTO();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				try {
					Query query = context.createQuery("from EmpApplnEntriesDBO bo where bo.recordStatus='A' and bo.applicationNo=:applicationNo").setParameter("applicationNo", Integer.parseInt(applicationNo));
					EmpApplnEntriesDBO empApplnEntriesDBO = (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList());
					//EmpApplnEntriesDBO empApplnEntriesDBO = employeeApplicationTransaction.getEmployeeApplication(applicationNo);
					if (!Utils.isNullOrEmpty(empApplnEntriesDBO) && empApplnEntriesDBO.recordStatus == 'A') {
						employeeApplicantDTO.jobDetailDTO = new JobDetailsDTO();
						employeeApplicantDTO.empApplnPersonalDataDTO = new EmpApplnPersonalDataDTO();
						employeeApplicantDTO.addressDetailDTO = new EmpApplnPersonalDataDTO();
						employeeApplicantDTO.educationalDetailDTO = new EducationalDetailsDTO();
						employeeApplicantDTO.professionalExperienceDTO = new ProfessionalExperienceDTO();
						employeeApplicantDTO.researchDetailDTO = new ResearchDetailsDTO();				
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.applicationNo)) {
							employeeApplicantDTO.applicationNo = empApplnEntriesDBO.applicationNo;
						}
						// applied date
						if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getSubmissionDate()))
							employeeApplicantDTO.setSubmissionDate(empApplnEntriesDBO.getSubmissionDate().toLocalDate());
						//Job Details
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empEmployeeCategoryDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empEmployeeCategoryDBO.employeeCategoryName)) {
							employeeApplicantDTO.jobDetailDTO.postAppliedFor = empApplnEntriesDBO.empEmployeeCategoryDBO.employeeCategoryName;
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO())) {
							employeeApplicantDTO.setJobCategoryDTO(new SelectDTO());
							employeeApplicantDTO.getJobCategoryDTO().setValue(String.valueOf(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO().getId()));
							employeeApplicantDTO.getJobCategoryDTO().setLabel(empApplnEntriesDBO.getEmpEmployeeJobCategoryDBO().getEmployeeJobName());
						}
						//Personal Details
						EmpApplnPersonalDataDBO empApplnPersonalDataDBO = empApplnEntriesDBO.empApplnPersonalDataDBO;
						if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO) && empApplnPersonalDataDBO.recordStatus == 'A') {
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.applicantName))
								employeeApplicantDTO.empApplnPersonalDataDTO.applicantName = empApplnEntriesDBO.applicantName;
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.erpGenderDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.erpGenderDBO.genderName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.genderId = empApplnEntriesDBO.erpGenderDBO.genderName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.fatherName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.fatherName = empApplnPersonalDataDBO.fatherName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.motherName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.motherName = empApplnPersonalDataDBO.motherName;
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.dob)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.dateOfBirth = Utils.convertLocalDateToStringDate(empApplnEntriesDBO.dob);
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.personalEmailId)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.emailId = empApplnEntriesDBO.personalEmailId;
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.mobileNo)) {
								String countryCode = "";
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.mobileNoCountryCode)) {
									countryCode = empApplnEntriesDBO.mobileNoCountryCode;
								}
								employeeApplicantDTO.empApplnPersonalDataDTO.mobileNo = countryCode + " " + empApplnEntriesDBO.mobileNo;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.alternateNo)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.alternateNo = empApplnPersonalDataDBO.alternateNo;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.aadharNo)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.aadharNo = empApplnPersonalDataDBO.aadharNo;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpMaritalStatusDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpMaritalStatusDBO.maritalStatusName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.maritalStatusId = empApplnPersonalDataDBO.erpMaritalStatusDBO.maritalStatusName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpCountryDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpCountryDBO.nationalityName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.nationalityId = empApplnPersonalDataDBO.erpCountryDBO.nationalityName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.passportNo)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.passportNo = empApplnPersonalDataDBO.passportNo;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReligionDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReligionDBO.religionName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.religionId = empApplnPersonalDataDBO.erpReligionDBO.religionName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.isMinority)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.isMinority = empApplnPersonalDataDBO.isMinority ? "Yes" : "No";
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReservationCategoryDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpReservationCategoryDBO.reservationCategoryName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.reservationCategoryId = empApplnPersonalDataDBO.erpReservationCategoryDBO.reservationCategoryName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpBloodGroupDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpBloodGroupDBO.bloodGroupName)) {
								employeeApplicantDTO.empApplnPersonalDataDTO.bloodGroupId = empApplnPersonalDataDBO.erpBloodGroupDBO.bloodGroupName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.isDifferentlyAbled) && empApplnPersonalDataDBO.isDifferentlyAbled) {
								employeeApplicantDTO.empApplnPersonalDataDTO.isDifferentlyAbled = "Yes";
								if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpDifferentlyAbledDBO) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.erpDifferentlyAbledDBO.differentlyAbledName)) {
									employeeApplicantDTO.empApplnPersonalDataDTO.differentlyAbledId = empApplnPersonalDataDBO.erpDifferentlyAbledDBO.differentlyAbledName;
								} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.differentlyAbledDetails)) {
									employeeApplicantDTO.empApplnPersonalDataDTO.differentlyAbledDetails = empApplnPersonalDataDBO.differentlyAbledDetails;
								}
							} else {
								employeeApplicantDTO.empApplnPersonalDataDTO.isDifferentlyAbled = "No";
							}
							//Address Details
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentAddressLine1)) {
								employeeApplicantDTO.addressDetailDTO.currentAddressLine1 = empApplnPersonalDataDBO.currentAddressLine1;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentAddressLine2)) {
								employeeApplicantDTO.addressDetailDTO.currentAddressLine2 = empApplnPersonalDataDBO.currentAddressLine2;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCountry) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCountry.countryName)) {
								employeeApplicantDTO.addressDetailDTO.currentCountryId = empApplnPersonalDataDBO.currentCountry.countryName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentState) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentState.stateName)) {
								employeeApplicantDTO.addressDetailDTO.currentStateId = empApplnPersonalDataDBO.currentState.stateName;
							} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentStateOthers)) {
								employeeApplicantDTO.addressDetailDTO.currentStateId = empApplnPersonalDataDBO.currentStateOthers;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCity) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCity.cityName)) {
								employeeApplicantDTO.addressDetailDTO.currentCityId = empApplnPersonalDataDBO.currentCity.cityName;
							} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentCityOthers)) {
								employeeApplicantDTO.addressDetailDTO.currentCityId = empApplnPersonalDataDBO.currentCityOthers;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.currentPincode)) {
								employeeApplicantDTO.addressDetailDTO.currentPincode = empApplnPersonalDataDBO.currentPincode;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentAddressLine1)) {
								employeeApplicantDTO.addressDetailDTO.permanentAddressLine1 = empApplnPersonalDataDBO.permanentAddressLine1;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentAddressLine2)) {
								employeeApplicantDTO.addressDetailDTO.permanentAddressLine2 = empApplnPersonalDataDBO.permanentAddressLine2;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCountry) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCountry.countryName)) {
								employeeApplicantDTO.addressDetailDTO.permanentCountryId = empApplnPersonalDataDBO.permanentCountry.countryName;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentState) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentState.stateName)) {
								employeeApplicantDTO.addressDetailDTO.permanentStateId = empApplnPersonalDataDBO.permanentState.stateName;
							} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentStateOthers)) {
								employeeApplicantDTO.addressDetailDTO.permanentStateId = empApplnPersonalDataDBO.permanentStateOthers;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCity) && !Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCity.cityName)) {
								employeeApplicantDTO.addressDetailDTO.permanentCityId = empApplnPersonalDataDBO.permanentCity.cityName;
							} else if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentCityOthers)) {
								employeeApplicantDTO.addressDetailDTO.permanentCityId = empApplnPersonalDataDBO.permanentCityOthers;
							}
							if (!Utils.isNullOrEmpty(empApplnPersonalDataDBO.permanentPincode)) {
								employeeApplicantDTO.addressDetailDTO.permanentPincode = empApplnPersonalDataDBO.permanentPincode;
							}
							if(!Utils.isNullOrEmpty(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO())) {
								if(!Utils.isNullOrEmpty(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getFileNameUnique()) && 
										!Utils.isNullOrEmpty(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode()) && 
										!Utils.isNullOrEmpty(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getFileNameOriginal())) {
									FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
									fileUploadDownloadDTO.setActualPath(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getFileNameUnique());
									fileUploadDownloadDTO.setProcessCode(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
									fileUploadDownloadDTO.setOriginalFileName(empApplnPersonalDataDBO.getPhotoDocumentUrlDBO().getFileNameOriginal());
									aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
									employeeApplicantDTO.getEmpApplnPersonalDataDTO().setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
								}
							}
						}
						//Educational Details
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnEducationalDetailsDBOs)) {
							//qualification level list
							List<EmpApplnEducationalDetailsDTO> qualificationLevelList = new ArrayList<>();
							List<EmpApplnEducationalDetailsDTO> otherQualificationLevelList = new ArrayList<>();
							Integer highestQualificationLevelId = null;
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.highestQualificationLevel))
								highestQualificationLevelId = empApplnEntriesDBO.highestQualificationLevel;
							for (EmpApplnEducationalDetailsDBO empApplnEducationalDetailsDBO : empApplnEntriesDBO.empApplnEducationalDetailsDBOs) {
								if (empApplnEducationalDetailsDBO.recordStatus == 'A') {
									EmpApplnEducationalDetailsDTO applnEducationalDetailsDTO = new EmpApplnEducationalDetailsDTO();
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO.id)
											&& !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName)) {
										applnEducationalDetailsDTO.qualificationLevelId = empApplnEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName;
										if (!Utils.isNullOrEmpty(highestQualificationLevelId) && highestQualificationLevelId == empApplnEducationalDetailsDBO.erpQualificationLevelDBO.id) {
											employeeApplicantDTO.educationalDetailDTO.highestQualificationLevelId = empApplnEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName;
										}
									} else if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.qualificationOthers)) {
										applnEducationalDetailsDTO.qualificationLevelId = empApplnEducationalDetailsDBO.qualificationOthers;
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.currentStatus)) {
										applnEducationalDetailsDTO.currentStatus = empApplnEducationalDetailsDBO.currentStatus;
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.course)) {
										applnEducationalDetailsDTO.course = empApplnEducationalDetailsDBO.course;
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.specialization)) {
										applnEducationalDetailsDTO.specialization = empApplnEducationalDetailsDBO.specialization;
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.yearOfCompletion)) {
										applnEducationalDetailsDTO.yearOfCompletion = String.valueOf(empApplnEducationalDetailsDBO.yearOfCompletion);
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.institute)) {
										applnEducationalDetailsDTO.institute = empApplnEducationalDetailsDBO.institute;
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.boardOrUniversity)) {
										applnEducationalDetailsDTO.boardOrUniversity = empApplnEducationalDetailsDBO.boardOrUniversity;
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.gradeOrPercentage)) {
										applnEducationalDetailsDTO.gradeOrPercentage = empApplnEducationalDetailsDBO.gradeOrPercentage;
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpCountryDBO())) {
										applnEducationalDetailsDTO.setCountryId(String.valueOf(empApplnEducationalDetailsDBO.getErpCountryDBO().getId()));
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpStateDBO())) {
										applnEducationalDetailsDTO.setStateId(String.valueOf(empApplnEducationalDetailsDBO.getErpStateDBO().getId()));
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpStateDBO())) {
										applnEducationalDetailsDTO.setStateOther(empApplnEducationalDetailsDBO.getStateOthers());
									}
									if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO())) {
										applnEducationalDetailsDTO.setErpBoardOrUniversity(new SelectDTO());
										applnEducationalDetailsDTO.getErpBoardOrUniversity().setLabel(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().getUniversityBoardName());
										applnEducationalDetailsDTO.getErpBoardOrUniversity().setValue(String.valueOf(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().getId()));
									}
									if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpInstitutionDBO())) {
										applnEducationalDetailsDTO.setErpInstitute(new SelectDTO());
										applnEducationalDetailsDTO.getErpInstitute().setLabel(empApplnEducationalDetailsDBO.getErpInstitutionDBO().getInstitutionName());
										applnEducationalDetailsDTO.getErpInstitute().setValue(String.valueOf(empApplnEducationalDetailsDBO.getErpInstitutionDBO().getId()));
									}
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.documentsDBOSet)) {
										List<EmpApplnEducationalDetailsDocumentsDTO> documentsList = new ArrayList<>();
										for (EmpApplnEducationalDetailsDocumentsDBO documentsDBO : empApplnEducationalDetailsDBO.documentsDBOSet) {
											if (documentsDBO.recordStatus == 'A') {
												EmpApplnEducationalDetailsDocumentsDTO empApplnEducationalDetailsDocumentsDTO = new EmpApplnEducationalDetailsDocumentsDTO();
												//------
												if(!Utils.isNullOrEmpty(documentsDBO.getEducationalDocumentsUrlDBO())) {
													empApplnEducationalDetailsDocumentsDTO.setNewFile(false);
													empApplnEducationalDetailsDocumentsDTO.setProcessCode(documentsDBO.getEducationalDocumentsUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
													empApplnEducationalDetailsDocumentsDTO.setActualPath(documentsDBO.getEducationalDocumentsUrlDBO().getFileNameUnique());
													empApplnEducationalDetailsDocumentsDTO.setOriginalFileName(documentsDBO.getEducationalDocumentsUrlDBO().getFileNameOriginal());
												}
												//------
												documentsList.add(empApplnEducationalDetailsDocumentsDTO);
											}
										}
										applnEducationalDetailsDTO.documentList = documentsList;
									}
									
									if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.qualificationOthers)) {
										otherQualificationLevelList.add(applnEducationalDetailsDTO);
									} else {
										qualificationLevelList.add(applnEducationalDetailsDTO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(qualificationLevelList)) {
								qualificationLevelList.sort((o1, o2) -> {
									int comp = 0;
									if (!Utils.isNullOrEmpty(o1.qualificationLevelId) && !Utils.isNullOrEmpty(o2.qualificationLevelId)) {
										comp = o1.qualificationLevelId.compareTo(o2.qualificationLevelId);
										if (!Utils.isNullOrEmpty(o1.yearOfCompletion) && !Utils.isNullOrEmpty(o2.yearOfCompletion) && comp == 0)
											return o1.yearOfCompletion.compareTo(o2.yearOfCompletion);
										else
											return comp;
									}
									return comp;
								});
								employeeApplicantDTO.educationalDetailDTO.qualificationLevelsList = qualificationLevelList;
							}
							if (!Utils.isNullOrEmpty(otherQualificationLevelList)) {
								otherQualificationLevelList.sort((o1, o2) -> {
									int comp = 0;
									if (!Utils.isNullOrEmpty(o1.yearOfCompletion) && !Utils.isNullOrEmpty(o2.yearOfCompletion)) {
										comp = o1.yearOfCompletion.compareTo(o2.yearOfCompletion);
									}
									return comp;
								});
								//otherQualificationLevelList.sort(Comparator.comparing(o -> o.yearOfCompletion));
								employeeApplicantDTO.educationalDetailDTO.otherQualificationLevelsList = otherQualificationLevelList;
							}
							//Eligibility Test list
							List<EmpApplnEligibilityTestDTO> eligibilityTestList = new ArrayList<>();
							for (EmpApplnEligibilityTestDBO empApplnEligibilityTestDBO : empApplnEntriesDBO.empApplnEligibilityTestDBOs) {
								if (empApplnEligibilityTestDBO.recordStatus == 'A') {
									EmpApplnEligibilityTestDTO applnEligibilityTestDTO = new EmpApplnEligibilityTestDTO();
									if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.empEligibilityExamListDBO) && !Utils.isNullOrEmpty(empApplnEligibilityTestDBO.empEligibilityExamListDBO.eligibilityExamName)) {
										applnEligibilityTestDTO.eligibilityTestId = empApplnEligibilityTestDBO.empEligibilityExamListDBO.eligibilityExamName;
									}
									if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.testYear)) {
										applnEligibilityTestDTO.testYear = String.valueOf(empApplnEligibilityTestDBO.testYear);
									}
									
									if (!Utils.isNullOrEmpty(empApplnEligibilityTestDBO.eligibilityTestDocumentDBOSet)) {
										List<EmpApplnEligibilityTestDocumentDTO> eligibilityTestDocumentsList = new ArrayList<>();
										for (EmpApplnEligibilityTestDocumentDBO eligibilityTestDocumentDBO : empApplnEligibilityTestDBO.eligibilityTestDocumentDBOSet) {
											if (eligibilityTestDocumentDBO.recordStatus == 'A') {
												EmpApplnEligibilityTestDocumentDTO empApplnEligibilityTestDocumentDTO = new EmpApplnEligibilityTestDocumentDTO();
												if(!Utils.isNullOrEmpty(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO())) {
													empApplnEligibilityTestDocumentDTO.setNewFile(false);
													empApplnEligibilityTestDocumentDTO.setProcessCode(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
													empApplnEligibilityTestDocumentDTO.setActualPath(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getFileNameUnique());
													empApplnEligibilityTestDocumentDTO.setOriginalFileName(eligibilityTestDocumentDBO.getEligibilityDocumentUrlDBO().getFileNameOriginal());
												}
												eligibilityTestDocumentsList.add(empApplnEligibilityTestDocumentDTO);
											}
										}
										applnEligibilityTestDTO.eligibilityTestDocumentsList = eligibilityTestDocumentsList;
									}
									
									eligibilityTestList.add(applnEligibilityTestDTO);
								}
							}
							if (!Utils.isNullOrEmpty(eligibilityTestList)) {
								eligibilityTestList.sort((o1, o2) -> {
									int comp = 0;
									if (!Utils.isNullOrEmpty(o1.testYear) && !Utils.isNullOrEmpty(o2.testYear)) {
										comp = o1.testYear.compareTo(o2.testYear);
									}
									return comp;
								});
								//eligibilityTestList.sort(Comparator.comparing(o -> o.testYear));
								employeeApplicantDTO.educationalDetailDTO.eligibilityTestList = eligibilityTestList;
							}
						}
						//Professional Experience
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnWorkExperienceDBOs)) {
							List<EmpApplnWorkExperienceDTO> professionalExperienceList = new ArrayList<>();
							for (EmpApplnWorkExperienceDBO applnWorkExperienceDBO : empApplnEntriesDBO.empApplnWorkExperienceDBOs) {
								if (applnWorkExperienceDBO.recordStatus == 'A') {
									if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.isCurrentExperience) && applnWorkExperienceDBO.isCurrentExperience) {
										employeeApplicantDTO.professionalExperienceDTO.isCurrentlyWorking = "Yes";
										EmpApplnWorkExperienceDTO currentExperienceDTO = new EmpApplnWorkExperienceDTO();
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.workExperienceTypeName)) {
											currentExperienceDTO.workExperienceTypeId = applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.workExperienceTypeName;
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.subjectCategory)) {
											currentExperienceDTO.functionalAreaId = applnWorkExperienceDBO.empApplnSubjectCategoryDBO.subjectCategory;
										} else if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.functionalAreaOthers)) {
											currentExperienceDTO.functionalAreaOthers = applnWorkExperienceDBO.functionalAreaOthers;
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.isPartTime) && applnWorkExperienceDBO.isPartTime) {
											currentExperienceDTO.employmentType = "part time";
										} else {
											currentExperienceDTO.employmentType = "full time";
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empDesignation)) {
											currentExperienceDTO.designation = applnWorkExperienceDBO.empDesignation;
										}
//										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
//											currentExperienceDTO.fromDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceFromDate);
//										}
//										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
//											currentExperienceDTO.toDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceToDate);
//										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
											currentExperienceDTO.setFromDate(applnWorkExperienceDBO.workExperienceFromDate);
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
											currentExperienceDTO.setToDate(applnWorkExperienceDBO.workExperienceToDate);
										}
										if (applnWorkExperienceDBO.workExperienceYears != null) {
											currentExperienceDTO.years = String.valueOf(applnWorkExperienceDBO.workExperienceYears);
										}
										if (applnWorkExperienceDBO.workExperienceMonth != null) {
											currentExperienceDTO.months = String.valueOf(applnWorkExperienceDBO.workExperienceMonth);
										}
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.noticePeriod)) {
											currentExperienceDTO.noticePeriod = empApplnEntriesDBO.noticePeriod;
										}
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.currentMonthlySalary)) {
											currentExperienceDTO.currentSalary = String.valueOf(empApplnEntriesDBO.currentMonthlySalary);
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.institution)) {
											currentExperienceDTO.institution = String.valueOf(applnWorkExperienceDBO.institution);
										}
										
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceDocumentsDBOSet)) {
											List<EmpApplnWorkExperienceDocumentDTO> workExperienceDocumentsList = new ArrayList<>();
											for (EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO : applnWorkExperienceDBO.workExperienceDocumentsDBOSet) {
												if (empApplnWorkExperienceDocumentDBO.recordStatus == 'A') {
													EmpApplnWorkExperienceDocumentDTO empApplnWorkExperienceDocumentDTO = new EmpApplnWorkExperienceDocumentDTO();
													if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
														empApplnWorkExperienceDocumentDTO.setNewFile(false);
														empApplnWorkExperienceDocumentDTO.setProcessCode(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
														empApplnWorkExperienceDocumentDTO.setActualPath(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getFileNameUnique());
														empApplnWorkExperienceDocumentDTO.setOriginalFileName(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getFileNameOriginal());
													}
													workExperienceDocumentsList.add(empApplnWorkExperienceDocumentDTO);
												}
											}
											currentExperienceDTO.experienceDocumentList = workExperienceDocumentsList;
										}
										
										employeeApplicantDTO.professionalExperienceDTO.currentExperience = currentExperienceDTO;
									} else {
										EmpApplnWorkExperienceDTO professionExperienceDTO = new EmpApplnWorkExperienceDTO();
										if (Utils.isNullOrEmpty(employeeApplicantDTO.professionalExperienceDTO.isCurrentlyWorking))
											employeeApplicantDTO.professionalExperienceDTO.isCurrentlyWorking = "No";
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.workExperienceTypeName)) {
											professionExperienceDTO.workExperienceTypeId = applnWorkExperienceDBO.empApplnWorkExperienceTypeDBO.workExperienceTypeName;
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.subjectCategory)) {
											professionExperienceDTO.functionalAreaId = applnWorkExperienceDBO.empApplnSubjectCategoryDBO.subjectCategory;
										} else if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.functionalAreaOthers)) {
											professionExperienceDTO.functionalAreaOthers = applnWorkExperienceDBO.functionalAreaOthers;
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.isPartTime) && applnWorkExperienceDBO.isPartTime) {
											professionExperienceDTO.employmentType = "part time";
										} else {
											professionExperienceDTO.employmentType = "full time";
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.empDesignation)) {
											professionExperienceDTO.designation = applnWorkExperienceDBO.empDesignation;
										}
//										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
//											professionExperienceDTO.fromDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceFromDate);
//										}
//										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
//											professionExperienceDTO.toDate = Utils.convertLocalDateToStringDate(applnWorkExperienceDBO.workExperienceToDate);
//										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
											professionExperienceDTO.setFromDate(applnWorkExperienceDBO.workExperienceFromDate);
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
											professionExperienceDTO.setToDate(applnWorkExperienceDBO.workExperienceToDate);
										}
										if (applnWorkExperienceDBO.workExperienceYears != null) {
											professionExperienceDTO.years = String.valueOf(applnWorkExperienceDBO.workExperienceYears);
										}
										if (applnWorkExperienceDBO.workExperienceMonth != null) {
											professionExperienceDTO.months = String.valueOf(applnWorkExperienceDBO.workExperienceMonth);
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.institution)) {
											professionExperienceDTO.institution = applnWorkExperienceDBO.institution;
										}
										if (!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceDocumentsDBOSet)) {
											List<EmpApplnWorkExperienceDocumentDTO> workExperienceDocumentsList = new ArrayList<>();
											for (EmpApplnWorkExperienceDocumentDBO empApplnWorkExperienceDocumentDBO : applnWorkExperienceDBO.workExperienceDocumentsDBOSet) {
												if (empApplnWorkExperienceDocumentDBO.recordStatus == 'A') {
													EmpApplnWorkExperienceDocumentDTO empApplnWorkExperienceDocumentDTO = new EmpApplnWorkExperienceDocumentDTO();
													if(!Utils.isNullOrEmpty(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO())) {
														empApplnWorkExperienceDocumentDTO.setNewFile(false);
														empApplnWorkExperienceDocumentDTO.setProcessCode(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
														empApplnWorkExperienceDocumentDTO.setActualPath(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getFileNameUnique());
														empApplnWorkExperienceDocumentDTO.setOriginalFileName(empApplnWorkExperienceDocumentDBO.getExperienceDocumentsUrlDBO().getFileNameOriginal());
													}
													workExperienceDocumentsList.add(empApplnWorkExperienceDocumentDTO);
												}
											}
											professionExperienceDTO.experienceDocumentList = workExperienceDocumentsList;
										}
										professionalExperienceList.add(professionExperienceDTO);
									}
								}
							}
							if (!Utils.isNullOrEmpty(professionalExperienceList)) {
								professionalExperienceList.sort((o1, o2) -> {
									int comp = 0;
									if (!Utils.isNullOrEmpty(o1.getFromDate()) && !Utils.isNullOrEmpty(o2.getFromDate())) {
										comp = o1.getFromDate().compareTo(o2.getFromDate());
									}
									return comp;
								});
								//professionalExperienceList.sort(Comparator.comparing(o -> o.fromDate));
								employeeApplicantDTO.professionalExperienceDTO.professionalExperienceList = professionalExperienceList;
							}
							if (empApplnEntriesDBO.totalPreviousExperienceYears != null) {
								employeeApplicantDTO.professionalExperienceDTO.totalPreviousExperienceYears = String.valueOf(empApplnEntriesDBO.totalPreviousExperienceYears);
							}
							if (empApplnEntriesDBO.totalPreviousExperienceMonths != null) {
								employeeApplicantDTO.professionalExperienceDTO.totalPreviousExperienceMonths = String.valueOf(empApplnEntriesDBO.totalPreviousExperienceMonths);
							}
							if (empApplnEntriesDBO.totalPartTimePreviousExperienceYears != null) {
								employeeApplicantDTO.professionalExperienceDTO.totalPartTimePreviousExperienceYears = String.valueOf(empApplnEntriesDBO.totalPartTimePreviousExperienceYears);
							}
							if (empApplnEntriesDBO.totalPartTimePreviousExperienceMonths != null) {
								employeeApplicantDTO.professionalExperienceDTO.totalPartTimePreviousExperienceMonths = String.valueOf(empApplnEntriesDBO.totalPartTimePreviousExperienceMonths);
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.majorAchievements)) {
								employeeApplicantDTO.professionalExperienceDTO.majorAchievements = empApplnEntriesDBO.majorAchievements;
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.expectedSalary)) {
								employeeApplicantDTO.professionalExperienceDTO.expectedSalary = String.valueOf(empApplnEntriesDBO.expectedSalary);
							}
						}
						//Research Details
						if (!Utils.isNullOrEmpty(employeeApplicantDTO.researchDetailDTO)) {
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.isResearchExperiencePresent) && empApplnEntriesDBO.isResearchExperiencePresent) {
								boolean isTypeResearch = true;
								employeeApplicantDTO.researchDetailDTO.isResearchExperience = "Yes";
								if(!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO)){
									if(!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.orcidNo))
										employeeApplicantDTO.researchDetailDTO.orcidId = empApplnEntriesDBO.empApplnPersonalDataDBO.orcidNo;
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getVidwanNo())) {
										employeeApplicantDTO.researchDetailDTO.inflibnetVidwanNo = String.valueOf(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getVidwanNo());
									}
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getScopusNo())) {
										employeeApplicantDTO.researchDetailDTO.scopusId = empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getScopusNo();
									}
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getHIndexNo())) {
										employeeApplicantDTO.researchDetailDTO.hIndex = String.valueOf(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getHIndexNo());
									}
								}
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs)) {
									EmpApplnAddtnlInfoEntriesDTO researchEntries = new EmpApplnAddtnlInfoEntriesDTO();
									//List<EmpApplnAddtnlInfoHeadingDBO> researchEntriesHeadings = employeeApplicationTransaction.getResearchDetails(empApplnEntriesDBO.empEmployeeCategoryDBO.id,isTypeResearch);
									List<EmpApplnAddtnlInfoHeadingDBO> researchEntriesHeadings = employeeApplicationTransaction.getResearchDetails(empApplnEntriesDBO.empEmployeeCategoryDBO.id, isTypeResearch, context);
									Map<Integer, EmpApplnAddtnlInfoEntriesDBO> parameterEntriesMap = new HashMap<>();
									for (EmpApplnAddtnlInfoEntriesDBO applnAddtnlInfoEntriesDBO : empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs) {
										if (applnAddtnlInfoEntriesDBO.recordStatus == 'A'
												&& !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO) && !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id)
												&& !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading) && !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading.isTypeResearch)
												&& applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading.isTypeResearch) {
											parameterEntriesMap.put(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id, applnAddtnlInfoEntriesDBO);
										}
									}
									List<EmpApplnAddtnlInfoHeadingDTO> researchEntriesHeadingsDTOs = employeeApplicationHelper.getResearchHeadingDTOs(researchEntriesHeadings, parameterEntriesMap, true);
									if (!Utils.isNullOrEmpty(researchEntriesHeadingsDTOs)) {
										researchEntries.researchEntriesHeadings = researchEntriesHeadingsDTOs;
										employeeApplicantDTO.researchDetailDTO.researchEntries = researchEntries;
									}
								}
							} else {
								employeeApplicantDTO.researchDetailDTO.isResearchExperience = "No";
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.isInterviewedBefore) && empApplnEntriesDBO.isInterviewedBefore) {
								employeeApplicantDTO.researchDetailDTO.isInterviewedBefore = "Yes";
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeDepartment)) {
									employeeApplicantDTO.researchDetailDTO.interviewedBeforeDepartment = empApplnEntriesDBO.interviewedBeforeDepartment;
								}
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeYear)) {
									employeeApplicantDTO.researchDetailDTO.interviewedBeforeYear = String.valueOf(empApplnEntriesDBO.interviewedBeforeYear);
								}
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeApplicationNo)) {
									employeeApplicantDTO.researchDetailDTO.interviewedBeforeApplicationNo = String.valueOf(empApplnEntriesDBO.interviewedBeforeApplicationNo);
								}
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.interviewedBeforeSubject)) {
									employeeApplicantDTO.researchDetailDTO.interviewedBeforeSubject = empApplnEntriesDBO.interviewedBeforeSubject;
								}
							} else {
								employeeApplicantDTO.researchDetailDTO.isInterviewedBefore = "No";
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnVacancyInformationDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnVacancyInformationDBO.vacancyInformationName)) {
								employeeApplicantDTO.researchDetailDTO.vacancyInformationId = empApplnEntriesDBO.empApplnVacancyInformationDBO.vacancyInformationName;
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.aboutVacancyOthers)) {
								employeeApplicantDTO.researchDetailDTO.aboutVacancyOthers = empApplnEntriesDBO.aboutVacancyOthers;
							}
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.otherInformation)) {
								employeeApplicantDTO.researchDetailDTO.otherInformation = empApplnEntriesDBO.otherInformation;
							}
						}
						//Additional Informations
						Map<Integer, Map<Integer, Map<Integer, List<Tuple>>>> additionalInformationMap = employeeApplicationHelper.convertAdditionalInformationToMap(employeeApplicationTransaction.getEmployeeApplicationAdditionalInformations(empApplnEntriesDBO.getId()));
						if(!Utils.isNullOrEmpty(additionalInformationMap)){
							employeeApplicantDTO.setAdditionalInformations(employeeApplicationHelper.convertAdditionalInformationMapToDTO(additionalInformationMap));
						}
						//Specialization
						Tuple specializationList = employeeApplicationTransaction.getAppliedSubjectSpecialization(applicationNo);
						if(!Utils.isNullOrEmpty(specializationList)) {
							if(!Utils.isNullOrEmpty(specializationList.get("Text"))){
								employeeApplicantDTO.setEmpApplnSubjectCategorySpecialization(String.valueOf(specializationList.get("Text")));
							}
						}
						//Subject
						Tuple subjectList = employeeApplicationTransaction.getAppliedSubject(applicationNo);
						if(!Utils.isNullOrEmpty(subjectList)) {
							if(!Utils.isNullOrEmpty(subjectList.get("Text"))){
								employeeApplicantDTO.setEmpApplnSubjectCategory(String.valueOf(subjectList.get("Text")));
							}
							if(!Utils.isNullOrEmpty(subjectList.get("acd"))){
								if(subjectList.get("acd").toString().trim().equalsIgnoreCase("1") || subjectList.get("acd").toString().trim().equalsIgnoreCase("true"))
									employeeApplicantDTO.setAcademic(true);
								else
									employeeApplicantDTO.setAcademic(false);
							}
						}
						//Location
						Tuple LocationList = employeeApplicationTransaction.getAppliedLocationPref(applicationNo);
						if(!Utils.isNullOrEmpty(LocationList)){
							if(!Utils.isNullOrEmpty(String.valueOf(LocationList.get("text")))){
								employeeApplicantDTO.setLocationPref(String.valueOf(LocationList.get("text")).toString());
							}
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Exception error) {

			}
		});
		return employeeApplicantDTO;
	}

	public EmployeeApplicantDTO getEmployeeApplicationInterviewDetails(Integer empApplnEntriesId, String interviewRound) throws Exception {
		EmployeeApplicantDTO employeeApplicantDTO = new EmployeeApplicantDTO();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				try {
					Query query = context.createQuery("select bo from EmpApplnEntriesDBO bo where bo.id=:empApplnEntriesId and bo.recordStatus='A'");
					query.setParameter("empApplnEntriesId", empApplnEntriesId);
					EmpApplnEntriesDBO empApplnEntriesDBO = (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList());
					//List<EmpApplnEntriesDBO> empApplnEntriesDBOS = employeeApplicationTransaction.getAppliedEmployeeApplications(empApplicationRegistrationId);
					if (!Utils.isNullOrEmpty(empApplnEntriesDBO) && empApplnEntriesDBO.recordStatus == 'A') {
						employeeApplicantDTO.empApplnPersonalDataDTO = new EmpApplnPersonalDataDTO();
						employeeApplicantDTO.researchDetailDTO = new ResearchDetailsDTO();
						//SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
						//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
						Integer empApplnEntriesId = null;
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.id)) {
							empApplnEntriesId = empApplnEntriesDBO.id;
							employeeApplicantDTO.empApplnEntriesId = empApplnEntriesDBO.id;
						}
						if (!Utils.isNullOrEmpty(empApplnEntriesDBO.applicationNo))
							employeeApplicantDTO.applicationNo = empApplnEntriesDBO.applicationNo;
						if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getOfferLetterUrlDbo())) {
							FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
							fileUploadDownloadDTO.setActualPath(empApplnEntriesDBO.getOfferLetterUrlDbo().getFileNameUnique());
							fileUploadDownloadDTO.setOriginalFileName(empApplnEntriesDBO.getOfferLetterUrlDbo().getFileNameOriginal());
							fileUploadDownloadDTO.setProcessCode(empApplnEntriesDBO.getOfferLetterUrlDbo().getUrlFolderListDBO().getUploadProcessCode());
							employeeApplicantDTO.setOfferLetterfileUploadDownloadDTO(fileUploadDownloadDTO);
						}
						else if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getRegretLetterUrlDbo())) {
							FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
							fileUploadDownloadDTO.setActualPath(empApplnEntriesDBO.getRegretLetterUrlDbo().getFileNameUnique());
							fileUploadDownloadDTO.setOriginalFileName(empApplnEntriesDBO.getRegretLetterUrlDbo().getFileNameOriginal());
							fileUploadDownloadDTO.setProcessCode(empApplnEntriesDBO.getRegretLetterUrlDbo().getUrlFolderListDBO().getUploadProcessCode());
							employeeApplicantDTO.setRegretLetterfileUploadDownloadDTO(fileUploadDownloadDTO);
						}

						if (!Utils.isNullOrEmpty(interviewRound) && "1".equalsIgnoreCase(interviewRound)) {
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnInterviewSchedulesDBOs)) {
								for (EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO : empApplnEntriesDBO.empApplnInterviewSchedulesDBOs) {
									if (empApplnInterviewSchedulesDBO.recordStatus == 'A') {
										if (Integer.valueOf(interviewRound) == empApplnInterviewSchedulesDBO.interviewRound) {
											if (!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.isApplicantAvailable)) {
												if (!empApplnInterviewSchedulesDBO.isApplicantAvailable) {
													employeeApplicantDTO.isAvailableForTheInterview = "false";
													employeeApplicantDTO.empApplnNonAvailabilityDTO = new EmpApplnNonAvailabilityDTO();
													if (!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO) && !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId)) {
														employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId = String.valueOf(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId);
														employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName) ? empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName : null;
														employeeApplicantDTO.empApplnNonAvailabilityDTO.interviewRound = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.interviewRound) ? String.valueOf(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.interviewRound) : null;
//														employeeApplicantDTO.empApplnNonAvailabilityDTO.isReschedulable = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.isReschedulable) && empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.isReschedulable
//																? "true" : "false";
													} else {
														employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers) ? empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers : null;
													}
												} else {
													employeeApplicantDTO.isAvailableForTheInterview = "true";
													//Research Details
													if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs)) {
														boolean isTypeResearch = false;
														EmpApplnAddtnlInfoEntriesDTO researchEntries = new EmpApplnAddtnlInfoEntriesDTO();
														//List<EmpApplnAddtnlInfoHeadingDBO> researchEntriesHeadings = employeeApplicationTransaction.getResearchDetails(empApplnEntriesDBO.empEmployeeCategoryDBO.id,isTypeResearch);
														List<EmpApplnAddtnlInfoHeadingDBO> researchEntriesHeadings = employeeApplicationTransaction.getResearchDetails(empApplnEntriesDBO.empEmployeeCategoryDBO.id, isTypeResearch, context);
														Map<Integer, EmpApplnAddtnlInfoEntriesDBO> parameterEntriesMap = new HashMap<>();
														for (EmpApplnAddtnlInfoEntriesDBO applnAddtnlInfoEntriesDBO : empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs) {
															if (applnAddtnlInfoEntriesDBO.recordStatus == 'A'
																	&& !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO) && !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id)
																	&& !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading) && !Utils.isNullOrEmpty(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading.isTypeResearch)
																	&& !applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.empApplnAddtnlInfoHeading.isTypeResearch) {
																parameterEntriesMap.put(applnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id, applnAddtnlInfoEntriesDBO);
															}
														}
														List<EmpApplnAddtnlInfoHeadingDTO> researchEntriesHeadingsDTOs = employeeApplicationHelper.getResearchHeadingDTOs(researchEntriesHeadings, parameterEntriesMap, false);
														if (!Utils.isNullOrEmpty(researchEntriesHeadingsDTOs)) {
															researchEntries.researchEntriesHeadings = researchEntriesHeadingsDTOs;
															employeeApplicantDTO.researchDetailDTO.researchEntries = researchEntries;
														}
													}
//													if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empApplnPersonalDataId)) {
//														employeeApplicantDTO.empApplnPersonalDataDTO.empApplnPersonalDataId = empApplnEntriesDBO.empApplnPersonalDataDBO.empApplnPersonalDataId;
//														employeeApplicantDTO.empApplnPersonalDataDTO.empApplnEntriesId = !Utils.isNullOrEmpty(empApplnEntriesId) ? empApplnEntriesId : null;
//														if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.uanNo)) {
//															employeeApplicantDTO.empApplnPersonalDataDTO.isUanNo = "true";
//															employeeApplicantDTO.empApplnPersonalDataDTO.uanNo = empApplnEntriesDBO.empApplnPersonalDataDBO.uanNo;
//														} else {
//															employeeApplicantDTO.empApplnPersonalDataDTO.isUanNo = "false";
//														}
//													}
												}
											}
											break;
										}
									}
								}
							}
						} else if (!Utils.isNullOrEmpty(interviewRound) && "2".equalsIgnoreCase(interviewRound)) {
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnInterviewSchedulesDBOs)) {
								for (EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO : empApplnEntriesDBO.empApplnInterviewSchedulesDBOs) {
									if (empApplnInterviewSchedulesDBO.recordStatus == 'A') {
										if (Integer.valueOf(interviewRound) == empApplnInterviewSchedulesDBO.interviewRound) {
											if (!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.isApplicantAvailable)) {
												employeeApplicantDTO.isAvailableForTheInterview = "true";
												if (!empApplnInterviewSchedulesDBO.isApplicantAvailable) {
													employeeApplicantDTO.isAvailableForTheInterview = "false";
													employeeApplicantDTO.empApplnNonAvailabilityDTO = new EmpApplnNonAvailabilityDTO();
													if (!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO) && !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId)) {
														employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId = String.valueOf(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId);
														employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName) ? empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName : null;
														employeeApplicantDTO.empApplnNonAvailabilityDTO.interviewRound = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.interviewRound) ? String.valueOf(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.interviewRound) : null;
//														employeeApplicantDTO.empApplnNonAvailabilityDTO.isReschedulable = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.isReschedulable) && empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.isReschedulable
//																? "true" : "false";
													} else {
														employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers) ? empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers : null;
													}
												}
											}
											break;
										}
									}
								}
							}  
						} else if (!Utils.isNullOrEmpty(interviewRound) && "3".equalsIgnoreCase(interviewRound)) {
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnInterviewSchedulesDBOs)) {
								for (EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO : empApplnEntriesDBO.empApplnInterviewSchedulesDBOs) {
									if (empApplnInterviewSchedulesDBO.recordStatus == 'A') {
										if (Integer.valueOf(interviewRound) == empApplnInterviewSchedulesDBO.interviewRound) {
											if (!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.isApplicantAvailable)) {
												employeeApplicantDTO.isAvailableForTheInterview = "true";
												if (!empApplnInterviewSchedulesDBO.isApplicantAvailable) {
													employeeApplicantDTO.isAvailableForTheInterview = "false";
													employeeApplicantDTO.empApplnNonAvailabilityDTO = new EmpApplnNonAvailabilityDTO();
													if (!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO) && !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId)) {
														employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId = String.valueOf(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId);
														employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName) ? empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName : null;
														employeeApplicantDTO.empApplnNonAvailabilityDTO.interviewRound = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.interviewRound) ? String.valueOf(empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.interviewRound) : null;
													} else {
														employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers) ? empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers : null;
													}
												}
											}
											break;
										}
									}
								}
							}  
						} else if (!Utils.isNullOrEmpty(interviewRound) && "selected".equalsIgnoreCase(interviewRound)) {
							employeeApplicantDTO.empJobDetailsDTO = new EmpJobDetailsDTO();
							employeeApplicantDTO.additionalPersonalDataDTO = new EmpAddtnlPersonalDataDTO();
							employeeApplicantDTO.educationalDetailDTO = new EducationalDetailsDTO();
						//	employeeApplicantDTO.empPfGratuityNomineesDTO = new ArrayList<>();
							employeeApplicantDTO.setEmpPfNomineesDTO(new ArrayList<EmpPfGratuityNomineesDTO>());
							employeeApplicantDTO.setEmpGratuityNomineesDTO(new ArrayList<EmpPfGratuityNomineesDTO>());
						//	employeeApplicantDTO.familyDependentDTO = new ArrayList<>();
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getApplicationCurrentProcessStatus())) {
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode())) {
									if (empApplnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_OFFER_ACCEPTED")) {
										employeeApplicantDTO.isAcceptOffer = "true";
									} else if (empApplnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_OFFER_DECLINED")) {
										employeeApplicantDTO.isAcceptOffer = "false";
									}
								}
							}
							employeeApplicantDTO.empApplnNonAvailabilityDTO = new EmpApplnNonAvailabilityDTO();
							if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnNonAvailabilityDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId)) {
								employeeApplicantDTO.isAcceptOffer = "false";
								employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId = String.valueOf(empApplnEntriesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId);
								employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName) ? empApplnEntriesDBO.empApplnNonAvailabilityDBO.nonAvailabilityName : null;
//								employeeApplicantDTO.empApplnNonAvailabilityDTO.isReschedulable = !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnNonAvailabilityDBO.isReschedulable) && empApplnEntriesDBO.empApplnNonAvailabilityDBO.isReschedulable
//										? "true" : "false";
								employeeApplicantDTO.empApplnNonAvailabilityDTO.isFinalSelection = !Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnNonAvailabilityDBO.isFinalSelection) && empApplnEntriesDBO.empApplnNonAvailabilityDBO.isFinalSelection
										? "true" : "false";
							} else if (!Utils.isNullOrWhitespace(empApplnEntriesDBO.jobRejectionReason)) {
								employeeApplicantDTO.isAcceptOffer = "false";
								employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName = !Utils.isNullOrEmpty(empApplnEntriesDBO.jobRejectionReason) ? empApplnEntriesDBO.jobRejectionReason : null;
							} else {
								//Educational Details
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnEducationalDetailsDBOs)) {
									List<EmpApplnEducationalDetailsDTO> qualificationLevelList = new ArrayList<>();
									List<EmpApplnEducationalDetailsDTO> otherQualificationLevelList = new ArrayList<>();
									Integer highestQualificationLevelId = null;
									for (EmpApplnEducationalDetailsDBO empApplnEducationalDetailsDBO : empApplnEntriesDBO.empApplnEducationalDetailsDBOs) {
										if (empApplnEducationalDetailsDBO.recordStatus == 'A') {
											EmpApplnEducationalDetailsDTO empApplnEducationalDetailsDTO = new EmpApplnEducationalDetailsDTO();
											if (Utils.isNullOrEmpty(highestQualificationLevelId)) {
												highestQualificationLevelId = empApplnEducationalDetailsDBO.empApplnEntriesDBO.highestQualificationLevel;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.empApplnEducationalDetailsId)) {
												empApplnEducationalDetailsDTO.empApplnEducationalDetailsId = empApplnEducationalDetailsDBO.empApplnEducationalDetailsId;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.empApplnEntriesDBO) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.empApplnEntriesDBO.id)) {
												empApplnEducationalDetailsDTO.empApplnEntriesId = empApplnEducationalDetailsDBO.empApplnEntriesDBO.id;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO.id)) {
												empApplnEducationalDetailsDTO.qualificationLevelId = String.valueOf(empApplnEducationalDetailsDBO.erpQualificationLevelDBO.id);
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName)) {
													empApplnEducationalDetailsDTO.qualificationName = empApplnEducationalDetailsDBO.erpQualificationLevelDBO.qualificationLevelName;
												}
												if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpQualificationLevelDBO().getQualificationLevelCode())) {
													empApplnEducationalDetailsDTO.setQualificationLevelCode(empApplnEducationalDetailsDBO.getErpQualificationLevelDBO().getQualificationLevelCode());
												}
											} else if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.qualificationOthers)) {
												empApplnEducationalDetailsDTO.qualificationOthers = empApplnEducationalDetailsDBO.qualificationOthers;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.currentStatus)) {
												empApplnEducationalDetailsDTO.currentStatus = empApplnEducationalDetailsDBO.currentStatus;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.course)) {
												empApplnEducationalDetailsDTO.course = empApplnEducationalDetailsDBO.course;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.specialization)) {
												empApplnEducationalDetailsDTO.specialization = empApplnEducationalDetailsDBO.specialization;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.yearOfCompletion)) {
												empApplnEducationalDetailsDTO.yearOfCompletion = String.valueOf(empApplnEducationalDetailsDBO.yearOfCompletion);
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.institute)) {
												empApplnEducationalDetailsDTO.institute = empApplnEducationalDetailsDBO.institute;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.boardOrUniversity)) {
												empApplnEducationalDetailsDTO.boardOrUniversity = empApplnEducationalDetailsDBO.boardOrUniversity;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.gradeOrPercentage)) {
												empApplnEducationalDetailsDTO.gradeOrPercentage = empApplnEducationalDetailsDBO.gradeOrPercentage;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpStateDBO) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpStateDBO.id)) {
												empApplnEducationalDetailsDTO.stateId = String.valueOf(empApplnEducationalDetailsDBO.erpStateDBO.id);
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpCountryDBO) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.erpCountryDBO.id)) {
												empApplnEducationalDetailsDTO.countryId = String.valueOf(empApplnEducationalDetailsDBO.erpCountryDBO.id);
											}
											if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO())) {
												empApplnEducationalDetailsDTO.setErpBoardOrUniversity(new SelectDTO());
												empApplnEducationalDetailsDTO.getErpBoardOrUniversity().setLabel(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().getUniversityBoardName());
												empApplnEducationalDetailsDTO.getErpBoardOrUniversity().setValue(String.valueOf(empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().getId()));
											}
											if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpInstitutionDBO())) {
												empApplnEducationalDetailsDTO.setErpInstitute(new SelectDTO());
												empApplnEducationalDetailsDTO.getErpInstitute().setLabel(empApplnEducationalDetailsDBO.getErpInstitutionDBO().getInstitutionName());
												empApplnEducationalDetailsDTO.getErpInstitute().setValue(String.valueOf(empApplnEducationalDetailsDBO.getErpInstitutionDBO().getId()));
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.stateOthers)) {
												empApplnEducationalDetailsDTO.stateOther = empApplnEducationalDetailsDBO.stateOthers;
											}
											if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.documentsDBOSet)) {
												List<EmpApplnEducationalDetailsDocumentsDTO> documentsList = new ArrayList<>();
												for (EmpApplnEducationalDetailsDocumentsDBO documentsDBO : empApplnEducationalDetailsDBO.documentsDBOSet) {
													if (documentsDBO.recordStatus == 'A') {
														EmpApplnEducationalDetailsDocumentsDTO empApplnEducationalDetailsDocumentsDTO = new EmpApplnEducationalDetailsDocumentsDTO();
														if (!Utils.isNullOrEmpty(documentsDBO.id))
															empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId = documentsDBO.id;
														if (!Utils.isNullOrEmpty(documentsDBO.empApplnEducationalDetailsDBO) && !Utils.isNullOrEmpty(documentsDBO.empApplnEducationalDetailsDBO.empApplnEducationalDetailsId))
															empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId = documentsDBO.empApplnEducationalDetailsDBO.empApplnEducationalDetailsId;
														if (!Utils.isNullOrEmpty(documentsDBO.educationalDocumentsUrl))
															empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl = documentsDBO.educationalDocumentsUrl;
														//------
														if(!Utils.isNullOrEmpty(documentsDBO.getEducationalDocumentsUrlDBO())) {
															empApplnEducationalDetailsDocumentsDTO.setNewFile(false);
															empApplnEducationalDetailsDocumentsDTO.setProcessCode(documentsDBO.getEducationalDocumentsUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
															empApplnEducationalDetailsDocumentsDTO.setActualPath(documentsDBO.getEducationalDocumentsUrlDBO().getFileNameUnique());
															empApplnEducationalDetailsDocumentsDTO.setOriginalFileName(documentsDBO.getEducationalDocumentsUrlDBO().getFileNameOriginal());
														}
														//------
														documentsList.add(empApplnEducationalDetailsDocumentsDTO);
													}
												}
												empApplnEducationalDetailsDTO.documentList = documentsList;
											}
											/*if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.qualificationOthers)) {
												otherQualificationLevelList.add(empApplnEducationalDetailsDTO);
											} else {*/
												qualificationLevelList.add(empApplnEducationalDetailsDTO);
											//}
										}
									}
									if (!Utils.isNullOrEmpty(qualificationLevelList))
										qualificationLevelList.sort((o1, o2) -> {
												return o1.yearOfCompletion.compareTo(o2.yearOfCompletion);
									 	});
										/*qualificationLevelList.sort((o1, o2) -> {
											int comp = 0;
											if (!Utils.isNullOrEmpty(o1.qualificationLevelId) && !Utils.isNullOrEmpty(o2.qualificationLevelId)) {
												comp = o1.qualificationLevelId.compareTo(o2.qualificationLevelId);
												if (!Utils.isNullOrEmpty(o1.yearOfCompletion) && !Utils.isNullOrEmpty(o2.yearOfCompletion) && comp == 0)
													return o1.yearOfCompletion.compareTo(o2.yearOfCompletion);
												else
													return comp;
											}
											return comp;
										});*/
									if (!Utils.isNullOrEmpty(otherQualificationLevelList))
										otherQualificationLevelList.sort(Comparator.comparing(o -> o.yearOfCompletion));
									employeeApplicantDTO.educationalDetailDTO.highestQualificationLevelId = String.valueOf(highestQualificationLevelId);
									employeeApplicantDTO.educationalDetailDTO.qualificationLevelsList = qualificationLevelList;
									employeeApplicantDTO.educationalDetailDTO.otherQualificationLevelsList = otherQualificationLevelList;
								}
								//Other Details
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO) && empApplnEntriesDBO.empApplnPersonalDataDBO.recordStatus == 'A') {
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.getErpMaritalStatusDBO())) {
										employeeApplicantDTO.setMaritalStatusDTO(new SelectDTO());
										employeeApplicantDTO.getMaritalStatusDTO().setValue(String.valueOf(empApplnEntriesDBO.empApplnPersonalDataDBO.getErpMaritalStatusDBO().getId()));
										employeeApplicantDTO.getMaritalStatusDTO().setLabel(empApplnEntriesDBO.empApplnPersonalDataDBO.getErpMaritalStatusDBO().getMaritalStatusName());
									}
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empApplnPersonalDataId))
										employeeApplicantDTO.additionalPersonalDataDTO.empApplnPersonalDataId = String.valueOf(empApplnEntriesDBO.empApplnPersonalDataDBO.empApplnPersonalDataId);
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO) && empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.recordStatus == 'A') {
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.empAddtnlPersonalDataId))
											employeeApplicantDTO.additionalPersonalDataDTO.empAddtnlPersonalDataId = String.valueOf(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.empAddtnlPersonalDataId);
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.panNo))
											employeeApplicantDTO.additionalPersonalDataDTO.panNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.panNo;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.fourWheelerNo))
											employeeApplicantDTO.additionalPersonalDataDTO.fourWheelerNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.fourWheelerNo;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.twoWheelerNo))
											employeeApplicantDTO.additionalPersonalDataDTO.twoWheelerNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.twoWheelerNo;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactName))
											employeeApplicantDTO.additionalPersonalDataDTO.emergencyContactName = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactName;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactAddress))
											employeeApplicantDTO.additionalPersonalDataDTO.emergencyContactAddress = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactAddress;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactRelationship))
											employeeApplicantDTO.additionalPersonalDataDTO.emergencyContactRelatonship = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactRelationship;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyMobileNo))
											employeeApplicantDTO.additionalPersonalDataDTO.emergencyMobileNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyMobileNo;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactHome))
											employeeApplicantDTO.additionalPersonalDataDTO.emergencyContactHome = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactHome;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactWork))
											employeeApplicantDTO.additionalPersonalDataDTO.emergencyContactWork = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.emergencyContactWork;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportNo))
											employeeApplicantDTO.additionalPersonalDataDTO.passportNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportNo;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportIssuedDate))
											employeeApplicantDTO.additionalPersonalDataDTO.passportIssuedDate = Utils.convertLocalDateToStringDate(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportIssuedDate);
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportStatus))
											employeeApplicantDTO.additionalPersonalDataDTO.passportStatus = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportStatus;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportDateOfExpiry))
											employeeApplicantDTO.additionalPersonalDataDTO.passportDateOfExpiry = Utils.convertLocalDateToStringDate(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportDateOfExpiry);
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportComments))
											employeeApplicantDTO.additionalPersonalDataDTO.passportComments = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.passportComments;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaNo))
											employeeApplicantDTO.additionalPersonalDataDTO.visaNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaNo;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaIssuedDate))
											employeeApplicantDTO.additionalPersonalDataDTO.visaIssuedDate = Utils.convertLocalDateToStringDate(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaIssuedDate);
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaStatus))
											employeeApplicantDTO.additionalPersonalDataDTO.visaStatus = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaStatus;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaDateOfExpiry))
											employeeApplicantDTO.additionalPersonalDataDTO.visaDateOfExpiry = Utils.convertLocalDateToStringDate(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaDateOfExpiry);
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaComments))
											employeeApplicantDTO.additionalPersonalDataDTO.visaComments = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.visaComments;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.familyBackgroundBrief))
											employeeApplicantDTO.additionalPersonalDataDTO.familyBackgroundBrief = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.familyBackgroundBrief;
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.isAadharAvailable)) {
											if (empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.isAadharAvailable) {
												employeeApplicantDTO.additionalPersonalDataDTO.isAadharAvailable = "true";
												if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.aadharNo))
													employeeApplicantDTO.additionalPersonalDataDTO.aadharNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.aadharNo;
											} else {
												employeeApplicantDTO.additionalPersonalDataDTO.isAadharAvailable = "false";
												if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.isAadharEnrolled) && empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.isAadharEnrolled) {
													employeeApplicantDTO.additionalPersonalDataDTO.isAadharEnrolled = "true";
													if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.aadharEnrolledNo))
														employeeApplicantDTO.additionalPersonalDataDTO.aadharEnrolledNo = empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO.aadharEnrolledNo;
												} else {
													employeeApplicantDTO.additionalPersonalDataDTO.isAadharEnrolled = "false";
												}
											}
										}
									}
									//Family Details
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empFamilyDetailsAddtnlDBOS)) {
										List<EmpFamilyDetailsAddtnlDTO> familyDetailsAddtnlList = new ArrayList<EmpFamilyDetailsAddtnlDTO>();
										List<EmpFamilyDetailsAddtnlDTO> dependentDetailsAddtnlList = new ArrayList<EmpFamilyDetailsAddtnlDTO>();
										empApplnEntriesDBO.empApplnPersonalDataDBO.empFamilyDetailsAddtnlDBOS.forEach(addtnlDetailDBO -> {
											if(addtnlDetailDBO.getRecordStatus() == 'A') {
												EmpFamilyDetailsAddtnlDTO familyDetailsDTO = new EmpFamilyDetailsAddtnlDTO();
												familyDetailsDTO.setEmpFamilyDetailsAddtnlId(String.valueOf(addtnlDetailDBO.getEmpFamilyDetailsAddtnlId()));
												if(!Utils.isNullOrEmpty(addtnlDetailDBO.getDependentDob())) {
													familyDetailsDTO.setDependDOB(addtnlDetailDBO.getDependentDob());
												}
												if(!Utils.isNullOrEmpty(addtnlDetailDBO.getDependentName())) {
													familyDetailsDTO.setDependentName(addtnlDetailDBO.getDependentName());
												}
												if(!Utils.isNullOrEmpty(addtnlDetailDBO.getDependentProfession())) {
													familyDetailsDTO.setDependentProfession(addtnlDetailDBO.getDependentProfession());
												}
												if(!Utils.isNullOrEmpty(addtnlDetailDBO.getDependentQualification())) {
													familyDetailsDTO.setDependentQualification(addtnlDetailDBO.getDependentQualification());
												}
												if(!Utils.isNullOrEmpty(addtnlDetailDBO.getEmpApplnPersonalDataDBO())) {
													familyDetailsDTO.setEmpApplnPersonalDataId(String.valueOf(addtnlDetailDBO.getEmpApplnPersonalDataDBO().getEmpApplnPersonalDataId()));
												}
												if(!Utils.isNullOrEmpty(addtnlDetailDBO.getRelationship())) {
												    familyDetailsDTO.setRelationship(addtnlDetailDBO.getRelationship());
											    if(familyDetailsDTO.getRelationship().equalsIgnoreCase("FATHER") || familyDetailsDTO.getRelationship().equalsIgnoreCase("MOTHER")
													|| familyDetailsDTO.getRelationship().equalsIgnoreCase("SPOUSE")) {
												familyDetailsAddtnlList.add(familyDetailsDTO);
												} else {
												 dependentDetailsAddtnlList.add(familyDetailsDTO);
												}
											  }
											}
										});
										employeeApplicantDTO.setFamilyDetailsAddtnlList(familyDetailsAddtnlList);
										employeeApplicantDTO.setDependentDetailsAddtnlList(dependentDetailsAddtnlList);
									}			
									//Family Dependent Details
//									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empFamilyDetailsAddtnlDBOS)) {
//										List<FamilyDependentDTO> familyDependentDTOS = new ArrayList<>();
//										Map<String, List<EmpFamilyDetailsAddtnlDTO>> dependentMap = new HashMap<>();
//										Map<String, List<EmpFamilyDetailsAddtnlDTO>> dependentMap1 = new HashMap<>();
//										for (EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO : empApplnEntriesDBO.empApplnPersonalDataDBO.empFamilyDetailsAddtnlDBOS) {
//											if (empFamilyDetailsAddtnlDBO.recordStatus == 'A') {
//												if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBO.relationship)) {
//													List<EmpFamilyDetailsAddtnlDTO> dto;
//													if(empFamilyDetailsAddtnlDBO.relationship.contains("FATHER") || empFamilyDetailsAddtnlDBO.relationship.contains("MOTHER") || empFamilyDetailsAddtnlDBO.relationship.contains("SPOUSE")) {
//														if (dependentMap.containsKey(empFamilyDetailsAddtnlDBO.relationship)) {
//															dto = dependentMap.get(empFamilyDetailsAddtnlDBO.relationship);
//															EmpFamilyDetailsAddtnlDTO empFamilyDetailsAddtnlDTO = employeeApplicationHelper.convertFamilyDetailsAddtnlDBOToDTO(empFamilyDetailsAddtnlDBO);
//															dto.add(empFamilyDetailsAddtnlDTO);
//														} else {
//															dto = new ArrayList<>();
//															EmpFamilyDetailsAddtnlDTO empFamilyDetailsAddtnlDTO = employeeApplicationHelper.convertFamilyDetailsAddtnlDBOToDTO(empFamilyDetailsAddtnlDBO);
//															dto.add(empFamilyDetailsAddtnlDTO);
//														}
//														dependentMap.put(empFamilyDetailsAddtnlDBO.relationship, dto);
//													}else {
//														if (dependentMap1.containsKey(empFamilyDetailsAddtnlDBO.relationship)) {
//															dto = dependentMap1.get(empFamilyDetailsAddtnlDBO.relationship);
//															EmpFamilyDetailsAddtnlDTO empFamilyDetailsAddtnlDTO = employeeApplicationHelper.convertFamilyDetailsAddtnlDBOToDTO(empFamilyDetailsAddtnlDBO);
//															dto.add(empFamilyDetailsAddtnlDTO);
//														} else {
//															dto = new ArrayList<>();
//															EmpFamilyDetailsAddtnlDTO empFamilyDetailsAddtnlDTO = employeeApplicationHelper.convertFamilyDetailsAddtnlDBOToDTO(empFamilyDetailsAddtnlDBO);
//															dto.add(empFamilyDetailsAddtnlDTO);
//														}
//														dependentMap1.put(empFamilyDetailsAddtnlDBO.relationship, dto);
//													}
//													
//												}
//											}
//										}
//										if (!Utils.isNullOrEmpty(dependentMap)) {
//											dependentMap.forEach((relationShip, empFamilyDetailsAddtnlDTOS) -> {
//												if(relationShip.contains("FATHER") || relationShip.contains("MOTHER") || relationShip.contains("SPOUSE")) {
//													FamilyDependentDTO dto = new FamilyDependentDTO();
//													dto.relationship = relationShip;
//													dto.empFamilyDetailsAddtnlDTOs = empFamilyDetailsAddtnlDTOS;
//													familyDependentDTOS.add(dto);
//												}
//											});
//											employeeApplicantDTO.familyDependentDTO = familyDependentDTOS;
//										}
//										if (!Utils.isNullOrEmpty(dependentMap1)) {
//											FamilyDependentDTO dto = new FamilyDependentDTO();
//											dto.setRelationship("Dependent");
//											List<EmpFamilyDetailsAddtnlDTO> empFamilyDetailsAddtnlDTOList = new ArrayList<EmpFamilyDetailsAddtnlDTO>();
//											dependentMap1.forEach((relationShip, empFamilyDetailsAddtnlDTOS) -> {
//												empFamilyDetailsAddtnlDTOList.addAll(empFamilyDetailsAddtnlDTOS);
//											});
//											dto.setEmpFamilyDetailsAddtnlDTOs(empFamilyDetailsAddtnlDTOList);
//											familyDependentDTOS.add(dto);
//											employeeApplicantDTO.familyDependentDTO = familyDependentDTOS;
//										}
//									}
								}
								//
								if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO) && !Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.id) && empApplnEntriesDBO.empJobDetailsDBO.recordStatus == 'A') {
//									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.reportingDate)) {
//										employeeApplicantDTO.reportingDate = Utils.convertLocalDateTimeToStringDateTime(empApplnEntriesDBO.empJobDetailsDBO.reportingDate);
//									}
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpJobDetailsDBO().getIsJoiningDateConfirmed())) {
										employeeApplicantDTO.empJobDetailsDTO.setIsJoiningDateConfirmed(empApplnEntriesDBO.getEmpJobDetailsDBO().getIsJoiningDateConfirmed());
									}
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpJobDetailsDBO().getPreferedJoiningDateTime())) {
										employeeApplicantDTO.empJobDetailsDTO.setPreferedJoiningDateTime(empApplnEntriesDBO.getEmpJobDetailsDBO().getPreferedJoiningDateTime());
									}
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpJobDetailsDBO().getJoiningDateRejectReason())) {
										employeeApplicantDTO.empJobDetailsDTO.setJoiningDateRejectReason(empApplnEntriesDBO.getEmpJobDetailsDBO().getJoiningDateRejectReason());
									}
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.joiningDate)) {
										employeeApplicantDTO.joiningDate = Utils.convertLocalDateTimeToStringDateTime(empApplnEntriesDBO.empJobDetailsDBO.joiningDate);
									}
									employeeApplicantDTO.empJobDetailsDTO.empJobDetailsId = String.valueOf(empApplnEntriesDBO.empJobDetailsDBO.id);
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.id))
										employeeApplicantDTO.empJobDetailsDTO.empApplnEntriesId = String.valueOf(empApplnEntriesDBO.id);
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.uanNo))
										employeeApplicantDTO.empJobDetailsDTO.uanNo = empApplnEntriesDBO.empJobDetailsDBO.uanNo;
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.pfAccountNo))
										employeeApplicantDTO.empJobDetailsDTO.pfAccountNo = String.valueOf(empApplnEntriesDBO.empJobDetailsDBO.pfAccountNo);
//									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.pfDate))
//										employeeApplicantDTO.empJobDetailsDTO.pfDate = sdf.format(empApplnEntriesDBO.empJobDetailsDBO.pfDate);
									
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.pfDate))
										employeeApplicantDTO.empJobDetailsDTO.pfDate = empApplnEntriesDBO.empJobDetailsDBO.pfDate;
									if(!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.gratuityNo))
								    	employeeApplicantDTO.empJobDetailsDTO.gratuityNo = empApplnEntriesDBO.empJobDetailsDBO.gratuityNo;
								    if(!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.gratuityDate))
								    	employeeApplicantDTO.empJobDetailsDTO.gratuityDate = empApplnEntriesDBO.empJobDetailsDBO.gratuityDate;	
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.isSibAccountAvailable)) {
										if (empApplnEntriesDBO.empJobDetailsDBO.isSibAccountAvailable) {
											employeeApplicantDTO.empJobDetailsDTO.isSibAccountAvailable = "true";
											if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.sibAccountBank))
												employeeApplicantDTO.empJobDetailsDTO.sibAccountBank = empApplnEntriesDBO.empJobDetailsDBO.sibAccountBank;
											if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.branchIfscCode))
												employeeApplicantDTO.empJobDetailsDTO.ifscCode = empApplnEntriesDBO.empJobDetailsDBO.branchIfscCode;
										} else
											employeeApplicantDTO.empJobDetailsDTO.isSibAccountAvailable = "false";
									}
									//Nominee Details
									if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.empPfGratuityNomineesDBOS)) {
										List<EmpPfGratuityNomineesDTO> pfDetailsDTO = new ArrayList<>();
										List<EmpPfGratuityNomineesDTO> gratuityDTO = new ArrayList<>();
										empApplnEntriesDBO.empJobDetailsDBO.empPfGratuityNomineesDBOS.forEach(empPfGratuityNomineesDBO -> {
											if (empPfGratuityNomineesDBO.recordStatus == 'A') {
												EmpPfGratuityNomineesDTO empPfGratuityNomineesDTO = new EmpPfGratuityNomineesDTO();
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.empPfGratuityNomineesId))
													empPfGratuityNomineesDTO.empPfGratuityNomineesId = String.valueOf(empPfGratuityNomineesDBO.empPfGratuityNomineesId);
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.empJobDetailsDBO) && !Utils.isNullOrEmpty(empPfGratuityNomineesDBO.empJobDetailsDBO.id))
													empPfGratuityNomineesDTO.empJobDetailsId = String.valueOf(empPfGratuityNomineesDBO.empJobDetailsDBO.id);
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.nominee))
													empPfGratuityNomineesDTO.nominee = empPfGratuityNomineesDBO.nominee;
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.nomineeAddress))
													empPfGratuityNomineesDTO.nomineeAddress = empPfGratuityNomineesDBO.nomineeAddress;
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.sharePercentage))
													empPfGratuityNomineesDTO.sharePercentage = String.valueOf(empPfGratuityNomineesDBO.sharePercentage);
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.nomineeRelationship))
													empPfGratuityNomineesDTO.nomineeRelationship = empPfGratuityNomineesDBO.nomineeRelationship;
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.under18GuardName))
													empPfGratuityNomineesDTO.under18GuardName = empPfGratuityNomineesDBO.under18GuardName;
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.under18GuardianAddress))
													empPfGratuityNomineesDTO.under18GuardianAddress = empPfGratuityNomineesDBO.under18GuardianAddress;
												if (!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.nomineeDob))
												//	empPfGratuityNomineesDTO.nomineeDob = Utils.convertLocalDateToStringDate(empPfGratuityNomineesDBO.nomineeDob);
												    empPfGratuityNomineesDTO.setNomineesDob(empPfGratuityNomineesDBO.nomineeDob);
												if(!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.getIsPf()) && empPfGratuityNomineesDBO.getIsPf()) {
													empPfGratuityNomineesDTO.setIsPf(empPfGratuityNomineesDBO.getIsPf());
													empPfGratuityNomineesDTO.setIsGratuity(empPfGratuityNomineesDBO.getIsGratuity());
													pfDetailsDTO.add(empPfGratuityNomineesDTO);
												} else if(!Utils.isNullOrEmpty(empPfGratuityNomineesDBO.getIsGratuity()) && empPfGratuityNomineesDBO.getIsGratuity()) {
													empPfGratuityNomineesDTO.setIsGratuity(empPfGratuityNomineesDBO.getIsGratuity());
													empPfGratuityNomineesDTO.setIsPf(empPfGratuityNomineesDBO.getIsPf());
													gratuityDTO.add(empPfGratuityNomineesDTO);
												}
											}
										});
										employeeApplicantDTO.setEmpPfNomineesDTO(pfDetailsDTO);
										employeeApplicantDTO.setEmpGratuityNomineesDTO(gratuityDTO);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Exception error) {
				error.printStackTrace();
			}
		});
		return employeeApplicantDTO;
	}

	public void submitEmployeeApplicationInterviewDetails(EmployeeApplicantDTO employeeApplicantDTO, int userId, ApiResult result) throws Exception {
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				try {
					if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnEntriesId)) {
						//Integer interviewRound = !Utils.isNullOrEmpty(employeeApplicantDTO.interviewRound) ? Integer.valueOf(employeeApplicantDTO.interviewRound) : null;
						List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
						if (!Utils.isNullOrEmpty(employeeApplicantDTO.interviewRound)) {
							//empApplnEntriesDBO = commonApiTransaction.find(EmpApplnEntriesDBO.class, employeeApplicantDTO.empApplnEntriesId);
							EmpApplnEntriesDBO empApplnEntriesDBO = context.find(EmpApplnEntriesDBO.class, employeeApplicantDTO.empApplnEntriesId);
							empApplnEntriesDBO.modifiedUsersId = userId;
							String processCode = null;
							boolean isAvailable = false;
					//		boolean isReschedulable = false;
							if ("1".equalsIgnoreCase(employeeApplicantDTO.interviewRound)) {
								EmpApplnPersonalDataDTO empApplnPersonalDataDTO = employeeApplicantDTO.empApplnPersonalDataDTO;
								ResearchDetailsDTO researchDetailDTO = employeeApplicantDTO.researchDetailDTO;
								Set<EmpApplnInterviewSchedulesDBO> schedulesDBOSet = new HashSet<>();
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnInterviewSchedulesId)) {
									EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO = context.find(EmpApplnInterviewSchedulesDBO.class, Integer.parseInt(employeeApplicantDTO.empApplnInterviewSchedulesId));
									//empApplnPersonalDataDBO = commonApiTransaction.find(EmpApplnPersonalDataDBO.class, empApplnPersonalDataDTO.empApplnPersonalDataId);
									//empApplnInterviewSchedulesDBO.empApplnInterviewSchedulesId = Integer.parseInt(employeeApplicantDTO.empApplnInterviewSchedulesId);
									empApplnInterviewSchedulesDBO.empApplnEntriesDBO = empApplnEntriesDBO;
									empApplnInterviewSchedulesDBO.modifiedUsersId = userId;
									empApplnInterviewSchedulesDBO.recordStatus = 'A';
									if (!Utils.isNullOrEmpty(employeeApplicantDTO.isAvailableForTheInterview) && "true".equalsIgnoreCase(employeeApplicantDTO.isAvailableForTheInterview)) {
										isAvailable = true;
										empApplnInterviewSchedulesDBO.isApplicantAvailable = true;
										empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = null;
										empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = null;
										if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO) && !Utils.isNullOrEmpty(empApplnPersonalDataDTO.isUanNo) && "true".equalsIgnoreCase(empApplnPersonalDataDTO.isUanNo)) {
											if (!Utils.isNullOrEmpty(empApplnPersonalDataDTO.empApplnPersonalDataId)) {
												EmpApplnPersonalDataDBO empApplnPersonalDataDBO = context.find(EmpApplnPersonalDataDBO.class, empApplnPersonalDataDTO.empApplnPersonalDataId);
												//empApplnPersonalDataDBO.empApplnPersonalDataId = empApplnPersonalDataDTO.empApplnPersonalDataId;
												empApplnPersonalDataDBO.empApplnEntriesDBO = empApplnEntriesDBO;
												empApplnPersonalDataDBO.modifiedUsersId = userId;
												empApplnPersonalDataDBO.recordStatus = 'A';
												empApplnPersonalDataDBO.uanNo = empApplnPersonalDataDTO.uanNo;
												empApplnEntriesDBO.empApplnPersonalDataDBO = empApplnPersonalDataDBO;
											}
										}
										if (!Utils.isNullOrEmpty(researchDetailDTO)) {
											Set<Integer> addtnlInfoEntriesDBOsIdSet = new HashSet<>();
											Set<EmpApplnAddtnlInfoEntriesDBO> addtnlInfoEntriesDBOSet = new HashSet<>();
											if (!Utils.isNullOrEmpty(researchDetailDTO.researchEntries)) {
												EmpApplnAddtnlInfoEntriesDTO empApplnAddtnlInfoEntriesDTO = researchDetailDTO.researchEntries;
												if (!Utils.isNullOrEmpty(empApplnAddtnlInfoEntriesDTO.researchEntriesHeadings)) {
													for (EmpApplnAddtnlInfoHeadingDTO empApplnAddtnlInfoHeadingDTO : empApplnAddtnlInfoEntriesDTO.researchEntriesHeadings) {
														if (!Utils.isNullOrEmpty(empApplnAddtnlInfoHeadingDTO.parameters) && !Utils.isNullOrEmpty(empApplnAddtnlInfoHeadingDTO.isTypeResearch) && !empApplnAddtnlInfoHeadingDTO.isTypeResearch) {
															for (EmpApplnAddtnlInfoParameterDTO empApplnAddtnlInfoParameterDTO : empApplnAddtnlInfoHeadingDTO.parameters) {
																if (!Utils.isNullOrEmpty(empApplnAddtnlInfoParameterDTO.isDisplayInApplication) && empApplnAddtnlInfoParameterDTO.isDisplayInApplication) {
																	EmpApplnAddtnlInfoEntriesDBO empApplnAddtnlInfoEntriesDBO = new EmpApplnAddtnlInfoEntriesDBO();
																	empApplnAddtnlInfoEntriesDBO.createdUsersId = userId;
																	empApplnAddtnlInfoEntriesDBO.empApplnEntriesDBO = empApplnEntriesDBO;
																	if (!Utils.isNullOrEmpty(empApplnAddtnlInfoParameterDTO.id)) {
																		empApplnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO = new EmpApplnAddtnlInfoParameterDBO();
																		empApplnAddtnlInfoEntriesDBO.empApplnAddtnlInfoParameterDBO.id = Integer.valueOf(empApplnAddtnlInfoParameterDTO.id);
																	}
																	empApplnAddtnlInfoEntriesDBO.addtnlInfoValue = empApplnAddtnlInfoParameterDTO.parameterValue;
																	empApplnAddtnlInfoEntriesDBO.recordStatus = 'A';
																	addtnlInfoEntriesDBOSet.add(empApplnAddtnlInfoEntriesDBO);
																}
															}
														}
													}
												}
												empApplnEntriesDBO.empApplnAddtnlInfoEntriesDBOs = addtnlInfoEntriesDBOSet;
											}
										}
									} else {
										empApplnInterviewSchedulesDBO.isApplicantAvailable = false;
										if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO)) {
											if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId)) {
												empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = null;
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = new EmpApplnNonAvailabilityDBO();
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId = Integer.parseInt(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId);
//												if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.isReschedulable) && "true".equalsIgnoreCase(employeeApplicantDTO.empApplnNonAvailabilityDTO.isReschedulable)) {
//													isReschedulable = true;
//												}
											} else if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName)) {
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = null;
												empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName;
											}
										}
									}
									schedulesDBOSet.add(empApplnInterviewSchedulesDBO);
									empApplnEntriesDBO.empApplnInterviewSchedulesDBOs = schedulesDBOSet;
								// 	processCode = isAvailable ? "EMP_R1_APPLICANT_ACCEPTED" : (isReschedulable ? "EMP_R1_APPLICANT_REQUEST_RESCHEDULE" : "EMP_R1_APPLICANT_DECLINED");
									processCode = isAvailable ? "EMP_STAGE1_APPLICANT_ACCEPTED" : "EMP_STAGE1_APPLICANT_DECLINED" ;
								}
							} else if ("2".equalsIgnoreCase(employeeApplicantDTO.interviewRound)) {
								Set<EmpApplnInterviewSchedulesDBO> schedulesDBOSet = new HashSet<>();
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnInterviewSchedulesId)) {
									EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO = commonApiTransaction.find(EmpApplnInterviewSchedulesDBO.class, Integer.parseInt(employeeApplicantDTO.empApplnInterviewSchedulesId));
									//empApplnInterviewSchedulesDBO.empApplnInterviewSchedulesId = Integer.parseInt(employeeApplicantDTO.empApplnInterviewSchedulesId);
									empApplnInterviewSchedulesDBO.empApplnEntriesDBO = empApplnEntriesDBO;
									empApplnInterviewSchedulesDBO.modifiedUsersId = userId;
									empApplnInterviewSchedulesDBO.recordStatus = 'A';
									if (!Utils.isNullOrEmpty(employeeApplicantDTO.isAvailableForTheInterview) && "true".equalsIgnoreCase(employeeApplicantDTO.isAvailableForTheInterview)) {
										isAvailable = true;
										empApplnInterviewSchedulesDBO.isApplicantAvailable = true;
										empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = null;
										empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = null;
									} else {
										empApplnInterviewSchedulesDBO.isApplicantAvailable = false;
										if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO)) {
											if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId)) {
												empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = null;
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = new EmpApplnNonAvailabilityDBO();
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId = Integer.parseInt(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId);
//												if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.isReschedulable) && "true".equalsIgnoreCase(employeeApplicantDTO.empApplnNonAvailabilityDTO.isReschedulable)) {
//													isReschedulable = true;
//												}
											} else if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName)) {
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = null;
												empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName;
											}
										}
									}
									schedulesDBOSet.add(empApplnInterviewSchedulesDBO);
									empApplnEntriesDBO.empApplnInterviewSchedulesDBOs = schedulesDBOSet;
							//		processCode = isAvailable ? "EMP_R2_APPLICANT_ACCEPTED" : (isReschedulable ? "EMP_R2_APPLICANT_REQUEST_RESCHEDULE" : "EMP_R2_APPLICANT_DECLINED");
									processCode = isAvailable ? "EMP_STAGE2_SCHEDULE_APPLICANT_ACCEPTED" : "EMP_STAGE2_SCHEDULE_APPLICANT_DECLINED";
								}
							} else if ("3".equalsIgnoreCase(employeeApplicantDTO.interviewRound)) {
								Set<EmpApplnInterviewSchedulesDBO> schedulesDBOSet = new HashSet<>();
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnInterviewSchedulesId)) {
									EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO = commonApiTransaction.find(EmpApplnInterviewSchedulesDBO.class, Integer.parseInt(employeeApplicantDTO.empApplnInterviewSchedulesId));
									empApplnInterviewSchedulesDBO.empApplnEntriesDBO = empApplnEntriesDBO;
									empApplnInterviewSchedulesDBO.modifiedUsersId = userId;
									empApplnInterviewSchedulesDBO.recordStatus = 'A';
									if (!Utils.isNullOrEmpty(employeeApplicantDTO.isAvailableForTheInterview) && "true".equalsIgnoreCase(employeeApplicantDTO.isAvailableForTheInterview)) {
										isAvailable = true;
										empApplnInterviewSchedulesDBO.isApplicantAvailable = true;
										empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = null;
										empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = null;
									} else {
										empApplnInterviewSchedulesDBO.isApplicantAvailable = false;
										if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO)) {
											if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId)) {
												empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = null;
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = new EmpApplnNonAvailabilityDBO();
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId = Integer.parseInt(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId);
											} else if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName)) {
												empApplnInterviewSchedulesDBO.empApplnNonAvailabilityDBO = null;
												empApplnInterviewSchedulesDBO.reasonForNonAvailabilityOthers = employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName;
											}
										}
									}
									schedulesDBOSet.add(empApplnInterviewSchedulesDBO);
									empApplnEntriesDBO.empApplnInterviewSchedulesDBOs = schedulesDBOSet;
									processCode = isAvailable ? "EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED" : "EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED";
								}

							} else if ("selected".equalsIgnoreCase(employeeApplicantDTO.interviewRound)) {
								//SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
								//SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
								EducationalDetailsDTO educationalDetailDTO = employeeApplicantDTO.educationalDetailDTO;
								EmpAddtnlPersonalDataDTO additionalPersonalDataDTO = employeeApplicantDTO.additionalPersonalDataDTO;
								EmpJobDetailsDTO empJobDetailsDTO = employeeApplicantDTO.empJobDetailsDTO;
								List<EmpPfGratuityNomineesDTO> empPfGratuityNomineesDTOs = new ArrayList<EmpPfGratuityNomineesDTO>();
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.getEmpPfNomineesDTO())) {
									empPfGratuityNomineesDTOs.addAll(employeeApplicantDTO.getEmpPfNomineesDTO());
								}
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.getEmpGratuityNomineesDTO())) {
									empPfGratuityNomineesDTOs.addAll(employeeApplicantDTO.getEmpGratuityNomineesDTO());
								}
								List<EmpFamilyDetailsAddtnlDTO> empFamilyDetailsAddtnlDTO = new ArrayList<EmpFamilyDetailsAddtnlDTO>();
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.getFamilyDetailsAddtnlList())) {
									empFamilyDetailsAddtnlDTO.addAll(employeeApplicantDTO.getFamilyDetailsAddtnlList());
								}
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.getDependentDetailsAddtnlList())) {
									empFamilyDetailsAddtnlDTO.addAll(employeeApplicantDTO.getDependentDetailsAddtnlList());
								}
								if (!Utils.isNullOrEmpty(employeeApplicantDTO.isAcceptOffer) && "false".equalsIgnoreCase(employeeApplicantDTO.isAcceptOffer)) {
									if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO)) {
										if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId)) {
											empApplnEntriesDBO.jobRejectionReason = null;
											empApplnEntriesDBO.empApplnNonAvailabilityDBO = new EmpApplnNonAvailabilityDBO();
											empApplnEntriesDBO.empApplnNonAvailabilityDBO.empApplnNonAvailabilityId = Integer.parseInt(employeeApplicantDTO.empApplnNonAvailabilityDTO.empApplnNonAvailabilityId);
										} else if (!Utils.isNullOrEmpty(employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName)) {
											empApplnEntriesDBO.empApplnNonAvailabilityDBO = null;
											empApplnEntriesDBO.jobRejectionReason = employeeApplicantDTO.empApplnNonAvailabilityDTO.nonAvailabilityName;
										}
									}
								} else {
									isAvailable = true;
									//Educational Details
									if (!Utils.isNullOrEmpty(educationalDetailDTO)) {
										Set<EmpApplnEducationalDetailsDBO> applnEducationalDetailsDBOs = new HashSet<>();
										Set<Integer> applnEducationalDetailsDBOsIdSet = new HashSet<>();
										Set<EmpApplnEducationalDetailsDocumentsDBO> documentsDBOSet = new HashSet<>();
										Set<Integer> educationalDetailsDocumentDBOIds = new HashSet<>();
										if (!Utils.isNullOrEmpty(educationalDetailDTO.qualificationLevelsList)) {
											for (EmpApplnEducationalDetailsDTO empApplnEducationalDetailsDTO : educationalDetailDTO.qualificationLevelsList) {
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.qualificationLevelId)) {
													EmpApplnEducationalDetailsDBO empApplnEducationalDetailsDBO = new EmpApplnEducationalDetailsDBO();
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId)) {
														empApplnEducationalDetailsDBO = context.find(EmpApplnEducationalDetailsDBO.class, empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
														empApplnEducationalDetailsDBO.modifiedUsersId = userId;
														applnEducationalDetailsDBOsIdSet.add(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
													} else {
														empApplnEducationalDetailsDBO.createdUsersId = userId;
													}
													empApplnEducationalDetailsDBO.empApplnEntriesDBO = empApplnEntriesDBO;
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.countryId)) {
														empApplnEducationalDetailsDBO.erpCountryDBO = new ErpCountryDBO();
														empApplnEducationalDetailsDBO.erpCountryDBO.id = Integer.parseInt(empApplnEducationalDetailsDTO.countryId);
													}
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.stateId)) {
														empApplnEducationalDetailsDBO.erpStateDBO = new ErpStateDBO();
														empApplnEducationalDetailsDBO.erpStateDBO.id = Integer.parseInt(empApplnEducationalDetailsDTO.stateId);
													}
													if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpBoardOrUniversity())) {
														empApplnEducationalDetailsDBO.setErpUniversityBoardDBO(new ErpUniversityBoardDBO());
														empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getErpBoardOrUniversity().getValue()));
													}
													if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpInstitutionDBO())) {
														empApplnEducationalDetailsDBO.setErpInstitutionDBO(new ErpInstitutionDBO());
														empApplnEducationalDetailsDBO.getErpInstitutionDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getErpInstitute().getValue()));
													}
													documentsDBOSet = new HashSet<EmpApplnEducationalDetailsDocumentsDBO>();
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.documentList)) {
														Map<Integer, EmpApplnEducationalDetailsDocumentsDBO> docMap = !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getDocumentsDBOSet())
																?empApplnEducationalDetailsDBO.getDocumentsDBOSet().stream()
																		.collect(Collectors.toMap(emp->emp.getId(), emp->emp)):new HashMap<Integer, EmpApplnEducationalDetailsDocumentsDBO>();
												
														for (EmpApplnEducationalDetailsDocumentsDTO empApplnEducationalDetailsDocumentsDTO : empApplnEducationalDetailsDTO.documentList) {
															/*EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
															if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId)) {
																empApplnEducationalDetailsDocumentsDBO.id = empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId;
																empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
																educationalDetailsDocumentDBOIds.add(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsDocumentsId);
															} else {
																empApplnEducationalDetailsDocumentsDBO.createdUsersId = userId;
															}
															if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId)) {
																empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO = new EmpApplnEducationalDetailsDBO();
																empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO.empApplnEducationalDetailsId = empApplnEducationalDetailsDocumentsDTO.empApplnEducationalDetailsId;
															} else {
																empApplnEducationalDetailsDocumentsDBO.empApplnEducationalDetailsDBO = empApplnEducationalDetailsDBO;
															}
															if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl)) {
																empApplnEducationalDetailsDocumentsDBO.educationalDocumentsUrl = empApplnEducationalDetailsDocumentsDTO.educationalDocumentsUrl;
															}*/
															EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
															if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()) 
																	&& !Utils.isNullOrEmpty(docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()))){
																empApplnEducationalDetailsDocumentsDBO = docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
																empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
																educationalDetailsDocumentDBOIds.add(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
															} else {
																empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
																empApplnEducationalDetailsDocumentsDBO.setEmpApplnEducationalDetailsDBO(empApplnEducationalDetailsDBO);
																empApplnEducationalDetailsDocumentsDBO.createdUsersId = userId;
															}
															
															UrlAccessLinkDBO documentsUrlDBO;
															if(Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())){
																documentsUrlDBO = new UrlAccessLinkDBO();
																documentsUrlDBO.setCreatedUsersId(userId);
																documentsUrlDBO.setRecordStatus('A');
															}
															else {
																documentsUrlDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
																documentsUrlDBO.setModifiedUsersId(userId);
															}
															if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getNewFile()) && empApplnEducationalDetailsDocumentsDTO.getNewFile()) {
																documentsUrlDBO = createURLAccessLinkDBO(documentsUrlDBO, empApplnEducationalDetailsDocumentsDTO.getProcessCode(),empApplnEducationalDetailsDocumentsDTO.getUniqueFileName(), empApplnEducationalDetailsDocumentsDTO.getOriginalFileName(), userId, "");
																if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getUniqueFileName())) {
																	uniqueFileNameList.addAll(createFileListForActualCopy(empApplnEducationalDetailsDocumentsDTO.getProcessCode(), empApplnEducationalDetailsDocumentsDTO.getUniqueFileName()));
																}
															}
															empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(documentsUrlDBO);
															empApplnEducationalDetailsDocumentsDBO.recordStatus = 'A';
															documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
														}
													}
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.documentsDBOSet)) {
														for (EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO : empApplnEducationalDetailsDBO.documentsDBOSet) {
															if (!educationalDetailsDocumentDBOIds.contains(empApplnEducationalDetailsDocumentsDBO.id)) {
																UrlAccessLinkDBO urlAccessLinkDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
																if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
																	urlAccessLinkDBO.setRecordStatus('D');
																	urlAccessLinkDBO.setModifiedUsersId(userId);
																	empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(urlAccessLinkDBO);
																}
																empApplnEducationalDetailsDocumentsDBO.recordStatus = 'D';
																empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
																documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
															}
														}
													}
													empApplnEducationalDetailsDBO.documentsDBOSet = documentsDBOSet;
													empApplnEducationalDetailsDBO.recordStatus = 'A';
													applnEducationalDetailsDBOs.add(empApplnEducationalDetailsDBO);
												}
											}

										}
										/*if (!Utils.isNullOrEmpty(educationalDetailDTO.otherQualificationLevelsList)) {
											for (EmpApplnEducationalDetailsDTO empApplnEducationalDetailsDTO : educationalDetailDTO.otherQualificationLevelsList) {
												if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.qualificationOthers)) {
													EmpApplnEducationalDetailsDBO empApplnEducationalDetailsDBO = new EmpApplnEducationalDetailsDBO();
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId)) {
														//empApplnEducationalDetailsDBO = commonApiTransaction.find(EmpApplnEducationalDetailsDBO.class, empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
														empApplnEducationalDetailsDBO = context.find(EmpApplnEducationalDetailsDBO.class, empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
														//empApplnEducationalDetailsDBO.empApplnEducationalDetailsId = empApplnEducationalDetailsDTO.empApplnEducationalDetailsId;
														empApplnEducationalDetailsDBO.modifiedUsersId = userId;
														applnEducationalDetailsDBOsIdSet.add(empApplnEducationalDetailsDTO.empApplnEducationalDetailsId);
													} else {
														empApplnEducationalDetailsDBO.createdUsersId = userId;
													}
													empApplnEducationalDetailsDBO.empApplnEntriesDBO = empApplnEntriesDBO;
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.countryId)) {
														empApplnEducationalDetailsDBO.erpCountryDBO = new ErpCountryDBO();
														empApplnEducationalDetailsDBO.erpCountryDBO.id = Integer.parseInt(empApplnEducationalDetailsDTO.countryId);
													}
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.stateId)) {
														empApplnEducationalDetailsDBO.erpStateDBO = new ErpStateDBO();
														empApplnEducationalDetailsDBO.erpStateDBO.id = Integer.parseInt(empApplnEducationalDetailsDTO.stateId);
													}
													if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.getErpBoardOrUniversity())) {
														empApplnEducationalDetailsDBO.setErpUniversityBoardDBO(new ErpUniversityBoardDBO());
														empApplnEducationalDetailsDBO.getErpUniversityBoardDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getErpBoardOrUniversity().getValue()));
													}
													if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getErpInstitutionDBO())) {
														empApplnEducationalDetailsDBO.setErpInstitutionDBO(new ErpInstitutionDBO());
														empApplnEducationalDetailsDBO.getErpInstitutionDBO().setId(Integer.parseInt(empApplnEducationalDetailsDTO.getErpInstitute().getValue()));
													}
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDTO.documentList)) {
														Map<Integer, EmpApplnEducationalDetailsDocumentsDBO> docMap = !Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.getDocumentsDBOSet())
																?empApplnEducationalDetailsDBO.getDocumentsDBOSet().stream()
																		.collect(Collectors.toMap(emp->emp.getId(), emp->emp)):new HashMap<Integer, EmpApplnEducationalDetailsDocumentsDBO>();
						
														for (EmpApplnEducationalDetailsDocumentsDTO empApplnEducationalDetailsDocumentsDTO : empApplnEducationalDetailsDTO.documentList) {
															EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
															if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()) 
																	&& !Utils.isNullOrEmpty(docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId()))){
																empApplnEducationalDetailsDocumentsDBO = docMap.get(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
																empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
																educationalDetailsDocumentDBOIds.add(empApplnEducationalDetailsDocumentsDTO.getEmpApplnEducationalDetailsDocumentsId());
															} else {
																empApplnEducationalDetailsDocumentsDBO = new EmpApplnEducationalDetailsDocumentsDBO();
																empApplnEducationalDetailsDocumentsDBO.setEmpApplnEducationalDetailsDBO(empApplnEducationalDetailsDBO);
																empApplnEducationalDetailsDocumentsDBO.createdUsersId = userId;
																empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
															}
															UrlAccessLinkDBO documentsUrlDBO;
															if(Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())){
																documentsUrlDBO = new UrlAccessLinkDBO();
																documentsUrlDBO.setCreatedUsersId(userId);
																documentsUrlDBO.setRecordStatus('A');
															}else {
																documentsUrlDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
																documentsUrlDBO.setModifiedUsersId(userId);
																documentsUrlDBO.setRecordStatus('A');
															}
															if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getNewFile()) && empApplnEducationalDetailsDocumentsDTO.getNewFile()) {
																documentsUrlDBO = createURLAccessLinkDBO(documentsUrlDBO, empApplnEducationalDetailsDocumentsDTO.getProcessCode(),empApplnEducationalDetailsDocumentsDTO.getUniqueFileName(), empApplnEducationalDetailsDocumentsDTO.getOriginalFileName(), userId, "");
																if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getProcessCode()) && !Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDTO.getUniqueFileName())) {
																	uniqueFileNameList.addAll(createFileListForActualCopy(empApplnEducationalDetailsDocumentsDTO.getProcessCode(), empApplnEducationalDetailsDocumentsDTO.getUniqueFileName()));
																}
															}
															empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(documentsUrlDBO);
															documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
														}
													}
													if (!Utils.isNullOrEmpty(empApplnEducationalDetailsDBO.documentsDBOSet)) {
														for (EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO : empApplnEducationalDetailsDBO.documentsDBOSet) {
															if (!educationalDetailsDocumentDBOIds.contains(empApplnEducationalDetailsDocumentsDBO.id)) {
																empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
																empApplnEducationalDetailsDocumentsDBO.recordStatus = 'D';
																if(!Utils.isNullOrEmpty(empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO())) {
																	UrlAccessLinkDBO urlAccessLinkDBO = empApplnEducationalDetailsDocumentsDBO.getEducationalDocumentsUrlDBO();
																	if(!Utils.isNullOrEmpty(urlAccessLinkDBO)){
																		urlAccessLinkDBO.setRecordStatus('D');
																		urlAccessLinkDBO.setModifiedUsersId(userId);
																	}
																	empApplnEducationalDetailsDocumentsDBO.setEducationalDocumentsUrlDBO(urlAccessLinkDBO);
																}
																documentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
															}
														}
													}
													empApplnEducationalDetailsDBO.documentsDBOSet = documentsDBOSet;
													empApplnEducationalDetailsDBO.recordStatus = 'A';
													applnEducationalDetailsDBOs.add(empApplnEducationalDetailsDBO);
												}
											}
										}*/
										/*if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnEducationalDetailsDBOs)) {
											for (EmpApplnEducationalDetailsDBO educationalDetailsDBO : empApplnEntriesDBO.empApplnEducationalDetailsDBOs) {
												if (!applnEducationalDetailsDBOsIdSet.contains(educationalDetailsDBO.empApplnEducationalDetailsId)) {
													if (!Utils.isNullOrEmpty(educationalDetailsDBO.documentsDBOSet)) {
														Set<EmpApplnEducationalDetailsDocumentsDBO> empApplnEducationalDetailsDocumentsDBOSet = new HashSet<>();
														for (EmpApplnEducationalDetailsDocumentsDBO empApplnEducationalDetailsDocumentsDBO : educationalDetailsDBO.documentsDBOSet) {
															empApplnEducationalDetailsDocumentsDBO.modifiedUsersId = userId;
															empApplnEducationalDetailsDocumentsDBOSet.add(empApplnEducationalDetailsDocumentsDBO);
														}
														educationalDetailsDBO.documentsDBOSet = empApplnEducationalDetailsDocumentsDBOSet;
													}
													educationalDetailsDBO.modifiedUsersId = userId;
													applnEducationalDetailsDBOs.add(educationalDetailsDBO);
												}
											}
										}*/
										if (!Utils.isNullOrEmpty(applnEducationalDetailsDBOs))
											empApplnEntriesDBO.empApplnEducationalDetailsDBOs = applnEducationalDetailsDBOs;
									}
									//additional Details
									if (!Utils.isNullOrEmpty(additionalPersonalDataDTO)) {
										EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO = new EmpAddtnlPersonalDataDBO();
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.empAddtnlPersonalDataId)) {
											empAddtnlPersonalDataDBO = context.find(EmpAddtnlPersonalDataDBO.class, Integer.parseInt(additionalPersonalDataDTO.empAddtnlPersonalDataId));
											empAddtnlPersonalDataDBO.modifiedUsersId = userId;
										} else {
											empAddtnlPersonalDataDBO.createdUsersId = userId;
										}
										empAddtnlPersonalDataDBO.recordStatus = 'A';
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.empApplnPersonalDataId)) {
											empAddtnlPersonalDataDBO.empApplnPersonalDataDBO = new EmpApplnPersonalDataDBO();
											empAddtnlPersonalDataDBO.empApplnPersonalDataDBO.empApplnPersonalDataId = Integer.parseInt(additionalPersonalDataDTO.empApplnPersonalDataId);
										} else {
											empAddtnlPersonalDataDBO.empApplnPersonalDataDBO = empApplnEntriesDBO.empApplnPersonalDataDBO;
										}
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.panNo))
											empAddtnlPersonalDataDBO.panNo = additionalPersonalDataDTO.panNo;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.fourWheelerNo))
											empAddtnlPersonalDataDBO.fourWheelerNo = additionalPersonalDataDTO.fourWheelerNo;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.twoWheelerNo))
											empAddtnlPersonalDataDBO.twoWheelerNo = additionalPersonalDataDTO.twoWheelerNo;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.emergencyContactName))
											empAddtnlPersonalDataDBO.emergencyContactName = additionalPersonalDataDTO.emergencyContactName;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.emergencyContactAddress))
											empAddtnlPersonalDataDBO.emergencyContactAddress = additionalPersonalDataDTO.emergencyContactAddress;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.emergencyContactRelatonship))
											empAddtnlPersonalDataDBO.emergencyContactRelationship = additionalPersonalDataDTO.emergencyContactRelatonship;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.emergencyMobileNo))
											empAddtnlPersonalDataDBO.emergencyMobileNo = additionalPersonalDataDTO.emergencyMobileNo;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.emergencyContactHome))
											empAddtnlPersonalDataDBO.emergencyContactHome = additionalPersonalDataDTO.emergencyContactHome;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.emergencyContactWork))
											empAddtnlPersonalDataDBO.emergencyContactWork = additionalPersonalDataDTO.emergencyContactWork;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.passportNo))
											empAddtnlPersonalDataDBO.passportNo = additionalPersonalDataDTO.passportNo;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.passportIssuedDate)) {
											LocalDate passportIssuedDate = Utils.convertStringDateTimeToLocalDate(additionalPersonalDataDTO.passportIssuedDate);
											empAddtnlPersonalDataDBO.passportIssuedDate = passportIssuedDate;
										}
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.passportStatus))
											empAddtnlPersonalDataDBO.passportStatus = additionalPersonalDataDTO.passportStatus;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.passportDateOfExpiry)) {
											LocalDate passportDateOfExpiry = Utils.convertStringDateTimeToLocalDate(additionalPersonalDataDTO.passportDateOfExpiry);
											empAddtnlPersonalDataDBO.passportDateOfExpiry = passportDateOfExpiry;
										}
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.passportComments))
											empAddtnlPersonalDataDBO.passportComments = additionalPersonalDataDTO.passportComments;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.visaNo))
											empAddtnlPersonalDataDBO.visaNo = additionalPersonalDataDTO.visaNo;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.visaIssuedDate)) {
											LocalDate visaIssuedDate = Utils.convertStringDateTimeToLocalDate(additionalPersonalDataDTO.visaIssuedDate);
											empAddtnlPersonalDataDBO.visaIssuedDate = visaIssuedDate;
										}
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.visaStatus))
											empAddtnlPersonalDataDBO.visaStatus = additionalPersonalDataDTO.visaStatus;
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.visaDateOfExpiry)) {
											LocalDate visaDateOfExpiry = Utils.convertStringDateTimeToLocalDate(additionalPersonalDataDTO.visaDateOfExpiry);
											empAddtnlPersonalDataDBO.visaDateOfExpiry = visaDateOfExpiry;
										}
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.visaComments))
											empAddtnlPersonalDataDBO.visaComments = additionalPersonalDataDTO.visaComments;

										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.isAadharAvailable)) {
											if ("true".equalsIgnoreCase(additionalPersonalDataDTO.isAadharAvailable)) {
												empAddtnlPersonalDataDBO.isAadharAvailable = true;
												if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.aadharNo))
													empAddtnlPersonalDataDBO.aadharNo = additionalPersonalDataDTO.aadharNo;
											} else {
												empAddtnlPersonalDataDBO.isAadharAvailable = false;
												if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.isAadharEnrolled)) {
													if ("true".equalsIgnoreCase(additionalPersonalDataDTO.isAadharEnrolled)) {
														empAddtnlPersonalDataDBO.isAadharEnrolled = true;
														if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.aadharEnrolledNo))
															empAddtnlPersonalDataDBO.aadharEnrolledNo = additionalPersonalDataDTO.aadharEnrolledNo;
													} else {
														empAddtnlPersonalDataDBO.isAadharEnrolled = false;
														empAddtnlPersonalDataDBO.aadharEnrolledNo = null;
													}
												}
											}
										}
										if (!Utils.isNullOrEmpty(additionalPersonalDataDTO.familyBackgroundBrief))
											empAddtnlPersonalDataDBO.familyBackgroundBrief = additionalPersonalDataDTO.familyBackgroundBrief;
										empApplnEntriesDBO.empApplnPersonalDataDBO.empAddtnlPersonalDataDBO = empAddtnlPersonalDataDBO;
										if(!Utils.isNullOrEmpty(employeeApplicantDTO.getMaritalStatusDTO())) {
											empApplnEntriesDBO.empApplnPersonalDataDBO.setErpMaritalStatusDBO(new ErpMaritalStatusDBO());
											empApplnEntriesDBO.empApplnPersonalDataDBO.getErpMaritalStatusDBO().setId(Integer.parseInt(employeeApplicantDTO.getMaritalStatusDTO().getValue()));
										}
									}
									//Job Details
									EmpJobDetailsDBO empJobDetailsDBO = new EmpJobDetailsDBO();
									if (!Utils.isNullOrEmpty(empJobDetailsDTO)) {					
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.empJobDetailsId)) {
											empJobDetailsDBO = context.find(EmpJobDetailsDBO.class, Integer.parseInt(empJobDetailsDTO.empJobDetailsId));
											empJobDetailsDBO.modifiedUsersId = userId;
										} else {
											empJobDetailsDBO.createdUsersId = userId;
										}
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.getIsJoiningDateConfirmed())) {
											empJobDetailsDBO.setIsJoiningDateConfirmed(empJobDetailsDTO.getIsJoiningDateConfirmed());
										}
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.getJoiningDateRejectReason())) {
											empJobDetailsDBO.setJoiningDateRejectReason(empJobDetailsDTO.getJoiningDateRejectReason());
										} 
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.getPreferedJoiningDateTime())) {
											empJobDetailsDBO.setPreferedJoiningDateTime(empJobDetailsDTO.getPreferedJoiningDateTime());
										}
										empJobDetailsDBO.recordStatus = 'A';
										empJobDetailsDBO.empApplnEntriesId = empApplnEntriesDBO;
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.uanNo))
											empJobDetailsDBO.uanNo = empJobDetailsDTO.uanNo;
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.pfAccountNo))
											empJobDetailsDBO.pfAccountNo = empJobDetailsDTO.pfAccountNo;
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.pfDate)) {
											empJobDetailsDBO.pfDate = empJobDetailsDTO.pfDate;
										}
										if(!Utils.isNullOrEmpty(empJobDetailsDTO.gratuityNo)) {
											empJobDetailsDBO.gratuityNo = empJobDetailsDTO.gratuityNo;
									     }
									    if(!Utils.isNullOrEmpty(empJobDetailsDTO.gratuityDate)) {
											empJobDetailsDBO.gratuityDate = empJobDetailsDTO.gratuityDate;
									     }
										if (!Utils.isNullOrEmpty(empJobDetailsDTO.isSibAccountAvailable)) {
											if ("true".equalsIgnoreCase(empJobDetailsDTO.isSibAccountAvailable)) {
												empJobDetailsDBO.isSibAccountAvailable = true;
												if (!Utils.isNullOrEmpty(empJobDetailsDTO.sibAccountBank))
													empJobDetailsDBO.sibAccountBank = empJobDetailsDTO.sibAccountBank;
												if (!Utils.isNullOrEmpty(empJobDetailsDTO.ifscCode))
													empJobDetailsDBO.branchIfscCode = empJobDetailsDTO.ifscCode;
											} else {
												empJobDetailsDBO.isSibAccountAvailable = false;
											}
										}
										empApplnEntriesDBO.empJobDetailsDBO = empJobDetailsDBO;
									}
									//Nominee details
									if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTOs)) {
										Set<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOS = new HashSet<EmpPfGratuityNomineesDBO>();
										Set<Integer> pfGratuityNomineesDBOSIdSet = new HashSet<>();
										for (EmpPfGratuityNomineesDTO empPfGratuityNomineesDTO : empPfGratuityNomineesDTOs) {
											EmpPfGratuityNomineesDBO empPfGratuityNomineesDBO = new EmpPfGratuityNomineesDBO();
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.empPfGratuityNomineesId)) {
												empPfGratuityNomineesDBO = context.find(EmpPfGratuityNomineesDBO.class, Integer.parseInt(empPfGratuityNomineesDTO.empPfGratuityNomineesId));
												empPfGratuityNomineesDBO.modifiedUsersId = userId;
												pfGratuityNomineesDBOSIdSet.add(Integer.parseInt(empPfGratuityNomineesDTO.empPfGratuityNomineesId));
											} else {
												empPfGratuityNomineesDBO.createdUsersId = userId;
											}
											empPfGratuityNomineesDBO.recordStatus = 'A';
											empPfGratuityNomineesDBO.empJobDetailsDBO = empJobDetailsDBO;
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.nominee))
												empPfGratuityNomineesDBO.nominee = empPfGratuityNomineesDTO.nominee;
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.nomineeAddress))
												empPfGratuityNomineesDBO.nomineeAddress = empPfGratuityNomineesDTO.nomineeAddress;
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.sharePercentage))
												empPfGratuityNomineesDBO.sharePercentage = new BigDecimal(empPfGratuityNomineesDTO.sharePercentage);
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.nomineeRelationship))
												empPfGratuityNomineesDBO.nomineeRelationship = empPfGratuityNomineesDTO.nomineeRelationship;
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.under18GuardName))
												empPfGratuityNomineesDBO.under18GuardName = empPfGratuityNomineesDTO.under18GuardName;
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.under18GuardianAddress))
												empPfGratuityNomineesDBO.under18GuardianAddress = empPfGratuityNomineesDTO.under18GuardianAddress;
											if (!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.getNomineesDob())) {
												empPfGratuityNomineesDBO.setNomineeDob(empPfGratuityNomineesDTO.getNomineesDob());
//												LocalDate nomineeDob = Utils.convertStringDateTimeToLocalDate(empPfGratuityNomineesDTO.nomineeDob);
//												empPfGratuityNomineesDBO.nomineeDob = nomineeDob;
											}
											if(!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.getIsPf())) {
												if(empPfGratuityNomineesDTO.getIsPf().equals(true)) {
													empPfGratuityNomineesDBO.setIsPf(true); 
													empPfGratuityNomineesDBO.setIsGratuity(false);
												}
											}
											if(!Utils.isNullOrEmpty(empPfGratuityNomineesDTO.getIsGratuity())) {
												if(empPfGratuityNomineesDTO.getIsGratuity().equals(true)) {
													empPfGratuityNomineesDBO.setIsGratuity(true);
													empPfGratuityNomineesDBO.setIsPf(false); 
												}
											}
											empPfGratuityNomineesDBOS.add(empPfGratuityNomineesDBO);
										}
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empJobDetailsDBO.empPfGratuityNomineesDBOS)) {
											for (EmpPfGratuityNomineesDBO empPfGratuityNomineesDBO : empApplnEntriesDBO.empJobDetailsDBO.empPfGratuityNomineesDBOS) {
												if (!pfGratuityNomineesDBOSIdSet.contains(empPfGratuityNomineesDBO.empPfGratuityNomineesId)) {
													empPfGratuityNomineesDBO.setRecordStatus('D');
													empPfGratuityNomineesDBO.modifiedUsersId = userId;
													empPfGratuityNomineesDBOS.add(empPfGratuityNomineesDBO);
												}
											}
										}
										if (!empPfGratuityNomineesDBOS.isEmpty())
											empApplnEntriesDBO.empJobDetailsDBO.empPfGratuityNomineesDBOS = empPfGratuityNomineesDBOS;
									}
									//Family Dependent
									if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO)) {
										Set<EmpFamilyDetailsAddtnlDBO> empFamilyDetailsAddtnlDBOS = new HashSet<>();
										Set<Integer> familyDetailsDboId = new HashSet<>();
										empFamilyDetailsAddtnlDTO.forEach(familyDetails -> {
											//EmpPfGratuityNomineesDBO empPfGratuityNomineesDBO = new EmpPfGratuityNomineesDBO();
											EmpFamilyDetailsAddtnlDBO familyDetailsDbo = new EmpFamilyDetailsAddtnlDBO();
											if (!Utils.isNullOrEmpty(familyDetails.empFamilyDetailsAddtnlId)) {
												familyDetailsDbo = context.find(EmpFamilyDetailsAddtnlDBO.class, Integer.parseInt(familyDetails.empFamilyDetailsAddtnlId));
												familyDetailsDbo.modifiedUsersId = userId;
												familyDetailsDboId.add(Integer.parseInt(familyDetails.empFamilyDetailsAddtnlId));
											} else {
												familyDetailsDbo.createdUsersId = userId;
											}
											if (!Utils.isNullOrEmpty(familyDetails.getDependDOB())) {
												familyDetailsDbo.setDependentDob(familyDetails.getDependDOB());
											}
											if (!Utils.isNullOrEmpty(familyDetails.getDependentName())) {
												familyDetailsDbo.setDependentName(familyDetails.getDependentName());
											}
											if (!Utils.isNullOrEmpty(familyDetails.getDependentProfession())) {
												familyDetailsDbo.setDependentProfession(familyDetails.getDependentProfession());
											}
											if (!Utils.isNullOrEmpty(familyDetails.getDependentQualification())) {
												familyDetailsDbo.setDependentQualification(familyDetails.getDependentQualification());
											}
											if (!Utils.isNullOrEmpty(familyDetails.getEmpApplnPersonalDataId())) {
												familyDetailsDbo.setEmpApplnPersonalDataDBO(new EmpApplnPersonalDataDBO());
												familyDetailsDbo.getEmpApplnPersonalDataDBO().setEmpApplnPersonalDataId(Integer.valueOf(familyDetails.getEmpApplnPersonalDataId()));
											}
											if (!Utils.isNullOrEmpty(familyDetails.getRelationship())) {
												familyDetailsDbo.setRelationship(familyDetails.getRelationship());
											}
											familyDetailsDbo.setRecordStatus('A');
											familyDetailsDbo.setCreatedUsersId(Integer.valueOf(userId));
											empFamilyDetailsAddtnlDBOS.add(familyDetailsDbo);
										});
										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getEmpFamilyDetailsAddtnlDBOS())) {
											for (EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO : empApplnEntriesDBO.getEmpApplnPersonalDataDBO().getEmpFamilyDetailsAddtnlDBOS()) {
												if (!familyDetailsDboId.contains(empFamilyDetailsAddtnlDBO.empFamilyDetailsAddtnlId)) {
													empFamilyDetailsAddtnlDBO.setRecordStatus('D');
													empFamilyDetailsAddtnlDBO.modifiedUsersId = userId;
													empFamilyDetailsAddtnlDBOS.add(empFamilyDetailsAddtnlDBO);
												}
											}
										}
										if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBOS)) {
											empApplnEntriesDBO.getEmpApplnPersonalDataDBO().setEmpFamilyDetailsAddtnlDBOS(new HashSet<EmpFamilyDetailsAddtnlDBO>());
											empApplnEntriesDBO.getEmpApplnPersonalDataDBO().setEmpFamilyDetailsAddtnlDBOS(empFamilyDetailsAddtnlDBOS);
										}
									}
									//Family Dependent
//									if (!Utils.isNullOrEmpty(employeeApplicantDTO.familyDependentDTO)) {
//										Set<EmpFamilyDetailsAddtnlDBO> empFamilyDetailsAddtnlDBOS = new HashSet<>();
//										Set<Integer> familyDependentDTOsIdSet = new HashSet<>();
//										for (FamilyDependentDTO familyDependentDTO : employeeApplicantDTO.familyDependentDTO) {
//											if (!Utils.isNullOrEmpty(familyDependentDTO.relationship)) {
//												if (!Utils.isNullOrEmpty(familyDependentDTO.empFamilyDetailsAddtnlDTOs)) {
//													for (EmpFamilyDetailsAddtnlDTO empFamilyDetailsAddtnlDTO : familyDependentDTO.empFamilyDetailsAddtnlDTOs) {
//														EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO = new EmpFamilyDetailsAddtnlDBO();
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.empFamilyDetailsAddtnlId)) {
//															empFamilyDetailsAddtnlDBO = context.find(EmpFamilyDetailsAddtnlDBO.class, Integer.parseInt(empFamilyDetailsAddtnlDTO.empFamilyDetailsAddtnlId));
//															empFamilyDetailsAddtnlDBO.modifiedUsersId = userId;
//															familyDependentDTOsIdSet.add(Integer.parseInt(empFamilyDetailsAddtnlDTO.empFamilyDetailsAddtnlId));
//														} else
//															empFamilyDetailsAddtnlDBO.createdUsersId = userId;
//														empFamilyDetailsAddtnlDBO.recordStatus = 'A';
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.empApplnPersonalDataId)) {
//															empFamilyDetailsAddtnlDBO.empApplnPersonalDataDBO = new EmpApplnPersonalDataDBO();
//															empFamilyDetailsAddtnlDBO.empApplnPersonalDataDBO.empApplnPersonalDataId = Integer.parseInt(empFamilyDetailsAddtnlDTO.empApplnPersonalDataId);
//														} else {
//															empFamilyDetailsAddtnlDBO.empApplnPersonalDataDBO = empApplnEntriesDBO.empApplnPersonalDataDBO;
//														}
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.relationship)) {
//															empFamilyDetailsAddtnlDBO.relationship = empFamilyDetailsAddtnlDTO.relationship;
//														}
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.dependentName)) {
//															empFamilyDetailsAddtnlDBO.dependentName = empFamilyDetailsAddtnlDTO.dependentName;
//														}
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.dependentDob)) {
//															LocalDate dependentDob = Utils.convertStringDateTimeToLocalDate(empFamilyDetailsAddtnlDTO.dependentDob);
//															empFamilyDetailsAddtnlDBO.dependentDob = dependentDob;
//														}
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.dependentQualification)) {
//															empFamilyDetailsAddtnlDBO.dependentQualification = empFamilyDetailsAddtnlDTO.dependentQualification;
//														}
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.dependentProfession)) {
//															empFamilyDetailsAddtnlDBO.dependentProfession = empFamilyDetailsAddtnlDTO.dependentProfession;
//														}
//														if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDTO.otherDependentRelationship)) {
//															empFamilyDetailsAddtnlDBO.otherDependentRelationship = empFamilyDetailsAddtnlDTO.otherDependentRelationship;
//														}
//														empFamilyDetailsAddtnlDBOS.add(empFamilyDetailsAddtnlDBO);
//													}
//												}
//											}
//										}
//										if (!Utils.isNullOrEmpty(empApplnEntriesDBO.empApplnPersonalDataDBO.empFamilyDetailsAddtnlDBOS)) {
//											for (EmpFamilyDetailsAddtnlDBO empFamilyDetailsAddtnlDBO : empApplnEntriesDBO.empApplnPersonalDataDBO.empFamilyDetailsAddtnlDBOS) {
//												if (!familyDependentDTOsIdSet.contains(empFamilyDetailsAddtnlDBO.empFamilyDetailsAddtnlId)) {
//													empFamilyDetailsAddtnlDBO.modifiedUsersId = userId;
//													empFamilyDetailsAddtnlDBOS.add(empFamilyDetailsAddtnlDBO);
//												}
//											}
//										}
//										if (!Utils.isNullOrEmpty(empFamilyDetailsAddtnlDBOS))
//											empApplnEntriesDBO.empApplnPersonalDataDBO.empFamilyDetailsAddtnlDBOS = empFamilyDetailsAddtnlDBOS;
//									}
								}
							//	processCode = isAvailable ? "EMP_JOB_OFFER_ACCEPTED" : "EMP_JOB_OFFER_REJECTED";
								processCode = isAvailable ? "EMP_OFFER_ACCEPTED" : "EMP_OFFER_DECLINED";
							}
							Tuple workFlowProcess = CommonApiTransaction.getInstance().getErpWorkFlowProcessIdbyProcessCode(processCode);
							employeeApplicationHelper.submitApplicantProcessCodeAndLog(commonApiTransaction, workFlowProcess, userId, empApplnEntriesDBO);
							context.merge(empApplnEntriesDBO);
							if((uniqueFileNameList.size() > 0)) {
								aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList)
							    .subscribe(res -> {
							        if (res.success()) {
							            System.out.println("Move operation succeeded");
							        } else {
							            System.out.println("Move operation failed: " + res.message());
							        }
							    });
							}
							result.success = true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Exception error) {
				result.failureMessage = "Something went wrong";
			}
		});
	}

	public void getApplicantDetailsFromPrevApplication(String empApplicationRegistrationId, ApiResult result) throws Exception {
		EmployeeApplicantDTO employeeApplicantDTO = new EmployeeApplicantDTO();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				try {
					Query query = context.createQuery("select bo from EmpApplnEntriesDBO bo  " +
							" where bo.empApplnRegistrationsDBO.id=:empApplicationRegistrationId and bo.recordStatus='A' and bo.submissionDate is not null order by bo.submissionDate desc")
							.setParameter("empApplicationRegistrationId", Integer.parseInt(empApplicationRegistrationId)).setMaxResults(1);
					EmpApplnEntriesDBO empApplnEntriesDBO = (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList());
					//EmpApplnEntriesDBO empApplnEntriesDBO = employeeApplicationTransaction.getEmployeeApplication(applicationNo);
					if (!Utils.isNullOrEmpty(empApplnEntriesDBO) && empApplnEntriesDBO.recordStatus == 'A') {
						employeeApplicantDTO.jobDetailDTO = new JobDetailsDTO();
						employeeApplicantDTO.empApplnPersonalDataDTO = new EmpApplnPersonalDataDTO();
						employeeApplicantDTO.addressDetailDTO = new EmpApplnPersonalDataDTO();
						employeeApplicantDTO.educationalDetailDTO = new EducationalDetailsDTO();
						employeeApplicantDTO.professionalExperienceDTO = new ProfessionalExperienceDTO();
						employeeApplicantDTO.researchDetailDTO = new ResearchDetailsDTO();
						employeeApplicantDTO.setJobCategoryDTO(new SelectDTO());
						//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
						result.dto = employeeApplicationHelper.getApplicantDetails(employeeApplicantDTO,empApplnEntriesDBO,true,context);
					}else{
						result.dto = null;
					}
					result.success = true;
				} catch (Exception e) {
					e.printStackTrace();
					result.dto = null;
					result.failureMessage = "Something went wrong";
				}
			}

			@Override
			public void onError(Exception error) {
				error.printStackTrace();
				result.dto = null;
				result.failureMessage = "Something went wrong";
			}
		});
	}
	public UrlAccessLinkDBO createURLAccessLinkDBO(UrlAccessLinkDBO urlAccessLinkDBO, String processCode, String uniqueFileName, String orignialFileName, Integer userId, String saveMode) {
    	String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
		urlAccessLinkDBO.setFileNameUnique(awsConfig[RedisAwsConfig.ACTUAL_PATH] + uniqueFileName);
		urlAccessLinkDBO.setTempFileNameUnique(awsConfig[RedisAwsConfig.TEMP_PATH]  + uniqueFileName);
		UrlFolderListDBO folderListDBO = new UrlFolderListDBO();
		if(!Utils.isNullOrEmpty(awsConfig[RedisAwsConfig.FOLDER_LIST_ID])) {
			folderListDBO.setId(Integer.parseInt(awsConfig[RedisAwsConfig.FOLDER_LIST_ID]));
		}
		urlAccessLinkDBO.setUrlFolderListDBO(folderListDBO);
		urlAccessLinkDBO.setIsQueued(false);
		urlAccessLinkDBO.setIsServiced(true);
		urlAccessLinkDBO.setFileNameOriginal(orignialFileName);
		urlAccessLinkDBO.setCreatedUsersId(userId);
		urlAccessLinkDBO.setModifiedUsersId(userId);
		urlAccessLinkDBO.setRecordStatus('A');
		return urlAccessLinkDBO;
	}
	/*public UrlAccessLinkDBO createURLAccessLinkDBOForOld(UrlAccessLinkDBO urlAccessLinkDBO, String processCode, String uniqueFileName, String orignialFileName, Integer userId, String saveMode) {
    	String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
		urlAccessLinkDBO.setFileNameUnique( uniqueFileName);
		urlAccessLinkDBO.setTempFileNameUnique(uniqueFileName);
		UrlFolderListDBO folderListDBO = new UrlFolderListDBO();
		if(!Utils.isNullOrEmpty(awsConfig[RedisAwsConfig.FOLDER_LIST_ID])) {
			folderListDBO.setId(Integer.parseInt(awsConfig[RedisAwsConfig.FOLDER_LIST_ID]));
		}
		urlAccessLinkDBO.setUrlFolderListDBO(folderListDBO);
		urlAccessLinkDBO.setIsQueued(false);
		urlAccessLinkDBO.setIsServiced(true);
		urlAccessLinkDBO.setFileNameOriginal(orignialFileName);
		urlAccessLinkDBO.setCreatedUsersId(userId);
		urlAccessLinkDBO.setModifiedUsersId(userId);
		urlAccessLinkDBO.setRecordStatus('A');
		return urlAccessLinkDBO;
	}*/
	public List<FileUploadDownloadDTO> createFileListForActualCopy(String processCode, String uniqueFileName) {
    	String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
		fileUploadDownloadDTO.setTempPath(awsConfig[RedisAwsConfig.TEMP_PATH] );
		fileUploadDownloadDTO.setActualPath(awsConfig[RedisAwsConfig.ACTUAL_PATH]);
	    fileUploadDownloadDTO.setUniqueFileName(uniqueFileName);
	    fileUploadDownloadDTO.setBucketName(awsConfig[RedisAwsConfig.BUCKET_NAME]);
	    fileUploadDownloadDTO.setTempBucketName(awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME]);
	    uniqueFileNameList.add(fileUploadDownloadDTO);
		return uniqueFileNameList;
	}
	public String getRemainingAfterLastSlash(String inputString) {
        int lastSlashIndex = inputString.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return inputString.substring(lastSlashIndex + 1);
        } else {
            return inputString;
        }
    }

}
