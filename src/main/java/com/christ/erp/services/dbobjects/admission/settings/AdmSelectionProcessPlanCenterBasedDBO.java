package com.christ.erp.services.dbobjects.admission.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
public class AdmSelectionProcessPlanCenterBasedDBO implements Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_plan_center_based_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_plan_detail_id")
	public AdmSelectionProcessCenterDetailsDBO admSelectionProcessCenterDetailsDBO;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_venue_city_id")
	public AdmSelectionProcessVenueCityDBO admSelectionProcessVenueCityDBO;
	
	
	@Column(name = "venue_max_seats")
	public Integer venue_max_seats;
	
	@Column(name = "venue_available_seats")
	public Integer venue_available_seats;
	
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
