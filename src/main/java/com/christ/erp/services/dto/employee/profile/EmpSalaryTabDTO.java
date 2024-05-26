package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class EmpSalaryTabDTO {
    private Integer empId;
    private List<FileUploadDownloadDTO> uniqueFileNameList;
    private List<SalaryDetailsDTO> salaryDetailsDTOList;
    private EmpPFandGratuityDTO empPFandGratuityDTO;
}
