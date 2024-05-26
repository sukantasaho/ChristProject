package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "emp_addtnl_personal_data")
public class EmpAddtnlPersonalDataDBO implements Serializable {

    private static final long serialVersionUID = -5824781770918716916L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_addtnl_personal_data_id")
    public int empAddtnlPersonalDataId;

    @OneToOne
    @JoinColumn(name="emp_appln_personal_data_id")
    public EmpApplnPersonalDataDBO empApplnPersonalDataDBO;
    
    @OneToOne
    @JoinColumn(name="emp_personal_data_id")
    public EmpPersonalDataDBO empPersonalDataDBO;

    @Column(name="pan_no")
    public String panNo;

    @Column(name="four_wheeler_no")
    public String fourWheelerNo;
    
    @OneToOne(cascade = CascadeType.ALL)
  	@JoinColumn(name = "four_wheeler_document_url_id")
  	public UrlAccessLinkDBO fourWheelerDocumentUrlDBO;

    @Column(name="two_wheeler_no")
    public String twoWheelerNo;
    
    @OneToOne(cascade = CascadeType.ALL)
  	@JoinColumn(name = "two_wheeler_document_url_id")
  	public UrlAccessLinkDBO twoWheelerDocumentUrlDBO;

    @Column(name = "is_aadhar_available")
    public Boolean isAadharAvailable;

    @Column(name="aadhar_no")
    public String aadharNo;

    @Column(name = "is_aadhar_enrolled")
    public Boolean isAadharEnrolled;

    @Column(name="aadhar_enrolled_no")
    public String aadharEnrolledNo;

    @Column(name="emergency_contact_name")
    public String emergencyContactName;

    @Column(name="emergency_contact_address")
    public String emergencyContactAddress;

    @Column(name="emergency_contact_relatonship")
    public String emergencyContactRelationship;

    @Column(name="emergency_mobile_no")
    public String emergencyMobileNo;

    @Column(name="emergency_contact_home")
    public String emergencyContactHome;

    @Column(name="emergency_contact_work")
    public String emergencyContactWork;

    @Column(name="passport_no")
    public String passportNo;

    @Column(name="passport_issued_date")
    public LocalDate passportIssuedDate;

    @Column(name="passport_status")
    public String passportStatus;

    @Column(name="passport_date_of_expiry")
    public LocalDate passportDateOfExpiry;

    @Column(name="passport_comments")
    public String passportComments;

    @Column(name="passport_issued_place")
    public String passportIssuedPlace;

    @Column(name="visa_no")
    public String visaNo;

    @Column(name="visa_issued_date")
    public LocalDate visaIssuedDate;

    @Column(name="visa_status")
    public String visaStatus;

    @Column(name="visa_date_of_expiry")
    public LocalDate visaDateOfExpiry;

    @Column(name="visa_comments")
    public String visaComments;

    @Column(name="frro_no")
    public String frroNo;

    @Column(name="frro_issued_date")
    public LocalDate frroIssuedDate;

    @Column(name="frro_status")
    public String frroStatus;

    @Column(name="frro_date_of_expiry")
    public LocalDate frroDateOfExpiry;

    @Column(name="frro_comments")
    public String frroComments;

    @Column(name="family_background_brief")
    public String familyBackgroundBrief;

    @Column(name="visa_upload_url")
    public String visaUploadUrl;
    
    @OneToOne(cascade = CascadeType.ALL)
 	@JoinColumn(name = "visa_upload_url_id")
 	public UrlAccessLinkDBO visaUploadUrlDBO;

    @Column(name="passport_upload_url")
    public String passportUploadUrl;
    
	@OneToOne(cascade = CascadeType.ALL)
 	@JoinColumn(name = "passport_upload_url_id")
 	public UrlAccessLinkDBO passportUploadUrlDBO;

    @Column(name="frro_upload_url")
    public String frroUploadUrl;
   
    @OneToOne(cascade = CascadeType.ALL)
 	@JoinColumn(name = "FRRO_upload_url_id")
 	public UrlAccessLinkDBO frroUploadUrlDBO;
    
    
    @Column(name="four_wheeler_document_url")
    public String fourWheelerDocumentUrl;

    @Column(name="two_wheeler_document_url")
    public String twoWheelerDocumentUrl;
    
    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @Column(name = "adhar_upload_url")
    private String adharUploadUrl;
    
    @OneToOne(cascade = CascadeType.ALL)
   	@JoinColumn(name = "adhar_upload_url_id")
   	public UrlAccessLinkDBO adharUploadUrlDBO;
    
    @Column(name = "pan_upload_url")
    private String panUploadUrl;
    
    @OneToOne(cascade = CascadeType.ALL)
 	@JoinColumn(name = "pan_upload_url_id")
 	public UrlAccessLinkDBO panUploadUrlDBO;
}