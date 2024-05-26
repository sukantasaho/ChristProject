package com.christ.erp.services.handlers.curriculum.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpDepartmentMissionVisionDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpDepartmentMissionVisionDetailsDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.curriculum.settings.DepartmentMissionVisionDTO;
import com.christ.erp.services.dto.curriculum.settings.DepartmentMissionVisionDetailsDTO;
import com.christ.erp.services.transactions.curriculum.settings.DepartmentVisionAndMissionTransaction;

public class DepartmentVisionAndMissionHandler {
	private static volatile DepartmentVisionAndMissionHandler departmentVisionAndMissionHandler = null;
	
	DepartmentVisionAndMissionTransaction departmentVisionAndMissionTransaction = DepartmentVisionAndMissionTransaction.getInstance();

	public static DepartmentVisionAndMissionHandler getInstance() {
		if (departmentVisionAndMissionHandler == null) {
			departmentVisionAndMissionHandler = new DepartmentVisionAndMissionHandler();
		}
		return departmentVisionAndMissionHandler;
	}

	public List<DepartmentMissionVisionDTO> getGridData() throws Exception {
		List<DepartmentMissionVisionDTO> gridList = null;
		List<ErpDepartmentMissionVisionDBO> list = departmentVisionAndMissionTransaction.getGridData();
		if (!Utils.isNullOrEmpty(list)) {
			gridList = new ArrayList<>();
			for (ErpDepartmentMissionVisionDBO tuple : list ) {
				DepartmentMissionVisionDTO  dto = new DepartmentMissionVisionDTO();
				dto.id = String.valueOf(tuple.id);
				dto.departmentMission = !Utils.isNullOrEmpty(tuple.departmentMission) ?  Utils.htmlToText(tuple.departmentMission) :"";
				dto.departmentVision = !Utils.isNullOrEmpty(tuple.departmentVision) ?  Utils.htmlToText(tuple.departmentVision) :"";
				dto.department = new LookupItemDTO();
				dto.department.value= String.valueOf(tuple.erpDepartmentDBO.id);
				dto.department.label = tuple.erpDepartmentDBO.departmentName.toString();
				gridList.add(dto);
			}
		}
		return gridList;
     }

	public DepartmentMissionVisionDTO edit(String id, Boolean isDepart) throws Exception {
		DepartmentMissionVisionDTO dto = null;
		if (!Utils.isNullOrEmpty(id)) {
			if(Utils.isNullOrEmpty(isDepart)) {
        		isDepart=false;
        	}
			ErpDepartmentMissionVisionDBO dbo  = departmentVisionAndMissionTransaction.edit(Integer.parseInt(id), isDepart);
			if (dbo != null) {
				dto = new DepartmentMissionVisionDTO();
				dto.id = String.valueOf( dbo.id);
				dto.department = new LookupItemDTO();
				dto.department.value = dbo.erpDepartmentDBO.id.toString();
				dto.department.label= dbo.erpDepartmentDBO.departmentName;
				dto.departmentMission = dbo.departmentMission;
				dto.departmentVision = dbo.departmentVision;
				ArrayList<DepartmentMissionVisionDetailsDTO> departmentMissionVisionDetailsDTOList = new ArrayList<>();
				if (!Utils.isNullOrEmpty(dbo.erpDepartmentMissionVisionDetailsDBOSet)) {
					for (ErpDepartmentMissionVisionDetailsDBO sub : dbo.erpDepartmentMissionVisionDetailsDBOSet) {
						if(sub.recordStatus == 'A') {
							DepartmentMissionVisionDetailsDTO subDto = new DepartmentMissionVisionDetailsDTO();
							subDto.id = String.valueOf(sub.id);
							subDto.missionReferenceNumber = sub.missionReferenceNumber;
							subDto.missionCategory = sub.missionCategory;
							subDto.missionStatement = sub.missionStatement;
							departmentMissionVisionDetailsDTOList.add(subDto);
						}
					 }
				}  
				Collections.sort(departmentMissionVisionDetailsDTOList);
				dto.levels = departmentMissionVisionDetailsDTOList;
			}
		}
		return dto;
	}

