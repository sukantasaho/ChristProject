package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeProfileAdditionalInformation {
	public String id;
	public String isCurrentDetails;
	public String startDate;
	public String endDate;
	public LookupItemDTO semester;
	public String workHourPerWeek;
//	public LookupItemDTO campus;
//	public LookupItemDTO department;
	public LookupItemDTO paymentType;
	public String payscaleDetailsId;
	public String comments;
	public String honararium;
	private SelectDTO campusDepartment;
}
