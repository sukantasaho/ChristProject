package com.christ.erp.services.dbobjects.employee.salary;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;


@Entity
@Data
@Table(name="emp_pay_scale_components")
public class EmpPayScaleComponentsDBOWebFlux {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pay_scale_components_id")
    private Integer id;

    @Column(name="salary_component_name")
    private String salaryComponentName;

    @Column(name="salary_component_short_name")
    private String salaryComponentShortName;

    @Column(name="pay_scale_type")
    private String payScaleType;

    @Column(name="is_component_basic")
    private Boolean isComponentBasic;

    @Column(name="salary_component_display_order")
    private Integer salaryComponentDisplayOrder;

    @Column(name="is_caculation_type_percentage")
    private Boolean isCalculationTypePercentage;

    @Column(name="percentage")
    private BigDecimal percentage;

    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private Character recordStatus;
}
