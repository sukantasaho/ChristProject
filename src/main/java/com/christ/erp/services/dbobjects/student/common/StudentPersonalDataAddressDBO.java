package com.christ.erp.services.dbobjects.student.common;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dbobjects.common.ErpOccupationDBO;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.common.ErpSalutationDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_personal_data_address")
@Getter
@Setter
public class StudentPersonalDataAddressDBO  {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="student_personal_data_address_id")
	public Integer id;	
	
	@Column(name="current_address_line_1")
	public String  currentAddressLine1;
	
	@Column(name="current_address_line_2")
	public String currentAddressLine2;
	
	@ManyToOne
	@JoinColumn(name="current_country_id")
	public ErpCountryDBO erpCountryDBO;
	
	@ManyToOne
	@JoinColumn(name="current_state_id")
	public ErpStateDBO erpStateDBO;
	
	@Column(name="current_state_others")
	public String currentStateOthers;
	
	@ManyToOne
	@JoinColumn(name="current_city_id")
	public ErpCityDBO erpCityDBO;
	
	@Column(name="current_city_others")
	public String currentCityOthers;
	
	@Column(name="current_pincode")
	public String currentPincode;
	
	@Column(name="is_permanent_equals_current")
	public Boolean permanentEqualsCurrent;


	@Column(name="permanent_address_line_1")
	public String  permanentAddressLine1;
	
	@Column(name="permanent_address_line_2")
	public String permanentAddressLine2;
	
	@ManyToOne
	@JoinColumn(name="permanent_country_id")
	public ErpCountryDBO permanentCountryDBO;
	
	@ManyToOne
	@JoinColumn(name="permanent_state_id")
	public ErpStateDBO permanentStateDBO;
	
	@Column(name="permanent_state_others")
	public String permanentStateOthers;
	
	@ManyToOne
	@JoinColumn(name="permanent_city_id")
	public ErpCityDBO permanentCityDBO;
	
	@Column(name="permanent_city_others")
	public String permanentCityOthers;
	
	@Column(name="permanent_pincode")
	public String permanentPincode;
	
    @ManyToOne
    @JoinColumn(name="birth_country_id")
    public ErpCountryDBO birthCountry;

    @ManyToOne
    @JoinColumn(name="birth_state_id")
    public ErpStateDBO birthState;

    @Column(name="birth_state_others")
    public String birthStateOthers;

    @ManyToOne
    @JoinColumn(name="birth_city_id")
    public ErpCityDBO birthCity;

    @Column(name="birth_city_others")
    public String birthCityOthers;

    @Column(name="birth_pincode")
    public String birthPincode;
	
	@Column(name="father_mobile_no_country_code")
	public String fatherMobileNoCountryCode;
	
	@ManyToOne
	@JoinColumn(name="father_salutation_id")
	public ErpSalutationDBO fatherErpSalutationDBO;
	
	@Column(name="father_name")
	public String fatherName;
	
	@Column(name="father_mobile_no")
	public String fatherMobileNo;
	
	@Column(name="father_email")
	public String fatherEmail;
	
	@ManyToOne
	@JoinColumn(name="father_erp_qualification_level_id")
	public ErpQualificationLevelDBO fatherErpQualificationLevelDBO;
	
	@ManyToOne
	@JoinColumn(name="father_occupation_id")
	public ErpOccupationDBO fatherErpOccupationDBO;
	
	@Column(name="father_yearly_income_range_from")
	public BigDecimal fatherYearlyIncomeRangeFrom;
	
	@Column(name="father_yearly_income_range_to")
	public BigDecimal fatherYearlyIncomeRangeTo;
	
	@ManyToOne
	@JoinColumn(name="father_income_currency_id")
	public ErpCurrencyDBO fatherErpCurrencyDBO;
	
	@ManyToOne
	@JoinColumn(name="mother_salutation_id")
	public ErpSalutationDBO motherErpSalutationDBO;
	
	@Column(name="mother_name")
	public String motherName;
	
	@Column(name="mother_email")
	public String motherEmail;
	
	@Column(name="mother_mobile_no")
	public String motherMobileNo;
	
	@Column(name="mother_mobile_no_country_code")
	public String motherMobileNoCountryCode;
	
	@ManyToOne
	@JoinColumn(name="mother_erp_qualification_level_id")
	public ErpQualificationLevelDBO motherErpQualificationLevelDBO;
	
	@ManyToOne
	@JoinColumn(name="mother_occupation_id")
	public ErpOccupationDBO motherErpOccupationDBO;
	
	@Column(name="mother_yearly_income_range_from")
	public BigDecimal motherYearlyIncomeRangeFrom;
	
	@Column(name="mother_yearly_income_range_to")
	public BigDecimal motherYearlyIncomeRangeTo;
	
	@ManyToOne
	@JoinColumn(name="mother_income_currency_id")
	public ErpCurrencyDBO motherErpCurrencyDBO;
	
	@ManyToOne
	@JoinColumn(name="guardian_salutation_id")
	public ErpSalutationDBO guardianErpSalutationDBO;
	
	@Column(name="guardian_name")
	public String  guardianName;
	
	@Column(name="guardian_email")
	public String guardianEmail;
	
	@Column(name="guardian_mobile_no_country_code")
	public String guardianMobileNoCountryCode;
	
	@Column(name="guardian_mobile_no")
	public String guardianMobileNo;
	
    @Column(name="guardian_address_line_1")
    public String guardianAddressLine1;

    @Column(name="guardian_address_line_2")
    public String guardianAddressLine2;

    @ManyToOne
    @JoinColumn(name="guardian_country_id")
    public ErpCountryDBO guardianCountry;

    @ManyToOne
    @JoinColumn(name="guardian_state_id")
    public ErpStateDBO guardianState;

    @Column(name="guardian_state_others")
    public String guardianStateOthers;

    @ManyToOne
    @JoinColumn(name="guardian_city_id")
    public ErpCityDBO guardianCity;

    @Column(name="guardian_city_others")
    public String guardianCityOthers;

    @Column(name="guardian_pincode")
    public String guardianPincode;

	@Column(name = "family_annual_income")
	public BigDecimal familyAnnualIncome;

	@ManyToOne
	@JoinColumn(name="family_annual_income_currency_id")
	public ErpCurrencyDBO familyAnnualIncomeCurrency;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;

	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;

	@Column(name = "record_status")
	public Character recordStatus;

}
