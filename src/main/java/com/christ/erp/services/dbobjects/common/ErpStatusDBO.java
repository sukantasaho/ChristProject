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

@Getter
@Setter

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_status")
public class ErpStatusDBO implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_status_id")
    public Integer id;
	
	@Column(name = "status_code")
	public String statusCode;
	
	@Column(name = "status_description")
	public String statusDescription;
	
	@Column(name = "status_display_text")
	public String statusDisplayText;
	
	@Column(name = "is_reason_mandatory")
	public Boolean isReasonMandatory;
	
	@ManyToOne
	@JoinColumn(name = "erp_status_group_id")
	public ErpStatusGroup erpStatusGroup;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

	
	
	
}
