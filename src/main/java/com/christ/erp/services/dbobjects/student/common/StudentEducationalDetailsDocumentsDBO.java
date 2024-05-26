package com.christ.erp.services.dbobjects.student.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name="student_educational_details_documents")
public class StudentEducationalDetailsDocumentsDBO implements Serializable {

    private static final long serialVersionUID = 3227015472622000902L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_educational_details_documents_id")
    private int id;

    @ManyToOne
    @JoinColumn(name="student_educational_details_id")
    private StudentEducationalDetailsDBO studentEducationalDetailsDBO;

    @Column(name="documents_url")
    private String documentsUrl;

    @Column(name="created_users_id", updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
