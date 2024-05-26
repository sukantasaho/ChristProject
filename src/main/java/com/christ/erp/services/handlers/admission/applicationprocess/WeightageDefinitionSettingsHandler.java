package com.christ.erp.services.handlers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionGeneralDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionLocationCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmPrerequisiteExamDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailAddSlotDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDetailDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionGeneralDTO;
import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDetailsDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.weightageGeneralDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.helpers.admission.CommonAdmissionHelper;
import com.christ.erp.services.helpers.admission.applicationprocess.WeightageDefinitionSettingsHelper;
import com.christ.erp.services.transactions.admission.CommonAdmissionTransaction;
import com.christ.erp.services.transactions.admission.applicationprocess.WeightageDefinitionSettingsTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("rawtypes")
public class WeightageDefinitionSettingsHandler {

	@Autowired
	private WeightageDefinitionSettingsTransaction weightageDefinitionSettingsTransaction;
	
	@Autowired
	private CommonAdmissionTransaction commonAdmissionTransaction;
	
	@Autowired
	private WeightageDefinitionSettingsHelper weightageDefinitionSettingsHelper;

	@Autowired
	private CommonAdmissionHelper commonAdmissionHelper;

	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public Flux<AdmWeightageDefinitionDTO> getGridData() {
	      return weightageDefinitionSettingsTransaction.getGridData()
	    		  .flatMapMany(Flux::fromIterable)
	    		  .map(this::convertGridDboToDto);
	}

	public Mono<AdmWeightageDefinitionDTO> edit(int id) {	
		return weightageDefinitionSettingsTransaction.edit(id)
				.map(dbo->commonFetchEdit.apply(dbo, id)).flatMap(item-> item);
	}

