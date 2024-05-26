package com.christ.erp.services.dbobjects.admission.settings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "adm_admission_type")
public class AdmAdmissionTypeDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_admission_type_id")
    public int id;

    @Column(name = "admission_type")
    public String admissionType;

    @Column(name = "admission_intake_year_number")
    public Integer admissionIntakeYearNumber;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
