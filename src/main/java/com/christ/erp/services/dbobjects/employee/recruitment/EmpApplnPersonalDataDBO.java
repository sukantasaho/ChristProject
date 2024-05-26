package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.christ.erp.services.dbobjects.common.ErpBloodGroupDBO;
import com.christ.erp.services.dbobjects.common.ErpCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpDifferentlyAbledDBO;
import com.christ.erp.services.dbobjects.common.ErpMaritalStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpReligionDBO;
import com.christ.erp.services.dbobjects.common.ErpReservationCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_appln_personal_data")
@Setter
@Getter
public class EmpApplnPersonalDataDBO implements Serializable{

	private static final long serialVersionUID = -5362469824472798775L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_personal_data_id")
    public int empApplnPersonalDataId;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
 	
	@OneToOne(cascade = CascadeType.ALL)
 	@JoinColumn(name = "profile_photo_url_id")
 	public UrlAccessLinkDBO photoDocumentUrlDBO;
	
	@Column(name="father_name")
    public String fatherName;
	
	@Column(name="mother_name")
    public String motherName;
	
	@ManyToOne
	@JoinColumn(name="erp_country_id")
	public ErpCountryDBO erpCountryDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_marital_status_id")
	public ErpMaritalStatusDBO erpMaritalStatusDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_religion_id")
	public ErpReligionDBO erpReligionDBO;	
	
	@ManyToOne
	@JoinColumn(name="erp_blood_group_id")
	public ErpBloodGroupDBO erpBloodGroupDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_reservation_category_id")
	public ErpReservationCategoryDBO erpReservationCategoryDBO;
	
	@Column(name = "is_differently_abled")
	public Boolean isDifferentlyAbled;
	
	@Column(name="differently_abled_details")
    public String differentlyAbledDetails;
	
	@ManyToOne
	@JoinColumn(name="erp_differently_abled_id")
	public ErpDifferentlyAbledDBO erpDifferentlyAbledDBO;
	
	@Column(name="aadhar_no")
    public String aadharNo;
	
	@Column(name="passport_no")
    public String passportNo;
	
	@Column(name="alternate_no")
    public String alternateNo;
	
	@Column(name="current_address_line_1")
    public String currentAddressLine1;
	
	@Column(name="current_address_line_2")
    public String currentAddressLine2;
	
	@ManyToOne
	@JoinColumn(name="current_country_id")
	public ErpCountryDBO currentCountry;
	
	@ManyToOne
	@JoinColumn(name="current_state_id")
	public ErpStateDBO currentState;
	
	@Column(name="current_state_others")
    public String currentStateOthers;
	
	@ManyToOne
	@JoinColumn(name="current_city_id")
    public ErpCityDBO currentCity;
	
	@Column(name="current_city_others")
    public String currentCityOthers;
	
	@Column(name="current_pincode")
    public String currentPincode;
	
	@Column(name = "is_permanent_equals_current")
	public Boolean isPermanentEqualsCurrent;
	
	@Column(name="permanent_address_line_1")
    public String permanentAddressLine1;
	
	@Column(name="permanent_address_line_2")
    public String permanentAddressLine2;
	
	@ManyToOne
	@JoinColumn(name="permanent_country_id")
	public ErpCountryDBO permanentCountry;
	
	@ManyToOne
	@JoinColumn(name="permanent_state_id")
	public ErpStateDBO permanentState;
	
	@Column(name="permanent_state_others")
    public String permanentStateOthers;
	
	@ManyToOne
	@JoinColumn(name="permanent_city_id")
    public ErpCityDBO permanentCity;
	
	@Column(name="permanent_city_others")
    public String permanentCityOthers;
	
	@Column(name="permanent_pincode")
    public String permanentPincode;
	
	@Column(name="profile_photo_url")
    public String profilePhotoUrl;
	
	@ManyToOne
	@JoinColumn(name="erp_qualification_level_id")
	public ErpQualificationLevelDBO erpQualificationLevelDBO;
	
	@Column(name="orcid_no")
    public String orcidNo;
	
	@Column(name="scopus_no")
    public String scopusNo;
	
	@Column(name="uan_no")
    public String uanNo;
	
	@Column(name = "is_minority")
	public Boolean isMinority;

	@OneToOne(mappedBy = "empApplnPersonalDataDBO", cascade = CascadeType.ALL)
	public EmpAddtnlPersonalDataDBO empAddtnlPersonalDataDBO;

	@OneToMany(mappedBy = "empApplnPersonalDataDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpFamilyDetailsAddtnlDBO> empFamilyDetailsAddtnlDBOS = new HashSet<>();

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

	@Column(name="vidwan_no")
    public String vidwanNo;
	
	@Column(name="h_index_no")
    public Integer hIndexNo;
}
