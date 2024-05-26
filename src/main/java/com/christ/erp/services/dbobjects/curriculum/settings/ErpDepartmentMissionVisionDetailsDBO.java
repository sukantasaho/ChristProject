package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
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
@Table(name = "erp_department_mission_vision_details")
@Getter
@Setter
public class ErpDepartmentMissionVisionDetailsDBO  implements Serializable{

	private static final long serialVersionUID = -4742240781003300034L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_department_mission_vision_details_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_department_mission_vision_id")
	public ErpDepartmentMissionVisionDBO  erpDepartmentMissionVisionDBO;
	
	@Column(name = "mission_reference_number")
	public String missionReferenceNumber;
	
	@Column(name = "mission_category")
	public String missionCategory;
	
	@Column(name = "mission_statement")
	public String missionStatement;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;

}
