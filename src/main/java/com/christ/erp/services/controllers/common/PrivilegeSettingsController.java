package com.christ.erp.services.controllers.common;

import java.util.List;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.ErpUsersDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.ModuleDTO;
import com.christ.erp.services.dto.common.ModuleSubDTO;
import com.christ.erp.services.dto.common.RoleOrUserPermissionDTO;
import com.christ.erp.services.dto.common.SysRoleDTO;
import com.christ.erp.services.dto.common.SysRoleGroupDTO;
import com.christ.erp.services.dto.common.SysUserRoleMapDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.PrivilegeSettingsHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/Secured/Common/PrivilegeSettings")
@SuppressWarnings("rawtypes")
public class PrivilegeSettingsController extends BaseApiController {

	PrivilegeSettingsHandler privilegeSettingsHandler = PrivilegeSettingsHandler.getInstance();

	@Autowired
	private PrivilegeSettingsHandler privilegeSettingsHandler1;

	@RequestMapping(value = "/editRole", method = RequestMethod.POST)
	public Mono<ApiResult<SysRoleDTO>> editRole(@RequestParam("id") String roleId) {
		ApiResult<SysRoleDTO> result = new ApiResult<SysRoleDTO>();
		try { 
			result.dto = privilegeSettingsHandler.editRole(roleId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} 
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result); 
	}

