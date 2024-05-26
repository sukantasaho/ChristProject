package com.christ.erp.services.dbobjects.student.common;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ModeOfStudy;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpSpecializationDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="student")
public class StudentDBO implements Serializable {
	private static final long serialVersionUID = 4069624329758578770L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="student_id")
	public int id;	
	
	@Column(name="register_no")
	public String  registerNo;
	
	@Column(name="student_name")
	public String studentName;
	
	@Column(name="erp_current_status_time")
	public LocalDateTime erpCurrentStatusTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_gender_id")
	public ErpGenderDBO erpGenderDBO;
	
	@Column(name="student_dob")
	public LocalDate studentDob;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="aca_batch_id")
	public AcaBatchDBO acaBatchDBO;
	
	@Column(name="student_university_email_id")
	public String studentUniversityEmailId;
	
	@Column(name="student_personal_email_id")
	public String studentPersonalEmailId;
	
	@Column(name="student_mobile_no_country_code")
	public String studentMobileNoCountryCode;
	
	@Column(name="student_mobile_no")
	public String studentMobileNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_appln_entries_id")
	public StudentApplnEntriesDBO studentApplnEntriesDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_personal_data_id")
	public StudentPersonalDataDBO studentPersonalDataId;	
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="erp_status_id")
	public ErpStatusDBO erpStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="aca_class_id")
	public AcaClassDBO acaClassDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="aca_virtual_class_id")
	public AcaClassDBO acaVirtualClassDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="admitted_year_id")
	public ErpAcademicYearDBO admittedYearId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="current_academic_year_id")
	public ErpAcademicYearDBO currentAcademicYear;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_campus_programme_mapping_id")
	public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_admission_category_id")
	public ErpAdmissionCategoryDBO erpAdmissionCategoryDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_specialization_id")
	public ErpSpecializationDBO erpSpecializationDBO;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "mode_of_study",columnDefinition="ENUM('PART_TIME','FULL_TIME','AIDED','UNAIDED'")
	private ModeOfStudy modeOfStudy;

	
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studentDBO", cascade = CascadeType.ALL)
    public Set<AccFeeDemandDBO> accFeeDemandDBOSet;
    

}
