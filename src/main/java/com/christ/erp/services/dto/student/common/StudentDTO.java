package com.christ.erp.services.dto.student.common;

import java.time.LocalDate;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO extends ModelBaseDTO {
	public int id;	
	public String registerNo;
	public String studentName;
	public String genderName;
	public LocalDate studentDob;
	private Integer erpGender;
	public String studentUniversityEmailId;
	public String studentPersonalEmailId;
	public String studentMobileNoCountryCode;
	public String studentMobileNo;
	public SelectDTO admittedYearId;
	public StudentApplnEntriesDTO studentApplnEntriesDTO;
	private StudentPersonalDataDTO studentPersonalDataDTO;
	private ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingId;
	private HostelAdmissionsDTO admissionDTO;
	private String status;
	private String acaClassDTO;
	private String offlinePrefix;	
	private SelectDTO acaClassSelectDTO;
	private Boolean isSelected;
}
