package com.christ.erp.services.handlers.administraton.academicCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarUserTypesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarUserTypesDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.administraton.academicCalendar.AcademicCalendarUserTypeTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AcademicCalendarUserTypeHandler {
	
	@Autowired
	AcademicCalendarUserTypeTransaction academicCalendarUserTypeTransaction;

	public Flux<ErpCalendarUserTypesDTO> getGridData() {
		return academicCalendarUserTypeTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	private ErpCalendarUserTypesDTO convertDboToDto(ErpCalendarUserTypesDBO dbo) {
		ErpCalendarUserTypesDTO dto = new ErpCalendarUserTypesDTO();
		if (!Utils.isNullOrEmpty(dto)) {
			dto.setId(dbo.getId());
			dto.setStudent(dbo.isStudent());
			dto.setUserType(dbo.getUserType());
			if (!Utils.isNullOrEmpty(dbo.getEmpEmployeeCategoryDBO())) {
				dto.setEmpEmployeeCategoryDTO(new SelectDTO());
				dto.getEmpEmployeeCategoryDTO().setValue(String.valueOf(dbo.getEmpEmployeeCategoryDBO().getId()));
				dto.getEmpEmployeeCategoryDTO().setLabel(dbo.getEmpEmployeeCategoryDBO().getEmployeeCategoryName());
			}
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ErpCalendarUserTypesDTO> dto,String userId) {
		return dto.handle((erpCalendarUserTypesDTO, synchronousSink) -> {
			boolean istrue = academicCalendarUserTypeTransaction.duplicateCheck(erpCalendarUserTypesDTO);
			if (istrue) {
				synchronousSink.error(new DuplicateException("The User type for the employee category is already added"));
			} else {
				synchronousSink.next(erpCalendarUserTypesDTO);
			}
		}).cast(ErpCalendarUserTypesDTO.class).map(data -> convertDtoToDbo(data,userId)).flatMap(s -> {
			if (!Utils.isNullOrEmpty(s.getId())) {
				academicCalendarUserTypeTransaction.update(s);
			} else {
				academicCalendarUserTypeTransaction.save(s);
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}
	
	private ErpCalendarUserTypesDBO convertDtoToDbo(ErpCalendarUserTypesDTO dto, String userId) {
		ErpCalendarUserTypesDBO dbo = Utils.isNullOrEmpty(dto.getId()) ? new ErpCalendarUserTypesDBO() : academicCalendarUserTypeTransaction.edit(dto.getId());
		dbo.setUserType(dto.getUserType().replaceAll("\\s+"," ").trim());
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		dbo.setStudent(dto.isStudent());
		if (!Utils.isNullOrEmpty(dto.getEmpEmployeeCategoryDTO())) {
			dbo.setEmpEmployeeCategoryDBO(new EmpEmployeeCategoryDBO());
			dbo.getEmpEmployeeCategoryDBO().setId(Integer.parseInt(dto.getEmpEmployeeCategoryDTO().getValue()));
		}
		if(dto.isStudent()) {
			dbo.setEmpEmployeeCategoryDBO(null);
		}
		dbo.setRecordStatus('A');
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}				
		return dbo;
	}
	public Mono<ErpCalendarUserTypesDTO> edit(int id) {
		return Mono.just(academicCalendarUserTypeTransaction.edit(id)).map(this::convertDboToDto);
	}

	@SuppressWarnings("rawtypes")
	public  Mono<ApiResult> delete(int id, String userId) {
		return academicCalendarUserTypeTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}
}
