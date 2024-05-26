package com.christ.erp.services.handlers.admission.applicationprocess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailAllotmentDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailProgDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanProgrammeDBO;
import com.christ.erp.services.dbobjects.admission.settings.*;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dto.admission.applicationprocess.*;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.helpers.admission.applicationprocess.SelectionProcessPlanHelper;
import com.christ.erp.services.transactions.admission.applicationprocess.SelectionProcessPlanTransaction;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SelectionProcessPlanHandler {
    private static volatile SelectionProcessPlanHandler selectionProcessPlanHandler = null;

    public static SelectionProcessPlanHandler getInstance() {
        if (selectionProcessPlanHandler == null) {
            selectionProcessPlanHandler = new SelectionProcessPlanHandler();
        }
        return selectionProcessPlanHandler;
    }

    SelectionProcessPlanHelper selectionProcessPlanHelper = SelectionProcessPlanHelper.getInstance();
    SelectionProcessPlanTransaction selectionProcessPlanTransaction = SelectionProcessPlanTransaction.getInstance();
    @Autowired
    SelectionProcessPlanTransaction selectionProcessPlanTransaction1;


    //	public List<AdmSelectionProcessPlanDTO> getGridData(String admissionBatchId , String  intakeId, String date) throws Exception{
//		List<AdmSelectionProcessPlanDTO> dtoList = null;
//		AdmSelectionProcessPlanDTO dto = null;
//		List<Tuple> dboList = selectionProcessPlanTransaction.getGridData(admissionBatchId,intakeId, date);
//		if(!Utils.isNullOrEmpty(dboList)) {
//			dtoList = new ArrayList<AdmSelectionProcessPlanDTO>();
//			for (Tuple dbo : dboList) {
//				dto = new AdmSelectionProcessPlanDTO();
//				dto.id = String.valueOf(dbo.get("id"));
//				if(!Utils.isNullOrEmpty(dbo.get("yearId"))) {
//					ExModelBaseDTO acBaseDTO = new ExModelBaseDTO();
//					acBaseDTO.id = String.valueOf(dbo.get("yearId"));
//					dto.acadamicYear =acBaseDTO;
//				}
//				if(!Utils.isNullOrEmpty(dbo.get("batch"))){
//					dto.setAdmIntakeBatch(new SelectDTO());
//					dto.getAdmIntakeBatch().setLabel(String.valueOf(dbo.get("batch")));
//				}
//				dto.sessionname = !Utils.isNullOrEmpty(dbo.get("session")) ? String.valueOf(dbo.get("session")) : "";
//				dto.applicationopenfrom = !Utils.isNullOrEmpty(dbo.get("openFrom")) ? String.valueOf(dbo.get("openFrom")) : "";
//				dto.applicationopentill = !Utils.isNullOrEmpty(dbo.get("openTill")) ? String.valueOf(dbo.get("openTill")) : "";
//				dto.selectionprocessstartdate = !Utils.isNullOrEmpty(dbo.get("sdate")) ? String.valueOf(dbo.get("sdate")) : "";
//				dto.selectionprocessenddate = !Utils.isNullOrEmpty(dbo.get("edate")) ? String.valueOf(dbo.get("edate")) : "";
//				dto.resultdeclarationdate = !Utils.isNullOrEmpty(dbo.get("resultDecl")) ? String.valueOf(dbo.get("resultDecl")) : "";
//				dto.lastdateofadmission = !Utils.isNullOrEmpty(dbo.get("lastAdm")) ? String.valueOf(dbo.get("lastAdm")) : "";
//				dtoList.add(dto);
//			}
//		}
//		return dtoList;
//	}

    public Flux<AdmSelectionProcessPlanDTO> getGridData(String admissionBatchId, String intakeId, String date) {
        return selectionProcessPlanTransaction1.getGridData(admissionBatchId, intakeId, date).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
    }

    public AdmSelectionProcessPlanDTO convertDBOToDTO(Tuple dbo) {
        AdmSelectionProcessPlanDTO dto = new AdmSelectionProcessPlanDTO();
        if (!Utils.isNullOrEmpty(dbo)) {
            dto = new AdmSelectionProcessPlanDTO();
            dto.id = String.valueOf(dbo.get("id"));
            if (!Utils.isNullOrEmpty(dbo.get("yearId"))) {
                ExModelBaseDTO acBaseDTO = new ExModelBaseDTO();
                acBaseDTO.id = String.valueOf(dbo.get("yearId"));
                dto.acadamicYear = acBaseDTO;
            }
//            if (!Utils.isNullOrEmpty(dbo.get("batch"))) {
//                dto.setAdmIntakeBatch(new SelectDTO());
//                dto.getAdmIntakeBatch().setLabel(String.valueOf(dbo.get("batch")));
//            }
            dto.sessionname = !Utils.isNullOrEmpty(dbo.get("session")) ? String.valueOf(dbo.get("session")) : "";
			if(!Utils.isNullOrEmpty(dbo.get("openFrom"))){
				dto.setApplicationopenfrom((LocalDateTime) dbo.get("openFrom"));
			}
            if(!Utils.isNullOrEmpty(dbo.get("openTill"))){
                dto.setApplicationopentill((LocalDateTime) dbo.get("openTill"));
            }
            if(!Utils.isNullOrEmpty(dbo.get("sdate"))){
                dto.setSelectionprocessstartdate((LocalDateTime) dbo.get("sdate"));
            }
            if(!Utils.isNullOrEmpty(dbo.get("edate"))){
                dto.setSelectionprocessenddate((LocalDateTime) dbo.get("edate"));
            }
            if(!Utils.isNullOrEmpty(dbo.get("resultDecl"))){
                dto.setResultdeclarationdate((LocalDateTime) dbo.get("resultDecl"));
            }
            if(!Utils.isNullOrEmpty(dbo.get("lastAdm"))){
                dto.setLastdateofadmission((LocalDateTime) dbo.get("lastAdm"));
            }
			dto.setAdmissionType(new SelectDTO());
			dto.getAdmissionType().setLabel(!Utils.isNullOrEmpty(dbo.get("aType")) ? String.valueOf(dbo.get("aType")) : "");
            if(!Utils.isNullOrEmpty(dbo.get("is_conducted_in_india"))){
                if(dbo.get("is_conducted_in_india").toString().equals("true") || dbo.get("is_conducted_in_india").toString().equals("1") ) {
                    dto.setIndiaoroutsideindia(true);
                } else {
                    dto.setIndiaoroutsideindia(false);
                }
            }
        }
        return dto;
    }

    public boolean saveOrUpdate(AdmSelectionProcessPlanDTO data, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
        boolean isSaved = false;
        boolean duplicated = false;
        AdmSelectionProcessPlanDBO dbo = null;
        duplicated = duplicateCheck(data, result);
        if (!duplicated) {
            if (!Utils.isNullOrEmpty(data)) {
                if (Utils.isNullOrEmpty(data.id)) {
                    dbo = new AdmSelectionProcessPlanDBO();
                    dbo.createdUsersId = Integer.parseInt(userId);
                } else {
                    dbo = selectionProcessPlanTransaction.edit(Integer.parseInt(data.id));
                    dbo.modifiedUsersId = Integer.parseInt(userId);
                }
                if (!Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(data)) {
                    if (!Utils.isNullOrEmpty(data.acadamicYear) && !Utils.isNullOrEmpty(data.acadamicYear.id)) {
                        ErpAcademicYearDBO yearDBO = new ErpAcademicYearDBO();
                        yearDBO.id = Integer.parseInt(data.acadamicYear.id);
                        dbo.erpAcademicYearDBO = yearDBO;
                    }
                    if (!Utils.isNullOrEmpty(data.sessionname)) {
                        dbo.selectionProcessSession = data.sessionname;
                    }
                    if (!Utils.isNullOrEmpty(data.indiaoroutsideindia)) {
                        dbo.isConductedInIndia = data.indiaoroutsideindia;
                    }
                    if (!Utils.isNullOrEmpty(data.applicationopenfrom)) {
                        dbo.applicationOpenFrom = data.applicationopenfrom;
                    }
                    if (!Utils.isNullOrEmpty(data.applicationopentill)) {
                        dbo.applicationOpenTill = data.applicationopentill;
                    }
                    if (!Utils.isNullOrEmpty(data.selectionprocessstartdate)) {
                        dbo.selectionProcessStartDate = data.selectionprocessstartdate;
                    }
                    if (!Utils.isNullOrEmpty(data.selectionprocessenddate)) {
                        dbo.selectionProcessEndDate = data.selectionprocessenddate;
                    }
                    if (!Utils.isNullOrEmpty(data.resultdeclarationdate)) {
                        dbo.resultDeclarationDate = data.resultdeclarationdate;
                    }
                    if (!Utils.isNullOrEmpty(data.lastDateForFeePayment)) {
                        dbo.lastDateForFeePayment = data.lastDateForFeePayment;
                    }
                    if (!Utils.isNullOrEmpty(data.lastdateofadmission)) {
                        dbo.lastDateOfAdmission = data.lastdateofadmission;
                    }
//                    if (!Utils.isNullOrEmpty(data.getAdmIntakeBatch())) {
//                        dbo.setAdmIntakeBatchDBO(new AdmIntakeBatchDBO());
//                        dbo.getAdmIntakeBatchDBO().setId(Integer.parseInt(data.getAdmIntakeBatch().getValue()));
//                    }
                    if (Utils.isNullOrEmpty(data.id)) {
                        Set<AdmSelectionProcessPlanProgrammeDBO> childDboList = new HashSet<AdmSelectionProcessPlanProgrammeDBO>();
                        AdmSelectionProcessPlanProgrammeDBO childDbo = null;
                        for (AdmSelectionProcessPlanProgrammeDTO programme : data.getAdmSelectionProcessPlanProgrammeList()) {
                            if (!Utils.isNullOrEmpty(programme)) {
                                if (!Utils.isNullOrEmpty(programme.getAdmProgrammeBatch())) {
                                    for (SelectDTO prog : programme.getAdmProgrammeBatch()) {
                                        childDbo = new AdmSelectionProcessPlanProgrammeDBO();
                                        childDbo = selectionProcessPlanHelper.convertChildDTOtoDBO(prog, childDbo, dbo);
                                        childDbo.createdUsersId = Integer.parseInt(userId);
                                        childDboList.add(childDbo);
                                    }
                                }
                            }
                        }
                        dbo.admSelectionProcessPlanProgrammeDBOs = childDboList;
                    } else {
//                        List<Integer> newCampusMapIds = null;
//                        if (!Utils.isNullOrEmpty(data.id) && !Utils.isNullOrEmpty(data.programWithPreferance) && data.programWithPreferance.size() > 0) {
//                            AdmSelectionProcessPlanProgrammeDBO childDbo = null;
//                            boolean campusMapIdUpdate = true;
//                            newCampusMapIds = new ArrayList<Integer>();
//                            for (AdmSelectionProcessPlanProgrammeDBO editDbo : dbo.admSelectionProcessPlanProgrammeDBOs) {
//                                if (!Utils.isNullOrEmpty(editDbo.id) && !Utils.isNullOrEmpty(editDbo.recordStatus) && editDbo.recordStatus == 'A') {
//                                    campusMapIdUpdate = false;
//                                    for (ProgramPreferenceDTO preferenceDTO : data.programWithPreferance) {
//                                        if (!Utils.isNullOrEmpty(preferenceDTO.campusMappingId) && !Utils.isNullOrEmpty(editDbo.erpCampusProgrammeMappingDBO) &&
//                                                Integer.parseInt(preferenceDTO.campusMappingId) == editDbo.erpCampusProgrammeMappingDBO.id) {
//                                            newCampusMapIds.add(Integer.parseInt(preferenceDTO.campusMappingId));
//                                            editDbo.recordStatus = 'A';
//                                            campusMapIdUpdate = true;
//                                        }
//                                    }
//                                    if (!campusMapIdUpdate) {
//                                        editDbo.recordStatus = 'D';
//                                        editDbo.modifiedUsersId = Integer.parseInt(userId);
//                                    }
//                                }
//                            }
//                            for (ProgramPreferenceDTO newProgramDto : data.programWithPreferance) {
//                                if (Utils.isNullOrEmpty(newProgramDto.id)) {
//                                    childDbo = new AdmSelectionProcessPlanProgrammeDBO();
//                                    childDbo = selectionProcessPlanHelper.convertChildDTOtoDBO(newProgramDto, childDbo, dbo);
//                                    childDbo.createdUsersId = Integer.parseInt(userId);
//                                    dbo.admSelectionProcessPlanProgrammeDBOs.add(childDbo);
//                                }
//                            }
//                        }
                        Map<Integer, List<AdmSelectionProcessPlanProgrammeDBO>> admSelectionProcessPlanProgrammeExistMap = new HashMap<>();
                        Set<AdmSelectionProcessPlanProgrammeDBO> childDboList = new HashSet<AdmSelectionProcessPlanProgrammeDBO>();
                        if (!Utils.isNullOrEmpty(dbo.getAdmSelectionProcessPlanProgrammeDBOs())) {
                            admSelectionProcessPlanProgrammeExistMap = dbo.getAdmSelectionProcessPlanProgrammeDBOs().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.groupingBy(b -> b.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getId()));
                        }
                        Set<AdmSelectionProcessPlanProgrammeDBO> admSelectionProcessPlanProgrammeSet = new HashSet<>();
                        for (AdmSelectionProcessPlanProgrammeDTO newProgramDto : data.getAdmSelectionProcessPlanProgrammeList()) {
                            AdmSelectionProcessPlanProgrammeDBO childDbo = null;
                            List<AdmSelectionProcessPlanProgrammeDBO> progExistList = admSelectionProcessPlanProgrammeExistMap.get(Integer.parseInt(newProgramDto.getAdmissionType().getValue()));
                            Map<Integer, AdmSelectionProcessPlanProgrammeDBO> dataMap = null;
                            if(!Utils.isNullOrEmpty(progExistList)){
                                admSelectionProcessPlanProgrammeExistMap.remove(Integer.parseInt(newProgramDto.getAdmissionType().getValue()));
                                dataMap = progExistList.stream().collect(Collectors.toMap(s -> s.getAdmProgrammeBatchDBO().getId(), s -> s));
                            }
                            if (!Utils.isNullOrEmpty(newProgramDto.getAdmProgrammeBatch())) {
                                for (SelectDTO prog : newProgramDto.getAdmProgrammeBatch()) {
                                    if (!Utils.isNullOrEmpty(dataMap) && dataMap.containsKey(Integer.parseInt(prog.getValue()))) {
                                        childDbo = dataMap.get(Integer.parseInt(prog.getValue()));
                                        childDbo.setModifiedUsersId(Integer.parseInt(userId));
                                        childDbo = selectionProcessPlanHelper.convertChildDTOtoDBO(prog, childDbo, dbo);
                                        childDboList.add(childDbo);
                                        dataMap.remove(Integer.parseInt(prog.getValue()));
                                    } else {
                                        childDbo = new AdmSelectionProcessPlanProgrammeDBO();
                                        childDbo = selectionProcessPlanHelper.convertChildDTOtoDBO(prog, childDbo, dbo);
                                        childDbo.createdUsersId = Integer.parseInt(userId);
                                        childDboList.add(childDbo);
                                    }
                                }
                            }
                            if(!Utils.isNullOrEmpty(dataMap)){
                                dataMap.forEach((key, value) -> {
                                    value.setRecordStatus('D');
                                    value.setModifiedUsersId(Integer.parseInt(userId));
                                    childDboList.add(value);
                                });
                            }
                        }
                        if(!Utils.isNullOrEmpty(admSelectionProcessPlanProgrammeExistMap)){
                            for (List<AdmSelectionProcessPlanProgrammeDBO> value : admSelectionProcessPlanProgrammeExistMap.values()) {
                                value.forEach( data1 -> {
                                    data1.setRecordStatus('D');
                                    data1.setModifiedUsersId(Integer.parseInt(userId));
                                    childDboList.add(data1);
                                });
                            }
                        }
//                        admSelectionProcessPlanProgrammeSet.add(childDboList);
                        dbo.setAdmSelectionProcessPlanProgrammeDBOs(childDboList);
                    }
                    dbo.recordStatus = 'A';
                    isSaved = selectionProcessPlanTransaction.saveOrUpdate(dbo);
                }
            }
        }
        return isSaved;
    }

    public boolean duplicateCheck(AdmSelectionProcessPlanDTO data, ApiResult<ModelBaseDTO> result) throws Exception {
        boolean duplicateCheck = false;
        List<AdmSelectionProcessPlanDBO> dbos = null;
        dbos = selectionProcessPlanTransaction.getDuplicateCheck(data);
        if (!Utils.isNullOrEmpty(dbos) && dbos.size() > 0) {
            duplicateCheck = true;
            result.failureMessage = "Duplicate entry for Selection Process Plan";
        }
        return duplicateCheck;
    }


    public AdmSelectionProcessPlanDTO edit(String id) throws Exception {
        AdmSelectionProcessPlanDTO dto = null;
        if (!Utils.isNullOrEmpty(id)) {
            AdmSelectionProcessPlanDBO dbo = selectionProcessPlanTransaction1.edit2(Integer.parseInt(id));
            Set<Integer> intakeIds = new HashSet<>();
            if (!Utils.isNullOrEmpty(dbo)) {
                dto = new AdmSelectionProcessPlanDTO();
                if (!Utils.isNullOrEmpty(dbo.id)) {
                    dto.id = String.valueOf(dbo.id);
                }
                if (!Utils.isNullOrEmpty(dbo.erpAcademicYearDBO)) {
                    ExModelBaseDTO baseDTO = new ExModelBaseDTO();
                    baseDTO.id = String.valueOf(dbo.erpAcademicYearDBO.id);
                    dto.acadamicYear = baseDTO;
                }
                if (!Utils.isNullOrEmpty(dbo.selectionProcessSession)) {
                    dto.sessionname = String.valueOf(dbo.selectionProcessSession);
                }
                if (!Utils.isNullOrEmpty(dbo.isConductedInIndia)) {
                    dto.indiaoroutsideindia = dbo.isConductedInIndia;
                }
                if (!Utils.isNullOrEmpty(dbo.applicationOpenFrom)) {
                    dto.applicationopenfrom = dbo.applicationOpenFrom;
                }
                if (!Utils.isNullOrEmpty(dbo.applicationOpenTill)) {
                    dto.applicationopentill = dbo.applicationOpenTill;
                }
                if (!Utils.isNullOrEmpty(dbo.selectionProcessStartDate)) {
                    dto.selectionprocessstartdate = dbo.selectionProcessStartDate;
                }
                if (!Utils.isNullOrEmpty(dbo.selectionProcessEndDate)) {
                    dto.selectionprocessenddate = dbo.selectionProcessEndDate;
                }
                if (!Utils.isNullOrEmpty(dbo.resultDeclarationDate)) {
                    dto.resultdeclarationdate = dbo.resultDeclarationDate;
                }
                if (!Utils.isNullOrEmpty(dbo.lastDateForFeePayment)) {
                    dto.lastDateForFeePayment = dbo.lastDateForFeePayment;
                }
                if (!Utils.isNullOrEmpty(dbo.lastDateOfAdmission)) {
                    dto.lastdateofadmission = dbo.lastDateOfAdmission;
                }
//                if (!Utils.isNullOrEmpty(dbo.getAdmIntakeBatchDBO())) {
//                    dto.setAdmIntakeBatch(new SelectDTO());
//                    dto.getAdmIntakeBatch().setValue(String.valueOf(dbo.getAdmIntakeBatchDBO().getId()));
//                    dto.getAdmIntakeBatch().setLabel(dbo.getAdmIntakeBatchDBO().getAdmIntakeBatchName());
//                }


               if(!Utils.isNullOrEmpty(dbo.getAdmSelectionProcessPlanProgrammeDBOs())){
                   dto.setAdmIntakeBatch(new ArrayList<>());
                   for (AdmSelectionProcessPlanProgrammeDBO prog :dbo.getAdmSelectionProcessPlanProgrammeDBOs()) {
                       if(prog.getRecordStatus() == 'A'){
                           AdmProgrammeSettingsDBO progSetting = prog.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO();
                           if(!Utils.isNullOrEmpty(progSetting.getAdmIntakeBatchDBO())  && !intakeIds.contains(progSetting.getAdmIntakeBatchDBO().getId())){
                               intakeIds.add(progSetting.getAdmIntakeBatchDBO().getId());
                               SelectDTO selectDTO = new SelectDTO();
                               selectDTO.setValue(String.valueOf(progSetting.getAdmIntakeBatchDBO().getId()));
                               selectDTO.setLabel(progSetting.getAdmIntakeBatchDBO().getAdmIntakeBatchName());
                               dto.getAdmIntakeBatch().add(selectDTO);
                           }
                       }
                   }
               }



                if (!Utils.isNullOrEmpty(dbo.admSelectionProcessPlanProgrammeDBOs)) {

//                    ProgramPreferenceDTO preferenceDTO = null;
//                    List<ProgramPreferenceDTO> preferenceDTOList = new ArrayList<ProgramPreferenceDTO>();
//                    for (AdmSelectionProcessPlanProgrammeDBO childDbo : dbo.admSelectionProcessPlanProgrammeDBOs) {
//                        if (!Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO)
//                                && !Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.recordStatus) && childDbo.erpCampusProgrammeMappingDBO.recordStatus == 'A'
//                                && !Utils.isNullOrEmpty(childDbo.recordStatus) && childDbo.recordStatus == 'A') {
//                            preferenceDTO = new ProgramPreferenceDTO();
//                            preferenceDTO.id = String.valueOf(childDbo.id);
//                            preferenceDTO.programId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id);
//                            preferenceDTO.campusMappingId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.id);
//                            preferenceDTO.value = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.id);
//                            if (!Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpCampusDBO)) {
//                                preferenceDTO.preferenceId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
//                                preferenceDTO.preferenceOption = 'C';
//                            }
//                            if (!Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpLocationDBO)) {
//                                preferenceDTO.preferenceId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.erpLocationDBO.id);
//                                preferenceDTO.preferenceOption = 'L';
//                            }
//                            if (!Utils.isNullOrEmpty(childDbo.getAcaBatchDBO())) {
//                                preferenceDTO.acaBatchId = String.valueOf(childDbo.getAcaBatchDBO().getId());
//                            }
//                            preferenceDTOList.add(preferenceDTO);
//                        }
//                    }
//                    dto.programWithPreferance = preferenceDTOList;

                    List<AdmSelectionProcessPlanProgrammeDTO> programmeList = new ArrayList<>();
                    Map<String,List<AdmSelectionProcessPlanProgrammeDBO>> AdmSelectionProcessPlanProgrammeDTOMap = new HashMap<>();
                    AdmSelectionProcessPlanProgrammeDTOMap = dbo.admSelectionProcessPlanProgrammeDBOs.stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.groupingBy(b -> b.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getAdmissionType()));
                    if(!Utils.isNullOrEmpty(AdmSelectionProcessPlanProgrammeDTOMap)){
                        for (Map.Entry<String, List<AdmSelectionProcessPlanProgrammeDBO>> entry : AdmSelectionProcessPlanProgrammeDTOMap.entrySet()){
                            AdmSelectionProcessPlanProgrammeDTO programmeDTO = new AdmSelectionProcessPlanProgrammeDTO();
                            List<AdmSelectionProcessPlanProgrammeDBO> programmeList1 = entry.getValue();
                            if(!Utils.isNullOrEmpty(programmeList1)){
                                programmeDTO.setAdmissionType(new SelectDTO());
                                programmeDTO.getAdmissionType().setValue(String.valueOf(programmeList1.get(0).getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getId()));
                                programmeDTO.getAdmissionType().setLabel(programmeList1.get(0).getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getAdmissionType());
                                programmeDTO.setAdmProgrammeBatch(new ArrayList<>());
                                for (AdmSelectionProcessPlanProgrammeDBO prog :programmeList1){
                                    SelectDTO batchProg = new SelectDTO();
                                    AdmProgrammeSettingsDBO programmeSetting = prog.getAdmProgrammeBatchDBO().getAdmProgrammeSettingsDBO();
                                    String value1 = prog.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeNameForApplication();
                                    if(!Utils.isNullOrEmpty(prog.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO())){
                                        value1 +="-"+prog.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getShortName();
                                    } else {
                                        value1 +="-"+prog.getAdmProgrammeBatchDBO().getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationShortName();
                                    }
                                    batchProg.setValue(String.valueOf(prog.getAdmProgrammeBatchDBO().getId()));
                                    batchProg.setLabel(value1);
                                    programmeDTO.getAdmProgrammeBatch().add(batchProg);
                                }
                                programmeList.add(programmeDTO);
                            }
                        }
                    }
                    dto.setAdmSelectionProcessPlanProgrammeList(programmeList);
                }
            }
        }
        return dto;
    }

    public boolean delete(String id, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
        List<String> duplicateData =  new ArrayList<>();
        List<Tuple> values = selectionProcessPlanTransaction1.getCheckPlan(Integer.parseInt(id));
        if(!Utils.isNullOrEmpty(values)){
            for (Tuple value : values) {
                if(!Utils.isNullOrEmpty(value) && !Utils.isNullOrEmpty(value.get(0))){
                    duplicateData.add(String.valueOf(value.get(0)));
                }
            }
        }
        if(!duplicateData.isEmpty()){
            result.success = false;
            result.failureMessage = "Session was Already Selected by Applicants ";
            return false;
        }
        return selectionProcessPlanTransaction1.delete(id, userId);
    }

    public AdmSelectionProcessPlanDTO editDetailsList(String id) throws Exception {
        AdmSelectionProcessPlanDTO dto = null;
        if (!Utils.isNullOrEmpty(id) && !Utils.isNullOrWhitespace(id)) {
            AdmSelectionProcessPlanDBO dbo = selectionProcessPlanTransaction1.edit1(Integer.parseInt(id));
            if (!Utils.isNullOrEmpty(dbo)) {
                dto = new AdmSelectionProcessPlanDTO();
                if (!Utils.isNullOrEmpty(dbo.id)) {
                    dto.id = String.valueOf(dbo.id);
                }
                if (!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
                    dto.acadamicYear = new ExModelBaseDTO();
                    dto.acadamicYear.id = String.valueOf(dbo.erpAcademicYearDBO.id);
                }
                if (!Utils.isNullOrEmpty(dbo.selectionProcessStartDate)) {
                    dto.selectionprocessstartdate = dbo.selectionProcessStartDate;
                }
                if (!Utils.isNullOrEmpty(dbo.selectionProcessSession)) {
                    dto.sessionname = dbo.selectionProcessSession.toString();
                }
                if (!Utils.isNullOrEmpty(dbo.selectionProcessEndDate)) {
                    dto.selectionprocessenddate = dbo.selectionProcessEndDate;
                }
                if (!Utils.isNullOrEmpty(dbo.selectionProcessEndDate)) {
                    dto.setIndiaoroutsideindia(dbo.isConductedInIndia);
                }
                if (!Utils.isNullOrEmpty(dbo.admSelectionProcessPlanProgrammeDBOs)) {
                    ProgramPreferenceDTO preferenceDTO = null;
                    List<ProgramPreferenceDTO> preferenceDTOList = new ArrayList<ProgramPreferenceDTO>();
                    for (AdmSelectionProcessPlanProgrammeDBO childDbo : dbo.admSelectionProcessPlanProgrammeDBOs) {
                        if (!Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO)
                                && !Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.recordStatus) && childDbo.erpCampusProgrammeMappingDBO.recordStatus == 'A'
                                && !Utils.isNullOrEmpty(childDbo.recordStatus) && childDbo.recordStatus == 'A') {

                            preferenceDTO = new ProgramPreferenceDTO();
                            preferenceDTO.id = String.valueOf(childDbo.id);
//                            preferenceDTO.campusMappingId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.id);
//                            preferenceDTO.value = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.id);
//                            if (!Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
//                                preferenceDTO.programId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id);
//                            }
//                            if (!Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpCampusDBO)) {
//                                preferenceDTO.preferenceId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
//                                preferenceDTO.preferenceOption = 'C';
//                            }
//                            if (!Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpLocationDBO)) {
//                                preferenceDTO.preferenceId = String.valueOf(childDbo.erpCampusProgrammeMappingDBO.erpLocationDBO.id);
//                                preferenceDTO.preferenceOption = 'L';
//                            }
                            preferenceDTO.setValue(String.valueOf(childDbo.getAdmProgrammeBatchDBO().getId()));

                            AdmProgrammeBatchDBO data = childDbo.getAdmProgrammeBatchDBO();
                            String value = data.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeNameForApplication();
                            if(!Utils.isNullOrEmpty(data) && !Utils.isNullOrEmpty(data.getErpCampusProgrammeMappingDBO().erpCampusDBO)){
                                value += " ("+data.getErpCampusProgrammeMappingDBO().erpCampusDBO.getShortName()+")";
                            } else if(!Utils.isNullOrEmpty(data) && !Utils.isNullOrEmpty(data.getErpCampusProgrammeMappingDBO().erpLocationDBO)) {
                                value += " ("+data.getErpCampusProgrammeMappingDBO().erpLocationDBO.locationShortName+")";
                            }
                            value +="-"+data.getAdmProgrammeSettingsDBO().getAdmAdmissionTypeDBO().getAdmissionType();
                            preferenceDTO.setLabel(value);

                            preferenceDTOList.add(preferenceDTO);
                        }
                    }
                    dto.programWithPreferance = preferenceDTOList;
                }
                selectionProcessPlanHelper.convertDetailDBOsToDTOs(dbo, dto);
            }
        }
        return dto;
    }

    public boolean saveOrUpdateSlotDetails(AdmSelectionProcessPlanDetailAddSlotDTO data, ApiResult<ModelBaseDTO> result,String userId) throws NumberFormatException, Exception {
        AdmSelectionProcessPlanDetailDBO detailDbo = null;
        Map<Integer,AdmSelectionProcessPlanCenterBasedDBO> AdmSelectionProcessPlanCenterBasedDBOExistMap = new HashMap<>();
        boolean isSaved = false;
        if (!Utils.isNullOrEmpty(data)) {
//            boolean isduplicate = duplicateCheckdetails(data, result);
            List<Tuple> duplicate = selectionProcessPlanTransaction1.duplicateCheckSlot(data);
            if (Utils.isNullOrEmpty(duplicate)) {
                if (!Utils.isNullOrWhitespace(data.id)) {
                    detailDbo = selectionProcessPlanTransaction.editSlotDetails(Integer.parseInt(data.id.trim()));
                    detailDbo.modifiedUsersId = Integer.parseInt(userId);
                } else {
                    detailDbo = new AdmSelectionProcessPlanDetailDBO();
                    detailDbo.createdUsersId = Integer.parseInt(userId);
                }
                if (!Utils.isNullOrEmpty(data.selectionProcessName) && !Utils.isNullOrEmpty(data.selectionProcessName.id)) {
                    AdmSelectionProcessTypeDBO typeDBO = new AdmSelectionProcessTypeDBO();
                    typeDBO.id = Integer.parseInt(data.selectionProcessName.id);
                    detailDbo.admSelectionProcessTypeDBO = typeDBO;
                }
                if (!Utils.isNullOrEmpty(data.processOrder)) {
                    detailDbo.processOrder = Integer.parseInt(data.processOrder);
                }
                if (!Utils.isNullOrEmpty(data.selectionprocessdate)) {
                    detailDbo.selectionProcessDate = data.selectionprocessdate;
                }
                if (!Utils.isNullOrEmpty(data.time)) {
                    detailDbo.selectionProcessTime = Utils.convertStringTimeToLocalTime(data.time);
                }
                if (!Utils.isNullOrEmpty(data.slot)) {
                    detailDbo.slot = data.slot;
                }
                if (!Utils.isNullOrEmpty(data.programWithPreferance) && data.programWithPreferance.size() > 0) {
                    convertprogPrefDtoTODbo(data, detailDbo, userId);
                }
                if (!Utils.isNullOrEmpty(data.citieslist)) {

                    if (Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanCenterBasedDBOs)) {
                        detailDbo.admSelectionProcessPlanCenterBasedDBOs = new HashSet<AdmSelectionProcessPlanCenterBasedDBO>();
                        for (AdmSelectionProcessPlanCenterBasedDTO centerBasedDTO : data.citieslist) {
                            AdmSelectionProcessPlanCenterBasedDBO centerBasedDBO = new AdmSelectionProcessPlanCenterBasedDBO();
                            centerBasedDBO.createdUsersId = Integer.parseInt(userId);
                            centerBasedDBO = selectionProcessPlanHelper.convertCenterBasedDtoToDbo(centerBasedDTO, centerBasedDBO, detailDbo);
                            detailDbo.admSelectionProcessPlanCenterBasedDBOs.add(centerBasedDBO);
                        }
                    } else {
                        AdmSelectionProcessPlanCenterBasedDBO centerBasedDBO;
                        if (!Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanCenterBasedDBOs)) {
                            for (AdmSelectionProcessPlanCenterBasedDBO centerBasedDBO2 : detailDbo.admSelectionProcessPlanCenterBasedDBOs) {
                                if (!Utils.isNullOrEmpty(centerBasedDBO2.id) && centerBasedDBO2.recordStatus == 'A') {
                                    AdmSelectionProcessPlanCenterBasedDBOExistMap.put(centerBasedDBO2.id,centerBasedDBO2);
                                }
                            }

                            for (AdmSelectionProcessPlanCenterBasedDTO centerBasedDTO2 : data.citieslist) {
                                if (!Utils.isNullOrEmpty(centerBasedDTO2.id) && AdmSelectionProcessPlanCenterBasedDBOExistMap.containsKey(Integer.parseInt(centerBasedDTO2.id))) {
                                    centerBasedDBO = AdmSelectionProcessPlanCenterBasedDBOExistMap.get(Integer.parseInt(centerBasedDTO2.id));
                                    centerBasedDBO.modifiedUsersId = Integer.parseInt(userId);
                                    centerBasedDBO = selectionProcessPlanHelper.convertCenterBasedDtoToDbo(centerBasedDTO2, centerBasedDBO, detailDbo);
                                    detailDbo.admSelectionProcessPlanCenterBasedDBOs.add(centerBasedDBO);
                                    AdmSelectionProcessPlanCenterBasedDBOExistMap.remove(Integer.parseInt(centerBasedDTO2.id));
                                } else {
                                    centerBasedDBO = new AdmSelectionProcessPlanCenterBasedDBO();
                                    centerBasedDBO.createdUsersId = Integer.parseInt(userId);
                                    centerBasedDBO = selectionProcessPlanHelper.convertCenterBasedDtoToDbo(centerBasedDTO2, centerBasedDBO, detailDbo);
                                    detailDbo.admSelectionProcessPlanCenterBasedDBOs.add(centerBasedDBO);
                                }
                            }
                        }
                        if (!Utils.isNullOrEmpty(AdmSelectionProcessPlanCenterBasedDBOExistMap)){
                            for ( AdmSelectionProcessPlanCenterBasedDBO centerBasedDBO1: AdmSelectionProcessPlanCenterBasedDBOExistMap.values()) {
                                centerBasedDBO1.recordStatus = 'D';
                                centerBasedDBO1.modifiedUsersId = Integer.parseInt(userId);
                            }
                        }
                    }
                }
                if (!Utils.isNullOrEmpty(data.parentId)) {
                    AdmSelectionProcessPlanDBO dbo = new AdmSelectionProcessPlanDBO();
                    dbo.id = Integer.parseInt(data.parentId);
                    detailDbo.admSelectionProcessPlanDBO = dbo;
                    detailDbo.recordStatus = 'A';
                    isSaved = selectionProcessPlanTransaction1.saveOrUpdateDetails(detailDbo);
                }
            }
            else {
                Set<String> progName = new HashSet<>();
                duplicate.forEach( progamme -> {
                    progName.add(String.valueOf(progamme.get("prog")));
                });
                String msg = " Already plan was Created for " + progName;
                result.failureMessage = msg.replace("[","' ").replace("]"," '");
            }
        }
        return isSaved;
    }

