package com.christ.erp.services.helpers.admission.applicationprocess;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionGeneralDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionLocationCampusDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionWorkExperienceDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmPrerequisiteExamDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionDBO;
import com.christ.erp.services.dbobjects.common.ErpReligionDBO;
import com.christ.erp.services.dbobjects.common.ErpReservationCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpResidentCategoryDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmPrerequisiteExamDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.weightageGeneralDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WeightageDefinitionSettingsHelper {

	public AdmPrerequisiteExamDTO convertAdmPrerequisiteExamDBOtoDTO(AdmWeightageDefinitionDetailDBO detailDBO) {
			AdmPrerequisiteExamDTO prerequisiteExamDTO = new AdmPrerequisiteExamDTO();
			if(!Utils.isNullOrEmpty(detailDBO.getAdmPrerequisiteExamDBO())) {
			prerequisiteExamDTO.setId(detailDBO.getAdmPrerequisiteExamDBO().getId());
			prerequisiteExamDTO.setExamName(detailDBO.getAdmPrerequisiteExamDBO().getExamName());
			prerequisiteExamDTO.setParentId(detailDBO.getId());
			prerequisiteExamDTO.setScore(detailDBO.getScore());
			}
			return prerequisiteExamDTO;
	}
	public AdmQualificationListDTO convertAdmQualificationListDBOtoDTO(AdmWeightageDefinitionDetailDBO detailDBO) {
		AdmQualificationListDTO qualificationListDTO = new AdmQualificationListDTO();
		if(!Utils.isNullOrEmpty(detailDBO.getAdmQualificationListDBO())) {
		qualificationListDTO.setId(detailDBO.getAdmQualificationListDBO().getId());
		qualificationListDTO.setQualificationName(detailDBO.getAdmQualificationListDBO().getQualificationName());
		qualificationListDTO.setParentId(detailDBO.getId());
		qualificationListDTO.setScore(detailDBO.getScore());
		qualificationListDTO.setQualificationOrder(detailDBO.getAdmQualificationListDBO().getQualificationOrder());
		}
		return qualificationListDTO;
	}

	public weightageGeneralDTO convertErpReservationCategoryDBOtoDTO(ErpReservationCategoryDBO erpReservationCategoryDBO) {
		weightageGeneralDTO weightageGeneralDTO = new weightageGeneralDTO();
		LookupItemDTO item = new LookupItemDTO(String.valueOf(erpReservationCategoryDBO.getId()), erpReservationCategoryDBO.getReservationCategoryName());
		weightageGeneralDTO.setItem(item);
		return weightageGeneralDTO;
	}

	public weightageGeneralDTO convertErpInstitutionDBOtoDTO(ErpInstitutionDBO erpInstitutionDBO) {
		weightageGeneralDTO weightageGeneralDTO = new weightageGeneralDTO();
		LookupItemDTO item = new LookupItemDTO(String.valueOf(erpInstitutionDBO.getId()), erpInstitutionDBO.getInstitutionName());
		weightageGeneralDTO.setItem(item);
		return weightageGeneralDTO;
	}
	
	public weightageGeneralDTO convertErpGenderDBOtoDTO(ErpGenderDBO  erpGenderDBO) {
		weightageGeneralDTO weightageGeneralDTO = new weightageGeneralDTO();
		LookupItemDTO item = new LookupItemDTO(String.valueOf(erpGenderDBO.getErpGenderId()), erpGenderDBO.getGenderName());
		weightageGeneralDTO.setItem(item);
		return weightageGeneralDTO;
	}
	
	public weightageGeneralDTO convertErpReligionDBOtoDTO(ErpReligionDBO erpReligionDBO) {
		weightageGeneralDTO weightageGeneralDTO = new weightageGeneralDTO();
		LookupItemDTO item = new LookupItemDTO();
		item.label = erpReligionDBO.getReligionName();
		item.value = String.valueOf(erpReligionDBO.getId());
		weightageGeneralDTO.setItem(item);
		return weightageGeneralDTO;
		
	}
	public weightageGeneralDTO convertErpResidentCategoryDBOtoDTO(ErpResidentCategoryDBO erpResidentCategoryDBO) {
		weightageGeneralDTO weightageGeneralDTO = new weightageGeneralDTO();
		LookupItemDTO item = new LookupItemDTO(String.valueOf(erpResidentCategoryDBO.getId()), erpResidentCategoryDBO.getResidentCategoryName());
		weightageGeneralDTO.setItem(item);
		return weightageGeneralDTO;
	}

	public weightageGeneralDTO convertAdmWeightageDefinitionWorkExperienceDBOtoDTO(AdmWeightageDefinitionWorkExperienceDBO admWeightageDefinitionWorkExperienceDBO) {
		weightageGeneralDTO weightageGeneralDTO = new weightageGeneralDTO();
		LookupItemDTO item = new LookupItemDTO(String.valueOf(admWeightageDefinitionWorkExperienceDBO.getId()), admWeightageDefinitionWorkExperienceDBO.getWorkExperienceName(), false);
		weightageGeneralDTO.setItem(item);
		return weightageGeneralDTO;
	}
	
	public weightageGeneralDTO convertAdmQualificationDegreeListDBOtoDTO(AdmQualificationDegreeListDBO admQualificationDegreeListDBO) {
		weightageGeneralDTO weightageGeneralDTO = new weightageGeneralDTO();
		LookupItemDTO item = new LookupItemDTO(String.valueOf(admQualificationDegreeListDBO.getId()), admQualificationDegreeListDBO.getDegreeName());
		weightageGeneralDTO.setItem(item);
		return weightageGeneralDTO;
	}
	public AdmWeightageDefinitionDetailDBO convertDetailsDTOtoDBO(Integer score, Integer parentId, String userId, AdmWeightageDefinitionDBO dbo) {
		AdmWeightageDefinitionDetailDBO detailDBO = new AdmWeightageDefinitionDetailDBO();
		detailDBO.setScore(score);
		if(!Utils.isNullOrEmpty(parentId)) {
			detailDBO.setId(parentId);
			detailDBO.setModifiedUsersId(Integer.parseInt(userId));
		}else {
			detailDBO.setCreatedUsersId(Integer.parseInt(userId));
		}
		detailDBO.setRecordStatus('A');
		detailDBO.setAdmWeightageDefinitionDBO(dbo);
		return detailDBO;
	}
	public AdmWeightageDefinitionGeneralDBO convertGeneralDTOtoDBO(weightageGeneralDTO itemGeneral, String userId) {
		AdmWeightageDefinitionGeneralDBO generalDBO = new AdmWeightageDefinitionGeneralDBO();
		if(!Utils.isNullOrEmpty(itemGeneral.getParentId())) {
			if(!Utils.isNullOrEmpty(itemGeneral.getParentId())) {
				generalDBO.setModifiedUsersId(Integer.parseInt(userId));
				generalDBO.setId(itemGeneral.getParentId());
			}else {
				generalDBO.setCreatedUsersId(Integer.parseInt(userId));
			}
		}
		if(!Utils.isNullOrEmpty(itemGeneral.getScore()))
			generalDBO.setScore(itemGeneral.getScore());
		generalDBO.setRecordStatus('A');
		return generalDBO;
	}
	public Set<AdmWeightageDefinitionGeneralDBO> convertGenetalDBOstoDTOsSet(Set<AdmWeightageDefinitionGeneralDBO> itemGeneraDbosSet,
			AdmWeightageDefinitionDTO dto, String userId, AdmWeightageDefinitionDBO dbo, Set<Integer> admWeightageDefinitionGeneralDBOIds) {
    	dto.getAdmWeightageDefinitionGeneralDTOsSet().forEach(itemGeneral->{
    		if(!Utils.isNullOrEmpty(itemGeneral.getErpReservationCategoryDTOList())) {
    			itemGeneral.getErpReservationCategoryDTOList().forEach(itemCastRes->{
    				if(!Utils.isNullOrEmpty(itemCastRes.getItem()) && !Utils.isNullOrEmpty(itemCastRes.getItem().getValue())) {
	    				AdmWeightageDefinitionGeneralDBO  generalDBO = convertGeneralDTOtoDBO( itemCastRes , userId);
	    				generalDBO.setAdmWeightageDefinitionDBO(dbo);
	    				ErpReservationCategoryDBO categoryDBO = new ErpReservationCategoryDBO();
	    				categoryDBO.setId(Integer.parseInt(itemCastRes.getItem().getValue()));
	    				generalDBO.setErpReservationCategoryDBO(categoryDBO);
	    				itemGeneraDbosSet.add(generalDBO);
						admWeightageDefinitionGeneralDBOIds.add(itemCastRes.getParentId());
    				}
    			});
    		}
    		if(!Utils.isNullOrEmpty(itemGeneral.getErpResidentCategoryDTOList())) {
    			itemGeneral.getErpResidentCategoryDTOList().forEach(itemResidentCary->{
    				if(!Utils.isNullOrEmpty(itemResidentCary.getItem()) && !Utils.isNullOrEmpty(itemResidentCary.getItem().getValue())) {
	    				AdmWeightageDefinitionGeneralDBO  generalDBO = convertGeneralDTOtoDBO( itemResidentCary , userId);
	    				generalDBO.setAdmWeightageDefinitionDBO(dbo);
	    				ErpResidentCategoryDBO categoryDBO = new ErpResidentCategoryDBO();
	    				categoryDBO.setId(Integer.parseInt(itemResidentCary.getItem().getValue()));
	    				generalDBO.setErpResidentCategoryDBO(categoryDBO);
	    				itemGeneraDbosSet.add(generalDBO);
						admWeightageDefinitionGeneralDBOIds.add(itemResidentCary.getParentId());
    				}
    			});
    		}
    		if(!Utils.isNullOrEmpty(itemGeneral.getErpReligionDTOList())) {
    			itemGeneral.getErpReligionDTOList().forEach(ItemReligion->{
    				if(!Utils.isNullOrEmpty(ItemReligion.getItem()) && !Utils.isNullOrEmpty(ItemReligion.getItem().getValue())) {
	    				AdmWeightageDefinitionGeneralDBO  generalDBO = convertGeneralDTOtoDBO( ItemReligion , userId);
	    				generalDBO.setAdmWeightageDefinitionDBO(dbo);
	    				ErpReligionDBO erpReligionDBO = new ErpReligionDBO();
	    				erpReligionDBO.setId(Integer.parseInt(ItemReligion.getItem().getValue()));
	    				generalDBO.setErpReligionDBO(erpReligionDBO);
	    				itemGeneraDbosSet.add(generalDBO);
						admWeightageDefinitionGeneralDBOIds.add(ItemReligion.getParentId());
    				}
    			});
    		}
    		if(!Utils.isNullOrEmpty(itemGeneral.getErpGenderDTOList())) {
    			itemGeneral.getErpGenderDTOList().forEach(itemGender->{
    				if(!Utils.isNullOrEmpty(itemGender.getItem()) && !Utils.isNullOrEmpty(itemGender.getItem().getValue())) {
	    				AdmWeightageDefinitionGeneralDBO  generalDBO = convertGeneralDTOtoDBO( itemGender , userId);
	    				generalDBO.setAdmWeightageDefinitionDBO(dbo);
	    				ErpGenderDBO erpGenderDBO = new ErpGenderDBO();
	    				erpGenderDBO.setErpGenderId(Integer.parseInt(itemGender.getItem().getValue()));
	    				generalDBO.setErpGenderDBO(erpGenderDBO);
	    				itemGeneraDbosSet.add(generalDBO);
						admWeightageDefinitionGeneralDBOIds.add(itemGender.getParentId());
    				}
    			});
    		}	
    		if(!Utils.isNullOrEmpty(itemGeneral.getAdmQualificationDegreeListDTOList())) {
    			itemGeneral.getAdmQualificationDegreeListDTOList().forEach(itemQLDegree->{
    				if(!Utils.isNullOrEmpty(itemQLDegree.getItem()) && !Utils.isNullOrEmpty(itemQLDegree.getItem().getValue())) {
	    				AdmWeightageDefinitionGeneralDBO  generalDBO = convertGeneralDTOtoDBO( itemQLDegree , userId);
	    				generalDBO.setAdmWeightageDefinitionDBO(dbo);
	    				AdmQualificationDegreeListDBO degreeListDBO = new AdmQualificationDegreeListDBO();
	    				degreeListDBO.setId(Integer.parseInt(itemQLDegree.getItem().getValue()));
	    				generalDBO.setAdmQualificationDegreeListDBO(degreeListDBO);
	    				itemGeneraDbosSet.add(generalDBO);
						admWeightageDefinitionGeneralDBOIds.add(itemQLDegree.getParentId());
    				}
    			});
    		}	
    		if(!Utils.isNullOrEmpty(itemGeneral.getErpInstitutionDTOList())) {
    			itemGeneral.getErpInstitutionDTOList().forEach(itemInstitute->{
    				if(!Utils.isNullOrEmpty(itemInstitute.getItem()) && !Utils.isNullOrEmpty(itemInstitute.getItem().getValue())) {
	    				AdmWeightageDefinitionGeneralDBO  generalDBO = convertGeneralDTOtoDBO( itemInstitute , userId);
	    				generalDBO.setAdmWeightageDefinitionDBO(dbo);
	    				ErpInstitutionDBO erpInstitutionDBO = new ErpInstitutionDBO();
	    				erpInstitutionDBO.setId(Integer.parseInt(itemInstitute.getItem().getValue()));
	    				generalDBO.setErpInstitutionDBO(erpInstitutionDBO);
	    				itemGeneraDbosSet.add(generalDBO);
						admWeightageDefinitionGeneralDBOIds.add(itemInstitute.getParentId());
    				}
    			});
    		}
    		if(!Utils.isNullOrEmpty(itemGeneral.getAdmWeightageDefinitionWorkExperienceDTOList())) {
    			itemGeneral.getAdmWeightageDefinitionWorkExperienceDTOList().forEach(itemWDworkExp->{
    				if(!Utils.isNullOrEmpty(itemWDworkExp.getItem()) && !Utils.isNullOrEmpty(itemWDworkExp.getItem().getValue())) {
	    				AdmWeightageDefinitionGeneralDBO  generalDBO = convertGeneralDTOtoDBO( itemWDworkExp , userId);
	    				generalDBO.setAdmWeightageDefinitionDBO(dbo);
	    				AdmWeightageDefinitionWorkExperienceDBO  workExperienceDBO = new AdmWeightageDefinitionWorkExperienceDBO();
	    				workExperienceDBO.setId(Integer.parseInt(itemWDworkExp.getItem().getValue()));
	    				generalDBO.setAdmWeightageDefinitionWorkExperienceDBO(workExperienceDBO);
	    				itemGeneraDbosSet.add(generalDBO);
						admWeightageDefinitionGeneralDBOIds.add(itemWDworkExp.getParentId());
    				}
    			});
    		}	
    		
    	});
		return itemGeneraDbosSet;		
	}
	public Set<AdmWeightageDefinitionDetailDBO> convertWeightDefDetailDTOstoDBOsSet(
			Set<AdmWeightageDefinitionDetailDBO> weightageDefinitionDetailDBOsSet, AdmWeightageDefinitionDTO dto,
			String userId, AdmWeightageDefinitionDBO dbo, Set<Integer> admWeightageDefinitionDetailDBOIds) {
    	dto.getAdmWeightageDefinitionDetailDTOSet().forEach(itemDetails->{
    		if(!Utils.isNullOrEmpty(itemDetails.getAdmPrerequisiteExamDTOList())) {
    			itemDetails.getAdmPrerequisiteExamDTOList().forEach(itemPrerequest->{
    				if(!Utils.isNullOrEmpty(itemPrerequest.getScore()) && !Utils.isNullOrEmpty(itemPrerequest.getId())) {
    					AdmWeightageDefinitionDetailDBO detailDBO = convertDetailsDTOtoDBO(itemPrerequest.getScore(), 
    							 itemPrerequest.getParentId(), userId, dbo);
						AdmPrerequisiteExamDBO prerequisiteExamDBO = new AdmPrerequisiteExamDBO();
    					prerequisiteExamDBO.setId(itemPrerequest.getId());
    					detailDBO.setAdmPrerequisiteExamDBO(prerequisiteExamDBO);
    					weightageDefinitionDetailDBOsSet.add(detailDBO);
						admWeightageDefinitionDetailDBOIds.add(itemPrerequest.getParentId());
    				}
    			});
    		}
    		if(!Utils.isNullOrEmpty(itemDetails.getAdmQualificationListDTOList())) {
    			itemDetails.getAdmQualificationListDTOList().forEach(itemQualification->{
    				if(!Utils.isNullOrEmpty(itemQualification.getScore()) && !Utils.isNullOrEmpty(itemQualification.getId())) {
    					AdmWeightageDefinitionDetailDBO detailDBO = convertDetailsDTOtoDBO(itemQualification.getScore(), 
    						 itemQualification.getParentId(), userId, dbo);
    					AdmQualificationListDBO qualificationListDBO = new AdmQualificationListDBO();
    					qualificationListDBO.setId(itemQualification.getId());
    					detailDBO.setAdmQualificationListDBO(qualificationListDBO);
    					weightageDefinitionDetailDBOsSet.add(detailDBO);
						admWeightageDefinitionDetailDBOIds.add(itemQualification.getParentId());
    				}
    			});
    		}
    		if(!Utils.isNullOrEmpty(itemDetails.getAdmSelectionProcessPlanDetailDTOList())) {
    			itemDetails.getAdmSelectionProcessPlanDetailDTOList().forEach(itemPlan->{
					itemPlan.getSlotslist().forEach(itemPlanDetails->{
						itemPlanDetails.getAdmissionSelectionProcessTypeDetailsList().forEach(itemSpTypeDetails->{
							if(!Utils.isNullOrEmpty(itemSpTypeDetails.getScore()) && !Utils.isNullOrEmpty(itemSpTypeDetails.getId())) {
								AdmWeightageDefinitionDetailDBO detailDBO = convertDetailsDTOtoDBO(itemSpTypeDetails.getScore(),
										 itemSpTypeDetails.getParentId(), userId, dbo);
								AdmSelectionProcessPlanDetailDBO planDetailDBO = new AdmSelectionProcessPlanDetailDBO();
								planDetailDBO.setId(itemPlanDetails.getDetailId());
								detailDBO.setAdmSelectionProcessPlanDetailDBO(planDetailDBO);
								AdmSelectionProcessTypeDetailsDBO processTypeDetailsDBO = new AdmSelectionProcessTypeDetailsDBO();
								processTypeDetailsDBO.setId(Integer.parseInt(itemSpTypeDetails.getId()));
								detailDBO.setAdmSelectionProcessTypeDetailsDBO(processTypeDetailsDBO);
								weightageDefinitionDetailDBOsSet.add(detailDBO);
								admWeightageDefinitionDetailDBOIds.add(itemSpTypeDetails.getParentId());

							}
						});
					});
    			});
    		}
    	});
		return weightageDefinitionDetailDBOsSet;
	}

	public Set<AdmWeightageDefinitionLocationCampusDBO> convertLocationCampusDTOsToDBOsSet(Set<AdmWeightageDefinitionLocationCampusDBO> locationCampusDBOSet,
	    AdmWeightageDefinitionDTO dto, String userId, AdmWeightageDefinitionDBO dbo) {
		dto.getCampusOrlocationsMappping().forEach(programPreferenceDTO -> {
			AdmWeightageDefinitionLocationCampusDBO admWeightageDefinitionLocationCampusDBO = new AdmWeightageDefinitionLocationCampusDBO();
			if(!Utils.isNullOrEmpty(programPreferenceDTO.getParentId())){
				admWeightageDefinitionLocationCampusDBO.setId(programPreferenceDTO.getParentId());
				admWeightageDefinitionLocationCampusDBO.setModifiedUsersId(Integer.parseInt(userId));
			}else{
				admWeightageDefinitionLocationCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
			}
			ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
			erpCampusProgrammeMappingDBO.id = Integer.parseInt(programPreferenceDTO.getValue());
			admWeightageDefinitionLocationCampusDBO.setErpCampusProgrammeMappingDBO(erpCampusProgrammeMappingDBO);
			admWeightageDefinitionLocationCampusDBO.setAdmWeightageDefinitionDBO(dbo);
			admWeightageDefinitionLocationCampusDBO.setRecordStatus('A');
			locationCampusDBOSet.add(admWeightageDefinitionLocationCampusDBO);
		});
		return locationCampusDBOSet;
	}
}
