package com.christ.erp.services.controllers.administration.academicCalandar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarDTO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarPersonalDTO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarToDoListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.administraton.academicCalendar.MonthViewHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Administraton/AcademicCalandar/MonthView")
public class MonthViewController {

	@Autowired 
	MonthViewHandler monthViewHandler;
	
	@PostMapping(value = "/getEventView")
	public Flux<ErpCalendarDTO> getEventView(@RequestParam String date,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return monthViewHandler.getEventView(date,userId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@PostMapping(value = "/getDay")
	public Flux<ErpCalendarPersonalDTO> getDay(@RequestParam String Date,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return monthViewHandler.getDay(Date,userId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdateEvent")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateEvent(@RequestBody Mono<ErpCalendarPersonalDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return monthViewHandler.saveOrUpdateEvent(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdateTodo")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateTodo(@RequestBody Mono<ErpCalendarToDoListDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return monthViewHandler.saveOrUpdateTodo(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getToDoList")
	public Flux<ErpCalendarToDoListDTO> getToDoList(@RequestParam String date,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return monthViewHandler.getToDoList(userId,date).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/deleteToDo")
	public Mono<ResponseEntity<ApiResult>> deleteToDo(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return monthViewHandler.deleteToDo(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getEventDetails")
	public Mono<ErpCalendarDTO> getEventDetails(@RequestBody ErpCalendarDTO data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return monthViewHandler.getEventDetails(data,userId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}	
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdateEventDetails")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateEventDetails(@RequestBody Mono<ErpCalendarDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return monthViewHandler.saveOrUpdateEventDetails(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/printDay")
	public Flux<ErpCalendarDTO> printDay(@RequestParam String fDate,@RequestParam String tDate){
		return monthViewHandler.printDay(fDate,tDate).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/deleteReminder")
	public Mono<ResponseEntity<ApiResult>> deleteReminder(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return monthViewHandler.deleteReminder(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}