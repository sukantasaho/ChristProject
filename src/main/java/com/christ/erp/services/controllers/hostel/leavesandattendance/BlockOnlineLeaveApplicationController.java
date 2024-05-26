package com.christ.erp.services.controllers.hostel.leavesandattendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.hostel.leavesandattendance.HostelBlockLeavesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.leavesandattendance.BlockOnlineLeaveApplicationHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/LeavesAndAttendance/BlockOnlineLeaveApplication")
public class BlockOnlineLeaveApplicationController {
	
	@Autowired
	BlockOnlineLeaveApplicationHandler blockOnlineLeaveApplicationHandler;
	
	@PostMapping(value = "/getCheckInOrBlockedStudents")
	public Flux<HostelBlockLeavesDTO> getGridData(@RequestParam String hostelId, @RequestParam(required = false) String blockId, @RequestParam(required = false) String blockUnitId,Boolean isUserSpecific, Boolean isToBlock, Integer academicYearId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return blockOnlineLeaveApplicationHandler.getStudentToBlock(hostelId, userId, blockId,  blockUnitId, isUserSpecific, academicYearId, isToBlock).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/blockOrUnblockStudents")
	public Mono<ResponseEntity<ApiResult>> blockStudents(@RequestBody Mono<List<HostelBlockLeavesDTO>> data,@RequestParam Boolean isBlock, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return blockOnlineLeaveApplicationHandler.blockOrUnblockStudents(data, userId, isBlock).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
}
