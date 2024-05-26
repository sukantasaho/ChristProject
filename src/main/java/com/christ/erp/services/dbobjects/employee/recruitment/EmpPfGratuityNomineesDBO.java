package com.christ.erp.services.dbobjects.employee.recruitment;

import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emp_pf_gratuity_nominees")
@Setter
@Getter
public class EmpPfGratuityNomineesDBO implements Serializable {

    private static final long serialVersionUID = 5634971793119909373L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pf_gratuity_nominees_id")
    public int empPfGratuityNomineesId;

    @ManyToOne
    @JoinColumn(name="emp_job_details_id")
    public EmpJobDetailsDBO empJobDetailsDBO;

    @Column(name="nominee")
    public String nominee;

    @Column(name="nominee_address")
    public String nomineeAddress;

    @Column(name="nominee_relationship")
    public String nomineeRelationship;

    @Column(name="nominee_dob")
    public LocalDate nomineeDob;

    @Column(name="share_percentage")
    public BigDecimal sharePercentage;

    @Column(name="under18_guard_name")
    public String under18GuardName;

    @Column(name="under_18_guardian_address")
    public String under18GuardianAddress;

    @Column(name = "is_pf")
    public Boolean isPf;

    @Column(name = "is_gratuity")
    public Boolean isGratuity;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
