package com.christ.erp.services.dbobjects.account.settings;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="acc_gst_percentage")
public class AccGSTPercentageDBO implements Serializable{
	
	private static final long serialVersionUID = 1406879379192453743L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_gst_percentage_id")
	public int id;
	
	@Column(name="is_current")
	public Boolean isCurrent;
	
	@Column(name="SGST_percentage", precision = 5, scale = 2)
	public BigDecimal  SGSTPercentage;
	
	@Column(name="IGST_percentage", precision = 5, scale = 2)
	public BigDecimal  IGSTPercentage;
	
	@Column(name="CGST_percentage", precision = 5, scale = 2)
	public BigDecimal  CGSTPercentage;
	
	@Column(name="applicable_from_date")
	public LocalDate applicableFromDate;

	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;
}
