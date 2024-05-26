package com.christ.erp.services.dto.employee.salary;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class EmpDailyWageSlabDTO extends ModelBaseDTO {

	public ExModelBaseDTO empCategory;
	public ExModelBaseDTO jobCategory;
	public List<EmpDailyWageSlabDetailsDTO> empDailyWageDetails;

}