	private final BiFunction<AdmWeightageDefinitionDBO, Integer, Mono<AdmWeightageDefinitionDTO>> commonFetchEdit = (dbo, id) ->{
		List<Integer> campusMappingIds = dbo.getAdmWeightageDefinitionLocationCampusDBOsSet().stream().map(item->item.getErpCampusProgrammeMappingDBO().getId()).collect(Collectors.toList());
		List<Integer> admWDdetailPre = new ArrayList<>();
		List<Integer> admWDdetailQul = new ArrayList<>();
		Mono<AdmWeightageDefinitionDTO> mono = Mono.just(convertDboToDto(dbo));
		Mono<AdmWeightageDefinitionDTO> monoPrerequisiteExam = mono.zipWhen(admdef ->
			commonAdmissionTransaction.getAdmPrerequisiteExam(Integer.parseInt(admdef.getErpAcademicYearDTO().getValue()),Integer.parseInt(admdef.getErpProgrammeDTO().getValue()))
			.flatMapMany(Flux::fromIterable).map(item->item).collect(Collectors.toList())).map(tuple2->{
				AdmWeightageDefinitionDTO admDef = tuple2.getT1();
				Set<AdmPrerequisiteExamDTO> set = new HashSet<>();
				admDef.getAdmWeightageDefinitionDetailDTOSet().forEach(item->{
					item.getAdmPrerequisiteExamDTOList().forEach(itemP->admWDdetailPre.add(itemP.getId()));
				});
				if(!Utils.isNullOrEmpty(tuple2.getT2())){
					tuple2.getT2().forEach(itemQ->{
						if(!admWDdetailPre.contains(Integer.parseInt(itemQ[0].toString()))) {
							AdmPrerequisiteExamDTO examDTO = new AdmPrerequisiteExamDTO();
							examDTO.setId(Integer.parseInt(itemQ[0].toString()));
							examDTO.setExamName(itemQ[1].toString());
							set.add(examDTO);
						}
					});
				}
				if(!Utils.isNullOrEmpty(set)) {
					admDef.getAdmWeightageDefinitionDetailDTOSet().forEach(item->{
						item.getAdmPrerequisiteExamDTOList().addAll(set);
						item.getAdmPrerequisiteExamDTOList().sort(Comparator.comparing(AdmPrerequisiteExamDTO::getExamName));
					});
				}
			return admDef;
		}).cast(AdmWeightageDefinitionDTO.class);

		Mono<AdmWeightageDefinitionDTO> monoQualification = monoPrerequisiteExam.zipWhen(admdef-> commonAdmissionTransaction.getAdmQualificationList(Integer.parseInt(admdef.getErpProgrammeDTO().getValue()))
				.flatMapMany(Flux::fromIterable).map(item->item).collect(Collectors.toList())).map(tuple2->{
			AdmWeightageDefinitionDTO admDef = tuple2.getT1();
			admDef.getAdmWeightageDefinitionDetailDTOSet().forEach(item->{
				item.getAdmQualificationListDTOList().forEach(itemP->admWDdetailQul.add(itemP.getId()));
			});
			Set<AdmQualificationListDTO> set = new HashSet<>();
			if(!Utils.isNullOrEmpty(tuple2.getT2())){
				tuple2.getT2().forEach(itemNew->{
					if(!admWDdetailQul.contains(Integer.parseInt(itemNew[0].toString()))) {
						AdmQualificationListDTO dto = new AdmQualificationListDTO();
						dto.setId(Integer.parseInt(itemNew[0].toString()));
						dto.setQualificationName(itemNew[1].toString());
						dto.setQualificationOrder(Integer.parseInt( itemNew[2].toString()));
						set.add(dto);
					}
				});
			}
			if(!Utils.isNullOrEmpty(set)) {
				admDef.getAdmWeightageDefinitionDetailDTOSet().forEach(item->{
					item.getAdmQualificationListDTOList().addAll(set);
					item.getAdmQualificationListDTOList().sort(Comparator.comparing(AdmQualificationListDTO::getQualificationOrder));
				});
			}
			return admDef;
		}).cast(AdmWeightageDefinitionDTO.class);

		return monoQualification.zipWhen(admdef-> commonAdmissionTransaction.getAdmSelectionProcessPlanDetail(Integer.parseInt(admdef.getErpAcademicYearDTO().getValue()), campusMappingIds)
	   .flatMapMany(Flux::fromIterable)
	   .map(commonAdmissionHelper::convertAdmSelectionProcessPlanDBOtoDTO).collect(Collectors.toList())).map(tuple2->{
			AdmWeightageDefinitionDTO ad = tuple2.getT1();
			List<AdmSelectionProcessPlanDetailDTO> set = new ArrayList<>();
			Map<Integer,AdmSelectionProcessPlanDetailDTO> sessionIdMap = new HashMap<>();
			Map<Integer, AdmSelectionProcessPlanDetailAddSlotDTO> slotDetailOld = new HashMap<>();
			Map<Integer, AdmSelectionProcessPlanDetailAddSlotDTO> slotDetailNew = new HashMap<>();
			List<AdmSelectionProcessPlanDetailAddSlotDTO> addSlotList = new ArrayList<>();
			Set<Integer> sessionIdList = new HashSet<>();
			ad.getAdmWeightageDefinitionDetailDTOSet().forEach(itemOld->{
				if(!Utils.isNullOrEmpty(itemOld.getAdmSelectionProcessPlanDetailDTOList())) {
					itemOld.getAdmSelectionProcessPlanDetailDTOList().forEach(itemSP->{
						sessionIdMap.put(itemSP.getSessionId(), itemSP);
					});
				}
				if(!Utils.isNullOrEmpty(tuple2.getT2())){
					tuple2.getT2().forEach(item->{
						if(!Utils.isNullOrEmpty(item.getSessionId())) {
							if(!sessionIdMap.containsKey(item.getSessionId()) && !sessionIdList.contains(item.getSessionId())){
								sessionIdList.add(item.getSessionId());
								set.add(item);
							}else if(sessionIdMap.containsKey(item.getSessionId()) && !sessionIdList.contains(item.getSessionId())) {
								AdmSelectionProcessPlanDetailDTO oldObj = sessionIdMap.get(item.getSessionId());
								if(!Utils.isNullOrEmpty(oldObj) && !Utils.isNullOrEmpty(oldObj.getSlotslist()) && !Utils.isNullOrEmpty(item.getSlotslist())) {
									slotDetailOld.clear();
									slotDetailNew.clear();
									addSlotList.clear();
									AdmSelectionProcessPlanDetailDTO addSlot = new AdmSelectionProcessPlanDetailDTO();
									item.getSlotslist().forEach(itemSl->slotDetailNew.put(itemSl.getDetailId(), itemSl));
									oldObj.getSlotslist().forEach(itemSlNew->slotDetailOld.put(itemSlNew.getDetailId(), itemSlNew));
									slotDetailNew.entrySet().forEach(itemNewSlot->{
										if(slotDetailOld.containsKey(itemNewSlot.getKey())){
											addSlotList.add(slotDetailOld.get(itemNewSlot.getKey()));
										}
										else {
											addSlotList.add(itemNewSlot.getValue());
										}
									});
									sessionIdList.add(item.getSessionId());
									addSlot.setSlotslist(addSlotList);
									addSlot.setSessionId(item.getSessionId());
									addSlot.setSessionName(item.getSessionName());
									set.add(addSlot);
								}
							}
						}
					});
				}
			});
			ad.getAdmWeightageDefinitionDetailDTOSet().forEach(item->{
				item.setAdmSelectionProcessPlanDetailDTOList(set);
				item.getAdmSelectionProcessPlanDetailDTOList().sort(Comparator.comparing(AdmSelectionProcessPlanDetailDTO::getSessionName));
			});
			return ad;
		}).cast(AdmWeightageDefinitionDTO.class);
	};

