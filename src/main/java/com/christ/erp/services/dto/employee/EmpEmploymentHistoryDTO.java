package com.christ.erp.services.dto.employee;

import java.time.LocalDate;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpEmploymentHistoryDTO {
	private String employeeCategory;
	private String jobCategory;
	private String department;
	private String campus;
	private String jobTitle;
	private String designation;
	private LocalDate periodFrom;
	private LocalDate periodTo;


	public  EmpEmploymentHistoryDTO(){

	}
	public  EmpEmploymentHistoryDTO(String employeeCategory, String jobCategory, String department, String campus, String jobTitle,
									String designation,	LocalDate periodFrom, LocalDate periodTo){
		this.employeeCategory = employeeCategory;
		this.jobCategory = jobCategory;
		this.department = department;
		this.campus = campus;
		this.jobTitle = jobTitle;
		this.designation = designation;
		this.periodFrom = periodFrom;
		this.periodTo = periodTo;

	}

}
