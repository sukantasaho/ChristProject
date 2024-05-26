package com.christ.erp.services.dbobjects.admission.settings;

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
@Table(name = "adm_selection_process_center_details")
@Getter
@Setter
public class AdmSelectionProcessCenterDetailsDBO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_selection_process_center_details_id")
    public int id;
    
	@ManyToOne
	@JoinColumn(name="adm_selection_process_venue_city_id")
    public AdmSelectionProcessVenueCityDBO admSelectionProcessVenueCityDBO;
    
    @Column(name="center_name")
    public String centerName;
    
    @Column(name="center_code")
    public String centerCode;
    
    @Column(name="center_address")
    public String centerAddress;
    
    @Column(name="center_max_seats")
    public Integer centerMaxSeats;
    
    @Column(name="center_priority_order")
    public Integer centerPriorityOrder;
    
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public Character recordStatus;
}
