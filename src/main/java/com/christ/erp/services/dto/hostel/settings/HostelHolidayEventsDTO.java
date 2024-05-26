package com.christ.erp.services.dto.hostel.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.common.HostelProgrammeDetailsDTO;

public class HostelHolidayEventsDTO extends ModelBaseDTO {

	public ExModelBaseDTO academicYear;
	public ExModelBaseDTO hostel;
    public String eventType;
    public String description;
    public String fromDate;
    public String toDate;
    public String fromSession;
    public String toSession;
    public List<HostelProgrammeDetailsDTO> campusProgrammesMapping;
    public String[] checked;
	public List<Integer> editIds; 
}

