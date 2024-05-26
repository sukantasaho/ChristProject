package com.christ.erp.services.handlers.hostel.settings;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDetailsDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.helpers.hostel.settings.AdmissionFormHelper;
import com.christ.erp.services.transactions.hostel.settings.AdmissionFormTransaction;

public class AdmissionFormHandler {

	private static volatile AdmissionFormHandler admissionFormHandler = null;
	AdmissionFormTransaction admissionFormTransaction = AdmissionFormTransaction.getInstance();
	AdmissionFormHelper admissionFormHelper = AdmissionFormHelper.getInstance();
	
    public static AdmissionFormHandler getInstance() {
        if(admissionFormHandler==null) {
        	admissionFormHandler = new AdmissionFormHandler();
        }
        return admissionFormHandler;
    }
    public final String HOSTEL_CANCELLED = "HOSTEL_CANCELLED";
    public final String HOSTEL_ADMITTED = "HOSTEL_ADMITTED";
	public HostelApplicationDTO getHostelApplicationData(String acadamicYearId, String hostelApplicationNum,
			String registerNo,  String applicationNo, ApiResult<HostelApplicationDTO> result) throws Exception {
		HostelApplicationDTO dto = null;
		HostelApplicationDBO hostelApplicationDBO = null;
		HostelAdmissionsDBO hostelAdmissionsDBO = null;
		LookupItemDTO lookupItemDTO = null;
		hostelAdmissionsDBO = admissionFormTransaction.getHostelAdmissionData(acadamicYearId, hostelApplicationNum,  registerNo, applicationNo);
		if(!Utils.isNullOrEmpty(hostelAdmissionsDBO)) {
			dto = new HostelApplicationDTO();
			dto = admissionFormHelper.convertHostelAdmissionDBOtoDTO(hostelAdmissionsDBO, dto, lookupItemDTO);
		}else {
			hostelApplicationDBO = admissionFormTransaction.getHostelApplicationData(acadamicYearId, hostelApplicationNum,  registerNo, applicationNo, result);
			if(!Utils.isNullOrEmpty(hostelApplicationDBO)) {
				dto = new HostelApplicationDTO();
				dto = admissionFormHelper.convertHostelAdmissionDBOtoDTO(hostelApplicationDBO, dto, lookupItemDTO);
			}
		}
		return dto;
	}

	public HostelApplicationDTO saveOrUpdate(HostelApplicationDTO data, String userId, ApiResult<HostelApplicationDTO> result) throws Exception {
		HostelAdmissionsDBO dbo = null;
		List<HostelAdmissionsDBO> duplicateCheck =  null;
		boolean isSaved = false;
		duplicateCheck = admissionFormTransaction.DuplicateCheck(data);
		if(Utils.isNullOrEmpty(duplicateCheck)) {
			if(Utils.isNullOrEmpty(data.hostelAdmissionId)) {
				dbo = new HostelAdmissionsDBO();
				dbo.createdUsersId = Integer.parseInt(userId);
				Integer statusId = admissionFormTransaction.getStatusId(HOSTEL_ADMITTED);
				if(!Utils.isNullOrEmpty(statusId)) {
					ErpStatusDBO erpStatusDBO = new ErpStatusDBO();
					erpStatusDBO.id = statusId;
					dbo.erpStatusDBO = erpStatusDBO;
				}
			}else {
				dbo = admissionFormTransaction.getHostelAdmissionDBO(data.hostelAdmissionId);
				if(!Utils.isNullOrEmpty(dbo)) {			
					dbo.modifiedUsersId = Integer.parseInt(userId);
				}
			}
		    if(!Utils.isNullOrEmpty(dbo)) {
			    dbo = admissionFormHelper.convertDTOtoHostelAdmissionDBO(dbo, data, userId);
		    	isSaved = admissionFormTransaction.saveOrUpdate(dbo);
		    	if(isSaved) {
		    		dbo = admissionFormTransaction.getHostelAdmissionDBO(String.valueOf(dbo.id));
		    		getHostelTempleteData(data, dbo);
		    		result.success = true;
		    	}
		    }
		}else {
			result.success = false;
			result.failureMessage = "Duplicate record exist";
			duplicateCheck.forEach(item->{
//				if(!Utils.isNullOrEmpty(item.erpStatusDBO)) {
//					result.failureMessage = "Record is already exist need to be checkout";
//				}
			});
		}
		return data;
	}
	
