package com.christ.erp.services.dbobjects.account.fee;

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

import com.christ.erp.services.dbobjects.common.ErpSpecializationDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "acc_batch_fee")
public class AccBatchFeeDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_batch_fee_id")
	private int id;
	
	@ManyToOne
	@JoinColumn( name = "aca_batch_id")
	private AcaBatchDBO acaBatchDBO;
	
	@Column( name = "fee_collection_set")
	private String feeCollectionSet;
	
	@ManyToOne
	@JoinColumn( name = "erp_specialization_id")
	private ErpSpecializationDBO erpSpecializationDBO;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	  
	@Column( name = "record_status")
    private char recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accBatchFeeDBO", cascade = CascadeType.ALL)
    private Set<AccBatchFeeDurationsDBO> accBatchFeeDurationsDBOSet;
}
