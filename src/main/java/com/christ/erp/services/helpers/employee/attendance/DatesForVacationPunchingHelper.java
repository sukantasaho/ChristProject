package com.christ.erp.services.helpers.employee.attendance;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpDateForVacationPunchingDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpVacationPunchingDatesCDMapDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.attendance.EmpDateForVacationPunchingDTO;

public class DatesForVacationPunchingHelper {
	private static volatile DatesForVacationPunchingHelper datesForVacationPunchingHelper = null;
	public static DatesForVacationPunchingHelper getInstance() {
		if(datesForVacationPunchingHelper==null) {
			datesForVacationPunchingHelper = new DatesForVacationPunchingHelper();
	    }
		return datesForVacationPunchingHelper;
	}
	
	public List<EmpDateForVacationPunchingDTO> setDBOsToDTOs(List<EmpDateForVacationPunchingDBO> dboList,
			List<EmpDateForVacationPunchingDTO> punchingDTOs) {
		if (dboList != null && dboList.size() > 0) {
			punchingDTOs = new ArrayList <EmpDateForVacationPunchingDTO> ();
            for (EmpDateForVacationPunchingDBO dbo: dboList) {
            	 EmpDateForVacationPunchingDTO vacationPunchingDTO = new EmpDateForVacationPunchingDTO();                         	             
                 vacationPunchingDTO.empCategory = new ExModelBaseDTO();
                 vacationPunchingDTO.id = String.valueOf(dbo.id);
                 if (!Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO)) {
                	   vacationPunchingDTO.empCategory.id =  !Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO.id) ? dbo.empEmployeeCategoryDBO.id.toString() : "";
                	   vacationPunchingDTO.empCategory.text = !Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO.employeeCategoryName) ? dbo.empEmployeeCategoryDBO.employeeCategoryName : "";	                        	  
                 } else {
                	   vacationPunchingDTO.empCategory.id = "";
                	   vacationPunchingDTO.empCategory.text = "";
                 }	                        	
		         vacationPunchingDTO.vacationPunchingStartDate =!Utils.isNullOrEmpty(dbo.vacationPunchingStartDate) ? dbo.vacationPunchingStartDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")) : "";
		         vacationPunchingDTO.vacationPunchingEndDate = !Utils.isNullOrEmpty(dbo.vacationPunchingEndDate) ? dbo.vacationPunchingEndDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")) : "";                                                         
		         punchingDTOs.add(vacationPunchingDTO);	                           
            } 
        }
		return punchingDTOs;
	}
	
	public EmpDateForVacationPunchingDBO setDTOsToDBOs(EmpDateForVacationPunchingDBO dbo, EmpDateForVacationPunchingDTO data, String userId) {
		if (dbo==null) {
			dbo = new EmpDateForVacationPunchingDBO();
		} 
	    if (!Utils.isNullOrEmpty(data.empCategory) && !Utils.isNullOrWhitespace(data.empCategory.id)) {
		    EmpEmployeeCategoryDBO categoryDBO = new EmpEmployeeCategoryDBO();
		    categoryDBO.id = Integer.parseInt(data.empCategory.id);
		    dbo.empEmployeeCategoryDBO = categoryDBO;
	    }
		if (!Utils.isNullOrEmpty(data.vacationPunchingStartDate)) {
			dbo.vacationPunchingStartDate = Utils.convertStringDateTimeToLocalDate(data.vacationPunchingStartDate);					
		}
		if (!Utils.isNullOrEmpty(data.vacationPunchingEndDate)) {
			dbo.vacationPunchingEndDate =  Utils.convertStringDateTimeToLocalDate(data.vacationPunchingEndDate);							  
		}
		if (!Utils.isNullOrEmpty(data.description)) {
			dbo.punchingDatesDescription = data.description;
		}
		return dbo;
	}

	public Set<Integer> GetCampusDeanearyDeptIds(EmpDateForVacationPunchingDTO data, Set<Integer> campusDeanearyDeptIds) {
		for (String ids : data.checked) {
			if (ids!=null && !ids.isEmpty()) {
				StringBuffer id = new StringBuffer();										
				for (int i=0;i<ids.length();i++) {
					char ch = ids.charAt(i);
					if(ch=='-') {
						 break;
					}													
					id.append(ch);
				}
				if (!Utils.isNullOrEmpty(id)) {
					campusDeanearyDeptIds.add(Integer.valueOf(id.toString().trim()));
				}
			}
		}
		return campusDeanearyDeptIds;
	}

	public EmpDateForVacationPunchingDBO setNewcddMapDTOsToDBOs(EmpDateForVacationPunchingDBO dbo,
			EmpDateForVacationPunchingDTO data, Set<Integer> campusDeanearyDeptIds, String userId) {
		if(!Utils.isNullOrEmpty(campusDeanearyDeptIds)) {
			 for (Integer id : campusDeanearyDeptIds) {								    	
		    	   EmpVacationPunchingDatesCDMapDBO cddMapDBO = new EmpVacationPunchingDatesCDMapDBO();
		    	   ErpCampusDepartmentMappingDBO campusDeaneryDepartmentDBO = new ErpCampusDepartmentMappingDBO();
				    	campusDeaneryDepartmentDBO.id = id;								    	
				    	cddMapDBO.erpCampusDepartmentMappingDBO = campusDeaneryDepartmentDBO;
				    	cddMapDBO.empDateForVacationPunchingDBO = dbo;
				    	cddMapDBO.createdUsersId = Integer.parseInt(userId);
				    	cddMapDBO.recordStatus = 'A';
				    	dbo.cdMapDBOs.add(cddMapDBO);
			   }
		}
		return dbo;
	}

	public Set<Integer> setModifiedcddMapDTOsToDBOs(EmpDateForVacationPunchingDBO dbo, Set<Integer> childIds,
			Set<Integer> campusDeanearyDeptIds, String userId) {
		if (!Utils.isNullOrEmpty(dbo.cdMapDBOs)) {
	    	for (EmpVacationPunchingDatesCDMapDBO dboChild : dbo.cdMapDBOs) {							    	
	    		if (!campusDeanearyDeptIds.contains(dboChild.erpCampusDepartmentMappingDBO.id) && dboChild.recordStatus == 'A') {
	    			dboChild.modifiedUsersId = Integer.parseInt(userId);
	    			dboChild.recordStatus = 'D';
	    		}else if(campusDeanearyDeptIds.contains(dboChild.erpCampusDepartmentMappingDBO.id) && dboChild.recordStatus == 'D') {
	    			dboChild.modifiedUsersId = Integer.parseInt(userId);
	    			dboChild.recordStatus = 'A';
	    			childIds.add(dboChild.erpCampusDepartmentMappingDBO.id);
	    		}
	    		else {
	    			childIds.add(dboChild.erpCampusDepartmentMappingDBO.id);
	    		}							    										    		
	    	}	
    	}
		return childIds;
	}

	public EmpDateForVacationPunchingDBO createNewcddMapDTOsToDBOs(EmpDateForVacationPunchingDBO dbo, Integer newId,
			String userId) {
		EmpVacationPunchingDatesCDMapDBO cdMapDBO = new EmpVacationPunchingDatesCDMapDBO();
		ErpCampusDepartmentMappingDBO campusDeaneryDepartmentDBO = new ErpCampusDepartmentMappingDBO();
    	campusDeaneryDepartmentDBO.id = newId;								    	
    	cdMapDBO.erpCampusDepartmentMappingDBO = campusDeaneryDepartmentDBO;
    	cdMapDBO.empDateForVacationPunchingDBO = dbo;
    	cdMapDBO.modifiedUsersId = Integer.parseInt(userId);
    	cdMapDBO.recordStatus = 'A';
    	dbo.cdMapDBOs.add(cdMapDBO);
		return dbo;
	}

	public EmpDateForVacationPunchingDTO setEditDBOsToDTOs(EmpDateForVacationPunchingDBO dbo) {
		EmpDateForVacationPunchingDTO dto = null;
		if (!Utils.isNullOrEmpty(dbo)) {
			dto = new EmpDateForVacationPunchingDTO();
			dto.id = String.valueOf(dbo.id);
			dto.empCategory = new ExModelBaseDTO();
			dto.empCategory.id = String.valueOf(dbo.empEmployeeCategoryDBO.id);
			dto.vacationPunchingStartDate = dbo.vacationPunchingStartDate.toString();
			dto.vacationPunchingEndDate = dbo.vacationPunchingEndDate.toString();
			dto.description = dbo.punchingDatesDescription; 
            ArrayList<String> arrayChecked = new ArrayList<String>();
            if (!Utils.isNullOrEmpty(dbo.cdMapDBOs)) {
            	for (EmpVacationPunchingDatesCDMapDBO dboChild : dbo.cdMapDBOs) {
            		if (dboChild.recordStatus=='A') {
            		    String chekedId = "";
            		    if(!Utils.isNullOrEmpty(dboChild.erpCampusDepartmentMappingDBO) && !Utils.isNullOrEmpty(dboChild.erpCampusDepartmentMappingDBO.id)
            		    		&& !Utils.isNullOrEmpty(dboChild.erpCampusDepartmentMappingDBO.erpCampusDBO) && !Utils.isNullOrEmpty(dboChild.erpCampusDepartmentMappingDBO.erpCampusDBO.id)
            		    		&& !Utils.isNullOrEmpty(dboChild.erpCampusDepartmentMappingDBO.erpDepartmentDBO.erpDeaneryDBO) && !Utils.isNullOrEmpty(dboChild.erpCampusDepartmentMappingDBO.erpDepartmentDBO.erpDeaneryDBO.id)) {	            		    	
		            	          chekedId = dboChild.erpCampusDepartmentMappingDBO.id+"-"+dboChild.erpCampusDepartmentMappingDBO.erpCampusDBO.id+"-"+
		            	 		  dboChild.erpCampusDepartmentMappingDBO.erpDepartmentDBO.erpDeaneryDBO.id+"-"+dboChild.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id;
            		    }
		            	if (chekedId!=null && !chekedId.isEmpty()) {
		            		arrayChecked.add(chekedId);			            	
		            	}
            		}
				}
	            if (arrayChecked.size()>0) {
	            	String[] checkedArray = Utils.GetStringArray(arrayChecked);
	            	dto.checked = checkedArray;
	            }	
            }
	    }
		return dto;	
	}
}
