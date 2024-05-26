package com.christ.erp.services.handlers.account.fee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeCategoryDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDurationsDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDurationsDetailDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.account.fee.DemandDTO;
import com.christ.erp.services.dto.account.fee.DemandProgramDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.helpers.account.fee.DemandSlipGenertaionHelper;
import com.christ.erp.services.transactions.account.fee.DemandSlipGenerationTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class DemandSlipGenerationHandler {
	
	@Autowired
	DemandSlipGenerationTransaction demandSlipGenerationTransaction;
	@Autowired
	DemandSlipGenertaionHelper demandSlipGenertaionHelper;
	
	public Flux<SelectDTO> getAcademicYearNo(int academicYearId, int campusId) {
		return demandSlipGenerationTransaction.getBatchId(academicYearId, campusId).flatMapMany(Flux::fromIterable).map(this::convertYearNoToDTO);
	}
    public SelectDTO convertYearNoToDTO(int yearNo) {
    	SelectDTO dto = new SelectDTO();
    	dto.setLabel(Integer.toString(yearNo));
    	dto.setValue(Integer.toString(yearNo));
    	return dto;
    }
    
	public Flux<SelectDTO> getAllBatches(int academicYearId, int campusId) {
		return demandSlipGenerationTransaction.getAllTheBatches(academicYearId, campusId).flatMapMany(Flux::fromIterable).map(this::convertYearNoToDTO);
	}
	
	public SelectDTO convertAcaToDTO(int yearNo) {
    	SelectDTO dto = new SelectDTO();
    	dto.setLabel(Integer.toString(yearNo));
    	dto.setValue(Integer.toString(yearNo));
    	return dto;
	}
	
	public Flux<DemandProgramDTO> getAllProgramsByYearNo(int academicYearId, int campusId, int yearNo) {
		List<AcaDurationDetailDBO> durationList = demandSlipGenerationTransaction.getAllProgramsByYearNo(academicYearId, campusId, yearNo);
		if(!Utils.isNullOrEmpty(durationList)) {
			return convertAcaToDemandDTO(durationList, academicYearId);
		}else {
			return Flux.just(new DemandProgramDTO());
		}
	}
	
	public Flux<DemandProgramDTO> convertAcaToDemandDTO(List<AcaDurationDetailDBO> durationList, int academicYearId) {
		Set<Integer> batchIdSet = durationList.stream().collect(Collectors.groupingBy(s->s.getAcaBatchDBO().getId())).keySet();

		List<StudentDBO> studentList = demandSlipGenerationTransaction.getStudentsBybatchId(batchIdSet, null, true, null, null);
		Map<Integer, List<StudentDBO>> studentBatchwiseMap = studentList.stream().collect(Collectors.groupingBy(a->a.getAcaBatchDBO().getId()));
    	
		List<AccFeeDemandDBO> existingDemandList =  demandSlipGenerationTransaction.getDemandGeneratedForBatch(batchIdSet, academicYearId);
		Map<Integer, List<AccFeeDemandDBO>>  batchDemandMap = existingDemandList.stream().collect(Collectors.groupingBy(d->d.getStudentDBO().getAcaBatchDBO().getId()));
    	
		Flux<DemandProgramDTO> demandDtoFlux = Flux.push(f->{
	    	durationList.forEach(d->{
	    		DemandProgramDTO demandProgramDTO = new DemandProgramDTO();
	    		demandProgramDTO.setId(d.getAcaBatchDBO().getId());
	    		demandProgramDTO.setProgramName(d.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName() + "("+
	        			d.getAcaBatchDBO().getBatchName() + ")");
	        	if(!Utils.isNullOrEmpty(studentBatchwiseMap) && !Utils.isNullOrEmpty(studentBatchwiseMap.get(d.getAcaBatchDBO().getId()))) {
	        		demandProgramDTO.setStatus(Integer.toString(studentBatchwiseMap.get(d.getAcaBatchDBO().getId()).size()));
	        		if(!Utils.isNullOrEmpty(batchDemandMap) && !Utils.isNullOrEmpty(batchDemandMap.get(d.getAcaBatchDBO().getId()))) {
	        			demandProgramDTO.setStatus(Integer.toString(batchDemandMap.get(d.getAcaBatchDBO().getId()).size()) +  "/" + demandProgramDTO.getStatus());
	        		}
	        		else {
	        			demandProgramDTO.setStatus("0/" + demandProgramDTO.getStatus());
	        		}
	        	}
	        	else {
	        		demandProgramDTO.setStatus("0/0");
	        	}
	        	f.next(demandProgramDTO);
	    	});
	    	f.complete();
    	});
    	return demandDtoFlux;
	}

	public Mono<ApiResult<DemandDTO>> generateDemand(Mono<DemandDTO> data, String userId) {
		var apiResult = new ApiResult<DemandDTO>();
		apiResult.setSuccess(true);
		return data.map(demandDTO-> {
			Map<Integer, Integer> selectedBatchIdMap = demandDTO.getProgrammeList().stream().collect(Collectors.toMap(a->a.getId(), a->a.getId()));
			List<Set<AccFeeDemandDBO>> demandDboList = convertDemandDTOtoDemandDBO(selectedBatchIdMap, userId, apiResult,demandDTO);
			 if(apiResult.success) {
				demandSlipGenerationTransaction.saveDemand(demandDboList);
			}
			return apiResult;
		});
	}
	
	private List<Set<AccFeeDemandDBO>> convertDemandDTOtoDemandDBO(Map<Integer, Integer> selectedBatchIdMap, String userId, ApiResult<DemandDTO> apiResult,
			DemandDTO demandDTO){
		List<StudentDBO> studentList = demandSlipGenerationTransaction.getStudentsBybatchId(selectedBatchIdMap.keySet(), Integer.parseInt(demandDTO.getAcademicYearDTO().getValue()), false, demandDTO.getRegisterNoFrom(), demandDTO.getRegisterNoTo());
		List<Set<AccFeeDemandDBO>> demandDboList = new ArrayList<Set<AccFeeDemandDBO>>();
		List<String> noDefinitionList = new ArrayList<String>();
		if(!Utils.isNullOrEmpty(studentList)) {
			List<AccBatchFeeDBO> batchFeeDBOList = demandSlipGenerationTransaction.generateTuitionFeeDemand(selectedBatchIdMap.keySet(), Integer.parseInt(demandDTO.getAcademicYearDTO().getValue()));
			if(!Utils.isNullOrEmpty(batchFeeDBOList)) {
				Map<Pair<Integer,String>, List<AccBatchFeeDBO>> feeTypewiseBatchFeeMap = new HashMap<Pair<Integer,String>, List<AccBatchFeeDBO>>();
				if(!Utils.isNullOrEmpty(batchFeeDBOList)) {
					feeTypewiseBatchFeeMap = batchFeeDBOList.stream().collect(Collectors.groupingBy(f->(Pair.of(f.getAcaBatchDBO().getId(),  f.getFeeCollectionSet() == null ?"common": f.getErpSpecializationDBO()!=null? f.getFeeCollectionSet() + "_" + f.getErpSpecializationDBO().getId():f.getFeeCollectionSet()))));
				}
				Map<Pair<Integer,String>, List<AccBatchFeeDBO>> feeTypewiseBatchFeeMapNew = new HashMap<Pair<Integer,String>, List<AccBatchFeeDBO>>();
				if(!Utils.isNullOrEmpty(feeTypewiseBatchFeeMap)) {
					feeTypewiseBatchFeeMapNew.putAll(feeTypewiseBatchFeeMap);
				}
				if(!Utils.isNullOrEmpty(feeTypewiseBatchFeeMapNew)) {
					studentList.forEach(studentDBO->{
						List<AccBatchFeeDBO> accBatchFeeDBOList = new ArrayList<AccBatchFeeDBO>();
						Set<AccFeeDemandDBO> accFeeDemandDBOSet = new HashSet<AccFeeDemandDBO>();
						if(!Utils.isNullOrEmpty(studentDBO.getErpSpecializationDBO())) {
							accBatchFeeDBOList = feeTypewiseBatchFeeMapNew.get(Pair.of(studentDBO.getAcaBatchDBO().getId(),"Specialisation wise Fee" + "_" + studentDBO.getErpSpecializationDBO().getId()));
						}
						else if(!Utils.isNullOrEmpty(studentDBO.getModeOfStudy())) {
							accBatchFeeDBOList = feeTypewiseBatchFeeMapNew.get(Pair.of(studentDBO.getAcaBatchDBO().getId(), studentDBO.getModeOfStudy()));
						}
						if(Utils.isNullOrEmpty(accBatchFeeDBOList)) {
							accBatchFeeDBOList = feeTypewiseBatchFeeMapNew.get(Pair.of(studentDBO.getAcaBatchDBO().getId(),"common"));
						}
						accFeeDemandDBOSet = createBatchFeeCategory(accBatchFeeDBOList, studentDBO, userId);
						if(!Utils.isNullOrEmpty(accFeeDemandDBOSet)) {
							demandDboList.add(accFeeDemandDBOSet);
						}
						else {
							noDefinitionList.add(studentDBO.getErpCampusProgrammeMappingId().getErpProgrammeDBO().getProgrammeName() + "(" + studentDBO.getAcaBatchDBO().getBatchName() + ")");	
						}
					});
				}
				if(!demandDTO.getNotFoundAccepted() && noDefinitionList.size() > 0) {
					apiResult.setSuccess(false);
					StringBuffer msg = new StringBuffer("");
					msg.append("Definition not found for the batches ");
					noDefinitionList.forEach(def->{
						 msg.append(def);
					});	
					apiResult.setDto(new DemandDTO());
					apiResult.getDto().setDefinitionNotFoundDet(msg.toString());
				}
			}
			else {
				apiResult.setSuccess(false);
				apiResult.setFailureMessage("Definition not found for the selected programs !");
			}
		}
		else {
			apiResult.setSuccess(false);
			apiResult.setFailureMessage("No students Found for demand generation !");
		}
		return demandDboList;
	}
	public Set<AccFeeDemandDBO> createBatchFeeCategory(List<AccBatchFeeDBO> accBatchFeeDBOList, StudentDBO studentDBO, String userId) {
		Set<AccFeeDemandDBO> accFeeDemandDBOSet = new HashSet<AccFeeDemandDBO>();
		if(!Utils.isNullOrEmpty( accBatchFeeDBOList)) {
			List<AccBatchFeeDurationsDBO> accBatchFeeDurationsDBOList = accBatchFeeDBOList.get(0).getAccBatchFeeDurationsDBOSet().stream().toList();
			AccBatchFeeDurationsDBO accBatchFeeDurationsDBO = new AccBatchFeeDurationsDBO();
			if(!Utils.isNullOrEmpty(accBatchFeeDurationsDBOList)) {
				accBatchFeeDurationsDBO = accBatchFeeDurationsDBOList.get(0);
			}
			if(!Utils.isNullOrEmpty( accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet())) {
				List<AccBatchFeeDurationsDetailDBO> accBatchFeeDurationsDetailDBOList = accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet().stream()
						.filter(a->a.getAcaDurationDetailDBO() == null).toList();
				AccBatchFeeDurationsDetailDBO accBatchFeeDurationsDetailDBO = new AccBatchFeeDurationsDetailDBO();
				if(!Utils.isNullOrEmpty(accBatchFeeDurationsDetailDBOList)) {
					accBatchFeeDurationsDetailDBO = accBatchFeeDurationsDetailDBOList.get(0);
				}
				if(!Utils.isNullOrEmpty(accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet())) {
					AccBatchFeeCategoryDBO accBatchFeeCategoryDBO = new AccBatchFeeCategoryDBO();
					List<AccBatchFeeCategoryDBO> accBatchFeeCategoryDBOList = accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet().stream()
							.filter(c->c.getErpAdmissionCategoryDBO().getId() == studentDBO.getErpAdmissionCategoryDBO().getId()).toList();
					if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBOList)) {
						accBatchFeeCategoryDBO = accBatchFeeCategoryDBOList.get(0);
					}
					if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBO)) {
						accFeeDemandDBOSet = demandSlipGenertaionHelper.copyAccBatchFeeToDemandDBO(accBatchFeeCategoryDBO, userId, null, null, studentDBO, null);
					}
				}
			}
		}
		return accFeeDemandDBOSet;
	}
}
