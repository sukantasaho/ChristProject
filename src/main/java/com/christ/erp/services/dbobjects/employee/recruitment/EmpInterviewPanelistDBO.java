package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;


@Entity
@Table(name = "emp_interview_panelist")

public class EmpInterviewPanelistDBO implements Serializable {
	
	private static final long serialVersionUID = 7997747508470249186L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_interview_panelist_id")
	public Integer id;		
	
	@ManyToOne
	@JoinColumn(name="internal_erp_users_id")
	public ErpUsersDBO internalErpUsersDBO;
	
	@ManyToOne
	@JoinColumn(name="external_erp_users_id")
	public ErpUsersDBO externalErpUsersDBO;	
	
	@ManyToOne
	@JoinColumn(name="emp_interview_university_externals_id")
	public EmpInterviewUniversityExternalsDBO empInterviewUniversityExternalsDBO;	
	
		
	@Column(name="created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus; 		
	
    @ManyToOne
	@JoinColumn(name="erp_academic_year_id") 
	public ErpAcademicYearDBO erpAcademicYearDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_location_id")
    public ErpLocationDBO erpLocationDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_department_id")
    public ErpDepartmentDBO erpDepartmentDBO;
}
