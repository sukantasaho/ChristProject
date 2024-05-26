package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.employee.common.EmpMajorAchievementsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmpWorkExperienceTabDTO {
    private int empId;
    private List<EmpProfileWorkExperienceDTO> EmpProfileWorkExperienceDTOList;
    private List<EmpProfileMajorAchievementsDTO> empMajorAchievementsDTOList;
    private List<FileUploadDownloadDTO> uniqueFileNameList;
}
