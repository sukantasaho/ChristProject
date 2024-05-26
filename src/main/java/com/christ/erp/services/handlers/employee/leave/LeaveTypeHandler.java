package com.christ.erp.services.handlers.employee.leave;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveTypeDTO;
import com.christ.erp.services.transactions.employee.leave.LeaveTypeTransaction;

public class LeaveTypeHandler {
	private static volatile LeaveTypeHandler  leaveTypeHandler=null;
	
	LeaveTypeTransaction leaveTypeTransaction=LeaveTypeTransaction.getInstance();
	
	public static LeaveTypeHandler getInstance() {
        if(leaveTypeHandler==null) {
        	leaveTypeHandler = new LeaveTypeHandler();
        }
        return leaveTypeHandler;
    }

	public  boolean delete(EmpLeaveTypeDTO leaveTypeMapping, String userId) throws Exception {
		return leaveTypeTransaction.delete(leaveTypeMapping,userId);
	}

	public  EmpLeaveTypeDTO edit(int id) throws Exception {
		EmpLeaveTypeDTO empLeaveTypeDTO = new EmpLeaveTypeDTO();
		EmpLeaveTypeDBO dbleavetypeMappingInfo = leaveTypeTransaction.edit(id);
		if (dbleavetypeMappingInfo != null) {
            empLeaveTypeDTO.id = dbleavetypeMappingInfo.id;
            empLeaveTypeDTO.leavetype = dbleavetypeMappingInfo.leaveTypeName;
            empLeaveTypeDTO.leavecode = dbleavetypeMappingInfo.leaveTypeCode;
            empLeaveTypeDTO.leaveTypeColorCodeHexvalue = dbleavetypeMappingInfo.leaveTypeColorCodeHexvalue;
            empLeaveTypeDTO.isApplyOnline = dbleavetypeMappingInfo.isApplyOnline;
            empLeaveTypeDTO.partialDaysAllowed = dbleavetypeMappingInfo.partialDaysAllowed;
           // empLeaveTypeDTO.autoApprovedDays = dbleavetypeMappingInfo.autoApprovedDays;
           // empLeaveTypeDTO.continousDays = dbleavetypeMappingInfo.continousDays;
            empLeaveTypeDTO.maxOnlineLeaveInMonth = dbleavetypeMappingInfo.maxOnlineLeaveInMonth;
            empLeaveTypeDTO.isExemption = dbleavetypeMappingInfo.isExemption;
            empLeaveTypeDTO.supportingDoc = dbleavetypeMappingInfo.supportingDoc;
            empLeaveTypeDTO.isLeave = dbleavetypeMappingInfo.isLeave;
          //  empLeaveTypeDTO.autoApproveLeave = dbleavetypeMappingInfo.autoApproveLeave;
            empLeaveTypeDTO.isLeaveAdvance=dbleavetypeMappingInfo.isLeaveAdvance;
            empLeaveTypeDTO.leavePolicy = dbleavetypeMappingInfo.leavePolicy;
            empLeaveTypeDTO.maxOnlineLeavePermittedInMonth=dbleavetypeMappingInfo.maxOnlineLeavePermittedInMonth;
            empLeaveTypeDTO.maxOnlineLeaveWithProof=dbleavetypeMappingInfo.maxOnlineLeaveWithProof;
            empLeaveTypeDTO.isHolidayCounted=dbleavetypeMappingInfo.isHolidayCounted;
            empLeaveTypeDTO.isSundayCounted=dbleavetypeMappingInfo.isSundayCounted;
            
        }
		return empLeaveTypeDTO;
	}

	public  List<EmpLeaveTypeDTO> getGridData() throws Exception {
		 List<Tuple> mappings = leaveTypeTransaction.getGridData();
		 List<EmpLeaveTypeDTO>  listleavetypes = new ArrayList<>();
         if(mappings != null && mappings.size() > 0) {
             for(Tuple mapping : mappings) {
                 EmpLeaveTypeDTO mappingInfo = new EmpLeaveTypeDTO();
                 mappingInfo.id = Integer.parseInt(mapping.get("ID").toString());
                 mappingInfo.leavetype = mapping.get("leaveTypeName").toString();
                 mappingInfo.leavecode = mapping.get("leaveTypeCode").toString();
                 mappingInfo.isApplyOnline = (Boolean) mapping.get("isApplyOnline");
                 mappingInfo.isExemption= (Boolean) mapping.get("Exemption");
                 mappingInfo.isLeave= (Boolean) mapping.get("CanAllotLeave");
                 listleavetypes.add(mappingInfo);
             }
         }
		return listleavetypes;
	}

