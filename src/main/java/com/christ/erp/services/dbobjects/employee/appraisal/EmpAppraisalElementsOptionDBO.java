package com.christ.erp.services.dbobjects.employee.appraisal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "emp_appraisal_elements_option")
public class EmpAppraisalElementsOptionDBO implements Serializable{

    private static final long serialVersionUID = -6991167945571026092L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_appraisal_elements_option_id")
    public Integer id;

    @Column(name="option_group_name")
    public String optionGroupName;

    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
