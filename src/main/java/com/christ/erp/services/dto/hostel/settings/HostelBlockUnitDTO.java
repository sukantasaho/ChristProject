package com.christ.erp.services.dto.hostel.settings;

import java.util.List;
import java.util.Map;
import com.christ.erp.services.dto.common.LookupItemDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelBlockUnitDTO {

	public String id;
	public LookupItemDTO block;
	public LookupItemDTO hostel;
	public String hostelUnit;
	public String totalFloors;
	public String hostelFloorsNo;
	public String isLeaveSubmissionOnline; 
	public String leaveSubmissionNextDayBy;
	public String leaveSubmissionSaturdayBy;
	public Map<String,Boolean> parentsCommunicationOption; 
	public Map<String,Boolean> punchingExemptionForMorining; 
	public Map<String,Boolean> punchingExemptionForEvening;
	public List<HostelBlockUnitDetailsDTO> hostelBlockUnitDetails;
	private List<HostelFloorDTO> hostelFloorDTOSet;
}
