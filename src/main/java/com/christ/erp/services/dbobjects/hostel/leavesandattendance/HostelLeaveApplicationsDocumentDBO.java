package com.christ.erp.services.dbobjects.hostel.leavesandattendance;

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

@Setter
@Getter

@Entity
@Table(name = "hostel_leave_applications_document")
public class HostelLeaveApplicationsDocumentDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_leave_applications_document_id")
	private int Id;
	
	@ManyToOne
	@JoinColumn(name = "hostel_leave_applications_id")
	private HostelLeaveApplicationsDBO hostelLeaveApplicationsDBO;
	
	@Column(name = "application_documents_url")
	private String applicationDocumentsUrl;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
}
