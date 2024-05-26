package com.christ.erp.services.handlers.hostel.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDetailsDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockUnitDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockUnitDetailsDTO;
import com.christ.erp.services.transactions.hostel.settings.BlockAndUnitTransaction;

public class BlockAndUnitHandler {
	
	private static volatile BlockAndUnitHandler blockAndUnitHandler = null;
	BlockAndUnitTransaction blockAndUnitTransaction = BlockAndUnitTransaction.getInstance();

    public static BlockAndUnitHandler getInstance() {
        if(blockAndUnitHandler==null) {
        	blockAndUnitHandler = new BlockAndUnitHandler();
        }
        return blockAndUnitHandler;
    }
    public List<HostelBlockUnitDTO> getGridData() throws Exception {
    	List<HostelBlockUnitDTO> hostelBlockUnitDTO = new ArrayList<>();
    	List<HostelBlockUnitDBO> list;
		list = blockAndUnitTransaction.getGridData();
		for(HostelBlockUnitDBO dbo : list) {
			HostelBlockUnitDTO gridDTO = new HostelBlockUnitDTO();
			gridDTO.id = dbo.id.toString();
			gridDTO.hostel = new LookupItemDTO();
			if(!Utils.isNullOrEmpty(dbo.hostelBlockDBO) && !Utils.isNullOrEmpty(dbo.hostelBlockDBO.hostelDBO)){
				gridDTO.hostel.label = dbo.hostelBlockDBO.hostelDBO.hostelName;
			}
			gridDTO.block = new LookupItemDTO();
			if(!Utils.isNullOrEmpty(dbo.hostelBlockDBO) && !Utils.isNullOrEmpty(dbo.hostelBlockDBO.hostelDBO)) {
				gridDTO.block.label = dbo.hostelBlockDBO.blockName;
			}
			if(!Utils.isNullOrEmpty(dbo.hostelUnit)){
				gridDTO.hostelUnit = dbo.hostelUnit;
			}
			if(!Utils.isNullOrEmpty(dbo.totalFloors)){
				gridDTO.totalFloors = dbo.totalFloors.toString();
			}
			if(!Utils.isNullOrEmpty(dbo.isLeaveSubmissionOnline))
			gridDTO.isLeaveSubmissionOnline = dbo.isLeaveSubmissionOnline ? "Yes":"No";
			hostelBlockUnitDTO.add(gridDTO);
		}
    	return hostelBlockUnitDTO;
    }
	  
