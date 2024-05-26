package com.christ.erp.services.dbobjects.employee.recruitment;

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@Table(name = "emp_appln_addtnl_info_heading")
public class EmpApplnAddtnlInfoHeadingDBO implements Serializable {
	
	private static final long serialVersionUID = -169034721720795155L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_appln_addtnl_info_heading_id")
	public Integer id;

	@Column(name = "addtnl_info_heading_name")
	public String addtnlInfoHeadingName;

	@ManyToOne
	@JoinColumn(name = "emp_employee_category_id")
	public EmpEmployeeCategoryDBO empEmployeeCategoryId;

	@Column(name = "heading_display_order")
	public Integer headingDisplayOrder;
	
	@Column(name = "is_type_research")
	public Boolean isTypeResearch;

	@Column(name = "created_users_id")
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empApplnAddtnlInfoHeading",cascade=CascadeType.ALL)
	public Set<EmpApplnAddtnlInfoParameterDBO> empApplnAddtnlInfoParameterMap = new HashSet<>();
}