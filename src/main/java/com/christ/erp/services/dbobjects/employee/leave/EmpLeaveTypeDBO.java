package com.christ.erp.services.dbobjects.employee.leave;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_leave_type")
@Getter
@Setter
public class EmpLeaveTypeDBO implements Serializable {

	private static final long serialVersionUID = 7421029991882333015L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_leave_type_id")
	public int id;

	@NotEmpty(message = "leave type name is not empty")
	@Column(name = "leave_type_name")
	public String leaveTypeName;

	@NotEmpty(message = "leave type code is not empty")
	@Column(name = "leave_type_code")
	public String leaveTypeCode;

	@Column(name = "leave_type_color_code_hexvalue")
	public String leaveTypeColorCodeHexvalue;

	@Column(name = "is_apply_online")
	public Boolean isApplyOnline;

	@Column(name = "is_partial_allowed")
	public Boolean partialDaysAllowed;

//	@Column(name = "is_continous_days")
//	public Boolean continousDays;

	@Column(name = "is_leave_exemption")
	public Boolean isExemption;

	@Column(name = "is_leave_type_document_required")
	public Boolean supportingDoc;

	@Column(name = "is_leave")
	public Boolean isLeave;

	@Column(name = "is_auto_approve_leave")
	public Boolean autoApproveLeave;

	@Column(name = "auto_approve_days")
	public Integer autoApprovedDays;

	@Column(name = "max_online_leave_in_month")
	public Integer maxOnlineLeaveInMonth;

	@Column(name = "is_leave_advance")
	public Boolean isLeaveAdvance;

	@Column(name = "leave_policy", columnDefinition = "mediumtext")
	public String leavePolicy;

	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;

	@Column(name = "max_online_leave_permitted_in_month")
	public Integer maxOnlineLeavePermittedInMonth;

	@Column(name = "max_online_leave_with_proof")
	public Integer maxOnlineLeaveWithProof;

	@Column(name = "is_sunday_counted")
	public Boolean isSundayCounted;

	@Column(name = "is_holiday_counted")
	public Boolean isHolidayCounted;
}
