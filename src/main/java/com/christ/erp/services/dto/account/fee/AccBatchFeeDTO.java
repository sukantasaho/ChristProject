package com.christ.erp.services.dto.account.fee;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccBatchFeeDTO {
	private int id;
	private SelectDTO feeCollectionSet;
	private SelectDTO erpSpecializationDTO;
	private SelectDTO batchYearDTO;
	private SelectDTO programmeDTO;
	private SelectDTO acaBatchDTO;
	private List<AccBatchFeeDurationsDTO> accBatchFeeDurationsDTOList;
	private String batchYearAndBatchName;
	private String programAndCampus;
	
}
