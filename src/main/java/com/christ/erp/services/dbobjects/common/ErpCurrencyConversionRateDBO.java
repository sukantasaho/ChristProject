package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "erp_currency_conversion_rate")
public class ErpCurrencyConversionRateDBO implements Serializable{

	private static final long serialVersionUID = -7925813369707437863L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_currency_conversion_rate_id")
	public int id;
	
	@Column(name="exchange_time")
	public LocalDateTime exchangeTime;
	
	@Column(name="exchange_rate", precision = 12, scale = 4)
	public BigDecimal  exchangeRate;
	
	@ManyToOne
	@JoinColumn(name="erp_currency_id")
	public ErpCurrencyDBO erpCurrencyDBO;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
