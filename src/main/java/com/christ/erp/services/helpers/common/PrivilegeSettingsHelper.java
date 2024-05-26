package com.christ.erp.services.helpers.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.SysFunctionDBO;
import com.christ.erp.services.dbobjects.common.SysMenuDBO;
import com.christ.erp.services.dbobjects.common.SysRoleDBO;
import com.christ.erp.services.dbobjects.common.SysRoleFunctionMapDBO;
import com.christ.erp.services.dbobjects.common.SysRoleGroupDBO;
import com.christ.erp.services.dbobjects.common.SysUserFunctionOverrideDBO;
import com.christ.erp.services.dbobjects.common.SysUserRoleMapDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.common.ErpCampusDTO;
import com.christ.erp.services.dto.common.ErpUsersDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.MenuScreenDTO;
import com.christ.erp.services.dto.common.ModuleDTO;
import com.christ.erp.services.dto.common.ModuleSubDTO;
import com.christ.erp.services.dto.common.RoleOrUserPermissionDTO;
import com.christ.erp.services.dto.common.SysFunctionDTO;
import com.christ.erp.services.dto.common.SysRoleDTO;
import com.christ.erp.services.dto.common.SysUserRoleMapDTO;
import com.christ.erp.services.security.JWTPasswordEncoder;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
@Service
public class PrivilegeSettingsHelper {
	
//    @Autowired
//    private JWTPasswordEncoder passwordEncoder;
	
	@Autowired
	private CommonApiTransaction commonApiTransaction1;
	
	private static volatile PrivilegeSettingsHelper privilegeSettingsHelper = null;

	public static PrivilegeSettingsHelper getInstance() {
		if (privilegeSettingsHelper == null) {
			privilegeSettingsHelper = new PrivilegeSettingsHelper();
		}
		return privilegeSettingsHelper;
	}

	public void convertSysRoleDBOtoDTO(SysRoleDBO dbo, SysRoleDTO dto) {
		if(!Utils.isNullOrEmpty(dbo.id)) {
			dto.id = String.valueOf(dbo.id);
		}
		if(!Utils.isNullOrEmpty(dbo.roleName)) {
			dto.roleName = dbo.roleName;
		}
		if(!Utils.isNullOrEmpty(dbo.sysRoleGroup) && !Utils.isNullOrEmpty(dbo.sysRoleGroup.id)) {
			LookupItemDTO lookupItemDTO = new LookupItemDTO();
			lookupItemDTO.value = String.valueOf(dbo.sysRoleGroup.id);
			lookupItemDTO.label = String.valueOf(dbo.sysRoleGroup.roleGroupName);
			dto.roleGroup = lookupItemDTO;
		}
	}

	public void convertSysRoleDTOtoDBO(SysRoleDBO sysRoleDBO, SysRoleDTO data) {
		if(!Utils.isNullOrEmpty(data.roleName)) {
			sysRoleDBO.roleName = data.roleName;
		}
		if(!Utils.isNullOrEmpty(data.roleGroup) && !Utils.isNullOrEmpty(data.roleGroup.value)) {
			SysRoleGroupDBO roleGroup = new SysRoleGroupDBO();
			roleGroup.id = Integer.parseInt(data.roleGroup.value.trim());
			sysRoleDBO.sysRoleGroup = roleGroup;
		}
		sysRoleDBO.recordStatus = 'A';
	}

	public void convertErpUsersDTOtoDBO(ErpUsersDBO erpUsersDBO, ErpUsersDTO data, String userId, List<ErpUsersCampusDBO> userCampusList, Map<Integer, ErpUsersCampusDBO> userCampusPermissionMap,
			Map<Integer, List<SysUserFunctionOverrideDBO>> userCampusForAddPermissionMap) {
	//	boolean isOverridePermissionGratedAvailble = false;
		Set<Integer> userActiveCampusIds = new HashSet<Integer>();
		Set<Integer> campusDeletedIds = new HashSet<Integer>();
		if(!Utils.isNullOrEmpty(data.getErpUserName())) {
			erpUsersDBO.setUserName(data.getErpUserName());
		}
		if(!Utils.isNullOrEmpty(data.userName)) {
			erpUsersDBO.loginId = data.userName.trim();
		}
		JWTPasswordEncoder passwordEncoder = new JWTPasswordEncoder();
		if(!Utils.isNullOrEmpty(data.newPassword) && !Utils.isNullOrEmpty(data.id)) {
//			erpUsersDBO.loginPassword = data.newPassword;
			if(!erpUsersDBO.getLoginPassword().equals(data.getNewPassword())) {
				erpUsersDBO.loginPassword = passwordEncoder.encode(data.newPassword);
			}
		} else {
			erpUsersDBO.loginPassword = passwordEncoder.encode(data.newPassword);
		}
		if(!Utils.isNullOrEmpty(data.employee) && !Utils.isNullOrEmpty(data.employee.value)) {
			EmpDBO empDBO = new EmpDBO();
			empDBO.id = Integer.parseInt(data.employee.value.trim());
			erpUsersDBO.empDBO = new EmpDBO();
			erpUsersDBO.empDBO = empDBO;
			erpUsersDBO.erpCampusDepartmentMappingDBO = null;
		}
		if(!Utils.isNullOrEmpty(data.isActive)) {
			erpUsersDBO.recordStatus = Boolean.valueOf(data.isActive) == true ? 'A':'I';
		}
		if(!Utils.isNullOrEmpty(data.getValidDate())) {
//			erpUsersDBO.userValidUpto = Utils.convertStringDateTimeToLocalDateTime(data.validDate);
			erpUsersDBO.userValidUpto = data.getValidDate();

		} else {
			erpUsersDBO.userValidUpto = null;
		}
		if(!Utils.isNullOrEmpty(data.department)) {
//			ErpDepartmentDBO departmentDBO = new ErpDepartmentDBO(); 
//			departmentDBO.id = Integer.parseInt(data.department.value);
//			erpUsersDBO.erpDepartmentDBO = departmentDBO;
			erpUsersDBO.empDBO = null;
			erpUsersDBO.setErpCampusDepartmentMappingDBO(new ErpCampusDepartmentMappingDBO());
			erpUsersDBO.getErpCampusDepartmentMappingDBO().setId(Integer.parseInt(data.department.value));
			
		}
		if(!Utils.isNullOrEmpty(data.roleArray)) {
			Map<Integer, SysUserRoleMapDTO> mapRoleIdCampuseListMap = new HashMap<Integer, SysUserRoleMapDTO>();
			if(!Utils.isNullOrEmpty(data.roleArray)) {
				for (SysUserRoleMapDTO sysUserRoleMapDTO  : data.roleArray) {
					mapRoleIdCampuseListMap.put(Integer.parseInt(sysUserRoleMapDTO.role.value),sysUserRoleMapDTO);
				}
			}
			SysUserRoleMapDBO sysUserRoleMapDBO = null;
			boolean isExist = false; Integer roleId = null;
			Map<Integer, Set<Integer>> roleCampuseList = new HashMap<Integer, Set<Integer>>();
			Set<Integer> campusList = null, roleList = new HashSet<Integer>();
		    Collections.sort(erpUsersDBO.sysUserRoleMapDBOs);
			if(!Utils.isNullOrEmpty(mapRoleIdCampuseListMap) && !Utils.isNullOrEmpty(erpUsersDBO.sysUserRoleMapDBOs)) {
				for (SysUserRoleMapDBO sysUserRoleMapDBO2 : erpUsersDBO.sysUserRoleMapDBOs) {
					isExist = false;
					if(!Utils.isNullOrEmpty(sysUserRoleMapDBO2.sysRoleDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO2.erpCampusDBO)) {
						if(!roleList.contains(sysUserRoleMapDBO2.sysRoleDBO.id)) {;
							if(!Utils.isNullOrEmpty(campusList) && !Utils.isNullOrEmpty(roleId)) {
								roleCampuseList.put(roleId, campusList);
							}
							roleId = sysUserRoleMapDBO2.sysRoleDBO.id;
							roleList.add(roleId);
							campusList = new HashSet<Integer>();
						}
						SysUserRoleMapDTO roleMapDTO = mapRoleIdCampuseListMap.get(sysUserRoleMapDBO2.sysRoleDBO.id);
						if(!Utils.isNullOrEmpty(roleMapDTO)) {
							for (LookupItemDTO itemDTO : roleMapDTO.campusList) {
								if(!Utils.isNullOrEmpty(itemDTO.value) && Integer.parseInt(itemDTO.value) == sysUserRoleMapDBO2.erpCampusDBO.id) {
									isExist = true;
									campusList.add(Integer.parseInt(itemDTO.value));
									userActiveCampusIds.add(Integer.parseInt(itemDTO.value));
//									if(campusDeletedIds.contains(Integer.parseInt(itemDTO.value))) {
//										campusDeletedIds.remove(Integer.parseInt(itemDTO.value));
//									}
								}
							}
							if(!isExist) {
								sysUserRoleMapDBO2.modifiedUsersId = Integer.parseInt(userId);
								sysUserRoleMapDBO2.recordStatus = 'D';
								campusList.add(sysUserRoleMapDBO2.erpCampusDBO.id);
								campusDeletedIds.add(sysUserRoleMapDBO2.erpCampusDBO.id);
							}
						}else {
							sysUserRoleMapDBO2.modifiedUsersId = Integer.parseInt(userId);
							sysUserRoleMapDBO2.recordStatus = 'D';
							campusDeletedIds.add(sysUserRoleMapDBO2.getErpCampusDBO().getId());
						}
					}										
				}
				if(!Utils.isNullOrEmpty(roleId) && !Utils.isNullOrEmpty(campusList))
				roleCampuseList.put(roleId, campusList);
			}
			

			
			if(Utils.isNullOrEmpty(erpUsersDBO.sysUserRoleMapDBOs)) {
				erpUsersDBO.sysUserRoleMapDBOs = new LinkedList<SysUserRoleMapDBO>();
			}
			for (SysUserRoleMapDTO sysUserRoleMapDTO : data.roleArray) {
				for (LookupItemDTO campus : sysUserRoleMapDTO.campusList) {
					if(!Utils.isNullOrEmpty(campus.value) && !Utils.isNullOrEmpty(sysUserRoleMapDTO.role.value)) {
						Set<Integer> campusList1 = null;
						if(!Utils.isNullOrEmpty(roleCampuseList))
							campusList1 = roleCampuseList.get(Integer.parseInt(sysUserRoleMapDTO.role.value));
						if(!Utils.isNullOrEmpty(campusList1) && !campusList1.contains(Integer.parseInt(campus.value))) {
							sysUserRoleMapDBO = new SysUserRoleMapDBO();
							sysUserRoleMapDBO = createSysUserRoleMapDBO(sysUserRoleMapDBO,campus.value, erpUsersDBO, sysUserRoleMapDTO.role, userId);
							erpUsersDBO.sysUserRoleMapDBOs.add(sysUserRoleMapDBO);
							//ErpUserCampus 
							userActiveCampusIds.add(sysUserRoleMapDBO.getErpCampusDBO().getId());
							if(!userCampusPermissionMap.containsKey(sysUserRoleMapDBO.getErpCampusDBO().getId())) {
								ErpUsersCampusDBO userCampus = new ErpUsersCampusDBO();
								userCampus.setErpUsersDBO(erpUsersDBO);
								userCampus.setErpCampusDBO(sysUserRoleMapDBO.getErpCampusDBO());
								userCampus.setIsPreferred(true);
								userCampus.setCreatedUsersId(Integer.parseInt(userId));
								userCampus.setRecordStatus('A');
								userCampusList.add(userCampus);
								userCampusPermissionMap.put(userCampus.getErpCampusDBO().getId(), userCampus);
							}
						}else if((Utils.isNullOrEmpty(campusList1))){
							sysUserRoleMapDBO = new SysUserRoleMapDBO();
							sysUserRoleMapDBO = createSysUserRoleMapDBO(sysUserRoleMapDBO,campus.value, erpUsersDBO, sysUserRoleMapDTO.role, userId);
							erpUsersDBO.sysUserRoleMapDBOs.add(sysUserRoleMapDBO);
							//ErpUserCampus
							userActiveCampusIds.add(sysUserRoleMapDBO.getErpCampusDBO().getId());
							if(!userCampusPermissionMap.containsKey(sysUserRoleMapDBO.getErpCampusDBO().getId())) {
								ErpUsersCampusDBO userCampus = new ErpUsersCampusDBO();
								userCampus.setErpUsersDBO(erpUsersDBO);
								userCampus.setErpCampusDBO(sysUserRoleMapDBO.getErpCampusDBO());
								userCampus.setIsPreferred(true);
								userCampus.setCreatedUsersId(Integer.parseInt(userId));
								userCampus.setRecordStatus('A');
								userCampusList.add(userCampus);
							    userCampusPermissionMap.put(userCampus.getErpCampusDBO().getId(), userCampus);
							}
						}
					}
				}
			}
		}
		//ErpUserCampus 
		campusDeletedIds.forEach( campusId ->{
			if(!userCampusForAddPermissionMap.containsKey(campusId) && !userActiveCampusIds.contains(campusId)) {
				 if(userCampusPermissionMap.containsKey(campusId)) {
					ErpUsersCampusDBO campus = userCampusPermissionMap.get(campusId);
					campus.setRecordStatus('D');
					campus.setModifiedUsersId(Integer.parseInt(userId));
					userCampusList.add(campus);
				 }
			}  
		});
	//	return isOverridePermissionGratedAvailble;
	}

