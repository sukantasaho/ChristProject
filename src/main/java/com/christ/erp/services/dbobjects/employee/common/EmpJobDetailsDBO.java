package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpPfGratuityNomineesDBO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "emp_job_details")
public class EmpJobDetailsDBO implements Serializable {

	private static final long serialVersionUID = -4709727046335441790L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_job_details_id")
	public Integer id;

	@OneToOne
	@JoinColumn(name = "emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesId;
	
	@OneToOne
	@JoinColumn(name = "emp_id")
	public EmpDBO empDBO;

//	@Column(name = "application_no")
//	public Integer applicationNo;

	@Column(name = "is_with_pf")
	public Boolean isWithPf;

	@Column(name = "is_with_gratuity")
	public Boolean isWithGratuity;

	@Column(name = "is_esi_applicable")
	public Boolean isEsiApplicable;

	@Column(name = "is_uan_no_available")
	public Boolean isUanNoAvailable;

	@Column(name = "pf_account_no")
	public String pfAccountNo;

	@Column(name = "pf_date")
	public LocalDate pfDate;

	@Column(name = "gratuity_no")
	public String gratuityNo;

	@Column(name = "gratuity_date")
	public LocalDate gratuityDate;

	@Column(name = "uan_no")
	public String uanNo;

	@Column(name = "is_sib_account_available")
	public Boolean isSibAccountAvailable;

	@Column(name = "sib_account_bank")
	public String sibAccountBank;

	@Column(name = "branch_ifsc_code")
	public String branchIfscCode;

//	@ManyToOne
//	@JoinColumn(name = "emp_pay_scale_details_id")
//	public EmpPayScaleDetailsDBO empPayScaleDetailsId;

//	@Column(name = "vc_comments")
//	public String vcComments;

	@Column(name = "reporting_date")
	public LocalDateTime reportingDate;

	@Column(name = "joining_date")
	public LocalDateTime joiningDate;
	
	@Column(name = "retirement_date")
	public LocalDate retirementDate;

	@Column(name = "is_offer_accepted")
	public Boolean isOfferAccepted;

	@Column(name = "is_wait_for_documents")
	public Boolean isWaitForDocuments;

	@Column(name = "documents_remarks")
	public String documentsRemarks;

	@Column(name = "documents_submission_due_date")
	public LocalDate documentsSubmissionDueDate;

	@Column(name = "is_display_website")
	public Boolean isDisplayWebsite;

//	@Column(name = "memo_date")
//	public LocalDate memoDate;

//	@Column(name = "memo_details")
//	public String memoDetails;

//	@Column(name = "memo_upload_url")
//	public String memoUploadUrl;

//	@Column(name = "memo_ref_no")
//	public String memoRefNo;

	@Column(name = "is_biometric_configuration_completed")
	public Boolean isbiometricConfigurationCompleted;

	@Column(name = "smart_card_no")
	public String smartCardNo;
	
	@ManyToOne
	@JoinColumn(name = "emp_guest_contract_details_id")
	public EmpGuestContractDetailsDBO empGuestContractDetailsDBO;

	@Column(name = "is_vacation_applicable")
	public Boolean isVacationApplicable;
	
	@Column(name = "recognised_exp_years")
	public Integer recognisedExpYears;
	
	@Column(name = "recognised_exp_months")
	public Integer recognisedExpMonths;
	
	@OneToOne
	@JoinColumn(name = "emp_leave_category_allotment_id")
	public EmpLeaveCategoryAllotmentDBO empLeaveCategoryAllotmentId;
	
	@ManyToOne
	@JoinColumn(name = "holiday_time_zone_id")
	public EmpTimeZoneDBO holidayTimeZoneDBO;
	
	@Column(name = "is_holiday_time_zone_applicable")
	public Boolean isHolidayTimeZoneApplicable;
	
	@ManyToOne
	@JoinColumn(name = "vacation_time_zone_id")
	public EmpTimeZoneDBO vacationTimeZoneDBO;
	
	@Column(name = "is_vacation_time_zone_applicable")
	public Boolean isVacationTimeZoneApplicable;

	@Column(name = "is_duty_roster_applicable")
	public Boolean isDutyRosterApplicable;

	@OneToMany(mappedBy = "empJobDetailsDBO", cascade = CascadeType.ALL)
	public Set<EmpPfGratuityNomineesDBO> empPfGratuityNomineesDBOS = new HashSet<>();

	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_Status")
	public char recordStatus;
	
	@Column(name = "contract_start_date")
	private LocalDate contractStartDate;
	
	@Column(name = "contract_end_date")
	private LocalDate contractEndDate;
	
	@Column(name = "contract_remarks")
	private String contractRemarks;	
	
	@Column(name = "stage3_comments")
	private String stage3Comments;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stage3_interviewed_by")
	private EmpDBO stage3InterviewedBy;
	
	@Column(name = "prefered_joining_date")
	private LocalDateTime preferedJoiningDateTime;
	
	@Column(name = "joining_date_reject_reason")
	private String joiningDateRejectReason;
	
	@Column(name = "is_joining_date_confirmed")
	private Boolean isJoiningDateConfirmed;

	@Column(name = "is_holiday_working")
	public Boolean isHolidayWorking;

	@Column(name = "is_punching_exempted")
	public Boolean isPunchingExempted;

	@Column(name = "esi_insurance_no")
	private String esiInsuranceNo;

}