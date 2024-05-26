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
@Table(name = "erp_programme_level")
@Getter
@Setter
public class ErpProgrammeLevelDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_programme_level_id")
    public Integer id;

    @Column(name = "programme_level")
    public String programmeLevel;

    @Column(name = "record_status")
    public char recordStatus;

    @Column(name = "created_users_id")
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;
}
