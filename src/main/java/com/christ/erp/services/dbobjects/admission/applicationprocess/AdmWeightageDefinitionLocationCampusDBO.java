package com.christ.erp.services.dbobjects.admission.applicationprocess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="adm_weightage_definition_location_campus")
@Getter
@Setter
public class AdmWeightageDefinitionLocationCampusDBO  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_weightage_definition_location_campus_id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name="adm_weightage_definition_id")
    private AdmWeightageDefinitionDBO admWeightageDefinitionDBO;

    @ManyToOne
    @JoinColumn(name="erp_campus_programme_mapping_id")
    private ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;

    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;

}
