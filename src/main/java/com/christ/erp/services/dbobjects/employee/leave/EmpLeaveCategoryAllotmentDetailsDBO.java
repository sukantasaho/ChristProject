package com.christ.erp.services.dbobjects.employee.leave;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "emp_leave_category_allotment_details")
public class EmpLeaveCategoryAllotmentDetailsDBO implements Serializable{

	private static final long serialVersionUID = 4821923181588138851L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_leave_category_allotment_details_id")
    public int id;
	
	@ManyToOne
	@JoinColumn(name="emp_leave_category_allotment_id")
	public EmpLeaveCategoryAllotmentDBO empLeaveCategoryAllotmentDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_leave_type_id")
	public EmpLeaveTypeDBO empLeaveTypeDBO;
		
	@Column(name="is_applicable")
	public Boolean isApplicable;
	
	@Column(name="allotted_leaves")
	public Integer allottedLeaves;
	
	@Column(name="accumulated_leave")
	public Integer accumulatedLeave;
	
	@ManyToOne
	@JoinColumn(name="add_to_leave_type")
	public EmpLeaveTypeDBO addToLeaveType;
	
	@Column(name="display_order")
	public Integer displayOrder;
	
	@Column(name="is_initialization_required")
	public Boolean isInitializationRequired;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
