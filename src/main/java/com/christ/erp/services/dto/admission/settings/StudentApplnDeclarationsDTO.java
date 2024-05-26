package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentApplnDeclarationsDTO {
    private int id;
    private SelectDTO programmeLevelDTO;
    private SelectDTO erpAcademicYearDTO;
    private List<SelectDTO> campusProgrammeList;
    private SelectDTO campusProgrammeDTO;
    private List<StudentApplnDeclarationsDetailsDTO> studentApplnDeclarationsDetailsDTOList;
}
