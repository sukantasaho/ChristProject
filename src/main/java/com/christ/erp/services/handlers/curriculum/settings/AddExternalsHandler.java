package com.christ.erp.services.handlers.curriculum.settings;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.curriculum.common.ErpExternalsCategoryDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsAdditionalDetailsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.settings.ExternalsAdditionalDetailsDTO;
import com.christ.erp.services.dto.curriculum.settings.ExternalsDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.curriculum.settings.AddExternalsTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AddExternalsHandler {

	@Autowired
	AddExternalsTransaction addExternalsTransaction;

	public Flux<ExternalsDTO> getGridData() {
		return addExternalsTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public ExternalsDTO convertDboToDto(ExternalsDBO dbo) {
		ExternalsDTO dto = new ExternalsDTO();
		BeanUtils.copyProperties(dbo, dto);
		dto.setDob(dbo.getDob());
		dto.setDepartment(new SelectDTO());
		if (!Utils.isNullOrEmpty(dbo.getErpDepartmentDBO())) {
			dto.getDepartment().setValue(String.valueOf(dbo.getErpDepartmentDBO().getId()));
			dto.getDepartment().setLabel(dbo.getErpDepartmentDBO().getDepartmentName());
		}
		dto.setExternalsCategory(new SelectDTO());
		if (!Utils.isNullOrEmpty(dbo.getErpExternalsCategoryDBO())) {
			dto.getExternalsCategory().setValue(String.valueOf(dbo.getErpExternalsCategoryDBO().getId()));
			dto.getExternalsCategory().setLabel(dbo.getErpExternalsCategoryDBO().getExternalsCategoryName());
		}
		dto.setStudent(new StudentDTO());
		if (!Utils.isNullOrEmpty(dbo.getStudentDBO())) {
			dto.getStudent().setId(dbo.getStudentDBO().getId());
			dto.getStudent().setStudentName(dbo.getStudentDBO().getStudentName());
			dto.getStudent().setRegisterNo(dbo.getStudentDBO().getRegisterNo());
		}
		if (!Utils.isNullOrEmpty(dbo.getExternalsAdditionalDetailsDBO())) {
			ExternalsAdditionalDetailsDTO externalsAdditionalDetailsDTO = new ExternalsAdditionalDetailsDTO();
			BeanUtils.copyProperties(dbo.getExternalsAdditionalDetailsDBO(), externalsAdditionalDetailsDTO);
			externalsAdditionalDetailsDTO.setErpQualificationLevel(new SelectDTO());
			if (!Utils.isNullOrEmpty(dbo.getExternalsAdditionalDetailsDBO().getErpQualificationLevelDBO())) {
				externalsAdditionalDetailsDTO.getErpQualificationLevel().setValue(String.valueOf(dbo.getExternalsAdditionalDetailsDBO().getErpQualificationLevelDBO().getId()));
				externalsAdditionalDetailsDTO.getErpQualificationLevel().setLabel(String.valueOf(dbo.getExternalsAdditionalDetailsDBO().getErpQualificationLevelDBO().getQualificationLevelName()));				
			}					
			dto.setExternalsAdditionalDetails(externalsAdditionalDetailsDTO);
		}
		return dto;	
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ExternalsDTO> dto, String userId) {
		return dto.handle((externalsDTO, synchronousSink) -> {
			boolean istrue = addExternalsTransaction.duplicateCheck(externalsDTO);
			if (istrue) {
				synchronousSink.error(new DuplicateException("The External member is already added "));
			} else {
				synchronousSink.next(externalsDTO);
			}
		}).cast(ExternalsDTO.class).map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
			if (!Utils.isNullOrEmpty(s.getId())) {
				addExternalsTransaction.update(s);
			} else {
				addExternalsTransaction.save(s);
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	private ExternalsDBO convertDtoToDbo(ExternalsDTO dto, String userId) {
		ExternalsDBO dbo = new ExternalsDBO();	
		BeanUtils.copyProperties(dto, dbo);
		dbo.setDob(dto.getDob());
		dbo.setErpDepartmentDBO(new ErpDepartmentDBO());		
		dbo.getErpDepartmentDBO().setId(Integer.parseInt(dto.getDepartment().getValue()));
		dbo.setErpExternalsCategoryDBO(new ErpExternalsCategoryDBO());
		dbo.getErpExternalsCategoryDBO().setId(Integer.parseInt(dto.getExternalsCategory().getValue()));
		if (!Utils.isNullOrEmpty(dto.getStudent().getId())) {
			dbo.setStudentDBO(new StudentDBO());
			dbo.getStudentDBO().setId(dto.getStudent().getId());
		}else {
			dbo.setStudentDBO(null);
		}			
		if (!Utils.isNullOrEmpty(dto.getExternalsAdditionalDetails())) {		
			ExternalsAdditionalDetailsDBO externalsAdditionalDetailsDBO = new ExternalsAdditionalDetailsDBO();
			BeanUtils.copyProperties(dto.getExternalsAdditionalDetails(), externalsAdditionalDetailsDBO);
			externalsAdditionalDetailsDBO.setExternalsDBO(dbo);
			externalsAdditionalDetailsDBO.setErpQualificationLevelDBO(new ErpQualificationLevelDBO());				
			externalsAdditionalDetailsDBO.getErpQualificationLevelDBO().setId(Integer.parseInt((dto.getExternalsAdditionalDetails().getErpQualificationLevel().getValue())));
			externalsAdditionalDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
			if (!Utils.isNullOrEmpty(dto.getExternalsAdditionalDetails().getId())) {
				externalsAdditionalDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
			}
			externalsAdditionalDetailsDBO.setRecordStatus('A');
			dbo.setExternalsAdditionalDetailsDBO(externalsAdditionalDetailsDBO);
		}
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		return dbo;
	}

	public Mono<ExternalsDTO> edit(int id) {
		return addExternalsTransaction.edit(id).map(this::convertDboToDto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return addExternalsTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}
}
