package com.christ.erp.services.dbobjects.employee.appraisal;

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
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

@Entity
@Table(name = "emp_appraisal_session")

public class EmpAppraisalSessionDBO implements Serializable {
	
	private static final long serialVersionUID = 1014345467771532631L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appraisal_session_id")
	public Integer id;	
	
	@Column(name="appraisal_session_name")
	public String appraisalSessionName;
	
	@Column(name="session_type")
	public String sessionType;
	
	@ManyToOne
	@JoinColumn(name="erp_academic_year_id") 
	public ErpAcademicYearDBO erpAcademicYearDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_location_id")
    public ErpLocationDBO erpLocationDBO;
    
    @Column(name="appraisal_session_year")
    public int appraisalSessionYear;
    
    @Column(name="appraisal_session_month")
    public int appraisalSessionMonth;
    
    @ManyToOne
    @JoinColumn(name="emp_employee_category_id")
    public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
    
	@Column(name="created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus; 

}
