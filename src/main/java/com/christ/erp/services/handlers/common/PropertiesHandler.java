package com.christ.erp.services.handlers.common;

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
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.common.SysPropertiesDBO;
import com.christ.erp.services.dbobjects.common.SysPropertiesDetailsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.common.SysPropertiesDTO;
import com.christ.erp.services.dto.common.SysPropertiesDetailsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.common.PropertiesTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PropertiesHandler {

	@Autowired
	PropertiesTransaction propertiesTransaction;

	public Flux<SysPropertiesDTO> getGridData(){
		return propertiesTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}

	public SysPropertiesDTO convertDboToDto(SysPropertiesDBO dbo) {
		SysPropertiesDTO sysPropertiesDTO = new SysPropertiesDTO();
		BeanUtils.copyProperties(dbo, sysPropertiesDTO);
		if(!Utils.isNullOrEmpty(dbo.getSysPropertiesDetailsDBOSet())) {
			List<SysPropertiesDetailsDTO> sysPropertiesDetailsDTOList = new ArrayList<SysPropertiesDetailsDTO>();
			dbo.getSysPropertiesDetailsDBOSet().forEach(dbodetsils -> {
				if(dbodetsils.getRecordStatus() == 'A') {
					SysPropertiesDetailsDTO sysPropertiesDetailsDTO = new SysPropertiesDetailsDTO();
					if(!Utils.isNullOrEmpty(dbodetsils.getId())) {
						sysPropertiesDetailsDTO.setId(dbodetsils.getId());
					}
					if(!Utils.isNullOrEmpty(dbodetsils.getPropertyDetailValue())) {
						sysPropertiesDetailsDTO.setPropertyDetailValue(dbodetsils.getPropertyDetailValue());
					}
					sysPropertiesDetailsDTO.setErpCampusDTO(new SelectDTO());
					if(!Utils.isNullOrEmpty(dbodetsils.getErpCampusDBO())) {
						sysPropertiesDTO.setPropertyType("C");
						sysPropertiesDetailsDTO.getErpCampusDTO().setValue(String.valueOf(dbodetsils.getErpCampusDBO().getId()));
						sysPropertiesDetailsDTO.getErpCampusDTO().setLabel(dbodetsils.getErpCampusDBO().getCampusName());
					}
					sysPropertiesDetailsDTO.setErpLocationDTO(new SelectDTO());
					if(!Utils.isNullOrEmpty(dbodetsils.getErpLocationDBO())) {
						sysPropertiesDTO.setPropertyType("L");
						sysPropertiesDetailsDTO.getErpLocationDTO().setValue(String.valueOf(dbodetsils.getErpLocationDBO().getId()));
						sysPropertiesDetailsDTO.getErpLocationDTO().setLabel(dbodetsils.getErpLocationDBO().getLocationName());
					}
					sysPropertiesDetailsDTOList.add(sysPropertiesDetailsDTO);
				}
			});
			sysPropertiesDTO.setSysPropertiesDetailsDTOList(sysPropertiesDetailsDTOList);
		}
		return sysPropertiesDTO;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return propertiesTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<SysPropertiesDTO> dto, String userId) {
		return dto
				.handle((sysPropertiesDTO, synchronousSink) ->  {
					boolean isTrue = propertiesTransaction.duplicateCheck(sysPropertiesDTO);
					if(isTrue) {
						synchronousSink.error(new DuplicateException("The Property Name is already added"));
					}else {
						synchronousSink.next(sysPropertiesDTO);
					}
				}).cast(SysPropertiesDTO.class)
				.map(data -> convertDtoToSysPropertiesDbo(data, userId))
				.flatMap( s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						propertiesTransaction.update(s);
					}else {
						propertiesTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private SysPropertiesDBO convertDtoToSysPropertiesDbo(SysPropertiesDTO dto, String userId) {
		SysPropertiesDBO dbo = null;
		if(Utils.isNullOrEmpty(dto.getId())) {
			dbo = new SysPropertiesDBO();
			dbo.setCreatedUsersId(Integer.parseInt(userId));
		}else {
			dbo = propertiesTransaction.edit(dto.getId());
			dbo.setModifiedUsersId(Integer.parseInt(userId));		
		}	
		dbo.setRecordStatus('A');
		BeanUtils.copyProperties(dto, dbo);
		Set<SysPropertiesDetailsDBO> existSysDetailsSet = null;
		if(!Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(dbo.getSysPropertiesDetailsDBOSet())) {
			existSysDetailsSet =  !Utils.isNullOrEmpty(dbo) ?  dbo.getSysPropertiesDetailsDBOSet() : null;
		}
		Map<Integer, SysPropertiesDetailsDBO> sysPropertiesDetailsDBOMap = new HashMap<Integer, SysPropertiesDetailsDBO>();
		if(!Utils.isNullOrEmpty(existSysDetailsSet)) {
			existSysDetailsSet.forEach(existSysProperties -> {
				if(existSysProperties.getRecordStatus() == 'A') {
					sysPropertiesDetailsDBOMap.put(existSysProperties.getId(), existSysProperties);
				}
			});
		}
		Set<SysPropertiesDetailsDBO> sysPropertiesDetailsDBOSet = new HashSet<SysPropertiesDetailsDBO>();
		if(!Utils.isNullOrEmpty(dto.getCommonProperty())) {
			if(!dto.getCommonProperty()) {
				if(!Utils.isNullOrEmpty(dto.getSysPropertiesDetailsDTOList())) {
					SysPropertiesDBO sysPropertiesDBO = dbo;
					dto.getSysPropertiesDetailsDTOList().forEach(sysDetails -> {
						SysPropertiesDetailsDBO sysPropertiesDetailsDBO = null;
						if(sysPropertiesDetailsDBOMap.containsKey(sysDetails.getId())) {
							sysPropertiesDetailsDBO = sysPropertiesDetailsDBOMap.get(sysDetails.getId());
							sysPropertiesDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
							sysPropertiesDetailsDBOMap.remove(sysDetails.getId());
						}else {
							sysPropertiesDetailsDBO = new SysPropertiesDetailsDBO();
							sysPropertiesDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						}
						if(!Utils.isNullOrEmpty(sysDetails.getErpCampusDTO())) {
							if(!Utils.isNullOrEmpty(sysDetails.getErpCampusDTO().getValue())) {
								sysPropertiesDetailsDBO.setErpCampusDBO(new ErpCampusDBO());
								sysPropertiesDetailsDBO.getErpCampusDBO().setId(Integer.parseInt(sysDetails.getErpCampusDTO().getValue()));
							}
						}
						if(!Utils.isNullOrEmpty(sysDetails.getErpLocationDTO())) {
							if(!Utils.isNullOrEmpty(sysDetails.getErpLocationDTO().getValue())) {
								sysPropertiesDetailsDBO.setErpLocationDBO(new ErpLocationDBO());
								sysPropertiesDetailsDBO.getErpLocationDBO().setId(Integer.parseInt(sysDetails.getErpLocationDTO().getValue()));
							}
						}
						if(!Utils.isNullOrEmpty(sysDetails.getPropertyDetailValue())) {
							sysPropertiesDetailsDBO.setPropertyDetailValue(sysDetails.getPropertyDetailValue());
						}
						sysPropertiesDetailsDBO.setSysPropertiesDBO(sysPropertiesDBO);
						sysPropertiesDetailsDBO.setRecordStatus('A');
						sysPropertiesDetailsDBOSet.add(sysPropertiesDetailsDBO);
					});	
				}
				if(!Utils.isNullOrEmpty(sysPropertiesDetailsDBOMap)) {
					sysPropertiesDetailsDBOMap.forEach((entry, value)-> {
						value.setModifiedUsersId( Integer.parseInt(userId));
						value.setRecordStatus('D');
						sysPropertiesDetailsDBOSet.add(value);
					});
				}
				dbo.setSysPropertiesDetailsDBOSet(sysPropertiesDetailsDBOSet);
			}
		}	
		return dbo;
	}

	public Mono<SysPropertiesDTO> edit(int id) {
		SysPropertiesDBO sysPropertiesDBO = propertiesTransaction.edit(id);
		SysPropertiesDTO dto = this.convertDboToDto(sysPropertiesDBO);
		dto.setIsEdit(true);
		return Mono.just(dto);
	}	
}
