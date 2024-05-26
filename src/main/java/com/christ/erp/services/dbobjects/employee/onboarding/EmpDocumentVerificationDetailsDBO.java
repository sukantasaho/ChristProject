package com.christ.erp.services.dbobjects.employee.onboarding;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="emp_document_verification_details")
public class EmpDocumentVerificationDetailsDBO implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_document_verification_details_id")
    public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_document_verification_id")
	public EmpDocumentVerificationDBO empDocumentVerificationDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_document_checklist_sub_id")
	public EmpDocumentChecklistSubDBO empDocumentChecklistSubDBO;
	
	@Column(name = "verification_status")
	public String verificationStatus;
	
	@Column(name = "verification_remarks")
	public String verificationRemarks;
	
	@Column(name = "created_users_id",updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	    
    @Column(name = "record_status")
	public Character recordStatus;
}
