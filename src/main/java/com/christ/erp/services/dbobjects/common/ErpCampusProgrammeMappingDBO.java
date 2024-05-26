package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_campus_programme_mapping")
@Getter
@Setter
public class ErpCampusProgrammeMappingDBO  {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_campus_programme_mapping_id")
    public int id;
	
    @ManyToOne
    @JoinColumn(name="erp_location_id")
    public ErpLocationDBO erpLocationDBO;

    @ManyToOne
    @JoinColumn(name="erp_campus_id")
    public ErpCampusDBO erpCampusDBO;

    @ManyToOne
	@JoinColumn(name = "erp_programme_id")
	public ErpProgrammeDBO erpProgrammeDBO;
    
//    @ManyToOne
//    @JoinColumn(name="acc_account_id")
//    public AccAccountsDBO accAccountsDBO;
	
	@Column(name = "programme_commence_year")
	public int programmeCommenceYear;
	
	@Column(name = "programme_inactivated_year")
	public Integer programmeInactivatedYear;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
