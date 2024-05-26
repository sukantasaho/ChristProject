package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import java.util.List;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.NestedSelectDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EmployeeApplicantDTO {

	public int empApplnEntriesId;
	public String saveMode;
	public Integer applicationNo;
	public String empApplicationRegistrationId;
	public String isAvailableForTheInterview;
	public String empApplnInterviewSchedulesId;
	public String interviewRound;
	public String isAcceptOffer;
	public String reportingDate;
	public String joiningDate;
	public JobDetailsDTO jobDetailDTO;
	public EmpApplnPersonalDataDTO empApplnPersonalDataDTO;
	public EmpApplnPersonalDataDTO addressDetailDTO;
	public EducationalDetailsDTO educationalDetailDTO;
	public ProfessionalExperienceDTO professionalExperienceDTO;
	public ResearchDetailsDTO researchDetailDTO;
	public EmpAddtnlPersonalDataDTO additionalPersonalDataDTO;
	public EmpJobDetailsDTO empJobDetailsDTO;
//	public List<EmpPfGratuityNomineesDTO> empPfGratuityNomineesDTO;
	public EmpApplnNonAvailabilityDTO empApplnNonAvailabilityDTO;
//	public List<FamilyDependentDTO> familyDependentDTO;
	private SelectDTO jobCategoryDTO;
	private List<EmpPfGratuityNomineesDTO> empGratuityNomineesDTO;
	private List<EmpPfGratuityNomineesDTO> empPfNomineesDTO;
	private FileUploadDownloadDTO OfferLetterfileUploadDownloadDTO;
	private FileUploadDownloadDTO regretLetterfileUploadDownloadDTO;
	private List<EmpFamilyDetailsAddtnlDTO> familyDetailsAddtnlList;
	private List<EmpFamilyDetailsAddtnlDTO> dependentDetailsAddtnlList;
	private EmpApplnAddtnlInfoEntriesDTO additionalInformations;
	private String empApplnSubjectCategory;
	private String empApplnSubjectCategorySpecialization;
	private String locationPref;
	private boolean academic;
	private SelectDTO maritalStatusDTO;
	private LocalDate submissionDate;

}
