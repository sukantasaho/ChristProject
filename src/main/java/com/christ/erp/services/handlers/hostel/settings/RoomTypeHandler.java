package com.christ.erp.services.handlers.hostel.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFacilitySettingDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDetailsDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelRoomTypeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelRoomTypeDetailsDTO;
import com.christ.erp.services.transactions.hostel.settings.RoomTypeTransaction;

public class RoomTypeHandler {
	
	private static volatile RoomTypeHandler roomTypeHandler = null;
	RoomTypeTransaction roomTypeTransaction = RoomTypeTransaction.getInstance();

    public static RoomTypeHandler getInstance() {
        if(roomTypeHandler==null) {
        	roomTypeHandler = new RoomTypeHandler();
        }
        return roomTypeHandler;
    }
    public List<HostelRoomTypeDTO> getGridData() throws Exception {
    	List<HostelRoomTypeDTO> hostelRoomTypeDTO = new ArrayList<>();
    	List<HostelRoomTypeDBO> list;
		list = roomTypeTransaction.getGridData();
		for(HostelRoomTypeDBO dbo : list) {
			HostelRoomTypeDTO gridDTO = new HostelRoomTypeDTO();
			gridDTO.id = dbo.id.toString();
			gridDTO.hostel = new LookupItemDTO();
			gridDTO.hostel.label = dbo.hostelDBO.hostelName;
			gridDTO.roomType = dbo.roomType;
			gridDTO.totalOccupants = dbo.totalOccupants.toString();
			gridDTO.roomTypeDescription = dbo.roomTypeDescription;
			gridDTO.roomTypeCategory = new LookupItemDTO();
			gridDTO.roomTypeCategory.label = dbo.roomTypeCategory;
			hostelRoomTypeDTO.add(gridDTO);
		}
    	return hostelRoomTypeDTO;
    }
	  
