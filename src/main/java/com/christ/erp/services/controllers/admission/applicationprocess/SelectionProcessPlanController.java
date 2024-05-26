package com.christ.erp.services.controllers.admission.applicationprocess;

import java.util.ArrayList;
import java.util.List;

import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailAddSlotDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailAddVenueDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.admission.applicationprocess.SelectionProcessPlanHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;

@RestController
@RequestMapping(value="/Secured/Admission/ApplicationProcess/SelectionProcessPlan")
public class SelectionProcessPlanController extends BaseApiController {
	
	SelectionProcessPlanHandler selectionProcessPlanHandler =SelectionProcessPlanHandler.getInstance();
	@Autowired
	SelectionProcessPlanHandler selectionProcessPlanHandler1;
	
//	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
//    public Mono<ApiResult<List<AdmSelectionProcessPlanDTO>>> getGridData(@RequestParam("admissionBatchId") String admissionBatchId,@RequestParam("intakeId") String intakeId, String date)  {
//    	ApiResult<List<AdmSelectionProcessPlanDTO>> result = new ApiResult<>();
//        try {
//        	List<AdmSelectionProcessPlanDTO> dtoList = selectionProcessPlanHandler.getGridData(admissionBatchId,intakeId, date);
//            if(!Utils.isNullOrEmpty(dtoList)) {
//                result.success = true;
//                result.dto = dtoList;
//            }else {
//                result.success = false;
//            }
//        }catch (Exception error) {
//            result.success = false;
//            result.dto = null;
//            result.failureMessage = error.getMessage();
//        }
//        return Utils.monoFromObject(result);
//    }

	@PostMapping(value = "/getGridData")
	public Flux<AdmSelectionProcessPlanDTO> getGridData(@RequestParam("admissionBatchId") String admissionBatchId, @RequestParam("intakeId") String intakeId,@RequestParam String date) throws Exception {
		return selectionProcessPlanHandler1.getGridData(admissionBatchId,intakeId,date).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
   
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AdmSelectionProcessPlanDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			result.success = selectionProcessPlanHandler.saveOrUpdate(data, userId, result);
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = "Sorry! An exception occurred.";
		}
		return Utils.monoFromObject(result);
	}
	 
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AdmSelectionProcessPlanDTO>> edit(@RequestParam("id") String id) {
		ApiResult<AdmSelectionProcessPlanDTO> result = new ApiResult<AdmSelectionProcessPlanDTO>();
		try { 	
	       	result.dto = selectionProcessPlanHandler1.edit(id);
	       	if(!Utils.isNullOrEmpty(result.dto)) {
	       		result.success = true;
	       	}
	    }catch (Exception error) {
	           result.success = false;
	           result.dto = null;
	           result.failureMessage = "Sorry! An exception occurred.";
	    }
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("id") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
		 result.success = selectionProcessPlanHandler1.delete(id,userId,result);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = "Sorry! An exception occurred.";
		}
	   return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/editDetailsList", method = RequestMethod.POST)
	public Mono<ApiResult<AdmSelectionProcessPlanDTO>> editDetailsList(@RequestParam("id") String id) {
		ApiResult<AdmSelectionProcessPlanDTO> result = new ApiResult<AdmSelectionProcessPlanDTO>();
		try { 	
	       	result.dto = selectionProcessPlanHandler1.editDetailsList(id);
	       	if(!Utils.isNullOrEmpty(result.dto)) {
	       		result.success = true;
	       	}
	    }catch (Exception error) {
	           result.success = false;
	           result.dto = null;
	           result.failureMessage = "Sorry! An exception occurred.";
	    }
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/saveOrUpdateSlotDetails", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateSlotDetails(@RequestBody AdmSelectionProcessPlanDetailAddSlotDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			result.success = selectionProcessPlanHandler1.saveOrUpdateSlotDetails(data, result, userId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			}
		}catch (Exception error) {
			   result.success = false;
			   result.dto = null;
			   result.failureMessage = "Sorry! An exception occurred.";
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/saveOrUpdateVenueDetails", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateVenueDetails(@RequestBody AdmSelectionProcessPlanDetailAddVenueDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			result.success = selectionProcessPlanHandler1.saveOrUpdateVenueDetails(data, result, userId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			}
		}catch (Exception error) {
			   result.success = false;
			   result.dto = null;
			   result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/editDetails", method = RequestMethod.POST)
	public Mono<ApiResult<AdmSelectionProcessPlanDetailDTO>> editDetails(@RequestParam("ids") List<Integer> ids, @RequestParam("parentId") String parentId, @RequestParam("id") String id) {
		ApiResult<AdmSelectionProcessPlanDetailDTO> result = new ApiResult<AdmSelectionProcessPlanDetailDTO>();
		try {
		 result.dto = selectionProcessPlanHandler.editDetails(ids, parentId, id);
			if(!Utils.isNullOrEmpty(result.dto)) {
				if((!Utils.isNullOrEmpty(result.dto.slotslist) && result.dto.slotslist.size()>0) ||
						(!Utils.isNullOrEmpty(result.dto.venueslist) &&  result.dto.venueslist.size()>0 )) {
					result.success = true;
				}
			}
		}catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = "Sorry! An exception occurred.";
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/deleteDetails", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> deleteDetails(@RequestParam("id") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
		 result.success = selectionProcessPlanHandler1.deleteDetails(id,userId,result);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = "Sorry! An exception occurred.";
		}
	   return Utils.monoFromObject(result);
	}

	@PostMapping(value ="/getCityVenueList")
	public Flux<AdmSelectionProcessVenueCityDTO> getCityVenueList(@RequestParam Boolean isConductedInIndia) {
		return selectionProcessPlanHandler1.getCityVenueList(isConductedInIndia).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value ="/getCheckDetails")
	public Mono<ResponseEntity<ApiResult>> getCheckDetails(@RequestParam Integer detailsId) {
		return selectionProcessPlanHandler1.getCheckDetails(detailsId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value ="/getCheckPlan")
	public Mono<ResponseEntity<ApiResult>> getCheckPlan(@RequestParam Integer planId,@RequestParam List<Integer> progIds) {
		return selectionProcessPlanHandler1.getCheckPlan(planId,progIds).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/isApplicationNumberCreated")
	public Mono<ResponseEntity<ApiResult>> isApplicationNumberCreated(@RequestParam ArrayList<Integer> batchIds) {
		return selectionProcessPlanHandler1.isApplicationNumberCreated(batchIds).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}
