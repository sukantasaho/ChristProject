package com.christ.erp.services.dto.admission.applicationprocess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmSelectionProcessPlanDTO extends ModelBaseDTO {
	
    public ExModelBaseDTO acadamicYear;
    public String sessionname;
    public Boolean indiaoroutsideindia;
    public LocalDateTime applicationopenfrom;
    public LocalDateTime applicationopentill;
    public LocalDateTime selectionprocessstartdate;
    public LocalDateTime selectionprocessenddate;
    public LocalDateTime resultdeclarationdate;
    public LocalDateTime lastDateForFeePayment;
    public LocalDateTime lastdateofadmission;
    public List<ProgramPreferenceDTO> programWithPreferance;
    public AdmSelectionProcessPlanDetailDTO details;
    private List<SelectDTO> admIntakeBatch;
    private SelectDTO admissionType;
    private List<AdmSelectionProcessPlanProgrammeDTO> admSelectionProcessPlanProgrammeList;
}
