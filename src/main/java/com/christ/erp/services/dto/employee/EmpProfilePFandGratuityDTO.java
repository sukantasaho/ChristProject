package com.christ.erp.services.dto.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.christ.erp.services.common.Utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpProfilePFandGratuityDTO {
	private int empPfGratuityNomineesId;
	private Integer empJobDetailsId;
	private String nomineeName;
	private String nomineeAddress;
	private String nomineeRelationship;
	private LocalDate nomineeDob;
	private String sharePercentage;
	private String under18GuardName;
	private String under18GuardianAddress;
	private Boolean isPf;
	private Boolean isGratuity;
	
	public EmpProfilePFandGratuityDTO() {
	}
	
	public EmpProfilePFandGratuityDTO( int empPfGratuityNomineesId, Integer empJobDetailsId, String nomineeName, String nomineeAddress, String nomineeRelationship, LocalDate nomineeDob,
			BigDecimal sharePercentage, String under18GuardName, String under18GuardianAddress, Boolean isPf, Boolean isGratuity) {
		this.empPfGratuityNomineesId = empPfGratuityNomineesId; 
		this.empJobDetailsId = empJobDetailsId;
		this.nomineeName = nomineeName;
		this.nomineeAddress = nomineeAddress;
		this.nomineeRelationship = nomineeRelationship;
		this.nomineeDob = nomineeDob;
		this.sharePercentage = !Utils.isNullOrEmpty(sharePercentage)? String.valueOf(sharePercentage):"";;
		this.under18GuardName = under18GuardName;
		this.under18GuardianAddress = under18GuardianAddress;
		this.isPf = isPf;
		this.isGratuity = isGratuity;
	}
}
