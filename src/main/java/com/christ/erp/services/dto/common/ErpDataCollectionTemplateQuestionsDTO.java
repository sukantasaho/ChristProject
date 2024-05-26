package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class ErpDataCollectionTemplateQuestionsDTO implements Comparable<ErpDataCollectionTemplateQuestionsDTO> {
    private int id;
    private Integer displayLevel;
    private Integer displayOrder;
    private String feedbackQuestion;
    private Boolean isMandatory;
    private SelectDTO questionType;
    private Boolean goToQuestionBasedAnswer;
    private Boolean isMultipleFileUpload;
    private Boolean isAllowHalfIcon;
    private SelectDTO ratingScale;
    private SelectDTO ratingShape;
    private SelectDTO ratingColor;
    private Boolean isAddRatingLabel;
    private Integer sliderMinValue;
    private Integer sliderMaxValue;
    private Integer sliderInterval;
    private Boolean isAddImageLabel;
    private boolean open;
    private SelectDTO erpDataCollectionTemplateSectionDTO;
    private Integer sectionNo;
    private String sectionValue;
    private List<ErpDataCollectionTemplateQuestionsOptionsDTO> erpDataCollectionTemplateQuestionsOptionsDTOS;

    @Override
    public int compareTo(@NotNull ErpDataCollectionTemplateQuestionsDTO dto) {
        return this.displayOrder.compareTo(dto.displayOrder);
    }
}
