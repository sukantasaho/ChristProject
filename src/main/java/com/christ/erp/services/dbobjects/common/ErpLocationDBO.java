package com.christ.erp.services.dbobjects.common;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="erp_location")
@Getter
@Setter
public class ErpLocationDBO  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_location_id")
    public Integer id;

    @Column(name="location_name")
    public String locationName;

    @Column(name="location_short_name")
    public String locationShortName;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public Character recordStatus;
}