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
@Table( name = "acc_fee_demand_adjustment_detail")
public class AccFeeDemandAdjustmentDocumentsDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_adjustment_detail_id")
	private int id;

	@ManyToOne
	@JoinColumn( name = "acc_fee_demand_adjustment_id")
	private AccFeeDemandAdjustmentDBO accFeeDemandAdjustmentDBO;
	
	@Column( name = "document_name")
	private String documentName;

	@Column( name = "document_url")
	private String documentUrl;

	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "record_status")
    private char recordStatus;

}
