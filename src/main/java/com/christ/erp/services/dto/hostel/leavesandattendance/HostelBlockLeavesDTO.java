package com.christ.erp.services.dto.hostel.leavesandattendance;

import java.time.LocalDate;

import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelBlockLeavesDTO {
	public int id;
	public String blockReason;
	public LocalDate blockDate;
	public ErpAcademicYearDTO erpAcademicYearDTO;
	public HostelAdmissionsDTO hostelAdmissionsDTO;
	public Integer modifiedUsersId;
}
