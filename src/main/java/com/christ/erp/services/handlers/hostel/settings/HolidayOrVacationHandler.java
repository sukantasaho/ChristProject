package com.christ.erp.services.handlers.hostel.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelHolidayEventsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelHolidayEventsProgrammesDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelHolidayEventsDTO;
import com.christ.erp.services.transactions.hostel.settings.HolidayOrVacationTransaction;

public class HolidayOrVacationHandler {

	private static volatile HolidayOrVacationHandler holidayOrVacationHandler = null;
    public static HolidayOrVacationHandler getInstance() {
        if(holidayOrVacationHandler==null) {
        	holidayOrVacationHandler = new HolidayOrVacationHandler();
        }
        return holidayOrVacationHandler;
    }
    
    HolidayOrVacationTransaction holidayOrVacationTransaction = HolidayOrVacationTransaction.getInstance();
    
	public List<HostelHolidayEventsDTO> getGridData() throws Exception {
		List<HostelHolidayEventsDTO> list = null;
		List<HostelHolidayEventsDBO>  dboList= holidayOrVacationTransaction.getGridData();
		if(!Utils.isNullOrEmpty(dboList)) {
			list = new ArrayList<HostelHolidayEventsDTO>();
			for (HostelHolidayEventsDBO dbo : dboList) {
				HostelHolidayEventsDTO dto = new HostelHolidayEventsDTO();
				dto.id = String.valueOf(dbo.id);
				ExModelBaseDTO baseDTO = new ExModelBaseDTO();
				baseDTO.id = !Utils.isNullOrEmpty(dbo.erpAcademicYearDBO)?String.valueOf(dbo.erpAcademicYearDBO.id):"";
				baseDTO.text = !Utils.isNullOrEmpty(dbo.erpAcademicYearDBO)?dbo.erpAcademicYearDBO.academicYearName:"";
				dto.academicYear = baseDTO;
				ExModelBaseDTO baseDTO2 = new ExModelBaseDTO();
				baseDTO2.id = !Utils.isNullOrEmpty(dbo.hostelDBO)?String.valueOf(dbo.hostelDBO.id):"";
				baseDTO2.text = !Utils.isNullOrEmpty(dbo.hostelDBO)?dbo.hostelDBO.hostelName:"";
				dto.hostel = baseDTO2;
				dto.eventType = !Utils.isNullOrEmpty(dbo.holidayType)?dbo.holidayType:"";
				dto.fromDate = !Utils.isNullOrEmpty(dbo.holidayFromDate)?Utils.convertLocalDateToStringDate(dbo.holidayFromDate):"";
				dto.toDate = !Utils.isNullOrEmpty(dbo.holidayToDate)?Utils.convertLocalDateToStringDate(dbo.holidayToDate):"";
				dto.description = !Utils.isNullOrEmpty(dbo.holidayDescription)?dbo.holidayDescription:"";
				list.add(dto);
			}
		}
		return list;
	}
	
