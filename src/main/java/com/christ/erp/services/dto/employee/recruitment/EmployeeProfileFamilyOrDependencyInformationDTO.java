package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

public class EmployeeProfileFamilyOrDependencyInformationDTO {
	public String familyBackgroundBrief;
	public EmpFamilyDetailsAddtnlDTO father;
	public EmpFamilyDetailsAddtnlDTO mother;
	public EmpFamilyDetailsAddtnlDTO spouse;
	public List<EmpFamilyDetailsAddtnlDTO> dependentList;
	public List<EmpFamilyDetailsAddtnlDTO> childrenList;
}
