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

import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.common.AccFeeDemandAdjustmentCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "acc_fee_demand_adjustment_detail")
public class AccFeeDemandAdjustmentDetailDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_adjustment_detail_id")
	private int id;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_adjustment_id")
	private AccFeeDemandAdjustmentDBO accFeeDemandAdjustmentDBO;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_detail_id")
	private AccFeeDemandDetailDBO accFeeDemandDetailDBO;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_heads_account_id")
	private AccFeeHeadsAccountDBO accFeeHeadsAccountDBO;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_adjustment_category_id")
	private AccFeeDemandAdjustmentCategoryDBO accFeeDemandAdjustmentCategory;
	
	@Column( name = "adjustment_amount")
    private BigDecimal adjustmentAmount;
	
	@ManyToOne
	@JoinColumn( name = "adjustment_approved_emp_id")
	private EmpDBO empDBO;
	
	@Column( name = "refund_amount")
    private BigDecimal refundAmount;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;
}
