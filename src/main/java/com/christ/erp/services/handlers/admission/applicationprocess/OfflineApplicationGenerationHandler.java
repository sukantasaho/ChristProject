package com.christ.erp.services.handlers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmAdmissionTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmOfflineApplicationGenerationDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmOfflineApplicationIssueDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ErpNumberGenerationDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ErpReceiptsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmIntakeBatchDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationGenerationDTO;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationIssueDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleComponentsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.admission.applicationprocess.OfflineApplicationGenerationTransaction;
import com.christ.erp.services.transactions.admission.applicationprocess.OfflineApplicationIssueTransaction;
import com.christ.erp.services.transactions.employee.salary.SalaryComponentsTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class OfflineApplicationGenerationHandler {

    @Autowired
    OfflineApplicationGenerationTransaction offlineApplicationGenerationTransaction;

    public Flux<OfflineApplicationGenerationDTO> getGridData(Integer yearId) {
        return offlineApplicationGenerationTransaction.getGridData(yearId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
    }

    public OfflineApplicationGenerationDTO convertDboToDto(AdmOfflineApplicationGenerationDBO dbo) {
        OfflineApplicationGenerationDTO dto = new OfflineApplicationGenerationDTO();
//        BeanUtils.copyProperties(dbo, dto);
        dto.setId(dbo.getId());
        dto.setApplicantName(dbo.getApplicantName());
        dto.setApplicationIssueDate(String.valueOf(dbo.getApplicationIssueDate()));
        dto.setEmailToSendApplicationLink(dbo.getEmailToSendApplicationLink());
        dto.setMobileNoToSendApplicationLink(dbo.getMobileNoToSendApplicationLink());
        if(!Utils.isNullOrEmpty(dbo.getMobileNoCountryCode())) {
            dto.setMobileNoCountryCode(new SelectDTO());
            dto.getMobileNoCountryCode().setLabel(dbo.getMobileNoCountryCode());
        }

        if (!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
            dto.setErpAcademicYearDTO(new ErpAcademicYearDTO());
            dto.getErpAcademicYearDTO().setId(dbo.getErpAcademicYearDBO().getId());
        }

        if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBO())) {
            dto.setAdmProgrammeBatchDTO(new SelectDTO());
            dto.getAdmProgrammeBatchDTO().setValue(String.valueOf(dbo.getAdmProgrammeBatchDBO().getId()));
            dto.getAdmProgrammeBatchDTO().setLabel(dbo.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO())) {
            dto.setAdmAdmissionTypeDTO(new SelectDTO());
            dto.getAdmAdmissionTypeDTO().setValue(String.valueOf(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getId()));
            dto.getAdmAdmissionTypeDTO().setLabel(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getAdmissionType());
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmIntakeBatchDBO())) {
            dto.setAdmIntakeBatchDTO(new SelectDTO());
            dto.getAdmIntakeBatchDTO().setValue(String.valueOf(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmIntakeBatchDBO().getId()));
            dto.getAdmIntakeBatchDTO().setLabel(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmIntakeBatchDBO().getAdmIntakeBatchName());
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getErpProgrammeDBO())) {
            dto.setErpProgrammeDTO(new SelectDTO());
            dto.getErpProgrammeDTO().setValue(String.valueOf(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getErpProgrammeDBO().getId()));
            dto.getErpProgrammeDTO().setLabel(dbo.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getErpProgrammeDBO().getProgrammeName());
        }
        if (!Utils.isNullOrEmpty(dbo.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO())) {
            dto.setErpCampusDTO(new SelectDTO());
            dto.getErpCampusDTO().setValue(String.valueOf(dbo.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId()));
            dto.getErpCampusDTO().setLabel(dbo.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
        }

        dto.setIsFeePaid(dbo.getIsFeePaid());
        dto.setRecieptReferenceNo(dbo.getRecieptReferenceNo());
        dto.setIsSelectionProcessDateAllotted(dbo.getIsSelectionProcessDateAllotted());
        dto.setIsLinkExpired(dbo.getIsLinkExpired());
        dto.setApplicationLinkUrl(dbo.getApplicationLinkUrl());
        return dto;
    }

    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> saveOrUpdate(Mono<OfflineApplicationGenerationDTO> dto, String userId) {
        return dto
                .handle((offlineApplicationGenerationDTO, synchronousSink) -> {
                    boolean istrue = offlineApplicationGenerationTransaction.duplicateCheck(offlineApplicationGenerationDTO);
                    if (istrue) {
                        synchronousSink.error(new DuplicateException("Already exist Offline Application Issue for this application number and batch year."));
                    } else {
                        synchronousSink.next(offlineApplicationGenerationDTO);
                    }
                }).cast(OfflineApplicationGenerationDTO.class)
                .map(data -> convertDtoToDbo(data, userId))
                .flatMap(s -> {
                    if (!Utils.isNullOrEmpty(s.getId())) {
                        offlineApplicationGenerationTransaction.update(s);
                    } else {
                        offlineApplicationGenerationTransaction.save(s);
                    }
                    return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
    }

    public AdmOfflineApplicationGenerationDBO convertDtoToDbo(OfflineApplicationGenerationDTO dto, String userId) {
        AdmOfflineApplicationGenerationDBO dbo = null;
        if (!Utils.isNullOrEmpty(dto.getId())) {
            dbo = offlineApplicationGenerationTransaction.getOfflineApplicationGenerationDetail(dto.getId());
            dbo.setModifiedUsersId(Integer.parseInt(userId));
        } else {
            dbo = new AdmOfflineApplicationGenerationDBO();
        }
        if (!Utils.isNullOrEmpty(dto.getId())) {
            dbo.setId(dto.getId());
        }

        dbo.setApplicantName(dto.getApplicantName());
        dbo.setApplicationIssueDate(LocalDate.parse(dto.getApplicationIssueDate()));

        dbo.setEmailToSendApplicationLink(dto.getEmailToSendApplicationLink());
        dbo.setMobileNoCountryCode(dto.getMobileNoCountryCode().getLabel());
        dbo.setMobileNoToSendApplicationLink(dto.getMobileNoToSendApplicationLink());
        dbo.setErpAcademicYearDBO(new ErpAcademicYearDBO());
        dbo.getErpAcademicYearDBO().setId(dto.getErpAcademicYearDTO().getId());
        dbo.setAdmProgrammeBatchDBO(new AdmProgrammeBatchDBO());


        List<Integer> programmeBatchIds = offlineApplicationGenerationTransaction.getAdmProgrammeBatchId(Integer.parseInt(dto.getAdmIntakeBatchDTO().getValue()));

        if (!programmeBatchIds.isEmpty()) {
            Integer firstId = programmeBatchIds.get(0);

            AdmProgrammeBatchDBO admProgrammeBatchDBO = new AdmProgrammeBatchDBO();
            admProgrammeBatchDBO.setId(firstId);
            dbo.setAdmProgrammeBatchDBO(admProgrammeBatchDBO);
        }

        dbo.setIsFeePaid(dto.getIsFeePaid());
        dbo.setRecieptReferenceNo(dto.getRecieptReferenceNo());
        dbo.setIsSelectionProcessDateAllotted(dto.getIsSelectionProcessDateAllotted());
        dbo.setCreatedUsersId(Integer.parseInt(userId));
        dbo.setModifiedUsersId(Integer.parseInt(userId));
        dbo.setRecordStatus('A');
        return dbo;
    }

    public Mono<OfflineApplicationGenerationDTO> edit(int id) {
        return Mono.just(offlineApplicationGenerationTransaction.getOfflineApplicationGenerationDetail(id)).map(this::convertDboToDto);
    }

    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> delete(int id, String userId) {
        return offlineApplicationGenerationTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
    }

    public Flux<SelectDTO> getProgrammeByDate(String date) {
        LocalDate currentDate = null;
        if (!Utils.isNullOrEmpty(date)) {
            currentDate = Utils.convertStringDateToLocalDate(date);
        }
        return offlineApplicationGenerationTransaction.getProgrammeByDate(currentDate).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO1);
    }

    public SelectDTO convertDBOToDTO1(Tuple dbo) {
        SelectDTO dto = new SelectDTO();
        if (!Utils.isNullOrEmpty(dbo)) {
            dto.setValue(String.valueOf(dbo.get("id")));
            dto.setLabel(String.valueOf(dbo.get("name")));
        }
        return dto;
    }

    public Flux<SelectDTO> getIntakeBatchByProgramme(Integer programmeId) {
        return offlineApplicationGenerationTransaction.getIntakeBatchByProgramme(programmeId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO2);
    }

    public SelectDTO convertDBOToDTO2(Tuple dbo) {
        SelectDTO dto = new SelectDTO();
        if (!Utils.isNullOrEmpty(dbo)) {
            dto.setValue(String.valueOf(dbo.get("id")));
            dto.setLabel(String.valueOf(dbo.get("name")));
        }
        return dto;
    }

    public Flux<SelectDTO> getAdmissionTypeByBatch(Integer programmeId, Integer intakeBatchId) {
        return offlineApplicationGenerationTransaction.getAdmissionTypeByBatch(programmeId, intakeBatchId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO3);
    }

    public SelectDTO convertDBOToDTO3(Tuple dbo) {
        SelectDTO dto = new SelectDTO();
        if (!Utils.isNullOrEmpty(dbo)) {
            dto.setValue(String.valueOf(dbo.get("id")));
            dto.setLabel(String.valueOf(dbo.get("name")));
        }
        return dto;
    }

    public Flux<SelectDTO> getCampusOrLocation(Integer programmeId, Integer intakeBatchId, Integer admissionTypeId) {
        return offlineApplicationGenerationTransaction.getCampusOrLocation(programmeId, intakeBatchId, admissionTypeId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO4);
    }

    public SelectDTO convertDBOToDTO4(Tuple dbo) {
        SelectDTO dto = new SelectDTO();
        if (!Utils.isNullOrEmpty(dbo)) {
            dto.setValue(String.valueOf(dbo.get("id")));
            dto.setLabel(String.valueOf(dbo.get("name")));
        }
        return dto;
    }

}
