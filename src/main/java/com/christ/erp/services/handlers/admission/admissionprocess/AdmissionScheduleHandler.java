package com.christ.erp.services.handlers.admission.admissionprocess;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmAdmissionScheduleDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmAdmissionScheduleDatesDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmAdmissionScheduleTimeDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dto.admission.admissionprocess.AdmAdmissionScheduleDTO;
import com.christ.erp.services.dto.admission.admissionprocess.AdmAdmissionScheduleDatesDTO;
import com.christ.erp.services.dto.admission.admissionprocess.AdmAdmissionScheduleTimeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.admission.admissionprocess.AdmissionScheduleTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdmissionScheduleHandler {

	@Autowired
	AdmissionScheduleTransaction admissionScheduleTransaction;

	public Flux<AdmAdmissionScheduleDTO> getGridData() {
		return admissionScheduleTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertAdmAdmissionScheduleDBOToDto);
	}

	private AdmAdmissionScheduleDTO convertAdmAdmissionScheduleDBOToDto(AdmAdmissionScheduleDBO admAdmissionScheduleDBO) {
		AdmAdmissionScheduleDTO admAdmissionScheduleDTO = new AdmAdmissionScheduleDTO();
		if(!Utils.isNullOrEmpty(admAdmissionScheduleDBO)) {
			admAdmissionScheduleDTO.setId(admAdmissionScheduleDBO.getId());
			admAdmissionScheduleDTO.setErpAcademicYear(new SelectDTO());
			admAdmissionScheduleDTO.getErpAcademicYear().setValue(admAdmissionScheduleDBO.getErpAcademicYearDBO().getId().toString());
			admAdmissionScheduleDTO.getErpAcademicYear().setLabel(admAdmissionScheduleDBO.getErpAcademicYearDBO().getAcademicYearName());
			admAdmissionScheduleDTO.setErpCampus(new SelectDTO());
			admAdmissionScheduleDTO.getErpCampus().setValue(admAdmissionScheduleDBO.getErpCampusDBO().getId().toString());
			admAdmissionScheduleDTO.getErpCampus().setLabel(admAdmissionScheduleDBO.getErpCampusDBO().getCampusName());
			admAdmissionScheduleDTO.setRecordStatus(admAdmissionScheduleDBO.getRecordStatus());
		}
		return admAdmissionScheduleDTO; 
	}

	public Mono<AdmAdmissionScheduleDTO> edit(String id) {
		AdmAdmissionScheduleDBO admAdmissionScheduleDBO = admissionScheduleTransaction.getData2(Integer.parseInt(id));
		return convertDboToAdmAdmissionScheduleDTO(admAdmissionScheduleDBO);
	}

	private Mono<AdmAdmissionScheduleDTO> convertDboToAdmAdmissionScheduleDTO(AdmAdmissionScheduleDBO dbo) {
		AdmAdmissionScheduleDTO dto=new AdmAdmissionScheduleDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setId(dbo.getId());
			dto.setErpAcademicYear(new SelectDTO());
			dto.getErpAcademicYear().setValue(dbo.getErpAcademicYearDBO().getId().toString());
			dto.getErpAcademicYear().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
			dto.setErpCampus(new SelectDTO());
			dto.getErpCampus().setValue(dbo.getErpCampusDBO().getId().toString());
			dto.getErpCampus().setLabel(dbo.getErpCampusDBO().getCampusName());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			if(!Utils.isNullOrEmpty(dbo.getSaturdayEndTimeSlot()))
			dto.setSaturdayEndTimeSlot(dbo.getSaturdayEndTimeSlot().format(formatter));
			dto.setAdmScheduleFromDate(dbo.getAdmScheduleFromDate());
			dto.setAdmScheduleToDate(dbo.getAdmScheduleToDate());
			dto.setIsSundayInclude(dbo.getIsSundayInclude());
			dto.setIsHolidayInclude(dbo.getIsHolidayInclude());
			dto.setRecordStatus(dbo.getRecordStatus());
			
			List<String> timeList=new ArrayList<String>();
			if(!Utils.isNullOrEmpty(dbo.getAdmAdmissionScheduleDatesDBOSet())) {
				for(AdmAdmissionScheduleDatesDBO date:dbo.getAdmAdmissionScheduleDatesDBOSet()) {
					if(date.getRecordStatus() == 'A') {
						if(!Utils.isNullOrEmpty(date.getAdmAdmissionScheduleTimeDBOSet())) {
								for(AdmAdmissionScheduleTimeDBO time:date.getAdmAdmissionScheduleTimeDBOSet()) {
									if(!timeList.contains(time.getAdmScheduleTimeSlot().format(formatter)))
									timeList.add(time.getAdmScheduleTimeSlot().format(formatter));
								}
						}
					}
				}
			}
			
			
//			if (!Utils.isNullOrEmpty(dbo.getAdmAdmissionScheduleDatesDBOSet())) {
//				dbo.getAdmAdmissionScheduleDatesDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').forEach(d -> {
//					if (!Utils.isNullOrEmpty(d.getAdmAdmissionScheduleTimeDBOSet())) {
//						d.getAdmAdmissionScheduleTimeDBOSet().forEach(t -> {
//							if (!timeList.contains(t.getAdmScheduleTimeSlot().format(formatter)))
//								timeList.add(t.getAdmScheduleTimeSlot().format(formatter));
//						});
//					}
//				});
//			}
			
			List<AdmAdmissionScheduleDatesDTO> admAdmissionScheduleDatesDTOList =new LinkedList<AdmAdmissionScheduleDatesDTO>();
			for (AdmAdmissionScheduleDatesDBO dates : dbo.getAdmAdmissionScheduleDatesDBOSet()) {
				if(dates.getRecordStatus()=='A') {
					int maxCountPerDay=0;
					int maxSelCountPerDay=0;
				AdmAdmissionScheduleDatesDTO admAdmissionScheduleDatesDTO = new AdmAdmissionScheduleDatesDTO();
				admAdmissionScheduleDatesDTO.setId(dates.getId());
				admAdmissionScheduleDatesDTO.setAdmScheduleDate(dates.getAdmScheduleDate());
				admAdmissionScheduleDatesDTO.setIsSunday(dates.getIsSunday());
				admAdmissionScheduleDatesDTO.setIsHoliday(dates.getIsHoliday());
				admAdmissionScheduleDatesDTO.setIsDateNotAvailable(dates.getIsDateNotAvailable());
				admAdmissionScheduleDatesDTO.setRecordStatus(dates.getRecordStatus());

				List<AdmAdmissionScheduleTimeDTO> admAdmissionScheduleTimeDTOList = new LinkedList<AdmAdmissionScheduleTimeDTO>();
				List<String> tempTimeList = new ArrayList<>(timeList);
				for (AdmAdmissionScheduleTimeDBO time : dates.getAdmAdmissionScheduleTimeDBOSet()) {
					AdmAdmissionScheduleTimeDTO admAdmissionScheduleTimeDTO = new AdmAdmissionScheduleTimeDTO();
					admAdmissionScheduleTimeDTO.setId(time.getId());
					if (!Utils.isNullOrEmpty(time.getAdmScheduleTimeSlot()))
						admAdmissionScheduleTimeDTO.setAdmScheduleTimeSlot(time.getAdmScheduleTimeSlot().format(formatter));
					admAdmissionScheduleTimeDTO.setMaxNoOfSeatInSlot(time.getMaxNoOfSeatInSlot());
					admAdmissionScheduleTimeDTO.setRecordStatus(time.getRecordStatus());
					if(!Utils.isNullOrEmpty(time.getStudentApplnEntriesDBOs())) {
						admAdmissionScheduleTimeDTO.setSelectedNoOfSeatInSlot(time.getStudentApplnEntriesDBOs().size());
						maxSelCountPerDay=maxSelCountPerDay+time.getStudentApplnEntriesDBOs().size();
					}
					admAdmissionScheduleTimeDTOList.add(admAdmissionScheduleTimeDTO);
					tempTimeList.remove(time.getAdmScheduleTimeSlot().format(formatter));
					maxCountPerDay=maxCountPerDay+time.getMaxNoOfSeatInSlot();
				}
				for (int i = 0; i < tempTimeList.size(); i++) {
					AdmAdmissionScheduleTimeDTO admAdmissionScheduleTimeDTO = new AdmAdmissionScheduleTimeDTO();
					admAdmissionScheduleTimeDTO.setAdmScheduleTimeSlot(tempTimeList.get(i));
					admAdmissionScheduleTimeDTOList.add(admAdmissionScheduleTimeDTO);
				}

				admAdmissionScheduleTimeDTOList.sort(Comparator.comparing(AdmAdmissionScheduleTimeDTO::getAdmScheduleTimeSlot));
				for (int i = 0; i < 8 - (dates.getAdmAdmissionScheduleTimeDBOSet().size() + tempTimeList.size()); i++) {
					AdmAdmissionScheduleTimeDTO admAdmissionScheduleTimeDTO = new AdmAdmissionScheduleTimeDTO();
					admAdmissionScheduleTimeDTO.setRecordStatus('A');
					admAdmissionScheduleTimeDTOList.add(admAdmissionScheduleTimeDTO);
				}
				
				admAdmissionScheduleDatesDTO.setMaxNoOfSeatInSlotPerDay(maxCountPerDay);
				admAdmissionScheduleDatesDTO.setSelectedNoOfSeatInSlotPerDay(maxSelCountPerDay);
				admAdmissionScheduleDatesDTO.setAdmAdmissionScheduleTimeDTOList(admAdmissionScheduleTimeDTOList);
				admAdmissionScheduleDatesDTOList.add(admAdmissionScheduleDatesDTO);
				}
			}
			admAdmissionScheduleDatesDTOList.sort(Comparator.comparing(AdmAdmissionScheduleDatesDTO::getAdmScheduleDate));
			dto.setAdmAdmissionScheduleDatesDTOList(admAdmissionScheduleDatesDTOList);
		}
		return Mono.just(dto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<AdmAdmissionScheduleDTO> dto, String userId) {
		return dto
				.handle((admAdmissionScheduleDTO, synchronousSink) ->  {
					AdmAdmissionScheduleDBO recordAlreadyExist = admissionScheduleTransaction.getData(Integer.parseInt(admAdmissionScheduleDTO.getErpAcademicYear().getValue()),Integer.parseInt(admAdmissionScheduleDTO.getErpCampus().getValue()));
				
					if ( Utils.isNullOrEmpty(admAdmissionScheduleDTO.getId()) && !Utils.isNullOrEmpty(recordAlreadyExist))
						synchronousSink.error(new GeneralException("Duplicate record. Data already exist for selected year and campus. Click on edit if you need to modify."));
					else
					synchronousSink.next(admAdmissionScheduleDTO);
					
				}).cast(AdmAdmissionScheduleDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap( s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						admissionScheduleTransaction.update(s);
					}else {
						admissionScheduleTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public AdmAdmissionScheduleDBO convertDtoToDbo(AdmAdmissionScheduleDTO dto, String userId) {
		AdmAdmissionScheduleDBO admAdmissionScheduleDBO = Utils.isNullOrEmpty(dto.getId()) ? new AdmAdmissionScheduleDBO() : admissionScheduleTransaction.getData(Integer.parseInt(dto.getErpAcademicYear().getValue()),Integer.parseInt(dto.getErpCampus().getValue()));
		
		Map<Integer,AdmAdmissionScheduleDatesDBO> existAdmAdmissionScheduleDatesDBOMap = !Utils.isNullOrEmpty(admAdmissionScheduleDBO.getAdmAdmissionScheduleDatesDBOSet()) ? 
				admAdmissionScheduleDBO.getAdmAdmissionScheduleDatesDBOSet().stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s->s.getId(), s->s)): new HashMap<Integer,AdmAdmissionScheduleDatesDBO>();
		Map<Integer,AdmAdmissionScheduleTimeDBO> existAdmAdmissionScheduleTimeDBOMap =  new HashMap<Integer,AdmAdmissionScheduleTimeDBO>();
		if(!Utils.isNullOrEmpty(admAdmissionScheduleDBO.getAdmAdmissionScheduleDatesDBOSet())) {
			for(AdmAdmissionScheduleDatesDBO date:admAdmissionScheduleDBO.getAdmAdmissionScheduleDatesDBOSet()) {
				if(date.getRecordStatus() == 'A') {
					if(!Utils.isNullOrEmpty(date.getAdmAdmissionScheduleTimeDBOSet())) {
							for(AdmAdmissionScheduleTimeDBO time:date.getAdmAdmissionScheduleTimeDBOSet()) {
								existAdmAdmissionScheduleTimeDBOMap.put(time.getId(), time);
							}
					}
				}
			}
		}
		
		if(Utils.isNullOrEmpty(dto.getId())) {
			admAdmissionScheduleDBO.setCreatedUsersId(Integer.parseInt(userId));
		}else {
			admAdmissionScheduleDBO.setModifiedUsersId(Integer.parseInt(userId));
		}
		admAdmissionScheduleDBO.setAdmScheduleFromDate(dto.getAdmScheduleFromDate());
		admAdmissionScheduleDBO.setAdmScheduleToDate(dto.getAdmScheduleToDate());
		if(!Utils.isNullOrEmpty(dto.getSaturdayEndTimeSlot()))
		admAdmissionScheduleDBO.setSaturdayEndTimeSlot(Utils.convertStringTimeToLocalTime(dto.getSaturdayEndTimeSlot()));
		admAdmissionScheduleDBO.setErpAcademicYearDBO(new ErpAcademicYearDBO());
		admAdmissionScheduleDBO.getErpAcademicYearDBO().setId(Integer.parseInt(dto.getErpAcademicYear().getValue()));
		admAdmissionScheduleDBO.setErpCampusDBO(new ErpCampusDBO());
		admAdmissionScheduleDBO.getErpCampusDBO().setId(Integer.parseInt(dto.getErpCampus().getValue()));
		admAdmissionScheduleDBO.setRecordStatus('A');
		admAdmissionScheduleDBO.setIsSundayInclude(dto.getIsSundayInclude());
		admAdmissionScheduleDBO.setIsHolidayInclude(dto.getIsHolidayInclude());
		Set<AdmAdmissionScheduleDatesDBO> admAdmissionScheduleDatesDBOSet = new HashSet<AdmAdmissionScheduleDatesDBO>();
		if (!Utils.isNullOrEmpty(dto.getAdmAdmissionScheduleDatesDTOList())) {
			for (AdmAdmissionScheduleDatesDTO dates : dto.getAdmAdmissionScheduleDatesDTOList()) {
				boolean timePresent=false;
				if (!Utils.isNullOrEmpty(dates.getAdmScheduleDate())) {
					AdmAdmissionScheduleDatesDBO admAdmissionScheduleDatesDBO = null;
					if (existAdmAdmissionScheduleDatesDBOMap.containsKey(dates.getId())) {
						admAdmissionScheduleDatesDBO = existAdmAdmissionScheduleDatesDBOMap.get(dates.getId());
						admAdmissionScheduleDatesDBO.setModifiedUsersId(Integer.parseInt(userId));
						admAdmissionScheduleDatesDBO.setRecordStatus('A');
						existAdmAdmissionScheduleDatesDBOMap.remove(dates.getId());
					} else {
						admAdmissionScheduleDatesDBO = new AdmAdmissionScheduleDatesDBO();
						admAdmissionScheduleDatesDBO.setCreatedUsersId(Integer.parseInt(userId));
						admAdmissionScheduleDatesDBO.setRecordStatus('A');
					}

					if (!Utils.isNullOrEmpty(dates.getAdmScheduleDate())) {
						admAdmissionScheduleDatesDBO.setAdmScheduleDate(dates.getAdmScheduleDate());
					}
					if (!Utils.isNullOrEmpty(dates.getIsDateNotAvailable())) {
						admAdmissionScheduleDatesDBO.setIsDateNotAvailable(dates.getIsDateNotAvailable());
					}
					if (!Utils.isNullOrEmpty(dates.getIsHoliday())) {
						admAdmissionScheduleDatesDBO.setIsHoliday(dates.getIsHoliday());
					}
					if (!Utils.isNullOrEmpty(dates.getIsSunday())) {
						admAdmissionScheduleDatesDBO.setIsSunday(dates.getIsSunday());
					}
					admAdmissionScheduleDatesDBO.setAdmAdmissionScheduleDBO(admAdmissionScheduleDBO);

					Set<AdmAdmissionScheduleTimeDBO> admAdmissionScheduleTimeDBOSet = new HashSet<AdmAdmissionScheduleTimeDBO>();
					if (!Utils.isNullOrEmpty(dates.getAdmAdmissionScheduleTimeDTOList())) {
						for (AdmAdmissionScheduleTimeDTO time : dates.getAdmAdmissionScheduleTimeDTOList()) {
							if (!Utils.isNullOrEmpty(time.getAdmScheduleTimeSlot()) && !Utils.isNullOrEmpty(time.getMaxNoOfSeatInSlot())) {
								AdmAdmissionScheduleTimeDBO admAdmissionScheduleTimeDBO = null;
								if (existAdmAdmissionScheduleTimeDBOMap.containsKey(time.getId())) {
									admAdmissionScheduleTimeDBO = existAdmAdmissionScheduleTimeDBOMap.get(time.getId());
									admAdmissionScheduleTimeDBO.setModifiedUsersId(Integer.parseInt(userId));
									admAdmissionScheduleTimeDBO.setRecordStatus('A');
									existAdmAdmissionScheduleTimeDBOMap.remove(time.getId());
								} else {
									admAdmissionScheduleTimeDBO = new AdmAdmissionScheduleTimeDBO();
									admAdmissionScheduleTimeDBO.setCreatedUsersId(Integer.parseInt(userId));
									admAdmissionScheduleTimeDBO.setRecordStatus('A');
								}
								if (!Utils.isNullOrEmpty(time.getMaxNoOfSeatInSlot())) {
									admAdmissionScheduleTimeDBO.setMaxNoOfSeatInSlot(time.getMaxNoOfSeatInSlot());
								}
								if (!Utils.isNullOrEmpty(time.getAdmScheduleTimeSlot())) {
									admAdmissionScheduleTimeDBO.setAdmScheduleTimeSlot(Utils.convertStringTimeToLocalTime(time.getAdmScheduleTimeSlot()));
								}
								admAdmissionScheduleTimeDBO.setAdmAdmissionScheduleDatesDBO(admAdmissionScheduleDatesDBO);
								admAdmissionScheduleTimeDBOSet.add(admAdmissionScheduleTimeDBO);
								timePresent=true;
							}
						}
					}
					admAdmissionScheduleDatesDBO.setAdmAdmissionScheduleTimeDBOSet(admAdmissionScheduleTimeDBOSet);
					if(timePresent)
					admAdmissionScheduleDatesDBOSet.add(admAdmissionScheduleDatesDBO);
				}
			}
		}
		  admAdmissionScheduleDBO.setAdmAdmissionScheduleDatesDBOSet(admAdmissionScheduleDatesDBOSet);
		return admAdmissionScheduleDBO;
	}

	public Mono<ApiResult> delete(String id, String userId) {
		ApiResult result=new ApiResult();
		AdmAdmissionScheduleDBO admAdmissionScheduleDBO = admissionScheduleTransaction.getData2(Integer.parseInt(id));
		admAdmissionScheduleDBO.setRecordStatus('D');
		admAdmissionScheduleDBO.setModifiedUsersId(Integer.parseInt(userId));
		
			if(!Utils.isNullOrEmpty(admAdmissionScheduleDBO.getAdmAdmissionScheduleDatesDBOSet())) {
				 for (AdmAdmissionScheduleDatesDBO admAdmissionScheduleDatesDBO : admAdmissionScheduleDBO.getAdmAdmissionScheduleDatesDBOSet()) {
					admAdmissionScheduleDatesDBO.setRecordStatus('D');
					admAdmissionScheduleDatesDBO.setModifiedUsersId(Integer.parseInt(userId));
					
					if(!Utils.isNullOrEmpty(admAdmissionScheduleDatesDBO.getAdmAdmissionScheduleTimeDBOSet())) {
						 for (AdmAdmissionScheduleTimeDBO admAdmissionScheduleTimeDBO : admAdmissionScheduleDatesDBO.getAdmAdmissionScheduleTimeDBOSet()) {
							admAdmissionScheduleTimeDBO.setRecordStatus('D');
							admAdmissionScheduleTimeDBO.setModifiedUsersId(Integer.parseInt(userId));
						 }
					}
				}
			}
			admissionScheduleTransaction.update(admAdmissionScheduleDBO);
			result.success=true;
		return  Utils.monoFromObject(result);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> onSearch(Mono<AdmAdmissionScheduleDTO> dto) {
		ApiResult result=new ApiResult();
		return dto.handle((admAdmissionScheduleDTO, synchronousSink) -> {
			AdmAdmissionScheduleDBO recordAlreadyExist = admissionScheduleTransaction.getData(Integer.parseInt(admAdmissionScheduleDTO.getErpAcademicYear().getValue()),
					Integer.parseInt(admAdmissionScheduleDTO.getErpCampus().getValue()));

			if (Utils.isNullOrEmpty(admAdmissionScheduleDTO.getId()) && !Utils.isNullOrEmpty(recordAlreadyExist))
				synchronousSink.error(new GeneralException("Duplicate record. Data already exist for selected year and campus. Click on edit if you need to modify."));
			else {
				result.success=true;
				synchronousSink.next(result);
			}
		});
	}

}
