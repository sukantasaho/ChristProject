package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleMatrixDetailDTO;
import com.christ.erp.services.dto.employee.salary.PayScaleMappingDTO;
import com.christ.erp.services.dto.employee.salary.PayScaleMappingItemDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppointmentApprovalDTO extends ModelBaseDTO {
	
	public String applicationnumberlength;
	public String name;
	public String applicationNumber;
	public ExModelBaseDTO category;
	public ExModelBaseDTO jobCategorie;
	public ExModelBaseDTO campus;
	public ExModelBaseDTO department;
	public ExModelBaseDTO designation;
	public ExModelBaseDTO designationForStaffAlbum;
	public ExModelBaseDTO title;
	public ExModelBaseDTO employeeGroup;
	public ExModelBaseDTO grade;
	public ExModelBaseDTO revisedYear;
	public ExModelBaseDTO level;
	public ExModelBaseDTO cell;
	public ExModelBaseDTO payScale;
	public ExModelBaseDTO basicPay;
	public String radioButton;
	public String grossyPay;
	public String perHour;
	public String perCourse;
	public String dailyAmount;
	public List<SalaryComponentDTO> payScaleDetailsComponents; 
	public String consolidateAmount;
}
