package com.christ.erp.services.controllers.hostel.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.student.RoomOccupancyDetailsHandler;

import reactor.core.publisher.Flux;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Student/RoomOccupancyDetails")

public class RoomOccupancyDetailsController {
	
	@Autowired
	private RoomOccupancyDetailsHandler roomOccupancyDetailsHandler;
	
	@PostMapping(value="/getUnitsByBlock")
	public Flux<SelectDTO> getUnitsByBlock(@RequestParam String blockId) {
		return roomOccupancyDetailsHandler.getUnitsByBlock(blockId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value ="/getBlock")
	public Flux<SelectDTO> getBlock(@RequestParam String hostelId) {
		return roomOccupancyDetailsHandler.getBlock(hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
		
	@GetMapping(value = "/getOccupancyDetails")
	public Flux<HostelBlockDTO> getOccupancyDetails(@RequestParam String yearId, @RequestParam String hostelId, @RequestParam (required = false) String blockId, @RequestParam(required = false) String unitId) {
		return roomOccupancyDetailsHandler.getOccupancyDetails(yearId, hostelId, blockId, unitId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
}
