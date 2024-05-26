package com.christ.erp.services.controllers.hostel.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;
import com.christ.erp.services.dto.hostel.settings.HostelSeatAvailabilityDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.settings.SeatAvailabilityHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Settings/SeatAvailability")

public class SeatAvailabilityController {

	@Autowired
	private SeatAvailabilityHandler seatAvailabilityHandler;

	@PostMapping(value= "/getHostelRoomType")
	public Flux<HostelDTO> getHostelRoomType(@RequestParam String hostelId, @RequestParam String academicYearId) {
		return seatAvailabilityHandler.getHostelRoomType(hostelId, academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@GetMapping(value = "/getGridData")
	public Flux<HostelSeatAvailabilityDTO> getGridData(@RequestParam String yearId) {
		return seatAvailabilityHandler.getGridData(yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelSeatAvailabilityDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return seatAvailabilityHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/edit")
	public Mono<HostelSeatAvailabilityDTO> edit(@RequestParam int id) {
		return seatAvailabilityHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value= "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return  seatAvailabilityHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}	
}
