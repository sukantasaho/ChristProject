package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "erp_specialization")
@Getter
@Setter
public class ErpSpecializationDBO implements Serializable {

    private static final long serialVersionUID = 68156880291631769L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_specialization_id")
    public int id;

    @Column(name="specialization_name")
    public String specializationName;

    @Column(name = "record_status")
    public char recordStatus;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;
}
