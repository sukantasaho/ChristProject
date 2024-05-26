package com.christ.erp.services.dbobjects.employee.attendance;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "emp_time_zone_employeewise")
public class EmpTimeZoneEmployeewiseDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_time_zone_employeewise_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "emp_time_zone_id")
    private EmpTimeZoneDBO empTimeZoneDBO;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private EmpDBO empDBO;

    @Column(name = "created_users_id")
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
