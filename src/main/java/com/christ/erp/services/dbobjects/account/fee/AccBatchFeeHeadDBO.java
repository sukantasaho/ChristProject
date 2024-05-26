package com.christ.erp.services.dbobjects.account.fee;

import java.io.Serializable;
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

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "acc_batch_fee_head")
@Getter
@Setter
public class AccBatchFeeHeadDBO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5207409320465430662L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_batch_fee_head_id")
	private int id;

	@ManyToOne
	@JoinColumn(name= "acc_batch_fee_category_id")
	private AccBatchFeeCategoryDBO accBatchFeeCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name= "acc_fee_heads_id")
	private AccFeeHeadsDBO accFeeHeadsDBO;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	  
	@Column( name = "record_status")
    private char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accBatchFeeHeadDBO", cascade = CascadeType.ALL)
	private Set<AccBatchFeeAccountDBO> accBatchFeeAccountDBOSet;

}
