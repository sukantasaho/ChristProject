package com.christ.erp.services.dto.admission.admissionprocess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessDTO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SelectionProcessRescheduleRequestDTO {

    private String applicantName;
    private String programme;
    private String campus;
    private SelectionProcessDTO selectionProcessDTO;
    private String erpCampusProgrammeMappingId;
    
    //
    private int id;
    private boolean okmsg = false ;
    private boolean approved ;
    private boolean rejected;
    private Integer applicationNo;
    private Integer studentEnteriesId;
    private Integer selectionProcessPlanDetailsId;
    private Integer selectionProcessType;
    private String name;
    private SelectDTO programmeName;
    private String campusName;
    private String selectionProcessSessionName;
    private LocalDate selectionProcessDate;
    private String selectionProcessVenue;
    private LocalDate selectionProcess2Date;
    private String selectionProcess2Venue;
    private Integer rescheduleCount;
    private boolean shortList;
    private SelectDTO selectionDate;
	private List<AdmSelectionProcessVenueCityDTO> venus;
	private SelectDTO processSelection1Date;
	private AdmSelectionProcessVenueCityDTO process1Venue;
	private SelectDTO processSelection2Date;
	private AdmSelectionProcessVenueCityDTO process2Venue;
	public Integer reschudeleId;
	public LocalDateTime requestReceivedDateTime;
	public LocalDate process1Date;

}
