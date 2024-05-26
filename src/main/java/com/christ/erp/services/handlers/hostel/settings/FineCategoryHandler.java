package com.christ.erp.services.handlers.hostel.settings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.dto.account.AccFeeHeadsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.hostel.settings.FineCategoryTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FineCategoryHandler {

	@Autowired
	FineCategoryTransaction fineCategoryTransaction;
	
	public Flux<HostelFineCategoryDTO> getGridData() {
		List<HostelFineCategoryDBO> fineCategoryDBOList = fineCategoryTransaction.getGridData();
		return this.convertDboToDto(fineCategoryDBOList);
	}

	private Flux<HostelFineCategoryDTO> convertDboToDto(List<HostelFineCategoryDBO> fineCategoryDBOList) {
		List<HostelFineCategoryDTO> fineCategoryDTO = new ArrayList<HostelFineCategoryDTO>();
		if(!Utils.isNullOrEmpty(fineCategoryDBOList)) {
			fineCategoryDBOList.forEach(fineCategoryDBO -> {
				HostelFineCategoryDTO fineDTO = new HostelFineCategoryDTO();
				fineDTO.setId(fineCategoryDBO.getId());
				if(!Utils.isNullOrEmpty(fineCategoryDBO.getFineCategory()))	{
					fineDTO.setFineCategory(fineCategoryDBO.getFineCategory());
				}
				if(!Utils.isNullOrEmpty(fineCategoryDBO.getFineAmount())) {
					fineDTO.setFineAmount(fineCategoryDBO.getFineAmount());
				}
				if(!Utils.isNullOrEmpty(fineCategoryDBO.getHostelDBO())) {
					fineDTO.setHostelDTO(new SelectDTO());
					fineDTO.getHostelDTO().setValue(String.valueOf(fineCategoryDBO.getHostelDBO().getId()));
					fineDTO.getHostelDTO().setLabel(fineCategoryDBO.getHostelDBO().getHostelName());
				}
				if(fineCategoryDBO.getIsAbsentFine().equals(true)) {
					fineDTO.setFineGroup("Absent Fine");
				}
				if(fineCategoryDBO.getIsDisciplinaryFine().equals(true)) {
					fineDTO.setFineGroup("Disciplinary Action Fine");
				}
				if(fineCategoryDBO.getIsOthersFine().equals(true)) {
					fineDTO.setFineGroup("Other Fine");
				}
				fineCategoryDTO.add(fineDTO);
			});	
		}
		return Flux.fromIterable(fineCategoryDTO);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return fineCategoryTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Mono<HostelFineCategoryDTO> edit(int id) {
		HostelFineCategoryDBO dbo = fineCategoryTransaction.edit(id);
		return convertDboToDto1(dbo);
	}

	private Mono<HostelFineCategoryDTO> convertDboToDto1(HostelFineCategoryDBO dbo) {
		HostelFineCategoryDTO fineDTO = new HostelFineCategoryDTO();
		fineDTO.setId(dbo.getId());
		if(!Utils.isNullOrEmpty(dbo.getFineAmount())) {
		fineDTO.setFineAmount(dbo.getFineAmount());
		}
		if(!Utils.isNullOrEmpty(dbo.getFineCategory())) {
		fineDTO.setFineCategory(dbo.getFineCategory());
		}
		if(!Utils.isNullOrEmpty(dbo.getHostelDBO())) {
		fineDTO.setHostelDTO(new SelectDTO());
		fineDTO.getHostelDTO().setValue(String.valueOf(dbo.getHostelDBO().getId()));
		fineDTO.getHostelDTO().setLabel(dbo.getHostelDBO().getHostelName());
		}
		if(!Utils.isNullOrEmpty(dbo.getAccFeeHeadsDBO())) {
			fineDTO.setAccFeeHeadsDTO(new AccFeeHeadsDTO());	
			fineDTO.getAccFeeHeadsDTO().setId(dbo.getAccFeeHeadsDBO().getId());
			fineDTO.getAccFeeHeadsDTO().setHeading(dbo.getAccFeeHeadsDBO().getHeading());
		}
		if(dbo.getIsAbsentFine().equals(true)) {
			fineDTO.setIsAbsentFine(true);	
		} else {
			fineDTO.setIsAbsentFine(false);	
		}
	    if(dbo.getIsDisciplinaryFine().equals(true)) {
			fineDTO.setIsDisciplinaryFine(true);
		} else {
			fineDTO.setIsDisciplinaryFine(false);
		}
	    if(dbo.getIsOthersFine().equals(true)) {
	    	fineDTO.setIsOthersFine(true);
	    }
		else {
			fineDTO.setIsOthersFine(false);
		}
		return Mono.just(fineDTO);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<HostelFineCategoryDTO> dto, String userId) {
		return dto
				.handle((HostelFineCategoryDTO, synchronousSink) -> {
				if(!HostelFineCategoryDTO.getIsOthersFine()) {
					boolean istrue = fineCategoryTransaction.duplicateCheck(HostelFineCategoryDTO);
					if (istrue) {
						synchronousSink.error(new DuplicateException("Fine Amount already added for this hostel"));
					} else {
						synchronousSink.next(HostelFineCategoryDTO);
					} 
				} else if(fineCategoryTransaction.isDuplicate(HostelFineCategoryDTO)) {
					synchronousSink.error(new DuplicateException("Category already added for this hostel"));
				} else {
	    			 synchronousSink.next(HostelFineCategoryDTO); 
	    		   }
				}).cast(HostelFineCategoryDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						fineCategoryTransaction.update(s);
					} else {
						fineCategoryTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private HostelFineCategoryDBO convertDtoToDbo(HostelFineCategoryDTO dto, String userId) {
		HostelFineCategoryDBO dbo = null;
		if (!Utils.isNullOrEmpty(dto.getId())) {
		    dbo = fineCategoryTransaction.edit(dto.getId());
		} 
		if(Utils.isNullOrEmpty(dbo)) {
			dbo = new HostelFineCategoryDBO();
			dbo.setCreatedUsersId(Integer.parseInt(userId));
		} else {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		if(!Utils.isNullOrEmpty(dto.getFineCategory())) {
			dbo.setFineCategory(dto.getFineCategory());
		}
		if(!Utils.isNullOrEmpty(dto.getFineAmount())) {
			dbo.setFineAmount(dto.getFineAmount());
		}
		if(!Utils.isNullOrEmpty(dto.getHostelDTO())) {
			dbo.setHostelDBO(new HostelDBO());
			dbo.getHostelDBO().setId(Integer.parseInt(dto.getHostelDTO().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getAccFeeHeadsDTO())) {
			dbo.setAccFeeHeadsDBO(new AccFeeHeadsDBO());
			dbo.getAccFeeHeadsDBO().setId(dto.getAccFeeHeadsDTO().getId());
		}
		if(dto.getIsAbsentFine().equals(true)) {
			dbo.setIsAbsentFine(true);
		} else {
			dbo.setIsAbsentFine(false);
		}
		if(dto.getIsDisciplinaryFine().equals(true)) {
			dbo.setIsDisciplinaryFine(true);
		} else {
			dbo.setIsDisciplinaryFine(false);
		}
		if(dto.getIsOthersFine().equals(true)) {
			dbo.setIsOthersFine(true);
		} else {
			dbo.setIsOthersFine(false);
		}
		dbo.setRecordStatus('A');
		return dbo;
	}
}
