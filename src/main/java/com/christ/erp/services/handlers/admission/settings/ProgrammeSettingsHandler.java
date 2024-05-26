package com.christ.erp.services.handlers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccFeePaymentModeDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ProgrammeMode;
import com.christ.erp.services.dbobjects.admission.settings.*;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeBatchDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeDocumentSettingsDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeQualificationSettingsDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.helpers.admission.settings.ProgrammeSettingsHelper;
import com.christ.erp.services.transactions.admission.settings.ProgrammeSettingsTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgrammeSettingsHandler {

    public static volatile ProgrammeSettingsHandler programmeSettingsHandler = null;

    public static ProgrammeSettingsHandler getInstance() {
        if (programmeSettingsHandler == null) {
            programmeSettingsHandler = new ProgrammeSettingsHandler();
        }
        return programmeSettingsHandler;
    }

    ProgrammeSettingsTransaction transaction = ProgrammeSettingsTransaction.getInstance();
    ProgrammeSettingsHelper helper = ProgrammeSettingsHelper.getInstance();

    @Autowired
    ProgrammeSettingsTransaction programmeSettingsTransaction1;

    public boolean saveOrUpdate(AdmProgrammeSettingsDTO data, ApiResult<ModelBaseDTO> result, String userId) throws Exception {
        boolean isSaved = false;
        AdmProgrammeSettingsDBO dbo = null;
        AdmProgrammePreferenceSettingsDBO preferenceSettingsDBO = null;
        Set<AdmProgrammePreferenceSettingsDBO> preferenceSettingsDBOsSet = null;
        AdmProgrammeQualificationSettingsDBO qualificationSettingsDBO = null;
        Set<AdmProgrammeQualificationSettingsDBO> qualificationSettingsDBOsSet = null;
        AdmProgrammeDocumentSettingsDBO documentSettingsDBO = null;
        Set<AdmProgrammeDocumentSettingsDBO> documentSettingsDBOsSet = null;
        if (!Utils.isNullOrEmpty(data)) {
            if (Utils.isNullOrEmpty(data.id)) {
                dbo = new AdmProgrammeSettingsDBO();
                dbo.createdUsersId = Integer.parseInt(userId);
            } else {
                if (!Utils.isNullOrEmpty(data.id))
                    dbo = transaction.editById(Integer.parseInt(data.id));
                dbo.setModifiedUsersId(Integer.parseInt(userId));
            }
            if (!Utils.isNullOrEmpty(dbo)) {
                dbo.recordStatus = 'A';
                if (!Utils.isNullOrEmpty(data.programme)) {
                    ErpProgrammeDBO programmeDBO = new ErpProgrammeDBO();
                    programmeDBO.id = Integer.parseInt(data.programme.getValue());
                    dbo.erpProgrammeDBO = programmeDBO;
                }
                if (!Utils.isNullOrEmpty(data.noOfPreferenceRequiredInApplication)) {
                    dbo.noOfPreferenceRequired = Integer.parseInt(data.noOfPreferenceRequiredInApplication.trim());
                }
                if (!Utils.isNullOrEmpty(data.preferenceBasedOn)) {
                    dbo.preferenceOption = data.preferenceBasedOn;
                }
                if(!Utils.isNullOrEmpty(data.getErpAcademicYear())){
					dbo.setErpAcademicYearDBO(new ErpAcademicYearDBO());
					dbo.getErpAcademicYearDBO().setId(Integer.parseInt(data.getErpAcademicYear().getValue()));

                    Map<String ,Integer> admissionTypeMap = new HashMap<>();
					admissionTypeMap = programmeSettingsTransaction1.getAdmissionType().stream().collect(Collectors.toMap(AdmAdmissionTypeDBO::getAdmissionType, AdmAdmissionTypeDBO::getAdmissionIntakeYearNumber));

                    if(!Utils.isNullOrEmpty(admissionTypeMap)  &&  !Utils.isNullOrEmpty(data.getAdmissionType()) &&  admissionTypeMap.containsKey(data.getAdmissionType().getLabel())){
                        Integer intake = admissionTypeMap.get(data.getAdmissionType().getLabel());
                        Integer value = intake - 1;
                        if(value == 0){
                            dbo.setAdmBatchYear(new ErpAcademicYearDBO());
					        dbo.getAdmBatchYear().setId(Integer.parseInt(data.getErpAcademicYear().getValue()));
                        } else {
                            Integer yearId = programmeSettingsTransaction1.getAcademic(data.getErpAcademicYear().getAcademicYear()-value);
                            dbo.setAdmBatchYear(new ErpAcademicYearDBO());
							dbo.getAdmBatchYear().setId(yearId);
                        }
                    }
                }
                if (!Utils.isNullOrEmpty(data.getAdmissionType())) {
                    dbo.setAdmAdmissionTypeDBO(new AdmAdmissionTypeDBO());
                    dbo.getAdmAdmissionTypeDBO().setId(Integer.parseInt(data.getAdmissionType().getValue()));
                }
                if (!Utils.isNullOrEmpty(data.getIntakeBatch())) {
                    dbo.setAdmIntakeBatchDBO(new AdmIntakeBatchDBO());
                    dbo.getAdmIntakeBatchDBO().setId(Integer.parseInt(data.getIntakeBatch().getValue()));
                } else {
                    Tuple batch = programmeSettingsTransaction1.getIntakeBatch(data.programme,data.getErpAcademicYear());
                    if(!Utils.isNullOrEmpty(batch)){
                        dbo.setAdmIntakeBatchDBO(new AdmIntakeBatchDBO());
                        dbo.getAdmIntakeBatchDBO().setId(Integer.parseInt(String.valueOf(batch.get("id"))));
                    }

                }
                if (!Utils.isNullOrEmpty(data.getOtherProgrammePref())) {
                    dbo.setIsHavingOtherProgrammePreferences(data.getOtherProgrammePref());
                } else {
                    dbo.setIsHavingOtherProgrammePreferences(false);
                }

                Set<Integer> specialListIds = new HashSet<>();
                if (!Utils.isNullOrEmpty(data.getSpecializationList())) {
                    data.getSpecializationList().forEach(a -> {
                        specialListIds.add(Integer.parseInt(a.getValue()));
                    });
                }

                Set<AdmProgrammeBatchDBO> admProgrammeBatchDBOSet = new HashSet<>();
                Map<Integer, AdmProgrammeBatchDBO> AdmProgrammeBatchDBOExistMap = null;
                if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBOSet())) {
                    AdmProgrammeBatchDBOExistMap = dbo.getAdmProgrammeBatchDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getErpCampusProgrammeMappingDBO().getId(), s -> s));
                }

                if (!Utils.isNullOrEmpty(data.getCampusList())) {
                    for (SelectDTO campus : data.getCampusList()) {
                        AdmProgrammeBatchDBO admProgrammeBatchDBO = null;
                        if (!Utils.isNullOrEmpty(AdmProgrammeBatchDBOExistMap) && AdmProgrammeBatchDBOExistMap.containsKey(Integer.parseInt(campus.getValue()))) {
                            admProgrammeBatchDBO = AdmProgrammeBatchDBOExistMap.get(Integer.parseInt(campus.getValue()));
                            admProgrammeBatchDBO.setModifiedUsersId(Integer.parseInt(userId));
                            AdmProgrammeBatchDBOExistMap.remove(Integer.parseInt(campus.getValue()));
                        } else {
                            admProgrammeBatchDBO = new AdmProgrammeBatchDBO();
                            admProgrammeBatchDBO.setCreatedUsersId(Integer.parseInt(userId));
                        }
                        admProgrammeBatchDBO.setRecordStatus('A');
                        admProgrammeBatchDBO.setAdmProgrammeSettingsDBO(dbo);
                        admProgrammeBatchDBO.setErpCampusProgrammeMappingDBO(new ErpCampusProgrammeMappingDBO());
                        admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().setId(Integer.parseInt(campus.getValue()));
                        admProgrammeBatchDBO.setIsSpecialisationRequired(data.getSpecializationRequired());
                        admProgrammeBatchDBOSet.add(admProgrammeBatchDBO);
                    }
                } else {
                    for (SelectDTO location : data.getLocationList()) {
                        AdmProgrammeBatchDBO admProgrammeBatchDBO = null;
                        if (!Utils.isNullOrEmpty(AdmProgrammeBatchDBOExistMap) && AdmProgrammeBatchDBOExistMap.containsKey(Integer.parseInt(location.getValue()))) {
                            admProgrammeBatchDBO = AdmProgrammeBatchDBOExistMap.get(Integer.parseInt(location.getValue()));
                            admProgrammeBatchDBO.setModifiedUsersId(Integer.parseInt(userId));
                            AdmProgrammeBatchDBOExistMap.remove(Integer.parseInt(location.getValue()));
                        } else {
                            admProgrammeBatchDBO = new AdmProgrammeBatchDBO();
                            admProgrammeBatchDBO.setCreatedUsersId(Integer.parseInt(userId));
                        }
                        admProgrammeBatchDBO.setRecordStatus('A');
                        admProgrammeBatchDBO.setAdmProgrammeSettingsDBO(dbo);
                        admProgrammeBatchDBO.setErpCampusProgrammeMappingDBO(new ErpCampusProgrammeMappingDBO());
                        admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().setId(Integer.parseInt(location.getValue()));
                        admProgrammeBatchDBO.setIsSpecialisationRequired(data.getSpecializationRequired());
                        admProgrammeBatchDBOSet.add(admProgrammeBatchDBO);
                    }
                }
                if (!Utils.isNullOrEmpty(AdmProgrammeBatchDBOExistMap)) {
                    for (AdmProgrammeBatchDBO admProgrammeBatchDBO : AdmProgrammeBatchDBOExistMap.values()) {
                        admProgrammeBatchDBO.setRecordStatus('D');
                        admProgrammeBatchDBO.setModifiedUsersId(Integer.parseInt(userId));
                        admProgrammeBatchDBOSet.add(admProgrammeBatchDBO);
                    }
                }
                dbo.setAdmProgrammeBatchDBOSet(admProgrammeBatchDBOSet);


