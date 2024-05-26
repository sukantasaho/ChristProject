package com.christ.erp.services.dbobjects.account.fee;

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

@Entity
@Getter
@Setter
@Table( name = "acc_fee_demand_combination")
public class AccFeeDemandCombinationDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_combination_id")
	private int id;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_id")
	private AccFeeDemandDBO accFeeDemandDBO;
	
	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_combination_type_id")
	private AccFeeDemandCombinationTypeDBO accFeeDemandCombinationTypeDBO;

	@Column( name = "entries_id")
    private Integer entriesId;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;
	
}