	private  SysUserRoleMapDBO createSysUserRoleMapDBO(SysUserRoleMapDBO sysUserRoleMapDBO, String value, ErpUsersDBO erpUsersDBO, LookupItemDTO role, String userId) {
		SysRoleDBO  sysRoleDBO = new SysRoleDBO();
		sysRoleDBO.id = (!Utils.isNullOrEmpty(role) && !Utils.isNullOrEmpty(role.value))?Integer.parseInt(role.value):null;
		sysUserRoleMapDBO.sysRoleDBO = sysRoleDBO;
		ErpCampusDBO erpCampusDBO = new ErpCampusDBO();
		erpCampusDBO.id = !Utils.isNullOrEmpty(value) ?Integer.parseInt(value):null;
		erpCampusDBO.createdUsersId = Integer.parseInt(userId);
		sysUserRoleMapDBO.erpCampusDBO = erpCampusDBO;
		sysUserRoleMapDBO.recordStatus = 'A';
		sysUserRoleMapDBO.erpUsersDBO = erpUsersDBO;
		return sysUserRoleMapDBO;
	}

	public ErpUsersDTO convertErpUsersDBOtoDTO(ErpUsersDBO dbo, ErpUsersDTO dto) {
		if(!Utils.isNullOrEmpty(dbo.id)) {
			dto.id = String.valueOf(dbo.id);
			var employee = commonApiTransaction1.getEmployeemByUserId(dbo.getId());
			if(!Utils.isNullOrEmpty(employee)) {
				LookupItemDTO empItemDTO = new LookupItemDTO();
				empItemDTO.value = employee.getValue();
				empItemDTO.label = employee.getLabel();
				dto.employee = empItemDTO;
				dto.department = null;
			}		
		}
		if(!Utils.isNullOrEmpty(dbo.loginId)) {
			dto.userName = dbo.loginId;
		}
		if(!Utils.isNullOrEmpty(dbo.getUserName())) {
			dto.setErpUserName(dbo.getUserName());
		}
		if(!Utils.isNullOrEmpty(dbo.loginPassword)) {
			dto.newPassword = dbo.loginPassword;
		}
		if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO())) {
			LookupItemDTO departItemDTO = new LookupItemDTO();
			departItemDTO.value = String.valueOf(dbo.getErpCampusDepartmentMappingDBO().getId());
			departItemDTO.label = dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName();
			dto.department = departItemDTO;
			dto.employee = null;
		}
