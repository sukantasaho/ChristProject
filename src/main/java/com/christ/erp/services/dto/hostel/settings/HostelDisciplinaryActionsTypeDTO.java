package com.christ.erp.services.dto.hostel.settings;

import java.util.List;

import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelDisciplinaryActionsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelDisciplinaryActionsTypeDTO {
	
	private int id;
	private String hostelDisciplinaryActions;
	private int fineAmount;
	private List<HostelDisciplinaryActionsDTO> hostelDisciplinaryActionsDTO;
}
