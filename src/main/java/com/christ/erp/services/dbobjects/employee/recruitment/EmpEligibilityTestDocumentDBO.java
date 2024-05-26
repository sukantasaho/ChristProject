package com.christ.erp.services.dbobjects.employee.recruitment;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_eligibility_test_document")
@Setter
@Getter
public class EmpEligibilityTestDocumentDBO {
	 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_eligibility_test_document_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "emp_eligibility_test_id")
	private EmpEligibilityTestDBO empEligibilityTestDBO;
	
	@Column(name = "eligibility_document_url")
	private String eligibilityDocumentUrl;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "eligibility_document_url_id")
	public UrlAccessLinkDBO eligibilityDocumentUrlDBO;
}
