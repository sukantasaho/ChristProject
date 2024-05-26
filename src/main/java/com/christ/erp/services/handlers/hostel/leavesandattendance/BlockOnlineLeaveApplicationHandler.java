package com.christ.erp.services.handlers.hostel.leavesandattendance;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelBlockLeavesDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultApprovalDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.leavesandattendance.HostelBlockLeavesDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.transactions.hostel.leavesandattendance.BlockOnlineLeaveApplicationTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BlockOnlineLeaveApplicationHandler {

	@Autowired
	BlockOnlineLeaveApplicationTransaction blockOnlineLeaveApplicationTransaction;

	public Flux<HostelBlockLeavesDTO> getStudentToBlock(String hostelId, String userId,String blockId, String blockUnitId,Boolean isUserSpecific,Integer academicYearId,Boolean isToBlock) {
		if(isToBlock) {
			return blockOnlineLeaveApplicationTransaction.getStudentToBlock(hostelId, userId, blockId, blockUnitId, isUserSpecific, academicYearId).flatMapMany(Flux::fromIterable).map(this::convertHostelAdmissionDBOtoHostelBlockLeavesDTO);		

		} else {
			return blockOnlineLeaveApplicationTransaction.getStudentToUnBlock(hostelId, userId, blockId, blockUnitId, isUserSpecific, academicYearId).flatMapMany(Flux::fromIterable).map(this::convertHostelBlockLeavesDBOtoDTO);
		}
	}

	public HostelBlockLeavesDTO convertHostelAdmissionDBOtoHostelBlockLeavesDTO(HostelAdmissionsDBO dbo) {
		var dto = new HostelBlockLeavesDTO();
		dto.setHostelAdmissionsDTO(convertHostelAdmissionDBOtoDTO(dbo));
		dto.setErpAcademicYearDTO(new ErpAcademicYearDTO());
		dto.getErpAcademicYearDTO().setId(dbo.getErpAcademicYearDBO().getId());
		return dto;
	}

	private HostelBlockLeavesDTO convertHostelBlockLeavesDBOtoDTO(HostelBlockLeavesDBO dbo) {
		var dto = new HostelBlockLeavesDTO();
		BeanUtils.copyProperties(dbo, dto);
		dto.setHostelAdmissionsDTO(convertHostelAdmissionDBOtoDTO(dbo.getHostelAdmissionsDBO()));
		dto.setErpAcademicYearDTO(new ErpAcademicYearDTO());
		dto.getErpAcademicYearDTO().setId(dbo.getId());
		return dto;
	}

	public HostelAdmissionsDTO convertHostelAdmissionDBOtoDTO(HostelAdmissionsDBO dbo) { 
		HostelAdmissionsDTO dto = new HostelAdmissionsDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setId(dbo.getId());
			dto.setHostelApplicationDTO(new HostelApplicationDTO());
			dto.getHostelApplicationDTO().setHostelApplicationNum(dbo.getHostelApplicationDBO().getApplicationNo());
			dto.setStudentDTO(new StudentDTO());
			dto.getStudentDTO().setRegisterNo(dbo.getStudentDBO().getRegisterNo());
			dto.getStudentDTO().setStudentName(dbo.getStudentDBO().getStudentName());
			dto.getStudentDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
			dto.getStudentDTO().getErpCampusProgrammeMappingId().setProgramName(dbo.getStudentDBO().getErpCampusProgrammeMappingId().getErpProgrammeDBO().getProgrammeName());
			dto.setRoomNo(new SelectDTO());
			dto.getRoomNo().setLabel(String.valueOf(dbo.getHostelBedDBO().getHostelRoomsDBO().getId()));
			dto.getRoomNo().setLabel(String.valueOf(dbo.getHostelBedDBO().getHostelRoomsDBO().getRoomNo()));			
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> blockOrUnblockStudents(Mono<List<HostelBlockLeavesDTO>> data, String userId,Boolean isBlock) {
		return data.map(s->{
			var apiResult = new ApiResult<FinalResultApprovalDTO>();
			if(isBlock) {			
				blockOnlineLeaveApplicationTransaction.blockStudents(convertDtoToDbo(s,userId));
				apiResult.setSuccess(true);
			} else {
				apiResult.setSuccess(blockOnlineLeaveApplicationTransaction.unblockStudents(s.stream().map(p->p.getId()).collect(Collectors.toList()), userId));				
			}
			return apiResult;
		});
	}

	private List<HostelBlockLeavesDBO> convertDtoToDbo(List<HostelBlockLeavesDTO> dtoList, String userId) {
		var dboList = new ArrayList<HostelBlockLeavesDBO>();
		dtoList.forEach(s->{
			var dbo = new HostelBlockLeavesDBO();
			BeanUtils.copyProperties(s, dbo);
			dbo.setErpAcademicYearDBO(new ErpAcademicYearDBO());
			dbo.getErpAcademicYearDBO().setId(s.getErpAcademicYearDTO().getId());
			dbo.setHostelAdmissionsDBO(new HostelAdmissionsDBO());
			dbo.getHostelAdmissionsDBO().setId(s.getHostelAdmissionsDTO().getId()); 
			dbo.setCreatedUsersId(Integer.parseInt(userId));
			dbo.setRecordStatus('A');
			dboList.add(dbo);
		});	
		return dboList;
	}
}
