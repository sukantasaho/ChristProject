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

import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table( name = "acc_batch_fee_durations_detail")
public class AccBatchFeeDurationsDetailDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_batch_fee_durations_detail_id")
	private int id;
	
	@ManyToOne
	@JoinColumn( name = "acc_batch_fee_durations_id")
	private AccBatchFeeDurationsDBO accBatchFeeDurationsDBO;
	
	@ManyToOne
	@JoinColumn( name = "aca_duration_detail_id")
	private AcaDurationDetailDBO acaDurationDetailDBO;
	
	@ManyToOne
	@JoinColumn( name = "acc_financial_year_id")
	private AccFinancialYearDBO accFinancialYearDBO;
	  
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	 
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	  
	@Column( name = "record_status")
    private char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accBatchFeeDurationsDetailDBO", cascade = CascadeType.ALL)
	private Set<AccBatchFeeCategoryDBO> accBatchFeeCategoryDBOSet;
}
