package com.christ.erp.services.handlers.hostel.fineanddisciplinary;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelDisciplinaryActionsDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDisciplinaryActionsTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelDisciplinaryActionsDTO;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelFineEntryDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDisciplinaryActionsTypeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.handlers.hostel.common.CommonHostelHandler;
import com.christ.erp.services.transactions.hostel.common.CommonHostelTransaction;
import com.christ.erp.services.transactions.hostel.fineanddisciplinary.DisciplinaryActionEntryTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DisciplinaryActionEntryHandler {

	@Autowired
	private DisciplinaryActionEntryTransaction disciplinaryActionEntryTransaction;

	@Autowired
	CommonHostelTransaction commonHostelTransaction1;

	@Autowired
	CommonHostelHandler commonHostelHandler1;

	public Flux<HostelDisciplinaryActionsDTO> getGridData(String yearId, String hostelId, String blockId, String unitId) {
		List<HostelDisciplinaryActionsDBO> hostelApplicationDBOList = disciplinaryActionEntryTransaction.getGridData(yearId, hostelId, blockId, unitId);
		return this.convertDboToDto(hostelApplicationDBOList, yearId, hostelId, blockId, unitId);
	}

	private Flux<HostelDisciplinaryActionsDTO> convertDboToDto(List<HostelDisciplinaryActionsDBO> hostelApplicationDBOList, String yearId, String hostelId, String blockId, String unitId) {
		List<HostelDisciplinaryActionsDTO> disciplinaryActionDTOList = new ArrayList<HostelDisciplinaryActionsDTO>();
		hostelApplicationDBOList.forEach(disciplinaryData -> {
			HostelDisciplinaryActionsDTO disciplinaryActionDTO = new HostelDisciplinaryActionsDTO();
			disciplinaryActionDTO.setId(disciplinaryData.getId());
			if(!Utils.isNullOrEmpty(disciplinaryData.getDisciplinaryActionsDate())) {
				disciplinaryActionDTO.setDisciplinaryActionDate(disciplinaryData.getDisciplinaryActionsDate());
			}
			if(!Utils.isNullOrEmpty(disciplinaryData.getRemarks())) {
				disciplinaryActionDTO.setRemarks(disciplinaryData.getRemarks());	
			}
			if(!Utils.isNullOrEmpty(disciplinaryData.getHostelDisciplinaryActionsTypeDBO())) {
				disciplinaryActionDTO.setHostelDisciplinaryActionsTypeDTO(new HostelDisciplinaryActionsTypeDTO());	
				disciplinaryActionDTO.getHostelDisciplinaryActionsTypeDTO().setId(disciplinaryData.getHostelDisciplinaryActionsTypeDBO().getId());
				if(!Utils.isNullOrEmpty(disciplinaryData.getHostelDisciplinaryActionsTypeDBO().getHostelDisciplinaryActions())) {
					disciplinaryActionDTO.getHostelDisciplinaryActionsTypeDTO().setHostelDisciplinaryActions(disciplinaryData.getHostelDisciplinaryActionsTypeDBO().getHostelDisciplinaryActions());
				}
				if(!Utils.isNullOrEmpty(disciplinaryData.getHostelDisciplinaryActionsTypeDBO().getFineAmount())) {
					disciplinaryActionDTO.getHostelDisciplinaryActionsTypeDTO().setFineAmount(disciplinaryData.getHostelDisciplinaryActionsTypeDBO().getFineAmount());	
				}
				if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO())) {
					disciplinaryActionDTO.setHostelAdmisionDTO(new HostelAdmissionsDTO());
					if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getId())) {
						disciplinaryActionDTO.getHostelAdmisionDTO().setId(disciplinaryData.getHostelAdmissionsDBO().getId());
					}
					if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelDBO())) {
					disciplinaryActionDTO.getHostelAdmisionDTO().setHostel(new SelectDTO());
					disciplinaryActionDTO.getHostelAdmisionDTO().getHostel().setValue(String.valueOf(disciplinaryData.getHostelAdmissionsDBO().getHostelDBO().getId()));
					disciplinaryActionDTO.getHostelAdmisionDTO().getHostel().setLabel(disciplinaryData.getHostelAdmissionsDBO().getHostelDBO().getHostelName());
					}
					if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO())) {
						disciplinaryActionDTO.getHostelAdmisionDTO().setStudentDTO(new StudentDTO());
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO().getRegisterNo())) {
							disciplinaryActionDTO.getHostelAdmisionDTO().getStudentDTO().setRegisterNo(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO().getRegisterNo());	
						}
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO().getStudentName())) {
							disciplinaryActionDTO.getHostelAdmisionDTO().getStudentDTO().setStudentName(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO().getStudentName());
						}
					}
					if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO())) {
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO().getAcaClassDBO().getClassName())) {
						disciplinaryActionDTO.getHostelAdmisionDTO().getStudentDTO().setAcaClassDTO(disciplinaryData.getHostelAdmissionsDBO().getStudentDBO().getAcaClassDBO().getClassName());
					    }
					}
					if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO())){
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getId())) {
							disciplinaryActionDTO.getHostelAdmisionDTO().setBedNo(new SelectDTO());
							disciplinaryActionDTO.getHostelAdmisionDTO().getBedNo().setValue(String.valueOf(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getId()));
							disciplinaryActionDTO.getHostelAdmisionDTO().getBedNo().setLabel(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getBedNo());
						}
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO())) {
							if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getId())) {
								disciplinaryActionDTO.getHostelAdmisionDTO().setRoomNo(new SelectDTO());
								disciplinaryActionDTO.getHostelAdmisionDTO().getRoomNo().setValue(String.valueOf(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getId()));
								disciplinaryActionDTO.getHostelAdmisionDTO().getRoomNo().setLabel(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
							}
						}
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO())) {
							if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getId())) {
								disciplinaryActionDTO.getHostelAdmisionDTO().setFloorNo(new SelectDTO());
								disciplinaryActionDTO.getHostelAdmisionDTO().getFloorNo().setValue(String.valueOf(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getId()));
								disciplinaryActionDTO.getHostelAdmisionDTO().getFloorNo().setLabel(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getFloorNo().toString());
							}
						}		
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO())){
							if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId())) {
								disciplinaryActionDTO.getHostelAdmisionDTO().setUnit(new SelectDTO());
								disciplinaryActionDTO.getHostelAdmisionDTO().getUnit().setValue(String.valueOf(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId()));
								disciplinaryActionDTO.getHostelAdmisionDTO().getUnit().setLabel(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit());
							}
						}
						if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO())) {
							if(!Utils.isNullOrEmpty(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId())) {
								disciplinaryActionDTO.getHostelAdmisionDTO().setBlock(new SelectDTO());
								disciplinaryActionDTO.getHostelAdmisionDTO().getBlock().setValue(String.valueOf(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId()));
								disciplinaryActionDTO.getHostelAdmisionDTO().getBlock().setLabel(disciplinaryData.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName());
							}
						}
					}
				}
			}
			disciplinaryActionDTOList.add(disciplinaryActionDTO);
		});
		return Flux.fromIterable(disciplinaryActionDTOList);
	}

	public Mono<HostelDisciplinaryActionsDTO> edit(int id) {
		HostelDisciplinaryActionsDBO dbo = disciplinaryActionEntryTransaction.edit(id);
		return convertDboToDto(dbo);
	}

	private Mono<HostelDisciplinaryActionsDTO> convertDboToDto(HostelDisciplinaryActionsDBO dbo) {
		HostelDisciplinaryActionsDTO disciplinaryActionDTO = new HostelDisciplinaryActionsDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			disciplinaryActionDTO.setId(dbo.getId());
		}
		if(!Utils.isNullOrEmpty(dbo.getDisciplinaryActionsDate())) {
			disciplinaryActionDTO.setDisciplinaryActionDate(dbo.getDisciplinaryActionsDate());
		}
		if(!Utils.isNullOrEmpty(dbo.getRemarks())) {
			disciplinaryActionDTO.setRemarks(dbo.getRemarks());	
		}
		if(!Utils.isNullOrEmpty(dbo.getAcademicYearDBO())) {
			disciplinaryActionDTO.setAcademicYearDTO(new SelectDTO());
			disciplinaryActionDTO.getAcademicYearDTO().setValue(String.valueOf(dbo.getAcademicYearDBO().getId()));
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelDisciplinaryActionsTypeDBO())) {
			disciplinaryActionDTO.setHostelDisciplinaryActionsTypeDTO(new HostelDisciplinaryActionsTypeDTO());	
			disciplinaryActionDTO.getHostelDisciplinaryActionsTypeDTO().setId(dbo.getHostelDisciplinaryActionsTypeDBO().getId());
			if(!Utils.isNullOrEmpty(dbo.getHostelDisciplinaryActionsTypeDBO().getHostelDisciplinaryActions())) {
				disciplinaryActionDTO.getHostelDisciplinaryActionsTypeDTO().setHostelDisciplinaryActions(dbo.getHostelDisciplinaryActionsTypeDBO().getHostelDisciplinaryActions());
			}
			if(!Utils.isNullOrEmpty(dbo.getHostelDisciplinaryActionsTypeDBO().getFineAmount())) {
				disciplinaryActionDTO.getHostelDisciplinaryActionsTypeDTO().setFineAmount(dbo.getHostelDisciplinaryActionsTypeDBO().getFineAmount());	
			}
			HostelFineEntryDBO fineEntryDBO = disciplinaryActionEntryTransaction.getFineEntryData(disciplinaryActionDTO.getId());
			HostelFineEntryDTO fineDTO = new HostelFineEntryDTO();
			fineDTO.setId(fineEntryDBO.getId());
			if(!Utils.isNullOrEmpty(fineEntryDBO.getFineAmount())) {
				fineDTO.setFineAmount(fineEntryDBO.getFineAmount());
			}
			if(!Utils.isNullOrEmpty(fineEntryDBO.getRemarks())) {
				fineDTO.setRemarks(fineEntryDBO.getRemarks());	
			}
			if(!Utils.isNullOrEmpty(fineEntryDBO.getHostelFineCategoryDBO())) {
				fineDTO.setFineCategoryDTO(new HostelFineCategoryDTO());
				fineDTO.getFineCategoryDTO().setId(fineEntryDBO.getHostelFineCategoryDBO().getId());
				fineDTO.getFineCategoryDTO().setFineCategory(fineEntryDBO.getHostelFineCategoryDBO().getFineCategory());
			}
			disciplinaryActionDTO.setHostelFineEntryDTO(fineDTO);
			if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO())) {
				disciplinaryActionDTO.setHostelAdmisionDTO(new HostelAdmissionsDTO());
				disciplinaryActionDTO.getHostelAdmisionDTO().setId(dbo.getHostelAdmissionsDBO().getId());
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelDBO())) {	
				disciplinaryActionDTO.getHostelAdmisionDTO().setHostel(new SelectDTO());
				disciplinaryActionDTO.getHostelAdmisionDTO().getHostel().setValue(String.valueOf(dbo.getHostelAdmissionsDBO().getHostelDBO().getId()));
				disciplinaryActionDTO.getHostelAdmisionDTO().getHostel().setLabel(dbo.getHostelAdmissionsDBO().getHostelDBO().getHostelName());
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getStudentDBO())) {
					disciplinaryActionDTO.getHostelAdmisionDTO().setStudentDTO(new StudentDTO());
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getStudentDBO().getRegisterNo())) {
						disciplinaryActionDTO.getHostelAdmisionDTO().getStudentDTO().setRegisterNo(dbo.getHostelAdmissionsDBO().getStudentDBO().getRegisterNo());	
					}
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentName())) {
						disciplinaryActionDTO.getHostelAdmisionDTO().getStudentDTO().setStudentName(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentName());
					}
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getStudentDBO().getAcaClassDBO())) {
					disciplinaryActionDTO.getHostelAdmisionDTO().getStudentDTO().setAcaClassDTO(dbo.getHostelAdmissionsDBO().getStudentDBO().getAcaClassDBO().getClassName());
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO())){
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getId())) {
						disciplinaryActionDTO.getHostelAdmisionDTO().setBedNo(new SelectDTO());
						disciplinaryActionDTO.getHostelAdmisionDTO().getBedNo().setValue(String.valueOf(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getId()));
						disciplinaryActionDTO.getHostelAdmisionDTO().getBedNo().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getBedNo());
					}
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getId())) {
							disciplinaryActionDTO.getHostelAdmisionDTO().setRoomNo(new SelectDTO());
							disciplinaryActionDTO.getHostelAdmisionDTO().getRoomNo().setValue(String.valueOf(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getId()));
							disciplinaryActionDTO.getHostelAdmisionDTO().getRoomNo().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
						}
					}
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getId())) {
							disciplinaryActionDTO.getHostelAdmisionDTO().setFloorNo(new SelectDTO());
							disciplinaryActionDTO.getHostelAdmisionDTO().getFloorNo().setValue(String.valueOf(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getId()));
							disciplinaryActionDTO.getHostelAdmisionDTO().getFloorNo().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getFloorNo().toString());
						}
					}		
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO())){
						if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId())) {
							disciplinaryActionDTO.getHostelAdmisionDTO().setUnit(new SelectDTO());
							disciplinaryActionDTO.getHostelAdmisionDTO().getUnit().setValue(String.valueOf(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getId()));
							disciplinaryActionDTO.getHostelAdmisionDTO().getUnit().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit());
						}
					}
					if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO())) {
						if(!Utils.isNullOrEmpty(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId())) {
							disciplinaryActionDTO.getHostelAdmisionDTO().setBlock(new SelectDTO());
							disciplinaryActionDTO.getHostelAdmisionDTO().getBlock().setValue(String.valueOf(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getId()));
							disciplinaryActionDTO.getHostelAdmisionDTO().getBlock().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName());
						}
					}
				}
			}
		}
		return Mono.just(disciplinaryActionDTO);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return disciplinaryActionEntryTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelDisciplinaryActionsDTO> dto,  String userId) {
		return dto.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						disciplinaryActionEntryTransaction.update(s);
					} else {
						disciplinaryActionEntryTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private HostelDisciplinaryActionsDBO convertDtoToDbo(HostelDisciplinaryActionsDTO dto, String userId) {
		HostelFineCategoryDBO fineCategory = disciplinaryActionEntryTransaction.getFineCategory(dto.getHostelAdmisionDTO().getHostel().getValue());
		HostelDisciplinaryActionsDBO dbo = null;
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo = disciplinaryActionEntryTransaction.edit(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		} else {
			dbo = new HostelDisciplinaryActionsDBO();
			dbo.setCreatedUsersId(Integer.parseInt(userId));
		}
		if(!Utils.isNullOrEmpty(dto.getRemarks()) || Utils.isNullOrEmpty(dto.getRemarks())) {
			dbo.setRemarks(dto.getRemarks());
		}
		if(!Utils.isNullOrEmpty(dto.getAcademicYearDTO())) {
			dbo.setAcademicYearDBO(new ErpAcademicYearDBO());
			dbo.getAcademicYearDBO().setId(Integer.parseInt(dto.getAcademicYearDTO().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getHostelAdmisionDTO())) {
			dbo.setHostelAdmissionsDBO(new HostelAdmissionsDBO());
			dbo.getHostelAdmissionsDBO().setId(dto.getHostelAdmisionDTO().getId());
		}
		if(!Utils.isNullOrEmpty(dto.getDisciplinaryActionDate())) {
			dbo.setDisciplinaryActionsDate(dto.getDisciplinaryActionDate());
		}
		if(!Utils.isNullOrEmpty(dto.getHostelDisciplinaryActionsTypeDTO())) {
			dbo.setHostelDisciplinaryActionsTypeDBO(new HostelDisciplinaryActionsTypeDBO());
			dbo.getHostelDisciplinaryActionsTypeDBO().setId(dto.getHostelDisciplinaryActionsTypeDTO().getId());
		}
		dbo.setRecordStatus('A');
		HostelFineEntryDBO fine = commonHostelHandler1.getFineCategory(dto.getId(), dto.getHostelAdmisionDTO().getId(), userId);
		if(!Utils.isNullOrEmpty(fineCategory)) {
		 fine.setHostelFineCategoryDBO(new HostelFineCategoryDBO());
		 fine.getHostelFineCategoryDBO().setId(fineCategory.getId());
		}
		fine.setHostelDisciplinaryActionsDBO(dbo);
		if(!Utils.isNullOrEmpty(dto.getDisciplinaryActionDate())) {
			fine.setDate(dto.getDisciplinaryActionDate());
		}
		if(!Utils.isNullOrEmpty(dto.getRemarks())) {
			fine.setRemarks(dto.getRemarks());
		}
		if(!Utils.isNullOrEmpty(dto.getHostelDisciplinaryActionsTypeDTO())) {
			fine.setFineAmount(dto.getHostelDisciplinaryActionsTypeDTO().getFineAmount());
		}
		dbo.setHostelFineEntryDBO(fine);
		return dbo;
	}
}
