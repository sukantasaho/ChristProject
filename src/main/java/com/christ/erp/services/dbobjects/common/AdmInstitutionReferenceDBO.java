package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="adm_institution_reference")
@Getter
@Setter
public class AdmInstitutionReferenceDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_institution_reference_id")
    public int id;

    @Column(name="institution_reference_name")
    public String institutionReferenceName;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
