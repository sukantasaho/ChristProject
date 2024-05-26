package com.christ.erp.services.dbobjects.employee.onboarding;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "emp_document_checklist_sub")
public class EmpDocumentChecklistSubDBO implements Serializable {

	private static final long serialVersionUID = 5559616709971610294L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_document_checklist_sub_id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "emp_document_checklist_main_id")
    public EmpDocumentChecklistMainDBO empDocumentChecklistMainDBO;

    @Column(name = "document_checklist_sub_name")
    public String documentChecklistSubName;

    @Column(name = "document_checklist_sub_display_order")
    public Integer documentChecklistSubDisplayOrder;

    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
