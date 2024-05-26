package com.christ.erp.services.handlers.support.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpUserGroupDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportAreaDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportCategoryGroupDBO;
import com.christ.erp.services.dbobjects.support.settings.SupportRoleDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.support.common.CommonSupportTransaction;

import reactor.core.publisher.Flux;

@Service
public class CommonSupportHandler {
	
	@Autowired
	CommonSupportTransaction commonSupportTransaction;
	
	public Flux<SelectDTO> getSupportArea(){
		return commonSupportTransaction.getSupportArea().flatMapMany(Flux::fromIterable).map(this::convertSupportAreaDboToDto);
	}

	public SelectDTO convertSupportAreaDboToDto(SupportAreaDBO supportAreaDBO){
		SelectDTO supportAreaDTO = new SelectDTO();
		if(!Utils.isNullOrEmpty(supportAreaDBO.getId()) && !Utils.isNullOrEmpty(supportAreaDBO.getSupportArea())) {
			supportAreaDTO.setLabel(supportAreaDBO.getSupportArea());
			supportAreaDTO.setValue(String.valueOf(supportAreaDBO.getId()));
		}
		return supportAreaDTO;
	}
	
	public Flux<SelectDTO> getSupportCategoryGroupByArea(int supportAreaId){
		return commonSupportTransaction.getSupportCategoryGroupByArea(supportAreaId).flatMapMany(Flux::fromIterable).map(this::convertSupportCategoryGroupDboToDto);
		
	}

	public SelectDTO convertSupportCategoryGroupDboToDto(SupportCategoryGroupDBO supportCategoryGroupDBO){
		SelectDTO supCategoryGroup = new SelectDTO();
		if(!Utils.isNullOrEmpty(supportCategoryGroupDBO.getId()) && !Utils.isNullOrEmpty(supportCategoryGroupDBO.getSupportCategoryGroup())) {
			supCategoryGroup.setLabel(supportCategoryGroupDBO.getSupportCategoryGroup());
			supCategoryGroup.setValue(Integer.toString(supportCategoryGroupDBO.getId()));
		}
		return supCategoryGroup;
	}
	
	public Flux<SelectDTO> getErpUserGroup() {
		return commonSupportTransaction.getErpUserGroup().flatMapMany(Flux::fromIterable).map(this::convertErpUserGroupDboToDto);
	}
	public SelectDTO convertErpUserGroupDboToDto(ErpUserGroupDBO erpUserGroupDBO) {
		SelectDTO erpUserGroupDTO = new SelectDTO();
		if(!Utils.isNullOrEmpty(erpUserGroupDBO.getId()) && !Utils.isNullOrEmpty(erpUserGroupDBO.getUserGroupName())) {
			erpUserGroupDTO.setLabel(erpUserGroupDBO.getUserGroupName());
			erpUserGroupDTO.setValue(Integer.toString(erpUserGroupDBO.getId()));		
		}
		return erpUserGroupDTO;
	}
	public Flux<SelectDTO> getSupportRole() {
		return commonSupportTransaction.getSupportRole().flatMapMany(Flux::fromIterable).map(this::convertSupportRoleDboToDto);
	}
	public SelectDTO convertSupportRoleDboToDto(SupportRoleDBO supportRoleDBO) {
		SelectDTO supportRoleDTO = new SelectDTO();
		if(!Utils.isNullOrEmpty(supportRoleDBO.getId()) && !Utils.isNullOrEmpty(supportRoleDBO.getSupportRole())) {
			supportRoleDTO.setLabel(supportRoleDBO.getSupportRole());
			supportRoleDTO.setValue(Integer.toString(supportRoleDBO.getId()));		
		}
		return supportRoleDTO;
	}
	
	
}
