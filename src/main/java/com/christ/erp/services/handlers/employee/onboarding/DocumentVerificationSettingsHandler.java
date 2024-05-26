package com.christ.erp.services.handlers.employee.onboarding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Tuple;

import java.util.Set;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistMainDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistSubDBO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistMainDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistSubDTO;
import com.christ.erp.services.helpers.employee.onboarding.DocumentVerificationSettingsHelper;
import com.christ.erp.services.transactions.employee.onboarding.DocumentVerificationSettingsTransaction;

public class DocumentVerificationSettingsHandler {
	private static volatile DocumentVerificationSettingsHandler documentVerificationSettingsHandler = null;
    DocumentVerificationSettingsTransaction documentVerificationSettingsTransaction = DocumentVerificationSettingsTransaction.getInstance();
    DocumentVerificationSettingsHelper documentVerificationSettingsHelper = DocumentVerificationSettingsHelper.getInstance();

    public static DocumentVerificationSettingsHandler getInstance() {
        if(documentVerificationSettingsHandler==null) {
        	documentVerificationSettingsHandler = new DocumentVerificationSettingsHandler();
        }
        return documentVerificationSettingsHandler;
    }

    public List<EmpDocumentChecklistMainDTO> getGridData() throws Exception{
        List<EmpDocumentChecklistMainDTO> headings = new ArrayList<EmpDocumentChecklistMainDTO>();
        List<Tuple> headingList = documentVerificationSettingsTransaction.getChecklistHeadings();
        if(headingList != null && !headingList.isEmpty()){
            for(Tuple heading : headingList){
                EmpDocumentChecklistMainDTO checklist = new EmpDocumentChecklistMainDTO();
                checklist.id = Integer.parseInt(heading.get("id").toString());
                checklist.headingName = !Utils.isNullOrEmpty(heading.get("name").toString()) ? String.valueOf(heading.get("name").toString()) : "";
                checklist.headingOrder = !Utils.isNullOrEmpty(heading.get("displayOrder").toString()) ? String.valueOf(heading.get("displayOrder").toString()) : ""; 
                checklist.isForeignNationalDocumentChecklist = (!Utils.isNullOrEmpty(Boolean.valueOf(heading.get("checklistForeignNational").toString())) && Boolean.valueOf(heading.get("checklistForeignNational").toString())) ? "Yes" : "No"; 
                headings.add(checklist);
            }
        }
        return headings;
    }
    
    public String saveOrUpdate(EmpDocumentChecklistMainDTO headingData, String userId) throws Exception{
    	boolean isSaved = false;
    	String failureMessage = "";
    	try {
			if(!Utils.isNullOrEmpty(headingData)) {
				Map<String, EmpDocumentChecklistSubDTO> checklistDocumentsNewMap = new HashMap<String, EmpDocumentChecklistSubDTO>();
				Map<Integer, EmpDocumentChecklistSubDTO> checklistDocumentsExistingMap = new HashMap<Integer, EmpDocumentChecklistSubDTO>();
				String dupliateCheck = documentVerificationSettingsHelper.checklistDuplicateCheck(headingData,checklistDocumentsExistingMap,checklistDocumentsNewMap);
				if(!Utils.isNullOrEmpty(dupliateCheck)) {
					failureMessage = dupliateCheck;
				}else {
					EmpDocumentChecklistMainDBO heading = null;
					if(headingData.id != 0) {
						heading = documentVerificationSettingsTransaction.getChecklistMainDBOById(headingData.id);
					}
					if(Utils.isNullOrEmpty(heading)) {
						heading = new EmpDocumentChecklistMainDBO();
					}
					heading.documentChecklistName = !Utils.isNullOrEmpty(headingData.headingName) ? headingData.headingName : "";
					heading.documentChecklistDisplayOrder = !Utils.isNullOrEmpty(headingData.headingOrder) ? Integer.parseInt(headingData.headingOrder) : null;
					heading.isForeignNationalDocumentChecklist = !Utils.isNullOrEmpty(headingData.isForeignNationalDocumentChecklist) ?
						Boolean.valueOf(headingData.isForeignNationalDocumentChecklist) : false;
					heading.recordStatus = 'A';
					Set<EmpDocumentChecklistSubDBO> subBoSet = new HashSet<EmpDocumentChecklistSubDBO>();
					if(!Utils.isNullOrEmpty(heading.subChecklist)) {
						for(EmpDocumentChecklistSubDBO subDBO : heading.subChecklist) {
							if(subDBO.recordStatus == 'A') {
								if(checklistDocumentsExistingMap.containsKey(subDBO.id)) {
									EmpDocumentChecklistSubDTO document = checklistDocumentsExistingMap.get(subDBO.id);
									subDBO.documentChecklistSubName = !Utils.isNullOrEmpty(document.documentName) ? document.documentName : "";
									subDBO.documentChecklistSubDisplayOrder = !Utils.isNullOrEmpty(document.documentOrder) ? Integer.parseInt(document.documentOrder) : null;
									checklistDocumentsExistingMap.remove(subDBO.id);
								}else {
									subDBO.recordStatus = 'D';
								}
							}else {
								subDBO.recordStatus = 'D';
							}
							subDBO.modifiedUsersId = Integer.parseInt(userId);
							subBoSet.add(subDBO);
						}
					}
					if(!Utils.isNullOrEmpty(checklistDocumentsNewMap)) {
						for(Entry<String, EmpDocumentChecklistSubDTO> entry : checklistDocumentsNewMap.entrySet()) {
							EmpDocumentChecklistSubDBO sub = new EmpDocumentChecklistSubDBO();
							sub.createdUsersId = Integer.valueOf(userId);
							sub.documentChecklistSubName = !Utils.isNullOrEmpty(entry.getValue().documentName) ? entry.getValue().documentName : "";
							sub.documentChecklistSubDisplayOrder = !Utils.isNullOrEmpty(entry.getValue().documentOrder) ? Integer.parseInt(entry.getValue().documentOrder) : null;
							sub.recordStatus = 'A';
							sub.empDocumentChecklistMainDBO = heading;
							subBoSet.add(sub);	
						}
					}
					heading.subChecklist = subBoSet;
					if(heading.id == null) {
						heading.createdUsersId = Integer.valueOf(userId);
                    }else {
                    	heading.modifiedUsersId = Integer.parseInt(userId);
                    }
					isSaved = documentVerificationSettingsTransaction.saveOrUpdateOrDelete(heading);
					if(!isSaved) {
						failureMessage = "Sorry, Operation failed.";
					}
				}
    		}
			return failureMessage;
    	} catch (Exception e) { }
    	return failureMessage;
    }

