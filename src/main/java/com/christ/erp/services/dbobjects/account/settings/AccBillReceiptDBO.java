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

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name="acc_bill_receipt")
public class AccBillReceiptDBO implements Serializable{

	private static final long serialVersionUID = 5948750032808800525L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_bill_rceipt_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name="acc_financial_year_id")
	public AccFinancialYearDBO accFinancialYearDBO;
	
	@Column(name="type")
	public String type;
	
	@Column(name="type_code")
	public String typeCode;
	
	@Column(name="bill_receipt_no_prefix")
	public String billReceiptNoPrefix;
	
	@Column(name="bill_receipt_start_no")
	public Integer billReceiptStartNo;
	
	@Column(name="bill_receipt_current_no")
	public Integer billReceiptCurrentNo;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
