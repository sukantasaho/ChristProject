package com.christ.erp.services.controllers.employee.recruitment;

import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.recruitment.IntrviewScoreEntryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController

public class InterviewScoreEntryController extends BaseApiController{
	
	private static final String secure = "/Secured/Employee/Recruitment/InterviewScoreEntry";
	private static final String protect = "/Protected/Employee/Recruitment/InterviewScoreEntry";
	
	@Autowired
	IntrviewScoreEntryHandler interviewScoreEntryHandler;
	
	@PostMapping(value = protect + "/getPaneListForAppln")
    public Flux<SelectDTO> getPaneListForAppln(@RequestParam Integer applicationNumber) {
        return interviewScoreEntryHandler.getPaneListForAppln(applicationNumber).switchIfEmpty(Mono.error(new NotFoundException(null)));
    } 
	
	@PostMapping(value = protect + "/getPanelListByUserId")
    public Flux<SelectDTO> getPanelListByUserId(@RequestParam Integer applicationNumber, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return interviewScoreEntryHandler.getPanelListByUserId(applicationNumber, userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    } 
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = secure + "/checkAdminUser")
	public Mono<ResponseEntity<ApiResult>> checkAdminUser() {
		return interviewScoreEntryHandler.checkAdminUser().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@RequestMapping(value = protect + "/getInterviewTemplateforCategory")
	public Mono<ApiResult<InterviewScoreEntryDTO>> getInterviewTemplateforCategory(@RequestBody InterviewScoreEntryDTO interviewScoreEntryDTO ){		
		ApiResult<InterviewScoreEntryDTO> resultDto = new ApiResult<InterviewScoreEntryDTO>();		
		try {
			if(resultDto.failureMessage== null || resultDto.failureMessage.isEmpty()) {
				interviewScoreEntryHandler.getIntervewTemplateForCategory(resultDto,interviewScoreEntryDTO);
			}else {
				resultDto.success = false;
			}
		} catch (Exception error) {
			resultDto.dto = null;
			resultDto.failureMessage = error.getMessage();
			resultDto.success = false;
		}
		return Utils.monoFromObject(resultDto);
	}
	
	@RequestMapping(value = secure + "/saveInterviewMarks")
	public Mono<ApiResult<ModelBaseDTO>> saveInterviewMarks(@RequestBody InterviewScoreEntryDTO dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		result.success = false;
		dto.userId = Integer.parseInt(userId);
		try {
			interviewScoreEntryHandler.validateScoreDetails(dto,result);
		} catch (Exception e) {
			if(result.failureMessage== null || result.failureMessage.isEmpty())
			result.failureMessage = "Error in validation";
			e.printStackTrace();
		}
		if(result.failureMessage == null || result.failureMessage.isEmpty()) {
			try {
				interviewScoreEntryHandler.saveInterviewScore(dto,result);
			} catch (Exception e) {
				if(result.failureMessage== null || result.failureMessage.isEmpty())
					result.failureMessage = "Error in saving ";
				e.printStackTrace();
			}
		}
		return Utils.monoFromObject(result);
	}
}
