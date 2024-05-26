package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeProfileAddressDTO {
	public String addressLineOne;
	public String addressLineTwo;
	public LookupItemDTO country;
	public String pincode;
	public LookupItemDTO state;
	public boolean sameAsPermenent;
	private String otherState;
	private SelectDTO district;
}
