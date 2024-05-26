package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpProfileWorkExpDocDTO {
    private int id;
    private int workExperienceId;
    private CommonUploadDownloadDTO document;

    public EmpProfileWorkExpDocDTO(){

    }
    public EmpProfileWorkExpDocDTO(int id, Integer workExperienceId , String fileNameUnique, String fileNameOriginal, String uploadProcessCode){
        this.id = id;
        this.workExperienceId = workExperienceId;
        if(!Utils.isNullOrEmpty(fileNameUnique)) {
            CommonUploadDownloadDTO commonUploadDownloadDTO = new CommonUploadDownloadDTO();
            commonUploadDownloadDTO.setUniqueFileName(fileNameUnique);
            commonUploadDownloadDTO.setOriginalFileName(fileNameOriginal);
            commonUploadDownloadDTO.setProcessCode(uploadProcessCode);
            commonUploadDownloadDTO.setNewFile(false);
            this.document = commonUploadDownloadDTO;
        }
     }

}
