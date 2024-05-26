package com.christ.erp.services.handlers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.*;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dto.admission.settings.AdmPrerequisiteSettingsDTO;
import com.christ.erp.services.dto.admission.settings.AdmPrerequisiteSettingsDetailsDTO;
import com.christ.erp.services.dto.admission.settings.AdmPrerequisiteSettingsDetailsPeriodDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.settings.ExamAssessmentTemplateDetailsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.admission.CommonAdmissionTransaction;
import com.christ.erp.services.transactions.admission.settings.ApplicationPrerequisitesTransaction;
import com.christ.erp.services.transactions.admission.settings.ProgrammeSettingsTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.DateFormatSymbols;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationPrerequisitesHandler {
//    private static volatile ApplicationPrerequisitesHandler applicationPrerequisitesHandler = null;
//    CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

    @Autowired
    ApplicationPrerequisitesTransaction applicationPrerequisitesTransaction1;
    @Autowired
    ProgrammeSettingsTransaction programmeSettingsTransaction;
//    ApplicationPrerequisitesTransaction applicationPrerequisitesTransaction = ApplicationPrerequisitesTransaction.getInstance();
//
//    public static ApplicationPrerequisitesHandler getInstance() {
//        if (applicationPrerequisitesHandler == null) {
//            applicationPrerequisitesHandler = new ApplicationPrerequisitesHandler();
//        }
//        return applicationPrerequisitesHandler;
//    }

//	public ApiResult<ModelBaseDTO> saveOrUpdate(AdmPrerequisiteSettingsDTO admPrerequisiteSettingsDTO, String userId) throws Exception {
//		ApiResult<ModelBaseDTO> response = new ApiResult<>();
//		boolean isSaved = false;
//		boolean flag = true;
//		if (Utils.isNullOrEmpty(admPrerequisiteSettingsDTO.getIsEdit())) {
//			if (Utils.isNullOrEmpty(admPrerequisiteSettingsDTO.id)) {
//				if (applicationPrerequisitesTransaction.duplicateCheck(admPrerequisiteSettingsDTO)) {
//					response.failureMessage = " Duplicate entry for the Programme for the same Academic Year and Admission type.";
//					flag = false;
//				}
//			}
//		}
//		if (flag) {
//			boolean isLocationUpdated = true;
//			boolean isPeriodSettingUpdated = true;
//			Set<AdmPrerequisiteSettingsDBO> admPrerequisiteSettingsDBOS = new HashSet<>();
//			ArrayList<Tuple> filteredLocation = new ArrayList<>();
//			List<Tuple> locationData = applicationPrerequisitesTransaction.getLocation(admPrerequisiteSettingsDTO.academicYear.id, admPrerequisiteSettingsDTO.programme.id);
//			for (PrerequisiteSettingsDTO dto : admPrerequisiteSettingsDTO.prerequisitesettings) {
//				filteredLocation.addAll(locationData);
//				for (LocationModel location : dto.location) {
//					for (Tuple list : locationData) {
//						String ids = String.valueOf(list.get("id"));
//						if (!Utils.isNullOrEmpty(location.id)) {
//							if (Integer.parseInt(location.id) == Integer.parseInt(ids)) {
//								filteredLocation.remove(list);
//							}
//						}
//					}
//				}
//				ArrayList<Tuple> filteredPeriod = new ArrayList<>();
//				if (!Utils.isNullOrEmpty(dto.id)) {
//					List<Tuple> periodList = applicationPrerequisitesTransaction.getPeriodList(Integer.parseInt(dto.id));
//					filteredPeriod.addAll(periodList);
//					ArrayList<Tuple> toRemovearray = new ArrayList<>();
//					for (AdmPreRequisiteSettingPeriodDTO periodDTO : dto.admPreRequisiteSettingPeriodDTOS) {
//						for (Tuple period : filteredPeriod) {
//							String pid = String.valueOf(period.get("id"));
//							if (!Utils.isNullOrEmpty(periodDTO.id)) {
//								if (Integer.parseInt(periodDTO.id) == Integer.parseInt(pid)) {
//									toRemovearray.add(period);
//								}
//							}
//						}
//					}
//					if (toRemovearray.size() > 0) {
//						filteredPeriod.removeAll(toRemovearray);
//					}
//				}
//				if (filteredLocation.size() > 0) {
//					for (Tuple filterLocId : filteredLocation) {
//						isLocationUpdated = applicationPrerequisitesTransaction.deleteLocation(filterLocId);
//						if (isLocationUpdated == false) {
//							break;
//						}
//					}
//				}
//				if (filteredPeriod.size() > 0) {
//					for (Tuple filterPerId : filteredPeriod) {
//						isPeriodSettingUpdated = applicationPrerequisitesTransaction.deletePeriod(filterPerId);
//						if (isPeriodSettingUpdated == false) {
//							break;
//						}
//					}
//				}
//			}
//			if (isLocationUpdated && isPeriodSettingUpdated) {
//				for (PrerequisiteSettingsDTO dto : admPrerequisiteSettingsDTO.prerequisitesettings) {
//					for (LocationModel location : dto.location) {
//						AdmPrerequisiteSettingsDBO dbo = new AdmPrerequisiteSettingsDBO();
//						if (!Utils.isNullOrEmpty(location.id)) {
//							dbo.id = Integer.parseInt(location.id);
//							dbo.modifiedUsersId = Integer.parseInt(userId);
//						} else {
//							dbo.createdUsersId = Integer.parseInt(userId);
//						}
//						if (!Utils.isNullOrEmpty(admPrerequisiteSettingsDTO.academicYear)) {
//							ErpAcademicYearDBO academicYear = new ErpAcademicYearDBO();
//							academicYear.id = Integer.parseInt(admPrerequisiteSettingsDTO.academicYear.id);
//							dbo.erpAcademicYearDBO = academicYear;
//						}
//						if (!Utils.isNullOrEmpty(admPrerequisiteSettingsDTO.programme)) {
//							ErpProgrammeDBO programme = new ErpProgrammeDBO();
//							programme.id = Integer.valueOf(admPrerequisiteSettingsDTO.programme.id);
//							dbo.erpProgrammeDBO = programme;
//						}
//						if (!Utils.isNullOrEmpty(admPrerequisiteSettingsDTO.getAdmAdmissionTypeDTO())) {
//							AdmAdmissionTypeDBO admAdmissionTypeDBO = new AdmAdmissionTypeDBO();
//							admAdmissionTypeDBO.setId(Integer.parseInt(admPrerequisiteSettingsDTO.getAdmAdmissionTypeDTO().getValue()));
//							dbo.setAdmAdmissionTypeDBO(admAdmissionTypeDBO);
//						}
//						if (!Utils.isNullOrEmpty(dto.PreRequisiteExam)) {
//							AdmPrerequisiteExamDBO exampre = new AdmPrerequisiteExamDBO();
//							exampre.id = Integer.valueOf(dto.PreRequisiteExam.id);
//							dbo.admPrerequisiteExamDBO = exampre;
//						}
//						if (!Utils.isNullOrEmpty(dto.minmarksforChristite)) {
//							dbo.minMarksForChristite = Integer.valueOf(dto.minmarksforChristite);
//						}
//						if (!Utils.isNullOrEmpty(dto.totalMarks)) {
//							dbo.totalMarks = Integer.parseInt(dto.totalMarks);
//						}
//						if (!Utils.isNullOrEmpty(dto.minimumMarks)) {
//							dbo.minMarks = Integer.valueOf(dto.minimumMarks);
//						}
//						if (!Utils.isNullOrEmpty(dto.mandatorys)) {
//							dbo.isExamMandatory = dto.mandatorys;
//						}
//						if (!Utils.isNullOrEmpty(location)) {
//							ErpLocationDBO locations = new ErpLocationDBO();
//							locations.id = Integer.parseInt(location.value);
//							dbo.erpLocationDBO = locations;
//						}
//						if (!Utils.isNullOrEmpty(dto.admPreRequisiteSettingPeriodDTOS)) {
//							Set<AdmPrerequisiteSettingPeriodDBO> period = new HashSet<>();
//							for (AdmPreRequisiteSettingPeriodDTO periodDTO : dto.admPreRequisiteSettingPeriodDTOS) {
//								AdmPrerequisiteSettingPeriodDBO periodDBO = new AdmPrerequisiteSettingPeriodDBO();
//								periodDBO.examYear = Integer.valueOf(periodDTO.year);
//								periodDBO.examMonth = Integer.valueOf(periodDTO.month);
//								if (!Utils.isNullOrEmpty(periodDTO.id) && !Utils.isNullOrEmpty(location.id)) {
//									periodDBO.id = Integer.valueOf(periodDTO.id);
//									periodDBO.modifiedUsersId = Integer.parseInt(userId);
//								} else {
//									periodDBO.createdUsersId = Integer.parseInt(userId);
//								}
//								periodDBO.admPrerequisiteSettingsDBO = dbo;
//								periodDBO.recordStatus = 'A';
//								period.add(periodDBO);
//							}
//							dbo.admPrerequisiteSettingPeriodDBOS = period;
//						}
//						dbo.recordStatus = 'A';
//						admPrerequisiteSettingsDBOS.add(dbo);
//					}
//				}
//				if (admPrerequisiteSettingsDBOS.size() > 0) {
//					isSaved = applicationPrerequisitesTransaction.saveOrUpdate(admPrerequisiteSettingsDBOS);
//				}
//			}
//			if (isSaved) {
//				response.success = true;
//			}
//		}
//		return response;
//	}

//	public List<AdmPrerequisiteSettingsDTO> getGridData() throws Exception {
//		List<AdmPrerequisiteSettingsDTO> gridList = null;
//		List<Tuple> list = applicationPrerequisitesTransaction.getGridData();
//		if (!Utils.isNullOrEmpty(list)) {
//			gridList = new ArrayList<>();
//			for (Tuple tuple : list) {
//				AdmPrerequisiteSettingsDTO dto = new AdmPrerequisiteSettingsDTO();
//				dto.academicYear = new ExModelBaseDTO();
//				dto.academicYear.id = String.valueOf(tuple.get("academicYearId"));
//				dto.academicYear.text = String.valueOf(tuple.get("academicYearName"));
//				dto.programme = new ExModelBaseDTO();
//				dto.programme.id = String.valueOf(tuple.get("programmeId"));
//				dto.programme.text = String.valueOf(tuple.get("programmeName"));
//				dto.setAdmAdmissionTypeDTO(new SelectDTO());
//				dto.getAdmAdmissionTypeDTO().setLabel(String.valueOf(tuple.get("admission_type")));
//				dto.getAdmAdmissionTypeDTO().setValue(String.valueOf(tuple.get("adm_admission_type_id")));
//				gridList.add(dto);
//			}
//		}
//		return gridList;
//	}

//	public boolean delete(AdmPrerequisiteSettingsDTO data) throws Exception {
//		boolean result = false;
//		result = applicationPrerequisitesTransaction.delete(data);
//		return result;
//	}

//	public AdmPrerequisiteSettingsDTO edit(String academicYearId, String programmeId) throws Exception {
//		AdmPrerequisiteSettingsDTO dto = null;
//		if (!Utils.isNullOrEmpty(academicYearId) && !Utils.isNullOrEmpty(programmeId)) {
//			List<Tuple> listPreRequisiteExam = applicationPrerequisitesTransaction.getPrerequisiteExams(academicYearId, programmeId);
//			String programmeName = applicationPrerequisitesTransaction.getProgrammeNameById(programmeId);
//			if (listPreRequisiteExam.size() > 0) {
//				dto = new AdmPrerequisiteSettingsDTO();
//				dto.id = 0;
//				dto.academicYear = new ExModelBaseDTO();
//				dto.academicYear.id = academicYearId;
//				dto.academicYear.text = "";
//				dto.programme = new ExModelBaseDTO();
//				dto.programme.id = programmeId;
//				dto.programme.text = programmeName;
//				dto.setIsEdit(true);
//				dto.prerequisitesettings = new ArrayList<>();
//				for (Tuple tuple : listPreRequisiteExam) {
//					PrerequisiteSettingsDTO dto_sub = new PrerequisiteSettingsDTO();
//					dto_sub.PreRequisiteExam = new ExModelBaseDTO();
//					dto_sub.PreRequisiteExam.id = tuple.get("prerequisiteExamId").toString();
//					dto_sub.PreRequisiteExam.text = tuple.get("prerequisiteExamName").toString();
//					dto_sub.minimumMarks = tuple.get("minMarks").toString();
//					if (Utils.isNullOrEmpty(tuple.get("minMarksChristite")))
//						dto_sub.minmarksforChristite = null;
//					else
//						dto_sub.minmarksforChristite = tuple.get("minMarksChristite").toString();
//					dto_sub.totalMarks = tuple.get("totalMarks").toString();
//					dto_sub.mandatorys = (Boolean) tuple.get("mandatory");
//					dto_sub.id = String.valueOf(tuple.get("id"));
//					dto_sub.admPreRequisiteSettingPeriodDTOS = new ArrayList<>();
//					Integer settingsId = (Integer) tuple.get("id");
//					List<Tuple> periodList = applicationPrerequisitesTransaction.getPeriodList(settingsId);
//					for (Tuple tuple_period : periodList) {
//						AdmPreRequisiteSettingPeriodDTO periodDto = new AdmPreRequisiteSettingPeriodDTO();
//						periodDto.year = String.valueOf(tuple_period.get("years"));
//						periodDto.month = String.valueOf(tuple_period.get("months"));
//						periodDto.id = String.valueOf(tuple_period.get("id"));
//						dto_sub.admPreRequisiteSettingPeriodDTOS.add(periodDto);
//					}
//					dto_sub.location = new ArrayList<>();
//					LocationModel loc_dto = new LocationModel();
//					loc_dto.value = tuple.get("locationId").toString();
//					loc_dto.label = tuple.get("locationName").toString();
//					loc_dto.id = String.valueOf(tuple.get("id"));
//					dto_sub.location.add(loc_dto);
//					dto.prerequisitesettings.add(dto_sub);
//				}
//			}
//		}
//		return dto;
//	}

//    public ApiResult<List<LookupItemDTO>> getYearList() {
//        ApiResult<List<LookupItemDTO>> generatedYear = new ApiResult<>();
//        try {
//            Integer currentAcademicYear = commonApiTransaction.getCurrentAcademicYear();
//            int minYear = currentAcademicYear;
//            int maxYear = 2050;
//            generatedYear.dto = new ArrayList<>();
//            for (int i = minYear; i <= maxYear; i++) {
//                LookupItemDTO itemInfo = new LookupItemDTO();
//                itemInfo.value = String.valueOf(i);
//                itemInfo.label = String.valueOf(i);
//                generatedYear.dto.add(itemInfo);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return generatedYear;
//    }

    public Mono<ApiResult> delete(int id, String userId) {
        return applicationPrerequisitesTransaction1.delete(id, userId).map(Utils::responseResult);
    }

    public Flux<AdmPrerequisiteSettingsDTO> getGridData(Integer yearId, String programmeId, String typeId) {
        return applicationPrerequisitesTransaction1.getGridData(yearId, programmeId, typeId).flatMapMany(Flux::fromIterable).map(this::convertDboListToDtoList);
    }

    private AdmPrerequisiteSettingsDTO convertDboListToDtoList(AdmPrerequisiteSettingsDBO dbo) {
        AdmPrerequisiteSettingsDTO dto = new AdmPrerequisiteSettingsDTO();
        dto.setId(dbo.getId());
        if (!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO())) {
            dto.setErpProgrammeDTO(new SelectDTO());
            dto.getErpProgrammeDTO().setValue(String.valueOf(dbo.getErpProgrammeDBO().getId()));
            dto.getErpProgrammeDTO().setLabel(dbo.getErpProgrammeDBO().getProgrammeNameForApplication());
        }
        if (!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
            dto.setErpAcademicYearDTO(new SelectDTO());
            dto.getErpAcademicYearDTO().setValue(String.valueOf(dbo.getErpAcademicYearDBO().getId()));
            dto.getErpAcademicYearDTO().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmAdmissionTypeDBO())) {
            dto.setAdmAdmissionTypeDTO(new SelectDTO());
            dto.getAdmAdmissionTypeDTO().setValue(String.valueOf(dbo.getAdmAdmissionTypeDBO().getId()));
            dto.getAdmAdmissionTypeDTO().setLabel(dbo.getAdmAdmissionTypeDBO().getAdmissionType());
        }
        return dto;
    }

    public Mono<AdmPrerequisiteSettingsDTO> edit(Integer id) {
        var dbo = applicationPrerequisitesTransaction1.edit(id);
        return !Utils.isNullOrEmpty(dbo) ? Mono.just(convertDboToDto(dbo)) : Mono.empty();
    }

    public AdmPrerequisiteSettingsDTO convertDboToDto(AdmPrerequisiteSettingsDBO dbo) {
        AdmPrerequisiteSettingsDTO dto = new AdmPrerequisiteSettingsDTO();
        dto.setId(dbo.getId());
        dto.setIsDocumentUploadMandatory(dbo.getIsDocumentUploadMandatory());
        dto.setIsExamMandatory(dbo.getIsExamMandatory());
        dto.setIsRegisterNoMandatory(dbo.getIsRegisterNoMandatory());
        dto.setIsScoreDetailsMandatory(dbo.getIsScoreDetailsMandatory());
        if (!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO())) {
            dto.setErpProgrammeDTO(new SelectDTO());
            dto.getErpProgrammeDTO().setValue(String.valueOf(dbo.getErpProgrammeDBO().getId()));
            dto.getErpProgrammeDTO().setLabel(dbo.getErpProgrammeDBO().getProgrammeNameForApplication());
        }
        if (!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
            dto.setErpAcademicYearDTO(new SelectDTO());
            dto.getErpAcademicYearDTO().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
            dto.getErpAcademicYearDTO().setValue(String.valueOf(dbo.getErpAcademicYearDBO().getId()));
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmAdmissionTypeDBO())) {
            dto.setAdmAdmissionTypeDTO(new SelectDTO());
            dto.getAdmAdmissionTypeDTO().setLabel(dbo.getAdmAdmissionTypeDBO().getAdmissionType());
            dto.getAdmAdmissionTypeDTO().setValue(String.valueOf(dbo.getAdmAdmissionTypeDBO().getId()));
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmPrerequisiteSettingsDetailsDBOSet())) {
            dto.setAdmPrerequisiteSettingsDetailsDTOList(new ArrayList<>());
            dbo.getAdmPrerequisiteSettingsDetailsDBOSet().forEach(detailSet -> {
                if (detailSet.getRecordStatus() == 'A') {
                    AdmPrerequisiteSettingsDetailsDTO detailDto = new AdmPrerequisiteSettingsDetailsDTO();
                    detailDto.setId(detailSet.getId());
                    detailDto.setMinMarks(detailSet.getMinMarks());
                    detailDto.setMinMarksForChristite(detailSet.getMinMarksForChristite());
                    detailDto.setTotalMarks(detailSet.getTotalMarks());
                    if (!Utils.isNullOrEmpty(detailSet.getErpLocationDBO())) {
                        detailDto.setErpLocationDTO(new SelectDTO());
                        detailDto.getErpLocationDTO().setValue(String.valueOf(detailSet.getErpLocationDBO().getId()));
                        detailDto.getErpLocationDTO().setLabel(detailSet.getErpLocationDBO().getLocationName());
                    }
                    if (!Utils.isNullOrEmpty(detailSet.getAdmPrerequisiteExamDBO())) {
                        detailDto.setAdmPrerequisiteExamDTO(new SelectDTO());
                        detailDto.getAdmPrerequisiteExamDTO().setLabel(detailSet.getAdmPrerequisiteExamDBO().getExamName());
                        detailDto.getAdmPrerequisiteExamDTO().setValue(String.valueOf(detailSet.getAdmPrerequisiteExamDBO().getId()));
                    }
                    if (!Utils.isNullOrEmpty(detailSet.getAdmPrerequisiteSettingsDetailsPeriodDBOSet())) {
                        detailDto.setAdmPrerequisiteSettingsDetailsPeriodDTOList(new ArrayList<>());
                        detailSet.getAdmPrerequisiteSettingsDetailsPeriodDBOSet().forEach(periodSet -> {
                            if (periodSet.getRecordStatus() == 'A') {
                                AdmPrerequisiteSettingsDetailsPeriodDTO periodDto = new AdmPrerequisiteSettingsDetailsPeriodDTO();
                                periodDto.setId(periodSet.getId());
                                periodDto.setYear(periodSet.getExamYear());
                                periodDto.setMonth(new SelectDTO());
                                periodDto.getMonth().setLabel(new DateFormatSymbols().getMonths()[periodSet.getExamMonth() - 1]);
                                periodDto.getMonth().setValue(String.valueOf(periodSet.getExamMonth()));
                                detailDto.getAdmPrerequisiteSettingsDetailsPeriodDTOList().add(periodDto);
                            }
                        });
                    }
                    dto.getAdmPrerequisiteSettingsDetailsDTOList().add(detailDto);
                    dto.getAdmPrerequisiteSettingsDetailsDTOList().sort(Comparator.comparing(s ->s.getAdmPrerequisiteExamDTO().getLabel()));
                }
            });
        }
        return dto;
    }

    public Mono<ApiResult> saveOrUpdate(Mono<AdmPrerequisiteSettingsDTO> dto, String userId) {
        return dto.handle((admPrerequisiteSettingsDTO, synchronousSink) -> {
            boolean istrue = applicationPrerequisitesTransaction1.duplicateCheck(admPrerequisiteSettingsDTO);
            if (istrue) {
                synchronousSink.error(new DuplicateException("Pre-requisite details already added for the programme, year and admission type "));
            } else {
                synchronousSink.next(admPrerequisiteSettingsDTO);
            }
        }).cast(AdmPrerequisiteSettingsDTO.class).map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
            if (!Utils.isNullOrEmpty(s.getId())) {
                applicationPrerequisitesTransaction1.update(s);
            } else {
                applicationPrerequisitesTransaction1.save(s);
            }
            return Mono.just(Boolean.TRUE);
        }).map(Utils::responseResult);

    }

    public AdmPrerequisiteSettingsDBO convertDtoToDbo(AdmPrerequisiteSettingsDTO dto, String userId) {
        AdmPrerequisiteSettingsDBO dbo = new AdmPrerequisiteSettingsDBO();
        if (!Utils.isNullOrEmpty(dto.getId())) {
            dbo = applicationPrerequisitesTransaction1.edit(dto.getId());
            dbo.setModifiedUsersId(Integer.valueOf(userId));
        }
        dbo.setIsExamMandatory(dto.getIsExamMandatory());
        dbo.setIsRegisterNoMandatory(dto.getIsRegisterNoMandatory());
        dbo.setIsDocumentUploadMandatory(dto.getIsDocumentUploadMandatory());
        dbo.setIsScoreDetailsMandatory(dto.getIsScoreDetailsMandatory());
        dbo.setCreatedUsersId(Integer.valueOf(userId));
        dbo.setRecordStatus('A');
        if (!Utils.isNullOrEmpty(dto.getErpAcademicYearDTO())) {
            if (!Utils.isNullOrEmpty(dto.getErpAcademicYearDTO().getValue())) {
                Map<Integer ,Integer> admissionTypeMap = new HashMap<>();
                var admissionType = programmeSettingsTransaction.getAdmissionType();
                if(!Utils.isNullOrEmpty(admissionType)) {
                    admissionTypeMap = admissionType.stream().collect(Collectors.toMap(AdmAdmissionTypeDBO::getId, AdmAdmissionTypeDBO::getAdmissionIntakeYearNumber));
                }
                if(!Utils.isNullOrEmpty(admissionTypeMap)  &&  !Utils.isNullOrEmpty(dto.getAdmAdmissionTypeDTO()) &&  admissionTypeMap.containsKey(dto.getAdmAdmissionTypeDTO().getValue())){
                    Integer intake = admissionTypeMap.get(dto.getAdmAdmissionTypeDTO().getValue());
                    Integer value = intake - 1;
                    if(value == 0){
                        dbo.setAdmBatchYear(new ErpAcademicYearDBO());
                        dbo.getAdmBatchYear().setId(Integer.parseInt(dto.getErpAcademicYearDTO().getValue()));
                    } else {
                        Integer yearId = programmeSettingsTransaction.getAcademic(Integer.parseInt(dto.getErpAcademicYearDTO().getValue())-value);
                        dbo.setAdmBatchYear(new ErpAcademicYearDBO());
                        dbo.getAdmBatchYear().setId(yearId);
                    }
                }
                dbo.setErpAcademicYearDBO(new ErpAcademicYearDBO());
                dbo.getErpAcademicYearDBO().setId(Integer.valueOf(dto.getErpAcademicYearDTO().getValue()));
            }
        }
        if (!Utils.isNullOrEmpty(dto.getErpProgrammeDTO())) {
            if (!Utils.isNullOrEmpty(dto.getErpProgrammeDTO().getValue())) {
                dbo.setErpProgrammeDBO(new ErpProgrammeDBO());
                dbo.getErpProgrammeDBO().setId(Integer.parseInt(dto.getErpProgrammeDTO().getValue()));
            }
        }
        if (!Utils.isNullOrEmpty(dto.getAdmAdmissionTypeDTO())) {
            if (!Utils.isNullOrEmpty(dto.getAdmAdmissionTypeDTO().getValue())) {
                dbo.setAdmAdmissionTypeDBO(new AdmAdmissionTypeDBO());
                dbo.getAdmAdmissionTypeDBO().setId(Integer.parseInt(dto.getAdmAdmissionTypeDTO().getValue()));
            }
        }
        var dbo1 =dbo;
        Map<Integer, AdmPrerequisiteSettingsDetailsDBO> detailMap = new LinkedHashMap<>();
        if (!Utils.isNullOrEmpty(dbo.getAdmPrerequisiteSettingsDetailsDBOSet())) {
            detailMap = dbo.getAdmPrerequisiteSettingsDetailsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(AdmPrerequisiteSettingsDetailsDBO::getId, s -> s));
        }
        var detailMap1 = detailMap;
        if (!Utils.isNullOrEmpty(dto.getAdmPrerequisiteSettingsDetailsDTOList())) {
            Set<AdmPrerequisiteSettingsDetailsDBO> admPrerequisiteSettingsDetailsDBOSet = new HashSet<>();
            dto.getAdmPrerequisiteSettingsDetailsDTOList().forEach(dtoList -> {
                AdmPrerequisiteSettingsDetailsDBO admPrerequisiteSettingsDetailsDBO = null;
                if (detailMap1.containsKey(dtoList.getId())) {
                    admPrerequisiteSettingsDetailsDBO = detailMap1.get(dtoList.getId());
                    admPrerequisiteSettingsDetailsDBO.setModifiedUsersId(Integer.valueOf(userId));
                    detailMap1.remove(dtoList.getId());
                } else {
                    admPrerequisiteSettingsDetailsDBO = new AdmPrerequisiteSettingsDetailsDBO();
                    admPrerequisiteSettingsDetailsDBO.setCreatedUsersId(Integer.valueOf(userId));
                }
                admPrerequisiteSettingsDetailsDBO.setAdmPrerequisiteSettingsDBO(dbo1);
                admPrerequisiteSettingsDetailsDBO.setRecordStatus('A');
                admPrerequisiteSettingsDetailsDBO.setMinMarks(dtoList.getMinMarks());
                admPrerequisiteSettingsDetailsDBO.setTotalMarks(dtoList.getTotalMarks());
                admPrerequisiteSettingsDetailsDBO.setMinMarksForChristite(dtoList.getMinMarksForChristite());
                if (!Utils.isNullOrEmpty(dtoList.getAdmPrerequisiteExamDTO())) {
                    if (!Utils.isNullOrEmpty(dtoList.getAdmPrerequisiteExamDTO().getValue())) {
                        admPrerequisiteSettingsDetailsDBO.setAdmPrerequisiteExamDBO(new AdmPrerequisiteExamDBO());
                        admPrerequisiteSettingsDetailsDBO.getAdmPrerequisiteExamDBO().setId(Integer.parseInt(dtoList.getAdmPrerequisiteExamDTO().getValue()));
                    }
                }
                if (!Utils.isNullOrEmpty(dtoList.getErpLocationDTO())) {
                    if (!Utils.isNullOrEmpty(dtoList.getErpLocationDTO().getValue())) {
                        admPrerequisiteSettingsDetailsDBO.setErpLocationDBO(new ErpLocationDBO());
                        admPrerequisiteSettingsDetailsDBO.getErpLocationDBO().setId(Integer.valueOf(dtoList.getErpLocationDTO().getValue()));
                    }
                }
                Map<Integer, AdmPrerequisiteSettingsDetailsPeriodDBO> periodMap = new LinkedHashMap<>();
                if (!Utils.isNullOrEmpty(admPrerequisiteSettingsDetailsDBO.getAdmPrerequisiteSettingsDetailsPeriodDBOSet())) {
                    periodMap = admPrerequisiteSettingsDetailsDBO.getAdmPrerequisiteSettingsDetailsPeriodDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(AdmPrerequisiteSettingsDetailsPeriodDBO::getId, s -> s));
                }
                var periodMap1 = periodMap;
                if (!Utils.isNullOrEmpty(dtoList.getAdmPrerequisiteSettingsDetailsPeriodDTOList())) {
                    var admPrerequisiteSettingsDetailsDBO1 = admPrerequisiteSettingsDetailsDBO;
                    Set<AdmPrerequisiteSettingsDetailsPeriodDBO> admPrerequisiteSettingsDetailsPeriodDBOSet = new HashSet<>();
                    dtoList.getAdmPrerequisiteSettingsDetailsPeriodDTOList().forEach(periodList -> {
                        AdmPrerequisiteSettingsDetailsPeriodDBO admPrerequisiteSettingsDetailsPeriodDBO = null;
                        if (periodMap1.containsKey(periodList.getId())) {
                            admPrerequisiteSettingsDetailsPeriodDBO = periodMap1.get(periodList.getId());
                            admPrerequisiteSettingsDetailsPeriodDBO.setModifiedUsersId(Integer.valueOf(userId));
                            periodMap1.remove(periodList.getId());
                        } else {
                            admPrerequisiteSettingsDetailsPeriodDBO = new AdmPrerequisiteSettingsDetailsPeriodDBO();
                            admPrerequisiteSettingsDetailsPeriodDBO.setCreatedUsersId(Integer.valueOf(userId));
                        }
                        admPrerequisiteSettingsDetailsPeriodDBO.setCreatedUsersId(Integer.valueOf(userId));
                        admPrerequisiteSettingsDetailsPeriodDBO.setRecordStatus('A');
                        admPrerequisiteSettingsDetailsPeriodDBO.setAdmPrerequisiteSettingsDetailsDBO(admPrerequisiteSettingsDetailsDBO1);
                        admPrerequisiteSettingsDetailsPeriodDBO.setExamYear(periodList.getYear());
                                if (!Utils.isNullOrEmpty(periodList.getMonth())) {
                                    if (!Utils.isNullOrEmpty(periodList.getMonth().getValue())) {
                                        admPrerequisiteSettingsDetailsPeriodDBO.setExamMonth(Integer.valueOf(periodList.getMonth().getValue()));
                                    }
                                }
                        admPrerequisiteSettingsDetailsPeriodDBOSet.add(admPrerequisiteSettingsDetailsPeriodDBO);
                    });
                    if (!Utils.isNullOrEmpty(periodMap1)) {
                        periodMap1.forEach((mapId, mapValue) -> {
                            mapValue.setModifiedUsersId(Integer.valueOf(userId));
                            mapValue.setRecordStatus('D');
                            admPrerequisiteSettingsDetailsPeriodDBOSet.add(mapValue);
                        });
                    }
                    admPrerequisiteSettingsDetailsDBO.setAdmPrerequisiteSettingsDetailsPeriodDBOSet(admPrerequisiteSettingsDetailsPeriodDBOSet);
                }
                admPrerequisiteSettingsDetailsDBOSet.add(admPrerequisiteSettingsDetailsDBO);
            });
            if (!Utils.isNullOrEmpty(detailMap1)) {
                detailMap1.forEach((mapId, mapValue) -> {
                    mapValue.setModifiedUsersId(Integer.valueOf(userId));
                    mapValue.setRecordStatus('D');
                    mapValue.getAdmPrerequisiteSettingsDetailsPeriodDBOSet().forEach(s -> {
                        s.setModifiedUsersId(Integer.valueOf(userId));
                        s.setRecordStatus('D');
                    });
                    admPrerequisiteSettingsDetailsDBOSet.add(mapValue);
                });
            }
            dbo.setAdmPrerequisiteSettingsDetailsDBOSet(admPrerequisiteSettingsDetailsDBOSet);
        }
        return dbo;
    }
}
