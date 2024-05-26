package com.christ.erp.services.dbobjects.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "erp_academic_year_detail")
public class ErpAcademicYearDetailsDBOWebflux implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_academic_year_detail_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "erp_campus_id")
    private ErpCampusDBO campus;

    @ManyToOne
    @JoinColumn(name = "erp_academic_year_id")
    private ErpAcademicYearDBOWebflux academicYear;

    @Column(name = "is_academic_year_current")
    private Boolean isAcademicYearCurrent;

    @Column(name = "academic_year_start_date")
    private LocalDate academicYearStartDate;

    @Column(name = "academic_year_end_date")
    private LocalDate academicYearEndDate;

    @Column(name = "record_status")
    private char recordStatus;

    @Column(name = "created_users_id")
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
}
