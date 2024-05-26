package com.christ.erp.services.dbobjects.employee.onboarding;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;


@SuppressWarnings("serial")
@Entity
@Table(name="emp_document_verification")
public class EmpDocumentVerificationDBO  implements Serializable
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_document_verification_id")
    public Integer id;	
	
	
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplyEntryDBO;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "empDocumentVerificationDBO")
	public Set<EmpDocumentVerificationDetailsDBO> empDocumentVerificationDetailsDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	
	@Column(name = "wait_for_document")
	public Boolean waitForDocument;
	

	@Column(name = "wait_remarks")
	public String remarks;
	
	
	@Column(name = "is_in_draft_mode")
	public Boolean isInDraftMode;
	
	
    @Column(name = "submission_due_date")
    public LocalDate submissionDueDate;
    
    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;
    
    @Column(name = "record_status")
    public Character recordStatus;
    
    

}
