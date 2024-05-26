package com.christ.erp.services.dbobjects.employee.recruitment;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@Entity
@Table(name="emp_appln_registrations")
public class EmpApplnRegistrationsDBO implements Serializable {

    private static final long serialVersionUID = 8276578504658063500L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_registrations_id")
    public int id;

    @Column(name="applicant_name")
    public String applicantName;

    @Column(name="email")
    public String email;

    @Column(name="passwd")
    public String passwd;

    @Column(name="orcid_no")
    public String orcid_no;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
