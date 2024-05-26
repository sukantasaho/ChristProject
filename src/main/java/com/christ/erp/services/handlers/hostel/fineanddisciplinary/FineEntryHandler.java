package com.christ.erp.services.handlers.hostel.fineanddisciplinary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelFineEntryDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.transactions.hostel.fineanddisciplinary.FineEntryTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FineEntryHandler {

	@Autowired
	FineEntryTransaction fineEntryTransaction;

	public Flux<HostelFineEntryDTO> getGridData(Integer academicYearId,Integer hostelId,Integer blockId,Integer unitId,String userId ) {		
		return fineEntryTransaction.getGridData( academicYearId, hostelId, blockId, unitId, userId ).flatMapMany(Flux::fromIterable).map(this::convertDboToDto);		
	}

	public HostelFineEntryDTO convertDboToDto(HostelFineEntryDBO dbo) {
		HostelFineEntryDTO dto = new HostelFineEntryDTO();
		dto.setId(dbo.getId());
		dto.setFineAmount(dbo.getFineAmount());
		dto.setDate(dbo.getDate());
		dto.setRemarks(dbo.getRemarks());
		dto.setFineCategoryDTO(new HostelFineCategoryDTO());
		dto.getFineCategoryDTO().setId(dbo.getHostelFineCategoryDBO().getId());
		dto.getFineCategoryDTO().setFineCategory(dbo.getHostelFineCategoryDBO().getFineCategory());
		dto.setIsFixedAmount(dbo.getHostelFineCategoryDBO().getAccFeeHeadsDBO().isFixedAmount());
		dto.setHostelAdmission(new HostelAdmissionsDTO());
		dto.getHostelAdmission().setId(dbo.getHostelAdmissionsDBO().getId());
		dto.getHostelAdmission().setStudentDTO(new StudentDTO());
		dto.getHostelAdmission().getStudentDTO().setStudentName(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentName());
		dto.getHostelAdmission().getStudentDTO().setRegisterNo(dbo.getHostelAdmissionsDBO().getStudentDBO().getRegisterNo());
		dto.getHostelAdmission().getStudentDTO().setAcaClassDTO(dbo.getHostelAdmissionsDBO().getStudentDBO().getAcaClassDBO().getClassName());
		dto.getHostelAdmission().setHostel(new SelectDTO());
		dto.getHostelAdmission().getHostel().setLabel(dbo.getHostelAdmissionsDBO().getHostelDBO().getHostelName());
		dto.getHostelAdmission().setBedNo(new SelectDTO());
		dto.getHostelAdmission().getBedNo().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getBedNo());
		dto.getHostelAdmission().setBlock(new SelectDTO());
		dto.getHostelAdmission().getBlock().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO()
				.getHostelFloorDBO().getHostelBlockUnitDBO().getHostelBlockDBO().getBlockName());
		dto.getHostelAdmission().setUnit(new SelectDTO());
		dto.getHostelAdmission().getUnit().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO()
				.getHostelFloorDBO().getHostelBlockUnitDBO().getHostelUnit());
		dto.getHostelAdmission().setRoomNo(new SelectDTO());
		dto.getHostelAdmission().getRoomNo().setLabel(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return fineEntryTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Mono<HostelFineEntryDTO> edit(int id) {
		return Mono.just(fineEntryTransaction.edit(id)).map(this::convertDboToDto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelFineEntryDTO> dto, String userId) {
		return dto
				.handle((hostelFineEntryDTO, synchronousSink) -> {
					synchronousSink.next(hostelFineEntryDTO);
				}).cast(HostelFineEntryDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						fineEntryTransaction.update(s);
					} else {
						fineEntryTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public HostelFineEntryDBO convertDtoToDbo(HostelFineEntryDTO dto, String userId) {
		HostelFineEntryDBO dbo = Utils.isNullOrEmpty(dto.getId()) ? new HostelFineEntryDBO() : fineEntryTransaction.edit(dto.getId());
		dbo.setDate(dto.getDate());
		dbo.setRemarks(dto.getRemarks());
		dbo.setFineAmount(dto.getFineAmount());
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		dbo.setHostelAdmissionsDBO(new HostelAdmissionsDBO());
		if(!Utils.isNullOrEmpty(dto.getHostelAdmission())) {
			dbo.getHostelAdmissionsDBO().setId(dto.getHostelAdmission().getId());
		}
		dbo.setHostelFineCategoryDBO(new HostelFineCategoryDBO());
		if(!Utils.isNullOrEmpty(dto.getFineCategoryDTO())) {
			dbo.getHostelFineCategoryDBO().setId(dto.getFineCategoryDTO().getId());
		}
		if (!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		dbo.setRecordStatus('A');
		return dbo;
	}
}
