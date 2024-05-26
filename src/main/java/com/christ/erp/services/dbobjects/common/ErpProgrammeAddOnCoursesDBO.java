package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_programme_add_on_courses")
@Getter
@Setter
public class ErpProgrammeAddOnCoursesDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_programme_add_on_courses_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "erp_programme_batchwise_settings_id")
	private ErpProgrammeBatchwiseSettingsDBO erpProgrammeBatchwiseSettingsDBO;

	@ManyToOne
	@JoinColumn(name = "erp_course_category_id")
	private ErpCourseCategoryDBO erpCourseCategoryDBO;

	@Column(name = "min_no_of_courses")
	private Integer minNoOfCourses;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;

}
