package com.christ.erp.services.dto.admission.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class AdmProgrammePreferenceSettingsDTO extends ModelBaseDTO implements Comparable<AdmProgrammePreferenceSettingsDTO>{

	public ExModelBaseDTO programmepreference;
    public ExModelBaseDTO campus;
    public ExModelBaseDTO location;
    public List<LookupItemDTO> locations;
    public List<LookupItemDTO> campusList;
    public String isSpecialisationRequired;
    public String campusMappingId;
	@Override
	public int compareTo(AdmProgrammePreferenceSettingsDTO obj) {
		return this.id.compareTo(obj.id);
	}
}
