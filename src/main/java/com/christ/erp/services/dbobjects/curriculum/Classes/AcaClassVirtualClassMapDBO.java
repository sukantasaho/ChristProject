package com.christ.erp.services.dbobjects.curriculum.Classes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aca_class_virtual_class_map")
@Setter
@Getter
public class AcaClassVirtualClassMapDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_class_virtual_class_map_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "aca_base_class_id")
	private AcaClassDBO acaBaseClassDBO;
	
	@ManyToOne
	@JoinColumn(name = "aca_virtual_class_id")
	private AcaClassDBO acaVirtualClassDBO;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
}