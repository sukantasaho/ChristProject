package com.christ.erp.services.dbobjects.admission.settings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "student_appln_declarations_template")
@Getter
@Setter
public class StudentApplnDeclarationsTemplateDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_appln_declarations_template_id")
    private int id;

    @Column(name = "student_appln_declarations")
    private String studentApplnDeclarations;

    @Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
