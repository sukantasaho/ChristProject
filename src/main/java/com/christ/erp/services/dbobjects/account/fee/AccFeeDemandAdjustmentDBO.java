package com.christ.erp.services.dbobjects.account.fee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "acc_fee_demand_adjustment")
public class AccFeeDemandAdjustmentDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_adjustment_id")
	private int id;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_id")
	private AccFeeDemandDBO accFeeDemandDBO;
	
	@ManyToOne
	@JoinColumn( name = "erp_work_flow_process_id")
	private ErpWorkFlowProcessDBO erpWorkFlowProcessDBO;
	
	@Column( name = "adjustment_no")
    private Integer adjustmentNo;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type",columnDefinition="ENUM('I','C','S','A','F','R')")
    private AdjustmentType adjustmentType;
	
	@Column( name = "total_adjustment")
    private BigDecimal TotalAdjustment;
	
	@Column(name="is_adjustment_applied")
	private boolean isAdjustmentApplied;
	
	@Column(name="installment_due_date")
	private LocalDate installmentDueDate;
		
	@Column(name="is_installment_settled")
	private boolean isInstallment_settled;
	
	@Column(name="installment_paid_date")
	private LocalDate installmentPaidDate;
	
	@ManyToOne
	@JoinColumn( name = "adjustment_parent_id")
	private AccFeeDemandAdjustmentDBO accFeeDemandAdjustmentDBO;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "advance_type",columnDefinition="ENUM('CREDIT','DEBIT'")
	private AdvanceType advanceType;
	
	@ManyToOne
	@JoinColumn( name = "acc_account_id")
	private AccAccountsDBO accAccountsDBO;
	
	@Column(name="refunded_to_account_number")
	private String refundedToAccountNumber;

	@Column(name="refunded_to_account_ifsc_code")
	private String refundedToAccountIfscCode;

	@Column(name="refunded_to_account_bank_name")
	private String refundedToAccountBankName;	
	
	@Column(name="refunded_to_account_branch_name")
	private String refundedToAccountBranchName;	
	
	@Column(name="refunded_to_account_name")
	private String refundedToAccountName;	
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accFeeDemandAdjustmentDBO", cascade = CascadeType.ALL)
	private Set<AccFeeDemandAdjustmentDetailDBO> accFeeDemandAdjustmentDetailDBOSet;
	
}