//				if(!Utils.isNullOrEmpty(data.currency) && !Utils.isNullOrEmpty(data.currency.id.trim())) { // sambath
//					ErpCurrencyDBO currencyDBO = new ErpCurrencyDBO();
//					currencyDBO.id = Integer.parseInt(data.currency.id.trim());
//					dbo.erpCurrencyDBO = currencyDBO;
//				}
//				if(!Utils.isNullOrEmpty(data.indianapplicant) && !Utils.isNullOrEmpty(data.indianapplicant.trim())) {
//					dbo.feeForIndianApplicants = BigDecimal.valueOf(Double.parseDouble(data.indianapplicant.trim()));
//				}
//				if(!Utils.isNullOrEmpty(data.internationalapplicant) && !Utils.isNullOrEmpty(data.internationalapplicant.trim())) {
//					dbo.feeForInternationalApplicants = BigDecimal.valueOf(Double.parseDouble(data.internationalapplicant.trim()));
//				}
                if (!Utils.isNullOrEmpty(data.getAccFeesHeads())) {
                    dbo.setAccFeeHeadsDBO(new AccFeeHeadsDBO());
                    dbo.getAccFeeHeadsDBO().setId(Integer.parseInt(data.getAccFeesHeads().getValue()));
                }
                if (!Utils.isNullOrEmpty(data.secondlanguage)) {
                    dbo.isSecondLanguage = data.secondlanguage;
                }
                if (!Utils.isNullOrEmpty(data.researchtopicrequired)) {
                    dbo.isResearchTopicRequired = data.researchtopicrequired;
                }
                if (!Utils.isNullOrEmpty(data.workexperiencerequired)) {
                    dbo.isWorExperienceRequired = data.workexperiencerequired;
                }
                if (!Utils.isNullOrEmpty(data.workexperiencemandatory)) {
                    dbo.isWorkExperienceMandatory = data.workexperiencemandatory;
                }
                if (!Utils.isNullOrEmpty(data.minimumnoofmonthsexperiencerequired)) {
                    dbo.minNoOfMonthsExperience = Integer.parseInt(data.minimumnoofmonthsexperiencerequired.trim());
                } else {
                    dbo.minNoOfMonthsExperience = null;
                }
                if (!Utils.isNullOrEmpty(data.getIsProgrammeModeDisplayed())) {
                    dbo.isProgrammeModeDisplayed = data.getIsProgrammeModeDisplayed();
                    if (!Utils.isNullOrEmpty(data.modeofapplication)) {
                        dbo.applicationMode = ApplicationMode.valueOf(data.modeofapplication);
                    }
                }

                if (!Utils.isNullOrEmpty(data.termsCondition) && !Utils.isNullOrEmpty(data.termsCondition.id)) {
                    ErpTemplateDBO templateDBO = new ErpTemplateDBO();
                    templateDBO.id = Integer.parseInt(data.termsCondition.id);
                    dbo.erpTemplateDBO = templateDBO;
                }
                if (!Utils.isNullOrEmpty(data.getProgrammeMode())) {
                    dbo.setModeOfStudy(ProgrammeMode.valueOf(data.getProgrammeMode().getValue()));
                }
