package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.dto.common.LookupItemDTO;

public class WorkTimeDetailsDTO {
	public LookupItemDTO generalTimeZone;
    public String isHolidayTimeZoneApplicable;
    public LookupItemDTO holidayTimeZone;
    public String isVacationTimeZoneApplicable;
    public LookupItemDTO vacationTimeZone;
    public String isDutyRosterApplicable;
}
