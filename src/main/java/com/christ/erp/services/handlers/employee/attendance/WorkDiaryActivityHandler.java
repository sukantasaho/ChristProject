package com.christ.erp.services.handlers.employee.attendance;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryActivityDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryMainActivityDBO;
import com.christ.erp.services.dto.common.ModelDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryActivityDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.employee.attendance.WorkDiaryActivityTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WorkDiaryActivityHandler {
	
	@Autowired
	WorkDiaryActivityTransaction workDiaryActivityTransaction;
	
	public Flux<EmpWorkDiaryActivityDTO> getGridData(){
		return workDiaryActivityTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	public EmpWorkDiaryActivityDTO convertDboToDto(EmpWorkDiaryActivityDBO dbo) {
		EmpWorkDiaryActivityDTO dto = new EmpWorkDiaryActivityDTO();
		BeanUtils.copyProperties(dbo, dto);
		dto.setMainActivity(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getEmpWorkDiaryMainActivityDBO())) {
			dto.getMainActivity().setValue(String.valueOf(dbo.getEmpWorkDiaryMainActivityDBO().getId()));//dbo.getId()
			dto.getMainActivity().setLabel(dbo.getEmpWorkDiaryMainActivityDBO().getMainActivityName());
		}
		dto.setDepatment(new SelectDTO());
		if( !Utils.isNullOrEmpty(dbo.getErpDepartmentDBO())) {
			dto.getDepatment().setValue(String.valueOf(dbo.getErpDepartmentDBO().id));
			dto.getDepatment().setLabel(dbo.getErpDepartmentDBO().departmentName);
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<EmpWorkDiaryActivityDTO> dto, String userId) {
		return dto
				.handle((empWorkDiaryActivityDTO, synchronousSink) -> {	
					boolean isTeachingTrue = workDiaryActivityTransaction.duplicateCheck(empWorkDiaryActivityDTO);
					if(isTeachingTrue) {
						if(empWorkDiaryActivityDTO.isForTeaching()) {
							synchronousSink.error(new DuplicateException("Activity Name already exists"));
						} else {
							synchronousSink.error(new DuplicateException("Activity Name and Department already exists"));
						}		
					} else {
						synchronousSink.next(empWorkDiaryActivityDTO);
					}
				}).cast(EmpWorkDiaryActivityDTO.class)
				 .map(data -> convertDtoToDbo(data, userId))
				 .flatMap(s -> {
					 if (!Utils.isNullOrEmpty(s.getId())) {
						 workDiaryActivityTransaction.update(s);
					 } else {
						 workDiaryActivityTransaction.save(s);
					 }
					 return Mono.just(Boolean.TRUE);
				 }).map(Utils::responseResult);
	}
						
	public EmpWorkDiaryActivityDBO convertDtoToDbo(EmpWorkDiaryActivityDTO dto, String userId) {
		EmpWorkDiaryActivityDBO dbo = new EmpWorkDiaryActivityDBO();
		BeanUtils.copyProperties(dto, dbo);
		dbo.setEmpWorkDiaryMainActivityDBO(new EmpWorkDiaryMainActivityDBO());
		dbo.getEmpWorkDiaryMainActivityDBO().setId(Integer.parseInt(dto.getMainActivity().getValue()));
		if(!dto.isForTeaching()) {
			dbo.setErpDepartmentDBO(new ErpDepartmentDBO());
			dbo.getErpDepartmentDBO().setId(Integer.parseInt(dto.getDepatment().getValue()));
		}
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		return dbo;
	}

	public Mono<EmpWorkDiaryActivityDTO> edit(int id) {
		return workDiaryActivityTransaction.edit(id).map(this::convertDboToDto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return workDiaryActivityTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}	
}
