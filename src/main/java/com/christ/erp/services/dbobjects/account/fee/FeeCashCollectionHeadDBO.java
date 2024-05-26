package com.christ.erp.services.dbobjects.account.fee;

import java.io.Serializable;
import java.math.BigDecimal;
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

import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccGSTPercentageDBO;


@Entity
@Table(name="fee_cash_collection_head")
public class FeeCashCollectionHeadDBO implements Serializable {
	private static final long serialVersionUID = 3386575990159948464L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="fee_cash_collection_head_id")
	public int id;	
	
	@ManyToOne
	@JoinColumn(name="fee_cash_collection_id")
	public FeeCashCollectionDBO feeCashCollectionDBO;
	
	@ManyToOne
	@JoinColumn(name="acc_fee_heads_id")
	public AccFeeHeadsDBO accFeeHeadsDBO;
	
	@ManyToOne
	@JoinColumn(name="acc_gst_percentage_id")
	public AccGSTPercentageDBO accGSTPercentageDBO;
	
	@Column(name="cgst_amount")
	public BigDecimal  cgstAmount;
	
	@Column(name="sgst_amount")
	public BigDecimal  sgstAmount;
	
	@Column(name="igst_amount")
	public BigDecimal  igstAmount;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "feeCashCollectionHeadDBO", cascade = CascadeType.ALL)
	public Set<FeeCashCollectionAccountDBO> feeCashCollectionAccountList = new HashSet<>();
}
