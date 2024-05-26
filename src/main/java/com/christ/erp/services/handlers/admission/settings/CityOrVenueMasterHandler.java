package com.christ.erp.services.handlers.admission.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessCenterDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessCenterDetailsDTO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.admission.settings.CityOrVenueMasterTransaction;

public class CityOrVenueMasterHandler {

private static volatile CityOrVenueMasterHandler  cityOrVenueMasterHandler=null;
	
    CityOrVenueMasterTransaction transaction=CityOrVenueMasterTransaction.getInstance();
    
	public static CityOrVenueMasterHandler getInstance() {
        if(cityOrVenueMasterHandler==null) {
        	cityOrVenueMasterHandler = new CityOrVenueMasterHandler();
        }
        return cityOrVenueMasterHandler;
    }

	public List<AdmSelectionProcessVenueCityDTO> getGridData() throws Exception {
		List<AdmSelectionProcessVenueCityDTO> dto = null;
		List<Tuple> mappings = transaction.getGridData();
		if(!Utils.isNullOrEmpty(mappings)) {
			dto = new ArrayList<AdmSelectionProcessVenueCityDTO>();
	        for(Tuple mapping : mappings) {
	        	AdmSelectionProcessVenueCityDTO mappingInfo = new AdmSelectionProcessVenueCityDTO(); 	
	            if(!Utils.isNullOrEmpty(mapping.get("Mode"))) {
	            	if(!Utils.isNullOrEmpty(mapping.get("ID"))) {
		                mappingInfo.id = mapping.get("ID").toString();
		        	}
	            	if(String.valueOf(mapping.get("Mode")).equals("Others")) {
		            	ExModelBaseDTO  exBaseDTO = new ExModelBaseDTO();
		            	exBaseDTO.text = mapping.get("Mode").toString();
		                mappingInfo.mode = exBaseDTO;
	            	}
	            	else if(String.valueOf(mapping.get("Mode")).equals("Online Entrance")) {
		            	ExModelBaseDTO  exBaseDTO = new ExModelBaseDTO();
		            	exBaseDTO.text = mapping.get("Mode").toString();
		                mappingInfo.mode = exBaseDTO;
	            	}else if(String.valueOf(mapping.get("Mode")).equals("Center Based Entrance")) {
	            		ExModelBaseDTO  exBaseDTO = new ExModelBaseDTO();
		            	exBaseDTO.text = mapping.get("Mode").toString();
		                mappingInfo.mode = exBaseDTO;
	            	}
	            	if(!Utils.isNullOrEmpty(mapping.get("Venue"))) {
	            		mappingInfo.venue = mapping.get("Venue").toString();
	            	}
	            	dto.add(mappingInfo);
	            }   
	       }
		}
		return dto;
	}

	public boolean delete(String id, String userId) throws Exception {
		return transaction.delete(id, userId);
	}
	
