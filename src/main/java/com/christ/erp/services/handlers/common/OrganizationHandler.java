package com.christ.erp.services.handlers.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpOrganizationsDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.OrganizationsDTO;
import com.christ.erp.services.transactions.common.OrganizationTransaction;

public class OrganizationHandler {
	private static volatile OrganizationHandler organizationHandler=null;
	
	OrganizationTransaction organizationTransaction = OrganizationTransaction.getInstance(); 

	public static OrganizationHandler getInstance() {
		if (organizationHandler == null) {
			organizationHandler = new OrganizationHandler();
		}
		return organizationHandler;
	}

	public OrganizationsDTO getOrganizationData() {
		OrganizationsDTO organizationsDTO = null;
		try {
			ErpOrganizationsDBO erpOrganizationsDBO = organizationTransaction.getErpOrganizationsDBO(0);
			if (erpOrganizationsDBO != null) {
				organizationsDTO =  new OrganizationsDTO();
				organizationsDTO.id = String.valueOf(erpOrganizationsDBO.id);
				organizationsDTO.organizationName = erpOrganizationsDBO.organizationName;
				organizationsDTO.address1 = erpOrganizationsDBO.address1;
				organizationsDTO.address2 = erpOrganizationsDBO.address2;
				organizationsDTO.address3 = erpOrganizationsDBO.address3;
				organizationsDTO.organizationVision = erpOrganizationsDBO.organizationVision; 
				organizationsDTO.organizationMission = erpOrganizationsDBO.organizationMission;
				organizationsDTO.organizationCoreValues = erpOrganizationsDBO.organizationCoreValues;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return organizationsDTO;
	}

	public ApiResult<ModelBaseDTO> updateOrganization(OrganizationsDTO data, String userId) throws Exception{
		ApiResult<ModelBaseDTO> api = new ApiResult<ModelBaseDTO>();
		ErpOrganizationsDBO header = null;
		if (!Utils.isNullOrWhitespace(data.id)) {
			header =  organizationTransaction.getErpOrganizationsDBO(Integer.parseInt(data.id));
		}
		if (header != null) {
			header.organizationName = data.organizationName;
			header.address1 = data.address1;
			header.address2 = data.address2;
			header.address3 = data.address3;
			if (!Utils.isNullOrEmpty(data.organizationVision)) {
				header.organizationVision = data.organizationVision;
			}
		    if (!Utils.isNullOrEmpty(data.organizationMission)) {
		    	header.organizationMission = data.organizationMission;
		    }
			if (!Utils.isNullOrEmpty(data.organizationCoreValues)) {
				header.organizationCoreValues = data.organizationCoreValues;
			}
			header.modifiedUsersId = Integer.parseInt(userId);
			boolean result = organizationTransaction.updateErpOrganizationsDBO(header);
			if (result) {
				api.success = true;
				api.dto = new ModelBaseDTO();
				api.dto.id = String.valueOf(header.id);
			}
			else {
				api.success = false;
			}
	    	
		} else {
	    	api.failureMessage = " Data is not available.";
	    	api.success = false;
	    }
		return api;
	}
	
	public OrganizationsDTO getOrganizationMissionVision() {
		OrganizationsDTO organizationsDTO = null;
		try {
			ErpOrganizationsDBO erpOrganizationsDBO = organizationTransaction.getErpOrganizationsDBO(0);
			if (erpOrganizationsDBO != null) {
				organizationsDTO =  new OrganizationsDTO();
				organizationsDTO.id = String.valueOf(erpOrganizationsDBO.id);
				organizationsDTO.organizationMission = Utils.htmlToText(erpOrganizationsDBO.organizationMission);
				organizationsDTO.organizationVision = Utils.htmlToText(erpOrganizationsDBO.organizationVision);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return organizationsDTO;
	}

}
