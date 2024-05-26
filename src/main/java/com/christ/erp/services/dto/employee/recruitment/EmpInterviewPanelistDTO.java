package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class EmpInterviewPanelistDTO {
	public String id;
	public ExModelBaseDTO academicYear;
	public ExModelBaseDTO location;
	public ExModelBaseDTO department;
	public List<ExModelBaseDTO> internalPanelList;
	public List<ExModelBaseDTO> externalPanelList;
	public List<ExModelBaseDTO> universityExternalPanelList;
	
}
