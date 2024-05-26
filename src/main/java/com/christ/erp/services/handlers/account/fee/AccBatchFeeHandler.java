package com.christ.erp.services.handlers.account.fee;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionDBO;
import com.christ.erp.services.dbobjects.common.AccFeeDemandAdjustmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryCampusMappingDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDurationsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.helpers.account.fee.AccBatchFeeHelper;
import com.christ.erp.services.transactions.account.fee.AccBatchFeeTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccBatchFeeHandler {
	
	@Autowired
	AccBatchFeeTransaction accBatchFeeTransaction;
	
	@Autowired
	AccBatchFeeHelper accBatchFeeHelper;
	
	public Flux<AccBatchFeeDTO> getGridData() {
		return accBatchFeeTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertAccBatchFeeDBOToDto);
	}

	public AccBatchFeeDTO convertAccBatchFeeDBOToDto(AccBatchFeeDBO accBatchFeeDBO) {
		AccBatchFeeDTO accBatchFeeDTO = null;
		if(!Utils.isNullOrEmpty(accBatchFeeDBO)) {
			accBatchFeeDTO = new AccBatchFeeDTO();
			accBatchFeeDTO.setId(accBatchFeeDBO.getId());
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO())) {
				if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO().getErpCampusProgrammeMappingDBO())) {
					accBatchFeeDTO.setProgramAndCampus(accBatchFeeDBO.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName() +
							" (" + accBatchFeeDBO.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName() +	 ")");
				}
				if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO())) {
					accBatchFeeDTO.setBatchYearAndBatchName(accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO().getErpAcademicYearDBO().getAcademicYearName() 
							+ " (" + accBatchFeeDBO.getAcaBatchDBO().getBatchName() + ")");
				}
			}
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getFeeCollectionSet())) {
				SelectDTO feeCollectioSetDto = new SelectDTO();
				feeCollectioSetDto.setLabel(accBatchFeeDBO.getFeeCollectionSet());
				feeCollectioSetDto.setValue(accBatchFeeDBO.getFeeCollectionSet());
				accBatchFeeDTO.setFeeCollectionSet(feeCollectioSetDto);
			}
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getErpSpecializationDBO())) {
				SelectDTO erpSpecializationDTO = new SelectDTO();
				erpSpecializationDTO.setValue(Integer.toString(accBatchFeeDBO.getErpSpecializationDBO().getId()));
				erpSpecializationDTO.setLabel(accBatchFeeDBO.getErpSpecializationDBO().getSpecializationName());
				accBatchFeeDTO.setErpSpecializationDTO(erpSpecializationDTO);
			}
		}
		return accBatchFeeDTO;
	}
	
	public Flux<SelectDTO> getBatchNameByProgramAndYear(int programId, int batchYearId) {
		return accBatchFeeTransaction.getBatchNameByProgramAndYear(programId, batchYearId).flatMapMany(Flux::fromIterable).map(this::convertAcaBatchDBOToAcaBatchSelectTO);
	}
	
	public SelectDTO convertAcaBatchDBOToAcaBatchSelectTO(AcaBatchDBO acaBatchDBO) {
		SelectDTO batchYearDTO = null;
		if(!Utils.isNullOrEmpty(acaBatchDBO)) {
			if(!Utils.isNullOrEmpty(acaBatchDBO.getBatchName())) {
				batchYearDTO = new SelectDTO();
				batchYearDTO.setValue(Integer.toString(acaBatchDBO.getId()));
				if(!Utils.isNullOrEmpty(acaBatchDBO.getErpCampusProgrammeMappingDBO()) && !Utils.isNullOrEmpty(acaBatchDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO())) {
					batchYearDTO.setLabel(acaBatchDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
				}
			}
		}
		return batchYearDTO;
	}

	public Mono<List<AccBatchFeeDurationsDTO>> getDetails(int batchId) {
		AcaBatchDBO acaBatchDBO =  accBatchFeeTransaction.getAcaBatchDBO(batchId);
		
		String type = "";
		if(!Utils.isNullOrEmpty(acaBatchDBO)) {
			if(!Utils.isNullOrEmpty(acaBatchDBO.getErpProgrammeBatchwiseSettingsDBO()) && !Utils.isNullOrEmpty(acaBatchDBO.getErpProgrammeBatchwiseSettingsDBO().getAcaSessionTypeDBO())) {
				type = acaBatchDBO.getErpProgrammeBatchwiseSettingsDBO().getAcaSessionTypeDBO().getCurriculumCompletionType();
			}
		}
		int campusId = 0;
		List<AcaDurationDetailDBO> durationList = new ArrayList<AcaDurationDetailDBO>();
		if(type.equalsIgnoreCase("CREDIT") || type.equalsIgnoreCase("SUBMISSION")) {
			if(!Utils.isNullOrEmpty(acaBatchDBO.getBatchStartingAcaDurationDetail())) {
				AcaDurationDetailDBO acaDurationDetailDBO = acaBatchDBO.getBatchStartingAcaDurationDetail();
				AcaSessionDBO acaSessionDBO = acaDurationDetailDBO.getAcaSessionDBO();
				acaSessionDBO.setTermNumber(1);
				acaSessionDBO.setYearNumber(1);
				acaDurationDetailDBO.setAcaSessionDBO(acaSessionDBO);
				durationList.add(acaBatchDBO.getBatchStartingAcaDurationDetail());
			}
			campusId = acaBatchDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId();
		}
		else {
			durationList = accBatchFeeTransaction.getDurationsByBatch(batchId);	
			campusId = durationList.get(0).getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId();
		}
		List<AccBatchFeeDurationsDTO> durationDTOList = accBatchFeeHelper.getAccBatchFeeDurationDTOList(durationList);
		List<ErpAdmissionCategoryCampusMappingDBO> admissionCategoryList = accBatchFeeTransaction.getAdmissionCategoryByCampus(campusId);
		List<AccAccountsDBO> accountNoList = accBatchFeeTransaction.getAccountNosByCampus(campusId);
		List<AccFeeHeadsDBO> feeHeadsList = accBatchFeeTransaction.getFeeHeads();
		List<AccBatchFeeDurationsDTO> accBactchFeeDurationsDTOList = new ArrayList<AccBatchFeeDurationsDTO>();
		if(!Utils.isNullOrEmpty(durationDTOList) && !Utils.isNullOrEmpty(admissionCategoryList) && !Utils.isNullOrEmpty(accountNoList) &&
				!Utils.isNullOrEmpty(feeHeadsList)) {
			accBactchFeeDurationsDTOList =  accBatchFeeHelper.convertDBOsToAccBatchFeeDurationsDTO(durationDTOList, 
				admissionCategoryList, accountNoList, feeHeadsList);
		}
		return Mono.just(accBactchFeeDurationsDTOList);
	}
	
	public Mono<ApiResult> saveOrUpdate(Mono<AccBatchFeeDTO> dto, String userId){
		return dto
			.handle((accBatchFeeDTO, synchronousink)->{
			 boolean isTrue = accBatchFeeTransaction.duplicateCheck(accBatchFeeDTO);
				if(isTrue) {
					synchronousink.error(new DuplicateException("Fee Definition already exists."));
				}
				else {
					synchronousink.next(accBatchFeeDTO);
				}
			}).cast(AccBatchFeeDTO.class).map(data-> accBatchFeeHelper.convertAccBatchFeeDTOToDbo(data, userId))
				.flatMap(accBatchFeeDBO->{
					if(!Utils.isNullOrEmpty(accBatchFeeDBO.getId())) {
						accBatchFeeTransaction.update(accBatchFeeDBO);
					}
					else {
						accBatchFeeTransaction.save(accBatchFeeDBO);
					}
					return Mono.just(Boolean.TRUE);
			 }).map(Utils::responseResult);
	}
	
	public Mono<AccBatchFeeDTO> edit(int id) {
		AccBatchFeeDBO accBatchFeeDBO = accBatchFeeTransaction.getAccBatchFeeById(id);
		AccBatchFeeDTO accBatchFeeDTO =  accBatchFeeHelper.convertAccBatchFeeDboToDto(accBatchFeeDBO);
        return Mono.just(accBatchFeeDTO);
    }
	
	public Mono<ApiResult> delete(int id, String userId) {
		return accBatchFeeTransaction.delete(id, userId).map(Utils::responseResult);
	}
	
	public Flux<SelectDTO> getDemandAdjustmentCateory() {
		return accBatchFeeTransaction.getDemandAdjustmentCatgeory().flatMapMany(Flux::fromIterable).map(this::convertAccFeeDemandAdjustmentCategoryToSelectTO);
	}
	public SelectDTO convertAccFeeDemandAdjustmentCategoryToSelectTO(AccFeeDemandAdjustmentCategoryDBO accFeeDemandAdjustmentCategoryDBO) {
		SelectDTO accFeeDemandAdjustmentCategoryDTO = null;
		if(!Utils.isNullOrEmpty(accFeeDemandAdjustmentCategoryDBO)) {
			if(!Utils.isNullOrEmpty(accFeeDemandAdjustmentCategoryDBO.getAdjustmentCategory())) {
				accFeeDemandAdjustmentCategoryDTO = new SelectDTO();
				accFeeDemandAdjustmentCategoryDTO.setValue(Integer.toString(accFeeDemandAdjustmentCategoryDBO.getId()));
				accFeeDemandAdjustmentCategoryDTO.setLabel(accFeeDemandAdjustmentCategoryDBO.getAdjustmentCategory());
			}
		}
		return accFeeDemandAdjustmentCategoryDTO;
	}	
}
