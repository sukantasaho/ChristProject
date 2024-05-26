package com.christ.erp.services.handlers.support.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpUserGroupDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryCampusDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryCampusDetailsDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryCampusDetailsEmployeeDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryGroupDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryUserGroupDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportRoleDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.support.common.SupportRoleDTO;
import com.christ.erp.services.dto.support.settings.SupportCategoryCampusDTO;
import com.christ.erp.services.dto.support.settings.SupportCategoryCampusDetailsDTO;
import com.christ.erp.services.dto.support.settings.SupportCategoryDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.handlers.support.common.CommonSupportHandler;
import com.christ.erp.services.transactions.support.settings.SupportCategoryTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SupportCategoryHandler {
	
	@Autowired
	SupportCategoryTransaction supportCategoryTransaction;
	
	@Autowired
	CommonSupportHandler commonSupportHandler;

	public Mono<ApiResult> saveOrUpdateSupportCategory(Mono<SupportCategoryDTO> dto, String userId){
		return dto
			.handle((supportCategoryDTO, synchronousink)->{
			 boolean isTrue = supportCategoryTransaction.duplicateCheck(supportCategoryDTO);
				if(isTrue) {
					synchronousink.error(new DuplicateException("Category Name " + supportCategoryDTO.getSupportCategoryName() + " already exits "));
				}
				else {
					synchronousink.next(supportCategoryDTO);
				}
			}).cast(SupportCategoryDTO.class).map(data->convertSupportCategoryDtoToDbo(data, userId))
				.flatMap(supportCategoryDbo->{
					if(!Utils.isNullOrEmpty(supportCategoryDbo.getId())) {
						supportCategoryTransaction.updateSupportCategory(supportCategoryDbo);
					}
					else {
						supportCategoryTransaction.saveSupportCategory(supportCategoryDbo);	
					}
					return Mono.just(Boolean.TRUE);
			 }).map(Utils::responseResult);
	}
	
	public Flux<SupportCategoryDTO> getGridData() {
		return supportCategoryTransaction.getAllCategories().flatMapMany(Flux::fromIterable).map(this::convertGridDataCategoryDboToDto);
	}
	
	public SupportCategoryDTO convertGridDataCategoryDboToDto(SupportCategoryDBO supportCategoryDBO) {
		SupportCategoryDTO supportCategoryDTO = new SupportCategoryDTO();
		
		BeanUtils.copyProperties(supportCategoryDBO, supportCategoryDTO);
		
		supportCategoryDTO.setId(supportCategoryDBO.getId());
		supportCategoryDTO.setSupportCategoryName(supportCategoryDBO.getSupportCategoryName());
		
		//support category group assignment
		SelectDTO supportCategoryGroupDTO = new SelectDTO();
		supportCategoryGroupDTO.setValue(Integer.toString(supportCategoryDBO.getSupportCategoryGroupDBO().getId()));
		supportCategoryGroupDTO.setLabel(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportCategoryGroup());
		supportCategoryDTO.setSupportCategoryGroupDTO(supportCategoryGroupDTO);
		
		//support category area assignment
		if(!Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportAreaDBO())) {
			SelectDTO supportAreaTO = new SelectDTO();
			supportAreaTO.setValue(Integer.toString(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportAreaDBO().getId()));
			supportAreaTO.setLabel(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportAreaDBO().getSupportArea());
			supportCategoryDTO.setSupportAreaTo(supportAreaTO);
		}
		
		//Department assignment to SelectTo
		SelectDTO erpDepartmentDTO = new SelectDTO();
		erpDepartmentDTO.setValue(Integer.toString(supportCategoryDBO.getErpDepartmentDBO().getId()));
		erpDepartmentDTO.setLabel(supportCategoryDBO.getErpDepartmentDBO().getDepartmentName());
		supportCategoryDTO.setErpDepartmentDTO(erpDepartmentDTO);
		List<String> campusStringList = new ArrayList<String>();
		
		//support category campus assignment
		if(!Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryCampusDBOSet())) {
			supportCategoryDBO.getSupportCategoryCampusDBOSet().forEach(supCatCampus->{
				campusStringList.add(supCatCampus.getErpCampusDBO().getCampusName());
			});
			String campusCommaSeparated = campusStringList.stream().collect(Collectors.joining(", "));
			supportCategoryDTO.setSupportCampuses(campusCommaSeparated);
		}
		
		//support category user group collection assignment
		List<String> userGroupList = new ArrayList<String>();
		if(!Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryUserGroupDBOSet())) {
			supportCategoryDBO.getSupportCategoryUserGroupDBOSet().forEach(supCatUserGrp->{
				userGroupList.add(supCatUserGrp.getErpUserGroupDBO().getUserGroupName());
			});
			String userGrpCommaSeparated = userGroupList.stream().collect(Collectors.joining(", "));
			supportCategoryDTO.setUserGroupNames(userGrpCommaSeparated);
		}
		return supportCategoryDTO;
	}
	
	public Mono<SupportCategoryDTO> editSupportCategory(int id) {
		SupportCategoryDBO supportCategoryDBO = supportCategoryTransaction.getSupportCategoryDataById(id);
		SupportCategoryDTO supportCategoryDTO = null;
		if(!Utils.isNullOrEmpty(supportCategoryDBO)) {
			supportCategoryDTO = convertCategoryDboToDtoEdit(supportCategoryDBO);
		}
        return Mono.just(supportCategoryDTO);
    }
	
	public SupportCategoryDTO convertCategoryDboToDtoEdit(SupportCategoryDBO supportCategoryDBO) {
		SupportCategoryDTO supportCategoryDTO = new SupportCategoryDTO();
		
		BeanUtils.copyProperties(supportCategoryDBO, supportCategoryDTO);
		List<SupportRoleDBO> supportRoleList = supportCategoryTransaction.getSupportRole();
		Map<Integer, SupportRoleDBO> supportRoleMasterMap = !Utils.isNullOrEmpty(supportRoleList) ? supportRoleList.stream().collect(Collectors.toMap(role->role.getId(), role->role)):new HashMap<Integer, SupportRoleDBO>();
		
		List<ErpUserGroupDBO> erpUserGroupList = supportCategoryTransaction.getErpUserGroup();
		Map<Integer, ErpUserGroupDBO> erpUserGroupMap = !Utils.isNullOrEmpty(erpUserGroupList) ? erpUserGroupList.stream().collect(Collectors.toMap(role->role.getId(), role->role)):new HashMap<Integer, ErpUserGroupDBO>();
	
		//set support category dto in to main table dto
		supportCategoryDTO.setSupportCategoryName(supportCategoryDBO.getSupportCategoryName());
		if(!Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryGroupDBO())) {
			SelectDTO supportCategoryGroupDTO = new SelectDTO();
			supportCategoryGroupDTO.setValue(Integer.toString(supportCategoryDBO.getSupportCategoryGroupDBO().getId()));
			supportCategoryGroupDTO.setLabel(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportCategoryGroup());
			supportCategoryDTO.setSupportCategoryGroupDTO(supportCategoryGroupDTO);
		}
		
		if(!Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryGroupDBO()) && !Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportAreaDBO())) {
			SelectDTO supportAreaTo = new SelectDTO();
			supportAreaTo.setValue(Integer.toString(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportAreaDBO().getId()));
			supportAreaTo.setLabel(supportCategoryDBO.getSupportCategoryGroupDBO().getSupportAreaDBO().getSupportArea());
			supportCategoryDTO.setSupportAreaTo(supportAreaTo);
		}
		
		//set department dto in to main table dto
		SelectDTO erpDepartmentDTO = new SelectDTO();
		erpDepartmentDTO.setValue(Integer.toString(supportCategoryDBO.getErpDepartmentDBO().getId()));
		erpDepartmentDTO.setLabel(supportCategoryDBO.getErpDepartmentDBO().getDepartmentName());
		supportCategoryDTO.setErpDepartmentDTO(erpDepartmentDTO);
		
		//set support category campus dto list into main table dto 
		if(!Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryCampusDBOSet())) {
			List<SupportCategoryCampusDTO> supportCategoryCampusDTOList = new ArrayList<SupportCategoryCampusDTO>();
			supportCategoryDBO.getSupportCategoryCampusDBOSet().stream().filter(supCatCampus->supCatCampus.getRecordStatus() == 'A').forEach(supCatCampus->{
				SupportCategoryCampusDTO supportCategoryCampusDTO = new SupportCategoryCampusDTO();
				BeanUtils.copyProperties(supCatCampus, supportCategoryCampusDTO);
				supportCategoryCampusDTO.setId(supCatCampus.getId());
				
				SelectDTO erpCampusDTO = new SelectDTO();
				erpCampusDTO.setLabel(supCatCampus.getErpCampusDBO().getCampusName());
				erpCampusDTO.setValue(Integer.toString(supCatCampus.getErpCampusDBO().getId()));
				supportCategoryCampusDTO.setErpCampusDTO(erpCampusDTO);
				
				//set support category campus details into support category campus
				if(!Utils.isNullOrEmpty(supCatCampus.getSupportCategoryCampusDetailsDBOs())) {
					List<SupportCategoryCampusDetailsDTO> supportCategoryCampusDetailsDTOList = new ArrayList<SupportCategoryCampusDetailsDTO>();
					supCatCampus.getSupportCategoryCampusDetailsDBOs().stream().filter(supCatCampDet->supCatCampDet.getRecordStatus() == 'A').forEach(supCatCampDet->{
						SupportCategoryCampusDetailsDTO supportCategoryCampusDetailsDTO = new SupportCategoryCampusDetailsDTO();
						BeanUtils.copyProperties(supCatCampDet, supportCategoryCampusDetailsDTO);
						supportCategoryCampusDetailsDTO.setId(supCatCampDet.getId());
						SupportCategoryCampusDTO supportCategoryCampusDTONew = new SupportCategoryCampusDTO();
						supportCategoryCampusDTONew.setId(supCatCampus.getId());
						
						//set support role to to Support Category Details
						SupportRoleDTO supportRoleDTO = new SupportRoleDTO();
						supportRoleDTO.setId(supCatCampDet.getSupportRoleDBO().getId());
						supportRoleDTO.setSupportRole(supCatCampDet.getSupportRoleDBO().getSupportRole());
						supportRoleDTO.setExecutionLevel(supCatCampDet.getSupportRoleDBO().getExecutionLevel());
						supportCategoryCampusDetailsDTO.setSupportRoleDTO(supportRoleDTO);
						int totalSecs = supCatCampDet.getMaxTimeToResolve();
						int hours = totalSecs / 3600;
						int minutes = (totalSecs % 3600) / 60;
						StringBuffer timeString = new StringBuffer();
						if(hours < 10) {
							timeString.append("0");
						}
						timeString.append(Integer.valueOf(hours)+":");
						if(minutes<10) {
							timeString.append("0");	
						}
						timeString.append(Integer.valueOf(minutes));
						supportCategoryCampusDetailsDTO.setMaxTimeToResolve(timeString.toString());
						supportCategoryCampusDetailsDTO.setGroupEmailId(supCatCampDet.getGroupEmailId());
						if(supportRoleMasterMap.containsKey(supCatCampDet.getSupportRoleDBO().getId())) {
							supportRoleMasterMap.remove(supCatCampDet.getSupportRoleDBO().getId());
						}
						//set support category 
						if(!Utils.isNullOrEmpty(supCatCampDet.getSupportCategoryCampusDetailsEmployeeDBOs())){
							List<SelectDTO> supportCategoryDetailsEmployeeDTOList = new ArrayList<SelectDTO>();
							supCatCampDet.getSupportCategoryCampusDetailsEmployeeDBOs().stream().filter(supCatEmp->supCatEmp.getRecordStatus() == 'A').forEach(supCatEmp->{
								//Employee To setting
								SelectDTO empDto = new SelectDTO();
								empDto.setValue(Integer.toString(supCatEmp.getEmpDBO().getId()));
								empDto.setLabel(supCatEmp.getEmpDBO().getEmpName());
								supportCategoryDetailsEmployeeDTOList.add(empDto);
								
								});
							supportCategoryCampusDetailsDTO.setSupportCategoryCampusDetailsEmployeeDTOList(supportCategoryDetailsEmployeeDTOList.stream().sorted(Comparator.comparing(o -> o.getLabel())).collect(Collectors.toList()));
						}
						supportCategoryCampusDetailsDTOList.add(supportCategoryCampusDetailsDTO);
					});
					if(!Utils.isNullOrEmpty(supportRoleMasterMap)) {
						supportRoleMasterMap.forEach((roleId,roleBo)->{
							SupportCategoryCampusDetailsDTO supportCategoryCampusDetailsDTO = new SupportCategoryCampusDetailsDTO();
							SupportRoleDTO supportRoleDTO = new SupportRoleDTO();
							supportRoleDTO.setId(roleBo.getId());
							supportRoleDTO.setSupportRole(roleBo.getSupportRole());
							supportRoleDTO.setExecutionLevel(roleBo.getExecutionLevel());
							supportCategoryCampusDetailsDTO.setSupportRoleDTO(supportRoleDTO);
							supportCategoryCampusDetailsDTOList.add(supportCategoryCampusDetailsDTO);
						});
					}
					if(!Utils.isNullOrEmpty(supportCategoryCampusDetailsDTOList)) {
						List<SupportCategoryCampusDetailsDTO> supportCategoryCampusDetailsDTOListSorted = supportCategoryCampusDetailsDTOList.stream().sorted(Comparator.comparing(o -> o.getSupportRoleDTO().getExecutionLevel())).collect(Collectors.toList());
						supportCategoryCampusDTO.setSupportCategoryCampusDetailsDTOList(supportCategoryCampusDetailsDTOListSorted);
					}
				}
				supportCategoryCampusDTOList.add(supportCategoryCampusDTO);
			});
			supportCategoryDTO.setSupportCategoryCampusDTOList(supportCategoryCampusDTOList.stream().sorted(Comparator.comparing(o -> o.getErpCampusDTO().getLabel())).collect(Collectors.toList()));
		}
		//set support category user group list in to main table dto
		List<LookupItemDTO> supportCategoryUserGroupDTOList = new ArrayList<LookupItemDTO>();
		if(!Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryUserGroupDBOSet())) {
			supportCategoryDBO.getSupportCategoryUserGroupDBOSet().stream().filter(supCatUserGrp->supCatUserGrp.getRecordStatus() == 'A').forEach(supCatUserGrp->{
				//user group TO setting
				LookupItemDTO supportCategoryUserGroupDTO = new LookupItemDTO();
				supportCategoryUserGroupDTO.setValue(Integer.toString(supCatUserGrp.getErpUserGroupDBO().getId()));
				supportCategoryUserGroupDTO.setLabel(supCatUserGrp.getErpUserGroupDBO().getUserGroupName());
				supportCategoryUserGroupDTO.setStatus(true);
				supportCategoryUserGroupDTOList.add(supportCategoryUserGroupDTO);
				if(erpUserGroupMap.containsKey(supCatUserGrp.getErpUserGroupDBO().getId())) {
					erpUserGroupMap.remove(supCatUserGrp.getErpUserGroupDBO().getId());
				}
			});
		}
		if(!Utils.isNullOrEmpty(erpUserGroupMap)) {
			erpUserGroupMap.forEach((useGroupId, userGroupDBO )->{
				LookupItemDTO supportCategoryUserGroupDTO = new LookupItemDTO();
				supportCategoryUserGroupDTO.setValue(Integer.toString(useGroupId));
				supportCategoryUserGroupDTO.setLabel(userGroupDBO.getUserGroupName());
				supportCategoryUserGroupDTO.setStatus(false);
				supportCategoryUserGroupDTOList.add(supportCategoryUserGroupDTO);
				
			});
		}
		List<LookupItemDTO> supportCategoryUserGroupDTOListSorted = supportCategoryUserGroupDTOList.stream().sorted(Comparator.comparing(o -> o.getLabel())).collect(Collectors.toList());
		supportCategoryDTO.setSupportCategoryUserGroupDTOList(supportCategoryUserGroupDTOListSorted);
		return supportCategoryDTO;
	}
	
	public Mono<ApiResult> deleteSupportCategory(int id, String userId) {
		return supportCategoryTransaction.delete(id, userId).map(Utils::responseResult);
	}
	
	public SupportCategoryDBO convertSupportCategoryDtoToDbo(SupportCategoryDTO supportCategoryDTO, String userId) {
		SupportCategoryDBO supportCategoryDBO = !Utils.isNullOrEmpty(supportCategoryDTO.getId())?supportCategoryTransaction.getSupportCategoryDataById(supportCategoryDTO.getId()):new SupportCategoryDBO();
		//BeanUtils.copyProperties(supportCategoryDTO, supportCategoryDBO);
		supportCategoryDBO.setCreatedUsersId(Integer.parseInt(userId));
		supportCategoryDBO.setModifiedUsersId(Integer.parseInt(userId));
		supportCategoryDBO.setRecordStatus('A');
		//set support category group
		if(!Utils.isNullOrEmpty(supportCategoryDTO.getSupportCategoryGroupDTO())){
			SupportCategoryGroupDBO supportCategoryGroupDBO = new SupportCategoryGroupDBO();
			supportCategoryGroupDBO.setId(Integer.parseInt(supportCategoryDTO.getSupportCategoryGroupDTO().getValue()));
			supportCategoryDBO.setSupportCategoryGroupDBO(supportCategoryGroupDBO);
		}
		//set department
		if(!Utils.isNullOrEmpty(supportCategoryDTO.getErpDepartmentDTO())){
			ErpDepartmentDBO erpDepartmentDBO = new ErpDepartmentDBO();
			erpDepartmentDBO.setId(Integer.parseInt(supportCategoryDTO.getErpDepartmentDTO().getValue()));
			supportCategoryDBO.setErpDepartmentDBO(erpDepartmentDBO);
		}

		supportCategoryDBO.setCreatedUsersId(Integer.parseInt(userId));
		supportCategoryDBO.setModifiedUsersId(Integer.parseInt(userId));
		supportCategoryDBO.setRecordStatus('A');
		supportCategoryDBO.setIsUploadRequired(supportCategoryDTO.getIsUploadRequired());
		supportCategoryDBO.setNotificationEmailRequired(supportCategoryDTO.getNotificationEmailRequired());
		supportCategoryDBO.setNotificationSmsRequired(supportCategoryDTO.getNotificationSmsRequired());
		supportCategoryDBO.setSupportCategoryName(supportCategoryDTO.getSupportCategoryName());
		
		//set support category group
		if(!Utils.isNullOrEmpty(supportCategoryDTO.getSupportCategoryGroupDTO())){
			SupportCategoryGroupDBO supportCategoryGroupDBO = new SupportCategoryGroupDBO();
			supportCategoryGroupDBO.setId(Integer.parseInt(supportCategoryDTO.getSupportCategoryGroupDTO().getValue()));
			supportCategoryDBO.setSupportCategoryGroupDBO(supportCategoryGroupDBO);
		}
		
		Map<Integer, SupportCategoryCampusDBO> supportCategoryCampusDBOMap =  !Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryCampusDBOSet())
				?supportCategoryDBO.getSupportCategoryCampusDBOSet().stream().collect(Collectors.toMap(catCampus->catCampus.getErpCampusDBO().getId(), catCampus->catCampus)):new HashMap<Integer, SupportCategoryCampusDBO>();
		//setting support category campus in support category main table
		if(!Utils.isNullOrEmpty(supportCategoryDTO.getSupportCategoryCampusDTOList())) {
			Set<SupportCategoryCampusDBO> supCategoryCampusBoSet = new HashSet<SupportCategoryCampusDBO>();
			supportCategoryDTO.getSupportCategoryCampusDTOList().forEach(supCatCampus->{
				SupportCategoryCampusDBO supportCategoryCampusDBO = supportCategoryCampusDBOMap.containsKey(Integer.parseInt(supCatCampus.getErpCampusDTO().getValue()))?
						supportCategoryCampusDBOMap.get(Integer.parseInt(supCatCampus.getErpCampusDTO().getValue())):new SupportCategoryCampusDBO();
				if(!Utils.isNullOrEmpty(supportCategoryCampusDBO.getId())) {
					supportCategoryCampusDBOMap.remove(Integer.parseInt(supCatCampus.getErpCampusDTO().getValue()));
				}
						
				ErpCampusDBO erpCampusDBO = new ErpCampusDBO();
				erpCampusDBO.setId(Integer.parseInt(supCatCampus.getErpCampusDTO().getValue()));
				supportCategoryCampusDBO.setErpCampusDBO(erpCampusDBO);
				
				supportCategoryCampusDBO.setSupportCategoryDBO(supportCategoryDBO);
				supportCategoryCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
				supportCategoryCampusDBO.setModifiedUsersId(Integer.parseInt(userId));
				supportCategoryCampusDBO.setRecordStatus('A');
				
				//Setting support category campus details 
				if(!Utils.isNullOrEmpty(supCatCampus.getSupportCategoryCampusDetailsDTOList())) {
					Set<SupportCategoryCampusDetailsDBO> supportCategoryCampusDetailsDBOs = new HashSet<SupportCategoryCampusDetailsDBO>();
					
					Map<Integer, SupportCategoryCampusDetailsDBO> supportCategoryCampusDetailsDBOMap =  !Utils.isNullOrEmpty(supportCategoryCampusDBO.getSupportCategoryCampusDetailsDBOs())
							?supportCategoryCampusDBO.getSupportCategoryCampusDetailsDBOs().stream()
							.collect(Collectors.toMap(campDet->campDet.getSupportRoleDBO().getId(), campDet->campDet)):new HashMap<Integer, SupportCategoryCampusDetailsDBO>();
					supCatCampus.getSupportCategoryCampusDetailsDTOList().forEach(supCatCampDet->{
						SupportCategoryCampusDetailsDBO supportCategorCampusDetailsDBO = supportCategoryCampusDetailsDBOMap.containsKey(supCatCampDet.getSupportRoleDTO().getId())?
								supportCategoryCampusDetailsDBOMap.get(supCatCampDet.getSupportRoleDTO().getId()):new SupportCategoryCampusDetailsDBO();
						supportCategorCampusDetailsDBO.setGroupEmailId(supCatCampDet.getGroupEmailId());
						String[] h1=supCatCampDet.getMaxTimeToResolve().split(":");
						int hour=Integer.parseInt(h1[0]);
						int minute=Integer.parseInt(h1[1]);
						int timeInSec = (60 * minute) + (3600 * hour);
						supportCategorCampusDetailsDBO.setMaxTimeToResolve(timeInSec);
						
						supportCategorCampusDetailsDBO.setSupportCategoryCampusDBO(supportCategoryCampusDBO);//parent id
						supportCategorCampusDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						supportCategorCampusDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						supportCategorCampusDetailsDBO.setRecordStatus('A');
						//setting support role to detail table
						SupportRoleDBO supportRoleDBO = null;
						if(!Utils.isNullOrEmpty(supCatCampDet.getSupportRoleDTO())) {
							if(!Utils.isNullOrEmpty(supCatCampDet.getSupportRoleDTO().getId())) {
								supportRoleDBO = new SupportRoleDBO();
								supportRoleDBO.setId(supCatCampDet.getSupportRoleDTO().getId());
								supportRoleDBO.setExecutionLevel(supCatCampDet.getSupportRoleDTO().getExecutionLevel());
							}
						}
						supportCategorCampusDetailsDBO.setSupportRoleDBO(supportRoleDBO);
						//setting support category employee details in campus detail table
						if(!Utils.isNullOrEmpty(supCatCampDet.getSupportCategoryCampusDetailsEmployeeDTOList())) {
							Set<SupportCategoryCampusDetailsEmployeeDBO> supportCategoryCampusDetailsEmployeeDBOs = new HashSet<SupportCategoryCampusDetailsEmployeeDBO>();
							Map<Integer, SupportCategoryCampusDetailsEmployeeDBO> supportCategoryCampusDetailsEmployeeDBOMap = !Utils.isNullOrEmpty(supportCategorCampusDetailsDBO.getSupportCategoryCampusDetailsEmployeeDBOs())
									?supportCategorCampusDetailsDBO.getSupportCategoryCampusDetailsEmployeeDBOs().stream()
											.collect(Collectors.toMap(emp->emp.getEmpDBO().getId(), emp->emp)):new HashMap<Integer, SupportCategoryCampusDetailsEmployeeDBO>();
							supCatCampDet.getSupportCategoryCampusDetailsEmployeeDTOList().forEach(supCatEmpDet->{
								SupportCategoryCampusDetailsEmployeeDBO supportCategoryDetailsEmployeeDBO = supportCategoryCampusDetailsEmployeeDBOMap.containsKey(Integer.parseInt(supCatEmpDet.getValue()))?
										supportCategoryCampusDetailsEmployeeDBOMap.get(Integer.parseInt(supCatEmpDet.getValue())):new SupportCategoryCampusDetailsEmployeeDBO();
								if(!Utils.isNullOrEmpty(supportCategoryDetailsEmployeeDBO.getId())) {
									supportCategoryCampusDetailsEmployeeDBOMap.remove(Integer.parseInt(supCatEmpDet.getValue()));	
								}
								EmpDBO empDBO = new EmpDBO();
								empDBO.setId(Integer.parseInt(supCatEmpDet.getValue()));
								supportCategoryDetailsEmployeeDBO.setEmpDBO(empDBO);
								supportCategoryDetailsEmployeeDBO.setSupportCategoryCampusDetailsDBO(supportCategorCampusDetailsDBO);
								supportCategoryDetailsEmployeeDBO.setCreatedUsersId(Integer.parseInt(userId));
								supportCategoryDetailsEmployeeDBO.setModifiedUsersId(Integer.parseInt(userId));
								supportCategoryDetailsEmployeeDBO.setRecordStatus('A');
								supportCategoryCampusDetailsEmployeeDBOs.add(supportCategoryDetailsEmployeeDBO);
							});
							if(!Utils.isNullOrEmpty(supportCategoryCampusDetailsEmployeeDBOMap)){
								supportCategoryCampusDetailsEmployeeDBOMap.forEach((empId, detEmpDBO)->{
									detEmpDBO.setRecordStatus('D');
									supportCategoryCampusDetailsEmployeeDBOs.add(detEmpDBO);
								});
							}
							supportCategorCampusDetailsDBO.setSupportCategoryCampusDetailsEmployeeDBOs(supportCategoryCampusDetailsEmployeeDBOs);
						}
						supportCategoryCampusDetailsDBOs.add(supportCategorCampusDetailsDBO);
					});
					supportCategoryCampusDBO.setSupportCategoryCampusDetailsDBOs(supportCategoryCampusDetailsDBOs);
				}
				supCategoryCampusBoSet.add(supportCategoryCampusDBO);
			});
			if(!Utils.isNullOrEmpty(supportCategoryCampusDBOMap)) {
				supportCategoryCampusDBOMap.forEach((campusDelId, campusDelBO)->{
					campusDelBO.setRecordStatus('D');
					Set<SupportCategoryCampusDetailsDBO> detailsDbosDel = campusDelBO.getSupportCategoryCampusDetailsDBOs().stream().map(campusDet->{
						campusDet.setRecordStatus('D');
						campusDet.setModifiedUsersId(Integer.parseInt(userId));
						Set<SupportCategoryCampusDetailsEmployeeDBO> detailsDbos = campusDet.getSupportCategoryCampusDetailsEmployeeDBOs().stream().map(empDel->{
							empDel.setRecordStatus('D');
							empDel.setModifiedUsersId(Integer.parseInt(userId));
							return empDel;
						}).collect(Collectors.toSet());
						campusDet.setSupportCategoryCampusDetailsEmployeeDBOs(detailsDbos);
						return campusDet;
					}).collect(Collectors.toSet());
					campusDelBO.setSupportCategoryCampusDetailsDBOs(detailsDbosDel);
					supCategoryCampusBoSet.add(campusDelBO);
				});
			}
			supportCategoryDBO.setSupportCategoryCampusDBOSet(supCategoryCampusBoSet);
		}
		//support category user group in support category main table
		if(!Utils.isNullOrEmpty(supportCategoryDTO.getSupportCategoryUserGroupDTOList())) {
			Map<Integer, SupportCategoryUserGroupDBO> supportCategoryUserGroupDBOMap = !Utils.isNullOrEmpty(supportCategoryDBO.getSupportCategoryUserGroupDBOSet())
					?supportCategoryDBO.getSupportCategoryUserGroupDBOSet().stream()
							.collect(Collectors.toMap(usr->usr.getErpUserGroupDBO().getId(), usr->usr)):new HashMap<Integer, SupportCategoryUserGroupDBO>();
			Set<SupportCategoryUserGroupDBO> supportCategoryUserGroupDBOSet = new HashSet<SupportCategoryUserGroupDBO>();
			supportCategoryDTO.getSupportCategoryUserGroupDTOList().stream().filter(supCatUserGroup->supCatUserGroup.isStatus()).forEach(supCatUserGroup->{
				if(!Utils.isNullOrEmpty(supCatUserGroup.getValue())) {
					if(!Utils.isNullOrEmpty(supCatUserGroup.getValue())) {
						SupportCategoryUserGroupDBO supportCategoryUserGroupDBO = new SupportCategoryUserGroupDBO();
						if(supportCategoryUserGroupDBOMap.containsKey(Integer.parseInt(supCatUserGroup.getValue()))) {
							supportCategoryUserGroupDBO = supportCategoryUserGroupDBOMap.get(Integer.parseInt(supCatUserGroup.getValue()));
							supportCategoryUserGroupDBOMap.remove(Integer.parseInt(supCatUserGroup.getValue()));
						}
						supportCategoryUserGroupDBO.setSupportCategoryDBO(supportCategoryDBO);
						//set erpUserGroupDBO
						ErpUserGroupDBO erpUserGroupDBO = new ErpUserGroupDBO();
						erpUserGroupDBO.setId(Integer.parseInt(supCatUserGroup.getValue()));
						supportCategoryUserGroupDBO.setErpUserGroupDBO(erpUserGroupDBO);
						supportCategoryUserGroupDBO.setSupportCategoryDBO(supportCategoryDBO);
						supportCategoryUserGroupDBO.setCreatedUsersId(Integer.parseInt(userId));
						supportCategoryUserGroupDBO.setModifiedUsersId(Integer.parseInt(userId));
						supportCategoryUserGroupDBO.setRecordStatus('A');
						supportCategoryUserGroupDBOSet.add(supportCategoryUserGroupDBO);
					}
				}
			});
			if(!Utils.isNullOrEmpty(supportCategoryUserGroupDBOMap)) {
				supportCategoryUserGroupDBOMap.forEach((usrGrpId, usrGrpBo)->{
					usrGrpBo.setRecordStatus('D');
					supportCategoryUserGroupDBOSet.add(usrGrpBo);
				});
				
			}
			supportCategoryDBO.setSupportCategoryUserGroupDBOSet(supportCategoryUserGroupDBOSet);
		}
		return supportCategoryDBO;
	}
	
	public Flux<SelectDTO> getCampus() {
		return supportCategoryTransaction.getCampus().flatMapMany(Flux::fromIterable).map(this::convertCampusDboToDto);
	}
	public SelectDTO convertCampusDboToDto(ErpCampusDBO erpCampusDBO) {
		SelectDTO campusDto = null;
		if(!Utils.isNullOrEmpty(erpCampusDBO)) {
			if(!Utils.isNullOrEmpty(erpCampusDBO.getCampusName())) {
				campusDto = new SelectDTO();
				campusDto.setValue(Integer.toString(erpCampusDBO.getId()));
				campusDto.setLabel(erpCampusDBO.getCampusName());
			}
		}
		return campusDto;
	}
}

