package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name ="aca_student_sessionwise ")
@Getter
@Setter

public class AcaStudentSessionwiseDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_student_sessionwise_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name ="aca_student_yearwise_id")
	private AcaStudentYearwiseDBO acaStudentYearwiseDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_duration_detail_id")
	private AcaDurationDetailDBO acaDurationDetailDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_class_id")
	private AcaClassDBO acaClassDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_virtual_class_id")
    private AcaClassDBO acaVirtualClassDBO;
	
	@Column (name = "is_current")
	private Boolean isCurrent;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

}
