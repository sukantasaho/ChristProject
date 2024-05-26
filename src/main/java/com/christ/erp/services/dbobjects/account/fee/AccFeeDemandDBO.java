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
import com.christ.erp.services.dbobjects.common.AcaSessionDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "acc_fee_demand")
public class AccFeeDemandDBO {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name ="acc_fee_demand_id")
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "erp_academic_year_id")
	private ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "acc_financial_year_id")
	private AccFinancialYearDBO accFinancialYearDBO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "aca_session_id")
	private AcaSessionDBO acaSessionDBO;
	
	
	@Column( name = "demand_slip_no")
	private Integer demandSlipNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "student_id")
	private StudentDBO studentDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "student_appln_entries_id")
	private StudentApplnEntriesDBO studentApplnEntriesDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "acc_batch_fee_category_id")
	private AccBatchFeeCategoryDBO accBatchFeeCategoryDBO;
	
	@Column( name = "is_tuition_fee")
	private Boolean isTuitionFee;
	
	@Column( name = "modified_users_id")
    private Integer modifiedUsersId;
	
	@Column( name = "created_users_id")
	private Integer createdUsersId;
	  
	@Column( name = "record_status")
    private char recordStatus;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accFeeDemandDBO", cascade = CascadeType.ALL)
	private Set<AccFeeDemandCombinationDBO> accFeeDemandCombinationDBOSet;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accFeeDemandDBO", cascade = CascadeType.ALL)
	private Set<AccFeeDemandDetailDBO> accFeeDemandDetailDBOSet;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accFeeDemandDBO", cascade = CascadeType.ALL)
	private Set<AccFeeDemandAdjustmentDBO> accFeeDemandAdjustmentDBOSet;

}
