package com.christ.erp.services.dbobjects.hostel.settings;

import java.io.Serializable;
import java.time.LocalDate;
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

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;

@SuppressWarnings("serial")
@Entity
@Table(name = "hostel_holiday_events")
public class HostelHolidayEventsDBO implements Serializable {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "hostel_holiday_events_id")
    public int id;
    
    @ManyToOne
    @JoinColumn(name = "erp_academic_year_id")
    public ErpAcademicYearDBO erpAcademicYearDBO;
    
    @ManyToOne
    @JoinColumn(name = "hostel_id")
    public HostelDBO hostelDBO;
    
    @Column(name = "holiday_type")
    public String holidayType;
    
    @Column(name = "holiday_description")
    public String holidayDescription;
    
    @Column(name = "holiday_from_date")
    public LocalDate holidayFromDate;
    
    @Column(name = "holiday_to_date")
    public LocalDate holidayToDate;
    
    @Column(name = "is_holiday_to_evening")
    public boolean isHolidayToEvening;
    
    @Column(name = "is_holiday_from_morning")
    public boolean isHolidayFromMorning;
    
    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hostelHolidayEventsDBO", cascade = CascadeType.ALL)
   	public Set<HostelHolidayEventsProgrammesDBO> hostelHolidayEventsProgrammesDBO = new HashSet<>();

}
