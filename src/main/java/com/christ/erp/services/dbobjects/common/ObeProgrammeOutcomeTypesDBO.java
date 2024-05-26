package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "obe_programme_outcome_types")
@Getter
@Setter
public class ObeProgrammeOutcomeTypesDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="obe_programme_outcome_types_id")
    private int id;
    
    @Column(name="obe_programme_outcome_types")
    private String obeProgrammeOutcomeTypes;
    
    @Column(name="obe_programme_outcome_code")
    private String obeProgrammeOutcomeCode;
    
    @Column(name = "record_status")
    private char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
}
