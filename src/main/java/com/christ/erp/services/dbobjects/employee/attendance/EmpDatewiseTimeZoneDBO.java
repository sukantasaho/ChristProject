package com.christ.erp.services.dbobjects.employee.attendance;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emp_datewise_time_zone")
@Setter
@Getter
public class EmpDatewiseTimeZoneDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_datewise_time_zone_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="emp_id")
    private EmpDBO empDBO;

    @ManyToOne
    @JoinColumn(name = "emp_time_zone_id")
    private EmpTimeZoneDBO empTimeZoneDBO;

    @Column(name = "time_zone_start_date")
    private LocalDate timeZoneStartDate;

    @Column(name = "time_zone_end_date")
    private LocalDate timeZoneEndDate;

    @Column(name = "number_of_days")
    private Integer numberOfDays;

    @Column(name = "description")
    private String description;

    @Column(name = "created_users_id")
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}