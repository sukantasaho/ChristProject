package com.christ.erp.services.handlers.employee.leave;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDetailsDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentDTO;
import com.christ.erp.services.helpers.employee.leave.LeaveAllotmentHelper;
import com.christ.erp.services.transactions.employee.leave.LeaveAllotmentTransaction;

public class LeaveAllotmentHandler {

	private static volatile LeaveAllotmentHandler  leaveAllotmentHandler=null;

	public static LeaveAllotmentHandler getInstance() {
		if(leaveAllotmentHandler==null) {
			leaveAllotmentHandler = new LeaveAllotmentHandler();
		}
		return leaveAllotmentHandler;
	}
	LeaveAllotmentTransaction leaveAllotmentTransaction=LeaveAllotmentTransaction.getInstance();
	LeaveAllotmentHelper leaveAllotmentHelper=LeaveAllotmentHelper.getInstance();

	public List<EmpLeaveAllotmentDTO> getGridData() throws Exception {
		List<EmpLeaveAllotmentDTO> dto = null;
		List<Tuple> mappings = leaveAllotmentTransaction.getGridData();
		if(mappings != null && mappings.size() > 0) {
			dto = new ArrayList<>();
			for(Tuple mapping : mappings) {
				EmpLeaveAllotmentDTO mappingInfo = new EmpLeaveAllotmentDTO();
				mappingInfo.id =  mapping.get("id").toString();
				mappingInfo.leaveCategoryId =  mapping.get("id").toString();
				mappingInfo.leaveCategoryName = mapping.get("LeaveCategoryName").toString();
				mappingInfo.month = Utils.getMonthName(Integer.valueOf(mapping.get("Month").toString()));
				dto.add(mappingInfo);
			}
		}
		return dto;
	}

	public EmpLeaveAllotmentDTO edit(String id) throws Exception {
		List<EmpLeaveCategoryAllotmentDetailsDBO> allotmentMappings = null;
		EmpLeaveAllotmentDTO dto = null;
		allotmentMappings = leaveAllotmentTransaction.edit(id);
		if(!Utils.isNullOrEmpty(allotmentMappings)) {
			dto = leaveAllotmentHelper.setDBOsToDTOs(allotmentMappings,dto);
		}
		return dto;
	}

	public boolean delete(String id, String userId) throws Exception {
		Boolean deleted = false;
		EmpLeaveCategoryAllotmentDBO dbo = null;
		if(id != null && !id.isEmpty()) {
			//deleted = leaveAllotmentTransaction.delete(leaveCategoryName, userId);
//			dbo = leaveAllotmentTransaction.getLeaveCategory(leaveCategoryName);
//			if(!Utils.isNullOrEmpty(dbo)) {
//				dbo.modifiedUsersId = Integer.parseInt(userId);
//				dbo.recordStatus = 'D';
//				if(!Utils.isNullOrEmpty(dbo.empLeaveCategoryAllotmentDetailsDBO)) {
//					for (EmpLeaveCategoryAllotmentDetailsDBO dboList : dbo.empLeaveCategoryAllotmentDetailsDBO) {
//						dboList.modifiedUsersId = Integer.parseInt(userId);
//						dboList.recordStatus = 'D';
//						dbo.empLeaveCategoryAllotmentDetailsDBO.add(dboList);
//					}
			deleted = leaveAllotmentTransaction.delete(id,userId);
				}			
		return deleted;
	}

	public boolean saveOrUpdate(EmpLeaveAllotmentDTO data, ApiResult<ModelBaseDTO> result, String userId) throws Exception {
		boolean saveOrUpdate = false;
		boolean duplicated = false;
		EmpLeaveCategoryAllotmentDBO dbo = null;
		duplicated = duplicateCheck(data, result);
		if (!duplicated) {
			if(!Utils.isNullOrEmpty(data.id) && data.allomentIds.size() > 0) {
				if(!Utils.isNullOrEmpty(data.id)) {
					dbo = leaveAllotmentTransaction.getLeaveCategory(Integer.parseInt(data.id));
					HashSet<Integer> parentAllIds = new HashSet<Integer>();
					HashSet<Integer> parentActiveIds = new HashSet<Integer>();
					dbo = leaveAllotmentHelper.setOldDTOsToDBOs(data, dbo, parentAllIds, parentActiveIds, userId);
					data.leaveCategoryId = data.id;
					dbo = leaveAllotmentHelper.setOldAddOrRemoveDTOsToDBOs(data, dbo, parentAllIds, parentActiveIds, userId);
					saveOrUpdate = leaveAllotmentTransaction.saveLeaveCategoryAndAlloted(dbo);  
				}      
			}else {
				Set<EmpLeaveCategoryAllotmentDetailsDBO> setData = null;	
				dbo = leaveAllotmentHelper.setNewDTOsToDBOs(setData, dbo, data, userId);
				saveOrUpdate = leaveAllotmentTransaction.saveLeaveCategoryAndAlloted(dbo);
			}
		}
		return saveOrUpdate;
	}

	public Boolean duplicateCheck(EmpLeaveAllotmentDTO data,  ApiResult<ModelBaseDTO> result) throws Exception {
		Boolean duplicate = false;
		List<EmpLeaveCategoryAllotmentDetailsDBO> duplicateChecking = null;
		duplicateChecking = leaveAllotmentTransaction.duplicateCheck(data);
		if(!Utils.isNullOrEmpty(duplicateChecking)) {
			if(duplicateChecking.size() > 0) {
				for(EmpLeaveCategoryAllotmentDetailsDBO leaveAllotmentDBO : duplicateChecking) {
					if(!String.valueOf(leaveAllotmentDBO.recordStatus).isEmpty()) {
						if(leaveAllotmentDBO.recordStatus == 'A') {
							duplicate = true;
							result.failureMessage = "Duplicate entry for Leave Category "+leaveAllotmentDBO.empLeaveCategoryAllotmentDBO.empLeaveCategoryAllotmentName;
						}
					}
				}
			}
		}

		return duplicate;
	}
}
