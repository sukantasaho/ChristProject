package com.christ.erp.services.dbobjects.admission.applicationprocess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationDegreeListDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpInstitutionDBO;
import com.christ.erp.services.dbobjects.common.ErpReligionDBO;
import com.christ.erp.services.dbobjects.common.ErpReservationCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpResidentCategoryDBO;

import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name="adm_weightage_definition_general")
@Getter
@Setter
public class AdmWeightageDefinitionGeneralDBO  {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_weightage_definition_general_id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name="adm_weightage_definition_id")
    private AdmWeightageDefinitionDBO admWeightageDefinitionDBO;

    @ManyToOne
    @JoinColumn(name="erp_religion_id")
    private ErpReligionDBO erpReligionDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_reservation_category_id")
    private ErpReservationCategoryDBO erpReservationCategoryDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_gender_id")
    private ErpGenderDBO erpGenderDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_institution_id")
    private ErpInstitutionDBO erpInstitutionDBO;
    
    @ManyToOne
    @JoinColumn(name="erp_resident_category_id")
    private ErpResidentCategoryDBO erpResidentCategoryDBO;
    
    @ManyToOne
    @JoinColumn(name="adm_qualification_degree_list_id")
    private AdmQualificationDegreeListDBO admQualificationDegreeListDBO;
    
    @ManyToOne
    @JoinColumn(name="adm_weightage_definition_work_experience_id")
    private AdmWeightageDefinitionWorkExperienceDBO admWeightageDefinitionWorkExperienceDBO;
    
    @Column(name="score")
    private Integer score;

    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;

}