    @SuppressWarnings({ "unchecked", "rawtypes" })
  	public ApiResult<ModelBaseDTO> saveOrUpdate(HostelBlockUnitDTO data, String userId) throws Exception {
      	ApiResult<ModelBaseDTO> result = new ApiResult();
      	Boolean isDuplicate = false;
      	if (!Utils.isNullOrEmpty(data)) {
      		HostelBlockUnitDBO hostelBlockUnitDBO = null;
	  		if(!Utils.isNullOrWhitespace(data.id)) {
	  			hostelBlockUnitDBO = blockAndUnitTransaction.getHostelBlockUnitDBO(Integer.parseInt(data.id));
	  		}
	  		if(hostelBlockUnitDBO == null) {
	  			hostelBlockUnitDBO = new HostelBlockUnitDBO();
	  			hostelBlockUnitDBO.createdUsersId = Integer.parseInt(userId);
	  		}
	  		else {
	  			hostelBlockUnitDBO.modifiedUsersId = Integer.parseInt(userId);
	  		}
	  		isDuplicate = blockAndUnitTransaction.isDuplicate(data);
	  		if(isDuplicate) {
	  			result.success = false;
				result.dto = null;
				result.failureMessage = "Duplicate entry for the Unit:" + data.hostelUnit;
				return result;
	  		}
  			hostelBlockUnitDBO.hostelBlockDBO = new HostelBlockDBO();
  			hostelBlockUnitDBO.hostelBlockDBO.id = Integer.parseInt(data.block.value);
  			hostelBlockUnitDBO.hostelUnit = data.hostelUnit.trim();
  			hostelBlockUnitDBO.totalFloors = Integer.parseInt(data.totalFloors);
  			hostelBlockUnitDBO.isLeaveSubmissionOnline = data.isLeaveSubmissionOnline.equalsIgnoreCase("Yes")?true:false;
  			hostelBlockUnitDBO.leaveSubmissionNextDayBy = Utils.convertStringTimeToLocalTime(data.leaveSubmissionNextDayBy);
  			hostelBlockUnitDBO.leaveSubmissionSaturdayBy =Utils.convertStringTimeToLocalTime(data.leaveSubmissionSaturdayBy);
  			hostelBlockUnitDBO.isSmsMorningAbsence = data.parentsCommunicationOption.get("isSmsForMorningAbsence");
  			hostelBlockUnitDBO.isSmsEveningAbsence = data.parentsCommunicationOption.get("isSmsForEveningAbsence");
  			hostelBlockUnitDBO.isEmailMorningAbsence = data.parentsCommunicationOption.get("isEmailForMorningAbsence");
  			hostelBlockUnitDBO.isEmailEveningAbsence = data.parentsCommunicationOption.get("isEmailForEveningAbsence");
  			hostelBlockUnitDBO.isPunchingExemptionSundayMorning = data.punchingExemptionForMorining.get("isSunday");
  			hostelBlockUnitDBO.isPunchingExemptionHolidayMorning = data.punchingExemptionForMorining.get("isHoliday");
  			hostelBlockUnitDBO.isPunchingExemptionSundayEvening = data.punchingExemptionForMorining.get("isSunday");
  			hostelBlockUnitDBO.isPunchingExemptionHolidayEvening = data.punchingExemptionForMorining.get("isHoliday");
  			hostelBlockUnitDBO.recordStatus = 'A';
  			Set<HostelBlockUnitDetailsDBO> hostelBlockUnitDetailsDBOSet = new HashSet<>();
			for( HostelBlockUnitDetailsDTO details:data.hostelBlockUnitDetails) {
				HostelBlockUnitDetailsDBO hostelBlockUnitDetailsDBO = new HostelBlockUnitDetailsDBO();
				hostelBlockUnitDetailsDBO.hostelBlockUnitDBO = hostelBlockUnitDBO;
				if(!Utils.isNullOrWhitespace(details.id)) {
					hostelBlockUnitDetailsDBO.id = Integer.parseInt(details.id);
		  		}
				hostelBlockUnitDetailsDBO.sequenceNo = Integer.parseInt(details.sequenceNo);
				hostelBlockUnitDetailsDBO.erpUsersDBO = new ErpUsersDBO();
				hostelBlockUnitDetailsDBO.erpUsersDBO.id = Integer.parseInt(details.erpUser.value);
				hostelBlockUnitDetailsDBO.hostelPosition = details.hostelPosition;
				hostelBlockUnitDetailsDBO.positionMobileNo= details.positionMobileNo;
				hostelBlockUnitDetailsDBO.positionEmail = details.positionEmail;
				hostelBlockUnitDetailsDBO.positionPhoneNo = details.positionPhoneNo;
				hostelBlockUnitDetailsDBO.isSentSmsEmailMorningAbsence = Utils.isNullOrEmpty(details.isSentSmsEmailMorningAbsence) ? null:details.isSentSmsEmailMorningAbsence.equalsIgnoreCase("Yes") ? true:false;
				hostelBlockUnitDetailsDBO.isSentSmsEmailEveningAbsence = Utils.isNullOrEmpty(details.isSentSmsEmailEveningAbsence) ? null:details.isSentSmsEmailEveningAbsence.equalsIgnoreCase("Yes") ? true:false;
				hostelBlockUnitDetailsDBOSet.add(hostelBlockUnitDetailsDBO);
			}
			Set<HostelBlockUnitDetailsDBO> orginalHostelBlockUnitDetailsDBOSet = hostelBlockUnitDBO.hostelBlockUnitDetailsDBOSet;
			Set<HostelBlockUnitDetailsDBO> updatedHostelBlockUnitDetailsDBOSet = new HashSet<>();
			Map<Integer,HostelBlockUnitDetailsDBO> existDetailsMap = new HashMap<>();
			if(!Utils.isNullOrEmpty(orginalHostelBlockUnitDetailsDBOSet)) {
				orginalHostelBlockUnitDetailsDBOSet.forEach(dbo-> {
					if(dbo.recordStatus=='A') {
						existDetailsMap.put(dbo.id, dbo);
					}
				});
			}
			for(HostelBlockUnitDetailsDBO item : hostelBlockUnitDetailsDBOSet) {
				HostelBlockUnitDetailsDBO hostelBlockUnitDetails = null;
				if(existDetailsMap.containsKey(item.id)) {	
					hostelBlockUnitDetails = item;
					hostelBlockUnitDetails.recordStatus = 'A';
					hostelBlockUnitDetails.modifiedUsersId = Integer.parseInt(userId);
					existDetailsMap.remove(item.id);
                }
				else {
					hostelBlockUnitDetails = item;
					hostelBlockUnitDetails.recordStatus = 'A';
					hostelBlockUnitDetails.createdUsersId = Integer.parseInt(userId);
				}
				updatedHostelBlockUnitDetailsDBOSet.add(hostelBlockUnitDetails);
            }
			if(!Utils.isNullOrEmpty(existDetailsMap)) {
				existDetailsMap.forEach((entry, value)-> {
					value.modifiedUsersId = Integer.parseInt(userId);
					value.recordStatus = 'D';
					updatedHostelBlockUnitDetailsDBOSet.add(value);
				});
			}
			hostelBlockUnitDBO.hostelBlockUnitDetailsDBOSet = updatedHostelBlockUnitDetailsDBOSet;
			result.success = blockAndUnitTransaction.saveOrUpdate(hostelBlockUnitDBO);
  		}
      	return result;
    }
    
