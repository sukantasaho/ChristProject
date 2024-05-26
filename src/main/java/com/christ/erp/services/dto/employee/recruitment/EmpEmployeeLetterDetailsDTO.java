package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpEmployeeLetterDetailsDTO {
	
	int id;
	public int empId;
	public String letterType;
	public Integer letterRefNo;
	public LocalDate letterDate;
	
	public EmpEmployeeLetterDetailsDTO(int empId, String letterType,Integer letterRefNo, LocalDate letterDate) {
		this.empId = empId;
		this.letterType = letterType; 
		this.letterRefNo = letterRefNo;
		this.letterDate = letterDate;
	}

}
