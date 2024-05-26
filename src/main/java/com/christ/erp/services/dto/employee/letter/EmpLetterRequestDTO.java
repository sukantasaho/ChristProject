package com.christ.erp.services.dto.employee.letter;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class EmpLetterRequestDTO extends ExModelBaseDTO {
	public String value;
    public String label;
	public String id;
	public ExModelBaseDTO letterTemplate;
	public String letterName;
	public String startNo;
	public String letterHelpText;
	public String letterNoPrefix;
	public Boolean availableOnline;
	public char recordStatus;
	

}
