package com.christ.erp.services.dbobjects.employee.salary;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name="emp_pay_scale_level")

public class EmpPayScaleLevelDBO implements Serializable {

    private static final long serialVersionUID = 4936113003904273224L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pay_scale_level_id")
    private Integer id;
    
    @Column(name="emp_pay_scale_level")
    private String empPayScaleLevel;
    
    @Column(name ="level_order")
    private Integer levelOrder;
    
    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
