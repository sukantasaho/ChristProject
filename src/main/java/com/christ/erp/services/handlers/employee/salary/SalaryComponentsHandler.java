package com.christ.erp.services.handlers.employee.salary;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleComponentsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.employee.salary.SalaryComponentsTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class SalaryComponentsHandler {

	@Autowired
	SalaryComponentsTransaction salaryComponentsTransaction;

	public Flux<EmpPayScaleComponentsDTO> getGridData() {
		return salaryComponentsTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public EmpPayScaleComponentsDTO convertDboToDto(EmpPayScaleComponentsDBO dbo) {
		EmpPayScaleComponentsDTO dto = new EmpPayScaleComponentsDTO();
		BeanUtils.copyProperties(dbo, dto);
		dto.setPayScaleType(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getPayScaleType())) {
			dto.getPayScaleType().setLabel(dbo.getPayScaleType());
			dto.getPayScaleType().setValue(dbo.getPayScaleType());
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<EmpPayScaleComponentsDTO> dto, String userId) {
		return dto
				.handle((salaryComponentsDTO, synchronousSink) -> {
					boolean istrue = salaryComponentsTransaction.duplicateCheck(salaryComponentsDTO);
					if (istrue) {					
						synchronousSink.error(new DuplicateException("Duplicate entry for the scale type selected"));
					} else {
						synchronousSink.next(salaryComponentsDTO);
					}
				}).cast(EmpPayScaleComponentsDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						salaryComponentsTransaction.update(s);
					} else {
						salaryComponentsTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}
	
	public EmpPayScaleComponentsDBO convertDtoToDbo(EmpPayScaleComponentsDTO dto, String userId) {
		EmpPayScaleComponentsDBO dbo = null;
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo = salaryComponentsTransaction.getEmpPayScaleComponentDetail(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		} else {
			dbo = new EmpPayScaleComponentsDBO();			
		}
		dbo.setIsCalculationTypePercentage(dto.getIsCalculationTypePercentage());
		dbo.setIsComponentBasic(dto.getIsComponentBasic());	
		dbo.setPercentage(dto.getPercentage());
		dbo.setSalaryComponentDisplayOrder(dto.getSalaryComponentDisplayOrder());
		dbo.setSalaryComponentName(dto.getSalaryComponentName());
		dbo.setSalaryComponentShortName(dto.getSalaryComponentShortName());
		if(!Utils.isNullOrEmpty(dto.getIsComponentBasic())){
			if(dto.getIsComponentBasic()) {
				dbo.setPercentage(null);
				dbo.setIsCalculationTypePercentage(false);
				dbo.setIsComponentBasic(dto.getIsComponentBasic());
			}
		}
		if(!Utils.isNullOrEmpty(dto.getIsCalculationTypePercentage())) {
			if(dto.getIsCalculationTypePercentage().equals(false)) {
				dbo.setPercentage(null);
			}
		}
		if(!Utils.isNullOrEmpty(dto.getPayScaleType().getLabel())) {
			dbo.setPayScaleType(dto.getPayScaleType().getLabel());
		}
		dbo.setCreatedUsersId(Integer.parseInt(userId));	
		dbo.setRecordStatus('A');
		return dbo;
	}
	
	public Mono<EmpPayScaleComponentsDTO> edit(int id) {
		return Mono.just(salaryComponentsTransaction.getEmpPayScaleComponentDetail(id)).map(this::convertDboToDto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return salaryComponentsTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> isDuplicateDisplayOrder(String payScaleType, String id, String displayOrder) {
		var apiResult = new ApiResult();
		Boolean isDuplicate = salaryComponentsTransaction.isDuplicateDisplayOrder(payScaleType,id,displayOrder);
		if(isDuplicate) {
			apiResult.setSuccess(true);
			apiResult.setFailureMessage("Duplicate display order number");		
		} else {
			apiResult.setSuccess(false);		
		}
		return Mono.just(apiResult);
	}
}