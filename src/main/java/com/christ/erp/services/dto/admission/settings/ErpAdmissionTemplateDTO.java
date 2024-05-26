package com.christ.erp.services.dto.admission.settings;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpAdmissionTemplateDTO {
	private int id;
	private SelectDTO templateType;
	private String templateGroupName;
    private String templateGroupCode;
    private SelectDTO campus;
    private SelectDTO programLevel;
    private int templateId;
    private String templateName;
    private String templateCode;
    private SelectDTO templateFor;
    private SelectDTO programGrid;
    private String fromName;
    private String mailSubject;
    private String templateDescription;
    private String templateContent;
    private String availableTags;
    private List<SelectDTO> programSelectedForTemplate;
    private boolean verified;
    private String templateIdForSms;
}

