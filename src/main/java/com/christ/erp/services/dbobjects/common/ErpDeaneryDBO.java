package com.christ.erp.services.dbobjects.common;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name="erp_deanery")
@Getter
@Setter
public class ErpDeaneryDBO implements Serializable {

    private static final long serialVersionUID = 6543829149282513786L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_deanery_id")
    public int id;

    @Column(name="deanery_name")
    public String deaneryName;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
