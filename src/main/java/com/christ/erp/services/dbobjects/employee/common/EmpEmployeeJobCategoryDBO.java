package com.christ.erp.services.dbobjects.employee.common;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "emp_employee_job_category")
@Setter
@Getter
public class EmpEmployeeJobCategoryDBO implements Serializable{

	private static final long serialVersionUID = -8598013720920491758L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_employee_job_category_id")
	public Integer id;
	
	@Column(name = "employee_job_name")
    public String employeeJobName;

	@Column(name = "job_category_code")
	public String jobCategoryCode;

	@ManyToOne
	@JoinColumn(name="emp_employee_category_id")
	public EmpEmployeeCategoryDBO empEmployeeCategoryId;
	
	@Column(name = "is_show_in_appln")
    public Boolean isShowInAppln;
	
	@Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;
    
    @Column(name="record_Status")
	public char recordStatus;
}
