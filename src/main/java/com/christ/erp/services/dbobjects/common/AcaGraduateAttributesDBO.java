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
@Table(name="aca_graduate_attributes")
@Getter
@Setter
public class AcaGraduateAttributesDBO {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="aca_graduate_attributes_id")
    private int id;

    @Column(name="graduate_attributes")
    private String graduateAttributes;
    
    @ManyToOne
    @JoinColumn(name="aca_graduate_levels_id")
    private AcaGraduateLevelsDBO acaGraduateLevelsDBO;
    
    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private Character recordStatus;
}
