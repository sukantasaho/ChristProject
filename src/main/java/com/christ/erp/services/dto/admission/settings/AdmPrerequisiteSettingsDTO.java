package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdmPrerequisiteSettingsDTO {
//	public int id;
//	public ExModelBaseDTO academicYear;
//	public ExModelBaseDTO programme;
//	private SelectDTO admPrerequisiteExamDTO;
//	public List<PrerequisiteSettingsDTO> prerequisitesettings;
//	private List<AdmPreRequisiteSettingPeriodDTO> prerequisitesettingsDTO;
//	private Boolean isEdit;
//	private SelectDTO admAdmissionTypeDTO;
//	private int id;
    private int id;
    private SelectDTO erpAcademicYearDTO;
    private SelectDTO erpProgrammeDTO;
    private Boolean isExamMandatory;
    private SelectDTO admAdmissionTypeDTO;
    private Boolean isScoreDetailsMandatory;
    private Boolean isDocumentUploadMandatory;
    private Boolean isRegisterNoMandatory;
    public List<AdmPrerequisiteSettingsDetailsDTO> admPrerequisiteSettingsDetailsDTOList;
}
