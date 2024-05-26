package com.christ.erp.services.dbobjects.employee.salary;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter

@Entity
@Table(name="emp_pay_scale_grade_mapping_detail")
public class EmpPayScaleGradeMappingDetailDBO implements Serializable {

    private static final long serialVersionUID = 4936113003904273224L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pay_scale_grade_mapping_detail_id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name="emp_pay_scale_grade_mapping_id")
    public EmpPayScaleGradeMappingDBO empPayScaleGradeMappingDBO;

//    @Column(name="pay_scale_level")
//    public String payScaleLevel;

    @ManyToOne
    @JoinColumn(name="emp_pay_scale_level_id")
    public EmpPayScaleLevelDBO empPayScaleLevelDBO;
    
    @Column(name="pay_scale")
    public String payScale;

    @Column(name="pay_scale_display_order")
    public Integer payScaleDisplayOrder;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
}