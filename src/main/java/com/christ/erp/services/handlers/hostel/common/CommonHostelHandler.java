package com.christ.erp.services.handlers.hostel.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.HostelProgrammeDetailsDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelDisciplinaryActionsDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelLeaveTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.*;
import com.christ.erp.services.dto.account.AccFeeHeadsAccountDTO;
import com.christ.erp.services.dto.account.AccFeeHeadsDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeLevelDTO;
import com.christ.erp.services.dto.employee.common.HostelProgrammeDetailsDTO;
import com.christ.erp.services.dto.hostel.common.CommonHostelDTO;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelDisciplinaryActionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDisciplinaryActionsTypeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFacilitySettingsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.transactions.hostel.common.CommonHostelTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.Tuple;
import java.util.stream.Collectors;
import java.math.BigInteger;
import java.util.*;


@Service
public class CommonHostelHandler {

	private static volatile CommonHostelHandler commonHostelHandler = null;
	CommonHostelTransaction commonHostelTransaction = CommonHostelTransaction.getInstance();

	@Autowired
	CommonHostelTransaction commonHostelTransaction1;

	public static CommonHostelHandler getInstance() {
		if(commonHostelHandler==null) {
			commonHostelHandler = new CommonHostelHandler();
		}
		return commonHostelHandler;
	}

