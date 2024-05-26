package com.christ.erp.services.dto.student.common;

import com.christ.erp.services.dto.common.ModelBaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class StudentPersonalDataDTO extends ModelBaseDTO {

	public StudentPersonalDataAddressDTO studentPersonalDataAddressDTO;
    
	private int id;
	private StudentPersonalDataAddtnlDTO studentPersonalDataAddtnlDTO;
	
}
