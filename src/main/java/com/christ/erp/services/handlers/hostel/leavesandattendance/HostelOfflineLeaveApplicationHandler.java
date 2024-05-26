package com.christ.erp.services.handlers.hostel.leavesandattendance;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelLeaveApplicationsDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelLeaveApplicationsDocumentDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelLeaveTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.leavesandattendance.HostelLeaveApplicationsDTO;
import com.christ.erp.services.dto.hostel.leavesandattendance.HostelLeaveApplicationsDocumentDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.leavesandattendance.HostelOfflineLeaveApplicationTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HostelOfflineLeaveApplicationHandler {

	@Autowired
	HostelOfflineLeaveApplicationTransaction hostelOfflineLeaveApplicationTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction;

	public Mono<HostelAdmissionsDTO> getStudentDetails(String registerNo,String yearId) {
		HostelAdmissionsDBO data = hostelOfflineLeaveApplicationTransaction.getStudentDetails(registerNo,yearId);
		return this.convertHostelAdmissionsDBOToDTO(data);
	}

	private Mono<HostelAdmissionsDTO> convertHostelAdmissionsDBOToDTO(HostelAdmissionsDBO data) {
		HostelAdmissionsDTO hostelAdmissionsDTO = new HostelAdmissionsDTO();
		if(!Utils.isNullOrEmpty(data)) {
			if(!Utils.isNullOrEmpty(data.getId())) {
				hostelAdmissionsDTO.setId(data.getId());
			}
			hostelAdmissionsDTO.setStudentDTO(new StudentDTO());
			if(!Utils.isNullOrEmpty(data.getStudentDBO())) {
				hostelAdmissionsDTO.getStudentDTO().setStudentName(data.getStudentDBO().getStudentName());
			}
			if(!Utils.isNullOrEmpty(data.getHostelDBO())) {
				hostelAdmissionsDTO.setHostel(new SelectDTO());
				hostelAdmissionsDTO.getHostel().setLabel(data.getHostelDBO().getHostelName());
			}
			if(!Utils.isNullOrEmpty(data.getHostelBedDBO())) {
				if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO())) {
					hostelAdmissionsDTO.setRoomNo(new SelectDTO());
					hostelAdmissionsDTO.getRoomNo().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
					if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelRoomTypeDBO())) {
						hostelAdmissionsDTO.setRoomtype(new SelectDTO());
						hostelAdmissionsDTO.getRoomtype().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getHostelRoomTypeDBO().getRoomType());	
					}
					if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO())) {
						if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO())) {
							if(!Utils.isNullOrEmpty(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO())) {
								hostelAdmissionsDTO.setBlock(new SelectDTO());
								hostelAdmissionsDTO.getBlock().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName());
							}
							hostelAdmissionsDTO.setUnit(new SelectDTO());
							hostelAdmissionsDTO.getUnit().setLabel(data.getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit());
						}
					}
				}
			}
		}
		return Mono.just(hostelAdmissionsDTO);
	}

	public Flux<HostelLeaveApplicationsDTO> getGridData(String academicYear,String hostelId,String blockId,String unitId){
		List<HostelLeaveApplicationsDBO> list = hostelOfflineLeaveApplicationTransaction.getGridData(academicYear,hostelId,blockId,unitId);
		return this.convetDBOToDTO(list);
	}

	private Flux<HostelLeaveApplicationsDTO> convetDBOToDTO(List<HostelLeaveApplicationsDBO> list) {
		List<HostelLeaveApplicationsDTO> hostelLeaveApplicationsDTOList = new ArrayList<HostelLeaveApplicationsDTO>();
		List<HostelLeaveApplicationsDocumentDTO> hostelLeaveApplicationsDocumentDTOList = new ArrayList<HostelLeaveApplicationsDocumentDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				HostelLeaveApplicationsDTO hostelLeaveApplicationsDTO = new HostelLeaveApplicationsDTO();
				if(!Utils.isNullOrEmpty(data.getId())) {
					hostelLeaveApplicationsDTO.setId(data.getId());
				}
				hostelLeaveApplicationsDTO.setHostelAdmissionsDTO(new HostelAdmissionsDTO());
				hostelLeaveApplicationsDTO.getHostelAdmissionsDTO().setStudentDTO(new StudentDTO());
				if(!Utils.isNullOrEmpty(data.getHostelAdmissionDBO())) {
					if(!Utils.isNullOrEmpty(data.getHostelAdmissionDBO().getStudentDBO())) {
						hostelLeaveApplicationsDTO.getHostelAdmissionsDTO().getStudentDTO().setStudentName(data.getHostelAdmissionDBO().getStudentDBO().getStudentName());
						hostelLeaveApplicationsDTO.getHostelAdmissionsDTO().getStudentDTO().setRegisterNo(data.getHostelAdmissionDBO().getStudentDBO().getRegisterNo());
						if(!Utils.isNullOrEmpty(data.getHostelAdmissionDBO().getStudentDBO().getAcaClassDBO())) {
							hostelLeaveApplicationsDTO.getHostelAdmissionsDTO().getStudentDTO().setAcaClassDTO(data.getHostelAdmissionDBO().getStudentDBO().getAcaClassDBO().getClassName());
						}
					}
					if(!Utils.isNullOrEmpty(data.getHostelAdmissionDBO().getHostelBedDBO())) {
						if(!Utils.isNullOrEmpty(data.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO())) {
							hostelLeaveApplicationsDTO.getHostelAdmissionsDTO().setRoomNo(new SelectDTO());
							hostelLeaveApplicationsDTO.getHostelAdmissionsDTO().getRoomNo().setLabel(data.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
						}
					}
				}
				if(!Utils.isNullOrEmpty(data.getHostelLeaveTypeDBO())) {
					hostelLeaveApplicationsDTO.setHostelLeaveType(new SelectDTO());
					hostelLeaveApplicationsDTO.getHostelLeaveType().setValue(String.valueOf(data.getHostelLeaveTypeDBO().getId()));
					hostelLeaveApplicationsDTO.getHostelLeaveType().setLabel(data.getHostelLeaveTypeDBO().getLeaveTypeName());
				}
				if(!Utils.isNullOrEmpty(data.getRequestType())) {
					hostelLeaveApplicationsDTO.setRequestType(data.getRequestType());
				}
				if(!Utils.isNullOrEmpty(data.getLeaveFromDate())) {
					hostelLeaveApplicationsDTO.setLeaveFromDate(data.getLeaveFromDate());
				}
				if(!Utils.isNullOrEmpty(data.getLeaveFromDateSession())) {
					hostelLeaveApplicationsDTO.setLeaveFromDateSession(data.getLeaveFromDateSession());
				}
				if(!Utils.isNullOrEmpty(data.getLeaveToDate())) {
					hostelLeaveApplicationsDTO.setLeaveToDate(data.getLeaveToDate());
				}
				if(!Utils.isNullOrEmpty(data.getLeaveToDateSession())) {
					hostelLeaveApplicationsDTO.setLeaveToDateSession(data.getLeaveToDateSession());
				}
				if(!Utils.isNullOrEmpty(data.isOffline())) {
					if(data.isOffline()) {
						hostelLeaveApplicationsDTO.setApplicationMode("Offline");
						hostelLeaveApplicationsDTO.setOffline(data.isOffline());
					}else {
						hostelLeaveApplicationsDTO.setApplicationMode("Online");
						hostelLeaveApplicationsDTO.setOffline(data.isOffline());
					}
				}
				if(!Utils.isNullOrEmpty(data.getErpApplicationWorkFlowProcessId())) {
					hostelLeaveApplicationsDTO.setApproverStatus(data.getErpApplicationWorkFlowProcessId().getApplicationStatusDisplayText());
				}else {
					hostelLeaveApplicationsDTO.setApproverStatus("HOSTEL_LEAVE_APPLICATION_SUBMISSION");
				}
				if(!Utils.isNullOrEmpty(data.getLeaveReason())) {
					hostelLeaveApplicationsDTO.setLeaveReason(data.getLeaveReason());
				}
				if(!Utils.isNullOrEmpty(data.getHostelLeaveApplicationsDocumentDBOSet())) {
					data.getHostelLeaveApplicationsDocumentDBOSet().forEach(dboDoc -> {
						if(dboDoc.getRecordStatus() == 'A') {
							HostelLeaveApplicationsDocumentDTO hostelLeaveApplicationsDocumentDTO = new HostelLeaveApplicationsDocumentDTO();
							BeanUtils.copyProperties(dboDoc, hostelLeaveApplicationsDocumentDTO);
							hostelLeaveApplicationsDocumentDTOList.add(hostelLeaveApplicationsDocumentDTO);
						}
					});
					hostelLeaveApplicationsDTO.setHostelLeaveApplicationsDocumentDTOList(hostelLeaveApplicationsDocumentDTOList);
				}
				hostelLeaveApplicationsDTOList.add(hostelLeaveApplicationsDTO);
			});
		}
		return Flux.fromIterable(hostelLeaveApplicationsDTOList);
	}

	public Mono<HostelLeaveApplicationsDTO> edit(int id) {
		HostelLeaveApplicationsDBO dbo = hostelOfflineLeaveApplicationTransaction.edit(id);
		return convertEditHostelLeaveApplicationsDboToDto(dbo);
	}

	public Mono<HostelLeaveApplicationsDTO> convertEditHostelLeaveApplicationsDboToDto(HostelLeaveApplicationsDBO dbo) {
		HostelLeaveApplicationsDTO dto = new HostelLeaveApplicationsDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			BeanUtils.copyProperties(dbo, dto);
			if(!Utils.isNullOrEmpty(dbo.getHostelLeaveTypeDBO())) {
				dto.setHostelLeaveType(new SelectDTO());
				dto.getHostelLeaveType().setValue(String.valueOf(dbo.getHostelLeaveTypeDBO().getId()));
				dto.getHostelLeaveType().setLabel(dbo.getHostelLeaveTypeDBO().getLeaveTypeName());
			}
			dto.setHostelAdmissionsDTO(new HostelAdmissionsDTO());
			dto.getHostelAdmissionsDTO().setStudentDTO(new StudentDTO());
			if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO())) {
				dto.getHostelAdmissionsDTO().setId(dbo.getHostelAdmissionDBO().getId());
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getErpAcademicYearDBO())) {
					dto.setAcademicYear(new SelectDTO());
					dto.getAcademicYear().setLabel(dbo.getHostelAdmissionDBO().getErpAcademicYearDBO().getAcademicYearName());
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getStudentDBO())) {
					dto.getHostelAdmissionsDTO().getStudentDTO().setStudentName(dbo.getHostelAdmissionDBO().getStudentDBO().getStudentName());
					dto.getHostelAdmissionsDTO().getStudentDTO().setRegisterNo(dbo.getHostelAdmissionDBO().getStudentDBO().getRegisterNo());
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getHostelBedDBO())) {
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO())) {
						dto.getHostelAdmissionsDTO().setRoomNo(new SelectDTO());
						dto.getHostelAdmissionsDTO().getRoomNo().setLabel(dbo.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
						if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO())) {
							dto.getHostelAdmissionsDTO().setUnit(null);
							if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO())) {
								dto.getHostelAdmissionsDTO().setBlock(new SelectDTO());
								dto.getHostelAdmissionsDTO().getBlock().setLabel(dbo.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName());
								dto.getHostelAdmissionsDTO().setUnit(new SelectDTO());
								dto.getHostelAdmissionsDTO().getUnit().setLabel(dbo.getHostelAdmissionDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit());
							}
						}
					}
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getHostelRoomTypeDBO())) {
					dto.getHostelAdmissionsDTO().setRoomtype(new SelectDTO());
					dto.getHostelAdmissionsDTO().getRoomtype().setLabel(dbo.getHostelAdmissionDBO().getHostelRoomTypeDBO().getRoomType());
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionDBO().getHostelDBO())) {
					dto.getHostelAdmissionsDTO().setHostel(new SelectDTO());
					dto.getHostelAdmissionsDTO().getHostel().setLabel(dbo.getHostelAdmissionDBO().getHostelDBO().getHostelName());
				}	
			}
			dto.setHostelLeaveApplicationsDocumentDTOList(new ArrayList<HostelLeaveApplicationsDocumentDTO>());
			if(!Utils.isNullOrEmpty(dbo.getHostelLeaveApplicationsDocumentDBOSet())) {
				dbo.getHostelLeaveApplicationsDocumentDBOSet().forEach(dbo1 -> {
					if(dbo1.getRecordStatus() == 'A') {
						HostelLeaveApplicationsDocumentDTO dto1 = new HostelLeaveApplicationsDocumentDTO();
						BeanUtils.copyProperties(dbo1, dto1);
						File file = new File(dbo1.getApplicationDocumentsUrl());
						String fileName = new File(dbo1.getApplicationDocumentsUrl()).getName();
						if(file.exists() && !file.isDirectory()) { 
							dto1.setExtension(dbo1.getApplicationDocumentsUrl().substring(dbo1.getApplicationDocumentsUrl().lastIndexOf(".")+1));
							dto1.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
							dto.getHostelLeaveApplicationsDocumentDTOList().add(dto1);
						}
					}
				});
			}
		}
		return Mono.just(dto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return hostelOfflineLeaveApplicationTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelLeaveApplicationsDTO> dto, String userId) {
		List<String> errorList = new ArrayList<String>();
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
		return dto
				.handle((hostelLeaveApplicationsDTO, synchronousSink) -> {
					List<HostelLeaveApplicationsDBO> dbo1 = hostelOfflineLeaveApplicationTransaction.duplicateCheck(hostelLeaveApplicationsDTO);
					if (!Utils.isNullOrEmpty(dbo1)) {
						dbo1.forEach(data -> {
							errorList.add("Leave is already applied from "+formatter.format(data.getLeaveFromDate()) +" ( "+data.getLeaveFromDateSession()+ " ) "+formatter.format(data.getLeaveToDate()) +" ( "+data.getLeaveToDateSession()+ " ) ");
						});
						synchronousSink.error(new GeneralException(errorList.toString().replace("[", "").replace("]", "")));	
					} else {
						synchronousSink.next(hostelLeaveApplicationsDTO);
					}
				}).cast(HostelLeaveApplicationsDTO.class)
				.map(data -> convertDtoToDbo(data,userId))
				.flatMap(s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						hostelOfflineLeaveApplicationTransaction.update(s);
						
					}else {
						String type = "HOSTEL_LEAVE_APPLICATION_APPROVED";
						Integer submissionId = hostelOfflineLeaveApplicationTransaction.getWorkFlowProcessId(type);
						hostelOfflineLeaveApplicationTransaction.save(s,submissionId);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public HostelLeaveApplicationsDBO convertDtoToDbo(HostelLeaveApplicationsDTO dto, String userId) {
		HostelLeaveApplicationsDBO dbo = null;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo = hostelOfflineLeaveApplicationTransaction.edit(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}else {
			dbo = new HostelLeaveApplicationsDBO();
			dbo.setApplicantStatusLogTime(LocalDateTime.now());
			dbo.setApplicationStatusLogTime(LocalDateTime.now());
			dbo.setCreatedUsersId(Integer.parseInt(userId));
			dbo.setOffline(true);
			dbo.setRecordStatus('A');
		}
		if(!Utils.isNullOrEmpty(dto.getHostelAdmissionsDTO())) {
			HostelAdmissionsDBO hostelAdmissionsDBO = new HostelAdmissionsDBO();
			if(!Utils.isNullOrEmpty(dto.getHostelAdmissionsDTO().getId())) {
				hostelAdmissionsDBO.setId(dto.getHostelAdmissionsDTO().getId());
				dbo.setHostelAdmissionDBO(hostelAdmissionsDBO);
			}
		}
		if(!Utils.isNullOrEmpty(dto.getHostelLeaveType())) {
			dbo.setHostelLeaveTypeDBO(new HostelLeaveTypeDBO());
			dbo.getHostelLeaveTypeDBO().setId(Integer.parseInt(dto.getHostelLeaveType().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getLeaveFromDate())) {
			dbo.setLeaveFromDate(dto.getLeaveFromDate());	
		}
		if(!Utils.isNullOrEmpty(dto.getLeaveFromDateSession())) {
			dbo.setLeaveFromDateSession(dto.getLeaveFromDateSession());
		}
		if(!Utils.isNullOrEmpty(dto.getLeaveToDate())) {
			dbo.setLeaveToDate(dto.getLeaveToDate());
		}
		if(!Utils.isNullOrEmpty(dto.getLeaveToDateSession())) {
			dbo.setLeaveToDateSession(dto.getLeaveToDateSession());
		}
		if(!Utils.isNullOrEmpty(dto.getRequestType())) {
			dbo.setRequestType(dto.getRequestType());
		}
		if(!Utils.isNullOrEmpty(dto.getLeaveReason())) {
			dbo.setLeaveReason(dto.getLeaveReason());
		}	
		if(Utils.isNullOrEmpty(dbo.getErpApplicantWorkFlowProcessId())) {
			String type = "HOSTEL_LEAVE_APPLICATION_SUBMISSION";
			Integer submissionId = hostelOfflineLeaveApplicationTransaction.getWorkFlowProcessId(type);
			dbo.setErpApplicantWorkFlowProcessId(new ErpWorkFlowProcessDBO());
			dbo.getErpApplicantWorkFlowProcessId().setId(submissionId);
			String type1 = "HOSTEL_LEAVE_APPLICATION_APPROVED";
			Integer approverId = hostelOfflineLeaveApplicationTransaction.getWorkFlowProcessId(type1);
			dbo.setErpApplicationWorkFlowProcessId(new ErpWorkFlowProcessDBO());
			dbo.getErpApplicationWorkFlowProcessId().setId(approverId);
		}
		Set<HostelLeaveApplicationsDocumentDBO> existSubSet = !Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(dbo.getHostelLeaveApplicationsDocumentDBOSet()) ? dbo.getHostelLeaveApplicationsDocumentDBOSet() : null;
		Map<Integer,HostelLeaveApplicationsDocumentDBO> hostelLeaveApplicationsDocumentDBOMap = new HashMap<Integer,HostelLeaveApplicationsDocumentDBO>();
		if(!Utils.isNullOrEmpty(existSubSet)) {
			existSubSet.forEach(dbo1 -> {
				if(dbo1.getRecordStatus() == 'A') {
					hostelLeaveApplicationsDocumentDBOMap.put(dbo1.getId(), dbo1);
				}
			});
		}
		Set<HostelLeaveApplicationsDocumentDBO> hostelLeaveApplicationsDocumentDBOSet = new HashSet<HostelLeaveApplicationsDocumentDBO>();
		if(!Utils.isNullOrEmpty(dto.getHostelLeaveApplicationsDocumentDTOList())) {
			HostelLeaveApplicationsDBO hostelLeaveApplicationsDBO = dbo;
			dto.getHostelLeaveApplicationsDocumentDTOList().forEach(dto1 -> {
				HostelLeaveApplicationsDocumentDBO hostelLeaveApplicationsDocumentDBO = null;
				if(hostelLeaveApplicationsDocumentDBOMap.containsKey(dto1.getId())) {
					hostelLeaveApplicationsDocumentDBO = hostelLeaveApplicationsDocumentDBOMap.get(dto1.getId());
					hostelLeaveApplicationsDocumentDBO.setModifiedUsersId(Integer.parseInt(userId));
					hostelLeaveApplicationsDocumentDBOMap.remove(dto1.getId());
				}else {
					hostelLeaveApplicationsDocumentDBO = new HostelLeaveApplicationsDocumentDBO();
					hostelLeaveApplicationsDocumentDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				hostelLeaveApplicationsDocumentDBO.setRecordStatus('A');
				if(!Utils.isNullOrEmpty(dto1.getFileName()) && !Utils.isNullOrEmpty(dto1.getExtension())) {
					File file = new File("ImageUpload//"+dto1.getFileName()+"."+dto1.getExtension());
					hostelLeaveApplicationsDocumentDBO.setApplicationDocumentsUrl(file.getAbsolutePath());
				}
				hostelLeaveApplicationsDocumentDBO.setHostelLeaveApplicationsDBO(hostelLeaveApplicationsDBO);
				hostelLeaveApplicationsDocumentDBOSet.add(hostelLeaveApplicationsDocumentDBO);
			});
		}
		if(!Utils.isNullOrEmpty(hostelLeaveApplicationsDocumentDBOMap)) {
			hostelLeaveApplicationsDocumentDBOMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				hostelLeaveApplicationsDocumentDBOSet.add(value);
			});
		}
		dbo.setHostelLeaveApplicationsDocumentDBOSet(hostelLeaveApplicationsDocumentDBOSet);
		return dbo;
	}
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> wardenApprove(Mono<HostelLeaveApplicationsDTO> dto, String userId) {
		return dto
				.map(data -> convertWardenApprove(data,userId))
				.flatMap(s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						hostelOfflineLeaveApplicationTransaction.update1(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}
	
	public HostelLeaveApplicationsDBO convertWardenApprove(HostelLeaveApplicationsDTO dto, String userId) {
		HostelLeaveApplicationsDBO dbo = hostelOfflineLeaveApplicationTransaction.edit(dto.getId());
		dbo.setModifiedUsersId(Integer.parseInt(userId));
		Integer notificstionId = null;
		if(!Utils.isNullOrEmpty(dto.getWardenApproval())) {
			if(dto.getWardenApproval().equalsIgnoreCase("Approve")) {
				String type1 = "HOSTEL_LEAVE_APPLICATION_APPROVED";
				notificstionId = hostelOfflineLeaveApplicationTransaction.getWorkFlowProcessId(type1);
				ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
				erpWorkFlowProcessDBO.setId(notificstionId);
				dbo.setErpApplicationWorkFlowProcessId(erpWorkFlowProcessDBO);
				dbo.setApplicationStatusLogTime(LocalDateTime.now());
			}
			if(dto.getWardenApproval().equalsIgnoreCase("Reject")) {
				String type1 = "HOSTEL_LEAVE_APPLICATION_REJECTED";
				notificstionId = hostelOfflineLeaveApplicationTransaction.getWorkFlowProcessId(type1);
				ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
				erpWorkFlowProcessDBO.setId(notificstionId);
				dbo.setErpApplicationWorkFlowProcessId(erpWorkFlowProcessDBO);
				dbo.setApplicationStatusLogTime(LocalDateTime.now());
			}
		}
		return dbo;
	}	
}