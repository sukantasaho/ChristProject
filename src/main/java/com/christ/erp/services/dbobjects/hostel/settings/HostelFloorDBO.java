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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="hostel_floor")
@Setter
@Getter
public class HostelFloorDBO implements Serializable {

    private static final long serialVersionUID = -6719584823226995689L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hostel_floor_id")
    public Integer id;
    
    @ManyToOne
    @JoinColumn(name = "hostel_block_unit_id")
    public HostelBlockUnitDBO hostelBlockUnitDBO;

    @Column(name = "floor_no")
    public Integer floorNo;
    
    @Column(name = "total_rooms")
    public Integer totalRooms;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(mappedBy ="hostelFloorDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<HostelRoomsDBO> hostelRoomsDBOSet = new HashSet<>() ;
}
