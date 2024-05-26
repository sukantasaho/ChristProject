package com.christ.erp.services.dbobjects.common;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name="erp_blood_group")
public class ErpBloodGroupDBO implements Serializable {

    private static final long serialVersionUID = 8205644328262651286L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_blood_group_id")
    public int id;

    @Column(name="blood_group_name")
    public String bloodGroupName;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
