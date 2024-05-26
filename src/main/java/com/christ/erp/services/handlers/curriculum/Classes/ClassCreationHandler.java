package com.christ.erp.services.handlers.curriculum.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpRoomsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ErpCampusDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaBatchDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaClassDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDetailDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.transactions.curriculum.Classes.ClassCreationTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@Service
public class ClassCreationHandler {

	@Autowired
	ClassCreationTransaction classCreationTransaction;

	public Flux<AcaClassDTO> getGridData(String academicYearId, String campusId, String sessionType) {
		List<AcaClassDBO> list = classCreationTransaction.getGridData(academicYearId, campusId, sessionType);
		return this.convertDboToDto(list);
	}

	private Flux<AcaClassDTO> convertDboToDto(List<AcaClassDBO> list) {
		List<AcaClassDTO> listDTO = new ArrayList<AcaClassDTO>();
		list.forEach(data -> {
			AcaClassDTO acaClassDTO = new AcaClassDTO();
			acaClassDTO.setId(data.getId());
			if(!Utils.isNullOrEmpty(data.getClassCode())) {
				acaClassDTO.setClassCode(data.getClassCode());
			}
			if(!Utils.isNullOrEmpty(data.getClassName())) {
				acaClassDTO.setClassName(data.getClassName());
			}
			if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getAcaDurationDBO().getErpAcademicYearDBO())) {
				acaClassDTO.setAcademicYear(new SelectDTO());
				acaClassDTO.getAcademicYear().setValue(String.valueOf(data.getAcaDurationDetailDBO().getAcaDurationDBO().getErpAcademicYearDBO().getId()));
				acaClassDTO.getAcademicYear().setLabel(data.getAcaDurationDetailDBO().getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
			}
			if(!Utils.isNullOrEmpty(data.getErpRoomsDBO())) {
				acaClassDTO.setErpRooms(new SelectDTO());
				acaClassDTO.getErpRooms().setValue(String.valueOf(data.getErpRoomsDBO().getId()));
				acaClassDTO.getErpRooms().setLabel(String.valueOf(data.getErpRoomsDBO().getRoomNo()));
			} 
			if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getAcaSessionDBO())) {
				acaClassDTO.setSessionDTO(new AcaSessionDTO());
				acaClassDTO.getSessionDTO().setTermNumber(data.getAcaDurationDetailDBO().getAcaSessionDBO().getTermNumber());
			}
			if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getAcaBatchDBO())) {
				acaClassDTO.setErpProgrammeDTO(new ErpProgrammeDTO());
				acaClassDTO.getErpProgrammeDTO().setId(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
				acaClassDTO.getErpProgrammeDTO().setProgrammeName(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				acaClassDTO.getErpProgrammeDTO().setProgrammeCode(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeCode());
			}
			if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO())) {
				acaClassDTO.setErpProgrammeDTO(new ErpProgrammeDTO());
				acaClassDTO.getErpProgrammeDTO().setId(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
				acaClassDTO.getErpProgrammeDTO().setProgrammeCode(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeCode());
				acaClassDTO.getErpProgrammeDTO().setProgrammeName(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			}
			if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getAcaSessionDBO())) {
				acaClassDTO.setSessionDTO(new AcaSessionDTO());
				acaClassDTO.getSessionDTO().setSessionName(data.getAcaDurationDetailDBO().getAcaSessionDBO().getSessionName());
			}
			listDTO.add(acaClassDTO);
		});
		return Flux.fromIterable(listDTO);
	}

	public Mono<ApiResult> saveOrUpdate(Mono<List<AcaClassDTO>> dto, String userId) {
		return dto
				.handle((acaClassDTO, synchronousSink) -> {
					synchronousSink.next(acaClassDTO);
				}).cast(ArrayList.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if(!s.isEmpty()) {
						classCreationTransaction.saveOrUpdate(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<AcaClassDBO> convertDtoToDbo(List<AcaClassDTO> dtos, String userId) {
		List<Integer> ids = new ArrayList<Integer>();
		dtos.forEach(classDto -> {
			ids.add(classDto.getId());
		});
		List<AcaClassDBO> classDboList = classCreationTransaction.edit1(ids);
		Map<Integer, AcaClassDBO> classMap = new HashMap<Integer, AcaClassDBO>();
		classDboList.forEach(exist -> {
			classMap.put(exist.getId(), exist);
		});
		List<AcaClassDBO> list = new ArrayList<AcaClassDBO>();	
		dtos.forEach(dto -> {
			AcaClassDBO acaClassDBO = null;
			if(classMap.containsKey(dto.getId())) {
				acaClassDBO = classMap.get(dto.getId());
				acaClassDBO.setModifiedUsersId(Integer.parseInt(userId));
			} else {
				acaClassDBO = new AcaClassDBO();
				acaClassDBO.setCreatedUsersId(Integer.parseInt(userId));
			}
			if(!Utils.isNullOrEmpty(dto.getClassName())) {
				acaClassDBO.setClassName(dto.getClassName());
				acaClassDBO.setClassCode(dto.getClassName());
			}
			if(!Utils.isNullOrEmpty(dto.getSection())) {
				acaClassDBO.setSection(dto.getSection());
			}
			if(!Utils.isNullOrEmpty(dto.getCampusCode())) {
				acaClassDBO.setCampusCode(dto.getCampusCode());
			}
			if(!Utils.isNullOrEmpty(dto.getErpRooms())) {
				acaClassDBO.setErpRoomsDBO(new ErpRoomsDBO());
				acaClassDBO.getErpRoomsDBO().setId(Integer.valueOf(dto.getErpRooms().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getAcaDurationDetailDTO())) {
				acaClassDBO.setAcaDurationDetailDBO(new AcaDurationDetailDBO());
				acaClassDBO.getAcaDurationDetailDBO().setId(Integer.parseInt(dto.getAcaDurationDetailDTO().getValue()));
			}
			Integer campusDepartmentId = classCreationTransaction.getCampusDepartmentMapping(dto.getCampusSelect().getValue(), dto.getDepartmentSelect().getValue());
			if(!Utils.isNullOrEmpty(campusDepartmentId)) {
				acaClassDBO.setErpCampusDepartmentMappingDBO(new ErpCampusDepartmentMappingDBO());
				acaClassDBO.getErpCampusDepartmentMappingDBO().setId(campusDepartmentId);
			}
			acaClassDBO.setRecordStatus('A');
			list.add(acaClassDBO);
		});
		return list;
	}

	public Mono<AcaClassDTO> edit(int id) {
		AcaClassDBO acaClassDbo = classCreationTransaction.edit(id);
		return convertDboToDto(acaClassDbo);
	}

	private Mono<AcaClassDTO> convertDboToDto(AcaClassDBO acaClassDbo) {
		AcaClassDTO dto = new AcaClassDTO();
		if(!Utils.isNullOrEmpty(acaClassDbo)) {
			dto.setId(acaClassDbo.getId());
			if(!Utils.isNullOrEmpty(acaClassDbo.getClassName())) {
				dto.setClassName(acaClassDbo.getClassName());
				dto.setClassCode(acaClassDbo.getClassCode());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getCampusCode())) {
				dto.setCampusCode(acaClassDbo.getCampusCode());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getSection())) {
				dto.setSection(acaClassDbo.getSection());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getErpRoomsDBO())) {
				dto.setErpRooms(new SelectDTO());
				dto.getErpRooms().setValue(String.valueOf(acaClassDbo.getErpRoomsDBO().getId()));
				dto.getErpRooms().setLabel(String.valueOf(acaClassDbo.getErpRoomsDBO().getRoomNo()));
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getAcaDurationDetailDBO())) {
				dto.setAcaDurationDetailDTO(new SelectDTO());
				dto.getAcaDurationDetailDTO().setValue(String.valueOf(acaClassDbo.getAcaDurationDetailDBO().getId()));
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getAcaDurationDetailDBO().getAcaDurationDBO())) {
				dto.setAcademicYear(new SelectDTO());
				dto.getAcademicYear().setValue(String.valueOf(acaClassDbo.getAcaDurationDetailDBO().getAcaDurationDBO().getErpAcademicYearDBO().getId()));
				dto.getAcademicYear().setLabel(acaClassDbo.getAcaDurationDetailDBO().getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getAcaDurationDetailDBO().getAcaSessionDBO())) {
				dto.setSessionDTO(new AcaSessionDTO());
				dto.getSessionDTO().setTermNumber(acaClassDbo.getAcaDurationDetailDBO().getAcaSessionDBO().getTermNumber());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getErpCampusDepartmentMappingDBO())) {
				dto.setDepartmentSelect(new SelectDTO());
				dto.getDepartmentSelect().setValue(String.valueOf(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getId()));
				dto.getDepartmentSelect().setLabel(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName());
				dto.setCampusSelect(new SelectDTO());
				dto.getCampusSelect().setValue(String.valueOf(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getId()));
				dto.getCampusSelect().setLabel(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getAcaDurationDetailDBO().getAcaBatchDBO())) {
				dto.setErpProgrammeDTO(new ErpProgrammeDTO());
				dto.getErpProgrammeDTO().setId(acaClassDbo.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
				dto.getErpProgrammeDTO().setProgrammeName(acaClassDbo.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				dto.getErpProgrammeDTO().setProgrammeCode(acaClassDbo.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeCode());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getAcaDurationDetailDBO().getAcaSessionDBO())) {
				dto.setSessionDTO(new AcaSessionDTO());
				dto.getSessionDTO().setSessionName(acaClassDbo.getAcaDurationDetailDBO().getAcaSessionDBO().getSessionName());
			}
			if(!Utils.isNullOrEmpty(acaClassDbo.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO())) {
				dto.setErpProgrammeDTO(new ErpProgrammeDTO());
				dto.getErpProgrammeDTO().setId(acaClassDbo.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
				dto.getErpProgrammeDTO().setProgrammeCode(acaClassDbo.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeCode());
				dto.getErpProgrammeDTO().setProgrammeName(acaClassDbo.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			}
		}
		return Mono.just(dto);
	}

	public Mono<ApiResult> delete(int id, String userId) {
		return classCreationTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Flux<SelectDTO> getBatchName(String batchYearId, String campusId, String programId) {
		return classCreationTransaction.getBatchName(batchYearId, campusId, programId).flatMapMany(Flux::fromIterable).map(this::convertBatchDboToDto);
	}

	public SelectDTO convertBatchDboToDto(Tuple dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.get("aca_batch_id")));
		dto.setLabel(dbo.get("batch_name").toString().concat("(" + dbo.get("intake").toString()+ ")"));
		return dto;
	}

	public Flux<AcaDurationDetailDTO> getDurationDetailForSubmission(String yearId, String campusId, String levelId, String typeId) {
		List<Tuple> list = classCreationTransaction.getDurationDetailForSubmission(campusId, yearId, typeId, levelId);
		return this.convertDurationDboToDto(list);
	}

	private Flux<AcaDurationDetailDTO> convertDurationDboToDto(List<Tuple> list) {
		List<AcaDurationDetailDTO> dtoList = new ArrayList<AcaDurationDetailDTO>();
		list.forEach(data -> {
			AcaDurationDetailDTO dto = new AcaDurationDetailDTO();
			dto.setId(Integer.parseInt(data.get("aca_duration_detail_id").toString()));
			if(!Utils.isNullOrEmpty(data.get("erp_programme_id"))) { 
				dto.setErpCampusProgrammeMappingDTO(new ErpCampusProgrammeMappingDTO());
				dto.getErpCampusProgrammeMappingDTO().setErpProgrammeDTO(new ErpProgrammeDTO());
				dto.getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().setId(Integer.parseInt(data.get("erp_programme_id").toString()));
				dto.getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().setProgrammeName(data.get("programme_name").toString());
				dto.getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().setProgrammeCode(data.get("programme_code").toString());
				dto.getErpCampusProgrammeMappingDTO().setCampusDTO(new ErpCampusDTO());
				dto.getErpCampusProgrammeMappingDTO().getCampusDTO().setShortName(data.get("campusCode").toString());
			}
			if(!Utils.isNullOrEmpty(data.get("erp_academic_year_id"))) {
				dto.setAcaDurationDTO(new AcaDurationDTO());
				dto.getAcaDurationDTO().setErpAcademicYearDTO(new ErpAcademicYearDTO());
				dto.getAcaDurationDTO().getErpAcademicYearDTO().setId(Integer.parseInt(data.get("erp_academic_year_id").toString()));
				dto.getAcaDurationDTO().getErpAcademicYearDTO().setAcademicYear(Integer.parseInt(data.get("academic_year").toString()));
			}
			dto.setAcaSessionDTO(new AcaSessionDTO());
			dto.getAcaSessionDTO().setSessionName(data.get("session").toString());
			dtoList.add(dto);
		});
		return Flux.fromIterable(dtoList);
	}

	public Flux<AcaDurationDetailDTO> getDurationDetailForTerm(String batchId) {
		List<AcaDurationDetailDBO> list = classCreationTransaction.getDurationDetailForTerm(batchId);
		return this.convertTermDboToDto(list);
	}

	private Flux<AcaDurationDetailDTO> convertTermDboToDto(List<AcaDurationDetailDBO> dbo) {
		List<AcaDurationDetailDTO> dtoList = new ArrayList<AcaDurationDetailDTO>();
		dbo.forEach(data -> {
			AcaDurationDetailDTO dto = new AcaDurationDetailDTO();
			dto.setId(data.getId());
			if(!Utils.isNullOrEmpty(data.getAcaBatchDBO().getErpCampusProgrammeMappingDBO())) {
				dto.setAcaBatchDTO(new AcaBatchDTO());
				dto.getAcaBatchDTO().setErpCampusProgrammeMappingDTO(new ErpCampusProgrammeMappingDTO());
				dto.getAcaBatchDTO().getErpCampusProgrammeMappingDTO().setCampusDTO(new ErpCampusDTO());
				dto.getAcaBatchDTO().getErpCampusProgrammeMappingDTO().getCampusDTO().setCampusId(String.valueOf(data.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId()));
				dto.getAcaBatchDTO().getErpCampusProgrammeMappingDTO().getCampusDTO().setShortName(data.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getShortName());
				dto.getAcaBatchDTO().getErpCampusProgrammeMappingDTO().setErpProgrammeDTO(new ErpProgrammeDTO());
				dto.getAcaBatchDTO().getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().setId(data.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
				dto.getAcaBatchDTO().getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().setProgrammeName(data.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				dto.getAcaBatchDTO().getErpCampusProgrammeMappingDTO().getErpProgrammeDTO().setProgrammeCode(data.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeCode());
			}
			if(!Utils.isNullOrEmpty(data.getAcaDurationDBO().getErpAcademicYearDBO())) {
				dto.setAcaDurationDTO(new AcaDurationDTO());
				dto.getAcaDurationDTO().setErpAcademicYearDTO(new ErpAcademicYearDTO());
				dto.getAcaDurationDTO().getErpAcademicYearDTO().setId(data.getAcaDurationDBO().getErpAcademicYearDBO().getId());
				dto.getAcaDurationDTO().getErpAcademicYearDTO().setAcademicYear(data.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYear());
			}
			if(!Utils.isNullOrEmpty(data.getAcaSessionDBO())) {
				dto.setAcaSessionDTO(new AcaSessionDTO());
				dto.getAcaSessionDTO().setTermNumber(data.getAcaSessionDBO().getTermNumber());	
			}
			dtoList.add(dto);
		});
		return Flux.fromIterable(dtoList);
	}

	public  Mono<ApiResult> isClassCreated(String yearId, String campusCode, String section, String programId) {
		var apiResult = new ApiResult();
		var classDbo = classCreationTransaction.isClassCreated(yearId, campusCode, section, programId);
		if(!Utils.isNullOrEmpty(classDbo)) {
			apiResult.setFailureMessage("Class Already Created for section" + classDbo.getSection());
		} else {
			apiResult.setSuccess(true);		
		}
		return Mono.just(apiResult);	
	}
}
