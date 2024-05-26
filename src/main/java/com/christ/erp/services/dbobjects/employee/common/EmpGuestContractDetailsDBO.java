package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;

import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "emp_guest_contract_details")
@Getter
@Setter
public class EmpGuestContractDetailsDBO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_guest_contract_details_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	public EmpDBO empDBO;
	
	@Column(name="guest_subject_specilization")
	public String guestSubjectSpecialization;
	
	@Column(name="guest_tutoring_semester")
	public String guestTutoringSemester;
	
	@Column(name="guest_reffered_by")
	public String guestReferredBy;
	
	@Column(name="guest_working_hours_week")
	public BigDecimal guestWorkingHoursWeek;
	
	@Column(name="contract_emp_start_date")
	public LocalDate contractEmpStartDate;
	
	@Column(name="contract_emp_end_date")
	public LocalDate contractEmpEndDate;
	
	@Column(name="contract_emp_letter_no")
	public String contractEmpLetterNo;
	
	@Column(name="contract_emp_document_url")
	public String contractEmpDocumentUrl;
	
	@Column(name="guest_contract_remarks")
	public String guestContractRemarks;
	
	@ManyToOne//(cascade = CascadeType.ALL)
	@JoinColumn(name = "emp_pay_scale_details_id")
	public EmpPayScaleDetailsDBO empPayScaleDetailsDBO;
	
	@Column(name="is_current")
	public Boolean isCurrent;
	
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;	
	
	@ManyToOne
	@JoinColumn(name = "erp_campus_department_mapping_id")
	private ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "contract_emp_document_url_id")
	public UrlAccessLinkDBO contractEmpDocumentUrlDBO;

	@Column(name="pay_scale_type")
	public String payScaleType;

	@Column(name="pay_amount")
	public BigDecimal payAmount;

}
