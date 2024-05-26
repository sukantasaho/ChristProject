package com.christ.erp.services.dto.curriculum.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentMissionVisionDetailsDTO implements Comparable<DepartmentMissionVisionDetailsDTO>
{
	
	public String id;
	public String missionReferenceNumber;
	public String missionCategory;
	public String missionStatement;
	@Override
	public int compareTo(DepartmentMissionVisionDetailsDTO dto) {
		return  this.missionReferenceNumber.compareTo(dto.missionReferenceNumber);	
	}
}
