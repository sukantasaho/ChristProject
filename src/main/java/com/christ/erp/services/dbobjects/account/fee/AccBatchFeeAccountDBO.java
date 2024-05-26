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

import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.common.AccFeeDemandAdjustmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acc_batch_fee_account")
@Getter
@Setter
public class AccBatchFeeAccountDBO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_batch_fee_account_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name= "acc_batch_fee_head_id")
	private AccBatchFeeHeadDBO accBatchFeeHeadDBO;

	@ManyToOne
	@JoinColumn(name= "acc_accounts_id")
	private AccAccountsDBO accAccountsDBO;

	@ManyToOne
	@JoinColumn(name= "erp_currency_id")
	private ErpCurrencyDBO erpCurrencyDBO;
	
	@ManyToOne
	@JoinColumn(name= "acc_fee_demand_adjustment_category_id")
	private AccFeeDemandAdjustmentCategoryDBO accFeeDemandAdjustmentCategoryDBO;
	
	@Column( name = "fee_account_amount")
    private BigDecimal feeAccountAmount;

	@Column( name = "fee_scholarship_amount")
    private BigDecimal feeScholarshipAmount;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;
}
