package com.christ.erp.services.dbobjects.account.fee;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acc_fee_payment_receipts_detail")
@Getter
@Setter
public class AccFeePaymentReceiptsDetailsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_fee_payment_receipts_detail_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "acc_fee_payment_receipts_id")
    private AccFinancialYearDBO accFinancialYearDBO;
	
	@ManyToOne
	@JoinColumn(name = "acc_financial_year_id")
    private AccFeePaymentReceiptsDBO accFeePaymentReceiptsDBO;
	
	@ManyToOne
	@JoinColumn(name = "acc_fee_demand_detail_id")
    private AccFeeDemandDetailDBO accFeeDemandDetailDBO;
	
	@ManyToOne
	@JoinColumn(name = "acc_fee_demand_adjustment_detail_id")
    private AccFeeDemandAdjustmentDetailDBO accFeeDemandAdjustmentDetailDBO;
	
	@Column(name="amount_to_be_paid_in_receipt_account")
	private BigDecimal amountToBePaidInReceiptAccount;
	
	@Column(name="scholarship_amount_in_receipt_account")
	private BigDecimal scholarshipAmountInReceiptAccount;
	
	@Column(name="installment_amount_in_receipt_account")
	private BigDecimal installmentAmountInReceiptAccount;
	
	@Column(name="concession_amount_in_receipt_account")
	private BigDecimal concessionAmountInReceiptAccount;
	
	@Column(name="paid_amount_in_receipt_account")
	private BigDecimal paidAmountInReceiptAccount;
	
	@Column(name="advance_amount_in_receipt_account")
	private BigDecimal advanceAmountInReceiptAccount;
	
	@Column(name="refunded_amount_in_receipt_account")
	private BigDecimal refundedAmountInReceiptAccount;
	
	@Column(name="remarks")
	private String remarks;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
	
}