	public boolean saveOrUpdate(HostelHolidayEventsDTO data, String userId, ApiResult<ModelBaseDTO> result) throws Exception {
		boolean isSaved = false;
		boolean duplicated = false;
		HostelHolidayEventsDBO dbo = null;
		duplicated = duplicateCheck(data, result);
		if(!duplicated && !Utils.isNullOrEmpty(data)) {
			if(Utils.isNullOrEmpty(data.id)) {
				dbo = new HostelHolidayEventsDBO();
				dbo.createdUsersId = Integer.parseInt(userId);
				dbo.recordStatus = 'A';
			}else {
				dbo = holidayOrVacationTransaction.edit(Integer.parseInt(data.id));
				dbo.modifiedUsersId = Integer.parseInt(userId);
			}
			if(!Utils.isNullOrEmpty(dbo)) {
				if(!Utils.isNullOrEmpty(data.academicYear) && !Utils.isNullOrEmpty(data.academicYear.id)) {
					ErpAcademicYearDBO yearDBO = new ErpAcademicYearDBO();
					yearDBO.id = Integer.parseInt(data.academicYear.id);
					dbo.erpAcademicYearDBO = yearDBO;
				}
				if(!Utils.isNullOrEmpty(data.hostel) && !Utils.isNullOrEmpty(data.hostel.id)) {
					HostelDBO hostelDBO = new HostelDBO();
					hostelDBO.id = Integer.parseInt(data.hostel.id);
					dbo.hostelDBO = hostelDBO;
				}
				if(!Utils.isNullOrEmpty(data.eventType)) {
					dbo.holidayType = data.eventType;
				}
				if(!Utils.isNullOrEmpty(data.fromDate)) {
					dbo.holidayFromDate = Utils.convertStringDateTimeToLocalDate(data.fromDate);
				}
				if(!Utils.isNullOrEmpty(data.toDate)) {
					dbo.holidayToDate = Utils.convertStringDateTimeToLocalDate(data.toDate);
				}
				if(!Utils.isNullOrEmpty(data.description)) {
					dbo.holidayDescription = data.description;
				}
				if(!Utils.isNullOrEmpty(data.fromSession)) {
					dbo.isHolidayFromMorning = Boolean.parseBoolean(data.fromSession);
				}
				if(!Utils.isNullOrWhitespace(data.toSession)) {
					dbo.isHolidayToEvening = Boolean.parseBoolean(data.toSession);
				}
				dbo.recordStatus = 'A';
				Set<Integer> campusProgrammeIds = null;
				HostelHolidayEventsProgrammesDBO childDbo = null;
				if (!Utils.isNullOrEmpty(data.checked)) {
					campusProgrammeIds = new HashSet<Integer>();
					campusProgrammeIds = Utils.GetCampusDepartmentMappingIds(data.checked, campusProgrammeIds);		
					if(!Utils.isNullOrEmpty(campusProgrammeIds)) {
						if(Utils.isNullOrEmpty(dbo.id)) {
							for (Integer id : campusProgrammeIds) {
								 childDbo = new HostelHolidayEventsProgrammesDBO();
								 childDbo.createdUsersId = Integer.parseInt(userId);
								 childDbo = hostelHolidayEventsDTOtoDBO(childDbo, dbo, id, userId);
								 dbo.hostelHolidayEventsProgrammesDBO.add(childDbo);
							}
						}else {
							Set<Integer> isExisistingCampusProgramIds =  new HashSet<Integer>();
							for (HostelHolidayEventsProgrammesDBO isExistingChildDbo2 : dbo.hostelHolidayEventsProgrammesDBO) {
								if(!Utils.isNullOrEmpty(isExistingChildDbo2.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(isExistingChildDbo2.erpCampusProgrammeMappingDBO.id)) {
									if(!campusProgrammeIds.contains(isExistingChildDbo2.erpCampusProgrammeMappingDBO.id)) {
										isExistingChildDbo2.modifiedUsersId = Integer.parseInt(userId);
										isExistingChildDbo2.recordStatus = 'D';
									}else {
										isExisistingCampusProgramIds.add(isExistingChildDbo2.erpCampusProgrammeMappingDBO.id);
									}
							    }
						    }
							for (Integer id3 : campusProgrammeIds) {
								if(!isExisistingCampusProgramIds.contains(id3)) {
									 childDbo = new HostelHolidayEventsProgrammesDBO();
									 childDbo.createdUsersId = Integer.parseInt(userId);
									 childDbo = hostelHolidayEventsDTOtoDBO(childDbo, dbo, id3, userId);
									 dbo.hostelHolidayEventsProgrammesDBO.add(childDbo);
								}
							}
					    }
				    }
			    }
		    }
			isSaved = holidayOrVacationTransaction.saveOrUpadte(dbo);
	    }
		return isSaved;
	}

	private boolean duplicateCheck(HostelHolidayEventsDTO data, ApiResult<ModelBaseDTO> result) throws Exception {
		List<Object[]> listDBO =  holidayOrVacationTransaction.getDuplicateCheck(data);
		if(Utils.isNullOrEmpty(listDBO))
		{
			return false;
		}else {
			result.failureMessage = "Duplicate entry for Hostel";
			return true;
		}
	}

	private HostelHolidayEventsProgrammesDBO hostelHolidayEventsDTOtoDBO(HostelHolidayEventsProgrammesDBO childDbo,
			HostelHolidayEventsDBO dbo, Integer id3, String userId) {
		 childDbo.hostelHolidayEventsDBO = dbo;
		 ErpCampusProgrammeMappingDBO campusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
		 campusProgrammeMappingDBO.id = id3;
		 childDbo.erpCampusProgrammeMappingDBO = campusProgrammeMappingDBO;
		 childDbo.recordStatus = 'A';
		return childDbo;
	}
	
	public HostelHolidayEventsDTO edit(String id) throws Exception {
		HostelHolidayEventsDBO dbo = null;
		HostelHolidayEventsDTO dto = null;
		if(!Utils.isNullOrEmpty(id)) {
			dbo = holidayOrVacationTransaction.edit(Integer.parseInt(id.trim()));
		}
		if(!Utils.isNullOrEmpty(dbo)) {
			dto = new HostelHolidayEventsDTO();
			if(!Utils.isNullOrEmpty(dbo.id))
				dto.id = String.valueOf(dbo.id);
			if(!Utils.isNullOrEmpty(dbo.erpAcademicYearDBO)) {
				ExModelBaseDTO baseDTO = new ExModelBaseDTO();
				baseDTO.id = String.valueOf(dbo.erpAcademicYearDBO.id);
				dto.academicYear = baseDTO;
			}
			if(!Utils.isNullOrEmpty(dbo.hostelDBO)) {
				ExModelBaseDTO baseDTO = new ExModelBaseDTO();
				baseDTO.id = String.valueOf(dbo.hostelDBO.id);
				dto.hostel = baseDTO;
			}
			if(!Utils.isNullOrEmpty(dbo.holidayFromDate)) {
				dto.fromDate = Utils.convertLocalDateToStringDate(dbo.holidayFromDate);
			}
			if(!Utils.isNullOrEmpty(dbo.holidayToDate)) {
				dto.toDate = Utils.convertLocalDateToStringDate(dbo.holidayToDate);
			}
			if(!Utils.isNullOrEmpty(dbo.holidayType)) {
				dto.eventType = dbo.holidayType;
			}
			if(!Utils.isNullOrEmpty(dbo.isHolidayFromMorning)) {
				dto.fromSession = String.valueOf(dbo.isHolidayFromMorning);
			}
			if(!Utils.isNullOrEmpty(dbo.isHolidayToEvening)) {
				dto.toSession = String.valueOf(dbo.isHolidayToEvening);
			}
			if(!Utils.isNullOrEmpty(dbo.holidayDescription)) {
				dto.description = String.valueOf(dbo.holidayDescription);
			}
			if(!Utils.isNullOrEmpty(dbo.hostelHolidayEventsProgrammesDBO)) {
				ArrayList<String> checked = new ArrayList<String>();
				for (HostelHolidayEventsProgrammesDBO childDbo : dbo.hostelHolidayEventsProgrammesDBO) {
					if(childDbo.recordStatus == 'A' && !Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpCampusDBO)
							&& !Utils.isNullOrEmpty(childDbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
						String str = "";
						str = childDbo.erpCampusProgrammeMappingDBO.id+"-"+childDbo.erpCampusProgrammeMappingDBO.erpCampusDBO.id+"-"+childDbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id;
						
						checked.add(str);
					}
				}
				if (!Utils.isNullOrEmpty(checked.size())) {
		            	String[] checkedArray = Utils.GetStringArray(checked);
		            	dto.checked = checkedArray;
		         }	
			}
		}
		return dto;
	}

	public boolean delete(String id, String userId) throws Exception {
		return holidayOrVacationTransaction.delete(id, userId);
	}

	
}
	
