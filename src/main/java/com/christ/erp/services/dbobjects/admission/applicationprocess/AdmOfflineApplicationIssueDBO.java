package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;

@Entity
@Table(name="adm_offline_application_issue")
public class AdmOfflineApplicationIssueDBO implements Serializable{
	
	private static final long serialVersionUID = 8078970318427532510L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_offline_application_issue_id")
    public int id;
	
	@ManyToOne
	@JoinColumn(name="erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_receipts_id")
	public ErpReceiptsDBO erpReceiptsDBO;
	
	@Column(name="appln_no")
	public String applnNo;
	
	@Column(name="applicant_name")
	public String applicantName;
	
	@Column(name="mobile_no_country_code")
	public String mobileNoCountryCode;
	
	@Column(name="mobile_no")
	public String mobileNo;
	
	@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;
 
    @Column(name="record_status")
    public char recordStatus;

}
