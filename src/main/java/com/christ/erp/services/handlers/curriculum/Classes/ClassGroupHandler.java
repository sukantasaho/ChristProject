package com.christ.erp.services.handlers.curriculum.Classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.AcaCourseDBO;
import com.christ.erp.services.dbobjects.common.AcaCourseSessionwiseDBO;
import com.christ.erp.services.dbobjects.common.AcaScriptsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassGroupDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassGroupDetailsDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassGroupStudentsDBO;
import com.christ.erp.services.dbobjects.curriculum.common.AttActivityDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.Classes.AcaClassGroupDTO;
import com.christ.erp.services.dto.curriculum.Classes.AcaClassGroupDetailsDTO;
import com.christ.erp.services.dto.curriculum.Classes.AcaClassGroupStudentsDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.curriculum.Classes.ClassGroupTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClassGroupHandler {

	@Autowired
	ClassGroupTransaction  classGroupTransaction;

	public Flux<StudentDTO> getAcaStudentList(Integer courseId, List<Integer> classIdList, Integer activityId, Integer sessionGroupId, Integer academicYearId) {
		List<AcaScriptsDBO> studentList = classGroupTransaction.getAcaStudentList(courseId, classIdList, activityId, sessionGroupId, academicYearId);
		return Mono.just(studentList).flatMapMany(Flux::fromIterable).map(this::convertStudentDtoToDbo);
	}

	public StudentDTO convertStudentDtoToDbo(AcaScriptsDBO dbo) {
		StudentDTO dto = new StudentDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setId(dbo.getAcaStudentSessionwiseDBO().getAcaStudentYearwiseDBO().getStudentDBO().getId());
			dto.setStudentName(dbo.getAcaStudentSessionwiseDBO().getAcaStudentYearwiseDBO().getStudentDBO().getStudentName());
			dto.setRegisterNo(dbo.getAcaStudentSessionwiseDBO().getAcaStudentYearwiseDBO().getStudentDBO().getRegisterNo());
			if(!Utils.isNullOrEmpty(dbo.getAcaStudentSessionwiseDBO().getAcaVirtualClassDBO())) {
				dto.setAcaClassSelectDTO(new SelectDTO());
				dto.getAcaClassSelectDTO().setValue(String.valueOf(dbo.getAcaStudentSessionwiseDBO().getAcaVirtualClassDBO().getId()));
				dto.getAcaClassSelectDTO().setLabel(dbo.getAcaStudentSessionwiseDBO().getAcaVirtualClassDBO().getClassName());
			}
			else {
				dto.setAcaClassSelectDTO(new SelectDTO());
				dto.getAcaClassSelectDTO().setValue(String.valueOf(dbo.getAcaStudentSessionwiseDBO().getAcaClassDBO().getId()));
				dto.getAcaClassSelectDTO().setLabel(dbo.getAcaStudentSessionwiseDBO().getAcaClassDBO().getClassName());	
			}
		}
		return dto;
	}

	public Flux<AcaClassGroupDTO> getGridData(String academicYearId, String sessionId, String campusId) {
		List<AcaClassGroupDBO> acaClassGroupDBOList = classGroupTransaction.getGridData(academicYearId, sessionId, campusId);
		return this.convertDboToDto(acaClassGroupDBOList);
	}

	private Flux<AcaClassGroupDTO> convertDboToDto(List<AcaClassGroupDBO> acaClassGroupDBOList) {
		List<AcaClassGroupDTO> acaClassGroupDTOList = new ArrayList<AcaClassGroupDTO>();
		acaClassGroupDBOList.forEach(data -> {
			AcaClassGroupDTO dto = new AcaClassGroupDTO();
			dto.setId(data.getId());
			if(!Utils.isNullOrEmpty(data.getClassGroupName())) {
				dto.setClassGroupName(data.getClassGroupName());
			}
			if(!Utils.isNullOrEmpty(data.getAcaCourseDBO())) {
				dto.setAcaCourseDTO(new SelectDTO());
				dto.getAcaCourseDTO().setValue(String.valueOf(data.getAcaCourseDBO().getId()));
				dto.getAcaCourseDTO().setLabel(data.getAcaCourseDBO().getCourseName());
			}
			if(!Utils.isNullOrEmpty(data.getAttActivityDBO())) {
				dto.setAttActivityDTO(new SelectDTO());
				dto.getAttActivityDTO().setValue(String.valueOf(data.getAttActivityDBO().getId()));
				dto.getAttActivityDTO().setLabel(data.getAttActivityDBO().getActivityName()); 
			}
			dto.setClassNameList(data.getAcaClassGroupDetailsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A')
					.map(a -> a.getAcaClassDBO().getClassName())
					.collect(Collectors.joining(",")));	
			acaClassGroupDTOList.add(dto);
		});
		return Flux.fromIterable(acaClassGroupDTOList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<AcaClassGroupDTO> dto, String userId) {
		return dto.handle((classGroupDTO, synchronousSink) -> {
			boolean istrue = classGroupTransaction.duplicateCheck(classGroupDTO);
			if (istrue) {
				synchronousSink.error(new DuplicateException("Class Group Already Created "));
			} else {
				synchronousSink.next(classGroupDTO);
			}
		}).cast(AcaClassGroupDTO.class).map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
			if (!Utils.isNullOrEmpty(s.getId())) {
				classGroupTransaction.update(s);
			} else {
				classGroupTransaction.save(s);
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	private AcaClassGroupDBO convertDtoToDbo(AcaClassGroupDTO dto, String userId) {
		AcaClassGroupDBO dbo = null;
		if(Utils.isNullOrEmpty(dto.getId())) {
			dbo = new AcaClassGroupDBO();
			dbo.setCreatedUsersId(Integer.parseInt(userId));
			dbo.setRecordStatus('A');
		}else {
			dbo = classGroupTransaction.edit(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setId(dto.getId());
		Integer acaDurationId = classGroupTransaction.getDuration(dto.getAcademicYearId().getValue(), dto.getSessionGroupId().getValue());
		if(!Utils.isNullOrEmpty(acaDurationId)) {
			dbo.setAcaDurationDBO(new AcaDurationDBO());
			dbo.getAcaDurationDBO().setId(acaDurationId);
		}
		if(!Utils.isNullOrEmpty(dto.getClassGroupName())) {
			dbo.setClassGroupName(dto.getClassGroupName());
		}
		if(!Utils.isNullOrEmpty(dto.getAcaCourseDTO())) {
			dbo.setAcaCourseDBO(new AcaCourseDBO());
			dbo.getAcaCourseDBO().setId(Integer.parseInt(dto.getAcaCourseDTO().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getAttActivityDTO())) {
			dbo.setAttActivityDBO(new AttActivityDBO());	
			dbo.getAttActivityDBO().setId(Integer.parseInt(dto.getAttActivityDTO().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getAcaCampusDTO())) {
			dbo.setErpCampusDBO(new ErpCampusDBO());
			dbo.getErpCampusDBO().setId(Integer.parseInt(dto.getAcaCampusDTO().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getAcaDepartmentDTO())) {
			dbo.setErpDepartmentDBO(new ErpDepartmentDBO());
			dbo.getErpDepartmentDBO().setId(Integer.parseInt(dto.getAcaDepartmentDTO().getValue()));
		} 	
		Set<AcaClassGroupDetailsDBO> classGroupDetailsDBO = !Utils.isNullOrEmpty(dbo) ?  dbo.getAcaClassGroupDetailsDBOSet() : null;
		Map<Integer, AcaClassGroupDetailsDBO> map = classGroupDetailsDBO.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
		AcaClassGroupDBO dbo1 = dbo;
		if(!Utils.isNullOrEmpty(dto.getAcaClassGroupDetailsDTO())) {
			var classList = dto.getAcaClassGroupDetailsDTO().stream().map(s -> Integer.parseInt(s.getAcaClassDTO().getValue())).collect(Collectors.toList());
			var scriptList = classGroupTransaction.getCourseSessionId(classList);
			var mapList =scriptList.stream().collect(Collectors.groupingBy(s -> !Utils.isNullOrEmpty(s.getAcaStudentSessionwiseDBO().getAcaVirtualClassDBO()) ?
					s.getAcaStudentSessionwiseDBO().getAcaVirtualClassDBO().getId() : s.getAcaStudentSessionwiseDBO().getAcaClassDBO().getId(), 
					Collectors.toList()));	
			dto.getAcaClassGroupDetailsDTO().forEach(classDetailsDTO -> {
				AcaClassGroupDetailsDBO classGroupDBO = null;
				if(Utils.isNullOrEmpty(classDetailsDTO.getId())) {
					classGroupDBO = new AcaClassGroupDetailsDBO();	
				} else {
					classGroupDBO = map.get(classDetailsDTO.getId());
				}
				if(!Utils.isNullOrEmpty(classDetailsDTO.getAcaClassDTO())) {
					classGroupDBO.setAcaClassDBO(new AcaClassDBO());
					classGroupDBO.getAcaClassDBO().setId(Integer.parseInt(classDetailsDTO.getAcaClassDTO().getValue()));
					classGroupDBO.setAcaCourseSessionwiseDBO(new AcaCourseSessionwiseDBO());
					classGroupDBO.getAcaCourseSessionwiseDBO().setId(mapList.get(Integer.parseInt(classDetailsDTO.getAcaClassDTO().getValue())).get(0).getAcaCourseSessionwiseDBO().getId());
				}
				classGroupDBO.setId(classDetailsDTO.getId());
				classGroupDBO.setAcaClassGroupDBO(dbo1);
				if(!Utils.isNullOrEmpty(classDetailsDTO.getAcaClassDTO())) {
					classGroupDBO.setAcaClassDBO(new AcaClassDBO());
					classGroupDBO.getAcaClassDBO().setId(Integer.parseInt(classDetailsDTO.getAcaClassDTO().getValue()));
				}
				classGroupDBO.setCreatedUsersId(Integer.parseInt(userId));
				if(!Utils.isNullOrEmpty(classDetailsDTO.getId())) {
					classGroupDBO.setModifiedUsersId(Integer.parseInt(userId));
				}
				classGroupDBO.setRecordStatus('A');
				classGroupDetailsDBO.add(classGroupDBO);
				Set<AcaClassGroupStudentsDBO> existDBOSet = !Utils.isNullOrEmpty(classGroupDBO) ? classGroupDBO.getAcaClassGroupStudentDBOSet() :null;  
				Map<Integer, AcaClassGroupStudentsDBO> acaClassGroupStudentsmap = existDBOSet.stream().collect(Collectors.toMap(a-> a.getId(), a -> a)); 
				if (!Utils.isNullOrEmpty(existDBOSet)) {
					existDBOSet.forEach(dbos -> {
						if (dbos.getRecordStatus() =='A') {
							acaClassGroupStudentsmap.put(dbos.getId(), dbos);
						}
					});
				}
				Set<AcaClassGroupStudentsDBO> acaClassGroupStudentsDbo = new HashSet<AcaClassGroupStudentsDBO>();  
				if(!Utils.isNullOrEmpty(classDetailsDTO.getAcaClassGroupStudentsDTO())) {
					classDetailsDTO.getAcaClassGroupStudentsDTO().forEach(subdtos -> {
						AcaClassGroupStudentsDBO subdbos = null;
						if(!Utils.isNullOrEmpty(subdtos.getId()) && acaClassGroupStudentsmap.containsKey(subdtos.getId())) {	
							subdbos = acaClassGroupStudentsmap.get(subdtos.getId());
							subdbos.setModifiedUsersId(Integer.parseInt(userId));
							acaClassGroupStudentsmap.remove(subdtos.getId());
						} else {
							subdbos = new AcaClassGroupStudentsDBO();
							subdbos.setCreatedUsersId(Integer.parseInt(userId));
						}
					});
					AcaClassGroupDetailsDBO dbo2 = classGroupDBO;
					if(!Utils.isNullOrEmpty(classDetailsDTO.getAcaClassGroupStudentsDTO())) {
						classDetailsDTO.getAcaClassGroupStudentsDTO().forEach(dtos-> {
							AcaClassGroupStudentsDBO classGroupStudentsDBO = new AcaClassGroupStudentsDBO();
							BeanUtils.copyProperties(dtos, classGroupStudentsDBO);
							classGroupStudentsDBO.setAcaClassGroupDetailsDBO(dbo2);
							classGroupStudentsDBO.setId(dtos.getId());
							if(!Utils.isNullOrEmpty(dtos.getStudentDTO())) {
								classGroupStudentsDBO.setStudentDBO(new StudentDBO());
								classGroupStudentsDBO.getStudentDBO().setId(dtos.getStudentDTO().getId());
							}
							classGroupStudentsDBO.setCreatedUsersId(Integer.parseInt(userId));
							if(!Utils.isNullOrEmpty(dtos.getId()))
								classGroupStudentsDBO.setModifiedUsersId(Integer.parseInt(userId));
							classGroupStudentsDBO.setRecordStatus('A');
							acaClassGroupStudentsDbo.add(classGroupStudentsDBO);
						});
						classGroupDBO.setAcaClassGroupStudentDBOSet(acaClassGroupStudentsDbo);
					}
				}
				if(!Utils.isNullOrEmpty(acaClassGroupStudentsmap)) {
					acaClassGroupStudentsmap.forEach((entry, value)-> {
						value.setModifiedUsersId(Integer.parseInt(userId));
						value.setRecordStatus('D');
						acaClassGroupStudentsDbo.add(value);
					});
				}
			});
		}
		dbo.setAcaClassGroupDetailsDBOSet(classGroupDetailsDBO);
		return dbo;
	}

	public Mono<AcaClassGroupDTO> edit(int id) {
		AcaClassGroupDBO dbo = classGroupTransaction.edit(id);
		return convertDboToDto(dbo);
	}

	private Mono<AcaClassGroupDTO> convertDboToDto(AcaClassGroupDBO dbo) {
		AcaClassGroupDTO dto = new AcaClassGroupDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setId(dbo.getId());
			if(!Utils.isNullOrEmpty(dbo.getAcaDurationDBO())) {
				dto.setAcaDurationDTO(new AcaDurationDTO());
				dto.getAcaDurationDTO().setId(dbo.getAcaDurationDBO().getId());
			}
			if(!Utils.isNullOrEmpty(dbo.getAcaDurationDBO().getAcaSessionGroupDBO())) {
				dto.setSessionGroupId(new SelectDTO());
				dto.getSessionGroupId().setValue(String.valueOf(dbo.getAcaDurationDBO().getAcaSessionGroupDBO().getId()));
				dto.getSessionGroupId().setLabel(dbo.getAcaDurationDBO().getAcaSessionGroupDBO().getSessionGroupName());
			}
			if(!Utils.isNullOrEmpty(dbo.getAcaDurationDBO().getErpAcademicYearDBO())) {
				dto.setAcademicYearId(new SelectDTO());
				dto.getAcademicYearId().setValue(String.valueOf(dbo.getAcaDurationDBO().getErpAcademicYearDBO().getId()));
				dto.getAcademicYearId().setLabel(dbo.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
			}
			if(!Utils.isNullOrEmpty(dbo.getClassGroupName())) {
				dto.setClassGroupName(dbo.getClassGroupName());
			}
			if(!Utils.isNullOrEmpty(dbo.getErpCampusDBO())) {
				dto.setAcaCampusDTO(new SelectDTO());
				dto.getAcaCampusDTO().setValue(String.valueOf(dbo.getErpCampusDBO().getId()));
				dto.getAcaCampusDTO().setLabel(dbo.getErpCampusDBO().getCampusName());
			}
			if(!Utils.isNullOrEmpty(dbo.getErpDepartmentDBO())) {
				dto.setAcaDepartmentDTO(new SelectDTO());
				dto.getAcaDepartmentDTO().setValue(String.valueOf(dbo.getErpDepartmentDBO().getId()));
				dto.getAcaDepartmentDTO().setLabel(dbo.getErpDepartmentDBO().getDepartmentName());
			}
			if(!Utils.isNullOrEmpty(dbo.getAcaCourseDBO())) {
				dto.setAcaCourseDTO(new SelectDTO());
				dto.getAcaCourseDTO().setValue(String.valueOf(dbo.getAcaCourseDBO().getId()));
				dto.getAcaCourseDTO().setLabel(dbo.getAcaCourseDBO().getCourseName());
			} else {
				dto.setAttActivityDTO(new SelectDTO());
				dto.getAttActivityDTO().setValue(String.valueOf(dbo.getAttActivityDBO().getId()));
				dto.getAttActivityDTO().setLabel(dbo.getAttActivityDBO().getActivityName()); 
			}
			List<AcaClassGroupDetailsDTO> acaClassGroupDetailsList = new ArrayList<AcaClassGroupDetailsDTO>();
			if(!Utils.isNullOrEmpty(dbo.getAcaClassGroupDetailsDBOSet())) {
				dto.setClassNameList(dbo.getAcaClassGroupDetailsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').map(a -> a.getAcaClassDBO().getClassName())
						.collect(Collectors.joining(",")));	
				List<Integer> classListId = new ArrayList<Integer>();
				classListId = dbo.getAcaClassGroupDetailsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').map(a -> a.getAcaClassDBO().getId()).collect(Collectors.toList());
				Integer courseId = !Utils.isNullOrEmpty(dto.getAcaCourseDTO()) ? Integer.parseInt(dto.getAcaCourseDTO().getValue()) : null;
				Integer activityId = !Utils.isNullOrEmpty(dto.getAttActivityDTO()) ? Integer.parseInt(dto.getAttActivityDTO().getValue()) : null;
				List<AcaScriptsDBO> studentList = classGroupTransaction.getAcaStudentList
						(courseId, classListId, activityId, Integer.parseInt(dto.getSessionGroupId().getValue()),
								Integer.parseInt(dto.getAcademicYearId().getValue())).stream()
						.collect(Collectors.toList());
				dbo.getAcaClassGroupDetailsDBOSet().forEach(classGroupDetailsSet -> {
					if(classGroupDetailsSet.getRecordStatus() == 'A') {
						AcaClassGroupDetailsDTO acaClassGroupDetailsDTO = new AcaClassGroupDetailsDTO();
						acaClassGroupDetailsDTO.setId(classGroupDetailsSet.getId());
						acaClassGroupDetailsDTO.setAcaClassDTO(new SelectDTO());
						acaClassGroupDetailsDTO.getAcaClassDTO().setValue(String.valueOf(classGroupDetailsSet.getAcaClassDBO().getId()));
						acaClassGroupDetailsDTO.getAcaClassDTO().setLabel(classGroupDetailsSet.getAcaClassDBO().getClassName());
						List<AcaClassGroupStudentsDTO> acaClassGroupStudentsList = new ArrayList<AcaClassGroupStudentsDTO>();
						if(!Utils.isNullOrEmpty(classGroupDetailsSet.getAcaClassGroupStudentDBOSet())) {
							classGroupDetailsSet.getAcaClassGroupStudentDBOSet().forEach(student -> {
								if(student.getRecordStatus() == 'A') {
									AcaClassGroupStudentsDTO studentDTO = new AcaClassGroupStudentsDTO();
									studentDTO.setId(student.getId());
									studentDTO.setStudentDTO(new StudentDTO());
									studentDTO.getStudentDTO().setId(student.getStudentDBO().getId());
									studentDTO.getStudentDTO().setStudentName(student.getStudentDBO().getStudentName());
									studentDTO.getStudentDTO().setRegisterNo(student.getStudentDBO().getRegisterNo());
									studentDTO.getStudentDTO().setAcaClassSelectDTO(new SelectDTO());
									studentDTO.getStudentDTO().getAcaClassSelectDTO().setValue(String.valueOf(classGroupDetailsSet.getAcaClassDBO().getId()));
									studentDTO.getStudentDTO().getAcaClassSelectDTO().setLabel(classGroupDetailsSet.getAcaClassDBO().getClassName());
									studentDTO.getStudentDTO().setIsSelected(true);
									acaClassGroupStudentsList.add(studentDTO);
								}
							});	
							studentList.forEach(value  -> {
								if(value.getAcaClassDBO().getId() == classGroupDetailsSet.getAcaClassDBO().getId()) {
									AcaClassGroupStudentsDTO studDTO = new AcaClassGroupStudentsDTO();	
									studDTO.setStudentDTO(new StudentDTO());
									studDTO.getStudentDTO().setId(value.getAcaStudentSessionwiseDBO().getAcaStudentYearwiseDBO().getStudentDBO().getId());
									studDTO.getStudentDTO().setStudentName(value.getAcaStudentSessionwiseDBO().getAcaStudentYearwiseDBO().getStudentDBO().getStudentName());
									studDTO.getStudentDTO().setRegisterNo(value.getAcaStudentSessionwiseDBO().getAcaStudentYearwiseDBO().getStudentDBO().getRegisterNo());
									studDTO.getStudentDTO().setIsSelected(false);
									acaClassGroupStudentsList.add(studDTO);
								}
							});
							acaClassGroupDetailsDTO.setAcaClassGroupStudentsDTO(acaClassGroupStudentsList);
						}
						acaClassGroupDetailsList.add(acaClassGroupDetailsDTO);
					}
				});
				dto.setAcaClassGroupDetailsDTO(acaClassGroupDetailsList);
			}
		}
		return Mono.just(dto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return classGroupTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}
}
