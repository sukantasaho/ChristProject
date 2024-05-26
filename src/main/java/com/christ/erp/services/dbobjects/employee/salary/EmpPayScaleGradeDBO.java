package com.christ.erp.services.dbobjects.employee.salary;

import java.io.Serializable;

import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "emp_pay_scale_grade")
@Setter
@Getter
public class EmpPayScaleGradeDBO implements Serializable{

	private static final long serialVersionUID = -7406774630912263872L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pay_scale_grade_id")
    public int id;

	@Column(name="grade_name")
    public String gradeName;
	
	@ManyToOne
    @JoinColumn(name="emp_employee_category_id")
    public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
