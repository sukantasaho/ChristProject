package com.christ.erp.services.dto.admission.applicationprocess;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessPlanDetailAddVenueDTO extends ModelBaseDTO implements Comparable<AdmSelectionProcessPlanDetailAddVenueDTO>{

	public String parentId;
	public LocalDate selectionprocessdate;
	public String processOrder;
	public ExModelBaseDTO selectionProcessName;
	public String mode;
	public Boolean isShortListFlag;
	public ExModelBaseDTO venue;
	public String avaliableseats;
	public String venueSelectionProcess;
	public String dateSelectionProcess;
	public List<ProgramPreferenceDTO> programWithPreferance;
	public List<AdmSelectionProcessPlanDetailProgDTO> detailProg;
	public List<AdmSelectionProcessPlanCenterBasedDTO> citieslist;
	public List<AdmSelectionProcessPlanDetailAllotmentDTO> timewithseats;
	public String secondRoundVenueSp;
	public String secondRoundDateSp;
	public String followSameVenue;
	public String secondRoundEligibility;
	public String totalStudentFilled;
	@Override
	public int compareTo(AdmSelectionProcessPlanDetailAddVenueDTO obj) {
		return this.id.compareTo(obj.id);
	}
	
}
