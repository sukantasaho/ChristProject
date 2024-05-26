package com.christ.erp.services.helpers.employee.onboarding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistMainDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistSubDBO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistMainDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistSubDTO;
import com.christ.erp.services.transactions.employee.onboarding.DocumentVerificationSettingsTransaction;

public class DocumentVerificationSettingsHelper {
	
	private static volatile DocumentVerificationSettingsHelper documentVerificationSettingsHelper = null;
    DocumentVerificationSettingsTransaction documentVerificationSettingsTransaction = DocumentVerificationSettingsTransaction.getInstance();

    public static DocumentVerificationSettingsHelper getInstance() {
        if(documentVerificationSettingsHelper==null) {
        	documentVerificationSettingsHelper = new DocumentVerificationSettingsHelper();
        }
        return documentVerificationSettingsHelper;
    }

	public String checklistDuplicateCheck(EmpDocumentChecklistMainDTO headingData, Map<Integer, EmpDocumentChecklistSubDTO> checklistDocumentsExistingMap, 
			Map<String, EmpDocumentChecklistSubDTO> checklistDocumentsNewMap) throws Exception{
		String errorMsg = "";
		List<EmpDocumentChecklistMainDBO> mainBoList = documentVerificationSettingsTransaction.getChecklistMainBos(headingData);
		if(!Utils.isNullOrEmpty(mainBoList)) {
			for(EmpDocumentChecklistMainDBO bo : mainBoList) {
				if(!Utils.isNullOrEmpty(headingData.headingName) && headingData.headingName.equalsIgnoreCase(bo.documentChecklistName)) {
					errorMsg = "Duplicate entry for Document Group : "+ headingData.headingName+".";
					break;
				}
				if(!Utils.isNullOrEmpty(headingData.headingOrder) && Integer.parseInt(headingData.headingOrder) == bo.documentChecklistDisplayOrder) {
					errorMsg = "Duplicate entry for Group Display Order : "+headingData.headingOrder+".";
					break;
				}
			}
		}
		if(Utils.isNullOrEmpty(errorMsg) && !Utils.isNullOrEmpty(headingData.checklistDocuments)) {
			List<EmpDocumentChecklistSubDBO> subBoList = documentVerificationSettingsTransaction.getChecklistSubBos();
			if(!Utils.isNullOrEmpty(subBoList)) {
				for(EmpDocumentChecklistSubDTO dto : headingData.checklistDocuments) {
					List<String> duplicateList = new ArrayList<String>();
					for(EmpDocumentChecklistSubDBO sub : subBoList) {
						boolean existingDoc = false;
						boolean newDoc = false;
						if(dto.documentName.equalsIgnoreCase(sub.documentChecklistSubName)) {
							if(Utils.isNullOrEmpty(dto.headingId)) {
								if(!Utils.isNullOrEmpty(dto.headingId) && sub.empDocumentChecklistMainDBO.id == dto.headingId) {
									newDoc = true;
								}else {
									duplicateList.add(dto.documentName);
								}
							}else {
								existingDoc = true;
							}
						}else {
							if(Utils.isNullOrEmpty(dto.id)) {
								newDoc = true;
							}else {
								existingDoc = true;
							}
						}
						if(newDoc) {
							if(!checklistDocumentsNewMap.containsKey(dto.documentName))
								checklistDocumentsNewMap.put(dto.documentName, dto);
						}
						if(existingDoc) {
							if(!checklistDocumentsExistingMap.containsKey(dto.id))
								checklistDocumentsExistingMap.put(dto.id, dto);
						}
					}
					if(!Utils.isNullOrEmpty(duplicateList)) {
						String docName = duplicateList.get(0);
						errorMsg = "Duplicate entry for Document Name : "+ docName +".";
						break;
					}
				}
			}
		}
    	return errorMsg;
	}
}
