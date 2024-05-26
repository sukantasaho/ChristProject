package com.christ.erp.services.dto.admission.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmProgrammeSettingsDTO extends ModelBaseDTO{

	private ErpAcademicYearDTO erpAcademicYear;
	public SelectDTO programme;
	public SelectDTO accFeesHeads;
//	public List<String> paymentModes;
//	public ExModelBaseDTO currency;
	public ExModelBaseDTO termsCondition;
	public String noOfPreferenceRequiredInApplication;
	public char preferenceBasedOn;
//	public String indianapplicant;
//	public String internationalapplicant;
	public Boolean secondlanguage;
	public Boolean researchtopicrequired;
	public Boolean  workexperiencerequired;
	public Boolean  workexperiencemandatory;
	public String  minimumnoofmonthsexperiencerequired;
	public String modeofapplication;
	public ExModelBaseDTO printTemplate;
//    public List<AdmProgrammePreferenceSettingsDTO> programmepreferences;
    public List<AdmProgrammeQualificationSettingsDTO> qualificationsettings;
    public List<AdmProgrammeDocumentSettingsDTO> additionaldocumenttemplate;
    public List<SelectDTO> admProgrammeFeePaymentModeDTOList;
	private SelectDTO intakeBatch;
	private SelectDTO admissionType;
	private List<SelectDTO> campusList;
	private List<SelectDTO> locationList;
	private List<SelectDTO> specializationList;
	private Boolean specializationRequired;
	private Integer noOfpreference;
	private Boolean otherProgrammePref;
	private List<SelectDTO> advanceFeepayModes;
	private SelectDTO programmeMode;
	private String campusOrLocations;
	private Boolean isProgrammeModeDisplayed;
	private String heading;

}
