package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter

public class EmpLevelsAndPromotionsDTO {

	private int id;
	private LocalDate effectiveDateOfPromotion;
	private String remarks;
	private boolean promotionApproved;
	private SelectDTO empDesignation;
	private SelectDTO empPayScaleLevel;
	private SelectDTO cell;
	private EmployeeProfileLetterDTO employeeProfileLetterDTO;
}