	public Mono<ApiResult> delete(int id, String userId) {
		return weightageDefinitionSettingsTransaction.delete(id, userId).map(Utils::responseResult);
	}

	public Mono<ApiResult> saveOrUpdate(Mono<AdmWeightageDefinitionDTO> dto, String userId) {
		Set<Integer> admWeightageDefinitionDetailDBOIds = new HashSet<>();
		Set<Integer> admWeightageDefinitionGeneralDBOIds = new HashSet<>();
		Mono<AdmWeightageDefinitionDTO> mono = dto.handle((admWeightageDefinitionDTO, synchronousSink) -> {
			boolean istrue = weightageDefinitionSettingsTransaction.duplicateCheck(admWeightageDefinitionDTO);
			if (istrue) {
				synchronousSink.error(new DuplicateException("‘Weightage is already defined for this programme.Please choose edit to make any changes in defined weightage’"));
			} else {
				synchronousSink.next(admWeightageDefinitionDTO);
			}
		}).cast(AdmWeightageDefinitionDTO.class);
		return mono.map(data -> Mono.just(convertAdmWeightageDefinitionDTOToDBO(data,new AdmWeightageDefinitionDBO(), userId, admWeightageDefinitionDetailDBOIds, admWeightageDefinitionGeneralDBOIds)))
			.flatMap(t -> t.flatMap(admWeightageDefinitionDBO -> {
				if(!Utils.isNullOrEmpty(admWeightageDefinitionDBO.getId())) {
					weightageDefinitionSettingsTransaction.update(admWeightageDefinitionDBO, admWeightageDefinitionDetailDBOIds, admWeightageDefinitionGeneralDBOIds, userId);
				} else
					weightageDefinitionSettingsTransaction.save(admWeightageDefinitionDBO);
				return Mono.just(Boolean.TRUE);
			})).map(Utils::responseResult);
//			convertFunc.apply(data, userId, admWeightageDefinitionDetailDBOIds, admWeightageDefinitionGeneralDBOIds))
//			.flatMap(t -> t.flatMap(item->{
//				if(!Utils.isNullOrEmpty(item.getId()))
//					weightageDefinitionSettingsTransaction.update(item);
//				else
//					weightageDefinitionSettingsTransaction.save(item);
//			return Mono.just(Boolean.TRUE);
//		})).map(Utils::responseResult);
	}

	//public BiFunction<AdmWeightageDefinitionDTO,String,Mono<AdmWeightageDefinitionDBO>> convertFunc = (dto,userId) -> Mono.just(convertAdmWeightageDefinitionDTOToDBO(dto,new AdmWeightageDefinitionDBO(), userId));

	public AdmWeightageDefinitionDBO convertAdmWeightageDefinitionDTOToDBO(AdmWeightageDefinitionDTO dto, AdmWeightageDefinitionDBO dbo, String userId,
		Set<Integer> admWeightageDefinitionDetailDBOIds, Set<Integer> admWeightageDefinitionGeneralDBOIds) {
		if (!Utils.isNullOrEmpty(dto.getId()))
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		else
			dbo.setCreatedUsersId(Integer.parseInt(userId));
		BeanUtils.copyProperties(dto, dbo);
		if(!Utils.isNullOrEmpty(dto.getErpAcademicYearDTO()) && !Utils.isNullOrEmpty(dto.getErpAcademicYearDTO().getValue())) {
			ErpAcademicYearDBO erpAcademicYearDbo = new ErpAcademicYearDBO();
			erpAcademicYearDbo.setId(Integer.parseInt(dto.getErpAcademicYearDTO().getValue()));
			dbo.setErpAcademicYearDBO(erpAcademicYearDbo);
		}
		if(!Utils.isNullOrEmpty(dto.getErpProgrammeDTO()) && !Utils.isNullOrEmpty(dto.getErpProgrammeDTO().getValue())) {
			ErpProgrammeDBO erpProgrammeDBO = new ErpProgrammeDBO();
			erpProgrammeDBO.setId(Integer.parseInt(dto.getErpProgrammeDTO().getValue()));
			dbo.setErpProgrammeDBO(erpProgrammeDBO);
		}
		dbo.setRecordStatus('A');
		Set<AdmWeightageDefinitionDetailDBO> weightageDefinitionDetailDBOsSet = new HashSet<>();
		if(!Utils.isNullOrEmpty(dto.getAdmWeightageDefinitionDetailDTOSet())) {
			weightageDefinitionDetailDBOsSet  = weightageDefinitionSettingsHelper.convertWeightDefDetailDTOstoDBOsSet(weightageDefinitionDetailDBOsSet, dto, userId, dbo, admWeightageDefinitionDetailDBOIds);
			dbo.setAdmWeightageDefinitionDetailDBOsSet(weightageDefinitionDetailDBOsSet);
		}
		Set<AdmWeightageDefinitionGeneralDBO> itemGeneralDbosSet = new HashSet<>();
		if(!Utils.isNullOrEmpty(dto.getAdmWeightageDefinitionGeneralDTOsSet())) {
			itemGeneralDbosSet = weightageDefinitionSettingsHelper.convertGenetalDBOstoDTOsSet(itemGeneralDbosSet, dto, userId, dbo, admWeightageDefinitionGeneralDBOIds);
			dbo.setAdmWeightageDefinitionGeneralDBOsSet(itemGeneralDbosSet);
		}
		Set<AdmWeightageDefinitionLocationCampusDBO> locationCampusDBOSet = new HashSet<>();
		if(!Utils.isNullOrEmpty(dto.getCampusOrlocationsMappping())) {
			locationCampusDBOSet = weightageDefinitionSettingsHelper.convertLocationCampusDTOsToDBOsSet(locationCampusDBOSet, dto, userId, dbo);
			dbo.setAdmWeightageDefinitionLocationCampusDBOsSet(locationCampusDBOSet);
		}
		return dbo;
	}
	