    @SuppressWarnings({ "unchecked", "rawtypes" })
  	public ApiResult<ModelBaseDTO> saveOrUpdate(HostelRoomTypeDTO data, String userId) throws Exception {
      	ApiResult<ModelBaseDTO> result = new ApiResult();
      	Boolean isDuplicate = false;
  		HostelRoomTypeDBO hostelRoomTypeDBO = null;
      	if (!Utils.isNullOrEmpty(data)) {
	  		if(!Utils.isNullOrWhitespace(data.id)) {
	  			hostelRoomTypeDBO = roomTypeTransaction.getRoomTypeDBO(Integer.parseInt(data.id));
	  		}
	  		if(hostelRoomTypeDBO == null) {
	  			hostelRoomTypeDBO = new HostelRoomTypeDBO();
	  			hostelRoomTypeDBO.createdUsersId = Integer.parseInt(userId);
	  		}
	  		else {
	  			hostelRoomTypeDBO.modifiedUsersId = Integer.parseInt(userId);
	  		}
	  		isDuplicate = roomTypeTransaction.isDuplicate(data);
	  		if(isDuplicate) {
	  			result.success = false;
				result.dto = null;
				result.failureMessage = "Duplicate entry for the Room Type:" + data.roomType;
				return result;
	  		}
  			hostelRoomTypeDBO.hostelDBO = new HostelDBO();
  			hostelRoomTypeDBO.hostelDBO.id = Integer.parseInt(data.hostel.value);
  			hostelRoomTypeDBO.roomType = data.roomType.trim();
  			hostelRoomTypeDBO.totalOccupants = Integer.parseInt(data.totalOccupants);
  			hostelRoomTypeDBO.roomTypeDescription = data.roomTypeDescription;
  			hostelRoomTypeDBO.roomTypeCategory = data.roomTypeCategory.label;
  			hostelRoomTypeDBO.recordStatus = 'A';
  			Set<HostelRoomTypeDetailsDBO> hostelRoomTypeDetailsDBOSet = new HashSet<>();
  			for(HostelRoomTypeDetailsDTO details:data.hostelFacilitySetting) {
  				HostelRoomTypeDetailsDBO hostelRoomTypeDetailsDBO = new HostelRoomTypeDetailsDBO();
  				hostelRoomTypeDetailsDBO.hostelRoomTypeDBO = hostelRoomTypeDBO;
				if(!Utils.isNullOrWhitespace(details.roomTypeDetailsId)) {
					hostelRoomTypeDetailsDBO.id = Integer.parseInt(details.roomTypeDetailsId);
		  		}
				hostelRoomTypeDetailsDBO.hostelFacilitySettingDBO = new HostelFacilitySettingDBO();
				hostelRoomTypeDetailsDBO.hostelFacilitySettingDBO.id = Integer.parseInt(details.value);
				hostelRoomTypeDetailsDBOSet.add(hostelRoomTypeDetailsDBO);
			}
  			for(HostelRoomTypeDetailsDTO details:data.roomImages) {
  				HostelRoomTypeDetailsDBO hostelRoomTypeDetailsDBO = new HostelRoomTypeDetailsDBO();
  				hostelRoomTypeDetailsDBO.hostelRoomTypeDBO = hostelRoomTypeDBO;
				if(!Utils.isNullOrWhitespace(details.roomTypeDetailsId)) {
					hostelRoomTypeDetailsDBO.id = Integer.parseInt(details.roomTypeDetailsId);
		  		}
				File file = new File("ImageUpload//"+details.fileName+"."+details.extension);
				hostelRoomTypeDetailsDBO.roomImageUrl=file.getAbsolutePath();
				hostelRoomTypeDetailsDBOSet.add(hostelRoomTypeDetailsDBO);
			}
  			Set<HostelRoomTypeDetailsDBO> orginalRoomTypeDetailsDBOSet = hostelRoomTypeDBO.hostelRoomTypeDetailsDBOSet;
			Set<HostelRoomTypeDetailsDBO> updatedRoomTypeDetailsDBOSet = new HashSet<>();
			Map<Integer,HostelRoomTypeDetailsDBO> existDetailsMap = new HashMap<>();
			if(!Utils.isNullOrEmpty(orginalRoomTypeDetailsDBOSet)) {
				orginalRoomTypeDetailsDBOSet.forEach(dbo-> {
					if(dbo.recordStatus=='A') {
						existDetailsMap.put(dbo.id, dbo);
					}
				});
			}
			for(HostelRoomTypeDetailsDBO item : hostelRoomTypeDetailsDBOSet) {
				HostelRoomTypeDetailsDBO hostelRoomTypeDetails = null;
				if(existDetailsMap.containsKey(item.id)) {	
					hostelRoomTypeDetails = item;
					hostelRoomTypeDetails.recordStatus = 'A';
					hostelRoomTypeDetails.modifiedUsersId = Integer.parseInt(userId);
					existDetailsMap.remove(item.id);
                }
				else {
					hostelRoomTypeDetails = item;
					hostelRoomTypeDetails.recordStatus = 'A';
					hostelRoomTypeDetails.createdUsersId = Integer.parseInt(userId);
				}
				updatedRoomTypeDetailsDBOSet.add(hostelRoomTypeDetails);
            }
			if(!Utils.isNullOrEmpty(existDetailsMap)) {
				existDetailsMap.forEach((entry, value)-> {
					value.modifiedUsersId = Integer.parseInt(userId);
					value.recordStatus = 'D';
					updatedRoomTypeDetailsDBOSet.add(value);
				});
			}
			hostelRoomTypeDBO.hostelRoomTypeDetailsDBOSet = updatedRoomTypeDetailsDBOSet;
			result.success = roomTypeTransaction.saveOrUpdate(hostelRoomTypeDBO);
  		}
      	return result;
    }
    
