package com.christ.erp.services.dto.hostel.settings;

import com.christ.erp.services.dto.common.LookupItemDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelBlockUnitDetailsDTO {

	public String id;
	public String sequenceNo;
	public LookupItemDTO erpUser;
	public String hostelPosition;
	public String positionMobileNo;
	public String positionEmail;
	public String positionPhoneNo;
	public String isSentSmsEmailMorningAbsence;
	public String isSentSmsEmailEveningAbsence;
}
