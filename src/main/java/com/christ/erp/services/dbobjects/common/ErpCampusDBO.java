package com.christ.erp.services.dbobjects.common;

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
@Table(name="erp_campus")
@Getter
@Setter
public class ErpCampusDBO {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_campus_id")
    public Integer id;

    @Column(name="campus_name")
    public String campusName;

    @Column(name="short_name")
    public String shortName;

    @Column(name="campus_color_code")
    public String campusColorCode;

    @ManyToOne
    @JoinColumn(name="erp_location_id")
    public ErpLocationDBO erpLocationDBO;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public Character recordStatus;

}