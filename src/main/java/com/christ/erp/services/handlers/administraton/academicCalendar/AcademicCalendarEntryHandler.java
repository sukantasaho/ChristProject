package com.christ.erp.services.handlers.administraton.academicCalendar;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarCampusDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarCategoryDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarDatesDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarUserTypesDBO;
import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpCalendarUserTypesDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.administraton.academicCalendar.AcademicCalendarEntryTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AcademicCalendarEntryHandler {

	@Autowired
	AcademicCalendarEntryTransaction academicCalendarEntryTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	public Flux<SelectDTO> getUserType() {
		List<ErpCalendarUserTypesDBO> list = academicCalendarEntryTransaction.getUserType();
		return convertErpAcademicCalendarUserTypesDBOdto(list);		
	}

	private Flux<SelectDTO> convertErpAcademicCalendarUserTypesDBOdto(List<ErpCalendarUserTypesDBO> list) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(String.valueOf(data.getId()));
				selectDTO.setLabel(data.getUserType());
				selectDTOList.add(selectDTO);
			});
		}
		return Flux.fromIterable(selectDTOList);
	}

	public Flux<SelectDTO> getErpAcademicCalendarCategory() {
		List<ErpCalendarCategoryDBO> list = academicCalendarEntryTransaction.getErpCalendarCategory();
		return convertErpCalendarCategoryDBOtoDto(list);
	}

	private Flux<SelectDTO> convertErpCalendarCategoryDBOtoDto(List<ErpCalendarCategoryDBO> list) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(String.valueOf(data.getId()));
				selectDTO.setLabel(data.getCategoryName());
				selectDTOList.add(selectDTO);
			});
		}
		return Flux.fromIterable(selectDTOList);
	}

	public Flux<ErpCalendarDTO> edit(String academicYearId, String locId) {
		List<ErpCalendarDBO> erpCalendarDBOList = academicCalendarEntryTransaction.edit(academicYearId,locId);
		return convertErpCalendarDBOtoDto(erpCalendarDBOList);
	}

	private Flux<ErpCalendarDTO> convertErpCalendarDBOtoDto(List<ErpCalendarDBO> erpCalendarDBOList) {
		List<ErpCalendarDTO> ErpCalendarDTOList = new ArrayList<ErpCalendarDTO>();
		Map<String,String> orderMap = new HashMap<String, String>();
		AtomicInteger order = new AtomicInteger(0);
		if(!Utils.isNullOrEmpty(erpCalendarDBOList)) {
			erpCalendarDBOList.forEach(data -> {
				int year = data.getFromDate().getYear();
				Month dayOfMonth = data.getFromDate().getMonth();
				if(orderMap.containsKey(year+dayOfMonth.toString())) {
					ErpCalendarDTO ErpCalendarDTO = this.getErpCalendarDTO(data);
					ErpCalendarDTO.setDisplayOrder(order.get());
					ErpCalendarDTOList.add(ErpCalendarDTO);
					orderMap.put(year+dayOfMonth.toString(), year+dayOfMonth.toString());
				}else {
					ErpCalendarDTO ErpCalendarDTO = this.getErpCalendarDTO(data);
					int i = order.addAndGet(1);
					ErpCalendarDTO.setDisplayOrder(i);
					ErpCalendarDTOList.add(ErpCalendarDTO);
					orderMap.put(year+dayOfMonth.toString(), year+dayOfMonth.toString());
					order.set(i);
				}
			});
		}
		return Flux.fromIterable(ErpCalendarDTOList);
	}

	private ErpCalendarDTO getErpCalendarDTO(ErpCalendarDBO data) {
		Map<Integer, Integer> campusAcademicCalenderMap = new HashMap<Integer, Integer>();
		Map<Integer,Integer> userTypeAcademicCalenderMap = new HashMap<Integer, Integer>();
		ErpCalendarDTO erpCalendarDTO = new ErpCalendarDTO();
		erpCalendarDTO.setId(data.getId());
		erpCalendarDTO.setActivitiesEvents(data.getActivitiesEvents());
		erpCalendarDTO.setCategorySelect(new SelectDTO());
		erpCalendarDTO.getCategorySelect().setValue(String.valueOf(data.getErpCalendarCategoryDBO().getId()));
		erpCalendarDTO.getCategorySelect().setLabel(data.getErpCalendarCategoryDBO().getCategoryName());
		erpCalendarDTO.setFromDate(data.getFromDate());
		erpCalendarDTO.setToDate(data.getToDate());
		erpCalendarDTO.setLocationSelect(new SelectDTO());
		erpCalendarDTO.getLocationSelect().setValue(String.valueOf(data.getErpLocationDBO().getId()));
		erpCalendarDTO.getLocationSelect().setLabel(data.getErpLocationDBO().getLocationName());
		erpCalendarDTO.setAcademicYearSelect(new SelectDTO());
		erpCalendarDTO.getAcademicYearSelect().setValue(String.valueOf(data.getErpAcademicYearDBO().getId()));
		erpCalendarDTO.getAcademicYearSelect().setLabel(data.getErpAcademicYearDBO().getAcademicYearName());
		erpCalendarDTO.setRecordStatus(data.getRecordStatus());
		String month = data.getFromDate().getMonth().toString();
		String year = String.valueOf(data.getFromDate().getYear());
		erpCalendarDTO.setMonthAndYear(month+" "+ year);
		if(!Utils.isNullOrEmpty(data.getErpCalendarCampusDBOSet())) {
			List<SelectDTO> campusList = new ArrayList<SelectDTO>();
			campusAcademicCalenderMap.put(data.getId(), data.getId());
			if(campusAcademicCalenderMap.containsKey(data.getId())) {
				data.getErpCalendarCampusDBOSet().forEach(data1 -> {
					if(data1.getRecordStatus() == 'A') {
						if(data1.getErpCalendarDBO().getId() == data.getId()) {
							SelectDTO selectDTO = new SelectDTO();
							selectDTO.setValue(String.valueOf(data1.getErpCampusDBO().getId()));
							selectDTO.setLabel(data1.getErpCampusDBO().getCampusName());
							campusList.add(selectDTO);
							campusAcademicCalenderMap.put(data.getId(), data.getId());
							erpCalendarDTO.setExistCampusList(campusList);
						}else {
							SelectDTO selectDTO = new SelectDTO();
							selectDTO.setValue(String.valueOf(data1.getErpCampusDBO().getId()));
							selectDTO.setLabel(data1.getErpCampusDBO().getCampusName());
							campusList.add(selectDTO);
							campusAcademicCalenderMap.put(data.getId(), data.getId());
							erpCalendarDTO.setExistCampusList(campusList);
						}
					}
				});
			}
		}
		if(!Utils.isNullOrEmpty(data.getErpCalendarUserTypesDetailsDBOSet())) {
			List<SelectDTO> applicableList = new ArrayList<SelectDTO>();
			userTypeAcademicCalenderMap.put(data.getId(), data.getId());
			if(userTypeAcademicCalenderMap.containsKey(data.getId())) {
				data.getErpCalendarUserTypesDetailsDBOSet().forEach(data1 -> {
					if(data1.getRecordStatus() == 'A') {
						if(data1.getErpCalendarDBO().getId() == data.getId()) {
							SelectDTO selectDTO = new SelectDTO();
							selectDTO.setValue(String.valueOf(data1.getErpCalendarUserTypesDBO().getId()));
							selectDTO.setLabel(data1.getErpCalendarUserTypesDBO().getUserType());
							applicableList.add(selectDTO);
							campusAcademicCalenderMap.put(data.getId(), data.getId());
							erpCalendarDTO.setExistUserTypeList(applicableList);
						}else {
							SelectDTO selectDTO = new SelectDTO();
							selectDTO.setValue(String.valueOf(data1.getId()));
							selectDTO.setLabel(data1.getErpCalendarUserTypesDBO().getUserType());
							applicableList.add(selectDTO);
							campusAcademicCalenderMap.put(data.getId(), data.getId());
							erpCalendarDTO.setExistUserTypeList(applicableList);
						}
					}
				});
			}
		}
		return erpCalendarDTO;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Mono<ApiResult> saveOrUpdate(Mono<List<ErpCalendarDTO>> dto,String academicYearId,String locId,String userId) {
		return dto
				.handle((erpCalendarDTO,synchronousSink) -> {
					List<String> activitynameList1 = erpCalendarDTO.stream().distinct().map(p -> p.getActivitiesEvents().replaceAll("\\s+"," ").trim()).collect(Collectors.toList());
					List<String> activitynameList = activitynameList1.stream().distinct().collect(Collectors.toList());
					List<String> errorList = new ArrayList<String>();
					List<String> errorDate = new ArrayList<String>();
					List<ErpCalendarDBO> erpCalendarDBOList = academicCalendarEntryTransaction.duplicateCheck(academicYearId,locId,activitynameList);
					Map<String,List<ErpCalendarDBO>> existMap = !Utils.isNullOrEmpty(erpCalendarDBOList) ? erpCalendarDBOList.stream().collect(Collectors.groupingBy(s -> s.getActivitiesEvents().trim(), Collectors.toList())) : new HashMap<String, List<ErpCalendarDBO>>();
					if(!Utils.isNullOrEmpty(existMap)) {
						if(!Utils.isNullOrEmpty(erpCalendarDTO)) {
							erpCalendarDTO.forEach(dto1 ->  {
								List<ErpCalendarDBO> list = existMap.get(dto1.getActivitiesEvents().trim());
								if(!Utils.isNullOrEmpty(list)) {
									list.forEach(dbo1 -> {
										List<Integer> existDboCamStr = dbo1.getErpCalendarCampusDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').map(s -> s.getErpCampusDBO().getId()).collect(Collectors.toList());
										List<Integer> existDtoCamStr = dto1.getExistCampusList().stream().map(s -> Integer.parseInt(s.getValue())).collect(Collectors.toList());
										if((dbo1.getActivitiesEvents().equalsIgnoreCase(dto1.getActivitiesEvents().trim())) && dbo1.getFromDate().isEqual(dto1.getFromDate()) && dbo1.getToDate().isEqual(dto1.getToDate()) && Utils.isNullOrEmpty(dto1.getId())) {
											existDtoCamStr.forEach(dtoCam -> {
												if(existDboCamStr.contains(dtoCam)) {
													if(!errorList.contains(dto1.getActivitiesEvents().trim())) {
														errorList.add(dto1.getActivitiesEvents().trim());	
													}
												}
											});
										}
										List<LocalDate> dateList = dbo1.getErpCalendarDatesDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').map(s -> s.getDate()).collect(Collectors.toList());
										if(!Utils.isNullOrEmpty(dateList)) {
											if((dbo1.getActivitiesEvents().equalsIgnoreCase(dto1.getActivitiesEvents().trim())) && ((dateList.contains(dto1.getFromDate()) || dateList.contains(dto1.getToDate())) || ((dto1.getFromDate().isBefore(dbo1.getFromDate()) || dbo1.getToDate().isAfter(dto1.getToDate())))) && Utils.isNullOrEmpty(dto1.getId())) {
												existDtoCamStr.forEach(dtoCam -> {
													if(existDboCamStr.contains(dtoCam))  {
														if(!errorDate.contains(dto1.getActivitiesEvents().trim())) {
															errorDate.add(dto1.getActivitiesEvents().trim());
														}
													}
												});
											}
										}
									});
								}
							});
						}
					}
					if(!Utils.isNullOrEmpty(errorList)) {
						synchronousSink.error(new GeneralException(" Activity already present for "+ errorList));
					}else if(!Utils.isNullOrEmpty(errorDate)) {
						synchronousSink.error(new GeneralException(errorDate + " are alredy present between these dates"));
					}else {
						synchronousSink.next(erpCalendarDTO);	
					}
				}).map(data -> convertErpCalendarDtoDbo((List<ErpCalendarDTO>) data,academicYearId,locId,userId))
				.flatMap( s ->{ 
					academicCalendarEntryTransaction.update(s);	
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult); 
	}

	private List<ErpCalendarDBO> convertErpCalendarDtoDbo(List<ErpCalendarDTO> dto,String academicYearId,String locId, String userId) {
		List<ErpCalendarDBO> erpCalendarDBONewList = new ArrayList<ErpCalendarDBO>();
		List<ErpCalendarDBO> erpCalendarDBOExistList = academicCalendarEntryTransaction.edit(academicYearId,locId);
		List<String> existActivityName = !Utils.isNullOrEmpty(erpCalendarDBOExistList) ? erpCalendarDBOExistList.stream().map(p -> p.getActivitiesEvents()).collect(Collectors.toList()) : new ArrayList<String>();
		List<Integer> dtoList = !Utils.isNullOrEmpty(dto) ? dto.stream().filter(p -> !Utils.isNullOrEmpty(p.getId())).map(s -> s.getId()).collect(Collectors.toList()) :new ArrayList<Integer>();
		Map<Integer,ErpCalendarDBO> existErpAcademicCalenMap = !Utils.isNullOrEmpty(erpCalendarDBOExistList) ? erpCalendarDBOExistList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer, ErpCalendarDBO>();
		List<Integer> listId = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(dto)) {
			dto.forEach(data -> {
				if(!Utils.isNullOrEmpty(erpCalendarDBOExistList)) {
					erpCalendarDBOExistList.forEach(existDbo -> {
						if(existDbo.getActivitiesEvents().equalsIgnoreCase(data.getActivitiesEvents().trim()) && existDbo.getFromDate().isEqual(data.getFromDate()) && existDbo.getToDate().isEqual(data.getToDate()) && (Utils.isNullOrEmpty(data.getId()))) {
							List<Integer> campusExistDboIn = !Utils.isNullOrEmpty(existDbo.getErpCalendarCampusDBOSet()) ? existDbo.getErpCalendarCampusDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').map(s -> s.getErpCampusDBO().getId()).collect(Collectors.toList()): new ArrayList<Integer>();
							List<Integer> campusExistDtoIn = data.getExistCampusList().stream().map(s-> Integer.parseInt(s.getValue())).collect(Collectors.toList());
							Set<ErpCalendarCampusDBO> erpCalendarCampusDBOSet = new HashSet<ErpCalendarCampusDBO>();
							campusExistDtoIn.forEach((k) -> {
								if(!campusExistDboIn.contains(k)) {
									ErpCalendarCampusDBO erpCalendarCampusDBO = new ErpCalendarCampusDBO();
									erpCalendarCampusDBO.setErpCampusDBO(new ErpCampusDBO());
									erpCalendarCampusDBO.getErpCampusDBO().setId(k);
									erpCalendarCampusDBO.setErpCalendarDBO(existDbo);
									erpCalendarCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
									erpCalendarCampusDBO.setRecordStatus('A');
									erpCalendarCampusDBOSet.add(erpCalendarCampusDBO);
								}
							});
							if(!Utils.isNullOrEmpty(erpCalendarCampusDBOSet)) {
								existDbo.setModifiedUsersId(Integer.parseInt(userId));
								existDbo.setErpCalendarCampusDBOSet(erpCalendarCampusDBOSet);
								erpCalendarDBONewList.add(existDbo);
							}
						}else if(!Utils.isNullOrEmpty(data.getId()) && !listId.contains(data.getId()) && existActivityName.contains(data.getActivitiesEvents().trim())) {
							ErpCalendarDBO erpCalendarDBO    = this.convertdtoToDbo1(data,academicYearId,locId,userId,existErpAcademicCalenMap);
							erpCalendarDBONewList.add(erpCalendarDBO);
							listId.add(data.getId());
							existActivityName.add(data.getActivitiesEvents().trim());
							existErpAcademicCalenMap.remove(data.getId());
						}else if(!existActivityName.contains(data.getActivitiesEvents().trim()) && existDbo.getActivitiesEvents().equalsIgnoreCase(data.getActivitiesEvents().trim()) && existDbo.getFromDate() != data.getFromDate() && existDbo.getToDate().isEqual(data.getToDate()) && (Utils.isNullOrEmpty(data.getId()))) {
							ErpCalendarDBO erpCalendarDBO    = this.convertdtoToDbo1(data,academicYearId,locId,userId,existErpAcademicCalenMap);
							erpCalendarDBONewList.add(erpCalendarDBO);
							existActivityName.add(data.getActivitiesEvents().trim());
						}else if(!existActivityName.contains(data.getActivitiesEvents().trim()) && existDbo.getActivitiesEvents().equalsIgnoreCase(data.getActivitiesEvents().trim()) && existDbo.getFromDate().isEqual(data.getFromDate()) && existDbo.getToDate() != data.getToDate()  && (Utils.isNullOrEmpty(data.getId()))) {
							ErpCalendarDBO erpCalendarDBO    = this.convertdtoToDbo1(data,academicYearId,locId,userId,existErpAcademicCalenMap);
							erpCalendarDBONewList.add(erpCalendarDBO);
							existActivityName.add(data.getActivitiesEvents().trim());
						}else if(existDbo.getActivitiesEvents().equalsIgnoreCase(data.getActivitiesEvents().trim()) && existDbo.getFromDate() != data.getFromDate() && existDbo.getToDate() != data.getToDate() && existActivityName.contains(data.getActivitiesEvents().trim())  && (Utils.isNullOrEmpty(data.getId()))) {
							ErpCalendarDBO erpCalendarDBO    = this.convertdtoToDbo1(data,academicYearId,locId,userId,existErpAcademicCalenMap);
							erpCalendarDBONewList.add(erpCalendarDBO);
							existActivityName.add(data.getActivitiesEvents().trim());
						}else if(!existActivityName.contains(data.getActivitiesEvents().trim())) {
							ErpCalendarDBO erpCalendarDBO    = this.convertdtoToDbo1(data,academicYearId,locId,userId,existErpAcademicCalenMap);
							erpCalendarDBONewList.add(erpCalendarDBO);
							existActivityName.add(data.getActivitiesEvents().trim());
						}
					});
				}else {
					ErpCalendarDBO erpCalendarDBO    = this.convertdtoToDbo1(data,academicYearId,locId,userId,existErpAcademicCalenMap);
					erpCalendarDBONewList.add(erpCalendarDBO);
					existActivityName.add(erpCalendarDBO.getActivitiesEvents().trim());
				}
			});
		}
		if(!Utils.isNullOrEmpty(existErpAcademicCalenMap)) {
			if(!Utils.isNullOrEmpty(dtoList)) {
				dtoList.forEach(id -> {
					if(existErpAcademicCalenMap.containsKey(id)) {
						existErpAcademicCalenMap.remove(id);
					}
				});
			}
		}
		if(!Utils.isNullOrEmpty(dtoList)){
			if(!Utils.isNullOrEmpty(existErpAcademicCalenMap)) {
				existErpAcademicCalenMap.forEach((key,value) -> {
					value.setModifiedUsersId(Integer.parseInt(userId));
					value.setRecordStatus('D');
					if(!Utils.isNullOrEmpty(value.getErpCalendarCampusDBOSet())) {
						value.getErpCalendarCampusDBOSet().forEach(cam -> {
							cam.setModifiedUsersId(Integer.parseInt(userId));
							cam.setRecordStatus('D');
						});
					}
					if(!Utils.isNullOrEmpty(value.getErpCalendarDatesDBOSet())) {
						value.getErpCalendarDatesDBOSet().forEach(date -> {
							date.setModifiedUsersId(Integer.parseInt(userId));
							date.setRecordStatus('D');
						});
					}
					if(!Utils.isNullOrEmpty(value.getErpCalendarUserTypesDetailsDBOSet())) {
						value.getErpCalendarUserTypesDetailsDBOSet().forEach(type -> {
							type.setModifiedUsersId(Integer.parseInt(userId));
							type.setRecordStatus('D');
						});
					}
					erpCalendarDBONewList.add(value);
				});
			}
		}
		return erpCalendarDBONewList;
	}

	private ErpCalendarDBO convertdtoToDbo1(ErpCalendarDTO data, String academicYearId, String locId, String userId,Map<Integer,ErpCalendarDBO> existErpAcademicCalenMap) {
		ErpCalendarDBO erpCalendarDBO = !Utils.isNullOrEmpty(data.getId()) && existErpAcademicCalenMap.containsKey(data.getId()) ? existErpAcademicCalenMap.get(data.getId()) : new ErpCalendarDBO();
		Map<Integer,ErpCalendarCampusDBO> existErpAcademicCalendarCampusMap = !Utils.isNullOrEmpty(erpCalendarDBO) && !Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarCampusDBOSet()) ? erpCalendarDBO.getErpCalendarCampusDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getErpCampusDBO().getId(), s -> s)) : null;
		Map<Integer,ErpCalendarUserTypesDetailsDBO> existErpAcademicCalendarUserTypesDetailsMap = !Utils.isNullOrEmpty(erpCalendarDBO) && !Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarUserTypesDetailsDBOSet()) ? erpCalendarDBO.getErpCalendarUserTypesDetailsDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getErpCalendarUserTypesDBO().getId(), s -> s)) : null;
		Map<LocalDate,ErpCalendarDatesDBO> existErpAcademicCalendarDatesMap = !Utils.isNullOrEmpty(erpCalendarDBO) && !Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarDatesDBOSet()) ? erpCalendarDBO.getErpCalendarDatesDBOSet().stream().filter(p -> p.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getDate(), s -> s)) : null;
		Set<ErpCalendarCampusDBO> erpAcademicCalendarCampusDBOSet = new HashSet<ErpCalendarCampusDBO>();
		Set<ErpCalendarUserTypesDetailsDBO> erpAcademicCalendarUserTypesDetailsDBOSet = new HashSet<ErpCalendarUserTypesDetailsDBO>();
		Set<ErpCalendarDatesDBO> erpAcademicCalendarDatesDBOSet = new HashSet<ErpCalendarDatesDBO>();
		if(!Utils.isNullOrEmpty(data.getId())) {
			erpCalendarDBO.setModifiedUsersId(Integer.parseInt(userId));
		}else {
			erpCalendarDBO.setId(data.getId());
			erpCalendarDBO.setCreatedUsersId(Integer.parseInt(userId));
		}
		erpCalendarDBO.setActivitiesEvents(data.getActivitiesEvents().trim());
		erpCalendarDBO.setCreatedUsersId(Integer.parseInt(userId));
		erpCalendarDBO.setErpAcademicYearDBO(new ErpAcademicYearDBO());
		erpCalendarDBO.getErpAcademicYearDBO().setId(Integer.parseInt(academicYearId));
		erpCalendarDBO.setErpLocationDBO(new ErpLocationDBO());
		erpCalendarDBO.getErpLocationDBO().setId(Integer.parseInt(locId));
		erpCalendarDBO.setErpCalendarCategoryDBO(new ErpCalendarCategoryDBO());
		erpCalendarDBO.getErpCalendarCategoryDBO().setId(Integer.parseInt(data.getCategorySelect().getValue()));
		erpCalendarDBO.setFromDate(data.getFromDate());
		erpCalendarDBO.setToDate(data.getToDate());
		erpCalendarDBO.setRecordStatus('A');
		if(!Utils.isNullOrEmpty(data.getExistCampusList())) {
			data.getExistCampusList().forEach(data3 -> {
				ErpCalendarCampusDBO erpAcademicCalendarCampusDBO = null;
				if(!Utils.isNullOrEmpty(existErpAcademicCalendarCampusMap)  && existErpAcademicCalendarCampusMap.containsKey(Integer.parseInt(data3.getValue()))) {
					erpAcademicCalendarCampusDBO = existErpAcademicCalendarCampusMap.get(Integer.parseInt(data3.getValue()));
					if(erpAcademicCalendarCampusDBO.getErpCalendarDBO().getId() == data.getId()) {
						erpAcademicCalendarCampusDBO.setModifiedUsersId(Integer.parseInt(userId));
						erpAcademicCalendarCampusDBOSet.add(erpAcademicCalendarCampusDBO);
						existErpAcademicCalendarCampusMap.remove(Integer.parseInt(data3.getValue()));
					}
				}else {
					erpAcademicCalendarCampusDBO = new ErpCalendarCampusDBO();
					erpAcademicCalendarCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				erpAcademicCalendarCampusDBO.setRecordStatus('A');
				erpAcademicCalendarCampusDBO.setErpCalendarDBO(erpCalendarDBO);
				erpAcademicCalendarCampusDBO.setErpCampusDBO(new ErpCampusDBO());
				erpAcademicCalendarCampusDBO.getErpCampusDBO().setId(Integer.parseInt(data3.getValue()));
				erpAcademicCalendarCampusDBOSet.add(erpAcademicCalendarCampusDBO);
			});
		}
		if(!Utils.isNullOrEmpty(data.getExistUserTypeList())) {
			data.getExistUserTypeList().forEach(data5 -> {
				ErpCalendarUserTypesDetailsDBO erpAcademicCalendarUserTypesDetailsDBO = null;
				if(!Utils.isNullOrEmpty(existErpAcademicCalendarUserTypesDetailsMap) && existErpAcademicCalendarUserTypesDetailsMap.containsKey(Integer.parseInt(data5.getValue()))) {
					erpAcademicCalendarUserTypesDetailsDBO = existErpAcademicCalendarUserTypesDetailsMap.get(Integer.parseInt(data5.getValue()));
					erpAcademicCalendarUserTypesDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
					existErpAcademicCalendarUserTypesDetailsMap.remove(Integer.parseInt(data5.getValue()));
				}else {
					erpAcademicCalendarUserTypesDetailsDBO = new ErpCalendarUserTypesDetailsDBO();
					erpAcademicCalendarUserTypesDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					erpAcademicCalendarUserTypesDetailsDBO.setRecordStatus('A');
				}
				erpAcademicCalendarUserTypesDetailsDBO.setErpCalendarDBO(erpCalendarDBO);
				erpAcademicCalendarUserTypesDetailsDBO.setErpCalendarUserTypesDBO(new ErpCalendarUserTypesDBO());
				erpAcademicCalendarUserTypesDetailsDBO.getErpCalendarUserTypesDBO().setId(Integer.parseInt(data5.getValue()));
				erpAcademicCalendarUserTypesDetailsDBOSet.add(erpAcademicCalendarUserTypesDetailsDBO);
			});
		}
		List<LocalDate> datesList = null;
		if(data.getFromDate() == data.getToDate()) {
			datesList = !Utils.isNullOrEmpty(erpCalendarDBO) && !Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarDatesDBOSet()) ? erpCalendarDBO.getErpCalendarDatesDBOSet().stream().map(s -> s.getDate()).collect(Collectors.toList()) : new ArrayList<LocalDate>();
			datesList.add(data.getFromDate());
		}else {
			datesList = academicCalendarEntryTransaction.getDatesBetweenStartDateEndDate(data.getFromDate(), data.getToDate());
			datesList.add(data.getToDate());
		}
		if(!Utils.isNullOrEmpty(datesList)) {
			datesList.forEach(date -> {
				ErpCalendarDatesDBO erpAcademicCalendarDatesDBO = null;
				if(!Utils.isNullOrEmpty(existErpAcademicCalendarDatesMap) && existErpAcademicCalendarDatesMap.containsKey(date)) {
					erpAcademicCalendarDatesDBO = existErpAcademicCalendarDatesMap.get(date);
					erpAcademicCalendarDatesDBO.setModifiedUsersId(Integer.parseInt(userId));
					existErpAcademicCalendarDatesMap.remove(date);
				}else {
					erpAcademicCalendarDatesDBO = new ErpCalendarDatesDBO();
					erpAcademicCalendarDatesDBO.setCreatedUsersId(Integer.parseInt(userId));
					erpAcademicCalendarDatesDBO.setRecordStatus('A');
				}
				erpAcademicCalendarDatesDBO.setErpCalendarDBO(erpCalendarDBO);
				erpAcademicCalendarDatesDBO.setDate(date);
				erpAcademicCalendarDatesDBOSet.add(erpAcademicCalendarDatesDBO);
			});
		}				
		if(!Utils.isNullOrEmpty(existErpAcademicCalendarCampusMap)) {
			existErpAcademicCalendarCampusMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				erpAcademicCalendarCampusDBOSet.add(value);
			});
		}
		if(!Utils.isNullOrEmpty(existErpAcademicCalendarUserTypesDetailsMap)) {
			existErpAcademicCalendarUserTypesDetailsMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				erpAcademicCalendarUserTypesDetailsDBOSet.add(value);
			});
		}
		if(!Utils.isNullOrEmpty(existErpAcademicCalendarDatesMap)) {
			existErpAcademicCalendarDatesMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				erpAcademicCalendarDatesDBOSet.add(value);
			});
		}
		erpCalendarDBO.setErpCalendarCampusDBOSet(erpAcademicCalendarCampusDBOSet);
		erpCalendarDBO.setErpCalendarUserTypesDetailsDBOSet(erpAcademicCalendarUserTypesDetailsDBOSet);
		erpCalendarDBO.setErpCalendarDatesDBOSet(erpAcademicCalendarDatesDBOSet);
		return erpCalendarDBO;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> importFromPreviousYear(Mono<ErpCalendarDTO> dto, String userId) {
		return dto
				.handle((ErpCalendarDTO, synchronousSink) ->  {
					synchronousSink.next(ErpCalendarDTO);
				}).cast(ErpCalendarDTO.class)
				.map(data -> convertDtoPreDbo(data, userId))
				.flatMap( s -> {
					academicCalendarEntryTransaction.update(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<ErpCalendarDBO> convertDtoPreDbo(ErpCalendarDTO dto, String userId) {
		List<ErpCalendarDBO> erpCalendarDBOList = new ArrayList<ErpCalendarDBO>();
		List<ErpCalendarDBO> existErpCalendarDBOList = academicCalendarEntryTransaction.edit(dto.getImportFromYear().getValue(), dto.getLocationSelect().getValue());
		long days = ChronoUnit.DAYS.between(dto.getImportFromDate(),dto.getImportToDate());
		if(!Utils.isNullOrEmpty(existErpCalendarDBOList)) {
			existErpCalendarDBOList.forEach(data -> {
				ErpCalendarDBO erpCalendarDBO = new ErpCalendarDBO();
				if(!Utils.isNullOrEmpty(data)) {
					BeanUtils.copyProperties(data, erpCalendarDBO,"id");
					erpCalendarDBO.getErpAcademicYearDBO().setId(Integer.parseInt(dto.getImportToYear().getValue()));
					erpCalendarDBO.getErpLocationDBO().setId(Integer.parseInt(dto.getLocationSelect().getValue()));
					erpCalendarDBO.setCreatedUsersId(Integer.parseInt(userId));
					erpCalendarDBO.setModifiedUsersId(null);
					erpCalendarDBO.setRecordStatus('A');
					LocalDate localFromdate = data.getFromDate().plusDays(days);	
					erpCalendarDBO.setFromDate(localFromdate);
					LocalDate localTodate = data.getToDate().plusDays(days);	
					erpCalendarDBO.setToDate(localTodate);
					Set<ErpCalendarCampusDBO> erpCalendarCampusDBOSet = new HashSet<ErpCalendarCampusDBO>();
					if(!Utils.isNullOrEmpty(erpCalendarDBO.getErpCalendarCampusDBOSet())) {
						erpCalendarDBO.getErpCalendarCampusDBOSet().forEach(data1 -> {
							if(data1.getRecordStatus() == 'A'){
								ErpCalendarCampusDBO erpCalendarCampusDBO = new ErpCalendarCampusDBO();
								BeanUtils.copyProperties(data1, erpCalendarCampusDBO,"id","erpAcademicCalendarDBO");
								erpCalendarCampusDBO.setErpCalendarDBO(erpCalendarDBO);
								erpCalendarCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
								erpCalendarCampusDBO.setModifiedUsersId(null);
								erpCalendarCampusDBO.setRecordStatus('A');
								erpCalendarCampusDBOSet.add(erpCalendarCampusDBO);
							}
						});
					}
					if(!Utils.isNullOrEmpty(erpCalendarCampusDBOSet)) {
						erpCalendarDBO.setErpCalendarCampusDBOSet(erpCalendarCampusDBOSet);
					}
					Set<ErpCalendarUserTypesDetailsDBO> erpCalendarUserTypesDetailsDBOSet = new HashSet<ErpCalendarUserTypesDetailsDBO>();
					if(!Utils.isNullOrEmpty(data.getErpCalendarUserTypesDetailsDBOSet())) {
						data.getErpCalendarUserTypesDetailsDBOSet().forEach(data2 -> {
							if(data2.getRecordStatus() == 'A') {
								ErpCalendarUserTypesDetailsDBO erpCalendarUserTypesDetailsDBO = new ErpCalendarUserTypesDetailsDBO();
								BeanUtils.copyProperties(data2, erpCalendarUserTypesDetailsDBO,"id","erpAcademicCalendarDBO");
								erpCalendarUserTypesDetailsDBO.setErpCalendarDBO(erpCalendarDBO);
								erpCalendarUserTypesDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
								erpCalendarUserTypesDetailsDBO.setModifiedUsersId(null);
								erpCalendarUserTypesDetailsDBO.setRecordStatus('A');
								erpCalendarUserTypesDetailsDBOSet.add(erpCalendarUserTypesDetailsDBO);
							}
						});
					}
					if(!Utils.isNullOrEmpty(erpCalendarUserTypesDetailsDBOSet)) {
						erpCalendarDBO.setErpCalendarUserTypesDetailsDBOSet(erpCalendarUserTypesDetailsDBOSet);
					}
					Set<ErpCalendarDatesDBO> erpCalendarDatesDBOSet = new HashSet<ErpCalendarDatesDBO>();
					List<LocalDate> datesList = new ArrayList<LocalDate>();
					if(erpCalendarDBO.getFromDate() == erpCalendarDBO.getToDate()) {
						datesList.add(erpCalendarDBO.getFromDate());
					}else {
						datesList = erpCalendarDBO.getFromDate().datesUntil(erpCalendarDBO.getToDate()).collect(Collectors.toList());
						datesList.add(erpCalendarDBO.getToDate());
					}
					if(!Utils.isNullOrEmpty(datesList)) {
						datesList.forEach(date3 -> {
							ErpCalendarDatesDBO erpCalendarDatesDBO = new ErpCalendarDatesDBO();
							erpCalendarDatesDBO.setCreatedUsersId(Integer.parseInt(userId));
							erpCalendarDatesDBO.setRecordStatus('A');
							erpCalendarDatesDBO.setErpCalendarDBO(erpCalendarDBO);
							erpCalendarDatesDBO.setDate(date3);
							erpCalendarDatesDBOSet.add(erpCalendarDatesDBO);
						});
					}
					if(!Utils.isNullOrEmpty(erpCalendarDatesDBOSet)) {
						erpCalendarDBO.setErpCalendarDatesDBOSet(erpCalendarDatesDBOSet);
					}
				}
				erpCalendarDBOList.add(erpCalendarDBO);
			});
		}
		return erpCalendarDBOList;
	}

	@SuppressWarnings({ "rawtypes" })
	public Mono<ApiResult> academiccalenderUpload(String academicId,Mono<EmpApplnAdvertisementImagesDTO> data, String userId) {
		List<ErpCampusDBO> campusList = academicCalendarEntryTransaction.getCampusList();
		List<ErpLocationDBO> locationList = academicCalendarEntryTransaction.getLocationList();
		Map<String,Integer> locationMap = !Utils.isNullOrEmpty(locationList) ? locationList.stream().collect(Collectors.toMap(s -> s.getLocationName(), s -> s.getId())): new HashMap<String,Integer>();
		Map<String,List<ErpCampusDBO>> locCamusMap = !Utils.isNullOrEmpty(campusList) ? campusList.stream().collect(Collectors.groupingBy(s -> s.getErpLocationDBO().getLocationName())): new HashMap<String,List<ErpCampusDBO>>();
		Map<String,Integer> campusMap = !Utils.isNullOrEmpty(campusList) ? campusList.stream().collect(Collectors.toMap(s -> s.getCampusName(), s -> s.getId())) : new HashMap<String,Integer>();
		List<ErpCalendarCategoryDBO>  categoryList = academicCalendarEntryTransaction.getErpCalendarCategory();
		Map<String,Integer> categoryMap = !Utils.isNullOrEmpty(categoryList) ? categoryList.stream().collect(Collectors.toMap(s -> s.getCategoryName(), s -> s.getId())) : new HashMap<String, Integer>();
		List<ErpCalendarUserTypesDBO> applicableForList = academicCalendarEntryTransaction.getUserType();
		Map<String,Integer> applicableForMap = !Utils.isNullOrEmpty(applicableForList) ? applicableForList.stream().collect(Collectors.toMap(s -> s.getUserType(), s -> s.getId())) : new HashMap<String, Integer>();
		Map<Integer,List<String>> map1 = new HashMap<Integer, List<String>>();
		List<ErpCalendarDBO> existErpCalendarDBOList = academicCalendarEntryTransaction.getExistDetails(Integer.parseInt(academicId));
		Map<Integer,List<ErpCalendarDBO>> existLocAcademicMap = !Utils.isNullOrEmpty(existErpCalendarDBOList) ? existErpCalendarDBOList.stream().collect(Collectors.groupingBy(s -> s.getErpLocationDBO().getId())): new HashMap<Integer,List<ErpCalendarDBO>>();
		return data
				.handle((data1,synchronousSink) -> {
					XSSFWorkbook workbook = null;
					try {
						workbook = new XSSFWorkbook("ExcelUpload1//"+data1.fileName+"."+data1.extension);
					}catch (Exception e) {
						e.printStackTrace();
					}
					XSSFSheet sheet = workbook.getSheetAt(0);
					if(!Utils.isNullOrEmpty(sheet.getRow(0))) {
						int rowLength = sheet.getRow(0).getLastCellNum();
						Integer p = 1;
						for(Row row : sheet) {
							if(p != 1) {
								map1.put(p, new ArrayList<String>());
								for(int cn = 0; cn<rowLength; cn++) {
									Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
									if(cell == null) {
										String cellValue = null;
										map1.get(p).add(cellValue);
									} else if (cell.getCellType() == CellType.NUMERIC) {
										if (DateUtil.isCellDateFormatted(cell)) {
											String[] s = cell.getLocalDateTimeCellValue().toLocalTime().toString().split(":"); 
											if(Integer.parseInt(s[0]) != 00) {
												map1.get(p).add(cell.getLocalDateTimeCellValue().toLocalTime() + "");
											} else {
												map1.get(p).add(cell.getLocalDateTimeCellValue() + "");
											}
										} else {
											map1.get(p).add(String.valueOf((int)cell.getNumericCellValue()));
										}
									} else {
										String cellValue =  cell.toString() ; 
										map1.get(p).add(cellValue);
									}
								}
							}
							p++;
						}
					}
					List<String> campusInvalid = new ArrayList<String>();
					List<LocalDate> wrongDate = new ArrayList<LocalDate>();
					DateTimeFormatter dataeformat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
					List<String> duplicateCheck1 = new ArrayList<String>();
					List<String> duplicateCheck2 = new ArrayList<String>();
					List<String> duplicateCheck = new ArrayList<String>();
					Map<String,List<String>> duplicateCheckMap = new HashMap<String,List<String>>();
					map1.forEach((k,v) -> {
						if(!Utils.isNullOrEmpty(v.get(0))) {
							if(duplicateCheckMap.containsKey(v.get(0))) {
								List<String> strList = duplicateCheckMap.get(v.get(0));
								if(!Utils.isNullOrEmpty(strList)) {
									if(strList.contains(v.get(2) +" for "+v.get(0))) {
										if(!duplicateCheck.contains(v.get(2) +" for "+v.get(0))) {
											duplicateCheck.add(v.get(2) +" for "+v.get(0));
										}
									}else {
										duplicateCheckMap.get(v.get(0)).add(v.get(2) +" for "+v.get(0));
									}
								}
							}else {
								List<String> strl = new ArrayList<String>();
								strl.add(v.get(2) +" for "+v.get(0));
								duplicateCheckMap.put(v.get(0),strl);
							}
						}
						if(!Utils.isNullOrEmpty(v.get(1))) {
							String [] items = v.get(1).split("\\s*,\\s*");
							List<String> container = Arrays.asList(items);				
							if(!Utils.isNullOrEmpty(v.get(0))) {
								if(locCamusMap.containsKey(v.get(0))) {
									List<ErpCampusDBO> camList = locCamusMap.get(v.get(0));
									if(!Utils.isNullOrEmpty(v.get(1))) {
										List<String> campusnameList = !Utils.isNullOrEmpty(camList) ? camList.stream().map(p -> p.getCampusName()).collect(Collectors.toList()) : new ArrayList<String>();
										if(!Utils.isNullOrEmpty(container)) {
											container.forEach(campusName -> {
												if(!campusnameList.contains(campusName)) {
													campusInvalid.add(v.get(0));
												}	
											});
										}
									}
									Integer locId = locationMap.get(v.get(0));
									if(existLocAcademicMap.containsKey(locId)) {
										List<ErpCalendarDBO> existList = existLocAcademicMap.get(locId);	
										if(!Utils.isNullOrEmpty(existList)) {
											existList.forEach(dbo -> {
												List<String> existCamList = dbo.getErpCalendarCampusDBOSet().stream().filter(s -> s.getErpCampusDBO().getRecordStatus() == 'A').map(p -> p.getErpCampusDBO().getCampusName()).collect(Collectors.toList());
												if((dbo.getErpLocationDBO().getId() == locId) && (dbo.getActivitiesEvents().equals(v.get(2))) && (dbo.getFromDate().equals(LocalDate.parse(v.get(3).toString(),dataeformat))) && (dbo.getToDate().equals(LocalDate.parse(v.get(4).toString(),dataeformat)))) {
													if(existCamList.containsAll(container)) {
														duplicateCheck1.add(v.get(2));
													}
												}
											});
										}
									}
								}
							}
						}
						if(!Utils.isNullOrEmpty(v.get(3)) && !Utils.isNullOrEmpty(v.get(4))) {
							if(LocalDate.parse(v.get(4).toString(),dataeformat).isBefore(LocalDate.parse(v.get(3).toString(),dataeformat))) {
								wrongDate.add(LocalDate.parse(v.get(4).toString(),dataeformat));
							}
						}
					});			
					if(Utils.isNullOrEmpty(map1)) {
						synchronousSink.error(new GeneralException("Warning  Excel Sheet is Empty" ));
					}else if(!Utils.isNullOrEmpty(duplicateCheck1)) {
						synchronousSink.error(new GeneralException(duplicateCheck1 + " Duplicate activity name in already present"));
					}else if(!Utils.isNullOrEmpty(campusInvalid)) {
						synchronousSink.error(new GeneralException("Wrong campus entered for "+campusInvalid.toString()));
					}else if(!Utils.isNullOrEmpty(wrongDate)) {
						synchronousSink.error(new GeneralException(wrongDate + " is greater than To Date"));
					}else if(!Utils.isNullOrEmpty(duplicateCheck)) {
						synchronousSink.error(new GeneralException(duplicateCheck + " Duplicate, activity name is alredy present in excel sheet"));
					}else if(!Utils.isNullOrEmpty(duplicateCheck2)) {
						synchronousSink.error(new GeneralException(duplicateCheck2 + " Duplicate, activity name is already exist"));
					}else {
						synchronousSink.next(map1);
					}
				}).map(data2 -> convertDtoMapToDbo(map1,userId,campusMap,locationMap,locCamusMap,academicId,categoryMap,applicableForMap,existLocAcademicMap))
				.flatMap( s ->{ 
					academicCalendarEntryTransaction.update(s);	
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	@SuppressWarnings("unlikely-arg-type")
	public List<ErpCalendarDBO> convertDtoMapToDbo(Map<Integer, List<String>> map1, String userId,Map<String, Integer> campusMap, Map<String, Integer> locationMap, Map<String, List<ErpCampusDBO>> locCamusMap,String academicId, Map<String, Integer> categoryMap,Map<String,Integer> applicableForMap, Map<Integer, List<ErpCalendarDBO>> existLocAcademicMap) {
		List<ErpCalendarDBO> erpCalendarDBOList = new ArrayList<ErpCalendarDBO>();
		DateTimeFormatter dataeformat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
		map1.forEach((k,v) -> {
			if(!Utils.isNullOrEmpty(v.get(1))) {
				String [] items = v.get(1).split("\\s*,\\s*");
				List<String> container = Arrays.asList(items);	
				if(locationMap.containsKey(v.get(0))) {
					Integer locId = locationMap.get(v.get(0));
					if(existLocAcademicMap.containsKey(locId)) {
						List<ErpCalendarDBO> existList = existLocAcademicMap.get(locId);
						List<String> existListName = !Utils.isNullOrEmpty(existList) ? existList.stream().map(p -> p.getActivitiesEvents()).collect(Collectors.toList()) : new ArrayList<String>();
						if(!Utils.isNullOrEmpty(existList)) {
							Set<ErpCalendarCampusDBO> erpAcademicCalendarCampusDBOSet1 = new HashSet<ErpCalendarCampusDBO>();
							List<String> duplicateName = new ArrayList<String>();
							existList.forEach(data1 -> {
								List<String> existCamList = data1.getErpCalendarCampusDBOSet().stream().filter(s -> s.getErpCampusDBO().getRecordStatus() == 'A').map(p -> p.getErpCampusDBO().getCampusName()).collect(Collectors.toList());
								if((data1.getErpLocationDBO().getId() == locId) && (data1.getActivitiesEvents().equalsIgnoreCase(v.get(2))) && (data1.getFromDate().equals(LocalDate.parse(v.get(3).toString(),dataeformat))) && (data1.getToDate().equals(LocalDate.parse(v.get(4).toString(),dataeformat)))) {
									container.forEach(data2 -> {
										if(!existCamList.contains(data2)) {
											Integer campusId = campusMap.get(data2);
											ErpCalendarCampusDBO erpCalendarCampusDBO = new ErpCalendarCampusDBO();
											erpCalendarCampusDBO.setErpCampusDBO(new ErpCampusDBO());
											erpCalendarCampusDBO.getErpCampusDBO().setId(campusId);
											erpCalendarCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
											erpCalendarCampusDBO.setErpCalendarDBO(data1);
											erpCalendarCampusDBO.setRecordStatus('A');
											erpAcademicCalendarCampusDBOSet1.add(erpCalendarCampusDBO);
										}
									});
									if(!Utils.isNullOrEmpty(erpAcademicCalendarCampusDBOSet1)) {
										data1.setErpCalendarCampusDBOSet(erpAcademicCalendarCampusDBOSet1);
										erpCalendarDBOList.add(data1);
									}
								}else if((data1.getErpLocationDBO().getId() == locId) && (data1.getActivitiesEvents().equalsIgnoreCase(v.get(2))) && (data1.getFromDate().equals(LocalDate.parse(v.get(3).toString(),dataeformat))) && (data1.getToDate() != LocalDate.parse(v.get(4).toString(),dataeformat)) && !existCamList.contains(container)) {
									ErpCalendarDBO	erpCalendarDBO = this.convertDtoToErpCalendarDBO(k,v,campusMap,locId,userId,categoryMap,applicableForMap,academicId);
									erpCalendarDBOList.add(erpCalendarDBO);
								}else if((data1.getErpLocationDBO().getId() == locId) && (data1.getActivitiesEvents().equalsIgnoreCase(v.get(2))) && (data1.getFromDate() != LocalDate.parse(v.get(3).toString(),dataeformat)) && (data1.getToDate() != LocalDate.parse(v.get(4).toString(),dataeformat)) && !existCamList.contains(container) && !duplicateName.contains(v.get(2))) {
									ErpCalendarDBO	erpCalendarDBO = this.convertDtoToErpCalendarDBO(k,v,campusMap,locId,userId,categoryMap,applicableForMap,academicId);
									erpCalendarDBOList.add(erpCalendarDBO);
									duplicateName.add(v.get(2));
								}else if((data1.getErpLocationDBO().getId() == locId) && (data1.getActivitiesEvents().equalsIgnoreCase(v.get(2))) && (data1.getFromDate() != LocalDate.parse(v.get(3).toString(),dataeformat)) && (data1.getToDate().isEqual(LocalDate.parse(v.get(4).toString(),dataeformat))) && !existCamList.contains(container)) {
									ErpCalendarDBO	erpCalendarDBO = this.convertDtoToErpCalendarDBO(k,v,campusMap,locId,userId,categoryMap,applicableForMap,academicId);
									erpCalendarDBOList.add(erpCalendarDBO);
								}
								if(!existListName.contains(v.get(2))) {
									ErpCalendarDBO	erpCalendarDBO = this.convertDtoToErpCalendarDBO(k,v,campusMap,locId,userId,categoryMap,applicableForMap,academicId);
									erpCalendarDBOList.add(erpCalendarDBO);
									existListName.add(erpCalendarDBO.getActivitiesEvents());
								}
							});	
						}else {
							ErpCalendarDBO	erpCalendarDBO = this.convertDtoToErpCalendarDBO(k,v,campusMap,locId,userId,categoryMap,applicableForMap,academicId);
							erpCalendarDBOList.add(erpCalendarDBO);
						}
					}else {
						ErpCalendarDBO	erpCalendarDBO = this.convertDtoToErpCalendarDBO(k,v,campusMap,locId,userId,categoryMap,applicableForMap,academicId);
						erpCalendarDBOList.add(erpCalendarDBO);
					}
				}
			}
		});
		return erpCalendarDBOList;
	}

	private ErpCalendarDBO convertDtoToErpCalendarDBO(Integer k, List<String> v,Map<String, Integer> campusMap,Integer locId,String userId, Map<String, Integer> categoryMap,Map<String,Integer> applicableForMap,String academicId) {
		ErpCalendarDBO erpCalendarDBO = new ErpCalendarDBO();
		Set<ErpCalendarCampusDBO> erpAcademicCalendarCampusDBOSet = new HashSet<ErpCalendarCampusDBO>();
		Set<ErpCalendarDatesDBO> erpAcademicCalendarDatesDBOSet = new HashSet<ErpCalendarDatesDBO>();
		erpCalendarDBO.setErpLocationDBO(new ErpLocationDBO());
		erpCalendarDBO.getErpLocationDBO().setId(locId);
		DateTimeFormatter dataeformat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
		erpCalendarDBO.setFromDate(LocalDate.parse(v.get(3).toString(),dataeformat));
		erpCalendarDBO.setToDate(LocalDate.parse(v.get(4).toString(),dataeformat));
		erpCalendarDBO.setActivitiesEvents(v.get(2));
		erpCalendarDBO.setErpAcademicYearDBO(new ErpAcademicYearDBO());
		erpCalendarDBO.getErpAcademicYearDBO().setId(Integer.parseInt(academicId));
		erpCalendarDBO.setCreatedUsersId(Integer.parseInt(userId));
		erpCalendarDBO.setRecordStatus('A');
		if(!Utils.isNullOrEmpty(v.get(1))) {
			String [] items = v.get(1).split("\\s*,\\s*");
			List<String> container = Arrays.asList(items);	
			container.forEach(data -> {
				ErpCalendarCampusDBO erpCalendarCampusDBO = new ErpCalendarCampusDBO();
				Integer campusId = campusMap.get(data);
				ErpCampusDBO erpCampusDBO = new ErpCampusDBO();
				erpCampusDBO.setId(campusId);
				erpCalendarCampusDBO.setErpCampusDBO(erpCampusDBO);
				erpCalendarCampusDBO.setRecordStatus('A');
				erpCalendarCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
				erpCalendarCampusDBO.setErpCalendarDBO(erpCalendarDBO);
				erpAcademicCalendarCampusDBOSet.add(erpCalendarCampusDBO);
			});
			erpCalendarDBO.setErpCalendarCampusDBOSet(erpAcademicCalendarCampusDBOSet);
		}
		if(!Utils.isNullOrEmpty(v.get(3)) && !Utils.isNullOrEmpty(v.get(4))) {
			List<LocalDate> datesList = new ArrayList<LocalDate>();
			if(LocalDate.parse(v.get(3).toString(),dataeformat).equals(LocalDate.parse(v.get(4).toString(),dataeformat))) {
				datesList.add(LocalDate.parse(v.get(3).toString(),dataeformat));
			}else {
				datesList = academicCalendarEntryTransaction.getDatesBetweenStartDateEndDate(LocalDate.parse(v.get(3).toString(),dataeformat), LocalDate.parse(v.get(4).toString(),dataeformat));
				datesList.add(LocalDate.parse(v.get(4).toString(),dataeformat));
			}
			if(!Utils.isNullOrEmpty(datesList)) {
				datesList.forEach(data -> {
					ErpCalendarDatesDBO erpAcademicCalendarDatesDBO = new ErpCalendarDatesDBO();
					erpAcademicCalendarDatesDBO.setDate(data);
					erpAcademicCalendarDatesDBO.setRecordStatus('A');
					erpAcademicCalendarDatesDBO.setErpCalendarDBO(erpCalendarDBO);
					erpAcademicCalendarDatesDBO.setCreatedUsersId(Integer.parseInt(userId));
					erpAcademicCalendarDatesDBOSet.add(erpAcademicCalendarDatesDBO);
				});
				erpCalendarDBO.setErpCalendarDatesDBOSet(erpAcademicCalendarDatesDBOSet);
			}
		}
		if(categoryMap.containsKey(v.get(5))) {
			Integer categoryId = categoryMap.get(v.get(5));
			erpCalendarDBO.setErpCalendarCategoryDBO(new ErpCalendarCategoryDBO());
			erpCalendarDBO.getErpCalendarCategoryDBO().setId(categoryId);
		}
		Set<ErpCalendarUserTypesDetailsDBO> erpAcademicCalendarUserTypesDetailsDBOSet = new HashSet<ErpCalendarUserTypesDetailsDBO>();
		String [] items = v.get(6).split("\\s*,\\s*");
		List<String> container = Arrays.asList(items);
		container.forEach(data2 -> {
			if(applicableForMap.containsKey(data2)) {
				int applicableForId = applicableForMap.get(data2);
				ErpCalendarUserTypesDetailsDBO erpAcademicCalendarUserTypesDetailsDBO = new ErpCalendarUserTypesDetailsDBO();
				erpAcademicCalendarUserTypesDetailsDBO.setErpCalendarDBO(erpCalendarDBO);
				erpAcademicCalendarUserTypesDetailsDBO.setErpCalendarUserTypesDBO(new ErpCalendarUserTypesDBO());
				erpAcademicCalendarUserTypesDetailsDBO.getErpCalendarUserTypesDBO().setId(applicableForId);
				erpAcademicCalendarUserTypesDetailsDBO.setRecordStatus('A');
				erpAcademicCalendarUserTypesDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
				erpAcademicCalendarUserTypesDetailsDBOSet.add(erpAcademicCalendarUserTypesDetailsDBO);
			}
		});	
		erpCalendarDBO.setErpCalendarUserTypesDetailsDBOSet(erpAcademicCalendarUserTypesDetailsDBOSet);
		return erpCalendarDBO;
	}
}