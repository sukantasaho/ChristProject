package com.christ.erp.services.handlers.curriculum.timeTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.curriculum.timeTable.TimeTableTemplateCampusDBO;
import com.christ.erp.services.dbobjects.curriculum.timeTable.TimeTableTemplateDBO;
import com.christ.erp.services.dbobjects.curriculum.timeTable.TimeTableTemplateDayDBO;
import com.christ.erp.services.dbobjects.curriculum.timeTable.TimeTableTemplatePeriodDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.timeTable.TemplateMasterDTO;
import com.christ.erp.services.dto.curriculum.timeTable.TimeTableDetailsDTO;
import com.christ.erp.services.dto.curriculum.timeTable.TimeTablePeriodDetailsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.curriculum.timeTable.TemplateMasterTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({"rawtypes"})
@Service
public class TemplateMasterHandler {

	@Autowired
	private TemplateMasterTransaction templateMasterTransaction;

	public Flux<TemplateMasterDTO> getGridData() {
		return templateMasterTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public TemplateMasterDTO convertDBOToDTO(TimeTableTemplateDBO dbo) {
		TemplateMasterDTO dto = new TemplateMasterDTO();
		dto.setId(dbo.getId());
		dto.setNameOfThePeriodTemplate(dbo.getTimeTableName());
		dto.setPeriodDurationInMinutes(dbo.getPeriodDurationInMinutes());
		dto.setCampus(new ArrayList<SelectDTO>());
		if(!Utils.isNullOrEmpty(dbo.getTimeTableTemplateCampusDBOSet())) {
			dbo.getTimeTableTemplateCampusDBOSet().forEach(campus -> {
				if(campus.getRecordStatus() == 'A') {
					SelectDTO campusDto = new SelectDTO();
					campusDto.setValue(campus.getErpCampusDBO().getId().toString());
					campusDto.setLabel(campus.getErpCampusDBO().getCampusName());
					dto.getCampus().add(campusDto);
				}
			});
		}
		return dto;
	}

	public Mono<TemplateMasterDTO> edit(int timeTableTemplateId) {
		return this.convertDboToDto(templateMasterTransaction.edit(timeTableTemplateId));
	}

	public Mono<TemplateMasterDTO> convertDboToDto(TimeTableTemplateDBO dbo) {
		TemplateMasterDTO dto = new TemplateMasterDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setId(dbo.getId());
			dto.setNameOfThePeriodTemplate(dbo.getTimeTableName());
			dto.setPeriodDurationInMinutes(dbo.getPeriodDurationInMinutes());
			dto.setIsStatic(dbo.getIsStatic());
			if(!Utils.isNullOrEmpty(dbo.getTimeTableTemplateCampusDBOSet())) {
				Set<SelectDTO> campusList = new HashSet<SelectDTO>();
				dbo.getTimeTableTemplateCampusDBOSet().forEach(campusDbo -> {
					SelectDTO campus = new SelectDTO();
					campus.setValue(campusDbo.getErpCampusDBO().getId().toString());
					campus.setLabel(campusDbo.getErpCampusDBO().getCampusName());
					campusList.add(campus);
				});
				dto.setCampus(campusList.stream().collect(Collectors.toList()));
			}
			dto.setTimeTableDetailsList(new ArrayList<TimeTableDetailsDTO>());

			//Time Table Days
			if(!Utils.isNullOrEmpty(dbo.getTimeTableTemplateDayDBOSet())) {
				dbo.getTimeTableTemplateDayDBOSet().forEach( dayDbo -> {
					TimeTableDetailsDTO timeTableDetailsDTO =  new TimeTableDetailsDTO();
					timeTableDetailsDTO.setId(dayDbo.getId());
					timeTableDetailsDTO.setDayName(dayDbo.getDayName());
					timeTableDetailsDTO.setDayOfWeek(dayDbo.getDayOfWeek());
					timeTableDetailsDTO.setTimeTablePeriodDetailsList(new ArrayList<TimeTablePeriodDetailsDTO>());

					//period details of day
					if(!Utils.isNullOrEmpty(dayDbo.getTimeTableTemplatePeriodDBOSet())) {
						dayDbo.getTimeTableTemplatePeriodDBOSet().forEach(periodDetailsDbo -> {
							if(periodDetailsDbo.getRecordStatus() == 'A') {
								TimeTablePeriodDetailsDTO timeTablePeriodDetailsDTO = new TimeTablePeriodDetailsDTO();
								timeTablePeriodDetailsDTO.setId(periodDetailsDbo.getId());
								timeTablePeriodDetailsDTO.setPeriodName(periodDetailsDbo.getPeriodName());
								timeTablePeriodDetailsDTO.setFromTime(periodDetailsDbo.getPeriodStartTime());
								timeTablePeriodDetailsDTO.setToTime(periodDetailsDbo.getPeriodEndTime());
								timeTablePeriodDetailsDTO.setPeriodOrder(periodDetailsDbo.getPeriodOrder());
								timeTablePeriodDetailsDTO.setDurationInHour(periodDetailsDbo.getDurationInHour());
								timeTableDetailsDTO.getTimeTablePeriodDetailsList().add(timeTablePeriodDetailsDTO);
								timeTableDetailsDTO.getTimeTablePeriodDetailsList().sort(Comparator.comparing(TimeTablePeriodDetailsDTO::getPeriodOrder));
							}
						});
					}
					dto.getTimeTableDetailsList().add(timeTableDetailsDTO);
				});
			}
			dto.getTimeTableDetailsList().sort(Comparator.comparing(TimeTableDetailsDTO::getDayOfWeek));
		}
		return !Utils.isNullOrEmpty(dto.getId()) ? Mono.just(dto) : Mono.error(new NotFoundException(null));
	}

