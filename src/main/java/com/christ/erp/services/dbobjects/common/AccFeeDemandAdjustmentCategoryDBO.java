package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.account.fee.AdjustmentType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "acc_fee_demand_adjustment_category")
public class AccFeeDemandAdjustmentCategoryDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_adjustment_category_id")
	private int id;
	
	@Column( name = "adjustment_category")
    private String adjustmentCategory;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "adjustment_type",columnDefinition="ENUM('I','C','S','A','F','R')")
    private AdjustmentType adjustmentType;

	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;
}
