package com.christ.erp.services.helpers.employee.leave;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDetailsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentListDTO;
import java.util.Comparator;

public class LeaveAllotmentHelper {
	private static volatile LeaveAllotmentHelper  leaveAllotmentHelper=null;
	public static LeaveAllotmentHelper getInstance() {
        if(leaveAllotmentHelper==null) {
        	leaveAllotmentHelper = new LeaveAllotmentHelper();
        }
        return leaveAllotmentHelper;
    }
	public EmpLeaveAllotmentDTO setDBOsToDTOs(List<EmpLeaveCategoryAllotmentDetailsDBO> allotmentMappings,
			EmpLeaveAllotmentDTO dto) {
        dto = new EmpLeaveAllotmentDTO();
        // use for matching records when its Update
        dto.allomentIds = new HashSet<Integer>();
        List<EmpLeaveAllotmentListDTO> leaveAllotmentListDTOs = new ArrayList<>();
        for (EmpLeaveCategoryAllotmentDetailsDBO dbo : allotmentMappings) {
            if((dbo.recordStatus == 'A' && dbo.empLeaveTypeDBO.recordStatus == 'A' && dbo.empLeaveCategoryAllotmentDBO.recordStatus == 'A')
            		|| dbo.recordStatus == 'A') {
                if(Utils.isNullOrEmpty(dto.id)) {
                    dto.id = String.valueOf(dbo.empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentId);
                    dto.leaveCategoryId = String.valueOf(dbo.empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentId);
                    dto.leaveCategoryName = dbo.empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentName.toString();
                    dto.month = dbo.empLeaveCategoryAllotmentDBO.leaveIinitializeMonth.toString();
                    dto.leaveAllotment = new ArrayList<EmpLeaveAllotmentListDTO>();
                }
                dto.allomentIds.add((Integer)dbo.id);
                EmpLeaveAllotmentListDTO leaveallotment = new EmpLeaveAllotmentListDTO();
                leaveallotment.isApplicable = dbo.isApplicable;
                leaveallotment.allottedLeaves = dbo.allottedLeaves;
                leaveallotment.accumulatedLeave = dbo.accumulatedLeave;
                leaveallotment.leaveallotedId = (Integer)dbo.id;
                if(dbo.empLeaveTypeDBO != null && dbo.empLeaveTypeDBO.id != 0 && !String.valueOf(dbo.empLeaveTypeDBO.id).isEmpty()) {
                    ExModelBaseDTO exbaseLeaveType = new ExModelBaseDTO();
                    exbaseLeaveType.id = String.valueOf(dbo.empLeaveTypeDBO.id);
                    exbaseLeaveType.text = dbo.empLeaveTypeDBO.leaveTypeName;
                    leaveallotment.leavetypeId = (int) Integer.parseInt(exbaseLeaveType.id);
                    leaveallotment.leavetypeName = exbaseLeaveType.text;
                } else {
                    leaveallotment.leavetypeId = Integer.parseInt("");
                    leaveallotment.leavetypeName = "";
                }
                ExModelBaseDTO addLeaveType = new ExModelBaseDTO();
                if(dbo.addToLeaveType != null && dbo.addToLeaveType.id != 0	&& !String.valueOf(dbo.addToLeaveType.id).isEmpty()) {
                    addLeaveType.id = String.valueOf(dbo.addToLeaveType.id);
                    leaveallotment.addToLeaveType = addLeaveType;
                }else {
                    addLeaveType.id = "";
                    addLeaveType.text = "";
                    leaveallotment.addToLeaveType = addLeaveType;
                }
                if(!Utils.isNullOrEmpty(dbo.displayOrder))
                	leaveAllotmentListDTOs.add(leaveallotment);
                leaveallotment.displayOrder = dbo.displayOrder;
                leaveallotment.isInitializationRequired = dbo.isInitializationRequired;
                dto.leaveAllotment.add(leaveallotment);
            }
        }
        leaveAllotmentListDTOs.sort(Comparator.comparing(o -> o.displayOrder));
        dto.leaveAllotment=leaveAllotmentListDTOs;
		return dto;
	}
	
