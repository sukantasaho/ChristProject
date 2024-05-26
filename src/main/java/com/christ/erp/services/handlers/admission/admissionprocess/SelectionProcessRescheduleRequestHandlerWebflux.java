package com.christ.erp.services.handlers.admission.admissionprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.admission.admissionprocess.SelectionProcessRescheduleRequestDTO;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessDTO;
import com.christ.erp.services.transactions.admission.admissionprocess.SelectionProcessRescheduleRequestTransactionWebflux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;

@Service
public class SelectionProcessRescheduleRequestHandlerWebflux {

    @Autowired
    private SelectionProcessRescheduleRequestTransactionWebflux transactionWebflux;

    public Mono<SelectionProcessRescheduleRequestDTO> getApplicantDetails(String studentApplnEntriesId) {
        SelectionProcessRescheduleRequestDTO selectionProcessRescheduleRequestDTOWebflux = new SelectionProcessRescheduleRequestDTO();
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            StudentApplnEntriesDBO studentApplnEntriesDBO = transactionWebflux.getStudentApplnEntriesDBO(studentApplnEntriesId);
            if(!Utils.isNullOrEmpty(studentApplnEntriesDBO)){
                selectionProcessRescheduleRequestDTOWebflux.setApplicantName(studentApplnEntriesDBO.getApplicantName());
                if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO())){
                    selectionProcessRescheduleRequestDTOWebflux.setErpCampusProgrammeMappingId(String.valueOf(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getId()));
                    selectionProcessRescheduleRequestDTOWebflux.setProgramme(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
                    if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO())){
                        selectionProcessRescheduleRequestDTOWebflux.setCampus(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
                    }else if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getErpLocationDBO())){
                        selectionProcessRescheduleRequestDTOWebflux.setCampus(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getErpLocationDBO().getLocationName());
                    }
                    //ApiResult<SelectionProcessDTO> result = new ApiResult<>();
                    //CommonApiHandler.getInstance().getSelectionProcessPrefferedDates(result,String.valueOf(studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getId()));
                }
                if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getAdmSelectionProcessDBOS())){
                    studentApplnEntriesDBO.getAdmSelectionProcessDBOS().stream().filter(admSelectionProcessDBO -> admSelectionProcessDBO.getRecordStatus()=='A').forEach(admSelectionProcessDBO -> {
                        if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO())){
                            SelectionProcessDTO selectionProcessDTO = new SelectionProcessDTO();
                            if(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getProcessOrder() == 1){
                                if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO())){
                                    selectionProcessDTO.setSelectionProcessRoundOneDate(formatter.format(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate()));
                                    selectionProcessDTO.setSelectionProcessRoundOneVenue(admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO().getCenterName());
                                }else{
                                    selectionProcessDTO.setSelectionProcessRoundOneDate(formatter.format(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate()));
                                    selectionProcessDTO.setSelectionProcessRoundOneVenue(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
                                }
                            }else{
                                if(!Utils.isNullOrEmpty(admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO())){
                                    selectionProcessDTO.setSelectionProcessRoundTwoDate(formatter.format(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate()));
                                    selectionProcessDTO.setSelectionProcessRoundTwoVenue(admSelectionProcessDBO.getAdmSelectionProcessCenterDetailsDBO().getCenterName());
                                }else{
                                    selectionProcessDTO.setSelectionProcessRoundTwoDate(formatter.format(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getSelectionProcessDate()));
                                    selectionProcessDTO.setSelectionProcessRoundTwoVenue(admSelectionProcessDBO.getAdmSelectionProcessPlanDetailDBO().getAdmSelectionProcessVenueCityDBO().getVenueName());
                                }
                            }
                            selectionProcessRescheduleRequestDTOWebflux.setSelectionProcessDTO(selectionProcessDTO);
                        }
                    });
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Mono.just(selectionProcessRescheduleRequestDTOWebflux);
    }

    public Mono<ApiResult> getSelectionProcessPrefferedDates(String erpCampusProgrammeMappingId){
        return transactionWebflux.getSelectionProcessPrefferedDates("254");
    }
}