//				if(!Utils.isNullOrEmpty(data.printTemplate.id) && !Utils.isNullOrEmpty(data.printTemplate.id.trim())) {
//					ErpTemplateDBO templateDBO = new ErpTemplateDBO();
//					templateDBO.id = Integer.parseInt(data.printTemplate.id);
//					dbo.erpApplnPrintTemplate = templateDBO;
//				}

//				if(!Utils.isNullOrEmpty(data.getPaymentModes())) {
//					StringBuffer value = new StringBuffer();
//					data.getPaymentModes().forEach(a -> {
//						value.append(a);
//					});
//					dbo.setOnlinePaymentModes(value.toString()); 
//				}

                if (Utils.isNullOrEmpty(data.id)) {

                    // preference adding is in grid
//					if(!Utils.isNullOrEmpty(data.programmepreferences) && data.programmepreferences.size()>0) {
//						preferenceSettingsDBOsSet = new HashSet<AdmProgrammePreferenceSettingsDBO>();
//						for (AdmProgrammePreferenceSettingsDTO preferenceSettingsDTO : data.programmepreferences) {
//							if(Utils.isNullOrEmpty(preferenceSettingsDTO.id)) {
//								preferenceSettingsDBO =  new AdmProgrammePreferenceSettingsDBO();
//							}
//							preferenceSettingsDBO = helper.getAdmProgrammPreferenceSettingsDBO(preferenceSettingsDTO, preferenceSettingsDBO, dbo, userId);
//							if(!Utils.isNullOrEmpty(preferenceSettingsDBO)) {
//								preferenceSettingsDBOsSet.add(preferenceSettingsDBO);
//							}
//						}
//						dbo.admProgrammePreferenceSettingsSetDbos = preferenceSettingsDBOsSet;
//					}

                    if (!Utils.isNullOrEmpty(data.qualificationsettings)) {
                        qualificationSettingsDBOsSet = new HashSet<AdmProgrammeQualificationSettingsDBO>();
                        for (AdmProgrammeQualificationSettingsDTO qualificationSettingsDTO : data.qualificationsettings) {
//							if(Utils.isNullOrEmpty(qualificationSettingsDTO.id)) {
                            qualificationSettingsDBO = new AdmProgrammeQualificationSettingsDBO();
//							}
                            qualificationSettingsDBO = helper.getAdmProgrammQualificationSettingsDBO(qualificationSettingsDTO, qualificationSettingsDBO, dbo, userId);
                            if (!Utils.isNullOrEmpty(qualificationSettingsDBO)) {
                                qualificationSettingsDBOsSet.add(qualificationSettingsDBO);
                            }
                        }
                        dbo.admProgrammeQualificationSettingsSetDbos = qualificationSettingsDBOsSet;
                    }
                    if (!Utils.isNullOrEmpty(data.additionaldocumenttemplate)) {
                        documentSettingsDBOsSet = new HashSet<AdmProgrammeDocumentSettingsDBO>();
                        for (AdmProgrammeDocumentSettingsDTO qualificationSettingsDTO : data.additionaldocumenttemplate) {
//							if(Utils.isNullOrEmpty(qualificationSettingsDTO.id)) {
                            documentSettingsDBO = new AdmProgrammeDocumentSettingsDBO();
//							}
                            documentSettingsDBO = helper.getAdmProgrammeDocumentSettingsDBO(qualificationSettingsDTO, documentSettingsDBO, dbo, userId);
                            if (!Utils.isNullOrEmpty(documentSettingsDBO)) {
                                documentSettingsDBOsSet.add(documentSettingsDBO);
                            }
                        }
                        dbo.admProgrammeDocumentSettingSetDbos = documentSettingsDBOsSet;
                    }
                } else {
                    //preference adding is in grid
//						Set<Integer> activeProgrammeSetttings = new HashSet<Integer>();
//						if(!Utils.isNullOrEmpty(data.programmepreferences) && data.programmepreferences.size()>0) {
//							for (AdmProgrammePreferenceSettingsDBO preferenceSettingsDBOUpdate : dbo.admProgrammePreferenceSettingsSetDbos) {
//								for (AdmProgrammePreferenceSettingsDTO preferenceSettingsDTO : data.programmepreferences) {
//									if(!Utils.isNullOrEmpty(preferenceSettingsDTO.id) &&!Utils.isNullOrEmpty(preferenceSettingsDTO.id.trim()) &&
//										!Utils.isNullOrEmpty(preferenceSettingsDBOUpdate.id) && preferenceSettingsDTO.id.trim().equals(String.valueOf(preferenceSettingsDBOUpdate.id))) {
//										activeProgrammeSetttings.add(preferenceSettingsDBOUpdate.id);
//										preferenceSettingsDBOUpdate = helper.getAdmProgrammPreferenceSettingsDBO(preferenceSettingsDTO, preferenceSettingsDBOUpdate, dbo, userId);
//									    break;
//									}
//								}
//							}
//							for (AdmProgrammePreferenceSettingsDBO preferenceSettingsDBO2 : dbo.admProgrammePreferenceSettingsSetDbos) {
//								if(!activeProgrammeSetttings.contains(preferenceSettingsDBO2.id) && !Utils.isNullOrEmpty(preferenceSettingsDBO2.recordStatus)&& preferenceSettingsDBO2.recordStatus=='A') {
//									preferenceSettingsDBO2.modifiedUsersId = Integer.parseInt(userId);
//									preferenceSettingsDBO2.recordStatus = 'D';
//								}
//							}
//							for (AdmProgrammePreferenceSettingsDTO preferenceSettingsDTO : data.programmepreferences) {
//								if(Utils.isNullOrEmpty(preferenceSettingsDTO.id)) {
//									preferenceSettingsDBO = new AdmProgrammePreferenceSettingsDBO();
//									preferenceSettingsDBO = helper.getAdmProgrammPreferenceSettingsDBO(preferenceSettingsDTO, preferenceSettingsDBO, dbo, userId);
//									if(!Utils.isNullOrEmpty(preferenceSettingsDBO)) {
//										dbo.admProgrammePreferenceSettingsSetDbos.add(preferenceSettingsDBO);
//									}
//								}
//							}
//						}

                    if (!Utils.isNullOrEmpty(data.qualificationsettings)) {
//							Set<Integer> activateQualificationDBO = new HashSet<Integer>();
                        Map<Integer, AdmProgrammeQualificationSettingsDBO> admProgrammeQualificationSettingsExistMap = new HashMap<>();
//                        if (!Utils.isNullOrEmpty(dbo.admProgrammeQualificationSettingsSetDbos)) {
//                            for (AdmProgrammeQualificationSettingsDBO admProgQulSet : dbo.admProgrammeQualificationSettingsSetDbos) {
//                                if (admProgQulSet.getRecordStatus() == 'A') {
//                                    admProgrammeQualificationSettingsExistMap.put(admProgQulSet.getId(), admProgQulSet);
//                                }
//                            }
//                        }
                        admProgrammeQualificationSettingsExistMap = dbo.admProgrammeQualificationSettingsSetDbos.stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s));

