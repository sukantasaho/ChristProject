package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;

@Entity
@Table(name="emp_appln_addtnl_info_entries")
public class EmpApplnAddtnlInfoEntriesDBO implements Serializable{
	
	private static final long serialVersionUID = -3127525293722728866L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_appln_addtnl_info_entries_id")
    public int empApplnAddtnlInfoEntriesId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_addtnl_info_parameter_id")
	public EmpApplnAddtnlInfoParameterDBO empApplnAddtnlInfoParameterDBO;
	
	@Column(name = "addtnl_info_value")
	public String addtnlInfoValue;
	
	@Column(name = "research_count")
	public Integer researchCount;

	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;
}
