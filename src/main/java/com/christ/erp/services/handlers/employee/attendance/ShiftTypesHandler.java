package com.christ.erp.services.handlers.employee.attendance;

import java.util.ArrayList;
import java.util.List;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpShiftTypesDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.attendance.EmpShiftTypesDTO;
import com.christ.erp.services.transactions.employee.attendance.ShiftTypesTransaction;

public class ShiftTypesHandler {

	private static volatile ShiftTypesHandler shiftTypesHandler = null;
	ShiftTypesTransaction shiftTypesTransaction = ShiftTypesTransaction.getInstance();

    public static ShiftTypesHandler getInstance() {
        if(shiftTypesHandler==null) {
        	shiftTypesHandler = new ShiftTypesHandler();
        }
        return shiftTypesHandler;
    }
	    
    public List<EmpShiftTypesDTO> getGridData() {
        List<EmpShiftTypesDTO> gridList = null;
		try {
	        List <EmpShiftTypesDBO> list = shiftTypesTransaction.getGridData();
	        if(!Utils.isNullOrEmpty(list)) {
	        	 gridList= new ArrayList<EmpShiftTypesDTO>();

	        	for (EmpShiftTypesDBO dbo: list) {
	        		EmpShiftTypesDTO dto = new EmpShiftTypesDTO(); 
	                 dto.id = String.valueOf(dbo.id);
	                 dto.shiftName  = dbo.shiftName;
	                 dto.shiftShortName = dbo.shiftShortName;
	                 dto.campus = new ExModelBaseDTO();
	                 dto.campus.id = dbo.erpCampusDBO.id.toString();
	                 dto.campus.text = dbo.erpCampusDBO.campusName;
	 			    gridList.add(dto);	                           

	        	}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return gridList;
    }
	    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ApiResult<ModelBaseDTO> saveOrUpdate(EmpShiftTypesDTO data, String userId) throws Exception {
    	ApiResult<ModelBaseDTO> result = new ApiResult();
    	Boolean isDuplicate = false;
    	List<EmpShiftTypesDBO> shiftTypesListByShiftName = null;
    	List<EmpShiftTypesDBO> shiftTypesListByShiftShortName = null;
    	if (!Utils.isNullOrEmpty(data)) {
    		EmpShiftTypesDBO empShiftTypes = null;
			if(Utils.isNullOrWhitespace(data.id) == false) {
				empShiftTypes = shiftTypesTransaction.getEmpShiftType(Integer.parseInt(data.id));
			}
			shiftTypesListByShiftName = shiftTypesTransaction.getShiftTypeByShiftName(data.shiftName,data.id,data.campus.id);
			shiftTypesListByShiftShortName = shiftTypesTransaction.getShiftTypeByShiftShortName(data.shiftShortName,data.id,data.campus.id);

			if(shiftTypesListByShiftName != null && !shiftTypesListByShiftName.isEmpty()) {
				isDuplicate = true;
				result.success = false;
				result.dto = null;
				result.failureMessage = "Duplicate entry for the Shift Name: " + data.shiftName;
			}
			else if(shiftTypesListByShiftShortName != null && !shiftTypesListByShiftShortName.isEmpty()) {
				isDuplicate = true;
				result.success = false;
				result.dto = null;
				result.failureMessage = "Duplicate entry for the Shift Short Name: " + data.shiftShortName;
			}
			if(empShiftTypes == null) {
				empShiftTypes = new EmpShiftTypesDBO();
				empShiftTypes.createdUsersId = Integer.parseInt(userId);
				
			}
			if(!isDuplicate) {
				empShiftTypes.shiftName = data.shiftName.trim();
				empShiftTypes.shiftShortName = data.shiftShortName.trim();
				empShiftTypes.isWeeklyOff = data.isWeeklyOff;
				EmpTimeZoneDBO empTimeZoneDBO = new EmpTimeZoneDBO();
				if(data.isWeeklyOff) {
					empShiftTypes.empTimeZoneDBO = null;
				}
				else{
					empTimeZoneDBO.setId(Integer.parseInt(data.timeZone.id));
					empShiftTypes.empTimeZoneDBO = empTimeZoneDBO;
				}
				
				ErpCampusDBO erpCampusDBO = new ErpCampusDBO();
				erpCampusDBO.id = Integer.parseInt(data.campus.id);
				empShiftTypes.erpCampusDBO = erpCampusDBO;
				
				empShiftTypes.recordStatus = 'A';
				if(empShiftTypes.id == null) {
					shiftTypesTransaction.saveOrUpdate(empShiftTypes);
				} else {
					empShiftTypes.modifiedUsersId = Integer.parseInt(userId);
					shiftTypesTransaction.saveOrUpdate(empShiftTypes);
				}
				if(empShiftTypes.id != 0) {
					result.success = true;
				}
			}
		}
    	return result;
    }
    
    public EmpShiftTypesDTO selectShiftTypes(String id) {
    	EmpShiftTypesDTO empShiftTypesDTO = new EmpShiftTypesDTO();
		try {
			EmpShiftTypesDBO empshiftTypes = shiftTypesTransaction.getEmpShiftType(Integer.parseInt(id));
			if(empshiftTypes != null) {
				empShiftTypesDTO.id = empshiftTypes.id.toString();
				empShiftTypesDTO.shiftName =empshiftTypes.shiftName;
				empShiftTypesDTO.shiftShortName = empshiftTypes.shiftShortName;
				empShiftTypesDTO.isWeeklyOff = empshiftTypes.isWeeklyOff;
				empShiftTypesDTO.campus = new ExModelBaseDTO();
				empShiftTypesDTO.campus.id = empshiftTypes.erpCampusDBO.id.toString();
				empShiftTypesDTO.campus.text = empshiftTypes.erpCampusDBO.campusName;
				empShiftTypesDTO.timeZone = new ExModelBaseDTO();
				if(empshiftTypes.empTimeZoneDBO != null) {
					empShiftTypesDTO.timeZone.id = String.valueOf(empshiftTypes.empTimeZoneDBO.getId());
					empShiftTypesDTO.timeZone.text = empshiftTypes.empTimeZoneDBO.getTimeZoneName();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return empShiftTypesDTO;
    }
	    
    public boolean deleteShiftTypes(String  id,String userId) {
    	try {
    		EmpShiftTypesDBO empShiftTypes = shiftTypesTransaction.getEmpShiftType(Integer.parseInt(id));
	    	if(empShiftTypes != null) {
	    		empShiftTypes.recordStatus = 'D';
	    		empShiftTypes.modifiedUsersId = Integer.parseInt(userId);
				if(empShiftTypes.id != null) {
					return shiftTypesTransaction.saveOrUpdate(empShiftTypes);
				}
			}
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
}