	public LookupItemDTO getHostelSeatsAvailableDetatils(String academicYearId,
			String hostelId, String roomTypeId) throws Exception {
		LookupItemDTO availableSeats = null; int seat =0;
		availableSeats = new LookupItemDTO();
		HostelSeatAvailabilityDBO availabilityDBO = admissionFormTransaction.getHostelSeatesAvailblity(Integer.parseInt(academicYearId),Integer.parseInt(hostelId), Integer.parseInt(roomTypeId));
		if(!Utils.isNullOrEmpty(availabilityDBO) && !Utils.isNullOrEmpty(availabilityDBO.hostelSeatAvailabilityDetailsDBO)) {
			BigInteger hostelSeatfilled = admissionFormTransaction.getTotalHostelSeatsFilled(Integer.parseInt(academicYearId),Integer.parseInt(hostelId), Integer.parseInt(roomTypeId));
			if(!Utils.isNullOrEmpty(availabilityDBO.hostelSeatAvailabilityDetailsDBO) && !Utils.isNullOrEmpty(hostelSeatfilled)) {
				for (HostelSeatAvailabilityDetailsDBO  detailsDBO : availabilityDBO.hostelSeatAvailabilityDetailsDBO) {
					if(!Utils.isNullOrEmpty(detailsDBO.hostelRoomTypeDBO) && detailsDBO.hostelRoomTypeDBO.id == Integer.parseInt(roomTypeId)) {
						seat = detailsDBO.availableSeats - hostelSeatfilled.intValue();
						if(!Utils.isNullOrEmpty(seat) && seat != 0 ) {
							availableSeats.value = String.valueOf(seat);
						}
						break;
					}
				}
			}
		}
		return availableSeats;
	}

	public void getHostelTempleteData(HostelApplicationDTO data, HostelAdmissionsDBO dbo) throws Exception {
		if(!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO) && !Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO.studentPersonalDataDBO)){
			if(!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO.studentPersonalDataDBO.studentPersonalDataAddressDBO)) 
			admissionFormHelper.convertHostelTemplateStudentAddrDBOtoDTO(data,dbo.studentApplnEntriesDBO.studentPersonalDataDBO.studentPersonalDataAddressDBO);
			if(!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO.studentPersonalDataDBO.studentPersonalDataAddtnlDBO) && 
					!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO.studentPersonalDataDBO.studentPersonalDataAddtnlDBO.erpReligionDBO) && 
						!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO.studentPersonalDataDBO.studentPersonalDataAddtnlDBO.erpReligionDBO.religionName)) {
				data.religion = dbo.studentApplnEntriesDBO.studentPersonalDataDBO.studentPersonalDataAddtnlDBO.erpReligionDBO.religionName;
			}
		}
		if(!Utils.isNullOrEmpty(dbo.hostelDBO)) {
			admissionFormHelper.convertHostelTemplateToPrintingString(data,dbo.hostelDBO);
		}
	}

	public void cancelAdmissionForm(HostelApplicationDTO data, String userId,
			ApiResult<ModelBaseDTO> result) throws Exception {
		boolean isSaved = false;
		if(!Utils.isNullOrEmpty(data.id)) {
			HostelAdmissionsDBO dbo = admissionFormTransaction.getHostelAdmissionDBO(data.id);
			if(!Utils.isNullOrEmpty(dbo)) {
				dbo.modifiedUsersId = Integer.parseInt(userId);
				if(!Utils.isNullOrEmpty(data.isCanceled) && data.isCanceled == true) {
					Integer statusId = admissionFormTransaction.getStatusId(HOSTEL_CANCELLED);
					if(!Utils.isNullOrEmpty(statusId)) {
						ErpStatusDBO erpStatusDBO = new ErpStatusDBO();
						erpStatusDBO.id  = statusId;
						dbo.erpStatusDBO = erpStatusDBO;
					}
					dbo.cancelledByUserId = Integer.parseInt(userId);
				}
				if(!Utils.isNullOrEmpty(data.cancelReason)) {
					dbo.cancelledReason = data.cancelReason;
				}
				isSaved = admissionFormTransaction.saveOrUpdate(dbo);
		    	if(isSaved) {
		    		result.success = true;
		    	}else {
		    		result.success = false;
		    	}
			}
		}
	}

	public HostelApplicationDTO printAdmissionForm(HostelApplicationDTO data, String userId,
			ApiResult<HostelApplicationDTO> result) throws Exception {
		HostelAdmissionsDBO dbo = null;
		if(!Utils.isNullOrEmpty(data.hostelAdmissionId)) {
			dbo = admissionFormTransaction.getHostelAdmissionDBO(String.valueOf(data.hostelAdmissionId));
			getHostelTempleteData(data, dbo);
		}
		return data;
	}
}
