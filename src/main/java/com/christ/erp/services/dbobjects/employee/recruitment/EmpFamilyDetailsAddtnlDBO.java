package com.christ.erp.services.dbobjects.employee.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "emp_family_details_addtnl")
public class EmpFamilyDetailsAddtnlDBO implements Serializable {

    private static final long serialVersionUID = -7156981522022103360L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_family_details_addtnl_id")
    public int empFamilyDetailsAddtnlId;

    @ManyToOne
    @JoinColumn(name="emp_appln_personal_data_id")
    public EmpApplnPersonalDataDBO empApplnPersonalDataDBO;

    @ManyToOne
    @JoinColumn(name="emp_personal_data_id")
    public EmpPersonalDataDBO empPersonalDataDBO;

    @Column(name="relationship")
    public String relationship;

    @Column(name="dependent_name")
    public String dependentName;

    @Column(name="dependent_dob")
    public LocalDate dependentDob;

    @Column(name="dependent_qualification")
    public String dependentQualification;

    @Column(name="dependent_profession")
    public String dependentProfession;

    @Column(name="other_dependent_relationship")
    public String otherDependentRelationship;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