	public boolean saveOrUpdate(AdmSelectionProcessVenueCityDTO data, ApiResult<ModelBaseDTO> result, String userId) throws Exception {
		boolean isSaved = false;
		boolean duplicated = false;
		AdmSelectionProcessVenueCityDBO dbo = null;
		AdmSelectionProcessCenterDetailsDBO centerDbo = null;
		Set<AdmSelectionProcessCenterDetailsDBO> listCenterDbo = null;
		duplicated = duplicateCheck(data, result);
		if(!duplicated) {
			if(!Utils.isNullOrEmpty(data)) {
				if(Utils.isNullOrEmpty(data.id)) {
					dbo = new AdmSelectionProcessVenueCityDBO();
					dbo.createdUsersId = Integer.parseInt(userId);
				}else {
					dbo = transaction.edit(Integer.parseInt(data.id));
					dbo.modifiedUsersId = Integer.parseInt(userId);
				}
				if(!Utils.isNullOrEmpty(data.mode.text) && !Utils.isNullOrEmpty(dbo)) {
					if(data.mode.text.equals("Others") || data.mode.text.equals("Center Based Entrance")) {
						dbo.selectionProcessMode = data.mode.text;
						if(!Utils.isNullOrEmpty(data.country.id)) {
							ErpCountryDBO country = new ErpCountryDBO();
							country.id = Integer.parseInt(data.country.id);
							dbo.erpCountryDBO = country;
						}else
							dbo.erpCountryDBO = null;
						if(!Utils.isNullOrEmpty(data.state.id)) {
							ErpStateDBO state = new ErpStateDBO();
							state.id = Integer.parseInt(data.state.id);
							dbo.erpStateDBO = state;
						}else
							dbo.erpStateDBO = null;
					 	if(!Utils.isNullOrEmpty(data.venue)) 
						    dbo.venueName = data.venue;
					 	else
					 		dbo.venueName = null;
						if(!Utils.isNullOrEmpty(data.address)) 
						    dbo.venueAddress = data.address;
						else
							dbo.venueAddress = null;
						if(!Utils.isNullOrEmpty(data.maxSeats)) 
						    dbo.venueMaxSeats = Integer.parseInt(data.maxSeats);
						else 
							dbo.venueMaxSeats = null;
						if(data.mode.text.equals("Center Based Entrance")){
							if(!Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(data.centerDetails)) {
								if(Utils.isNullOrEmpty(data.id)) {
									listCenterDbo = new HashSet<AdmSelectionProcessCenterDetailsDBO>();
									for (AdmSelectionProcessCenterDetailsDTO centerDto : data.centerDetails) {		
										if(!Utils.isNullOrEmpty(centerDto) && Utils.isNullOrEmpty(centerDto.id)) {
											centerDbo = new AdmSelectionProcessCenterDetailsDBO();
											centerDbo = getNewCenterDBO(dbo, centerDbo, centerDto, userId);
										} 
										listCenterDbo.add(centerDbo);
									}
									dbo.centerDetailsDBOs =listCenterDbo;
								}
								else {
									List<Integer> activeRecordIds = null;
									if(!Utils.isNullOrEmpty(dbo.centerDetailsDBOs) && !Utils.isNullOrEmpty(data.centerDetails)) {
										activeRecordIds = new ArrayList<Integer>();
										for (AdmSelectionProcessCenterDetailsDBO centerDboUpdate : dbo.centerDetailsDBOs) {
											for (AdmSelectionProcessCenterDetailsDTO centerDto : data.centerDetails) {												  
												if(!Utils.isNullOrEmpty(centerDboUpdate.id) && !Utils.isNullOrEmpty(centerDto.id)
														&& centerDto.id.trim().equals(String.valueOf(centerDboUpdate.id).trim())) {
													activeRecordIds.add(Integer.parseInt(centerDto.id.trim()));
													centerDboUpdate = getNewCenterDBO(dbo, centerDboUpdate, centerDto, userId);
												}	
											}
	                                     }
										for (AdmSelectionProcessCenterDetailsDTO  newCenterDto: data.centerDetails) {
											if(Utils.isNullOrEmpty(newCenterDto.id)) {
												centerDbo = new AdmSelectionProcessCenterDetailsDBO();
												centerDbo = getNewCenterDBO(dbo, centerDbo, newCenterDto, userId);
												dbo.centerDetailsDBOs.add(centerDbo);
											}
										}
										for (AdmSelectionProcessCenterDetailsDBO centerDboUpdate : dbo.centerDetailsDBOs) {
											if(!Utils.isNullOrEmpty(centerDboUpdate.id) && !activeRecordIds.contains(centerDboUpdate.id)) {
												 if(centerDboUpdate.recordStatus == 'A' || centerDboUpdate.recordStatus == 'I') {
													 centerDboUpdate.recordStatus = 'D';
													 centerDboUpdate.modifiedUsersId = Integer.parseInt(userId);
												 }
											}
										}
									}else {
										for (AdmSelectionProcessCenterDetailsDTO  newCenterDto: data.centerDetails) {
											if(Utils.isNullOrEmpty(newCenterDto.id)) {
												centerDbo = new AdmSelectionProcessCenterDetailsDBO();
												centerDbo = getNewCenterDBO(dbo, centerDbo, newCenterDto, userId);
												dbo.centerDetailsDBOs.add(centerDbo);
											}
										}
									}
								}
							}
						}
					}else if(data.mode.text.equals("Online Entrance")) {
						if(!Utils.isNullOrEmpty(data.venue)) 
							dbo.venueName = data.venue;
						    dbo.selectionProcessMode = data.mode.text;
							if(!Utils.isNullOrEmpty(data.maxSeats)) 
								dbo.venueMaxSeats = Integer.parseInt(data.maxSeats);
					}
				}
				dbo.recordStatus = 'A';
				isSaved = transaction.saveOrUpdate(dbo);
			}
		}
		return isSaved;
	}

	private boolean duplicateCheck(AdmSelectionProcessVenueCityDTO data, ApiResult<ModelBaseDTO> result) throws Exception {
		boolean duplicateCheck = false;
		List<AdmSelectionProcessVenueCityDBO> dbos = null;
		dbos = transaction.getDuplicateCheck(data);
		if(!Utils.isNullOrEmpty(dbos)) {
			duplicateCheck = true;
			result.failureMessage = "Duplicate entry for Selection Process Venue/City";
		}
		return duplicateCheck;
	}

