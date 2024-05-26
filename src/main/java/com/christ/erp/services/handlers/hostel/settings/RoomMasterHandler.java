package com.christ.erp.services.handlers.hostel.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBedDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFloorDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomsDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.common.CommonHostelDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBedDTO;
import com.christ.erp.services.dto.hostel.settings.HostelFloorDTO;
import com.christ.erp.services.dto.hostel.settings.HostelRoomsDTO;
import com.christ.erp.services.dto.hostel.settings.RoomMasterDTO;
import com.christ.erp.services.transactions.hostel.settings.BlockAndUnitTransaction;
import com.christ.erp.services.transactions.hostel.settings.RoomMasterTransaction;

public class RoomMasterHandler {
	
	private static volatile RoomMasterHandler roomMasterHandler = null;
	RoomMasterTransaction roomMasterTransaction = RoomMasterTransaction.getInstance();
	BlockAndUnitTransaction blockUnitTransaction = BlockAndUnitTransaction.getInstance();

    public static RoomMasterHandler getInstance() {
        if(roomMasterHandler==null) {
        	roomMasterHandler = new RoomMasterHandler();
        }
        return roomMasterHandler;
    }
    
    public List<RoomMasterDTO> getGridData() throws Exception {
    	List<RoomMasterDTO> roomMasterDTO = new ArrayList<>();
    	List<HostelBlockUnitDBO> list;
		list = blockUnitTransaction.getGridData();
		for(HostelBlockUnitDBO dbo : list) {
			if(!Utils.isNullOrEmpty(dbo.hostelFloorDBOSet)) {
				boolean isDeleted = true;
				Integer totalRooms=0;
				RoomMasterDTO gridDTO = new RoomMasterDTO();
				gridDTO.id = dbo.id.toString();
				gridDTO.hostelName = new LookupItemDTO();
				gridDTO.hostelName.label = dbo.hostelBlockDBO.hostelDBO.hostelName;
				gridDTO.blockName = new LookupItemDTO();
				gridDTO.blockName.label = dbo.hostelBlockDBO.blockName;
				gridDTO.unit = new LookupItemDTO();
				gridDTO.unit.label = dbo.hostelUnit;
				gridDTO.totalFloors = dbo.totalFloors.toString();
				for(HostelFloorDBO floorDBO : dbo.hostelFloorDBOSet) {
					if(floorDBO.recordStatus=='A') {
						totalRooms += floorDBO.totalRooms;
						isDeleted = false;
					}
				}
				gridDTO.totalRoomCount = totalRooms.toString();
				if(!isDeleted) {
					roomMasterDTO.add(gridDTO);
				}
			}
		}
    	return roomMasterDTO;
    }
	  
