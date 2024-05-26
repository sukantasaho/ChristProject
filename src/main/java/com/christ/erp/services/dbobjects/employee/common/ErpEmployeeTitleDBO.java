package com.christ.erp.services.dbobjects.employee.common;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="emp_title")
@Setter
@Getter
public class ErpEmployeeTitleDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_title_id")
    public  Integer id;

    @Column(name="title_name")
    public String titleName;

    @Column(name = "created_users_id")
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}