package com.christ.erp.services.dbobjects.curriculum.settings;

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

import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;

@Entity
@Table(name = "erp_department_mission_vision")
public class ErpDepartmentMissionVisionDBO implements Serializable {

	private static final long serialVersionUID = -3537787004254546081L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_department_mission_vision_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_department_id")
	public ErpDepartmentDBO   erpDepartmentDBO;
	
	@Column(name = "department_vision" , columnDefinition = "mediumtext" )
	public String departmentVision;
	
	@Column(name = "department_mission",columnDefinition = "mediumtext")
	public String departmentMission;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@OneToMany(mappedBy = "erpDepartmentMissionVisionDBO", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	public Set<ErpDepartmentMissionVisionDetailsDBO> erpDepartmentMissionVisionDetailsDBOSet = new HashSet<>();
}