    @SuppressWarnings({ "unchecked", "rawtypes" })
  	public ApiResult<ModelBaseDTO> saveOrUpdate(RoomMasterDTO data, String userId) throws Exception {
      	ApiResult<ModelBaseDTO> result = new ApiResult();
      	HostelBlockUnitDBO blockUnitDBO = new HostelBlockUnitDBO();
      	blockUnitDBO = blockUnitTransaction.getHostelBlockUnitDBO(Integer.parseInt(data.unit.value));
		Set<HostelFloorDBO> hostelFloorDBOSet = new HashSet<>();
      	if (!Utils.isNullOrEmpty(data)) {
  			for(HostelFloorDTO hostelFloor:data.floorDetails) {
  				Set<HostelRoomsDBO> hostelRoomDBOSet = new HashSet<>();
  				HostelFloorDBO hostelFloorDBO = new HostelFloorDBO();
  				if (!Utils.isNullOrEmpty(hostelFloor.id)) {
  					hostelFloorDBO = roomMasterTransaction.getFloorDBO(Integer.parseInt(hostelFloor.id));
  				}
  	  			hostelFloorDBO.hostelBlockUnitDBO = new HostelBlockUnitDBO();
  	  			hostelFloorDBO.hostelBlockUnitDBO.id = Integer.parseInt(data.unit.value);
  	  			hostelFloorDBO.floorNo = Integer.parseInt(hostelFloor.floorNumber);
  	  			hostelFloorDBO.totalRooms = Integer.parseInt(hostelFloor.roomCount);
  	  			hostelFloorDBO.recordStatus = 'A';
  	  			for(HostelRoomsDTO roomsDTO:hostelFloor.roomDetails) {
  	  				List<HostelBedDBO> hostelBedDBOList = new ArrayList<>();
  	  				HostelRoomsDBO hostelRoomDBO = new HostelRoomsDBO();
	  	  			if(!Utils.isNullOrWhitespace(roomsDTO.id)) {
	  	  				hostelRoomDBO = roomMasterTransaction.getHostelRoomDBO(Integer.parseInt(roomsDTO.id));
	  		  		}
	  				hostelRoomDBO.hostelFloorDBO = hostelFloorDBO;
	  				hostelRoomDBO.roomNo = roomsDTO.roomNumber;
	  				HostelRoomTypeDBO hRoomTypeDBO = new HostelRoomTypeDBO();
	  				hRoomTypeDBO.id = Integer.parseInt(roomsDTO.roomType.value);
	  				hostelRoomDBO.hostelRoomTypeDBO = hRoomTypeDBO;
	  				hostelRoomDBO.recordStatus = 'A';
	  				for(HostelBedDTO bedDTO:roomsDTO.bedDetails) {
	  					HostelBedDBO bedDBO = new HostelBedDBO();
	  					if(!Utils.isNullOrWhitespace(bedDTO.id)) {
	  						bedDBO.id = Integer.parseInt(bedDTO.id);
		  		  		}
	  					bedDBO.bedNo = bedDTO.bedName;
	  					bedDBO.hostelRoomsDBO = hostelRoomDBO;
	  					bedDBO.recordStatus = 'A';
	  					hostelBedDBOList.add(bedDBO);
	  				}
	  				Set<HostelBedDBO> orginalHostelBedDBOSet = hostelRoomDBO.hostelBedDBOSet;
	  				Set<HostelBedDBO> updatedHostelBedDBOSet = new LinkedHashSet<>();
	  				Map<Integer,HostelBedDBO> existDetailsMap = new HashMap<>();
	  				if(!Utils.isNullOrEmpty(orginalHostelBedDBOSet)) {
	  					orginalHostelBedDBOSet.forEach(dbo-> {
	  						if(dbo.recordStatus=='A') {
	  							existDetailsMap.put(dbo.id, dbo);
	  						}
	  					});
	  				}
	  				for(HostelBedDBO item : hostelBedDBOList) {
	  					HostelBedDBO hostelBed = null;
	  					if(existDetailsMap.containsKey(item.id)) {	
	  						hostelBed = item;
	  						hostelBed.recordStatus = 'A';
	  						hostelBed.modifiedUsersId = Integer.parseInt(userId);
	  						existDetailsMap.remove(item.id);
	  	                }
	  					else {
	  						hostelBed = item;
	  						hostelBed.recordStatus = 'A';
	  						hostelBed.createdUsersId = Integer.parseInt(userId);
	  					}
	  					updatedHostelBedDBOSet.add(hostelBed);
	  	            }
	  				if(!Utils.isNullOrEmpty(existDetailsMap)) {
	  					existDetailsMap.forEach((entry, value)-> {
	  						value.modifiedUsersId = Integer.parseInt(userId);
	  						value.recordStatus = 'D';
	  						updatedHostelBedDBOSet.add(value);
	  					});
	  				}
	  				hostelRoomDBO.hostelBedDBOSet = updatedHostelBedDBOSet;
	  				hostelRoomDBOSet.add(hostelRoomDBO);
  	  			}
	  	  		Set<HostelRoomsDBO> orginalHostelRoomsDBOSet = hostelFloorDBO.hostelRoomsDBOSet;
				Set<HostelRoomsDBO> updatedHostelRoomsDBOSet = new HashSet<>();
				Map<Integer,HostelRoomsDBO> existDetailsMap = new HashMap<>();
				if(!Utils.isNullOrEmpty(orginalHostelRoomsDBOSet)) {
					orginalHostelRoomsDBOSet.forEach(dbo-> {
						if(dbo.recordStatus=='A') {
							existDetailsMap.put(dbo.id, dbo);
						}
					});
				}
				for(HostelRoomsDBO item : hostelRoomDBOSet) {
					HostelRoomsDBO hostelRoomTypeDetails = null;
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
					updatedHostelRoomsDBOSet.add(hostelRoomTypeDetails);
	            }
				if(!Utils.isNullOrEmpty(existDetailsMap)) {
					existDetailsMap.forEach((entry, value)-> {
						value.modifiedUsersId = Integer.parseInt(userId);
						value.recordStatus = 'D';
						updatedHostelRoomsDBOSet.add(value);
					});
				}
  	  			hostelFloorDBO.hostelRoomsDBOSet = updatedHostelRoomsDBOSet;
  	  			hostelFloorDBOSet.add(hostelFloorDBO);
  			}
  			Set<HostelFloorDBO> orginalHostelFloorDBOSet = blockUnitDBO.hostelFloorDBOSet;
			Set<HostelFloorDBO> updatedHostelFloorDBOSet = new HashSet<>();
			Map<Integer,HostelFloorDBO> existDetailsMap = new HashMap<>();
			if(!Utils.isNullOrEmpty(orginalHostelFloorDBOSet)) {
				orginalHostelFloorDBOSet.forEach(dbo-> {
					if(dbo.recordStatus=='A') {
						existDetailsMap.put(dbo.id, dbo);
					}
				});
			}
			for(HostelFloorDBO item : hostelFloorDBOSet) {
				HostelFloorDBO hostelRoomTypeDetails = null;
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
				updatedHostelFloorDBOSet.add(hostelRoomTypeDetails);
            }
			if(!Utils.isNullOrEmpty(existDetailsMap)) {
				existDetailsMap.forEach((entry, value)-> {
					value.modifiedUsersId = Integer.parseInt(userId);
					value.recordStatus = 'D';
					updatedHostelFloorDBOSet.add(value);
				});
			}
  			blockUnitDBO.hostelFloorDBOSet = updatedHostelFloorDBOSet;
			result.success = roomMasterTransaction.saveOrUpdate(blockUnitDBO);
  		}
      	return result;
    }
    
