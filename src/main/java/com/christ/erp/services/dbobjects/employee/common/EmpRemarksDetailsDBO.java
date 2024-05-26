package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

@Getter
@Setter
@Entity
@Table(name = "emp_remarks_details")
public class EmpRemarksDetailsDBO implements Serializable {

	private static final long serialVersionUID = -4709727046335441790L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_remarks_details_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	public EmpDBO empDBO;
	
	@Column(name = "remarks_date")
	public LocalDate remarksDate;

	@Column(name = "remarks_details")
	public String remarksDetails;
	
	@Column(name = "remarks_upload_url")
	public String remarksUploadUrl;
	
	@Column(name = "remarks_ref_no")
	public String remarksRefNo;
	
	@Column(name = "is_for_office_use")
	public boolean isForOfficeUse;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_Status")
	public char recordStatus;
	
	@OneToOne(cascade = CascadeType.ALL)
 	@JoinColumn(name = "remarks_upload_url_id")
 	public UrlAccessLinkDBO remarksUploadUrlDBO;

}
