package com.christ.erp.services.dto.hostel.fineanddisciplinary;

import java.time.LocalDate;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDisciplinaryActionsTypeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelDisciplinaryActionsDTO {

	private int id;
	private SelectDTO academicYearDTO;
	private HostelAdmissionsDTO hostelAdmisionDTO;
	private HostelDisciplinaryActionsTypeDTO hostelDisciplinaryActionsTypeDTO;
	private HostelFineCategoryDTO  fineCategoryDTO;
	private LocalDate disciplinaryActionDate;
	private String remarks;
	private HostelFineEntryDTO hostelFineEntryDTO;
}
