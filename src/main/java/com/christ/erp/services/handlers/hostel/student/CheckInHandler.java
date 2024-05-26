package com.christ.erp.services.handlers.hostel.student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusLogDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsFacilityDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBedDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFacilitySettingDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFloorDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomsDBO;
import com.christ.erp.services.dto.admission.applicationprocess.ErpResidentCategoryDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsFacilityDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataAddtnlDTO;
import com.christ.erp.services.dto.student.common.StudentPersonalDataDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.common.CommonHostelTransaction;
import com.christ.erp.services.transactions.hostel.student.CheckInTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CheckInHandler {

	@Autowired
	CheckInTransaction checkInTransaction;

	@Autowired
	CommonHostelTransaction commonHostelTransaction1;

	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public Flux<HostelAdmissionsDTO> getGridData(String academicYearId, String hostelId) {
		List<HostelAdmissionsDBO> hostelAdmissionsDBOList = checkInTransaction.getGridData(academicYearId,hostelId);
		return this.convertDboToDto(hostelAdmissionsDBOList);
	}	

	public Flux<HostelAdmissionsDTO> convertDboToDto(List<HostelAdmissionsDBO> hostelAdmissionsDBOList) {
		List<HostelAdmissionsDTO> hostelAdmissionsDTOList = new ArrayList<HostelAdmissionsDTO>();
		if(!Utils.isNullOrEmpty(hostelAdmissionsDBOList)) {
			hostelAdmissionsDBOList.forEach(data -> {
				HostelAdmissionsDTO hostelAdmissionsDTO = new HostelAdmissionsDTO();
				if(!Utils.isNullOrEmpty(data.getId())) {
					hostelAdmissionsDTO.setId(data.getId());
				}
				hostelAdmissionsDTO.setHostelApplicationDTO(new HostelApplicationDTO());
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getApplicationNo())) {
					hostelAdmissionsDTO.getHostelApplicationDTO().setHostelApplicationNo(String.valueOf(data.getHostelApplicationDBO().getApplicationNo()));
				}
				if(!Utils.isNullOrEmpty(data.getHostelRoomTypeDBO())) {
					hostelAdmissionsDTO.setHostelRoomTypeDTO(new SelectDTO());
					hostelAdmissionsDTO.getHostelRoomTypeDTO().setValue(String.valueOf(data.getHostelRoomTypeDBO().getId()));
					hostelAdmissionsDTO.getHostelRoomTypeDTO().setLabel(data.getHostelRoomTypeDBO().getRoomType());
				}
				hostelAdmissionsDTO.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
				if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getApplicantName())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicantName(data.getStudentApplnEntriesDBO().getApplicantName());
				}
				if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getApplicationNo())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicationNumber(String.valueOf(data.getStudentApplnEntriesDBO().getApplicationNo()));
				}
				if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getStudentPersonalDataDBO())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setStudentPersonalDataDTO(new StudentPersonalDataDTO());
					if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getStudentPersonalDataDBO().getStudentPersonalDataAddressDBO())) {
						hostelAdmissionsDTO.getStudentApplnEntriesDTO().getStudentPersonalDataDTO().setStudentPersonalDataAddtnlDTO(new StudentPersonalDataAddtnlDTO());
						hostelAdmissionsDTO.getStudentApplnEntriesDTO().getStudentPersonalDataDTO().getStudentPersonalDataAddtnlDTO().setProfilePhotoUrl(data.getStudentApplnEntriesDBO().getStudentPersonalDataDBO().getStudentPersonalDataAddtnlDBO().getProfilePhotoUrl());
					}
				}
				if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setProgramName(data.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				}
				if(!Utils.isNullOrEmpty(data.getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getResidentCategoryName())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setErpResidentCategory(new ErpResidentCategoryDTO());
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpResidentCategory().setResidentCategoryName(data.getStudentApplnEntriesDBO().getErpResidentCategoryDBO().getResidentCategoryName());
				}	
				hostelAdmissionsDTO.setStudentDTO(new StudentDTO());
				if(!Utils.isNullOrEmpty(data.getStudentDBO().getRegisterNo())) {
					hostelAdmissionsDTO.getStudentDTO().setRegisterNo(data.getStudentDBO().getRegisterNo());
				}
				if(!Utils.isNullOrEmpty(data.getStudentDBO().getAcaClassDBO())) {
					hostelAdmissionsDTO.getStudentDTO().setAcaClassDTO(data.getStudentDBO().getAcaClassDBO().getClassName());
				}
				if(!Utils.isNullOrEmpty(data.getErpStatusDBO())) {
					if (data.getErpStatusDBO().statusCode.equalsIgnoreCase("HOSTEL_CHECK_IN"))
					hostelAdmissionsDTO.setErpStatus("Checked-In");
					else if (data.getErpStatusDBO().statusCode.equalsIgnoreCase("HOSTEL_ADMITTED"))
					hostelAdmissionsDTO.setErpStatus("Not Reported");
				} 
				hostelAdmissionsDTOList.add(hostelAdmissionsDTO);
			});
		}
		return  Flux.fromIterable(hostelAdmissionsDTOList);
	}

	public Mono<HostelAdmissionsDTO> edit(int id) {
		HostelAdmissionsDBO dbo = checkInTransaction.edit(id);
		return convertDboToDto(dbo);
	}

	public Mono<HostelAdmissionsDTO> convertDboToDto(HostelAdmissionsDBO dbo) {
		HostelAdmissionsDTO hostelAdmissionsDTO = new HostelAdmissionsDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			hostelAdmissionsDTO.setId(dbo.getId());
		}
		hostelAdmissionsDTO.setHostel(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getHostelDBO().getId())) {
			hostelAdmissionsDTO.getHostel().setValue(String.valueOf(dbo.getHostelDBO().getId()));
			hostelAdmissionsDTO.getHostel().setLabel(dbo.getHostelDBO().getHostelName());
		}
		hostelAdmissionsDTO.setHostelApplicationDTO(new HostelApplicationDTO());
		if(!Utils.isNullOrEmpty(dbo.getHostelApplicationDBO().getApplicationNo())) {
			hostelAdmissionsDTO.getHostelApplicationDTO().setHostelApplicationNo(String.valueOf(dbo.getHostelApplicationDBO().getApplicationNo()));
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelRoomTypeDBO())) {
			hostelAdmissionsDTO.setHostelRoomTypeDTO(new SelectDTO());
			hostelAdmissionsDTO.getHostelRoomTypeDTO().setValue(String.valueOf(dbo.getHostelRoomTypeDBO().getId()));
			hostelAdmissionsDTO.getHostelRoomTypeDTO().setLabel(dbo.getHostelRoomTypeDBO().getRoomType());
		}
		hostelAdmissionsDTO.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
		if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicantName())) {
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicantName(dbo.getStudentApplnEntriesDBO().getApplicantName());
		}
		if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicationNo())) {
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicationNumber(String.valueOf(dbo.getStudentApplnEntriesDBO().getApplicationNo()));
		}
		if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName())) {
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setProgramName(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
		}
		hostelAdmissionsDTO.setStudentDTO(new StudentDTO());
		if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getRegisterNo())) {
			hostelAdmissionsDTO.getStudentDTO().setRegisterNo(dbo.getStudentDBO().getRegisterNo());
		}
		if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getAcaClassDBO())) {
			hostelAdmissionsDTO.getStudentDTO().setAcaClassDTO(dbo.getStudentDBO().getAcaClassDBO().getClassName());
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO())){
			if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getId())) {
				hostelAdmissionsDTO.setBedNo(new SelectDTO());
				hostelAdmissionsDTO.getBedNo().setValue(String.valueOf(dbo.getHostelBedDBO().getId()));
				hostelAdmissionsDTO.getBedNo().setLabel(dbo.getHostelBedDBO().getBedNo());
			}
			if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO())) {
				if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getId())) {
					hostelAdmissionsDTO.setRoomNo(new SelectDTO());
					hostelAdmissionsDTO.getRoomNo().setValue(String.valueOf(dbo.getHostelBedDBO().getHostelRoomsDBO().getId()));
					hostelAdmissionsDTO.getRoomNo().setLabel(dbo.getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
				}
			}
			if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO())) {
				if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getId())) {
					hostelAdmissionsDTO.setFloorNo(new SelectDTO());
					hostelAdmissionsDTO.getFloorNo().setValue(String.valueOf(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getId()));
					hostelAdmissionsDTO.getFloorNo().setLabel(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getFloorNo().toString());
				}
			}		
			if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO())){
				if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId())) {
					hostelAdmissionsDTO.setUnit(new SelectDTO());
					hostelAdmissionsDTO.getUnit().setValue(String.valueOf(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId()));
					hostelAdmissionsDTO.getUnit().setLabel(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit());
				}
			}
			if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO())) {
				if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId())) {
					hostelAdmissionsDTO.setBlock(new SelectDTO());
					hostelAdmissionsDTO.getBlock().setValue(String.valueOf(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId()));
					hostelAdmissionsDTO.getBlock().setLabel(dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName());
				}
			}
		}
		if(!Utils.isNullOrEmpty(dbo.getBiometricId())) {
			hostelAdmissionsDTO.setBiometricId(dbo.getBiometricId());
		}
		if(!Utils.isNullOrEmpty(dbo.getCheckInDate())) {
			hostelAdmissionsDTO.setCheckInDate(dbo.getCheckInDate());
		}
		Map<Integer, HostelFacilitySettingDBO> facilitiesListMap = checkInTransaction.getHostelFacilities().stream().collect(Collectors.toMap(s->s.getId(), s->s)); 
		List<HostelAdmissionsFacilityDTO> facilitiesDto = new ArrayList<HostelAdmissionsFacilityDTO>();
		dbo.getHostelFacilityDBOSet().forEach(subdbo -> {
			if(subdbo.getRecordStatus() == 'A') {
				if(facilitiesListMap.containsKey(subdbo.getHostelFacilitySettingsDBO().getId())) {
					HostelAdmissionsFacilityDTO dtos = new HostelAdmissionsFacilityDTO();   	
					dtos.setId(subdbo.getId());
					if(!Utils.isNullOrEmpty(subdbo.getFacilityDescription())) {
						dtos.setFacilityDescription(subdbo.getFacilityDescription());
					}
					if(!Utils.isNullOrEmpty(subdbo.getHostelFacilitySettingsDBO())) {
						dtos.setHostelFacilitySettingsDTO(new SelectDTO());
						dtos.getHostelFacilitySettingsDTO().setValue(subdbo.getHostelFacilitySettingsDBO().getId().toString());
						dtos.getHostelFacilitySettingsDTO().setLabel(subdbo.getHostelFacilitySettingsDBO().getFacilityName());
						dtos.setIsChecked(true);
					}
					facilitiesListMap.remove(subdbo.getHostelFacilitySettingsDBO().getId());
					facilitiesDto.add(dtos);
				} 
			}
		});
		facilitiesListMap.forEach((key, value) -> {
			HostelAdmissionsFacilityDTO dtos = new HostelAdmissionsFacilityDTO(); 
			dtos.setHostelFacilitySettingsDTO(new SelectDTO());
			dtos.getHostelFacilitySettingsDTO().setValue(value.getId().toString());
			dtos.getHostelFacilitySettingsDTO().setLabel(value.getFacilityName());
			dtos.setIsChecked(false);
			facilitiesDto.add(dtos);
		});
		hostelAdmissionsDTO.setHostelAdmissionsFacilityList(facilitiesDto);
		return Mono.just(hostelAdmissionsDTO);	
	}

	public Mono<HostelAdmissionsDTO> getDataByStudent(String applnNo, String regNo, String name) {
		HostelAdmissionsDBO dbo = checkInTransaction.getDataByStudent(applnNo, regNo, name);
		return convertDboToDto(dbo);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelAdmissionsDTO> dto,  String userId) {
		return dto.map(data -> convertDtoToDbo(data ,userId))
				.flatMap(s -> {
					checkInTransaction.merge(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo(HostelAdmissionsDTO dto, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<ErpStatusLogDBO> statusList = new ArrayList<ErpStatusLogDBO>();
		HostelAdmissionsDBO dbo = null;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo = checkInTransaction.edit(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		if(!Utils.isNullOrEmpty(dto.getBiometricId())) {
			dbo.setBiometricId(dto.getBiometricId());
		}
		if(!Utils.isNullOrEmpty(dto.getCheckInDate())) {
			dbo.setCheckInDate(dto.getCheckInDate());
		}
		if(!Utils.isNullOrEmpty(dto.getRoomNo())) {
			dbo.getHostelBedDBO().setHostelRoomsDBO(new HostelRoomsDBO());
			dbo.getHostelBedDBO().getHostelRoomsDBO().setId(Integer.parseInt(dto.getRoomNo().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getFloorNo())) {
			dbo.getHostelBedDBO().getHostelRoomsDBO().setHostelFloorDBO(new HostelFloorDBO());
			dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().setId(Integer.parseInt(dto.getFloorNo().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getUnit())) {
			dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().setHostelBlockUnitDBO(new HostelBlockUnitDBO());
			dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().setId(Integer.parseInt(dto.getUnit().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getBlock())) {
			dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().setHostelBlockDBO(new HostelBlockDBO());
			dbo.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().setId(Integer.parseInt(dto.getBlock().getValue()));
		}
		dbo.setModifiedUsersId(Integer.parseInt(userId));
		Set<HostelAdmissionsFacilityDBO> existDBOSet = dbo.getHostelFacilityDBOSet();
		Map<Integer, HostelAdmissionsFacilityDBO> map = new HashMap<Integer, HostelAdmissionsFacilityDBO>();
		if(!Utils.isNullOrEmpty(existDBOSet)) {
			existDBOSet.forEach(dbos-> {
				if (dbos.getRecordStatus()=='A') {
					map.put(dbos.getId(), dbos);
				}
			});
		}
		Set<HostelAdmissionsFacilityDBO> admissionDbo = new HashSet<HostelAdmissionsFacilityDBO>();  
		if(!Utils.isNullOrEmpty(dto.getHostelAdmissionsFacilityList())) {
			dto.getHostelAdmissionsFacilityList().forEach(fdto -> {
				HostelAdmissionsFacilityDBO facilityDbo = null;
				if(!Utils.isNullOrEmpty(fdto.getId()) && map.containsKey(fdto.getId())) {
					facilityDbo = map.get(fdto.getId());
					facilityDbo.setModifiedUsersId(Integer.parseInt(userId));
					map.remove(fdto.getId());
				} else {
					facilityDbo = new HostelAdmissionsFacilityDBO();
					facilityDbo.setCreatedUsersId(Integer.parseInt(userId));
				}
			});
			HostelAdmissionsDBO dbo1 = dbo;
			if(!Utils.isNullOrEmpty(dto.getHostelAdmissionsFacilityList())) {
				dto.getHostelAdmissionsFacilityList().forEach(fdto -> {
					HostelAdmissionsFacilityDBO facilityDbo = new HostelAdmissionsFacilityDBO();
					facilityDbo.setId(fdto.getId());
					facilityDbo.setHostelAdmissionsDBO(dbo1);
					if(!Utils.isNullOrEmpty(fdto.getHostelFacilitySettingsDTO())) {
						facilityDbo.setHostelFacilitySettingsDBO(new HostelFacilitySettingDBO());
						facilityDbo.getHostelFacilitySettingsDBO().setId(Integer.parseInt(fdto.getHostelFacilitySettingsDTO().getValue()));
					} 
					if(!Utils.isNullOrEmpty(fdto.getFacilityDescription())) {
						facilityDbo.setFacilityDescription(fdto.getFacilityDescription());
					}
					facilityDbo.setCreatedUsersId(Integer.parseInt(userId));
					if(!Utils.isNullOrEmpty(fdto.getId()))
						facilityDbo.setModifiedUsersId(Integer.parseInt(userId));
					facilityDbo.setRecordStatus('A');
					admissionDbo.add(facilityDbo);
				});
				dbo.setHostelFacilityDBOSet(admissionDbo);
			}
		}
		if(!Utils.isNullOrEmpty(map)) {
			map.forEach((entry, value)-> {
				value.setModifiedUsersId(Integer.parseInt(userId));
				value.setRecordStatus('D');
				admissionDbo.add(value);
			});
		}
		Integer statusId = checkInTransaction.getStatusId("HOSTEL_CHECK_IN");
		if(!Utils.isNullOrEmpty(statusId)) {	
			dbo.getErpStatusDBO().setId(statusId);
			dbo.setErpCurrentStatusTime(LocalDateTime.now());
		}
	// status 
		ErpStatusLogDBO statusLogDBO = new ErpStatusLogDBO();
		statusLogDBO.setEntryId(dto.getId());  
		ErpStatusDBO statusDBO = new ErpStatusDBO();
		statusDBO.setId(statusId);
		statusLogDBO.setErpStatusDBO(statusDBO);
		statusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
		statusLogDBO.setRecordStatus('A');
		statusList.add(statusLogDBO);

  //bed save
		List<Integer> bedId = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(dto.getBedNo())) {
			if(!Utils.isNullOrEmpty(dto.getBedNo().getValue())) {
				bedId.add(Integer.parseInt(dto.getBedNo().getValue()));
			}
		}
		Map<Integer, HostelBedDBO> hostelBedDboMap = checkInTransaction.getHostelBed1(bedId).stream().collect(Collectors.toMap(s-> s.getId(), s->s)); 
		List<HostelBedDBO> hostelBedDBOList = new ArrayList<HostelBedDBO>();
		if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO())) {
			if(dbo.getHostelBedDBO().getId() != Integer.parseInt(dto.getBedNo().getValue())) {
				HostelBedDBO hostelBedDBO = new HostelBedDBO();
				hostelBedDBO = dbo.getHostelBedDBO();
				hostelBedDBO.setOccupied(false);
				hostelBedDBOList.add(hostelBedDBO);
			}
		} 
		if(!Utils.isNullOrEmpty(hostelBedDboMap)) {
			if(hostelBedDboMap.containsKey(Integer.parseInt(dto.getBedNo().getValue()))) {
				HostelBedDBO hostelBedDBO = hostelBedDboMap.get(Integer.parseInt(dto.getBedNo().getValue()));
				hostelBedDBO.setOccupied(true);
				hostelBedDBO.setModifiedUsersId(Integer.parseInt(userId));
				dbo.setHostelBedDBO(hostelBedDBO);
				hostelBedDboMap.remove(Integer.parseInt(dto.getBedNo().getValue()));
			}
		}
		if(!Utils.isNullOrEmpty(hostelBedDBOList)) {
//			checkInTransaction.update1(hostelBedDBOList);
			data.addAll(hostelBedDBOList);
		}
		data.addAll(statusList);
		data.add(dbo);
		return data;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> duplicateCheck(String regNo, String applnNo, String name) {
		ApiResult apiResult =  new ApiResult();
		HostelAdmissionsDBO dbo = checkInTransaction.duplicateCheckdata(regNo, applnNo, name);
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew();
		Integer academicYearId = currYear.getId();
		if(!Utils.isNullOrEmpty(dbo)) {
			if(dbo.getErpAcademicYearDBO().getId() == academicYearId ) {
				apiResult.setFailureMessage("This student is already checked-in for the " +dbo.getHostelDBO().getHostelName()+ " in the " +dbo.getErpAcademicYearDBO().getAcademicYearName()
						+" Kindly use the edit option to update");
			} else if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO().getId() < academicYearId)) {
				apiResult.setFailureMessage("This student is already checked-in for the " +dbo.getHostelDBO().getHostelName()+ " in the " +dbo.getErpAcademicYearDBO().getAcademicYearName()
						+ " Kindly check out the student from that year to Check-In this year");
			} 
		} else {
			apiResult.setSuccess(true);		
		}
		return Mono.just(apiResult);	
	}

}
