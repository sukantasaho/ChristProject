package com.christ.erp.services.handlers.employee.settings;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnNumberGenerationDBO;
import com.christ.erp.services.dto.employee.settings.EmpApplnNumberGenerationDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.employee.settings.ApplicationNumbersTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApplicationNumbersHandler {

    @Autowired
    ApplicationNumbersTransaction applicationNumbersTransaction;

    public Flux<EmpApplnNumberGenerationDTO> getGridData() {
    	return applicationNumbersTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
    }
	
    public EmpApplnNumberGenerationDTO convertDboToDto (EmpApplnNumberGenerationDBO dbo) {
    	EmpApplnNumberGenerationDTO dto = new EmpApplnNumberGenerationDTO();
	    BeanUtils.copyProperties(dbo, dto);
	    return dto;
    }
    
    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> saveOrUpdate(Mono<EmpApplnNumberGenerationDTO> dto, String userId) {
    	return dto
    	   .handle((empApplnNumberGenerationDTO, synchronousSink) -> {
    		   if(applicationNumbersTransaction.isApplicationRangeExists(empApplnNumberGenerationDTO)) {
    			   synchronousSink.error(new DuplicateException("Given Application Number Range Already Exists"));	
    		   }
    		   else if(applicationNumbersTransaction.CalendarYearDuplicateCheck(empApplnNumberGenerationDTO)) {
    			   synchronousSink.error(new DuplicateException("Duplicate Calendar Year"));
    		   } 
    		   else if(empApplnNumberGenerationDTO.getIsCurrentRange()) {
    			   EmpApplnNumberGenerationDBO dbo = applicationNumbersTransaction.isRangeExistDuplicate(empApplnNumberGenerationDTO);
    			   if(!Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(dbo.getCalendarYear())) {
    				   synchronousSink.error(new DuplicateException("Current Range is already set as Yes for " +dbo.getCalendarYear()));
					}
    			   else {
    				   synchronousSink.next(empApplnNumberGenerationDTO); 
				    }
    		   }
    		   else {
    			   synchronousSink.next(empApplnNumberGenerationDTO); 
    		   }
    	    }).cast(EmpApplnNumberGenerationDTO.class)	 
    	     .map(data -> convertDtoToDbo(data, userId))
    	      .flatMap(s -> {
    		   if(!Utils.isNullOrEmpty(s.getEmpApplnNumberGenerationId())) {
    			   applicationNumbersTransaction.update(s); 
    		   } else { 
    			   applicationNumbersTransaction.save(s); 
    		   }
    		   return Mono.just(Boolean.TRUE);
    	   }).map(Utils::responseResult); 
    }
	
    public Mono<EmpApplnNumberGenerationDTO> edit(int id) {
	    return applicationNumbersTransaction.edit(id).map(this::convertDboToDto);
    }
	
    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> delete(int id, String userId) {
	    return applicationNumbersTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
    }
	
    public EmpApplnNumberGenerationDBO convertDtoToDbo(EmpApplnNumberGenerationDTO dto, String userId) {
	    EmpApplnNumberGenerationDBO dbo = new EmpApplnNumberGenerationDBO();
        BeanUtils.copyProperties(dto, dbo);
        dbo.setCreatedUsersId(Integer.parseInt(userId));
        if (!Utils.isNullOrEmpty(dto.getEmpApplnNumberGenerationId())) {
        	dbo.setModifiedUsersId(Integer.parseInt(userId)); 
        } 
        if (!Utils.isNullOrEmpty(dbo.getCurrentApplnNo())) {
        	dbo.setCurrentApplnNo(dbo.getApplnNumberFrom());
        }
        dbo.setRecordStatus('A');
        return dbo; 
    }
}
