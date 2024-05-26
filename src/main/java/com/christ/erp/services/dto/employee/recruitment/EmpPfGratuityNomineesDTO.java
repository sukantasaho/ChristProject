package com.christ.erp.services.dto.employee.recruitment;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.christ.erp.services.common.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpPfGratuityNomineesDTO {

    public String empPfGratuityNomineesId;
    public String empJobDetailsId;
    public String nominee;
    public String nomineeAddress;
    public String nomineeRelationship;
    public String nomineeDob;
    public String sharePercentage;
    public String under18GuardName;
    public String under18GuardianAddress;
    public Boolean isPf;
    public Boolean isGratuity;
    public int empId;
    private LocalDate nomineesDob;
    
    public EmpPfGratuityNomineesDTO(int empId,String nominee, String nomineeAddress, String nomineeRelationship, LocalDate nomineeDob,BigDecimal sharePercentage,
    		String under18GuardName, String under18GuardianAddress, Boolean isPf,  Boolean isGratuity) {
    	this.empId = empId;
    	this.nominee = nominee;
    	this.nomineeAddress = nomineeAddress;
    	this.nomineeRelationship = nomineeRelationship;
    	this.nomineeDob = !Utils.isNullOrEmpty(nomineeDob)?Utils.convertLocalDateToStringDate(nomineeDob):"";
    	this.sharePercentage = !Utils.isNullOrEmpty(sharePercentage)?String.valueOf(sharePercentage):"";
    	this.under18GuardName = under18GuardName;
    	this.under18GuardianAddress = under18GuardianAddress;
    	this.isPf = isPf;
    	this.isGratuity = isGratuity;
    }
}
