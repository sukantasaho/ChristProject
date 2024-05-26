package com.christ.erp.services.handlers.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.SysFunctionDBO;
import com.christ.erp.services.dbobjects.common.SysMenuDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleDBO;
import com.christ.erp.services.dbobjects.common.SysRoleDBO;
import com.christ.erp.services.dbobjects.common.SysRoleFunctionMapDBO;
import com.christ.erp.services.dbobjects.common.SysRoleGroupDBO;
import com.christ.erp.services.dbobjects.common.SysUserFunctionOverrideDBO;
import com.christ.erp.services.dbobjects.common.SysUserRoleMapDBO;
import com.christ.erp.services.dto.common.ErpCampusDTO;
import com.christ.erp.services.dto.common.ErpUsersDTO;
import com.christ.erp.services.dto.common.MenuScreenDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.ModuleDTO;
import com.christ.erp.services.dto.common.ModuleSubDTO;
import com.christ.erp.services.dto.common.RoleOrUserPermissionDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.common.SysFunctionDTO;
import com.christ.erp.services.dto.common.SysRoleDTO;
import com.christ.erp.services.dto.common.SysRoleGroupDTO;
import com.christ.erp.services.dto.common.SysUserRoleMapDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.helpers.common.PrivilegeSettingsHelper;
import com.christ.erp.services.transactions.common.PrivilegeSettingsTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@Service
public class PrivilegeSettingsHandler {

	private static volatile PrivilegeSettingsHandler privilegeSettingsHandler = null;

	public static PrivilegeSettingsHandler getInstance() {
		if (privilegeSettingsHandler == null) {
			privilegeSettingsHandler = new PrivilegeSettingsHandler();
		}
		return privilegeSettingsHandler;
	}

	PrivilegeSettingsTransaction privilegeSettingsTransaction = PrivilegeSettingsTransaction.getInstance();
	PrivilegeSettingsHelper  privilegeSettingsHelper = PrivilegeSettingsHelper.getInstance();
	
	@Autowired
	private CommonApiHandler commonApiHandler;	

	@Autowired
	private PrivilegeSettingsTransaction privilegeSettingsTransaction1;
	
	@Autowired
	PrivilegeSettingsHelper privilegeSettingsHelper1;

	public ApiResult<ModelBaseDTO> saveOrUpdateRole(SysRoleDTO data, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
		SysRoleDBO sysRoleDBO = null;
		boolean isDuplicated =  isCheckDuplicatedRole(data);
		if(!isDuplicated) {
			if(!Utils.isNullOrEmpty(data)) {
				if(Utils.isNullOrEmpty(data.id)) {
					sysRoleDBO = new SysRoleDBO();
					sysRoleDBO.createdUsersId =  Integer.parseInt(userId);
				}else{
					sysRoleDBO = privilegeSettingsTransaction.editRole(data.id);
					sysRoleDBO.modifiedUsersId =  Integer.parseInt(userId);
				}
				if(!Utils.isNullOrEmpty(sysRoleDBO)) {
					privilegeSettingsHelper.convertSysRoleDTOtoDBO(sysRoleDBO,data);
					boolean isSaved = privilegeSettingsTransaction.saveOrUpdateRole(sysRoleDBO);
					if(isSaved) {
						result.success = true;
					}
				}
			}
		}else{
			result.failureMessage = "Duplicate record exists for Role Name:" + data.roleName;
			result.success = false;
		}
		return result;
	}

	private boolean isCheckDuplicatedRole(SysRoleDTO data) throws Exception {
		if(!Utils.isNullOrEmpty(data)) {
			List<Object[]> isCheckDuplicated = privilegeSettingsTransaction.isCheckDuplicatedRole(data);
			if(!Utils.isNullOrEmpty(isCheckDuplicated)) {
				return true;
			}
		}
		return false;
	}

	public SysRoleDTO editRole(String roleId) throws Exception {
		SysRoleDBO dbo = null;
		SysRoleDTO dto = null;
		if(!Utils.isNullOrEmpty(roleId)) {
			dbo = privilegeSettingsTransaction.editRole(roleId);
			if(!Utils.isNullOrEmpty(dbo)) {
				dto = new SysRoleDTO();
				privilegeSettingsHelper.convertSysRoleDBOtoDTO(dbo, dto);
			}
		}
		return dto;
	}

	//	public boolean deleteRole(String roleId, String userId) throws Exception {
	//		return privilegeSettingsTransaction.deleteRole(roleId, userId);
	//	}

	public ApiResult<ModelBaseDTO> saveOrUpdateUser(ErpUsersDTO data, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
		ErpUsersDBO dbo = null;
		List<Object[]> users = null;
		Map<Integer, ErpUsersCampusDBO> userCampusPermissionMap = new HashMap<Integer, ErpUsersCampusDBO>();
		Map<Integer, List<SysUserFunctionOverrideDBO>> userCampusForAddPermissionMap = new HashMap<Integer, List<SysUserFunctionOverrideDBO>>();
		boolean isDuplicated =  isCheckDuplicatedUser(data);
		if(!isDuplicated) {
			if(!Utils.isNullOrEmpty(data.getEmployee())) {
				users = privilegeSettingsTransaction.getExistingUser(data);
			}
			if(Utils.isNullOrEmpty(users)) {
				if(!Utils.isNullOrEmpty(data)) {
					if(Utils.isNullOrEmpty(data.id)) {
						dbo = new ErpUsersDBO();
						dbo.createdUsersId =  Integer.parseInt(userId);
					}else{
						dbo = privilegeSettingsTransaction.editUser(data.id);
						Set<Integer> userIds = new HashSet<Integer>();
						userIds.add(Integer.parseInt(data.id));
						userCampusPermissionMap = privilegeSettingsTransaction1.getUserCampus(userIds).stream().collect(Collectors.toMap(s -> s.getErpCampusDBO().getId(), s -> s));
						userCampusForAddPermissionMap = privilegeSettingsTransaction1.getUserCampusForAddPermission(data.id).stream().
								collect(Collectors.groupingBy(s -> s.getErpCampusDBO().getId()));
						dbo.modifiedUsersId =  Integer.parseInt(userId);
					}
					if(!Utils.isNullOrEmpty(dbo)) {
						List<ErpUsersCampusDBO> userCampusList = new ArrayList<ErpUsersCampusDBO>();
						privilegeSettingsHelper.convertErpUsersDTOtoDBO(dbo,data, userId, userCampusList,userCampusPermissionMap,userCampusForAddPermissionMap);	
						Boolean isSaved = privilegeSettingsTransaction.saveOrUpdateUser(dbo);
						privilegeSettingsTransaction1.saveOrUpdateUserCampus(userCampusList);
						if(isSaved) {
							result.success = true;
						}
					}
				}
			} else {
				result.failureMessage = "Duplicate record exists for Selected Employee: " + data.getEmployee().getLabel();
				result.success = false;
			}

		}else{
			result.failureMessage = "Duplicate record exists for User Name: " + data.userName;
			result.success = false;
		}
		return result;
	}

	private boolean isCheckDuplicatedUser(ErpUsersDTO data) throws Exception {
		if(!Utils.isNullOrEmpty(data)) {
			List<Object[]> isCheckDuplicated = privilegeSettingsTransaction.isCheckDuplicatedUser(data);
			if(!Utils.isNullOrEmpty(isCheckDuplicated)) {
				return true;
			}
		}
		return false;
	}

	public ErpUsersDTO editUser(String userId) throws Exception {
		ErpUsersDBO dbo = null;
		ErpUsersDTO dto = null;
		if(!Utils.isNullOrEmpty(userId)) {
			dbo = privilegeSettingsTransaction1.editUser(userId);
			if(!Utils.isNullOrEmpty(dbo)) {
				dto = new ErpUsersDTO();
				dto = privilegeSettingsHelper1.convertErpUsersDBOtoDTO(dbo, dto);
			}
		}
		return dto;
	}

	//	public boolean deleteUser(String idUser, String userId) throws Exception {
	//		return privilegeSettingsTransaction.deleteUser(idUser, userId);
	//	}

	public RoleOrUserPermissionDTO editPermissionsToRole(String roleId) {
		RoleOrUserPermissionDTO rolePermissionDTO = null;
		SysRoleDBO  sysRoleDBO = null;
		List<SysMenuDBO>  sysMenuDBOs = privilegeSettingsTransaction.getAllSysMenuAndFuctions();
		if(!Utils.isNullOrEmpty(roleId)) {
			sysRoleDBO = privilegeSettingsTransaction.editPermissionGratedToRole(roleId);
			if(!Utils.isNullOrEmpty(sysMenuDBOs)) {
				rolePermissionDTO = privilegeSettingsHelper.convertSysRolePermissionDBOtoDTO(sysMenuDBOs, rolePermissionDTO, sysRoleDBO.sysRoleFunctionMapDBOs, roleId);
			}
		}
		return rolePermissionDTO;
	}

	public boolean saveOrUpdatePermissionsToRole(RoleOrUserPermissionDTO data, String roleId, String userId) throws Exception {
		SysRoleDBO  sysRoleDBO = null;
		boolean isSaved = false;
		if(!Utils.isNullOrEmpty(roleId)){
			sysRoleDBO = privilegeSettingsTransaction.editPermissionGratedToRole(roleId);
			privilegeSettingsHelper.convertRolePermissionGratedDTOtoDBO(sysRoleDBO.sysRoleFunctionMapDBOs, data, userId);
			isSaved =  privilegeSettingsTransaction.saveOrUpdatePermissionsToRole(sysRoleDBO);
		}
		return isSaved;
	}

	public RoleOrUserPermissionDTO editPermissionsToUser(String userId) {
		RoleOrUserPermissionDTO userPermissionDTO = null;
		List<SysUserRoleMapDBO>   userRoleMapDBOsforAvailableCampus = null;
		List<SysUserFunctionOverrideDBO> overrideDBOs = null;
		List<SysMenuDBO>  sysMenuDBOs = privilegeSettingsTransaction.getAllSysMenuAndFuctions();
		if(!Utils.isNullOrEmpty(userId)) {
			userRoleMapDBOsforAvailableCampus = privilegeSettingsTransaction.getCampusListAvailbleToUser(userId);	
			overrideDBOs = privilegeSettingsTransaction.editAditionalPermissionGratedToUser(userId, null);
			CommonApiHandler commonApiHandler1 = new CommonApiHandler();
			List<ErpCampusDTO> allCampuses = commonApiHandler1.getCampusDetails();
			userPermissionDTO = privilegeSettingsHelper.convertUserPermissionGratedDBOtoDTO(sysMenuDBOs, userRoleMapDBOsforAvailableCampus, overrideDBOs, userPermissionDTO, userId, allCampuses);
		}
		return userPermissionDTO;
	}

