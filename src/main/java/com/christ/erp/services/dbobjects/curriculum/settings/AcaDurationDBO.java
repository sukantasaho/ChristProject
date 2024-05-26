package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
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

import com.christ.erp.services.dbobjects.common.AcaSessionGroupDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "aca_duration")
@Getter
@Setter
public class AcaDurationDBO implements Serializable {
	private static final long serialVersionUID = 8970206728073797144L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="aca_duration_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name= "erp_academic_year_id")
	private ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name= "aca_session_group_id")
	private AcaSessionGroupDBO acaSessionGroupDBO;
	
	@Column(name = "duration_name")
	private String durationName;
	
	@Column(name= "is_current_session")
	private Boolean isCurrentSession;
	
	@Column(name = "academic_duration_start_date")
	private LocalDate academicDurationStartDate;

	@Column(name = "academic_duration_end_date")
	private LocalDate academicDurationEndDate;

	@Column(name = "open_elective_start_date")
	private LocalDate openElectiveStartDate;

	@Column(name = "open_elective_end_date")
	private LocalDate openElectiveEndDate;

	@Column(name = "open_elective_registration_start_date")
	private LocalDate openElectiveRegistrationStartDate;

	@Column(name = "open_elective_registration_end_date")
	private LocalDate openElectiveRegistrationEndDate;

	@Column(name = "course_plan_start_date")
	private LocalDate coursePlanStartDate;

	@Column(name = "course_plan_end_date")
	private LocalDate coursePlanEndDate;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	  
	@Column( name = "record_status")
    private char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "acaDurationDBO", cascade = CascadeType.ALL)
	private Set<AcaDurationDetailDBO> acaDurationDetailDBOSet;
}
