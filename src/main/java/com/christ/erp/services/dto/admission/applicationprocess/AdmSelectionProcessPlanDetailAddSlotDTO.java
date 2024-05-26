package com.christ.erp.services.dto.admission.applicationprocess;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDetailsDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessPlanDetailAddSlotDTO extends ModelBaseDTO implements Comparable<AdmSelectionProcessPlanDetailAddSlotDTO> {
	
	public String parentId;
	public LocalDate selectionprocessdate;
	public String slot;
	public String time;
	public String processOrder;
	public ExModelBaseDTO selectionProcessName;
	public String mode;
	public List<ProgramPreferenceDTO> programWithPreferance;
	public List<AdmSelectionProcessPlanDetailProgDTO> detailProg;
	public List<AdmSelectionProcessPlanCenterBasedDTO> citieslist;
	public Boolean isShortListFlag;
	public String totalStudentFilled;
	public List<AdmissionSelectionProcessTypeDetailsDTO> admissionSelectionProcessTypeDetailsList;
	public Integer detailId;
	@Override
	public int compareTo(AdmSelectionProcessPlanDetailAddSlotDTO obj) {
		return this.id.compareTo(obj.id);
	}
}
