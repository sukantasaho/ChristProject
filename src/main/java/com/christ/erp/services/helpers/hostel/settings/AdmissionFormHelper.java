package com.christ.erp.services.helpers.hostel.settings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDetailsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dbobjects.student.common.StudentPersonalDataAddressDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.handlers.hostel.common.CommonHostelHandler;
import com.christ.erp.services.transactions.hostel.settings.AdmissionFormTransaction;

public class AdmissionFormHelper {

	private static volatile AdmissionFormHelper admissionFormHelper = null;
    AdmissionFormTransaction admissionFormTransaction = AdmissionFormTransaction.getInstance();
    CommonHostelHandler commonHostelHandler = CommonHostelHandler.getInstance();
    
    public static AdmissionFormHelper getInstance() {
        if(admissionFormHelper==null) {
        	admissionFormHelper = new AdmissionFormHelper();
        }
        return admissionFormHelper;
    }
    public final String HOSTEL_NAME = "[HOSTEL_NAME]";
    public final String ACADEMIC_YEAR = "[ACADEMIC_YEAR]";
    public final String CAMPUS = "[CAMPUS]";
    
	public HostelApplicationDTO convertHostelAdmissionDBOtoDTO(HostelApplicationDBO dbo, HostelApplicationDTO dto,
			LookupItemDTO lookupItemDTO) throws Exception {
		if(!Utils.isNullOrEmpty(dbo.id)) {
			dto.id = String.valueOf(dbo.id);
			dto.hostelAdmissionId = "";
		}
		if(!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO)) {
		    dto.studentApplicationId = String.valueOf(dbo.studentApplnEntriesDBO.id);
		}
		if(!Utils.isNullOrEmpty(dbo.studentDBO)) {
		    dto.studentId = String.valueOf(dbo.studentDBO.id);
		    dto.student = new StudentDTO();
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentName)) {
				dto.student.studentName = dbo.studentDBO.studentName;
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentName)) {	
				List<Integer> hostelIds ;
				if(!Utils.isNullOrEmpty(dbo.studentDBO.erpGenderDBO)) {
					hostelIds = new ArrayList<Integer>();
					if(!Utils.isNullOrEmpty(dbo.studentDBO.erpGenderDBO.genderName))
					dto.student.genderName = dbo.studentDBO.erpGenderDBO.genderName;
					List<LookupItemDTO> hostelList = commonHostelHandler.getHostelsByGender(String.valueOf(dbo.studentDBO.erpGenderDBO.erpGenderId));
					if(!Utils.isNullOrEmpty(hostelList)) {
						dto.hostelListByGender = hostelList;
						hostelList.forEach(item ->{
							hostelIds.add(Integer.parseInt(item.value));
						});
					}
					if(!Utils.isNullOrEmpty(dbo.hostelDBO) && hostelIds.contains(dbo.hostelDBO.id)) {
						lookupItemDTO = new LookupItemDTO();
						lookupItemDTO.value = String.valueOf(dbo.hostelDBO.id);
						lookupItemDTO.label = String.valueOf(dbo.hostelDBO.hostelName);
						dto.hostel = lookupItemDTO;
					}
				}
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentDob)) {
				dto.student.studentDob = dbo.studentDBO.studentDob;
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.registerNo) || !Utils.isNullOrEmpty(dbo.applicationNo)) {
				if(!Utils.isNullOrEmpty(dbo.applicationNo)) 
				dto.student.registerNo = dbo.applicationNo.toString();
				else
				dto.student.registerNo = dbo.studentDBO.registerNo;
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentMobileNo)) {
				dto.student.studentMobileNo = !Utils.isNullOrEmpty(dbo.studentDBO.studentMobileNoCountryCode)? 
						dbo.studentDBO.studentMobileNoCountryCode:""+dbo.studentDBO.studentMobileNo;
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentPersonalEmailId)) {
				dto.student.studentPersonalEmailId = dbo.studentDBO.studentPersonalEmailId;
			}
			if(!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO) &&
					!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO.applicationNo)){
				dto.hostelApplicationNo = String.valueOf(dbo.studentApplnEntriesDBO.applicationNo);
			}
		}
		int roomTypeId =0;
