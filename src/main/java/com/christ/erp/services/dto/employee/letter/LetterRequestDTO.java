package com.christ.erp.services.dto.employee.letter;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class LetterRequestDTO extends ModelBaseDTO{
	public ExModelBaseDTO letterType;
	public ExModelBaseDTO reasonType;
	public String appliedDate;
	public String details;
	public String status;
	public String helpText;
	public String reasonText;
	public Boolean isReasonText;
	public String processCode;
	public Boolean isStatusCompleted;
}
