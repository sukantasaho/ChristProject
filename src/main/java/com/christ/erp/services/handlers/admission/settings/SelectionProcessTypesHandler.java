package com.christ.erp.services.handlers.admission.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmissionScoreCardDBO;
import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDTO;
import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDetailsDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.admission.settings.SelectionProcessTypesTransaction;
import jdk.jshell.execution.Util;


public class SelectionProcessTypesHandler {
	
	private static volatile SelectionProcessTypesHandler selectionProcessTypesHandler = null;
	SelectionProcessTypesTransaction selectionProcessTypesTransaction = SelectionProcessTypesTransaction.getInstance();

	public static SelectionProcessTypesHandler getInstance() {
		if (selectionProcessTypesHandler == null) {
			selectionProcessTypesHandler = new SelectionProcessTypesHandler();
		}
		return selectionProcessTypesHandler;
	}
	
	public List<AdmissionSelectionProcessTypeDTO> getGridData() {
		List<AdmissionSelectionProcessTypeDTO> admissionSelectionProcessTypeDTO = new ArrayList<>();
		List<Tuple> list;
		try {
			list = selectionProcessTypesTransaction.getGridData();
			if(!Utils.isNullOrEmpty(list)){
				for (Tuple tuple : list) {
					AdmissionSelectionProcessTypeDTO gridDTO = new AdmissionSelectionProcessTypeDTO();
					gridDTO.id = tuple.get("ID").toString();
					gridDTO.selectionProcessName = String.valueOf(tuple.get("selectionProcessName"));
					gridDTO.mode=String.valueOf(tuple.get("mode"));
					gridDTO.setAdmitCardDisplayName(String.valueOf(tuple.get("displayName")));
					admissionSelectionProcessTypeDTO.add(gridDTO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admissionSelectionProcessTypeDTO;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public ApiResult<ModelBaseDTO> saveOrUpdateSelectionProcessType(AdmissionSelectionProcessTypeDTO data, String userId)
			throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		AdmSelectionProcessTypeDBO header = null;
		data.selectionProcessName = data.selectionProcessName.trim();
		Boolean isDuplicate = selectionProcessTypesTransaction.isDuplicate(data.selectionProcessName, data.id);
		if(!isDuplicate) {
			if (Utils.isNullOrWhitespace(data.id) == false) {
				header = selectionProcessTypesTransaction.getAdminSelectionProcessTypeDBO(Integer.parseInt(data.id));
				header.setModifiedUsersId(Integer.parseInt(userId));
			}
			if (Utils.isNullOrEmpty(header)) {
				header = new AdmSelectionProcessTypeDBO();
				header.createdUsersId = Integer.parseInt(userId);
			}
			header.selectionStageName = data.selectionProcessName;
			header.setAdmitCardDisplayName(data.getAdmitCardDisplayName());
			header.mode = data.mode;
			header.isShortlistAfterThisStage = Boolean.valueOf(data.isShortist);
			header.recordStatus = 'A';
			Set<AdmSelectionProcessTypeDetailsDBO> admissionSelectionProcessTypeDetailsUpdateDBO = new HashSet<>();
			Set<AdmSelectionProcessTypeDetailsDBO> existDBOSet= header.admissionSelectionProcessTypeDetailsDBOSet;
			Map<Integer,AdmSelectionProcessTypeDetailsDBO> existDBOMap = new HashMap<Integer, AdmSelectionProcessTypeDetailsDBO>();
			if(!Utils.isNullOrEmpty(existDBOSet)) {
				existDBOSet.forEach(dbo-> {
					if(dbo.recordStatus=='A') {
						existDBOMap.put(dbo.id, dbo);
					}
				});
			}
			for(AdmissionSelectionProcessTypeDetailsDTO item : data.levels) {
				AdmSelectionProcessTypeDetailsDBO detail = null;
				if(!Utils.isNullOrWhitespace(item.id) && existDBOMap.containsKey(Integer.parseInt(item.id))) {	
	    			detail = existDBOMap.get((Integer.parseInt(item.id)));
	    			detail.modifiedUsersId = Integer.parseInt(userId);
	    			existDBOMap.remove(Integer.parseInt(item.id));
                }
				else {
					detail = new AdmSelectionProcessTypeDetailsDBO();
        			detail.createdUsersId = Integer.parseInt(userId);
				}
        		detail.order = Integer.parseInt(item.order);
 				detail.subProcessName = item.subProcess;
				 if(!Utils.isNullOrEmpty(item.scoreCard.id)){
					 detail.admissionScoreCardDBO = new AdmScoreCardDBO();
					 detail.admissionScoreCardDBO.id = Integer.parseInt(item.scoreCard.id);
				 }
 				detail.panelistCount = Integer.parseInt(item.pannelListCount);
 				detail.recordStatus = 'A';
                detail.adminSelectionProcessTypeDBO = header;
                admissionSelectionProcessTypeDetailsUpdateDBO.add(detail);
               
            }
			if(!Utils.isNullOrEmpty(existDBOMap)) {
				existDBOMap.forEach((entry, value)-> {
					value.modifiedUsersId = Integer.parseInt(userId);
					value.recordStatus = 'D';
					admissionSelectionProcessTypeDetailsUpdateDBO.add(value);
				});
			}
			header.admissionSelectionProcessTypeDetailsDBOSet = admissionSelectionProcessTypeDetailsUpdateDBO;
			if(selectionProcessTypesTransaction.saveOrUpdate(header)) {
				result.success = true;
			}
		}
		else {
        	result.failureMessage = "Duplicate record exists for Selection Process Name:" + data.selectionProcessName;
			result.success = false;
        }
		return result;
	}
	
	public AdmissionSelectionProcessTypeDTO selectAdmissionSelectionProcessType(String id) {
		AdmissionSelectionProcessTypeDTO admissionSelectionProcessTypeDTO = new AdmissionSelectionProcessTypeDTO();
		try {
			AdmSelectionProcessTypeDBO adminSelectionProcessType = selectionProcessTypesTransaction.getAdminSelectionProcessTypeDBO(Integer.parseInt(id));
			if (!Utils.isNullOrEmpty(adminSelectionProcessType)) {
				admissionSelectionProcessTypeDTO = new AdmissionSelectionProcessTypeDTO();
				admissionSelectionProcessTypeDTO.id = String.valueOf(adminSelectionProcessType.id);
				admissionSelectionProcessTypeDTO.selectionProcessName = String.valueOf(adminSelectionProcessType.selectionStageName);
				admissionSelectionProcessTypeDTO.setAdmitCardDisplayName(adminSelectionProcessType.getAdmitCardDisplayName());
				admissionSelectionProcessTypeDTO.mode = String.valueOf(adminSelectionProcessType.mode);
				admissionSelectionProcessTypeDTO.isShortist = Boolean.valueOf(adminSelectionProcessType.isShortlistAfterThisStage); 
				admissionSelectionProcessTypeDTO.levels = new ArrayList<>();
				if (adminSelectionProcessType.admissionSelectionProcessTypeDetailsDBOSet != null
						&& adminSelectionProcessType.admissionSelectionProcessTypeDetailsDBOSet.size() > 0) {
					for (AdmSelectionProcessTypeDetailsDBO item : adminSelectionProcessType.admissionSelectionProcessTypeDetailsDBOSet) {
						AdmissionSelectionProcessTypeDetailsDTO levelInfo = new AdmissionSelectionProcessTypeDetailsDTO();
						if (item.recordStatus == 'A') {
							levelInfo.id = String.valueOf(item.id);
							levelInfo.order = String.valueOf(item.order);
							levelInfo.subProcess = item.subProcessName.toString();
							levelInfo.scoreCard = new ExModelBaseDTO();
							if(!Utils.isNullOrEmpty(item.admissionScoreCardDBO)){
								levelInfo.scoreCard.id = String.valueOf(item.admissionScoreCardDBO.id);
							}
							levelInfo.pannelListCount = String.valueOf(item.panelistCount);
							admissionSelectionProcessTypeDTO.levels.add(levelInfo);
						}
					}
					Collections.sort(admissionSelectionProcessTypeDTO.levels);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admissionSelectionProcessTypeDTO;
	}

	public boolean deleteAdmissionSelectionProcessType(String id, String userId) {
		try {
			AdmSelectionProcessTypeDBO adminSelectionProcessTypeDBO = selectionProcessTypesTransaction
					.getAdminSelectionProcessTypeDBO(Integer.parseInt(id));
			if (!Utils.isNullOrEmpty(adminSelectionProcessTypeDBO)) {
				adminSelectionProcessTypeDBO.recordStatus = 'D';
				adminSelectionProcessTypeDBO.modifiedUsersId = Integer.parseInt(userId);
				for (AdmSelectionProcessTypeDetailsDBO item : adminSelectionProcessTypeDBO.admissionSelectionProcessTypeDetailsDBOSet) {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
				}
				if (adminSelectionProcessTypeDBO.id != null) {
					return selectionProcessTypesTransaction.saveOrUpdate(adminSelectionProcessTypeDBO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
