package com.christ.erp.services.dto.common;


import com.christ.erp.services.dbobjects.common.ErpProgrammeDegreeDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpProgrammeAddtnlDetailsDTO {
	
	private int id;
    private ErpProgrammeDTO erpProgrammeDTO;
    private Integer changedFromYear;
    private Integer changedToYear;
    private String programmeName;
    private String programmeCode;
    private String  programmeShortName;
    private ErpProgrammeDegreeDTO erpProgrammeDegreeDTO;
    private ErpDeaneryDTO erpDeaneryDTO;
    private String searchKeyword;
    private String mode;
  //  private String valueAddedOrCareerOriented;
    private Boolean isMultipleMajor;
//    private Boolean isHavingPso;
//    private Boolean isHavingPeo;
    private Boolean isImplementedCBCS;
    private Integer cbcsImplementedYear;
    private Boolean isImplementedECS;
    private Integer ecsImplementedYear;
	private SelectDTO coordinatingDepartment;
	private SelectDTO erpProgrammeDegree;	
	private SelectDTO erpDeanery;
//	private String  interDisciplinaryOrInnovative;
	private String  programmeDisplayname;
}
