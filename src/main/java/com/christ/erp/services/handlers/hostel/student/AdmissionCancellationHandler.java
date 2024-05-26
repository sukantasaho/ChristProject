package com.christ.erp.services.handlers.hostel.student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusLogDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.student.AdmissionCancellationTransaction;
import reactor.core.publisher.Mono;

@Service
public class AdmissionCancellationHandler {

	@Autowired
	AdmissionCancellationTransaction admissionCancellationTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	public Mono<HostelAdmissionsDTO> getStudentHostelData(String hostelApplnNo, String regNo, String name, String yearId) {
		HostelAdmissionsDBO dbo = admissionCancellationTransaction.getStudentHostelData(hostelApplnNo, regNo, name, yearId);
		return convertDboToDto(dbo);
	}

	private Mono<HostelAdmissionsDTO> convertDboToDto(HostelAdmissionsDBO dbo) {
		HostelAdmissionsDTO hostelAdmissionsDTO = new HostelAdmissionsDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			hostelAdmissionsDTO.setId(dbo.getId());
			if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
				hostelAdmissionsDTO.setErpAcademicYearDTO(new SelectDTO()); 
				hostelAdmissionsDTO.getErpAcademicYearDTO().setValue(String.valueOf(dbo.getErpAcademicYearDBO().getId()));
				hostelAdmissionsDTO.getErpAcademicYearDTO().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
			}
			hostelAdmissionsDTO.setHostel(new SelectDTO());
			if(!Utils.isNullOrEmpty(dbo.getHostelDBO())) {
				hostelAdmissionsDTO.getHostel().setValue(String.valueOf(dbo.getHostelDBO().getId()));
				hostelAdmissionsDTO.getHostel().setLabel(dbo.getHostelDBO().getHostelName());
			}
			hostelAdmissionsDTO.setHostelApplicationDTO(new HostelApplicationDTO());
			if(!Utils.isNullOrEmpty(dbo.getHostelApplicationDBO())) {
				hostelAdmissionsDTO.getHostelApplicationDTO().setId(String.valueOf(dbo.getHostelApplicationDBO().getId()));
				if(!Utils.isNullOrEmpty(dbo.getHostelApplicationDBO().getApplicationNo())) {
					hostelAdmissionsDTO.getHostelApplicationDTO().setHostelApplicationNo(String.valueOf(dbo.getHostelApplicationDBO().getApplicationNo()));
				}
			}
			hostelAdmissionsDTO.setStudentDTO(new StudentDTO());
			if(!Utils.isNullOrEmpty(dbo.getHostelApplicationDBO().getStudentDBO())) {
				hostelAdmissionsDTO.getStudentDTO().setRegisterNo(dbo.getHostelApplicationDBO().getStudentDBO().getRegisterNo());
				hostelAdmissionsDTO.getStudentDTO().setStudentName(dbo.getHostelApplicationDBO().getStudentDBO().getStudentName());
			}
			hostelAdmissionsDTO.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
			if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO())) {
				hostelAdmissionsDTO.getStudentApplnEntriesDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
				if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO())) {
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setProgramName(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
					hostelAdmissionsDTO.getStudentApplnEntriesDTO().getErpCampusProgrammeMappingId().setCampusName(dbo.getStudentApplnEntriesDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
				}
			}
			if(!Utils.isNullOrEmpty(dbo.getErpStatusDBO())) {
				hostelAdmissionsDTO.setErpStatus(dbo.getErpStatusDBO().getStatusDisplayText());
			}
			if(!Utils.isNullOrEmpty(dbo.getHostelRoomTypeDBO())) {
				hostelAdmissionsDTO.setHostelRoomTypeDTO(new SelectDTO());
				hostelAdmissionsDTO.getHostelRoomTypeDTO().setValue(String.valueOf(dbo.getHostelRoomTypeDBO().getId()));
				hostelAdmissionsDTO.getHostelRoomTypeDTO().setLabel(dbo.getHostelRoomTypeDBO().getRoomType());
			}
			if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO())) {
				if(!Utils.isNullOrEmpty(dbo.getHostelBedDBO().getHostelRoomsDBO())) {
					hostelAdmissionsDTO.setRoomNo(new SelectDTO());
					hostelAdmissionsDTO.getRoomNo().setValue(String.valueOf(dbo.getHostelBedDBO().getHostelRoomsDBO().getId()));
					hostelAdmissionsDTO.getRoomNo().setLabel(dbo.getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
				}
			}
		}
		return !Utils.isNullOrEmpty(dbo) ? Mono.just(hostelAdmissionsDTO) : Mono.error(new NotFoundException(null));
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelAdmissionsDTO> dto, String userId) {
		return dto.handle((admissionDTO, synchronousSink) -> {
			boolean duplicate = admissionCancellationTransaction.duplicateCheck(admissionDTO);
			if(duplicate) {
				synchronousSink.error(new DuplicateException("This student’s hostel admission is already cancelled"));
			} else {
				synchronousSink.next(admissionDTO);
			}
		}).cast(HostelAdmissionsDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					admissionCancellationTransaction.update(s);

					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo(HostelAdmissionsDTO data, String userId) {
		List<Object> result = new ArrayList<Object>();
		Integer statusId = admissionCancellationTransaction.getStatusId("HOSTEL_CANCELLED");
		Tuple cancelled = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_ADMISSION_CANCELLED");
		HostelApplicationDBO applicationDBO = admissionCancellationTransaction.getApplicantData(data.getHostelApplicationDTO().getId());
		HostelAdmissionsDBO dbo = null;
		if(Utils.isNullOrEmpty(dbo)) {
			if(!Utils.isNullOrEmpty(data.getId())) {
				dbo = admissionCancellationTransaction.edit(data.getId());
				dbo.setModifiedUsersId(Integer.parseInt(userId));
			}
			if(!Utils.isNullOrEmpty(data.getCancelledReason())) {
				dbo.setCancelledReason(data.getCancelledReason());
			}
			if(!Utils.isNullOrEmpty(userId)) {
				dbo.setCancelledByUserId(Integer.parseInt(userId));
			}
			if(!Utils.isNullOrEmpty(statusId)) {	
				dbo.getErpStatusDBO().setId(statusId);
				dbo.setErpCurrentStatusTime(LocalDateTime.now());
			}
			if(!Utils.isNullOrEmpty(cancelled.get("applicant_status_display_text").toString())) {
				ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
				applicant.id =Integer.parseInt(cancelled.get("erp_work_flow_process_id").toString());
				applicationDBO.setHostelApplicantCurrentProcessStatus(applicant);
				applicationDBO.setHostelApplicantStatusTime(LocalDateTime.now());
			}
			if(!Utils.isNullOrEmpty(cancelled.get("application_status_display_text").toString())) {
				ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
				application.id = Integer.parseInt(cancelled.get("erp_work_flow_process_id").toString());
				applicationDBO.setHostelApplicationCurrentProcessStatus(application);
				applicationDBO.setHostelApplicationStatusTime(LocalDateTime.now());
			}
			applicationDBO.setModifiedUsersId(Integer.parseInt(userId));
			ErpStatusLogDBO statusLogDBO = new ErpStatusLogDBO();
			statusLogDBO.setEntryId(data.getId());  
			ErpStatusDBO statusDBO = new ErpStatusDBO();
			statusDBO.setId(statusId);
			statusLogDBO.setErpStatusDBO(statusDBO);
			statusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			statusLogDBO.setRecordStatus('A');
			result.add(statusLogDBO);
		}
		result.add(dbo);
		result.add(applicationDBO);
		return result;
	}
}
