package com.christ.erp.services.handlers.employee.settings;
import com.christ.erp.services.common.Utils;

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateGroupDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.attendance.LetterTemplatesDTO;
import com.christ.erp.services.transactions.employee.settings.LetterTemplateTransaction;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LetterTemplateHandler {
    private static volatile LetterTemplateHandler letterTemplateHandler = null;
    LetterTemplateTransaction letterTemplateTransaction = LetterTemplateTransaction.getInstance();

    public static LetterTemplateHandler getInstance() {
        if(letterTemplateHandler==null) {
        	letterTemplateHandler = new LetterTemplateHandler();
        }
        return letterTemplateHandler;
    }

    public boolean saveOrUpdate(LetterTemplatesDTO data, String userId) throws Exception {
    	boolean flag=false;
        if(!Utils.isNullOrEmpty(data)) {
        	boolean isGroupTemplateAdd = false;
        	List <ErpTemplateGroupDBO> list =null;
			list = letterTemplateTransaction.getDuplicate(data);
			Integer maxCount=0;
			String count = "";
			if(data.id!=null && !data.id.isEmpty()) {
				ErpTemplateDBO grpId = letterTemplateTransaction.edit(String.valueOf(data.id));
				Tuple c = letterTemplateTransaction.getCount(grpId.erpTemplateGroupDBO.id);
				maxCount = Integer.parseInt(c.get("counter").toString());
				if(maxCount == 0) {
					count = "";
				}
				else {
					count = maxCount.toString();
				}
			}
			if (list != null && list.size() != 0) {
				int i = 0;
				for(ErpTemplateGroupDBO dbo: list) {
					if(i==0) {
						i++;
						ErpTemplateDBO	erpTemplateDBO = new ErpTemplateDBO();
						if(Utils.isNullOrWhitespace(data.id) == false) {
							erpTemplateDBO = letterTemplateTransaction.getId(Integer.parseInt(data.id));
						}
						erpTemplateDBO.templateDescription = data.templateDescription;
						erpTemplateDBO.templateType = data.letterType;
						EmpEmployeeCategoryDBO emp = new EmpEmployeeCategoryDBO();
						emp.id = Integer.parseInt(data.empCategory.id);
						erpTemplateDBO.empEmployeeCategoryDBO = emp;
						erpTemplateDBO.templateType = "Letter" ;
						erpTemplateDBO.recordStatus = 'A';
						erpTemplateDBO.templateName = data.templateName;
//						int subId= 6;
//						ErpModuleSubDBO sub =new ErpModuleSubDBO();
//						sub.id = subId;
//						erpTemplateDBO.erpModuleSubDBO=sub;
						if(!Utils.isNullOrEmpty(data.id) == false) {
							ErpTemplateGroupDBO dboId = letterTemplateTransaction.getDBO(data.groupName);
							Tuple c = letterTemplateTransaction.getCount(dboId.id);
							maxCount = Integer.parseInt(c.get("counter").toString());
							if(maxCount == 0) {
								count = "";
							}
							else {
								count = maxCount.toString();
							}
							erpTemplateDBO.templateCode = data.groupCode + count;
						}
						ErpTemplateGroupDBO erpTemplateGroupDBO =new ErpTemplateGroupDBO();
						erpTemplateGroupDBO.id = dbo.id;
						erpTemplateDBO.erpTemplateGroupDBO = erpTemplateGroupDBO;
						erpTemplateDBO.templateContent = data.ckTemplate;
						erpTemplateDBO.availableTags =data.ckTemplateForTags;
						erpTemplateDBO.createdUsersId= Integer.parseInt(userId);
						isGroupTemplateAdd = true ;
						if(erpTemplateDBO!=null) {
							erpTemplateDBO.modifiedUsersId = Integer.parseInt(userId);
	                    }
						flag = letterTemplateTransaction.saveOrUpdate(erpTemplateDBO);
	                    break;
					}
				}
			 }
			 if(!isGroupTemplateAdd) {
				 ErpTemplateGroupDBO temp = new ErpTemplateGroupDBO();
				 Set<ErpTemplateDBO> sub = new HashSet<>();
				 if(Utils.isNullOrWhitespace(data.id) == false) {
					 temp = letterTemplateTransaction.getGroupId(Integer.parseInt(data.id));
					 temp.modifiedUsersId = Integer.parseInt(userId);
					
				 }
				 else {
					 temp = new ErpTemplateGroupDBO();
					 temp.createdUsersId= Integer.parseInt(userId);
					 ErpTemplateGroupDBO dboId = letterTemplateTransaction.getDBO(data.groupName);
					 if(dboId!=null) {
						 Tuple c = letterTemplateTransaction.getCount(dboId.id);
						 maxCount = Integer.parseInt(c.get("counter").toString());
						 if(maxCount == 0) {
							 count = "";
						 }
						 else {
							 count = maxCount.toString();
						 }
					 }
				 }
				 temp.templateGroupCode = data.groupCode;
				 temp.templateGroupName = data.groupName;
				 temp.recordStatus = 'A';	
				 ErpTemplateDBO	erpTemplateDBO = new ErpTemplateDBO();
				 erpTemplateDBO.templateDescription = data.templateDescription;
				 erpTemplateDBO.templateType = data.letterType;
				 EmpEmployeeCategoryDBO emp = new EmpEmployeeCategoryDBO();
				 emp.id = Integer.parseInt(data.empCategory.id);
				 erpTemplateDBO.empEmployeeCategoryDBO = emp;
				 erpTemplateDBO.templateType = "Letter" ;
				 erpTemplateDBO.recordStatus = 'A';
				 erpTemplateDBO.templateName = data.templateName;
				 erpTemplateDBO.templateCode = data.groupCode + count;
				 erpTemplateDBO.erpTemplateGroupDBO = temp;
				 erpTemplateDBO.templateContent = data.ckTemplate;
				 erpTemplateDBO.availableTags =data.ckTemplateForTags;
				 erpTemplateDBO.createdUsersId= Integer.parseInt(userId);
				 int subId= 6;
//				 ErpModuleSubDBO mod =new ErpModuleSubDBO();
//				 mod.id = subId;
//				 erpTemplateDBO.erpModuleSubDBO=mod;
				 sub.add(erpTemplateDBO);
				 temp.erpTemplateDBOSet = sub;
                 flag =  letterTemplateTransaction.groupSaveOrUpdate(temp);                
			}			 
        }
        return flag;
    }

    public List<LetterTemplatesDTO> getGridData() throws Exception {
        List<LetterTemplatesDTO> gridList = null;
        List <ErpTemplateDBO> list = letterTemplateTransaction.getGridData();
        if(!Utils.isNullOrEmpty(list)) {
        	gridList= new ArrayList<LetterTemplatesDTO>();
            for (ErpTemplateDBO dbo: list) {
            	LetterTemplatesDTO dto = new LetterTemplatesDTO(); 
                dto.id = String.valueOf(dbo.id);
                dto.empCategory = new ExModelBaseDTO();
                if (!Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO)) {
                	dto.empCategory.id =  !Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO.id) ? dbo.empEmployeeCategoryDBO.id.toString() : "";
                    dto.empCategory.text = !Utils.isNullOrEmpty(dbo.empEmployeeCategoryDBO.employeeCategoryName) ? dbo.empEmployeeCategoryDBO.employeeCategoryName : "";	                        	  
                } 	
			    dto.templateGroupCode = !Utils.isNullOrEmpty(dbo.templateCode) ?(dbo.templateCode) : "";
			    dto.templateName = !Utils.isNullOrEmpty(dbo.templateName) ?(dbo.templateName) : "";
			    dto.groupName = !Utils.isNullOrEmpty(dbo.erpTemplateGroupDBO.templateGroupName) ? (dbo.erpTemplateGroupDBO.templateGroupName):"";
			    dto.groupCode= !Utils.isNullOrEmpty(dbo.erpTemplateGroupDBO.templateGroupCode) ? (dbo.erpTemplateGroupDBO.templateGroupCode):"";
			    gridList.add(dto);	                           
            }           
        }
        return gridList;
    }

    public boolean delete(String id) throws Exception {
    	boolean flag=false;
    	flag = letterTemplateTransaction.delete(id);    		
        return flag;        
    }
    
    public LetterTemplatesDTO edit(String id) throws Exception {
    	ErpTemplateDBO bo = letterTemplateTransaction.edit(id);
    	LetterTemplatesDTO dto = new LetterTemplatesDTO();
    	if(bo != null) {
    		dto.id = String.valueOf(bo.id);
    		dto.empCategory = new ExModelBaseDTO();
    		dto.isEdit=true;
    		if (!Utils.isNullOrEmpty(bo.empEmployeeCategoryDBO)) {
    			dto.empCategory.id = String.valueOf(bo.empEmployeeCategoryDBO.id);
    		}
    		if (!Utils.isNullOrEmpty(bo.erpTemplateGroupDBO.templateGroupCode)) {
    			dto.groupCode = bo.erpTemplateGroupDBO.templateGroupCode;
    		}
    		if (!Utils.isNullOrEmpty(bo.erpTemplateGroupDBO.templateGroupName)) {
    			dto.groupName = bo.erpTemplateGroupDBO.templateGroupName;
    		}
    		if (!Utils.isNullOrEmpty(bo.templateName)) {
    			dto.templateName =bo.templateName;
    		}
    		if (!Utils.isNullOrEmpty(bo.templateDescription)) {
    			dto.templateDescription=bo.templateDescription;
    		}
    		if (!Utils.isNullOrEmpty(bo.availableTags)) {
    			dto.ckTemplateForTags=bo.availableTags;
    		}
    		if (!Utils.isNullOrEmpty(bo.templateType)) {
    			dto.letterType=bo.templateType;
    		}
    		if (!Utils.isNullOrEmpty(bo.templateContent)) {
    			dto.ckTemplate=bo.templateContent;
    		}
    		if (!Utils.isNullOrEmpty(bo.templateCode)) {
    			dto.templateGroupCode = bo.templateCode;
    		}
    		if(!Utils.isNullOrEmpty(bo.erpTemplateGroupDBO.templateGroupName) && bo.erpTemplateGroupDBO.templateGroupName.equalsIgnoreCase("Appointment Letter")) {
    			dto.letterType = "appointment"	;
    			dto.isAppointmentOrOffer = true;				
    		}
    		else if(!Utils.isNullOrEmpty(bo.erpTemplateGroupDBO.templateGroupName) && bo.erpTemplateGroupDBO.templateGroupName.equalsIgnoreCase("Offer Letter")) {
    			dto.letterType = "offer";
    			dto.isAppointmentOrOffer = true;
    		}
    		else {
    			dto.letterType = "other";
    			dto.isAppointmentOrOffer = false;
    		}

    	}
    	return dto;

    }
}
