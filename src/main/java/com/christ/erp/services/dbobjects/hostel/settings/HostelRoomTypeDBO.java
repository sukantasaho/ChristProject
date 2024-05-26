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

@Getter
@Setter
@Entity
@Table(name="hostel_room_type")
public class HostelRoomTypeDBO implements Serializable {

    private static final long serialVersionUID = -6719584823226995689L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hostel_room_type_id")
    public Integer id;
    
    @ManyToOne
    @JoinColumn(name = "hostel_id")
    public HostelDBO hostelDBO;

    @Column(name = "room_type")
    public String roomType;
    
    @Column(name = "total_occupants")
    public Integer totalOccupants;

    @Column(name = "room_type_description")
    public String roomTypeDescription;

    @Column(name = "room_type_category")
    public String roomTypeCategory;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

    @OneToMany(fetch =FetchType.LAZY,mappedBy = "hostelRoomTypeDBO", cascade = CascadeType.ALL)
    public Set<HostelRoomTypeDetailsDBO> hostelRoomTypeDetailsDBOSet =  new HashSet<>();
}