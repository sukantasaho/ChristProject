package com.christ.erp.services.dbobjects.common;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@ToString
@Table(name="erp_country")
public class ErpCountryDBOWebflux {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_country_id")
    private int id;

    @Column(name="country_name")
    private String countryName;

    @Column(name="nationality_name")
    private String nationalityName;

    @Column(name="phone_code")
    private String phone_code;

    @Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
