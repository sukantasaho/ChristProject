package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpRoomEmpMappingDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.*;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDBO;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "emp")
@Setter
@Getter
public class EmpDBO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_id")
	public Integer id;
	
	@Column(name="emp_name")
	public String empName;
	
	@Column(name="emp_no")
	public String empNumber;

	@ManyToOne
	@JoinColumn(name = "erp_gender_id")
	public ErpGenderDBO erpGenderDBO;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@Column(name="dob")
	public LocalDate empDOB;
	
	@Column(name="doj")
	public LocalDate empDOJ;
	
	@Column(name="deputation_start_date")
	public LocalDate depautationStartDate;
	
	@Column(name="mobile_no")
	public String empMobile;
	
	@Column(name="moble_no_country_code")
	public String countryCode;
	
	@Column(name="emp_university_email")
	public String empUniversityEmail;
	
	@Column(name="emp_personal_email")
	public String empPersonalEmail;
	
	@ManyToOne
	@JoinColumn(name = "erp_campus_department_mapping_id")
	public ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_employee_category_id")
	public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_employee_job_category_id")
	public EmpEmployeeJobCategoryDBO empEmployeeJobCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_designation_id")
	public EmpDesignationDBO empDesignationDBO; 
	
	@ManyToOne
	@JoinColumn(name = "emp_album_designation_id")
	public EmpDesignationDBO empAlbumDesignationDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_time_zone_id")
	public EmpTimeZoneDBO empTimeZoneDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_title_id")
	public ErpEmployeeTitleDBO erpEmployeeTitleDBO;
	
	@ManyToOne
	@JoinColumn(name = "deputation_department_title_id")
	public EmpTitleDBO deputationDepartmentTitleDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deputation_erp_campus_department_mapping_id")
	public ErpCampusDepartmentMappingDBO deputationErpCampusDepartmentMappingDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_group_id")
	public EmpEmployeeGroupDBO  empEmployeeGroupDBO; 
	
	@OneToOne(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JoinColumn(name="emp_personal_data_id")
    public EmpPersonalDataDBO empPersonalDataDBO;
	
	@OneToOne(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JoinColumn(name="emp_job_details_id")
    public EmpJobDetailsDBO empJobDetailsDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_subject_category_id")
	public EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO;
	
	@Column(name = "emp_signature_url")
	public String empSignatureUrl;
	
	@OneToOne(cascade = CascadeType.ALL)
 	@JoinColumn(name = "emp_signature_url_id")
 	public UrlAccessLinkDBO empSignatureUrlDBO;
	
//	@Column(name = "is_punching_excemption")
//	public Boolean isPunchingExcemption;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_subject_category_specialization_id")
	public EmpApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO;
    
	@OneToOne(mappedBy = "empDBO", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public ErpRoomEmpMappingDBO erpRoomEmpMappingDBO;
	
	@OneToMany(mappedBy = "empDBO", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public Set<EmpPayScaleDetailsDBO> empPayScaleDetailsDBOSet;
	
	@OneToOne(mappedBy = "empDBO", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public EmpApproversDBO empApproversDBO;
	
	@OneToOne(mappedBy = "empDBO", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public EmpResignationDBO empresignationDBO;
    
    @OneToMany(mappedBy ="empDBO",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	public Set<EmpLeaveAllocationDBO> empLeaveAllocationDBOSet = new HashSet<>();
    
    @OneToMany(mappedBy ="empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpRemarksDetailsDBO> empRemarksDetailsDBOSet = new HashSet<>() ;
    
    @OneToMany(mappedBy ="empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpGuestContractDetailsDBO> empGuestContractDetailsDBOSet = new HashSet<>() ;
    
    @OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpEducationalDetailsDBO> empEducationalDetailsDBOSet = new HashSet<>();
    
    @OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpEligibilityTestDBO> empEligibilityTestDBOSet = new HashSet<>();
    
    @OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   	public Set<EmpMajorAchievementsDBO> empMajorAchievementsDBOSet = new HashSet<>();
    
    @OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   	public Set<EmpEmployeeLetterDetailsDBO> empEmployeeLetterDetailsDBOSet = new HashSet<>();
    
    @OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   	public Set<EmpWorkExperienceDBO> empWorkExperienceDBOSet = new HashSet<>();
    
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;
	
	@OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<EmpLevelsAndPromotionsDBO> empLevelsAndPromotionsDBOSet;

	@OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<EmpDepartmentDesignationHistoryDBO> empDepartmentDesignationHistoryDBOSet;

	@OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<EmpEmployeeLetterDetailsDBO> EmpEmployeeLetterDetailsDBOsSet;



//	@OneToMany(mappedBy = "empDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	private Set<EmpResignationDBO> empResignationDBOSet;


}
