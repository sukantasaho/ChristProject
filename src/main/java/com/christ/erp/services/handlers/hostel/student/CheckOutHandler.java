package com.christ.erp.services.handlers.hostel.student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusLogDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsFacilityDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFacilitySettingDBO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsFacilityDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.student.CheckOutTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CheckOutHandler {
	@Autowired
	CheckOutTransaction checkOutTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public Flux<HostelAdmissionsDTO> getGridData(String academicYearId, String hostelId) {
		List<HostelAdmissionsDBO> hostelAdmissionsDBOList = checkOutTransaction.getGridData(academicYearId,hostelId);
		return this.convertDboToDto(hostelAdmissionsDBOList);
	}

	private Flux<HostelAdmissionsDTO> convertDboToDto(List<HostelAdmissionsDBO> hostelAdmissionsDBOList) {
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
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getAllottedHostelRoomTypeDBO())) {
					hostelAdmissionsDTO.getHostelApplicationDTO().setHostelRoomType(data.getHostelApplicationDBO().getAllottedHostelRoomTypeDBO().getRoomType());
				}
				if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO())) {
					if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getId())) {
						hostelAdmissionsDTO.setRoomNo(new SelectDTO());
						hostelAdmissionsDTO.getRoomNo().setValue(String.valueOf(data.getHostelBedDBO().getHostelRoomsDBO().getId()));
						hostelAdmissionsDTO.getRoomNo().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
					}
				}
				hostelAdmissionsDTO.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicantName())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicantName(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicantName());
				}
				if(!Utils.isNullOrEmpty(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicationNumber(String.valueOf(data.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo()));
				}
				hostelAdmissionsDTO.setStudentDTO(new StudentDTO());
				if(!Utils.isNullOrEmpty(data.getStudentDBO().getRegisterNo())) {
					hostelAdmissionsDTO.getStudentDTO().setRegisterNo(data.getStudentDBO().getRegisterNo());
				}
				if(!Utils.isNullOrEmpty(data.getStudentDBO().getStudentName())) {
					hostelAdmissionsDTO.getStudentDTO().setStudentName(data.getStudentDBO().getStudentName());
				}
				if(!Utils.isNullOrEmpty(data.getCheckInDate())) {
					hostelAdmissionsDTO.setCheckInDate(data.getCheckInDate());
				}
				if(!Utils.isNullOrEmpty(data.getCheckOutDate())) {
					hostelAdmissionsDTO.setCheckOutDate(data.getCheckOutDate());
				}
				hostelAdmissionsDTOList.add(hostelAdmissionsDTO);
			});
		}
		return  Flux.fromIterable(hostelAdmissionsDTOList);
	}

	public Mono<HostelAdmissionsDTO> getDataByStudent(String hostelApplicationNo, String regNo) {
		HostelAdmissionsDBO dbo = checkOutTransaction.getDataByStudent(hostelApplicationNo, regNo);
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
		if(!Utils.isNullOrEmpty(dbo.getHostelApplicationDBO().getAllottedHostelRoomTypeDBO())) {
			hostelAdmissionsDTO.getHostelApplicationDTO().setHostelRoomType(dbo.getHostelApplicationDBO().getAllottedHostelRoomTypeDBO().getRoomType());
		}
		hostelAdmissionsDTO.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
		if(!Utils.isNullOrEmpty(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicantName())) {
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicantName(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicantName());
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo())) {
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().setApplicationNumber(String.valueOf(dbo.getHostelApplicationDBO().getStudentApplnEntriesDBO().getApplicationNo()));
		}
		if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName())) {
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
			hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setProgramName(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
		}
		hostelAdmissionsDTO.setStudentDTO(new StudentDTO());
		if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getRegisterNo())) {
			hostelAdmissionsDTO.getStudentDTO().setRegisterNo(dbo.getStudentDBO().getRegisterNo());
		}
		if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentName())) {
			hostelAdmissionsDTO.getStudentDTO().setStudentName(dbo.getStudentDBO().getStudentName());
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
		if(!Utils.isNullOrEmpty(dbo.getCheckInDate())) {
			hostelAdmissionsDTO.setCheckInDate(dbo.getCheckInDate());
		}
		if(!Utils.isNullOrEmpty(dbo.getCheckOutDate())) {
			hostelAdmissionsDTO.setCheckOutDate(dbo.getCheckOutDate());
		}
		if(!Utils.isNullOrEmpty(dbo.getCheckOutRemarks())) {
			hostelAdmissionsDTO.setCheckOutRemarks(dbo.getCheckOutRemarks());
		}
		List<HostelAdmissionsFacilityDTO> facilitiesDto = new ArrayList<HostelAdmissionsFacilityDTO>();
		dbo.getHostelFacilityDBOSet().forEach(subdbo -> {
			if(subdbo.getRecordStatus() == 'A') {
				HostelAdmissionsFacilityDTO dtos = new HostelAdmissionsFacilityDTO();   	
				dtos.setId(subdbo.getId());
				if(!Utils.isNullOrEmpty(subdbo.getFacilityDescription())) {
					dtos.setFacilityDescription(subdbo.getFacilityDescription());
				}
				if(!Utils.isNullOrEmpty(subdbo.getHostelFacilitySettingsDBO())) {
					dtos.setHostelFacilitySettingsDTO(new SelectDTO());
					dtos.getHostelFacilitySettingsDTO().setValue(subdbo.getHostelFacilitySettingsDBO().getId().toString());
					dtos.getHostelFacilitySettingsDTO().setLabel(subdbo.getHostelFacilitySettingsDBO().getFacilityName());
				}

				if(Utils.isNullOrEmpty(subdbo.getVerifiedForCheckout())  || subdbo.getVerifiedForCheckout().equals(false)) {
					dtos.setIsChecked(false);
				} else {
					dtos.setIsChecked(true);   
				}
				facilitiesDto.add(dtos);
			} 
		});
		hostelAdmissionsDTO.setHostelAdmissionsFacilityList(facilitiesDto);
		return Mono.just(hostelAdmissionsDTO);	
	}

	public Mono<HostelAdmissionsDTO> edit(int id) {
		HostelAdmissionsDBO dbo = checkOutTransaction.edit(id);
		return convertDboToDto(dbo);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelAdmissionsDTO> dto,  String userId) {
		return dto.map(data -> convertDtoToDbo(data ,userId))
				.flatMap(s -> {
					checkOutTransaction.update(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo(HostelAdmissionsDTO dto, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<ErpStatusLogDBO> statusList = new ArrayList<ErpStatusLogDBO>();
		Integer statusId = checkOutTransaction.getStatusId("HOSTEL_CHECK_OUT");
		HostelAdmissionsDBO dbo = null;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo = checkOutTransaction.edit(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		if(!Utils.isNullOrEmpty(dto.getCheckOutDate())) {
			dbo.setCheckOutDate(dto.getCheckOutDate());
		}
		dbo.setCheckOutRemarks(!Utils.isNullOrEmpty(dto.getCheckOutRemarks()) ? dto.getCheckOutRemarks(): null);
		Set<HostelAdmissionsFacilityDBO> admissionDbo = new HashSet<HostelAdmissionsFacilityDBO>();  
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
				if(fdto.getIsChecked().equals(true)) {
					facilityDbo.setVerifiedForCheckout(true);
				} else {
					facilityDbo.setVerifiedForCheckout(false);
				}
				facilityDbo.setCreatedUsersId(Integer.parseInt(userId));
				if(!Utils.isNullOrEmpty(fdto.getId()))
					facilityDbo.setModifiedUsersId(Integer.parseInt(userId));
				facilityDbo.setRecordStatus('A');
				admissionDbo.add(facilityDbo);
			});
			dbo.setHostelFacilityDBOSet(admissionDbo);
		}
		if(!Utils.isNullOrEmpty(statusId)) {
			if(statusId != dbo.getErpStatusDBO().getId()) {
				dbo.getErpStatusDBO().setId(statusId);
				dbo.setErpCurrentStatusTime(LocalDateTime.now());
				ErpStatusLogDBO statusLogDBO = new ErpStatusLogDBO();
				statusLogDBO.setEntryId(dto.getId());  
				ErpStatusDBO statusDBO = new ErpStatusDBO();
				statusDBO.setId(statusId);
				statusLogDBO.setErpStatusDBO(statusDBO);
				statusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
				statusLogDBO.setRecordStatus('A');
				statusList.add(statusLogDBO);
				data.addAll(statusList);
			}
		}
		data.add(dbo);
		return data;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> duplicateCheck(String regNo, String hostelApplnNo) {
		ApiResult apiResult =  new ApiResult();
		HostelAdmissionsDBO dbo = checkOutTransaction.duplicateCheckdata(regNo, hostelApplnNo);
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew();
		Integer academicYearId = currYear.getId();
		if(!Utils.isNullOrEmpty(dbo)) {
			if(dbo.getErpAcademicYearDBO().getId() == academicYearId ) {
				apiResult.setFailureMessage("This student is already Checked-Out for the " +dbo.getHostelDBO().getHostelName()+ " in the " +dbo.getErpAcademicYearDBO().getAcademicYearName()
						+" Kindly use the edit option to update");
			} 
		} else {
			apiResult.setSuccess(true);		
		}
		return Mono.just(apiResult);	
	}
}