	public AdmWeightageDefinitionDTO convertDboToDto(AdmWeightageDefinitionDBO dbo) {
    	AdmWeightageDefinitionDTO dto = null;
    	if(!Utils.isNullOrEmpty(dbo)) {
    		dto = convertGridDboToDto(dbo);
    		Set<AdmWeightageDefinitionDetailDTO> detailDTOsSet = new HashSet<AdmWeightageDefinitionDetailDTO>();
    		AdmWeightageDefinitionDetailDTO detailDTO = new AdmWeightageDefinitionDetailDTO();
    		List<AdmPrerequisiteExamDTO> prerequisiteExamDTOsList = new ArrayList<AdmPrerequisiteExamDTO>();
    		List<AdmQualificationListDTO> qualificationDTOsList = new ArrayList<AdmQualificationListDTO>();
    		List<AdmSelectionProcessPlanDetailDTO> selectionProcessPlanDetailDTOsList = new ArrayList<AdmSelectionProcessPlanDetailDTO>();
        	if(!Utils.isNullOrEmpty(dbo.getAdmWeightageDefinitionDetailDBOsSet())) {
        		Map<Integer, AdmWeightageDefinitionDetailDBO> spMap = new HashMap<Integer, AdmWeightageDefinitionDetailDBO>();
        		Map<Integer, AdmSelectionProcessPlanDetailDBO> spDetailMap = new HashMap<Integer, AdmSelectionProcessPlanDetailDBO>();
        		Map<Integer, Integer> spDetailTypeMap = new HashMap<Integer, Integer>();
        		Map<Integer, AdmSelectionProcessPlanDBO> spPlanMap = new HashMap<Integer, AdmSelectionProcessPlanDBO>();
        		dbo.getAdmWeightageDefinitionDetailDBOsSet().forEach(item->{
        			if(!Utils.isNullOrEmpty(item.getScore()) && !Utils.isNullOrEmpty(item.getRecordStatus()) && item.getRecordStatus() == 'A') {
        				if(!Utils.isNullOrEmpty(item.getAdmPrerequisiteExamDBO()) && !Utils.isNullOrEmpty(item.getAdmPrerequisiteExamDBO().getExamName())) {
            				if(!Utils.isNullOrEmpty(item.getScore()))
            				prerequisiteExamDTOsList.add(weightageDefinitionSettingsHelper
            						.convertAdmPrerequisiteExamDBOtoDTO(item));
            			}
            			else if(!Utils.isNullOrEmpty(item.getAdmQualificationListDBO())) {
            				qualificationDTOsList.add(weightageDefinitionSettingsHelper
            						.convertAdmQualificationListDBOtoDTO(item));
            			}
            			else if(!Utils.isNullOrEmpty(item.getAdmSelectionProcessPlanDetailDBO())) {
            				spMap.put(item.getId(), item);
            				spPlanMap.put(item.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO().getId(), item.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessPlanDBO());
            				spDetailMap.put(item.getAdmSelectionProcessPlanDetailDBO().getId(), item.getAdmSelectionProcessPlanDetailDBO());
            				spDetailTypeMap.put(item.getId(), item.getAdmSelectionProcessTypeDetailsDBO().getId());
            			}
        			}
                });
        		spPlanMap.entrySet().forEach(itemPlan->{
        			AdmSelectionProcessPlanDetailDTO spdetailDTO = new AdmSelectionProcessPlanDetailDTO();
        			spdetailDTO.setSessionId(itemPlan.getValue().getId());
        			spdetailDTO.setSessionName(itemPlan.getValue().getSelectionProcessSession());
        			List<AdmSelectionProcessPlanDetailAddSlotDTO> slotList = new ArrayList<>();
        			spDetailMap.entrySet().forEach(itemPlanDetail-> {
        				if(!Utils.isNullOrEmpty(itemPlanDetail.getValue()) && !Utils.isNullOrEmpty(itemPlanDetail.getValue().getAdmSelectionProcessTypeDBO()) &&
        				    !Utils.isNullOrEmpty(itemPlanDetail.getValue().getAdmSelectionProcessPlanDBO())	&&
        				    itemPlanDetail.getValue().getAdmSelectionProcessPlanDBO().getId() == itemPlan.getValue().getId() &&
        					!Utils.isNullOrEmpty(itemPlanDetail.getValue().getAdmSelectionProcessTypeDBO().getSelectionStageName())) {
        					AdmSelectionProcessPlanDetailAddSlotDTO addSlotDTO = new AdmSelectionProcessPlanDetailAddSlotDTO();
            				addSlotDTO.setDetailId(itemPlanDetail.getValue().getId());
        					ExModelBaseDTO baseDTO = new ExModelBaseDTO();
            				baseDTO.id = String.valueOf(itemPlanDetail.getValue().getId());
            				baseDTO.text = itemPlanDetail.getValue().getAdmSelectionProcessTypeDBO().getSelectionStageName();
            				addSlotDTO.setSelectionProcessName(baseDTO);
            				List<AdmissionSelectionProcessTypeDetailsDTO> selectionProcessTypeDetailsDTOList = new ArrayList<>(); 
            				spMap.entrySet().forEach(itemSPDetailType->{
            					if(!Utils.isNullOrEmpty(itemSPDetailType.getValue()) && !Utils.isNullOrEmpty(itemSPDetailType.getValue().getAdmSelectionProcessPlanDetailDBO())
            							&& itemSPDetailType.getValue().getAdmSelectionProcessPlanDetailDBO().getId() == itemPlanDetail.getValue().getId()) {
            						AdmissionSelectionProcessTypeDetailsDTO  processTypeDetailsDTO = new AdmissionSelectionProcessTypeDetailsDTO();
            						processTypeDetailsDTO.setId(String.valueOf(itemSPDetailType.getValue().getAdmSelectionProcessTypeDetailsDBO().getId()));
            						if(!Utils.isNullOrEmpty(itemSPDetailType.getValue().getAdmSelectionProcessTypeDetailsDBO().getSubProcessName()))
            							processTypeDetailsDTO.setSubProcess(itemSPDetailType.getValue().getAdmSelectionProcessTypeDetailsDBO().getSubProcessName());
            						if(!Utils.isNullOrEmpty(itemSPDetailType.getValue().getScore()))
            							processTypeDetailsDTO.setScore(itemSPDetailType.getValue().getScore());
            						processTypeDetailsDTO.setParentId(itemSPDetailType.getKey());
            						selectionProcessTypeDetailsDTOList.add(processTypeDetailsDTO);
            					}
            				});
            				addSlotDTO.setAdmissionSelectionProcessTypeDetailsList(selectionProcessTypeDetailsDTOList);
            				slotList.add(addSlotDTO);
        				}
        			});
        			spdetailDTO.setSlotslist(slotList);
        			selectionProcessPlanDetailDTOsList.add(spdetailDTO);
        		});
        		addWeightDetailsToList(prerequisiteExamDTOsList, qualificationDTOsList, selectionProcessPlanDetailDTOsList, detailDTO);
    			detailDTOsSet.add(detailDTO);
        		dto.setAdmWeightageDefinitionDetailDTOSet(detailDTOsSet);
        	}else {
    			addWeightDetailsToList(prerequisiteExamDTOsList, qualificationDTOsList, selectionProcessPlanDetailDTOsList, detailDTO);
    			detailDTOsSet.add(detailDTO);
        		dto.setAdmWeightageDefinitionDetailDTOSet(detailDTOsSet);
        	}
        	Set<AdmWeightageDefinitionGeneralDTO> generalDTOsSet = new HashSet<AdmWeightageDefinitionGeneralDTO>();
    		AdmWeightageDefinitionGeneralDTO admWeightageDefinitionGeneralDTO = new AdmWeightageDefinitionGeneralDTO();
        	 List<weightageGeneralDTO> erpReligionDTOList = new ArrayList<weightageGeneralDTO>();
        	 List<weightageGeneralDTO> erpReservationCategoryDTOList = new ArrayList<weightageGeneralDTO>();
        	 List<weightageGeneralDTO> erpGenderDTOList = new ArrayList<weightageGeneralDTO>();
        	 List<weightageGeneralDTO> erpInstitutionDTOList = new ArrayList<weightageGeneralDTO>();
        	 List<weightageGeneralDTO> erpResidentCategoryDTOList = new ArrayList<weightageGeneralDTO>();
        	 List<weightageGeneralDTO> admQualificationDegreeListDTOList = new ArrayList<weightageGeneralDTO>();
        	 List<weightageGeneralDTO> admWeightageDefinitionWorkExperienceDTOList = new ArrayList<weightageGeneralDTO>();
            if(!Utils.isNullOrEmpty(dbo.getAdmWeightageDefinitionGeneralDBOsSet())){            	
            	dbo.getAdmWeightageDefinitionGeneralDBOsSet().forEach(generalItem->{
            		if(!Utils.isNullOrEmpty(generalItem.getRecordStatus()) && generalItem.getRecordStatus() == 'A' && !Utils.isNullOrEmpty(generalItem.getScore())) {
                		if(!Utils.isNullOrEmpty(generalItem.getErpReligionDBO()) && !Utils.isNullOrEmpty(generalItem.getErpReligionDBO().getReligionName())) {
                			weightageGeneralDTO generalDTO  = weightageDefinitionSettingsHelper.convertErpReligionDBOtoDTO(generalItem.getErpReligionDBO());
                			generalDTO.setScore(generalItem.getScore());
                			generalDTO.setParentId(generalItem.getId());
                			erpReligionDTOList.add(generalDTO);
                		}
                		else if(!Utils.isNullOrEmpty(generalItem.getErpGenderDBO()) && !Utils.isNullOrEmpty(generalItem.getErpGenderDBO().getGenderName())) {
                			weightageGeneralDTO generalDTO = weightageDefinitionSettingsHelper.convertErpGenderDBOtoDTO(generalItem.getErpGenderDBO());
                			generalDTO.setScore(generalItem.getScore());
                			generalDTO.setParentId(generalItem.getId());
                			erpGenderDTOList.add(generalDTO);
                		}else if(!Utils.isNullOrEmpty(generalItem.getErpInstitutionDBO()) && !Utils.isNullOrEmpty(generalItem.getErpInstitutionDBO().getInstitutionName())){
                			weightageGeneralDTO generalDTO = weightageDefinitionSettingsHelper.convertErpInstitutionDBOtoDTO(generalItem.getErpInstitutionDBO());
                			generalDTO.setScore(generalItem.getScore());
                			generalDTO.setParentId(generalItem.getId());
                			erpInstitutionDTOList.add(generalDTO);
                		}else if(!Utils.isNullOrEmpty(generalItem.getErpReservationCategoryDBO()) && !Utils.isNullOrEmpty(generalItem.getErpReservationCategoryDBO().getReservationCategoryName())) {
                			weightageGeneralDTO generalDTO = weightageDefinitionSettingsHelper.convertErpReservationCategoryDBOtoDTO(generalItem.getErpReservationCategoryDBO());
                			generalDTO.setScore(generalItem.getScore());
                			generalDTO.setParentId(generalItem.getId());
                			erpReservationCategoryDTOList.add(generalDTO);
                		}else if(!Utils.isNullOrEmpty(generalItem.getErpResidentCategoryDBO()) && !Utils.isNullOrEmpty(generalItem.getErpResidentCategoryDBO().getResidentCategoryName())) {
	                		weightageGeneralDTO generalDTO = weightageDefinitionSettingsHelper.convertErpResidentCategoryDBOtoDTO(generalItem.getErpResidentCategoryDBO());
	            			generalDTO.setScore(generalItem.getScore());
	            			generalDTO.setParentId(generalItem.getId());
	            			erpResidentCategoryDTOList.add(generalDTO);
                		}else if(!Utils.isNullOrEmpty(generalItem.getAdmQualificationDegreeListDBO()) && !Utils.isNullOrEmpty(generalItem.getAdmQualificationDegreeListDBO().getDegreeName())) {
                			weightageGeneralDTO generalDTO = weightageDefinitionSettingsHelper.convertAdmQualificationDegreeListDBOtoDTO(generalItem.getAdmQualificationDegreeListDBO());
	            			generalDTO.setScore(generalItem.getScore());
	            			generalDTO.setParentId(generalItem.getId());
	            			admQualificationDegreeListDTOList.add(generalDTO);
                		}
                		else if(!Utils.isNullOrEmpty(generalItem.getAdmWeightageDefinitionWorkExperienceDBO()) && !Utils.isNullOrEmpty(generalItem.getAdmWeightageDefinitionWorkExperienceDBO().getWorkExperienceName())) {
                			weightageGeneralDTO generalDTO = weightageDefinitionSettingsHelper.convertAdmWeightageDefinitionWorkExperienceDBOtoDTO(generalItem.getAdmWeightageDefinitionWorkExperienceDBO());
	            			generalDTO.setScore(generalItem.getScore());
	            			generalDTO.setParentId(generalItem.getId());
	            			admWeightageDefinitionWorkExperienceDTOList.add(generalDTO);
                		}
            		} 		
            	});
        		addGeneralToSet(erpReligionDTOList, erpGenderDTOList,erpInstitutionDTOList, erpReservationCategoryDTOList, erpResidentCategoryDTOList,
        				admQualificationDegreeListDTOList,admWeightageDefinitionWorkExperienceDTOList, admWeightageDefinitionGeneralDTO);
        		generalDTOsSet.add(admWeightageDefinitionGeneralDTO);
            	dto.setAdmWeightageDefinitionGeneralDTOsSet(generalDTOsSet);
            }else {
            	addGeneralToSet(erpReligionDTOList, erpGenderDTOList,erpInstitutionDTOList, erpReservationCategoryDTOList, erpResidentCategoryDTOList,
        				admQualificationDegreeListDTOList,admWeightageDefinitionWorkExperienceDTOList, admWeightageDefinitionGeneralDTO);
        		generalDTOsSet.add(admWeightageDefinitionGeneralDTO);
            	dto.setAdmWeightageDefinitionGeneralDTOsSet(generalDTOsSet);
            }
       }
		return dto;
    }
	
