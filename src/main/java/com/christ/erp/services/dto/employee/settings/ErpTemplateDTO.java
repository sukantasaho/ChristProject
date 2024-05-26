package com.christ.erp.services.dto.employee.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ErpTemplateDTO {
	public String id;
    public SelectDTO process;
    public String templateCode;
    public String templateName;
    public String templateDescription;
    public String types;
    public String fromName;
    public String mailSubject;
    public String templateContent;
    public String availableTags;
    public String programName;
    public char recordStatus;
    public String templateId;
  }
