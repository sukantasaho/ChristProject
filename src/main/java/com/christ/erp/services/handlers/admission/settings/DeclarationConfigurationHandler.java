package com.christ.erp.services.handlers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.StudentApplnDeclarationsDBO;
import com.christ.erp.services.dbobjects.admission.settings.StudentApplnDeclarationsDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.StudentApplnDeclarationsTemplateDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dto.admission.settings.StudentApplnDeclarationsDTO;
import com.christ.erp.services.dto.admission.settings.StudentApplnDeclarationsDetailsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.admission.settings.DeclarationConfigurationTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeclarationConfigurationHandler {

    @Autowired
    DeclarationConfigurationTransaction declarationConfigurationTransaction;

    public Flux<StudentApplnDeclarationsDTO> getGridData(Integer yearId) {
        return declarationConfigurationTransaction.getGridData(yearId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
    }

    private StudentApplnDeclarationsDTO convertDboToDto(StudentApplnDeclarationsDBO dbo) {
        StudentApplnDeclarationsDTO dto = new StudentApplnDeclarationsDTO();
        dto.setId(dbo.getId());
        if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO())) {
            dto.setCampusProgrammeDTO(new SelectDTO());
            dto.getCampusProgrammeDTO().setValue(String.valueOf(dbo.getErpCampusProgrammeMappingDBO().getId()));
            if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO()) && (!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO()) || !Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpLocationDBO()))){
                String campusOrLocationName= !Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO()) ? dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName(): dbo.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName();
                dto.getCampusProgrammeDTO().setLabel(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName()+ " ("+campusOrLocationName+")");
            }
            if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO())) {
                if (!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpProgrammeDegreeDBO())) {
                    dto.setProgrammeLevelDTO(new SelectDTO());
                    dto.getProgrammeLevelDTO().setLabel(dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpProgrammeDegreeDBO().getErpProgrammeLevelDBO().getProgrammeLevel());
                }
            }
        }
        if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
            dto.setErpAcademicYearDTO(new SelectDTO());
            dto.getErpAcademicYearDTO().setValue(String.valueOf(dbo.getErpAcademicYearDBO().getId()));
            dto.getErpAcademicYearDTO().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
        }
        if(!Utils.isNullOrEmpty(dbo.getStudentApplnDeclarationsDetailsDBOSet())) {
            dto.setStudentApplnDeclarationsDetailsDTOList(new ArrayList<>());
            dbo.getStudentApplnDeclarationsDetailsDBOSet().forEach(detailDBO-> {
                StudentApplnDeclarationsDetailsDTO studentApplnDeclarationsDetailsDTO = new StudentApplnDeclarationsDetailsDTO();
                if(detailDBO.getRecordStatus() =='A') {
                    studentApplnDeclarationsDetailsDTO.setId(detailDBO.getId());
                    studentApplnDeclarationsDetailsDTO.setDeclarationDisplayOrder(detailDBO.getDeclarationDisplayOrder());
                    studentApplnDeclarationsDetailsDTO.setIsMandatory(detailDBO.getIsMandatory());
                    if(!Utils.isNullOrEmpty(detailDBO.getStudentApplnDeclarationsTemplateDBO())) {
                        studentApplnDeclarationsDetailsDTO.setDeclarationTemplate(new SelectDTO());
                        studentApplnDeclarationsDetailsDTO.getDeclarationTemplate().setLabel(detailDBO.getStudentApplnDeclarationsTemplateDBO().getStudentApplnDeclarations());
                        studentApplnDeclarationsDetailsDTO.getDeclarationTemplate().setValue(String.valueOf(detailDBO.getStudentApplnDeclarationsTemplateDBO().getId()));
                    }
                    dto.getStudentApplnDeclarationsDetailsDTOList().add(studentApplnDeclarationsDetailsDTO);
                }
            });
        }
        return dto;
    }

    public Mono<StudentApplnDeclarationsDTO> edit(Integer id) {
        return Mono.just(declarationConfigurationTransaction.edit(id)).map(this::convertDboToDto);
    }

    public Mono<ApiResult> delete(Integer id, String userId) {
        return declarationConfigurationTransaction.delete(id,Integer.parseInt(userId)).map(Utils::responseResult);
    }

    public Mono<ApiResult> saveOrUpdate(Mono<StudentApplnDeclarationsDTO> data, String userId) {
        return data.handle((studentApplnDeclarationsDTO, synchronousSink) -> {
            List<String> duplicateList = null;
            if(Utils.isNullOrEmpty(studentApplnDeclarationsDTO.getId())) {
                List<StudentApplnDeclarationsDBO> values = declarationConfigurationTransaction.duplicateCheck(studentApplnDeclarationsDTO);
                if(!Utils.isNullOrEmpty(values)){
                    duplicateList = new ArrayList<>();
                    for (StudentApplnDeclarationsDBO dbo:values) {
                        if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO())){
                            String prog = dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName() + "(" + dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName() + ")";
                            duplicateList.add(prog);
                        }else {
                            String prog = dbo.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName() + "(" + dbo.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName() + ")";
                            duplicateList.add(prog);
                        }
                    }
                }
            }
            if(!Utils.isNullOrEmpty(duplicateList)) {
                String duplicateValue = duplicateList.stream().collect(Collectors.joining(","));
                synchronousSink.error(new DuplicateException("Duplicate entry of campus programme " + duplicateValue+ " for the year "+studentApplnDeclarationsDTO.getErpAcademicYearDTO().getLabel()));
            } else {
                synchronousSink.next(studentApplnDeclarationsDTO);
            }
        }).cast(StudentApplnDeclarationsDTO.class).map(dat -> convertDtoToDbo(dat, userId)).flatMap(s -> {
                declarationConfigurationTransaction.update(s);
            return Mono.just(Boolean.TRUE);
        }).map(Utils::responseResult);
    }
    private List<StudentApplnDeclarationsDBO> convertDtoToDbo(StudentApplnDeclarationsDTO data, String userId) {
        List<StudentApplnDeclarationsDBO> dboList = new ArrayList<>();
        if(!Utils.isNullOrEmpty(data.getCampusProgrammeList())) {
            data.getCampusProgrammeList().forEach(s-> {
                StudentApplnDeclarationsDBO dbo = new StudentApplnDeclarationsDBO();
                dboList.add(dtoToDBo(data, userId, s, dbo));
            });
        } else if (!Utils.isNullOrEmpty(data.getId())){
            StudentApplnDeclarationsDBO dbo = declarationConfigurationTransaction.edit(data.getId());
            dboList.add(dtoToDBo(data, userId, data.getCampusProgrammeDTO(), dbo));
        }
        return dboList;
    }

    private StudentApplnDeclarationsDBO  dtoToDBo(StudentApplnDeclarationsDTO data, String userId, SelectDTO s, StudentApplnDeclarationsDBO dbo) {
        if(!Utils.isNullOrEmpty(data.getErpAcademicYearDTO()) && !Utils.isNullOrEmpty(data.getErpAcademicYearDTO().getValue())) {
            dbo.setErpAcademicYearDBO(new ErpAcademicYearDBO());
            dbo.getErpAcademicYearDBO().setId(Integer.valueOf(data.getErpAcademicYearDTO().getValue()));
        }
        if(!Utils.isNullOrEmpty(s.getValue())) {
            dbo.setErpCampusProgrammeMappingDBO(new ErpCampusProgrammeMappingDBO());
            dbo.getErpCampusProgrammeMappingDBO().setId(Integer.parseInt(s.getValue()));
        }

        Map <Integer,StudentApplnDeclarationsDetailsDBO> declareMap = null;
        if(!Utils.isNullOrEmpty(dbo.getStudentApplnDeclarationsDetailsDBOSet())){
            declareMap = dbo.getStudentApplnDeclarationsDetailsDBOSet().stream().collect(Collectors.toMap(dec->dec.getId(), dec->dec));
        }
        var declareMap1 = declareMap;
        if(!Utils.isNullOrEmpty(data.getStudentApplnDeclarationsDetailsDTOList())) {
            Set<StudentApplnDeclarationsDetailsDBO> declaration = new HashSet<>();
            data.getStudentApplnDeclarationsDetailsDTOList().forEach(declare -> {
                StudentApplnDeclarationsDetailsDBO declarationsDetailsDBO = new StudentApplnDeclarationsDetailsDBO();
                if(!Utils.isNullOrEmpty(declareMap1) && declareMap1.containsKey(declare.getId())) {
                    declarationsDetailsDBO = declareMap1.get(declare.getId());
                    declarationsDetailsDBO.setModifiedUsersId(Integer.valueOf(userId));
                    declareMap1.remove(declare.getId());
                }
                if(!Utils.isNullOrEmpty(declare.getDeclarationTemplate()) && !Utils.isNullOrEmpty(declare.getDeclarationTemplate().getValue())) {
                    declarationsDetailsDBO.setStudentApplnDeclarationsTemplateDBO(new StudentApplnDeclarationsTemplateDBO());
                    declarationsDetailsDBO.getStudentApplnDeclarationsTemplateDBO().setId(Integer.parseInt(declare.getDeclarationTemplate().getValue()));
                }
                declarationsDetailsDBO.setDeclarationDisplayOrder(declare.getDeclarationDisplayOrder());
                declarationsDetailsDBO.setIsMandatory(declare.getIsMandatory());
                declarationsDetailsDBO.setRecordStatus('A');
                declarationsDetailsDBO.setCreatedUsersId(Integer.valueOf(userId));
                declarationsDetailsDBO.setStudentApplnDeclarationsDBO(dbo);
                declaration.add(declarationsDetailsDBO);
            });
            if(!Utils.isNullOrEmpty(declareMap1)) {
                declareMap1.forEach((key , value) ->{
                    value.setRecordStatus('D');
                    value.setModifiedUsersId(Integer.valueOf(userId));
                    declaration.add(value);
                });
            }
            dbo.setStudentApplnDeclarationsDetailsDBOSet(declaration);
        }
        dbo.setRecordStatus('A');
        dbo.setCreatedUsersId(Integer.valueOf(userId));
        return dbo;
    }
}
