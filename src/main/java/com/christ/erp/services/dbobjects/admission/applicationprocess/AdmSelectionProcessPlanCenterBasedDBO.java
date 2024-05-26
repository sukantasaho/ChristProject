package com.christ.erp.services.dbobjects.admission.applicationprocess;

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

import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_plan_center_based")
@Getter
@Setter
public class AdmSelectionProcessPlanCenterBasedDBO  {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_plan_center_based_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_plan_detail_id")
	public AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_venue_city_id")
	public AdmSelectionProcessVenueCityDBO admSelectionProcessVenueCityDBO;
	
	@Column(name = "venue_max_seats")
    public Integer venueMaxSeats;
	
	@Column(name = "venue_available_seats")
    public Integer venueAvailableSeats;
	
	@Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessPlanDetailDBO", cascade = CascadeType.ALL)
   	public Set<AdmSelectionProcessPlanDetailProgDBO> admSelectionProcessPlanDetailProgDBO = new HashSet<>();


}
