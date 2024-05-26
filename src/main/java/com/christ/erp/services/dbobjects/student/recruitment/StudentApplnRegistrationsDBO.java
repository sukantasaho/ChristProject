package com.christ.erp.services.dbobjects.student.recruitment;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="student_appln_registrations")
@Setter
@Getter
public class StudentApplnRegistrationsDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_appln_registrations_id")
    private int id;

    @Column(name="applicant_name")
    private String applicantName;

    @Column(name="email")
    private String email;

    @Column(name="passwd")
    private String passwd;

    @Column(name="ip_address")
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="old_christ_student_id")
    private StudentDBO studentDBO;

    @Column(name="applicant_dob")
    private LocalDate applicantDob;

    @Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}