	public  Boolean duplicateCheckLeaveType( EmpLeaveTypeDTO leaveTypeMapping) throws Exception {
		return leaveTypeTransaction.duplicateCheckLeaveType(leaveTypeMapping);
	}

	public  Boolean saveOrUpdate(EmpLeaveTypeDTO leaveTypeMapping, String userId) throws Exception {
		EmpLeaveTypeDBO header=null;
	    if(Utils.isNullOrEmpty(leaveTypeMapping.id) == true) {
	    	header=new EmpLeaveTypeDBO();
	    }else {
	    	header = leaveTypeTransaction.edit(leaveTypeMapping.id);
	    }
		header.leaveTypeName = leaveTypeMapping.leavetype;
        header.leaveTypeCode = leaveTypeMapping.leavecode.toUpperCase();
        header.leaveTypeColorCodeHexvalue = leaveTypeMapping.leaveTypeColorCodeHexvalue;
        header.isApplyOnline = leaveTypeMapping.isApplyOnline;
        if(header.isApplyOnline == true) {
        	if(Utils.isNullOrEmpty(leaveTypeMapping.maxOnlineLeaveInMonth)) {
        		header.maxOnlineLeaveInMonth = 0;
            }else {
                header.maxOnlineLeaveInMonth = leaveTypeMapping.maxOnlineLeaveInMonth;
            }
        	if(Utils.isNullOrEmpty(leaveTypeMapping.maxOnlineLeavePermittedInMonth)) {
        		header.maxOnlineLeavePermittedInMonth = 0;
            }else {
                header.maxOnlineLeavePermittedInMonth = leaveTypeMapping.maxOnlineLeavePermittedInMonth;
            }
        	if(Utils.isNullOrEmpty(leaveTypeMapping.maxOnlineLeaveWithProof)) {
        		header.maxOnlineLeaveWithProof = 0;
            }else {
                header.maxOnlineLeaveWithProof = leaveTypeMapping.maxOnlineLeaveWithProof;
            }
        }else {
        	header.maxOnlineLeaveInMonth = 0;
        	header.maxOnlineLeavePermittedInMonth = 0;
        	header.maxOnlineLeaveWithProof = 0;
        }
//        header.autoApproveLeave = leaveTypeMapping.autoApproveLeave;
//        if(header.autoApproveLeave == true) {
//        	if(!Utils.isNullOrEmpty(leaveTypeMapping.autoApprovedDays)) {
//        		header.autoApprovedDays = 0;
//            }else {
//            	header.autoApprovedDays = leaveTypeMapping.autoApprovedDays;
//            }
//        }else {
//        	header.autoApprovedDays = 0;
//        }
//        header.continousDays = leaveTypeMapping.continousDays;
        header.isExemption = leaveTypeMapping.isExemption;
        header.isLeave = leaveTypeMapping.isLeave;
        header.partialDaysAllowed = leaveTypeMapping.partialDaysAllowed;
        header.supportingDoc = leaveTypeMapping.supportingDoc;
        header.leavePolicy = leaveTypeMapping.leavePolicy;
        header.isLeaveAdvance=leaveTypeMapping.isLeaveAdvance;
        header.isSundayCounted=leaveTypeMapping.isSundayCounted;
        //header.isHolidayCounted=leaveTypeMapping.isHolidayCounted;
        Boolean isTrueOrFalse=null;
        if(header.id == 0) {
        	header.createdUsersId=Integer.parseInt(userId);
            header.recordStatus = 'A';
            isTrueOrFalse=leaveTypeTransaction.saveOrUpdate(header);
        }else {
        	header.modifiedUsersId=Integer.parseInt(userId);
            isTrueOrFalse=leaveTypeTransaction.saveOrUpdate(header);
        }
		return isTrueOrFalse;
	}
}
