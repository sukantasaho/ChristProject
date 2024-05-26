package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class EmpQualificationTabDTO {
    private int empId;
    private SelectDTO highestQualification;
    private String highestQualificationForAlbum;
    private List<EmpProfileEducationalDetailsDTO> empEducationalDetailsDTOList;
    private List<FileUploadDownloadDTO> uniqueFileNameList;
    private List<EmpEligibilityTestDTO> empEligibilityTestDTOList;
    public EmpQualificationTabDTO(){

    }
    public EmpQualificationTabDTO(Integer empId, Integer highestQualificationId, String highestQualificationName, String highestQualificationForAlbum){
        this.empId = empId;
        if(!Utils.isNullOrEmpty(highestQualificationId)) {
            this.highestQualification = new SelectDTO();
            this.highestQualification.setLabel(highestQualificationName);
            this.highestQualification.setValue(Integer.toString(highestQualificationId));
        }
        this.highestQualificationForAlbum = highestQualificationForAlbum;
    }
}
