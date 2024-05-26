package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class EmpInterviewExternalPanelistDTO {
	public String id;
	public String name;
	public String email;
	public ExModelBaseDTO countryCode;	
	public String mobile;
	public String uploadDocumentUrl;
	public List<EmpInterviewExternalPanelDocumentUploadDTO> empDocumentImages;
}
