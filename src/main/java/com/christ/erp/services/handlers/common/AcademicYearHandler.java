package com.christ.erp.services.handlers.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dto.common.AcademicYearDTO;
import com.christ.erp.services.dto.common.AcademicYearDetailsDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.common.AcademicYearTransaction;

public class AcademicYearHandler {

	private static volatile AcademicYearHandler academicYearHandler = null;
	AcademicYearTransaction academicYearTransaction = AcademicYearTransaction.getInstance();

    public static AcademicYearHandler getInstance() {
        if(academicYearHandler==null) {
        	academicYearHandler = new AcademicYearHandler();
        }
        return academicYearHandler;
    }
	    
    public List<AcademicYearDTO> getGridData() {
    	List<AcademicYearDTO> academicYearDTO = new ArrayList<>();
    	List<Tuple> list;
		try {
			list = academicYearTransaction.getGridData();
			for(Tuple tuple : list) {
				AcademicYearDTO gridDTO = new AcademicYearDTO();
				gridDTO.isCurrent = tuple.get("isCurrent").toString();
				gridDTO.id = tuple.get("ID").toString();
				gridDTO.year = tuple.get("academicYear").toString();
				academicYearDTO.add(gridDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return academicYearDTO;
    }
	    
    @SuppressWarnings({ "unchecked", "rawtypes", })
	public ApiResult<ModelBaseDTO> saveOrUpdate(AcademicYearDTO data, String userId) throws Exception {
    	ApiResult<ModelBaseDTO> result = new ApiResult();
    	Boolean isDuplicate = false;
    	List<ErpAcademicYearDBO> academicYear = null;
    	if (!Utils.isNullOrEmpty(data)) {ErpAcademicYearDBO header = null;
		if(Utils.isNullOrWhitespace(data.id) == false) {
			header = academicYearTransaction.getAcademicYearDBO(Integer.parseInt(data.id));
		}
		if(header == null) {
			header = new ErpAcademicYearDBO();
			header.createdUsersId = Integer.parseInt(userId);
			academicYear = academicYearTransaction.getAcademiYearByYear(data.academicYear.text);
			if(academicYear != null && !academicYear.isEmpty()) {
				isDuplicate = true;
				result.success = false;
				result.dto = null;
				result.failureMessage = "Duplicate entry for the year:" + data.academicYear.text;
			}
		}
		if(!isDuplicate) {
			academicYear = academicYearTransaction.getAcademicYearDBO();
			for(ErpAcademicYearDBO academicYearDBO : academicYear) {
				if(Boolean.valueOf(data.isCurrent) && Boolean.valueOf(academicYearDBO.isCurrentAcademicYear) && academicYearDBO.academicYear != Integer.parseInt(data.academicYear.text)) {
					ErpAcademicYearDBO currentAcademicYear = academicYearTransaction.getAcademicYearDBO(academicYearDBO.id);
					currentAcademicYear.isCurrentAcademicYear = false;
					academicYearTransaction.saveOrUpdate(currentAcademicYear);
				}
			}
			header.academicYear = Integer.parseInt(data.academicYear.text);
			header.academicYearName = data.academicYearName;
			header.recordStatus = 'A';
			header.isCurrentAcademicYear = Boolean.valueOf(data.isCurrent);
			if(header.id == null) {
				academicYearTransaction.saveOrUpdate(header);
			} else {
				header.modifiedUsersId = Integer.parseInt(userId);
				academicYearTransaction.saveOrUpdate(header);
			}
			if(header.id != 0) {
				List<ErpAcademicYearDetailsDBO> academicYearDetails = academicYearTransaction.getAcademiYearDetails();
				List<Integer> detailIds = new ArrayList<>();
				for(AcademicYearDetailsDTO item : data.campuses) {
					ErpAcademicYearDetailsDBO detail = null;
					if(Utils.isNullOrWhitespace(item.id) == false) {
						detail = academicYearTransaction.getAcademicYearDetailsDBO(Integer.parseInt(item.id));      
					}
					if(detail == null) {
						detail = new ErpAcademicYearDetailsDBO();
						detail.createdUsersId = Integer.parseInt(userId);
					}
					for(ErpAcademicYearDetailsDBO academicYearDetailsDBO : academicYearDetails) {
						if(Boolean.valueOf(item.isCurrent) && Boolean.valueOf(academicYearDetailsDBO.isAcademicYearCurrent) && academicYearDetailsDBO.academicYear.id != header.id && item.campus.id == academicYearDetailsDBO.campus.id) {
							ErpAcademicYearDetailsDBO currentAcademicYearDetails = academicYearTransaction.getAcademicYearDetailsDBO(academicYearDetailsDBO.id);        
							currentAcademicYearDetails.modifiedUsersId = Integer.parseInt(userId);
							academicYearTransaction.saveOrUpdate(currentAcademicYearDetails);
						}
					}
					if(item.campus.recordStatus == 'D') {
						detail.recordStatus = 'D';
					}
					else {
						detail.recordStatus = 'A';
					}
					ErpCampusDBO campusDBO = new ErpCampusDBO();
					campusDBO.id = item.campus.id;
					campusDBO.campusName = item.campus.campusName;
					detail.campus = campusDBO;
					detail.campus.id = item.campus.id;
					detail.isAcademicYearCurrent = Boolean.valueOf(item.isCurrent);
					detail.academicYearStartDate = Utils.convertStringDateTimeToLocalDate(item.startDate);
					detail.academicYearEndDate = Utils.convertStringDateTimeToLocalDate(item.endDate);
					detail.academicYear = header;
					if (detail.id == null) {
						academicYearTransaction.saveOrUpdate(detail);
					} else {
						academicYearTransaction.saveOrUpdate(detail);
					}
					detailIds.add(detail.id);
				}
				result.success = true;
			}
		}}
    	return result;
    }
    
    public AcademicYearDTO selectAcademicYear(String id) {
    	Map<Integer,String> campusMap = new HashMap<Integer,String>();
    	AcademicYearDTO academicYearDTO = new AcademicYearDTO();
    	List<Tuple> mappings;
		try {
			mappings = academicYearTransaction.getCampus();
			if(mappings != null && mappings.size() > 0) {
                for(Tuple mapping : mappings) {
               	 campusMap.put(Integer.parseInt(mapping.get("ID").toString()), mapping.get("Text").toString());
                }
            }
			ErpAcademicYearDBO academicYearInfo = academicYearTransaction.getAcademicYearDBO(Integer.parseInt(id));
			if(academicYearInfo != null) {
				academicYearDTO.id = academicYearInfo.id.toString();
				academicYearDTO.academicYear = new ExModelBaseDTO();
				academicYearDTO.academicYear.text = academicYearInfo.academicYear.toString();
				academicYearDTO.academicYearName = academicYearInfo.academicYearName.toString();
				academicYearDTO.isCurrent = academicYearInfo.isCurrentAcademicYear.toString();
				academicYearDTO.campuses = new ArrayList<>();
				if(academicYearInfo.academicYearDetails != null && academicYearInfo.academicYearDetails.size() > 0) {
					for(ErpAcademicYearDetailsDBO item : academicYearInfo.academicYearDetails) {
						AcademicYearDetailsDTO academicYearDetails = new AcademicYearDetailsDTO();
						academicYearDetails.id = item.id.toString();
						ErpCampusDBO campusDBO = new ErpCampusDBO();
						campusDBO.id = item.campus.id;
						campusDBO.campusName = item.campus.campusName;
						campusDBO.recordStatus = item.campus.recordStatus;
						academicYearDetails.campus = campusDBO;
						academicYearDetails.isCurrent = item.isAcademicYearCurrent.toString();
						academicYearDetails.endDate= Utils.convertLocalDateToStringDate(item.academicYearEndDate);
						System.out.println(academicYearDetails.endDate);
						academicYearDetails.startDate = item.academicYearStartDate.toString();
						academicYearDetails.endDate = item.academicYearEndDate.toString();
						campusMap.remove(item.campus.id);
						academicYearDTO.campuses.add(academicYearDetails);
					}
				}
			}
			for(Integer key : campusMap.keySet()) {
				Object value = campusMap.get(key);
				AcademicYearDetailsDTO academicYearDetails = new AcademicYearDetailsDTO();
				ErpCampusDBO campusDBO = new ErpCampusDBO();
				campusDBO.id =  key;
				campusDBO.campusName = (String) value;
				academicYearDetails.campus = campusDBO;
				academicYearDTO.campuses.add(academicYearDetails);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return academicYearDTO;
    }
	    
    public boolean deleteAcadmeicYear(String  id,String userId) {
    	try {
	    	ErpAcademicYearDBO erpAcademicYearDBO = academicYearTransaction.getAcademicYearDBO(Integer.parseInt(id));
	    	if(erpAcademicYearDBO != null) {
	    		erpAcademicYearDBO.recordStatus = 'D';
	    		erpAcademicYearDBO.modifiedUsersId = Integer.parseInt(userId);
				for (ErpAcademicYearDetailsDBO item : erpAcademicYearDBO.academicYearDetails) {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
				}
				if(erpAcademicYearDBO.id != null) {
					return academicYearTransaction.saveOrUpdate(erpAcademicYearDBO);
				}
			}
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
}
