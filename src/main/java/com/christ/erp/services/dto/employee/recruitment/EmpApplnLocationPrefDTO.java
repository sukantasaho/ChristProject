package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpApplnLocationPrefDTO {

	public Integer empApplnLocationPrefId;
	public Integer empApplnEntriesId;
	public Integer erpLocationId;
	private String locationName;
	
	public EmpApplnLocationPrefDTO(int id,int entriesId,String locationName) {
		this.empApplnLocationPrefId = id;
		this.empApplnEntriesId = entriesId;
		this.locationName = locationName;
	}
}
