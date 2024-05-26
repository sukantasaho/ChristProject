package com.christ.erp.services.dbobjects.support.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter

@Table(name = "support_category_campus_details_employee")
public class SupportCategoryCampusDetailsEmployeeDBO implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "support_category_campus_details_employee_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "support_category_campus_details_id")
	private SupportCategoryCampusDetailsDBO supportCategoryCampusDetailsDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	private EmpDBO empDBO;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
	
}