	public boolean saveOrUpdatePermissionsToUser(ModuleSubDTO data,  String idUser, String userId) throws Exception {
		List<SysUserFunctionOverrideDBO> userFunctionOverrideDBOs = null;
		List<ErpUsersCampusDBO> userCampusList = new ArrayList<ErpUsersCampusDBO>();
		Map<Integer, Set<Integer>> userCampusForRoles = new HashMap<Integer, Set<Integer>>();
		Set<Integer>  userCampusForAddPermissionOtherFunctionsSet = new HashSet<Integer>();
		List<SysUserRoleMapDBO> dboList = null;
		boolean isSaved = false;
		Map<Integer, ErpUsersCampusDBO> userCampusPermissionMap = new HashMap<Integer, ErpUsersCampusDBO>();
		if(!Utils.isNullOrEmpty(data) && !Utils.isNullOrEmpty(idUser)) {
			Set<Integer> functionIds = new HashSet<Integer>();
			for (SysFunctionDTO functionDTO : data.permissions) {
				if(!Utils.isNullOrEmpty(functionDTO))
					functionIds.add(Integer.parseInt(functionDTO.id));
			}
			if(!Utils.isNullOrEmpty(functionIds)) {
				userFunctionOverrideDBOs = privilegeSettingsTransaction.editAditionalPermissionGratedToUser(idUser, functionIds);
			}
			dboList = privilegeSettingsTransaction.getUserRoleMapDBOs(null, idUser);
			if(!Utils.isNullOrEmpty(dboList)) {
				//ErpUserCampus
				Set<Integer> userIds = new HashSet<Integer>();
				userIds.add(Integer.parseInt(idUser));
				userCampusForRoles = privilegeSettingsTransaction1.getUserRoleCampus(idUser).stream().collect(Collectors.groupingBy(
				                s -> Integer.parseInt(String.valueOf(s.get("userId"))),Collectors.mapping( s -> Integer.parseInt(String.valueOf(s.get("campusId"))), Collectors.toSet()
				                )
				        ));
				userCampusPermissionMap = privilegeSettingsTransaction1.getUserCampus(userIds).stream().collect(Collectors.toMap(s -> s.getErpCampusDBO().getId(), s -> s));
				List<SysUserFunctionOverrideDBO> list = privilegeSettingsTransaction1.getUserCampusForAddPermission(idUser);
				if(!Utils.isNullOrEmpty(list)) {
					userCampusForAddPermissionOtherFunctionsSet = list.stream().filter(s -> !functionIds.contains(s.getSysFunctionDBO().getId())).map(item -> item.getErpCampusDBO().getId()).collect(Collectors.toSet());
				}
			    Set<Integer> activeCampus = new HashSet<Integer>();
				 for (SysFunctionDTO functionDTO : data.permissions) {
					   
						//ErpUserCampus 
					   if(functionDTO.granted) {
						   for(ErpCampusDTO campusId :functionDTO.campusList) {
							   if(campusId.getIsGranted()) {
								   activeCampus.add(Integer.parseInt(campusId.getId()));
							   }
						   }
					   }
				 }
				userFunctionOverrideDBOs = privilegeSettingsHelper.convertUserPermissionGratedDTOtoDBO(userFunctionOverrideDBOs, data, idUser, userId, dboList,userCampusPermissionMap,
						userCampusList,userCampusForRoles,userCampusForAddPermissionOtherFunctionsSet,activeCampus);
			}
			if(!Utils.isNullOrEmpty(userFunctionOverrideDBOs))
				isSaved = privilegeSettingsTransaction.saveOrUpdatePermissionsToUser(userFunctionOverrideDBOs);
				privilegeSettingsTransaction1.saveOrUpdateUserCampus(userCampusList);
		}
		return isSaved;
	}

	public ApiResult<List<SysUserRoleMapDTO>> editRoleAssignedToUsers(String roleId, ApiResult<List<SysUserRoleMapDTO>> result) {
		List<SysUserRoleMapDBO> dboList = null;
		List<SysUserRoleMapDTO> dtoList = null;
		if(!Utils.isNullOrEmpty(roleId)) {
			dboList = privilegeSettingsTransaction.getUserRoleMapDBOs(roleId, null);
			dtoList = new ArrayList<SysUserRoleMapDTO>();
			if(!Utils.isNullOrEmpty(dboList)) {
				dtoList = new ArrayList<SysUserRoleMapDTO>();
				dtoList = privilegeSettingsHelper.converUserRoleMapDBOstoDTOs(dboList, dtoList);
			}
			result.dto = dtoList;
			result.success = true;
		}
		return result;
	}

