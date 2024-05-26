package com.christ.erp.services.dto.employee.common;

import java.time.LocalDate;
import com.christ.erp.services.common.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpResignationDTO {
	public String resignationId;
	public String submissionDate;
	public String dateOfLeaving;
	public String hodRecomendedRelievingDate;
	public String reasonForLeaving;
	public String isServingNoticePeriod;
	public String referenceNo;
	public String relieavingOrderDate;
	public String poRemarks;
	private int empId;
	
	public EmpResignationDTO(int empId,LocalDate submissionDate,LocalDate dateOfLeaving,LocalDate hodRecomendedRelievingDate,LocalDate relievingDate, String resignationName
							,String reasonOther, String poRemarks) {
		this.empId = empId;
		this.submissionDate = !Utils.isNullOrEmpty(submissionDate)? Utils.convertLocalDateToStringDate(submissionDate):"";
		this.dateOfLeaving = !Utils.isNullOrEmpty(dateOfLeaving)? Utils.convertLocalDateToStringDate(dateOfLeaving):"";
		this.hodRecomendedRelievingDate = !Utils.isNullOrEmpty(hodRecomendedRelievingDate)? Utils.convertLocalDateToStringDate(hodRecomendedRelievingDate):"";
		this.relieavingOrderDate = !Utils.isNullOrEmpty(relievingDate)? Utils.convertLocalDateToStringDate(relievingDate):"";
		this.reasonForLeaving = !Utils.isNullOrEmpty(resignationName) ? resignationName : reasonOther;
		this.poRemarks = poRemarks;
	}
	
}
