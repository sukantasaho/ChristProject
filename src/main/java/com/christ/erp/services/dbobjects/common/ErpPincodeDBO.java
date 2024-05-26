package com.christ.erp.services.dbobjects.common;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Table(name="erp_pincode")
@Setter
@Getter
public class ErpPincodeDBO implements Serializable {

    private static final long serialVersionUID = -1140899711312149866L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_pincode_id")
    public int id;

    @Column(name="pincode")
    public String pincode;

    @Column(name="postoffice")
    public String postoffice;

    @Column(name="description")
    public String description;

    @Column(name="branch_type")
    public String branchType;

    @Column(name="delivery_status")
    public String deliveryStatus;

    @Column(name="circle")
    public String circle;

    @Column(name="district")
    public String district;

    @Column(name="division")
    public String division;

    @Column(name="region")
    public String region;

    @Column(name="block")
    public String block;

    @Column(name="state")
    public String state;

    @Column(name="country")
    public String country;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @ManyToOne
    @JoinColumn(name="erp_city_id")
    public ErpCityDBO erpCityDBO;
    
    @Column(name="latitude")
    public String latitude;
    
    @Column(name="longitude")
    public String longitude;
    
    @Column(name="erp_country_id")
    public Integer erp_country_id;
    
}
