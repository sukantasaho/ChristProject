package com.christ.erp.services.handlers.admission.settings;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dto.admission.settings.AdmQualificationDegreeListDTO;
import com.christ.erp.services.dto.common.ModelDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.admission.settings.QualificationDegreeListTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QualificationDegreeListHandler {
	
	@Autowired
	QualificationDegreeListTransaction qualificationDegreeListTransaction;

	public Flux<AdmQualificationDegreeListDTO> getGridData() {
		return qualificationDegreeListTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	public AdmQualificationDegreeListDTO convertDboToDto(AdmQualificationDegreeListDBO dbo) {
		AdmQualificationDegreeListDTO dto = new AdmQualificationDegreeListDTO();
		BeanUtils.copyProperties(dbo, dto);
		dto.setQualification(new SelectDTO());
		if (!Utils.isNullOrEmpty(dbo.getAdmQualificationListDBO())) {
			dto.getQualification().setValue(String.valueOf(dbo.getAdmQualificationListDBO().getId()));
			dto.getQualification().setLabel(dbo.getAdmQualificationListDBO().getQualificationName());
		}
		return dto;
	}

	public Mono<AdmQualificationDegreeListDTO> edit(int id) {
		return qualificationDegreeListTransaction.edit(id).map(this::convertDboToDto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return qualificationDegreeListTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<AdmQualificationDegreeListDTO> dto, String userId) {
		return dto
				.handle((admQualificationDegreeListDTO, synchronousSink) -> {
					boolean istrue = qualificationDegreeListTransaction.duplicateCheck(admQualificationDegreeListDTO);
					if (istrue) {
						synchronousSink.error(new DuplicateException(admQualificationDegreeListDTO.getDegreeName().trim() + " is already entered."));
					} else {
						synchronousSink.next(admQualificationDegreeListDTO);
					}
				}).cast(AdmQualificationDegreeListDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						qualificationDegreeListTransaction.update(s);
					} else {
						qualificationDegreeListTransaction.save(s);
					}
				    return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}
	
	public AdmQualificationDegreeListDBO convertDtoToDbo(AdmQualificationDegreeListDTO dto, String userId) {
		AdmQualificationDegreeListDBO dbo = new AdmQualificationDegreeListDBO();
		BeanUtils.copyProperties(dto, dbo);
		dbo.setAdmQualificationListDBO(new AdmQualificationListDBO());
		dbo.getAdmQualificationListDBO().setId(Integer.parseInt(dto.getQualification().getValue()));;
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		return dbo;
	}	
}
