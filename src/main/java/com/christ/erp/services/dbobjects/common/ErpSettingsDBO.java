package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="erp_settings")
public class ErpSettingsDBO implements Serializable {

    private static final long serialVersionUID = -6860944444362979911L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_settings_id")
    public int id;

    @Column(name="property_name")
    public String propertyName;

    @Column(name="property_value")
    public String propertyValue;

    @ManyToOne
    @JoinColumn(name="erp_campus_id")
    public ErpCampusDBO erpCampusDBO;

    @Column(name="property_description")
    public String propertyDescription;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
