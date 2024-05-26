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
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;

@Entity
@Table(name="erp_number_generation")
public class ErpNumberGenerationDBO implements Serializable{ 
	
	private static final long serialVersionUID = -8664334933519801010L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_number_generation_id")
    public int id;
	
	@ManyToOne
	@JoinColumn(name="acc_financial_year_id")
	public AccFinancialYearDBO accFinancialYearDBO;
	
	@Column(name="number_type")
	public String numberType;
	
	@Column(name="number_prefix")
	public String numberPrefix;
	
	@Column(name="number_start")
	public Integer numberStart;
	
	@Column(name="current_number")
	public Integer currentNumber;
	
	@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;
 
    @Column(name="record_status")
    public char recordStatus;

}
