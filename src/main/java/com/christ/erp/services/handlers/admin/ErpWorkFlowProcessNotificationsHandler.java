package com.christ.erp.services.handlers.admin;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.common.ErpWorkFlowProcessNotificationsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.admin.ErpWorkFlowProcessNotificationsTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ErpWorkFlowProcessNotificationsHandler {

	@Autowired
	ErpWorkFlowProcessNotificationsTransaction erpWorkFlowProcessNotificationsTransaction;

	public Flux<ErpWorkFlowProcessNotificationsDTO> getGridData() {
		return erpWorkFlowProcessNotificationsTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public ErpWorkFlowProcessNotificationsDTO convertDboToDto(ErpWorkFlowProcessNotificationsDBO dbo) {
		ErpWorkFlowProcessNotificationsDTO dto = new ErpWorkFlowProcessNotificationsDTO();
		BeanUtils.copyProperties(dbo, dto);
		dto.setErpWorkFlowProcessDBO(new SelectDTO());
		if (!Utils.isNullOrEmpty(dbo.getErpWorkFlowProcessDBO())) {
			dto.getErpWorkFlowProcessDBO().setValue(String.valueOf(dbo.getErpWorkFlowProcessDBO().id));
			dto.getErpWorkFlowProcessDBO().setLabel(dbo.getErpWorkFlowProcessDBO().processCode);
		}
		dto.setErpEmailsTemplateDBO(new SelectDTO());
		if (!Utils.isNullOrEmpty(dbo.getErpEmailsTemplateDBO())) {
			dto.getErpEmailsTemplateDBO().setValue(String.valueOf(dbo.getErpEmailsTemplateDBO().id));
			dto.getErpEmailsTemplateDBO().setLabel(dbo.getErpEmailsTemplateDBO().templateName);
		}
		dto.setErpSmsTemplateDBO(new SelectDTO());
		if (!Utils.isNullOrEmpty(dbo.getErpSmsTemplateDBO())) {
			dto.getErpSmsTemplateDBO().setValue(String.valueOf(dbo.getErpSmsTemplateDBO().id));
			dto.getErpSmsTemplateDBO().setLabel(dbo.getErpSmsTemplateDBO().templateName);
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ErpWorkFlowProcessNotificationsDTO> dto, String userId) {
		return dto.handle((ErpWorkFlowProcessNotificationsDTO, synchronousSink) -> {
			boolean istrue = erpWorkFlowProcessNotificationsTransaction.duplicateCheck(ErpWorkFlowProcessNotificationsDTO);
			if (istrue) {
				synchronousSink.error(new DuplicateException("Duplicate Record for Notification Code. "));
			} else {
				synchronousSink.next(ErpWorkFlowProcessNotificationsDTO);
			}
		}).cast(ErpWorkFlowProcessNotificationsDTO.class).map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
			if (!Utils.isNullOrEmpty(s.getId())) {
				erpWorkFlowProcessNotificationsTransaction.update(s);
			} else {
				erpWorkFlowProcessNotificationsTransaction.save(s);
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	private static ErpWorkFlowProcessNotificationsDBO convertDtoToDbo(ErpWorkFlowProcessNotificationsDTO dto,String userId) {
		ErpWorkFlowProcessNotificationsDBO dbo = new ErpWorkFlowProcessNotificationsDBO();
		BeanUtils.copyProperties(dto, dbo);
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if (dto.getErpWorkFlowProcessDBO() != null && dto.getErpWorkFlowProcessDBO().getValue() != null && dto.getErpWorkFlowProcessDBO().getValue()!="") {
			dbo.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
			dbo.getErpWorkFlowProcessDBO().setId(Integer.parseInt(dto.getErpWorkFlowProcessDBO().getValue()));
		}else {
			dbo.setErpWorkFlowProcessDBO(null);
		}
		if (dto.getErpEmailsTemplateDBO() != null && dto.getErpEmailsTemplateDBO().getValue() != null && dto.getErpEmailsTemplateDBO().getValue() != "") {
			dbo.setErpEmailsTemplateDBO(new ErpTemplateDBO());
			dbo.getErpEmailsTemplateDBO().setId(Integer.parseInt(dto.getErpEmailsTemplateDBO().getValue()));
		}else {
			dbo.setErpEmailsTemplateDBO(null);
		}
		if (dto.getErpSmsTemplateDBO() != null && dto.getErpSmsTemplateDBO().getValue() != null && dto.getErpSmsTemplateDBO().getValue()!="") {
			dbo.setErpSmsTemplateDBO(new ErpTemplateDBO());
			dbo.getErpSmsTemplateDBO().setId(Integer.parseInt(dto.getErpSmsTemplateDBO().getValue()));
		}else {
			dbo.setErpSmsTemplateDBO(null);
		}
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		return dbo;
	}

	public Mono<ErpWorkFlowProcessNotificationsDTO> edit(int id) {
		return erpWorkFlowProcessNotificationsTransaction.edit(id).map(this::convertDboToDto);
	}
}
