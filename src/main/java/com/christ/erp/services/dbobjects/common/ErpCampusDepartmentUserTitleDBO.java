package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpTitleDBO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@SuppressWarnings("serial")
@Entity
@Table(name="erp_campus_department_user_title")
public class ErpCampusDepartmentUserTitleDBO implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_campus_department_user_title_id")
    public int id;

    @ManyToOne
    @JoinColumn(name="erp_campus_department_mapping_id")
    public ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;

    @ManyToOne
    @JoinColumn(name="erp_title_id")
    public EmpTitleDBO erpTitleDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_users_id")
    public ErpUsersDBO erpUsersDBO;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}