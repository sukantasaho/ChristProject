package com.christ.erp.services.dto.employee.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class EmployeeApplicationDTO {
	
	public String value;
    public String label;
    
    public List<EmployeeApplicationDetailsDTO> detailsDTO;

	/*---------- erp_qualification_level -------------*/
    public String qualificationLevelDegreeOrder;
    public boolean isAddMore;
    public boolean isMandatory;
    public boolean isStatusDisplay;
    public String boardType;
    public String qualificationLevelCode;
    
    /*---------- emp_employee_category -------------*/
    public boolean isEmployeeCategoryAcademic;
    public boolean isShowInAppln;
    
    /*---------- emp_appln_work_experience_type -------------*/
    public boolean isExperienceTypeAcademic;
    
    /*---------- emp_appln_addtnl_info_heading -------------*/
    public String employeeCategoryId;
    public String headingDisplayOrder;
    public boolean isTypeResearch;
    public String subjectCategoryId;
    public boolean showInAppln;
    
    public String jobCategoryCode;
    
}
