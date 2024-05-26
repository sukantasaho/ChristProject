package com.christ.erp.services.dbobjects.employee.appraisal;

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "emp_appraisal_template")
public class EmpAppraisalTemplateDBO implements Serializable{

    private static final long serialVersionUID = 4208813888511303556L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_appraisal_template_id")
    public Integer id;

    @Column(name="template_name")
    public String templateName;

    @Column(name="template_code")
    public String templateCode;

    @ManyToOne
    @JoinColumn(name = "emp_employee_category_id")
    public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;

    @Column(name="appraisal_type")
    public String appraisalType;

    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;

    @OneToMany(mappedBy = "empAppraisalTemplateDBO", cascade = CascadeType.ALL)
    public Set<EmpAppraisalElementsDBO> elementsDBOSet = new HashSet<>();
}