	public EmpDocumentChecklistMainDTO edit(String headingId) throws Exception {
		EmpDocumentChecklistMainDTO dto = new EmpDocumentChecklistMainDTO();
		EmpDocumentChecklistMainDBO dbo = documentVerificationSettingsTransaction.getChecklistMainDBOById(Integer.parseInt(headingId));
        if(!Utils.isNullOrEmpty(dbo)) {
            dto.id = dbo.id;
            dto.headingName = !Utils.isNullOrEmpty(dbo.documentChecklistName) ? dbo.documentChecklistName : "";
            dto.headingOrder = !Utils.isNullOrEmpty(dbo.documentChecklistDisplayOrder) ? dbo.documentChecklistDisplayOrder.toString() : "";
            dto.isForeignNationalDocumentChecklist = (!Utils.isNullOrEmpty(dbo.isForeignNationalDocumentChecklist) && dbo.isForeignNationalDocumentChecklist) ? "true" : "false";
            dto.checklistDocuments = new ArrayList<>();
            if(!Utils.isNullOrEmpty(dbo.subChecklist)) {
                for(EmpDocumentChecklistSubDBO sub : dbo.subChecklist){
              	  if(!Utils.isNullOrEmpty(sub.recordStatus) && sub.recordStatus == 'A') {
              		  EmpDocumentChecklistSubDTO document = new EmpDocumentChecklistSubDTO();
                        document.id = sub.id;
                        document.headingId = sub.empDocumentChecklistMainDBO.id;
                        document.documentName = !Utils.isNullOrEmpty(sub.documentChecklistSubName) ? sub.documentChecklistSubName : "";
                        document.documentOrder = !Utils.isNullOrEmpty(sub.documentChecklistSubDisplayOrder) ? sub.documentChecklistSubDisplayOrder.toString() : "";
                        dto.checklistDocuments.add(document);
              	  }
                }
                Collections.sort(dto.checklistDocuments, new Comparator<EmpDocumentChecklistSubDTO>() {
                    @Override
                    public int compare(EmpDocumentChecklistSubDTO o1, EmpDocumentChecklistSubDTO o2) {
                        return Integer.compare(Integer.parseInt(o1.documentOrder), Integer.parseInt(o2.documentOrder));
                    }
                });
            }
        } 
        return dto;
	}

	public boolean delete(String headingId, String userId) throws Exception {
		boolean isDeleted = false;
		EmpDocumentChecklistMainDBO headingDBO = documentVerificationSettingsTransaction.getChecklistMainDBOById(Integer.parseInt(headingId));
		if(!Utils.isNullOrEmpty(headingDBO) && headingDBO.id != 0) {
			headingDBO.modifiedUsersId = Integer.valueOf(userId);
			headingDBO.recordStatus = 'D';
			Set<EmpDocumentChecklistSubDBO> subSet = new HashSet<EmpDocumentChecklistSubDBO>();
			for(EmpDocumentChecklistSubDBO document : headingDBO.subChecklist) {
				document.modifiedUsersId = Integer.valueOf(userId);
				document.recordStatus = 'D';
				subSet.add(document);
			}
			headingDBO.subChecklist = subSet;
			isDeleted = documentVerificationSettingsTransaction.saveOrUpdateOrDelete(headingDBO);
		}
		return isDeleted;
	}
	
}
