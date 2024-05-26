package com.christ.erp.services.dbobjects.account.fee;

import java.time.LocalDateTime;
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
@Table(name = "acc_fee_payment_transactions_receipt_map")
@Getter
@Setter
public class AccFeePaymentReceiptsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_fee_payment_receipts_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "acc_financial_year_id")
    private AccFinancialYearDBO accFinancialYearDBO;
	
    @Column(name = "receipt_no")
    private Integer receiptNo;
    
    @Column(name = "receipt_date")
    private LocalDateTime receipt_date;
    
	@Column(name="receipts_url")
	private String receiptsUrl;
    
	@Column(name="is_provisional")
	private Boolean isProvisional;
	
	@Column(name="is_cancelled")
	private Boolean isCancelled;
    
	@Column(name="remarks")
	private String remarks;
	
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
	
}
