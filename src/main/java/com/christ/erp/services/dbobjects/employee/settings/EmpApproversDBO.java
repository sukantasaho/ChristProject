package com.christ.erp.services.dbobjects.employee.settings;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_approvers")
@Setter
@Getter
public class EmpApproversDBO implements Serializable {

private static final long serialVersionUID = 7180652615540088904L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_approvers_id")
	public int id;
	
	//@OneToOne(cascade = CascadeType.ALL)
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	@ManyToOne
	@JoinColumn(name = "leave_approver_id")
	public EmpDBO leaveApproverId;
	
	@ManyToOne
	@JoinColumn(name = "leave_authoriser_id")
	public EmpDBO leaveAuthorizerId;
	
	@ManyToOne
	@JoinColumn(name = "level_one_appraiser_id")
	public EmpDBO levelOneAppraiserId;
	
	@ManyToOne
	@JoinColumn(name = "level_two_appraiser_id")
	public EmpDBO levelTwoAppraiserId;
	
	@ManyToOne
	@JoinColumn(name = "work_diary_approver_id")
	public EmpDBO workDairyApproverId;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "record_Status")
	public char recordStatus;
	
	@OneToMany(mappedBy = "empApproversId",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<EmpApproversDetailsDBO> empApproversDetailsDBOSet;
	
}