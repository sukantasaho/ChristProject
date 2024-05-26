package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.time.LocalDateTime;
import java.util.HashSet;
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

import com.christ.erp.services.dbobjects.admission.settings.AdmIntakeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_plan")
@Getter
@Setter
public class AdmSelectionProcessPlanDBO  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_plan_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="adm_intake_batch_id")
	public AdmIntakeBatchDBO admIntakeBatchDBO;
	
	@Column(name = "selection_process_session")
	public String selectionProcessSession;
	
	@Column(name = "is_conducted_in_india")
	public boolean isConductedInIndia;
	
	@Column(name = "application_open_from")
	public LocalDateTime applicationOpenFrom;
	
	@Column(name = "application_open_till")
	public LocalDateTime applicationOpenTill;
	
	@Column(name = "selection_process_start_date")
	public LocalDateTime selectionProcessStartDate;
	
	@Column(name = "selection_process_end_date")
	public LocalDateTime selectionProcessEndDate;
	
	@Column(name = "result_declaration_date")
	public LocalDateTime resultDeclarationDate;

	@Column(name = "last_date_for_fee_payment")
	public LocalDateTime lastDateForFeePayment;
	
	@Column(name = "last_date_of_admission")
	public LocalDateTime lastDateOfAdmission;
	
	@Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessPlanDBO", cascade = CascadeType.ALL)
	public Set<AdmSelectionProcessPlanProgrammeDBO> admSelectionProcessPlanProgrammeDBOs = new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessPlanDBO", cascade = CascadeType.ALL)
   	public Set<AdmSelectionProcessPlanDetailDBO> admSelectionProcessPlanDetailDBO = new HashSet<>();
	
   	
}
