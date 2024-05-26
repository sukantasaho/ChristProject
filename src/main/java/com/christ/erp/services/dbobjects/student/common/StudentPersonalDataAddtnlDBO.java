package com.christ.erp.services.dbobjects.student.common;

import java.time.LocalDate;
import javax.persistence.*;

import com.christ.erp.services.dbobjects.common.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_personal_data_addtnl")
@Getter
@Setter
public class StudentPersonalDataAddtnlDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="student_personal_data_addtnl_id")
	public int id;	
	
	@ManyToOne
	@JoinColumn(name="erp_religion_id")
	public ErpReligionDBO erpReligionDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_country_id")
	public ErpCountryDBO erpCountryDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_blood_group_id")
	public ErpBloodGroupDBO erpBloodGroupDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_reservation_category_id")
	public ErpReservationCategoryDBO erpReservationCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_mother_tounge_id")
	public ErpMotherToungeDBO erpMotherToungeDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_differently_abled_id")
	public ErpDifferentlyAbledDBO erpDifferentlyAbledDBO;
	
	@Column(name="is_differently_abled")
	public Boolean  isDifferentlyAbled;
	
	@Column(name="is_aadhar_no_shared")
	public Boolean  aadharNoShared;
	
	@Column(name="aadhar_card_no")
	public String  aadharCardNo;
	
	@Column(name="is_aadhar_enrolled")
	public Boolean  aadharEnrolled;
	
	@Column(name="aadhar_enrolment_number")
	public String  aadharEnrolmentNumber;
	
	@Column(name="pio_or_oci")
	public String  pioOrOci;
	
	@Column(name="pio_or_oci_card_no")
	public String  pioOrOciCardNo;
	
	@Column(name="pio_or_oci_birth_place")
	public String  pioOrOciBirthPlace;
	
	@Column(name="applied_under_overseas_nri")
	public Boolean  appliedUnderOverseasNri;
	
	@Column(name="passport_no")
	public String  passportNo;
	
	@Column(name="passport_issued_date")
	public LocalDate  passportIssuedDate;
	
	@Column(name="passport_date_of_expiry")
	public LocalDate  passportDateOfExpiry;
	
    @ManyToOne
    @JoinColumn(name="passport_issued_country_id")
    private ErpCountryDBO passportIssuedCountry;
    
    @ManyToOne
    @JoinColumn(name="erp_second_language_id")
    private ErpSecondLanguageDBO erpSecondLanguageDBO;
    
	@Column(name="research_topic_details")
	public String  researchTopicDetails;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="birth_country_id")
	public ErpCountryDBO birthCountryDBO;

	@Column(name="birth_place")
	public String birthPlace;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="birth_state_id")
	public ErpStateDBO birthStateDBO;

	@Column(name="birth_state_others")
	public String birthStateOthers;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="birth_city_id")
	public ErpCityDBO birthCityDBO;

	@Column(name="birth_city_others")
	public String birthCityOthers;

	@Column(name="birth_pincode")
	public String birthPincode;

	@Column(name="sponsership_name")
	private String sponsershipName;

	@Column(name="sponsership_email")
	private String sponsershipEmail;

	@Column(name="sponsership_phone_number")
	private String sponsershipPhoneNumber;

	@Column(name = "sponsership_no_country_code")
	private Integer  sponsershipNoCountryCode;

	@ManyToOne
	@JoinColumn(name="sponsership_country_id")
	private ErpCountryDBO sponsershipCountry;
	
    @ManyToOne
    @JoinColumn(name="erp_institution_reference_id")
    private ErpInstitutionReferenceDBO erpInstitutionReferenceDBO;

	@ManyToOne
	@JoinColumn(name="adm_institution_reference_id")
	private AdmInstitutionReferenceDBO admInstitutionReferenceDBO;
	
	@Column(name="profile_photo_url")
	public String profilePhotoUrl;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;

	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;

	@Column(name = "record_status")
	public Character recordStatus;
}
