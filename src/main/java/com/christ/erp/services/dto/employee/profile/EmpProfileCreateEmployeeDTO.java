package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpProfileCreateEmployeeDTO {
    int id;
    private Integer applicationNo;
    private String employeeId;
    private String applicantName;
    private SelectDTO campus;
    private SelectDTO department;
    private SelectDTO specialisation;
    private SelectDTO employeeCategory;
    private SelectDTO jobCategory;

    public EmpProfileCreateEmployeeDTO(){

    }
    public EmpProfileCreateEmployeeDTO(int id, Integer applicationNo, String applicantName, Integer campusId, String campusName,
            Integer categoryId, String categoryName, Integer jobCategoryId, String employeeJobName, Integer specialisationId, String specialisationName ){
        this.id = id;
        this.applicationNo = applicationNo;
        this.applicantName = applicantName;
        if(!Utils.isNullOrEmpty(campusId)) {
            this.campus = new SelectDTO();
            this.campus.setLabel(campusName);
            this.campus.setValue(Integer.toString(campusId));
        }
        if(!Utils.isNullOrEmpty(categoryId)) {
            this.employeeCategory = new SelectDTO();
            this.employeeCategory.setLabel(categoryName);
            this.employeeCategory.setValue(Integer.toString(categoryId));
        }
        if(!Utils.isNullOrEmpty(jobCategoryId)) {
            this.jobCategory = new SelectDTO();
            this.jobCategory.setLabel(employeeJobName);
            this.jobCategory.setValue(Integer.toString(jobCategoryId));
        }
        if(!Utils.isNullOrEmpty(specialisationId)){
            this.specialisation = new SelectDTO();
            this.specialisation.setLabel(specialisationName);
            this.specialisation.setValue(Integer.toString(specialisationId));
        }
    }
}
