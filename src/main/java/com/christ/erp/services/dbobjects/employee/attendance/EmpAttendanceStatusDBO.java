package com.christ.erp.services.dbobjects.employee.attendance;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "emp_attendance_status")
public class EmpAttendanceStatusDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_attendance_status_id")
    private int id;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "status_description")
    private  String statusDescription;

    @Column(name = "status_color_code")
    private  String statusColorCode;

    @Column(name = "is_label")
    private Boolean isLabel;

    @Column(name = "is_color_change")
    private  Boolean isColorChange;

    @Column(name = "created_users_id")
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
