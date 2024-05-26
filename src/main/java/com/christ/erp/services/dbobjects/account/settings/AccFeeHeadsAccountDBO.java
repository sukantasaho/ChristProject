package com.christ.erp.services.dbobjects.account.settings;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="acc_fee_heads_account")
public class AccFeeHeadsAccountDBO implements Serializable {
	
	private static final long serialVersionUID = -1229265200940356941L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_fee_heads_account_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name="acc_fee_heads_id")
	public AccFeeHeadsDBO accFeeHeadsDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_id")
	public ErpCampusDBO erpCampusDBO;

	@Column(name="sap_code")
	public String sapCode;

	@ManyToOne
	@JoinColumn(name="acc_accounts_id")
	public AccAccountsDBO accAccountsDBO;	
	
	@Column(name="amount")
	public BigDecimal amount;
	
	@Column(name="amount_international")
	public BigDecimal amountInternational;
	
	@ManyToOne
	@JoinColumn(name= "erp_currency_id")
	private ErpCurrencyDBO erpCurrencyDBO;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
