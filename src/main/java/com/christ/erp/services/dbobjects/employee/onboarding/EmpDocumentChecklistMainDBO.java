package com.christ.erp.services.dbobjects.employee.onboarding;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="emp_document_checklist_main")
public class EmpDocumentChecklistMainDBO implements Serializable {
	
	private static final long serialVersionUID = -3119302690527524038L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_document_checklist_main_id")
    public Integer id;

    @Column(name = "document_checklist_name")
    public String documentChecklistName;
    
    @Column(name = "document_checklist_display_order")
    public Integer documentChecklistDisplayOrder;

    @Column(name = "is_document_addl_checklist_foreign_national")
    public Boolean isForeignNationalDocumentChecklist;

    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empDocumentChecklistMainDBO", cascade = CascadeType.ALL)
    public Set<EmpDocumentChecklistSubDBO> subChecklist = new HashSet<>();
}
