package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpAcademicYearDetailsDTO {

    private Integer id;
    private ErpCampusDTO campus;
    private ErpAcademicYearDTO academicYear;
    private Boolean isAcademicYearCurrent;
    private String academicYearStartDate;
    private String academicYearEndDate;
}
