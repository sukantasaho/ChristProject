package com.christ.erp.services.dto.employee.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpMajorAchievementsDTO {
	public String id;
	public String name;
	private int empId;
	
	public EmpMajorAchievementsDTO(int empId, String achievements) {
		this.empId = empId;
		this.name = achievements;
	}
}
