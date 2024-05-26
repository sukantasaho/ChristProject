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
@Table(name="erp_campus_department_mapping")
@Setter
@Getter
public class ErpCampusDepartmentMappingDBO implements Serializable {

    private static final long serialVersionUID = -131089970066597914L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_campus_department_mapping_id")
    public int id;

    @ManyToOne
    @JoinColumn(name="erp_campus_id")
    public ErpCampusDBO erpCampusDBO;

    @ManyToOne
    @JoinColumn(name="erp_department_id")
    public ErpDepartmentDBO erpDepartmentDBO;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
