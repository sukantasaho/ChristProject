package com.christ.erp.services.handlers.curriculum.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassVirtualClassMapDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.Classes.AcaClassVirtualClassMapDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaClassDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.curriculum.Classes.VirtualClassTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
@Service
public class VirtualClassHandler {

	@Autowired
	VirtualClassTransaction virtualClassTransaction;

	public Flux<AcaClassDTO> getProgrammeClassList(String academicYearId, String campusId, String levelId,String sessionGroupID) {
		List<Tuple> acaClassList = virtualClassTransaction.getProgrammeClassList(academicYearId,campusId,levelId,sessionGroupID);
		return this.convertDboToDto1(acaClassList);
	}

	private Flux<AcaClassDTO> convertDboToDto1(List<Tuple> acaClassList) {
		List<AcaClassDTO> acaClassDTOList = new ArrayList<AcaClassDTO>();
		Map<Integer,String> programmeBatchMap = new HashMap<Integer, String>();
		Map<Integer,String> programmeCampusMap = new HashMap<Integer, String>();
		Map<Integer,List<Tuple>> existMap = new HashMap<Integer, List<Tuple>>();
		Set<Integer> programmeIds = new HashSet<Integer>();
		if(!Utils.isNullOrEmpty(acaClassList)) {
			acaClassList.forEach(data -> {
				if(!Utils.isNullOrEmpty(data.get("aca_batch_id"))) {
					programmeBatchMap.put(Integer.parseInt(data.get("erp_programme_id").toString()),data.get("programme_name").toString());
					if(programmeIds.contains(Integer.parseInt(data.get("erp_programme_id").toString()))) {
						programmeIds.add(Integer.parseInt(data.get("erp_programme_id").toString()));
					}else {
						programmeIds.add(Integer.parseInt(data.get("erp_programme_id").toString()));
					}
				}
				if(Utils.isNullOrEmpty(data.get("aca_batch_id"))) {
					programmeCampusMap.put(Integer.parseInt(data.get("erp_programme_id").toString()),data.get("programme_name").toString());
					if(programmeIds.contains(Integer.parseInt(data.get("erp_programme_id").toString()))) {
						programmeIds.add(Integer.parseInt(data.get("erp_programme_id").toString()));
					}else {
						programmeIds.add(Integer.parseInt(data.get("erp_programme_id").toString()));
					}
				}
			});
		}
		if(!Utils.isNullOrEmpty(acaClassList)) {
			acaClassList.forEach(data -> {
				if(!Utils.isNullOrEmpty(data.get("erp_programme_id")) ==  programmeBatchMap.containsKey(data.get("erp_programme_id"))) {
					if(existMap.containsKey(data.get("erp_programme_id"))) {
						existMap.get(data.get("erp_programme_id")).add(data);	
					}else {
						List<Tuple> tupleList = new ArrayList<Tuple>();
						if(!Utils.isNullOrEmpty(tupleList)) {
							tupleList.clear();
						}
						tupleList.add(data);
						existMap.put(Integer.parseInt(data.get("erp_programme_id").toString()), tupleList);
					}
				}
				if(!Utils.isNullOrEmpty(data.get("erp_programme_id")) ==  programmeCampusMap.containsKey(data.get("erp_programme_id"))) {
					if(existMap.containsKey(data.get("erp_programme_id"))) {
						existMap.get(data.get("erp_programme_id")).add(data);	
					}else {
						List<Tuple> tupleList = new ArrayList<Tuple>();
						if(!Utils.isNullOrEmpty(tupleList)) {
							tupleList.clear();
						}
						tupleList.add(data);
						existMap.put(Integer.parseInt(data.get("erp_programme_id").toString()), tupleList);
					}
				}
			});
		}
		List<ErpProgrammeDTO> erpProgrammeDTOs = new ArrayList<ErpProgrammeDTO>();
		AcaClassDTO acaClassDTO = new AcaClassDTO();
		Map<Integer,Integer> erpProgrammeMap = new HashMap<Integer, Integer>();
		if(!Utils.isNullOrEmpty(programmeIds)) {
			if(!Utils.isNullOrEmpty(existMap)) {
				programmeIds.forEach(id -> {
					ErpProgrammeDTO erpProgrammeDTO = new ErpProgrammeDTO();
					if(existMap.containsKey(id)) {
						List<SelectDTO> classProgram = new ArrayList<SelectDTO>();
						List<Tuple> existList = existMap.get(id);
						if(!Utils.isNullOrEmpty(existList)) {
							existList.forEach(dbo -> {
								if(erpProgrammeMap.containsKey(Integer.parseInt(dbo.get("erp_programme_id").toString()))) {
									SelectDTO className1 = new SelectDTO();
									className1.setValue(dbo.get("aca_class_id").toString());
									className1.setLabel(dbo.get("class_Name").toString());
									classProgram.add(className1);
									erpProgrammeDTO.setProgrammeClassList(classProgram);
								}else {
									erpProgrammeMap.put(id, id);
									erpProgrammeDTO.setId(Integer.parseInt(dbo.get("erp_programme_id").toString()));
									erpProgrammeDTO.setProgrammeName(dbo.get("programme_name").toString());
									erpProgrammeDTOs.add(erpProgrammeDTO);
									SelectDTO className1 = new SelectDTO();
									className1.setValue(dbo.get("aca_class_id").toString());
									className1.setLabel(dbo.get("class_Name").toString());
									classProgram.add(className1);
									erpProgrammeDTO.setProgrammeClassList(classProgram);
									acaClassDTO.setErpProgrammeDTOList(erpProgrammeDTOs);
								}
							});
						}
					}
				});
			}
			acaClassDTOList.add(acaClassDTO);
		}
		return Flux.fromIterable(acaClassDTOList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<AcaClassDTO> dto, String userId) {
		return dto
				.handle((acaClassDTO, synchronousSink) -> {
					boolean istrue = virtualClassTransaction.duplicateCheck(acaClassDTO);
					if(istrue) {
						synchronousSink.error(new DuplicateException("Virtual class name is already exists"));
					}else {
						synchronousSink.next(acaClassDTO);
					}
				}).cast(AcaClassDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						virtualClassTransaction.update(s);
					}else {
						virtualClassTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private AcaClassDBO convertDtoToDbo(AcaClassDTO dto, String userId) {
		List<AcaClassDBO> list = new ArrayList<AcaClassDBO>();
		AcaClassDBO acaClassDBO = Utils.isNullOrEmpty(dto.getId()) ? new AcaClassDBO() : virtualClassTransaction.edit(dto.getId());
		if(Utils.isNullOrEmpty(dto.getId())) {
			acaClassDBO.setCreatedUsersId(Integer.parseInt(userId));
			acaClassDBO.setRecordStatus('A');
			acaClassDBO.setVirtualClass(true);
		}else {
			acaClassDBO.setModifiedUsersId(Integer.parseInt(userId));
		}
		if(!Utils.isNullOrEmpty(dto.getClassName())) {
			acaClassDBO.setClassName(dto.getClassName());
			acaClassDBO.setClassCode(dto.getClassName());
		}
		Integer durationId = virtualClassTransaction.getAcaDurationId(dto.getAcademicYear().getValue(), dto.getSessionGroup().getValue());
		if(!Utils.isNullOrEmpty(durationId)) {
			acaClassDBO.setAcaDurationDBO(new AcaDurationDBO());
			acaClassDBO.getAcaDurationDBO().setId(durationId);
		}
		Integer campusDepartmentId = virtualClassTransaction.getCampusDepartmentMapping(dto.getCampusSelect().getValue(), dto.getDepartmentSelect().getValue());
		if(!Utils.isNullOrEmpty(campusDepartmentId)) {
			acaClassDBO.setErpCampusDepartmentMappingDBO(new ErpCampusDepartmentMappingDBO());
			acaClassDBO.getErpCampusDepartmentMappingDBO().setId(campusDepartmentId);
		}
		Set<AcaClassVirtualClassMapDBO> existSubSet = !Utils.isNullOrEmpty(acaClassDBO) && !Utils.isNullOrEmpty(acaClassDBO.getAcaClassVirtualClassMapDBOSet()) ? acaClassDBO.getAcaClassVirtualClassMapDBOSet() : null;
		Map<Integer,AcaClassVirtualClassMapDBO> acaClassVirtualClassMapDBOMap = !Utils.isNullOrEmpty(existSubSet) ? existSubSet.stream().filter(p -> !Utils.isNullOrEmpty(p) && p.getRecordStatus() == 'A').collect(Collectors.toMap(s-> s.getId(),s -> s)) : null ;
		Map<Integer,AcaClassDBO> existMap = !Utils.isNullOrEmpty(existSubSet) ? existSubSet.stream().filter(p -> !Utils.isNullOrEmpty(p) && p.getRecordStatus() == 'A').collect(Collectors.toMap(s-> s.getAcaBaseClassDBO().getId(),s -> s.getAcaBaseClassDBO())) : null ;
		List<Integer> subIds = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(dto.getSelectedClassList())) {
			dto.getSelectedClassList().forEach(data -> {
				if(!Utils.isNullOrEmpty(data.getValue())) {
					subIds.add(Integer.parseInt(data.getValue()));
				}
			});
		}
		List<Integer> existIds = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(existSubSet)) {
			existSubSet.forEach(data -> {
				existIds.add(data.getAcaBaseClassDBO().getId());
			});
		}
		List<AcaClassDBO> acaClassDBOList = !Utils.isNullOrEmpty(subIds) ? virtualClassTransaction.getAcaClassDBOList(subIds) : null;
		Map<Integer,AcaClassDBO> acaClassMap = !Utils.isNullOrEmpty(acaClassDBOList) ? acaClassDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : null ;
		List<AcaClassDBO> dboList = new ArrayList<AcaClassDBO>();
		if(!Utils.isNullOrEmpty(acaClassDBOList)) {
			acaClassDBOList.forEach(data -> {
				data.setHavingVirtualClass(true);
				dboList.add(data);
			});
		}
		Map<Integer,Integer> countMap = new HashMap<Integer, Integer>();
		List<AcaClassVirtualClassMapDBO> list1 = !Utils.isNullOrEmpty(existIds) ? virtualClassTransaction.getAcaVirtualClassDBOList(existIds) : null;
		List<Integer> duplicateIds = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(list1)) {
			list1.forEach(data1-> {
				if(!Utils.isNullOrEmpty(data1.getAcaBaseClassDBO().getId())) {
					duplicateIds.add(data1.getAcaBaseClassDBO().getId());
				}
			});
		}
		if(!Utils.isNullOrEmpty(duplicateIds)) {
			for(Integer ids : duplicateIds) {
				Integer count = countMap.get(ids);
				if(count == null) {
					countMap.put(ids, 1);
				}else {
					countMap.put(ids, ++count);
				}
			}
		}
		Set<Entry<Integer, Integer>> entrySet = countMap.entrySet();
		for (Entry<Integer, Integer> entry : entrySet) {
			if(!Utils.isNullOrEmpty(subIds)) {
				subIds.forEach(data -> {
					if(data != entry.getKey()) {
						if(entry.getValue() == 1) {
							if(!Utils.isNullOrEmpty(existMap)) {
								if(existMap.containsKey(entry.getKey())) {
									AcaClassDBO acaClassDBO1 = existMap.get(entry.getKey());
									acaClassDBO1.setHavingVirtualClass(false);
									dboList.add(acaClassDBO1);
								}
							}
						}
					}
				});
			}
		}
		if(!Utils.isNullOrEmpty(dboList)) {
			virtualClassTransaction.saveList(dboList);
		}
		Set<AcaClassVirtualClassMapDBO> acaClassVirtualClassMapDBOSet = new HashSet<AcaClassVirtualClassMapDBO>();
		if(!Utils.isNullOrEmpty(dto.getAcaClassVirtualClassMapDTOList())) {
			dto.getAcaClassVirtualClassMapDTOList().forEach(dto1 -> {
				AcaClassVirtualClassMapDBO acaClassVirtualClassMapDBO = null;
				if(!Utils.isNullOrEmpty(acaClassVirtualClassMapDBOMap) && (acaClassVirtualClassMapDBOMap.containsKey(dto1.getId()))) {
					acaClassVirtualClassMapDBO = acaClassVirtualClassMapDBOMap.get(dto1.getId());
					acaClassVirtualClassMapDBO.setModifiedUsersId(Integer.parseInt(userId));
					acaClassVirtualClassMapDBOMap.remove(dto1.getId());
				}else {
					acaClassVirtualClassMapDBO = new AcaClassVirtualClassMapDBO();
					acaClassVirtualClassMapDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				acaClassVirtualClassMapDBO.setRecordStatus('A');
				acaClassVirtualClassMapDBO.setAcaVirtualClassDBO(acaClassDBO);
				if(!Utils.isNullOrEmpty(acaClassMap)) {
					AcaClassVirtualClassMapDBO subDbo = acaClassVirtualClassMapDBO;
					if(!Utils.isNullOrEmpty(dto1)) {
						AcaClassDBO acaBasAcaClassDBO = null;
						if(acaClassMap.containsKey(dto1.getBaseClassId())) {
							acaBasAcaClassDBO = acaClassMap.get(dto1.getBaseClassId());
							subDbo.setAcaBaseClassDBO(acaBasAcaClassDBO);
							subDbo.getAcaBaseClassDBO().setId(acaBasAcaClassDBO.getId());
							acaClassMap.remove(dto1.getBaseClassId());
							acaClassVirtualClassMapDBOSet.add(subDbo);
						}
					}
				}
			});
		}
		if(!Utils.isNullOrEmpty(acaClassVirtualClassMapDBOMap)) {
			acaClassVirtualClassMapDBOMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				acaClassVirtualClassMapDBOSet.add(value);
			});
		}
		acaClassDBO.setAcaClassVirtualClassMapDBOSet(acaClassVirtualClassMapDBOSet);
		return acaClassDBO;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		List<AcaClassDBO> dboList = new ArrayList<AcaClassDBO>();
		AcaClassDBO acaClassDBO = virtualClassTransaction.edit(id);
		if(!Utils.isNullOrEmpty(acaClassDBO)) {
			List<Integer> subIds = new ArrayList<Integer>();
			if(!Utils.isNullOrEmpty(acaClassDBO.getAcaClassVirtualClassMapDBOSet())) {
				acaClassDBO.getAcaClassVirtualClassMapDBOSet().forEach(data -> {
					if(data.getRecordStatus() == 'A') {
						subIds.add(data.getAcaBaseClassDBO().getId());
					}
				});
			}
			List<AcaClassDBO> acaVirtualClassDBOList = !Utils.isNullOrEmpty(subIds) ? virtualClassTransaction.getAcaClassDBOList(subIds) : null;
			Map<Integer,AcaClassDBO> acaClassMap = !Utils.isNullOrEmpty(acaVirtualClassDBOList) ? acaVirtualClassDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : null ;
			Map<Integer,Integer> countMap = new HashMap<Integer, Integer>();
			List<AcaClassVirtualClassMapDBO> list = virtualClassTransaction.getAcaVirtualClassDBOList(subIds);
			List<Integer> duplicateIds = new ArrayList<Integer>();
			if(!Utils.isNullOrEmpty(list)) {
				list.forEach(data1-> {
					if(!Utils.isNullOrEmpty(data1.getAcaBaseClassDBO().getId())) {
						duplicateIds.add(data1.getAcaBaseClassDBO().getId());
					}
				});
			}
			if(!Utils.isNullOrEmpty(duplicateIds)) {
				for(Integer ids : duplicateIds) {
					Integer count = countMap.get(ids);
					if(count == null) {
						countMap.put(ids, 1);
					}else {
						countMap.put(ids, ++count);
					}
				}
			}
			Set<Entry<Integer, Integer>> entrySet = countMap.entrySet();
			for (Entry<Integer, Integer> entry : entrySet) {
				if (entry.getValue() == 1) {
					AcaClassDBO acaClassDBO1 = acaClassMap.get(entry.getKey());
					acaClassDBO.setHavingVirtualClass(false);
					dboList.add(acaClassDBO);
				}
			}
			if(!Utils.isNullOrEmpty(dboList)) {
				virtualClassTransaction.saveList(dboList);
			}
		}
		return virtualClassTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Mono<AcaClassDTO> edit(int id) {
		AcaClassDBO acaClassDbo = virtualClassTransaction.edit(id);
		return convertDboToDto(acaClassDbo);
	}

