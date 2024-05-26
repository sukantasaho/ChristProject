package com.christ.erp.services.dbobjects.account.fee;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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

import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="fee_cash_collection")
public class FeeCashCollectionDBO implements Serializable{
	
	private static final long serialVersionUID = -4277592054379454658L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="fee_cash_collection_id")
	public int id;	

	@Column(name="receipt_no")
	public int receiptNo;
	
	@ManyToOne
	@JoinColumn(name="acc_financial_year_id")
	public AccFinancialYearDBO accFinancialYearDBO;
	
	@Column(name="cash_collection_date_time")
	public LocalDateTime cashCollectionDateTime;
	
	@ManyToOne
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name="student_id")
	public StudentDBO studentDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_id")
	public ErpCampusDBO erpCampusDBO;
	
	@Column(name="student_name")
	public String studentName;
	
	@ManyToOne
	@JoinColumn(name="acc_fee_payment_mode_id")
	public AccFeePaymentModeDBO accFeePaymentModeDBO;
	
	@Column(name="sub_total")
	public BigDecimal  subTotal;
	
	@Column(name="cgst_total_amount")
	public BigDecimal  cgstTotalAmount;
	
	@Column(name="sgst_total_amount")
	public BigDecimal  sgstTotalAmount;
	
	@Column(name="igst_total_amount")
	public BigDecimal  igstTotalAmount;
	
	@Column(name="total_amount")
	public BigDecimal  totalAmount;
	
	@Column(name="is_cancelled")
	public Boolean isCancelled;
	
	@Column(name="cancelled_reason")
	public String cancelledReason;
	
	@Column(name = "cancelled_by_user_id")
	public Integer cancelledUserId;
	
	@Column(name = "cancelled_date")
	public LocalDateTime cancelledDate;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "feeCashCollectionDBO", cascade = CascadeType.ALL)
	public Set<FeeCashCollectionHeadDBO> feeCashCollectionHeadList = new HashSet<>();
	
}