//							for (AdmProgrammeQualificationSettingsDBO programmeQualificationSettingsDBO : dbo.admProgrammeQualificationSettingsSetDbos) {
//								for (AdmProgrammeQualificationSettingsDTO qualificationSettingsDTO : data.qualificationsettings) {
//									if(!Utils.isNullOrEmpty(qualificationSettingsDTO.id) &&!Utils.isNullOrEmpty(qualificationSettingsDTO.id.trim())
//											&& !Utils.isNullOrEmpty(programmeQualificationSettingsDBO.id) && qualificationSettingsDTO.id.trim().equals(String.valueOf(programmeQualificationSettingsDBO.id))) {
//										activateQualificationDBO.add(programmeQualificationSettingsDBO.id);
//										programmeQualificationSettingsDBO = helper.getAdmProgrammQualificationSettingsDBO(qualificationSettingsDTO, programmeQualificationSettingsDBO, dbo, userId);
//									    break;
//									}
//								}
//							}

//							for (AdmProgrammeQualificationSettingsDBO qualificationSettingsDBO2 :  dbo.admProgrammeQualificationSettingsSetDbos) {
//								if(!activateQualificationDBO.contains(qualificationSettingsDBO2.id) && !Utils.isNullOrEmpty(qualificationSettingsDBO2.recordStatus)
//										&& qualificationSettingsDBO2.recordStatus=='A') {
//									qualificationSettingsDBO2.modifiedUsersId = Integer.parseInt(userId);
//									qualificationSettingsDBO2.recordStatus = 'D';
//								}
//							}
                        for (AdmProgrammeQualificationSettingsDTO qualificationSettingsDTO : data.qualificationsettings) {
                            if (!Utils.isNullOrEmpty(qualificationSettingsDTO.id) && !Utils.isNullOrEmpty(admProgrammeQualificationSettingsExistMap) &&
                                    admProgrammeQualificationSettingsExistMap.containsKey(Integer.parseInt(qualificationSettingsDTO.id))) {

                                qualificationSettingsDBO = admProgrammeQualificationSettingsExistMap.get(Integer.parseInt(qualificationSettingsDTO.id));
                                admProgrammeQualificationSettingsExistMap.remove(Integer.parseInt(qualificationSettingsDTO.id));
                            } else {
                                qualificationSettingsDBO = new AdmProgrammeQualificationSettingsDBO();
                            }
                            qualificationSettingsDBO = helper.getAdmProgrammQualificationSettingsDBO(qualificationSettingsDTO, qualificationSettingsDBO, dbo, userId);
                            if (!Utils.isNullOrEmpty(qualificationSettingsDBO)) {
                                dbo.admProgrammeQualificationSettingsSetDbos.add(qualificationSettingsDBO);
                            }

                        }
                        if (!Utils.isNullOrEmpty(admProgrammeQualificationSettingsExistMap)) {
                            for (Map.Entry<Integer, AdmProgrammeQualificationSettingsDBO> entry : admProgrammeQualificationSettingsExistMap.entrySet()) {
                                AdmProgrammeQualificationSettingsDBO value = entry.getValue();
                                Set<AdmProgrammeQualificationSubjectEligibilityDBO> eligibilitySetUpdate = new HashSet<>();
                                for (AdmProgrammeQualificationSubjectEligibilityDBO admProgrammeQualificationSubjectEligibilityDBO : value.getAdmProgrammeQualificationSubjectEligibilityDBO()) {
                                    admProgrammeQualificationSubjectEligibilityDBO.setRecordStatus('D');
                                    admProgrammeQualificationSubjectEligibilityDBO.setModifiedUsersId(Integer.parseInt(userId));
                                    eligibilitySetUpdate.add(admProgrammeQualificationSubjectEligibilityDBO);
                                }
                                value.setAdmProgrammeQualificationSubjectEligibilityDBO(eligibilitySetUpdate);
                                value.setRecordStatus('D');
                                value.setModifiedUsersId(Integer.parseInt(userId));
                                dbo.admProgrammeQualificationSettingsSetDbos.add(value);
                            }
                        }
                    }

                    if (!Utils.isNullOrEmpty(data.additionaldocumenttemplate)) {
//							Set<Integer> activatedocumentDBO = new HashSet<Integer>();
                        Map<Integer, AdmProgrammeDocumentSettingsDBO> admProgrammeDocumentSettingsDBOExistMap = new HashMap<>();
                        if (!Utils.isNullOrEmpty(dbo.admProgrammeDocumentSettingSetDbos)) {
                            for (AdmProgrammeDocumentSettingsDBO documentSettingsDBOUpdate : dbo.admProgrammeDocumentSettingSetDbos) {
                                if (documentSettingsDBOUpdate.getRecordStatus() == 'A') {
                                    admProgrammeDocumentSettingsDBOExistMap.put(documentSettingsDBOUpdate.getId(), documentSettingsDBOUpdate);
                                }
                            }
                        }

//							for (AdmProgrammeDocumentSettingsDBO documentSettingsDBO2 : dbo.admProgrammeDocumentSettingSetDbos) {
//								if(!activatedocumentDBO.contains(documentSettingsDBO2.id) && !Utils.isNullOrEmpty(documentSettingsDBO2.recordStatus) && documentSettingsDBO2.recordStatus=='A') {
//									documentSettingsDBO2.modifiedUsersId = Integer.parseInt(userId);
//									documentSettingsDBO2.recordStatus = 'D';
//								}
//							}

                        for (AdmProgrammeDocumentSettingsDTO documentSettingsDTO : data.additionaldocumenttemplate) {
                            if (!Utils.isNullOrEmpty(documentSettingsDTO.id) && !Utils.isNullOrEmpty(admProgrammeDocumentSettingsDBOExistMap) &&
                                    admProgrammeDocumentSettingsDBOExistMap.containsKey(Integer.parseInt(documentSettingsDTO.id))) {

                                documentSettingsDBO = admProgrammeDocumentSettingsDBOExistMap.get(Integer.parseInt(documentSettingsDTO.id));
                                admProgrammeDocumentSettingsDBOExistMap.remove(Integer.parseInt(documentSettingsDTO.id));
                            } else {
                                documentSettingsDBO = new AdmProgrammeDocumentSettingsDBO();
                            }
                            documentSettingsDBO = helper.getAdmProgrammeDocumentSettingsDBO(documentSettingsDTO, documentSettingsDBO, dbo, userId);
                            if (!Utils.isNullOrEmpty(documentSettingsDBO)) {
                                dbo.admProgrammeDocumentSettingSetDbos.add(documentSettingsDBO);
                            }
                        }
                        if (!Utils.isNullOrEmpty(admProgrammeDocumentSettingsDBOExistMap)) {
                            for (Map.Entry<Integer, AdmProgrammeDocumentSettingsDBO> entry : admProgrammeDocumentSettingsDBOExistMap.entrySet()) {
                                AdmProgrammeDocumentSettingsDBO value = entry.getValue();
                                value.setRecordStatus('D');
                                value.setModifiedUsersId(Integer.parseInt(userId));
                                dbo.admProgrammeDocumentSettingSetDbos.add(value);
                            }
                        }
                    }
                }

                AdmProgrammeSettingsDBO dbo1 = dbo;
                Set<AdmProgrammeFeePaymentModeDBO> admProgrammeFeePaymentModeDBOSet1 = !Utils.isNullOrEmpty(dbo) ? dbo.getAdmProgrammeFeePaymentModeDBOSet() : null;
                Map<Integer, AdmProgrammeFeePaymentModeDBO> pdmExistDboMap1 = new LinkedHashMap<Integer, AdmProgrammeFeePaymentModeDBO>();
                if (!Utils.isNullOrEmpty(admProgrammeFeePaymentModeDBOSet1)) {
                    pdmExistDboMap1 = admProgrammeFeePaymentModeDBOSet1.stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getAccFeePaymentModeDBO().getId(), s -> s));
                }
                var pdmExistDboMap = pdmExistDboMap1;
                if (!Utils.isNullOrEmpty(data.getAdmProgrammeFeePaymentModeDTOList())) {
                    var admProgrammeFeePaymentModeDBOSet = new LinkedHashSet<AdmProgrammeFeePaymentModeDBO>();
                    data.getAdmProgrammeFeePaymentModeDTOList().forEach(paymentMode -> {
                        if (!Utils.isNullOrEmpty(paymentMode)) {
                            AdmProgrammeFeePaymentModeDBO admProgrammeFeePaymentModeDBO = null;
                            if (pdmExistDboMap.containsKey(Integer.parseInt(paymentMode.getValue()))) {
                                admProgrammeFeePaymentModeDBO = pdmExistDboMap.get(Integer.parseInt(paymentMode.getValue()));
                                admProgrammeFeePaymentModeDBO.setModifiedUsersId(Integer.parseInt(userId));
                                pdmExistDboMap.remove(Integer.parseInt(paymentMode.getValue()));
                            } else {
                                admProgrammeFeePaymentModeDBO = new AdmProgrammeFeePaymentModeDBO();
                            }
                            admProgrammeFeePaymentModeDBO.setCreatedUsersId(Integer.parseInt(userId));
                            admProgrammeFeePaymentModeDBO.setAdmProgrammeSettingsDBO(dbo1);
                            admProgrammeFeePaymentModeDBO.setAccFeePaymentModeDBO(new AccFeePaymentModeDBO());
                            admProgrammeFeePaymentModeDBO.getAccFeePaymentModeDBO().setId(Integer.parseInt(paymentMode.getValue()));
                            admProgrammeFeePaymentModeDBO.setRecordStatus('A');
                            admProgrammeFeePaymentModeDBOSet.add(admProgrammeFeePaymentModeDBO);
                        }
                    });
                    if (!Utils.isNullOrEmpty(pdmExistDboMap)) {
                        pdmExistDboMap.forEach((deptId, valueDep) -> {
                            if (!Utils.isNullOrEmpty(valueDep)) {
                                valueDep.setModifiedUsersId(Integer.parseInt(userId));
                                valueDep.setRecordStatus('D');
                                admProgrammeFeePaymentModeDBOSet.add(valueDep);
                            }
                        });
                    }
                    dbo.setAdmProgrammeFeePaymentModeDBOSet(admProgrammeFeePaymentModeDBOSet);
                }
                isSaved = programmeSettingsTransaction1.saveOrUpdate(dbo);
            }
        }
        return isSaved;
    }

    public AdmProgrammeSettingsDTO edit(String id) throws Exception {
        AdmProgrammeSettingsDBO dbo = null;
        AdmProgrammeSettingsDTO dto = null;
        if (!Utils.isNullOrEmpty(id)) {
            dbo = transaction.editByProgramme(Integer.parseInt(id));
        }
        if (!Utils.isNullOrEmpty(dbo)) {
            dto = helper.setDboToDto(dbo, dto);
        }
        return dto;
    }

    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> duplicateCheck(Mono<AdmProgrammeSettingsDTO> dto, String userId) {
        return dto.handle((admProgrammeSettingsDTO, synchronousSink) -> {
            List<String> duplicate = new ArrayList<>();
            Set<Integer> programmeIds = new HashSet<>();
            if(admProgrammeSettingsDTO.getPreferenceBasedOn() == 'C' && !Utils.isNullOrEmpty(admProgrammeSettingsDTO.getCampusList()) ){
                for (SelectDTO selectDTO : admProgrammeSettingsDTO.getCampusList()) {
                    programmeIds.add(Integer.parseInt(selectDTO.getValue()));
                }
            } else if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.getLocationList())) {
                for (SelectDTO selectDTO : admProgrammeSettingsDTO.getLocationList()) {
                    programmeIds.add(Integer.parseInt(selectDTO.getValue()));
                }
            }
            List<AdmProgrammeSettingsDBO> list = programmeSettingsTransaction1.duplicateCheck(admProgrammeSettingsDTO,programmeIds);
            for (AdmProgrammeSettingsDBO admProgrammeSettingsDBO : list) {
                if(!Utils.isNullOrEmpty(admProgrammeSettingsDBO.getAdmProgrammeBatchDBOSet())){
                    for (AdmProgrammeBatchDBO admProgrammeBatchDBO : admProgrammeSettingsDBO.getAdmProgrammeBatchDBOSet()) {
                        if(admProgrammeSettingsDTO.getPreferenceBasedOn() == 'C'){
                            duplicate.add(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
                        } else {
                            duplicate.add(admProgrammeBatchDBO.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
                        }

                    }
                }
            }
            if(!Utils.isNullOrEmpty(duplicate) && !Utils.isNullOrEmpty(programmeIds) ){
                synchronousSink.error(new DuplicateException("Already Programme Setting Exist for Selected Campus/Location "+duplicate));
            } else if (!Utils.isNullOrEmpty(list)) {
                synchronousSink.error(new DuplicateException("Already Programme Setting Exist for Selected Prameters "));
            } else {
                synchronousSink.next(true);
            }
        }).cast(Boolean.class).map(Utils::responseResult);
    }

    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> delete(int id, String userId) {
        List<Tuple> data = programmeSettingsTransaction1.programmeExit(id);
        if(Utils.isNullOrEmpty(data)){
            return programmeSettingsTransaction1.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
        } else {
           return Mono.error(new DuplicateException ("Already selection process plan is done for this programme"));
        }
    }

    public Flux<AdmProgrammeSettingsDTO> getGridData(Integer yearId, String programmeId, String intakeBatchId) {
        return programmeSettingsTransaction1.getGridData(yearId, programmeId, intakeBatchId).flatMapMany(Flux::fromIterable).sort(Comparator.comparing(s->s.getErpProgrammeDBO().getProgrammeNameForApplication())).map(this::convertDtoToDbo);

    }

    public AdmProgrammeSettingsDTO convertDtoToDbo(AdmProgrammeSettingsDBO dbo) {
        AdmProgrammeSettingsDTO dto = new AdmProgrammeSettingsDTO();
        dto.id = String.valueOf(dbo.getId());
        if (!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
            dto.setErpAcademicYear(new ErpAcademicYearDTO());
            dto.getErpAcademicYear().setAcademicYear(dbo.getErpAcademicYearDBO().getAcademicYear());
            dto.getErpAcademicYear().setAcademicYearName(dbo.getErpAcademicYearDBO().getAcademicYearName());
            dto.getErpAcademicYear().setId(dbo.getErpAcademicYearDBO().getId());
        }
        if (!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO())) {
            dto.setProgramme(new SelectDTO());
            dto.getProgramme().setValue(String.valueOf(dbo.getErpProgrammeDBO().getId()));
            dto.getProgramme().setLabel(dbo.getErpProgrammeDBO().getProgrammeNameForApplication());
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmIntakeBatchDBO())) {
            dto.setIntakeBatch(new SelectDTO());
            dto.getIntakeBatch().setValue(String.valueOf(dbo.getAdmIntakeBatchDBO().getId()));
            dto.getIntakeBatch().setLabel(dbo.getAdmIntakeBatchDBO().getAdmIntakeBatchName());
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmAdmissionTypeDBO())) {
            dto.setAdmissionType(new SelectDTO());
            dto.getAdmissionType().setValue(String.valueOf(dbo.getAdmAdmissionTypeDBO().getId()));
            dto.getAdmissionType().setLabel(dbo.getAdmAdmissionTypeDBO().getAdmissionType());
        }
        if(!Utils.isNullOrEmpty(dbo.getAccFeeHeadsDBO())){
            if(!Utils.isNullOrEmpty(dbo.getAccFeeHeadsDBO().getHeading())){
                dto.setHeading(dbo.getAccFeeHeadsDBO().getHeading());
            }
        }
        dto.setOtherProgrammePref(dbo.getIsHavingOtherProgrammePreferences());
        dto.setPreferenceBasedOn(dbo.getPreferenceOption());
        if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBOSet())) {
            String campusOrLocations = dbo.getAdmProgrammeBatchDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').map(s -> !Utils.isNullOrEmpty(s.getErpCampusProgrammeMappingDBO().getErpLocationDBO()) ?
                            s.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName() : s.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName())
                    .collect(Collectors.joining(","));
            dto.setCampusOrLocations(campusOrLocations);
        }
        return dto;
    }
    public Mono<List<AdmProgrammeBatchDTO>> getOtherPreference(String settingsId) {
       return Mono.just(programmeSettingsTransaction1.getOtherPreference(settingsId)).map(this::convertPreferenceDboToDto);
    }
    public List<AdmProgrammeBatchDTO> convertPreferenceDboToDto(List<AdmProgrammeBatchPreferencesDBO> dbo) {
        var dtoList = new ArrayList<AdmProgrammeBatchDTO>();
        var map = dbo.stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.groupingBy(s -> s.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getId()));
        map.forEach((settingId, batchlist) -> {
            var dto = new AdmProgrammeBatchDTO();
            dto.setProgrammeSettingsDTO(new SelectDTO());
            dto.getProgrammeSettingsDTO().setValue(String.valueOf(settingId));
            dto.setBatchCampusList(new ArrayList<>());
            batchlist.forEach(s -> {
                if (!Utils.isNullOrEmpty(s.getAdmProgrammeBatchDBO()) && s.getAdmProgrammeBatchDBO().getRecordStatus() == 'A') {
                    var selectDto = new SelectDTO();
                    selectDto.setValue(String.valueOf(s.getAdmProgrammeBatchDBO().getId()));
                    if (!Utils.isNullOrEmpty(s.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO())) {
                        if (!Utils.isNullOrEmpty(s.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO())) {
                            selectDto.setLabel(s.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
                        } else if (!Utils.isNullOrEmpty(s.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpLocationDBO())) {
                            selectDto.setLabel(s.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
                        }
                    }
                    dto.getBatchCampusList().add(selectDto);
                }
            });
            dtoList.add(dto);
        });
        return dtoList;
    }

    public Flux<SelectDTO> getOtherPreferenceProgrammeList(AdmProgrammeSettingsDTO dto) {
        return programmeSettingsTransaction1.getOtherPreferenceProgrammeList(dto).flatMapMany(Flux::fromIterable).map(this::convertDboToSelectDto);
    }

    public SelectDTO convertDboToSelectDto(AdmProgrammeSettingsDBO dbo) {
        SelectDTO dto = new SelectDTO();
        dto.setValue(String.valueOf(dbo.getId()));
        if (!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO())) {
            dto.setLabel(dbo.getErpProgrammeDBO().getProgrammeNameForApplication());
        }
        return dto;
    }

    public Flux<SelectDTO> getProgrammeBatchBySettingId(String settingId) {
        return programmeSettingsTransaction1.getProgrammeBatchBySettingId(settingId).flatMapMany(Flux::fromIterable).map(this::convertDboToSelectDto);
    }

    public SelectDTO convertDboToSelectDto(AdmProgrammeBatchDBO dbo) {
        SelectDTO dto = new SelectDTO();
        dto.setValue(String.valueOf(dbo.getId()));
        if (!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO())) {
            if (!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO())) {
                dto.setLabel(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
            } else if (!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpLocationDBO())) {
                dto.setLabel(dbo.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
            }
        }
        return dto;
    }
    public Mono<ApiResult> saveProgrammePreferences(Mono<AdmProgrammeBatchDTO> data, String userId) {
        return data.handle((admProgrammeBatchDTO, synchronousSink) -> {
            synchronousSink.next(admProgrammeBatchDTO);
        }).cast(AdmProgrammeBatchDTO.class).map(dat -> convertPreferenceDtoToDbo(dat, userId)).flatMap(s -> {
            if (!Utils.isNullOrEmpty(s)) {
                programmeSettingsTransaction1.update(s);
            }
            return Mono.just(Boolean.TRUE);
        }).map(Utils::responseResult);
    }
    public List<AdmProgrammeBatchPreferencesDBO> convertPreferenceDtoToDbo(AdmProgrammeBatchDTO dto, String userId) {
        List<AdmProgrammeBatchPreferencesDBO> dboList = new ArrayList<>();
        var existDboList = programmeSettingsTransaction1.getOtherPreference(dto.getProgrammeSettingsDTO().getValue());
        Map<Integer,AdmProgrammeBatchPreferencesDBO>  dboMap = new HashMap<>();
        if (!Utils.isNullOrEmpty(existDboList)) {
            dboMap= existDboList.stream().collect(Collectors.toMap(s->s.getId(),s->s));
        }
        var map1 = dboMap;
        dto.getBatchCampusList().forEach(s -> {
            AdmProgrammeBatchPreferencesDBO dbo = new AdmProgrammeBatchPreferencesDBO();
            if (map1.containsKey(Integer.parseInt(s.getValue()))) {
                dbo =map1.get(Integer.parseInt(s.getValue()));
                dbo.setModifiedUsersId(Integer.parseInt(userId));
                map1.remove(Integer.parseInt(s.getValue()));
            }
            if (!Utils.isNullOrEmpty(dto.getProgrammeSettingsDTO())) {
                dbo.setAdmProgrammeSettingsDBO(new AdmProgrammeSettingsDBO());
                dbo.getAdmProgrammeSettingsDBO().setId(Integer.parseInt(dto.getProgrammeSettingsDTO().getValue()));
            }
            if (!Utils.isNullOrEmpty(s.getValue())) {
                dbo.setAdmProgrammeBatchDBO(new AdmProgrammeBatchDBO());
                dbo.getAdmProgrammeBatchDBO().setId(Integer.parseInt(s.getValue()));
            }
            dbo.setCreatedUsersId(Integer.valueOf(userId));
            dbo.setRecordStatus('A');
            dboList.add(dbo);
        });
        if(!Utils.isNullOrEmpty(map1)) {
            map1.forEach((id,bo)->{
               bo.setRecordStatus('D');
               bo.setModifiedUsersId(Integer.valueOf(userId));
               dboList.add(bo);
            });
        }
        return dboList;
    }

    public Mono<ApiResult> getCheckProg( Integer progSettingId, List<Integer> campOrlocIds) {
        List<Tuple> values = programmeSettingsTransaction1.getCheckProg(progSettingId,campOrlocIds);
        if(!Utils.isNullOrEmpty(values)){
            return  Mono.error(new DuplicateException("Selection process plan is already done"));
        }
        return Mono.just(Boolean.TRUE).map(Utils::responseResult);
    }
}