	private void addWeightDetailsToList(List<AdmPrerequisiteExamDTO> prerequisiteExamDTOsList,
			List<AdmQualificationListDTO> qualificationDTOsList,
			List<AdmSelectionProcessPlanDetailDTO> selectionProcessPlanDetailDTOsList,
			AdmWeightageDefinitionDetailDTO detailDTO) {
		prerequisiteExamDTOsList.sort(Comparator.comparing(AdmPrerequisiteExamDTO::getExamName));
		qualificationDTOsList.sort(Comparator.comparing(AdmQualificationListDTO::getQualificationOrder));
		selectionProcessPlanDetailDTOsList.sort(Comparator.comparing(AdmSelectionProcessPlanDetailDTO::getSessionName));
		detailDTO.setAdmPrerequisiteExamDTOList(prerequisiteExamDTOsList);
		detailDTO.setAdmQualificationListDTOList(qualificationDTOsList);
		detailDTO.setAdmSelectionProcessPlanDetailDTOList(selectionProcessPlanDetailDTOsList);
	}

	private void addGeneralToSet(List<weightageGeneralDTO> erpReligionDTOList,
			List<weightageGeneralDTO> erpGenderDTOList, List<weightageGeneralDTO> erpInstitutionDTOList,
			List<weightageGeneralDTO> erpReservationCategoryDTOList,
			List<weightageGeneralDTO> erpResidentCategoryDTOList,
			List<weightageGeneralDTO> admQualificationDegreeListDTOList,
			List<weightageGeneralDTO> admWeightageDefinitionWorkExperienceDTOList, AdmWeightageDefinitionGeneralDTO admWeightageDefinitionGeneralDTO) {
		admWeightageDefinitionGeneralDTO.setErpReligionDTOList(erpReligionDTOList);
		admWeightageDefinitionGeneralDTO.setErpGenderDTOList(erpGenderDTOList);
		admWeightageDefinitionGeneralDTO.setErpInstitutionDTOList(erpInstitutionDTOList);
		admWeightageDefinitionGeneralDTO.setErpReservationCategoryDTOList(erpReservationCategoryDTOList);
		admWeightageDefinitionGeneralDTO.setErpResidentCategoryDTOList(erpResidentCategoryDTOList);
		admWeightageDefinitionGeneralDTO.setAdmQualificationDegreeListDTOList(admQualificationDegreeListDTOList);
		admWeightageDefinitionGeneralDTO.setAdmWeightageDefinitionWorkExperienceDTOList(admWeightageDefinitionWorkExperienceDTOList);
	}
	
