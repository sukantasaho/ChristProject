package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpApplnAdvertisementImagesDTO {
	public String id;
	
	//------------
	private String originalFileName;
	private String uniqueFileName;
	private String folderListId;
	private String tempPath;
	private String actualPath;
	public Boolean newFile;
	private String processCode;
	//-----------
	public String url;
	public String fileName;
	public String extension;
	public char recordStatus;

}
