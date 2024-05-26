package com.christ.erp.services.dto.hostel.settings;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelAdmissionsDTO {
	private int id;
	private StudentDTO studentDTO;
	private HostelDTO hostelDTO;
	private SelectDTO hostelRoomTypeDTO;
	private String erpStatus;
	private SelectDTO erpAcademicYearDTO;
	private StudentApplnEntriesDTO studentApplnEntriesDTO;
	private HostelApplicationDTO hostelApplicationDTO;
	private SelectDTO block;
	private SelectDTO unit;
	private SelectDTO roomNo;
	private SelectDTO floorNo;
	private SelectDTO bedNo;
	private SelectDTO roomtype;
	private SelectDTO academicYear;
	private SelectDTO hostel;
	private Integer biometricId;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private String checkOutRemarks;
	private List<HostelAdmissionsFacilityDTO> hostelAdmissionsFacilityList;
	private String cancelledReason;
	private Integer cancelledByUserId;
}
