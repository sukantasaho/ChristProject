package com.christ.erp.services.handlers.employee.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentUserTitleDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpTitleDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.settings.ErpAssignTitleDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.settings.AssignTitleTransaction;
@Service
public class AssignTitleHandler {

	
	@Autowired
	CommonApiTransaction commonApiTransaction1;
	
	private static volatile AssignTitleHandler assignTitleHandler = null;
	public static AssignTitleHandler getInstance() {
		if(assignTitleHandler == null) {
			assignTitleHandler = new  AssignTitleHandler();
		}
		return assignTitleHandler;
	}
	AssignTitleTransaction transaction = AssignTitleTransaction.getInstance();
	public List<ErpAssignTitleDTO> getGridData() {
		List<ErpAssignTitleDTO> assignTitleDTOs  = null;
		List<ErpCampusDepartmentUserTitleDBO> dboList = null;
		try {
			dboList = transaction.getGridData();
			if(!Utils.isNullOrEmpty(dboList)) { 
				assignTitleDTOs = new ArrayList<ErpAssignTitleDTO>();
				Set<Integer> set = new HashSet<Integer>();
				ErpAssignTitleDTO dto = null;
				var userIds = dboList.stream().map(p-> p.getErpUsersDBO().getId()).collect(Collectors.toSet());				
				var empList = commonApiTransaction1.getEmployeeListmByUserIds(userIds);
				var empMap = empList.stream().collect(Collectors.toMap(a->a.getValue(), a->a));
				for (ErpCampusDepartmentUserTitleDBO dbo : dboList) {	
					if(!Utils.isNullOrEmpty(dbo.erpUsersDBO)) {
						if(!set.contains(dbo.erpUsersDBO.id)) {
							set.add(dbo.erpUsersDBO.id);
							dto = new ErpAssignTitleDTO();
							dto.id = String.valueOf(dbo.erpUsersDBO.id);
//							if(!Utils.isNullOrEmpty(dbo.erpUsersDBO.empDBO)) {
//								ExModelBaseDTO userModel = new ExModelBaseDTO();
//								userModel.id = !Utils.isNullOrEmpty(dbo.erpUsersDBO.empDBO.id) ? String.valueOf(dbo.erpUsersDBO.empDBO.id) : "";
//								userModel.text =!Utils.isNullOrEmpty(dbo.erpUsersDBO.empDBO.empName) ? dbo.erpUsersDBO.empDBO.empName : "";
//								dto.user = userModel;
//							}else {
//								ExModelBaseDTO userModel = new ExModelBaseDTO();
//								userModel.id = !Utils.isNullOrEmpty( dbo.erpUsersDBO.id) ?  String.valueOf(dbo.erpUsersDBO.id) : "";
//								userModel.text =!Utils.isNullOrEmpty( dbo.erpUsersDBO.userName) ?  dbo.erpUsersDBO.userName : "";
//								dto.user = userModel;
//							}
							if(!Utils.isNullOrEmpty(empMap.containsKey(String.valueOf(dbo.erpUsersDBO.id)))) {
									ExModelBaseDTO userModel = new ExModelBaseDTO();
									var empDto = empMap.get(String.valueOf(dbo.erpUsersDBO.id));
									userModel.id = empDto.getValue();
									userModel.text = empDto.getLabel();
									dto.user = userModel;								
							}
							if(!Utils.isNullOrEmpty(dbo.erpTitleDBO)) {
								ExModelBaseDTO titleModel = new ExModelBaseDTO();
								titleModel.id = !Utils.isNullOrEmpty(dbo.erpTitleDBO.id) ? String.valueOf(dbo.erpTitleDBO.id) : "";
								titleModel.text =!Utils.isNullOrEmpty(dbo.erpTitleDBO.titleName) ? dbo.erpTitleDBO.titleName : "";
								dto.empTitle = titleModel;
							}
							if(!Utils.isNullOrEmpty(dbo.erpCampusDepartmentMappingDBO)) {
								StringBuffer capusDept = new StringBuffer();
								String dept = "";
								String campus2 = "";
								StringBuffer campus = new StringBuffer();
							    Set<Integer> child = new HashSet<Integer>();
								for (ErpCampusDepartmentUserTitleDBO dbo2 : dboList) {
									if(dbo.erpUsersDBO.id == dbo2.erpUsersDBO.id) {
									if(!Utils.isNullOrEmpty(dbo2.erpCampusDepartmentMappingDBO.erpDepartmentDBO)) { 
										if(!child.contains(dbo2.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id)) {
											child.add(dbo2.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id);
											if(!Utils.isNullOrEmpty(dept)) {
												capusDept.append(dept+"\n"+campus);
												campus = new StringBuffer();
											}
										}
										if(!Utils.isNullOrEmpty(dbo2.erpCampusDepartmentMappingDBO.erpDepartmentDBO) && !Utils.isNullOrEmpty(dbo2.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName)) {
										dept=dbo2.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName;
										}
									}
									if(!Utils.isNullOrEmpty(dbo2.erpCampusDepartmentMappingDBO.erpCampusDBO) && !Utils.isNullOrEmpty(dbo2.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName)) {
										if(Utils.isNullOrEmpty(campus2)) {
											campus2 = dbo2.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName;
											campus.append("\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0"+dbo2.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName);
										}else {
											campus.append("\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0"+dbo2.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName+"\n");
										}
									}
										}
								 }
								capusDept.append("\n"+dept+"\n"+campus);
								ExModelBaseDTO departmentModel = new ExModelBaseDTO();
								departmentModel.text = capusDept.toString();
								dto.departmentCampus = departmentModel;
								assignTitleDTOs.add(dto);
							}else {
								ExModelBaseDTO departmentModel = new ExModelBaseDTO();
								departmentModel.text = "University level";
								dto.departmentCampus = departmentModel;
								assignTitleDTOs.add(dto);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assignTitleDTOs;
	}
	
	public boolean saveOrUpdate(ErpAssignTitleDTO data, ApiResult<ModelBaseDTO> result, String userId) throws Exception {
		boolean saveOrUpdate = false;
		if (data!=null) {
//			Boolean duplicate  = duplicateCheck(data, result);
	//		if (!duplicate) {
				List<ErpCampusDepartmentUserTitleDBO> dboList = null;						 
				if (!Utils.isNullOrWhitespace(data.user.id)) {
					dboList = transaction.getEmpAssignTitle(Integer.parseInt(data.user.id));		  
				}	
				Set<Integer> campusDepartmentMappingIds = new HashSet<Integer>();
				if (!Utils.isNullOrEmpty(data.checked) && !Utils.isNullOrWhitespace(data.checked.toString()) && data.checked.length>0) {
					campusDepartmentMappingIds = Utils.GetCampusDepartmentMappingIds(data.checked, campusDepartmentMappingIds);					    
				    if (Utils.isNullOrEmpty(data.id)) {
				    	dboList = new ArrayList<ErpCampusDepartmentUserTitleDBO>();
				    	dboList = setNewcddMapDTOsToDBOs(dboList, data, campusDepartmentMappingIds, userId);     
				    } else {
				    	Set<Integer> modifiedIds = new HashSet<Integer>();
				    	modifiedIds = setModifiedcddMapDTOsToDBOs(dboList, data, modifiedIds, campusDepartmentMappingIds, userId);
				    	if (!Utils.isNullOrEmpty(campusDepartmentMappingIds)) {
				    		Set<Integer> newIds = new HashSet<Integer>();
					    	for (Integer newId : campusDepartmentMappingIds) {
				    			if (!modifiedIds.contains(newId)) {
				    				newIds.add(newId);
				    			}
				    		}
					    	dboList = setNewcddMapDTOsToDBOs(dboList, data, newIds, userId);	
				       }
			    	}
		    	}else {
		    		if(!Utils.isNullOrEmpty(dboList)) {
		    			for (ErpCampusDepartmentUserTitleDBO dbo : dboList) {
		    				dbo.recordStatus = 'D';
		    				dbo.modifiedUsersId = Integer.parseInt(userId);
						}
		    		}
		    		ErpCampusDepartmentUserTitleDBO dbo = new ErpCampusDepartmentUserTitleDBO();
		    		if(!Utils.isNullOrEmpty(data.user.id)) {
						ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
						erpUsersDBO.id = (int)Integer.parseInt(data.user.id);
						dbo.erpUsersDBO = erpUsersDBO;
					}
					if(!Utils.isNullOrEmpty(data.empTitle.id)) {
						EmpTitleDBO empTitleDBO = new EmpTitleDBO();
						empTitleDBO.id = (int)Integer.parseInt(data.empTitle.id);
						dbo.erpTitleDBO = empTitleDBO;
					}	
					dbo.recordStatus = 'A';
			    	dbo.createdUsersId = Integer.parseInt(userId);
			    	dboList.add(dbo);
		    	}
				if (data.id == null || data.id.isEmpty()) {
					saveOrUpdate = transaction.saveOrUpdate(dboList);
				} else {
					saveOrUpdate = transaction.saveOrUpdate(dboList);
				}
				
		}
		return saveOrUpdate;
	}
	
	public Set<Integer> setModifiedcddMapDTOsToDBOs(List<ErpCampusDepartmentUserTitleDBO> dboList,
			ErpAssignTitleDTO data, Set<Integer> modifiedIds, Set<Integer> campusDepartmentMappingIds, String userId) {
		if (!Utils.isNullOrEmpty(dboList)) {
	    	for (ErpCampusDepartmentUserTitleDBO dbo : dboList) {	
	    		if(!Utils.isNullOrEmpty(dbo.erpCampusDepartmentMappingDBO)) {
	    			if (!campusDepartmentMappingIds.contains((Integer)dbo.erpCampusDepartmentMappingDBO.id)) {
		    			if(!Utils.isNullOrEmpty(data.empTitle.id)) {
							EmpTitleDBO empTitleDBO = new EmpTitleDBO();
							empTitleDBO.id = (int)Integer.parseInt(data.empTitle.id);
							dbo.erpTitleDBO = empTitleDBO;
						}
		    			dbo.modifiedUsersId = Integer.parseInt(userId);
		    			dbo.recordStatus = 'D';
		    		}else if(campusDepartmentMappingIds.contains((Integer)dbo.erpCampusDepartmentMappingDBO.id)) {
		    			if(!Utils.isNullOrEmpty(data.empTitle.id)) {
							EmpTitleDBO empTitleDBO = new EmpTitleDBO();
							empTitleDBO.id = (int)Integer.parseInt(data.empTitle.id);
							dbo.erpTitleDBO = empTitleDBO;
						}
		    			dbo.modifiedUsersId = Integer.parseInt(userId);
		    			dbo.recordStatus = 'A';
		    			modifiedIds.add(dbo.erpCampusDepartmentMappingDBO.id);
		    		}else {
		    			modifiedIds.add(dbo.erpCampusDepartmentMappingDBO.id);
		    		}
	    		}else {
	    			if(!Utils.isNullOrEmpty(data.empTitle.id)) {
						EmpTitleDBO empTitleDBO = new EmpTitleDBO();
						empTitleDBO.id = (int)Integer.parseInt(data.empTitle.id);
						dbo.erpTitleDBO = empTitleDBO;
					}
	    			if(!Utils.isNullOrEmpty(campusDepartmentMappingIds)) {
	    				for (Integer id : campusDepartmentMappingIds) {
	    					ErpCampusDepartmentMappingDBO campusDeaneryDepartmentDBO = new ErpCampusDepartmentMappingDBO();
	    					campusDeaneryDepartmentDBO.id = (int)id;								    
	    				    dbo.erpCampusDepartmentMappingDBO = campusDeaneryDepartmentDBO;
	    				}
	    			}
	    			modifiedIds.add(dbo.erpCampusDepartmentMappingDBO.id);
	    			dbo.modifiedUsersId = Integer.parseInt(userId);
	    			dbo.recordStatus = 'A';
	    		}
	    	}	
		}
		return modifiedIds;
	}
	
//	public Boolean duplicateCheck(ErpAssignTitleDTO data, ApiResult<ModelBaseDTO> result) throws Exception {
//		boolean duplicateCheck = false;
//		if (data!=null) {
//			List<ErpCampusDepartmentUserTitleDBO> dbos = null;
//			if (!Utils.isNullOrEmpty(data)) {
//				dbos = transaction.duplicateCheck(data);
//				if (!Utils.isNullOrEmpty(dbos) && dbos.size() > 0) {
//				    for (ErpCampusDepartmentUserTitleDBO dbo : dbos) {
//				    	if(!Utils.isNullOrEmpty(dbo.erpUsersDBO)) {
//				    		if(!Utils.isNullOrEmpty(dbo.erpUsersDBO.empDBO)) {
//						    	result.failureMessage = "Duplicate entry for User ("+dbo.erpUsersDBO.empDBO.empName+")";
//						    	break;
//				    		}else {
//				    			result.failureMessage = "Duplicate entry for User ("+dbo.erpUsersDBO.userName+")";
//				    			break;
//				    		}
//				    	}
//					}
//				    duplicateCheck = true;				                        
//				}					  
//			}					
//		}
//	    return duplicateCheck;	
//	}
//	
	public List<ErpCampusDepartmentUserTitleDBO> setNewcddMapDTOsToDBOs(List<ErpCampusDepartmentUserTitleDBO> dboList,
			ErpAssignTitleDTO data, Set<Integer> campusDeanearyDeptIds, String userId) {
		if(!Utils.isNullOrEmpty(campusDeanearyDeptIds)) {	
			 for (Integer id : campusDeanearyDeptIds) {	
				 ErpCampusDepartmentUserTitleDBO dbo = new ErpCampusDepartmentUserTitleDBO();
				 if(!Utils.isNullOrEmpty(data.user.id)) {
						ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
						erpUsersDBO.id = (int)Integer.parseInt(data.user.id);
						dbo.erpUsersDBO = erpUsersDBO;
				}
				if(!Utils.isNullOrEmpty(data.empTitle.id)) {
					EmpTitleDBO empTitleDBO = new EmpTitleDBO();
					empTitleDBO.id = (int)Integer.parseInt(data.empTitle.id);
					dbo.erpTitleDBO = empTitleDBO;
				}
	    	    ErpCampusDepartmentMappingDBO campusDeaneryDepartmentDBO = new ErpCampusDepartmentMappingDBO();
		    	campusDeaneryDepartmentDBO.id = (int)id;								    
		    	dbo.erpCampusDepartmentMappingDBO = campusDeaneryDepartmentDBO;
		    	dbo.recordStatus = 'A';
		    	dbo.createdUsersId = Integer.parseInt(userId);
			    dboList.add(dbo);	
			   }
		}
		return dboList;
	}
	
	public ErpAssignTitleDTO edit(String userBOId) throws Exception {
		List<ErpCampusDepartmentUserTitleDBO> dboList = null;
		ErpAssignTitleDTO dto = new ErpAssignTitleDTO();
		if(!Utils.isNullOrEmpty(userBOId)) {
			dboList = transaction.getEmpAssignTitle((int)Integer.parseInt(userBOId.trim()));
		}
		if(!Utils.isNullOrEmpty(dboList)) {
			ArrayList<String> arrayChecked = new ArrayList<String>();
			List<Integer> editIds=  new ArrayList<Integer>();
			for (ErpCampusDepartmentUserTitleDBO dbo : dboList) {
				if(!Utils.isNullOrEmpty(dbo.id)) {
					dto.id = String.valueOf(dbo.id);
					editIds.add((Integer)dbo.id);
				}
				if(!Utils.isNullOrEmpty(dbo.erpUsersDBO)) {
					ExModelBaseDTO user = new ExModelBaseDTO();
					user.id = String.valueOf(dbo.erpUsersDBO.id); 
					user.text = dbo.erpUsersDBO.userName;
					dto.user = user;
				}
				if(!Utils.isNullOrEmpty(dbo.erpTitleDBO)) {
					ExModelBaseDTO title = new ExModelBaseDTO();
					title.id = String.valueOf(dbo.erpTitleDBO.id); 
					dto.empTitle= title;
				}
				if(!Utils.isNullOrEmpty(dbo.erpCampusDepartmentMappingDBO) && !Utils.isNullOrEmpty(dbo.erpCampusDepartmentMappingDBO.erpCampusDBO)
						&& !Utils.isNullOrEmpty(dbo.erpCampusDepartmentMappingDBO.erpDepartmentDBO)) {
					String chekedId = "";
					chekedId = dbo.erpCampusDepartmentMappingDBO.id+"-"+dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.id+"-"
					           +dbo.erpCampusDepartmentMappingDBO.erpDepartmentDBO.id;
					if(!Utils.isNullOrEmpty(chekedId))
					arrayChecked.add(chekedId);
				}
			}
			dto.checked = Utils.GetStringArray(arrayChecked);
			dto.editIds = editIds;
		}
		return dto;
	}
	
	public boolean delete(String userBoId, String userId) throws Exception {
		boolean delete = false;
		List<ErpCampusDepartmentUserTitleDBO> dbos = null;
		if(!Utils.isNullOrEmpty(userBoId)) {
			dbos = transaction.getEmpAssignTitle((int)Integer.parseInt(userBoId));	
		}
		if(!Utils.isNullOrEmpty(dbos)) {
			for (ErpCampusDepartmentUserTitleDBO bo : dbos) {
				bo.modifiedUsersId = Integer.parseInt(userId);
				bo.recordStatus = 'D';
			}
			delete = transaction.saveOrUpdate(dbos);
		}
		return delete;	
	}
}
