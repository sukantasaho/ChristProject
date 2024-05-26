package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmpProfileGuestContractDetailsDTO {
    private int id;
    private String specialisation;
    private String semester;
    private BigDecimal guestWorkingHoursWeek;
    private String guestRefferedBy;
    private LocalDate contractEmpStartDate;
    private LocalDate contractEmpEndDate;
    private String contractEmpLetterNo;
    private CommonUploadDownloadDTO document;
    private String remarks;
    private SelectDTO campus;
    private SelectDTO department;
    private Boolean isCurrent;
    private SelectDTO payType;
    private BigDecimal payAmount;

    public EmpProfileGuestContractDetailsDTO(){

    }
    public EmpProfileGuestContractDetailsDTO(int id, String specialisation, String semester, BigDecimal guestWorkingHoursWeek, String guestRefferedBy,
             LocalDate contractEmpStartDate, LocalDate contractEmpEndDate, String contractEmpLetterNo, String fileNameUnique,
             String fileNameOrg, String processCode, String remarks,  Integer departmentId, String departmentName, Integer campusId,
             String campusName, Boolean isCurrent, String payType, BigDecimal payAmount){
        this.id = id;
        this.specialisation = specialisation;
        this.semester = semester;
        this.guestWorkingHoursWeek = guestWorkingHoursWeek;
        this.guestRefferedBy = guestRefferedBy;
        this.contractEmpStartDate = contractEmpStartDate;
        this.contractEmpEndDate = contractEmpEndDate;
        this.contractEmpLetterNo = contractEmpLetterNo;
        SelectDTO payTypeDTO = new SelectDTO();
        payTypeDTO.setLabel(payType);
        payTypeDTO.setValue(payType);
        this.payType = payTypeDTO;
        this.payAmount = payAmount;

        CommonUploadDownloadDTO documentUploadDTO = null;
        if(!Utils.isNullOrEmpty(fileNameUnique)) {
            documentUploadDTO = new CommonUploadDownloadDTO();
            documentUploadDTO.setActualPath(fileNameUnique);
            documentUploadDTO.setOriginalFileName(fileNameOrg);
            documentUploadDTO.setProcessCode(processCode);
            documentUploadDTO.setNewFile(false);
        }
        this.remarks = remarks;
        if(!Utils.isNullOrEmpty(departmentId)) {
            this.department = new SelectDTO();
            this.department.setLabel(departmentName);
            this.department.setValue(Integer.toString(departmentId));
        }
        if(!Utils.isNullOrEmpty(campusId)) {
            this.campus = new SelectDTO();
            this.campus.setLabel(campusName);
            this.campus.setValue(Integer.toString(departmentId));
        }
        this.isCurrent = isCurrent;
    }

}
