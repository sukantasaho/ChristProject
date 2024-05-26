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
@Table(name = "erp_programme_peo_mission_matrix")
@Getter
@Setter
public class ErpProgrammePeoMissionMatrixDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_programme_peo_mission_matrix_id")
    private int id;

    @Column(name = "intrinsic_value")
   	private Integer intrinsicValue;
    
    @Column(name = "rationale_for_mapping")
   	private String rationaleForMapping;
   	
   	@Column(name = "created_users_id",updatable=false)
   	private Integer createdUsersId;
   	
   	@Column(name = "modified_users_id")
   	private Integer modifiedUsersId;
   	
   	@Column(name = "record_status")
   	private char recordStatus;
   	
   	@ManyToOne
   	@JoinColumn(name= "obe_programme_outcome_details_id")
   	private ObeProgrammeOutcomeDetailsDBO obeProgrammeOutcomeDetailsDBO;
   	
   	@ManyToOne
   	@JoinColumn(name= "erp_department_mission_vision_details_id")
   	private ErpDepartmentMissionVisionDetailsDBO erpDepartmentMissionVisionDetailsDBO;
}
