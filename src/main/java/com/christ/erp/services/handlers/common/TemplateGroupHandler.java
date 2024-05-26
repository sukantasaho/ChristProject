package com.christ.erp.services.handlers.common;

import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateGroupDBO;
import com.christ.erp.services.dto.common.ErpTemplateGroupDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.common.TemplateGroupTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TemplateGroupHandler
{
	@Autowired
	TemplateGroupTransaction templateGroupTransaction;
	
	public Flux<ErpTemplateGroupDTO> getGridData() {
		return templateGroupTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public ErpTemplateGroupDTO convertDboToDto(ErpTemplateGroupDBO dbo) {
		ErpTemplateGroupDTO dto = new ErpTemplateGroupDTO();
		BeanUtils.copyProperties(dbo, dto);
		return dto;
	}
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ErpTemplateGroupDTO> dto, String userId) {
		return dto
			.handle((erpTemplateGroupDTO, synchronousSink) -> {
				boolean isTrue = templateGroupTransaction.duplicateCheck(erpTemplateGroupDTO);
				if (isTrue) {
					synchronousSink.error(new DuplicateException("Template Group Code is already exist"));
				} else {
					synchronousSink.next(erpTemplateGroupDTO);
				}
			}).cast(ErpTemplateGroupDTO.class)
			.map(data -> convertDtoToDbo(data, userId))
			.flatMap(s -> {
				if (!Utils.isNullOrEmpty(s.getId())) {
					templateGroupTransaction.update(s);
				} else {
					templateGroupTransaction.save(s);
				}
				return Mono.just(Boolean.TRUE);
			}).map(Utils::responseResult);
	}
	
	public Mono<ErpTemplateGroupDTO> edit(int id) {
		return templateGroupTransaction.edit(id).map(this::convertDboToDto);
	}
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return templateGroupTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public ErpTemplateGroupDBO convertDtoToDbo(ErpTemplateGroupDTO dto, String userId) {
		ErpTemplateGroupDBO dbo = new ErpTemplateGroupDBO();
		BeanUtils.copyProperties(dto, dbo);
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		return dbo;
	}
}