    public RoomMasterDTO edit(String id) throws Exception {
		HostelBlockUnitDBO blockUnitDBO = blockUnitTransaction.getHostelBlockUnitDBO(Integer.parseInt(id));
		RoomMasterDTO roomMasterDTO = new RoomMasterDTO();
		if(!Utils.isNullOrEmpty(blockUnitDBO)) {
			roomMasterDTO.id = blockUnitDBO.id.toString();
			roomMasterDTO.hostelName = new LookupItemDTO();
			roomMasterDTO.hostelName.value = blockUnitDBO.hostelBlockDBO.hostelDBO.id.toString();
			roomMasterDTO.hostelName.label = blockUnitDBO.hostelBlockDBO.hostelDBO.hostelName;
			roomMasterDTO.blockName = new LookupItemDTO();
			roomMasterDTO.blockName.value = blockUnitDBO.hostelBlockDBO.id.toString();
			roomMasterDTO.blockName.label = blockUnitDBO.hostelBlockDBO.blockName;
			roomMasterDTO.unit = new LookupItemDTO();
			roomMasterDTO.unit.value = blockUnitDBO.id.toString();
			roomMasterDTO.unit.label = blockUnitDBO.hostelUnit;
			roomMasterDTO.totalFloors = blockUnitDBO.totalFloors.toString();
			roomMasterDTO.floorDetails  = new ArrayList<>();
			int floorCount  = 0;
			for(HostelFloorDBO floorDBO:blockUnitDBO.hostelFloorDBOSet) {
				if(floorDBO.recordStatus=='A') {
					floorCount++;
					HostelFloorDTO floorDTO = new HostelFloorDTO();
					floorDTO.roomDetails = new ArrayList<>();
					floorDTO.id = floorDBO.id.toString();
					floorDTO.floorNumber = floorDBO.floorNo.toString();
					floorDTO.roomCount = floorDBO.totalRooms.toString();
					for(HostelRoomsDBO roomDBO:floorDBO.hostelRoomsDBOSet) {
						if(roomDBO.recordStatus=='A') {
							HostelRoomsDTO hostelRoomsDTO = new HostelRoomsDTO();
							hostelRoomsDTO.bedDetails = new ArrayList<>();
							hostelRoomsDTO.id = roomDBO.id.toString();
							hostelRoomsDTO.roomNumber = roomDBO.roomNo;
							hostelRoomsDTO.roomType = new CommonHostelDTO();
							hostelRoomsDTO.roomType.value = String.valueOf(roomDBO.hostelRoomTypeDBO.id);
							hostelRoomsDTO.roomType.label = roomDBO.hostelRoomTypeDBO.getRoomType();
							for(HostelBedDBO bedDBO:roomDBO.hostelBedDBOSet) {
								if(bedDBO.recordStatus=='A') {
									HostelBedDTO bedDTO = new HostelBedDTO();
									bedDTO.id = bedDBO.id.toString();
									bedDTO.bedName = bedDBO.bedNo;
									hostelRoomsDTO.bedDetails.add(bedDTO);
								}
							}
							Collections.sort(hostelRoomsDTO.bedDetails, new Comparator<HostelBedDTO>() {
								@Override
								public int compare(HostelBedDTO o1, HostelBedDTO o2) {
									return (Integer.parseInt(o1.id) - Integer.parseInt(o2.id));
								}
							});
							floorDTO.roomDetails.add(hostelRoomsDTO);
						}
					}
					Collections.sort(floorDTO.roomDetails, new Comparator<HostelRoomsDTO>() {
						@Override
						public int compare(HostelRoomsDTO o1, HostelRoomsDTO o2) {
							return (Integer.parseInt(o1.id) - Integer.parseInt(o2.id));
						}
					});
					roomMasterDTO.floorDetails.add(floorDTO);
				}
			}
			if(Integer.parseInt(roomMasterDTO.totalFloors)>floorCount) {
				for(int i=floorCount;i<=Integer.parseInt(roomMasterDTO.totalFloors);i++) {
					HostelFloorDTO hostelFloorDTO = new HostelFloorDTO();
					hostelFloorDTO.floorNumber = String.valueOf(i);
					roomMasterDTO.floorDetails.add(hostelFloorDTO);
				}
			}
		}
		
		Collections.sort(roomMasterDTO.floorDetails, new Comparator<HostelFloorDTO>() {
			@Override
			public int compare(HostelFloorDTO o1, HostelFloorDTO o2) {
				return (Integer.parseInt(o1.floorNumber) - Integer.parseInt(o2.floorNumber));
			}
		});
    	return roomMasterDTO;
    }
    
