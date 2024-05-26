package com.christ.erp.services.handlers.curriculum.curriculumDesign;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.AcaGraduateAttributesDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpDepartmentMissionVisionDetailsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammePeoMissionMatrixDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ObeProgrammeOutcomeDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ObeProgrammeOutcomeDetailsAttributeDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ObeProgrammeOutcomeDetailsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ObeProgrammeOutcomeUploadDetailsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.curriculumDesign.ProgrammeEducationalObjectivesMappingDTO;
import com.christ.erp.services.dto.curriculum.curriculumDesign.ProgrammeLearningOutcomeDTO;
import com.christ.erp.services.dto.curriculum.curriculumDesign.ProgrammeOutcomeDefinitionsDTO;
import com.christ.erp.services.dto.curriculum.curriculumDesign.ProgrammeSpecificOutcomeDTO;
import com.christ.erp.services.dto.curriculum.settings.DepartmentMissionVisionDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.curriculum.curriculumDesign.ProgrammeOutcomeDefinitionsTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({"rawtypes"})
@Service
public class ProgrammeOutcomeDefinitionsHandler {

	@Autowired
	private ProgrammeOutcomeDefinitionsTransaction programmeOutcomeDefinitionsTransaction;

	public Mono<List<ProgrammeOutcomeDefinitionsDTO>> getGridData(String yearId, String departId) {
		List<Tuple> data = programmeOutcomeDefinitionsTransaction.getGridData(yearId,departId);
		return this.convertDBOToDTO1(data);
	}

	public Mono<List<ProgrammeOutcomeDefinitionsDTO>> convertDBOToDTO1(List<Tuple>  dbos) {
		List<ProgrammeOutcomeDefinitionsDTO> dtos = new ArrayList<ProgrammeOutcomeDefinitionsDTO>();
		Map<Integer,ProgrammeOutcomeDefinitionsDTO> map = new HashMap<Integer, ProgrammeOutcomeDefinitionsDTO>(); 
		if(!Utils.isNullOrEmpty(dbos)) {
			dbos.forEach( dbo -> {
				if(!map.containsKey(Integer.parseInt(dbo.get("batchwiseId").toString()))) {
					ProgrammeOutcomeDefinitionsDTO dto = new ProgrammeOutcomeDefinitionsDTO();
					dto.setBatchwiseSettingId(Integer.parseInt(dbo.get("batchwiseId").toString()));
					dto.setDegreeLevel(dbo.get("progLevel").toString());
					dto.setProgramme(dbo.get("progName").toString());
					if(Utils.isNullOrEmpty(dbo.get("progOutComeDetailsId"))) {
						dto.setIsDefined(false);
					} else {
						dto.setIsDefined(true);
					}
					dto.setProgrammeOfferedCampus(new ArrayList<String>());
					dto.getProgrammeOfferedCampus().add(!Utils.isNullOrEmpty(dbo.get("campus"))?dbo.get("campus").toString():dbo.get("location").toString());
					map.put(Integer.parseInt(dbo.get("batchwiseId").toString()), dto);
				} else {
					ProgrammeOutcomeDefinitionsDTO dto = map.get(Integer.parseInt(dbo.get("batchwiseId").toString()));
					if(!dto.getProgrammeOfferedCampus().contains(!Utils.isNullOrEmpty(dbo.get("campus"))?dbo.get("campus").toString():dbo.get("location").toString())) {
						dto.getProgrammeOfferedCampus().add(!Utils.isNullOrEmpty(dbo.get("campus"))?dbo.get("campus").toString():dbo.get("location").toString());
						map.replace(Integer.parseInt(dbo.get("batchwiseId").toString()), dto);
					}
				}
			});
			if(!Utils.isNullOrEmpty(map)) {
				map.forEach((key,value) -> {
					dtos.add(value);
				});
			}
		}
		return  dtos.isEmpty()?Mono.error(new NotFoundException(null)):Mono.just(dtos);
	}

	public Mono<ProgrammeOutcomeDefinitionsDTO> edit(int batchwiseSettingId) {
		return this.convertDboToDto(programmeOutcomeDefinitionsTransaction.edit(batchwiseSettingId));

	}

