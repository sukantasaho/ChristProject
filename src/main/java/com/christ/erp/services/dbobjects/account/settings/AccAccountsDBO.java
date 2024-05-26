package com.christ.erp.services.dbobjects.account.settings;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="acc_accounts")
public class AccAccountsDBO implements Serializable{

	private static final long serialVersionUID = 7019201865130390606L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_accounts_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_id")
	public ErpCampusDBO erpCampusDBO;
	
	@Column(name="account_no")
	public String accountNo;
	
	@Column(name="account_name")
	public String accountName;
	
	@Column(name="is_university_account")
	public Boolean isUniversityAccount;
	
	@Column(name="account_code")
	public String accountCode;
	
	@Column(name="print_name")
	public String printName;
	
	@Column(name="logo_file_name")
	public String logoFileName;
	
	@Column(name="description_1")
	public String description1;
	
	@Column(name="description_2")
	public String description2;
	
	@Column(name="bank_info")
	public String bankInfo;
	
	@Column(name="verified_by")
	public String verifiedBy;
	
	@Column(name="NEFT_code")
	public String NEFTCode;
	
	@Column(name="NEFT_display_name")
	public String NEFTDisplayName;
	
	@Column(name="IFSC_code")
	public String IFSCCode;
	
	@Column(name="NEFT_code_for_sib_payment")
	public String NEFTCodeForSibPayment;
	
	@Column(name="swift_code")
	public String swiftCode;
	
	@Column(name="refund_from_account")
	public String refundFromAccount;
	
	@Column(name="sender_name")
	public String senderName;
	
	@Column(name="sender_email")
	public String senderEmail;
	
	@Column(name="refund_file_code")
	public String refundFileCode;
	
	@Column(name="refund_address_1")
	public String refundAddress1;
	
	@Column(name="refund_address_2")
	public String refundAddress2;
	
	@Column(name="refund_address_3")
	public String refundAddress3;
	
	@Column(name="refund_address_4")
	public String refundAddress4;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
