package com.christ.erp.services.dto.hostel.fineanddisciplinary;

import java.time.LocalDate;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelFineEntryDTO {
	
	private int id;
	private SelectDTO hostelAdmissionsDTO;
	private HostelFineCategoryDTO fineCategoryDTO;
	private LocalDate date;
	private Integer fineAmount;
	private String remarks;
	private HostelAdmissionsDTO hostelAdmission;
	private Boolean isFixedAmount;
}
 