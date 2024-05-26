package com.christ.erp.services.dto.hostel.leavesandattendance;

import java.time.LocalDate;
import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AbsenteesListDTO {
	
	private int id;
	private Boolean alertParent = false;
	private Boolean alertStudent = false;
	private Boolean addToFine = false;
	private String regNo;
	private String studentPhotoUrl;
	private String name;
	private String room;
	private String bed;
	private String studentEmail;
	private String studentPhoneNo;
	private String parentEmail;
	private String parentPhoneNo;
	private Integer admissionId;
	private List<SelectDTO> sessionsList;
	private SelectDTO academicYearSelected;
	private SelectDTO selectedHostel;
	private SelectDTO selectedBlock;
	private SelectDTO selectedUnit;
	private LocalDate leaveDate;
	private String programme;
	private String classProgramme;
	private String leaveSession;
	private Boolean morning = false;
	private Boolean evening = false;
	private Boolean alreadyFineExists = false;
	private Boolean alreadyParentAlertSent = false;
	private Boolean alreadyStudentAlertSent = false;

}
