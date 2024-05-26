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
@Table(name="erp_country")
@Getter
@Setter
public class ErpCountryDBO  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_country_id")
    public int id;

    @Column(name="country_name")
    public String countryName;

    @Column(name="nationality_name")
    public String nationalityName;

    @Column(name="phone_code")
    public String phoneCode;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public Character recordStatus;
}
