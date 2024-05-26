package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Table(name = "erp_qualification_level")
@Getter
@Setter
public class ErpQualificationLevelDBO implements Serializable{

	private static final long serialVersionUID = 3536124635087489812L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_qualification_level_id")
    public int id;

	@Column(name="qualification_level_name")
    public String qualificationLevelName;
	
	@Column(name = "qualification_level_degree_order")
	public Integer qualificationLevelDegreeOrder;
	
	@Column(name="qualification_level_code")
    public String qualificationLevelCode;
		
	@Column(name = "is_add_more")
	public Boolean isAddMore;
	
	@Column(name = "is_mandatory")
	public Boolean isMandatory;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

}
