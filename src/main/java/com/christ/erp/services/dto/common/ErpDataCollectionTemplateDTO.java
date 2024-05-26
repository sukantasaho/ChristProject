package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErpDataCollectionTemplateDTO{
    private int id;
    private String templateName;
    private SelectDTO templateFor;
    private String instructions;
    private List<ErpDataCollectionTemplateSectionDTO> erpDataCollectionTemplateSectionDTOS;
}
