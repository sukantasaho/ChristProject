package com.christ.erp.services.dbobjects.admission.applicationprocess;

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

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="adm_weightage_definition")
@Getter
@Setter
public class AdmWeightageDefinitionDBO {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_weightage_definition_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="erp_academic_year_id")
    private ErpAcademicYearDBO erpAcademicYearDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_programme_id")
    private ErpProgrammeDBO erpProgrammeDBO;
    
    @Column(name="pre_requisite_weigtage_total")
    private Integer preRequisiteWeigtageTotal;
    
    @Column(name="interview_weightage_total")
    private Integer interviewWeightageTotal;
    
    @Column(name="education_weightage_total")
    private Integer educationWeightageTotal;

    @Column(name="overall_total")
    private Integer overallTotal;

    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admWeightageDefinitionDBO", cascade = CascadeType.ALL)
    private Set<AdmWeightageDefinitionDetailDBO> admWeightageDefinitionDetailDBOsSet;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admWeightageDefinitionDBO", cascade = CascadeType.ALL)
    private Set<AdmWeightageDefinitionGeneralDBO>  admWeightageDefinitionGeneralDBOsSet;
   
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admWeightageDefinitionDBO", cascade = CascadeType.ALL)
    private Set<AdmWeightageDefinitionLocationCampusDBO>  admWeightageDefinitionLocationCampusDBOsSet ;
}
