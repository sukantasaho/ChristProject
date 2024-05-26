package com.christ.erp.services.dto.employee;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import com.christ.erp.services.dto.employee.profile.EmpProfileGuestContractDetailsDTO;
import com.christ.erp.services.dto.employee.profile.EmpProfileLeaveAllotmentDTO;
import com.christ.erp.services.dto.employee.profile.EmploymentTabJobDetailsDTO;
import com.christ.erp.services.dto.employee.profile.EmploymentTabResignationDTO;
import com.christ.erp.services.helpers.employee.recruitment.EmployeeProfileHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter

public class EmpEmploymentTabDTO {
	private int id;
	private String empNo;
	private SelectDTO employeeCategory;
	private SelectDTO jobCategory;
	private SelectDTO employeeGroup;
	private SelectDTO designation;
	private SelectDTO jobTitle;
	private SelectDTO subjectOrCategory;
	private SelectDTO specialization;
	private SelectDTO deputedDepartment;
	private SelectDTO campusDeputedDepartment;
	private SelectDTO department;
	private SelectDTO campus;
	private SelectDTO location;
	private SelectDTO designationForAlbum;
	private String isActive;
	private LocalDate doj;
	private EmploymentTabResignationDTO resignationDTO;
	private SelectDTO specializationForLetters;//pending
	private Integer extensionNumber;
	private String officialMailId;
	private SelectDTO block;
	private SelectDTO floor;
	private SelectDTO room;
	private List<EmpEmploymentHistoryDTO> employmentHistoryDTOList;
	private SelectDTO leaveApprover;
	private SelectDTO leaveAuthorizer;
	private SelectDTO levelOneAppraiser;
	private SelectDTO leveltwoAppraiser;
	private SelectDTO workDiaryAprover;
	private List<EmpRemarksDetailsDTO> empRemarksDetailsDTOList;
	private List<FileUploadDownloadDTO> uniqueFileNameList;
	private LocalDate departmentEffectiveDate;
	private LocalDate designationEffectiveDate;
	private LocalDate titleEffectiveDate;
	private LocalDate jobCategoryEffectiveDate;
	private List<EmpLetterDetailsDTO> letterDetailsDTOList;
	private EmploymentTabJobDetailsDTO jobDetailsDTO;
	private SelectDTO generalTimeZone;
	private Integer cabinNo;
	private List<EmpProfileGuestContractDetailsDTO> guestContractList;
	private List<EmpProfileLeaveAllotmentDTO>  empProfileLeaveAllotmentDTOList;
	private Integer leaveInitializeMonth;
	public EmpEmploymentTabDTO() {
		
	}
	
