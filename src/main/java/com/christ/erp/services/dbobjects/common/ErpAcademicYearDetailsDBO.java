package com.christ.erp.services.dbobjects.common;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_academic_year_detail")
@Getter
@Setter
public class ErpAcademicYearDetailsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_academic_year_detail_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_id")
	public ErpCampusDBO campus;
	
	@ManyToOne
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO academicYear;
	
	@Column(name = "is_academic_year_current")
	public Boolean isAcademicYearCurrent;
	
	@Column(name = "academic_year_start_date")
	public LocalDate academicYearStartDate;
	
	@Column(name = "academic_year_end_date")
	public LocalDate academicYearEndDate;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
}