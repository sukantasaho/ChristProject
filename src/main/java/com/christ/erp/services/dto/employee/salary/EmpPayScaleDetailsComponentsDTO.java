package com.christ.erp.services.dto.employee.salary;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpPayScaleDetailsComponentsDTO {
	
	private int id;
	private int payScaleDetailsId;
	private int empPayScaleComponentsDisplayOrder;
	private BigDecimal empSalaryComponentValue;
	
	public EmpPayScaleDetailsComponentsDTO(Integer payScaleDetailsId, Integer salaryComponentDisplayOrder,BigDecimal empSalaryComponentValue) {
		this.payScaleDetailsId = payScaleDetailsId;
		this.empPayScaleComponentsDisplayOrder = salaryComponentDisplayOrder;
		this.empSalaryComponentValue = empSalaryComponentValue;
		
	}

}
