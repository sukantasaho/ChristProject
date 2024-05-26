package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
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

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_work_flow_process")
@Setter
@Getter
public class ErpWorkFlowProcessDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_work_flow_process_id")
	public Integer id;

	@Column(name="process_code")
	public String processCode;
	
	@Column(name="process_description")
	public String processDescription;
	
	@Column(name="applicant_status_display_text")
	public String applicantStatusDisplayText;
	
	@Column(name="application_status_display_text")
	public String applicationStatusDisplayText;
	
	@ManyToOne
	@JoinColumn(name="erp_work_flow_process_group_id")
	public ErpWorkFlowProcessGroupDBO erpWorkFlowProcessGroupDBO;
	
	@Column(name="process_order")
	public Integer processOrder;
	
	@Column(name="screen_name")
	public String screenName;
	
	@Column(name="is_process_commence")
	public Boolean isProcessCommence;
	
	@Column(name="is_process_termination")
	public Boolean isProcessTermination;
	
//	@Column(name="notification_function_name") 
//	public String notificationFunctionName;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
}
