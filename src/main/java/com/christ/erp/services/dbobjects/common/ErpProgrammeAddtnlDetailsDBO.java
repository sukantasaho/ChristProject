package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_programme_addtnl_details")
@Getter
@Setter
public class ErpProgrammeAddtnlDetailsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_programme_addtnl_details_id ")
	private int id;

	@ManyToOne
	@JoinColumn(name = "erp_programme_id")
	private ErpProgrammeDBO erpProgrammeDBO;

	@Column(name = "changed_from_year")
	private Integer changedFromYear;

	@Column(name = "changed_to_year")
	private Integer changedToYear ;

	@Column(name = "programme_name")
	private String  programmeName;

	@Column(name = "programme_code")
	private String  programmeCode;

	@Column(name = "programme_short_name")
	private String  programmeShortName;

	@ManyToOne
	@JoinColumn(name = "erp_programme_degree_id")
	private ErpProgrammeDegreeDBO erpProgrammeDegreeDBO;

	@ManyToOne
	@JoinColumn(name = "erp_deanery_id")
	private ErpDeaneryDBO erpDeaneryDBO;

	@ManyToOne
	@JoinColumn(name = "coordinating_department_id")
	private ErpDepartmentDBO coordinatingDepartment;

	@Column(name = "search_keyword")
	private String  searchKeyword;

	@Column(name = "mode")
	private String  mode;

//	@Column(name = "inter_disciplinary_or_innovative")
//	private String  interDisciplinaryOrInnovative;

//	@Column(name = "value_added_or_career_oriented")
//	private String  valueAddedOrCareerOriented;

	@Column(name = "is_multiple_major")
	private Boolean isMultipleMajor;

//	@Column(name = "is_having_pso")
//	private Boolean isHavingPso;

//	@Column(name = "is_having_peo")
//	private Boolean isHavingPeo;

	@Column(name = "is_implemented_CBCS")
	private Boolean isImplementedCBCS;

	@Column(name = "CBCS_implemented_year")
	private Integer cbcsImplementedYear;

	@Column(name = "is_implemented_ECS")
	private Boolean  isImplementedECS;

	@Column(name = "ECS_implemented_year")
	private Integer ecsImplementedYear;

	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;

	@Column(name = "programme_display_name")
	private String  programmeDisplayname;
}
