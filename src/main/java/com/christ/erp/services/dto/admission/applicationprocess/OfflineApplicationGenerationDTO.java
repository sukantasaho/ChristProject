package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dbobjects.admission.settings.AdmAdmissionTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmIntakeBatchDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeBatchDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class OfflineApplicationGenerationDTO {

    public Integer id;
    public String applicantName;
    public String applicationIssueDate;
    public String emailToSendApplicationLink;
    public SelectDTO mobileNoCountryCode;
    public String mobileNoToSendApplicationLink;
    public ErpAcademicYearDTO erpAcademicYearDTO;
    public SelectDTO admProgrammeBatchDTO;
    public SelectDTO erpProgrammeDTO;
    public SelectDTO admIntakeBatchDTO;
    public SelectDTO admAdmissionTypeDTO;
    public SelectDTO erpCampusDTO;
    public Boolean isFeePaid;
    public String recieptReferenceNo;
    public Boolean isSelectionProcessDateAllotted;
    public Boolean isLinkExpired;
    public String applicationLinkUrl;

}
