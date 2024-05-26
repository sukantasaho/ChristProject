package com.christ.erp.services.handlers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.admission.settings.Boardtype;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.admission.settings.QualificationLevelsTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class QualificationLevelsHandler {

    @Autowired
    QualificationLevelsTransaction admQualificationTransaction;


    public Mono<ApiResult> saveOrUpdate(Mono<AdmQualificationListDTO> dto, String userId) {
        return dto
                .handle((AdmQualificationListDTO, synchronousSink) -> {
                    boolean istrue = admQualificationTransaction.duplicateCheck(AdmQualificationListDTO);
                    boolean istrue1 = admQualificationTransaction.duplicateOrderCheck(AdmQualificationListDTO);
                    if (istrue) {
                        synchronousSink.error(new DuplicateException("Duplicate entry for qualificationName/shortName"));
                    } else if(istrue1){
                        synchronousSink.error(new DuplicateException("Duplicate entry for display order"));
                    }
                    else {
                        synchronousSink.next(AdmQualificationListDTO);
                    }
                }).cast(AdmQualificationListDTO.class)
                .map(data -> convertDtoToDbo(data, userId))
                .flatMap(s -> {
                    if (!Utils.isNullOrEmpty(s.getId())) {
                        admQualificationTransaction.update(s);
                    } else {
                        admQualificationTransaction.save(s);
                    }
                    return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
    }




    public AdmQualificationListDBO convertDtoToDbo(AdmQualificationListDTO dto, String userId) {
        AdmQualificationListDBO dbo = null;
        if (!Utils.isNullOrEmpty(dto.getId())) {
            dbo = admQualificationTransaction.getAdmQualificationDetails(dto.getId());
            dbo.setModifiedUsersId(Integer.parseInt(userId));
        } else {
            dbo = new AdmQualificationListDBO();
        }

        dbo.setQualificationName(dto.getQualificationName());
        dbo.setQualificationOrder(dto.getQualificationOrder());
        dbo.setShortName(dto.getShortName());
//        dbo.setAdditionalDocument(dto.isAdditionalDocument());
        dbo.setIsAdditionalDocument(dto.getIsAdditionalDocument());
        if(!Utils.isNullOrEmpty(dto.getBoardType())){
            dbo.setBoardtype(Boardtype.valueOf(dto.getBoardType().getValue()));
        } else {
            dbo.setBoardtype(null);
        }
        dbo.setCreatedUsersId(dto.getCreatedUsersId());
//        dbo.setCreatedTime(dto.getCreatedTime());
//        dbo.setModifiedUsersId(dto.getModifiedUsersId());
//        dbo.setModifiedTime(dto.getModifiedTime());
        dbo.setRecordStatus('A');
        return dbo;
    }

    public Mono<ApiResult> delete(int id, String userId) {
        return admQualificationTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
    }


    public Mono<AdmQualificationListDTO> edit(int id) {
        return Mono.just(admQualificationTransaction.getAdmQualificationDetails(id)).map(this::convertDboToDto);
    }

    public Flux<AdmQualificationListDTO> getGridData() {
        return admQualificationTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
    }

    public AdmQualificationListDTO convertDboToDto(AdmQualificationListDBO dbo) {
        AdmQualificationListDTO dto = new AdmQualificationListDTO();
        dto.setId(dbo.getId());
        dto.setQualificationName(dbo.getQualificationName());
        dto.setQualificationOrder(dbo.getQualificationOrder());
        dto.setShortName(dbo.getShortName());
//        dto.setAdditionalDocument(dbo.getIsAdditionalDocument());
        dto.setIsAdditionalDocument(dbo.getIsAdditionalDocument());
        if(!Utils.isNullOrEmpty(dbo.getBoardtype())){
            dto.setBoardType(new SelectDTO());
            dto.getBoardType().setValue(String.valueOf(dbo.getBoardtype()));
            dto.getBoardType().setLabel(String.valueOf(dbo.getBoardtype()));
        }
//        // Null check
//        if (dbo.getBoardtype() != null) {
//            dto.setBoardType(String.valueOf(dbo.getBoardtype()));
//        }

        dto.setCreatedUsersId(dbo.getCreatedUsersId());
//        dto.setCreatedTime(dbo.getCreatedTime());
        dto.setModifiedUsersId(dbo.getModifiedUsersId());
//        dto.setModifiedTime(dbo.getModifiedTime());
        dto.setRecordStatus(dbo.getRecordStatus());

        return dto;
    }

}