	public EmpEmploymentTabDTO(int id, String empNo, Integer employeeCategoryId, String employeeCategory, Integer jobCategoryId, String jobCategoryName, Integer employeeGroupId, String employeeGroupName,
			Integer designationId, String designationName, Integer jobTitleId, String jobTitlename, Integer subjectId, String subjectName, Integer deputedDepartmentId, String deputedDepartment,
			Integer campusDeputedDepartmentId, String campusDeputedDepartment, Integer departmentId, String departmentName, Integer campusId, String campusName, Integer locationId, String locationName,
			Integer designationForAlbumId, String designationForAlbumName, char isActive, Integer empLeaveCategoryAllotmentId, String empLeaveCategoryAllotmentName, LocalDate doj,
			LocalDate resignationDate, Boolean vacation, Boolean showInWebsite, Integer extensionNumber, String officialMailId, Integer blockId, String block, Integer floorId, 
			Integer floorNo, Integer roomId, Integer roomNo, Integer specializationId, String specializationName, Integer leaveApproverId,
			String leaveApproverName, Integer leaveAuthorizerId, String leaveAuthorizerName, Integer levelOneAppraiserId, String levelOneAppraiserName,
			Integer leveltwoAppraiserId, String leveltwoAppraiserName, Integer workDiaryAproverId, String workDiaryAproverName,	LocalDate dateOfLeaving,
			LocalDate approvalDate, LocalDate relievingOrderDate, Integer reasonForLeavingId, String reasonForLeaving, String reasonOther, Integer noticePeriodServedDays,
			Integer generalTimeZoneId, String generalTimeZoneName, Boolean isHolidayWorking, Boolean isHolidayTimeZoneApplicable, Integer holidayTimeZoneId,
			String holidayTimeZone, Boolean isRosterAllotmentApplicable, Integer cabinNo, Boolean isPunchingExempted, LocalDate retirementDate, String recommendation, Boolean isExitInterviewAttended, Integer leaveInitializeMonth) {
		this.id = id;
		this.empNo = empNo;
		if(!Utils.isNullOrEmpty(employeeCategoryId)) {
			this.employeeCategory = new SelectDTO();
			this.employeeCategory.setLabel(employeeCategory);
			this.employeeCategory.setValue(Integer.toString(employeeCategoryId));
		}
		if(!Utils.isNullOrEmpty(jobCategoryId)) {
			this.jobCategory = new SelectDTO();
			this.jobCategory.setLabel(jobCategoryName);
			this.jobCategory.setValue(Integer.toString(jobCategoryId));
		}
		if(!Utils.isNullOrEmpty(employeeGroupId)) {
			this.employeeGroup = new SelectDTO();
			this.employeeGroup.setLabel(employeeGroupName);
			this.employeeGroup.setValue(Integer.toString(employeeGroupId));
		}
		if(!Utils.isNullOrEmpty(designationId)) {
			this.designation = new SelectDTO();
			this.designation.setLabel(designationName);
			this.designation.setValue(Integer.toString(designationId));
		}
			if(!Utils.isNullOrEmpty(jobTitleId)) {
			this.jobTitle = new SelectDTO();
			this.jobTitle.setLabel(jobTitlename);
			this.jobTitle.setValue(Integer.toString(jobTitleId));
		}
		if(!Utils.isNullOrEmpty(subjectId)) {
			this.subjectOrCategory = new SelectDTO();
			this.subjectOrCategory.setLabel(subjectName);
			this.subjectOrCategory.setValue(Integer.toString(subjectId));
		}
		if(!Utils.isNullOrEmpty(deputedDepartmentId)) {
			this.deputedDepartment = new SelectDTO();
			this.deputedDepartment.setLabel(deputedDepartment);
			this.deputedDepartment.setValue(Integer.toString(deputedDepartmentId));
		}
		if(!Utils.isNullOrEmpty(campusDeputedDepartmentId)) {
			this.campusDeputedDepartment = new SelectDTO();
			this.campusDeputedDepartment.setLabel(campusDeputedDepartment);
			this.campusDeputedDepartment.setValue(Integer.toString(campusDeputedDepartmentId));
		}
		if(!Utils.isNullOrEmpty(departmentId)) {
			this.department = new SelectDTO();
			this.department.setLabel(departmentName);
			this.department.setValue(Integer.toString(departmentId));
		}
		if(!Utils.isNullOrEmpty(campusId)) {
			this.campus = new SelectDTO();
			this.campus.setLabel(campusName);
			this.campus.setValue(Integer.toString(campusId));
		}
		if(!Utils.isNullOrEmpty(locationId)) {
			this.location = new SelectDTO();
			this.location.setLabel(locationName);
			this.location.setValue(Integer.toString(locationId));
		}
		if(!Utils.isNullOrEmpty(designationForAlbumId)) {
			this.designationForAlbum = new SelectDTO();
			this.designationForAlbum.setLabel(designationForAlbumName);
			this.designationForAlbum.setValue(Integer.toString(designationForAlbumId));
		}
		this.isActive = String.valueOf(isActive);
		if(!Utils.isNullOrEmpty(specializationId)) {
			this.specialization = new SelectDTO();
			this.specialization.setLabel(specializationName);
			this.specialization.setValue(Integer.toString(specializationId));
		}
		this.doj = doj;
		this.extensionNumber = extensionNumber;
		this.officialMailId = officialMailId;
		if(!Utils.isNullOrEmpty(blockId)) {
			this.block = new SelectDTO();
			this.block.setValue(Integer.toString(blockId));
			this.block.setLabel(block);
		}
		if(!Utils.isNullOrEmpty(floorId)) {
			this.floor = new SelectDTO();
			this.floor.setValue(Integer.toString(floorId));
			if(!Utils.isNullOrEmpty(floorNo)) {
				this.floor.setLabel(Integer.toString(floorNo));
			}
		}
		if(!Utils.isNullOrEmpty(roomId)) {
			this.room = new SelectDTO();
			this.room.setValue(Integer.toString(roomId));
			if(!Utils.isNullOrEmpty(roomNo)) {
				this.room.setLabel(Integer.toString(roomNo));
			}
		}
		if(!Utils.isNullOrEmpty(leaveApproverId)) {
			this.leaveApprover = new SelectDTO();
			this.leaveApprover.setLabel(leaveApproverName);
			this.leaveApprover.setValue(Integer.toString(leaveApproverId));
		}
		if(!Utils.isNullOrEmpty(leaveAuthorizerId)) {
			this.leaveAuthorizer = new SelectDTO();
			this.leaveAuthorizer.setLabel(leaveAuthorizerName);
			this.leaveAuthorizer.setValue(Integer.toString(leaveAuthorizerId));
		}
		if(!Utils.isNullOrEmpty(levelOneAppraiserId)) {
			this.levelOneAppraiser = new SelectDTO();
			this.levelOneAppraiser.setLabel(levelOneAppraiserName);
			this.levelOneAppraiser.setValue(Integer.toString(levelOneAppraiserId));
		}
		if(!Utils.isNullOrEmpty(leveltwoAppraiserId)) {
			this.leveltwoAppraiser = new SelectDTO();
			this.leveltwoAppraiser.setLabel(leveltwoAppraiserName);
			this.leveltwoAppraiser.setValue(Integer.toString(leveltwoAppraiserId));
		}
		if(!Utils.isNullOrEmpty(workDiaryAproverId)) {
			this.workDiaryAprover = new SelectDTO();
			this.workDiaryAprover.setLabel(workDiaryAproverName);
			this.workDiaryAprover.setValue(Integer.toString(workDiaryAproverId));
		}
		EmploymentTabJobDetailsDTO employmentTabJobDetailsDTO = new EmploymentTabJobDetailsDTO();
		if(!Utils.isNullOrEmpty(empLeaveCategoryAllotmentId)) {
			SelectDTO leaveCategoryDTO = new SelectDTO();
			leaveCategoryDTO.setLabel(empLeaveCategoryAllotmentName);
			leaveCategoryDTO.setValue(Integer.toString(empLeaveCategoryAllotmentId));
			employmentTabJobDetailsDTO.setLeaveCategory(leaveCategoryDTO);
		}
		employmentTabJobDetailsDTO.setVacation(vacation);
		employmentTabJobDetailsDTO.setShowInWebsite(showInWebsite);
		employmentTabJobDetailsDTO.setIsHolidayWorking(isHolidayWorking);
		employmentTabJobDetailsDTO.setHolidayTimeZoneApplicable(isHolidayTimeZoneApplicable);
		employmentTabJobDetailsDTO.setIsRosterAllotmentApplicable(isRosterAllotmentApplicable);
		employmentTabJobDetailsDTO.setIsPunchingExempted(isPunchingExempted);
		employmentTabJobDetailsDTO.setRetirementDate(retirementDate);
		this.setJobDetailsDTO(employmentTabJobDetailsDTO);
		if (!Utils.isNullOrEmpty(holidayTimeZoneId)) {
			SelectDTO generalTimeDto = new SelectDTO();
			generalTimeDto.setLabel(holidayTimeZone);
			generalTimeDto.setValue(Integer.toString(holidayTimeZoneId));
			this.generalTimeZone = generalTimeDto;
		}
		if (!Utils.isNullOrEmpty(generalTimeZoneId)) {
			SelectDTO generalTimeDto = new SelectDTO();
			generalTimeDto.setLabel(generalTimeZoneName);
			generalTimeDto.setValue(Integer.toString(generalTimeZoneId));
			this.generalTimeZone = generalTimeDto;
		}
		EmploymentTabResignationDTO employmentTabResignationDTO = new EmploymentTabResignationDTO();
		if(!Utils.isNullOrEmpty(reasonForLeavingId)) {
			SelectDTO reasonForLeavingDTO= new SelectDTO();
			reasonForLeavingDTO.setLabel(reasonForLeaving);
			reasonForLeavingDTO.setValue(Integer.toString(reasonForLeavingId));
			employmentTabResignationDTO.setReasonForLeaving(reasonForLeavingDTO);
		}
		employmentTabResignationDTO.setResignationDate(resignationDate);
		employmentTabResignationDTO.setDateOfLeaving(dateOfLeaving);
		employmentTabResignationDTO.setApprovalDate(approvalDate);
		employmentTabResignationDTO.setRelievingOrderDate(relievingOrderDate);
		employmentTabResignationDTO.setReasonForLeavingOther(reasonOther);
		employmentTabResignationDTO.setNoticePeriodServedDays(noticePeriodServedDays);
		employmentTabResignationDTO.setRecommendation(recommendation);
		employmentTabResignationDTO.setIsExitInterviewAttended(isExitInterviewAttended);
		this.resignationDTO = employmentTabResignationDTO;
		this.cabinNo = cabinNo;
		this.leaveInitializeMonth = leaveInitializeMonth;
	}
}
