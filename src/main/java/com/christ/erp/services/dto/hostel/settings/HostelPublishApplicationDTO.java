package com.christ.erp.services.dto.hostel.settings;

import com.christ.erp.services.dto.employee.settings.ErpTemplateDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelPublishApplicationDTO {

    public String id;
    public String academicyearId;
    public String hostelId;
    public String isOpenForFirstYear;
    public String isStatusForFirstYear;
    public String isOpenForSubsequentYear;
    public String isStatusForSubsequentYear;
    public String onlineApplicationPrefix;
    public String onlineApplicationStartNo;
    public String offlineApplicationPrefix;
    public String offlineApplicationStartNo;
    public String instructionTemplateId;
    public String declarationTemplateId;
    public String academicYearText;
    public String hostelText;
    public String onlineApplicationEndNo;
    public String offlineApplicationEndNo;
    private Integer onlineApplicationCurrentNo;
    private ErpTemplateDTO declarationTemplate;
    
}