	public AdmSelectionProcessVenueCityDTO edit(String id) throws Exception {
		AdmSelectionProcessVenueCityDTO dto = null;
		if(!Utils.isNullOrEmpty(id)) {
			AdmSelectionProcessVenueCityDBO dbo = transaction.edit(Integer.parseInt(id));
			if(!Utils.isNullOrEmpty(dbo)) {
				dto = new AdmSelectionProcessVenueCityDTO();
				if(!Utils.isNullOrEmpty(dbo.id)) {
				   dto.id = String.valueOf(dbo.id);
				}
				if(!Utils.isNullOrEmpty(dbo.selectionProcessMode)) {
					ExModelBaseDTO mode = new ExModelBaseDTO();
                    mode.text = dbo.selectionProcessMode;
					dto.mode = mode;
					if(dbo.selectionProcessMode.equals("Center Based Entrance")) {
						if(!Utils.isNullOrEmpty(dbo.id)) {			
							List<AdmSelectionProcessCenterDetailsDTO> centerListDto = null;
							AdmSelectionProcessCenterDetailsDTO centerDto = null;
							if(!Utils.isNullOrEmpty(dbo.centerDetailsDBOs) ) {
								centerListDto = new ArrayList<AdmSelectionProcessCenterDetailsDTO>();
								for (AdmSelectionProcessCenterDetailsDBO centerDetailsDBO : dbo.centerDetailsDBOs) {
									if(!Utils.isNullOrEmpty(centerDetailsDBO.recordStatus) && centerDetailsDBO.recordStatus=='A' || centerDetailsDBO.recordStatus=='I') {
										centerDto = new AdmSelectionProcessCenterDetailsDTO();
										centerDto.id = String.valueOf(centerDetailsDBO.id);
										centerDto.center = centerDetailsDBO.centerName;
										centerDto.centerCode = centerDetailsDBO.centerCode;
										centerDto.address = centerDetailsDBO.centerAddress;
										if(centerDetailsDBO.recordStatus=='A')
										    centerDto.active = true;
										else if(centerDetailsDBO.recordStatus=='I')
											centerDto.active = false;
										centerDto.maxSeats = String.valueOf(centerDetailsDBO.centerMaxSeats);
										centerDto.priorityOrder = String.valueOf(centerDetailsDBO.centerPriorityOrder);
										centerListDto.add(centerDto);
									}
								}
								dto.centerDetails = centerListDto;
							}
						}
					}
				}
				if(!Utils.isNullOrEmpty(dbo.erpCountryDBO)) {
					ExModelBaseDTO countryDTO = new ExModelBaseDTO();
					countryDTO.id = String.valueOf(dbo.erpCountryDBO.id);
					dto.country = countryDTO;
				}
				if(!Utils.isNullOrEmpty(dbo.erpStateDBO)) {
					ExModelBaseDTO stateDTO = new ExModelBaseDTO();
					stateDTO.id = String.valueOf(dbo.erpStateDBO.id);
					dto.state = stateDTO;
				}
				if(!Utils.isNullOrEmpty(dbo.venueAddress)) {
					dto.address = dbo.venueAddress;
				}
				if(!Utils.isNullOrEmpty(dbo.venueMaxSeats)) {
					dto.maxSeats = String.valueOf(dbo.venueMaxSeats);
				}
				if(!Utils.isNullOrEmpty(dbo.venueName)) {
					dto.venue = dbo.venueName;
				}
			}
		}
		return dto;
	} 
	
	public AdmSelectionProcessCenterDetailsDBO getNewCenterDBO(AdmSelectionProcessVenueCityDBO dbo, AdmSelectionProcessCenterDetailsDBO centerDbo,
			AdmSelectionProcessCenterDetailsDTO centerDto, String userId) {
		if(!Utils.isNullOrEmpty(centerDbo)) {
			centerDbo.admSelectionProcessVenueCityDBO = dbo;
			if(Utils.isNullOrEmpty(centerDto.id))
				centerDbo.createdUsersId = Integer.parseInt(userId);
			if(!Utils.isNullOrEmpty(centerDto.center))
			    centerDbo.centerName = centerDto.center;
			if(!Utils.isNullOrEmpty(centerDto.centerCode))
				centerDbo.centerCode = centerDto.centerCode;
			if(!Utils.isNullOrEmpty(centerDto.address))
				centerDbo.centerAddress = centerDto.address;
			if(!Utils.isNullOrEmpty(centerDto.maxSeats))
				centerDbo.centerMaxSeats = Integer.parseInt(centerDto.maxSeats.trim());
			if(!Utils.isNullOrEmpty(centerDto.priorityOrder))
				centerDbo.centerPriorityOrder = Integer.parseInt(centerDto.priorityOrder.trim());
			if(!Utils.isNullOrEmpty(centerDto.active)) {
			 if(centerDto.active == true)
				    centerDbo.recordStatus = 'A';
			 else if(centerDto.active == false)
					centerDbo.recordStatus = 'I';
			}
			if(!Utils.isNullOrEmpty(centerDto.id)) {
				centerDbo.modifiedUsersId = Integer.parseInt(userId);
			}
	    }
		return centerDbo;
    }
}