    public boolean delete(String  id,String userId) throws NumberFormatException, Exception {
		HostelBlockUnitDBO hostelBlockUnitDBO = blockUnitTransaction.getHostelBlockUnitDBO(Integer.parseInt(id));
    	if(hostelBlockUnitDBO != null) {
			for (HostelFloorDBO floorDBO : hostelBlockUnitDBO.hostelFloorDBOSet) {
				if(floorDBO.recordStatus=='A') {
					floorDBO.recordStatus = 'D';
					floorDBO.modifiedUsersId = Integer.parseInt(userId);
				}
				for (HostelRoomsDBO roomsDBO : floorDBO.hostelRoomsDBOSet) {
					if(roomsDBO.recordStatus=='A') {
						roomsDBO.recordStatus = 'D';
						roomsDBO.modifiedUsersId = Integer.parseInt(userId);
					}
					for (HostelBedDBO bedDBO : roomsDBO.hostelBedDBOSet) {
						if(bedDBO.recordStatus=='A') {
							bedDBO.recordStatus = 'D';
							bedDBO.modifiedUsersId = Integer.parseInt(userId);
						}
						roomsDBO.hostelBedDBOSet.add(bedDBO);
					}
					floorDBO.hostelRoomsDBOSet.add(roomsDBO);
				}
				hostelBlockUnitDBO.hostelFloorDBOSet.add(floorDBO);
			}
			if(hostelBlockUnitDBO.id != null) {
				return roomMasterTransaction.saveOrUpdate(hostelBlockUnitDBO);
			}
		}
    	return false;
    }
}