	public Mono<ApiResult> deleteRole(int id, String userId) {
		return privilegeSettingsTransaction1.deleteRole(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Mono<ApiResult> deleteUser(int id, String userId) {
		return privilegeSettingsTransaction1.deleteUser(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Flux<ModuleDTO> getMenus() {
		return privilegeSettingsTransaction1.getMenus().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public ModuleDTO convertDBOToDTO(SysMenuModuleDBO dbo){
		ModuleDTO moduleDto = new ModuleDTO();
		moduleDto.setId(String.valueOf(dbo.getId()));
		moduleDto.setText(dbo.getModuleName());
		moduleDto.setDisplayOrder(dbo.getIsDisplayed());
		moduleDto.setIconClassName(dbo.getIconClassName());
		moduleDto.setMenus(new ArrayList<ModuleSubDTO>());
		dbo.getSysMenuModuleSubDBOs().forEach(subModule -> {
			if(subModule.getRecordStatus() == 'A') {
				ModuleSubDTO moduleSubDto = new ModuleSubDTO();
				moduleSubDto.setSubModule(new SelectDTO());
				moduleSubDto.getSubModule().setValue(String.valueOf(subModule.getId()));
				moduleSubDto.getSubModule().setLabel(subModule.getSubModuleName());
				moduleSubDto.setDisplayOrder(subModule.getMenuScreenDisplayOrder());
//				moduleSubDto.setIconClassName(subModule.getIconClassName());
				moduleSubDto.setMenuScreenList(new ArrayList<MenuScreenDTO>());
				subModule.getSysMenuDBOs().forEach(sysMenu -> {
					if(sysMenu.getRecordStatus() == 'A' && sysMenu.getIsDisplayed()) {
						MenuScreenDTO sysMenuDto = new MenuScreenDTO();
						sysMenuDto.setMenuId(sysMenu.getId());
						sysMenuDto.setMenuName(sysMenu.getMenuScreenName());
						sysMenuDto.setDisplayOrder(sysMenu.getMenuScreenDisplayOrder());
						sysMenuDto.setSysFunctionDTO(new ArrayList<SysFunctionDTO>());
						sysMenu.getSysFunctionDBOSet().forEach(functions -> {
							if(functions.getRecordStatus() == 'A') {
								SysFunctionDTO function = new SysFunctionDTO();
								function.id = String.valueOf(functions.getId());
								function.setFunctionName(functions.getFunctionName());
								function.setFunctionDescription(functions.getFunctionName()+"\n"+functions.getFunctionDescription());
								sysMenuDto.getSysFunctionDTO().add(function);
							}
						});
						moduleSubDto.getMenuScreenList().add(sysMenuDto);
					}
				});
				moduleSubDto.getMenuScreenList().sort(Comparator.comparing(MenuScreenDTO::getDisplayOrder));
				moduleDto.getMenus().add(moduleSubDto);
			}
		});
		moduleDto.getMenus().sort(Comparator.comparing(ModuleSubDTO::getDisplayOrder));
		return moduleDto;
	}

	public Mono<List<ModuleSubDTO>>  getMenuRoles(String menuId) {
		List<SysMenuDBO> data = privilegeSettingsTransaction1.getMenuRoles(menuId);
		List<SysRoleDBO> roles = privilegeSettingsTransaction1.getRoles(menuId);
		return this.convertDBOToDTORole(data,roles);
	}

	public Mono<List<ModuleSubDTO>> convertDBOToDTORole(List<SysMenuDBO> data, List<SysRoleDBO> roles){
		List<ModuleSubDTO> dtos = new ArrayList<ModuleSubDTO>();
		roles.forEach(role -> {
			ModuleSubDTO dto = new ModuleSubDTO();
			dto.setRoles(new SelectDTO());
			dto.getRoles().setValue(String.valueOf(role.getId()));
			dto.getRoles().setLabel(role.getRoleName());
			dto.setMenu(true);
			dto.setPermissions(new ArrayList<SysFunctionDTO>());
			data.forEach( dbo -> {
				if(dbo.getRecordStatus() == 'A') {
					dto.setMenuId(String.valueOf(dbo.getId()));
					dbo.getSysFunctionDBOSet().forEach(function -> {
						if(function.getRecordStatus() == 'A') {
							SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
							sysFunctionDTO.id = String.valueOf(function.getId());
							sysFunctionDTO.setFunctionName(function.getFunctionName());
							sysFunctionDTO.setFunctionDescription(function.getFunctionDescription());
							function.getSysRoleFunctionMapDBOSet().forEach(sysRoleFunctionMap -> {
								if((sysRoleFunctionMap.getSysRoleDBO().getId() == role.getId()) && (function.getId() == sysRoleFunctionMap.getSysFunctionDBO().getId()) && sysRoleFunctionMap.getRecordStatus() == 'A' ) {
									sysFunctionDTO.setGranted(true);
									sysFunctionDTO.setAuthorised(sysRoleFunctionMap.getAuthorised());
								}
							});
							dto.getPermissions().add(sysFunctionDTO);
						}
					});
					dtos.add(dto);
				}
			});
		});
		dtos.sort(Comparator.comparing(s -> s.getRoles().getLabel()));
		return Mono.just(dtos);
	}

	public Mono<List<ModuleSubDTO>>  getMenuUsers(String menuId) {
		List<ErpUsersDBO> usersDetails = new ArrayList<ErpUsersDBO>(); 
		List<SysMenuDBO> data = privilegeSettingsTransaction1.getMenuRoles(menuId);
		List<ErpUsersDBO> usersData = privilegeSettingsTransaction1.getUsers(menuId);
		List<ErpUsersDBO> usersData1 = privilegeSettingsTransaction1.getUsersFunctionOverride(menuId);
		Map<Integer,Set<Integer>> userAdditionalPermissionsMap = new HashMap<Integer, Set<Integer>>();
		List<Integer> usersId = new ArrayList<Integer>();
		usersData.forEach(user -> {
//			if(user.getRecordStatus() == 'A' || user.getRecordStatus() == 'I') {
				user.getSysUserRoleMapDBOs().forEach( sysUserRole -> {
					if(sysUserRole.getRecordStatus() == 'A') {
						if(sysUserRole.getSysRoleDBO().getRecordStatus() == 'A') {
							sysUserRole.getSysRoleDBO().getSysRoleFunctionMapDBOs().forEach(SysRoleFunctionMap -> {
								if(SysRoleFunctionMap.getRecordStatus() == 'A') {
									if(!usersId.contains(user.getId())) {
										usersId.add(user.getId());
										usersDetails.add(user);
									}
								}
							});
						}
					}
				});
//			}

		});
		usersData1.forEach( value -> {
//			if(value.getRecordStatus() == 'A' || value.getRecordStatus() == 'I') {
				value.getSysUserFunctionOverrideDBOSet().forEach( sysUserFunctionOverrideDBO -> {
					if(sysUserFunctionOverrideDBO.getRecordStatus() == 'A') {
						if(!usersId.contains(value.getId())) {
							usersId.add(value.getId());
							usersDetails.add(value);
						}
					}
				});
//			}		
		});

		List<SysUserFunctionOverrideDBO> usersAdditionalFunctions = privilegeSettingsTransaction1.getUsersAdditionalPermissions(usersId);
		usersAdditionalFunctions.forEach( additionalFunctionsUser -> {
			if(!userAdditionalPermissionsMap.containsKey(additionalFunctionsUser.getErpUsersDBO().getId())) {
				Set<Integer> function = new LinkedHashSet<Integer>();
				function.add(additionalFunctionsUser.getSysFunctionDBO().getId());
				userAdditionalPermissionsMap.put(additionalFunctionsUser.getErpUsersDBO().getId(), function);
			} else {
				Set<Integer> function = userAdditionalPermissionsMap.get(additionalFunctionsUser.getErpUsersDBO().getId());
				function.add(additionalFunctionsUser.getSysFunctionDBO().getId());
				userAdditionalPermissionsMap.replace(additionalFunctionsUser.getErpUsersDBO().getId(), function);
			}
		});
		return this.convertDBOToDTO1(data,usersDetails,userAdditionalPermissionsMap);
	}

	public Mono<List<ModuleSubDTO>> convertDBOToDTO1(List<SysMenuDBO> data, List<ErpUsersDBO> users, Map<Integer, Set<Integer>> userAdditionalPermissionsMap){
		Map<Integer,Set<Integer>> userRoles = new HashMap<Integer, Set<Integer>>();
		Map<Integer,List<Integer>> roleFunctionsListMap = new HashMap<Integer, List<Integer>>();
		Map<String,List<Integer>> roleCampuslistMap = new HashMap<String, List<Integer>>();
		List<ModuleSubDTO> dtos = new ArrayList<ModuleSubDTO>();
		List<Integer> duplicateCheckCampus = new ArrayList<Integer>();
		users.forEach( uservalue -> {
			Set<Integer> roleIds = new LinkedHashSet<Integer>();
			uservalue.getSysUserRoleMapDBOs().forEach(role -> {
				if(role.getRecordStatus() == 'A'){
					roleIds.add(role.getSysRoleDBO().getId());
					if(!roleCampuslistMap.containsKey(String.valueOf(role.getSysRoleDBO().getId())+uservalue.getId())) {
						List<Integer> campus = new ArrayList<Integer>();
						campus.add(role.getErpCampusDBO().getId());
						roleCampuslistMap.put(String.valueOf(role.getSysRoleDBO().getId())+uservalue.getId(), campus);
	
						if(!roleFunctionsListMap.containsKey(role.getSysRoleDBO().getId())) {
							List<Integer> funtionsIds = new ArrayList<Integer>();
							role.getSysRoleDBO().getSysRoleFunctionMapDBOs().forEach( functions -> {
								if(functions.getRecordStatus() == 'A') {
									funtionsIds.add(functions.getSysFunctionDBO().getId());
								}
							});
							roleFunctionsListMap.put(role.getSysRoleDBO().getId(), funtionsIds);
						}
					} else {
						List<Integer> campus = roleCampuslistMap.get(String.valueOf(role.getSysRoleDBO().getId())+uservalue.getId());
						if(!campus.contains(role.getErpCampusDBO().getId())) {
							campus.add(role.getErpCampusDBO().getId());
							roleCampuslistMap.replace(String.valueOf(role.getSysRoleDBO().getId())+uservalue.getId(), campus);
						}
					}
				}
			});
			userRoles.put(uservalue.getId(), roleIds);
			ModuleSubDTO dto = new ModuleSubDTO();
			dto.setUsers(new SelectDTO());
			dto.getUsers().setValue(String.valueOf(uservalue.getId()));
			dto.getUsers().setLabel(uservalue.getLoginId());
			dto.setMenu(true);
			dto.setTotalAdditionalPrivilegeAllowed( Utils.getTotalAdditionalPrivilageAllowed());
			dto.setTotalAdditionalPrivilegeCount(userAdditionalPermissionsMap.containsKey(uservalue.getId()) ? userAdditionalPermissionsMap.get(uservalue.getId()).size(): 0);
			dto.setPermissions(new ArrayList<SysFunctionDTO>()); 
			data.forEach( dbo -> {
				dto.setMenuId(String.valueOf(dbo.getId()));
				dbo.getSysFunctionDBOSet().forEach(function -> {
					if(function.getRecordStatus() == 'A') {
						SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
						sysFunctionDTO.id = String.valueOf(function.getId());
						sysFunctionDTO.setFunctionName(function.getFunctionName());
						sysFunctionDTO.setFunctionDescription(function.getFunctionDescription());	
						sysFunctionDTO.setCampusList(new ArrayList<ErpCampusDTO>());
						duplicateCheckCampus.clear();
						function.getSysUserFunctionOverrideDBOSet().forEach(sysUserFunctionOverrideDBO -> {
							if(sysUserFunctionOverrideDBO.getErpUsersDBO().getId().equals(uservalue.getId()) && function.getId() == sysUserFunctionOverrideDBO.getSysFunctionDBO().getId()
									&& sysUserFunctionOverrideDBO.getRecordStatus() == 'A') {
								if(!duplicateCheckCampus.contains(sysUserFunctionOverrideDBO.getErpCampusDBO().getId())) {
									sysFunctionDTO.setGranted(true);
									ErpCampusDTO campus = new ErpCampusDTO();
									campus.setCampusId(sysUserFunctionOverrideDBO.getErpCampusDBO().getId().toString());
									campus.setCampusName(sysUserFunctionOverrideDBO.getErpCampusDBO().getCampusName());
									campus.setIsGranted(sysUserFunctionOverrideDBO.isAllowed);
									campus.setIsAdditionalPrivilege(sysUserFunctionOverrideDBO.isAllowed);
									campus.setCommonPrivilege(!sysUserFunctionOverrideDBO.isAllowed);
									sysFunctionDTO.getCampusList().add(campus);
									duplicateCheckCampus.add(sysUserFunctionOverrideDBO.getErpCampusDBO().getId());
								}
							} 
						});	
						this.addCommonCampus(userRoles,uservalue,roleCampuslistMap,roleFunctionsListMap,function,duplicateCheckCampus,sysFunctionDTO);
						dto.getPermissions().add(sysFunctionDTO);
					}
				});
				dtos.add(dto);
			});
		});
		dtos.sort(Comparator.comparing(s -> s.getUsers().getLabel()));
		return Mono.just(dtos);
	}
	public void addCommonCampus(Map<Integer, Set<Integer>> userRoles, ErpUsersDBO uservalue, Map<String, List<Integer>> roleCampuslistMap,
			Map<Integer, List<Integer>> roleFunctionsListMap, SysFunctionDBO function, List<Integer> duplicateCheckCampus, SysFunctionDTO sysFunctionDTO) {
		Set<Integer> roles = userRoles.get(uservalue.getId());
		roles.forEach(role -> {
			List<Integer> functionsList = roleFunctionsListMap.get(role);
			if(functionsList.contains(function.getId())) {
				List<Integer> campuslistRole = roleCampuslistMap.get(String.valueOf(role)+uservalue.getId());
				campuslistRole.forEach(campusRole -> {
					if(!duplicateCheckCampus.contains(campusRole)) {		
						ErpCampusDTO campus1 = new ErpCampusDTO();
						campus1.setCampusId(campusRole.toString());
						//						campus1.setCampusName(sysUserFunctionOverrideDBO.getErpCampusDBO().getCampusName());
						campus1.setIsGranted(true);
						campus1.setIsAdditionalPrivilege(false);
						campus1.setCommonPrivilege(true);
						sysFunctionDTO.setGranted(true);
						sysFunctionDTO.getCampusList().add(campus1);
						duplicateCheckCampus.add(campusRole);
					}
				});
			}
		});
	}

	public Mono<ApiResult> saveOrUpdateFunctionsToRoles(Mono<List<ModuleSubDTO>> dto, String userId) {
		return dto.map(data -> saveOrUpdateFunctionsToRolesDtoToDbo(data,userId))
				.flatMap( s -> { privilegeSettingsTransaction1.saveRoles(s);
				return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<SysRoleFunctionMapDBO> saveOrUpdateFunctionsToRolesDtoToDbo(List<ModuleSubDTO> dto1, String userId) {
		Map<String, SysRoleFunctionMapDBO> map1;
		List<SysRoleFunctionMapDBO> rolesInfo;
		Map<Integer,Set<Integer>> roleUsers = new HashMap<Integer, Set<Integer>>();
		Map<String,Set<Integer>>  roleUsersCampusList = new HashMap<String, Set<Integer>>();
		Set<Integer> functionsList = new HashSet<Integer>();
		Set<Integer> usersList = new HashSet<Integer>();
		List<SysRoleFunctionMapDBO> value = new ArrayList<SysRoleFunctionMapDBO>();
		Set<Integer> roles = new HashSet<Integer>();
		if(dto1.get(0).getMenu()) {
			rolesInfo = privilegeSettingsTransaction1.getRolefunctions(null,dto1.get(0).getMenuId());
			map1 = rolesInfo.stream().filter(s -> s.getRecordStatus() == 'A')
					.collect(Collectors.toMap(s -> String.valueOf(s.getSysRoleDBO().getId()) + String.valueOf(s.sysFunctionDBO.getId()), s -> s));
		} else {
			rolesInfo =privilegeSettingsTransaction1.getRolesForFunction(dto1.get(0).permissions.get(0).id);
			map1 = rolesInfo.stream().filter(s -> s.getRecordStatus() == 'A')
					.collect(Collectors.toMap(s -> String.valueOf(s.getSysRoleDBO().getId()) + String.valueOf(s.sysFunctionDBO.getId()), s -> s));
		}

		rolesInfo.forEach( data -> {
			data.getSysRoleDBO().getSysUserRoleMapDBOSet().forEach( users -> {
				if(!roleUsers.containsKey(data.getSysRoleDBO().getId())) {
					Set<Integer> user = new LinkedHashSet<Integer>();
					user.add(users.getErpUsersDBO().getId());
					roleUsers.put(data.getSysRoleDBO().getId(), user);
				} else {
					Set<Integer> user = roleUsers.get(data.getSysRoleDBO().getId());
					user.add(users.getErpUsersDBO().getId());
					roleUsers.replace(data.getSysRoleDBO().getId(), user);
				}
				if(!roleUsersCampusList.containsKey(String.valueOf(data.getSysRoleDBO().getId())+users.getErpUsersDBO().getId())) {
					Set<Integer> user = new LinkedHashSet<Integer>();
					user.add(users.getErpCampusDBO().getId());
					roleUsersCampusList.put(String.valueOf(data.getSysRoleDBO().getId())+users.getErpUsersDBO().getId(), user);
				} else {
					Set<Integer> user = roleUsersCampusList.get(String.valueOf(data.getSysRoleDBO().getId())+users.getErpUsersDBO().getId());
					user.add(users.getErpCampusDBO().getId());
					roleUsersCampusList.replace(String.valueOf(data.getSysRoleDBO().getId())+users.getErpUsersDBO().getId(), user);
				}
				usersList.add(users.getErpUsersDBO().getId());
			});
		});
		dto1.forEach( dto -> {
			dto.getPermissions().forEach(function -> {
				if(function.granted) {
					if(!map1.containsKey(dto.getRoles().getValue()+function.id)) 	{
						SysRoleFunctionMapDBO sysRoleFunction = new SysRoleFunctionMapDBO();
						sysRoleFunction.setSysRoleDBO(new SysRoleDBO());
						sysRoleFunction.getSysRoleDBO().setId(Integer.parseInt(dto.getRoles().getValue()));
						roles.add(Integer.parseInt(dto.getRoles().getValue()));
						sysRoleFunction.setSysFunctionDBO(new SysFunctionDBO());
						sysRoleFunction.getSysFunctionDBO().setId(Integer.parseInt(function.id));
						sysRoleFunction.setAuthorised(true);
						functionsList.add(Integer.parseInt(function.id));
						sysRoleFunction.setRecordStatus('A');
						sysRoleFunction.setCreatedUsersId(Integer.parseInt(userId));
						value.add(sysRoleFunction);
					} else {
						SysRoleFunctionMapDBO sysRoleFunction = map1.get(dto.getRoles().getValue()+function.id);
						if(!Utils.isNullOrEmpty(function.getAuthorised())) {
							if(!function.getAuthorised() == sysRoleFunction.getAuthorised()) {
								sysRoleFunction.setAuthorised(function.getAuthorised());
								sysRoleFunction.setModifiedUsersId(Integer.parseInt(userId));
								value.add(sysRoleFunction);
							}
						}
						map1.remove(dto.getRoles().getValue()+function.id);
					}
				} else {
					if(map1.containsKey(dto.getRoles().getValue()+function.id)) {
						//					if(map.containsKey(Integer.parseInt(function.id))) {
						SysRoleFunctionMapDBO sysRoleFunction = map1.get(dto.getRoles().getValue()+function.id);
						sysRoleFunction.setRecordStatus('D');
						sysRoleFunction.setModifiedUsersId(Integer.parseInt(userId));
						value.add(sysRoleFunction);

						functionsList.add(Integer.parseInt(function.id));
						roles.add(Integer.parseInt(dto.getRoles().getValue()));
						map1.remove(dto.getRoles().getValue()+function.id);
					}
				}
			});
		});
		map1.forEach((key,values) -> {
			values.setRecordStatus('D');
			values.setModifiedUsersId(Integer.parseInt(userId));
			value.add(values);
		});
		return value;
	}

	public Mono<ApiResult> saveOrUpdateFunctionsToUsers(Mono<List<ModuleSubDTO>> dto, String userId) {
		List<ErpUsersCampusDBO> userCampusList = new ArrayList<ErpUsersCampusDBO>();
		return dto.map(data -> saveOrUpdateFunctionsToUsersDtoToDbo(data,userId,userCampusList))
				.flatMap( s -> { privilegeSettingsTransaction1.saveUsers(s);
				privilegeSettingsTransaction1.saveOrUpdateUserCampus(userCampusList);
				return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<SysUserFunctionOverrideDBO> saveOrUpdateFunctionsToUsersDtoToDbo(List<ModuleSubDTO> dto1, String userId,List<ErpUsersCampusDBO> userCampusList) {
		List<SysUserFunctionOverrideDBO> value = new ArrayList<SysUserFunctionOverrideDBO>();
		Map<Integer, Set<Integer>> userCampusForRoles = new HashMap<Integer, Set<Integer>>();
		Map<Integer,Set<Integer>> userActiveCampuesMap = new HashMap<Integer, Set<Integer>>();
		Map<Integer,Set<Integer>> userCampusForAddPermissionOtherFunctionsMap = new HashMap<Integer, Set<Integer>>();
		Set<Integer> userIds = new HashSet<Integer>();
		Set<Integer> functionIds = new HashSet<Integer>();
		dto1.get(0).getPermissions().forEach(functions -> {
			functionIds.add(Integer.parseInt(functions.id));
		});
		dto1.forEach(dto -> {
			if(!Utils.isNullOrEmpty(dto.getUsers()))
				userIds.add(Integer.parseInt(dto.getUsers().getValue()));
		});
		//ErpUserCampus
		List<SysUserFunctionOverrideDBO> list = privilegeSettingsTransaction1.getUsersAdditionalPermissions(new ArrayList<Integer>(userIds));
		if(!Utils.isNullOrEmpty(list)) {
			userCampusForAddPermissionOtherFunctionsMap = list.stream().filter(s -> !functionIds.contains(s.getSysFunctionDBO().getId())).
					collect(Collectors.groupingBy(s -> s.getErpUsersDBO().getId(),Collectors.mapping(s -> s.getErpCampusDBO().getId(), Collectors.toSet())));
		}
		userCampusForRoles = privilegeSettingsTransaction1.getUserRoleCampus1(userIds).stream().collect(Collectors.groupingBy(
		                s -> Integer.parseInt(String.valueOf(s.get("userId"))),Collectors.mapping(s -> Integer.parseInt(String.valueOf(s.get("campusId"))), Collectors.toSet())
		            ));
		Map<Integer, Map<Integer, ErpUsersCampusDBO>> userCampusPermissionMap = privilegeSettingsTransaction1.getUserCampus(userIds).stream()
			    .collect(Collectors.groupingBy(s -> s.getErpUsersDBO().getId(), Collectors.toMap(r -> r.getErpCampusDBO().getId(), r -> r)));
		
		Map<String, SysUserFunctionOverrideDBO> map;
		if(dto1.get(0).getMenu()) {
			map = privilegeSettingsTransaction1.getUserfunctions(userIds,dto1.get(0).getMenuId()).stream().filter(s -> s.getRecordStatus() == 'A')
					.collect(Collectors.toMap(s -> String.valueOf(s.getErpUsersDBO().getId())+s.getSysFunctionDBO().getId()+s.getErpCampusDBO().getId(), s ->s ));
		} else {
			map = privilegeSettingsTransaction1.getUsersForFunction(userIds,dto1.get(0).permissions.get(0).id).stream().filter(s -> s.getRecordStatus() == 'A')
					.collect(Collectors.toMap(s -> String.valueOf(s.getErpUsersDBO().getId())+s.getSysFunctionDBO().getId()+s.getErpCampusDBO().getId(), s ->s ));
		}

		for(ModuleSubDTO dto : dto1) {
		    Set<Integer> roleCampus = new HashSet<Integer>();
		    Set<Integer> userOtherFunctionsSet = new HashSet<Integer>();
			if(userCampusForRoles.containsKey(Integer.parseInt(dto.getUsers().getValue()))) {
				roleCampus = userCampusForRoles.get(Integer.parseInt(dto.getUsers().getValue()));
			}
			Map<Integer, ErpUsersCampusDBO> userCampusMap = new HashMap<Integer, ErpUsersCampusDBO>();
			if(!Utils.isNullOrEmpty(userCampusPermissionMap)  && userCampusPermissionMap.containsKey(Integer.parseInt(dto.getUsers().getValue()))) {
				userCampusMap = userCampusPermissionMap.get(Integer.parseInt(dto.getUsers().getValue()));
			}
			userActiveCampuesMap.put(Integer.parseInt(dto.getUsers().getValue()), new HashSet<Integer>());
			if(userCampusForAddPermissionOtherFunctionsMap.containsKey(Integer.parseInt(dto.getUsers().getValue()))) {
			 userOtherFunctionsSet = userCampusForAddPermissionOtherFunctionsMap.get(Integer.parseInt(dto.getUsers().getValue()));
			}
			for(SysFunctionDTO function :dto.getPermissions()) {
				if(function.granted) {
					for(ErpCampusDTO campus : function.getCampusList()) {	
						//ErpUserCampus  
						if(campus.getIsGranted()) {
							Set<Integer> activeCampus = userActiveCampuesMap.get(Integer.parseInt(dto.getUsers().getValue()));
							activeCampus.add(Integer.parseInt(campus.getCampusId()));
						}
						if(campus.getIsGranted() && !userCampusMap.containsKey(Integer.parseInt(campus.getCampusId()))) {
							ErpUsersCampusDBO userCampus = new ErpUsersCampusDBO();
							userCampus.setErpUsersDBO(new ErpUsersDBO());
							userCampus.getErpUsersDBO().setId(Integer.parseInt(dto.getUsers().getValue()));
							userCampus.setErpCampusDBO(new ErpCampusDBO());
							userCampus.getErpCampusDBO().setId(Integer.parseInt(campus.getCampusId()));
							userCampus.setIsPreferred(true);
							userCampus.setCreatedUsersId(Integer.parseInt(userId));
							userCampus.setRecordStatus('A');
							userCampusList.add(userCampus);
							userCampusMap.put(Integer.parseInt(campus.getCampusId()), userCampus);
						} else if(!campus.getIsGranted() &&  !Utils.isNullOrEmpty(userCampusMap) && userCampusMap.containsKey(Integer.parseInt(campus.getCampusId()))
								&& !roleCampus.contains(Integer.parseInt(campus.getCampusId())) && !userOtherFunctionsSet.contains(Integer.parseInt(campus.getCampusId()))) {
							ErpUsersCampusDBO userCampus =  userCampusMap.get(Integer.parseInt(campus.getCampusId()));
							userCampus.setRecordStatus('D');
							userCampus.setModifiedUsersId(Integer.parseInt(userId));
							userCampusList.add(userCampus);
						}
						//						   
						if(campus.getIsAdditionalPrivilege()) { 
							if(!map.containsKey(dto.getUsers().getValue()+function.id+campus.getCampusId())) {
								SysUserFunctionOverrideDBO sysUserFunctionOverride = new SysUserFunctionOverrideDBO();
								sysUserFunctionOverride.setErpUsersDBO(new ErpUsersDBO());
								sysUserFunctionOverride.getErpUsersDBO().setId(Integer.valueOf(dto.getUsers().getValue()));
								sysUserFunctionOverride.setSysFunctionDBO(new SysFunctionDBO());
								sysUserFunctionOverride.getSysFunctionDBO().setId(Integer.parseInt(function.id));
								sysUserFunctionOverride.setErpCampusDBO(new ErpCampusDBO());
								sysUserFunctionOverride.getErpCampusDBO().setId(Integer.parseInt(campus.getCampusId()));
								sysUserFunctionOverride.setAllowed(true);
								sysUserFunctionOverride.setRecordStatus('A');
								sysUserFunctionOverride.setCreatedUsersId(Integer.parseInt(userId));
								value.add(sysUserFunctionOverride);
							} else {
								map.remove(dto.getUsers().getValue()+function.id+campus.getCampusId());
							}
						} 
						if(campus.isGranted) {
							if(map.containsKey(dto.getUsers().getValue()+function.id+campus.getCampusId())) {
								SysUserFunctionOverrideDBO sysUserFunctionOverride = map.get(dto.getUsers().getValue()+function.id+campus.getCampusId());
								sysUserFunctionOverride.setRecordStatus('D');
								sysUserFunctionOverride.setModifiedUsersId(Integer.parseInt(userId));
								value.add(sysUserFunctionOverride);
								map.remove(dto.getUsers().getValue()+function.id+campus.getCampusId());
							}
						}
						//Common Privilege Removed For campus
						if(!campus.isGranted && !campus.getIsAdditionalPrivilege()) {
							if(!map.containsKey(dto.getUsers().getValue()+function.id+campus.getCampusId())) {
								SysUserFunctionOverrideDBO sysUserFunctionOverride = new SysUserFunctionOverrideDBO();
								sysUserFunctionOverride.setErpUsersDBO(new ErpUsersDBO());
								sysUserFunctionOverride.getErpUsersDBO().setId(Integer.valueOf(dto.getUsers().getValue()));
								sysUserFunctionOverride.setSysFunctionDBO(new SysFunctionDBO());
								sysUserFunctionOverride.getSysFunctionDBO().setId(Integer.parseInt(function.id));
								sysUserFunctionOverride.setErpCampusDBO(new ErpCampusDBO());
								sysUserFunctionOverride.getErpCampusDBO().setId(Integer.parseInt(campus.getCampusId()));
								sysUserFunctionOverride.setAllowed(false);
								sysUserFunctionOverride.setRecordStatus('A');
								sysUserFunctionOverride.setCreatedUsersId(Integer.parseInt(userId));
								value.add(sysUserFunctionOverride);
							} else {
								map.remove(dto.getUsers().getValue()+function.id+campus.getCampusId());
							}
						}
					}	
				} else {
//					function.getCampusList().forEach(campus -> {
					for(ErpCampusDTO campus : function.getCampusList()) {
						if(map.containsKey(dto.getUsers().getValue()+function.id+campus.getCampusId())) {
							SysUserFunctionOverrideDBO sysUserFunctionOverride = map.get(dto.getUsers().getValue()+function.id+campus.getCampusId());
							sysUserFunctionOverride.setRecordStatus('D');
							sysUserFunctionOverride.setModifiedUsersId(Integer.parseInt(userId));
							value.add(sysUserFunctionOverride);
							map.remove(dto.getUsers().getValue()+function.id+campus.getCampusId());
							
							//ErpUserCampus 
							if(!Utils.isNullOrEmpty(userCampusMap) && userCampusMap.containsKey(Integer.parseInt(campus.getCampusId()))
									&& !roleCampus.contains(Integer.parseInt(campus.getCampusId())) && !userOtherFunctionsSet.contains(Integer.parseInt(campus.getCampusId()))) {
								ErpUsersCampusDBO userCampus =  userCampusMap.get(Integer.parseInt(campus.getCampusId()));
								userCampus.setRecordStatus('D');
								userCampus.setModifiedUsersId(Integer.parseInt(userId));
								userCampusList.add(userCampus);
							}
						}
					}
//					});
				}
			}
		}
		
//		map.forEach((key,values) -> {
//			values.setRecordStatus('D');
//			values.setModifiedUsersId(Integer.parseInt(userId));
//			value.add(values);
//			
//		});
		
		for (Map.Entry<String, SysUserFunctionOverrideDBO> entry : map.entrySet()) {
		   SysUserFunctionOverrideDBO values = entry.getValue();
		    values.setRecordStatus('D');
		    values.setModifiedUsersId(Integer.parseInt(userId));
		    value.add(values);
		    
		    //ErpUserCampus
		    Set<Integer> roleCampus  = new HashSet<Integer>();
		    Set<Integer> userOtherFunctionsSet = new HashSet<Integer>();
			if(userCampusForRoles.containsKey(values.getErpUsersDBO().getId())) {
				 roleCampus = userCampusForRoles.get(values.getErpUsersDBO().getId());
			}
			Map<Integer, ErpUsersCampusDBO> userCampusMap = null;
			if(!Utils.isNullOrEmpty(userCampusPermissionMap)  && userCampusPermissionMap.containsKey(values.getErpUsersDBO().getId())) {
				userCampusMap = userCampusPermissionMap.get(values.getErpUsersDBO().getId());
			}
			Set<Integer> userActiveCampus = userActiveCampuesMap.get(values.getErpUsersDBO().getId());
			if(userCampusForAddPermissionOtherFunctionsMap.containsKey((values.getErpUsersDBO().getId()))) {
				 userOtherFunctionsSet = userCampusForAddPermissionOtherFunctionsMap.get((values.getErpUsersDBO().getId()));
			}
			if( !Utils.isNullOrEmpty(userCampusMap) && userCampusMap.containsKey(values.getErpCampusDBO().getId()) && !roleCampus.contains(values.getErpCampusDBO().getId()) 
					&& !userActiveCampus.contains(values.getErpCampusDBO().getId()) && !userOtherFunctionsSet.contains(values.getErpCampusDBO().getId())) {
				ErpUsersCampusDBO userCampus =  userCampusMap.get(values.getErpCampusDBO().getId());
				userCampus.setRecordStatus('D');
				userCampus.setModifiedUsersId(Integer.parseInt(userId));
				userCampusList.add(userCampus);
			}

		}
		return value;
	}

	public Mono<List<ModuleSubDTO>>  getRolesOrUsersForFunction(String functionId, Boolean role) {
		List<SysRoleFunctionMapDBO> roleData = null;
		List<SysUserFunctionOverrideDBO> userOverrideData = null;
		List<Integer> usersId = new ArrayList<Integer>();
		Map<Integer,ErpUsersDBO> userInfo = new HashMap<Integer, ErpUsersDBO>();
		Map<String,List<Integer>> roleCampuslistMap = new HashMap<String, List<Integer>>();
		Map<Integer,Set<Integer>> userRoles = new HashMap<Integer, Set<Integer>>();
		Map<Integer,Set<Integer>> userAdditionalPermissionsMap = new HashMap<Integer, Set<Integer>>();
		Set<String> userAdditionalPermissionsCampus = new HashSet<String>();
		SysRoleFunctionMapDBO FunctionDetials = null;
		if(role) {
			roleData = privilegeSettingsTransaction1.getRolesForFunction1(functionId);
		} else {
			userOverrideData = privilegeSettingsTransaction1.getUsersForFunction(null,functionId);
			userOverrideData.forEach(user -> {
				if(user.getErpUsersDBO().getRecordStatus() == 'A') {		
					if(!usersId.contains(user.getErpUsersDBO().getId())) {
						usersId.add(user.getErpUsersDBO().getId());
					}
					userAdditionalPermissionsCampus.add(String.valueOf(user.getErpUsersDBO().getId())+user.getErpCampusDBO().getId());
				}
			});

			List<ErpUsersDBO> userDatas = privilegeSettingsTransaction1.getUsersForFunctions(functionId);
			if(!Utils.isNullOrEmpty(userDatas)) {
				FunctionDetials = userDatas.get(0).getSysUserRoleMapDBOs().get(0).getSysRoleDBO().getSysRoleFunctionMapDBOs().iterator().next();
			}
			userDatas.forEach(user -> {
				if(user.getRecordStatus() == 'A') {
					if(!userInfo.containsKey(user.getId())) {
						userInfo.put(user.getId(), user);
						usersId.add(user.getId());
					}
					user.getSysUserRoleMapDBOs().forEach( rolesOfUsers-> {
						if(rolesOfUsers.getRecordStatus() == 'A') {
							if(rolesOfUsers.getSysRoleDBO().getRecordStatus() == 'A') {
								rolesOfUsers.getSysRoleDBO().getSysRoleFunctionMapDBOs().forEach(SysRoleFunctionMap -> {
									if(SysRoleFunctionMap.getRecordStatus() == 'A') {
										if(!userInfo.containsKey(user.getId())) {
											userInfo.put(user.getId(), user);
											usersId.add(user.getId());
										}
										if(!userRoles.containsKey(user.getId())) {
											Set<Integer> roles = new  LinkedHashSet<Integer>();
											roles.add(rolesOfUsers.getSysRoleDBO().getId());
											userRoles.put(user.getId(), roles);						
										} else {
											Set<Integer> roles = userRoles.get(user.getId());
											roles.add(rolesOfUsers.getSysRoleDBO().getId());
											userRoles.replace(user.getId(), roles);
										}
										if(!roleCampuslistMap.containsKey(String.valueOf(rolesOfUsers.getSysRoleDBO().getId())+user.getId())) {
											List<Integer> campus = new ArrayList<Integer>();
											campus.add(rolesOfUsers.getErpCampusDBO().getId());
											roleCampuslistMap.put(String.valueOf(rolesOfUsers.getSysRoleDBO().getId())+user.getId(), campus);
										} else {
											List<Integer> campus = roleCampuslistMap.get(String.valueOf(rolesOfUsers.getSysRoleDBO().getId())+user.getId());
											campus.add(rolesOfUsers.getErpCampusDBO().getId());
											roleCampuslistMap.replace(String.valueOf(rolesOfUsers.getSysRoleDBO().getId())+user.getId(), campus);
										}
									}
								});
							}
						}
					});
				}
			});
			List<SysUserFunctionOverrideDBO> usersAdditionalFunctions = privilegeSettingsTransaction1.getUsersAdditionalPermissions(usersId);
			usersAdditionalFunctions.forEach( additionalFunctionsUser -> {
				if(!userAdditionalPermissionsMap.containsKey(additionalFunctionsUser.getErpUsersDBO().getId())) {
					Set<Integer> function = new LinkedHashSet<Integer>();
					function.add(additionalFunctionsUser.getSysFunctionDBO().getId());
					userAdditionalPermissionsMap.put(additionalFunctionsUser.getErpUsersDBO().getId(), function);
				} else {
					Set<Integer> function = userAdditionalPermissionsMap.get(additionalFunctionsUser.getErpUsersDBO().getId());
					function.add(additionalFunctionsUser.getSysFunctionDBO().getId());
					userAdditionalPermissionsMap.replace(additionalFunctionsUser.getErpUsersDBO().getId(), function);
				}
			});
		}
		return  (role) ? this.convertDBOToDTO1(roleData) : this.convertDBOToDTO(userOverrideData,userRoles,roleCampuslistMap,userInfo,userAdditionalPermissionsMap,FunctionDetials,userAdditionalPermissionsCampus) ;
	}

	public Mono<List<ModuleSubDTO>> convertDBOToDTO1(List<SysRoleFunctionMapDBO> data){
		List<ModuleSubDTO> dtos = new ArrayList<ModuleSubDTO>();
		data.forEach(dbo -> {
			ModuleSubDTO dto = new ModuleSubDTO();
			dto.setRoles(new SelectDTO());
			dto.getRoles().setValue(String.valueOf(dbo.getSysRoleDBO().getId()));
			dto.getRoles().setLabel(dbo.getSysRoleDBO().getRoleName());
			dto.setPermissions(new ArrayList<SysFunctionDTO>());
			SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
			sysFunctionDTO.id = String.valueOf(dbo.getSysFunctionDBO().getId());
			sysFunctionDTO.setFunctionName(dbo.getSysFunctionDBO().getFunctionName());
			sysFunctionDTO.setFunctionDescription(dbo.getSysFunctionDBO().getFunctionDescription());
			sysFunctionDTO.setAuthorised(dbo.getAuthorised());
			dto.setMenuId(String.valueOf(dbo.getSysFunctionDBO().getSysMenuDBO().getId()));
			sysFunctionDTO.setGranted(true);
			dto.getPermissions().add(sysFunctionDTO);
			dtos.add(dto);
		});
		if(!Utils.isNullOrEmpty(dtos)) {
			dtos.sort(Comparator.comparing(s -> s.getRoles().getLabel()));
		}
		return !Utils.isNullOrEmpty(dtos) ? Mono.just(dtos) : Mono.error(new NotFoundException(null));  
	}

	public Mono<List<ModuleSubDTO>> convertDBOToDTO(List<SysUserFunctionOverrideDBO> data, Map<Integer, Set<Integer>> userRoles, Map<String,
			List<Integer>> roleCampuslistMap, Map<Integer, ErpUsersDBO> userInfo, Map<Integer, Set<Integer>> userAdditionalPermissionsMap, SysRoleFunctionMapDBO functionDetials, Set<String> userAdditionalPermissionsCampus){

		List<ModuleSubDTO> dtos = new ArrayList<ModuleSubDTO>();
		Map<String,SysFunctionDTO> map = new HashMap<String, SysFunctionDTO>();
		List<Integer> duplicateCheckCampus = new ArrayList<Integer>();
		data.forEach(dbo -> {
			if(dbo.getErpUsersDBO().getRecordStatus() == 'A') {
				duplicateCheckCampus.clear();
				if(!map.containsKey(String.valueOf(dbo.getErpUsersDBO().getId())+dbo.getSysFunctionDBO().getId())) {		
					ModuleSubDTO dto = new ModuleSubDTO();
					dto.setUsers(new SelectDTO());
					dto.getUsers().setValue(String.valueOf(dbo.getErpUsersDBO().getId()));
					dto.getUsers().setLabel(dbo.getErpUsersDBO().getLoginId());
					dto.setPermissions(new ArrayList<SysFunctionDTO>());
					SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
					sysFunctionDTO.id = String.valueOf(dbo.getSysFunctionDBO().getId());
					sysFunctionDTO.setFunctionName(dbo.getSysFunctionDBO().getFunctionName());
					sysFunctionDTO.setFunctionDescription(dbo.getSysFunctionDBO().getFunctionDescription());
					sysFunctionDTO.setCampusList(new ArrayList<ErpCampusDTO>());
					dto.setTotalAdditionalPrivilegeAllowed(Utils.getTotalAdditionalPrivilageAllowed());
					dto.setTotalAdditionalPrivilegeCount(userAdditionalPermissionsMap.containsKey(dbo.getErpUsersDBO().getId()) ? userAdditionalPermissionsMap.get(dbo.getErpUsersDBO().getId()).size(): 0);
	
					ErpCampusDTO campus = new ErpCampusDTO();
					campus.setCampusId(dbo.getErpCampusDBO().getId().toString());
					campus.setCampusName(dbo.getErpCampusDBO().getCampusName());
					campus.setIsGranted(dbo.isAllowed);
					campus.setIsAdditionalPrivilege(dbo.isAllowed);
					campus.setCommonPrivilege(!dbo.isAllowed);
					sysFunctionDTO.getCampusList().add(campus);
					duplicateCheckCampus.add(dbo.getErpCampusDBO().getId());
	
					if(userRoles.containsKey(dbo.getErpUsersDBO().getId())) {
						Set<Integer> roles = userRoles.get(dbo.getErpUsersDBO().getId());
						roles.forEach(role -> {
							if(roleCampuslistMap.containsKey(String.valueOf(role)+dbo.getErpUsersDBO().getId())) {
								List<Integer> campuslist = roleCampuslistMap.get(String.valueOf(role)+dbo.getErpUsersDBO().getId());
								campuslist.forEach( commonCampus ->{
									if(!duplicateCheckCampus.contains(commonCampus) && !userAdditionalPermissionsCampus.contains(String.valueOf(dbo.getErpUsersDBO().getId())+commonCampus)) {
										ErpCampusDTO campus1 = new ErpCampusDTO();
										campus1.setCampusId(commonCampus.toString());
										//									campus1.setCampusName(dbo.getErpCampusDBO().getCampusName());
										campus1.setIsGranted(true);
										campus1.setIsAdditionalPrivilege(false);
										campus1.setCommonPrivilege(true);
										sysFunctionDTO.getCampusList().add(campus1);
										duplicateCheckCampus.add(commonCampus);
									}
	
								});
							}
						});
						userRoles.remove(dbo.getErpUsersDBO().getId());
					}
					dto.setMenuId(String.valueOf(dbo.getSysFunctionDBO().getSysMenuDBO().getId()));
					sysFunctionDTO.setGranted(true);
					dto.getPermissions().add(sysFunctionDTO);
					map.put(String.valueOf(dbo.getErpUsersDBO().getId())+dbo.getSysFunctionDBO().getId(), sysFunctionDTO);
					dtos.add(dto);
				} else {
					SysFunctionDTO dto = map.get(String.valueOf(dbo.getErpUsersDBO().getId())+dbo.getSysFunctionDBO().getId());
					ErpCampusDTO campus = new ErpCampusDTO();
					campus.setCampusId(dbo.getErpCampusDBO().getId().toString());
					campus.setCampusName(dbo.getErpCampusDBO().getCampusName());
					campus.setIsGranted(dbo.isAllowed);
					campus.setIsAdditionalPrivilege(dbo.isAllowed);
					campus.setCommonPrivilege(!dbo.isAllowed);
					dto.getCampusList().add(campus);
	
					duplicateCheckCampus.add(dbo.getErpCampusDBO().getId());
				}
			}
		});

		userRoles.forEach((key ,value) -> {
			duplicateCheckCampus.clear();
			ErpUsersDBO user = userInfo.get(key);
			ModuleSubDTO dto = new ModuleSubDTO();
			dto.setUsers(new SelectDTO());
			dto.getUsers().setValue(String.valueOf(user.getId()));
			dto.getUsers().setLabel(user.getLoginId());
			dto.setTotalAdditionalPrivilegeAllowed(Utils.getTotalAdditionalPrivilageAllowed());
			dto.setTotalAdditionalPrivilegeCount(userAdditionalPermissionsMap.containsKey(user.getId()) ? userAdditionalPermissionsMap.get(user.getId()).size(): 0);
			dto.setPermissions(new ArrayList<SysFunctionDTO>());
			SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
			sysFunctionDTO.id = String.valueOf(functionDetials.getSysFunctionDBO().getId());
			sysFunctionDTO.setFunctionName(functionDetials.getSysFunctionDBO().getFunctionName());
			sysFunctionDTO.setFunctionDescription(functionDetials.getSysFunctionDBO().getFunctionDescription());
			sysFunctionDTO.setCampusList(new ArrayList<ErpCampusDTO>());
			if(userRoles.containsKey(user.getId())) {
				Set<Integer> roles = userRoles.get(user.getId());
				roles.forEach(role -> {
					if(roleCampuslistMap.containsKey(String.valueOf(role)+user.getId())) {
						List<Integer> campuslist = roleCampuslistMap.get(String.valueOf(role)+user.getId());
						campuslist.forEach( commonCampus ->{
							if(!duplicateCheckCampus.contains(commonCampus)) {
								ErpCampusDTO campus1 = new ErpCampusDTO();
								campus1.setCampusId(commonCampus.toString());
								//								campus1.setCampusName(dbo.getErpCampusDBO().getCampusName());
								campus1.setIsGranted(true);
								campus1.setIsAdditionalPrivilege(false);
								campus1.setCommonPrivilege(true);
								sysFunctionDTO.getCampusList().add(campus1);
								duplicateCheckCampus.add(commonCampus);
							}

						});
					}
				});
			}
			dto.setMenuId(String.valueOf(functionDetials.getSysFunctionDBO().getSysMenuDBO().getId()));
			sysFunctionDTO.setGranted(true);
			dto.getPermissions().add(sysFunctionDTO);
			dtos.add(dto);
		});
		if(!Utils.isNullOrEmpty(dtos)) {
			dtos.sort(Comparator.comparing(s -> s.getUsers().getLabel()));
		}
		return !Utils.isNullOrEmpty(dtos) ? Mono.just(dtos) : Mono.error(new NotFoundException(null));
	}

	public Flux<ErpUsersDTO> getUserFilter(String empIdOrName, boolean isActive) {
		boolean isNumeric = empIdOrName.chars().allMatch( Character::isDigit );
		return privilegeSettingsTransaction1.getUserFilter(empIdOrName,isActive,isNumeric).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public ErpUsersDTO convertDBOToDTO(Tuple dbo){
		ErpUsersDTO dto = null;
		if(!Utils.isNullOrEmpty(dbo)) {
			dto = new ErpUsersDTO();
			dto.id = dbo.get(0).toString();
			dto.setUserName(dbo.get(1).toString());
			if(!Utils.isNullOrEmpty(dbo.get(2))) {
				dto.setEmpName(dbo.get(2).toString());
			}
		}
		return dto;
	}

	public  Mono<ApiResult> deleteMenuRole(String roleId, List<Integer> functionIds, String userId) {
		List<SysRoleFunctionMapDBO> value = privilegeSettingsTransaction1.getfunctionsForRole(roleId,functionIds);
		return this.convertDBO(value,userId);
	}

	public  Mono<ApiResult> deleteMenuRoleGroup(List<Integer> roleGroupId, List<Integer> functionIds, String userId) {
		List<SysRoleFunctionMapDBO> value = privilegeSettingsTransaction1.getfunctionsForRoleGroup(roleGroupId,functionIds);
		return this.convertDBO(value,userId);
	}

	public Mono<ApiResult> convertDBO(List<SysRoleFunctionMapDBO> value, String userId){
		ApiResult result = new ApiResult();
		List<SysRoleFunctionMapDBO> dbo = new ArrayList<SysRoleFunctionMapDBO>();
		if(!Utils.isNullOrEmpty(value)) {
			value.forEach(data -> {
				if(!Utils.isNullOrEmpty(data)) {
					data.setRecordStatus('D');
					data.setModifiedUsersId(Integer.valueOf(userId));
					dbo.add(data);
				}
			});
			privilegeSettingsTransaction1.saveRoles(dbo);
			result.setSuccess(true);
		}
		return result.isSuccess() ? Mono.just(result) : Mono.error(new NotFoundException(null)); 
	}

	public Mono<List<SysRoleGroupDTO>>  getMenuRoleGroup(String menuId) {
		List<Integer> roleIds = new ArrayList<Integer>();
		List<Integer> groupList = new ArrayList<Integer>();
		Map<Integer, SysFunctionDBO> data = privilegeSettingsTransaction1.getMenuFunctions(menuId).stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
		List<SysRoleGroupDBO> roleGroups = privilegeSettingsTransaction1.getRoleGroup(null);
		roleGroups.forEach( role -> {
			role.getSysRoleDBOSet().forEach( roles -> {
				roleIds.add(roles.getId());
			});
		});
	    Map<Integer, List<SysRoleFunctionMapDBO>> roleMap = privilegeSettingsTransaction1.getRolefunctionList(roleIds,menuId).stream().collect(Collectors.groupingBy(s -> s.getSysRoleDBO().getId()));
		return this.convertRoleGoupDBOToDTO(data,roleGroups,menuId,roleMap);
	}

	public Mono<List<SysRoleGroupDTO>> convertRoleGoupDBOToDTO(Map<Integer, SysFunctionDBO> data, List<SysRoleGroupDBO> roleGroups, String menuId, Map<Integer, List<SysRoleFunctionMapDBO>> roleMap){
		Map<Integer,SysRoleGroupDTO> map = new HashMap<Integer, SysRoleGroupDTO>();
		List<SysRoleGroupDTO> dtos = new ArrayList<SysRoleGroupDTO>();
		List<Integer> duplicateIds = new ArrayList<Integer>();	
		roleGroups.forEach( group -> {
			group.getSysRoleDBOSet().forEach(role -> {
//				List<SysRoleFunctionMapDBO> value = privilegeSettingsTransaction1.getRolefunctions(String.valueOf(role.getId()),menuId);
				List<SysRoleFunctionMapDBO> value = !Utils.isNullOrEmpty(roleMap.get(role.getId())) ? roleMap.get(role.getId()) : new ArrayList<SysRoleFunctionMapDBO>() ;
				if(data.size() == value.size()) {
					if(!map.containsKey(role.getSysRoleGroup().getId())) {
						SysRoleGroupDTO roleDto = new SysRoleGroupDTO(); 
						roleDto.setRoleGroupName(new SelectDTO());
						roleDto.getRoleGroupName().setValue(String.valueOf(role.getSysRoleGroup().getId()));
						roleDto.getRoleGroupName().setLabel(role.getSysRoleGroup().getRoleGroupName());
						roleDto.setRoles(new ArrayList<ModuleSubDTO>());
						ModuleSubDTO dto = new ModuleSubDTO();
						dto.setRoles(new SelectDTO());
						dto.getRoles().setValue(String.valueOf(role.getId()));
						dto.getRoles().setLabel(role.getRoleName());
						dto.setPermissions(new ArrayList<SysFunctionDTO>()); 
						value.forEach( function -> {
							SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
							sysFunctionDTO.id = String.valueOf(function.getId());
							sysFunctionDTO.setFunctionName(function.getSysFunctionDBO().getFunctionName());
							sysFunctionDTO.setFunctionDescription(function.getSysFunctionDBO().getFunctionDescription());
							if(function.getRecordStatus() == 'A') {
								sysFunctionDTO.setGranted(true);
							}	
							dto.getPermissions().add(sysFunctionDTO);
						});
						roleDto.getRoles().add(dto);
						map.put(role.getSysRoleGroup().getId(), roleDto);
					} else {
						SysRoleGroupDTO roleDto = map.get(role.getSysRoleGroup().getId());
						ModuleSubDTO dto = new ModuleSubDTO();
						dto.setRoles(new SelectDTO());
						dto.getRoles().setValue(String.valueOf(role.getId()));
						dto.getRoles().setLabel(role.getRoleName());
						dto.setPermissions(new ArrayList<SysFunctionDTO>());
						value.forEach( function -> {
							SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
							sysFunctionDTO.id = String.valueOf(function.getId());
							sysFunctionDTO.setFunctionName(function.getSysFunctionDBO().getFunctionName());
							sysFunctionDTO.setFunctionDescription(function.getSysFunctionDBO().getFunctionDescription());
							if(function.getRecordStatus() == 'A') {
								sysFunctionDTO.setGranted(true);
							}	
							dto.getPermissions().add(sysFunctionDTO);
						});
						roleDto.getRoles().add(dto);
						map.replace(role.getSysRoleGroup().getId(), roleDto);
					}
				} 
				else {
					duplicateIds.add(group.getId());
				}
			});
		});
		map.forEach((key,value) -> {
			if(!duplicateIds.contains(key)) {
				dtos.add(value);
			}
		});			
		if(!Utils.isNullOrEmpty(dtos)) {
			dtos.sort(Comparator.comparing(s -> s.getRoleGroupName().getLabel()));
		}
		return Mono.just(dtos);
	}


	public Mono<ApiResult> saveMenuRoleGroup(List<Integer> roleGroupId, Mono<List<Integer>> dto, String userId) {
		return dto.map(data -> saveMenuRoleGroupDtoToDbo(roleGroupId,data,userId))
				.flatMap( s -> { privilegeSettingsTransaction1.saveRoles(s);
				return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<SysRoleFunctionMapDBO> saveMenuRoleGroupDtoToDbo(List<Integer> roleGroupId, List<Integer> data, String userId) {
		List<SysRoleFunctionMapDBO> value = new ArrayList<SysRoleFunctionMapDBO>();
		Map<String, SysRoleFunctionMapDBO> map;
		map = privilegeSettingsTransaction1.getfunctionsForRoleGroup(null, data).stream().filter(s -> s.getRecordStatus() == 'A')
				.collect(Collectors.toMap(s -> String.valueOf(s.getSysRoleDBO().getId())+s.getSysFunctionDBO().getId(), s -> s));
		List<SysRoleDBO> roleGroups = privilegeSettingsTransaction1.getRolesForGroups(roleGroupId);
		roleGroups.forEach(role -> {
			data.forEach(functionId -> {
				if(!map.containsKey(String.valueOf(role.getId())+functionId)) {
					SysRoleFunctionMapDBO sysRoleFunctionMapDBO = new SysRoleFunctionMapDBO();
					sysRoleFunctionMapDBO.setSysRoleDBO(new SysRoleDBO());
					sysRoleFunctionMapDBO.getSysRoleDBO().setId(role.getId());
					sysRoleFunctionMapDBO.setSysFunctionDBO(new SysFunctionDBO());
					sysRoleFunctionMapDBO.getSysFunctionDBO().setId(functionId);
					sysRoleFunctionMapDBO.setAuthorised(true);
					sysRoleFunctionMapDBO.setRecordStatus('A');
					sysRoleFunctionMapDBO.setCreatedUsersId(Integer.parseInt(userId));
					value.add(sysRoleFunctionMapDBO);
				} else {
					map.remove(String.valueOf(role.getId())+functionId);
				}
			});
		});
		map.forEach((key,values) -> {
			values.setRecordStatus('D');
			values.setModifiedUsersId(Integer.parseInt(userId));
			value.add(values);
		});
		return value;
	}

	public Mono<List<SysRoleGroupDTO>>  getRoleGroupForFunction(String functionId) {
		List<SysRoleGroupDBO> roleData = privilegeSettingsTransaction1.getRoleGroupForFunction(functionId);
		Map<Integer, Integer> roleGroups = privilegeSettingsTransaction1.getRoleGroup(null).stream().collect(Collectors.toMap(s -> s.getId(), s -> s.getSysRoleDBOSet().size()));
		return convertRoleGroupForFunctionDBOToDTO(roleData,roleGroups);
	}

	public Mono<List<SysRoleGroupDTO>> convertRoleGroupForFunctionDBOToDTO(List<SysRoleGroupDBO> roleData, Map<Integer, Integer> roleGroups){
		List<SysRoleGroupDTO> dtos = new ArrayList<SysRoleGroupDTO>();
		Map<Integer,SysRoleGroupDTO> map = new HashMap<Integer, SysRoleGroupDTO>();
		if(!Utils.isNullOrEmpty(roleData)) {
			roleData.forEach( group -> {
				if(roleGroups.containsKey(group.getId())) {
					Integer groupSize = roleGroups.get(group.getId());
					if(group.getSysRoleDBOSet().size() == groupSize) {
						if(!map.containsKey(group.getId())) {
							SysRoleGroupDTO roleDto = new SysRoleGroupDTO();
							roleDto.setRoleGroupName(new SelectDTO());
							roleDto.getRoleGroupName().setValue(String.valueOf(group.getId()));
							roleDto.getRoleGroupName().setLabel(group.getRoleGroupName());
							roleDto.setRoles(new ArrayList<ModuleSubDTO>());
							group.getSysRoleDBOSet().forEach(role -> {
								ModuleSubDTO dto = new ModuleSubDTO();
								dto.setRoles(new SelectDTO());
								dto.getRoles().setValue(String.valueOf(role.getId()));
								dto.getRoles().setLabel(role.getRoleName());
								dto.setPermissions(new ArrayList<SysFunctionDTO>());
								role.getSysRoleFunctionMapDBOs().forEach( function -> {
									SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
									sysFunctionDTO.id = String.valueOf(function.getId());
									sysFunctionDTO.setFunctionName(function.getSysFunctionDBO().getFunctionName());
									sysFunctionDTO.setFunctionDescription(function.getSysFunctionDBO().getFunctionDescription());
									if(function.getRecordStatus() == 'A') {
										sysFunctionDTO.setGranted(true);
									}	
									dto.getPermissions().add(sysFunctionDTO);
								});
								roleDto.getRoles().add(dto);
							});
							map.put(group.getId(), roleDto);
						} else {
							SysRoleGroupDTO roleDto = map.get(group.getId());
							group.getSysRoleDBOSet().forEach(role -> {
								ModuleSubDTO dto = new ModuleSubDTO();
								dto.setRoles(new SelectDTO());
								dto.getRoles().setValue(String.valueOf(role.getId()));
								dto.getRoles().setLabel(role.getRoleName());
								dto.setPermissions(new ArrayList<SysFunctionDTO>());
								role.getSysRoleFunctionMapDBOs().forEach( function -> {
									SysFunctionDTO sysFunctionDTO = new SysFunctionDTO();
									sysFunctionDTO.id = String.valueOf(function.getId());
									sysFunctionDTO.setFunctionName(function.getSysFunctionDBO().getFunctionName());
									sysFunctionDTO.setFunctionDescription(function.getSysFunctionDBO().getFunctionDescription());
									if(function.getRecordStatus() == 'A') {
										sysFunctionDTO.setGranted(true);
									}	
									dto.getPermissions().add(sysFunctionDTO);
								});
								roleDto.getRoles().add(dto);
							});
							map.replace(group.getId(), roleDto);
						}
					}
				}
			});
		}
		map.forEach((key,value) -> {
			dtos.add(value);
		});
		if(!Utils.isNullOrEmpty(dtos)) {
			dtos.sort(Comparator.comparing(s -> s.getRoleGroupName().getLabel()));
		}
		return Mono.just(dtos);
	}

//	public  Mono<ApiResult> deleteMenuUser(String menuUserId, List<Integer> functionIds, String userId) {
//		List<SysUserFunctionOverrideDBO> value = privilegeSettingsTransaction1.getfunctionsForUser(menuUserId,functionIds);
//		return this.convertDBO1(value,userId);
//	}
//
//	public Mono<ApiResult> convertDBO1(List<SysUserFunctionOverrideDBO> value, String userId){
//		ApiResult result = new ApiResult();
//		List<SysUserFunctionOverrideDBO> dbo = new ArrayList<SysUserFunctionOverrideDBO>();
//		if(!Utils.isNullOrEmpty(value)) {
//			value.forEach(data -> {
//				if(!Utils.isNullOrEmpty(data)) {
//					data.setRecordStatus('D');
//					data.setModifiedUsersId(Integer.valueOf(userId));
//					dbo.add(data);
//				}
//			});
//			privilegeSettingsTransaction1.saveUsers(dbo);
//			result.setSuccess(true);
//		}
//		return result.isSuccess() ? Mono.just(result) : Mono.error(new NotFoundException(null)); 
//	}

	public Flux<ModuleDTO> getFillterMenuRoles(List<Integer> menuId) {
		return privilegeSettingsTransaction1.geFilltertMenuRoles(menuId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public Mono<List<RoleOrUserPermissionDTO>> getUsersAdditionalPermissions(List<Integer> usersIds) {
		Map<Integer,List<Integer>> userFunctions = new HashMap<Integer, List<Integer>>();
//		Map<Integer,List<SysUserFunctionOverrideDBO>> userFunctions = new HashMap<Integer, List<Integer>>();
		List<SysUserFunctionOverrideDBO> uesrsDatas = privilegeSettingsTransaction1.getUsersAdditionalPermissions(usersIds);
		uesrsDatas.forEach( user -> {
			if(!userFunctions.containsKey(user.getErpUsersDBO().getId())) {
				List<Integer> list = new ArrayList<Integer>();
				list.add(user.getSysFunctionDBO().getId());
				userFunctions.put(user.getErpUsersDBO().getId(),list);
			} else {
				List<Integer> list = userFunctions.get(user.getErpUsersDBO().getId());
				list.add(user.getSysFunctionDBO().getId());
				userFunctions.replace(user.getErpUsersDBO().getId(), list);
			}
		});
		Set<Integer> userIdsSet =  new HashSet<Integer>(usersIds);
		return this.convertDBOToUsersDTO(uesrsDatas,userFunctions,userIdsSet);
	}

	public Mono<List<RoleOrUserPermissionDTO>> convertDBOToUsersDTO(List<SysUserFunctionOverrideDBO> uesrsDatas, Map<Integer, List<Integer>> userFunctions, Set<Integer> userIdsSet){
		List<RoleOrUserPermissionDTO> values = new ArrayList<RoleOrUserPermissionDTO>() ;
		Map<Integer,RoleOrUserPermissionDTO> map = new HashMap<Integer, RoleOrUserPermissionDTO>();
		Map<String,ModuleDTO> moduleMap = new HashMap<String, ModuleDTO>();
		Map<String,ModuleSubDTO> submoduleMap = new HashMap<String, ModuleSubDTO>();
		Map<String,SysFunctionDTO> sysFunctionMap = new HashMap<String, SysFunctionDTO>();
		List<String> duplicateCheck = new ArrayList<String>();
		uesrsDatas.forEach( user -> {
			if(!Utils.isNullOrEmpty(userIdsSet) && userIdsSet.contains(user.getErpUsersDBO().getId())) {
				userIdsSet.remove(user.getErpUsersDBO().getId());
			}
			if(!map.containsKey(user.getErpUsersDBO().getId())) {
				RoleOrUserPermissionDTO dto = new RoleOrUserPermissionDTO();
				dto.setUserName(new ErpUsersDTO());
				dto.getUserName().id = String.valueOf(user.getErpUsersDBO().getId());
				dto.getUserName().setEmpName(user.getErpUsersDBO().getUserName());
				dto.getUserName().setTotalAdditionalPrivilegeAllowed( Utils.getTotalAdditionalPrivilageAllowed());
				dto.getUserName().setTotalAdditionalPrivilegeCount(0);
				dto.setScreens(new ArrayList<ModuleDTO>());

				if(!moduleMap.containsKey(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId())) {
					ModuleDTO moduleDto = new ModuleDTO();
					moduleDto.setId(String.valueOf(user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId()));
					moduleDto.setText(user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName());
					
					moduleDto.setMenus(new ArrayList<ModuleSubDTO>());

					if(!submoduleMap.containsKey(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId())) {
						ModuleSubDTO subModuleDto = new ModuleSubDTO();
						subModuleDto.setScreenName(new MenuScreenDTO());
						subModuleDto.getScreenName().setMenuId(user.getSysFunctionDBO().getSysMenuDBO().getId());
						subModuleDto.getScreenName().setMenuName(user.getSysFunctionDBO().getSysMenuDBO().getMenuScreenName());
						subModuleDto.setPermissions(new ArrayList<SysFunctionDTO>());
						if(!duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						}
						submoduleMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
						moduleDto.getMenus().add(subModuleDto);
						dto.getScreens().add(moduleDto);
					} else {
						ModuleSubDTO subModuleDto = submoduleMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId());
						if(!duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						} else {
							SysFunctionDTO functionDto = sysFunctionMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
						}
						submoduleMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
					}
					moduleMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId(), moduleDto);
				} else {
					ModuleDTO moduleDto = moduleMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId());
					if(!submoduleMap.containsKey(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId())) {
						ModuleSubDTO subModuleDto = new ModuleSubDTO();
						subModuleDto.setScreenName(new MenuScreenDTO());
						subModuleDto.getScreenName().setMenuId(user.getSysFunctionDBO().getSysMenuDBO().getId());
						subModuleDto.getScreenName().setMenuName(user.getSysFunctionDBO().getSysMenuDBO().getMenuScreenName());
						subModuleDto.setPermissions(new ArrayList<SysFunctionDTO>());
						if(duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						}  else {
							SysFunctionDTO functionDto = sysFunctionMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
						}
						submoduleMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
						moduleDto.getMenus().add(subModuleDto);
						map.put(user.getErpUsersDBO().getId(), dto);

					} else {
						ModuleSubDTO subModuleDto = submoduleMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId());
						if(duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						} else {
							SysFunctionDTO functionDto = sysFunctionMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
						}

						submoduleMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
					}
					moduleMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId(), moduleDto);
				}
				map.put(user.getErpUsersDBO().getId(), dto);
			} else {
				RoleOrUserPermissionDTO dto = map.get(user.getErpUsersDBO().getId());
				if(!moduleMap.containsKey(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId())) {
					ModuleDTO moduleDto = new ModuleDTO();
					moduleDto.setId(String.valueOf(user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId()));
					moduleDto.setText(user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName());
					moduleDto.setMenus(new ArrayList<ModuleSubDTO>());

					if(!submoduleMap.containsKey(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId())) {
						ModuleSubDTO subModuleDto = new ModuleSubDTO();
						subModuleDto.setScreenName(new MenuScreenDTO());
						subModuleDto.getScreenName().setMenuId(user.getSysFunctionDBO().getSysMenuDBO().getId());
						subModuleDto.getScreenName().setMenuName(user.getSysFunctionDBO().getSysMenuDBO().getMenuScreenName());
						subModuleDto.setPermissions(new ArrayList<SysFunctionDTO>());

						if(!duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						} else {
							SysFunctionDTO functionDto = sysFunctionMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
						}
						submoduleMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
						moduleDto.getMenus().add(subModuleDto);
						dto.getScreens().add(moduleDto);
						map.put(user.getErpUsersDBO().getId(), dto);
					} else {
						ModuleSubDTO subModuleDto = submoduleMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId());
						if(!duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						} else {
							SysFunctionDTO functionDto = sysFunctionMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
						}
						submoduleMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
					}
					moduleMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId(), moduleDto);
				} else {
					ModuleDTO moduleDto = moduleMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId());
					if(!submoduleMap.containsKey(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId())) {
						ModuleSubDTO subModuleDto = new ModuleSubDTO();
						subModuleDto.setScreenName(new MenuScreenDTO());
						subModuleDto.getScreenName().setMenuId(user.getSysFunctionDBO().getSysMenuDBO().getId());
						subModuleDto.getScreenName().setMenuName(user.getSysFunctionDBO().getSysMenuDBO().getMenuScreenName());
						subModuleDto.setPermissions(new ArrayList<SysFunctionDTO>());
						if(!duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						}  else {
							SysFunctionDTO functionDto = sysFunctionMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
						}
						submoduleMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
						moduleDto.getMenus().add(subModuleDto);
						map.put(user.getErpUsersDBO().getId(), dto);
					} else {
						ModuleSubDTO subModuleDto = submoduleMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId());
						if(!duplicateCheck.contains(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId())) {
							SysFunctionDTO functionDto = new SysFunctionDTO();
							functionDto.setOverrideId(String.valueOf(user.getSysFunctionDBO().getId()));
							functionDto.setFunctionName(user.getSysFunctionDBO().getFunctionName());
							functionDto.setCampusList(new ArrayList<ErpCampusDTO>());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.put(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
							subModuleDto.getPermissions().add(functionDto);
							duplicateCheck.add(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							dto.getUserName().setTotalAdditionalPrivilegeCount(dto.getUserName().getTotalAdditionalPrivilegeCount()+1);
						} else {
							SysFunctionDTO functionDto = sysFunctionMap.get(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId());
							ErpCampusDTO campusDto = new ErpCampusDTO();
							campusDto.setId(user.getErpCampusDBO().getId().toString());
							campusDto.setCampusName(user.getErpCampusDBO().getCampusName());
							campusDto.setIsGranted(true);
							campusDto.setIsAdditionalPrivilege(true);
							functionDto.getCampusList().add(campusDto);
							sysFunctionMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getId(), functionDto);
						}
						submoduleMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getId(), subModuleDto);
					}
					moduleMap.replace(String.valueOf(user.getErpUsersDBO().getId())+user.getSysFunctionDBO().getSysMenuDBO().getSysMenuModuleSubDBO().getSysMenuModuleDBO().getId(), moduleDto);
				}
				map.replace(user.getErpUsersDBO().getId(), dto);
			}
		});
		map.forEach((key,value) -> {
			values.add(value);
		});
		if(!Utils.isNullOrEmpty(userIdsSet)) {
			userIdsSet.forEach(userId -> {
				RoleOrUserPermissionDTO dto = new RoleOrUserPermissionDTO();
				dto.setUserName(new ErpUsersDTO());
				dto.getUserName().id = String.valueOf(userId);
//				dto.getUserName().setEmpName(user.getErpUsersDBO().getUserName());
				dto.getUserName().setTotalAdditionalPrivilegeAllowed( Utils.getTotalAdditionalPrivilageAllowed());
				dto.getUserName().setTotalAdditionalPrivilegeCount(0);
				values.add(dto);
			});

		}
		return Mono.just(values);
	}

	public Mono<ApiResult> saveOrUpdateRolePermissions(Mono<RoleOrUserPermissionDTO> dto, String userId) {
		return  dto.map(data -> saveOrUpdateRolePermissionsDtoToDbo(data,userId))
					.flatMap( s -> { privilegeSettingsTransaction1.saveRoles(s);
					return Mono.just(Boolean.TRUE);
					}).map(Utils::responseResult);
	}
	
	public List<SysRoleFunctionMapDBO> saveOrUpdateRolePermissionsDtoToDbo(RoleOrUserPermissionDTO dto1, String userId) {
		Map<Integer, SysRoleFunctionMapDBO> existRolePermissionsMap = privilegeSettingsTransaction1.getFunctionsForRole(dto1.getRoleName().id).stream().filter(s -> s.recordStatus == 'A')
									.collect(Collectors.toMap(s -> s.getSysFunctionDBO().getId(), s -> s));
		List<SysRoleFunctionMapDBO> dbos = new ArrayList<SysRoleFunctionMapDBO>();
		if(!Utils.isNullOrEmpty(dto1)) {
			dto1.getFunctionsList().forEach(function -> {
				SysRoleFunctionMapDBO dbo = null;
				if(function.isGranted()) {
					if(existRolePermissionsMap.containsKey(Integer.parseInt(function.id))) {
						dbo = existRolePermissionsMap.get(Integer.parseInt(function.id));
						dbo.setModifiedUsersId(Integer.parseInt(userId));
						existRolePermissionsMap.remove(Integer.parseInt(function.id));
					} else {
						dbo = new SysRoleFunctionMapDBO();
						dbo.setCreatedUsersId(Integer.parseInt(userId));
					}
					dbo.setSysRoleDBO(new SysRoleDBO());
					dbo.getSysRoleDBO().setId(Integer.parseInt(dto1.getRoleName().id));
					dbo.setSysFunctionDBO(new SysFunctionDBO());
					dbo.getSysFunctionDBO().setId(Integer.parseInt(function.id));
					dbo.setAuthorised(function.getAuthorised());
					dbo.setRecordStatus('A');
					dbos.add(dbo);
				} else {
					if(existRolePermissionsMap.containsKey(Integer.parseInt(function.id))) {
						dbo = existRolePermissionsMap.get(Integer.parseInt(function.id));
						dbo.setRecordStatus('D');
						dbo.setModifiedUsersId(Integer.parseInt(userId));
						existRolePermissionsMap.remove(Integer.parseInt(function.id));
						dbos.add(dbo);
					}
				}
			});
		}	
		return dbos;
	}
	
}
