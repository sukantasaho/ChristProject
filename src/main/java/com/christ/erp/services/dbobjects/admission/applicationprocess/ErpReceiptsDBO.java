package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeePaymentModeDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

@Entity
@Table(name="erp_receipts")
public class ErpReceiptsDBO implements Serializable{
	
	private static final long serialVersionUID = -8482034947902485501L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_receipts_id")
    public int id;
	
	@ManyToOne
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name="acc_financial_year_id")
	public AccFinancialYearDBO accFinancialYearDBO;
	
	@Column(name="receipt_no")
	public String receiptNo;
	
	@ManyToOne
	@JoinColumn(name="erp_number_generation_id")
	public ErpNumberGenerationDBO erpNumberGenerationDBO;
	
	@ManyToOne
	@JoinColumn(name="acc_fee_heads_id")
	public AccFeeHeadsDBO accFeeHeadsDBO;
	
	@ManyToOne
	@JoinColumn(name="acc_accounts_id")
	public AccAccountsDBO accAccountsDBO;
	
	@ManyToOne
	@JoinColumn(name="acc_fee_payment_mode_id")
	public AccFeePaymentModeDBO accFeePaymentModeDBO;
	
	@Column(name="DD_number")
	public String DdNumber;
	
	@Column(name = "receipts_amount")
	public BigDecimal receiptsAmount;
	
	@Column(name="receipts_date")
	public LocalDateTime receiptsDate;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	@Column(name="referenced_erp_table_name")
	public String referencedErpTableName;
	
	@Column(name = "is_cancelled")
	public Boolean isCancelled;
	
	@Column(name="cancel_comments")
	public String cancelComments;
	
	@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;
 
    @Column(name="record_status")
    public char recordStatus;
}
