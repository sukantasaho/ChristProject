package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpInterviewExternalPanelDocumentUploadDTO {
	public String url;
	public String fileName;
	public String extension;
	public char recordStatus;
	
	  
    //-----------
	public String id;
	public Boolean newFile;
 	private String originalFileName;
 	private String uniqueFileName;
 	private String processCode;
 	private String actualPath;
    //--------
}
