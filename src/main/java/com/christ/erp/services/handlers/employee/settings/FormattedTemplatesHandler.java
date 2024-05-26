package com.christ.erp.services.handlers.employee.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.SysMenuModuleSubDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.settings.ErpTemplateDTO;
import com.christ.erp.services.transactions.employee.settings.FormattedTemplatesTransaction;

public class FormattedTemplatesHandler {
	private static volatile FormattedTemplatesHandler formattedTemplatesHandler = null;
	FormattedTemplatesTransaction formattedTemplatesTransaction = FormattedTemplatesTransaction.getInstance();

	public static FormattedTemplatesHandler getInstance() {
		if (formattedTemplatesHandler == null) {
			formattedTemplatesHandler = new FormattedTemplatesHandler();
		}
		return formattedTemplatesHandler;
	}

	public List<ErpTemplateDTO> getGridData() {
		List<ErpTemplateDTO> erpTemplateDTO = new ArrayList<>();
		List<Tuple> list;
		try {
			list = formattedTemplatesTransaction.getGridData();
			for (Tuple tuple : list) {
				ErpTemplateDTO gridDTO = new ErpTemplateDTO();
				gridDTO.process = new SelectDTO();
				if(!Utils.isNullOrEmpty(tuple.get("subModuleName")) && !Utils.isNullOrEmpty(tuple.get("moduleName"))) {
				gridDTO.process.label = String.valueOf(tuple.get("subModuleName")).concat(" - " +String.valueOf(tuple.get("moduleName")));
				}
				gridDTO.templateCode = String.valueOf(tuple.get("templateCode"));
				gridDTO.id = String.valueOf(tuple.get("ID"));
				gridDTO.templateName = String.valueOf(tuple.get("templateName"));
				gridDTO.templateId = !Utils.isNullOrEmpty(tuple.get("templateId")) ? String.valueOf(tuple.get("templateId")) : null;
				erpTemplateDTO.add(gridDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return erpTemplateDTO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public ApiResult<ModelBaseDTO> saveOrUpdate(ErpTemplateDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		ErpTemplateDBO header = null;
		if (Utils.isNullOrWhitespace(data.id) == false) {
			header = formattedTemplatesTransaction.getErpTemplateDBO(Integer.parseInt(data.id));
		}
		Boolean isDuplicate = formattedTemplatesTransaction.isDuplicate(data.templateCode, data.id);
		if (!isDuplicate) {
			if (header == null) {
				header = new ErpTemplateDBO();
				header.createdUsersId = Integer.parseInt(userId);
			}
			try {
				SysMenuModuleSubDBO erpmoduleSubDBO = formattedTemplatesTransaction
						.getModuleSubDBO(Integer.parseInt(data.process.value));
				header.sysMenuModuleSubDBO = erpmoduleSubDBO;
				header.templateCode = data.templateCode.toUpperCase();
				header.templateName = data.templateName;
				header.templateDescription = data.templateDescription;
				header.templateType = data.types;
				if (header.templateType.equalsIgnoreCase("Mail")) {
					header.mailSubject = data.mailSubject;
					header.mailFromName = data.fromName;
				} else {
					header.mailSubject = null;
					header.mailFromName = null;
				}
				if (header.templateType.trim().equalsIgnoreCase("SMS")) {
					if(!Utils.isNullOrEmpty(data.templateId)){
						header.templateId = data.templateId;
					}
				} else {
					header.templateId =  null;
				}
				header.templateContent = data.templateContent;
				header.availableTags = data.availableTags;
				header.recordStatus = 'A';
				if (header.id != null) {
					header.modifiedUsersId = Integer.parseInt(userId);
				}
				formattedTemplatesTransaction.saveOrUpdate(header);
				if (header.id != 0) {
					result.success = true;
					result.dto = new ModelBaseDTO();
					result.dto.id = header.id.toString();
				}
				result.success = true;
			} catch (java.text.ParseException error) {
				Utils.log(error.getMessage());
			}
		} else {
			result.failureMessage = "Duplicate record exists for Template Code :" + data.templateCode;
			result.success = false;
		}
		return result;
	}

	public ErpTemplateDTO selectFormattedTemplates(String id) {
		Map<Integer, String> subModuleMap = new HashMap<Integer, String>();
		ErpTemplateDTO erpTemplateDTO = new ErpTemplateDTO();
		List<Tuple> mappings;
		try {
			mappings = formattedTemplatesTransaction.getSubModuleList();
			if (mappings != null && mappings.size() > 0) {
				for (Tuple mapping : mappings) {
					subModuleMap.put(Integer.parseInt(mapping.get("ID").toString()), mapping.get("Text").toString());
				}
			}
			ErpTemplateDBO selectFormattedTemplate = formattedTemplatesTransaction
					.getErpTemplateDBO(Integer.parseInt(id));
			if (selectFormattedTemplate != null) {
				erpTemplateDTO.id = selectFormattedTemplate.id.toString();
				erpTemplateDTO.process = new SelectDTO();
				erpTemplateDTO.process.value = String.valueOf(selectFormattedTemplate.getSysMenuModuleSubDBO().getId());
				erpTemplateDTO.process.label = selectFormattedTemplate.getSysMenuModuleSubDBO().getSubModuleName().concat(" - " +String.valueOf(selectFormattedTemplate.getSysMenuModuleSubDBO().getSysMenuModuleDBO().getModuleName()));
				erpTemplateDTO.templateCode = selectFormattedTemplate.templateCode;
				erpTemplateDTO.templateName = selectFormattedTemplate.templateName;
				erpTemplateDTO.types = selectFormattedTemplate.templateType;
				if (selectFormattedTemplate.templateType.equalsIgnoreCase("Mail")) {
					erpTemplateDTO.mailSubject = selectFormattedTemplate.mailSubject;
					erpTemplateDTO.fromName = selectFormattedTemplate.mailFromName;
				}
				if (selectFormattedTemplate.templateType.equalsIgnoreCase("SMS")) {
					if(!Utils.isNullOrEmpty(selectFormattedTemplate.templateId)){
						erpTemplateDTO.templateId = selectFormattedTemplate.templateId;
					}
				}
				erpTemplateDTO.templateDescription = selectFormattedTemplate.templateDescription;
				erpTemplateDTO.templateContent = selectFormattedTemplate.templateContent;
				erpTemplateDTO.availableTags = selectFormattedTemplate.availableTags;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return erpTemplateDTO;
	}

	public boolean deleteErpTemplate(String id, String userId) {
		try {
			ErpTemplateDBO erpTemplateDBO = formattedTemplatesTransaction.getErpTemplateDBO(Integer.parseInt(id));
			if (erpTemplateDBO != null) {
				erpTemplateDBO.recordStatus = 'D';
				erpTemplateDBO.modifiedUsersId = Integer.parseInt(userId);
				if (erpTemplateDBO.id != null) {
					return formattedTemplatesTransaction.saveOrUpdate(erpTemplateDBO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
