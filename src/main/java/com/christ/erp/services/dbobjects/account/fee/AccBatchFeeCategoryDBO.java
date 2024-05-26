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

import com.christ.erp.services.dbobjects.common.AccFeeDemandAdjustmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "acc_batch_fee_category")
@Getter
@Setter
public class AccBatchFeeCategoryDBO implements Serializable {
	private static final long serialVersionUID = -3231715796558567492L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_batch_fee_category_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name= "acc_batch_fee_durations_detail_id")
	private AccBatchFeeDurationsDetailDBO accBatchFeeDurationsDetailDBO;
	
	@ManyToOne
	@JoinColumn(name= "erp_admission_category_id")
	private ErpAdmissionCategoryDBO erpAdmissionCategoryDBO;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	  
	@Column( name = "record_status")
    private char recordStatus;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accBatchFeeCategoryDBO", cascade = CascadeType.ALL)
	private Set<AccBatchFeeHeadDBO> accBatchFeeHeadDBOSet;
}
