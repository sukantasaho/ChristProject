package com.christ.erp.services.dbobjects.curriculum.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ExamAssessmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentModeDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exam_assessment_template_details")
@Getter
@Setter
public class ExamAssessmentTemplateDetailsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exam_assessment_template_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "exam_assessment_template_id")
    private ExamAssessmentTemplateDBO examAssessmentTemplateDBO;
	
	@ManyToOne
	@JoinColumn(name = "exam_assessment_category_id")
    private ExamAssessmentCategoryDBO examAssessmentCategoryDBO;
	
    @Column(name = "category_order")
    private Integer categoryOrder;
    
    @Column(name = "category_cia_total_marks")
    private Integer categoryCiaTotalMarks;
    
    @Column(name = "category_cia_scale_down_to")
    private Integer categoryCiaScaleDownTo; 
    
    @Column(name = "category_cia_min_marks")
    private Integer categoryCiaMinMarks; 
    
    @Column(name = "category_ese_total_marks")
    private Integer categoryEseTotalMarks; 
    
    @Column(name = "category_ese_scale_down_to")
    private Integer categoryEseScaleDownTo;
    
    @Column(name = "category_ese_min_marks")
    private Integer categoryEseMinMarks;
    
	@ManyToOne
	@JoinColumn(name = "exam_assessment_mode_id")
    private ExamAssessmentModeDBO examAssessmentModeDBO;
	
    @Column(name = "is_QP_from_DB")
    private Boolean isQPfromDB; 
    
    @Column(name = "duration_of_exam")
    private Integer durationOfExam;
    
    @Column(name = "no_of_evaluators")
    private Integer noOfEvaluators;
    
    @Column(name = "created_users_id" , updatable = false)
    private Integer createdUsersId;
    
    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;

}
