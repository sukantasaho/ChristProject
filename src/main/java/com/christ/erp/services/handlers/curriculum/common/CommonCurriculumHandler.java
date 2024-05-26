package com.christ.erp.services.handlers.curriculum.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.AcaCourseDBO;
import com.christ.erp.services.dbobjects.common.AcaCourseSessionwiseDBO;
import com.christ.erp.services.dbobjects.common.AcaCourseTypeDBO;
import com.christ.erp.services.dbobjects.common.AcaScriptsDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionGroupDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeRoleDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeLevelDBO;
import com.christ.erp.services.dbobjects.common.ObeProgrammeOutcomeTypesDBO;
import com.christ.erp.services.dbobjects.common.ErpRBTDomainsDBO;
import com.christ.erp.services.dbobjects.common.ErpSpecializationDBO;
import com.christ.erp.services.dbobjects.curriculum.common.AttActivityDBO;
import com.christ.erp.services.dbobjects.curriculum.common.ErpAccreditationAffiliationTypeDBO;
import com.christ.erp.services.dbobjects.curriculum.common.ErpExternalsCategoryDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.common.AcaCourseDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionGroupDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionTypeDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaClassDTO;
import com.christ.erp.services.dto.curriculum.settings.ErpProgrammeBatchwiseSettingsDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.transactions.curriculum.common.CommonCurriculumTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommonCurriculumHandler {

	@Autowired
	CommonCurriculumTransaction commonCurriculumTransaction;

	public Flux<SelectDTO> getExternalsCategory(){
		return commonCurriculumTransaction.getExternalsCategory().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(ErpExternalsCategoryDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getExternalsCategoryName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getExternalsCategoryName());
		}
		return dto;
	}

	public Flux<StudentDTO> getStudentDetails(Integer admittedYear) {
		return commonCurriculumTransaction.getStudentDetails(admittedYear).flatMapMany(Flux::fromIterable).map(this::convertdboToDto);
	}

	public StudentDTO convertdboToDto(StudentDBO dbo) {
		StudentDTO dto = new StudentDTO();
		BeanUtils.copyProperties(dbo, dto);
		return dto;
	}

	public Flux<SelectDTO> getEmpsByDepartment(String departmentId) {
		return commonCurriculumTransaction.getEmpsByDepartment(departmentId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}

	public SelectDTO convertDBOToDTO(EmpDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getEmpName());
		}
		return dto;
	}

	public Flux<SelectDTO> getExternalMembersByCategory(int categoryId) {
		return commonCurriculumTransaction.getExternalMembersByCategory(categoryId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public SelectDTO convertDBOToDTO(ExternalsDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getExternalName());
		}
		return dto;
	}

	public Flux<SelectDTO> getCommitteeRoles() {
		return commonCurriculumTransaction.getExternalMembers().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public SelectDTO convertDBOToDTO(ErpCommitteeRoleDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getCommitteeRole());
		}
		return dto;
	}

	public Flux<SelectDTO> getProgrammeByDepartment(int departId) {
		return  commonCurriculumTransaction.getProgrammeByDepartment(departId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);	 
	}

	public SelectDTO convertDBOToDTO(ErpProgrammeDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getProgrammeName());
		}
		return dto;
	}

	public Flux<SelectDTO> getProgrammeByDepartmentFromDeptMapping(int departId) {
		return commonCurriculumTransaction.getProgrammeByDepartmentMapping(departId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public SelectDTO convertDBOToDTO(ErpProgrammeDepartmentMappingDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getErpProgrammeDBO().getProgrammeName());
		}
		return dto;
	}

	public Flux<SelectDTO> getRBTDomains() {
		return commonCurriculumTransaction.getRBTDomains().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(ErpRBTDomainsDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getRbtDomains())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getRbtDomains());
		}
		return dto;
	}

	public  Mono<List<ErpCommitteeDTO>> getImportYearList(String committeeType) {
		List<ErpCommitteeDBO> list = commonCurriculumTransaction.getImportYearList(committeeType.trim());
		return convertDBOToDTO(list);
	}

	public Mono<List<ErpCommitteeDTO>> convertDBOToDTO(List<ErpCommitteeDBO>  dbo) {
		List<ErpCommitteeDTO> dto1 = new ArrayList<ErpCommitteeDTO>();
		Map<Integer,ErpCommitteeDTO> yearList = new HashMap<Integer, ErpCommitteeDTO>();
		dbo.forEach( cDbo -> {
			if(!yearList.containsKey(cDbo.getErpAcademicYearDBO().getId())) {
				ErpCommitteeDTO dto = new ErpCommitteeDTO();
				dto.setErpAcademicYear(new SelectDTO());
				if(!Utils.isNullOrEmpty(cDbo.getErpAcademicYearDBO())) {
					dto.getErpAcademicYear().setValue(cDbo.getErpAcademicYearDBO().getId().toString());
					dto.getErpAcademicYear().setLabel(cDbo.getErpAcademicYearDBO().getAcademicYear().toString());
				}
				dto.setDepartmentList(new  ArrayList<SelectDTO>());
				SelectDTO dept = new SelectDTO();
				if(!Utils.isNullOrEmpty(cDbo.getErpDepartmentDBO())) {
					dept.setValue(cDbo.getErpDepartmentDBO().getId().toString());
					dept.setLabel(cDbo.getErpDepartmentDBO().getDepartmentName());
				} else {
					dept.setLabel("Common BOS");
				}
				dto.getDepartmentList().add(dept);
				yearList.put(cDbo.getErpAcademicYearDBO().getId(), dto);
			} else {
				ErpCommitteeDTO dto = yearList.get(cDbo.getErpAcademicYearDBO().getId());

				SelectDTO dept = new SelectDTO();
				if(!Utils.isNullOrEmpty(cDbo.getErpDepartmentDBO())) {
					dept.setValue(cDbo.getErpDepartmentDBO().getId().toString());
					dept.setLabel(cDbo.getErpDepartmentDBO().getDepartmentName().toString());
				} else {
					dept.setLabel("Common BOS");
				}

				dto.getDepartmentList().add(dept);
				yearList.replace(cDbo.getErpAcademicYearDBO().getId(), dto);
			}
		});
		if(!Utils.isNullOrEmpty(yearList)) {
			yearList.forEach((k,v) -> {
				dto1.add(v);
			});
		}
		return Mono.just(dto1) ;
	}

	public Flux<SelectDTO> getCampusByDepartment(int departmentId) {
		return commonCurriculumTransaction.getCampusByDepartment(departmentId).flatMapMany(Flux::fromIterable).map(this::convertErpCampusDepartmentMappingDBOtodto);
	}

	public SelectDTO convertErpCampusDepartmentMappingDBOtodto(ErpCampusDepartmentMappingDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getErpCampusDBO())) {
			dto.setValue(String.valueOf(dbo.getErpCampusDBO().getId()));
			dto.setLabel(dbo.getErpCampusDBO().getCampusName());
		}
		return dto;
	}

	public Flux<SelectDTO> getAcaCourseType() {
		return commonCurriculumTransaction.getAcaCourseType().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public SelectDTO convertDBOToDTO(AcaCourseTypeDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getCourseType());
		}
		return dto;
	}

	public Flux<SelectDTO> getSpecialization() {
		return commonCurriculumTransaction.getSpecialization().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(ErpSpecializationDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getSpecializationName());
		}
		return dto;
	}

	public Flux<AcaSessionTypeDTO> getAcaSessionType() {
		return commonCurriculumTransaction.getAcaSessionType().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public AcaSessionTypeDTO convertDboToDto(AcaSessionTypeDBO dbo) {
		var dto = new AcaSessionTypeDTO();
		BeanUtils.copyProperties(dbo, dto);
		return dto;
	}

	public Flux<SelectDTO> getAcaSession(String sessionTypeId,Integer sessionNumber, Boolean isTerm) {
		Boolean isTerm1 = Utils.isNullOrEmpty(isTerm) ? false : isTerm;
		return Mono.just(commonCurriculumTransaction.getAcaSession(Integer.parseInt(sessionTypeId),sessionNumber, isTerm1)).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(AcaSessionDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			if(!Utils.isNullOrEmpty(dbo.getTermNumber())) {
				dto.setLabel(dbo.getTermNumber().toString());
			} else {
				dto.setLabel(dbo.getSessionName());
			}
		}
		return dto;
	}

	public Flux<SelectDTO> getObeProgrammeOutcomeTypes() {
		return commonCurriculumTransaction.getObeProgrammeOutcomeTypes().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(ObeProgrammeOutcomeTypesDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getObeProgrammeOutcomeTypes());
		}
		return dto;
	}

	public Flux<AcaSessionGroupDTO> getAcaSessionGroup(String sessionTypeId) {
    	return Mono.just(commonCurriculumTransaction.getAcaSessionGroup(sessionTypeId)).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
    }

	public AcaSessionGroupDTO convertDboToDto(AcaSessionGroupDBO dbo) {
		var dto = new AcaSessionGroupDTO();
		BeanUtils.copyProperties(dbo, dto);
		return dto;
	}

	public Flux<SelectDTO> getAccreditationType() {
		return commonCurriculumTransaction.getAccreditationType().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SelectDTO convertDboToDto(ErpAccreditationAffiliationTypeDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAccreditationCode());
		}
		return dto;
	}

	public Flux<SelectDTO> getProgrammeLevel() {
		return commonCurriculumTransaction.getProgrammeLevel().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	public SelectDTO convertDboToDto(ErpProgrammeLevelDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getProgrammeLevel());
		}
		return dto;
	}

	public Flux<SelectDTO> getAcaSessionByGroup(String sessionGroupId) {
		return Mono.just(commonCurriculumTransaction.getAcaSessionByGroup(sessionGroupId)).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);		
	}

	public Flux<SelectDTO> getDepartmentByCampusId(String campusId) {
		return commonCurriculumTransaction.getDepartmentByCampusId(campusId).flatMapMany(Flux::fromIterable).map(this::convertCampusDboToDto);		
	}	
	
	public SelectDTO convertCampusDboToDto(ErpDepartmentDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getDepartmentName());
		}
		return dto;
	}

	public Flux<SelectDTO> getAttActivity() {
		return commonCurriculumTransaction.getAttActivity().flatMapMany(Flux::fromIterable).map(this::convertActivityDboToDto);		
	}
	
	public SelectDTO convertActivityDboToDto(AttActivityDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getActivityName());
		}
		return dto;
	}
	
	public Flux<ErpAcademicYearDTO> getAcademicYearFromDuration() {
		return commonCurriculumTransaction.getAcademicYearFromDuration().flatMapMany(Flux::fromIterable).map(this::convertAdmissionYearDboToDto);
	}

	public ErpAcademicYearDTO convertAdmissionYearDboToDto(ErpAcademicYearDBO dbo) {
		var dto = new ErpAcademicYearDTO();
		BeanUtils.copyProperties(dbo, dto);
		dto.setIsCurrentAcademicYear(dbo.getIsCurrentAcademicYear());
		dto.setIsCurrentAdmissionYear(dbo.getIsCurrentAdmissionYear());
		return dto;
	}
	
	public Flux<AcaCourseDTO> getAcaCourse(String academicYearId, String departmentId, String levelId,
			String sessionGroupId, String campusId) {
		return Mono.just(commonCurriculumTransaction.getAcaCourse(academicYearId, departmentId, levelId, sessionGroupId, campusId)).
				flatMapMany(Flux::fromIterable).map(this::convertCourseDboToDto);	
	}
	
	public AcaCourseDTO convertCourseDboToDto(AcaCourseDBO dbo) {
		AcaCourseDTO dto = new AcaCourseDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setId(dbo.getId());
			dto.setCourseName(dbo.getCourseName());
		}
		return dto;
	}

	public Flux<SelectDTO> getAcaClass(String academicYearId, String levelId, String sessionGroupId, String campusId, String courseId) {
		List<AcaClassDBO> acaClassList = commonCurriculumTransaction.getAcaClass(academicYearId, courseId, levelId, sessionGroupId, campusId);
		return this.convertAcaClassDboToDto(acaClassList);	
	}
	
	public Flux<SelectDTO> convertAcaClassDboToDto(List<AcaClassDBO> acaClassList) {
	    List<SelectDTO> dto1 = new ArrayList<SelectDTO>();
	    acaClassList.forEach(data -> {
	    	SelectDTO dto = new SelectDTO();
			dto.setValue(String.valueOf(data.getId()));
			dto.setLabel(data.getClassName());
			dto1.add(dto);
	    });
		return Flux.fromIterable(dto1);	    
	}

	public Flux<SelectDTO> getProgrammeByCampus(int campusId, int yearId) {
		return commonCurriculumTransaction.getProgrammeByCampus(campusId, yearId).flatMapMany(Flux::fromIterable).map(this::convertProgmDboToDto);
	}

	public SelectDTO convertProgmDboToDto(ErpProgrammeBatchwiseSettingsDBO dbo) {
		var dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getErpProgrammeDBO().getId()));
		dto.setLabel(dbo.getErpProgrammeDBO().getProgrammeName());
		return dto;
	}

	public Flux<SelectDTO> getBatchYear() {
		return commonCurriculumTransaction.getBatchYear().flatMapMany(Flux::fromIterable).map(this::convertBatchDboToDto);
	}
	
	public SelectDTO convertBatchDboToDto(ErpAcademicYearDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(dbo.getAcademicYearName());
		return dto;
	}

	public Flux<ErpAcademicYearDTO> getAcademicYearFromDurationAndSession(String typeId) {
		return commonCurriculumTransaction.getAcademicYearFromDurationAndSession(typeId).flatMapMany(Flux::fromIterable).map(this::convertSessionDboToDto);
	}
	
	public ErpAcademicYearDTO convertSessionDboToDto(ErpAcademicYearDBO dbo) {
		ErpAcademicYearDTO dto = new ErpAcademicYearDTO();
		dto.setId(dbo.getId());
		dto.setAcademicYearName(dbo.getAcademicYearName());
		return dto;
	}

	public Flux<SelectDTO> getProgrammeLevelByCampus(String yearId, String campusId, String typeId) {
		List<AcaDurationDetailDBO> list = commonCurriculumTransaction.getProgrammeLevelByCampus(yearId, campusId, typeId);
		return this.convertLevelDboToDto(list);
	}
	
	public Flux<SelectDTO> convertLevelDboToDto(List<AcaDurationDetailDBO> list) {
		List<SelectDTO> dtoList = new ArrayList<SelectDTO>();
		var set = list.stream().map(s -> s.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpProgrammeDegreeDBO().getErpProgrammeLevelDBO())
				.collect(Collectors.toSet());
		set.forEach(data -> {
			SelectDTO dto = new SelectDTO();
			dto.setValue(String.valueOf(data.getId()));
			dto.setLabel(data.getProgrammeLevel());
			dtoList.add(dto);
		});
		return Flux.fromIterable(dtoList);
	}
}