package com.christ.erp.services.dto.employee.onboarding;

import java.util.List;

public class EmpDocumentChecklistMainDTO {

	public int id;
    public String headingName;
    public String headingOrder;
    public String isForeignNationalDocumentChecklist;
    public Boolean recordStatus;
    public List<EmpDocumentChecklistSubDTO> checklistDocuments;
}
