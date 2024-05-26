package com.christ.erp.services.handlers.employee.attendance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpDateForVacationPunchingDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.attendance.EmpDateForVacationPunchingDTO;
import com.christ.erp.services.helpers.employee.attendance.DatesForVacationPunchingHelper;
import com.christ.erp.services.transactions.employee.attendance.DatesForVacationPunchingTransation;

public class DatesForVacationPunchingHandler {
	private static volatile DatesForVacationPunchingHandler datesForVacationPunchingHandler = null;
	public static DatesForVacationPunchingHandler getInstance() {
        if(datesForVacationPunchingHandler==null) {
        	datesForVacationPunchingHandler = new DatesForVacationPunchingHandler();
        }
        return datesForVacationPunchingHandler;
	}
	DatesForVacationPunchingTransation datesForVacationPunchingTransation = DatesForVacationPunchingTransation.getInstance();
	DatesForVacationPunchingHelper datesForVacationPunchingHelper = DatesForVacationPunchingHelper.getInstance();    
	
	public List<EmpDateForVacationPunchingDTO> getGridData() {
		List<EmpDateForVacationPunchingDTO> punchingDTOs = null;
		List<EmpDateForVacationPunchingDBO> dboList = null;
		try {
			dboList = datesForVacationPunchingTransation.getGridData();
			punchingDTOs = datesForVacationPunchingHelper.setDBOsToDTOs(dboList, punchingDTOs);	  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return punchingDTOs;
	}

	public boolean saveOrUpdate(EmpDateForVacationPunchingDTO data, ApiResult<ModelBaseDTO> result, String userId) throws Exception {
		boolean saveOrUpdate = false;
		if (data!=null) {
			Boolean duplicate  = duplicateCheck(data, result);
			if (!duplicate) {
				EmpDateForVacationPunchingDBO dbo = null;						 
				if (!Utils.isNullOrWhitespace(data.id)) {
					dbo = datesForVacationPunchingTransation.getEmpDateForVacationPunchingDTO(Integer.parseInt(data.id));		  
				}
				dbo = datesForVacationPunchingHelper.setDTOsToDBOs(dbo, data, userId);
				Set<Integer> campusDeanearyDeptIds = new HashSet<Integer>();
				if (!Utils.isNullOrEmpty(data.checked)) {
					campusDeanearyDeptIds = datesForVacationPunchingHelper.GetCampusDeanearyDeptIds(data, campusDeanearyDeptIds);					    
				    if (Utils.isNullOrEmpty(data.id)) {			
				    	dbo =  datesForVacationPunchingHelper.setNewcddMapDTOsToDBOs(dbo, data, campusDeanearyDeptIds, userId);     
				    } else {
				    	Set<Integer> childIds = new HashSet<Integer>();
				    	childIds = datesForVacationPunchingHelper.setModifiedcddMapDTOsToDBOs(dbo, childIds, campusDeanearyDeptIds, userId);
				    	if (!Utils.isNullOrEmpty(campusDeanearyDeptIds)) {
					    	for (Integer newId : campusDeanearyDeptIds) {
				    			if (!childIds.contains(newId)) {
				    				dbo = datesForVacationPunchingHelper.createNewcddMapDTOsToDBOs(dbo, newId, userId);
									saveOrUpdate = datesForVacationPunchingTransation.saveOrUpdate(dbo);	
				    			}
				    		}
				       }
			    	}
		    	}
				dbo.recordStatus = 'A';
				if (data.id == null || data.id.isEmpty()) {
					dbo.createdUsersId = Integer.parseInt(userId);
					saveOrUpdate = datesForVacationPunchingTransation.saveOrUpdate(dbo);
				} else {
					dbo.modifiedUsersId = Integer.parseInt(userId);
					saveOrUpdate = datesForVacationPunchingTransation.saveOrUpdate(dbo);
				}
			}	
		}
		return saveOrUpdate;
	}
		
    public Boolean duplicateCheck(EmpDateForVacationPunchingDTO data, ApiResult<ModelBaseDTO> dateForVacationPunching) throws Exception {
    	boolean duplicateCheck = false;
		if (data!=null) {
			List<EmpDateForVacationPunchingDBO> dateForVacationPunchingDBOs = null;
			if (!Utils.isNullOrEmpty(data.empCategory) && !Utils.isNullOrEmpty(data.vacationPunchingStartDate) && !Utils.isNullOrEmpty(data.vacationPunchingEndDate)) {
				dateForVacationPunchingDBOs = datesForVacationPunchingTransation.duplicateCheck(data);
				if (dateForVacationPunchingDBOs.size() > 0) {
				    for (EmpDateForVacationPunchingDBO empDateForVacationPunchingDBO : dateForVacationPunchingDBOs) {
				    	dateForVacationPunching.failureMessage = "Duplicate entry for Date for Vacation Punching  ("+empDateForVacationPunchingDBO.empEmployeeCategoryDBO.employeeCategoryName+")";
					}
				    duplicateCheck = true;				                        
				}					  
			}					
		}
	    return duplicateCheck;						 	
    }

	public boolean delete(String id, String userId) throws Exception {
		return datesForVacationPunchingTransation.delete(id, userId);
	}

	public EmpDateForVacationPunchingDTO edit(String id) throws Exception {
		EmpDateForVacationPunchingDBO dbo = null;
		EmpDateForVacationPunchingDTO dto = null;
		if(!Utils.isNullOrEmpty(id)) {
			dbo = datesForVacationPunchingTransation.getEmpDateForVacationPunchingDTO(Integer.parseInt(id.trim()));
		}
		if(!Utils.isNullOrEmpty(dbo))
		dto = datesForVacationPunchingHelper.setEditDBOsToDTOs(dbo);
		return dto;
	}
}
