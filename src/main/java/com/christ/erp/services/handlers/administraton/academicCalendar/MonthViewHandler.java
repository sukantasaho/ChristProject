package com.christ.erp.services.handlers.administraton.academicCalendar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarDatesDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarPersonalDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarToDoListDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpReminderNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarDTO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarPersonalDTO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarToDoListDTO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpReminderNotificationsDTO;
import com.christ.erp.services.transactions.administraton.academicCalendar.MonthViewTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MonthViewHandler {

	@Autowired
	MonthViewTransaction monthViewTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction;

	public Flux<ErpCalendarDTO> getEventView(String date,String userId) {
		List<ErpCalendarDBO> list = monthViewTransaction.getEventView(date);
		List<ErpCalendarToDoListDBO> erpCalendarToDoListDBOList = monthViewTransaction.getToDoList(userId,date);
		Integer campusId = monthViewTransaction.getEmpCampus(userId);
		Integer applicableFor = monthViewTransaction.getApplicableFor(userId);
		return convertErpCalendarDBOtodto(list,campusId,applicableFor,erpCalendarToDoListDBOList);		
	}

	private Flux<ErpCalendarDTO> convertErpCalendarDBOtodto(List<ErpCalendarDBO> list,Integer campusId,Integer applicableFor,List<ErpCalendarToDoListDBO> erpCalendarToDoListDBOList) {
		List<ErpCalendarDTO> erpCalendarDTOList = new ArrayList<ErpCalendarDTO>();
		Set<Integer> campusSet = new HashSet<Integer>();
		Set<Integer> campusList = new HashSet<Integer>();
		Set<Integer> applicableList = new HashSet<Integer>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(dbo -> {
				if(!Utils.isNullOrEmpty(dbo.getRecordStatus())) {
					if(dbo.getRecordStatus() == 'A') {
						if(!Utils.isNullOrEmpty(dbo.getErpCalendarCampusDBOSet())) {
							dbo.getErpCalendarCampusDBOSet().forEach(campusDbo -> {
								if(campusDbo.getRecordStatus() == 'A') {
									campusList.add(campusDbo.getErpCampusDBO().getId());
								}
							});
							dbo.getErpCalendarUserTypesDetailsDBOSet().forEach(applicableForDbo -> {
								if(applicableForDbo.getRecordStatus() == 'A') {
									applicableList.add(applicableForDbo.getErpCalendarUserTypesDBO().getEmpEmployeeCategoryDBO().getId());
								}
							});
							if(!Utils.isNullOrEmpty(dbo.getErpCalendarCampusDBOSet()) && !Utils.isNullOrEmpty(dbo.getErpCalendarUserTypesDetailsDBOSet())) {
								dbo.getErpCalendarCampusDBOSet().forEach(campusDbo -> {
									if(campusDbo.getRecordStatus() == 'A') {
										dbo.getErpCalendarUserTypesDetailsDBOSet().forEach(applicableForDbo -> {
											if(applicableForDbo.getRecordStatus() == 'A') {
												if(campusDbo.getErpCampusDBO().getId() == campusId && applicableForDbo.getErpCalendarUserTypesDBO().getEmpEmployeeCategoryDBO().getId() == applicableFor  && !campusSet.contains(dbo.getId())) {
													campusSet.add(dbo.getId());
													ErpCalendarDTO erpCalendarDTO = new ErpCalendarDTO();
													erpCalendarDTO.setId(dbo.getId());
													erpCalendarDTO.setTitle(dbo.getActivitiesEvents());
													erpCalendarDTO.setRecordStatus(dbo.getRecordStatus());
													erpCalendarDTO.setPublished(dbo.isPublished());
													erpCalendarDTO.setStart(dbo.getFromDate().atStartOfDay());
													erpCalendarDTO.setEnd(dbo.getToDate().atStartOfDay());
													if(!Utils.isNullOrEmpty(dbo.getErpCalendarPersonalDBOSet())) {
														dbo.getErpCalendarPersonalDBOSet().forEach(personalDbo -> {
															if(personalDbo.getRecordStatus() == 'A') {
																if(!Utils.isNullOrEmpty(personalDbo.getErpCalendarDBO().getId())) {
																	if(personalDbo.getErpCalendarDBO().getId() == dbo.getId()) {
																		if(!Utils.isNullOrEmpty(personalDbo.isImportant())) {
																			if(personalDbo.isImportant())
																				erpCalendarDTO.setImportant(personalDbo.isImportant());
																		}
																		if(!Utils.isNullOrEmpty(personalDbo.getImportantPriority())) {
																			if(personalDbo.getImportantPriority().trim().equalsIgnoreCase("High Priority"))
																				erpCalendarDTO.setColor("#FF0000");
																			if(personalDbo.getImportantPriority().trim().equalsIgnoreCase("Medium Priority"))
																				erpCalendarDTO.setColor("#FFA500");
																			if(personalDbo.getImportantPriority().trim().equalsIgnoreCase("Low Priority"))
																				erpCalendarDTO.setColor(" #FFFF00");
																			if(personalDbo.getImportantPriority().trim().equalsIgnoreCase("Not Important"))
																				erpCalendarDTO.setColor("#FFFFFF");
																		}else {
																			erpCalendarDTO.setColor("#FFFFFF");
																		}
																		erpCalendarDTO.setAllEvent(false);
																		erpCalendarDTO.setTableName("PERSONAL");
																	}
																}
															}
														});
													}else {
														erpCalendarDTO.setTableName("CALENDAR");
													}
													erpCalendarDTOList.add(erpCalendarDTO);
												}
											}
										});
									}
								});
							}
							if(!campusSet.contains(dbo.getId())) {
								campusSet.add(dbo.getId());
								ErpCalendarDTO erpCalendarDTO = new ErpCalendarDTO();
								erpCalendarDTO.setId(dbo.getId());
								erpCalendarDTO.setTitle(dbo.getActivitiesEvents());
								erpCalendarDTO.setRecordStatus(dbo.getRecordStatus());
								erpCalendarDTO.setPublished(dbo.isPublished());
								erpCalendarDTO.setStart(dbo.getFromDate().atStartOfDay());
								erpCalendarDTO.setEnd(dbo.getToDate().atStartOfDay());
								erpCalendarDTO.setAllEvent(true);
								erpCalendarDTO.setImportant(false);
								erpCalendarDTO.setTableName("CALENDAR");
								erpCalendarDTOList.add(erpCalendarDTO);
							}
						}
					}
				}
			});
		}
		if(!Utils.isNullOrEmpty(erpCalendarToDoListDBOList)) {
			List<Integer> ids = new ArrayList<Integer>();
			erpCalendarToDoListDBOList.forEach(toDoData -> {
				if(!ids.contains(toDoData.getId())) {
					if(!Utils.isNullOrEmpty(toDoData.getToDoDate())) {
						ErpCalendarDTO erpCalendarDTO = new ErpCalendarDTO();
						erpCalendarDTO.setId(toDoData.getId());
						ids.add(toDoData.getId());
						if(!Utils.isNullOrEmpty(toDoData.getToDoNote())) {
							erpCalendarDTO.setTitle(toDoData.getToDoNote());
						}
						erpCalendarDTO.setStart(toDoData.getToDoDate().atStartOfDay());
						erpCalendarDTO.setEnd(toDoData.getToDoDate().atStartOfDay());
						erpCalendarDTO.setImportant(false);
						erpCalendarDTO.setTableName("TODO");
						erpCalendarDTO.setAllEvent(false);
						erpCalendarDTO.setRecordStatus('A');
						erpCalendarDTOList.add(erpCalendarDTO);
					}
				}
			});
		}
		return Flux.fromIterable(erpCalendarDTOList);
	}

	public Flux<ErpCalendarPersonalDTO> getDay(String date, String userId) {
		Integer campusId = monthViewTransaction.getEmpCampus(userId);
		Integer applicableFor = monthViewTransaction.getApplicableFor(userId);
		List<ErpCalendarDatesDBO> erpCalendarDBOList = monthViewTransaction.getDay(date);
		List<ErpCalendarPersonalDBO> erpCalendarPersonalDBOList = monthViewTransaction.getToDoDay(date,userId);
		return convertDateDetailsToDto(erpCalendarDBOList,erpCalendarPersonalDBOList,campusId, applicableFor,userId);
	}

	private Flux<ErpCalendarPersonalDTO> convertDateDetailsToDto(List<ErpCalendarDatesDBO> erpCalendarDBOList,List<ErpCalendarPersonalDBO> erpCalendarPersonalDBOList,Integer campusId,Integer applicableFor,String userId) {
		List<ErpCalendarPersonalDTO> erpCalendarPersonalDTOList1 = new ArrayList<ErpCalendarPersonalDTO>();
		Set<Integer> campusSet = new HashSet<Integer>();
		Set<Integer> campusList = new HashSet<Integer>();
		Set<Integer> applicableList = new HashSet<Integer>();
		if(!Utils.isNullOrEmpty(erpCalendarDBOList)) {
			erpCalendarDBOList.forEach(dbo -> {
				if(!Utils.isNullOrEmpty(dbo.getErpCalendarDBO().getErpCalendarCampusDBOSet()) && !Utils.isNullOrEmpty(dbo.getErpCalendarDBO().getErpCalendarUserTypesDetailsDBOSet())) {
					dbo.getErpCalendarDBO().getErpCalendarCampusDBOSet().forEach(campusDBO -> {
						if(campusDBO.getRecordStatus() == 'A') {
							campusList.add(campusDBO.getErpCampusDBO().getId());
						}
					});
					dbo.getErpCalendarDBO().getErpCalendarUserTypesDetailsDBOSet().forEach(applicableForDBO -> {
						if(applicableForDBO.getRecordStatus() == 'A') {
							applicableList.add(applicableForDBO.getErpCalendarUserTypesDBO().getEmpEmployeeCategoryDBO().getId());
						}
					});	

				}
			});
		}
		if(!Utils.isNullOrEmpty(erpCalendarDBOList)) {
			erpCalendarDBOList.forEach(dbo -> {
				dbo.getErpCalendarDBO().getErpCalendarCampusDBOSet().forEach(campusDBO -> {
					if(campusDBO.getRecordStatus() == 'A') {
						dbo.getErpCalendarDBO().getErpCalendarUserTypesDetailsDBOSet().forEach(applicableForDBO -> {
							if(applicableForDBO.getRecordStatus() == 'A') {
								if(!campusSet.contains(dbo.getErpCalendarDBO().getId()) && campusList.contains(campusId) && applicableList.contains(applicableFor)) {
									if(!Utils.isNullOrEmpty(dbo.getErpCalendarDBO().getErpCalendarPersonalDBOSet())) {
										campusSet.add(dbo.getErpCalendarDBO().getId());
										ErpCalendarPersonalDTO erpCalendarPersonalDTO = new ErpCalendarPersonalDTO();
										erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(new ArrayList<ErpReminderNotificationsDTO>());
										erpCalendarPersonalDTO.setErpCalendarDTO(new ErpCalendarDTO());
										erpCalendarPersonalDTO.getErpCalendarDTO().setId(dbo.getErpCalendarDBO().getId());
										erpCalendarPersonalDTO.getErpCalendarDTO().setActivitiesEvents(dbo.getErpCalendarDBO().getActivitiesEvents());
										erpCalendarPersonalDTO.getErpCalendarDTO().setStart(dbo.getErpCalendarDBO().getFromDate().atStartOfDay());
										erpCalendarPersonalDTO.getErpCalendarDTO().setEnd(dbo.getErpCalendarDBO().getToDate().atStartOfDay());
										erpCalendarPersonalDTO.getErpCalendarDTO().setPublished(dbo.getErpCalendarDBO().isPublished());
										erpCalendarPersonalDTO.getErpCalendarDTO().setRecordStatus(dbo.getRecordStatus());
										erpCalendarPersonalDTO.getErpCalendarDTO().setAllEvent(false);
										erpCalendarPersonalDTO.setName("PERSONAL");
										dbo.getErpCalendarDBO().getErpCalendarPersonalDBOSet().forEach(personalDBO -> {	
											if(personalDBO.getRecordStatus() == 'A' && !Utils.isNullOrEmpty(personalDBO.getErpCalendarDBO().getId()) && !Utils.isNullOrEmpty(personalDBO.getEmpDBO())) {
												if(personalDBO.getEmpDBO().getId() == Integer.parseInt(userId) && personalDBO.getErpCalendarDBO().getId() == dbo.getErpCalendarDBO().getId()) {
													erpCalendarPersonalDTO.setId(personalDBO.getId());
													erpCalendarPersonalDTO.setEmpId(Integer.parseInt(userId));
													if(!Utils.isNullOrEmpty(personalDBO.isImportant())) {
														erpCalendarPersonalDTO.setImportant(personalDBO.isImportant());
													}
													if(!Utils.isNullOrEmpty(personalDBO.isCompleted())) {
														erpCalendarPersonalDTO.setCompleted(personalDBO.isCompleted());
													}
													if(!Utils.isNullOrEmpty(personalDBO.getEventsNote())) {
														erpCalendarPersonalDTO.setEventsNote(personalDBO.getEventsNote());
													}
													if(!Utils.isNullOrEmpty(personalDBO.getImportantPriority())){
														if(!Utils.isNullOrEmpty(personalDBO.getImportantPriority().trim())) {
															if(personalDBO.getImportantPriority().trim().equalsIgnoreCase("High Priority")) {
																erpCalendarPersonalDTO.setColor("#FF0000");
																erpCalendarPersonalDTO.setImportantPriority(personalDBO.getImportantPriority().trim());
															}
															if(personalDBO.getImportantPriority().trim().equalsIgnoreCase("Medium Priority")) {
																erpCalendarPersonalDTO.setColor("#FFA500");
																erpCalendarPersonalDTO.setImportantPriority(personalDBO.getImportantPriority().trim());
															}
															if(personalDBO.getImportantPriority().trim().equalsIgnoreCase("Low Priority")) {
																erpCalendarPersonalDTO.setColor(" #FFFF00");
																erpCalendarPersonalDTO.setImportantPriority(personalDBO.getImportantPriority().trim());
															}
															if(personalDBO.getImportantPriority().trim().equalsIgnoreCase("Not Important")) {
																erpCalendarPersonalDTO.setColor("#FFFFFF");
																erpCalendarPersonalDTO.setImportantPriority(personalDBO.getImportantPriority().trim());
															}
														}
													}else {
														erpCalendarPersonalDTO.setColor("#FFFFFF");
														erpCalendarPersonalDTO.setImportantPriority("Not Important");
													}
													erpCalendarPersonalDTO.setRecordStatus(personalDBO.getRecordStatus());
													if(!Utils.isNullOrEmpty(personalDBO.getErpReminderNotificationsDBOSet())) {
														personalDBO.getErpReminderNotificationsDBOSet().forEach(personalReminder -> {
															if(personalReminder.getRecordStatus() == 'A' && !Utils.isNullOrEmpty(personalReminder.getErpCalendarPersonalDBO()) && !Utils.isNullOrEmpty(personalReminder.getErpCalendarPersonalDBO().getEmpDBO())) {
																if(personalReminder.getErpCalendarPersonalDBO().getEmpDBO().getId() == Integer.parseInt(userId) && personalReminder.getErpCalendarPersonalDBO().getId() == personalDBO.getId()) {
																	ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
																	erpReminderNotificationsDTO.setId(personalReminder.getId());
																	erpReminderNotificationsDTO.setReminderDateTime(personalReminder.getReminderDateTime());
																	erpReminderNotificationsDTO.setReminderComments(personalReminder.getReminderComments());
																	erpReminderNotificationsDTO.setRecordStatus(personalReminder.getRecordStatus());
																	erpReminderNotificationsDTO = this.messageSentDto(personalReminder, erpReminderNotificationsDTO);
																	erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(new ArrayList<ErpReminderNotificationsDTO>());
																	erpCalendarPersonalDTO.getErpReminderNotificationsDTOList().add(erpReminderNotificationsDTO);
																}
															}
														});
													} else {
														ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
														erpReminderNotificationsDTO.setId(0);
														List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOList = new ArrayList<ErpReminderNotificationsDTO>();
														erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
														erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
													}
												}
											}
										});
										erpCalendarPersonalDTOList1.add(erpCalendarPersonalDTO);
									}else {
										campusSet.add(dbo.getErpCalendarDBO().getId());
										ErpCalendarPersonalDTO erpCalendarPersonalDTO = new ErpCalendarPersonalDTO();
										ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
										erpReminderNotificationsDTO.setId(0);
										List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOList = new ArrayList<ErpReminderNotificationsDTO>();
										erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
										erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
										erpCalendarPersonalDTO.setErpCalendarDTO(new ErpCalendarDTO());
										erpCalendarPersonalDTO.getErpCalendarDTO().setId(dbo.getErpCalendarDBO().getId());
										erpCalendarPersonalDTO.getErpCalendarDTO().setActivitiesEvents(dbo.getErpCalendarDBO().getActivitiesEvents());
										erpCalendarPersonalDTO.getErpCalendarDTO().setStart(dbo.getErpCalendarDBO().getFromDate().atStartOfDay());
										erpCalendarPersonalDTO.getErpCalendarDTO().setEnd(dbo.getErpCalendarDBO().getToDate().atStartOfDay());
										erpCalendarPersonalDTO.getErpCalendarDTO().setPublished(dbo.getErpCalendarDBO().isPublished());
										erpCalendarPersonalDTO.getErpCalendarDTO().setRecordStatus(dbo.getRecordStatus());
										erpCalendarPersonalDTO.getErpCalendarDTO().setAllEvent(false);
										erpCalendarPersonalDTO.setName("CALENDER");
										erpCalendarPersonalDTOList1.add(erpCalendarPersonalDTO);
									}
								}else if(!campusSet.contains(dbo.getErpCalendarDBO().getId()) && campusList.contains(campusDBO.getErpCampusDBO().getId()) && applicableList.contains(applicableForDBO.getErpCalendarUserTypesDBO().getEmpEmployeeCategoryDBO().getId())) {
									campusSet.add(dbo.getErpCalendarDBO().getId());
									ErpCalendarPersonalDTO erpCalendarPersonalDTO = new ErpCalendarPersonalDTO();
									ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
									erpReminderNotificationsDTO.setId(0);
									List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOList = new ArrayList<ErpReminderNotificationsDTO>();
									erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
									erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
									erpCalendarPersonalDTO.setErpCalendarDTO(new ErpCalendarDTO());
									erpCalendarPersonalDTO.getErpCalendarDTO().setId(dbo.getErpCalendarDBO().getId());
									erpCalendarPersonalDTO.getErpCalendarDTO().setActivitiesEvents(dbo.getErpCalendarDBO().getActivitiesEvents());
									erpCalendarPersonalDTO.getErpCalendarDTO().setStart(dbo.getErpCalendarDBO().getFromDate().atStartOfDay());
									erpCalendarPersonalDTO.getErpCalendarDTO().setEnd(dbo.getErpCalendarDBO().getToDate().atStartOfDay());
									erpCalendarPersonalDTO.getErpCalendarDTO().setPublished(dbo.getErpCalendarDBO().isPublished());
									erpCalendarPersonalDTO.getErpCalendarDTO().setRecordStatus(dbo.getRecordStatus());
									erpCalendarPersonalDTO.getErpCalendarDTO().setAllEvent(true);
									erpCalendarPersonalDTO.setName("CALENDER");
									erpCalendarPersonalDTOList1.add(erpCalendarPersonalDTO);
								}

							}
						});
					}
				});
			});
		}
		if(!Utils.isNullOrEmpty(erpCalendarPersonalDBOList)) {
			erpCalendarPersonalDBOList.forEach(personalTodo -> {
				ErpCalendarPersonalDTO erpCalendarPersonalDTO = new ErpCalendarPersonalDTO();
				erpCalendarPersonalDTO.setId(personalTodo.getId());
				erpCalendarPersonalDTO.setName("TODO");
				erpCalendarPersonalDTO.setCompleted(personalTodo.isCompleted());
				erpCalendarPersonalDTO.setRecordStatus(personalTodo.getRecordStatus());
				erpCalendarPersonalDTO.setEmpId(personalTodo.getEmpDBO().getId());
				erpCalendarPersonalDTO.setErpCalendarToDoListDTO(new ErpCalendarToDoListDTO());
				erpCalendarPersonalDTO.getErpCalendarToDoListDTO().setId(personalTodo.getErpCalendarToDoListDBO().getId());
				erpCalendarPersonalDTO.getErpCalendarToDoListDTO().setCompleted(personalTodo.getErpCalendarToDoListDBO().isCompleted());
				erpCalendarPersonalDTO.getErpCalendarToDoListDTO().setToDoStart(personalTodo.getErpCalendarToDoListDBO().getToDoDate().atStartOfDay());
				erpCalendarPersonalDTO.getErpCalendarToDoListDTO().setToDoNote(personalTodo.getErpCalendarToDoListDBO().getToDoNote());
				erpCalendarPersonalDTO.getErpCalendarToDoListDTO().setRecordStatus(personalTodo.getErpCalendarToDoListDBO().getRecordStatus());
				erpCalendarPersonalDTO.getErpCalendarToDoListDTO().setEmpId(personalTodo.getErpCalendarToDoListDBO().getEmpDBO().getId());
				if(!Utils.isNullOrEmpty(personalTodo.getErpReminderNotificationsDBOSet())) {
					AtomicInteger order = new AtomicInteger(0);
					personalTodo.getErpReminderNotificationsDBOSet().forEach(data -> {
						if(data.getRecordStatus() == 'A') {
							int i = order.addAndGet(1);
							order.set(i);
						}
					});
					if(order.get() != 0) {
						personalTodo.getErpReminderNotificationsDBOSet().forEach(data -> {
							if(data.getRecordStatus() == 'A') {
								if(personalTodo.getId() == data.getErpCalendarPersonalDBO().getId()) {
									ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
									erpReminderNotificationsDTO.setId(data.getId());
									erpReminderNotificationsDTO.setReminderDateTime(data.getReminderDateTime());
									erpReminderNotificationsDTO.setReminderComments(data.getReminderComments());
									erpReminderNotificationsDTO = this.messageSentDto(data, erpReminderNotificationsDTO);
									erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(new ArrayList<ErpReminderNotificationsDTO>());
									erpCalendarPersonalDTO.getErpReminderNotificationsDTOList().add(erpReminderNotificationsDTO);
								}
							}
						});
					}else {
						ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
						erpReminderNotificationsDTO.setId(0);
						List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOs = new ArrayList<ErpReminderNotificationsDTO>();
						erpReminderNotificationsDTOs.add(erpReminderNotificationsDTO);
						erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOs);
					}
				}else {
					ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
					erpReminderNotificationsDTO.setId(0);
					List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOs = new ArrayList<ErpReminderNotificationsDTO>();
					erpReminderNotificationsDTOs.add(erpReminderNotificationsDTO);
					erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOs);
				}
				erpCalendarPersonalDTOList1.add(erpCalendarPersonalDTO);
			});
		}
		return Flux.fromIterable(erpCalendarPersonalDTOList1);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdateEvent(Mono<ErpCalendarPersonalDTO> dto, String userId) {
		return dto
				.handle((erpCalendarDTO, synchronousSink) ->  {
					synchronousSink.next(erpCalendarDTO);	
				}).cast(ErpCalendarPersonalDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap( s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						monthViewTransaction.update(s,s.getId());
					}else {
						monthViewTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private Integer reminderId = null;
	private ErpCalendarPersonalDBO convertDtoToDbo(ErpCalendarPersonalDTO data, String userId) {
		ErpCalendarPersonalDBO erpCalendarPersonalDBO = Utils.isNullOrEmpty(data.getId()) ? new ErpCalendarPersonalDBO() : monthViewTransaction.getCalendarData(data.getId());
		if(Utils.isNullOrEmpty(data.getId())) {
			erpCalendarPersonalDBO.setCreatedUsersId(Integer.parseInt(userId));
		}else {
			erpCalendarPersonalDBO.setModifiedUsersId(Integer.parseInt(userId));
		}
		Set<ErpReminderNotificationsDBO> erpReminderNotificationsDBOSet = new HashSet<ErpReminderNotificationsDBO>();
		Map<Integer,ErpReminderNotificationsDBO> existCalendarPersonalReminderMap = !Utils.isNullOrEmpty(erpCalendarPersonalDBO) && !Utils.isNullOrEmpty(erpCalendarPersonalDBO.getErpReminderNotificationsDBOSet()) ? erpCalendarPersonalDBO.getErpReminderNotificationsDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) : null;
		Set<ErpNotificationsDBO> erpNotificationsDBOReminderSet = new HashSet<ErpNotificationsDBO>();
		Set<ErpSmsDBO> erpSmsDBOSet = new HashSet<ErpSmsDBO>();
		Set<ErpEmailsDBO> erpEmailDBOSet = new HashSet<ErpEmailsDBO>();
		if(!Utils.isNullOrEmpty(erpCalendarPersonalDBO)) {
			if(!Utils.isNullOrEmpty(data.getEventsNote())) {
				erpCalendarPersonalDBO.setEventsNote(data.getEventsNote());
			}
			if(!Utils.isNullOrEmpty(data.getImportantPriority())) {
				erpCalendarPersonalDBO.setImportantPriority(data.getImportantPriority().trim());
			}
			if(!Utils.isNullOrEmpty(userId)) {
				erpCalendarPersonalDBO.setEmpDBO(new EmpDBO());
				erpCalendarPersonalDBO.getEmpDBO().setId(Integer.parseInt(userId));		
			}
			if(!Utils.isNullOrEmpty(data.isImportant())) {
				erpCalendarPersonalDBO.setImportant(data.isImportant());
			}
			if(!Utils.isNullOrEmpty(data.isCompleted())) {
				erpCalendarPersonalDBO.setCompleted(data.isCompleted());
			}
			if(!Utils.isNullOrEmpty(data.getErpCalendarDTO())) {
				if(!Utils.isNullOrEmpty(data.getErpCalendarDTO().getId())) {
					erpCalendarPersonalDBO.setErpCalendarDBO(new ErpCalendarDBO());
					erpCalendarPersonalDBO.getErpCalendarDBO().setId(data.getErpCalendarDTO().getId());
				}
			}
			if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO())) {
				if(!Utils.isNullOrEmpty(erpCalendarPersonalDBO.getErpCalendarToDoListDBO())) {
					erpCalendarPersonalDBO.getErpCalendarToDoListDBO().setModifiedUsersId(Integer.parseInt(userId));
				}else {
					ErpCalendarToDoListDBO erpCalendarToDoListDBO = new ErpCalendarToDoListDBO();
					erpCalendarPersonalDBO.setErpCalendarToDoListDBO(erpCalendarToDoListDBO);
					erpCalendarPersonalDBO.getErpCalendarToDoListDBO().setCreatedUsersId(Integer.parseInt(userId));
				}
				erpCalendarPersonalDBO.getErpCalendarToDoListDBO().setRecordStatus('A');
				if(!Utils.isNullOrEmpty(userId)) {
					erpCalendarPersonalDBO.getErpCalendarToDoListDBO().setEmpDBO(new EmpDBO());
					erpCalendarPersonalDBO.getErpCalendarToDoListDBO().getEmpDBO().setId(Integer.parseInt(userId));
				}
				if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO().getToDoStart())) {
					erpCalendarPersonalDBO.getErpCalendarToDoListDBO().setToDoDate(data.getErpCalendarToDoListDTO().getToDoStart().toLocalDate());
				}
				if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO().getToDoNote())) {
					erpCalendarPersonalDBO.getErpCalendarToDoListDBO().setToDoNote(data.getErpCalendarToDoListDTO().getToDoNote());
				}
				if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO().isCompleted())) {
					erpCalendarPersonalDBO.getErpCalendarToDoListDBO().setCompleted(data.getErpCalendarToDoListDTO().isCompleted());
				}
			}
			erpCalendarPersonalDBO.setRecordStatus('A');
			if(!Utils.isNullOrEmpty(data.getErpReminderNotificationsDTOList())) {
				data.getErpReminderNotificationsDTOList().forEach(dataReminder -> {
					ErpReminderNotificationsDBO erpReminderNotificationsDBO = null;
					if(!Utils.isNullOrEmpty(existCalendarPersonalReminderMap) && existCalendarPersonalReminderMap.containsKey(dataReminder.getId())) {
						erpReminderNotificationsDBO = existCalendarPersonalReminderMap.get(dataReminder.getId());
						erpReminderNotificationsDBO.setModifiedUsersId(Integer.parseInt(userId));
						existCalendarPersonalReminderMap.remove(dataReminder.getId());
					}else {
						erpReminderNotificationsDBO = new ErpReminderNotificationsDBO();
						erpReminderNotificationsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					erpReminderNotificationsDBO.setRecordStatus('A');
					erpReminderNotificationsDBO.setErpCalendarPersonalDBO(erpCalendarPersonalDBO);
					if(!Utils.isNullOrEmpty(dataReminder.getReminderDateTime())) {
						erpReminderNotificationsDBO.setReminderDateTime(dataReminder.getReminderDateTime());
					}
					if(!Utils.isNullOrEmpty(dataReminder.getReminderComments())) {
						erpReminderNotificationsDBO.setReminderComments(dataReminder.getReminderComments());
					}
					// common method messageSent
					erpReminderNotificationsDBO = this.messageSentDBO(dataReminder, erpReminderNotificationsDBO, userId, erpNotificationsDBOReminderSet, erpSmsDBOSet, erpEmailDBOSet);
					erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
				});
			}	
		}
		if(Utils.isNullOrEmpty(data.getErpReminderNotificationsDTOList())) {
			Set<Integer> erpReminderId = existCalendarPersonalReminderMap.keySet();
			erpReminderId.forEach(ids -> {
				reminderId = ids;
			});
			ErpReminderNotificationsDBO erpReminderNotificationsDBO = existCalendarPersonalReminderMap.get(reminderId);
			Map<Integer,ErpNotificationsDBO> existErpNotificationMap = new HashMap<Integer, ErpNotificationsDBO>();
			Map<Integer,ErpSmsDBO> existErpSmsMap = new HashMap<Integer, ErpSmsDBO>();
			Map<Integer,ErpEmailsDBO> existErpEmailMap = new HashMap<Integer, ErpEmailsDBO>();
			if(!Utils.isNullOrEmpty(erpCalendarPersonalDBO) && !Utils.isNullOrEmpty(erpCalendarPersonalDBO.getErpReminderNotificationsDBOSet())) {
				erpCalendarPersonalDBO.getErpReminderNotificationsDBOSet().forEach(data1 -> {
					if(!Utils.isNullOrEmpty(data1.getErpNotificationsDBOSet())) {
						data1.getErpNotificationsDBOSet().forEach(data2 -> {
							existErpNotificationMap.put(data2.getId(), data2);
						});
					}
					if(!Utils.isNullOrEmpty(data1.getErpEmailsDBOSet())) {
						data1.getErpEmailsDBOSet().forEach(data3 -> {
							existErpEmailMap.put(data3.getId(), data3);
						});
					}
					if(!Utils.isNullOrEmpty(data1.getErpSmsDBOSet())) {
						data1.getErpSmsDBOSet().forEach(data4 -> {
							existErpSmsMap.put(data4.getId(), data4);
						});
					}
				});	
			}
			if(!Utils.isNullOrEmpty(existErpNotificationMap)) {
				existErpNotificationMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpNotificationsDBOReminderSet.add(value);
				});
				erpReminderNotificationsDBO.setErpNotificationsDBOSet(erpNotificationsDBOReminderSet);
				erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
			}
			if(!Utils.isNullOrEmpty(existErpSmsMap)) {
				existErpSmsMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpSmsDBOSet.add(value);
				});
				erpReminderNotificationsDBO.setErpSmsDBOSet(erpSmsDBOSet);
				erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
			}
			if(!Utils.isNullOrEmpty(existErpEmailMap)) {
				existErpEmailMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpEmailDBOSet.add(value);
				});
				erpReminderNotificationsDBO.setErpEmailsDBOSet(erpEmailDBOSet);
				erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
			}
		}
		if(!Utils.isNullOrEmpty(existCalendarPersonalReminderMap)) {
			existCalendarPersonalReminderMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				erpReminderNotificationsDBOSet.add(value);
			});
		}
		erpCalendarPersonalDBO.setErpReminderNotificationsDBOSet(erpReminderNotificationsDBOSet);
		reminderId = null;
		return erpCalendarPersonalDBO;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdateTodo(Mono<ErpCalendarToDoListDTO> dto, String userId) {
		return dto
				.handle((erpCalendarToDoListDTO, synchronousSink) ->  {
					synchronousSink.next(erpCalendarToDoListDTO);	
				}).cast(ErpCalendarToDoListDTO.class)
				.map(data -> convertCalendarToDoDtoToDbo(data, userId))
				.flatMap( s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						monthViewTransaction.update(s,s.getId());
					}else {
						monthViewTransaction.save1(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	Integer personalId = null;
	private ErpCalendarToDoListDBO convertCalendarToDoDtoToDbo(ErpCalendarToDoListDTO data, String userId) {
		ErpCalendarToDoListDBO erpCalendarToDoListDBO = Utils.isNullOrEmpty(data.getId()) ? new ErpCalendarToDoListDBO() : monthViewTransaction.getTodoDetails(data.getId());
		Map<Integer,ErpCalendarPersonalDBO> existTodoPersonalDBOMap = !Utils.isNullOrEmpty(erpCalendarToDoListDBO) && !Utils.isNullOrEmpty(erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet()) ? erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer, ErpCalendarPersonalDBO>();
		Set<ErpCalendarPersonalDBO> erpCalendarPersonalDBOSet = new HashSet<ErpCalendarPersonalDBO>();		
		Map<Integer,ErpReminderNotificationsDBO> existErpCalendarRemindersTodoMap = new HashMap<Integer, ErpReminderNotificationsDBO>(); 
		Set<ErpReminderNotificationsDBO> erpPersonalRemindersToDoSet = new HashSet<ErpReminderNotificationsDBO>();
		Set<ErpNotificationsDBO> erpNotificationsDBOSet = new HashSet<ErpNotificationsDBO>();
		Set<ErpSmsDBO> erpSmsDBOSet = new HashSet<ErpSmsDBO>();
		Set<ErpEmailsDBO> erpEmailsDBOSet = new HashSet<ErpEmailsDBO>();
		if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO) && !Utils.isNullOrEmpty(erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet())) {
			erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet().forEach(data1 -> {
				if(!Utils.isNullOrEmpty(data1)) {
					if(data1.getRecordStatus() == 'A') {
						if(!Utils.isNullOrEmpty(data1.getErpReminderNotificationsDBOSet())) {
							data1.getErpReminderNotificationsDBOSet().forEach(data2 -> {
								if(!Utils.isNullOrEmpty(data2)) {
									if(data2.getRecordStatus() == 'A' ) {
										if(data2.getErpCalendarPersonalDBO().getId() == data1.getId()) {
											existErpCalendarRemindersTodoMap.put(data2.getId(), data2);
											if(data2.getRecordStatus() == 'A') {
												reminderId = data2.getId();
											}
										}
									}
								}
							});
						}
					}
				}
			});
		}
		AtomicBoolean atomicBoolean = new AtomicBoolean();
		atomicBoolean.set(false);
		AtomicBoolean atomicBoolean1 = new AtomicBoolean();
		atomicBoolean1.set(false);
		if(!Utils.isNullOrEmpty(data)) {
			if(Utils.isNullOrEmpty(data.getId())) {
				erpCalendarToDoListDBO.setCreatedUsersId(Integer.parseInt(userId));
			}else {
				erpCalendarToDoListDBO.setModifiedUsersId(Integer.parseInt(userId));
			}
			erpCalendarToDoListDBO.setRecordStatus('A');
			if(!Utils.isNullOrEmpty(data.getToDoNote())) {
				erpCalendarToDoListDBO.setToDoNote(data.getToDoNote());
			}
			if(!Utils.isNullOrEmpty(data.isCompleted())) {
				erpCalendarToDoListDBO.setCompleted(data.isCompleted());
			}
			if(!Utils.isNullOrEmpty(userId)) {
				erpCalendarToDoListDBO.setEmpDBO(new EmpDBO());
				erpCalendarToDoListDBO.getEmpDBO().setId(Integer.parseInt(userId));
			}
			if(!Utils.isNullOrEmpty(data.getToDoDate())) {
				erpCalendarToDoListDBO.setToDoDate(data.getToDoDate());
				if(!Utils.isNullOrEmpty(data.getErpCalendarPersonalDTOList())) {
					data.getErpCalendarPersonalDTOList().forEach(erpPersonalTodo -> {
						ErpCalendarPersonalDBO erpPersonalTodoCalendarDBO = !Utils.isNullOrEmpty(existTodoPersonalDBOMap) && existTodoPersonalDBOMap.containsKey(erpPersonalTodo.getId()) ? existTodoPersonalDBOMap.get(erpPersonalTodo.getId()) : new ErpCalendarPersonalDBO(); 
						if(!Utils.isNullOrEmpty(erpPersonalTodo.getId())) {
							personalId = erpPersonalTodo.getId();
						}
						if(!Utils.isNullOrEmpty(existTodoPersonalDBOMap) && existTodoPersonalDBOMap.containsKey(erpPersonalTodo.getId())) {
							erpPersonalTodoCalendarDBO.setModifiedUsersId(Integer.parseInt(userId));
							existTodoPersonalDBOMap.remove(erpPersonalTodo.getId());
						}else {
							erpPersonalTodoCalendarDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						erpPersonalTodoCalendarDBO.setErpCalendarToDoListDBO(erpCalendarToDoListDBO);
						if(!Utils.isNullOrEmpty(userId)) {
							erpPersonalTodoCalendarDBO.setEmpDBO(new EmpDBO());
							erpPersonalTodoCalendarDBO.getEmpDBO().setId(Integer.parseInt(userId));
						}
						if(!Utils.isNullOrEmpty(erpPersonalTodo.isCompleted())) {
							erpPersonalTodoCalendarDBO.setCompleted(erpPersonalTodo.isCompleted());
						}
						erpPersonalTodoCalendarDBO.setRecordStatus('A');
						if(!Utils.isNullOrEmpty(erpPersonalTodo.getErpReminderNotificationsDTOList())) {
							erpPersonalTodo.getErpReminderNotificationsDTOList().forEach(reminderTodo -> {
								ErpReminderNotificationsDBO erpReminderNotificationsDBO = null;
								if(!Utils.isNullOrEmpty(existErpCalendarRemindersTodoMap) && existErpCalendarRemindersTodoMap.containsKey(reminderTodo.getId())) {
									erpReminderNotificationsDBO = existErpCalendarRemindersTodoMap.get(reminderTodo.getId());
									erpReminderNotificationsDBO.setModifiedUsersId(Integer.parseInt(userId));
									existErpCalendarRemindersTodoMap.remove(reminderTodo.getId());
								}else {
									erpReminderNotificationsDBO = new ErpReminderNotificationsDBO();
									erpReminderNotificationsDBO.setCreatedUsersId(Integer.parseInt(userId));
								}
								erpReminderNotificationsDBO.setRecordStatus('A');
								erpReminderNotificationsDBO.setErpCalendarPersonalDBO(erpPersonalTodoCalendarDBO);
								if(!Utils.isNullOrEmpty(reminderTodo.getReminderDateTime())) {
									erpReminderNotificationsDBO.setReminderDateTime(reminderTodo.getReminderDateTime());
								}
								if(!Utils.isNullOrEmpty(reminderTodo.getReminderComments())) {
									erpReminderNotificationsDBO.setReminderComments(reminderTodo.getReminderComments());
								}
								// common method messageSent
								erpReminderNotificationsDBO = this.messageSentDBO(reminderTodo, erpReminderNotificationsDBO, userId, erpNotificationsDBOSet, erpSmsDBOSet, erpEmailsDBOSet);
								erpPersonalRemindersToDoSet.add(erpReminderNotificationsDBO);
							});
							if(!atomicBoolean.get()) {
								if(!Utils.isNullOrEmpty(existErpCalendarRemindersTodoMap)) {
									existErpCalendarRemindersTodoMap.forEach((entry, value)-> {
										value.setModifiedUsersId( Integer.parseInt(userId));
										value.setRecordStatus('D');
										erpPersonalRemindersToDoSet.add(value);
									});
									atomicBoolean.set(true);
								}
								if(!atomicBoolean1.get()) {
									erpPersonalTodoCalendarDBO.setErpReminderNotificationsDBOSet(erpPersonalRemindersToDoSet);
									atomicBoolean1.set(true);
								}
							}
							erpCalendarPersonalDBOSet.add(erpPersonalTodoCalendarDBO);
						}else {
							AtomicBoolean reminderCheck = new AtomicBoolean();
							reminderCheck.set(false);
							Map<Integer,ErpNotificationsDBO> existErpNotificationMap = new HashMap<Integer, ErpNotificationsDBO>();
							Map<Integer,ErpSmsDBO> existErpSmsMap = new HashMap<Integer, ErpSmsDBO>();
							Map<Integer,ErpEmailsDBO> existErpEmailMap = new HashMap<Integer, ErpEmailsDBO>();
							Set<ErpReminderNotificationsDBO> erpNotificationsDBOReminderSet = new HashSet<ErpReminderNotificationsDBO>();
							ErpReminderNotificationsDBO erpReminderNotificationsDBO = !Utils.isNullOrEmpty(existErpCalendarRemindersTodoMap) && existErpCalendarRemindersTodoMap.containsKey(reminderId) ? existErpCalendarRemindersTodoMap.get(reminderId) : null;
							//							ErpCalendarPersonalDBO erpCalendarPersonalDBO = !Utils.isNullOrEmpty(existTodoPersonalDBOMap) &&  existTodoPersonalDBOMap.containsKey(personalId) ? existTodoPersonalDBOMap.get(personalId) : null;
							if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO) && !Utils.isNullOrEmpty(erpPersonalTodoCalendarDBO)) {
								data.getErpCalendarPersonalDTOList().forEach(reminder-> {
									if(!Utils.isNullOrEmpty(reminder) && !reminderCheck.get()) {
										if(Utils.isNullOrEmpty(reminder.getErpReminderNotificationsDTOList())) {
											if(!Utils.isNullOrEmpty(erpPersonalTodoCalendarDBO) && !Utils.isNullOrEmpty(erpPersonalTodoCalendarDBO.getErpReminderNotificationsDBOSet())) {
												erpPersonalTodoCalendarDBO.getErpReminderNotificationsDBOSet().forEach(data1 -> {
													if(!Utils.isNullOrEmpty(data1.getErpNotificationsDBOSet())) {
														data1.getErpNotificationsDBOSet().forEach(data2 -> {
															existErpNotificationMap.put(data2.getId(), data2);
														});
													}
													if(!Utils.isNullOrEmpty(data1.getErpEmailsDBOSet())) {
														data1.getErpEmailsDBOSet().forEach(data3 -> {
															existErpEmailMap.put(data3.getId(), data3);
														});
													}
													if(!Utils.isNullOrEmpty(data1.getErpSmsDBOSet())) {
														data1.getErpSmsDBOSet().forEach(data4 -> {
															existErpSmsMap.put(data4.getId(), data4);
														});
													}
												});	
											}
										}
										if(!Utils.isNullOrEmpty(existErpNotificationMap)) {
											existErpNotificationMap.forEach((entry, value)-> {
												value.setModifiedUsersId( Integer.parseInt(userId));
												value.setRecordStatus('D');
												erpNotificationsDBOSet.add(value);
											});
											erpReminderNotificationsDBO.setErpNotificationsDBOSet(erpNotificationsDBOSet);
											erpNotificationsDBOReminderSet.add(erpReminderNotificationsDBO);
										}
										if(!Utils.isNullOrEmpty(existErpSmsMap)) {
											existErpSmsMap.forEach((entry, value)-> {
												value.setModifiedUsersId( Integer.parseInt(userId));
												value.setRecordStatus('D');
												erpSmsDBOSet.add(value);
											});
											erpReminderNotificationsDBO.setErpSmsDBOSet(erpSmsDBOSet);
											erpNotificationsDBOReminderSet.add(erpReminderNotificationsDBO);
										}
										if(!Utils.isNullOrEmpty(existErpEmailMap)) {
											existErpEmailMap.forEach((entry, value)-> {
												value.setModifiedUsersId( Integer.parseInt(userId));
												value.setRecordStatus('D');
												erpEmailsDBOSet.add(value);
											});
											erpReminderNotificationsDBO.setErpEmailsDBOSet(erpEmailsDBOSet);
											erpNotificationsDBOReminderSet.add(erpReminderNotificationsDBO);
										}
										erpPersonalTodoCalendarDBO.setErpReminderNotificationsDBOSet(erpNotificationsDBOReminderSet);
										erpCalendarPersonalDBOSet.add(erpPersonalTodoCalendarDBO);
									}
									reminderCheck.set(true);
								});
								if(!atomicBoolean.get()) {
									if(!Utils.isNullOrEmpty(existErpCalendarRemindersTodoMap)) {
										existErpCalendarRemindersTodoMap.forEach((entry, value)-> {
											value.setModifiedUsersId( Integer.parseInt(userId));
											value.setRecordStatus('D');
											erpPersonalRemindersToDoSet.add(value);
										});
										atomicBoolean.set(true);
									}
									if(!atomicBoolean1.get()) {
										erpPersonalTodoCalendarDBO.setErpReminderNotificationsDBOSet(erpPersonalRemindersToDoSet);
										atomicBoolean1.set(true);
									}
								}

							}
						}
					});
				}
			}else {
				erpCalendarToDoListDBO.setToDoDate(null);
				erpCalendarToDoListDBO.setCreatedTime(LocalDateTime.now());
			}
			if(!Utils.isNullOrEmpty(existTodoPersonalDBOMap)) {
				existTodoPersonalDBOMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpCalendarPersonalDBOSet.add(value);
				});
			}
			erpCalendarToDoListDBO.setErpCalendarPersonalDBOSet(erpCalendarPersonalDBOSet);
		}
		reminderId = null;
		personalId = null;
		return erpCalendarToDoListDBO;
	}

	public Flux<ErpCalendarToDoListDTO> getToDoList(String userId,String date) {
		List<ErpCalendarToDoListDBO> erpCalendarToDoListDBOList = monthViewTransaction.getToDoList(userId,date);
		return convertToDoDBOToDTO(erpCalendarToDoListDBOList);
	}

	private Flux<ErpCalendarToDoListDTO> convertToDoDBOToDTO(List<ErpCalendarToDoListDBO> erpCalendarToDoListDBOList) {
		List<ErpCalendarToDoListDTO> erpCalendarToDoListDTOList = new ArrayList<ErpCalendarToDoListDTO>();
		Set<Integer> toDoDupli = new HashSet<Integer>();
		if(!Utils.isNullOrEmpty(erpCalendarToDoListDBOList)) {
			erpCalendarToDoListDBOList.forEach(toDoData -> {
				if(!toDoDupli.contains(toDoData.getId())) {
					toDoDupli.add(toDoData.getId());
					ErpCalendarToDoListDTO erpCalendarToDoListDTO = new ErpCalendarToDoListDTO();
					erpCalendarToDoListDTO.setId(toDoData.getId());
					if(!Utils.isNullOrEmpty(toDoData.getToDoNote())) {
						erpCalendarToDoListDTO.setToDoNote(toDoData.getToDoNote());
					}
					if(!Utils.isNullOrEmpty(toDoData.isCompleted())) {
						erpCalendarToDoListDTO.setCompleted(toDoData.isCompleted());
					}
					if(!Utils.isNullOrEmpty(toDoData.getEmpDBO())) {
						if(!Utils.isNullOrEmpty(toDoData.getEmpDBO().getId())) {
							erpCalendarToDoListDTO.setEmpId(toDoData.getEmpDBO().getId());
						}
					}
					erpCalendarToDoListDTO.setRecordStatus(toDoData.getRecordStatus());
					if(!Utils.isNullOrEmpty(toDoData.getToDoDate())) {
						erpCalendarToDoListDTO.setToDoDate(toDoData.getToDoDate());
						if(!Utils.isNullOrEmpty(toDoData.getErpCalendarPersonalDBOSet())) {
							toDoData.getErpCalendarPersonalDBOSet().forEach(personalTodo -> {
								if(personalTodo.getRecordStatus() == 'A') {
									if(personalTodo.getErpCalendarToDoListDBO().getId() == toDoData.getId()) {
										ErpCalendarPersonalDTO erpCalendarPersonalDTO = new ErpCalendarPersonalDTO();
										erpCalendarPersonalDTO.setId(personalTodo.getId());
										erpCalendarPersonalDTO.setEmpId(personalTodo.getEmpDBO().getId());
										erpCalendarPersonalDTO.setRecordStatus(personalTodo.getRecordStatus());
										if(!Utils.isNullOrEmpty(personalTodo.isCompleted())) {
											erpCalendarPersonalDTO.setCompleted(personalTodo.isCompleted());
										}
										erpCalendarPersonalDTO.setRecordStatus(personalTodo.getRecordStatus());
										if(!Utils.isNullOrEmpty(personalTodo.getErpReminderNotificationsDBOSet())) {
											personalTodo.getErpReminderNotificationsDBOSet().forEach(reminderData -> {
												if(reminderData.getRecordStatus() == 'A') {
													if(personalTodo.getId() == reminderData.getErpCalendarPersonalDBO().getId()) {
														ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
														erpReminderNotificationsDTO.setId(reminderData.getId());
														erpReminderNotificationsDTO.setReminderDateTime(reminderData.getReminderDateTime());
														erpReminderNotificationsDTO.setRecordStatus(reminderData.getRecordStatus());
														erpReminderNotificationsDTO.setReminderComments(reminderData.getReminderComments());
														// common method message sent dto
														erpReminderNotificationsDTO = this.messageSentDto(reminderData, erpReminderNotificationsDTO);
														erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(new ArrayList<ErpReminderNotificationsDTO>());
														erpCalendarPersonalDTO.getErpReminderNotificationsDTOList().add(erpReminderNotificationsDTO);
													}
												}
											});
										}else {
											List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOList = new ArrayList<ErpReminderNotificationsDTO>();
											ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
											erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
											erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
										}
										erpCalendarToDoListDTO.setErpCalendarPersonalDTOList(new ArrayList<ErpCalendarPersonalDTO>());
										erpCalendarToDoListDTO.getErpCalendarPersonalDTOList().add(erpCalendarPersonalDTO);
									}
								}
							});
						}
					}else {
						ErpCalendarPersonalDTO erpCalendarPersonalDTO = new ErpCalendarPersonalDTO();
						erpCalendarToDoListDTO.setErpCalendarPersonalDTOList(new ArrayList<ErpCalendarPersonalDTO>());
						List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOList = new ArrayList<ErpReminderNotificationsDTO>();
						ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
						erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
						erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
						erpCalendarToDoListDTO.getErpCalendarPersonalDTOList().add(erpCalendarPersonalDTO);
					}
					erpCalendarToDoListDTOList.add(erpCalendarToDoListDTO);
				}
			});
		}
		return Flux.fromIterable(erpCalendarToDoListDTOList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> deleteToDo(int id, String userId) {
		return monthViewTransaction.deleteToDo(id, userId).map(Utils::responseResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdateEventDetails(Mono<ErpCalendarDTO> dto, String userId) {
		return dto
				.handle((erpCalendarDTO, synchronousSink) ->  {
					synchronousSink.next(erpCalendarDTO);	
				}).cast(ErpCalendarDTO.class)
				.map(data -> ConvertEventDetailsToDbo(data, userId))
				.flatMap( s -> {
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private boolean ConvertEventDetailsToDbo(ErpCalendarDTO data, String userId) {
		boolean isTrue = false;
		Set<ErpNotificationsDBO> erpNotificationsDBOSet = new HashSet<ErpNotificationsDBO>();
		Set<ErpSmsDBO> erpSmsDBOSet = new HashSet<ErpSmsDBO>();
		Set<ErpEmailsDBO> erpEmailsDBOSet = new HashSet<ErpEmailsDBO>();
		if(!Utils.isNullOrEmpty(data)) {
			if(data.getTableName().equalsIgnoreCase("CALENDAR") || data.getTableName().equalsIgnoreCase("PERSONAL")) {
				ErpCalendarDBO erpCalendarDBO = monthViewTransaction.getCalendarDetails(data.getId());
				if(!Utils.isNullOrEmpty(erpCalendarDBO)) {
					Set<ErpCalendarPersonalDBO> erpCalendarPersonalDBOSet = new HashSet<ErpCalendarPersonalDBO>();
					Map<Integer,ErpCalendarPersonalDBO> existCalendarPersonalMap = !Utils.isNullOrEmpty(erpCalendarDBO) && !Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarPersonalDBOSet()) ? erpCalendarDBO.getErpCalendarPersonalDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer, ErpCalendarPersonalDBO>();
					Map<Integer,ErpReminderNotificationsDBO> existCalendarReminder = new HashMap<Integer, ErpReminderNotificationsDBO>();
					Set<Integer> reminderDupli = new HashSet<Integer>();
					if(!Utils.isNullOrEmpty(erpCalendarDBO)) {
						if(!Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarPersonalDBOSet())) {
							erpCalendarDBO.getErpCalendarPersonalDBOSet().forEach(personal -> {
								if(!Utils.isNullOrEmpty(personal.getErpReminderNotificationsDBOSet())) {
									personal.getErpReminderNotificationsDBOSet().forEach(reminder -> {
										if(!reminderDupli.contains(reminder.getId())) {
											reminderDupli.add(reminder.getId());
											existCalendarReminder.put(reminder.getId(), reminder);
											if(reminder.getRecordStatus() == 'A')
												reminderId = reminder.getId();
										}
									});
								}
							});
						}
					}
					if(!Utils.isNullOrEmpty(data.getErpCalendarPersonalDTOList())) {
						data.getErpCalendarPersonalDTOList().forEach(personalData -> {
							if(!Utils.isNullOrEmpty(personalData)) {
								ErpCalendarPersonalDBO erpCalendarPersonalDBO = null;	
								if(!Utils.isNullOrEmpty(existCalendarPersonalMap) && existCalendarPersonalMap.containsKey(personalData.getId())) {
									erpCalendarPersonalDBO = existCalendarPersonalMap.get(personalData.getId());
									erpCalendarPersonalDBO.setModifiedUsersId(Integer.parseInt(userId));
									existCalendarPersonalMap.remove(personalData.getId());
								}else {
									erpCalendarPersonalDBO = new ErpCalendarPersonalDBO();
									erpCalendarPersonalDBO.setCreatedUsersId(Integer.parseInt(userId));
								}
								if(!Utils.isNullOrEmpty(personalData.getEventsNote())) {
									erpCalendarPersonalDBO.setEventsNote(personalData.getEventsNote());
								}
								if(!Utils.isNullOrEmpty(userId)) {
									EmpDBO empDBO = new EmpDBO();
									empDBO.setId(Integer.parseInt(userId));
									erpCalendarPersonalDBO.setEmpDBO(empDBO);
								}
								if(!Utils.isNullOrEmpty(data.getId())) {
									erpCalendarPersonalDBO.setErpCalendarDBO(new ErpCalendarDBO());
									erpCalendarPersonalDBO.getErpCalendarDBO().setId(data.getId());
								}
								if(!Utils.isNullOrEmpty(personalData.isImportant())) {
									erpCalendarPersonalDBO.setImportant(personalData.isImportant());
								}
								if(!Utils.isNullOrEmpty(personalData.getImportantPriority())) {
									erpCalendarPersonalDBO.setImportantPriority(personalData.getImportantPriority());
								}
								erpCalendarPersonalDBO.setRecordStatus('A');
								Set<ErpReminderNotificationsDBO> erpReminderNotificationsDBOSet = new HashSet<ErpReminderNotificationsDBO>();
								if(!Utils.isNullOrEmpty(personalData.getErpReminderNotificationsDTOList())) {
									boolean value = false;
									ErpCalendarPersonalDBO erpCalendarPersonalDBO1 = erpCalendarPersonalDBO;
									personalData.getErpReminderNotificationsDTOList().forEach(reminderData -> {
										if(!Utils.isNullOrEmpty(reminderData)) {
											ErpReminderNotificationsDBO erpReminderNotificationsDBO = null;
											if(!Utils.isNullOrEmpty(existCalendarReminder) && existCalendarReminder.containsKey(reminderData.getId())) {
												erpReminderNotificationsDBO = existCalendarReminder.get(reminderData.getId());
												erpReminderNotificationsDBO.setModifiedUsersId(Integer.parseInt(userId));
												existCalendarReminder.remove(reminderData.getId());
											}else {
												erpReminderNotificationsDBO = new ErpReminderNotificationsDBO();
												erpReminderNotificationsDBO.setCreatedUsersId(Integer.parseInt(userId));
											}
											erpReminderNotificationsDBO.setRecordStatus('A');
											erpReminderNotificationsDBO.setErpCalendarPersonalDBO(erpCalendarPersonalDBO1);
											if(!Utils.isNullOrEmpty(reminderData.getReminderDateTime())) {
												erpReminderNotificationsDBO.setReminderDateTime(reminderData.getReminderDateTime());
											}
											if(!Utils.isNullOrEmpty(reminderData.getReminderComments())) {
												erpReminderNotificationsDBO.setReminderComments(reminderData.getReminderComments());
											}
											// common method messageSent
											erpReminderNotificationsDBO = this.messageSentDBO(reminderData, erpReminderNotificationsDBO, userId, erpNotificationsDBOSet, erpSmsDBOSet, erpEmailsDBOSet);
											erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
										}
									});
									if(!value) {
										if(!Utils.isNullOrEmpty(existCalendarReminder)) {
											existCalendarReminder.forEach((entry, value1)-> {
												value1.setModifiedUsersId( Integer.parseInt(userId));
												value1.setRecordStatus('D');
												erpReminderNotificationsDBOSet.add(value1);
											});
										}
										value = true;
									}
									erpCalendarPersonalDBO.setErpReminderNotificationsDBOSet(erpReminderNotificationsDBOSet);
								}else {
									if(!Utils.isNullOrEmpty(reminderId) && !Utils.isNullOrEmpty(existCalendarReminder)) {
										if(existCalendarReminder.containsKey(reminderId)) {
											ErpReminderNotificationsDBO erpReminderNotificationsDBO = existCalendarReminder.get(reminderId);
											Map<Integer,ErpNotificationsDBO> existErpNotificationMap = new HashMap<Integer, ErpNotificationsDBO>();
											Map<Integer,ErpSmsDBO> existErpSmsMap = new HashMap<Integer, ErpSmsDBO>();
											Map<Integer,ErpEmailsDBO> existErpEmailMap = new HashMap<Integer, ErpEmailsDBO>();
											if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO)) {
												if(erpReminderNotificationsDBO.getRecordStatus() == 'A') {
													if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpNotificationsDBOSet())) {
														erpReminderNotificationsDBO.getErpNotificationsDBOSet().forEach(dbo1 -> {
															if(dbo1.getRecordStatus() == 'A') {
																existErpNotificationMap.put(dbo1.getId(), dbo1);	
															}
														});
													}
													if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpSmsDBOSet())) {
														erpReminderNotificationsDBO.getErpSmsDBOSet().forEach(dbo2 -> {
															if(dbo2.getRecordStatus() == 'A') {
																existErpSmsMap.put(dbo2.getId(), dbo2);	
															}
														});
													}
													if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpEmailsDBOSet())) {
														erpReminderNotificationsDBO.getErpEmailsDBOSet().forEach(dbo3 -> {
															if(dbo3.getRecordStatus() == 'A') {
																existErpEmailMap.put(dbo3.getId(), dbo3);	
															}
														});
													}
												}		
												if(!Utils.isNullOrEmpty(existErpNotificationMap)) {
													existErpNotificationMap.forEach((entry, value)-> {
														value.setModifiedUsersId( Integer.parseInt(userId));
														value.setRecordStatus('D');
														erpNotificationsDBOSet.add(value);
													});
													erpReminderNotificationsDBO.setErpNotificationsDBOSet(erpNotificationsDBOSet);
													erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
												}
												if(!Utils.isNullOrEmpty(existErpSmsMap)) {
													existErpSmsMap.forEach((entry, value)-> {
														value.setModifiedUsersId( Integer.parseInt(userId));
														value.setRecordStatus('D');
														erpSmsDBOSet.add(value);
													});
													erpReminderNotificationsDBO.setErpSmsDBOSet(erpSmsDBOSet);
													erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
												}
												if(!Utils.isNullOrEmpty(existErpEmailMap)) {
													existErpEmailMap.forEach((entry, value)-> {
														value.setModifiedUsersId( Integer.parseInt(userId));
														value.setRecordStatus('D');
														erpEmailsDBOSet.add(value);
													});
													erpReminderNotificationsDBO.setErpEmailsDBOSet(erpEmailsDBOSet);
													erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
												}
												erpCalendarPersonalDBO.setErpReminderNotificationsDBOSet(erpReminderNotificationsDBOSet);
											}
										}
									}
								}
								erpCalendarPersonalDBOSet.add(erpCalendarPersonalDBO);
							}
						});	
					}
					if(!Utils.isNullOrEmpty(existCalendarPersonalMap)) {
						existCalendarPersonalMap.forEach((entry, value)-> {
							value.setModifiedUsersId( Integer.parseInt(userId));
							value.setRecordStatus('D');
							erpCalendarPersonalDBOSet.add(value);
						});
					}
					erpCalendarDBO.setErpCalendarPersonalDBOSet(erpCalendarPersonalDBOSet);
					isTrue = monthViewTransaction.update1(erpCalendarDBO);
				}	
			} else if(data.getTableName().equalsIgnoreCase("TODO")) {
				ErpCalendarToDoListDBO erpCalendarToDoListDBO = monthViewTransaction.getTodoDetails(data.getErpCalendarToDoListDTO().getId());
				Set<ErpCalendarPersonalDBO> erpCalendarPersonalDBOSet = new HashSet<ErpCalendarPersonalDBO>();
				Set<ErpReminderNotificationsDBO> erpReminderNotificationsDBOSet = new HashSet<ErpReminderNotificationsDBO>();
				if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO)) {
					Map<Integer,ErpCalendarPersonalDBO> existPersonalTodoMap = !Utils.isNullOrEmpty(erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet()) ? erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer,ErpCalendarPersonalDBO>();
					Map<Integer,ErpReminderNotificationsDBO> existReminderTodoMap = new HashMap<Integer, ErpReminderNotificationsDBO>();
					Set<Integer> reminderIds = new HashSet<Integer>();
					if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet())) {
						erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet().forEach(personalTodo -> {
							if(!Utils.isNullOrEmpty(personalTodo)) {
								if(personalTodo.getRecordStatus() == 'A') {
									if(!Utils.isNullOrEmpty(personalTodo.getErpReminderNotificationsDBOSet())) {
										personalTodo.getErpReminderNotificationsDBOSet().forEach(reminderTodo -> {
											if(!Utils.isNullOrEmpty(reminderTodo)) {
												if(reminderTodo.getRecordStatus() == 'A') {
													if(!reminderIds.contains(reminderTodo.getId())) {
														reminderIds.add(reminderTodo.getId());
														existReminderTodoMap.put(reminderTodo.getId(), reminderTodo);
														if(reminderTodo.getRecordStatus() == 'A')
															reminderId = reminderTodo.getId();
													}
												}
											}
										}); 
									}
								}
							}
						});
					}
					erpCalendarToDoListDBO.setModifiedUsersId(Integer.parseInt(userId));
					if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO().getToDoNote())) {
						erpCalendarToDoListDBO.setToDoNote(data.getErpCalendarToDoListDTO().getToDoNote());
					}
					if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO().getToDoDate())) {
						erpCalendarToDoListDBO.setToDoDate(data.getErpCalendarToDoListDTO().getToDoDate());
					}
					if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO().isCompleted())) {
						erpCalendarToDoListDBO.setCompleted(data.getErpCalendarToDoListDTO().isCompleted());
					}
					if(!Utils.isNullOrEmpty(userId)) {
						erpCalendarToDoListDBO.setEmpDBO(new EmpDBO());
						erpCalendarToDoListDBO.getEmpDBO().setId(Integer.parseInt(userId));
					}
					if(!Utils.isNullOrEmpty(data.getErpCalendarToDoListDTO().getErpCalendarPersonalDTOList())) {
						data.getErpCalendarToDoListDTO().getErpCalendarPersonalDTOList().forEach(personalTodo -> {
							ErpCalendarPersonalDBO erpCalendarPersonalDBO = null;
							if(!Utils.isNullOrEmpty(existPersonalTodoMap) && existPersonalTodoMap.containsKey(personalTodo.getId())) {
								erpCalendarPersonalDBO = existPersonalTodoMap.get(personalTodo.getId());
								erpCalendarPersonalDBO.setModifiedUsersId(Integer.parseInt(userId));
								existPersonalTodoMap.remove(personalTodo.getId());
							} else {
								erpCalendarPersonalDBO = new ErpCalendarPersonalDBO();
								erpCalendarPersonalDBO.setCreatedUsersId(Integer.parseInt(userId));
							}
							erpCalendarPersonalDBO.setRecordStatus('A');
							erpCalendarPersonalDBO.setErpCalendarToDoListDBO(erpCalendarToDoListDBO);
							if(!Utils.isNullOrEmpty(personalTodo.isCompleted())) {
								erpCalendarPersonalDBO.setCompleted(personalTodo.isCompleted());
							}
							if(!Utils.isNullOrEmpty(personalTodo.getErpReminderNotificationsDTOList())) {
								boolean isValue = false;
								ErpCalendarPersonalDBO erpCalendarPersonalDBO1 = erpCalendarPersonalDBO;
								personalTodo.getErpReminderNotificationsDTOList().forEach(reminderTodo -> {	
									ErpReminderNotificationsDBO erpReminderNotificationsDBO = null;
									if(!Utils.isNullOrEmpty(existReminderTodoMap) && existReminderTodoMap.containsKey(reminderTodo.getId())) {
										erpReminderNotificationsDBO = existReminderTodoMap.get(reminderTodo.getId());
										erpReminderNotificationsDBO.setModifiedUsersId(Integer.parseInt(userId));
										existReminderTodoMap.remove(reminderTodo.getId());
									} else {
										erpReminderNotificationsDBO = new ErpReminderNotificationsDBO();
										erpReminderNotificationsDBO.setCreatedUsersId(Integer.parseInt(userId));
									}
									erpReminderNotificationsDBO.setRecordStatus('A');
									erpReminderNotificationsDBO.setErpCalendarPersonalDBO(erpCalendarPersonalDBO1);
									if(!Utils.isNullOrEmpty(reminderTodo.getReminderDateTime())) {
										erpReminderNotificationsDBO.setReminderDateTime(reminderTodo.getReminderDateTime());
									}
									if(!Utils.isNullOrEmpty(reminderTodo.getReminderComments())) {
										erpReminderNotificationsDBO.setReminderComments(reminderTodo.getReminderComments());
									}
									//common method messageSent
									erpReminderNotificationsDBO = this.messageSentDBO(reminderTodo, erpReminderNotificationsDBO, userId, erpNotificationsDBOSet, erpSmsDBOSet, erpEmailsDBOSet);
									erpReminderNotificationsDBOSet.add(erpReminderNotificationsDBO);
								});
								erpCalendarPersonalDBO = erpCalendarPersonalDBO1;
								if(!isValue) {
									if(!Utils.isNullOrEmpty(existReminderTodoMap)) {
										existReminderTodoMap.forEach((entry, value)-> {
											value.setModifiedUsersId( Integer.parseInt(userId));
											value.setRecordStatus('D');
											erpReminderNotificationsDBOSet.add(value);
										});
									}
									isValue = true;
								}
								erpCalendarPersonalDBO.setErpReminderNotificationsDBOSet(erpReminderNotificationsDBOSet);
							}else {
								if(!Utils.isNullOrEmpty(reminderId) && !Utils.isNullOrEmpty(existReminderTodoMap)) {
									if(existReminderTodoMap.containsKey(reminderId)) {
										ErpReminderNotificationsDBO erpReminderNotificationsDBO = existReminderTodoMap.get(reminderId);
										Map<Integer,ErpNotificationsDBO> existErpNotificationMap = new HashMap<Integer, ErpNotificationsDBO>();
										Map<Integer,ErpSmsDBO> existErpSmsMap = new HashMap<Integer, ErpSmsDBO>();
										Map<Integer,ErpEmailsDBO> existErpEmailMap = new HashMap<Integer, ErpEmailsDBO>();
										Set<ErpReminderNotificationsDBO> erpNotificationsDBOReminderSet = new HashSet<ErpReminderNotificationsDBO>();
										if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO)) {
											if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpNotificationsDBOSet())) {
												erpReminderNotificationsDBO.getErpNotificationsDBOSet().forEach(dbo1 -> {
													if(dbo1.getRecordStatus() == 'A') {
														existErpNotificationMap.put(dbo1.getId(), dbo1);
													}	
												});
											}
											if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpSmsDBOSet())) {
												erpReminderNotificationsDBO.getErpSmsDBOSet().forEach(dbo2 -> {
													if(dbo2.getRecordStatus() == 'A') {
														existErpSmsMap.put(dbo2.getId(), dbo2);
													}	
												});
											}
											if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpEmailsDBOSet())) {
												erpReminderNotificationsDBO.getErpEmailsDBOSet().forEach(dbo3 -> {
													if(dbo3.getRecordStatus() == 'A') {
														existErpEmailMap.put(dbo3.getId(), dbo3);
													}	
												});
											}
										}	
										if(!Utils.isNullOrEmpty(existErpNotificationMap)) {
											existErpNotificationMap.forEach((entry, value)-> {
												value.setModifiedUsersId( Integer.parseInt(userId));
												value.setRecordStatus('D');
												erpNotificationsDBOSet.add(value);
											});
											erpReminderNotificationsDBO.setErpNotificationsDBOSet(erpNotificationsDBOSet);
											erpNotificationsDBOReminderSet.add(erpReminderNotificationsDBO);
										}
										if(!Utils.isNullOrEmpty(existErpSmsMap)) {
											existErpSmsMap.forEach((entry, value)-> {
												value.setModifiedUsersId( Integer.parseInt(userId));
												value.setRecordStatus('D');
												erpSmsDBOSet.add(value);
											});
											erpReminderNotificationsDBO.setErpSmsDBOSet(erpSmsDBOSet);
											erpNotificationsDBOReminderSet.add(erpReminderNotificationsDBO);
										}
										if(!Utils.isNullOrEmpty(existErpEmailMap)) {
											existErpEmailMap.forEach((entry, value)-> {
												value.setModifiedUsersId( Integer.parseInt(userId));
												value.setRecordStatus('D');
												erpEmailsDBOSet.add(value);
											});
											erpReminderNotificationsDBO.setErpEmailsDBOSet(erpEmailsDBOSet);
											erpNotificationsDBOReminderSet.add(erpReminderNotificationsDBO);
										}
										erpCalendarPersonalDBO.setErpReminderNotificationsDBOSet(erpNotificationsDBOReminderSet);
										erpCalendarPersonalDBOSet.add(erpCalendarPersonalDBO);
										erpCalendarPersonalDBO.setErpReminderNotificationsDBOSet(erpReminderNotificationsDBOSet);
									}
								}
							}
						});
					}
					if(!Utils.isNullOrEmpty(existPersonalTodoMap)) {
						existPersonalTodoMap.forEach((entry, value)-> {
							value.setModifiedUsersId( Integer.parseInt(userId));
							value.setRecordStatus('D');
							erpCalendarPersonalDBOSet.add(value);
						});
					}
					erpCalendarToDoListDBO.setErpCalendarPersonalDBOSet(erpCalendarPersonalDBOSet);
					isTrue = monthViewTransaction.update2(erpCalendarToDoListDBO);
				}
			}
		}
		return isTrue;
	}

	public Mono<ErpCalendarDTO> getEventDetails(ErpCalendarDTO data, String userId) {
		ErpCalendarDTO erpCalendarDTO = new ErpCalendarDTO();
		ErpCalendarPersonalDTO erpCalendarPersonalDTO = new ErpCalendarPersonalDTO();
		List<ErpCalendarPersonalDTO> erpCalendarPersonalDTOList = new ArrayList<ErpCalendarPersonalDTO>();
		ErpReminderNotificationsDTO erpReminderNotificationsDTO = new ErpReminderNotificationsDTO();
		List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOList = new ArrayList<ErpReminderNotificationsDTO>();
		if(!Utils.isNullOrEmpty(data)) {
			ErpCalendarDBO erpCalendarDBO = data.getTableName().equalsIgnoreCase("CALENDAR") || data.getTableName().equalsIgnoreCase("PERSONAL") ? monthViewTransaction.getCalendarDetails(data.getId()) : null;
			ErpCalendarToDoListDBO erpCalendarToDoListDBO = data.getTableName().equalsIgnoreCase("TODO") ? monthViewTransaction.getTodoDetails(data.getId()) : null;
			Set<ErpCalendarPersonalDBO> erpCalendarPersonalSet = !Utils.isNullOrEmpty(erpCalendarDBO) && !Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarPersonalDBOSet()) ? erpCalendarDBO.getErpCalendarPersonalDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toSet()) : null;
			Set<ErpCalendarPersonalDBO> erpCalendarPersonalDBOTOdoSet = !Utils.isNullOrEmpty(erpCalendarToDoListDBO) && !Utils.isNullOrEmpty(erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet()) ? erpCalendarToDoListDBO.getErpCalendarPersonalDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toSet()) : null;
			if(data.getTableName().equalsIgnoreCase("CALENDAR") || data.getTableName().equalsIgnoreCase("PERSONAL")) {
				if(!Utils.isNullOrEmpty(erpCalendarDBO)) {
					erpCalendarDTO.setId(erpCalendarDBO.getId());
					erpCalendarDTO.setActivitiesEvents(erpCalendarDBO.getActivitiesEvents());
					erpCalendarDTO.setFromDate(erpCalendarDBO.getFromDate());
					erpCalendarDTO.setToDate(erpCalendarDBO.getToDate());
					erpCalendarDTO.setRecordStatus(erpCalendarDBO.getRecordStatus());				
					if(!Utils.isNullOrEmpty(erpCalendarPersonalSet)) {
						erpCalendarPersonalSet.forEach(personalCalendar -> {
							if(!Utils.isNullOrEmpty(personalCalendar)) {
								erpCalendarPersonalDTO.setId(personalCalendar.getId());
								erpCalendarDTO.setTableName("PERSONAL");
								if(!Utils.isNullOrEmpty(personalCalendar.getEmpDBO())) {
									if(!Utils.isNullOrEmpty(personalCalendar.getEmpDBO().getId())) {
										erpCalendarPersonalDTO.setEmpId(personalCalendar.getEmpDBO().getId());
									}
								}
								if(!Utils.isNullOrEmpty(personalCalendar.getEventsNote())) {
									erpCalendarPersonalDTO.setEventsNote(personalCalendar.getEventsNote());
								}
								if(!Utils.isNullOrEmpty(personalCalendar.getImportantPriority().trim())) {
									if(personalCalendar.getImportantPriority().trim().equalsIgnoreCase("High Priority")) {
										erpCalendarPersonalDTO.setColor("#FF0000");
										erpCalendarPersonalDTO.setImportantPriority(personalCalendar.getImportantPriority().trim());
										if(!Utils.isNullOrEmpty(personalCalendar.isImportant())) {
											erpCalendarDTO.setImportant(personalCalendar.isImportant());
											erpCalendarPersonalDTO.setImportant(personalCalendar.isImportant());
										}
									}
									if(personalCalendar.getImportantPriority().trim().equalsIgnoreCase("Medium Priority")) {
										erpCalendarPersonalDTO.setColor("#FFA500");
										erpCalendarPersonalDTO.setImportantPriority(personalCalendar.getImportantPriority().trim());
										if(!Utils.isNullOrEmpty(personalCalendar.isImportant())) {
											erpCalendarDTO.setImportant(personalCalendar.isImportant());
											erpCalendarPersonalDTO.setImportant(personalCalendar.isImportant());
										}
									}
									if(personalCalendar.getImportantPriority().trim().equalsIgnoreCase("Low Priority")) {
										erpCalendarPersonalDTO.setColor(" #FFFF00");
										erpCalendarPersonalDTO.setImportantPriority(personalCalendar.getImportantPriority().trim());
										if(!Utils.isNullOrEmpty(personalCalendar.isImportant())) {
											erpCalendarDTO.setImportant(personalCalendar.isImportant());
											erpCalendarPersonalDTO.setImportant(personalCalendar.isImportant());
										}
									}
									if(personalCalendar.getImportantPriority().trim().equalsIgnoreCase("Not Important")) {
										erpCalendarPersonalDTO.setColor("#FFFFFF");
										erpCalendarPersonalDTO.setImportantPriority(personalCalendar.getImportantPriority().trim());
										erpCalendarPersonalDTO.setImportant(personalCalendar.isImportant());
									}

								}else {
									erpCalendarPersonalDTO.setColor("#FFFFFF");
									erpCalendarPersonalDTO.setImportantPriority("Not Important");
									erpCalendarPersonalDTO.setImportant(personalCalendar.isImportant());
								}
								erpCalendarPersonalDTO.setRecordStatus(personalCalendar.getRecordStatus());
								Set<ErpReminderNotificationsDBO> erpReminderNotificationsDBOSet = !Utils.isNullOrEmpty(personalCalendar) && !Utils.isNullOrEmpty(personalCalendar.getErpReminderNotificationsDBOSet()) ? personalCalendar.getErpReminderNotificationsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toSet()) : null;
								if(!Utils.isNullOrEmpty(erpReminderNotificationsDBOSet)) {
									erpReminderNotificationsDBOSet.forEach(reminderData -> {
										if(!Utils.isNullOrEmpty(reminderData)) {
											if(reminderData.getRecordStatus() == 'A') {
												if(personalCalendar.getId() == reminderData.getErpCalendarPersonalDBO().getId()) {
													erpReminderNotificationsDTO.setId(reminderData.getId());
													erpReminderNotificationsDTO.setRecordStatus(reminderData.getRecordStatus());
													erpReminderNotificationsDTO.setReminderDateTime(reminderData.getReminderDateTime());
													erpReminderNotificationsDTO.setReminderComments(reminderData.getReminderComments());
													// common method for message sent dto
													ErpReminderNotificationsDTO erpReminderNotificationsDTO1 = this.messageSentDto(reminderData, erpReminderNotificationsDTO);
													erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(new ArrayList<ErpReminderNotificationsDTO>());
													erpCalendarPersonalDTO.getErpReminderNotificationsDTOList().add(erpReminderNotificationsDTO1);
												}
											}
										}
									});
								}else {
									erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
									erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
								}
							}
						});
					} else {
						erpCalendarDTO.setTableName("CALENDAR");
						erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
						erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
					}
					erpCalendarPersonalDTOList.add(erpCalendarPersonalDTO);	
					erpCalendarDTO.setErpCalendarPersonalDTOList(erpCalendarPersonalDTOList);
				}
			} else if(data.getTableName().equalsIgnoreCase("TODO")) {
				if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO)) {
					ErpCalendarToDoListDTO erpCalendarToDoListDTO = new ErpCalendarToDoListDTO();
					erpCalendarToDoListDTO.setId(erpCalendarToDoListDBO.getId());
					erpCalendarToDoListDTO.setRecordStatus(erpCalendarToDoListDBO.getRecordStatus());
					if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO.getEmpDBO())) {
						if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO.getEmpDBO().getId())) {
							erpCalendarToDoListDTO.setEmpId(erpCalendarToDoListDBO.getEmpDBO().getId());
						}
					}
					if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO.getToDoNote())) {
						erpCalendarToDoListDTO.setToDoNote(erpCalendarToDoListDBO.getToDoNote());
					}
					if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO.getToDoDate())) {
						erpCalendarToDoListDTO.setToDoDate(erpCalendarToDoListDBO.getToDoDate());
					}
					if(!Utils.isNullOrEmpty(erpCalendarToDoListDBO.isCompleted())) {
						erpCalendarToDoListDTO.setCompleted(erpCalendarToDoListDBO.isCompleted());
					}
					if(!Utils.isNullOrEmpty(erpCalendarPersonalDBOTOdoSet)) {
						erpCalendarPersonalDBOTOdoSet.forEach(personalTodo -> {
							if(!Utils.isNullOrEmpty(personalTodo)) {
								if(personalTodo.getRecordStatus() == 'A') {
									erpCalendarPersonalDTO.setId(personalTodo.getId());
									erpCalendarPersonalDTO.setRecordStatus(personalTodo.getRecordStatus());
									if(!Utils.isNullOrEmpty(personalTodo.isCompleted())) {
										erpCalendarPersonalDTO.setCompleted(personalTodo.isCompleted());
									}
									Set<ErpReminderNotificationsDBO> erpReminderNotificationsDBOSet = !Utils.isNullOrEmpty(personalTodo) && !Utils.isNullOrEmpty(personalTodo.getErpReminderNotificationsDBOSet()) ? personalTodo.getErpReminderNotificationsDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toSet()) : null;
									if(!Utils.isNullOrEmpty(erpReminderNotificationsDBOSet)) {
										erpReminderNotificationsDBOSet.forEach(reminderTodo -> {
											if(!Utils.isNullOrEmpty(reminderTodo)) {
												if(reminderTodo.getRecordStatus() == 'A') {
													erpReminderNotificationsDTO.setId(reminderTodo.getId());
													erpReminderNotificationsDTO.setReminderDateTime(reminderTodo.getReminderDateTime());
													erpReminderNotificationsDTO.setRecordStatus(reminderTodo.getRecordStatus());
													erpReminderNotificationsDTO.setReminderComments(reminderTodo.getReminderComments());
													//common method for message sent dto
													ErpReminderNotificationsDTO erpReminderNotificationsDTO1 = this.messageSentDto(reminderTodo, erpReminderNotificationsDTO);
													erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO1);
													erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(new ArrayList<ErpReminderNotificationsDTO>());
													erpCalendarPersonalDTO.getErpReminderNotificationsDTOList().add(erpReminderNotificationsDTO1);
												}
											}
										});
									}else {
										erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
										erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
										erpCalendarToDoListDTO.setErpCalendarPersonalDTOList(erpCalendarPersonalDTOList);
									}
									erpCalendarPersonalDTOList.add(erpCalendarPersonalDTO);
									erpCalendarToDoListDTO.setErpCalendarPersonalDTOList(erpCalendarPersonalDTOList);
								}
							}
						});
					}else {
						erpCalendarPersonalDTOList.add(erpCalendarPersonalDTO);
						erpReminderNotificationsDTOList.add(erpReminderNotificationsDTO);
						erpCalendarPersonalDTO.setErpReminderNotificationsDTOList(erpReminderNotificationsDTOList);
						erpCalendarToDoListDTO.setErpCalendarPersonalDTOList(erpCalendarPersonalDTOList);
					}
					erpCalendarDTO.setTableName("TODO");
					erpCalendarDTO.setErpCalendarToDoListDTO(erpCalendarToDoListDTO);
				}
			}
		}
		return Mono.just(erpCalendarDTO);
	}

	public ErpReminderNotificationsDBO messageSentDBO(ErpReminderNotificationsDTO reminderTodo,ErpReminderNotificationsDBO erpReminderNotificationsDBO,String userId, Set<ErpNotificationsDBO> erpNotificationsDBOSet,Set<ErpSmsDBO> erpSmsDBOSet,Set<ErpEmailsDBO> erpEmailsDBOSet ) {
		if(!Utils.isNullOrEmpty(reminderTodo)) {
			Map<Integer,ErpNotificationsDBO> existErpNotificationsDBOMap = !Utils.isNullOrEmpty(erpReminderNotificationsDBO) && !Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpNotificationsDBOSet()) ? erpReminderNotificationsDBO.getErpNotificationsDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)): null;
			Map<Integer,ErpSmsDBO> existErpSmsDBOMap = !Utils.isNullOrEmpty(erpReminderNotificationsDBO) && !Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpSmsDBOSet()) ? erpReminderNotificationsDBO.getErpSmsDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)): null;
			Map<Integer,ErpEmailsDBO> existErpEmailsDBOMap = !Utils.isNullOrEmpty(erpReminderNotificationsDBO) && !Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpEmailsDBOSet()) ? erpReminderNotificationsDBO.getErpEmailsDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)): null;
			// reminder notification code
			if(!Utils.isNullOrEmpty(reminderTodo.isNotificationActivated())) {
				if(reminderTodo.isNotificationActivated()) {
					erpReminderNotificationsDBO.setNotificationActivated(true);
					ErpNotificationsDBO erpNotificationsDBO = null;
					if(!Utils.isNullOrEmpty(reminderTodo.getErpNotificationId())) {
						if(!Utils.isNullOrEmpty(existErpNotificationsDBOMap) && existErpNotificationsDBOMap.containsKey(reminderTodo.getErpNotificationId())) {
							erpNotificationsDBO = existErpNotificationsDBOMap.get(reminderTodo.getErpNotificationId());
							erpNotificationsDBO.setModifiedUsersId(Integer.parseInt(userId));
							existErpNotificationsDBOMap.remove(reminderTodo.getErpNotificationId());
						}else {
							erpNotificationsDBO = new ErpNotificationsDBO();
							erpNotificationsDBO.setCreatedUsersId(Integer.parseInt(userId));
							erpNotificationsDBO.setNotificationLogTime(LocalDateTime.now());
						}
					}else {
						erpNotificationsDBO = new ErpNotificationsDBO();
						erpNotificationsDBO.setCreatedUsersId(Integer.parseInt(userId));
						erpNotificationsDBO.setNotificationLogTime(LocalDateTime.now());
					}
					erpNotificationsDBO.setErpReminderNotificationsDBO(erpReminderNotificationsDBO);
					erpNotificationsDBO.setRecordStatus('A');
					if(!Utils.isNullOrEmpty(reminderTodo.getReminderDateTime())) {
						erpNotificationsDBO.setNotificationFromDateTime(reminderTodo.getReminderDateTime());
						erpNotificationsDBO.setNotificationToDateTime(reminderTodo.getReminderDateTime());
					}
					if(!Utils.isNullOrEmpty(userId)) {
						Integer userId1 = monthViewTransaction.getEmpId(userId);
						ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
						erpUsersDBO.setId(userId1);
						erpNotificationsDBO.setErpUsersDBO(erpUsersDBO);
						Integer campusId = monthViewTransaction.getEmpCampus(userId);
						ErpCampusDBO erpCampusDBO = new ErpCampusDBO();
						erpCampusDBO.setId(campusId);
						erpNotificationsDBO.setErpCampusDBO(erpCampusDBO);
						Integer departmentId = monthViewTransaction.getDepartment(userId);
						ErpDepartmentDBO erpDepartmentDBO = new ErpDepartmentDBO();
						erpDepartmentDBO.setId(departmentId);
						erpNotificationsDBO.setErpDepartmentDBO(erpDepartmentDBO);	
					}
					erpNotificationsDBOSet.add(erpNotificationsDBO);
				}
			}
			// reminder sms code
			if(!Utils.isNullOrEmpty(reminderTodo.isSmsActivated())) {
				if(reminderTodo.isSmsActivated()) {
					erpReminderNotificationsDBO.setSmsActivated(true);
					ErpTemplateDBO erpTemplateDBO = monthViewTransaction.getErpTemplateByTemplateCodeAndTemplateType("SMS","CALENDAR_TO_DO_REMINDER_SMS");	
					erpReminderNotificationsDBO.setErpTemplateDBOForSms(erpTemplateDBO);
					ErpSmsDBO erpSmsDBO = null;
					if(!Utils.isNullOrEmpty(reminderTodo.getErpSmsId())) {
						if(!Utils.isNullOrEmpty(existErpSmsDBOMap) && existErpSmsDBOMap.containsKey(reminderTodo.getErpSmsId())) {
							erpSmsDBO = existErpSmsDBOMap.get(reminderTodo.getErpSmsId());
							erpSmsDBO.setModifiedUsersId(Integer.parseInt(userId));
							existErpSmsDBOMap.remove(reminderTodo.getErpSmsId());
						}else {
							erpSmsDBO = new ErpSmsDBO();
							erpSmsDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
					}else {
						erpSmsDBO = new ErpSmsDBO();
						erpSmsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					erpSmsDBO.setErpReminderNotificationsDBO(erpReminderNotificationsDBO);
					erpSmsDBO.setRecordStatus('A');
					if(!Utils.isNullOrEmpty(userId)) {
						Integer userId1 = monthViewTransaction.getEmpId(userId);
						ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
						erpUsersDBO.setId(userId1);
						erpSmsDBO.setErpUsersDBO(erpUsersDBO);
					}
					String msgBody=erpTemplateDBO.getTemplateContent();
					String userName = monthViewTransaction.getEmpName(userId);
					msgBody = msgBody.replace("[Name]", userName);
					msgBody = msgBody.replace("[Comments]", erpReminderNotificationsDBO.getReminderComments());
					if(!Utils.isNullOrEmpty(erpTemplateDBO.getTemplateId()))
						erpSmsDBO.setTemplateId(erpTemplateDBO.getTemplateId());
					erpSmsDBO.recipientMobileNo = monthViewTransaction.getEmpMobile(userId);
					erpSmsDBO.smsContent=msgBody;
					erpSmsDBOSet.add(erpSmsDBO);
				}
			}
			//reminder email code

			if(!Utils.isNullOrEmpty(reminderTodo.isEmailActivated())) {
				// email template need to be added replace for template
				if(reminderTodo.isEmailActivated()) {
					erpReminderNotificationsDBO.setEmailActivated(true);
					ErpTemplateDBO erpTemplateDBO = monthViewTransaction.getErpTemplateByTemplateCodeAndTemplateType("EMail","CALENDAR_TO_DO_REMINDER_EMAIL");
					erpReminderNotificationsDBO.setErpTemplateDBOForEmail(erpTemplateDBO);
					ErpEmailsDBO erpEmailsDBO = null;
					if(!Utils.isNullOrEmpty(reminderTodo.getErpEmailId())) {
						if(!Utils.isNullOrEmpty(existErpEmailsDBOMap) && existErpEmailsDBOMap.containsKey(reminderTodo.getErpEmailId())) {
							erpEmailsDBO = existErpEmailsDBOMap.get(reminderTodo.getErpEmailId());
							erpEmailsDBO.setModifiedUsersId(Integer.parseInt(userId));
							existErpEmailsDBOMap.remove(reminderTodo.getErpEmailId());
						}else {
							erpEmailsDBO = new ErpEmailsDBO();
							erpEmailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
					}else {
						erpEmailsDBO = new ErpEmailsDBO();
						erpEmailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					}
					erpEmailsDBO.setErpReminderNotificationsDBO(erpReminderNotificationsDBO);
					erpEmailsDBO.setRecordStatus('A');
					if(!Utils.isNullOrEmpty(userId)) {
						Integer userId1 = monthViewTransaction.getEmpId(userId);
						ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
						erpUsersDBO.setId(userId1);
						erpEmailsDBO.setErpUsersDBO(erpUsersDBO);
					}
					String msgBody=erpTemplateDBO.getTemplateContent();
					String userName = monthViewTransaction.getEmpName(userId);
					msgBody = msgBody.replace("[Name]", userName);
					msgBody = msgBody.replace("[Comments]", erpReminderNotificationsDBO.getReminderComments());
					erpEmailsDBO.emailContent = msgBody;
					if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
						erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
					if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
						erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
					erpEmailsDBO.recipientEmail = monthViewTransaction.getEmpPersonalEmail(userId);
					erpEmailsDBOSet.add(erpEmailsDBO);
				}
			}
			if(!Utils.isNullOrEmpty(existErpNotificationsDBOMap)) {
				existErpNotificationsDBOMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpNotificationsDBOSet.add(value);
				});
			}
			if(!Utils.isNullOrEmpty(erpNotificationsDBOSet)) {
				erpReminderNotificationsDBO.setErpNotificationsDBOSet(erpNotificationsDBOSet);	
			}
			if(!Utils.isNullOrEmpty(existErpSmsDBOMap)) {
				existErpSmsDBOMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpSmsDBOSet.add(value);
				});
			}
			if(!Utils.isNullOrEmpty(erpSmsDBOSet)) {
				erpReminderNotificationsDBO.setErpSmsDBOSet(erpSmsDBOSet);
			}
			if(!Utils.isNullOrEmpty(existErpEmailsDBOMap)) {
				existErpEmailsDBOMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpEmailsDBOSet.add(value);
				});
			}
			if(!Utils.isNullOrEmpty(erpEmailsDBOSet)){
				erpReminderNotificationsDBO.setErpEmailsDBOSet(erpEmailsDBOSet);
			}
		}
		return erpReminderNotificationsDBO;	
	}

	public ErpReminderNotificationsDTO messageSentDto(ErpReminderNotificationsDBO erpReminderNotificationsDBO,ErpReminderNotificationsDTO erpReminderNotificationsDTO) {
		if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO)) {
			if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.isNotificationActivated())) {
				if(erpReminderNotificationsDBO.isNotificationActivated()) {
					erpReminderNotificationsDTO.setNotificationActivated(erpReminderNotificationsDBO.isNotificationActivated());
					if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpNotificationsDBOSet())) {
						erpReminderNotificationsDBO.getErpNotificationsDBOSet().forEach(personalNotification -> {
							if(!Utils.isNullOrEmpty(personalNotification)) {
								if(personalNotification.getRecordStatus() == 'A') {
									erpReminderNotificationsDTO.setErpNotificationId(personalNotification.getId());
								}
							}
						});
					}
				}
			}
			if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.isSmsActivated())) {
				if(erpReminderNotificationsDBO.isSmsActivated()) {
					erpReminderNotificationsDTO.setSmsActivated(erpReminderNotificationsDBO.isSmsActivated());	
					if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpSmsDBOSet())) {
						erpReminderNotificationsDBO.getErpSmsDBOSet().forEach(personalSms -> {
							if(!Utils.isNullOrEmpty(personalSms)) {
								if(personalSms.getRecordStatus() == 'A') {
									erpReminderNotificationsDTO.setErpSmsId(personalSms.getId());
								}
							}
						});
					}
				}
			}
			if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.isEmailActivated())) {
				if(erpReminderNotificationsDBO.isEmailActivated()){
					erpReminderNotificationsDTO.setEmailActivated(erpReminderNotificationsDBO.isEmailActivated());
					if(!Utils.isNullOrEmpty(erpReminderNotificationsDBO.getErpEmailsDBOSet())) {
						erpReminderNotificationsDBO.getErpEmailsDBOSet().forEach(personalemail -> {
							if(!Utils.isNullOrEmpty(personalemail)) {
								if(personalemail.getRecordStatus() == 'A') {
									erpReminderNotificationsDTO.setErpEmailId(personalemail.getId());
								}
							}
						});
					}
				}
			}
		}
		return erpReminderNotificationsDTO;
	}

	public Flux<ErpCalendarDTO> printDay(String fDate,String tDate) {
		List<ErpCalendarDBO> erpCalendarDBOList = monthViewTransaction.printDay(fDate,tDate);
		return convertDboToPrintdto(erpCalendarDBOList);
	}

	private Flux<ErpCalendarDTO> convertDboToPrintdto(List<ErpCalendarDBO> erpCalendarDBOList) {
		List<ErpCalendarDTO> erpCalendarDTOList = new ArrayList<ErpCalendarDTO>();
		if(!Utils.isNullOrEmpty(erpCalendarDBOList)) {
			erpCalendarDBOList.forEach(list -> {
				ErpCalendarDTO erpCalendarDTO = new ErpCalendarDTO();
				erpCalendarDTO.setId(list.getId());
				erpCalendarDTO.setActivitiesEvents(list.getActivitiesEvents());
				erpCalendarDTO.setFromDate(list.getFromDate());
				erpCalendarDTO.setToDate(list.getToDate());
				erpCalendarDTOList.add(erpCalendarDTO);
			});
		}
		return Flux.fromIterable(erpCalendarDTOList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> deleteReminder(int id, String userId) {
		return monthViewTransaction.deleteReminder(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}
}