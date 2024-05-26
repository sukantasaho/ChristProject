package com.christ.erp.services.dbobjects.employee.salary;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="emp_pay_scale_grade_mapping")
@Setter
@Getter
public class EmpPayScaleGradeMappingDBO implements Serializable {

    private static final long serialVersionUID = 3271713454889626578L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pay_scale_grade_mapping_id")
    public Integer id;

    @Column(name="pay_scale_revised_year")
    public Integer payScaleRevisedYear;

    @ManyToOne
    @JoinColumn(name="emp_pay_scale_grade_id")
    public EmpPayScaleGradeDBO empPayScaleGradeDBO;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

    @OneToMany(mappedBy = "empPayScaleGradeMappingDBO",fetch = FetchType.LAZY)
    public Set<EmpPayScaleGradeMappingDetailDBO> empPayScaleGradeMappingDetailDBOSet;
    
}