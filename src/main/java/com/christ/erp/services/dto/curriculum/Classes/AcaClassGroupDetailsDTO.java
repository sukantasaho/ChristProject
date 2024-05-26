package com.christ.erp.services.dto.curriculum.Classes;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AcaClassGroupDetailsDTO {
	
	private int id;
	private SelectDTO acaClassGroupDTO;
	private SelectDTO acaClassDTO;
	private SelectDTO acaCourseSessionwiseDTO;
	private List<AcaClassGroupStudentsDTO> acaClassGroupStudentsDTO;

}
