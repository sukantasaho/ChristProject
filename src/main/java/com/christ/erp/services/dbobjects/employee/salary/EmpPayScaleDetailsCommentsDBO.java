package com.christ.erp.services.dbobjects.employee.salary;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "emp_pay_scale_details_comments")
public class EmpPayScaleDetailsCommentsDBO implements Serializable {
    private static final long serialVersionUID = -3260453259379570838L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_pay_scale_details_comments_id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    public EmpDBO empDBO;

    @ManyToOne
    @JoinColumn(name = "emp_pay_scale_details_id")
    public EmpPayScaleDetailsDBO empPayScaleDetailsDBO;

    @Column(name="pay_scale_comments")
    public String payScaleComments;

    @Column(name="created_users_id")
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public Character recordStatus;
}