	@RequestMapping(value = "/saveOrUpdateRole", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateRole(@RequestBody SysRoleDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			privilegeSettingsHandler.saveOrUpdateRole(data, userId, result);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	//	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	//	public Mono<ApiResult<ModelBaseDTO>> deleteRole(@RequestParam("id") String roleId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	//		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
	//		try {
	//			result.success = privilegeSettingsHandler.deleteRole(roleId,userId);
	//		} catch (Exception error) {
	//			result.success = false;
	//			result.dto = null;
	//			result.failureMessage = error.getMessage();
	//		}
	//		return Utils.monoFromObject(result);
	//	}

	@RequestMapping(value = "/editRoleAssignedToUsers", method = RequestMethod.POST)
	public Mono<ApiResult<List<SysUserRoleMapDTO>>> editRoleAssignedToUsers(@RequestParam("id") String roleId) {
		ApiResult<List<SysUserRoleMapDTO>> result = new ApiResult<List<SysUserRoleMapDTO>>();
		try {
			result = privilegeSettingsHandler.editRoleAssignedToUsers(roleId, result);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result); 
	}

	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	public Mono<ApiResult<ErpUsersDTO>> editUser(@RequestParam("id") String userId) {
		ApiResult<ErpUsersDTO> result = new ApiResult<ErpUsersDTO>();
		try { 
			result.dto = privilegeSettingsHandler1.editUser(userId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result); 
	}

	@RequestMapping(value = "/saveOrUpdateUser", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateUser(@RequestBody ErpUsersDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			privilegeSettingsHandler1.saveOrUpdateUser(data, userId, result);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}


	//	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	//	public Mono<ApiResult<ModelBaseDTO>> deleteUser(@RequestParam("id") String idUser,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	//		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>(); 
	//		try {
	//			result.success = privilegeSettingsHandler.deleteUser(idUser,userId);
	//		} catch (Exception error) {
	//			result.success = false;
	//			result.dto = null;
	//			result.failureMessage = error.getMessage();
	//		}
	//		return Utils.monoFromObject(result);
	//	}


	@RequestMapping(value = "/editPermissionsToRole", method = RequestMethod.POST)
	public Mono<ApiResult<RoleOrUserPermissionDTO>> editPermissionsToRole(@RequestParam("id") String roleId) {
		ApiResult<RoleOrUserPermissionDTO> result = new ApiResult<RoleOrUserPermissionDTO>();
		try { 
			result.dto = privilegeSettingsHandler.editPermissionsToRole(roleId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} 
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result); 
	}

//	@RequestMapping(value = "/saveOrUpdatePermissionsToRole", method = RequestMethod.POST)
//	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdatePermissionsToRole(@RequestBody RoleOrUserPermissionDTO data, @RequestParam("id") String roleId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
//		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
//		try {
//			if(privilegeSettingsHandler.saveOrUpdatePermissionsToRole(data, roleId, userId)) {
//				result.success = true;
//			}else {
//				result.success = false;
//			}
//		} catch (Exception error) {
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = error.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}
	
	@PostMapping(value = "/saveOrUpdateRolePermissions")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdatePermissionsToRole( @RequestBody Mono<RoleOrUserPermissionDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return privilegeSettingsHandler1.saveOrUpdateRolePermissions(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@RequestMapping(value = "/editPermissionsToUser", method = RequestMethod.POST)
	public Mono<ApiResult<RoleOrUserPermissionDTO>> editPermissionsToUser(@RequestParam("id") String userId) {
		ApiResult<RoleOrUserPermissionDTO> result = new ApiResult<RoleOrUserPermissionDTO>();
		try { 
			result.dto = privilegeSettingsHandler.editPermissionsToUser(userId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} 
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result); 
	}

	@RequestMapping(value = "/saveOrUpdatePermissionsToUser", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdatePermissionsToUser(@RequestParam("id") String idUser, @RequestBody ModuleSubDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			if(privilegeSettingsHandler1.saveOrUpdatePermissionsToUser(data, idUser, userId)) {
				result.success = true;
			}else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@PostMapping(value = "/deleteRole")
	public Mono<ResponseEntity<ApiResult>> deleteRole(@RequestParam int roleId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return privilegeSettingsHandler1.deleteRole(roleId, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/deleteUser")
	public Mono<ResponseEntity<ApiResult>> deleteUser(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return privilegeSettingsHandler1.deleteUser(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getMenus") 
	public Flux<ModuleDTO> getMenus() {
		return privilegeSettingsHandler1.getMenus().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getMenuRoles") 
	public Mono<List<ModuleSubDTO>> getMenuRoles(@RequestParam String menuId) {
		return privilegeSettingsHandler1.getMenuRoles(menuId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getMenuUsers") 
	public Mono<List<ModuleSubDTO>> getMenuUsers(@RequestParam String menuId) {
		return privilegeSettingsHandler1.getMenuUsers(menuId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/saveOrUpdateFunctionsToRoles")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateFunctionsToRoles( @RequestBody Mono<List<ModuleSubDTO>> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return privilegeSettingsHandler1.saveOrUpdateFunctionsToRoles(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/saveOrUpdateFunctionsToUsers")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateFunctionsToUsers( @RequestBody Mono<List<ModuleSubDTO>> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return privilegeSettingsHandler1.saveOrUpdateFunctionsToUsers(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getRolesOrUsersForFunction") 
	public Mono<List<ModuleSubDTO>> getRolesOrUsersForFunction(@RequestParam String functionId,@RequestParam Boolean role) {
		return privilegeSettingsHandler1.getRolesOrUsersForFunction(functionId,role).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getUserFilter") 
	public Flux<ErpUsersDTO> getUserFilter(@RequestParam String empIdOrName, @RequestParam("isActive") boolean isActive) {
		return privilegeSettingsHandler1.getUserFilter(empIdOrName,isActive).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/deleteMenuRole")
	public Mono<ResponseEntity<ApiResult>> deleteMenuRole(@RequestParam String roleId,@RequestParam List<Integer> functionIds, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return privilegeSettingsHandler1.deleteMenuRole(roleId, functionIds,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	//	@PostMapping(value = "/deleteMenuUser")
	//    public Mono<ResponseEntity<ApiResult>> deleteMenuUser(@RequestParam String menuUserId,@RequestParam List<Integer> functionIds, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	//        return privilegeSettingsHandler1.deleteMenuUser(menuUserId, functionIds,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	//    }

	@PostMapping(value = "/getMenuRoleGroup") 
	public Mono<List<SysRoleGroupDTO>> getMenuRoleGroup(@RequestParam String menuId) {
		return privilegeSettingsHandler1.getMenuRoleGroup(menuId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/deleteMenuRoleGroup")
	public Mono<ResponseEntity<ApiResult>> deleteMenuRoleGroup(@RequestParam List<Integer> roleGroupId,@RequestParam List<Integer> functionIds, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return privilegeSettingsHandler1.deleteMenuRoleGroup(roleGroupId, functionIds,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/saveMenuRoleGroup")
	public Mono<ResponseEntity<ApiResult>> saveMenuRoleGroup(@RequestParam List<Integer> roleGroupId, @RequestBody Mono<List<Integer>> functionIds,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return privilegeSettingsHandler1.saveMenuRoleGroup(roleGroupId,functionIds,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getRoleGroupForFunction") 
	public Mono<List<SysRoleGroupDTO>> getRoleGroupForFunction(@RequestParam String functionId) {
		return privilegeSettingsHandler1.getRoleGroupForFunction(functionId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getFillterMenuRoles") 
	public Flux<ModuleDTO> getFillterMenuRoles(@RequestParam List<Integer> menuId) {
		return privilegeSettingsHandler1.getFillterMenuRoles(menuId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getUsersAdditionalPermissions") 
	public Mono<List<RoleOrUserPermissionDTO>> getUsersAdditionalPermissions(@RequestParam List<Integer> usersIds) {
		return privilegeSettingsHandler1.getUsersAdditionalPermissions(usersIds).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
