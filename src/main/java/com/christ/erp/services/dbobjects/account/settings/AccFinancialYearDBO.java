package com.christ.erp.services.dbobjects.account.settings;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="acc_financial_year")
public class AccFinancialYearDBO implements Serializable {
	
	private static final long serialVersionUID = -3301444841780051361L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_financial_year_id")
	public int id;
	
	@Column(name="financial_year")
	public String financialYear;
	
	@Column(name="financial_year_start_date")
	public LocalDate financialYearStartDate;
	
	@Column(name="financial_year_end_date")
	public LocalDate financialYearEndDate;
	
	@Column(name="is_current_for_fee")
	public Boolean isCurrentForFee;
	
	@Column(name="is_current_for_cash_collection")
	public Boolean isCurrentForCashCollection;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
