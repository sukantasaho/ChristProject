package com.christ.erp.services.dbobjects.admission.settings;

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

import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_venue_city")
@Getter
@Setter
public class AdmSelectionProcessVenueCityDBO{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_selection_process_venue_city_id")
    public int id;

    @Column(name="selection_process_mode")
    public String selectionProcessMode;
    
    @Column(name="venue_name")
    public String venueName;
    
    @Column(name="venue_address")
    public String venueAddress;
    
    @Column(name="venue_max_seats")
    public Integer venueMaxSeats;
    
    @ManyToOne
    @JoinColumn(name="erp_country_id")
    public ErpCountryDBO erpCountryDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_state_id")
    public ErpStateDBO erpStateDBO;
    
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admSelectionProcessVenueCityDBO", cascade = CascadeType.ALL)
	public Set<AdmSelectionProcessCenterDetailsDBO> centerDetailsDBOs = new HashSet<>();
}
