package com.christ.erp.services.dto.employee.common;

import java.time.LocalDate;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpDTO {
	
	public String empId;
	public String applnEntriesId;
	public String empName;
	public String empNo;
	public String campusId;
	public String campusName;
	public String departmentName;
	public String jobCategoryName;
	public String departmentId;
	public String jobCategoryId;
	public String employeeCategoryId;
	public String empDesignation;
	public String empUniversityMail;
	public String deputationStartDate;
	public String employeePhoto;
	public boolean isActive = true;
	public EmployeeApplicationDTO employeeCategory;
	public EmployeeApplicationDTO jobCategory;
	public LookupItemDTO employeeGroup;
	public LookupItemDTO employeeCampus;
	public LookupItemDTO employeeDepartment;
	public LookupItemDTO employeeDesignation;
	public LookupItemDTO employeeDeputedDepartmentTitle;
	public LookupItemDTO employeeDeputedDepartment;
	public LookupItemDTO employeeTitle;
	public LookupItemDTO employeeDesignationForStaffAlbum;
	public LookupItemDTO nationality;
	public LookupItemDTO maritalStatus;
	public String value;
    public String label;
    private int empPersonalDataId;
    private SelectDTO erpGender;
    private LocalDate empDOB;
    private String countryCode;
    private String empMobile;
    private LocalDate empDOJ;
    private Character recordStatus;
    private String empPersonalEmail;
    private SelectDTO  subjectCategory;
    private SelectDTO subjectCategorySpecializationName;
    private SelectDTO  empBlock;
    private SelectDTO empFloor;
    private SelectDTO empRoom;
    private Integer  cabinNo;
    private Integer telephoneExtension;
    private String telephoneNumber;
    private SelectDTO empTimeZone;
    private String profilePhotoUrl;
    private String fileNameOriginal;
    private String processCode;
    
    public EmpDTO(int id, Integer empPersonalDataId,Integer deputationDepartmentId 
	    		,String empNumber,String empName,String genderName,LocalDate empDOB,String countryCode,String empMobile,LocalDate empDOJ
	    		,Character recordStatus,String empPersonalEmail,String empUniversityEmail
	    		,String employeeCategoryName, String employeeJobName,String subjectCategory, String subjectCategorySpecializationName
	    		,String employeeGroupName,String empDesignationName,String empDesignationStaffAlbumName, String titleName, String departmentName, String campusName
	    	    ,String DeputedTitleName
	    	    ,String blockName
	    	    ,String floorName,String roomName
	    	    ,Integer cabinNo,Integer telephoneExtension,String telephoneNumber
	    	    ,LocalDate depautationStartDate,Integer applnEntriesId,String timeZoneName) {

    	this.empId = String.valueOf(id);
    	if(!Utils.isNullOrEmpty(empPersonalDataId)) {
    		this.empPersonalDataId = empPersonalDataId;
    	}
    	this.employeeDeputedDepartment = new LookupItemDTO();
    	if(!Utils.isNullOrEmpty(deputationDepartmentId)) {
    		this.employeeDeputedDepartment.setValue(String.valueOf(deputationDepartmentId));
    	}
    	this.empNo = empNumber;
    	this.empName = empName;
    	this.erpGender = new SelectDTO();
    	this.erpGender.setLabel(genderName);
    	this.empDOB = empDOB;
    	this.countryCode = countryCode;
    	this.empMobile = empMobile;
    	this.empDOJ = empDOJ;
    	this.recordStatus = recordStatus;
    	this.empPersonalEmail = empPersonalEmail;
    	this.empUniversityMail = empUniversityEmail;
    	this.employeeCategory = new EmployeeApplicationDTO();
    	this.employeeCategory.label = employeeCategoryName;
    	this.jobCategory = new EmployeeApplicationDTO();
    	this.jobCategory.label = employeeJobName;
    	this.subjectCategory = new SelectDTO();
    	this.subjectCategory.setLabel(subjectCategory);
    	this.subjectCategorySpecializationName = new SelectDTO();
    	this.subjectCategorySpecializationName.setLabel(subjectCategorySpecializationName);
    	this.employeeGroup = new LookupItemDTO();
    	this.employeeGroup.setLabel(employeeGroupName);
    	this.employeeDesignation = new LookupItemDTO();
    	this.employeeDesignation.setLabel(empDesignationName);
    	this.employeeDesignationForStaffAlbum = new LookupItemDTO();
    	this.employeeDesignationForStaffAlbum.setLabel(empDesignationStaffAlbumName);
    	this.employeeTitle = new LookupItemDTO();
    	this.employeeTitle.setLabel(titleName);
    	this.employeeDepartment = new LookupItemDTO();
    	this.employeeDepartment.setLabel(departmentName);
    	this.employeeCampus = new LookupItemDTO();
    	this.employeeCampus.setLabel(campusName);
    	
//    	this.employeeDeputedDepartment = new LookupItemDTO();
//    	this.employeeDeputedDepartment.setValue(String.valueOf(employeeDeputedDepartmentId));
    	
    	this.employeeDeputedDepartmentTitle = new LookupItemDTO();
    	this.employeeDeputedDepartmentTitle.setLabel(DeputedTitleName);
    	this.empBlock = new SelectDTO();
    	this.empBlock.setLabel(blockName);
    	this.empFloor = new SelectDTO();
    	this.empFloor.setLabel(floorName);
    	this.empRoom = new SelectDTO();
    	this.empRoom.setLabel(roomName);
    	this.cabinNo = cabinNo;
    	this.telephoneExtension = telephoneExtension;
    	this.telephoneNumber = telephoneNumber;
    	this.deputationStartDate = !Utils.isNullOrEmpty(depautationStartDate)? Utils.convertLocalDateToStringDate(depautationStartDate):"";
    	this.applnEntriesId = !Utils.isNullOrEmpty(applnEntriesId)? String.valueOf(applnEntriesId):"";
    	this.empTimeZone = new SelectDTO();
    	this.empTimeZone.setLabel(timeZoneName);

    }
  
    public EmpDTO(int id, String empName, String empNo, String empUniversityMail, String campusName, String departmentName, String empDesignation, 
    		String profilePhotoUrl, String fileNameOriginal, String processCode) {
    	this.empId = String.valueOf(id);
    	this.empName = empName;
    	this.empNo = empNo;
    	this.empUniversityMail = empUniversityMail;
    	this.campusName = campusName;    	
    	this.departmentName = departmentName;
    	this.empDesignation = empDesignation;
    	this.profilePhotoUrl = profilePhotoUrl;
    	this.fileNameOriginal = fileNameOriginal;
    	this.processCode = processCode;
    }
    
}
