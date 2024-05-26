package com.christ.erp.services.controllers.account.common;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.account.common.CommonAccountHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Protected/Accounts/CommonAccount")
public class CommonAccountController {
	
	@Autowired
	CommonAccountHandler commonAccountHandler;
	
	@RequestMapping(value = "/getFinancialYears", method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getFInacialYears() {
		ApiResult<List<LookupItemDTO>> categories = new ApiResult<List<LookupItemDTO>>();
		try {
		   	DBGateway.runJPA(new ITransactional() {
		   		@Override
	            public void onRun(EntityManager context) {
	            	String str="select acc_financial_year_id as ID, financial_year as Text from acc_financial_year  where record_status= 'A' order by financial_year desc";
	            	Utils.getDropdownData(categories, context, str.toString(), null);
	            }
	            @Override
	            public void onError(Exception error) {
	           	 categories.success = false;
	           	 categories.dto = null;
	           	 categories.failureMessage = error.getMessage();
	            }
		    }, true);  
		}catch(Exception error) {
			Utils.log(error.getMessage());
        }
        return Utils.monoFromObject(categories);
    }
	@PostMapping(value = "/getBatchYear")
	public Flux<SelectDTO> getBatchYear(){
		return commonAccountHandler.getBatchYear().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getProgrammesByBatchYear")
	public Flux<SelectDTO> getProgrammesByBatchYear(int batchYearId){
		return commonAccountHandler.getProgrammesByBatchYear(batchYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value = "/getBatchNameByProgramAndYear")
	public Flux<SelectDTO> getBatchNameByProgramAndYear(int programId, int batchYearId){
		return commonAccountHandler.getBatchNameByProgramAndYear(programId, batchYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