	public Mono<ApiResult> saveOrUpdate(Mono<TemplateMasterDTO> dto, String userId) {
		return dto.handle((templateMasterDTO, synchronousSink) -> {
			TimeTableTemplateDBO duplicateCheck = templateMasterTransaction.isDuplicateCheck(templateMasterDTO.getId(),templateMasterDTO.getNameOfThePeriodTemplate().trim().replaceAll("\\s+", ""));
			if(!Utils.isNullOrEmpty(duplicateCheck)) {
				synchronousSink.error(new DuplicateException(" Template Name Already Exists"));
			} else {
				synchronousSink.next(templateMasterDTO);
			}
		}).cast(TemplateMasterDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap( s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						templateMasterTransaction.update(s);
					} else {
						templateMasterTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public TimeTableTemplateDBO convertDtoToDbo(TemplateMasterDTO dto, String userId) {
		TimeTableTemplateDBO dbo;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo =  templateMasterTransaction.edit(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		} else {
			dbo = new TimeTableTemplateDBO();
			dbo.setCreatedUsersId(Integer.parseInt(userId));
		}
		dbo.setTimeTableName(dto.getNameOfThePeriodTemplate().trim().replace("\\s+", " "));
		dbo.setPeriodDurationInMinutes(dto.getPeriodDurationInMinutes());
		if(dto.getIsStatic()) {
			dbo.setIsStatic(true);
		} else {
			dbo.setIsStatic(false);
		}
		dbo.setRecordStatus('A');

		Map<Integer,TimeTableTemplateCampusDBO> timeTableTemplateCampusDBOMap = new HashMap<Integer, TimeTableTemplateCampusDBO>();
		if(!Utils.isNullOrEmpty(dbo.getTimeTableTemplateCampusDBOSet())) {
			dbo.getTimeTableTemplateCampusDBOSet().forEach( campus -> {
				if(campus.getRecordStatus() == 'A') {
					timeTableTemplateCampusDBOMap.put(campus.getErpCampusDBO().getId(), campus);
				}
			});
		}

		//Template campus
		Set<TimeTableTemplateCampusDBO> timeTableTemplateCampusDBOSetUpdate = new HashSet<TimeTableTemplateCampusDBO>();
		if(!Utils.isNullOrEmpty(dto.getCampus())) {
			dto.getCampus().forEach( campusDto -> {
				TimeTableTemplateCampusDBO timeTableTemplateCampusDBO;
				if(timeTableTemplateCampusDBOMap.containsKey(Integer.parseInt(campusDto.getValue()))) {
					timeTableTemplateCampusDBO = timeTableTemplateCampusDBOMap.get(Integer.parseInt(campusDto.getValue()));
					timeTableTemplateCampusDBO.setModifiedUsersId(Integer.parseInt(userId));
					timeTableTemplateCampusDBOMap.remove(Integer.parseInt(campusDto.getValue()));
				} else {
					timeTableTemplateCampusDBO = new TimeTableTemplateCampusDBO();
					timeTableTemplateCampusDBO.setCreatedUsersId(Integer.parseInt(campusDto.getValue()));
				}
				timeTableTemplateCampusDBO.setErpCampusDBO(new ErpCampusDBO());
				timeTableTemplateCampusDBO.getErpCampusDBO().setId(Integer.parseInt(campusDto.getValue()));
				timeTableTemplateCampusDBO.setTimeTableTemplateDBO(dbo);
				timeTableTemplateCampusDBO.setRecordStatus('A');
				timeTableTemplateCampusDBOSetUpdate.add(timeTableTemplateCampusDBO);
			});

			if(!Utils.isNullOrEmpty(timeTableTemplateCampusDBOMap)) {
				timeTableTemplateCampusDBOMap.forEach((key,value) -> {
					value.setRecordStatus('D');
					value.setModifiedUsersId(Integer.parseInt(userId));
					timeTableTemplateCampusDBOSetUpdate.add(value);
				});
			}
			dbo.setTimeTableTemplateCampusDBOSet(timeTableTemplateCampusDBOSetUpdate);
		}

		Map<Integer,TimeTableTemplateDayDBO> timeTableTemplateDayDBOExistMap = new HashMap<Integer, TimeTableTemplateDayDBO>();
		if(!Utils.isNullOrEmpty(dbo.getTimeTableTemplateDayDBOSet())) {
			dbo.getTimeTableTemplateDayDBOSet().forEach( day -> {
				if(day.getRecordStatus() == 'A') {
					timeTableTemplateDayDBOExistMap.put(day.getId(), day);
				}
			});
		}

		//Time Table Days 
		Set<TimeTableTemplateDayDBO> timeTableTemplateDayDBOSetUpdate = new HashSet<TimeTableTemplateDayDBO>();
		if(!Utils.isNullOrEmpty(dto.getTimeTableDetailsList())) {
			dto.getTimeTableDetailsList().forEach( detailsDto -> {
				TimeTableTemplateDayDBO timeTableTemplateDayDBO ;
				if(timeTableTemplateDayDBOExistMap.containsKey(detailsDto.getId())) {
					timeTableTemplateDayDBO = timeTableTemplateDayDBOExistMap.get(detailsDto.getId());
					timeTableTemplateDayDBO.setModifiedUsersId(Integer.parseInt(userId));
					timeTableTemplateDayDBOExistMap.remove(detailsDto.getId());
				} else {
					timeTableTemplateDayDBO = new TimeTableTemplateDayDBO();
					timeTableTemplateDayDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				timeTableTemplateDayDBO.setDayName(detailsDto.getDayName());
				timeTableTemplateDayDBO.setDayOfWeek(detailsDto.getDayOfWeek());
				timeTableTemplateDayDBO.setTimeTableTemplateDBO(dbo);
				timeTableTemplateDayDBO.setRecordStatus('A');

				Map<Integer,TimeTableTemplatePeriodDBO> timeTableTemplatePeriodDBOExistMap = new HashMap<Integer, TimeTableTemplatePeriodDBO>();
				if(!Utils.isNullOrEmpty(timeTableTemplateDayDBO.getTimeTableTemplatePeriodDBOSet())) {
					timeTableTemplatePeriodDBOExistMap.clear();
					timeTableTemplateDayDBO.getTimeTableTemplatePeriodDBOSet().forEach(periods -> {
						if(periods.getRecordStatus() == 'A') {
							timeTableTemplatePeriodDBOExistMap.put(periods.getId(), periods);
						}
					});
				}

				//Periods details for days 
				Set<TimeTableTemplatePeriodDBO> timeTableTemplatePeriodDBOSetUpdate = new HashSet<TimeTableTemplatePeriodDBO>();
				if(!Utils.isNullOrEmpty(detailsDto.getTimeTablePeriodDetailsList())) {
					detailsDto.getTimeTablePeriodDetailsList().forEach(periodsDto -> {
						TimeTableTemplatePeriodDBO timeTableTemplatePeriodDBO;
						if(timeTableTemplatePeriodDBOExistMap.containsKey(periodsDto.getId())) {
							timeTableTemplatePeriodDBO = timeTableTemplatePeriodDBOExistMap.get(periodsDto.getId());
							timeTableTemplatePeriodDBO.setModifiedUsersId(Integer.parseInt(userId));
							timeTableTemplatePeriodDBOExistMap.remove(periodsDto.getId());
						} else {
							timeTableTemplatePeriodDBO = new TimeTableTemplatePeriodDBO();
							timeTableTemplatePeriodDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						timeTableTemplatePeriodDBO.setTimeTableTemplateDayDBO(timeTableTemplateDayDBO);
						timeTableTemplatePeriodDBO.setPeriodName(periodsDto.getPeriodName());
						timeTableTemplatePeriodDBO.setPeriodStartTime(periodsDto.getFromTime());
						timeTableTemplatePeriodDBO.setPeriodEndTime(periodsDto.getToTime());
						timeTableTemplatePeriodDBO.setDurationInHour(periodsDto.getDurationInHour());
						timeTableTemplatePeriodDBO.setPeriodOrder(periodsDto.getPeriodOrder());
						timeTableTemplatePeriodDBO.setRecordStatus('A');
						timeTableTemplatePeriodDBOSetUpdate.add(timeTableTemplatePeriodDBO);
					});
				}
				if(!Utils.isNullOrEmpty(timeTableTemplatePeriodDBOExistMap)) {
					timeTableTemplatePeriodDBOExistMap.forEach((key,value) -> {
						value.setRecordStatus('D');
						value.setModifiedUsersId(Integer.parseInt(userId));
						timeTableTemplatePeriodDBOSetUpdate.add(value);
					});
				}
				timeTableTemplateDayDBO.setTimeTableTemplatePeriodDBOSet(timeTableTemplatePeriodDBOSetUpdate);
				timeTableTemplateDayDBOSetUpdate.add(timeTableTemplateDayDBO);
			});
			if(!Utils.isNullOrEmpty(timeTableTemplateDayDBOExistMap)) {
				timeTableTemplateDayDBOExistMap.forEach((key,value) -> {
					value.setRecordStatus('D');
					value.setModifiedUsersId(Integer.parseInt(userId));

					//if Day is deleted related periods of day to be deleted
					if(!Utils.isNullOrEmpty(value.getTimeTableTemplatePeriodDBOSet())) {
						value.getTimeTableTemplatePeriodDBOSet().forEach( data -> {
							data.setRecordStatus('D');
							data.setModifiedUsersId(Integer.parseInt(userId));
						});
					}
					timeTableTemplateDayDBOSetUpdate.add(value);
				});
			}
			dbo.setTimeTableTemplateDayDBOSet(timeTableTemplateDayDBOSetUpdate);
		}
		return dbo;
	}

	public Mono<ApiResult> delete(int id, String userId) {
		return templateMasterTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

}