	public boolean delete(String id, String userId) throws Exception{
		ErpDepartmentMissionVisionDBO dbo = departmentVisionAndMissionTransaction.edit(Integer.parseInt(id),false);
		if (dbo != null) {
			dbo.recordStatus = 'D' ;
			dbo.modifiedUsersId = Integer.parseInt(userId);
			for (ErpDepartmentMissionVisionDetailsDBO sub : dbo.erpDepartmentMissionVisionDetailsDBOSet) {
				sub.recordStatus = 'D';
				sub.modifiedUsersId = Integer.parseInt(userId);
			}
			if (dbo.id != 0) {
				return departmentVisionAndMissionTransaction.saveOrUpdate(dbo);
			}
		}
		return false;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(DepartmentMissionVisionDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<>();
		ErpDepartmentMissionVisionDBO  header = null;
		List<ErpDepartmentMissionVisionDBO>  list= departmentVisionAndMissionTransaction.getDuplicate(data);
		if(!Utils.isNullOrEmpty(list)) {
			result.failureMessage = "Duplicate entry for Department Name: "+data.department.label;
			result.dto = null;
			result.success = false;
		} else {
			if (!Utils.isNullOrWhitespace(data.id)) {
				header = departmentVisionAndMissionTransaction.edit(Integer.parseInt(data.id),false);
			}
			if (header == null) {
				header = new ErpDepartmentMissionVisionDBO();
				header.createdUsersId = Integer.parseInt(userId);
			} else {
				header.modifiedUsersId = Integer.parseInt(userId);
			}
			header.departmentMission = data.departmentMission;
			header.departmentVision = data.departmentVision;
			header.recordStatus = 'A';
			ErpDepartmentDBO dep = new ErpDepartmentDBO();
			dep.id = Integer.parseInt(data.department.value);
			dep.departmentName = data.department.label;
			header.erpDepartmentDBO = dep;
			Set<ErpDepartmentMissionVisionDetailsDBO> erpDepartmentMissionVisionDetailsDBOUpdate = new HashSet<ErpDepartmentMissionVisionDetailsDBO>();
			Set<ErpDepartmentMissionVisionDetailsDBO> existDBOSet= header.erpDepartmentMissionVisionDetailsDBOSet;
			Map<Integer,ErpDepartmentMissionVisionDetailsDBO> existDBOMap = new HashMap<Integer, ErpDepartmentMissionVisionDetailsDBO>();
			if (!Utils.isNullOrEmpty(existDBOSet)) {
				existDBOSet.forEach(dbo-> {
					if (dbo.recordStatus=='A') {
						existDBOMap.put(dbo.id, dbo);
					}
				});
			}
			if(!Utils.isNullOrEmpty(data.levels)) {
				for (DepartmentMissionVisionDetailsDTO sub : data.levels) {
					ErpDepartmentMissionVisionDetailsDBO dbo = null;
					if (!Utils.isNullOrWhitespace(sub.id) && existDBOMap.containsKey(Integer.parseInt(sub.id))) {	
		    			dbo = existDBOMap.get((Integer.parseInt(sub.id)));
		    			dbo.modifiedUsersId = Integer.parseInt(userId);
		    			existDBOMap.remove(Integer.parseInt(sub.id));
	                } else {
						dbo = new ErpDepartmentMissionVisionDetailsDBO();
	        			dbo.createdUsersId = Integer.parseInt(userId);
					}
					dbo.erpDepartmentMissionVisionDBO = header;
					dbo.missionReferenceNumber = sub.missionReferenceNumber;
					dbo.missionCategory =!Utils.isNullOrEmpty(sub.missionCategory) ?  sub.missionCategory :null;
					dbo.missionStatement = !Utils.isNullOrEmpty(sub.missionStatement) ?  sub.missionStatement :null;
					dbo.recordStatus = 'A';
					erpDepartmentMissionVisionDetailsDBOUpdate.add(dbo);
				}
			}
			if (!Utils.isNullOrEmpty(existDBOMap)) {
				existDBOMap.forEach((entry, value)-> {
					value.modifiedUsersId = Integer.parseInt(userId);
					value.recordStatus = 'D';
					erpDepartmentMissionVisionDetailsDBOUpdate.add(value);
				});
			}
			header.erpDepartmentMissionVisionDetailsDBOSet = erpDepartmentMissionVisionDetailsDBOUpdate;	
			if (departmentVisionAndMissionTransaction.saveOrUpdate(header)) {
				result.success = true;
			}
		}
		return result;
	}
	
}
