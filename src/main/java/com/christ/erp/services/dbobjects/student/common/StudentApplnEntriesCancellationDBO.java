package com.christ.erp.services.dbobjects.student.common;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name="student_appln_entries_cancellation")
public class StudentApplnEntriesCancellationDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="student_appln_entries_cancellation_id")
	private int id;	
	
	@ManyToOne
	@JoinColumn(name="student_appln_entries_id")
	private StudentApplnEntriesDBO studentApplnEntriesDBO;
	
	@Column(name="cancellation_request_date_time")
	private LocalDateTime  cancellationRequestDateTime;
	
	@ManyToOne
	@JoinColumn(name="student_appln_cancellation_reasons_id")
	private StudentApplnCancellationReasonsDBO studentApplnCancellationReasonsDBO;
	
	@Column(name="reason_for_cancellation_others")
	private String  reasonForCancellationOthers;
	
	@Column(name="refund_bank_name")
	private String  refundBankName;
	
	@Column(name="refund_ifsc_code")
	private String  refundIfscCode;
	
	@Column(name="refund_swift_code")
	private String  refundSwiftCode;
	
	@Column(name="refund_iban_no")
	private String  refundIbanNo;
	
	@Column(name="refund_account_number")
	private String  refundAccountNumber;
	
	@Column(name="refund_account_holder_name")
	private String  refundAccountHolderName;
	
	@Column(name="cheque_in_favour")
	private String  chequeInFavour;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "cancellation_type",columnDefinition="ENUM('ONLINE','OFFLINE'")
	private CancellationType CancellationType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "refund_type",columnDefinition="ENUM('ONLINE','CHEQUE','REFUND_OUTSIDE_INDIA'")
	private RefundType refundType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "refund_account_holder_type",columnDefinition="ENUM('PARENT','STUDENT'")
	private RefundAccountHolderType refundAccountHolderType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "refund_account_type",columnDefinition="ENUM('SAVINGS','CURRENT'")
	private RefundAccountType refundAccountType;

	@Column(name = "created_users_id", updatable = false)
	private Integer  createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer  modifiedUsersId;
	
	@Column(name = "record_status")
	private Character recordStatus;
	
}
