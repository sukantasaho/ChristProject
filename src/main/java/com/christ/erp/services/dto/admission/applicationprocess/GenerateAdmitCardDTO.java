package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateAdmitCardDTO {
    private Integer id;
    private SelectDTO academicYear;
    private SelectDTO session;
    private SelectDTO selectionProcessType;
    private String excelFileName;
    private String sessionStartDateEndDate;
    private String applicationRecieved;
    private String admitCardGenerated;
    private String admitCardPublished;
    private SelectDTO erpProgrammeDto;
    private String selectionProcessDate;
}
