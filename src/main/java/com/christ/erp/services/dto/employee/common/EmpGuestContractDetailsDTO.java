package com.christ.erp.services.dto.employee.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.christ.erp.services.common.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpGuestContractDetailsDTO {
	public String id;
	public String payScaleDetailsId;
	public String startDate;
	public String endDate;
	public String semester;
	public String workHourPerWeek;
	public String comments;
	public String guestReferredBy;
	public String isCurrentDetails;
	public String honararium;
	public String contractStartDate;
	public String contractEndDate;
	private String erpCampusDepartment;
	private String contractEmpLetterNo;
	private String guestContractRemarks;
	private int empId;
	
	public EmpGuestContractDetailsDTO(int empId,String departmentName, String campusName, String guestTutoringSemester, BigDecimal guestWorkingHoursWeek, String guestReferredBy
			                          ,LocalDate  contractEmpStartDate, LocalDate contractEmpEndDate, String contractEmpLetterNo, String guestContractRemarks) {
		this.empId = empId;
		this.erpCampusDepartment = departmentName+"("+campusName+")";
		this.semester = guestTutoringSemester;
		this.workHourPerWeek = !Utils.isNullOrEmpty(guestWorkingHoursWeek)?String.valueOf(guestWorkingHoursWeek):"";
		this.guestReferredBy = guestReferredBy;
		this.contractStartDate = !Utils.isNullOrEmpty(contractEmpStartDate)? Utils.convertLocalDateToStringDate(contractEmpStartDate):"";
		this.contractEndDate = !Utils.isNullOrEmpty(contractEmpEndDate)? Utils.convertLocalDateToStringDate(contractEmpEndDate):"";
		this.contractEmpLetterNo = contractEmpLetterNo;
		this.guestContractRemarks = guestContractRemarks;
	}
}
