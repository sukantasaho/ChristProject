package com.christ.erp.services.dto.admission.settings;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmissionSelectionProcessTypeDTO {
	
	public String id;
    public String selectionProcessName;
    public String mode;
    public Boolean isShortist; 
    public char recordStatus;
    public List<AdmissionSelectionProcessTypeDetailsDTO> levels;
    private String admitCardDisplayName;
}
