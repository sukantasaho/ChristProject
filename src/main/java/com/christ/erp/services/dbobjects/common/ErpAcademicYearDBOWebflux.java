package com.christ.erp.services.dbobjects.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "erp_academic_year")
public class ErpAcademicYearDBOWebflux implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_academic_year_id")
    private Integer id;

    @Column(name = "academic_year")
    private Integer academicYear;

    @Column(name = "academic_year_name")
    private String academicYearName;

    @Column(name = "record_status")
    private char recordStatus;

    @Column(name = "is_current_academic_year")
    private Boolean isCurrentAcademicYear;

    @Column(name = "created_users_id")
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "academicYear",cascade = CascadeType.ALL)
    private Set<ErpAcademicYearDetailsDBOWebflux> academicYearDetails;
}
