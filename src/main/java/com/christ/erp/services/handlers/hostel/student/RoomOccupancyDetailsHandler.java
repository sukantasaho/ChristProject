package com.christ.erp.services.handlers.hostel.student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBedDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockUnitDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFloorDTO;
import com.christ.erp.services.dto.hostel.settings.HostelRoomsDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.transactions.hostel.student.RoomOccupancyDetailsTransaction;

import reactor.core.publisher.Flux;

@Service
public class RoomOccupancyDetailsHandler {

	@Autowired
	private RoomOccupancyDetailsTransaction roomOccupancyDetailsTransaction;

	public Flux<SelectDTO> getBlock(String hostelId) {
		return roomOccupancyDetailsTransaction.getBlock(Integer.parseInt(hostelId)).flatMapMany(Flux::fromIterable).map(this::convertDboToDto1);
	}

	public SelectDTO convertDboToDto1(HostelBlockDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getBlockName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getBlockName());
		}
		return dto;
	}

	public Flux<SelectDTO> getUnitsByBlock(String blockId) {
		return roomOccupancyDetailsTransaction.getUnitsByBlock(blockId).flatMapMany(Flux::fromIterable).map(this::convertDboToDto2);
	}

	public SelectDTO convertDboToDto2(HostelBlockUnitDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo.getHostelUnit())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getHostelUnit());
		}
		return dto;
	}

	public Flux<HostelBlockDTO> getOccupancyDetails(String yearId, String hostelId, String blockId, String unitId) {
		List<Tuple> list = roomOccupancyDetailsTransaction.getOccupancyDetails(Integer.parseInt(yearId), Integer.parseInt(hostelId), blockId, unitId);
		return this.convertDboToDto5(list);
	}

	public Flux<HostelBlockDTO> convertDboToDto5(List<Tuple> dbo) {
		Map<String, Map<String, Map<String, Map<String, List<HostelBedDTO>>>>> map = new LinkedHashMap<String, Map<String, Map<String, Map<String, List<HostelBedDTO>>>>>();
		Map<String, String> blockmap = new HashMap<String, String>();
		Map<String, String> unitmap = new HashMap<String, String>();
		Map<String, String> floormap = new HashMap<String, String>();
		Map<String, String> roommap = new HashMap<String, String>();
		dbo.forEach(dbos -> {
			if(map.containsKey(dbos.get("hostel_block_id").toString())) {
				Map<String, Map<String, Map<String, List<HostelBedDTO>>>> unitMap = map.get(dbos.get("hostel_block_id").toString());
				blockmap.put(dbos.get("hostel_block_id").toString(), dbos.get("hostel_block_name").toString());
				if(Utils.isNullOrEmpty(unitMap)) {
					unitMap = new HashMap<String, Map<String, Map<String, List<HostelBedDTO>>>>();
				}
				if(unitMap.containsKey(dbos.get("hostel_block_unit_id").toString())) {
					Map<String, Map<String, List<HostelBedDTO>>> floorMap = unitMap.get(dbos.get("hostel_block_unit_id").toString());
					unitmap.put(dbos.get("hostel_block_unit_id").toString(), dbos.get("hostel_unit").toString());
					if(Utils.isNullOrEmpty(floorMap)) {
						floorMap = new HashMap<String, Map<String, List<HostelBedDTO>>>();
					}
					if(floorMap.containsKey(dbos.get("hostel_floor_id").toString())) {
						Map<String, List<HostelBedDTO>> roomMap = floorMap.get(dbos.get("hostel_floor_id").toString());
						floormap.put(dbos.get("hostel_floor_id").toString(), dbos.get("floor_no").toString());
						if(Utils.isNullOrEmpty(roomMap)) {
							roomMap = new HashMap<String, List<HostelBedDTO>>();
						}	
						if(roomMap.containsKey(dbos.get("room_no").toString())) {
							List<HostelBedDTO> bedDTOS = roomMap.get(dbos.get("room_no").toString());
							bedDTOS.add(setRoom(dbos));
							roomMap.put(dbos.get("room_no").toString(), bedDTOS);
							roommap.put(dbos.get("room_no").toString(), dbos.get("room_type").toString());						    
						} else {
							List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
							bedDTOS.add(setRoom(dbos));
							roomMap.put(dbos.get("room_no").toString(), bedDTOS);
							roommap.put(dbos.get("room_no").toString(), dbos.get("room_type").toString());
						}
						floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
					} else {
						Map<String, List<HostelBedDTO>> roomMap = new LinkedHashMap<String, List<HostelBedDTO>>();
						List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
						bedDTOS.add(setRoom(dbos));
						roomMap.put(dbos.get("room_no").toString(), bedDTOS);
						floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
						floormap.put(dbos.get("hostel_floor_id").toString(), dbos.get("floor_no").toString());
					}
					unitMap.put(dbos.get("hostel_block_unit_id").toString(), floorMap);
				} else {
					Map<String, List<HostelBedDTO>> roomMap = new LinkedHashMap<String, List<HostelBedDTO>>();
					List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
					bedDTOS.add(setRoom(dbos));
					roomMap.put(dbos.get("room_no").toString(), bedDTOS);
					Map<String, Map<String, List<HostelBedDTO>>> floorMap = new LinkedHashMap<String, Map<String, List<HostelBedDTO>>>();
					floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
					unitMap.put(dbos.get("hostel_block_unit_id").toString(), floorMap);
					unitmap.put(dbos.get("hostel_block_unit_id").toString(), dbos.get("hostel_unit").toString());
				}
			} else {
				Map<String, List<HostelBedDTO>> roomMap = new LinkedHashMap<String, List<HostelBedDTO>>();
				List<HostelBedDTO> bedDTOS = new ArrayList<HostelBedDTO>();
				bedDTOS.add(setRoom(dbos));
				roomMap.put(dbos.get("room_no").toString(), bedDTOS);
				Map<String, Map<String, List<HostelBedDTO>>> floorMap = new LinkedHashMap<String, Map<String, List<HostelBedDTO>>>();
				floorMap.put(dbos.get("hostel_floor_id").toString(), roomMap);
				Map<String, Map<String, Map<String, List<HostelBedDTO>>>> unitMap = new LinkedHashMap<String, Map<String, Map<String, List<HostelBedDTO>>>>();
				unitMap.put(dbos.get("hostel_block_unit_id").toString(), floorMap);
				map.put(dbos.get("hostel_block_id").toString(), unitMap);
				blockmap.put(dbos.get("hostel_block_id").toString(), dbos.get("hostel_block_name").toString());
			}
		});
		List<HostelBlockDTO> blockDtos = new ArrayList<HostelBlockDTO>();
		map.forEach((k, v )-> {	
			
			System.out.println(k);
			HostelBlockDTO dto = new HostelBlockDTO();
			dto.setId(Integer.parseInt(k));
			dto.setBlockName(blockmap.get(k).toString());
			List<HostelBlockUnitDTO> unitDtos = new ArrayList<HostelBlockUnitDTO>();
			v.forEach((y, z)-> {
				HostelBlockUnitDTO udto = new HostelBlockUnitDTO();
				udto.setId(y);
				udto.setHostelUnit(unitmap.get(y));
				List<HostelFloorDTO> floorDtos = new ArrayList<HostelFloorDTO>();
				z.forEach((a, b)-> {
					HostelFloorDTO fDto = new HostelFloorDTO();  
					fDto.setId(a);
					fDto.setFloorNumber(floormap.get(a));
					List<HostelRoomsDTO> roomDTO = new ArrayList<HostelRoomsDTO>();
					b.forEach((m, n)-> {
						roommap.forEach((v1,k1) -> {
							if(v1.equals(m)) {
								HostelRoomsDTO rDto = new HostelRoomsDTO();
								rDto.setId(m);
								rDto.setHostelRoomTypeDTO(new SelectDTO());
								rDto.getHostelRoomTypeDTO().setLabel(roommap.get(v1));
								rDto.setRoomNumber(roommap.get(m));
								rDto.setBedDetails(n);
								roomDTO.add(rDto);
							}
						});
					});
					roomDTO.sort(Comparator.comparing(HostelRoomsDTO::getId));
					fDto.setRoomDetails(roomDTO);
					floorDtos.add(fDto);
				});
				floorDtos.sort(Comparator.comparing(HostelFloorDTO::getFloorNumber));
				udto.setHostelFloorDTOSet(floorDtos);
				unitDtos.add(udto);
			});
			unitDtos.sort(Comparator.comparing(HostelBlockUnitDTO::getHostelUnit));
			dto.setHostelBlockUnitDTOSet(unitDtos);
			blockDtos.add(dto);
		});
		return Flux.fromIterable(blockDtos);
	}
	private HostelBedDTO setRoom(Tuple dbos) {
		HostelBedDTO bDto = new HostelBedDTO();
		bDto.setId(dbos.get("hostel_bed_id").toString());
		if(!Utils.isNullOrEmpty(dbos.get("bed_no"))) {
		bDto.setBedName(dbos.get("bed_no").toString());
		}
		if(!Utils.isNullOrEmpty(dbos.get("is_occupied"))) {
			String returnValue = dbos.get("is_occupied").toString();
			if (returnValue.equals("1")) {
				bDto.setIsOccupied(true);
			} else {
				bDto.setIsOccupied(false);
			}
		}
		if(!Utils.isNullOrEmpty(dbos.get("student_id"))) {
			bDto.setStudentDTO(new StudentDTO());
			bDto.getStudentDTO().setId(Integer.parseInt(dbos.get("student_id").toString()));
			if(!Utils.isNullOrEmpty(dbos.get("register_no"))) {
			bDto.getStudentDTO().setRegisterNo(dbos.get("register_no").toString());
			}
			if(!Utils.isNullOrEmpty(dbos.get("student_name"))) {
			bDto.getStudentDTO().setStudentName(dbos.get("student_name").toString());
			}
			bDto.getStudentDTO().setErpCampusProgrammeMappingId(new ErpCampusProgrammeMappingDTO());
			if(!Utils.isNullOrEmpty(dbos.get("programme_name").toString())) {
				bDto.getStudentDTO().getErpCampusProgrammeMappingId().setProgramName(dbos.get("programme_name").toString());
			}
			if(!Utils.isNullOrEmpty(dbos.get("campus_name").toString())) {
				bDto.getStudentDTO().getErpCampusProgrammeMappingId().setCampusName(dbos.get("campus_name").toString());
			}
		}
		return bDto;
	}
}