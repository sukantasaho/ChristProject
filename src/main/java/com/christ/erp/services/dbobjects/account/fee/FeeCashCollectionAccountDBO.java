package com.christ.erp.services.dbobjects.account.fee;

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

import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;

@Entity
@Table(name="fee_cash_collection_account")
public class FeeCashCollectionAccountDBO implements Serializable {
	
	private static final long serialVersionUID = -1001370701459069504L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="fee_cash_collection_account_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name="fee_cash_collection_head_id")
	public FeeCashCollectionHeadDBO feeCashCollectionHeadDBO;
	
	@ManyToOne
	@JoinColumn(name="acc_fee_heads_account_id")
	public AccFeeHeadsAccountDBO accFeeHeadsAccountDBO;
	
	@Column(name="paid_amount")
	public BigDecimal  paidAmount;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
