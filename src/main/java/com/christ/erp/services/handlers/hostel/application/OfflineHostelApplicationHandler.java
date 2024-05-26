package com.christ.erp.services.handlers.hostel.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationRoomTypePreferenceDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelPublishApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.common.ErpCampusDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationRoomTypePreferenceDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.application.OfflineHostelApplicationTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@Service
public class OfflineHostelApplicationHandler {

	@Autowired
	private OfflineHostelApplicationTransaction offlineHostelApplicationTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	public Flux<HostelApplicationDTO> getGridData(String yearId) {
		return offlineHostelApplicationTransaction.getGridData(yearId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto6);
	}

	public HostelApplicationDTO convertDboToDto6(HostelApplicationDBO dbo) {
		HostelApplicationDTO dto = new HostelApplicationDTO();
		dto.setId(String.valueOf(dbo.getId()));
		if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
			dto.setAcademicYear(new LookupItemDTO());
			dto.getAcademicYear().setValue(String.valueOf(dbo.getErpAcademicYearDBO().getId()));
			dto.getAcademicYear().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
		}
		if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO())) {
			dto.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
			dto.getStudentApplnEntriesDTO().setId(dbo.getStudentApplnEntriesDBO().getId());
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicantName())) {
				dto.getStudentApplnEntriesDTO().setApplicantName(dbo.getStudentApplnEntriesDBO().getApplicantName());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicationNo())) {
				dto.getStudentApplnEntriesDTO().setApplicationNumber(dbo.getStudentApplnEntriesDBO().getApplicationNo().toString());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpGenderDBO())) {
				dto.getStudentApplnEntriesDTO().setErpGender(dbo.getStudentApplnEntriesDBO().getErpGenderDBO().getErpGenderId());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO())) {
				dto.getStudentApplnEntriesDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
				dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setId(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getId());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName())) {
				dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setCampusDTO(new ErpCampusDTO());
				dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().getCampusDTO().setCampusName(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName())) {
				dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setErpProgrammeDTO(new ErpProgrammeDTO());
				dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().getErpProgrammeDTO().setProgrammeName(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			}
		}
		if(!Utils.isNullOrEmpty(dbo.getStudentDBO())) {
			dto.setStudent(new StudentDTO());
			dto.getStudent().setId(dbo.getStudentDBO().getId());
			if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getRegisterNo())) {
				dto.getStudent().setRegisterNo(dbo.getStudentDBO().getRegisterNo());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentName())) {
				dto.getStudent().setStudentName(dbo.getStudentDBO().getStudentName());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getErpGenderDBO())) {
				dto.getStudent().setErpGender(dbo.getStudentDBO().getErpGenderDBO().getErpGenderId());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getErpCampusProgrammeMappingId())) {
				dto.getStudent().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
				dto.getStudent().getErpCampusProgrammeMappingId().setId(dbo.getStudentDBO().getErpCampusProgrammeMappingId().getId());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getErpCampusProgrammeMappingId().getErpCampusDBO().getCampusName())) {
				dto.getStudent().getErpCampusProgrammeMappingId().setCampusName (dbo.getStudentDBO().getErpCampusProgrammeMappingId().getErpCampusDBO().getCampusName());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getErpCampusProgrammeMappingId().getErpProgrammeDBO().getProgrammeName())) {
				dto.getStudent().getErpCampusProgrammeMappingId().setProgramName(dbo.getStudentDBO().getErpCampusProgrammeMappingId().getErpProgrammeDBO().getProgrammeName());
			}
			if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getAcaClassDBO())) {
				dto.getStudent().setAcaClassDTO(dbo.getStudentDBO().getAcaClassDBO().getClassName());
			}
		}
		if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
			dto.setHostelApplicationNo(dbo.getApplicationNo().toString());
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelDBO())) {
			dto.setHostel(new LookupItemDTO());
			dto.getHostel().setValue(dbo.getHostelDBO().getId().toString());
			dto.getHostel().setLabel(dbo.getHostelDBO().getHostelName());
		}
		if(!Utils.isNullOrEmpty(dbo.getApplicationPrefix())) {
			dto.setApplicationPrefix(dbo.getApplicationPrefix());
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelApplicantCurrentProcessStatus())) {
			dto.setWorkFlowStatus(new SelectDTO());
			dto.getWorkFlowStatus().setValue(dbo.getHostelApplicantCurrentProcessStatus().getId().toString());
			dto.getWorkFlowStatus().setLabel(dbo.getHostelApplicantCurrentProcessStatus().getApplicantStatusDisplayText());
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelApplicationRoomTypePreferenceDBO())) {
			List<HostelApplicationRoomTypePreferenceDTO> hostelApplicationRoomTypeDTO = new ArrayList<HostelApplicationRoomTypePreferenceDTO>();
			dbo.getHostelApplicationRoomTypePreferenceDBO().forEach(dbos -> {
				HostelApplicationRoomTypePreferenceDTO dto1 = new HostelApplicationRoomTypePreferenceDTO();
				dto1.setId(dbos.getId());
				if(!Utils.isNullOrEmpty(dbos.getHostelRoomTypesDBO())) {
					dto1.setHostelRoomTypesDTO(new SelectDTO());
					dto1.getHostelRoomTypesDTO().setValue(dbos.getHostelRoomTypesDBO().getId().toString());
					dto1.getHostelRoomTypesDTO().setLabel(dbos.getHostelRoomTypesDBO().getRoomType());
				}
				hostelApplicationRoomTypeDTO.add(dto1);
			});
			dto.setHostelApplicationRoomTypePreferenceDTO(hostelApplicationRoomTypeDTO);
		}
		return dto;
	}

	public Mono<ApiResult> saveOrUpdate(Mono<HostelApplicationDTO> dto, String userId) {
		return dto.handle((hostelApplicationDTO, synchronousSink) -> {
			boolean istrue = offlineHostelApplicationTransaction.duplicateCheck(hostelApplicationDTO);
			if(istrue) {
				synchronousSink.error(new DuplicateException("Application is already submitted for this Application No/Reg.No"));
			} else {
				synchronousSink.next(hostelApplicationDTO);
			}
		}).cast(HostelApplicationDTO.class).map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						offlineHostelApplicationTransaction.update(s);
					} else {
						offlineHostelApplicationTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private HostelApplicationDBO convertDtoToDbo(HostelApplicationDTO dto1, String userId) {
		HostelApplicationDBO dbo = new HostelApplicationDBO();
		if(!Utils.isNullOrEmpty(dbo)) {
			if(!Utils.isNullOrEmpty(dto1.getId())) {
				dbo.setId(Integer.parseInt(dto1.getId()));
			}
			if(!Utils.isNullOrEmpty(dto1.getAcademicYear())) {
			dbo.setErpAcademicYearDBO(new ErpAcademicYearDBO());
			dbo.getErpAcademicYearDBO().setId(Integer.parseInt(dto1.getAcademicYear().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto1.getStudentApplnEntriesDTO())) {
				dbo.setStudentApplnEntriesDBO(new StudentApplnEntriesDBO());
				dbo.getStudentApplnEntriesDBO().setId(Integer.parseInt(dto1.getStudentApplnEntriesDTO().getId().toString()));
			}
			if(!Utils.isNullOrEmpty(dto1.getStudent())) {
				dbo.setStudentDBO(new StudentDBO());
				dbo.getStudentDBO().setId(dto1.getStudent().getId());
			}
			dbo.setHostelDBO(new HostelDBO());
			if(!Utils.isNullOrEmpty(dto1.getHostel())) {
				dbo.getHostelDBO().setId(Integer.parseInt(dto1.getHostel().getValue()));
			}
			dbo.setDateOfApplication(LocalDateTime.now());
			if(!Utils.isNullOrEmpty(dto1.getApplicationPrefix())) {
				dbo.setApplicationPrefix(dto1.getApplicationPrefix());
			}
			if(!Utils.isNullOrEmpty(dto1.getHostelApplicationNo())) {
				dbo.setApplicationNo(Integer.parseInt(dto1.getHostelApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(dto1.getWorkFlowStatus()) && dto1.getWorkFlowStatus().getLabel().equalsIgnoreCase("Applied-New")) {
				Tuple applied = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_NEW_APPLIED");
				if(applied.get("applicant_status_display_text")!= null  && !Utils.isNullOrWhitespace(applied.get("applicant_status_display_text").toString())) {
					ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
					applicant.id =Integer.parseInt(applied.get("erp_work_flow_process_id").toString());
					dbo.setHostelApplicantCurrentProcessStatus(applicant);
					dbo.setHostelApplicantStatusTime(LocalDateTime.now());
				}
				if(applied.get("application_status_display_text")!= null  && !Utils.isNullOrWhitespace(applied.get("application_status_display_text").toString())) {
					ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
					application.id = Integer.parseInt(applied.get("erp_work_flow_process_id").toString());
					dbo.setHostelApplicationCurrentProcessStatus(application);
					dbo.setHostelApplicationStatusTime(LocalDateTime.now());
				}
			} else if(!Utils.isNullOrEmpty(dto1.getWorkFlowStatus()) && dto1.getWorkFlowStatus().getLabel().equalsIgnoreCase("Applied-Renewal")) {
				Tuple renewal = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_RENEWAL_APPLIED");
				if(renewal.get("applicant_status_display_text")!= null  && !Utils.isNullOrWhitespace(renewal.get("applicant_status_display_text").toString())) {
					ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
					applicant.id = Integer.parseInt(renewal.get("erp_work_flow_process_id").toString());
					dbo.setHostelApplicantCurrentProcessStatus(applicant);
					dbo.setHostelApplicantStatusTime(LocalDateTime.now());
				}
				if(renewal.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(renewal.get("application_status_display_text").toString())) {
					ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
					application.id = Integer.parseInt(renewal.get("erp_work_flow_process_id").toString());
					dbo.setHostelApplicationCurrentProcessStatus(application);
					dbo.setHostelApplicationStatusTime(LocalDateTime.now());
				}
			} else {
				if(!Utils.isNullOrEmpty(dto1.getWorkFlowStatus()) && dto1.getWorkFlowStatus().getLabel().equalsIgnoreCase("Application Selected")) {
					Tuple selected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_SELECTED");
					if(selected.get("applicant_status_display_text")!= null  && !Utils.isNullOrWhitespace(selected.get("applicant_status_display_text").toString())) {
						ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
						applicant.id = Integer.parseInt(selected.get("erp_work_flow_process_id").toString());
						dbo.setHostelApplicantCurrentProcessStatus(applicant);
						dbo.setHostelApplicantStatusTime(LocalDateTime.now());
					}
					if(selected.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(selected.get("application_status_display_text").toString())) {
						ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
						application.id = Integer.parseInt(selected.get("erp_work_flow_process_id").toString());
						dbo.setHostelApplicationCurrentProcessStatus(application);
						dbo.setHostelApplicationStatusTime(LocalDateTime.now());
					}
				}
			}
			dbo.setIsOffline(Boolean.valueOf(true));
			dbo.setCreatedUsersId(Integer.parseInt(userId));
			if(!Utils.isNullOrEmpty(dto1.getId())) {
				dbo.setModifiedUsersId(Integer.parseInt(userId));
			}
			dbo.setRecordStatus('A');			
			Set<HostelApplicationRoomTypePreferenceDBO> roomDbo = new HashSet<HostelApplicationRoomTypePreferenceDBO>();  
			if(!Utils.isNullOrEmpty(dto1.getHostelApplicationRoomTypePreferenceDTO())) {
				dto1.getHostelApplicationRoomTypePreferenceDTO().forEach(dtos-> {
					HostelApplicationRoomTypePreferenceDBO detailsDBO = new HostelApplicationRoomTypePreferenceDBO();
					detailsDBO.setId(dtos.getId());
					detailsDBO.setHostelApplicationDBO(dbo);
					if(!Utils.isNullOrEmpty(dtos.getHostelRoomTypesDTO())) {
						detailsDBO.setHostelRoomTypesDBO(new HostelRoomTypeDBO());
						detailsDBO.getHostelRoomTypesDBO().setId(Integer.parseInt(dtos.getHostelRoomTypesDTO().getValue()));
					}
					if(!Utils.isNullOrEmpty(dtos.getPreferenceOrder())) {
						detailsDBO.setPreferenceOrder(dtos.getPreferenceOrder());
					}
					detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
					if(!Utils.isNullOrEmpty(dtos.getId())) {
						detailsDBO.setModifiedUsersId(Integer.parseInt(userId));
					}
					detailsDBO.setRecordStatus('A');
					roomDbo.add(detailsDBO);
				});
				dbo.setHostelApplicationRoomTypePreferenceDBO(roomDbo);
			}
		}
		return dbo;
	}

	public Mono<HostelApplicationDTO> edit(int id) {
		return offlineHostelApplicationTransaction.edit(id).map(this::convertDboToDto6);
	}

	public Mono<ApiResult> checkIsStudent(String registerNo) {
		ApiResult apiResult =  new ApiResult();
		boolean result = offlineHostelApplicationTransaction.checkIsStudent(registerNo);
		if(result == false) {
			apiResult.setFailureMessage("Selection to the hostel can be done only once the admission process is completed");	
		} else {
			apiResult.setSuccess(true);		
		}
		return Mono.just(apiResult);	
	}

	public Mono<ApiResult> delete(int id, String userId) {
		return offlineHostelApplicationTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Mono<HostelApplicationDTO> getStatusByHostel(String registerNo, String yearId, String hostelId, String applicationNo) {
		HostelApplicationDTO dto = new HostelApplicationDTO();
		boolean result = offlineHostelApplicationTransaction.getStatusByHostel(registerNo, yearId, hostelId, applicationNo);
		dto.setWorkFlowStatus(new SelectDTO());
		if(result == false) {
			dto.getWorkFlowStatus().setLabel("Applied-New");
		} else if(result == true) {
			dto.getWorkFlowStatus().setLabel("Applied-Renewal");
		} else {
			dto.getWorkFlowStatus().setLabel("Applied-New");
		}	
		return Mono.just(dto);
	}

	public Mono<HostelApplicationDTO> getHostelDataByRegNoOrApplnNo(String yearId, String registerNo, String applicationNo) {
		Mono<List<Tuple>> tuple = null;
		if(!Utils.isNullOrEmpty(registerNo)) {
			tuple =	offlineHostelApplicationTransaction.getHostelDataByRegNo(yearId, registerNo);
		} else {
			tuple = offlineHostelApplicationTransaction.getHostelDataByApplnNo(yearId, applicationNo);
		}
		return tuple.map(s -> convertTupleToDto(s));
	}

	private HostelApplicationDTO convertTupleToDto(List<Tuple> s) {
		HostelApplicationDTO dto = new HostelApplicationDTO();
		s.forEach(dbos -> {
			if(!Utils.isNullOrEmpty(dbos.get("register_no"))) {
				dto.setStudent(new StudentDTO());
				if(!Utils.isNullOrEmpty(dbos.get("student_id"))) {			
					dto.getStudent().setId(Integer.parseInt(dbos.get("student_id").toString()));
				}
				if(!Utils.isNullOrEmpty(dbos.get("register_no"))) {
					dto.getStudent().setRegisterNo(dbos.get("register_no").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("student_name"))) {
					dto.getStudent().setStudentName(dbos.get("student_name").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("class_name"))) {
					dto.getStudent().setAcaClassDTO(dbos.get("class_name").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("erp_gender_id"))) {
					dto.getStudent().setErpGender(Integer.parseInt(dbos.get("erp_gender_id").toString()));
				}
				if(!Utils.isNullOrEmpty(dbos.get("admitted_year_id"))) {
					dto.getStudent().setAdmittedYearId(new SelectDTO());
					dto.getStudent().getAdmittedYearId().setValue(String.valueOf(dbos.get("admitted_year_id")));
				}
				if(!Utils.isNullOrEmpty(dbos.get("erp_campus_programme_mapping_id"))) {
					dto.getStudent().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());		
					dto.getStudent().getErpCampusProgrammeMappingId().id = Integer.parseInt(dbos.get("erp_campus_programme_mapping_id").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("campus_name"))) {
					dto.getStudent().getErpCampusProgrammeMappingId().setCampusName(dbos.get("campus_name").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("programme_name"))) {
					dto.getStudent().getErpCampusProgrammeMappingId().setProgramName(dbos.get("programme_name").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("hostel_id"))) {
					dto.getStudent().setAdmissionDTO(new HostelAdmissionsDTO());
					dto.getStudent().getAdmissionDTO().setHostelDTO(new HostelDTO());
					dto.getStudent().getAdmissionDTO().getHostelDTO().setId(String.valueOf(dbos.get("hostel_id").toString()));
					if(!Utils.isNullOrEmpty(dbos.get("hostel_name"))) {
						dto.getStudent().getAdmissionDTO().getHostelDTO().setHostelName(dbos.get("hostel_name").toString());
					}
				}
				dto.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
				if(!Utils.isNullOrEmpty(dbos.get("student_appln_entries_id"))) {
					dto.getStudentApplnEntriesDTO().setId(Integer.parseInt(dbos.get("student_appln_entries_id").toString()));
				}
				if(!Utils.isNullOrEmpty(dbos.get("application_no"))) {
					dto.getStudentApplnEntriesDTO().setApplicationNumber(dbos.get("application_no").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("applicant_name"))) {
					dto.getStudentApplnEntriesDTO().setApplicantName(dbos.get("applicant_name").toString());
				}	
			} else {
				dto.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
				dto.getStudentApplnEntriesDTO().setId(Integer.parseInt(dbos.get("student_appln_entries_id").toString()));
				if(!Utils.isNullOrEmpty(dbos.get("application_no"))) {
					dto.getStudentApplnEntriesDTO().setApplicationNumber(dbos.get("application_no").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("applicant_name"))) {
					dto.getStudentApplnEntriesDTO().setApplicantName(dbos.get("applicant_name").toString());
				} 
				if(!Utils.isNullOrEmpty(dbos.get("campus_name"))) {
					dto.getStudentApplnEntriesDTO().setCampusOrLocation(dbos.get("campus_name").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("programme_name"))) {
					dto.getStudentApplnEntriesDTO().setProgramme(dbos.get("programme_name").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("erp_gender_id"))) {
					dto.getStudentApplnEntriesDTO().setErpGender(Integer.parseInt(dbos.get("erp_gender_id").toString()));
				}
				if(!Utils.isNullOrEmpty(dbos.get("erp_campus_programme_mapping_id"))) {
					dto.getStudentApplnEntriesDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());		
					dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().id = Integer.parseInt(dbos.get("erp_campus_programme_mapping_id").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("campus_name"))) {
					dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setCampusName(dbos.get("campus_name").toString());
				}
				if(!Utils.isNullOrEmpty(dbos.get("programme_name"))) {
					dto.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setProgramName(dbos.get("programme_name").toString());
				}
			}
		});
		return dto;
	}

	public Mono<ApiResult> checkPrivilegedUser() {
	    ApiResult result = new ApiResult();
	    result.setSuccess(true);
	    return Mono.just(result);
	}
	
	public Flux<SelectDTO> getHostelOfflinePrefix(String hostelId, String yearId) {
		return commonApiTransaction1.getOfflinePrefix(hostelId, yearId).flatMapMany(Flux::fromIterable).map(this::converthostelDboToDto);
	}

	public SelectDTO converthostelDboToDto(HostelPublishApplicationDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(dbo.getOfflineApplicationPrefix());
			dto.setLabel(dbo.getOfflineApplicationPrefix());
		}
		return dto;
	}

	public Mono<ApiResult> duplicateCheck(String yearId, String registerNo, String applnNo) {
		ApiResult apiResult =  new ApiResult();
		Boolean isDuplicate = offlineHostelApplicationTransaction.duplicateCheck(yearId,registerNo,applnNo);
		if(isDuplicate == true) {
			apiResult.setSuccess(false);
			apiResult.setFailureMessage("This Application is already submitted");	
		} else {
			apiResult.setSuccess(true);		
		}
		return Mono.just(apiResult);	
	}

}