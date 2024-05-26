package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmpEligibilityTestDTO {
    private int id;
    private SelectDTO exam;
    private Integer testYear;
    private List<EmpEligibilityTestDocumentDTO> empEligibilityTestDocumentDTOList;

    public EmpEligibilityTestDTO(){

    }
    public EmpEligibilityTestDTO(int id, Integer examId, String examName,Integer testYear){
        this.id = id;
        this.testYear = testYear;
        if(!Utils.isNullOrEmpty(examId)) {
            this.exam = new SelectDTO();
            this.exam.setLabel(examName);
            this.exam.setValue(Integer.toString(examId));
        }
    }
}
