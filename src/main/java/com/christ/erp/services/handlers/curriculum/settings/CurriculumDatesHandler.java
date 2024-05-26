package com.christ.erp.services.handlers.curriculum.settings;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaBatchDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDetailDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.transactions.curriculum.settings.CurriculumDatesTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CurriculumDatesHandler {

	@Autowired
	CurriculumDatesTransaction curriculumDatesTransaction;

	public Flux<SelectDTO> getErpCampus(Integer academicYearId, List<String> sessionIdList) {
		var sessionList = sessionIdList.stream().map(Integer::parseInt).collect(Collectors.toList());
		List<Integer> campusList = null;
		List<AcaDurationDetailDBO> durationList = curriculumDatesTransaction.getAcaDurationdetail(academicYearId,sessionList,campusList);
		var campusSet =durationList.stream().map(s-> !Utils.isNullOrEmpty(s.getErpCampusProgrammeMappingDBO())? 
				s.getErpCampusProgrammeMappingDBO().getErpCampusDBO():s.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO())
				.collect(Collectors.toSet());
		return Mono.just(campusSet).flatMapMany(Flux::fromIterable).map(this::convertDurationDetailToCampusDto);
	}

	public SelectDTO convertDurationDetailToCampusDto(ErpCampusDBO dbo) {
		var dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setLabel(String.valueOf(dbo.getId()));
			dto.setValue(dbo.getCampusName());
		}
		return dto;
	}

	public Flux<AcaDurationDetailDTO> getAcaDurationdetail(Integer academicYearId, List<String> sessionIdList, List<String> campusIdList) {
		var sessionList = sessionIdList.stream().map(Integer::parseInt).collect(Collectors.toList());
		var campusList = campusIdList.stream().map(Integer::parseInt).collect(Collectors.toList());
		var durationDetailList = Mono.just(curriculumDatesTransaction.getAcaDurationdetail(academicYearId,sessionList,campusList));
		return durationDetailList.flatMapMany(Flux::fromIterable).map(this::convertAdmissionYearDboToDto);
	}

	public AcaDurationDetailDTO convertAdmissionYearDboToDto(AcaDurationDetailDBO dbo) {
		var dto = new AcaDurationDetailDTO();
		BeanUtils.copyProperties(dbo, dto);	
		dto.setAcaSessionDTO(new AcaSessionDTO());
		if(!Utils.isNullOrEmpty(dbo.getAcaSessionDBO())) {
			dto.getAcaSessionDTO().setSessionName(dbo.getAcaSessionDBO().getSessionName());
		}
		dto.setErpCampusProgrammeMappingDTO(new ErpCampusProgrammeMappingDTO());
		dto.getErpCampusProgrammeMappingDTO().setErpProgrammeDTO(new ErpProgrammeDTO());
		dto.getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().setErpDeanery(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getAcaBatchDBO())) {
			dto.setAcaBatchDTO(new AcaBatchDTO());
			dto.getAcaBatchDTO().setBatchName(dbo.getAcaBatchDBO().getBatchName());
			if(!Utils.isNullOrEmpty(dbo.getAcaBatchDBO().getErpCampusProgrammeMappingDBO())) {				
				if(!Utils.isNullOrEmpty(dbo.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO())) {
					dto.getErpCampusProgrammeMappingDTO().setCampusName(dbo.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
				}
				if(!Utils.isNullOrEmpty(dbo.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO())) {
					dto.getErpCampusProgrammeMappingDTO().setProgramName(dbo.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
					if(!Utils.isNullOrEmpty(dbo.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpDeaneryDBO())) {
						dto.getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().getErpDeanery().setLabel(dbo.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpDeaneryDBO().getDeaneryName());					
					}
				}
			}
		} else if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO())) {			
			if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO())) {
				dto.getErpCampusProgrammeMappingDTO().setCampusName(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
			}			
			if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO())) {
				dto.getErpCampusProgrammeMappingDTO().setProgramName(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpDeaneryDBO())) {
					dto.getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().getErpDeanery().setLabel(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpDeaneryDBO().getDeaneryName());
				}
			}						
		}		
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> updateAcaDurationdetail(Mono<List<AcaDurationDetailDTO>> dto, String userId) {
		return dto.map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
			curriculumDatesTransaction.updateDurationList(s);
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	private List<AcaDurationDetailDBO> convertDtoToDbo(List<AcaDurationDetailDTO> data, String userId) {		
		var dboList = curriculumDatesTransaction.getAcaDurationDetailByIds(data.stream().map(s->s.getId()).collect(Collectors.toList()));
		var map = dboList.stream().collect(Collectors.toMap(s->s.getId(), s->s));
		var list =new LinkedList<AcaDurationDetailDBO>();
		data.forEach(p-> {
			var acaDurationDetailDBO = map.get(p.getId());
			acaDurationDetailDBO.setSessionStartDate(p.getSessionStartDate());
			acaDurationDetailDBO.setSessionEndDate(p.getSessionEndDate());
			acaDurationDetailDBO.setSessionFirstInstructionDate(p.getSessionFirstInstructionDate());
			acaDurationDetailDBO.setSessionLastInstructionDate(p.getSessionLastInstructionDate());
			acaDurationDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
			list.add(acaDurationDetailDBO);
		});	
		return list;
	}
}
