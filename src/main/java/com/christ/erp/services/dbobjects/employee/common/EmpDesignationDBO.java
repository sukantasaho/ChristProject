package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;

import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Setter
@Getter
@Entity
@Table(name = "emp_designation")
public class EmpDesignationDBO implements Serializable{

	private static final long serialVersionUID = 1528038023145989665L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_designation_id")
    public int id;

	@Column(name="emp_designation_name")
    public String empDesignationName;
	
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
