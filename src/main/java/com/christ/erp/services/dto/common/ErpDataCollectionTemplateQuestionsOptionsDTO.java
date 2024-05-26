package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class ErpDataCollectionTemplateQuestionsOptionsDTO {
    private int id;
    private BigDecimal optionValue;
    private String optionText;
    private String optionValueText;
    private Integer displayOrder;
    private boolean checked;
    private SelectDTO erpDataCollectionTemplateQuestion;
    private ErpDataCollectionTemplateSectionDTO erpDataCollectionTemplateSectionDTO;
}
