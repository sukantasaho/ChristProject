package com.christ.erp.services.dto.admission.settings;

import java.util.List;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmApplnNumbergeneratonDTO {

	public String id;
	public String year;
	public String onlinePrefix;
	public String onlineAppNoFrom;
	public String onlineAppNoTo;
	public String offlinePrefix;
	public String offlineAppNoFrom;
	public String offlineAppNoTo;
	private Integer onlineApplnCurrentNo;
	private Integer offlineApplnCurrentNo;
	public String programmes;
	public ErpAcademicYearDTO academicYear;
	public List<ProgramPreferenceDTO> selectedProgrammes;
	public List<ProgramPreferenceDTO> programWithPreference;
	public String campusOrLocationName;
}