//    private boolean duplicateCheckdetails(AdmSelectionProcessPlanDetailAddSlotDTO data, ApiResult<ModelBaseDTO> result) throws Exception {
//        boolean isDuplicate = false;
//        if (!Utils.isNullOrEmpty(data) && !Utils.isNullOrEmpty(data.selectionprocessdate)) {
//            if (!Utils.isNullOrEmpty(data.slot)) {
//                LocalDate spDate = data.selectionprocessdate;
//                String slot = data.slot.trim(), venueId = "", processOrder = data.processOrder.trim();
//                List<Object[]> detailsDbo = selectionProcessPlanTransaction.duplicateCheckdetails(spDate, slot, venueId, data.id, processOrder, data.parentId);
//                if (!Utils.isNullOrEmpty(detailsDbo) && detailsDbo.size() > 0) {
//                    result.failureMessage = "Sorrry, you cannot add duplicate data in Selection Process Date and Slot";
//                    isDuplicate = true;
//                }
//            }
//        }
//        return isDuplicate;
//    }

//    private boolean duplicateCheckdetails(AdmSelectionProcessPlanDetailAddVenueDTO data, ApiResult<ModelBaseDTO> result) throws Exception {
//        boolean isDuplicate = false;
//        if (!Utils.isNullOrEmpty(data) && !Utils.isNullOrEmpty(data.selectionprocessdate)) {
//            if (!Utils.isNullOrEmpty(data.venue) && !Utils.isNullOrEmpty(data.venue.id)) {
////                LocalDate spDate = Utils.convertStringDateTimeToLocalDate(data.selectionprocessdate);
//                LocalDate spDate = data.selectionprocessdate;
//                String venueId = data.venue.id.trim(), slot = "", processOrder = data.processOrder.trim();
//                List<Object[]> detailsDbo = selectionProcessPlanTransaction.duplicateCheckdetails(spDate, slot, venueId, data.id, processOrder, data.parentId);
//                if (!Utils.isNullOrEmpty(detailsDbo) && detailsDbo.size() > 0) {
//                    result.failureMessage = "Sorrry, you cannot add duplicate data in Selection Process Date and Venue";
//                    isDuplicate = true;
//                }
//            }
//        }
//        return isDuplicate;
//    }

    public boolean saveOrUpdateVenueDetails(AdmSelectionProcessPlanDetailAddVenueDTO data, ApiResult<ModelBaseDTO> result, String userId) throws NumberFormatException, Exception {
        AdmSelectionProcessPlanDetailDBO detailDbo = null;
        Map<Integer,AdmSelectionProcessPlanDetailAllotmentDBO> AdmSelectionProcessPlanDetailAllotmentDBOExistMap = new HashMap<>();
        boolean isSaved = false;
        if (!Utils.isNullOrEmpty(data)) {
//            boolean isDuplicated = duplicateCheckdetails(data, result);
            List<Tuple> duplicate = selectionProcessPlanTransaction1.duplicateCheckVenu(data);
            if (Utils.isNullOrEmpty(duplicate)) {
                if (!Utils.isNullOrWhitespace(data.id)) {
                    detailDbo = selectionProcessPlanTransaction.editSlotDetails(Integer.parseInt(data.id.trim()));
                } else {
                    detailDbo = new AdmSelectionProcessPlanDetailDBO();
                    detailDbo.createdUsersId = Integer.parseInt(userId);
                }
                if (!Utils.isNullOrEmpty(data.avaliableseats)) {
                    detailDbo.availableSeats = Integer.parseInt(data.avaliableseats);
                }
                if (!Utils.isNullOrEmpty(data.selectionprocessdate)) {
                    detailDbo.selectionProcessDate = data.selectionprocessdate;
                }
                if (!Utils.isNullOrEmpty(data.processOrder)) {
                    detailDbo.processOrder = Integer.parseInt(data.processOrder);
                    if (Integer.parseInt(data.processOrder) == 2) {
//                        if (!Utils.isNullOrEmpty(data.secondRoundEligibility) && data.secondRoundEligibility.equals("true")) {
//                            if (!Utils.isNullOrEmpty(data.secondRoundVenueSp) && !Utils.isNullOrEmpty(data.secondRoundDateSp)) {
//                                detailDbo.isCandidateChooseSp2Venue = Boolean.parseBoolean(data.secondRoundVenueSp);
//                                detailDbo.isCandidateChooseSp2Date = Boolean.parseBoolean(data.secondRoundDateSp);
//                            }
//                        } else if (!Utils.isNullOrEmpty(data.secondRoundEligibility) && data.secondRoundEligibility.equals("false")) {
//                            if (!Utils.isNullOrEmpty(data.selectionprocessdate) && !Utils.isNullOrEmpty(data.selectionprocessdate)) {
//                                detailDbo.isCandidateChooseSpDate = Boolean.parseBoolean(data.dateSelectionProcess);
//                                detailDbo.isCandidateChooseSpVenue = Boolean.parseBoolean(data.venueSelectionProcess);
//                            }
//                        } else {
//                            detailDbo.isCandidateChooseSp2Venue = null;
//                            detailDbo.isCandidateChooseSp2Date = null;
//                            detailDbo.isCandidateChooseSpDate = null;
//                            detailDbo.isCandidateChooseSpVenue = null;
//                        }
                       if(!Utils.isNullOrEmpty(data.secondRoundVenueSp)){
                           detailDbo.isCandidateChooseSp2Venue = Boolean.parseBoolean(data.secondRoundVenueSp);
                       }
                       if(!Utils.isNullOrEmpty(data.secondRoundDateSp)){
                           detailDbo.isCandidateChooseSp2Date = Boolean.parseBoolean(data.secondRoundDateSp);
                       }
                       if(!Utils.isNullOrEmpty(data.followSameVenue)){
                           detailDbo.isfollowTheSameVenueForSp2 = Boolean.parseBoolean(data.followSameVenue);
                       }

                    }
                }
                if (!Utils.isNullOrEmpty(data.selectionProcessName) && !Utils.isNullOrEmpty(data.selectionProcessName.id)) {
                    AdmSelectionProcessTypeDBO typeDBO = new AdmSelectionProcessTypeDBO();
                    typeDBO.id = Integer.parseInt(data.selectionProcessName.id);
                    detailDbo.admSelectionProcessTypeDBO = typeDBO;
                }
                if (!Utils.isNullOrEmpty(data.venue) && !Utils.isNullOrEmpty(data.venue.id)) {
                    AdmSelectionProcessVenueCityDBO venueCityDBO = new AdmSelectionProcessVenueCityDBO();
                    venueCityDBO.id = Integer.parseInt(data.venue.id);
                    detailDbo.admSelectionProcessVenueCityDBO = venueCityDBO;
                }
                if (!Utils.isNullOrEmpty(data.programWithPreferance) && data.programWithPreferance.size() > 0) {
                    convertprogPrefDtoTODbo(data, detailDbo, userId);
                }

                if (!Utils.isNullOrEmpty(data.timewithseats) && data.timewithseats.size() > 0) {
                    if (Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanDetailAllotmentDBOs) || detailDbo.admSelectionProcessPlanDetailAllotmentDBOs.size() == 0) {
                        detailDbo.admSelectionProcessPlanDetailAllotmentDBOs = new HashSet<AdmSelectionProcessPlanDetailAllotmentDBO>();
                        AdmSelectionProcessPlanDetailAllotmentDBO allotmentDBO = null;
//                        detailDbo.admSelectionProcessPlanDetailAllotmentDBOs = new HashSet<AdmSelectionProcessPlanDetailAllotmentDBO>();
                        for (AdmSelectionProcessPlanDetailAllotmentDTO detailAllotmentDTO : data.timewithseats) {
                            allotmentDBO = new AdmSelectionProcessPlanDetailAllotmentDBO();
                            allotmentDBO.createdUsersId = Integer.parseInt(userId);
                            allotmentDBO = selectionProcessPlanHelper.venueTimewithseatsDtoToDbo(detailAllotmentDTO, allotmentDBO, detailDbo);
                            detailDbo.admSelectionProcessPlanDetailAllotmentDBOs.add(allotmentDBO);
                        }
                    } else {
                        AdmSelectionProcessPlanDetailAllotmentDBO allotmentDBO = null;
                        if (!Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanDetailAllotmentDBOs) && detailDbo.admSelectionProcessPlanDetailAllotmentDBOs.size() > 0) {
                            for (AdmSelectionProcessPlanDetailAllotmentDBO allotmentDBO2 : detailDbo.admSelectionProcessPlanDetailAllotmentDBOs) {
                                if (!Utils.isNullOrEmpty(allotmentDBO2.id) && !Utils.isNullOrEmpty(allotmentDBO2.recordStatus) && allotmentDBO2.recordStatus == 'A') {
                                    AdmSelectionProcessPlanDetailAllotmentDBOExistMap.put(allotmentDBO2.id,allotmentDBO2);
                                }
                            }
                            for (AdmSelectionProcessPlanDetailAllotmentDTO detailAllotmentDTO : data.timewithseats) {
                                if (!Utils.isNullOrEmpty(detailAllotmentDTO.id) && AdmSelectionProcessPlanDetailAllotmentDBOExistMap.containsKey(Integer.parseInt(detailAllotmentDTO.id))) {
                                    allotmentDBO =  AdmSelectionProcessPlanDetailAllotmentDBOExistMap.get(Integer.parseInt(detailAllotmentDTO.id));
                                    allotmentDBO.modifiedUsersId = Integer.parseInt(userId);
                                    allotmentDBO = selectionProcessPlanHelper.venueTimewithseatsDtoToDbo(detailAllotmentDTO, allotmentDBO, detailDbo);
                                    detailDbo.admSelectionProcessPlanDetailAllotmentDBOs.add(allotmentDBO);
                                    AdmSelectionProcessPlanDetailAllotmentDBOExistMap.remove(Integer.parseInt(detailAllotmentDTO.id));
                                } else {
                                    allotmentDBO =  new AdmSelectionProcessPlanDetailAllotmentDBO();
                                    allotmentDBO.createdUsersId = Integer.parseInt(userId);
                                    allotmentDBO = selectionProcessPlanHelper.venueTimewithseatsDtoToDbo(detailAllotmentDTO, allotmentDBO, detailDbo);
                                    detailDbo.admSelectionProcessPlanDetailAllotmentDBOs.add(allotmentDBO);
                                }
                            }
                        }
                        if(!Utils.isNullOrEmpty(AdmSelectionProcessPlanDetailAllotmentDBOExistMap)){
                            for (AdmSelectionProcessPlanDetailAllotmentDBO subDbo:AdmSelectionProcessPlanDetailAllotmentDBOExistMap.values()) {
                                subDbo.recordStatus = 'D';
                                subDbo.modifiedUsersId = Integer.parseInt(userId);
                                detailDbo.admSelectionProcessPlanDetailAllotmentDBOs.add(subDbo);
                            }
                        }
                    }
                }
                if (!Utils.isNullOrEmpty(data.parentId)) {
                    AdmSelectionProcessPlanDBO dbo = new AdmSelectionProcessPlanDBO();
                    dbo.id = Integer.parseInt(data.parentId);
                    detailDbo.admSelectionProcessPlanDBO = dbo;
                    detailDbo.recordStatus = 'A';
                    isSaved = selectionProcessPlanTransaction.saveOrUpdateDetails(detailDbo);
                }
            } else {
                result.failureMessage = "Sorrry, you cannot add duplicate data in Selection Process Date and Venue";
            }
        }
        return isSaved;
    }

    private void convertprogPrefDtoTODbo(AdmSelectionProcessPlanDetailAddVenueDTO data,AdmSelectionProcessPlanDetailDBO detailDbo, String userId) {
        if (Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanDetailProgDBOs) || detailDbo.admSelectionProcessPlanDetailProgDBOs.size() == 0) {
            detailDbo.admSelectionProcessPlanDetailProgDBOs = new HashSet<AdmSelectionProcessPlanDetailProgDBO>();
            for (ProgramPreferenceDTO preferenceDTO : data.programWithPreferance) {
                AdmSelectionProcessPlanDetailProgDBO progDBO = new AdmSelectionProcessPlanDetailProgDBO();
                progDBO.createdUsersId = Integer.parseInt(userId);
                progDBO = selectionProcessPlanHelper.convertProgPrefDTOtoDBO(preferenceDTO, progDBO, detailDbo);
                detailDbo.admSelectionProcessPlanDetailProgDBOs.add(progDBO);
            }
        } else {
            if (!Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanDetailProgDBOs) && detailDbo.admSelectionProcessPlanDetailProgDBOs.size() > 0) {
                boolean isEditProgDBOs = false, newProgDBOs = false;
                for (AdmSelectionProcessPlanDetailProgDBO progDBO2 : detailDbo.admSelectionProcessPlanDetailProgDBOs) {
                    isEditProgDBOs = false;
                    if (!Utils.isNullOrEmpty(progDBO2.id) && !Utils.isNullOrEmpty(progDBO2.recordStatus) && progDBO2.recordStatus == 'A') {
                        for (ProgramPreferenceDTO preferenceDTO2 : data.programWithPreferance) {
                            if (!Utils.isNullOrEmpty(preferenceDTO2.id) && progDBO2.id == Integer.parseInt(preferenceDTO2.id)) {
                                progDBO2.modifiedUsersId = Integer.parseInt(userId);
                                progDBO2 = selectionProcessPlanHelper.convertProgPrefDTOtoDBO(preferenceDTO2, progDBO2, detailDbo);
                                isEditProgDBOs = true;
                            }
                            if (Utils.isNullOrEmpty(preferenceDTO2.id)) {
                                newProgDBOs = true;
                            }
                        }
                    }
                    if (!isEditProgDBOs) {
                        progDBO2.modifiedUsersId = Integer.parseInt(userId);
                        progDBO2.recordStatus = 'D';
                    }
                }
                if (newProgDBOs) {
                    for (ProgramPreferenceDTO preferenceDTO : data.programWithPreferance) {
                        if (Utils.isNullOrEmpty(preferenceDTO.id)) {
                            AdmSelectionProcessPlanDetailProgDBO progDBO = new AdmSelectionProcessPlanDetailProgDBO();
                            progDBO = selectionProcessPlanHelper.convertProgPrefDTOtoDBO(preferenceDTO, progDBO, detailDbo);
                            progDBO.createdUsersId = Integer.parseInt(userId);
                            detailDbo.admSelectionProcessPlanDetailProgDBOs.add(progDBO);
                        }
                    }
                }
            }
        }
    }

    private void convertprogPrefDtoTODbo(AdmSelectionProcessPlanDetailAddSlotDTO data,AdmSelectionProcessPlanDetailDBO detailDbo, String userId) {
        if (Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanDetailProgDBOs) || detailDbo.admSelectionProcessPlanDetailProgDBOs.size() == 0) {
            detailDbo.admSelectionProcessPlanDetailProgDBOs = new HashSet<AdmSelectionProcessPlanDetailProgDBO>();
            for (ProgramPreferenceDTO preferenceDTO : data.programWithPreferance) {
                AdmSelectionProcessPlanDetailProgDBO progDBO = new AdmSelectionProcessPlanDetailProgDBO();
                progDBO.createdUsersId = Integer.parseInt(userId);
                progDBO = selectionProcessPlanHelper.convertProgPrefDTOtoDBO(preferenceDTO, progDBO, detailDbo);
                detailDbo.admSelectionProcessPlanDetailProgDBOs.add(progDBO);
            }
        } else {
            if (!Utils.isNullOrEmpty(detailDbo.admSelectionProcessPlanDetailProgDBOs) && detailDbo.admSelectionProcessPlanDetailProgDBOs.size() > 0) {
                boolean isEditProgDBOs = false, newProgDBOs = false;
                for (AdmSelectionProcessPlanDetailProgDBO progDBO2 : detailDbo.admSelectionProcessPlanDetailProgDBOs) {
                    isEditProgDBOs = false;
                    if (!Utils.isNullOrEmpty(progDBO2.id) && !Utils.isNullOrEmpty(progDBO2.recordStatus) && progDBO2.recordStatus == 'A') {
                        for (ProgramPreferenceDTO preferenceDTO2 : data.programWithPreferance) {
                            if (!Utils.isNullOrEmpty(preferenceDTO2.id) && progDBO2.id == Integer.parseInt(preferenceDTO2.id)) {
                                progDBO2.modifiedUsersId = Integer.parseInt(userId);
                                progDBO2 = selectionProcessPlanHelper.convertProgPrefDTOtoDBO(preferenceDTO2, progDBO2, detailDbo);
                                isEditProgDBOs = true;
                            }
                            if (Utils.isNullOrEmpty(preferenceDTO2.id)) {
                                newProgDBOs = true;
                            }
                        }
                    }
                    if (!isEditProgDBOs) {
                        progDBO2.modifiedUsersId = Integer.parseInt(userId);
                        progDBO2.recordStatus = 'D';
                    }
                }
                if (newProgDBOs) {
                    for (ProgramPreferenceDTO preferenceDTO : data.programWithPreferance) {
                        if (Utils.isNullOrEmpty(preferenceDTO.id)) {
                            AdmSelectionProcessPlanDetailProgDBO progDBO = new AdmSelectionProcessPlanDetailProgDBO();
                            progDBO.createdUsersId = Integer.parseInt(userId);
                            progDBO = selectionProcessPlanHelper.convertProgPrefDTOtoDBO(preferenceDTO, progDBO, detailDbo);
                            detailDbo.admSelectionProcessPlanDetailProgDBOs.add(progDBO);
                        }
                    }
                }
            }
        }
    }

    public boolean deleteDetails(String id, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
        List<Tuple> values = selectionProcessPlanTransaction1.getCheckDetails(Integer.parseInt(id));
        List<String> duplicateData =  new ArrayList<>();
        if(!Utils.isNullOrEmpty(values)){
            for (Tuple value : values) {
                if(!Utils.isNullOrEmpty(value) && !Utils.isNullOrEmpty(value.get(0))){
                    duplicateData.add(String.valueOf(value.get(0)));
                }
            }
        }
        if(!duplicateData.isEmpty()){
                result.success = false;
                result.failureMessage = "Session was Already Selected by Applicant";
                return false;
        }
        return selectionProcessPlanTransaction1.deleteDetails(id, userId);
    }

    public AdmSelectionProcessPlanDetailDTO editDetails(List<Integer> ids, String parentId, String id) throws Exception {
        AdmSelectionProcessPlanDetailDBO detailsDbo = selectionProcessPlanTransaction.editDetails(ids, parentId, id);
        AdmSelectionProcessPlanDetailDTO detailDTO = new AdmSelectionProcessPlanDetailDTO();
        ;
        if (!Utils.isNullOrEmpty(detailsDbo)) {
            List<Integer> list = new ArrayList<Integer>();
            list.add(detailsDbo.id);
            if (!Utils.isNullOrEmpty(detailsDbo.admSelectionProcessPlanDBO)) {
                AdmSelectionProcessPlanDBO dbo = new AdmSelectionProcessPlanDBO();
                dbo = detailsDbo.admSelectionProcessPlanDBO;
                if (!Utils.isNullOrEmpty(detailsDbo.admSelectionProcessVenueCityDBO)) {
                    detailDTO.venueslist = new ArrayList<AdmSelectionProcessPlanDetailAddVenueDTO>();
                    AdmSelectionProcessPlanDetailAddVenueDTO addVenueDTO = new AdmSelectionProcessPlanDetailAddVenueDTO();
                    addVenueDTO = selectionProcessPlanHelper.convertAddVenueDboToDto(detailsDbo, addVenueDTO, dbo);
                    detailDTO.venueslist.add(addVenueDTO);
                } else {
                    detailDTO.slotslist = new ArrayList<AdmSelectionProcessPlanDetailAddSlotDTO>();
                    AdmSelectionProcessPlanDetailAddSlotDTO addSlotDTO = new AdmSelectionProcessPlanDetailAddSlotDTO();
                    addSlotDTO = selectionProcessPlanHelper.convertAddSlotDboToDto(detailsDbo, addSlotDTO, dbo);
                    detailDTO.slotslist.add(addSlotDTO);
                }
            }
            List<Tuple> details = selectionProcessPlanTransaction.getStudentApplnSPDatesBasedSPDetails(list);
            if (!Utils.isNullOrEmpty(details)) {
                details.forEach(item -> {
                    if (!Utils.isNullOrEmpty(detailDTO.slotslist)) {
                        detailDTO.slotslist.forEach(itemSlot -> {
                            if (!Utils.isNullOrEmpty(item.get("detailId")) && !Utils.isNullOrEmpty(itemSlot.id) &&
                                    !Utils.isNullOrEmpty(item.get("totalStudentAvailble")) && Integer.parseInt(item.get("detailId").toString()) == Integer.parseInt(itemSlot.id)) {
                                itemSlot.totalStudentFilled = item.get("totalStudentAvailble").toString();
                            }
                        });
                    } else if (!Utils.isNullOrEmpty(detailDTO.venueslist)) {
                        detailDTO.venueslist.forEach(itemVenue -> {
                            if (!Utils.isNullOrEmpty(item.get("detailId")) && !Utils.isNullOrEmpty(itemVenue.id) &&
                                    !Utils.isNullOrEmpty(item.get("totalStudentAvailble")) && Integer.parseInt(item.get("detailId").toString()) == Integer.parseInt(itemVenue.id)) {
                                itemVenue.totalStudentFilled = item.get("totalStudentAvailble").toString();
                            }
                        });
                    }
                });
            }
        }
        return detailDTO;
    }

    public Flux<AdmSelectionProcessVenueCityDTO> getCityVenueList(Boolean isConductedInIndia) {
        return selectionProcessPlanTransaction1.getCityVenueList(isConductedInIndia).flatMapMany(Flux::fromIterable).map(this::convertCityDboToDto);
    }

    private AdmSelectionProcessVenueCityDTO convertCityDboToDto(AdmSelectionProcessVenueCityDBO dbo) {
        AdmSelectionProcessVenueCityDTO dto = new AdmSelectionProcessVenueCityDTO();
        dto.setId(String.valueOf(dbo.getId()));
        dto.setAddress(dbo.getVenueAddress());
        dto.setVenue(dbo.getVenueName());
        dto.setMaxSeats(String.valueOf(dbo.getVenueMaxSeats()));
        if(!Utils.isNullOrEmpty(dbo.getSelectionProcessMode())) {
            dto.setMode(new ExModelBaseDTO());
            dto.getMode().setId(String.valueOf(dbo.getId()));
            dto.getMode().setText(dbo.getSelectionProcessMode());
        }
        if(!Utils.isNullOrEmpty(dbo.getErpCountryDBO())) {
            dto.setCountry(new ExModelBaseDTO());
            dto.getCountry().setId(String.valueOf(dbo.getErpCountryDBO().getId()));
            dto.getCountry().setText(dbo.getErpCountryDBO().getCountryName());
        }
        if(!Utils.isNullOrEmpty(dbo.getErpStateDBO())) {
            dto.setState(new ExModelBaseDTO());
            dto.getState().setId(String.valueOf(dbo.getErpStateDBO().getId()));
            dto.getState().setText(dbo.getErpStateDBO().getStateName());
        }
        return dto;
    }

    public Mono<ApiResult> getCheckDetails(Integer detailsId) {
        List<Tuple> values = selectionProcessPlanTransaction1.getCheckDetails(detailsId);
        if(!Utils.isNullOrEmpty(values)){
          return  Mono.error(new DuplicateException("Session was already selected by applicants"));
        }
        return Mono.just(Boolean.TRUE).map(Utils::responseResult);
    }

    public Mono<ApiResult> getCheckPlan(Integer planId,List<Integer> progIds) {
        List<Tuple> values = selectionProcessPlanTransaction1.getCheckPlan(planId);
        List<Tuple> values1 = selectionProcessPlanTransaction1.getCheckProg(planId,progIds);
        if(!Utils.isNullOrEmpty(values)){
            return  Mono.error(new DuplicateException("Session was already selected by applicants"));
        } else if(!Utils.isNullOrEmpty(values1)){
            return  Mono.error(new DuplicateException("Programmes was already used in slot details"));
        }
        return Mono.just(Boolean.TRUE).map(Utils::responseResult);
    }

    public Mono<ApiResult> isApplicationNumberCreated(ArrayList<Integer> batchIds) {
        var apiResult = new ApiResult<>();
        var result = selectionProcessPlanTransaction1.isApplicationNumberCreated(batchIds);
        if(!Utils.isNullOrEmpty(result)) {
            apiResult.setSuccess(false);
            apiResult.setFailureMessage(result.stream()
                    .map(s -> (result.indexOf(s) + 1) + ". " + s)
                    .collect(Collectors.joining(" ")));
        } else {
            apiResult.setSuccess(true);
        }
        return Mono.just(apiResult);
    }
}
