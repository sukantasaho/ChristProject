package com.christ.erp.services.dbobjects.hostel.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

@SuppressWarnings("serial")
@Entity
@Table(name = "hostel_holiday_events_programmes")
public class HostelHolidayEventsProgrammesDBO implements Serializable {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "hostel_holiday_events_programmes_id")
    public int id;
    
    @ManyToOne
    @JoinColumn(name = "hostel_holiday_events_id")
    public HostelHolidayEventsDBO hostelHolidayEventsDBO;
    
    @ManyToOne
    @JoinColumn(name = "erp_campus_programme_mapping_id")
    public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
    
    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
