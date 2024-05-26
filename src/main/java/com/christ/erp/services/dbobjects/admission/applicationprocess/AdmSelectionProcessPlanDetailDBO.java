package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.time.LocalDate;
import java.time.LocalTime;
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

import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnSelectionProcessDatesDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_plan_detail")
@Getter
@Setter
public class AdmSelectionProcessPlanDetailDBO{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_plan_detail_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_plan_id")
	public AdmSelectionProcessPlanDBO admSelectionProcessPlanDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_type_id")
	public AdmSelectionProcessTypeDBO admSelectionProcessTypeDBO;
	
	@Column(name = "process_order")
	public int processOrder;
	
	@Column(name = "selection_process_date")
	public LocalDate selectionProcessDate;

	@Column(name = "selection_process_time")
	public LocalTime selectionProcessTime;
	
	@Column(name = "slot")
	public String slot;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_venue_city_id")
	public AdmSelectionProcessVenueCityDBO admSelectionProcessVenueCityDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_template_id")
	public ErpTemplateDBO erpTemplateDBO;
	
	@Column(name = "available_seats")
	public Integer availableSeats;
	
	@Column(name = "is_candidate_choose_sp_venue")
	public Boolean isCandidateChooseSpVenue;

	@Column(name = "is_candidate_choose_sp_date")
	public Boolean isCandidateChooseSpDate;
	
	@Column(name = "is_candidate_choose_sp_2_venue")
	public Boolean isCandidateChooseSp2Venue;

	@Column(name = "is_candidate_choose_sp_2_date")
	public Boolean isCandidateChooseSp2Date;

	@Column(name = "is_follow_the_same_venue_for_sp2")
	public Boolean isfollowTheSameVenueForSp2;
	
	@Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessPlanDetailDBO", cascade = CascadeType.ALL)
   	public Set<AdmSelectionProcessPlanDetailProgDBO> admSelectionProcessPlanDetailProgDBOs = new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessPlanDetailDBO", cascade = CascadeType.ALL)
   	public Set<AdmSelectionProcessPlanCenterBasedDBO> admSelectionProcessPlanCenterBasedDBOs = new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessPlanDetailDBO", cascade = CascadeType.ALL)
   	public Set<AdmSelectionProcessPlanDetailAllotmentDBO> admSelectionProcessPlanDetailAllotmentDBOs = new HashSet<>();

	@OneToMany(mappedBy = "admSelectionProcessPlanDetailDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<StudentApplnSelectionProcessDatesDBO> studentApplnSelectionProcessDatesDBOS = new HashSet<>();
}
