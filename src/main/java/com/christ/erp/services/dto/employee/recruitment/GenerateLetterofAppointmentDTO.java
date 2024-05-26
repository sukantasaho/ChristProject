package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GenerateLetterofAppointmentDTO extends ModelBaseDTO {
	public String empName;
	public String empId;
	// public String doj;
	public String documentSubmissionStatus;
	public String generationStatus;
	private LocalDate doj;
}
