package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.dto.employee.EmpProfilePFandGratuityDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpPfGratuityNomineesDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class EmpPFandGratuityDTO {
    private int jobDetailsId;
    private Boolean isWithGratuity;
    private String licGratuityNo;
    private LocalDate licGratuityDate;
    private String uanNo;
    private Boolean isWithPF;
    private String pfAccountNo;
    private LocalDate pfDate;
    private Boolean isEsiApplicable;
    private String esiInsuranceNo;
    private Boolean isBankAccountAvailable;
    private String bankAccountNo;
    private String branchIFSCCode;
    private Boolean isUanNoAvailable;
    private List<EmpProfilePFandGratuityDTO> empPfGratuityNomineesDTOList;
    public EmpPFandGratuityDTO (){

    }
    public EmpPFandGratuityDTO(int id, Boolean isWithPf, Boolean isWithGratuity, Boolean isEsiApplicable, Boolean isUanNoAvailable,
            String pfAccountNo, LocalDate pfDate, String gratuityNo, LocalDate gratuityDate, String uanNo,
            Boolean isSibAccountAvailable, String sibAccountBank, String branchIfscCode, String esiInsuranceNo) {
        this.jobDetailsId = id;
        this.isWithPF = isWithPf;
        this.isEsiApplicable = isEsiApplicable;
        this.isUanNoAvailable = isUanNoAvailable;
        this.pfAccountNo = pfAccountNo;
        this.pfDate = pfDate;
        this.licGratuityNo = gratuityNo;
        this.licGratuityDate = gratuityDate;
        this.uanNo = uanNo;
        this.isBankAccountAvailable = isSibAccountAvailable;
        this.bankAccountNo = sibAccountBank;
        this.branchIFSCCode = branchIfscCode;
        this.esiInsuranceNo = esiInsuranceNo;
    }
}
