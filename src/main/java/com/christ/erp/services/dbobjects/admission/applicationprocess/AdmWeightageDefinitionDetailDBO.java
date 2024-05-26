package com.christ.erp.services.dbobjects.admission.applicationprocess;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.admission.settings.AdmPrerequisiteExamDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="adm_weightage_definition_detail")
@Getter
@Setter
public class AdmWeightageDefinitionDetailDBO  {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_weightage_definition_detail_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="adm_weightage_definition_id")
    private AdmWeightageDefinitionDBO admWeightageDefinitionDBO;

    @ManyToOne
    @JoinColumn(name="adm_prerequisite_exam_id")
    private AdmPrerequisiteExamDBO admPrerequisiteExamDBO;

    @ManyToOne
    @JoinColumn(name="adm_qualification_list_id")
    private AdmQualificationListDBO admQualificationListDBO;
    
    @ManyToOne
    @JoinColumn(name="adm_selection_process_plan_detail_id")
    private AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO;

    @ManyToOne
    @JoinColumn(name="adm_selection_process_type_details_id")
    private AdmSelectionProcessTypeDetailsDBO admSelectionProcessTypeDetailsDBO;

    @Column(name = "score")
    private Integer score;
    
    @Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;

    
}
