package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name =" aca_course_sessionwise")
@Getter
@Setter

public class AcaCourseSessionwiseDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aca_course_sessionwise_id")
    private int id;
	
	@ManyToOne
	@JoinColumn(name =" aca_course_yearwise_id")
	private AcaCourseYearwiseDBO acaCourseYearwiseDBO;
	
	@ManyToOne
	@JoinColumn(name = "aca_duration_detail_id")
	private AcaDurationDetailDBO acaDurationDetailDBO;
	
	@ManyToOne
	@JoinColumn(name =" aca_duration_id")
	private AcaDurationDBO acaDurationDBO;
	
	@ManyToOne
	@JoinColumn(name =" aca_session_id ")
	private AcaSessionDBO acaSessionDBO;
	
	@ManyToOne
	@JoinColumn(name = "offered_programme_id")
	private ErpProgrammeDBO erpProgrammeDBO;
	
	@ManyToOne
	@JoinColumn(name = "course_category_id")
	private ErpCourseCategoryDBO erpCourseCategoryDBO;
	
	@Column(name = "is_having_specialization")
	private Boolean isHavingSpecialization;
	
	@Column(name="created_users_id", updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
