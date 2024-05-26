package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_currency")
@Getter
@Setter
public class ErpCurrencyDBO implements Serializable{

	private static final long serialVersionUID = 2798641858153970392L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_currency_id")
	public int id;
	
	@Column(name="currency_name")
	public String currencyName;
	
	@Column(name="currency_code")
	public String currencyCode;
	
	@Column(name="currency_sub_division")
	public String currencySubDivision;
	
	@Column(name="currency_symbol")
	public String currencySymbol;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
