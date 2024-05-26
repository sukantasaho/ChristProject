package com.christ.erp.services.dbobjects.account.fee;

import java.math.BigDecimal;
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

import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "acc_fee_demand_detail")
public class AccFeeDemandDetailDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_detail_id")
	private int id;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_id")
	private AccFeeDemandDBO accFeeDemandDBO;
	
	@ManyToOne
	@JoinColumn( name = "acc_batch_fee_account_id")
	private AccBatchFeeAccountDBO accBatchFeeAccountDBO;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_heads_account_id")
	private AccFeeHeadsAccountDBO accFeeHeadsAccountDBO;
	
	@ManyToOne
	@JoinColumn( name = "erp_currency_id")
	private ErpCurrencyDBO erpCurrencyDBO;
	
	
	@Column( name = "amount_to_be_paid_in_account")
    private BigDecimal amountToBePaidInAccount;
	
	@Column( name = "total_additions_in_account")
    private BigDecimal totalAdditionsInAccount;
	
	@Column( name = "scholarship_in_account")
    private BigDecimal scholarshipInAccount;
	
	@Column( name = "installment_in_account")
    private BigDecimal installmentInAccount;
	
	@Column( name = "concession_in_account")
    private BigDecimal concessionInAccount;
	
	@Column( name = "deductions_in_account")
    private BigDecimal deductionsInAccount;

	@Column( name = "amount_to_be_paid_after_adjustments")
    private BigDecimal amountToBePaidAfterDeductions;
	
	@Column( name = "paid_amount_in_account")
    private BigDecimal paidAmountInAccount;

	@Column( name = "balance_amount_in_account")
    private BigDecimal balanceAmountInAccount;
	
	@Column( name = "advance_amount_in_account")
    private BigDecimal advanceAmountInAccount;

	@Column(name = "refunded_amount_from_account") 
    private BigDecimal refundedAmountFromAccount;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accFeeDemandDetailDBO", cascade = CascadeType.ALL)
	private Set<AccFeeDemandDetailLogDBO> accFeeDemandDetailLogDBOSet;

}
