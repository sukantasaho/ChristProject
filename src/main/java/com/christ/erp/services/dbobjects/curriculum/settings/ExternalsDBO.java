package com.christ.erp.services.dbobjects.curriculum.settings;


import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.curriculum.common.ErpExternalsCategoryDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "externals")
@Getter 
@Setter
public class ExternalsDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "externals_id")
	private int id;

	@Column(name = "external_name")
	private String externalName;

	@Column(name = "contact_no")
	private String contactNo;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "dob")
	private LocalDate dob;
	
	@Column(name = "admitted_year")
	private Integer admittedYear;

	@Column(name = "created_users_id", updatable=false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
	@ManyToOne
	@JoinColumn(name= "student_id")
	private StudentDBO studentDBO;

	@ManyToOne
	@JoinColumn(name = "erp_department_id")
	private ErpDepartmentDBO erpDepartmentDBO;

	@ManyToOne
	@JoinColumn(name = "erp_externals_category_id")
	private ErpExternalsCategoryDBO erpExternalsCategoryDBO;
	
	@OneToOne(mappedBy = "externalsDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL )
	private ExternalsAdditionalDetailsDBO externalsAdditionalDetailsDBO;
}
