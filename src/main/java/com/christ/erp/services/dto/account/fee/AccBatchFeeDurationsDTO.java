package com.christ.erp.services.dto.account.fee;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccBatchFeeDurationsDTO {
	private int id;
	private SelectDTO acaDurationDTO;
	private int yearNo;
	private SelectDTO academicYearDTO;
	private List<AccBatchFeeDurationsDetailsDTO> accBatchFeeDurationsDetailsDTOList;
}
