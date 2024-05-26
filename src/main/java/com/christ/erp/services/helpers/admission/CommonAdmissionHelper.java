package com.christ.erp.services.helpers.admission;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionWorkExperienceDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmPrerequisiteExamDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailAddSlotDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailDTO;
import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDetailsDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
@Service
public class CommonAdmissionHelper {
	
	/*
	 * private static volatile CommonAdmissionHelper commonAdmissionHelper = null;
	 * 
	 * public static CommonAdmissionHelper getInstance() {
	 * if(commonAdmissionHelper==null) { commonAdmissionHelper = new
	 * CommonAdmissionHelper(); } return commonAdmissionHelper; }
	 */
    
    public ApiResult<List<LookupItemDTO>> convertToLookupItemDTO(ApiResult<List<LookupItemDTO>> result,List<Tuple> mappings) throws ParseException {
    	if(!Utils.isNullOrEmpty(mappings)) {
			result.success = true;
			result.dto = new ArrayList<>();
			for(Tuple mapping : mappings) {
	            LookupItemDTO itemInfo = new LookupItemDTO();
	            itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
	            itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
	            result.dto.add(itemInfo);
	        }
		}
		return result;
    }
    
    public ApiResult<List<LookupItemDTO>> convertAccountHeadToLookupItemDTO(ApiResult<List<LookupItemDTO>> result,List<Tuple> mappings) throws ParseException {
    	if(!Utils.isNullOrEmpty(mappings)) {
			result.success = true;
			result.dto = new ArrayList<>();
			for(Tuple mapping : mappings) {
				LookupItemDTO itemInfo = new LookupItemDTO();
				itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
				itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : ""; // sambath
				result.dto.add(itemInfo);
			}
		}
		return result;
    }
    
	public AdmPrerequisiteExamDTO convertAdmPrerequisiteExamDBOtoDTO1(Object[] obj) {
		AdmPrerequisiteExamDTO prerequisiteExamDTO = new AdmPrerequisiteExamDTO();
		prerequisiteExamDTO.setId(!Utils.isNullOrEmpty(obj[0])?Integer.parseInt(obj[0].toString()):0);
		prerequisiteExamDTO.setExamName(!Utils.isNullOrEmpty(obj[1])?obj[1].toString():"");
		return prerequisiteExamDTO;
	}
	public AdmQualificationListDTO convertAdmQualificationListDBOtoDTO(Object[] obj) {
		AdmQualificationListDTO qualificationListDTO = new AdmQualificationListDTO();
		qualificationListDTO.setId(!Utils.isNullOrEmpty(obj[0])?Integer.parseInt(obj[0].toString()):0);
		qualificationListDTO.setQualificationName(!Utils.isNullOrEmpty(obj[1])?obj[1].toString():"");
		return qualificationListDTO;
	}
	public AdmSelectionProcessPlanDetailDTO convertAdmSelectionProcessPlanDBOtoDTO(AdmSelectionProcessPlanDBO dbo) {
		AdmSelectionProcessPlanDetailDTO detailDTO = new AdmSelectionProcessPlanDetailDTO();
		detailDTO.setSessionId(dbo.getId());
		detailDTO.setSessionName(dbo.getSelectionProcessSession());
		List<AdmSelectionProcessPlanDetailAddSlotDTO> addSlotDTOList = new ArrayList<AdmSelectionProcessPlanDetailAddSlotDTO>();
		dbo.getAdmSelectionProcessPlanDetailDBO().forEach(item->{
			AdmSelectionProcessPlanDetailAddSlotDTO slotDTO = new AdmSelectionProcessPlanDetailAddSlotDTO();			
			if(!Utils.isNullOrEmpty(item.getId()) && !Utils.isNullOrEmpty(item.getAdmSelectionProcessTypeDBO()) ) {
				slotDTO.setDetailId(item.getId());
				ExModelBaseDTO baseDTO = new ExModelBaseDTO();
				baseDTO.id = String.valueOf(item.getAdmSelectionProcessTypeDBO().getId());
				if(!Utils.isNullOrEmpty(item.getAdmSelectionProcessTypeDBO().getMode()))
				baseDTO.text = item.getAdmSelectionProcessTypeDBO().getMode();
				slotDTO.setSelectionProcessName(baseDTO);
			}
			List<AdmissionSelectionProcessTypeDetailsDTO> typeDetailsDTO = new ArrayList<>();
			item.getAdmSelectionProcessTypeDBO().getAdmissionSelectionProcessTypeDetailsDBOSet().forEach(itemDetails->{
				if(!Utils.isNullOrEmpty(itemDetails) && !Utils.isNullOrEmpty(itemDetails.getId())
						&& !Utils.isNullOrEmpty(itemDetails.getSubProcessName())) {
				AdmissionSelectionProcessTypeDetailsDTO detailsTypeDetailsDTO = new AdmissionSelectionProcessTypeDetailsDTO();
				detailsTypeDetailsDTO.setId(String.valueOf(itemDetails.getId()));
				detailsTypeDetailsDTO.setSubProcess(itemDetails.getSubProcessName());
				typeDetailsDTO.add(detailsTypeDetailsDTO);
				}
			});
			slotDTO.setAdmissionSelectionProcessTypeDetailsList(typeDetailsDTO);
			addSlotDTOList.add(slotDTO);
		});
		detailDTO.setSlotslist(addSlotDTOList);
		return detailDTO;
	}
	
