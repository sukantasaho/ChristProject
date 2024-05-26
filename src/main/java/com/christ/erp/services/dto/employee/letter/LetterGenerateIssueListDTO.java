package com.christ.erp.services.dto.employee.letter;

import com.christ.erp.services.dto.common.SelectDTO;

public class LetterGenerateIssueListDTO {
	public String letterRequestId;
	public String employeeId;
	public String employeeNumber;
	public String employeeName;
	public String letterType;
	public String requestReason;
	public String requestDate;
	public SelectDTO status;
	public String department;
	public String campus;
	public boolean isSelected;
	public String issueDate;
	public String appliedDate;
	public String rejectReason;
	
}