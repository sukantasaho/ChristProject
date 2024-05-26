package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpApplnEducationalDetailsDocumentsDTO {

    public int empApplnEducationalDetailsDocumentsId;
    public int empApplnEducationalDetailsId;
    public String educationalDocumentsUrl;
    //------
    private int id;
	private String originalFileName;
	private String uniqueFileName;
	public Boolean newFile;
	private String processCode;
	private String actualPath;
	private String tempPath;
	//---------
	
}