	private Mono<AcaClassDTO> convertDboToDto(AcaClassDBO acaClassDbo) {
		AcaClassDTO dto = new AcaClassDTO();
		List<SelectDTO> selectDto = new ArrayList<SelectDTO>();
		List<ErpProgrammeDTO> erpProgrammeDtoList = new ArrayList<ErpProgrammeDTO>();
		Map<Integer,List<AcaClassDBO>> existMap = new HashMap<Integer, List<AcaClassDBO>>();
		List<AcaClassVirtualClassMapDTO> acaClassVirtualClassMapDTOList = new ArrayList<AcaClassVirtualClassMapDTO>();
		dto.setId(acaClassDbo.getId());
		dto.setClassName(acaClassDbo.getClassName());
		dto.setClassCode(acaClassDbo.getClassCode());
		if(!Utils.isNullOrEmpty(acaClassDbo)) {
			BeanUtils.copyProperties(acaClassDbo, dto);
			dto.setAcaDurationDTO(new AcaDurationDTO());
			dto.getAcaDurationDTO().setId(acaClassDbo.getAcaDurationDBO().getId());
			dto.setSessionGroup(new SelectDTO());
			dto.getSessionGroup().setValue(String.valueOf(acaClassDbo.getAcaDurationDBO().getAcaSessionGroupDBO().getId()));
			dto.getSessionGroup().setLabel(acaClassDbo.getAcaDurationDBO().getAcaSessionGroupDBO().getSessionGroupName());
			dto.setAcademicYear(new SelectDTO());
			dto.getAcademicYear().setValue(String.valueOf(acaClassDbo.getAcaDurationDBO().getErpAcademicYearDBO().getId()));
			dto.getAcademicYear().setLabel(acaClassDbo.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
			dto.setCampusSelect(new SelectDTO());
			dto.getCampusSelect().setValue(String.valueOf(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getId()));
			dto.getCampusSelect().setLabel(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
			dto.setDepartmentSelect(new SelectDTO());
			dto.getDepartmentSelect().setValue(String.valueOf(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getId()));
			dto.getDepartmentSelect().setLabel(acaClassDbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName());
			if(!Utils.isNullOrEmpty(acaClassDbo.getAcaClassVirtualClassMapDBOSet())) {
				acaClassDbo.getAcaClassVirtualClassMapDBOSet().forEach(data1 -> {
					if(data1.getRecordStatus()=='A') {
						SelectDTO dto1 = new SelectDTO();
						dto1.setValue(String.valueOf(data1.getAcaBaseClassDBO().getId()));
						dto1.setLabel(data1.getAcaBaseClassDBO().getClassName());
						AcaClassVirtualClassMapDTO acaClassVirtualClassMapDTO = new AcaClassVirtualClassMapDTO();
						acaClassVirtualClassMapDTO.setId(data1.getId());
						acaClassVirtualClassMapDTO.setBaseClassId(data1.getAcaBaseClassDBO().getId());
						acaClassVirtualClassMapDTO.setBaseClassName(data1.getAcaBaseClassDBO().getClassName());
						acaClassVirtualClassMapDTO.setRecordStatus(data1.getRecordStatus());
						acaClassVirtualClassMapDTOList.add(acaClassVirtualClassMapDTO);
						selectDto.add(dto1);
					}
				});
				dto.setSelectedClassList(selectDto);
			}
			dto.setAcaClassVirtualClassMapDTOList(acaClassVirtualClassMapDTOList);
			List<Integer> baseClassIds = !Utils.isNullOrEmpty(acaClassDbo.getAcaClassVirtualClassMapDBOSet()) ? acaClassDbo.getAcaClassVirtualClassMapDBOSet().stream().filter(p ->p.getRecordStatus()=='A')
					.map(s-> s.getAcaBaseClassDBO().getId()).collect(Collectors.toList()) : null;
			List<AcaClassDBO> dboList = !Utils.isNullOrEmpty(baseClassIds) ? virtualClassTransaction.getAcaClassDBOList(baseClassIds) : null;
			Map<Integer,Integer> programmeMap = new HashMap<Integer, Integer>();
			List<ErpProgrammeDTO> programmeList = new ArrayList<ErpProgrammeDTO>();
			List<SelectDTO> classList = new ArrayList<SelectDTO>();
			List<String> classNameStrrList = new ArrayList<String>();
			Set<Integer> programmeIds = new HashSet<Integer>();
			if(!Utils.isNullOrEmpty(dboList)) {
				dboList.forEach(data1 -> {
					if(!Utils.isNullOrEmpty(data1.getAcaDurationDetailDBO().getAcaBatchDBO())) {
						if(programmeMap.containsKey(data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
							programmeMap.put(data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(), data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}else {
							programmeMap.put(data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(), data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}
						if(programmeIds.contains(data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
							programmeIds.add(data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}else {
							programmeIds.add(data1.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}
					} else if(!Utils.isNullOrEmpty(data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO())) {
						if(programmeMap.containsKey(data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
							programmeMap.put(data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(),data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}else {
							programmeMap.put(data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(),data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}
						if(programmeIds.contains(data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
							programmeIds.add(data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}else {
							programmeIds.add(data1.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
						}
					}
				});
			}
			List<ErpProgrammeDTO> erpProgrammelist = new ArrayList<ErpProgrammeDTO>();
			if(!Utils.isNullOrEmpty(programmeMap)) {
				if(!Utils.isNullOrEmpty(dboList)) {
					dboList.forEach(data -> {
						if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getAcaBatchDBO())) {
							if(existMap.containsKey(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
								existMap.get(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId()).add(data);	
							}else {
								List<AcaClassDBO> tupleList = new ArrayList<AcaClassDBO>();
								if(!Utils.isNullOrEmpty(tupleList)) {
									tupleList.clear();
								}
								tupleList.add(data);
								existMap.put(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(),tupleList);	
							}
						}
						if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO())) {
							if(existMap.containsKey(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
								existMap.get(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId()).add(data);	
							}else {
								List<AcaClassDBO> tupleList = new ArrayList<AcaClassDBO>();
								if(!Utils.isNullOrEmpty(tupleList)) {
									tupleList.clear();
								}
								tupleList.add(data);
								existMap.put(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(),tupleList);	
							}
						}
					});
				}
			}
			Map<Integer,Integer> existProgrammIds = new HashMap<Integer, Integer>();
			Map<Integer,Integer> existProgrammIds1 = new HashMap<Integer, Integer>();
			if(!Utils.isNullOrEmpty(existMap)){
				programmeIds.forEach(ids -> {
					ErpProgrammeDTO erpProgrammeDTO = new ErpProgrammeDTO();
					if(existMap.containsKey(ids)) {
						List<AcaClassDBO> list = existMap.get(ids);
						if(!Utils.isNullOrEmpty(list)) {
							list.forEach(data -> {
								if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getAcaBatchDBO())) {
									if(existProgrammIds.containsKey(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
										SelectDTO select = new SelectDTO();
										classNameStrrList.add(data.getClassName());
										String classList1 = classNameStrrList.stream().collect(Collectors.joining(", "));
										select.setLabel(classList1);
										erpProgrammeDTO.setProgrammeClasses(select);
									} else {
										erpProgrammeDTO.setId(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
										erpProgrammeDTO.setProgrammeName(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
										SelectDTO select = new SelectDTO();
										classNameStrrList.clear();
										classNameStrrList.add(data.getClassName());
										String classList1 = classNameStrrList.stream().collect(Collectors.joining(", "));
										select.setLabel(classList1);
										erpProgrammeDTO.setProgrammeClasses(select);
										erpProgrammeDTO.setProgrammeClasses(select);
										erpProgrammelist.add(erpProgrammeDTO);
										existProgrammIds.put(data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(), data.getAcaDurationDetailDBO().getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
										dto.setErpProgrammeDTOList(erpProgrammelist);
									}
								}
								if(!Utils.isNullOrEmpty(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO())) {
									if(existProgrammIds1.containsKey(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId())) {
										SelectDTO select = new SelectDTO();
										classNameStrrList.add(data.getClassName());
										String classList1 = classNameStrrList.stream().collect(Collectors.joining(", "));
										select.setLabel(classList1);
										erpProgrammeDTO.setProgrammeClasses(select);
									} else {
										erpProgrammeDTO.setId(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
										erpProgrammeDTO.setProgrammeName(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
										SelectDTO select = new SelectDTO();
										classNameStrrList.clear();
										classNameStrrList.add(data.getClassName());
										String classList1 = classNameStrrList.stream().collect(Collectors.joining(", "));
										select.setLabel(classList1);
										erpProgrammeDTO.setProgrammeClasses(select);
										erpProgrammeDTO.setProgrammeClasses(select);
										erpProgrammelist.add(erpProgrammeDTO);
										existProgrammIds1.put(data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId(), data.getAcaDurationDetailDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId());
										dto.setErpProgrammeDTOList(erpProgrammelist);
									}
								}
							});
						}
					}  
				});
			}
		}
		return Mono.just(dto);
	}

	public Flux<AcaClassDTO> getGridData(String academicYearId, String campusId) {
		List<AcaClassDBO> list = virtualClassTransaction.getGridData(academicYearId,campusId);
		return this.convertDboToDto(list);
	}

	private Flux<AcaClassDTO> convertDboToDto(List<AcaClassDBO> list) {
		List<AcaClassDTO> acaClassDtoList = new ArrayList<AcaClassDTO>();
		List<AcaClassVirtualClassMapDTO> acaClassVirtualClassMapDTOList = new ArrayList<AcaClassVirtualClassMapDTO>();
		Map<Integer,AcaClassDBO> acaClassMap = !Utils.isNullOrEmpty(list) ? list.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : null ;
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				AcaClassDTO acaClassDTO = new AcaClassDTO();
				acaClassDTO.setId(data.getId());
				acaClassDTO.setClassCode(data.getClassCode());
				acaClassDTO.setClassName(data.getClassName());
				if(!Utils.isNullOrEmpty(acaClassMap)) {
					if(acaClassMap.containsKey(data.getId())){
						AcaClassDBO acaClassDBO = acaClassMap.get(data.getId());
						if(!Utils.isNullOrEmpty(acaClassDBO.getAcaClassVirtualClassMapDBOSet())) {
							List<SelectDTO> selectClass = new ArrayList<SelectDTO>();
							acaClassDBO.getAcaClassVirtualClassMapDBOSet().forEach(data1 -> {
								if(data1.getRecordStatus() == 'A'){
									if(data1.getAcaVirtualClassDBO().getId() == data.getId()) {
										SelectDTO selectDTO = new SelectDTO();
										selectDTO.setValue(String.valueOf(data1.getAcaBaseClassDBO().getId()));
										selectDTO.setLabel(data1.getAcaBaseClassDBO().getClassName());
										selectClass.add(selectDTO);
									}
								}
							});
							acaClassDTO.setSelectedClassList(selectClass);
						}
					}
				}
				acaClassDtoList.add(acaClassDTO);
			});
		}
		return Flux.fromIterable(acaClassDtoList);
	}
}