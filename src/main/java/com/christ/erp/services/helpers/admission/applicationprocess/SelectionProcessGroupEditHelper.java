package com.christ.erp.services.helpers.admission.applicationprocess;

import javax.persistence.Tuple;

import org.springframework.stereotype.Service;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessGroupEditDetailsDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;

@Service
public class SelectionProcessGroupEditHelper {
	
	public LookupItemDTO convertSelectionDateDBOtoDTO(Object obj) {
    	LookupItemDTO dto = new LookupItemDTO();
    	Object[] obj2 =  (Object[]) obj;
    	dto.label = Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(obj2[0].toString()));
    	dto.value = obj2[1].toString(); 
		return dto;
    }
	
	public SelectionProcessGroupEditDetailsDTO convertgetGroupApplicantsDataDBOtoDTO(Tuple obj) {
		SelectionProcessGroupEditDetailsDTO dto = new SelectionProcessGroupEditDetailsDTO();
		dto.setStudentEntrieId(obj.get(0) != null ? obj.get(0).toString() : "");
		dto.setApplicationNo(!Utils.isNullOrEmpty(obj.get(1)) ? obj.get(1).toString() : "");
		dto.setApplicantName(obj.get(2) != null ? obj.get(2).toString() : "");
		dto.setPrograme(obj.get(3) != null ? obj.get(3).toString() : "");

		// SocreEntry Screen
		dto.setPhotoUrl(obj.get(4) != null ? obj.get(4).toString() : "");
		dto.setSelectionProcessId(obj.get(5) != null ? obj.get(5).toString() : "");

		return dto;
    }
	
	public LookupItemDTO convertSelectionGroupDBOtoDTO(Object obj) {
		LookupItemDTO dto = new LookupItemDTO();
    	Object[] obj2 =  (Object[]) obj;
		dto.value=obj2[0].toString();
		dto.label=obj2[1].toString();
		return dto;
    }

	public SelectionProcessGroupEditDetailsDTO convertgetApplicantDataDBOtoDTO(Object obj) {
		SelectionProcessGroupEditDetailsDTO dto = new SelectionProcessGroupEditDetailsDTO();
    	Object[] obj2 =  (Object[]) obj;
    	dto.setStudentEntrieId(obj2[0].toString());
    	dto.setApplicationNo(obj2[1].toString());
    	dto.setApplicantName(String.valueOf(obj2[2]));
    	dto.setPrograme(String.valueOf(obj2[3]));
//		dto.setId(obj2[4].toString());
		return dto;
    }
}
