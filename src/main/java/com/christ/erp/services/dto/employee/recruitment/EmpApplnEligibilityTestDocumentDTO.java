package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpApplnEligibilityTestDocumentDTO {

    public int empApplnEligibilityTestDocumentId;
    public int empApplnEligibilityTestId;
    public String eligibilityDocumentUrl;
    
    //-----------
    private int id;
 	private String originalFileName;
 	private String uniqueFileName;
 	public Boolean newFile;
 	private String processCode;
 	private String actualPath;
 	private String tempPath;
    //--------
}
