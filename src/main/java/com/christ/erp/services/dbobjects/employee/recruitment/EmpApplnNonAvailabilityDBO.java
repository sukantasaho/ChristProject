package com.christ.erp.services.dbobjects.employee.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@Entity
@Table(name = "emp_appln_non_availability")
public class EmpApplnNonAvailabilityDBO implements Serializable {

    private static final long serialVersionUID = 689390497664984183L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_non_availability_id")
    public int empApplnNonAvailabilityId;

    @Column(name="non_availability_name")
    public String nonAvailabilityName;

    @Column(name = "is_reschedulable")
    public Boolean isReschedulable;

    @Column(name="interview_round")
    public Integer interviewRound;

    @Column(name = "is_final_selection")
    public Boolean isFinalSelection;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
