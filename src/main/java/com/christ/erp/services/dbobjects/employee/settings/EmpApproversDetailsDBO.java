package com.christ.erp.services.dbobjects.employee.settings;

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

@Entity
@Table(name = "emp_approvers_details")
@Getter
@Setter
public class EmpApproversDetailsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_approvers_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "emp_approvers_id")
	private EmpApproversDBO empApproversId;
	
	@Column(name = "approval_type")
	private String approvalType;
	
	@ManyToOne
	@JoinColumn(name = "leave_approver_id")
	private EmpDBO leaveApproverId;
	
	@ManyToOne
	@JoinColumn(name = "leave_authoriser_id")
	private EmpDBO leaveAuthorizerId;
	
	@ManyToOne
	@JoinColumn(name = "level_one_appraiser_id")
	private EmpDBO levelOneAppraiserId;
	
	@ManyToOne
	@JoinColumn(name = "level_two_appraiser_id")
	private EmpDBO levelTwoAppraiserId;
	
	@ManyToOne
	@JoinColumn(name = "work_diary_approver_id")
	private EmpDBO workDairyApproverId;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_Status")
	private char recordStatus;
	
}
