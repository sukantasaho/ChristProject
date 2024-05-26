package com.christ.erp.services.dto.student.common;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentApplicationEditDTO {

    private int studentApplnEntriesId;
    private Integer applicationNo;
	private String studentName;
	private String photoUrl;
	private SelectDTO programApplied;
	private StudentBasicProfileDTO basicProfileDTO;
	private StudentPersonalDataAddtnlDTO personalData;
    private StudentPersonalDataAddressDTO studentAddressAndParentdetails;
	private List<StudentEducationalDetailsDTO> studentEducationalDetailsDTOList;
    private Boolean isHavingWorkExperience;
    private List<StudentWorkExperienceDTO> studentWorkExperienceDTOList;
    private List<StudentApplnSelectionProcessDatesDTO> StudentApplnSelectionProcessDatesDTOList;
    private SelectDTO admissionCategory;
    private String modeOfStudy;
    private SelectDTO appliedCampus;
}
