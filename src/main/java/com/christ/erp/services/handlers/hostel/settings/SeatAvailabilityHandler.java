package com.christ.erp.services.handlers.hostel.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDetailsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;
import com.christ.erp.services.dto.hostel.settings.HostelRoomTypeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelSeatAvailabilityDTO;
import com.christ.erp.services.dto.hostel.settings.HostelSeatAvailabilityDetailsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.hostel.settings.SeatAvailabilityTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SeatAvailabilityHandler {

	@Autowired
	private SeatAvailabilityTransaction seatAvailabilityTransaction;	

	public Flux<HostelDTO> getHostelRoomType(String hostelId, String academicYearId ) {
		List<HostelDTO> dto3 = new ArrayList<HostelDTO>();
		List<Tuple> map = seatAvailabilityTransaction.getHostelRoomType(Integer.parseInt(hostelId), Integer.parseInt(academicYearId));
		Map<String, HostelDTO> map1 = new HashMap<String, HostelDTO>();
		map.forEach(dbo -> {
			if(!map1.containsKey(dbo.get("hostel_id").toString())) {
				HostelDTO dto = new HostelDTO();
				dto.setId(dbo.get("hostel_id").toString());
				dto.setHostelName(dbo.get("hostel_name").toString());
				dto.setHostelRoomTypeDTO(new ArrayList<HostelRoomTypeDTO>());
				HostelRoomTypeDTO roomDTO = new HostelRoomTypeDTO();
				roomDTO.setId(dbo.get("hostel_room_type_id").toString());
				roomDTO.setRoomType(dbo.get("room_type").toString());
				if(!Utils.isNullOrEmpty(dbo.get("totalSeat"))) { 
					roomDTO.setTotalSeats(Integer.parseInt(dbo.get("totalSeat").toString()));
				}
				if(!Utils.isNullOrEmpty(dbo.get("available_seats"))) {
					roomDTO.setAvailableSeats(Integer.parseInt(dbo.get("available_seats").toString()));
				}
				if(!Utils.isNullOrEmpty(dbo.get("allocatedCount"))) {
					roomDTO.setAllocatedSeat(Integer.parseInt(dbo.get("allocatedCount").toString()));
				}
				dto.getHostelRoomTypeDTO().add(roomDTO);
				map1.put(dbo.get("hostel_id").toString(), dto);
			} else {
				HostelDTO dtos = map1.get(dbo.get("hostel_id").toString());
				HostelRoomTypeDTO roomDTO = new HostelRoomTypeDTO();
				roomDTO.setId(dbo.get("hostel_room_type_id").toString());
				roomDTO.setRoomType(dbo.get("room_type").toString());
				if(!Utils.isNullOrEmpty(dbo.get("totalSeat"))) { 
					roomDTO.setTotalSeats(Integer.parseInt(dbo.get("totalSeat").toString()));
				}
				if(!Utils.isNullOrEmpty(dbo.get("available_seats"))) {
					roomDTO.setAvailableSeats(Integer.parseInt(dbo.get("available_seats").toString()));
				}
				if(!Utils.isNullOrEmpty(dbo.get("allocatedCount"))) {
					roomDTO.setAllocatedSeat(Integer.parseInt(dbo.get("allocatedCount").toString()));
				}
				dtos.getHostelRoomTypeDTO().add(roomDTO);
				map1.replace(dbo.get("hostel_id").toString(), dtos);
			}
		});
		map1.forEach((k,v) -> {
			dto3.add(v);
		});
		return Flux.just(dto3.get(0));		
	}

	public Flux<HostelSeatAvailabilityDTO> getGridData(String yearId) {
		return seatAvailabilityTransaction.getGridData(yearId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO2);
	}

	public HostelSeatAvailabilityDTO convertDBOToDTO2 (HostelSeatAvailabilityDBO dbo) {
		HostelSeatAvailabilityDTO dto = new HostelSeatAvailabilityDTO();
		dto.setId(dbo.getId());
		if(!Utils.isNullOrEmpty(dbo.getHostelDBO())) {
			dto.setHostelDTO(new SelectDTO());
			dto.getHostelDTO().setValue(String.valueOf(dbo.getHostelDBO().getId()));
			dto.getHostelDTO().setLabel(dbo.getHostelDBO().getHostelName());
		}
		if(!Utils.isNullOrEmpty(dbo.getAcademicYearDBO())) {
			dto.setAcademicYearDTO(new SelectDTO());
			dto.getAcademicYearDTO().setValue(String.valueOf(dbo.getAcademicYearDBO().getId()));
			dto.getAcademicYearDTO().setLabel(dbo.getAcademicYearDBO().getAcademicYearName());
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelSeatAvailabilityDTO> dto, String userId) {
		return dto.handle((hostelSeatAvailabilityDTO, synchronousSink) -> {
			boolean istrue = seatAvailabilityTransaction.duplicateCheck(hostelSeatAvailabilityDTO);
			if (istrue) {
				synchronousSink.error(new DuplicateException("Seat already added for academic year"));
			} else {
				synchronousSink.next(hostelSeatAvailabilityDTO);
			}
		}).cast(HostelSeatAvailabilityDTO.class).map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
			if(!Utils.isNullOrEmpty(s.getId())) {
				seatAvailabilityTransaction.update(s);
			} else {
				seatAvailabilityTransaction.save(s);
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	private HostelSeatAvailabilityDBO convertDtoToDbo(HostelSeatAvailabilityDTO dto, String userId) {
		HostelSeatAvailabilityDBO dbo = new HostelSeatAvailabilityDBO();
		dbo.setId(dto.getId());
		if(!Utils.isNullOrEmpty(dto.getAcademicYearDTO())) {
			dbo.setAcademicYearDBO(new ErpAcademicYearDBO());
			dbo.getAcademicYearDBO().setId(Integer.parseInt(dto.getAcademicYearDTO().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getHostelDTO())) {
			dbo.setHostelDBO(new HostelDBO());
			dbo.getHostelDBO().setId(Integer.parseInt(dto.getHostelDTO().getValue()));
		}
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		if(!Utils.isNullOrEmpty(dto.getHostelSeatAvailabilityDetails())) {
			Set<HostelSeatAvailabilityDetailsDBO> hostelSeatAvailabilityDetailsDBOs = new HashSet<HostelSeatAvailabilityDetailsDBO>();  
			dto.getHostelSeatAvailabilityDetails().forEach(dtos -> {
				HostelSeatAvailabilityDetailsDBO dbos = new HostelSeatAvailabilityDetailsDBO();
				dbos.setId(dtos.getId());
				dbos.setHostelSeatAvailabilityDBO(dbo);
				if(!Utils.isNullOrEmpty(dtos.getTotalSeats())) {
					dbos.setTotalSeats(dtos.getTotalSeats());
				}
				if(!Utils.isNullOrEmpty(dtos.getAvailableSeats())) {
					dbos.setAvailableSeats(dtos.getAvailableSeats());
				}
				if(!Utils.isNullOrEmpty(dtos.getHostelRoomTypeDTO())) {
					dbos.setHostelRoomTypeDBO(new HostelRoomTypeDBO());
					dbos.getHostelRoomTypeDBO().setId(Integer.parseInt(dtos.getHostelRoomTypeDTO().id));
				}
				dbos.setCreatedUsersId(Integer.parseInt(userId));
				if (!Utils.isNullOrEmpty(dtos.getId())) {
					dbos.setModifiedUsersId(Integer.parseInt(userId));
				}
				dbos.setRecordStatus('A');
				hostelSeatAvailabilityDetailsDBOs.add(dbos); 
			});
			dbo.setHostelSeatAvailabilityDetailsDBO(hostelSeatAvailabilityDetailsDBOs);
		}
		return dbo;
	}

	public HostelSeatAvailabilityDTO convertDBOToDTO1 (HostelSeatAvailabilityDBO dbo) {
		HostelSeatAvailabilityDTO dto = new HostelSeatAvailabilityDTO();
		dto.setId(dbo.getId());
		if(!Utils.isNullOrEmpty(dbo.getHostelDBO())) {
			dto.setHostelDTO(new SelectDTO());
			dto.getHostelDTO().setValue(String.valueOf(dbo.getHostelDBO().getId()));
			dto.getHostelDTO().setLabel(dbo.getHostelDBO().getHostelName());
		}
		if(!Utils.isNullOrEmpty(dbo.getAcademicYearDBO())) {
			dto.setAcademicYearDTO(new SelectDTO());
			dto.getAcademicYearDTO().setValue(String.valueOf(dbo.getAcademicYearDBO().getId()));
			dto.getAcademicYearDTO().setLabel(dbo.getAcademicYearDBO().getAcademicYearName());
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelSeatAvailabilityDetailsDBO())) {
			List<HostelSeatAvailabilityDetailsDTO> seatAvailability = new ArrayList<HostelSeatAvailabilityDetailsDTO>();
			dbo.getHostelSeatAvailabilityDetailsDBO().forEach(subDbo -> {
				HostelSeatAvailabilityDetailsDTO seatAvailabilityDTO = new HostelSeatAvailabilityDetailsDTO();  
				seatAvailabilityDTO.setId(subDbo.getId());
				if(!Utils.isNullOrEmpty(subDbo.getTotalSeats())) {
				seatAvailabilityDTO.setTotalSeats(subDbo.getTotalSeats());
				}
				if(!Utils.isNullOrEmpty(subDbo.getAvailableSeats())) {
				seatAvailabilityDTO.setAvailableSeats(subDbo.getAvailableSeats());
				}
				if(!Utils.isNullOrEmpty(subDbo.getHostelRoomTypeDBO())) {
					seatAvailabilityDTO.setHostelRoomTypeDTO(new HostelRoomTypeDTO());
					seatAvailabilityDTO.getHostelRoomTypeDTO().setId(String.valueOf(subDbo.getHostelRoomTypeDBO().getId()));
					seatAvailabilityDTO.getHostelRoomTypeDTO().setRoomType(subDbo.getHostelRoomTypeDBO().getRoomType());
				}
				seatAvailability.add(seatAvailabilityDTO);
			});
			dto.setHostelSeatAvailabilityDetails(seatAvailability);
		}
		return dto;
	}

	public Mono<HostelSeatAvailabilityDTO> edit(int id) {
		return seatAvailabilityTransaction.edit(id).map(this::convertDBOToDTO1);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return seatAvailabilityTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}	
}
