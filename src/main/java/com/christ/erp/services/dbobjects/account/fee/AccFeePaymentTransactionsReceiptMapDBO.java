package com.christ.erp.services.dbobjects.account.fee;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acc_fee_payment_transactions_receipt_map")
@Getter
@Setter
public class AccFeePaymentTransactionsReceiptMapDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_fee_payment_transactions_receipt_map_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "acc_fee_demand_id")
    private AccFeeDemandDBO accFeeDemandDBO;
	
	@ManyToOne
	@JoinColumn(name = "acc_fee_demand_adjustment_id")
    private AccFeeDemandAdjustmentDBO accFeeDemandAdjustmentDBO;
	
	@ManyToOne
	@JoinColumn(name = "acc_fee_payment_receipts_id")
    private AccFeePaymentReceiptsDBO accFeePaymentReceiptsDBO;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
