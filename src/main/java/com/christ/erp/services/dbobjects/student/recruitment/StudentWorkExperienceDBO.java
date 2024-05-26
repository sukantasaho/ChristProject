package com.christ.erp.services.dbobjects.student.recruitment;

import java.time.LocalDate;
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
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpOccupationDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="student_work_experience")
@Setter
@Getter
public class StudentWorkExperienceDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_work_experience_id")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_appln_entries_id")
	private StudentApplnEntriesDBO studentApplnEntriesDBO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_id")
	private StudentDBO studentDBO;

	@Column(name = "organization_name")
	private String organizationName;

	@Column(name = "organization_address")
	private String organizationAddress;

	@Column(name = "designation")
	private String designation;

	@Column(name="work_experience_from_date")
	private LocalDate workExperienceFromDate;

	@Column(name="work_experience_to_date")
	private LocalDate workExperienceToDate;

	@Column(name = "work_experience_years")
	private Integer workExperienceYears;

	@Column(name = "work_experience_month")
	private Integer workExperienceMonth;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_occupation_id")
	private ErpOccupationDBO erpOccupationDBO;

	@Column(name = "occupation_others")
	public String occupationOthers;


	@Column(name="created_users_id", updatable=false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
    @OneToMany(mappedBy = "studentWorkExperienceDBO",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private Set<StudentWorkExperienceDocumentDBO> studentWorkExperienceDocumentDBOSet;

}
