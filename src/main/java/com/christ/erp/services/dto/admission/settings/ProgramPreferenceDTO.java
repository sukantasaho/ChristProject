package com.christ.erp.services.dto.admission.settings;

import java.util.List;
import java.util.Set;

import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDetailDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionGeneralDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramPreferenceDTO extends ExModelBaseDTO {
	
	public String value;
	public String label;
	public String programId;
	public char preferenceOption;
	public String preferenceId;
	public String campusMappingId;
	private Integer parentId;
	public String acaBatchId;
}