    public HostelBlockUnitDTO edit(String id) throws NumberFormatException, Exception {
    	HostelBlockUnitDTO hostelBlockUnitDTO = new HostelBlockUnitDTO();
		HostelBlockUnitDBO hostelBlockUnitDBO = blockAndUnitTransaction.getHostelBlockUnitDBO(Integer.parseInt(id));
		if(hostelBlockUnitDBO != null) {
			hostelBlockUnitDTO.id = hostelBlockUnitDBO.id.toString();
			hostelBlockUnitDTO.hostel = new LookupItemDTO();
			hostelBlockUnitDTO.hostel.value	= hostelBlockUnitDBO.hostelBlockDBO.hostelDBO.id.toString();
			hostelBlockUnitDTO.block = new LookupItemDTO();
			hostelBlockUnitDTO.block.value	= hostelBlockUnitDBO.hostelBlockDBO.id.toString();
			hostelBlockUnitDTO.hostelUnit = hostelBlockUnitDBO.hostelUnit;
			hostelBlockUnitDTO.totalFloors = hostelBlockUnitDBO.totalFloors.toString();
			hostelBlockUnitDTO.hostelFloorsNo = String.valueOf(hostelBlockUnitDBO.hostelFloorDBOSet.size());
			hostelBlockUnitDTO.isLeaveSubmissionOnline = hostelBlockUnitDBO.isLeaveSubmissionOnline ? "Yes":"No";
			hostelBlockUnitDTO.leaveSubmissionNextDayBy = hostelBlockUnitDBO.leaveSubmissionNextDayBy.toString();
			hostelBlockUnitDTO.leaveSubmissionSaturdayBy = hostelBlockUnitDBO.leaveSubmissionSaturdayBy.toString();
			hostelBlockUnitDTO.parentsCommunicationOption = new HashMap<String, Boolean>();
			hostelBlockUnitDTO.parentsCommunicationOption.put("isSmsForMorningAbsence", hostelBlockUnitDBO.isSmsMorningAbsence);
			hostelBlockUnitDTO.parentsCommunicationOption.put("isSmsForEveningAbsence", hostelBlockUnitDBO.isSmsEveningAbsence);
			hostelBlockUnitDTO.parentsCommunicationOption.put("isEmailForMorningAbsence", hostelBlockUnitDBO.isEmailMorningAbsence);
			hostelBlockUnitDTO.parentsCommunicationOption.put("isEmailForEveningAbsence", hostelBlockUnitDBO.isEmailEveningAbsence);
			if(!hostelBlockUnitDTO.parentsCommunicationOption.containsValue(true)) {
				hostelBlockUnitDTO.parentsCommunicationOption.put("isNone", true);
			}
			hostelBlockUnitDTO.punchingExemptionForMorining = new HashMap<String, Boolean>();
			hostelBlockUnitDTO.punchingExemptionForMorining.put("isHoliday", hostelBlockUnitDBO.isPunchingExemptionHolidayMorning);
			hostelBlockUnitDTO.punchingExemptionForMorining.put("isSunday", hostelBlockUnitDBO.isPunchingExemptionSundayMorning);
			if(!hostelBlockUnitDTO.punchingExemptionForMorining.containsValue(true)) {
				hostelBlockUnitDTO.punchingExemptionForMorining.put("isNone", true);
			}
			hostelBlockUnitDTO.punchingExemptionForEvening = new HashMap<String, Boolean>();
			hostelBlockUnitDTO.punchingExemptionForEvening.put("isHoliday", hostelBlockUnitDBO.isPunchingExemptionHolidayEvening);
			hostelBlockUnitDTO.punchingExemptionForEvening.put("isSunday", hostelBlockUnitDBO.isPunchingExemptionSundayEvening);
			if(!hostelBlockUnitDTO.punchingExemptionForEvening.containsValue(true)) {
				hostelBlockUnitDTO.punchingExemptionForEvening.put("isNone", true);
			}
			hostelBlockUnitDTO.hostelBlockUnitDetails = new ArrayList<HostelBlockUnitDetailsDTO>();
			Set<HostelBlockUnitDetailsDBO> hostelBlockUnitDetailsDBOList = hostelBlockUnitDBO.hostelBlockUnitDetailsDBOSet;
			for(HostelBlockUnitDetailsDBO hostelBlockUnitDetailsDBO:hostelBlockUnitDetailsDBOList) {
				if(hostelBlockUnitDetailsDBO.recordStatus=='A') {
  					HostelBlockUnitDetailsDTO hostelBlockUnitDetailsDTO = new HostelBlockUnitDetailsDTO();
  					hostelBlockUnitDetailsDTO.id = hostelBlockUnitDetailsDBO.id.toString();
  					hostelBlockUnitDetailsDTO.sequenceNo = hostelBlockUnitDetailsDBO.sequenceNo.toString();
  					hostelBlockUnitDetailsDTO.erpUser = new LookupItemDTO();
  					hostelBlockUnitDetailsDTO.erpUser.value = String.valueOf(hostelBlockUnitDetailsDBO.erpUsersDBO.id);
  					hostelBlockUnitDetailsDTO.erpUser.label = hostelBlockUnitDetailsDBO.erpUsersDBO.userName;
  					hostelBlockUnitDetailsDTO.hostelPosition = hostelBlockUnitDetailsDBO.hostelPosition;
  					hostelBlockUnitDetailsDTO.positionMobileNo = hostelBlockUnitDetailsDBO.positionMobileNo;
  					hostelBlockUnitDetailsDTO.positionEmail = hostelBlockUnitDetailsDBO.positionEmail;
  					hostelBlockUnitDetailsDTO.positionPhoneNo = hostelBlockUnitDetailsDBO.positionPhoneNo;
  					hostelBlockUnitDetailsDTO.isSentSmsEmailMorningAbsence = Utils.isNullOrEmpty(hostelBlockUnitDetailsDBO.isSentSmsEmailMorningAbsence) ? "":hostelBlockUnitDetailsDBO.isSentSmsEmailMorningAbsence ? "Yes":"No";
  					hostelBlockUnitDetailsDTO.isSentSmsEmailEveningAbsence = Utils.isNullOrEmpty(hostelBlockUnitDetailsDBO.isSentSmsEmailEveningAbsence) ? "":hostelBlockUnitDetailsDBO.isSentSmsEmailEveningAbsence ? "Yes":"No";
  					hostelBlockUnitDTO.hostelBlockUnitDetails.add(hostelBlockUnitDetailsDTO);
				}
			}
			hostelBlockUnitDTO.hostelBlockUnitDetails.sort((details1,details2)->Integer.parseInt(details1.sequenceNo)-Integer.parseInt(details2.sequenceNo));
		}
    	return hostelBlockUnitDTO;
    }
    
    public boolean delete(String  id,String userId) throws NumberFormatException, Exception {
		HostelBlockUnitDBO hostelBlockUnitDBO = blockAndUnitTransaction.getHostelBlockUnitDBO(Integer.parseInt(id));
    	if(hostelBlockUnitDBO != null) {
    		hostelBlockUnitDBO.recordStatus = 'D';
    		hostelBlockUnitDBO.modifiedUsersId = Integer.parseInt(userId);
			Set<HostelBlockUnitDetailsDBO> hostelBlockUnitDetailsDBOList = hostelBlockUnitDBO.hostelBlockUnitDetailsDBOSet;
			for (HostelBlockUnitDetailsDBO item : hostelBlockUnitDetailsDBOList) {
				if(item.recordStatus=='A') {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
				}
			}
			if(hostelBlockUnitDBO.id != null) {
				return blockAndUnitTransaction.saveOrUpdate(hostelBlockUnitDBO);
			}
		}
    	return false;
    }
}