	public void getHostels(ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonHostelTransaction.getHostels();
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getUnitsByBlockAndHostelId(ApiResult<List<LookupItemDTO>> result, String blockId, String hostelId) throws Exception {
		List<Tuple> mappings = commonHostelTransaction.getUnitsByBlockAndHostelId(blockId,hostelId);
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getBlockByHostelId(ApiResult<List<LookupItemDTO>> result, String hostelId)throws Exception {
		List<Tuple> mappings = commonHostelTransaction.getBlockByHostelId(hostelId);
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getHostelFacility(ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<Tuple> mappings = commonHostelTransaction.getHostelFacility();
		if(mappings.size()>0) {
			if(mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for(Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}

	public void getRoomTypeCategory(ApiResult<List<LookupItemDTO>> result) throws Exception {
		List<LookupItemDTO> roomTypeCategoryList = new ArrayList<>();
		roomTypeCategoryList.add(new LookupItemDTO("1","Student"));
		roomTypeCategoryList.add(new LookupItemDTO("2","Guest"));
		roomTypeCategoryList.add(new LookupItemDTO("3","Warden"));
		roomTypeCategoryList.add(new LookupItemDTO("4","Faculty"));
		result.dto = roomTypeCategoryList;
	}

	public void getFloorsByBlockUnitId(ApiResult<String> result, String blockUnitId) throws Exception {
		Tuple mapping = commonHostelTransaction.getFloorsByBlockUnitId(blockUnitId);
		if(!Utils.isNullOrEmpty(mapping)) {
			result.success = true;
			String totalFloors = !Utils.isNullOrEmpty(mapping.get("total_floors")) ? mapping.get("total_floors").toString() : "";
			result.dto = totalFloors;
		}
	}

	public void getHostelRoomTypes(ApiResult<List<CommonHostelDTO>> result, String hostelId) throws Exception {
		List<Tuple> mappings = commonHostelTransaction.getHostelRoomTypes(hostelId);
		result.dto = new ArrayList<>();
		if(!Utils.isNullOrEmpty(mappings)) {
			result.success = true;
			for(Tuple mapping : mappings) {
				CommonHostelDTO itemInfo = new CommonHostelDTO();
				itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
				itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
				itemInfo.maxOccupants = !Utils.isNullOrEmpty(mapping.get("total_occupants")) ? mapping.get("total_occupants").toString() : "";
				result.dto.add(itemInfo);
			}
		}
	}

	public List<HostelProgrammeDetailsDTO> getCampusProgrammesTree(String hostelId) throws Exception {
		List<HostelProgrammeDetailsDBO> dboList = null;
		List<HostelProgrammeDetailsDTO> listDTOs = null;
		dboList = commonHostelTransaction.getCampusProgrammesTree(hostelId);
		if(!Utils.isNullOrEmpty(dboList)) {
			listDTOs = new ArrayList<HostelProgrammeDetailsDTO>();
			Set<Integer> campusIds = new HashSet<Integer>();
			for (HostelProgrammeDetailsDBO dbo : dboList) {
				HostelProgrammeDetailsDTO dto = new HostelProgrammeDetailsDTO(); 
				if(!Utils.isNullOrEmpty(dbo.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(dbo.erpCampusProgrammeMappingDBO.erpCampusDBO) 
						&& !campusIds.contains(dbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id)) { 
					campusIds.add(dbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
					dto.value =String.valueOf(dbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id);		
					dto.label = dbo.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName;
					dto.children = new ArrayList <ErpProgrammeLevelDTO>();	
					for (HostelProgrammeDetailsDBO dbo2 : dboList) {
						if(!Utils.isNullOrEmpty(dbo2.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(dbo2.erpCampusProgrammeMappingDBO.erpCampusDBO) 
								&& (dbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id == dbo2.erpCampusProgrammeMappingDBO.erpCampusDBO.id )) {
							ErpProgrammeLevelDTO programme = new ErpProgrammeLevelDTO();	
							if(!Utils.isNullOrEmpty(dbo2.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
								programme.value = dbo2.erpCampusProgrammeMappingDBO.id+"-"+dbo2.erpCampusProgrammeMappingDBO.erpCampusDBO.id+"-"+dbo2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id;						 
								programme.label = dbo2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeName;
								dto.children.add(programme);
							}
						}
					}
					listDTOs.add(dto);
				}
			}
		}
		return listDTOs;
	}

	public void getHostelBlocks(ApiResult<List<CommonHostelDTO>> result) throws Exception {
		List<Tuple> mappings = commonHostelTransaction.getHostelBlocks();
		result.dto = new ArrayList<>();
		for(Tuple mapping : mappings) {
			CommonHostelDTO itemInfo = new CommonHostelDTO();
			itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
			itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
			result.dto.add(itemInfo);
		}
	}

	public void getMaximumOccupants(ApiResult<List<CommonHostelDTO>> result, String hostelId) throws Exception {
		List<Tuple> mappings = commonHostelTransaction.getBlockByHostelId(hostelId);
		result.dto = new ArrayList<>();
		for(Tuple mapping : mappings) {
			CommonHostelDTO itemInfo = new CommonHostelDTO();
			itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
			itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
			result.dto.add(itemInfo);
		}
	}

	public void getHostelSeatsAvailable(ApiResult<List<CommonHostelDTO>> result,String hostelId) throws Exception {
		Map<String, Integer> roomTypeMap = new HashMap<>();
		HostelDBO hostelDBO = commonHostelTransaction.getHostelSeatsAvailable(hostelId);
		for(HostelBlockDBO dbo : hostelDBO.hostelBlockDBOSet) {
			if(dbo.recordStatus == 'A') {
				for(HostelBlockUnitDBO blockUnitDBO: dbo.hostelBlockUnitDBOSet) {
					if(blockUnitDBO.recordStatus=='A') {
						for(HostelFloorDBO floorDBO:blockUnitDBO.hostelFloorDBOSet) {
							if(floorDBO.recordStatus=='A') {
								for(HostelRoomsDBO roomsDBO:floorDBO.hostelRoomsDBOSet) {
									if(roomsDBO.recordStatus=='A') {
										int bedCount=0;
										for(HostelBedDBO bedDBO:roomsDBO.hostelBedDBOSet) {
											if(bedDBO.recordStatus=='A') {
												bedCount+=1;
											}
										}
										if(roomTypeMap.containsKey(roomsDBO.hostelRoomTypeDBO.roomType)) {
											roomTypeMap.put(roomsDBO.hostelRoomTypeDBO.roomType, roomTypeMap.get(roomsDBO.hostelRoomTypeDBO.roomType) + bedCount);
										}
										else {
											roomTypeMap.put(roomsDBO.hostelRoomTypeDBO.roomType, bedCount);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		List<String> keyList = new ArrayList(roomTypeMap.keySet());
		List<Integer> valueList = new ArrayList(roomTypeMap.values());
		result.dto = new ArrayList<>();
		for(int i=0;i<keyList.size();++i) {
			CommonHostelDTO hostelDTO = new CommonHostelDTO();
			hostelDTO.maxOccupants = valueList.get(i).toString();
			hostelDTO.label = keyList.get(i);
			result.dto.add(hostelDTO);
		}
	}

	public List<LookupItemDTO> getHostelsByGender(String genderId) throws Exception {
		List<LookupItemDTO> dto = new ArrayList<>();
		if(!Utils.isNullOrEmpty(genderId)) {
			List<Tuple> mappings = commonHostelTransaction.getHostelsByGender(genderId);
			if(!Utils.isNullOrEmpty(mappings)) {
				mappings.forEach( (mapping) -> {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					dto.add(itemInfo);
				});
			}
		}
		return dto;
	}

	public Flux<SelectDTO> getHostel(Boolean showAllHostels, String userId) {
		if(showAllHostels){
			return commonHostelTransaction1.getHostel().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
		} else {
			return commonHostelTransaction1.getHostelByUser(userId)
					.flatMapMany(Flux::fromIterable).map(this::convertDboToDto)
					.switchIfEmpty(commonHostelTransaction1.getHostel().flatMapMany(Flux::fromIterable).map(this::convertDboToDto));
		}
	}

	public SelectDTO convertDboToDto(HostelDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getHostelName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getHostelName());
		}
		return dto;
	}

	public SelectDTO convertDboToDto(Tuple tuple) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(tuple.get("hostel_name")) && !Utils.isNullOrEmpty(tuple.get("hostel_name").toString())) {
			dto.setValue(tuple.get("hostel_id").toString());
			dto.setLabel(tuple.get("hostel_name").toString());
		}
		return dto;
	}

	public Flux<SelectDTO> getUnitsByBlock(String blockId) {
		return commonHostelTransaction1.getUnitsByBlock(blockId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto2);
	}

	public SelectDTO convertDboToDto2(HostelBlockUnitDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getHostelUnit())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getHostelUnit());
		}
		return dto;
	}


	public Flux<SelectDTO> getStatus() {
		return commonHostelTransaction1.getStatus().flatMapMany(Flux::fromIterable).map(this::convertDboToDto5);
	}

	public SelectDTO convertDboToDto5(ErpWorkFlowProcessDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getApplicantStatusDisplayText());
		}
		return dto;
	}

	public SelectDTO convertEmpDepDboToDto(EmpDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		StringBuffer empDepString = new StringBuffer("");
		empDepString.append(dbo.getEmpName());
		if(!Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO()) && !Utils.isNullOrEmpty(dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO())) {
			empDepString.append("(" + dbo.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName() + ")");
		}
		dto.setLabel(empDepString.toString());
		return dto;
	}

	public Mono<List<Object>> getRoomTypeByCount(String hostelId, String academicYearId) {
		List<HostelRoomTypeDBO> a = commonHostelTransaction1.getRoomTypeByCount(hostelId);
		List<Integer> roomTypeIds =	a.stream().map(q-> q.getId()).collect(Collectors.toList());
		List<HostelSeatAvailabilityDetailsDBO> seat = commonHostelTransaction1.getSeatAvailabilityCount(hostelId, academicYearId, roomTypeIds);
		List<HostelApplicationDBO> applicant = commonHostelTransaction1.getHostelApplicationCount(hostelId,academicYearId, roomTypeIds);
		return this.convertDboToDto3(a,seat,applicant,hostelId,academicYearId);
	}

	public Mono<List<Object>> convertDboToDto3(List<HostelRoomTypeDBO> a, List<HostelSeatAvailabilityDetailsDBO> seat, List<HostelApplicationDBO> applicant, String hostelId, String academicYearId) {
		List<Object> obj = new ArrayList<Object>();
		a.forEach(p -> {
			Integer apllicantSeat = 0;
			Integer countSeat = 0;
			if(!Utils.isNullOrEmpty(applicant)) {
			apllicantSeat = (int)applicant.stream().filter(appli-> appli.getAllottedHostelRoomTypeDBO().getId()==p.getId()).count();
			}
			if(!Utils.isNullOrEmpty(seat)) {
			countSeat = seat.stream().filter(seatAv -> seatAv.getHostelRoomTypeDBO().getId()==p.getId()).findAny().get().getAvailableSeats();
			}
			Integer result = countSeat- apllicantSeat;
			SelectDTO dto = new SelectDTO();
			if(!Utils.isNullOrEmpty(p.getRoomType())) {
				dto.setValue(String.valueOf(p.getId()));
				dto.setLabel(p.getRoomType().concat("[" +(String.valueOf(result) + "]")));
			}
			obj.add(dto);
		});
		return Mono.just(obj);
	}

	public Flux<SelectDTO> getBlockByHostelId1(String hostelId) {
		return commonHostelTransaction1.getBlockByHostelId1(Integer.parseInt(hostelId)).flatMapMany(Flux::fromIterable).map(this::convertDboToDto1);
	}

	public SelectDTO convertDboToDto1(HostelBlockDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getBlockName());
		}
		return dto;
	}

	public Mono<List<HostelFacilitySettingsDTO>> getHostelFacilities(String roomTypeId) {
		List<HostelFacilitySettingDBO> facilities = commonHostelTransaction1.getHostelFacilities();
		Map<Integer, HostelRoomTypeDetailsDBO> list = commonHostelTransaction1.getFacilityList(roomTypeId).stream().collect(Collectors.toMap(s -> s.getHostelFacilitySettingDBO().getId(), s -> s));
		return this.convertFacilityDboToDto(facilities, list);
	}
	
	private Mono<List<HostelFacilitySettingsDTO>> convertFacilityDboToDto(List<HostelFacilitySettingDBO> facilities, Map<Integer, HostelRoomTypeDetailsDBO> list) {
	    List<HostelFacilitySettingsDTO> dto = new ArrayList<HostelFacilitySettingsDTO>();
	    facilities.forEach(dbo -> {
			if(list.containsKey(dbo.getId())) {
				HostelFacilitySettingsDTO dtos = new HostelFacilitySettingsDTO();
				dtos.setId(dbo.getId());
				dtos.setFacilityName(dbo.getFacilityName());
				dtos.setFacility(true);
				dto.add(dtos);
			} else {
				HostelFacilitySettingsDTO dtos = new HostelFacilitySettingsDTO();
				dtos.setId(dbo.getId());
				dtos.setFacilityName(dbo.getFacilityName());
				dtos.setFacility(false);
				dto.add(dtos);
			}
		});
		return Mono.just(dto);
	}
	
	public Flux<SelectDTO> getFloorByUnit(String unitId) {
		return commonHostelTransaction1.getFloorByUnit(unitId).flatMapMany(Flux::fromIterable).map(this::convertFacilityDboToDto);
	}
	
	public SelectDTO convertFacilityDboToDto(HostelFloorDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(String.valueOf(dbo.getFloorNo()));
		}
		return dto;
	}
	
	public Flux<SelectDTO> getHostelBlockByUser(String hostelId,Boolean isUserSpecific, String userId) {
		if(!isUserSpecific){
			return commonHostelTransaction1.getBlockByHostelId1(Integer.parseInt(hostelId)).flatMapMany(Flux::fromIterable).map(this::convertHostelBlockDBOtoDTO);
		} else {
			return commonHostelTransaction1.getHostelBlockByUser(hostelId,userId).flatMapMany(Flux::fromIterable).map(this::convertHostelBlockDBOtoDTO)
					.switchIfEmpty(commonHostelTransaction1.getBlockByHostelId1(Integer.parseInt(hostelId)).flatMapMany(Flux::fromIterable).map(this::convertHostelBlockDBOtoDTO));
		}
	}
	
	public SelectDTO convertHostelBlockDBOtoDTO(HostelBlockDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(String.valueOf(dbo.getBlockName()));
		}
		return dto;
	}

	public Flux<SelectDTO> getHostelBlockUnitByUser(String userId, String blockId, Boolean isUserSpecific) {
		if(!isUserSpecific){
			return commonHostelTransaction1.getUnitsByBlock(blockId).flatMapMany(Flux::fromIterable).map(this::convertHostelBlockUnitDBOtoDTO);
		} else {
			return commonHostelTransaction1.getHostelBlockUnitByUser(userId,blockId).flatMapMany(Flux::fromIterable).map(this::convertHostelBlockUnitDBOtoDTO)		
					.switchIfEmpty(commonHostelTransaction1.getUnitsByBlock(blockId).flatMapMany(Flux::fromIterable).map(this::convertHostelBlockUnitDBOtoDTO));
		}
	}
	
	public SelectDTO convertHostelBlockUnitDBOtoDTO(HostelBlockUnitDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(String.valueOf(dbo.getHostelUnit()));
		}
		return dto;
	}

	public Flux<SelectDTO> getHostelLeaveType() {
		return commonHostelTransaction1.getHostelLeaveType().flatMapMany(Flux::fromIterable).map(this::convertHostelLevaeTypeDBOToDTO);
	}
	
	public SelectDTO convertHostelLevaeTypeDBOToDTO(HostelLeaveTypeDBO dbo) {
		SelectDTO selectDTO = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			selectDTO.setValue(String.valueOf(dbo.getId()));
			selectDTO.setLabel(dbo.getLeaveTypeName());
		}
		return selectDTO;
	}
	
	public Flux<HostelDisciplinaryActionsTypeDTO> getDisciplinaryActionType() {
		return commonHostelTransaction1.getDisciplinaryActionType().flatMapMany(Flux::fromIterable).map(this::convertDisciplinaryDboToDto);		
	}
	
	public HostelDisciplinaryActionsTypeDTO convertDisciplinaryDboToDto(HostelDisciplinaryActionsTypeDBO dbo) {
		HostelDisciplinaryActionsTypeDTO dto = new HostelDisciplinaryActionsTypeDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setId(dbo.getId());
			dto.setHostelDisciplinaryActions(String.valueOf(dbo.getHostelDisciplinaryActions()));
			dto.setFineAmount(dbo.getFineAmount());
		}
		return dto;
	}
	public Boolean isUserSpecific(String userId) {
		return commonHostelTransaction1.isUserSpecific(userId);		
	}

	public HostelFineEntryDBO getFineCategory(int id, int admissionId, String userId) {
		HostelFineEntryDBO fine = null;
		if (!Utils.isNullOrEmpty(id)) {
			fine = commonHostelTransaction1.edit(id);
			fine.setModifiedUsersId(Integer.parseInt(userId));
		} else {
			fine = new HostelFineEntryDBO();
			fine.setCreatedUsersId(Integer.parseInt(userId));
		} 
	if(!Utils.isNullOrEmpty(admissionId)) {
		fine.setHostelAdmissionsDBO(new HostelAdmissionsDBO());
		fine.getHostelAdmissionsDBO().setId(admissionId);
	}
	fine.setRecordStatus('A');
	return fine; 
    }
	

	public Flux<SelectDTO> getBedByRoomId(String roomId) {
		return commonHostelTransaction1.getBedByRoomId(roomId).flatMapMany(Flux::fromIterable).map(this::convertHostelBedDboToDto);
	}

	public SelectDTO convertHostelBedDboToDto(HostelBedDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getBedNo());
		}
		return dto;
	}

