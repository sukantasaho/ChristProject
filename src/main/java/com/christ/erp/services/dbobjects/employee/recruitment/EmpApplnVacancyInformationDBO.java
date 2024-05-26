package com.christ.erp.services.dbobjects.employee.recruitment;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="emp_appln_vacancy_information")
public class EmpApplnVacancyInformationDBO implements Serializable {

    private static final long serialVersionUID = 5101030111864791059L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_vacancy_information_id")
    public int id;

    @Column(name="vacancy_information_name")
    public String vacancyInformationName;

    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
