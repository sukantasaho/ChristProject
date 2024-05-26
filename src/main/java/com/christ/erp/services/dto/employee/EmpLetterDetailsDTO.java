package com.christ.erp.services.dto.employee;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class EmpLetterDetailsDTO {
    private Integer id;
    private SelectDTO letterTypeDTO;
    private Integer letterRefNo;
    private LocalDate letterDate;
    private CommonUploadDownloadDTO letterUploadDTO;

    public EmpLetterDetailsDTO(){

    }
    public EmpLetterDetailsDTO(Integer id, Integer letterTypeId,  String letterTypeName, Integer letterRefNo, LocalDate letterDate,
                               String letterFileNameUnique, String letterFileNameOrg, String letterProcessCode){
        this.id = id;
        if(!Utils.isNullOrEmpty(letterTypeId)){
            this.letterTypeDTO = new SelectDTO();
            this.letterTypeDTO.value = Integer.toString(letterTypeId);
            this.letterTypeDTO.label = letterTypeName;
        }
        this.letterRefNo = letterRefNo;
        this.letterDate = letterDate;
        CommonUploadDownloadDTO letterUploadDTO = null;
        if(!Utils.isNullOrEmpty(letterFileNameUnique)) {
            letterUploadDTO = new CommonUploadDownloadDTO();
            letterUploadDTO.setActualPath(letterFileNameUnique);
            letterUploadDTO.setOriginalFileName(letterFileNameOrg);
            letterUploadDTO.setProcessCode(letterProcessCode);
            letterUploadDTO.setNewFile(false);
        }
        this.letterUploadDTO = letterUploadDTO;
    }
}
