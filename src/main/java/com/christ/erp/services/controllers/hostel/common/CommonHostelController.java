package com.christ.erp.services.controllers.hostel.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.account.AccFeeHeadsDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.HostelProgrammeDetailsDTO;
import com.christ.erp.services.dto.hostel.common.CommonHostelDTO;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelDisciplinaryActionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDisciplinaryActionsTypeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFacilitySettingsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import com.christ.erp.services.dto.hostel.settings.HostelSeatAvailabilityDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.common.CommonHostelHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/Protected/CommonHostel")
@SuppressWarnings("unchecked")
public class CommonHostelController extends BaseApiController{

	CommonHostelHandler commonHostelHandler = CommonHostelHandler.getInstance();

	@Autowired
	CommonHostelHandler commonHostelHandler1;

	@RequestMapping(value = "/getHostels", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getHostels() {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			commonHostelHandler.getHostels(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getUnitsByBlockAndHostelId", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getUnitsByBlockAndHostelId(@RequestParam("blockId") String blockId,@RequestParam("hostelId") String hostelId) {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			commonHostelHandler.getUnitsByBlockAndHostelId(result,blockId,hostelId);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getBlockByHostelId",method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getBlockByHostelId(@RequestParam("hostelId") String hostelId) {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			commonHostelHandler.getBlockByHostelId(result,hostelId);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getHostelFacility", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getHostelFacility() {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			commonHostelHandler.getHostelFacility(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getRoomTypeCategory", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getRoomTypeCategory() {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			commonHostelHandler.getRoomTypeCategory(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getFloorsByBlockUnitId", method = RequestMethod.POST)
	public Mono<ApiResult<String>> getFloorsByBlockUnitId(@RequestParam("blockUnitId") String blockId) {
		ApiResult<String> result = new ApiResult<String>();
		try {
			commonHostelHandler.getFloorsByBlockUnitId(result,blockId);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getHostelRoomTypes", method = RequestMethod.POST)
	public Mono<ApiResult<List<CommonHostelDTO>>> getHostelRoomTypes(@RequestParam("hostelId") String hostelId) {
		ApiResult<List<CommonHostelDTO>> result = new ApiResult<List<CommonHostelDTO>>();
		try {
			commonHostelHandler.getHostelRoomTypes(result,hostelId);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getCampusProgrammesTree", method = RequestMethod.POST)
	public Mono<ApiResult<List<HostelProgrammeDetailsDTO>>> getCampusProgrammesTree(@RequestParam("hostelId") String hostelId) {	
		ApiResult<List<HostelProgrammeDetailsDTO>> result = new ApiResult<>();
		try {
			List<HostelProgrammeDetailsDTO> dto = commonHostelHandler.getCampusProgrammesTree(hostelId);
			if(!Utils.isNullOrEmpty(dto)){
				result.success = true;
				result.dto = dto;
			}else{
				result.success =false;
			}
		}catch (Exception error){
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}	

	@RequestMapping(value = "/getHostelBlocks", method = RequestMethod.POST)
	public Mono<ApiResult<List<CommonHostelDTO>>> getHostelBlocks() {
		ApiResult<List<CommonHostelDTO>> result = new ApiResult<List<CommonHostelDTO>>();
		try {
			commonHostelHandler.getHostelBlocks(result);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getHostelSeatsAvailable", method = RequestMethod.POST)
	public Mono<ApiResult<List<CommonHostelDTO>>> getHostelSeatsAvailable(@RequestParam("hostelId") String hostelId) {
		ApiResult<List<CommonHostelDTO>> result = new ApiResult<List<CommonHostelDTO>>();
		try {
			commonHostelHandler.getHostelSeatsAvailable(result,hostelId);
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getHostelsByGender", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getHostelsByGender(@RequestParam("genderId") String genderId) {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			result.dto = commonHostelHandler.getHostelsByGender(genderId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			}else {
				result.success = false;
			}
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@PostMapping(value= "/getHostel")
	public Flux<SelectDTO> getHostel(@RequestParam("showAllHostels") Boolean showAllHostels, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return commonHostelHandler1.getHostel(showAllHostels,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getStatus")
	public Flux<SelectDTO> getStatus() {
		return commonHostelHandler1.getStatus().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getRoomTypeByCount")
	public Mono<List<Object>> getRoomTypeByCount(@RequestParam String hostelId, @RequestParam String academicYearId) {
		return commonHostelHandler1.getRoomTypeByCount(hostelId, academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value ="/getBlockByHostelId1")
	public Flux<SelectDTO> getBlockByHostelId1(@RequestParam String hostelId) {
		return commonHostelHandler1.getBlockByHostelId1(hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value="/getUnitsByBlock")
	public Flux<SelectDTO> getUnitsByBlock(@RequestParam String blockId) {
		return commonHostelHandler1.getUnitsByBlock(blockId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value="/getHostelFacilities")
	public Mono<List<HostelFacilitySettingsDTO>> getHostelFacilities(@RequestParam String roomTypeId) {
		return commonHostelHandler1.getHostelFacilities(roomTypeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value="/getFloorByUnit")
	public Flux<SelectDTO> getFloorByUnit(@RequestParam String unitId) {
		return commonHostelHandler1.getFloorByUnit(unitId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value="/getHostelBlockByUser")
	public Flux<SelectDTO> getHostelBlockByUser(@RequestParam String hostelId,Boolean isUserSpecific, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return commonHostelHandler1.getHostelBlockByUser(hostelId,isUserSpecific,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value="/getHostelBlockUnitByUser")
	public Flux<SelectDTO> getHostelBlockUnitByUser(@RequestParam Boolean isUserSpecific, String blockId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return commonHostelHandler1.getHostelBlockUnitByUser(userId,blockId,isUserSpecific).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value="/getDisciplinaryActionType")
	public Flux<HostelDisciplinaryActionsTypeDTO> getDisciplinaryActionType() {
		return commonHostelHandler1.getDisciplinaryActionType().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value="/isUserSpecific")
	public Boolean isUserSpecific(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return commonHostelHandler1.isUserSpecific(userId);
	}

	@PostMapping(value = "/getHostelLeaveType")
	public Flux<SelectDTO> getHostelLeaveType(){
		return commonHostelHandler1.getHostelLeaveType().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value= "/getRoomTypeForStudent")
	public Flux<SelectDTO> getRoomTypeForStudent(@RequestParam String hostelId) {
		return commonHostelHandler1.getRoomTypeForStudent(hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value= "/getBedByRoomId")
	public Flux<SelectDTO> getBedByRoomId(@RequestParam String roomId) {
		return commonHostelHandler1.getBedByRoomId(roomId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value= "/getRoomByFloor")
	public Flux<SelectDTO> getRoomByFloor(@RequestParam String floorId) {
		return commonHostelHandler1.getRoomByFloor(floorId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getAccHeadsForHostelFees") 
    public Flux<AccFeeHeadsDTO> getAccHeadsForHostelFees(@RequestParam String hostelId) {
    	 return commonHostelHandler1.getAccHeadsForHostelFees(hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getDataByStudentNameOrRegNo")
	public Mono<HostelAdmissionsDTO> getDataByStudentNameOrRegNo(@RequestParam String yearId, @RequestParam String regNo, @RequestParam (required = false) String studentName ){
		return commonHostelHandler1.getDataByStudentNameOrRegNo(yearId, regNo, studentName).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value ="/getOtherDisciplinary")
	public Flux<HostelDisciplinaryActionsDTO> getOtherDisciplinary(@RequestParam String yearId, @RequestParam String regNo) {
		return commonHostelHandler1.getOtherDisciplinary(yearId, regNo);
	}
	
	@PostMapping(value = "/getFineCategoryOthers")
	public Flux<HostelFineCategoryDTO> getFineCategoryOthers() {
		return commonHostelHandler1.getFineCategoryOthers().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getStudentRegAndNameListFromHostel")
	public Flux<SelectDTO> getStudentRegAndNameListFromHostel(@RequestParam String data, Integer academicYearId) {
		return commonHostelHandler1.getStudentRegAndNameListFromHostel(data,academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value= "/getHostelByGenderAndCampus")
	public Flux<SelectDTO> getHostelByGenderAndCampus(@RequestParam String genderId, @RequestParam String erpCampusProgrammeMappingId ) {
		return commonHostelHandler1.getHostelByGenderAndCampus(genderId, erpCampusProgrammeMappingId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getHostelSelectedStatus")
	public Flux<SelectDTO> getHostelSelectedStatus() {
		return commonHostelHandler1.getHostelSelectedStatus().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
}
