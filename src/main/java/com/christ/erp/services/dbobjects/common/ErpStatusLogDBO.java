package com.christ.erp.services.dbobjects.common;

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
@Table(name = "erp_status_log")
@Getter
@Setter

public class ErpStatusLogDBO {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_status_log_id")
	private int id;
	
	@Column(name="entry_id")
	private Integer entryId;
	
	@ManyToOne
	@JoinColumn(name="erp_status_id")
	private ErpStatusDBO erpStatusDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_status_reason_id")
	private ErpStatusReasonsDBO erpStatusReasonsDBO;
	
	@Column(name= "status_reason_others")
	private String statusReasonOthers;
	
	@Column(name = "status_comments")
	private String statusComments;
	
	@Column(name="created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name="modified_users_id")
	private Integer modifiedUsersId;	
	
	@Column(name="record_status")
	private char recordStatus;
	
}