	public List<EmpLeaveCategoryAllotmentDetailsDBO> setUpadateDTOsToDBOs(EmpLeaveCategoryAllotmentDBO dbo, 
			EmpLeaveAllotmentDTO data, String userId) {
     	EmpLeaveCategoryAllotmentDetailsDBO empLeaveCategoryAllotmentDetailsDBO = null;
     	List<EmpLeaveCategoryAllotmentDetailsDBO> childDBOList = new ArrayList<EmpLeaveCategoryAllotmentDetailsDBO>();
     	if(!Utils.isNullOrEmpty(data.leaveAllotment)) {
             for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) { 
            	 empLeaveCategoryAllotmentDetailsDBO = new EmpLeaveCategoryAllotmentDetailsDBO();
                 if(data.leaveAllotment != null && !data.leaveAllotment.isEmpty()) {
                     EmpLeaveTypeDBO leaveType = new EmpLeaveTypeDBO();
                     leaveType.id = leaveallot.leavetypeId;
                     empLeaveCategoryAllotmentDetailsDBO.empLeaveTypeDBO = leaveType;
                     empLeaveCategoryAllotmentDetailsDBO.isApplicable = leaveallot.isApplicable;
                     if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
                    	 empLeaveCategoryAllotmentDetailsDBO.allottedLeaves = leaveallot.allottedLeaves;
                         if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty() && leaveallot.allottedLeaves >= leaveallot.accumulatedLeave) {
                        	 empLeaveCategoryAllotmentDetailsDBO.accumulatedLeave = leaveallot.accumulatedLeave;
                         }else {
                             if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty())
                            	 empLeaveCategoryAllotmentDetailsDBO.accumulatedLeave = leaveallot.allottedLeaves;
                         }
                         ExModelBaseDTO modelBase = new ExModelBaseDTO();
                         modelBase.id = leaveallot.addToLeaveType.id;
                         if(!Utils.isNullOrEmpty(modelBase.id)) {
                             EmpLeaveTypeDBO addleaveType = new EmpLeaveTypeDBO();
                             addleaveType.id = Integer.parseInt(modelBase.id);
                             empLeaveCategoryAllotmentDetailsDBO.addToLeaveType = addleaveType;
                         }else {
                        	 empLeaveCategoryAllotmentDetailsDBO.addToLeaveType = null;
                         }
                     }else {
                    	 empLeaveCategoryAllotmentDetailsDBO.accumulatedLeave = null;
                    	 empLeaveCategoryAllotmentDetailsDBO.allottedLeaves = null;
                    	 empLeaveCategoryAllotmentDetailsDBO.addToLeaveType = null;
                     }   
                     empLeaveCategoryAllotmentDetailsDBO.displayOrder = leaveallot.displayOrder;
                     empLeaveCategoryAllotmentDetailsDBO.isInitializationRequired = leaveallot.isInitializationRequired;
                     empLeaveCategoryAllotmentDetailsDBO.createdUsersId = Integer.parseInt(userId);
                     empLeaveCategoryAllotmentDetailsDBO.recordStatus = 'A';
                     empLeaveCategoryAllotmentDetailsDBO.empLeaveCategoryAllotmentDBO = dbo;
                     childDBOList.add(empLeaveCategoryAllotmentDetailsDBO);
                 }
             }
     	}
		return childDBOList;
	}
	
	public EmpLeaveCategoryAllotmentDBO setNewDTOsToDBOs(Set<EmpLeaveCategoryAllotmentDetailsDBO> setData, EmpLeaveCategoryAllotmentDBO dbo, 
			EmpLeaveAllotmentDTO data, String userId) {
		EmpLeaveCategoryAllotmentDetailsDBO empLeaveCategoryAllotmentDetailsDBO = null;
		if(Utils.isNullOrEmpty(dbo)) {
			dbo = new EmpLeaveCategoryAllotmentDBO();
			dbo.empLeaveCategoryAllotmentName = data.leaveCategoryName;
			dbo.leaveIinitializeMonth = Integer.parseInt(data.month);
			dbo.createdUsersId = Integer.parseInt(userId);
			dbo.recordStatus = 'A';
	     	dbo.empLeaveCategoryAllotmentDetailsDBO = new HashSet<EmpLeaveCategoryAllotmentDetailsDBO>();
	     	if(!Utils.isNullOrEmpty(data.leaveAllotment)) {
	             for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) { 
	            	 if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
	            		 empLeaveCategoryAllotmentDetailsDBO = new EmpLeaveCategoryAllotmentDetailsDBO();
		                 if(data.leaveAllotment != null && !data.leaveAllotment.isEmpty()) {
		                     EmpLeaveTypeDBO leaveType = new EmpLeaveTypeDBO();
		                     leaveType.id = leaveallot.leavetypeId;
		                     empLeaveCategoryAllotmentDetailsDBO.empLeaveTypeDBO = leaveType;
		                     empLeaveCategoryAllotmentDetailsDBO.isApplicable = leaveallot.isApplicable;
		                     if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
		                    	 empLeaveCategoryAllotmentDetailsDBO.allottedLeaves = leaveallot.allottedLeaves;
		                         if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty() && leaveallot.allottedLeaves >= leaveallot.accumulatedLeave) {
		                        	 empLeaveCategoryAllotmentDetailsDBO.accumulatedLeave = leaveallot.accumulatedLeave;
		                         }else {
		                             if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty())
		                            	 empLeaveCategoryAllotmentDetailsDBO.accumulatedLeave = leaveallot.allottedLeaves;
		                         }
		                         ExModelBaseDTO modelBase = new ExModelBaseDTO();
		                         modelBase.id = leaveallot.addToLeaveType.id;
		                         if(!Utils.isNullOrEmpty(modelBase.id)) {
		                             EmpLeaveTypeDBO addleaveType = new EmpLeaveTypeDBO();
		                             addleaveType.id = Integer.parseInt(modelBase.id);
		                             empLeaveCategoryAllotmentDetailsDBO.addToLeaveType = addleaveType;
		                         }else {
		                        	 empLeaveCategoryAllotmentDetailsDBO.addToLeaveType = null;
		                         }
		                     }
//		                     else {
//		                    	 empLeaveCategoryAllotmentDetailsDBO.accumulatedLeave = null;
//		                    	 empLeaveCategoryAllotmentDetailsDBO.allottedLeaves = null;
//		                    	 empLeaveCategoryAllotmentDetailsDBO.addToLeaveType = null;
//		                     }
		                     empLeaveCategoryAllotmentDetailsDBO.displayOrder = leaveallot.displayOrder;
		                     empLeaveCategoryAllotmentDetailsDBO.isInitializationRequired = leaveallot.isInitializationRequired;
		                     empLeaveCategoryAllotmentDetailsDBO.createdUsersId = Integer.parseInt(userId);
		                     empLeaveCategoryAllotmentDetailsDBO.recordStatus = 'A';
		                     empLeaveCategoryAllotmentDetailsDBO.empLeaveCategoryAllotmentDBO = dbo;
		                     dbo.empLeaveCategoryAllotmentDetailsDBO.add(empLeaveCategoryAllotmentDetailsDBO);
		                 }
	            	 }
	             }
	     	}
		}
		return dbo;
	}
	
	public EmpLeaveCategoryAllotmentDBO setOldDTOsToDBOs(EmpLeaveAllotmentDTO data,
		EmpLeaveCategoryAllotmentDBO dbo, HashSet<Integer> parentAllIds, HashSet<Integer> parentActiveIds, String userId) {
	    dbo.empLeaveCategoryAllotmentName = data.leaveCategoryName;
        dbo.leaveIinitializeMonth = Integer.parseInt(data.month);
        dbo.createdUsersId = Integer.parseInt(userId);
        dbo.recordStatus = 'A';
		for(EmpLeaveCategoryAllotmentDetailsDBO detailsDBO : dbo.empLeaveCategoryAllotmentDetailsDBO) {
            // added unique All Parent records id's
            parentAllIds.add(detailsDBO.id);
            if(!Utils.isNullOrEmpty(data.leaveAllotment)) {
                for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) {
                    if(!Utils.isNullOrEmpty(leaveallot.leaveallotedId) && leaveallot.leaveallotedId.equals(detailsDBO.id)) {
                        // added unique parent Active records id's (record Status='A')
                        parentActiveIds.add(leaveallot.leaveallotedId);
                        detailsDBO.empLeaveTypeDBO.id = leaveallot.leavetypeId;
                        detailsDBO.isApplicable = leaveallot.isApplicable;
                        if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
                            if(leaveallot.allottedLeaves != null && !String.valueOf(leaveallot.allottedLeaves).isEmpty())
                            	detailsDBO.allottedLeaves = leaveallot.allottedLeaves;
                            else
                            	detailsDBO.allottedLeaves = null;
                            if(leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty()) {
                                if(leaveallot.allottedLeaves >= leaveallot.accumulatedLeave)
                                	detailsDBO.accumulatedLeave = leaveallot.accumulatedLeave;
                                else
                                	detailsDBO.accumulatedLeave = leaveallot.allottedLeaves;
                            }else {
                            	detailsDBO.accumulatedLeave = null;
                            }
                            if(leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty() && leaveallot.addToLeaveType != null
                                    && leaveallot.addToLeaveType.id != null && !String.valueOf(leaveallot.addToLeaveType.id).isEmpty()) {
                                EmpLeaveTypeDBO addleaveType = new EmpLeaveTypeDBO();
                                addleaveType.id = (int) Integer.parseInt(leaveallot.addToLeaveType.id);
                                detailsDBO.addToLeaveType = addleaveType;
                            }else {
                            	detailsDBO.addToLeaveType = null;
                            }
                            detailsDBO.displayOrder = leaveallot.displayOrder;
                            detailsDBO.isInitializationRequired = leaveallot.isInitializationRequired;
                            detailsDBO.recordStatus = 'A';
                            detailsDBO.modifiedUsersId = Integer.parseInt(userId);
                            detailsDBO.empLeaveCategoryAllotmentDBO = dbo;
                            dbo.empLeaveCategoryAllotmentDetailsDBO.add(detailsDBO);
                        }
//                        else {
//                        	detailsDBO.accumulatedLeave = null;
//                        	detailsDBO.allottedLeaves = null;
//                        	detailsDBO.addToLeaveType = null;
//                        }
                        
                    }
                }
            }
        }
		return dbo;
	}
	
	public EmpLeaveCategoryAllotmentDBO setOldAddOrRemoveDTOsToDBOs(EmpLeaveAllotmentDTO data,
			EmpLeaveCategoryAllotmentDBO dbo, HashSet<Integer> parentAllIds, HashSet<Integer> parentActiveIds, String userId) {
	    // finding deactivated parent record id's
        HashSet<Integer> parentDectiveIds = new HashSet<Integer>();
        if(parentAllIds.size() > 0) {
            for(Integer id2 : parentAllIds) {
                if(!parentActiveIds.contains(id2))
                    parentDectiveIds.add(id2);
            }
        }
        // Deactive Parent Record in child table statusRecords 'A' to 'D' or 'D' to 'A'
        for(EmpLeaveCategoryAllotmentDetailsDBO oldDetailsDBO : dbo.empLeaveCategoryAllotmentDetailsDBO) {
            for(Integer id3 : parentDectiveIds) {
                if(oldDetailsDBO.id == (int)id3) {
                	oldDetailsDBO.recordStatus = 'D';
                	oldDetailsDBO.modifiedUsersId = Integer.parseInt(userId);
                	dbo.empLeaveCategoryAllotmentDetailsDBO.add(oldDetailsDBO);
                }
            }
        }
        // creating new record in child
        for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) {
            if(leaveallot.leaveallotedId == null || String.valueOf(leaveallot.leaveallotedId).isEmpty()) {
                if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
                	EmpLeaveCategoryAllotmentDetailsDBO newDetailsDBO = new EmpLeaveCategoryAllotmentDetailsDBO();
                	EmpLeaveCategoryAllotmentDBO dboCategory = new EmpLeaveCategoryAllotmentDBO();
                	dboCategory.empLeaveCategoryAllotmentId = (int)Integer.parseInt(data.leaveCategoryId);                                          
                	newDetailsDBO.empLeaveCategoryAllotmentDBO = dboCategory;
                    EmpLeaveTypeDBO leaveType = new EmpLeaveTypeDBO();
                    leaveType.id = leaveallot.leavetypeId;
                    newDetailsDBO.empLeaveTypeDBO = leaveType;
                    newDetailsDBO.isApplicable = leaveallot.isApplicable;
                	newDetailsDBO.allottedLeaves = leaveallot.allottedLeaves;
                    if(leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty() && leaveallot.allottedLeaves >= leaveallot.accumulatedLeave) {
                    	newDetailsDBO.accumulatedLeave = leaveallot.accumulatedLeave;
                    }else {
                        if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty())
                        	newDetailsDBO.accumulatedLeave = leaveallot.allottedLeaves;
                    }
                    if(leaveallot.addToLeaveType.id != null && !leaveallot.addToLeaveType.id.isEmpty()) {
                        ExModelBaseDTO modelBase = new ExModelBaseDTO();
                        modelBase.id = leaveallot.addToLeaveType.id;
                        EmpLeaveTypeDBO addleaveType = new EmpLeaveTypeDBO();
                        addleaveType.id = Integer.parseInt(modelBase.id);
                        newDetailsDBO.addToLeaveType = addleaveType;
                    }
                    newDetailsDBO.displayOrder = leaveallot.displayOrder;
                    newDetailsDBO.isInitializationRequired = leaveallot.isInitializationRequired;
                    newDetailsDBO.recordStatus = 'A';
                    newDetailsDBO.createdUsersId = Integer.parseInt(userId);
                    newDetailsDBO.empLeaveCategoryAllotmentDBO = dbo;
                    dbo.empLeaveCategoryAllotmentDetailsDBO.add(newDetailsDBO);
                }
//                else {
//                	newDetailsDBO.accumulatedLeave = null;
//                	newDetailsDBO.allottedLeaves = null;
//                	newDetailsDBO.addToLeaveType = null;
//                }
            }
        }
		return dbo;
	}
	
}
