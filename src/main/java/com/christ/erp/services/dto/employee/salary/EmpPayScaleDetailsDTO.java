package com.christ.erp.services.dto.employee.salary;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpPayScaleDetailsDTO {
	
	private int id;
	private int empId;
	private LocalDate payScaleEffectiveDate;
	private String payScaleComments;
	private String payScaleType;
	private String payScale;
	private BigDecimal wageRatePerType;
	private BigDecimal grossPay;
	
	
	public EmpPayScaleDetailsDTO(int id,int empId,LocalDate payScaleEffectiveDate,String payScaleComments,String payScaleType, String payScale,BigDecimal wageRatePerType,BigDecimal grossPay) {
		this.id= id;
		this.empId = empId;
		this.payScaleEffectiveDate = payScaleEffectiveDate;
		this.payScaleComments = payScaleComments;
		this.payScaleType =  payScaleType;
		this.payScale = payScale;
		this.wageRatePerType = wageRatePerType;
		this.grossPay = grossPay;
	}

}