//		if(!Utils.isNullOrEmpty(dbo.prefferedHostelRoomTypeDBO)) {
//			lookupItemDTO = new LookupItemDTO();
//			lookupItemDTO.value = String.valueOf(dbo.prefferedHostelRoomTypeDBO.id);
//			lookupItemDTO.label = String.valueOf(dbo.prefferedHostelRoomTypeDBO.roomType);
//			dto.roomType = lookupItemDTO;
//			roomTypeId = dbo.prefferedHostelRoomTypeDBO.id;
//		}else if(!Utils.isNullOrEmpty(dbo.allottedHostelRoomTypeDBO)) {
//			lookupItemDTO = new LookupItemDTO();
//			lookupItemDTO.value = String.valueOf(dbo.allottedHostelRoomTypeDBO.id);
//			lookupItemDTO.label = String.valueOf(dbo.allottedHostelRoomTypeDBO.roomType);
//			dto.roomType = lookupItemDTO;
//			roomTypeId = dbo.allottedHostelRoomTypeDBO.id;
//		}
		if(!Utils.isNullOrEmpty(dbo.hostelDBO)) {
			int seat=0;
			if(!Utils.isNullOrEmpty(dbo.erpAcademicYearDBO) &&  !Utils.isNullOrEmpty(roomTypeId)  && roomTypeId!=0) {
				HostelSeatAvailabilityDBO availabilityDBO = admissionFormTransaction.getHostelSeatesAvailblity(dbo.erpAcademicYearDBO.id,dbo.hostelDBO.id, roomTypeId);
                BigInteger hostelSeatfilled = admissionFormTransaction.getTotalHostelSeatsFilled(dbo.erpAcademicYearDBO.id,dbo.hostelDBO.id, roomTypeId);	
				if(!Utils.isNullOrEmpty(availabilityDBO) && !Utils.isNullOrEmpty(availabilityDBO.hostelSeatAvailabilityDetailsDBO)) {
					for (HostelSeatAvailabilityDetailsDBO  detailsDBO: availabilityDBO.hostelSeatAvailabilityDetailsDBO) {
						if(!Utils.isNullOrEmpty(detailsDBO.availableSeats) && !Utils.isNullOrEmpty(detailsDBO.hostelRoomTypeDBO)
								&& detailsDBO.hostelRoomTypeDBO.id == roomTypeId && detailsDBO.recordStatus == 'A') {						
						    if(!Utils.isNullOrEmpty(hostelSeatfilled)) {
						    	seat = detailsDBO.availableSeats - hostelSeatfilled.intValue();
								if(!Utils.isNullOrEmpty(seat) && seat != 0 ) {
									dto.availableSeats = String.valueOf(seat);
								}
						    }
						}
					}
				}
			}
		}
		return dto;
	}

	public HostelAdmissionsDBO convertDTOtoHostelAdmissionDBO(HostelAdmissionsDBO dbo, HostelApplicationDTO data, String userId) {
		if(!Utils.isNullOrEmpty(data.id)) {
			HostelApplicationDBO applicationDBO = new HostelApplicationDBO();
			applicationDBO.id = Integer.parseInt(data.id);
			dbo.hostelApplicationDBO = applicationDBO;
		} 
		if(!Utils.isNullOrEmpty(data.academicYear.value)) {
			ErpAcademicYearDBO academicYear = new ErpAcademicYearDBO();
			academicYear.id = Integer.parseInt(data.academicYear.value);
			dbo.erpAcademicYearDBO = academicYear;
		}
		if(!Utils.isNullOrEmpty(data.studentApplicationId)) {
			StudentApplnEntriesDBO studentApplnEntriesDBO = new StudentApplnEntriesDBO();
			studentApplnEntriesDBO.id = Integer.parseInt(data.studentApplicationId);
			dbo.studentApplnEntriesDBO = studentApplnEntriesDBO;
		}
		if(!Utils.isNullOrEmpty(data.studentId)) {
			StudentDBO studentDBO = new StudentDBO();
			studentDBO.id = Integer.parseInt(data.studentId);
			dbo.studentDBO = studentDBO;
		}
		if(!Utils.isNullOrEmpty(data.hostel.value)) {
			HostelDBO hostelDBO = new HostelDBO();
			hostelDBO.id = Integer.parseInt(data.hostel.value);
			dbo.hostelDBO = hostelDBO;
		}
		if(!Utils.isNullOrEmpty(data.roomType.value)) {
			HostelRoomTypeDBO hostelRoomTypeDBO = new HostelRoomTypeDBO();
			hostelRoomTypeDBO.id = Integer.parseInt(data.roomType.value);
			dbo.hostelRoomTypeDBO = hostelRoomTypeDBO;
		}
		if(!Utils.isNullOrEmpty(data.dateOfAdmission)) {
			dbo.dateOfAdmission = Utils.convertStringDateTimeToLocalDateTime(data.dateOfAdmission);
		}
//		dbo.currentStatus = "Admitted";
		dbo.recordStatus = 'A';
		return dbo;
	}

	public HostelApplicationDTO convertHostelAdmissionDBOtoDTO(HostelAdmissionsDBO dbo,
			HostelApplicationDTO dto, LookupItemDTO lookupItemDTO) throws Exception {
		if(!Utils.isNullOrEmpty(dbo.id)) {
			dto.hostelAdmissionId = String.valueOf(dbo.id);
		}
		if(!Utils.isNullOrEmpty(dbo.studentApplnEntriesDBO)) {
		    dto.studentApplicationId = String.valueOf(dbo.studentApplnEntriesDBO.id);
		}
		if(!Utils.isNullOrEmpty(dbo.dateOfAdmission)) {
		    dto.dateOfAdmission = Utils.convertLocalDateTimeToStringDate(dbo.dateOfAdmission);
		}
		if(!Utils.isNullOrEmpty(dbo.studentDBO)) {
		    dto.studentId = String.valueOf(dbo.studentDBO.id);
		    dto.student = new StudentDTO();
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentName)) {
				dto.student.studentName = dbo.studentDBO.studentName;
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.erpGenderDBO)) {
				if(!Utils.isNullOrEmpty(dbo.studentDBO.erpGenderDBO.genderName))
				dto.student.genderName = dbo.studentDBO.erpGenderDBO.genderName;
				List<Integer> hostelIds;
				List<LookupItemDTO> hostelList = commonHostelHandler.getHostelsByGender(String.valueOf(dbo.studentDBO.erpGenderDBO.erpGenderId));
				hostelIds = new ArrayList<Integer>();
				if(!Utils.isNullOrEmpty(hostelList)) {
					dto.hostelListByGender = hostelList;
					hostelList.forEach(item ->{
						hostelIds.add(Integer.parseInt(item.value));
					});
				}
				if(!Utils.isNullOrEmpty(dbo.hostelDBO) && hostelIds.contains(dbo.hostelDBO.id)) {
					lookupItemDTO = new LookupItemDTO();
					lookupItemDTO.value = String.valueOf(dbo.hostelDBO.id);
					lookupItemDTO.label = String.valueOf(dbo.hostelDBO.hostelName);
					dto.hostel = lookupItemDTO;
				}
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentDob)) {
				dto.student.studentDob = dbo.studentDBO.studentDob;
			}
			if(!Utils.isNullOrEmpty(dbo.hostelApplicationDBO) && !Utils.isNullOrEmpty(dbo.hostelApplicationDBO.studentDBO)
					&&(!Utils.isNullOrEmpty(dbo.studentDBO.registerNo) || !Utils.isNullOrEmpty(dbo.hostelApplicationDBO.applicationNo))) {
				if(!Utils.isNullOrEmpty(dbo.hostelApplicationDBO)) 
				dto.student.registerNo = dbo.hostelApplicationDBO.studentDBO.registerNo;
				else
				dto.student.registerNo =dbo.hostelApplicationDBO.applicationNo.toString();
				
			}
			if(!Utils.isNullOrEmpty(dbo.hostelApplicationDBO) && !Utils.isNullOrEmpty(dbo.hostelApplicationDBO.studentApplnEntriesDBO) &&
					!Utils.isNullOrEmpty(dbo.hostelApplicationDBO.studentApplnEntriesDBO.applicationNo)){
				dto.hostelApplicationNo = String.valueOf(dbo.hostelApplicationDBO.studentApplnEntriesDBO.applicationNo);
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentMobileNo)) {
				StringBuffer str = new StringBuffer();
				str.append(!Utils.isNullOrEmpty(dbo.studentDBO.studentMobileNoCountryCode)? dbo.studentDBO.studentMobileNoCountryCode+"-":"");
				str.append(dbo.studentDBO.studentMobileNo);
				dto.student.studentMobileNo=str.toString();
			}
			if(!Utils.isNullOrEmpty(dbo.studentDBO.studentPersonalEmailId)) {
				dto.student.studentPersonalEmailId = dbo.studentDBO.studentPersonalEmailId;
			}
		}
		if(!Utils.isNullOrEmpty(dbo.hostelRoomTypeDBO)) {
			lookupItemDTO = new LookupItemDTO();
			lookupItemDTO.value = String.valueOf(dbo.hostelRoomTypeDBO.id);
			lookupItemDTO.label = String.valueOf(dbo.hostelRoomTypeDBO.roomType);
			dto.roomType = lookupItemDTO;
		}
		if(!Utils.isNullOrEmpty(dbo.hostelDBO) && !Utils.isNullOrEmpty(dbo.erpAcademicYearDBO)) {
			HostelSeatAvailabilityDBO availabilityDBO = admissionFormTransaction.getHostelSeatesAvailblity(dbo.erpAcademicYearDBO.id,dbo.hostelDBO.id, dbo.hostelRoomTypeDBO.id);
			if(!Utils.isNullOrEmpty(availabilityDBO) && !Utils.isNullOrEmpty(availabilityDBO.hostelSeatAvailabilityDetailsDBO) && 
					!Utils.isNullOrEmpty(dbo.hostelRoomTypeDBO)) {
				BigInteger hostelSeatfilled = admissionFormTransaction.getTotalHostelSeatsFilled(dbo.erpAcademicYearDBO.id,dbo.hostelDBO.id, dbo.hostelRoomTypeDBO.id);
				if(!Utils.isNullOrEmpty(hostelSeatfilled)) {
					for (HostelSeatAvailabilityDetailsDBO  detailsDBO: availabilityDBO.hostelSeatAvailabilityDetailsDBO) {
						if(!Utils.isNullOrEmpty(detailsDBO.availableSeats) && !Utils.isNullOrEmpty(detailsDBO.hostelRoomTypeDBO) && detailsDBO.hostelRoomTypeDBO.id == dbo.hostelRoomTypeDBO.id && detailsDBO.recordStatus=='A') {
							dto.availableSeats = String.valueOf(detailsDBO.availableSeats - hostelSeatfilled.intValue());
							dto.prefernceForRoomStyle = String.valueOf(detailsDBO.availableSeats);
						}
					}
				}
			}
		}
		if(!Utils.isNullOrEmpty(dbo.erpStatusDBO)) {
			dto.status = !Utils.isNullOrEmpty(dbo.erpStatusDBO.statusCode)?dbo.erpStatusDBO.statusCode:"";
		}
		return dto;
	}
	
	public void convertHostelTemplateToPrintingString(HostelApplicationDTO data, HostelDBO hostelDBO) {
		if(!Utils.isNullOrEmpty(hostelDBO.erpTemplateDBO)) {
			if(!Utils.isNullOrEmpty(hostelDBO.erpTemplateDBO.templateContent)){
				String templetStr = hostelDBO.erpTemplateDBO.templateContent;
				if(!Utils.isNullOrEmpty(hostelDBO.hostelName))
					templetStr = templetStr.replace(HOSTEL_NAME, hostelDBO.hostelName.toString());
		    	if(!Utils.isNullOrEmpty(data.academicYear) && !Utils.isNullOrEmpty(data.academicYear.label))
		    		templetStr = templetStr.replace(ACADEMIC_YEAR, data.academicYear.label);
		       	if(!Utils.isNullOrEmpty(hostelDBO))
		    		templetStr = templetStr.replace(CAMPUS, "");
				data.printTemplate = templetStr;
			}
		}
	}

	public void convertHostelTemplateStudentAddrDBOtoDTO(HostelApplicationDTO data, StudentPersonalDataAddressDBO studentPersonalDataAddressDBO) {
		StringBuffer parentAddress = new StringBuffer(), gardianAddress = new StringBuffer();
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentAddressLine1)) {
			parentAddress.append(studentPersonalDataAddressDBO.permanentAddressLine1+",");
			gardianAddress.append(studentPersonalDataAddressDBO.permanentAddressLine1+",");
		}
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentCityDBO) && !Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentCityDBO.cityName)) {
			parentAddress.append(studentPersonalDataAddressDBO.permanentCityDBO.cityName+",");
			gardianAddress.append(studentPersonalDataAddressDBO.permanentCityDBO.cityName+",");
		}
		else if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentCityDBO)&& !Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentCityDBO.cityName)) {
			parentAddress.append(studentPersonalDataAddressDBO.permanentCityDBO.cityName+",");
			gardianAddress.append(studentPersonalDataAddressDBO.permanentCityDBO.cityName+",");
		}
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentStateDBO)
				&& !Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentStateDBO.stateName)) {
			parentAddress.append(studentPersonalDataAddressDBO.permanentStateDBO.stateName+",");
			gardianAddress.append(studentPersonalDataAddressDBO.permanentStateDBO.stateName+",");
		}
		else if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentStateOthers)) {
			parentAddress.append(studentPersonalDataAddressDBO.permanentStateOthers+",");
			gardianAddress.append(studentPersonalDataAddressDBO.permanentStateOthers+",");
		}
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentCountryDBO)
				&& !Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentCountryDBO.countryName)) {
			parentAddress.append(studentPersonalDataAddressDBO.permanentCountryDBO.countryName+",");
			gardianAddress.append(studentPersonalDataAddressDBO.permanentCountryDBO.countryName+",");
		}
	    if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.permanentPincode)){
			parentAddress.append(studentPersonalDataAddressDBO.permanentPincode+",");
			gardianAddress.append("pin code -"+studentPersonalDataAddressDBO.permanentPincode+",");
	    }
	    parentAddress.append("mobile no :");
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.fatherMobileNoCountryCode)) {
			parentAddress.append(studentPersonalDataAddressDBO.fatherMobileNoCountryCode+"-");
		    if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.fatherMobileNo))
				parentAddress.append(studentPersonalDataAddressDBO.fatherMobileNo+",");
		}else if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.fatherMobileNo))
			parentAddress.append(studentPersonalDataAddressDBO.fatherMobileNo+",");
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.motherMobileNoCountryCode)) {
			parentAddress.append(studentPersonalDataAddressDBO.motherMobileNoCountryCode+"-");
		    if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.motherMobileNo))
				parentAddress.append(studentPersonalDataAddressDBO.motherMobileNo+".");
		}else if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.motherMobileNo))
			parentAddress.append(studentPersonalDataAddressDBO.motherMobileNo+".");
	    gardianAddress.append("mobile no :");
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.guardianMobileNoCountryCode)) {
			gardianAddress.append(studentPersonalDataAddressDBO.guardianMobileNoCountryCode+"-");
		    if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.guardianMobileNo))
		    	gardianAddress.append(studentPersonalDataAddressDBO.guardianMobileNo+",");
		}else if(!Utils.isNullOrEmpty(studentPersonalDataAddressDBO.guardianMobileNo))
			gardianAddress.append(studentPersonalDataAddressDBO.guardianMobileNo+",");
		data.parentAddress = parentAddress.toString();
		data.guardianAddress = gardianAddress.toString();
	}
}