	 public LookupItemDTO convertAdmWeightageDefinitionWorkExperienceDBOtoDTO(AdmWeightageDefinitionWorkExperienceDBO dbo) {
		 LookupItemDTO itemDTO = new LookupItemDTO();
	    	if(!Utils.isNullOrEmpty(dbo.getId()) && !Utils.isNullOrEmpty(dbo.getWorkExperienceName())) {
	        	itemDTO.value = String.valueOf(dbo.getId());
	        	itemDTO.label = dbo.getWorkExperienceName();
	    	}
	    	return itemDTO;
	    }
	    
    public AdmQualificationListDTO convertAdmQualificationListDBOtoDTO(AdmQualificationListDBO dbo) {
    	AdmQualificationListDTO admQualificationListDTO = new AdmQualificationListDTO();
    	BeanUtils.copyProperties(dbo, admQualificationListDTO);
    	return admQualificationListDTO;
    }
    public LookupItemDTO convertAdmQualificationListDegreeDBOtoDTO(AdmQualificationDegreeListDBO dbo) {
    	LookupItemDTO itemDTO = new LookupItemDTO();
    	if(!Utils.isNullOrEmpty(dbo.getId()) && !Utils.isNullOrEmpty(dbo.getDegreeName())) {
        	itemDTO.value = String.valueOf(dbo.getId());
        	itemDTO.label = dbo.getDegreeName();
    	}
    	return itemDTO;
    }
    
    public LookupItemDTO convertSelectionProcessDateDBOtoDTO(Object obj) {
    	LookupItemDTO dto = new LookupItemDTO();
    	Object[] obj2 =  (Object[]) obj;
    	dto.value = obj2[1].toString();
    	dto.label = Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(obj2[0].toString()));
		return dto;
    }
    
    public LookupItemDTO convertSelectionProcessCenterDBOtoDTO(Object obj) {
    	LookupItemDTO dto = new LookupItemDTO();
    	Object[] obj2 =  (Object[]) obj;
    	dto.value=obj2[0].toString();
    	dto.label=obj2[1].toString();
		return dto;
    }
    
    public LookupItemDTO convertCampusProgrammeMappingDBOtoDTO(Object obj) {
    	LookupItemDTO dto = new LookupItemDTO();
    	Object[] obj2 =  (Object[]) obj;
    	dto.value=obj2[0].toString();
    	dto.label=obj2[1].toString();
		return dto;
    }
    
    public LookupItemDTO convertSelectionProcessTimetoDTO(Object[] obj) {
    	LookupItemDTO dto = new LookupItemDTO();
    	dto.value=String.valueOf(obj[0].toString());
    	dto.label=obj[1].toString();
		return dto;
    }

	public ProgramPreferenceDTO convertProgrammePreferenceToDTO(Tuple tuple) {
		ProgramPreferenceDTO itemDTO = new ProgramPreferenceDTO();
		String campusOrLocation = "";
		if(!Utils.isNullOrEmpty(tuple.get("mappingId"))) {
			itemDTO = new ProgramPreferenceDTO();
			if(!Utils.isNullOrEmpty(tuple.get("programId"))) {
				if(!Utils.isNullOrEmpty(tuple.get("campusOrLocation")) && tuple.get("campusOrLocation").toString().trim().equals("C")) {
					itemDTO.preferenceOption = 'C';
					campusOrLocation =  !Utils.isNullOrEmpty(tuple.get("campusName")) ? tuple.get("campusName").toString() : "";
					itemDTO.campusMappingId =  tuple.get("mappingId").toString();
				}else if(!Utils.isNullOrEmpty(tuple.get("campusOrLocation")) && tuple.get("campusOrLocation").toString().trim().equals("L")) {
					itemDTO.preferenceOption = 'L';
					campusOrLocation =  !Utils.isNullOrEmpty(tuple.get("locName")) ? tuple.get("locName").toString() : "";
					itemDTO.campusMappingId =  tuple.get("mappingId").toString();
				}
				itemDTO.programId = !Utils.isNullOrEmpty(tuple.get("programId")) ? tuple.get("programId").toString() : "";
				itemDTO.value = !Utils.isNullOrEmpty(tuple.get("mappingId")) ? tuple.get("mappingId").toString() : "";
				itemDTO.label = !Utils.isNullOrEmpty(tuple.get("programName")) ? tuple.get("programName").toString()+" ("+campusOrLocation+")" : "";
			}
		}
		return itemDTO;
	}

	public SelectDTO convertSelectionProcessSessionToDTO(Tuple tuple) {
		SelectDTO itemDTO = new SelectDTO();
		if(!Utils.isNullOrEmpty(tuple.get("admSelectionProcessPlanId")) && !Utils.isNullOrEmpty(tuple.get("sessionName"))){
			itemDTO.setValue(tuple.get("admSelectionProcessPlanId").toString());
			itemDTO.setLabel(tuple.get("sessionName").toString());
		}
		return itemDTO;
	}

	public LookupItemDTO convertSelectionProcessTypeToDTO(Tuple tuple) {
		LookupItemDTO itemDTO = new LookupItemDTO();
		if(!Utils.isNullOrEmpty(tuple.get("admSelectionProcessTypeId")) && !Utils.isNullOrEmpty(tuple.get("selectionStageName"))){
			itemDTO.value = String.valueOf(tuple.get("admSelectionProcessTypeId"));
			itemDTO.label = String.valueOf(tuple.get("selectionStageName"));
		}
		return itemDTO;
	}
}

