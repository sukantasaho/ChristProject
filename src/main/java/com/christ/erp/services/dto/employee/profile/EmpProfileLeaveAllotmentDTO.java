package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;

@Getter
@Setter
public class EmpProfileLeaveAllotmentDTO {
    private int leaveAllotmentId;
    private SelectDTO leaveType;
    private BigDecimal leavesAllotted;
    private BigDecimal leavesSanctioned;
    private BigDecimal leavesRemaining;
    private BigDecimal leavesPending;
    private Integer year;
    private String month;
    private Integer displayOrder;
    private Integer leaveInitializationMonth;

    public EmpProfileLeaveAllotmentDTO(){

    }
    public EmpProfileLeaveAllotmentDTO(Integer leaveTypeId, String leaveTypeName,Integer allottedLeaves, Integer leaveInitializationMonth, Integer displayOrder){
        if(!Utils.isNullOrEmpty(leaveTypeId)) {
            this.leaveType = new SelectDTO();
            this.leaveType.setLabel(leaveTypeName);
            this.leaveType.setValue(Integer.toString(leaveTypeId));
        }
        if(!Utils.isNullOrEmpty(allottedLeaves)) {
            this.leavesAllotted = BigDecimal.valueOf(allottedLeaves);
        }
        this.leaveInitializationMonth = leaveInitializationMonth;
        this.displayOrder = displayOrder;
    }
    public EmpProfileLeaveAllotmentDTO( Integer leaveAllotmentId, Integer leaveTypeId, String leaveTypeName, BigDecimal allottedLeaves, BigDecimal sanctionedLeaves,
                                        BigDecimal leavesRemaining, BigDecimal leavesPending, Integer year, String month) {
        this.leaveAllotmentId = leaveAllotmentId;
        if(!Utils.isNullOrEmpty(leaveTypeId)) {
            this.leaveType = new SelectDTO();
            this.leaveType.setLabel(leaveTypeName);
            this.leaveType.setValue(Integer.toString(leaveTypeId));
        }
        this.leavesAllotted = allottedLeaves;
        this.leavesSanctioned = sanctionedLeaves;
        this.leavesRemaining = leavesRemaining;
        this.leavesPending = leavesPending;
        this.month = month;
        this.year = year;
    }
}