	public Flux<SelectDTO> getRoomTypeForStudent(String hostelId) {
		return commonHostelTransaction1.getRoomTypeForStudent(hostelId).flatMapMany(Flux::fromIterable).map(this::convertHostelRoomTypeDboToDto);
	}

	public SelectDTO convertHostelRoomTypeDboToDto(HostelRoomTypeDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getRoomType());
		}
		return dto;
	}
	
	public Flux<SelectDTO> getRoomByFloor(String floorId) {
		return commonHostelTransaction1.getRoomByFloor(floorId).flatMapMany(Flux::fromIterable).map(this::convertHostelRoomDboToDto);
	}

	public SelectDTO convertHostelRoomDboToDto(HostelRoomsDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getRoomNo());
		}
		return dto;
	}

	public Flux<AccFeeHeadsDTO> getAccHeadsForHostelFees(String hostelId) {
		return commonHostelTransaction1.getAccHeadsForHostelFees(hostelId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}
	
	public AccFeeHeadsDTO convertDBOToDTO(AccFeeHeadsDBO dbo){
		AccFeeHeadsDTO dto = new AccFeeHeadsDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setId(dbo.getId());
			dto.setHeading(dbo.getHeading());
			dto.setFixedAmount(dbo.isFixedAmount);
			if(!Utils.isNullOrEmpty(dbo.getAccFeeHeadsAccountList())) {
			dbo.getAccFeeHeadsAccountList().forEach(subdbo ->{
				dto.setAmount(subdbo.getAmount());
			});
			}
		}
		return dto;
	}

	public Mono<HostelAdmissionsDTO> getDataByStudentNameOrRegNo(String yearId, String regNo, String studentName) {
		return commonHostelTransaction1.getDataByStudentNameOrRegNo(yearId, regNo, studentName).map(s->convertDboToDto(s));		
	}

	public HostelAdmissionsDTO convertDboToDto(HostelAdmissionsDBO dbo) {
		HostelAdmissionsDTO hostelAdmissionsDTO = new HostelAdmissionsDTO();
		if(!Utils.isNullOrEmpty(dbo.getId())) {
			hostelAdmissionsDTO.setId(dbo.getId());
		}
		hostelAdmissionsDTO.setHostel(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getHostelDBO())) {
			hostelAdmissionsDTO.getHostel().setValue(String.valueOf(dbo.getHostelDBO().getId()));
			hostelAdmissionsDTO.getHostel().setLabel(dbo.getHostelDBO().getHostelName());
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
		return hostelAdmissionsDTO;	
	}

	public Flux<HostelDisciplinaryActionsDTO> getOtherDisciplinary(String yearId, String regNo) {
		List<HostelDisciplinaryActionsDBO> disciplinaryList = commonHostelTransaction1.getOtherDisciplinary(yearId, regNo);
		if(!Utils.isNullOrEmpty(disciplinaryList)) {
			return this.convertDisciplinaryDboToDto(disciplinaryList, yearId, regNo);
		} 
		return null;
	}

	private Flux<HostelDisciplinaryActionsDTO> convertDisciplinaryDboToDto(List<HostelDisciplinaryActionsDBO> disciplinaryList, String yearId, String regNo) {
		List<HostelDisciplinaryActionsDTO> disciplinaryActionDTO = new ArrayList<HostelDisciplinaryActionsDTO>();
		disciplinaryList.forEach(data -> {
			HostelDisciplinaryActionsDTO dto = new HostelDisciplinaryActionsDTO();
			dto.setId(data.getId());
			if(!Utils.isNullOrEmpty(data.getDisciplinaryActionsDate())) {
			dto.setDisciplinaryActionDate(data.getDisciplinaryActionsDate());
			}
			if(!Utils.isNullOrEmpty(data.getRemarks())) {
			dto.setRemarks(data.getRemarks());
			}
			dto.setHostelDisciplinaryActionsTypeDTO(new HostelDisciplinaryActionsTypeDTO());
			dto.getHostelDisciplinaryActionsTypeDTO().setHostelDisciplinaryActions(data.getHostelDisciplinaryActionsTypeDBO().getHostelDisciplinaryActions());
			dto.getHostelDisciplinaryActionsTypeDTO().setFineAmount(data.getHostelDisciplinaryActionsTypeDBO().getFineAmount());
			disciplinaryActionDTO.add(dto);
		});
		return Flux.fromIterable(disciplinaryActionDTO);
	}


	
	public Flux<HostelFineCategoryDTO> getFineCategoryOthers() {					
		return commonHostelTransaction1.getFineCategoryOthers().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public  HostelFineCategoryDTO convertDboToDto(HostelFineCategoryDBO dbo) {
		HostelFineCategoryDTO dto = new HostelFineCategoryDTO();
		dto.setId(dbo.getId());
		if (!Utils.isNullOrEmpty(dbo.getAccFeeHeadsDBO())) {
			dto.setAccFeeHeadsDTO(new AccFeeHeadsDTO());
			dto.getAccFeeHeadsDTO().setFixedAmount(dbo.getAccFeeHeadsDBO().isFixedAmount());
		}		
		dto.setFineCategory(dbo.getFineCategory());
		dto.setFineAmount(dbo.getFineAmount());
		return dto;
	}

	
	public Flux<SelectDTO> getStudentRegAndNameListFromHostel(String data, Integer academicYearId) {
		return commonHostelTransaction1.getStudentRegAndNameListFromHostel(data, academicYearId).flatMapMany(Flux::fromIterable).map(this::convertAdmissionDboToDto);
	}

	public SelectDTO convertAdmissionDboToDto(HostelAdmissionsDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(dbo.getStudentDBO().getRegisterNo());
		dto.setLabel(dbo.getStudentDBO().getStudentName());
		return dto;
	}

	public Flux<SelectDTO> getHostelByGenderAndCampus(String genderId, String erpCampusProgrammeMappingId) {
		List<Tuple> tuple = commonHostelTransaction1.getHostelByGenderAndCampus(genderId, erpCampusProgrammeMappingId);
		return this.convertDboToDto(tuple);
	}

	private Flux<SelectDTO> convertDboToDto(List<Tuple> list) {
		List<SelectDTO> dto = new ArrayList<SelectDTO>();
		list.forEach(dbo -> {
			SelectDTO dto1 = new SelectDTO();
			dto1.setValue(dbo.get("hostel_id").toString());
			dto1.setLabel(dbo.get("hostel_name").toString());
			dto.add(dto1);
		});
		return Flux.fromIterable(dto);
	}

	public Flux<SelectDTO> getHostelSelectedStatus() {
		return commonHostelTransaction1.getHostelSelectedStatus().flatMapMany(Flux::fromIterable).map(this::convertStatusDboToDto);
	}

	public SelectDTO convertStatusDboToDto(ErpWorkFlowProcessDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getId())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getApplicantStatusDisplayText());
		}
		return dto;
	}

}
