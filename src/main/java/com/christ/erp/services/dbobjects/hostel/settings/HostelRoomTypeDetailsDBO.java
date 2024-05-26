package com.christ.erp.services.dbobjects.hostel.settings;

import java.io.Serializable;
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
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="hostel_room_type_details")
public class HostelRoomTypeDetailsDBO implements Serializable {

    private static final long serialVersionUID = -6719584823226995689L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hostel_room_type_details_id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "hostel_room_type_id")
    public HostelRoomTypeDBO hostelRoomTypeDBO;

    @ManyToOne
    @JoinColumn(name = "hostel_facility_setting_id")
    public HostelFacilitySettingDBO hostelFacilitySettingDBO;
    
    @Column(name="room_image_url")
    public String roomImageUrl;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

}