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

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
@Table(name="hostel_bed")


public class HostelBedDBO implements Serializable {

    private static final long serialVersionUID = -6719584823226995689L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hostel_bed_id")
    public Integer id;

    @Column(name = "bed_no")
    public String bedNo;

    @ManyToOne
    @JoinColumn(name = "hostel_rooms_id")
    public HostelRoomsDBO hostelRoomsDBO;

    @Column(name = "is_occupied")
    public boolean occupied;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
