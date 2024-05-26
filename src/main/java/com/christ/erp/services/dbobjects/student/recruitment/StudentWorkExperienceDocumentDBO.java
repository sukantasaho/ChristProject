package com.christ.erp.services.dbobjects.student.recruitment;

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

@Entity
@Table(name="student_work_experience_document")
@Setter
@Getter
public class StudentWorkExperienceDocumentDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_work_experience_document_id")
    private int id;

    @ManyToOne
    @JoinColumn(name="student_work_experience_id")
    private StudentWorkExperienceDBO studentWorkExperienceDBO;

    @Column(name="experience_documents_url")
    private String experienceDocumentsUrl;

    @Column(name="created_users_id", updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
}
