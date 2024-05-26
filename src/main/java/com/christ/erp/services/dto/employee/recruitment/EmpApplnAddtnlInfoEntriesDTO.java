package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpApplnAddtnlInfoEntriesDTO {

	public String empApplnAddtnlInfoEntriesId;
	public String isResearchExperience;
	public List<EmpApplnAddtnlInfoHeadingDTO> researchEntriesHeadings;
	private Integer entriesId;
	private Integer parameterId;
	private String addtnlInfoValue;
	
	public EmpApplnAddtnlInfoEntriesDTO(Integer entriesId,Integer parameterId,String addtnlInfoValue) {
		this.entriesId = entriesId;
		this.parameterId = parameterId;
		this.addtnlInfoValue = addtnlInfoValue;
	}
}
