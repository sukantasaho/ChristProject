package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "emp_interview_university_externals")
public class EmpInterviewUniversityExternalsDBO implements Serializable {
	private static final long serialVersionUID = 3214733828059071088L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_interview_university_externals_id")
	public Integer id;
	
	@Column(name="panelist_name")
	public String panelName;
	
	@Column(name="panelist_email")
	public String panelEmail;
	
	@Column(name = "panelist_mobile_no_country_code")
	public String panelMblCountryCode;
	
	@Column(name = "panelist_mobile_no")
	public String panelMblNo;
	
	@Column(name = "panelist_document_url")
	public String panelDocumentUrl;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "panelist_document_url_id")
	public UrlAccessLinkDBO panelistDocumentUrlDBO;
	
	@Column (name ="created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column (name ="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column (name = "record_status")
	public char recordStatus;

}