	public Mono<ProgrammeOutcomeDefinitionsDTO> convertDboToDto(List<ObeProgrammeOutcomeDBO> dbos) {
		List<ProgrammeOutcomeDefinitionsDTO> dtos = new ArrayList<ProgrammeOutcomeDefinitionsDTO>();
		if(!Utils.isNullOrEmpty(dbos)) {
			ProgrammeOutcomeDefinitionsDTO dto = new ProgrammeOutcomeDefinitionsDTO();
			Set<Integer> duplicateDocumentcheck = new HashSet<Integer>();
			dto.setBatchwiseSettingId(dbos.get(0).getErpProgrammeBatchwiseSettingsDBO().getId());
			dto.setProgrammeOutcomeList(new ArrayList<ProgrammeSpecificOutcomeDTO>());
			dto.setProgrammeSpecificOutcomeList(new ArrayList<ProgrammeSpecificOutcomeDTO>());
			dto.setProgrammeLearningGoalsList(new ArrayList<ProgrammeSpecificOutcomeDTO>());
			dto.setProgramEducationalObjectiveList(new ArrayList<ProgrammeSpecificOutcomeDTO>());
			Map<Integer,ProgrammeSpecificOutcomeDTO> programmeLearningGoalsMap = new HashMap<Integer, ProgrammeSpecificOutcomeDTO>();
			dbos.forEach(dbo ->{
				if("ProgramOutcome".equalsIgnoreCase(dbo.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeTypes().replaceAll("\\s", ""))) {
					dto.setIsPoDefined(true);
					dbo.getObeProgrammeOutcomeDetailsDBOSet().forEach(programmeOutcomeDBO -> {
						if(programmeOutcomeDBO.getRecordStatus() == 'A') {
							ProgrammeSpecificOutcomeDTO programmeOutcome = new  ProgrammeSpecificOutcomeDTO();
							programmeOutcome.setId(programmeOutcomeDBO.getId());
							programmeOutcome.setReferenceNumber(programmeOutcomeDBO.getReferenceNo());
							programmeOutcome.setReferenceNoOrder(programmeOutcomeDBO.getReferenceNoOrder());
							programmeOutcome.setStatement(programmeOutcomeDBO.getStatements());
							programmeOutcome.setGraduateAttributes(new ArrayList<SelectDTO>());
							if(!Utils.isNullOrEmpty(programmeOutcomeDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
								programmeOutcomeDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach(attribute -> {
									if(attribute.getRecordStatus() == 'A') {
										SelectDTO attributeDto = new SelectDTO();
										attributeDto.setValue(String.valueOf(attribute.getAcaGraduateAttributesDBO().getId()));
										attributeDto.setLabel(attribute.getAcaGraduateAttributesDBO().getGraduateAttributes());
										programmeOutcome.getGraduateAttributes().add(attributeDto);
									}
								});
							}
							if(!Utils.isNullOrEmpty(dbo.getErpApprovalLevelsDBO())) {
								programmeOutcome.setApprovedBy(new SelectDTO());
								programmeOutcome.getApprovedBy().setValue(String.valueOf(dbo.getErpApprovalLevelsDBO().getId()));
								programmeOutcome.getApprovedBy().setLabel(dbo.getErpApprovalLevelsDBO().getApprover());
							}

							programmeOutcome.setComments(dbo.getComments());
							programmeOutcome.setUploadDocuments(new ArrayList<EmpApplnAdvertisementImagesDTO>());
							if(!duplicateDocumentcheck.contains(dbo.getId())) {
								dbo.getObeProgrammeOutcomeUploadDetailsDBOSet().forEach( uploadDetails -> {
									if(uploadDetails.getRecordStatus() == 'A') {
										if(programmeOutcomeDBO.getReferenceNoOrder().doubleValue() == 1.0) {
											EmpApplnAdvertisementImagesDTO upload = new EmpApplnAdvertisementImagesDTO();
											upload.setId(String.valueOf(uploadDetails.getId()));
											File file = new File(uploadDetails.getDocumentUrl());
											//									if(file.exists() && !file.isDirectory()) { 
											upload.setExtension(uploadDetails.getDocumentUrl().substring(uploadDetails.getDocumentUrl().lastIndexOf(".")+1));
											upload.setUrl(uploadDetails.getDocumentUrl());
											String fileName = file.getName();
											upload.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
											upload.setRecordStatus(uploadDetails.getRecordStatus());
											programmeOutcome.getUploadDocuments().add(upload);
											duplicateDocumentcheck.add(dbo.getId());
											//									}
										}
									}
								});
							}
							dto.getProgrammeOutcomeList().add(programmeOutcome);
						}
					});
				}

				if("ProgramSpecificOutcome".equalsIgnoreCase(dbo.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeTypes().replaceAll("\\s", ""))) {
					dto.setIsPsoDefined(true);
					dbo.getObeProgrammeOutcomeDetailsDBOSet().forEach(programmeSpecificOutcomeDBO -> {
						if(programmeSpecificOutcomeDBO.getRecordStatus() == 'A') {
							ProgrammeSpecificOutcomeDTO programmeSpecificOutcome = new  ProgrammeSpecificOutcomeDTO();
							programmeSpecificOutcome.setId(programmeSpecificOutcomeDBO.getId());
							programmeSpecificOutcome.setReferenceNumber(programmeSpecificOutcomeDBO.getReferenceNo());
							programmeSpecificOutcome.setReferenceNoOrder(programmeSpecificOutcomeDBO.getReferenceNoOrder());
							programmeSpecificOutcome.setStatement(programmeSpecificOutcomeDBO.getStatements());
							programmeSpecificOutcome.setGraduateAttributes(new ArrayList<SelectDTO>());
							if(!Utils.isNullOrEmpty(programmeSpecificOutcomeDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
								programmeSpecificOutcomeDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach(attribute -> {
									if(attribute.getRecordStatus() == 'A') {
										SelectDTO attributeDto = new SelectDTO();
										attributeDto.setValue(String.valueOf(attribute.getAcaGraduateAttributesDBO().getId()));
										attributeDto.setLabel(attribute.getAcaGraduateAttributesDBO().getGraduateAttributes());
										programmeSpecificOutcome.getGraduateAttributes().add(attributeDto);
									}
								});
							}
							programmeSpecificOutcome.setApprovedBy(new SelectDTO());
							programmeSpecificOutcome.getApprovedBy().setValue(String.valueOf(dbo.getErpApprovalLevelsDBO().getId()));
							programmeSpecificOutcome.getApprovedBy().setLabel(dbo.getErpApprovalLevelsDBO().getApprover());
							programmeSpecificOutcome.setComments(dbo.getComments());
							programmeSpecificOutcome.setUploadDocuments(new ArrayList<EmpApplnAdvertisementImagesDTO>());
							if(!duplicateDocumentcheck.contains(dbo.getId())) {
								dbo.getObeProgrammeOutcomeUploadDetailsDBOSet().forEach( uploadDetails -> {
									if(uploadDetails.getRecordStatus() == 'A') {
										if(programmeSpecificOutcomeDBO.getReferenceNoOrder().doubleValue() == 1.0) {
											EmpApplnAdvertisementImagesDTO upload = new EmpApplnAdvertisementImagesDTO();
											upload.setId(String.valueOf(uploadDetails.getId()));
											File file = new File(uploadDetails.getDocumentUrl());
											//								if(file.exists() && !file.isDirectory()) { 
											upload.setExtension(uploadDetails.getDocumentUrl().substring(uploadDetails.getDocumentUrl().lastIndexOf(".")+1));
											upload.setUrl(uploadDetails.getDocumentUrl());
											String fileName = file.getName();
											upload.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
											upload.setRecordStatus(uploadDetails.getRecordStatus());
											programmeSpecificOutcome.getUploadDocuments().add(upload);
											duplicateDocumentcheck.add(dbo.getId());
											//								}	
										}
									}
								});
							}
							dto.getProgrammeSpecificOutcomeList().add(programmeSpecificOutcome);
						}
					});
				}

				if("ProgrammeLearningGoal".equalsIgnoreCase(dbo.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeTypes().replaceAll("\\s", ""))) {
					dto.setIsPlgDefined(true);
					dbo.getObeProgrammeOutcomeDetailsDBOSet().forEach(programmeLearningGoalsDBO -> {
						if(programmeLearningGoalsDBO.getRecordStatus() == 'A') {
							ProgrammeSpecificOutcomeDTO programmeLearningGoals = new  ProgrammeSpecificOutcomeDTO();
							programmeLearningGoals.setId(programmeLearningGoalsDBO.getId());
							programmeLearningGoals.setReferenceNumber(programmeLearningGoalsDBO.getReferenceNo());
							programmeLearningGoals.setReferenceNoOrder(programmeLearningGoalsDBO.getReferenceNoOrder());
							programmeLearningGoals.setStatement(programmeLearningGoalsDBO.getStatements());
							programmeLearningGoals.setGraduateAttributes(new ArrayList<SelectDTO>());
							if(!Utils.isNullOrEmpty(programmeLearningGoalsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
								programmeLearningGoalsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach(attribute -> {
									if(attribute.getRecordStatus() == 'A') {
										SelectDTO attributeDto = new SelectDTO();
										attributeDto.setValue(String.valueOf(attribute.getAcaGraduateAttributesDBO().getId()));
										attributeDto.setLabel(attribute.getAcaGraduateAttributesDBO().getGraduateAttributes());
										programmeLearningGoals.getGraduateAttributes().add(attributeDto);
									}
								});
							}
							programmeLearningGoals.setApprovedBy(new SelectDTO());
							programmeLearningGoals.getApprovedBy().setValue(String.valueOf(dbo.getErpApprovalLevelsDBO().getId()));
							programmeLearningGoals.getApprovedBy().setLabel(dbo.getErpApprovalLevelsDBO().getApprover());
							programmeLearningGoals.setComments(dbo.getComments());
							programmeLearningGoals.setUploadDocuments(new ArrayList<EmpApplnAdvertisementImagesDTO>());
							if(!duplicateDocumentcheck.contains(dbo.getId())) {
								dbo.getObeProgrammeOutcomeUploadDetailsDBOSet().forEach( uploadDetails -> {
									if(uploadDetails.getRecordStatus() == 'A') {
										if(programmeLearningGoalsDBO.getReferenceNoOrder().doubleValue() == 1.0) {
											EmpApplnAdvertisementImagesDTO upload = new EmpApplnAdvertisementImagesDTO();
											upload.setId(String.valueOf(uploadDetails.getId()));
											File file = new File(uploadDetails.getDocumentUrl());
											//								if(file.exists() && !file.isDirectory()) { 
											upload.setExtension(uploadDetails.getDocumentUrl().substring(uploadDetails.getDocumentUrl().lastIndexOf(".")+1));
											upload.setUrl(uploadDetails.getDocumentUrl());
											String fileName = file.getName();
											upload.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
											upload.setRecordStatus(uploadDetails.getRecordStatus());
											programmeLearningGoals.getUploadDocuments().add(upload);
											duplicateDocumentcheck.add(dbo.getId());
											//								}		
										}
									}
								});
							}
							programmeLearningGoals.setProgrammeLearningOutcomeList(new ArrayList<ProgrammeLearningOutcomeDTO>());
							programmeLearningGoalsMap.put(programmeLearningGoalsDBO.getId(), programmeLearningGoals);
							//							dto.getProgrammeLearningGoalsList().add(programmeLearningGoals);
						}
					});
				}
				if("ProgrammeLearningOutcome".equalsIgnoreCase(dbo.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeTypes().replaceAll("\\s", ""))) {
					dbo.getObeProgrammeOutcomeDetailsDBOSet().forEach(programmeLearningOutcomeDBO -> {
						if(programmeLearningGoalsMap.containsKey(programmeLearningOutcomeDBO.getObeOutcomeDetailParentId().getId())) {
							if(programmeLearningOutcomeDBO.getRecordStatus() == 'A') {
								ProgrammeSpecificOutcomeDTO programmeLearningGoals = programmeLearningGoalsMap.get(programmeLearningOutcomeDBO.getObeOutcomeDetailParentId().getId());
								ProgrammeLearningOutcomeDTO programmeLearningOutcome = new  ProgrammeLearningOutcomeDTO();
								programmeLearningOutcome.setId(programmeLearningOutcomeDBO.getId());
								programmeLearningOutcome.setReferenceNumber(programmeLearningOutcomeDBO.getReferenceNo());
								programmeLearningOutcome.setReferenceNoOrder(programmeLearningOutcomeDBO.getReferenceNoOrder());
								programmeLearningOutcome.setStatement(programmeLearningOutcomeDBO.getStatements());
								programmeLearningGoals.getProgrammeLearningOutcomeList().add(programmeLearningOutcome);
								programmeLearningGoals.getProgrammeLearningOutcomeList().sort(Comparator.comparing(s -> s.getReferenceNoOrder()));
							}
						}
					});
				}

				if("ProgramEducationalObjective".equalsIgnoreCase(dbo.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeTypes().replaceAll("\\s", ""))) {
					dto.setIsPeoDefined(true);
					dbo.getObeProgrammeOutcomeDetailsDBOSet().forEach(ProgramEducationalObjectiveDBO -> {
						if(ProgramEducationalObjectiveDBO.getRecordStatus() == 'A') {
							ProgrammeSpecificOutcomeDTO programEducationalObjective = new  ProgrammeSpecificOutcomeDTO();
							programEducationalObjective.setId(ProgramEducationalObjectiveDBO.getId());
							programEducationalObjective.setReferenceNumber(ProgramEducationalObjectiveDBO.getReferenceNo());
							programEducationalObjective.setStatement(ProgramEducationalObjectiveDBO.getStatements());
							programEducationalObjective.setReferenceNoOrder(ProgramEducationalObjectiveDBO.getReferenceNoOrder());
							programEducationalObjective.setApprovedBy(new SelectDTO());
							programEducationalObjective.getApprovedBy().setValue(String.valueOf(dbo.getErpApprovalLevelsDBO().getId()));
							programEducationalObjective.getApprovedBy().setLabel(dbo.getErpApprovalLevelsDBO().getApprover());
							programEducationalObjective.setComments(dbo.getComments());
							programEducationalObjective.setUploadDocuments(new ArrayList<EmpApplnAdvertisementImagesDTO>());
							if(!duplicateDocumentcheck.contains(dbo.getId())) {
								dbo.getObeProgrammeOutcomeUploadDetailsDBOSet().forEach( uploadDetails -> {
									if(uploadDetails.getRecordStatus() == 'A') {
										if(ProgramEducationalObjectiveDBO.getReferenceNoOrder().doubleValue() == 1.0){
											EmpApplnAdvertisementImagesDTO upload = new EmpApplnAdvertisementImagesDTO();
											upload.setId(String.valueOf(uploadDetails.getId()));
											File file = new File(uploadDetails.getDocumentUrl());
											//								if(file.exists() && !file.isDirectory()) { 
											upload.setExtension(uploadDetails.getDocumentUrl().substring(uploadDetails.getDocumentUrl().lastIndexOf(".")+1));
											upload.setUrl(uploadDetails.getDocumentUrl());
											String fileName = file.getName();
											upload.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
											upload.setRecordStatus(uploadDetails.getRecordStatus());
											programEducationalObjective.getUploadDocuments().add(upload);
											duplicateDocumentcheck.add(dbo.getId());
											//								}		
										}
									}
								});
							}

							programEducationalObjective.setProgrammeEducationalObjectivesMappingDTO(new ArrayList<ProgrammeEducationalObjectivesMappingDTO>());
							ProgramEducationalObjectiveDBO.getErpProgrammePeoMissionMatrixDBOSet().forEach( matrix -> {
								ProgrammeEducationalObjectivesMappingDTO mappingDto = new ProgrammeEducationalObjectivesMappingDTO();
								mappingDto.setId(matrix.getId());
								mappingDto.setDepartmentMissionVisionDetailsDTO(new DepartmentMissionVisionDetailsDTO());
								mappingDto.getDepartmentMissionVisionDetailsDTO().setId(String.valueOf(matrix.getErpDepartmentMissionVisionDetailsDBO().getId()));
								mappingDto.getDepartmentMissionVisionDetailsDTO().setMissionReferenceNumber(matrix.getErpDepartmentMissionVisionDetailsDBO().getMissionReferenceNumber());
								//								mappingDto.setMissionReferenceNumber(matrix.getErpDepartmentMissionVisionDetailsDBO().getMissionReferenceNumber());
								mappingDto.setIntrinsicValue(matrix.getIntrinsicValue());
								mappingDto.setRationaleForMapping(matrix.getRationaleForMapping());

								programEducationalObjective.getProgrammeEducationalObjectivesMappingDTO().add(mappingDto);
								programEducationalObjective.getProgrammeEducationalObjectivesMappingDTO().sort(Comparator.comparing(s -> s.getDepartmentMissionVisionDetailsDTO().getMissionReferenceNumber()));
							});
							dto.getProgramEducationalObjectiveList().add(programEducationalObjective);
						}
					});
				}
			});
			if(!Utils.isNullOrEmpty(programmeLearningGoalsMap)) {
				programmeLearningGoalsMap.forEach((key,value) -> {
					dto.getProgrammeLearningGoalsList().add(value);
				});
			}
			dto.getProgrammeOutcomeList().sort(Comparator.comparing(s -> s.getReferenceNoOrder()));
			dto.getProgrammeSpecificOutcomeList().sort(Comparator.comparing(s -> s.getReferenceNoOrder()));
			dto.getProgrammeLearningGoalsList().sort(Comparator.comparing(s -> s.getReferenceNoOrder()));
			dto.getProgramEducationalObjectiveList().sort(Comparator.comparing(s -> s.getReferenceNoOrder()));
			dtos.add(dto);
		}
		return  !Utils.isNullOrEmpty(dtos)?Mono.just(dtos.get(0)): Mono.error(new NotFoundException(null));
	}

	public Mono<ApiResult> saveOrUpdate(Mono<ProgrammeOutcomeDefinitionsDTO> dto, String userId) {
		return dto.handle((programmeOutcomeDefinitionsDTO, synchronousSink) -> {
			synchronousSink.next(programmeOutcomeDefinitionsDTO);
		}).cast(ProgrammeOutcomeDefinitionsDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap( s -> {
					programmeOutcomeDefinitionsTransaction.update(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<ObeProgrammeOutcomeDBO> convertDtoToDbo(ProgrammeOutcomeDefinitionsDTO dto, String userId) {
		List<ObeProgrammeOutcomeDBO> dbos = new ArrayList<ObeProgrammeOutcomeDBO>();
		List<ObeProgrammeOutcomeDBO> data = programmeOutcomeDefinitionsTransaction.edit(dto.getBatchwiseSettingId());
		Map<String, ObeProgrammeOutcomeDBO> existDataMap = data.stream().filter(s -> s.getRecordStatus() == 'A')
				.collect(Collectors.toMap(s -> s.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeTypes().replaceAll("\\s", ""), s -> s));
		Map<Integer, ObeProgrammeOutcomeDetailsDBO> childDetailsMap = new HashMap<Integer, ObeProgrammeOutcomeDetailsDBO>();
		data.forEach(dbo -> {
			dbo.getObeProgrammeOutcomeDetailsDBOSet().forEach(details -> {
				if(details.getRecordStatus() == 'A' && details.getObeOutcomeDetailParentId() != null) {
					childDetailsMap.put(details.getId(), details);
				}
			});
		});
		Map<Integer, ObeProgrammeOutcomeDetailsAttributeDBO> existAttribute = new HashMap<Integer, ObeProgrammeOutcomeDetailsAttributeDBO>();

		//ProgramOutcome
		if(!Utils.isNullOrEmpty(dto.getProgrammeOutcomeList())) {
			if(existDataMap.containsKey("ProgramOutcome")) {
				ObeProgrammeOutcomeDBO existProgramOutcome = existDataMap.get("ProgramOutcome");
				Map<Integer,ObeProgrammeOutcomeDetailsDBO> exmExistDBOMap = new HashMap<Integer, ObeProgrammeOutcomeDetailsDBO>();
				if(!Utils.isNullOrEmpty(existProgramOutcome.getObeProgrammeOutcomeDetailsDBOSet())) {
					existProgramOutcome.getObeProgrammeOutcomeDetailsDBOSet().forEach( details -> {
						if(details.getRecordStatus() == 'A') {
							exmExistDBOMap.put(details.getId(), details);
						}
					});
				}

				Set<ObeProgrammeOutcomeDetailsDBO> obeProgrammeOutcomeDetailsDBOSet =  new HashSet<ObeProgrammeOutcomeDetailsDBO>();
				dto.getProgrammeOutcomeList().forEach( programeOutcomeDto -> {
					existAttribute.clear();
					ObeProgrammeOutcomeDetailsDBO detailsDBO = null;
					if(exmExistDBOMap.containsKey(programeOutcomeDto.getId())) {
						detailsDBO = exmExistDBOMap.get(programeOutcomeDto.getId());
						detailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						exmExistDBOMap.remove(programeOutcomeDto.getId());
					} else {
						detailsDBO = new ObeProgrammeOutcomeDetailsDBO();
						detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					detailsDBO.setObeProgrammeOutcomeDBO(existProgramOutcome);
					detailsDBO.setReferenceNo(programeOutcomeDto.getReferenceNumber());
					detailsDBO.setStatements(programeOutcomeDto.getStatement());
					detailsDBO.setReferenceNoOrder(programeOutcomeDto.getReferenceNoOrder());
					detailsDBO.setRecordStatus('A');

					//Attributes
					Set<ObeProgrammeOutcomeDetailsAttributeDBO> obeProgrammeOutcomeDetailsAttributeDBOSet =  new HashSet<ObeProgrammeOutcomeDetailsAttributeDBO>();
					if(!Utils.isNullOrEmpty(detailsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
						detailsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach(attribute -> {
							if(attribute.getRecordStatus() == 'A') {
								existAttribute.put(attribute.getAcaGraduateAttributesDBO().getId(), attribute);
							}
						});
					}
					ObeProgrammeOutcomeDetailsDBO detailsDBO1 = detailsDBO;
					programeOutcomeDto.getGraduateAttributes().forEach(attributeDto -> {
						ObeProgrammeOutcomeDetailsAttributeDBO obeProgrammeOutcomeDetailsAttributeDBO = null;
						if(existAttribute.containsKey(Integer.parseInt(attributeDto.getValue()))) {
							obeProgrammeOutcomeDetailsAttributeDBO = existAttribute.get(Integer.parseInt(attributeDto.getValue()));
							obeProgrammeOutcomeDetailsAttributeDBO.setModifiedUsersId(Integer.parseInt(userId));
							existAttribute.remove(Integer.parseInt(attributeDto.getValue()));
						} else {
							obeProgrammeOutcomeDetailsAttributeDBO = new ObeProgrammeOutcomeDetailsAttributeDBO();
							obeProgrammeOutcomeDetailsAttributeDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						obeProgrammeOutcomeDetailsAttributeDBO.setObeProgrammeOutcomeDetailsDBO(detailsDBO1);
						obeProgrammeOutcomeDetailsAttributeDBO.setAcaGraduateAttributesDBO(new AcaGraduateAttributesDBO());
						obeProgrammeOutcomeDetailsAttributeDBO.getAcaGraduateAttributesDBO().setId(Integer.parseInt(attributeDto.getValue()));
						obeProgrammeOutcomeDetailsAttributeDBO.setRecordStatus('A');
						obeProgrammeOutcomeDetailsAttributeDBOSet.add(obeProgrammeOutcomeDetailsAttributeDBO);
					});
					if(!Utils.isNullOrEmpty(existAttribute)) {
						existAttribute.forEach((key,value)-> {
							value.setRecordStatus('D');
							value.setModifiedUsersId(Integer.parseInt(userId));
							obeProgrammeOutcomeDetailsAttributeDBOSet.add(value);
						});
					}
					detailsDBO.setObeProgrammeOutcomeDetailsAttributeDBOSet(obeProgrammeOutcomeDetailsAttributeDBOSet);

					obeProgrammeOutcomeDetailsDBOSet.add(detailsDBO);

					//uploadDetails
					if(!Utils.isNullOrEmpty(programeOutcomeDto.getUploadDocuments())) {
						this.upload(programeOutcomeDto,existProgramOutcome,userId);
					}
				});
				if(!Utils.isNullOrEmpty(exmExistDBOMap)) {
					exmExistDBOMap.forEach((Key,value) -> {
						value.setRecordStatus('D');
						value.setModifiedUsersId(Integer.parseInt(userId));
						if(!Utils.isNullOrEmpty(value.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
							value.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach( attribute -> {
								attribute.setRecordStatus('D');
								attribute.setModifiedUsersId(Integer.parseInt(userId));
							});
						}
						obeProgrammeOutcomeDetailsDBOSet.add(value);
					});
				}
				existProgramOutcome.setObeProgrammeOutcomeDetailsDBOSet(obeProgrammeOutcomeDetailsDBOSet);
				dbos.add(existProgramOutcome);
			}
		}


		//ProgramSpecificOutcome
		if(!Utils.isNullOrEmpty(dto.getProgrammeSpecificOutcomeList())) {
			if(existDataMap.containsKey("ProgramSpecificOutcome")) {
				ObeProgrammeOutcomeDBO existProgramSpecificOutcome = existDataMap.get("ProgramSpecificOutcome");
				Map<Integer,ObeProgrammeOutcomeDetailsDBO> exmExistDBOMap = new HashMap<Integer, ObeProgrammeOutcomeDetailsDBO>();
				if(!Utils.isNullOrEmpty(existProgramSpecificOutcome.getObeProgrammeOutcomeDetailsDBOSet())) {
					existProgramSpecificOutcome.getObeProgrammeOutcomeDetailsDBOSet().forEach( details -> {
						if(details.getRecordStatus() == 'A') {
							exmExistDBOMap.put(details.getId(), details);
						}
					});
				}

				Set<ObeProgrammeOutcomeDetailsDBO> obeProgrammeOutcomeDetailsDBOSet =  new HashSet<ObeProgrammeOutcomeDetailsDBO>();
				dto.getProgrammeSpecificOutcomeList().forEach( programmeSpecificOutcome -> {
					existAttribute.clear();
					ObeProgrammeOutcomeDetailsDBO detailsDBO = null;
					if(exmExistDBOMap.containsKey(programmeSpecificOutcome.getId())) {
						detailsDBO = exmExistDBOMap.get(programmeSpecificOutcome.getId());
						detailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						exmExistDBOMap.remove(programmeSpecificOutcome.getId());
					} else {
						detailsDBO = new ObeProgrammeOutcomeDetailsDBO();
						detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					detailsDBO.setObeProgrammeOutcomeDBO(existProgramSpecificOutcome);
					detailsDBO.setReferenceNo(programmeSpecificOutcome.getReferenceNumber());
					detailsDBO.setReferenceNoOrder(programmeSpecificOutcome.getReferenceNoOrder());
					detailsDBO.setStatements(programmeSpecificOutcome.getStatement());
					detailsDBO.setRecordStatus('A');

					//Attributes
					Set<ObeProgrammeOutcomeDetailsAttributeDBO> obeProgrammeOutcomeDetailsAttributeDBOSet =  new HashSet<ObeProgrammeOutcomeDetailsAttributeDBO>();
					if(!Utils.isNullOrEmpty(detailsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
						detailsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach(attribute -> {
							if(attribute.getRecordStatus() == 'A') {
								existAttribute.put(attribute.getAcaGraduateAttributesDBO().getId(), attribute);
							}
						});
					}
					ObeProgrammeOutcomeDetailsDBO detailsDBO1 = detailsDBO;
					programmeSpecificOutcome.getGraduateAttributes().forEach(attributeDto -> {
						ObeProgrammeOutcomeDetailsAttributeDBO obeProgrammeOutcomeDetailsAttributeDBO = null;
						if(existAttribute.containsKey(Integer.parseInt(attributeDto.getValue()))) {
							obeProgrammeOutcomeDetailsAttributeDBO = existAttribute.get(Integer.parseInt(attributeDto.getValue()));
							obeProgrammeOutcomeDetailsAttributeDBO.setModifiedUsersId(Integer.parseInt(userId));
							existAttribute.remove(Integer.parseInt(attributeDto.getValue()));
						} else {
							obeProgrammeOutcomeDetailsAttributeDBO = new ObeProgrammeOutcomeDetailsAttributeDBO();
							obeProgrammeOutcomeDetailsAttributeDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						obeProgrammeOutcomeDetailsAttributeDBO.setObeProgrammeOutcomeDetailsDBO(detailsDBO1);
						obeProgrammeOutcomeDetailsAttributeDBO.setAcaGraduateAttributesDBO(new AcaGraduateAttributesDBO());
						obeProgrammeOutcomeDetailsAttributeDBO.getAcaGraduateAttributesDBO().setId(Integer.parseInt(attributeDto.getValue()));
						obeProgrammeOutcomeDetailsAttributeDBO.setRecordStatus('A');
						obeProgrammeOutcomeDetailsAttributeDBOSet.add(obeProgrammeOutcomeDetailsAttributeDBO);
					});

					if(!Utils.isNullOrEmpty(existAttribute)) {
						existAttribute.forEach((key,value)-> {
							value.setRecordStatus('D');
							value.setModifiedUsersId(Integer.parseInt(userId));
							obeProgrammeOutcomeDetailsAttributeDBOSet.add(value);
						});
					}
					detailsDBO.setObeProgrammeOutcomeDetailsAttributeDBOSet(obeProgrammeOutcomeDetailsAttributeDBOSet);
					obeProgrammeOutcomeDetailsDBOSet.add(detailsDBO);

					//uploadDetails
					if(!Utils.isNullOrEmpty(programmeSpecificOutcome.getUploadDocuments())) {
						this.upload(programmeSpecificOutcome,existProgramSpecificOutcome,userId);
					}
				});
				if(!Utils.isNullOrEmpty(exmExistDBOMap)) {
					exmExistDBOMap.forEach((Key,value) -> {
						value.setRecordStatus('D');
						value.setModifiedUsersId(Integer.parseInt(userId));
						if(!Utils.isNullOrEmpty(value.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
							value.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach( attribute -> {
								attribute.setRecordStatus('D');
								attribute.setModifiedUsersId(Integer.parseInt(userId));
							});
						}
						obeProgrammeOutcomeDetailsDBOSet.add(value);
					});
				}
				existProgramSpecificOutcome.setObeProgrammeOutcomeDetailsDBOSet(obeProgrammeOutcomeDetailsDBOSet);
				dbos.add(existProgramSpecificOutcome);
			}
		}

		//ProgrammeLeaqrningGoal
		if(!Utils.isNullOrEmpty(dto.getProgrammeLearningGoalsList())) {
			if(existDataMap.containsKey("ProgrammeLearningGoal")) {
				ObeProgrammeOutcomeDBO existProgrammeLeaqrningGoal = existDataMap.get("ProgrammeLearningGoal");
				Map<Integer,ObeProgrammeOutcomeDetailsDBO> exmExistDBOMap = new HashMap<Integer, ObeProgrammeOutcomeDetailsDBO>();
				if(!Utils.isNullOrEmpty(existProgrammeLeaqrningGoal.getObeProgrammeOutcomeDetailsDBOSet())) {
					existProgrammeLeaqrningGoal.getObeProgrammeOutcomeDetailsDBOSet().forEach( details -> {
						if(details.getRecordStatus() == 'A') {
							exmExistDBOMap.put(details.getId(), details);
						}
					});
				}

				Set<ObeProgrammeOutcomeDetailsDBO> obeProgrammeOutcomeDetailsDBOSet =  new HashSet<ObeProgrammeOutcomeDetailsDBO>();
				dto.getProgrammeLearningGoalsList().forEach( programmeLearningGoals -> {
					existAttribute.clear();
					ObeProgrammeOutcomeDetailsDBO detailsDBO = null;
					if(exmExistDBOMap.containsKey(programmeLearningGoals.getId())) {
						detailsDBO = exmExistDBOMap.get(programmeLearningGoals.getId());
						detailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						exmExistDBOMap.remove(programmeLearningGoals.getId());
					} else {
						detailsDBO = new ObeProgrammeOutcomeDetailsDBO();
						detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					detailsDBO.setObeProgrammeOutcomeDBO(existProgrammeLeaqrningGoal);
					detailsDBO.setReferenceNo(programmeLearningGoals.getReferenceNumber());
					detailsDBO.setReferenceNoOrder(programmeLearningGoals.getReferenceNoOrder());
					detailsDBO.setStatements(programmeLearningGoals.getStatement());
					detailsDBO.setRecordStatus('A');

					//Attributes
					Set<ObeProgrammeOutcomeDetailsAttributeDBO> obeProgrammeOutcomeDetailsAttributeDBOSet =  new HashSet<ObeProgrammeOutcomeDetailsAttributeDBO>();
					if(!Utils.isNullOrEmpty(detailsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
						detailsDBO.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach(attribute -> {
							if(attribute.getRecordStatus() == 'A') {
								existAttribute.put(attribute.getAcaGraduateAttributesDBO().getId(), attribute);
							}
						});
					}
					ObeProgrammeOutcomeDetailsDBO detailsDBO1 = detailsDBO;
					programmeLearningGoals.getGraduateAttributes().forEach(attributeDto -> {
						ObeProgrammeOutcomeDetailsAttributeDBO obeProgrammeOutcomeDetailsAttributeDBO = null;
						if(existAttribute.containsKey(Integer.parseInt(attributeDto.getValue()))) {
							obeProgrammeOutcomeDetailsAttributeDBO = existAttribute.get(Integer.parseInt(attributeDto.getValue()));
							obeProgrammeOutcomeDetailsAttributeDBO.setModifiedUsersId(Integer.parseInt(userId));
							existAttribute.remove(Integer.parseInt(attributeDto.getValue()));
						} else {
							obeProgrammeOutcomeDetailsAttributeDBO = new ObeProgrammeOutcomeDetailsAttributeDBO();
							obeProgrammeOutcomeDetailsAttributeDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						obeProgrammeOutcomeDetailsAttributeDBO.setObeProgrammeOutcomeDetailsDBO(detailsDBO1);
						obeProgrammeOutcomeDetailsAttributeDBO.setAcaGraduateAttributesDBO(new AcaGraduateAttributesDBO());
						obeProgrammeOutcomeDetailsAttributeDBO.getAcaGraduateAttributesDBO().setId(Integer.parseInt(attributeDto.getValue()));
						obeProgrammeOutcomeDetailsAttributeDBO.setRecordStatus('A');
						obeProgrammeOutcomeDetailsAttributeDBOSet.add(obeProgrammeOutcomeDetailsAttributeDBO);
					});

					if(!Utils.isNullOrEmpty(existAttribute)) {
						existAttribute.forEach((key,value)-> {
							value.setRecordStatus('D');
							value.setModifiedUsersId(Integer.parseInt(userId));
							obeProgrammeOutcomeDetailsAttributeDBOSet.add(value);
						});
					}
					detailsDBO.setObeProgrammeOutcomeDetailsAttributeDBOSet(obeProgrammeOutcomeDetailsAttributeDBOSet);
					obeProgrammeOutcomeDetailsDBOSet.add(detailsDBO);

					//ProgrammeLeaqrningOutcome
					if(!Utils.isNullOrEmpty(programmeLearningGoals.getProgrammeLearningOutcomeList())) {
						if(existDataMap.containsKey("ProgrammeLearningOutcome")) {
							ObeProgrammeOutcomeDBO existProgrammeLearningOutcome = existDataMap.get("ProgrammeLearningOutcome");
							programmeLearningGoals.getProgrammeLearningOutcomeList().forEach(ProgrammeLearningOutcome -> {
								ObeProgrammeOutcomeDetailsDBO detailsDBO2 = null;
								if(childDetailsMap.containsKey(ProgrammeLearningOutcome.getId())) {
									detailsDBO2 = childDetailsMap.get(ProgrammeLearningOutcome.getId());
									detailsDBO2.setModifiedUsersId(Integer.parseInt(userId));
									childDetailsMap.remove(ProgrammeLearningOutcome.getId());
								} else {
									detailsDBO2 = new ObeProgrammeOutcomeDetailsDBO();
									detailsDBO2.setCreatedUsersId(Integer.parseInt(userId));
								}
								detailsDBO2.setObeProgrammeOutcomeDBO(existProgrammeLearningOutcome);
								detailsDBO2.setObeOutcomeDetailParentId(detailsDBO1);
								detailsDBO2.setReferenceNo(ProgrammeLearningOutcome.getReferenceNumber());
								detailsDBO2.setReferenceNoOrder(ProgrammeLearningOutcome.getReferenceNoOrder());
								detailsDBO2.setStatements(ProgrammeLearningOutcome.getStatement());
								detailsDBO2.setRecordStatus('A');
								obeProgrammeOutcomeDetailsDBOSet.add(detailsDBO2);
							});
						}
					}

					//uploadDetails
					if(!Utils.isNullOrEmpty(programmeLearningGoals.getUploadDocuments())) {
						this.upload(programmeLearningGoals,existProgrammeLeaqrningGoal,userId);
					}
				});
				if(!Utils.isNullOrEmpty(childDetailsMap)) {
					childDetailsMap.forEach((Key,value) -> {
						value.setRecordStatus('D');
						value.setModifiedUsersId(Integer.parseInt(userId));
						obeProgrammeOutcomeDetailsDBOSet.add(value);
					});
				}

				if(!Utils.isNullOrEmpty(exmExistDBOMap)) {
					exmExistDBOMap.forEach((Key,value) -> {
						value.setRecordStatus('D');
						value.setModifiedUsersId(Integer.parseInt(userId));
						if(!Utils.isNullOrEmpty(value.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
							value.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach( attribute -> {
								attribute.setRecordStatus('D');
								attribute.setModifiedUsersId(Integer.parseInt(userId));
							});
						}
						obeProgrammeOutcomeDetailsDBOSet.add(value);
					});
				}
				existProgrammeLeaqrningGoal.setObeProgrammeOutcomeDetailsDBOSet(obeProgrammeOutcomeDetailsDBOSet);
				dbos.add(existProgrammeLeaqrningGoal);
			}
		}

		//ProgramEducationalObjective
		if(!Utils.isNullOrEmpty(dto.getProgramEducationalObjectiveList())) {
			if(existDataMap.containsKey("ProgramEducationalObjective")) {
				ObeProgrammeOutcomeDBO existProgramEducationalObjective = existDataMap.get("ProgramEducationalObjective");
				Map<Integer,ObeProgrammeOutcomeDetailsDBO> exmExistDBOMap = new HashMap<Integer, ObeProgrammeOutcomeDetailsDBO>();
				Map<Integer,ErpProgrammePeoMissionMatrixDBO> missionMatrixDBOMap = new HashMap<Integer, ErpProgrammePeoMissionMatrixDBO>();

				if(!Utils.isNullOrEmpty(existProgramEducationalObjective.getObeProgrammeOutcomeDetailsDBOSet())) {
					existProgramEducationalObjective.getObeProgrammeOutcomeDetailsDBOSet().forEach( details -> {
						if(details.getRecordStatus() == 'A') {
							exmExistDBOMap.put(details.getId(), details);
							details.getErpProgrammePeoMissionMatrixDBOSet().forEach( mapping -> {
								if(mapping.getRecordStatus() == 'A') {
									missionMatrixDBOMap.put(mapping.getId(), mapping);
								}
							});
						}
					});
				}

				Set<ObeProgrammeOutcomeDetailsDBO> obeProgrammeOutcomeDetailsDBOSet =  new HashSet<ObeProgrammeOutcomeDetailsDBO>();
				dto.getProgramEducationalObjectiveList().forEach( programEducationalObjective -> {
					ObeProgrammeOutcomeDetailsDBO detailsDBO = null;
					if(exmExistDBOMap.containsKey(programEducationalObjective.getId())) {
						detailsDBO = exmExistDBOMap.get(programEducationalObjective.getId());
						detailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						exmExistDBOMap.remove(programEducationalObjective.getId());
					} else {
						detailsDBO = new ObeProgrammeOutcomeDetailsDBO();
						detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					detailsDBO.setObeProgrammeOutcomeDBO(existProgramEducationalObjective);
					detailsDBO.setReferenceNo(programEducationalObjective.getReferenceNumber());
					detailsDBO.setStatements(programEducationalObjective.getStatement());
					detailsDBO.setRecordStatus('A');
					detailsDBO.setErpProgrammePeoMissionMatrixDBOSet(new HashSet<ErpProgrammePeoMissionMatrixDBO>());

					if(!Utils.isNullOrEmpty(programEducationalObjective.getProgrammeEducationalObjectivesMappingDTO())) {
						ObeProgrammeOutcomeDetailsDBO detailsDBO1 = detailsDBO;
						programEducationalObjective.getProgrammeEducationalObjectivesMappingDTO().forEach( matrix -> {
							ErpProgrammePeoMissionMatrixDBO erpProgrammePeoMissionMatrixDBO = null;
							if(missionMatrixDBOMap.containsKey(matrix.getId())) {
								erpProgrammePeoMissionMatrixDBO =  missionMatrixDBOMap.get(matrix.getId());
								erpProgrammePeoMissionMatrixDBO.setModifiedUsersId(Integer.parseInt(userId));
								missionMatrixDBOMap.remove(matrix.getId());
							} else {
								erpProgrammePeoMissionMatrixDBO = new ErpProgrammePeoMissionMatrixDBO();
								erpProgrammePeoMissionMatrixDBO.setCreatedUsersId(Integer.parseInt(userId));
							}
							erpProgrammePeoMissionMatrixDBO.setErpDepartmentMissionVisionDetailsDBO(new ErpDepartmentMissionVisionDetailsDBO());
							erpProgrammePeoMissionMatrixDBO.setObeProgrammeOutcomeDetailsDBO(detailsDBO1);
							erpProgrammePeoMissionMatrixDBO.getErpDepartmentMissionVisionDetailsDBO().setId(Integer.parseInt(matrix.getDepartmentMissionVisionDetailsDTO().getId()));
							erpProgrammePeoMissionMatrixDBO.setIntrinsicValue(matrix.getIntrinsicValue());
							erpProgrammePeoMissionMatrixDBO.setRationaleForMapping(matrix.getRationaleForMapping());
							erpProgrammePeoMissionMatrixDBO.setRecordStatus('A');
							detailsDBO1.getErpProgrammePeoMissionMatrixDBOSet().add(erpProgrammePeoMissionMatrixDBO);
						});
					}
					obeProgrammeOutcomeDetailsDBOSet.add(detailsDBO);
				});
				existProgramEducationalObjective.setObeProgrammeOutcomeDetailsDBOSet(obeProgrammeOutcomeDetailsDBOSet);
				dbos.add(existProgramEducationalObjective);
			}
		}
		return dbos;
	}

	//uploadDetails
	public ObeProgrammeOutcomeDBO upload(ProgrammeSpecificOutcomeDTO programmeSpecificOutcome, ObeProgrammeOutcomeDBO existProgramSpecificOutcome, String userId) {
		if(programmeSpecificOutcome.getReferenceNoOrder().doubleValue() == 1.0) {
			Map<Integer,ObeProgrammeOutcomeUploadDetailsDBO> uploadExistDBOMap = new HashMap<Integer, ObeProgrammeOutcomeUploadDetailsDBO>();
			if(!Utils.isNullOrEmpty(existProgramSpecificOutcome.getObeProgrammeOutcomeUploadDetailsDBOSet())) {
				existProgramSpecificOutcome.getObeProgrammeOutcomeUploadDetailsDBOSet().forEach( uploadDetails -> {
					if(uploadDetails.getRecordStatus() == 'A') {
						uploadExistDBOMap.put(uploadDetails.getId(), uploadDetails);
					}
				});
			}

			programmeSpecificOutcome.getUploadDocuments().forEach( document -> {
				ObeProgrammeOutcomeUploadDetailsDBO obeProgrammeOutcomeUploadDetailsDBO = null;
				File file = new File("programmeOutcomeDetailsDocuments//"+document.getFileName()+"."+document.getExtension());
				if(!Utils.isNullOrEmpty(uploadExistDBOMap)) {
					if(Utils.isNullOrEmpty(document.getId()) && !uploadExistDBOMap.containsKey(Integer.parseInt(document.getId()))) {
						obeProgrammeOutcomeUploadDetailsDBO = new ObeProgrammeOutcomeUploadDetailsDBO();
						obeProgrammeOutcomeUploadDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					} 	else {
						obeProgrammeOutcomeUploadDetailsDBO = uploadExistDBOMap.get(Integer.parseInt(document.getId()));
						obeProgrammeOutcomeUploadDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						uploadExistDBOMap.remove(Integer.parseInt(document.getId()));
					}
				} else {
					obeProgrammeOutcomeUploadDetailsDBO = new ObeProgrammeOutcomeUploadDetailsDBO();
					obeProgrammeOutcomeUploadDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				obeProgrammeOutcomeUploadDetailsDBO.setObeProgrammeOutcomeDBO(existProgramSpecificOutcome);
				obeProgrammeOutcomeUploadDetailsDBO.setDocumentUrl(file.getAbsolutePath());
				obeProgrammeOutcomeUploadDetailsDBO.setRecordStatus('A');
				existProgramSpecificOutcome.getObeProgrammeOutcomeUploadDetailsDBOSet().add(obeProgrammeOutcomeUploadDetailsDBO);
			});
			if(!Utils.isNullOrEmpty(uploadExistDBOMap)) {
				uploadExistDBOMap.forEach((Key,value) -> {
					value.setRecordStatus('D');
					value.setModifiedUsersId(Integer.parseInt(userId));
					File file = new File(value.getDocumentUrl());
					if(file.exists()) {
						file.delete();
					}
					existProgramSpecificOutcome.getObeProgrammeOutcomeUploadDetailsDBOSet().add(value);
				});
			}
		}
		return existProgramSpecificOutcome;
	}

	public Flux<SelectDTO> getProgrammeListToImport(int departmentId, int fromYearId) {
		return  programmeOutcomeDefinitionsTransaction.getProgrammeListToImport(departmentId,fromYearId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(Tuple dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(dbo.get("id").toString());
		dto.setLabel(dbo.get("programmeName").toString());
		return dto;
	}

	public Mono<ApiResult> saveImportProgrammeDefinitions(Mono<ProgrammeOutcomeDefinitionsDTO> data, String userId) {
		List<Integer> programmesIds = new ArrayList<Integer>();
		return data.handle((programmeOutcomeDefinitionsDTO, synchronousSink) ->  {
			List<String> duplicateProgrammes = new ArrayList<String>();
			Set<String> dataExists = new HashSet<String>();
			if(!Utils.isNullOrEmpty(programmeOutcomeDefinitionsDTO.getProgrammeList())) {
				programmeOutcomeDefinitionsDTO.getProgrammeList().forEach( programme -> {
					programmesIds.add(Integer.parseInt(programme.getValue()));
				});
			}

			List<ErpProgrammeBatchwiseSettingsDBO> batchwiseSettingsDetails = programmeOutcomeDefinitionsTransaction.getBatchwiseSettingsDetails(programmesIds,Integer.parseInt(programmeOutcomeDefinitionsDTO.getToYear().getValue()));
			Map<Integer, ErpProgrammeBatchwiseSettingsDBO> batchwiseSettingsCheckMap = batchwiseSettingsDetails.stream().collect(Collectors.toMap(s -> s.getErpProgrammeDBO().getId(), s -> s));
			if(!Utils.isNullOrEmpty(programmeOutcomeDefinitionsDTO.getProgrammeList())) {
				programmeOutcomeDefinitionsDTO.getProgrammeList().forEach( progId -> {
					if(!batchwiseSettingsCheckMap.containsKey(Integer.parseInt(progId.getValue()))) {
						duplicateProgrammes.add(progId.getLabel());
					}
				});
			}

			List<Tuple> duplicateCheckForDataExist = programmeOutcomeDefinitionsTransaction.duplicateCheck(programmesIds,Integer.parseInt(programmeOutcomeDefinitionsDTO.getToYear().getValue()));
			if(!Utils.isNullOrEmpty(duplicateCheckForDataExist)) {
				duplicateCheckForDataExist.forEach( value -> {
					dataExists.add(value.get("programmeName").toString());
				});
			}

			if(!Utils.isNullOrEmpty(duplicateProgrammes)) {
				synchronousSink.error(new DuplicateException("ProgrammeBatchwiseSettings is not Defined for  below selected programmes "+duplicateProgrammes));
			} else if(!Utils.isNullOrEmpty(dataExists)) {
				synchronousSink.error(new DuplicateException("ProgrammeOutCome Details Already Defined for below selected programmes "+dataExists));
			} else {
				synchronousSink.next(programmeOutcomeDefinitionsDTO);
			}
		}).cast(ProgrammeOutcomeDefinitionsDTO.class)
				.map(data1 -> convertDboToDbo(data1,programmesIds,userId))
				.flatMap( s -> {
					programmeOutcomeDefinitionsTransaction.merge(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<ObeProgrammeOutcomeDBO> convertDboToDbo(ProgrammeOutcomeDefinitionsDTO dto, List<Integer> programmesIds, String userId) {
		List<ObeProgrammeOutcomeDBO> dboList = new ArrayList<ObeProgrammeOutcomeDBO>();
		Map<Integer, ErpProgrammeBatchwiseSettingsDBO> fromYearDatas = programmeOutcomeDefinitionsTransaction.getPerviousData(programmesIds,Integer.parseInt(dto.getFromYear().getValue())).stream()
				.collect(Collectors.toMap(s -> s.getErpProgrammeDBO().getId(), s -> s));

		List<ErpProgrammeBatchwiseSettingsDBO> toYearDatas = programmeOutcomeDefinitionsTransaction.getExistData(programmesIds,Integer.parseInt(dto.getToYear().getValue()));

		toYearDatas.forEach(data -> {
			if(fromYearDatas.containsKey(data.getErpProgrammeDBO().getId())) {
				ErpProgrammeBatchwiseSettingsDBO previousData = fromYearDatas.get(data.getErpProgrammeDBO().getId());
				Map<Integer, ObeProgrammeOutcomeDBO> previousProgrammeOutcomeData = previousData.getObeProgrammeOutcomeDBOSet().stream().filter(s -> s.getRecordStatus() == 'A')
						.collect(Collectors.toMap(s -> s.getObeProgrammeOutcomeTypesDBO().getId(), s -> s));

				//collecting 'PLO' details 
				Map<Integer,List<ObeProgrammeOutcomeDetailsDBO>> previousChildProgrammeOutcomeDetailsMap = new HashMap<Integer, List<ObeProgrammeOutcomeDetailsDBO>>();
				previousData.getObeProgrammeOutcomeDBOSet().forEach( outcomeType -> {
					if(outcomeType.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeCode().equals("PLO") && !Utils.isNullOrEmpty(outcomeType.getObeProgrammeOutcomeDetailsDBOSet()) 
							&& outcomeType.getRecordStatus() == 'A') {
						outcomeType.getObeProgrammeOutcomeDetailsDBOSet().forEach( childDetails -> {
							if(!previousChildProgrammeOutcomeDetailsMap.containsKey(childDetails.getObeOutcomeDetailParentId().getId())) {
								List<ObeProgrammeOutcomeDetailsDBO> listData = new ArrayList<ObeProgrammeOutcomeDetailsDBO>();
								listData.add(childDetails);
								previousChildProgrammeOutcomeDetailsMap.put(childDetails.getObeOutcomeDetailParentId().getId(), listData);
							} else {
								List<ObeProgrammeOutcomeDetailsDBO> listData = previousChildProgrammeOutcomeDetailsMap.get(childDetails.getObeOutcomeDetailParentId().getId());
								listData.add(childDetails);
								previousChildProgrammeOutcomeDetailsMap.replace(childDetails.getObeOutcomeDetailParentId().getId(), listData);
							}
						});
					}
				});

				//collecting outcomeType of ToYear data
				Map<String,ObeProgrammeOutcomeDBO> outcomeDetailsNewMap = new HashMap<String,ObeProgrammeOutcomeDBO>();				
				data.getObeProgrammeOutcomeDBOSet().forEach( value -> {
					if(value.getRecordStatus() == 'A') {
						outcomeDetailsNewMap.put(value.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeCode(),value);
					}
				});

				data.getObeProgrammeOutcomeDBOSet().forEach( programmeOutcomeNew -> {
					if(programmeOutcomeNew.getRecordStatus() == 'A' && !(programmeOutcomeNew.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeCode().equalsIgnoreCase("PLO"))) {
						if(previousProgrammeOutcomeData.containsKey(programmeOutcomeNew.getObeProgrammeOutcomeTypesDBO().getId())) {
							ObeProgrammeOutcomeDBO previousValues = previousProgrammeOutcomeData.get(programmeOutcomeNew.getObeProgrammeOutcomeTypesDBO().getId());

							//copying Approver and comments 
							programmeOutcomeNew.setErpApprovalLevelsDBO(previousValues.getErpApprovalLevelsDBO());
							programmeOutcomeNew.setComments(previousValues.getComments());
							programmeOutcomeNew.setModifiedUsersId(Integer.parseInt(userId));

							//copying programmeOutcomeDetails
							Set<ObeProgrammeOutcomeDetailsDBO> obeProgrammeOutcomeDetailsDBOSet = new HashSet<ObeProgrammeOutcomeDetailsDBO>();
							previousValues.getObeProgrammeOutcomeDetailsDBOSet().forEach( previousProgrammeOutcomeDetails -> {
								if(previousProgrammeOutcomeDetails.getRecordStatus() == 'A') {
									ObeProgrammeOutcomeDetailsDBO detailsDBO = new ObeProgrammeOutcomeDetailsDBO();
									BeanUtils.copyProperties(previousProgrammeOutcomeDetails, detailsDBO,"id","obeProgrammeOutcomeDBO");
									detailsDBO.setObeProgrammeOutcomeDBO(programmeOutcomeNew);
									detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
									detailsDBO.setModifiedUsersId(null);

									//copying programmeOutcomeDetails Attribute details
									Set<ObeProgrammeOutcomeDetailsAttributeDBO> obeProgrammeOutcomeDetailsAttributeDBOSet = new HashSet<ObeProgrammeOutcomeDetailsAttributeDBO>();
									if(!Utils.isNullOrEmpty(previousProgrammeOutcomeDetails.getObeProgrammeOutcomeDetailsAttributeDBOSet())) {
										previousProgrammeOutcomeDetails.getObeProgrammeOutcomeDetailsAttributeDBOSet().forEach( attibuteOldData ->{
											if(attibuteOldData.getRecordStatus() == 'A') {
												ObeProgrammeOutcomeDetailsAttributeDBO attributeNewData = new ObeProgrammeOutcomeDetailsAttributeDBO();
												BeanUtils.copyProperties(attibuteOldData, attributeNewData,"id","obeProgrammeOutcomeDetailsDBO");
												attributeNewData.setObeProgrammeOutcomeDetailsDBO(detailsDBO);
												attributeNewData.setCreatedUsersId(Integer.parseInt(userId));
												attributeNewData.setModifiedUsersId(null);
												obeProgrammeOutcomeDetailsAttributeDBOSet.add(attributeNewData);
											}
										});
									}

									//copying programmeOutcomeDetails MissionMatrix details
									Set<ErpProgrammePeoMissionMatrixDBO> ErpProgrammePeoMissionMatrixDBOSet = new HashSet<ErpProgrammePeoMissionMatrixDBO>();
									if(!Utils.isNullOrEmpty(previousProgrammeOutcomeDetails.getErpProgrammePeoMissionMatrixDBOSet())) {
										previousProgrammeOutcomeDetails.getErpProgrammePeoMissionMatrixDBOSet().forEach( oldMatrixDetails -> {
											if(oldMatrixDetails.getRecordStatus() == 'A') {
												ErpProgrammePeoMissionMatrixDBO matrixNew = new  ErpProgrammePeoMissionMatrixDBO();
												BeanUtils.copyProperties(oldMatrixDetails, matrixNew,"id","obeProgrammeOutcomeDetailsDBO");
												matrixNew.setObeProgrammeOutcomeDetailsDBO(detailsDBO);
												matrixNew.setCreatedUsersId(Integer.parseInt(userId));
												matrixNew.setModifiedUsersId(null);
												ErpProgrammePeoMissionMatrixDBOSet.add(matrixNew);
											}
										});
									}
									detailsDBO.setErpProgrammePeoMissionMatrixDBOSet(ErpProgrammePeoMissionMatrixDBOSet);
									detailsDBO.setObeProgrammeOutcomeDetailsAttributeDBOSet(obeProgrammeOutcomeDetailsAttributeDBOSet);
									obeProgrammeOutcomeDetailsDBOSet.add(detailsDBO);

									// copying 'PLO' Because 'PLG' is a parent of 'PLO'
									if(programmeOutcomeNew.getObeProgrammeOutcomeTypesDBO().getObeProgrammeOutcomeCode().equals("PLG")) {
										if(outcomeDetailsNewMap.containsKey("PLO")) {
											if(previousChildProgrammeOutcomeDetailsMap.containsKey(previousProgrammeOutcomeDetails.getId())) {
												List<ObeProgrammeOutcomeDetailsDBO> list = previousChildProgrammeOutcomeDetailsMap.get(previousProgrammeOutcomeDetails.getId());
												list.forEach( childData -> {
													ObeProgrammeOutcomeDetailsDBO detailsDBO1 = new ObeProgrammeOutcomeDetailsDBO();
													BeanUtils.copyProperties(childData, detailsDBO1,"id","obeProgrammeOutcomeDBO");
													detailsDBO1.setObeProgrammeOutcomeDBO(outcomeDetailsNewMap.get("PLO"));
													detailsDBO1.setObeOutcomeDetailParentId(detailsDBO);
													detailsDBO1.setCreatedUsersId(Integer.parseInt(userId));
													detailsDBO1.setModifiedUsersId(null);
													obeProgrammeOutcomeDetailsDBOSet.add(detailsDBO1);
												});
											}
										}
									}
								}
							});

							//copying ProgrammeOutcomeUploadDetail
							Set<ObeProgrammeOutcomeUploadDetailsDBO> obeProgrammeOutcomeUploadDetailsDBOSet = new HashSet<ObeProgrammeOutcomeUploadDetailsDBO>();
							if(!Utils.isNullOrEmpty(previousValues.getObeProgrammeOutcomeUploadDetailsDBOSet())){
								previousValues.getObeProgrammeOutcomeUploadDetailsDBOSet().forEach( previousProgrammeOutcomeUploadDetails -> {
									if(previousProgrammeOutcomeUploadDetails.getRecordStatus() == 'A') {
										ObeProgrammeOutcomeUploadDetailsDBO uploadDetails = new ObeProgrammeOutcomeUploadDetailsDBO();
										BeanUtils.copyProperties(previousProgrammeOutcomeUploadDetails, uploadDetails,"id","obeProgrammeOutcomeDBO");
										uploadDetails.setObeProgrammeOutcomeDBO(programmeOutcomeNew);
										uploadDetails.setCreatedUsersId(Integer.parseInt(userId));
										uploadDetails.setModifiedUsersId(null);
										obeProgrammeOutcomeUploadDetailsDBOSet.add(uploadDetails);
									}
								});
							}
							programmeOutcomeNew.setObeProgrammeOutcomeDetailsDBOSet(obeProgrammeOutcomeDetailsDBOSet);
							programmeOutcomeNew.setObeProgrammeOutcomeUploadDetailsDBOSet(obeProgrammeOutcomeUploadDetailsDBOSet);
							dboList.add(programmeOutcomeNew);
						}
					}
				});
			}
		});
		return dboList;
	}

}
