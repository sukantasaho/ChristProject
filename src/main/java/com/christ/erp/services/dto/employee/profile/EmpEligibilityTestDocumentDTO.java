package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpEligibilityTestDocumentDTO {
    private int id;
    private int documentId;
    private CommonUploadDownloadDTO document;
    public EmpEligibilityTestDocumentDTO(){

    }
    public EmpEligibilityTestDocumentDTO(int id, Integer documentId, String fileNameUnique, String fileNameOriginal, String uploadProcessCode){
        this.id = id;
        this.documentId  = documentId;
        if(!Utils.isNullOrEmpty(fileNameUnique)) {
            CommonUploadDownloadDTO commonUploadDownloadDTO = new CommonUploadDownloadDTO();
            commonUploadDownloadDTO.setActualPath(fileNameUnique);
            commonUploadDownloadDTO.setOriginalFileName(fileNameOriginal);
            commonUploadDownloadDTO.setProcessCode(uploadProcessCode);
            commonUploadDownloadDTO.setNewFile(false);
            this.document = commonUploadDownloadDTO;
        }
    }
}
