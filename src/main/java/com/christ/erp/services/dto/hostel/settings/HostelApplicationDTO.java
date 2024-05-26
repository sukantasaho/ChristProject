package com.christ.erp.services.dto.hostel.settings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelApplicationDTO extends ExModelBaseDTO {

	public Integer hostelApplicationNum;
	public String studentApplication;
	public LocalDateTime dateOfApplication;
	public String regesterNum;
	public LookupItemDTO academicYear;
	public String availableSeats;
	public String dateOfAdmission;
	public LookupItemDTO hostel;
	public String studentId;
	public String studentApplicationId;
	public LookupItemDTO roomType;
	public String hostelAdmissionId;
	public List<LookupItemDTO> hostelListByGender;
	public boolean isCanceled;
	public String cancelReason;
	public StudentDTO student;
	public String hostelApplicationNo;
	public String printTemplate;
	public String parentAddress;
	public String guardianAddress;
	public String status;
	public String religion;
	public String prefernceForRoomStyle;
	public String id;
	
	
    private StudentApplnEntriesDTO studentApplnEntriesDTO;
	private SelectDTO students;
	private boolean isOffline;
	private String modeOfApplication;
	private String admissionStatus;
	private HostelDisciplinaryActionsTypeDTO hostelDisciplinaryActionsTypeDTO;
	private String applicationPrefix;
	private SelectDTO workFlowStatus;
	private List<HostelApplicationRoomTypePreferenceDTO> hostelApplicationRoomTypePreferenceDTO;
	private String applicationNo;
	private String hostelRoomType;
	private SelectDTO hostelStatus;
	private SelectDTO selectedRoomType;
	private SelectDTO allotedRoomType;
	private String remarks;
	private String fileName;
	private String extension;
	private boolean isPublished;
	private LocalDate feePaymentEndDate;
	private boolean isChecked;

}
