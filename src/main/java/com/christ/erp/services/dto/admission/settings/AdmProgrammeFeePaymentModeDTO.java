package com.christ.erp.services.dto.admission.settings;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmProgrammeFeePaymentModeDTO {
	
	 private int id;
	 private List<SelectDTO> accFeePaymentModeList;
}
