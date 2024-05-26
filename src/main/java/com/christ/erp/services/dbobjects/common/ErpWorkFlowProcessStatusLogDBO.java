package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import java.time.LocalDateTime;

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

@Getter
@Setter

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_work_flow_process_status_log")
public class ErpWorkFlowProcessStatusLogDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_work_flow_process_status_log_id")
	public Integer id;
	
	@Column(name="entry_id")
	public Integer entryId;
	
	@ManyToOne
	@JoinColumn(name="erp_work_flow_process_id")
	public ErpWorkFlowProcessDBO erpWorkFlowProcessDBO;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
	
	@Column(name="created_time", insertable = false)
	public LocalDateTime createdTime;

}
