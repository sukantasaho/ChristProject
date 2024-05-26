package com.christ.erp.services.dto.student.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirstYearStudentDTO {
    private Integer studentApplnEntriesId;
    private Integer batchId;
    private Integer specializationId;
    private String modeOfStudy;
    private Integer yearNo;
    private Integer acaDurationId;
    private Integer acaDurationDetailId;
    private Integer admissionCategoryId;
    private Integer campusId;
}