	public AdmWeightageDefinitionDTO convertGridDboToDto(AdmWeightageDefinitionDBO dbo) {
		AdmWeightageDefinitionDTO dto = null;
    	if(!Utils.isNullOrEmpty(dbo)) {
    		dto = new AdmWeightageDefinitionDTO();
    		dto.setId(dbo.getId());
    		if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
    			LookupItemDTO academicYearDTO = new LookupItemDTO();
    			academicYearDTO.setValue(String.valueOf(dbo.getErpAcademicYearDBO().getId()));
    			academicYearDTO.setLabel(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO().getAcademicYearName())?dbo.getErpAcademicYearDBO().getAcademicYearName():"");
            	dto.setErpAcademicYearDTO(academicYearDTO);
        	}
        	if(!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO())) {
        		LookupItemDTO programmeDTO = new LookupItemDTO();
            	programmeDTO.setValue(String.valueOf(dbo.getErpProgrammeDBO().getId()));
            	programmeDTO.setLabel(String.valueOf(!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO().getProgrammeName())?dbo.getErpProgrammeDBO().getProgrammeName():""));
                dto.setErpProgrammeDTO(programmeDTO);
            	if(!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO().getErpProgrammeDegreeDBO())) {
            		LookupItemDTO erpProgrammeDegreeDTO = new LookupItemDTO();
            		erpProgrammeDegreeDTO.setValue(String.valueOf(dbo.getErpProgrammeDBO().getErpProgrammeDegreeDBO().getId()));
            		erpProgrammeDegreeDTO.setLabel(String.valueOf(dbo.getErpProgrammeDBO().getErpProgrammeDegreeDBO().getProgrammeDegree()));
            		dto.setErpProgrammeDegreeDTO(erpProgrammeDegreeDTO);
            	}
            	if(!Utils.isNullOrEmpty(dbo.getErpProgrammeDBO().getErpProgrammeLevelDBO())) {
            		LookupItemDTO erpProgrammeLevelDTO = new LookupItemDTO();
            		erpProgrammeLevelDTO.setValue(String.valueOf(dbo.getErpProgrammeDBO().getErpProgrammeLevelDBO().getId()));
            		erpProgrammeLevelDTO.setLabel(String.valueOf(dbo.getErpProgrammeDBO().getErpProgrammeLevelDBO().getProgrammeLevel()));
            		dto.setErpProgrammeLevelDTO(erpProgrammeLevelDTO);
            	}
        	}
        	if(!Utils.isNullOrEmpty(dbo.getPreRequisiteWeigtageTotal())) {
        		dto.setPreRequisiteWeigtageTotal(dbo.getPreRequisiteWeigtageTotal());
        	}
        	if(!Utils.isNullOrEmpty(dbo.getEducationWeightageTotal())) {
        		dto.setEducationWeightageTotal(dbo.getEducationWeightageTotal());
        	}
        	if(!Utils.isNullOrEmpty(dbo.getInterviewWeightageTotal())) {
        		dto.setInterviewWeightageTotal(dbo.getInterviewWeightageTotal());
        	}
        	if(!Utils.isNullOrEmpty(dbo.getOverallTotal())) {
        		dto.setOverallTotal(dbo.getOverallTotal());
        	}
            if(!Utils.isNullOrEmpty(dbo.getAdmWeightageDefinitionLocationCampusDBOsSet())) {
            	List<ProgramPreferenceDTO> campusList = new ArrayList<>();
            	List<ProgramPreferenceDTO> locationList = new ArrayList<>();
                dbo.getAdmWeightageDefinitionLocationCampusDBOsSet().forEach(item->{
                	if(!Utils.isNullOrEmpty(item.getErpCampusProgrammeMappingDBO()) && !Utils.isNullOrEmpty(item.getRecordStatus()) && item.getRecordStatus() == 'A') {
            			if(!Utils.isNullOrEmpty(item.getErpCampusProgrammeMappingDBO().getErpCampusDBO()) &&
            					!Utils.isNullOrEmpty(item.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName())){
							ProgramPreferenceDTO campus = new ProgramPreferenceDTO();
            				campus.setValue(String.valueOf(item.getErpCampusProgrammeMappingDBO().getId()));
            				campus.setLabel(item.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
							campus.setParentId(item.getId());
            				campusList.add(campus);
            			}else if(!Utils.isNullOrEmpty(item.getErpCampusProgrammeMappingDBO().getErpLocationDBO()) &&
            					!Utils.isNullOrEmpty(item.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName())){
							ProgramPreferenceDTO location = new ProgramPreferenceDTO();
            				location.setValue( String.valueOf(item.getErpCampusProgrammeMappingDBO().getId()));
            				location.setValue(item.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
							location.setParentId(item.getId());
            				locationList.add(location);
            			}
                	}
            	});
                if(!Utils.isNullOrEmpty(campusList)) {
                    dto.setCampusOrlocationsMappping(campusList);
                }else if(!Utils.isNullOrEmpty(locationList)) {
					dto.setCampusOrlocationsMappping(locationList);
				}
            }
    	}
		return dto;
	}
}