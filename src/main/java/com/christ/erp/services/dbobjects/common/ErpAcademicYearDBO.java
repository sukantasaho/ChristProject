package com.christ.erp.services.dbobjects.common;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "erp_academic_year")
@Getter
@Setter
public class ErpAcademicYearDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_academic_year_id")
	public Integer id;
	
	@Column(name="academic_year")
	public Integer academicYear;
	
	@Column(name="academic_year_name")
	public String academicYearName;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "is_current_academic_year")
	public Boolean isCurrentAcademicYear;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@OneToMany(mappedBy ="academicYear",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<ErpAcademicYearDetailsDBO> academicYearDetails;

	@Column(name = "is_current_admission_year")
	private Boolean isCurrentAdmissionYear;
}