    public HostelRoomTypeDTO edit(String id) throws NumberFormatException, Exception {
    	HostelRoomTypeDTO hostelRoomTypeDTO = new HostelRoomTypeDTO();
		HostelRoomTypeDBO hostelRoomTypeDBO = roomTypeTransaction.getRoomTypeDBO(Integer.parseInt(id));
		if(hostelRoomTypeDBO != null) {
			hostelRoomTypeDTO.id = hostelRoomTypeDBO.id.toString();
			hostelRoomTypeDTO.hostel = new LookupItemDTO();
			hostelRoomTypeDTO.hostel.value	= hostelRoomTypeDBO.hostelDBO.id.toString();
			hostelRoomTypeDTO.hostel.label	= hostelRoomTypeDBO.hostelDBO.hostelName;
			hostelRoomTypeDTO.roomType = hostelRoomTypeDBO.roomType;
			hostelRoomTypeDTO.totalOccupants = hostelRoomTypeDBO.totalOccupants.toString();
			hostelRoomTypeDTO.roomTypeDescription = hostelRoomTypeDBO.roomTypeDescription;
			hostelRoomTypeDTO.roomTypeCategory = new LookupItemDTO();
			hostelRoomTypeDTO.roomTypeCategory.label = hostelRoomTypeDBO.roomTypeCategory;
			hostelRoomTypeDTO.hostelFacilitySetting = new ArrayList<>();
			hostelRoomTypeDTO.roomImages = new ArrayList<>();
			Set<HostelRoomTypeDetailsDBO> hostelRoomTypeDetailsDBOList = hostelRoomTypeDBO.hostelRoomTypeDetailsDBOSet;
			for(HostelRoomTypeDetailsDBO hostelRoomTypeDetailsDBO:hostelRoomTypeDetailsDBOList) {
				if(hostelRoomTypeDetailsDBO.recordStatus=='A') {
					HostelRoomTypeDetailsDTO details = new HostelRoomTypeDetailsDTO();
					if(!Utils.isNullOrEmpty(hostelRoomTypeDetailsDBO.hostelFacilitySettingDBO)) {
						details.roomTypeDetailsId = hostelRoomTypeDetailsDBO.hostelFacilitySettingDBO.id.toString();
						details.value = hostelRoomTypeDetailsDBO.hostelFacilitySettingDBO.id.toString();
						details.label = hostelRoomTypeDetailsDBO.hostelFacilitySettingDBO.facilityName;
	  					hostelRoomTypeDTO.hostelFacilitySetting.add(details);
					}
					if(!Utils.isNullOrEmpty(hostelRoomTypeDetailsDBO.roomImageUrl)) {
						if(hostelRoomTypeDetailsDBO.recordStatus == 'A') {
							details.roomTypeDetailsId = hostelRoomTypeDetailsDBO.id.toString();
							File file = new File(hostelRoomTypeDetailsDBO.roomImageUrl);
							if(file.exists() && !file.isDirectory()) { 
								details.extension = hostelRoomTypeDetailsDBO.roomImageUrl.substring(hostelRoomTypeDetailsDBO.roomImageUrl.lastIndexOf(".")+1);
								String fileName = new File(hostelRoomTypeDetailsDBO.roomImageUrl).getName();
								details.url = hostelRoomTypeDetailsDBO.roomImageUrl;
								details.fileName = fileName.replaceFirst("[.][^.]+$", "");
								details.recordStatus = hostelRoomTypeDetailsDBO.recordStatus;
			  					hostelRoomTypeDTO.roomImages.add(details);
							}
						}
					}
				}
			}
		}
    	return hostelRoomTypeDTO;
    }
    
    public boolean delete(String  id,String userId) throws NumberFormatException, Exception {
		HostelRoomTypeDBO hostelRoomTypeDBO = roomTypeTransaction.getRoomTypeDBO(Integer.parseInt(id));
    	if(hostelRoomTypeDBO != null) {
    		hostelRoomTypeDBO.recordStatus = 'D';
    		hostelRoomTypeDBO.modifiedUsersId = Integer.parseInt(userId);
			Set<HostelRoomTypeDetailsDBO> hostelRoomTypeDetailsDBOList = hostelRoomTypeDBO.hostelRoomTypeDetailsDBOSet;
			for (HostelRoomTypeDetailsDBO item : hostelRoomTypeDetailsDBOList) {
				if(item.recordStatus == 'A') {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
				}
			}
			if(hostelRoomTypeDBO.id != null) {
				return roomTypeTransaction.saveOrUpdate(hostelRoomTypeDBO);
			}
		}
    	return false;
    }
}