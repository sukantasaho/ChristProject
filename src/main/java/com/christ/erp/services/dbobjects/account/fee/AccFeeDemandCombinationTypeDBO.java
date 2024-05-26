package com.christ.erp.services.dbobjects.account.fee;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "acc_fee_demand_combination_type")
public class AccFeeDemandCombinationTypeDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_combination_type_id")
	private int id;
	
	@Column( name = "demand_combination_type")
    private String demandCombinationType;
	
	@Column( name = "demand_combination_type_table")
    private String demandCombinationTypeTable;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;
}
