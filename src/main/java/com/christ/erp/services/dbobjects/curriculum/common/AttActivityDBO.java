package com.christ.erp.services.dbobjects.curriculum.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.AttTypeDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name =" att_activity")
@Getter
@Setter

public class AttActivityDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "att_activity_id")
	private int id;
	
	@Column(name ="activity_name")
	private String activityName;
	
	@ManyToOne
	@JoinColumn(name ="att_type_id")
	private AttTypeDBO attTypeDBO;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

}