//		if(!Utils.isNullOrEmpty(dbo.empDBO)) {
//			LookupItemDTO empItemDTO = new LookupItemDTO();
//			empItemDTO.value = String.valueOf(dbo.empDBO.id);
//			empItemDTO.label = dbo.empDBO.empName;
//			dto.employee = empItemDTO;
//			dto.department = null;
//		}
		if(!Utils.isNullOrEmpty(dbo.userValidUpto)) {
//			dto.validDate =  Utils.convertLocalDateTimeToStringDateTime( dbo.userValidUpto);
			dto.setValidDate(dbo.getUserValidUpto());
		} else {
//			dto.validDate = null;
			dto.setValidDate(null);
		}
		if(!Utils.isNullOrEmpty(dbo.recordStatus)) {
			if(dbo.recordStatus == 'A'){
				dto.isActive = true;
			}else if(dbo.recordStatus == 'I'){
				dto.isActive = false;
			}
		}
		if(!Utils.isNullOrEmpty(dbo.sysUserRoleMapDBOs)) {
			dto.roleArray = new ArrayList<>();
			SysUserRoleMapDTO sysUserRoleMapDTO = null;
			LookupItemDTO  lookupItemDTO = null;
			List<LookupItemDTO> campusList = null;
			List<Integer> list = new ArrayList<Integer>();
			Map<Integer,Map<Integer,String>> map = new HashMap<Integer, Map<Integer,String>>();
			Map<Integer,String> roleMap = new HashMap<Integer, String>();
			Map<Integer, String> CampusMap = null;
			Integer roleId = null;
			for (SysUserRoleMapDBO sysUserRoleMapDBO : dbo.sysUserRoleMapDBOs) {
				if(!Utils.isNullOrEmpty(sysUserRoleMapDBO.erpUsersDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO.sysRoleDBO) 
						&& !Utils.isNullOrEmpty(sysUserRoleMapDBO.erpCampusDBO)) {
					if(!list.contains(sysUserRoleMapDBO.sysRoleDBO.id)) {
						list.add(sysUserRoleMapDBO.sysRoleDBO.id);
						if(!Utils.isNullOrEmpty(roleId) && !Utils.isNullOrEmpty(CampusMap)) {
							map.put(roleId, CampusMap);
						}
						CampusMap = new HashMap<Integer, String>();
						roleId = sysUserRoleMapDBO.sysRoleDBO.id;
					}
					CampusMap.put(sysUserRoleMapDBO.erpCampusDBO.id, sysUserRoleMapDBO.erpCampusDBO.campusName);
					roleMap.put(sysUserRoleMapDBO.sysRoleDBO.id, sysUserRoleMapDBO.sysRoleDBO.roleName);
				}
			}
			if(!Utils.isNullOrEmpty(CampusMap)) {
				map.put(roleId, CampusMap);
			}
			for (Entry<Integer, Map<Integer, String>> roleMapEntry : map.entrySet()) {
				if(!Utils.isNullOrEmpty(roleMapEntry.getKey())) {
					sysUserRoleMapDTO = new SysUserRoleMapDTO();
					lookupItemDTO = new LookupItemDTO();
	                lookupItemDTO.value = String.valueOf(roleMapEntry.getKey());
	                lookupItemDTO.label = roleMap.get(roleMapEntry.getKey());
	                sysUserRoleMapDTO.role = lookupItemDTO;
	                campusList = new ArrayList<LookupItemDTO>();
				    for (Entry<Integer, String> campus : roleMapEntry.getValue().entrySet()) {
				    	lookupItemDTO = new LookupItemDTO();
						lookupItemDTO.value = String.valueOf(campus.getKey());
						lookupItemDTO.label = campus.getValue();
						campusList.add(lookupItemDTO);
					}
				    sysUserRoleMapDTO.campusList = campusList;
				    if(!Utils.isNullOrEmpty(sysUserRoleMapDTO)) 
						dto.roleArray.add(sysUserRoleMapDTO);
				}
			}
			if(!Utils.isNullOrEmpty(dto.roleArray))
			Collections.sort(dto.roleArray);
		}
		return dto;
	}

	public RoleOrUserPermissionDTO convertSysRolePermissionDBOtoDTO(List<SysMenuDBO> sysMenuDBOs,
			RoleOrUserPermissionDTO rolePermissionDTO, Set<SysRoleFunctionMapDBO> sysRoleFunctionMapDBOs, String roleId) {
		Map<Integer, SysRoleFunctionMapDBO> functionRoleMap = null;
		if(!Utils.isNullOrEmpty(sysRoleFunctionMapDBOs)) {
			functionRoleMap = new HashMap<Integer, SysRoleFunctionMapDBO>();
			for (SysRoleFunctionMapDBO sysRoleFunctionMapDBO : sysRoleFunctionMapDBOs) {
				if(!Utils.isNullOrEmpty(sysRoleFunctionMapDBO.sysFunctionDBO) && !Utils.isNullOrEmpty(sysRoleFunctionMapDBO.sysFunctionDBO.recordStatus) && sysRoleFunctionMapDBO.sysFunctionDBO.recordStatus == 'A' 
						&& !Utils.isNullOrEmpty(sysRoleFunctionMapDBO.recordStatus) && sysRoleFunctionMapDBO.recordStatus == 'A') {
					functionRoleMap.put(sysRoleFunctionMapDBO.sysFunctionDBO.id, sysRoleFunctionMapDBO);
				}
			}
		}else {
			sysRoleFunctionMapDBOs = new HashSet<SysRoleFunctionMapDBO>();
		}
		ModuleDTO moduleDTO = null;
		ModuleSubDTO moduleSubDTO = null;
		MenuScreenDTO menuScreenDTO = null;
		SysFunctionDTO sysFunctionDTO = null;
		List<Integer> uniqueModuleId = new ArrayList<Integer>();
		rolePermissionDTO = new RoleOrUserPermissionDTO();
		SysRoleDTO sysRoleDTO = new SysRoleDTO();
		sysRoleDTO.id = roleId;
		rolePermissionDTO.roleName = sysRoleDTO;
		rolePermissionDTO.screens = new ArrayList<ModuleDTO>();
		if(!Utils.isNullOrEmpty(sysMenuDBOs)) {
			int cuuerntId = 0;  boolean isCheckPermissionGrated = false;
			for (SysMenuDBO sysMenuDBO : sysMenuDBOs) {
				if(!Utils.isNullOrEmpty(sysMenuDBO.sysMenuModuleSubDBO) && !uniqueModuleId.contains(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id)) {
					uniqueModuleId.add(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id);
					cuuerntId = sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id;
					if(!Utils.isNullOrEmpty(moduleDTO)) {
						rolePermissionDTO.screens.add(moduleDTO);
					}
					moduleDTO = new ModuleDTO();
					moduleDTO.id = String.valueOf(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id);
					moduleDTO.text = sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.moduleName;
					moduleDTO.menus = new ArrayList<ModuleSubDTO>();
				}
				if(!Utils.isNullOrEmpty(sysMenuDBO.sysMenuModuleSubDBO) && !Utils.isNullOrEmpty(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO)
						&& cuuerntId == sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id) {
					moduleSubDTO = new ModuleSubDTO();
					moduleSubDTO.permissions = new ArrayList<SysFunctionDTO>();
					menuScreenDTO = new MenuScreenDTO();
					menuScreenDTO.ID = String.valueOf(sysMenuDBO.id);
					menuScreenDTO.Text = String.valueOf(sysMenuDBO.menuScreenName);
					
					for (SysFunctionDBO sysFunctionDBO : sysMenuDBO.sysFunctionDBOSet) {
						if(!Utils.isNullOrEmpty(sysFunctionDBO.recordStatus) && sysFunctionDBO.recordStatus == 'A') {
							sysFunctionDTO = new SysFunctionDTO();
							sysFunctionDTO.id = String.valueOf(sysFunctionDBO.id);
							sysFunctionDTO.permissionName = sysFunctionDBO.functionName;
							sysFunctionDTO.setFunctionDescription(sysFunctionDBO.getFunctionDescription());
							SysRoleFunctionMapDBO functionMapDBO = null;
							if(!Utils.isNullOrEmpty(functionRoleMap)) {
								functionMapDBO = functionRoleMap.get(sysFunctionDBO.id);
							}
							if(!Utils.isNullOrEmpty(functionMapDBO) && !Utils.isNullOrEmpty(functionMapDBO.sysFunctionDBO)) {
								if(functionMapDBO.recordStatus == 'A') {
									sysFunctionDTO.granted = true;
									sysFunctionDTO.setAuthorised(functionMapDBO.getAuthorised());
								}
							}
							moduleSubDTO.permissions.add(sysFunctionDTO);
						}
					}
					menuScreenDTO.isGranted  = isCheckPermissionGrated;
					moduleSubDTO.screenName = menuScreenDTO;
					if(!Utils.isNullOrEmpty(moduleDTO) && !Utils.isNullOrEmpty(moduleSubDTO)) {
						moduleDTO.menus.add(moduleSubDTO);
					}
				}
			}
		}
		rolePermissionDTO.screens.add(moduleDTO);
		return rolePermissionDTO;
	}


	public Set<SysRoleFunctionMapDBO> convertRolePermissionGratedDTOtoDBO(Set<SysRoleFunctionMapDBO> sysRoleFunctionMapDBOs,RoleOrUserPermissionDTO data, String userId) {
		Map<Integer,SysRoleFunctionMapDBO> existRoleFunctions = sysRoleFunctionMapDBOs.stream().filter(s -> s.getRecordStatus() == 'A')
				.collect(Collectors.toMap(s -> s.getSysFunctionDBO().getId(), s -> s));
		Map<Integer, List<SysFunctionDTO>> functionMap = null;
		Set<Integer> grantedFunctionId = null;
		List<SysFunctionDTO> allfunctionDTOs = null;
		boolean isExist = false;
		List<Integer> isAlreadySaved = null;
		SysRoleFunctionMapDBO roleFunctionMapDBO = null;
		SysFunctionDBO sysFunctionDBO = null;
		SysRoleDBO sysRoleDBO = null;
		if(!Utils.isNullOrEmpty(data)) {
			new ArrayList<Integer>();
			functionMap = new HashMap<Integer, List<SysFunctionDTO>>();
			allfunctionDTOs = new ArrayList<SysFunctionDTO>();
			grantedFunctionId = new HashSet<Integer>();
			isAlreadySaved = new ArrayList<Integer>();
			for (ModuleDTO moduleDTO : data.screens) {
				for (ModuleSubDTO moduleSubDTO : moduleDTO.menus) {
					functionMap.put(Integer.parseInt(moduleSubDTO.screenName.ID), moduleSubDTO.permissions);
				}
			}
			for (Entry<Integer, List<SysFunctionDTO>> sysFunctionDTOs : functionMap.entrySet()) {
				if (!Utils.isNullOrEmpty(sysFunctionDTOs)) {
					for (SysFunctionDTO sysFunctionDTO : sysFunctionDTOs.getValue()) {
						if(sysFunctionDTO.granted == true) {
							allfunctionDTOs.add(sysFunctionDTO);
							grantedFunctionId.add(Integer.parseInt(sysFunctionDTO.id));
						}
					}
				}
			}
			for (SysRoleFunctionMapDBO sysRoleFunctionMapDBO : sysRoleFunctionMapDBOs) {
//				if(!Utils.isNullOrEmpty(sysRoleFunctionMapDBO.sysFunctionDBO) && !Utils.isNullOrEmpty(allfunctionDTOs) && sysRoleFunctionMapDBO.recordStatus == 'A') {
				if(!Utils.isNullOrEmpty(sysRoleFunctionMapDBO.sysFunctionDBO) && sysRoleFunctionMapDBO.recordStatus == 'A') {
					isExist = false;
						if(!Utils.isNullOrEmpty(grantedFunctionId) && grantedFunctionId.contains(sysRoleFunctionMapDBO.sysFunctionDBO.id)) {
							isExist = true;
							isAlreadySaved.add(sysRoleFunctionMapDBO.sysFunctionDBO.id);
						}
					if(isExist == false) {
						sysRoleFunctionMapDBO.modifiedUsersId = Integer.parseInt(userId);
						sysRoleFunctionMapDBO.recordStatus = 'D';
					}
				}
			}
			for (SysFunctionDTO sysFunctionDTO : allfunctionDTOs) {
				if(!isAlreadySaved.contains(Integer.parseInt(sysFunctionDTO.id.trim()))) {
					roleFunctionMapDBO = new SysRoleFunctionMapDBO();
					sysFunctionDBO = new SysFunctionDBO();
					sysFunctionDBO.id = Integer.parseInt(sysFunctionDTO.id);
					roleFunctionMapDBO.sysFunctionDBO = sysFunctionDBO;
					if(!Utils.isNullOrEmpty(data.roleName)) {
						sysRoleDBO = new SysRoleDBO();
						sysRoleDBO.id = Integer.parseInt(data.roleName.id);
						roleFunctionMapDBO.sysRoleDBO = sysRoleDBO;
					}
					roleFunctionMapDBO.setAuthorised(sysFunctionDTO.getAuthorised());
					roleFunctionMapDBO.createdUsersId = Integer.parseInt(userId);
					roleFunctionMapDBO.recordStatus = 'A';
				} 
				else {
					SysRoleFunctionMapDBO roleFunctions = existRoleFunctions.get(Integer.parseInt(sysFunctionDTO.id.trim()));
					if(!sysFunctionDTO.getAuthorised() == roleFunctions.getAuthorised() ) {
						roleFunctions.setAuthorised(sysFunctionDTO.getAuthorised());
						roleFunctions.setModifiedUsersId(Integer.parseInt(userId));
						sysRoleFunctionMapDBOs.add(roleFunctionMapDBO);
					}
				}
					sysRoleFunctionMapDBOs.add(roleFunctionMapDBO);
			}
		}else {
			for (SysRoleFunctionMapDBO sysRoleFunctionMapDBO : sysRoleFunctionMapDBOs) {
				sysRoleFunctionMapDBO.modifiedUsersId = Integer.parseInt(userId);
				sysRoleFunctionMapDBO.recordStatus = 'D';
			}
		}
		return sysRoleFunctionMapDBOs;	
	}
	
	public RoleOrUserPermissionDTO convertUserPermissionGratedDBOtoDTO(List<SysMenuDBO> sysMenuDBOs,List<SysUserRoleMapDBO> userRoleMapDBOsforAvailableCampus, 
			List<SysUserFunctionOverrideDBO> overrideDBOs, RoleOrUserPermissionDTO userPermissionDTO, String id, List<ErpCampusDTO> allCampuses) {
		Map<Integer, String> userRoleCampusMap = null;
		Map<Integer, Set<Integer>> userRolesAndCampusListMap = null;
		Map<Integer, Set<Integer>> userRolesAndFuctionListMap = null;
		Map<Integer, SysFunctionDBO> userRoleUniqueFunctionMap = null;
		Map<Integer, Map<Integer, SysUserFunctionOverrideDBO>> functionOverrideMap = null;
		userRoleCampusMap = getUniqueCampusForUser(userRoleMapDBOsforAvailableCampus, userRoleCampusMap);
		userRolesAndCampusListMap = getUniqueCampusForRoles(userRoleMapDBOsforAvailableCampus, userRolesAndCampusListMap);
		userRolesAndFuctionListMap = getUniqueFuctionsForRoles(userRoleMapDBOsforAvailableCampus, userRolesAndFuctionListMap);
		userRoleUniqueFunctionMap = getUniqueFunctionsForUserbsaedOnRoles(userRoleMapDBOsforAvailableCampus, userRoleUniqueFunctionMap);
		functionOverrideMap = getFunctionsMapAvailbleForUser(overrideDBOs, functionOverrideMap);
		ModuleDTO moduleDTO = null;
		ModuleSubDTO moduleSubDTO = null;
		MenuScreenDTO menuScreenDTO = null;
		SysFunctionDTO sysFunctionDTO = null;
		ErpCampusDTO campusDTO = null;
		Set<ErpCampusDTO> campusDTOList = null;
		Map<Integer, ErpCampusDTO> campusMap = null;
		campusDTOList = getCampusAvaibleforUser(userRoleCampusMap, campusDTOList, campusDTO, allCampuses, campusMap); 
		List<Integer> uniqueModuleId = null;
		userPermissionDTO = new RoleOrUserPermissionDTO();
		ErpUsersDTO erpUsersDTO = new ErpUsersDTO();
		erpUsersDTO.id = id;
		if(!Utils.isNullOrEmpty(userRoleUniqueFunctionMap)) {
			erpUsersDTO.totalNormalPrivilegeAvailble = userRoleUniqueFunctionMap.size();
		}
		erpUsersDTO.totalAdditionalPrivilegeAllowed = Utils.getTotalAdditionalPrivilageAllowed();
		userPermissionDTO.userName = erpUsersDTO;
		userPermissionDTO.screens = new ArrayList<ModuleDTO>();
		List<Integer> roleId = null;
		Set<Integer> checkFunctionSet = null;
		if(!Utils.isNullOrEmpty(sysMenuDBOs) && !Utils.isNullOrEmpty(allCampuses)) {
			int currentId = 0, totalAdditionalPrivilegeCount = 0;
			Boolean isAdditionalprivilegeScreen = null, isAdditionalprivilegeFunction = null;
			boolean isGratedFunction = false, isGratedScreen = false;
			uniqueModuleId = new ArrayList<Integer>();
			roleId = new ArrayList<Integer>();
			for (SysMenuDBO sysMenuDBO : sysMenuDBOs) {
				if(!Utils.isNullOrEmpty(sysMenuDBO.sysMenuModuleSubDBO) && !uniqueModuleId.contains(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id)) {
					uniqueModuleId.add(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id);
					currentId = sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id;
					if(!Utils.isNullOrEmpty(moduleDTO)) {
						userPermissionDTO.screens.add(moduleDTO);
					}
					moduleDTO = new ModuleDTO();
					moduleDTO.id = String.valueOf(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id);
					moduleDTO.text = sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.moduleName;
					moduleDTO.menus = new ArrayList<ModuleSubDTO>();
				}
				if(!Utils.isNullOrEmpty(sysMenuDBO.sysMenuModuleSubDBO) && !Utils.isNullOrEmpty(sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO)
						&& currentId == sysMenuDBO.sysMenuModuleSubDBO.sysMenuModuleDBO.id) {
					isAdditionalprivilegeScreen = null;
					moduleSubDTO = new ModuleSubDTO();
					moduleSubDTO.permissions = new ArrayList<SysFunctionDTO>();
					menuScreenDTO = new MenuScreenDTO();
					menuScreenDTO.ID = String.valueOf(sysMenuDBO.id);
					menuScreenDTO.Text = String.valueOf(sysMenuDBO.menuScreenName);
					campusDTOList = new LinkedHashSet<ErpCampusDTO>();
					campusMap = new LinkedHashMap<Integer, ErpCampusDTO>();
					for (SysFunctionDBO sysFunctionDBO : sysMenuDBO.sysFunctionDBOSet) {
						if(sysFunctionDBO.getRecordStatus() == 'A') {
							isAdditionalprivilegeFunction = null;
							isGratedFunction = false;
							sysFunctionDTO = new SysFunctionDTO();
							sysFunctionDTO.id = String.valueOf(sysFunctionDBO.id);
							sysFunctionDTO.permissionName = sysFunctionDBO.functionName;
							sysFunctionDTO.setFunctionDescription(sysFunctionDBO.getFunctionDescription());
							sysFunctionDTO.campusList = new ArrayList<ErpCampusDTO>();						
							for (ErpCampusDTO campus : allCampuses) {
								campusDTO = new ErpCampusDTO();
								campusDTO.id = String.valueOf(campus.getId());
								campusDTO.shortName = campus.shortName;
								if(!Utils.isNullOrEmpty(userRoleUniqueFunctionMap) && userRoleUniqueFunctionMap.containsKey(Integer.parseInt(sysFunctionDTO.id.trim()))) {
									campusDTO.isAdditionalPrivilege = false;
									if(isAdditionalprivilegeFunction==null) {
										isAdditionalprivilegeFunction = false;
									}
									isAdditionalprivilegeScreen = false;
									if(!Utils.isNullOrEmpty(userRoleCampusMap) && userRoleCampusMap.containsKey(Integer.parseInt(campus.id))) {
										roleId.clear();
										for (Map.Entry<Integer, Set<Integer>>  entry: userRolesAndFuctionListMap.entrySet()) {
											if(entry.getValue().contains(sysFunctionDBO.id)) {
												roleId.add(entry.getKey());
											}
										}
										if(!Utils.isNullOrEmpty(roleId)) {
											checkFunctionSet = null;
											for (Integer roleid : roleId) {
												checkFunctionSet = userRolesAndCampusListMap.get(roleid);
												if(!Utils.isNullOrEmpty(checkFunctionSet)) {
													if(checkFunctionSet.contains(Integer.parseInt(campus.id))) {
														campusDTO.isGranted = true;
														isGratedFunction = true;
													}
												}
											}
										}
									}
									Map<Integer, SysUserFunctionOverrideDBO>  campusesOverrideDBO = null;
									if(!Utils.isNullOrEmpty(functionOverrideMap)) {
										campusesOverrideDBO = functionOverrideMap.get(Integer.parseInt(sysFunctionDTO.id.trim()));
									}
									if(!Utils.isNullOrEmpty(campusesOverrideDBO) && campusesOverrideDBO.containsKey(Integer.parseInt(campus.id))) {
										SysUserFunctionOverrideDBO userFunctionOverrideDBO = campusesOverrideDBO.get(Integer.parseInt(campus.id));
										if(!Utils.isNullOrEmpty(userFunctionOverrideDBO)) {
											campusDTO.overrideId = userFunctionOverrideDBO.id;
											campusDTO.isGranted = userFunctionOverrideDBO.isAllowed;
											campusDTO.isAdditionalPrivilege = true;
											isAdditionalprivilegeScreen = true;
											isAdditionalprivilegeFunction = true;
											if(!campusMap.containsKey(Integer.parseInt(campusDTO.id)) && userFunctionOverrideDBO.isAllowed == true)
												campusMap.put(Integer.parseInt(campusDTO.id), campusDTO);
										}
									}else {
										if(!campusMap.containsKey(Integer.parseInt(campusDTO.id)) && campusDTO.isGranted == true)
											campusMap.put(Integer.parseInt(campusDTO.id), campusDTO);
									}	
								}
								else if (!Utils.isNullOrEmpty(functionOverrideMap) && functionOverrideMap.containsKey(Integer.parseInt(sysFunctionDTO.id.trim()))) {
									Map<Integer, SysUserFunctionOverrideDBO>  campusesOverrideDBO = functionOverrideMap.get(Integer.parseInt(sysFunctionDTO.id.trim()));
									if(!Utils.isNullOrEmpty(campusesOverrideDBO) &&  campusesOverrideDBO.containsKey(Integer.parseInt(campus.id))) {
										SysUserFunctionOverrideDBO userFunctionOverrideDBO = campusesOverrideDBO.get(Integer.parseInt(campus.id));
										if(!Utils.isNullOrEmpty(userFunctionOverrideDBO)) {
											campusDTO.overrideId = userFunctionOverrideDBO.id;
											campusDTO.isGranted = userFunctionOverrideDBO.isAllowed;
											isGratedFunction = true;
											campusDTO.isAdditionalPrivilege = true;
											isAdditionalprivilegeScreen = true;
											isAdditionalprivilegeFunction = true;
											if(!campusMap.containsKey(Integer.parseInt(campusDTO.id)) && userFunctionOverrideDBO.isAllowed == true)
												campusMap.put(Integer.parseInt(campusDTO.id), campusDTO);
										}
									}

								}else {
									campusDTO.isGranted = false;
									campusDTO.isAdditionalPrivilege = null;
								}
								sysFunctionDTO.campusList.add(campusDTO);
							}
							if(!Utils.isNullOrEmpty(isAdditionalprivilegeFunction)) {
								sysFunctionDTO.isAdditionalPrivilege = isAdditionalprivilegeFunction;
								sysFunctionDTO.granted = isGratedFunction;
								if(isGratedFunction == true) {
									isGratedScreen = true;
								}
								if(isAdditionalprivilegeFunction) {
									totalAdditionalPrivilegeCount++;
								}
							}
							moduleSubDTO.permissions.add(sysFunctionDTO);
							Collections.sort(moduleSubDTO.permissions);
						}
					}
					for (ErpCampusDTO campus : allCampuses) {
						ErpCampusDTO isCampusDTO = campusMap.get(Integer.parseInt(campus.id));
						if(!Utils.isNullOrEmpty(isCampusDTO))
						campusDTOList.add(isCampusDTO);
						else
						campusDTOList.add(campus);	
					}
					menuScreenDTO.campusList = campusDTOList;
					moduleSubDTO.screenName = menuScreenDTO;
					if(!Utils.isNullOrEmpty(isAdditionalprivilegeScreen)) {
						menuScreenDTO.isAdditionalPrivilege = isGratedScreen;
						menuScreenDTO.isGranted = true;
					}
					if(!Utils.isNullOrEmpty(moduleDTO) && !Utils.isNullOrEmpty(moduleSubDTO)) {
						moduleDTO.menus.add(moduleSubDTO);
					}
				}
			}
			erpUsersDTO.totalAdditionalPrivilegeCount = totalAdditionalPrivilegeCount;
		}
		userPermissionDTO.screens.add(moduleDTO);
		return userPermissionDTO;
	}

	private Map<Integer, Set<Integer>> getUniqueFuctionsForRoles(List<SysUserRoleMapDBO> roleMapDBOs, Map<Integer, Set<Integer>> userRolesAndFuctionListMap) {
		if(!Utils.isNullOrEmpty(roleMapDBOs)) {
			Set<Integer> functionSet = null;
			Set<Integer> roleSet = new LinkedHashSet<Integer>();
			userRolesAndFuctionListMap = new LinkedHashMap<Integer, Set<Integer>>();
			for (SysUserRoleMapDBO sysUserRoleMapDBO : roleMapDBOs) {
				if(!Utils.isNullOrEmpty(sysUserRoleMapDBO.erpCampusDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO.erpCampusDBO.shortName) 
						&& !Utils.isNullOrEmpty(sysUserRoleMapDBO.recordStatus) && sysUserRoleMapDBO.recordStatus == 'A'
						&&  !Utils.isNullOrEmpty(sysUserRoleMapDBO.sysRoleDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO.sysRoleDBO.sysRoleFunctionMapDBOs)) {
					if(!roleSet.contains(sysUserRoleMapDBO.sysRoleDBO.id)) {
						roleSet.add(sysUserRoleMapDBO.sysRoleDBO.id);
						functionSet = new LinkedHashSet<Integer>();
						for (SysRoleFunctionMapDBO functionMapDBO : sysUserRoleMapDBO.sysRoleDBO.sysRoleFunctionMapDBOs) {
							if(!Utils.isNullOrEmpty(functionMapDBO.sysFunctionDBO) 
									&& !Utils.isNullOrEmpty(functionMapDBO.recordStatus) && functionMapDBO.recordStatus == 'A'
									&& !Utils.isNullOrEmpty(functionMapDBO.sysFunctionDBO.recordStatus) && functionMapDBO.sysFunctionDBO.recordStatus == 'A')
							functionSet.add(functionMapDBO.sysFunctionDBO.id);
						}
						userRolesAndFuctionListMap.put(sysUserRoleMapDBO.sysRoleDBO.id, functionSet);
					}
				}
					
			}

		}
		return userRolesAndFuctionListMap;
	
	}

	private Map<Integer, SysFunctionDBO> getUniqueFunctionsForUserbsaedOnRoles(List<SysUserRoleMapDBO> userRoleMapDBOs, Map<Integer, SysFunctionDBO> rolesFunctionMap) {
		if(!Utils.isNullOrEmpty(userRoleMapDBOs)) {
			Set<Integer> roleId = new HashSet<Integer>();
			rolesFunctionMap = new HashMap<Integer, SysFunctionDBO>();
			for (SysUserRoleMapDBO sysUserRoleMapDBO : userRoleMapDBOs) {
				if(!roleId.contains(sysUserRoleMapDBO.sysRoleDBO.id)) {
					for (SysRoleFunctionMapDBO roleFunctionMapDBO : sysUserRoleMapDBO.sysRoleDBO.sysRoleFunctionMapDBOs) {
						if(!Utils.isNullOrEmpty(roleFunctionMapDBO.sysFunctionDBO) && !Utils.isNullOrEmpty(roleFunctionMapDBO.sysFunctionDBO.functionName)
								&& roleFunctionMapDBO.recordStatus =='A' && roleFunctionMapDBO.sysFunctionDBO.recordStatus == 'A')
						rolesFunctionMap.put(roleFunctionMapDBO.sysFunctionDBO.id, roleFunctionMapDBO.sysFunctionDBO);
					}
				}
			}
		}
		return rolesFunctionMap;
	}

	private Set<ErpCampusDTO> getCampusAvaibleforUser(Map<Integer, String> campusMap, Set<ErpCampusDTO> campusDTOList, 
			ErpCampusDTO campusDTO, List<ErpCampusDTO> allCampuses, Map<Integer, ErpCampusDTO> CampusMap) {
		if(!Utils.isNullOrEmpty(allCampuses)) {
			campusDTOList = new LinkedHashSet<ErpCampusDTO>();
			CampusMap = new LinkedHashMap<Integer, ErpCampusDTO>();
			for (ErpCampusDTO erpCampusDTO : allCampuses) {
				campusDTO = new ErpCampusDTO();
				campusDTO.id = erpCampusDTO.id;
				campusDTO.shortName = erpCampusDTO.shortName;
				if(!Utils.isNullOrEmpty(campusMap) && !Utils.isNullOrEmpty(campusMap.entrySet()) && campusMap.containsKey(Integer.parseInt(erpCampusDTO.id))) {
					campusDTO.isGranted = false;
				}
				campusDTOList.add(campusDTO);
			}
		}
		return campusDTOList;
	}

	private Map<Integer, Map<Integer, SysUserFunctionOverrideDBO>> getFunctionsMapAvailbleForUser(List<SysUserFunctionOverrideDBO> overrideDBOs, Map<Integer, Map<Integer, SysUserFunctionOverrideDBO>> overrideMap) {
		Set<Integer> functionIds = null;
		Set<Integer> campusIds = null;
		Map<Integer, SysUserFunctionOverrideDBO> campusOverrideDBOMap = null;
		Integer functionId = null;
		if(!Utils.isNullOrEmpty(overrideDBOs)) {
			overrideMap = new  HashMap<Integer, Map<Integer, SysUserFunctionOverrideDBO>>();
			functionIds = new HashSet<Integer>();
			for (SysUserFunctionOverrideDBO sysUserFunctionOverrideDBO : overrideDBOs) {
				if(!Utils.isNullOrEmpty(sysUserFunctionOverrideDBO.sysFunctionDBO) && !Utils.isNullOrEmpty(sysUserFunctionOverrideDBO.erpCampusDBO.id) && sysUserFunctionOverrideDBO.recordStatus == 'A') {
					if(!functionIds.contains(sysUserFunctionOverrideDBO.sysFunctionDBO.id)) {
						functionIds.add(sysUserFunctionOverrideDBO.sysFunctionDBO.id);
						if(!Utils.isNullOrEmpty(functionId) && !Utils.isNullOrEmpty(campusIds)) {
							overrideMap.put(functionId,  campusOverrideDBOMap);
						}
						campusIds = new HashSet<Integer>();
						campusOverrideDBOMap = new HashMap<Integer, SysUserFunctionOverrideDBO>();
						functionId = sysUserFunctionOverrideDBO.sysFunctionDBO.id;
					}	
					campusIds.add(sysUserFunctionOverrideDBO.erpCampusDBO.id);
					campusOverrideDBOMap.put(sysUserFunctionOverrideDBO.erpCampusDBO.id, sysUserFunctionOverrideDBO);
				}
			}
			if(!Utils.isNullOrEmpty(functionId) && !Utils.isNullOrEmpty(campusOverrideDBOMap))
				overrideMap.put(functionId,  campusOverrideDBOMap);
		}
		return overrideMap;
	}

	private  Map<Integer, String> getUniqueCampusForUser(List<SysUserRoleMapDBO> userRoleMapDBOs, Map<Integer, String> campusMap) {
		if(!Utils.isNullOrEmpty(userRoleMapDBOs)) {
			campusMap = new  HashMap<Integer, String>();
			for (SysUserRoleMapDBO sysUserRoleMapDBO : userRoleMapDBOs) {
				if(!Utils.isNullOrEmpty(sysUserRoleMapDBO.erpCampusDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO.erpCampusDBO.shortName) 
						&& !Utils.isNullOrEmpty(sysUserRoleMapDBO.recordStatus) && sysUserRoleMapDBO.recordStatus == 'A' && !campusMap.containsKey(sysUserRoleMapDBO.erpCampusDBO.id)
						&&  !Utils.isNullOrEmpty(sysUserRoleMapDBO.sysRoleDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO.sysRoleDBO.sysRoleFunctionMapDBOs))
					campusMap.put(sysUserRoleMapDBO.erpCampusDBO.id, sysUserRoleMapDBO.erpCampusDBO.shortName);
			}
		}
		return campusMap;
	}
	
	private  Map<Integer, Set<Integer>> getUniqueCampusForRoles(List<SysUserRoleMapDBO> userRoleMapDBOs, Map<Integer, Set<Integer>> roleAndCampusListMap) {
		if(!Utils.isNullOrEmpty(userRoleMapDBOs)) {
			Collections.sort(userRoleMapDBOs);
			Integer roleId = null;
			Set<Integer> campusSet = null;
			Set<Integer> roleSet = new LinkedHashSet<Integer>();
			roleAndCampusListMap = new LinkedHashMap<Integer, Set<Integer>>();
			for (SysUserRoleMapDBO sysUserRoleMapDBO : userRoleMapDBOs) {
				if(!Utils.isNullOrEmpty(sysUserRoleMapDBO.erpCampusDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO.erpCampusDBO.shortName) 
						&& !Utils.isNullOrEmpty(sysUserRoleMapDBO.recordStatus) && sysUserRoleMapDBO.recordStatus == 'A' 
						&&  !Utils.isNullOrEmpty(sysUserRoleMapDBO.sysRoleDBO) && !Utils.isNullOrEmpty(sysUserRoleMapDBO.sysRoleDBO.sysRoleFunctionMapDBOs)) {
					if(!roleSet.contains(sysUserRoleMapDBO.sysRoleDBO.id)) {
						roleSet.add(sysUserRoleMapDBO.sysRoleDBO.id);
						if(!Utils.isNullOrEmpty(campusSet) && !Utils.isNullOrEmpty(roleId)) {
							roleAndCampusListMap.put(roleId, campusSet);
						}
						roleId = sysUserRoleMapDBO.sysRoleDBO.id;
						campusSet = new LinkedHashSet<Integer>();
					}
					campusSet.add(sysUserRoleMapDBO.erpCampusDBO.id);
				}
			}
			if(!Utils.isNullOrEmpty(campusSet) && !Utils.isNullOrEmpty(roleId))
			roleAndCampusListMap.put(roleId, campusSet);
		}
		return roleAndCampusListMap;
	}

	public List<SysUserFunctionOverrideDBO> convertUserPermissionGratedDTOtoDBO(List<SysUserFunctionOverrideDBO> userFunctionOverrideDBOs,
			ModuleSubDTO data, String idUser, String userId, List<SysUserRoleMapDBO> dboList, Map<Integer, ErpUsersCampusDBO> userCampusPermissionMap, List<ErpUsersCampusDBO> userCampusList, Map<Integer, Set<Integer>> userCampusForRoles,
			Set<Integer> userCampusForAddPermissionSet, Set<Integer> activeCampus ) {
		    Map<Integer, List<ErpCampusDTO>> functionCampusMap = null;
		
		    Set<Integer> roleCampus = new HashSet<Integer>();
		    if(userCampusForRoles.containsKey(Integer.parseInt(idUser))) {
		       roleCampus = userCampusForRoles.get(Integer.parseInt(idUser));
			 }
		    
		   if(!Utils.isNullOrEmpty(data.permissions)) {
			   functionCampusMap = new HashMap<Integer, List<ErpCampusDTO>>();
			   for (SysFunctionDTO functionDTO : data.permissions) {
				   functionCampusMap.put(Integer.parseInt(functionDTO.id), functionDTO.campusList);
				   
					//ErpUserCampus 
				   if(functionDTO.granted) {
					   for(ErpCampusDTO campusId :functionDTO.campusList) {
						   if(campusId.getIsGranted() && !Utils.isNullOrEmpty(userCampusPermissionMap) && !userCampusPermissionMap.containsKey(Integer.parseInt(campusId.getId()))) {
							   ErpUsersCampusDBO userCampus = new ErpUsersCampusDBO();
							   userCampus.setErpUsersDBO(new ErpUsersDBO());
							   userCampus.getErpUsersDBO().setId(Integer.parseInt(idUser));
							   userCampus.setErpCampusDBO(new ErpCampusDBO());
							   userCampus.getErpCampusDBO().setId(Integer.parseInt(campusId.getId()));
							   userCampus.setIsPreferred(true);
							   userCampus.setCreatedUsersId(Integer.parseInt(userId));
							   userCampus.setRecordStatus('A');
							   userCampusList.add(userCampus);
							   activeCampus.add(Integer.parseInt(campusId.getId()));
							   userCampusPermissionMap.put(Integer.parseInt(campusId.getId()), userCampus);
						   } else if(!campusId.getIsGranted() && !Utils.isNullOrEmpty(userCampusPermissionMap) && userCampusPermissionMap.containsKey(Integer.parseInt(campusId.getId())) && !roleCampus.contains(Integer.parseInt(campusId.getId()))
								   && !userCampusForAddPermissionSet.contains(Integer.parseInt(campusId.getId())) && !activeCampus.contains(Integer.parseInt(campusId.getId()))) {
							   ErpUsersCampusDBO userCampus =  userCampusPermissionMap.get(Integer.parseInt(campusId.getId()));
							   userCampus.setRecordStatus('D');
							   userCampus.setModifiedUsersId(Integer.parseInt(userId));
							   userCampusPermissionMap.remove(Integer.parseInt(campusId.getId()));
							   userCampusList.add(userCampus);
						   }
					   }
				   }
				}
		   }
		   
		   Map<Integer, Set<Integer>> userRolesAndCampusListMap = null;
		   Map<Integer, Set<Integer>> userRolesAndFuctionListMap = null;
		   userRolesAndCampusListMap = getUniqueCampusForRoles(dboList, userRolesAndCampusListMap);
		   userRolesAndFuctionListMap = getUniqueFuctionsForRoles(dboList, userRolesAndFuctionListMap);
		   Map<Integer, List<Integer>> isSavedfunctionCampusMap = null;
		   List<Integer> campusIds = null;
		   Set<Integer> functionIds = null;
		   SysUserFunctionOverrideDBO userFunctionOverrideDBO = null;
		   SysFunctionDBO functionDBO = null;
		   ErpUsersDBO erpUsersDBO  = new ErpUsersDBO();
		   erpUsersDBO.id = Integer.parseInt(idUser.trim());
		   ErpCampusDBO campusDBO = null;
		   Integer functionId = null;
		   boolean checkIsOverriddenUserRoleCampus = false;
		   List<Integer> roleIds = null;
		   if(!Utils.isNullOrEmpty(userFunctionOverrideDBOs)){
			   functionIds = new HashSet<Integer>();
			   isSavedfunctionCampusMap = new HashMap<Integer, List<Integer>>();
			   roleIds = new ArrayList<Integer>();
			   for (SysUserFunctionOverrideDBO dbo : userFunctionOverrideDBOs) {
				  if (!Utils.isNullOrEmpty(dbo.sysFunctionDBO) && functionCampusMap.containsKey(dbo.sysFunctionDBO.id)) {
					  List<ErpCampusDTO>  campusDTOList = functionCampusMap.get(dbo.sysFunctionDBO.id);
					  if(!Utils.isNullOrEmpty(campusDTOList)) {
						  if(!functionIds.contains(dbo.sysFunctionDBO.id)) {
							  if(!Utils.isNullOrEmpty(campusIds) && !Utils.isNullOrEmpty(functionId)) {
								  isSavedfunctionCampusMap.put(functionId, campusIds);
							  }
							  campusIds = new ArrayList<Integer>();
							  functionId = dbo.sysFunctionDBO.id;
							  functionIds.add(functionId);
						  }
						  for (ErpCampusDTO campusDTO : campusDTOList) {
							  if(!Utils.isNullOrEmpty(dbo.erpCampusDBO) && !Utils.isNullOrEmpty(campusDTO.id) && dbo.erpCampusDBO.id == Integer.parseInt(campusDTO.id)
									  && !Utils.isNullOrEmpty(dbo.sysFunctionDBO.id)) {
								  if(campusDTO.isGranted == false && !Utils.isNullOrEmpty(campusDTO.overrideId) && campusDTO.overrideId == dbo.id) {
									  if(dbo.isAllowed !=campusDTO.isGranted) {
										  dbo.modifiedUsersId = Integer.parseInt(userId);
										  dbo.recordStatus = 'D';
										  checkIsOverriddenUserRoleCampus = false; 
										  checkIsOverriddenUserRoleCampus = checkRoleCampusOrNew(userRolesAndFuctionListMap, userRolesAndCampusListMap, String.valueOf(dbo.sysFunctionDBO.id), campusDTO.id, checkIsOverriddenUserRoleCampus, roleIds);
										  if (checkIsOverriddenUserRoleCampus) { 
											  dbo.isAllowed = campusDTO.isGranted;
											  dbo.modifiedUsersId = Integer.parseInt(userId);
											  dbo.recordStatus = 'A';
										  }
									  }
								  }else if(campusDTO.isGranted == true && !Utils.isNullOrEmpty(campusDTO.overrideId) && campusDTO.overrideId == dbo.id){ 
							        	if(dbo.isAllowed !=campusDTO.isGranted) {
											  dbo.modifiedUsersId = Integer.parseInt(userId);
											  dbo.recordStatus = 'D';
											  checkIsOverriddenUserRoleCampus = false; 
											  checkIsOverriddenUserRoleCampus = checkRoleCampusOrNew(userRolesAndFuctionListMap, userRolesAndCampusListMap, String.valueOf(dbo.sysFunctionDBO.id), campusDTO.id, checkIsOverriddenUserRoleCampus, roleIds);
											  if (!checkIsOverriddenUserRoleCampus) { 
												  dbo.isAllowed = campusDTO.isGranted;
												  dbo.modifiedUsersId = Integer.parseInt(userId);
												  dbo.recordStatus = 'A';
											  }
							        	}else if(dbo.isAllowed == true && campusDTO.isGranted==true) {
							        		checkIsOverriddenUserRoleCampus = false; 
											checkIsOverriddenUserRoleCampus = checkRoleCampusOrNew(userRolesAndFuctionListMap, userRolesAndCampusListMap, String.valueOf(dbo.sysFunctionDBO.id), campusDTO.id, checkIsOverriddenUserRoleCampus, roleIds);
									        if (checkIsOverriddenUserRoleCampus) {
									        	dbo.modifiedUsersId = Integer.parseInt(userId);
												dbo.recordStatus = 'D';
									        }
							        	}
								  }
								  campusIds.add( dbo.erpCampusDBO.id);
							  }
						 }
					  }else {
						  dbo.modifiedUsersId = Integer.parseInt(userId);
						  dbo.recordStatus = 'D';
					  }
				  }
			   }
			   if(!Utils.isNullOrEmpty(campusIds) && !Utils.isNullOrEmpty(functionId)) {
				   isSavedfunctionCampusMap.put(functionId, campusIds);
			   }
		   }else {
			   userFunctionOverrideDBOs = new ArrayList<SysUserFunctionOverrideDBO>();
		   }
		   boolean  isCheckCampuseAvailableInUserRole = false;	
		   roleIds = new ArrayList<Integer>();
		   for (SysFunctionDTO functionDTO : data.permissions) {
			for (ErpCampusDTO campusDTO : functionDTO.campusList) {
				if(!Utils.isNullOrEmpty(campusDTO.id) && !Utils.isNullOrEmpty(functionDTO.id) && Utils.isNullOrEmpty(campusDTO.overrideId)) {					
					if(campusDTO.isGranted == false) {
						isCheckCampuseAvailableInUserRole = false; 
						for (Map.Entry<Integer, Set<Integer>> entry : userRolesAndFuctionListMap.entrySet()) {
							if(!Utils.isNullOrEmpty(entry.getValue()) && entry.getValue().contains(Integer.parseInt(functionDTO.id))) {
								Set<Integer> campuseSet = userRolesAndCampusListMap.get(entry.getKey());
								if(!Utils.isNullOrEmpty(campuseSet)) {
									if(campuseSet.contains(Integer.parseInt(campusDTO.id))) {
										isCheckCampuseAvailableInUserRole = true;
									}
								}
							}
						}
						if(isCheckCampuseAvailableInUserRole) {
							userFunctionOverrideDBO = new SysUserFunctionOverrideDBO();
							userFunctionOverrideDBO = convertuserFunctionOverrideDTOtoDBO(userFunctionOverrideDBO, campusDTO, erpUsersDBO, functionDBO, functionDTO.id, campusDBO, userId);
							userFunctionOverrideDBOs.add(userFunctionOverrideDBO);
						}
					}else {
						if(!Utils.isNullOrEmpty(functionIds) && functionIds.contains(Integer.parseInt(functionDTO.id))){
							List<Integer> campuseSet = isSavedfunctionCampusMap.get(Integer.parseInt(functionDTO.id));
							if(!Utils.isNullOrEmpty(campuseSet) && !campuseSet.contains(Integer.parseInt(campusDTO.id))) {
								isCheckCampuseAvailableInUserRole = false; 
								isCheckCampuseAvailableInUserRole = checkRoleCampusOrNew(userRolesAndFuctionListMap, userRolesAndCampusListMap, functionDTO.id, campusDTO.id, isCheckCampuseAvailableInUserRole, roleIds);
								if(!isCheckCampuseAvailableInUserRole) {
									userFunctionOverrideDBO = new SysUserFunctionOverrideDBO();
									userFunctionOverrideDBO = convertuserFunctionOverrideDTOtoDBO(userFunctionOverrideDBO, campusDTO, erpUsersDBO, functionDBO, functionDTO.id, campusDBO, userId);
									userFunctionOverrideDBOs.add(userFunctionOverrideDBO);
								}
							}
						}
						else{
							isCheckCampuseAvailableInUserRole = false; 
							roleIds.clear();
							for (Map.Entry<Integer, Set<Integer>> entry : userRolesAndFuctionListMap.entrySet()) {
								if(!Utils.isNullOrEmpty(entry.getValue()) && entry.getValue().contains(Integer.parseInt(functionDTO.id))) {
									roleIds.add(entry.getKey());
								}
							}
							if(!Utils.isNullOrEmpty(roleIds)){
							   for (Integer roleid : roleIds) {
								  Set<Integer>  roleCampusIds = userRolesAndCampusListMap.get(roleid);
								  if(!Utils.isNullOrEmpty(roleCampusIds) && roleCampusIds.contains(Integer.parseInt(campusDTO.id))) {
									 isCheckCampuseAvailableInUserRole = true;
								  }
							   }
							}
							if(!isCheckCampuseAvailableInUserRole) {
								userFunctionOverrideDBO = new SysUserFunctionOverrideDBO();
								userFunctionOverrideDBO = convertuserFunctionOverrideDTOtoDBO(userFunctionOverrideDBO, campusDTO, erpUsersDBO, functionDBO, functionDTO.id, campusDBO, userId);
								userFunctionOverrideDBOs.add(userFunctionOverrideDBO);
							}
						}
					}
				}
			}
		}
		return userFunctionOverrideDBOs;
	}
	
	private boolean checkRoleCampusOrNew(Map<Integer, Set<Integer>> userRolesAndFuctionListMap,Map<Integer, Set<Integer>> userRolesAndCampusListMap, String fuctionId, String campusId,
			boolean isCheckCampuseAvailableInUserRole, List<Integer> roleIds) {
		roleIds.clear();
		for (Map.Entry<Integer, Set<Integer>> entry : userRolesAndFuctionListMap.entrySet()) {
			if(!Utils.isNullOrEmpty(entry.getValue()) && entry.getValue().contains(Integer.parseInt(fuctionId))) {
				 Set<Integer>  roleCampusIds = userRolesAndCampusListMap.get(entry.getKey());
				  if(!Utils.isNullOrEmpty(roleCampusIds) && roleCampusIds.contains(Integer.parseInt(campusId))) {
					 isCheckCampuseAvailableInUserRole = true;
					 break;
				  }
			}
		}
		return isCheckCampuseAvailableInUserRole;
	}

	public SysUserFunctionOverrideDBO convertuserFunctionOverrideDTOtoDBO(SysUserFunctionOverrideDBO userFunctionOverrideDBO, ErpCampusDTO campusDTO,
			ErpUsersDBO erpUsersDBO, SysFunctionDBO functionDBO, String functionId, ErpCampusDBO campusDBO, String userId) {
		userFunctionOverrideDBO.erpUsersDBO = erpUsersDBO;
		functionDBO = new SysFunctionDBO();
		functionDBO.id = Integer.parseInt(functionId);
		userFunctionOverrideDBO.sysFunctionDBO = functionDBO;
		campusDBO = new ErpCampusDBO();
		campusDBO.id = Integer.parseInt(campusDTO.id.trim());
		userFunctionOverrideDBO.erpCampusDBO = campusDBO;
		userFunctionOverrideDBO.isAllowed = campusDTO.isGranted;
		userFunctionOverrideDBO.recordStatus = 'A';
		userFunctionOverrideDBO.createdUsersId = Integer.parseInt(userId);
		return userFunctionOverrideDBO;
	}

	public List<SysUserRoleMapDTO> converUserRoleMapDBOstoDTOs(List<SysUserRoleMapDBO> dboList, List<SysUserRoleMapDTO> dtoList) {
		List<Integer> userIds = new ArrayList<Integer>();
		for (SysUserRoleMapDBO dbo : dboList) {
			if(!Utils.isNullOrEmpty(dbo.erpUsersDBO) && !userIds.contains(dbo.erpUsersDBO.id)) {
				SysUserRoleMapDTO userRoleMapDTO = new SysUserRoleMapDTO();
				userRoleMapDTO.id = String.valueOf(dbo.id);
				if(!Utils.isNullOrEmpty(dbo.sysRoleDBO)) {
					LookupItemDTO itemDTO = new LookupItemDTO();
					itemDTO.value = String.valueOf(dbo.sysRoleDBO.id);
					itemDTO.label = dbo.sysRoleDBO.roleName;
					userRoleMapDTO.role = itemDTO;	
				}
				LookupItemDTO itemDTO = new LookupItemDTO();
				itemDTO.value = String.valueOf(dbo.erpUsersDBO.id);
				userIds.add(dbo.erpUsersDBO.id);
				itemDTO.label = !Utils.isNullOrEmpty(dbo.erpUsersDBO.getLoginId()) ? dbo.erpUsersDBO.getLoginId(): "";
				userRoleMapDTO.user = itemDTO;
				dtoList.add(userRoleMapDTO);
			}
		}
		return dtoList;
	}

}