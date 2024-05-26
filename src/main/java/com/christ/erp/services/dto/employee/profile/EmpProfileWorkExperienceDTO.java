package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EmpProfileWorkExperienceDTO {
    private int id;
    private SelectDTO workExperienceType;
    private SelectDTO subjectCategory;
    private Boolean isPartTime;
    private String designation;
    private LocalDate workExperienceFromDate;
    private LocalDate workExperienceToDate;
    private Integer workExperienceYears;
    private Integer workExperienceMonth;
    private String institution;
    private Boolean isAcademic;
    private List<EmpProfileWorkExpDocDTO> EmpProfileWorkExpDocDTOList;
    public EmpProfileWorkExperienceDTO(){

    }
    public EmpProfileWorkExperienceDTO(int id, Integer workExperienceTypeId, String workExperienceTypeName, Integer subjectCategoryId, String subjectCategory, Boolean isPartTime,
                                       String designation, LocalDate workExperienceFromDate, LocalDate workExperienceToDate,
                                       Integer workExperienceYears, Integer workExperienceMonth, String institution, Boolean isAcademic){
        this.id = id;
        if(!Utils.isNullOrEmpty(workExperienceTypeId)) {
            this.workExperienceType = new SelectDTO();
            this.workExperienceType.setLabel(workExperienceTypeName);
            this.workExperienceType.setValue(Integer.toString(workExperienceTypeId));
        }
        if(!Utils.isNullOrEmpty(subjectCategoryId)) {
            this.subjectCategory = new SelectDTO();
            this.subjectCategory.setLabel(subjectCategory);
            this.subjectCategory.setValue(Integer.toString(subjectCategoryId));
        }
        this.isPartTime = isPartTime;
        this.designation = designation;
        this.workExperienceFromDate = workExperienceFromDate;
        this.workExperienceToDate = workExperienceToDate;
        this.workExperienceYears = workExperienceYears;
        this.workExperienceMonth = workExperienceMonth;
        this.institution = institution;
        this.isAcademic = isAcademic;
    }
}
