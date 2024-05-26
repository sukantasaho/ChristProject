package com.christ.erp.services.handlers.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDataCollectionTemplateDBO;
import com.christ.erp.services.dbobjects.common.ErpDataCollectionTemplateQuestionsDBO;
import com.christ.erp.services.dbobjects.common.ErpDataCollectionTemplateQuestionsOptionsDBO;
import com.christ.erp.services.dbobjects.common.ErpDataCollectionTemplateSectionDBO;
import com.christ.erp.services.dto.common.*;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.common.DataCollectionTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataCollectionHandler {

    @Autowired
    private DataCollectionTransaction dataCollectionTransaction;

    public Flux<ErpDataCollectionTemplateDTO> getGridData() {
        return dataCollectionTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDBOToGridData);
    }

    private ErpDataCollectionTemplateDTO convertDBOToGridData(ErpDataCollectionTemplateDBO erpDataCollectionTemplateDBO) {
        ErpDataCollectionTemplateDTO erpDataCollectionTemplateDTO = new ErpDataCollectionTemplateDTO();
        erpDataCollectionTemplateDTO.setId(erpDataCollectionTemplateDBO.getId());
        erpDataCollectionTemplateDTO.setTemplateName(erpDataCollectionTemplateDBO.getTemplateName());
        erpDataCollectionTemplateDTO.setTemplateFor(new SelectDTO());
        erpDataCollectionTemplateDTO.getTemplateFor().setValue(erpDataCollectionTemplateDBO.getTemplateFor());
        erpDataCollectionTemplateDTO.setInstructions(erpDataCollectionTemplateDBO.getInstructions());
        return erpDataCollectionTemplateDTO;
    }

    public Mono<ApiResult> saveOrUpdate(Mono<ErpDataCollectionTemplateDTO> data, String userId) {
        return data.handle((erpDataCollectionTemplateDTO, synchronousSink) -> {
            boolean isDuplicate = dataCollectionTransaction.duplicateCheck(erpDataCollectionTemplateDTO);
            if (isDuplicate) {
                synchronousSink.error(new DuplicateException("Duplicate record for "+erpDataCollectionTemplateDTO.getTemplateName()));
            } else {
                synchronousSink.next(erpDataCollectionTemplateDTO);
            }
        }).cast(ErpDataCollectionTemplateDTO.class).map(dto -> convertDTOToDBO(dto, Integer.parseInt(userId))).flatMap(s -> {
            if(!Utils.isNullOrEmpty(s.getId())) {
                dataCollectionTransaction.update(s);
            } else {
                dataCollectionTransaction.save(s);
            }
            return Mono.just(Boolean.TRUE);
        }).map(Utils::responseResult);
    }

    private ErpDataCollectionTemplateDBO convertDTOToDBO(ErpDataCollectionTemplateDTO dto, Integer userId) {
        ErpDataCollectionTemplateDBO erpDataCollectionTemplateDBO = new ErpDataCollectionTemplateDBO();
        BeanUtils.copyProperties(dto,erpDataCollectionTemplateDBO);
        erpDataCollectionTemplateDBO.setTemplateFor(dto.getTemplateFor().getValue());
        erpDataCollectionTemplateDBO.setRecordStatus('A');
        erpDataCollectionTemplateDBO.setCreatedUsersId(userId);
        erpDataCollectionTemplateDBO.setModifiedUsersId(userId);
        ErpDataCollectionTemplateDBO templateDBO = null;
        if(!Utils.isNullOrEmpty(dto.getId())){
            templateDBO = dataCollectionTransaction.getTemplateDBO(dto.getId());
        }
        Map<Integer, ErpDataCollectionTemplateSectionDBO> sectionsMap = new HashMap<>();
        Map<Integer, Map<Integer, ErpDataCollectionTemplateQuestionsDBO>> sectionQuestionsMap = new HashMap<>();
        Map<Integer, Map<Integer, ErpDataCollectionTemplateQuestionsOptionsDBO>> questionOptionsMap = new HashMap<>();
        if(!Utils.isNullOrEmpty(templateDBO) && !Utils.isNullOrEmpty(templateDBO.getErpDataCollectionTemplateSectionDBOS())){
            templateDBO.getErpDataCollectionTemplateSectionDBOS().forEach(erpDataCollectionTemplateSectionDBO -> {
                erpDataCollectionTemplateSectionDBO.setRecordStatus('D');
                erpDataCollectionTemplateSectionDBO.setModifiedUsersId(userId);
                sectionsMap.put(erpDataCollectionTemplateSectionDBO.getId(),erpDataCollectionTemplateSectionDBO);
                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateQuestionsDBOS())){
                    Map<Integer, ErpDataCollectionTemplateQuestionsDBO> questionsDBOMap = new HashMap<>();
                    erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateQuestionsDBOS().forEach(erpDataCollectionTemplateQuestionsDBO -> {
                        erpDataCollectionTemplateQuestionsDBO.setRecordStatus('D');
                        erpDataCollectionTemplateQuestionsDBO.setModifiedUsersId(userId);
                        questionsDBOMap.put(erpDataCollectionTemplateQuestionsDBO.getId(),erpDataCollectionTemplateQuestionsDBO);
                        if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateQuestionsOptionsDBOS())){
                            Map<Integer, ErpDataCollectionTemplateQuestionsOptionsDBO> optionsDBOMap = new HashMap<>();
                            erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateQuestionsOptionsDBOS().forEach(erpDataCollectionTemplateQuestionsOptionsDBO -> {
                                erpDataCollectionTemplateQuestionsOptionsDBO.setRecordStatus('D');
                                erpDataCollectionTemplateQuestionsOptionsDBO.setModifiedUsersId(userId);
                                optionsDBOMap.put(erpDataCollectionTemplateQuestionsOptionsDBO.getId(),erpDataCollectionTemplateQuestionsOptionsDBO);
                            });
                            questionOptionsMap.put(erpDataCollectionTemplateQuestionsDBO.getId(), optionsDBOMap);
                        }
                    });
                    sectionQuestionsMap.put(erpDataCollectionTemplateSectionDBO.getId(),questionsDBOMap);
                }
            });
        }
        Set<ErpDataCollectionTemplateSectionDBO> erpDataCollectionTemplateSectionDBOS = new HashSet<>();
        if(!Utils.isNullOrEmpty(dto.getErpDataCollectionTemplateSectionDTOS())){
            Map<Integer, ErpDataCollectionTemplateSectionDBO> sectionMap = new HashMap<>();
            dto.getErpDataCollectionTemplateSectionDTOS().forEach(erpDataCollectionTemplateSectionDTO -> {
                ErpDataCollectionTemplateSectionDBO erpDataCollectionTemplateSectionDBO = new ErpDataCollectionTemplateSectionDBO();
                BeanUtils.copyProperties(erpDataCollectionTemplateSectionDTO,erpDataCollectionTemplateSectionDBO);
                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateSectionDTO.getId())){
                    sectionsMap.remove(erpDataCollectionTemplateSectionDTO.getId());
                }
                erpDataCollectionTemplateSectionDBO.setRecordStatus('A');
                erpDataCollectionTemplateSectionDBO.setCreatedUsersId(userId);
                erpDataCollectionTemplateSectionDBO.setModifiedUsersId(userId);
                erpDataCollectionTemplateSectionDBO.setErpDataCollectionTemplateDBO(erpDataCollectionTemplateDBO);
                sectionMap.put(erpDataCollectionTemplateSectionDTO.getSectionNo(), erpDataCollectionTemplateSectionDBO);
                Set<ErpDataCollectionTemplateQuestionsDBO> erpDataCollectionTemplateQuestionsDBOS = new HashSet<>();
                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateSectionDTO.getErpDataCollectionTemplateQuestionsDTOS())){
                    erpDataCollectionTemplateSectionDTO.getErpDataCollectionTemplateQuestionsDTOS().forEach(erpDataCollectionTemplateQuestionsDTO -> {
                        ErpDataCollectionTemplateQuestionsDBO erpDataCollectionTemplateQuestionsDBO = new ErpDataCollectionTemplateQuestionsDBO();
                        BeanUtils.copyProperties(erpDataCollectionTemplateQuestionsDTO,erpDataCollectionTemplateQuestionsDBO);
                        if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getId())){
                            if(!Utils.isNullOrEmpty(sectionQuestionsMap.get(erpDataCollectionTemplateSectionDTO.getId()))){
                                if(!Utils.isNullOrEmpty(sectionQuestionsMap.get(erpDataCollectionTemplateSectionDTO.getId()).get(erpDataCollectionTemplateQuestionsDTO.getId()))){
                                    sectionQuestionsMap.get(erpDataCollectionTemplateSectionDTO.getId()).remove(erpDataCollectionTemplateQuestionsDTO.getId());
                                }
                            }
                        }
                        erpDataCollectionTemplateQuestionsDBO.setRecordStatus('A');
                        erpDataCollectionTemplateQuestionsDBO.setCreatedUsersId(userId);
                        erpDataCollectionTemplateQuestionsDBO.setModifiedUsersId(userId);
                        erpDataCollectionTemplateQuestionsDBO.setErpDataCollectionTemplateSectionDBO(erpDataCollectionTemplateSectionDBO);
                        erpDataCollectionTemplateQuestionsDBO.setGoToQuestionBasedAnswer(erpDataCollectionTemplateQuestionsDTO.getGoToQuestionBasedAnswer());
                        if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType()) && !Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue())){
                            erpDataCollectionTemplateQuestionsDBO.setQuestionType(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue());
                            switch (erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue()) {
                                case "file upload" :
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getIsMultipleFileUpload())) {
                                        erpDataCollectionTemplateQuestionsDBO.setIsMultipleFileUpload(erpDataCollectionTemplateQuestionsDTO.getIsMultipleFileUpload());
                                    }
                                    break;
                                case "rating" :
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getIsAllowHalfIcon())) {
                                        erpDataCollectionTemplateQuestionsDBO.setIsAllowHalfIcon(erpDataCollectionTemplateQuestionsDTO.getIsAllowHalfIcon());
                                    }
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getRatingScale())) {
                                        erpDataCollectionTemplateQuestionsDBO.setRatingScale(Integer.parseInt(erpDataCollectionTemplateQuestionsDTO.getRatingScale().getValue()));
                                    }
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getRatingShape())) {
                                        erpDataCollectionTemplateQuestionsDBO.setRatingShape(erpDataCollectionTemplateQuestionsDTO.getRatingShape().getValue());
                                    }
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getRatingColor())) {
                                        erpDataCollectionTemplateQuestionsDBO.setRatingColor(erpDataCollectionTemplateQuestionsDTO.getRatingColor().getValue());
                                    }
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getIsAddRatingLabel())) {
                                        erpDataCollectionTemplateQuestionsDBO.setIsAddRatingLabel(erpDataCollectionTemplateQuestionsDTO.getIsAddRatingLabel());
                                    }
                                    break;
                                case "slider" :
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getSliderMinValue())) {
                                        erpDataCollectionTemplateQuestionsDBO.setSliderMinValue(erpDataCollectionTemplateQuestionsDTO.getSliderMinValue());
                                    }
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getSliderMaxValue())) {
                                        erpDataCollectionTemplateQuestionsDBO.setSliderMaxValue(erpDataCollectionTemplateQuestionsDTO.getSliderMaxValue());
                                    }
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getSliderInterval())) {
                                        erpDataCollectionTemplateQuestionsDBO.setSliderInterval(erpDataCollectionTemplateQuestionsDTO.getSliderInterval());
                                    }
                                    break;
                                case "image choice" :
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getIsAddImageLabel())) {
                                        erpDataCollectionTemplateQuestionsDBO.setIsAddImageLabel(erpDataCollectionTemplateQuestionsDTO.getIsAddImageLabel());
                                    }
                                    break;
                            }
                        }
                        Set<ErpDataCollectionTemplateQuestionsOptionsDBO> erpDataCollectionTemplateQuestionsOptionsDBOS = new HashSet<>();
                        if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getErpDataCollectionTemplateQuestionsOptionsDTOS())){
                            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType()) && !Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue())){
                                erpDataCollectionTemplateQuestionsDBO.setQuestionType(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue());
                                switch (erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue()) {
                                    case "file upload" :
                                        erpDataCollectionTemplateQuestionsDTO.getErpDataCollectionTemplateQuestionsOptionsDTOS().forEach(erpDataCollectionTemplateQuestionsOptionsDTO -> {
                                            if(erpDataCollectionTemplateQuestionsOptionsDTO.isChecked()){
                                                ErpDataCollectionTemplateQuestionsOptionsDBO erpDataCollectionTemplateQuestionsOptionsDBO = new ErpDataCollectionTemplateQuestionsOptionsDBO();
                                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDTO.getId())){
                                                    erpDataCollectionTemplateQuestionsOptionsDBO.setId(erpDataCollectionTemplateQuestionsOptionsDTO.getId());
                                                    if(!Utils.isNullOrEmpty(questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()))){
                                                        if(!Utils.isNullOrEmpty(questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()).get(erpDataCollectionTemplateQuestionsOptionsDTO.getId()))){
                                                            questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()).remove(erpDataCollectionTemplateQuestionsOptionsDTO.getId());
                                                        }
                                                    }
                                                }
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setOptionText(erpDataCollectionTemplateQuestionsOptionsDTO.getOptionText());
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setDisplayOrder(erpDataCollectionTemplateQuestionsOptionsDTO.getDisplayOrder());
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setRecordStatus('A');
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setCreatedUsersId(userId);
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setModifiedUsersId(userId);
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setErpDataCollectionTemplateQuestionsDBO(erpDataCollectionTemplateQuestionsDBO);
                                                erpDataCollectionTemplateQuestionsOptionsDBOS.add(erpDataCollectionTemplateQuestionsOptionsDBO);
                                            }
                                        });
                                        break;
                                    default:
                                        erpDataCollectionTemplateQuestionsDTO.getErpDataCollectionTemplateQuestionsOptionsDTOS().forEach(erpDataCollectionTemplateQuestionsOptionsDTO -> {
                                            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDTO.getOptionText())){
                                                ErpDataCollectionTemplateQuestionsOptionsDBO erpDataCollectionTemplateQuestionsOptionsDBO = new ErpDataCollectionTemplateQuestionsOptionsDBO();
                                                BeanUtils.copyProperties(erpDataCollectionTemplateQuestionsOptionsDTO,erpDataCollectionTemplateQuestionsOptionsDBO);
                                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDTO.getId())){
                                                    if(!Utils.isNullOrEmpty(questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()))){
                                                        if(!Utils.isNullOrEmpty(questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()).get(erpDataCollectionTemplateQuestionsOptionsDTO.getId()))){
                                                            questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()).remove(erpDataCollectionTemplateQuestionsOptionsDTO.getId());
                                                        }
                                                    }
                                                }
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setRecordStatus('A');
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setCreatedUsersId(userId);
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setModifiedUsersId(userId);
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setErpDataCollectionTemplateQuestionsDBO(erpDataCollectionTemplateQuestionsDBO);
                                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType()) && !Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue()) && "multiple choice".equalsIgnoreCase(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue())) {
                                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDTO.getErpDataCollectionTemplateSectionDTO())){
                                                        ErpDataCollectionTemplateSectionDBO sectionDBO = new ErpDataCollectionTemplateSectionDBO();
                                                        BeanUtils.copyProperties(erpDataCollectionTemplateQuestionsOptionsDTO.getErpDataCollectionTemplateSectionDTO(),sectionDBO);
                                                        if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDTO.getErpDataCollectionTemplateSectionDTO().getId())) {
                                                            sectionDBO.setId(erpDataCollectionTemplateQuestionsOptionsDTO.getErpDataCollectionTemplateSectionDTO().getId());
                                                        }
                                                        erpDataCollectionTemplateQuestionsOptionsDBO.setErpDataCollectionTemplateSectionDBO(sectionDBO);
                                                    }
                                                }
                                                erpDataCollectionTemplateQuestionsOptionsDBOS.add(erpDataCollectionTemplateQuestionsOptionsDBO);
                                            }
                                        });
                                }
                            }
                        }
                        if(!Utils.isNullOrEmpty(questionOptionsMap) && !Utils.isNullOrEmpty(questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()))){
                            erpDataCollectionTemplateQuestionsOptionsDBOS.addAll(questionOptionsMap.get(erpDataCollectionTemplateQuestionsDTO.getId()).values());
                        }
                        if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDBOS)){
                            erpDataCollectionTemplateQuestionsDBO.setErpDataCollectionTemplateQuestionsOptionsDBOS(erpDataCollectionTemplateQuestionsOptionsDBOS);
                            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType()) && !Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue()) && "rating".equalsIgnoreCase(erpDataCollectionTemplateQuestionsDTO.getQuestionType().getValue())) {
                                erpDataCollectionTemplateQuestionsDBO.setRatingScale(erpDataCollectionTemplateQuestionsOptionsDBOS.stream().filter(erpDataCollectionTemplateQuestionsOptionsDBO -> erpDataCollectionTemplateQuestionsOptionsDBO.getRecordStatus() == 'A').collect(Collectors.toSet()).size());
                            }
                        }
                        erpDataCollectionTemplateQuestionsDBOS.add(erpDataCollectionTemplateQuestionsDBO);
                    });
                    if(!Utils.isNullOrEmpty(sectionQuestionsMap) && !Utils.isNullOrEmpty(sectionQuestionsMap.get(erpDataCollectionTemplateSectionDTO.getId()))){
                        erpDataCollectionTemplateQuestionsDBOS.addAll(sectionQuestionsMap.get(erpDataCollectionTemplateSectionDTO.getId()).values());
                    }
                }
                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBOS))
                    erpDataCollectionTemplateSectionDBO.setErpDataCollectionTemplateQuestionsDBOS(erpDataCollectionTemplateQuestionsDBOS);
                erpDataCollectionTemplateSectionDBOS.add(erpDataCollectionTemplateSectionDBO);
            });
            if(!Utils.isNullOrEmpty(sectionsMap)){
                erpDataCollectionTemplateSectionDBOS.addAll(sectionsMap.values());
            }
            erpDataCollectionTemplateDBO.setErpDataCollectionTemplateSectionDBOS(erpDataCollectionTemplateSectionDBOS);
            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateDBO.getErpDataCollectionTemplateSectionDBOS())) {
                erpDataCollectionTemplateDBO.getErpDataCollectionTemplateSectionDBOS().forEach(erpDataCollectionTemplateSectionDBO -> {
                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateQuestionsDBOS())) {
                        erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateQuestionsDBOS().forEach(erpDataCollectionTemplateQuestionsDBO -> {
                            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateQuestionsOptionsDBOS())) {
                                erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateQuestionsOptionsDBOS().forEach(erpDataCollectionTemplateQuestionsOptionsDBO -> {
                                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getQuestionType()) && "multiple choice".equalsIgnoreCase(erpDataCollectionTemplateQuestionsDBO.getQuestionType())) {
                                        if(erpDataCollectionTemplateQuestionsDBO.getGoToQuestionBasedAnswer()){
                                            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateSectionDBO())
                                                && !Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateSectionDBO().getSectionNo())
                                                && sectionMap.containsKey(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateSectionDBO().getSectionNo())) {
                                                erpDataCollectionTemplateQuestionsOptionsDBO.setErpDataCollectionTemplateSectionDBO(sectionMap.get(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateSectionDBO().getSectionNo()));
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        return erpDataCollectionTemplateDBO;
    }

    public Mono<ErpDataCollectionTemplateDTO> edit(int id) {
        return dataCollectionTransaction.edit(id).map(this::convertDBOToDTO);
    }

    private ErpDataCollectionTemplateDTO convertDBOToDTO(ErpDataCollectionTemplateDBO erpDataCollectionTemplateDBO) {
        Map<Integer, Boolean> displaySectionMap = new HashMap<>();
        ErpDataCollectionTemplateDTO erpDataCollectionTemplateDTO = new ErpDataCollectionTemplateDTO();
        erpDataCollectionTemplateDTO.setId(erpDataCollectionTemplateDBO.getId());
        erpDataCollectionTemplateDTO.setTemplateName(erpDataCollectionTemplateDBO.getTemplateName());
        erpDataCollectionTemplateDTO.setTemplateFor(new SelectDTO());
        erpDataCollectionTemplateDTO.getTemplateFor().setValue(erpDataCollectionTemplateDBO.getTemplateFor());
        erpDataCollectionTemplateDTO.getTemplateFor().setLabel(erpDataCollectionTemplateDBO.getTemplateFor());
        erpDataCollectionTemplateDTO.setInstructions(erpDataCollectionTemplateDBO.getInstructions());
        if(!Utils.isNullOrEmpty(erpDataCollectionTemplateDBO.getErpDataCollectionTemplateSectionDBOS())){
            List<ErpDataCollectionTemplateSectionDTO> erpDataCollectionTemplateSectionDTOS = new ArrayList<>();
            erpDataCollectionTemplateDBO.getErpDataCollectionTemplateSectionDBOS().forEach(erpDataCollectionTemplateSectionDBO -> {
                if(erpDataCollectionTemplateSectionDBO.getRecordStatus() == 'A'){
                    ErpDataCollectionTemplateSectionDTO erpDataCollectionTemplateSectionDTO = new ErpDataCollectionTemplateSectionDTO();
                    BeanUtils.copyProperties(erpDataCollectionTemplateSectionDBO,erpDataCollectionTemplateSectionDTO);
                    erpDataCollectionTemplateSectionDTO.setErpDataCollectionTemplateDTO(new SelectDTO());
                    erpDataCollectionTemplateSectionDTO.getErpDataCollectionTemplateDTO().setValue(String.valueOf(erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateDBO().getId()));
                    erpDataCollectionTemplateSectionDTO.setDisplaySection(true);
                    if(!Utils.isNullOrEmpty(erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateQuestionsDBOS())){
                        List<ErpDataCollectionTemplateQuestionsDTO> erpDataCollectionTemplateQuestionsDTOS = new ArrayList<>();
                        erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateQuestionsDBOS().forEach(erpDataCollectionTemplateQuestionsDBO -> {
                            if (erpDataCollectionTemplateQuestionsDBO.getRecordStatus() == 'A') {
                                ErpDataCollectionTemplateQuestionsDTO erpDataCollectionTemplateQuestionsDTO = new ErpDataCollectionTemplateQuestionsDTO();
                                BeanUtils.copyProperties(erpDataCollectionTemplateQuestionsDBO,erpDataCollectionTemplateQuestionsDTO);
                                erpDataCollectionTemplateQuestionsDTO.setErpDataCollectionTemplateSectionDTO(new SelectDTO());
                                erpDataCollectionTemplateQuestionsDTO.getErpDataCollectionTemplateSectionDTO().setValue(String.valueOf(erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateSectionDBO().getId()));
                                erpDataCollectionTemplateQuestionsDTO.setQuestionType(new SelectDTO());
                                erpDataCollectionTemplateQuestionsDTO.getQuestionType().setValue(erpDataCollectionTemplateQuestionsDBO.getQuestionType());
                                erpDataCollectionTemplateQuestionsDTO.getQuestionType().setLabel(erpDataCollectionTemplateQuestionsDBO.getQuestionType());
                                erpDataCollectionTemplateQuestionsDTO.setSectionNo(erpDataCollectionTemplateSectionDBO.getSectionNo());
                                erpDataCollectionTemplateQuestionsDTO.setSectionValue(erpDataCollectionTemplateSectionDBO.getSectionValue());
                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getRatingScale())){
                                    erpDataCollectionTemplateQuestionsDTO.setRatingScale(new SelectDTO());
                                    erpDataCollectionTemplateQuestionsDTO.getRatingScale().setValue(String.valueOf(erpDataCollectionTemplateQuestionsDBO.getRatingScale()));
                                    erpDataCollectionTemplateQuestionsDTO.getRatingScale().setLabel(String.valueOf(erpDataCollectionTemplateQuestionsDBO.getRatingScale()));
                                }
                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getRatingShape())){
                                    erpDataCollectionTemplateQuestionsDTO.setRatingShape(new SelectDTO());
                                    erpDataCollectionTemplateQuestionsDTO.getRatingShape().setValue(erpDataCollectionTemplateQuestionsDBO.getRatingShape());
                                    erpDataCollectionTemplateQuestionsDTO.getRatingShape().setLabel(erpDataCollectionTemplateQuestionsDBO.getRatingShape());
                                }
                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getRatingScale())){
                                    erpDataCollectionTemplateQuestionsDTO.setRatingColor(new SelectDTO());
                                    erpDataCollectionTemplateQuestionsDTO.getRatingColor().setValue(erpDataCollectionTemplateQuestionsDBO.getRatingColor());
                                    erpDataCollectionTemplateQuestionsDTO.getRatingColor().setLabel(erpDataCollectionTemplateQuestionsDBO.getRatingColor());
                                }
                                List<ErpDataCollectionTemplateQuestionsOptionsDTO> erpDataCollectionTemplateQuestionsOptionsDTOS = new ArrayList<>();
                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateQuestionsOptionsDBOS())) {
                                    erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateQuestionsOptionsDBOS().forEach(erpDataCollectionTemplateQuestionsOptionsDBO -> {
                                        if (erpDataCollectionTemplateQuestionsOptionsDBO.getRecordStatus() == 'A') {
                                            ErpDataCollectionTemplateQuestionsOptionsDTO erpDataCollectionTemplateQuestionsOptionsDTO = new ErpDataCollectionTemplateQuestionsOptionsDTO();
                                            BeanUtils.copyProperties(erpDataCollectionTemplateQuestionsOptionsDBO, erpDataCollectionTemplateQuestionsOptionsDTO);
                                            erpDataCollectionTemplateQuestionsOptionsDTO.setErpDataCollectionTemplateQuestion(new SelectDTO());
                                            erpDataCollectionTemplateQuestionsOptionsDTO.getErpDataCollectionTemplateQuestion().setValue(String.valueOf(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateQuestionsDBO().getId()));
                                            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getQuestionType()) && "file upload".equalsIgnoreCase(erpDataCollectionTemplateQuestionsDBO.getQuestionType())) {
                                                erpDataCollectionTemplateQuestionsOptionsDTO.setChecked(true);
                                            }
                                            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsDBO.getQuestionType()) && "multiple choice".equalsIgnoreCase(erpDataCollectionTemplateQuestionsDBO.getQuestionType())
                                                && erpDataCollectionTemplateQuestionsDBO.getGoToQuestionBasedAnswer()) {
                                                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateSectionDBO())) {
                                                    displaySectionMap.put(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateSectionDBO().getSectionNo(), false);
                                                    erpDataCollectionTemplateQuestionsOptionsDTO.setErpDataCollectionTemplateSectionDTO(new ErpDataCollectionTemplateSectionDTO());
                                                    BeanUtils.copyProperties(erpDataCollectionTemplateQuestionsOptionsDBO.getErpDataCollectionTemplateSectionDBO(),erpDataCollectionTemplateQuestionsOptionsDTO.getErpDataCollectionTemplateSectionDTO());
                                                }
                                            }
                                            erpDataCollectionTemplateQuestionsOptionsDTOS.add(erpDataCollectionTemplateQuestionsOptionsDTO);
                                        }
                                    });
                                }
                                erpDataCollectionTemplateQuestionsOptionsDTOS.sort(Comparator.comparing(ErpDataCollectionTemplateQuestionsOptionsDTO::getDisplayOrder));
                                erpDataCollectionTemplateQuestionsDTO.setErpDataCollectionTemplateQuestionsOptionsDTOS(erpDataCollectionTemplateQuestionsOptionsDTOS);
                                erpDataCollectionTemplateQuestionsDTOS.add(erpDataCollectionTemplateQuestionsDTO);
                            }
                        });
                        erpDataCollectionTemplateQuestionsDTOS.sort(Comparator.comparing(ErpDataCollectionTemplateQuestionsDTO::getDisplayOrder));
                        erpDataCollectionTemplateSectionDTO.setErpDataCollectionTemplateQuestionsDTOS(erpDataCollectionTemplateQuestionsDTOS);
                    }
                    erpDataCollectionTemplateSectionDTOS.add(erpDataCollectionTemplateSectionDTO);
                }
            });
            erpDataCollectionTemplateSectionDTOS.sort(Comparator.comparing(ErpDataCollectionTemplateSectionDTO::getSectionNo));
            erpDataCollectionTemplateDTO.setErpDataCollectionTemplateSectionDTOS(erpDataCollectionTemplateSectionDTOS);
        }
        if(!Utils.isNullOrEmpty(displaySectionMap)) {
            erpDataCollectionTemplateDTO.getErpDataCollectionTemplateSectionDTOS().forEach(erpDataCollectionTemplateSectionDTO -> {
                if(!Utils.isNullOrEmpty(erpDataCollectionTemplateSectionDTO.getSectionNo()) && displaySectionMap.containsKey(erpDataCollectionTemplateSectionDTO.getSectionNo())) {
                    erpDataCollectionTemplateSectionDTO.setDisplaySection(false);
                } else {
                    erpDataCollectionTemplateSectionDTO.setDisplaySection(true);
                }
            });
        }
        return erpDataCollectionTemplateDTO;
    }

    public Mono<ApiResult> delete(int id, String userId) {
        return dataCollectionTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
    }
}
