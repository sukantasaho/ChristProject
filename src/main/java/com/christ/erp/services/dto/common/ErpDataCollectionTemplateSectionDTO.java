package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class ErpDataCollectionTemplateSectionDTO implements Comparable<ErpDataCollectionTemplateSectionDTO>{
    private int id;
    private Integer sectionNo;
    private String sectionValue;
    private SelectDTO erpDataCollectionTemplateDTO;
    private boolean displaySection;
    private List<ErpDataCollectionTemplateQuestionsDTO> erpDataCollectionTemplateQuestionsDTOS;

    @Override
    public int compareTo(@NotNull ErpDataCollectionTemplateSectionDTO dto) {
        return this.sectionNo.compareTo(dto.sectionNo);
    }
}
